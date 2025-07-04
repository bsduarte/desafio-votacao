CREATE EXTENSION pg_cron;
GRANT USAGE ON SCHEMA cron TO postgres;

create type voting_status as enum('OPEN', 'CLOSED', 'CANCELLED');
create type voting_result as enum('ACCEPTED', 'REJECTED', 'UNDEFINED');

create table if not exists associated (
	id UUID primary key default gen_random_uuid(),
	name VARCHAR(100) not NULL,
	email VARCHAR(100) unique not NULL,
	phone VARCHAR(20) unique not NULL,
	active BOOLEAN default true NOT NULL
);
create INDEX associated_name_idx on associated(name);

create table if not exists assembly (
	id UUID primary key default gen_random_uuid(),
	assembly_date DATE default CURRENT_DATE NOT NULL
);
create INDEX assembly_day_idx on assembly(assembly_date);

create table if not exists subject (
	id UUID primary key default gen_random_uuid(),
	headline VARCHAR(100) not NULL,
	description TEXT not NULL
);
create INDEX subject_headline_idx on subject(headline);

create table if not exists voting (
	id UUID primary key default gen_random_uuid(),
	subject UUID references subject(id),
	voting_interval INTERVAL default '1 minute' NOT NULL,
	opened_in TIMESTAMPTZ default now() NOT NULL,
	closes_in TIMESTAMPTZ,
	status voting_status default 'OPEN' NOT NULL,
	result voting_result default NULL,
	votes_in_favor INTEGER default 0 NOT NULL,
	votes_against INTEGER default 0 NOT NULL
);
create INDEX voting_opened_in_idx on voting(opened_in);
create INDEX voting_status_idx on voting(status);
create INDEX voting_result_idx on voting(result);

create table if not exists vote (
	id UUID primary key default gen_random_uuid(),
	voting UUID references voting(id),
	associated UUID NULL,
	vote_value BOOLEAN not NULL,
	counted BOOLEAN default false NOT NULL
);

create table if not exists subject_assembly (
	subject UUID references subject(id),
	assembly UUID references assembly(id),
    unique(subject, assembly)
);

create table if not exists associated_voting (
	associated UUID references associated(id),
	voting UUID references voting(id),
    unique(associated, voting)
);


-- Do not permit update an inactive associated, except for reactivating it
create or replace function update_associated_checking() returns trigger AS $$
BEGIN
	-- If the associated is inactive, do not allow any updates except for reactivating it
	IF OLD.active = false AND NEW.active = true THEN
		RETURN NEW; -- Allow reactivation
	ELSIF OLD.active = false THEN
		RAISE EXCEPTION '[CODE:UPDACT001] Cannot update an inactive associated';
	END IF;
	RETURN NEW;
END;
$$ language 'plpgsql';
create trigger up_associated
before update on associated
for each row
execute procedure update_associated_checking();


-- Do not delete, just inactivate the associated
create or replace function delete_associated() returns trigger AS $$
BEGIN
	UPDATE associated
	SET active = false
	WHERE id = OLD.id;
	RETURN NULL;
END;
$$ language 'plpgsql';
create trigger del_associated
before delete on associated
for each row
execute procedure delete_associated();


-- Do not delete an assembly if it already happened
-- Do not delete an assembly if it has associated subjects with not cancelled voting
create or replace function delete_assembly_checking() returns trigger AS $$
BEGIN
	-- If the assembly date is in the past, do not allow deletion
	IF OLD.assembly_date < CURRENT_DATE THEN
		RAISE EXCEPTION '[CODE:DELASB001] Cannot delete an assembly that already happened';
	END IF;

	-- If the assembly has associated subjects with not cancelled voting, do not allow deletion
	IF EXISTS (SELECT 1 FROM subject_assembly sa
				JOIN voting v ON v.subject = sa.subject
				WHERE sa.assembly = OLD.id AND v.status != 'CANCELLED') THEN
		RAISE EXCEPTION '[CODE:DELASB002] Cannot delete an assembly with not cancelled voting';
	END IF;	

	RETURN OLD; -- Allow deletion
END;
$$ language 'plpgsql';
create trigger del_assembly
before delete on assembly
for each row
execute procedure delete_assembly_checking();


create or replace function insert_vote_checking() returns trigger AS $$
BEGIN
	-- Checks if the associated is active before inserting a new vote
	IF NOT EXISTS (SELECT 1 FROM associated WHERE id = NEW.associated AND active = true) THEN
		RAISE EXCEPTION '[CODE:INSVOT001] This associated is not active or does not exist';
	END IF;

	-- Checks if the voting is OPEN before inserting a new vote
	IF NOT EXISTS (SELECT 1 FROM voting WHERE id = NEW.voting AND status = 'OPEN') THEN
		RAISE EXCEPTION '[CODE:INSVOT002] This voting is not OPEN or does not exist';
	END IF;

	-- A new vote is not counted yet
	IF NEW.counted != false THEN
		raise warning 'Counted must not be set manually. It will be set to false';
		NEW.counted = false;
	END IF;

	-- Checks if the associated is registered and haven't voted yet before inserting the new vote
	IF NEW.associated IS NULL THEN
		RAISE EXCEPTION '[CODE:INSVOT003] Missing associated';
	END IF;
	IF NOT EXISTS (SELECT 1 FROM associated WHERE id = NEW.associated) THEN
		RAISE EXCEPTION '[CODE:INSVOT004] Associated not registered';
	END IF;
	IF EXISTS (SELECT 1 FROM associated_voting WHERE associated = NEW.associated AND voting = NEW.voting) THEN
		RAISE EXCEPTION '[CODE:INSVOT005] This associated has already voted in this voting';
	END IF;

	-- Register the associated as having voted in this voting and anonymize its vote
	INSERT INTO associated_voting (associated, voting) VALUES (NEW.associated, NEW.voting);
	NEW.associated = NULL;

	RETURN NEW;
END;
$$ language 'plpgsql';
create trigger ins_new_vote
before insert on vote
for each row
execute procedure insert_vote_checking();

create or replace function update_vote_checking() returns trigger AS $$
BEGIN
	IF OLD.voting != NEW.voting THEN
		RAISE EXCEPTION '[CODE:UPDVOT001] Cannot change voting of a vote';
	END IF;
	IF OLD.associated != NEW.associated THEN
		RAISE EXCEPTION '[CODE:UPDVOT002] Cannot change associated of a vote';
	END IF;
	IF OLD.vote_value != NEW.vote_value THEN
		RAISE EXCEPTION '[CODE:UPDVOT003] Cannot change value of a vote';
	END IF;
	IF OLD.counted = true AND NEW.counted = false THEN
		RAISE EXCEPTION '[CODE:UPDVOT004] Cannot uncount an already counted vote';
	END IF;

	RETURN NEW;
END;
$$ language 'plpgsql';
create trigger up_vote
after update on vote
for each row
execute procedure update_vote_checking();


create or replace function insert_voting_checking() returns trigger AS $$
BEGIN
	-- Checks if the voting has a valid interval
	IF NEW.voting_interval < '1 minute' THEN
		RAISE EXCEPTION '[CODE:INSVTG001] The voting interval must not be less than 1 minute';
	END IF;
	IF NEW.voting_interval > '1 day' THEN
		RAISE EXCEPTION '[CODE:INSVTG002] The voting interval must not be greater than 1 day';
	END IF;

	-- Checks if the voting has a valid subject and it is associated with an assembly on this date
	IF NOT EXISTS (SELECT 1 FROM subject WHERE id = NEW.subject) THEN
		RAISE EXCEPTION '[CODE:INSVTG003] The referred subject is not registered';
	END IF;
	IF NOT EXISTS (SELECT 1 FROM subject_assembly WHERE subject = NEW.subject AND assembly IN (SELECT id FROM assembly WHERE assembly_date = CURRENT_DATE)) THEN
		RAISE EXCEPTION '[CODE:INSVTG004] The referred subject is not being discussed in an assembly on this date';
	END IF;

	-- Checks if there's no an already OPEN voting for this subject
	IF EXISTS (SELECT 1 FROM voting WHERE subject = NEW.subject AND status = 'OPEN') THEN
		RAISE EXCEPTION '[CODE:INSVTG005] There is an already OPEN voting for this subject at this moment';
	END IF;

	-- Warnings about default values
	IF NEW.opened_in != now() THEN
		raise warning 'opened_in value will be ignored. It will be set to now()';
		NEW.opened_in = now();
	END IF;
	IF NEW.status != 'OPEN' THEN
		raise warning 'status value will be ignored. It will be set to OPEN';
		NEW.status = 'OPEN';
	END IF;
	IF NEW.closes_in IS NOT NULL THEN
		raise warning 'closes_in value will be ignored. It will be set to opened_in + voting_interval';
	END IF;	

	-- Exceptions for default values
	IF NEW.result IS NOT NULL THEN
		RAISE EXCEPTION '[CODE:INSVTG006] The voting result must not be set manually';
	END IF;
	IF NEW.votes_in_favor != 0 THEN
		RAISE EXCEPTION '[CODE:INSVTG007] The vote count(favor) of a voting must not be set manually';
	END IF;
	IF NEW.votes_against != 0 THEN
		RAISE EXCEPTION '[CODE:INSVTG008] The vote count(against) of a voting must not be set manually';
	END IF;

	-- Estimates the voting closing time
	NEW.closes_in = NEW.opened_in + NEW.voting_interval;

	RETURN NEW;
END; 
$$ language 'plpgsql';
create trigger ins_new_voting
before insert on voting
for each row
execute procedure insert_voting_checking();


create or replace function before_update_voting_checking() returns trigger AS $$
BEGIN
	-- Checks if it is trying to finish an already finished voting
	iF (OLD.status = 'CLOSED' OR OLD.status = 'CANCELLED') AND OLD.status != NEW.status THEN
		RAISE EXCEPTION '[CODE:UPDVTG001] Cannot change the voting status. It is already %', OLD.status;
	END IF;

	IF OLD.subject != NEW.subject THEN
		RAISE EXCEPTION '[CODE:UPDVTG002] Cannot change the subject of a voting';
	END IF;
	IF OLD.opened_in != NEW.opened_in THEN
		RAISE EXCEPTION '[CODE:UPDVTG003] Cannot change the opening time of a voting';
	END IF;

	IF OLD.voting_interval = NEW.voting_interval AND OLD.closes_in != NEW.closes_in THEN
		RAISE EXCEPTION '[CODE:UPDVTG004] Cannot manually change the closing time of a voting';
	END IF;
	IF OLD.voting_interval != NEW.voting_interval THEN
		-- Estimates the new voting closing time
		NEW.closes_in = NEW.opened_in + NEW.voting_interval;
	END IF;

	RETURN NEW;
END;
$$ language 'plpgsql';
create trigger before_up_voting
before update on voting
for each row
execute procedure before_update_voting_checking();


create or replace function after_update_voting_checking() returns trigger AS $$
DECLARE
	new_votes_in_favor INTEGER;
	new_votes_against INTEGER;
BEGIN
	-- If the voting is closing, compute its result
	IF OLD.status = 'OPEN' AND NEW.status != 'OPEN' THEN
		SELECT * into new_votes_in_favor, new_votes_against from count_votes(NEW.id);
		IF NEW.status = 'CLOSED' THEN
			IF NEW.votes_in_favor + new_votes_in_favor > NEW.votes_against + new_votes_against THEN
				UPDATE voting
				SET result = 'ACCEPTED'
				WHERE id = NEW.id;
			ELSIF NEW.votes_against + new_votes_against > NEW.votes_in_favor + new_votes_in_favor THEN
				UPDATE voting
				SET result = 'REJECTED'
				WHERE id = NEW.id;
			ELSE
				UPDATE voting
				SET result = 'UNDEFINED'
				WHERE id = NEW.id;
			END IF;
		END IF;
	END IF;

	RETURN NEW;
END;
$$ language 'plpgsql';
create trigger after_up_voting
after update on voting
for each row
execute procedure after_update_voting_checking();


-- Do not allow updates into subject_assembly table
create or replace function update_subject_assembly_checking() returns trigger AS $$
BEGIN
	RAISE EXCEPTION '[CODE:UPDSBA001] Cannot manually update an subject/assembly association';
END;
$$ language 'plpgsql';
create trigger up_subject_assembly
before update on subject_assembly
for each row
execute procedure update_subject_assembly_checking();


-- Do not allow deletion into subject_assembly table if the subject has not cancelled voting
create or replace function delete_subject_assembly_checking() returns trigger AS $$
BEGIN
	-- Do not delete if the assembly is in the past
	IF EXISTS (SELECT 1 FROM assembly a WHERE a.id = OLD.assembly AND a.assembly_date < CURRENT_DATE) THEN
		RAISE EXCEPTION '[CODE:DELSBA001] Cannot disassociate a subject from an assembly in the past.';
	END IF;

	-- If the subject has at least one not cancelled voting on current date, do not allow deletion
	IF EXISTS (SELECT 1 FROM voting v WHERE v.subject = OLD.subject AND v.opened_in >= CURRENT_DATE AND v.status != 'CANCELLED') THEN
		RAISE EXCEPTION '[CODE:DELSBA002] Cannot disassociate a subject from an assembly if the subject has non-cancelled voting.';
	END IF;

	RETURN OLD; -- Allow deletion
END;
$$ language 'plpgsql';
create trigger del_subject_assembly
before delete on subject_assembly
for each row
execute procedure delete_subject_assembly_checking();


-- Do not allow updates into associated_voting table
create or replace function update_associated_voting_checking() returns trigger AS $$
BEGIN
	RAISE EXCEPTION '[CODE:UPDAVT001] Cannot manually update if an associated has already voted in a voting';
END;
$$ language 'plpgsql';
create trigger up_associated_voting
before update on associated_voting
for each row
execute procedure update_associated_voting_checking();


-- Do not allow deletion into associated_voting table
create or replace function delete_associated_voting_checking() returns trigger AS $$
BEGIN
	RAISE EXCEPTION '[CODE:DELAVT001] Cannot manually set an associated as not having voted in a voting';
END;
$$ language 'plpgsql';
create trigger del_associated_voting
before delete on associated_voting
for each row
execute procedure delete_associated_voting_checking();


-- Count votes of a voting
create or replace function count_votes(voting_id UUID, out new_votes_in_favor INTEGER, out new_votes_against INTEGER) as $$
BEGIN
	UPDATE vote
	SET counted = true
	WHERE voting = voting_id AND counted = false AND vote_value = true;
	GET diagnostics new_votes_in_favor = row_count;

	UPDATE vote
	SET counted = true
	WHERE voting = voting_id AND counted = false AND vote_value = false;
	GET diagnostics new_votes_against = row_count;

	UPDATE voting
	SET votes_in_favor = votes_in_favor + new_votes_in_favor, votes_against = votes_against + new_votes_against
	WHERE id = voting_id;
END;
$$ language plpgsql;
create or replace function count_votes_no_output(voting_id UUID) returns void as $$
DECLARE
	new_votes_in_favor INTEGER;
	new_votes_against INTEGER;
BEGIN
	SELECT * INTO new_votes_in_favor, new_votes_against
	FROM count_votes(voting_id);
END;
$$ language plpgsql;


-- Count votes of OPEN voting
create or replace function count_votes_open_voting() returns void as $$
BEGIN
	PERFORM count_votes_no_output(id)
	FROM voting
	WHERE status = 'OPEN';
END;
$$ language plpgsql;
-- Runs every minute
select cron.schedule('count_votes_job', '*/1 * * * * *', $$select count_votes_open_voting();$$);


-- Closes expired voting
create or replace function close_expired_voting() returns void as $$
BEGIN
    UPDATE voting
	SET status = 'CLOSED'
	WHERE status = 'OPEN' AND closes_in <= now();
END;
$$ language plpgsql;
-- Runs every minute
select cron.schedule('close_expired_voting_job', '*/1 * * * * *', $$select close_expired_voting();$$);
