CREATE EXTENSION pg_cron;
GRANT USAGE ON SCHEMA cron TO postgres;

create type voting_status as enum('OPEN', 'CLOSED', 'CANCELLED');
create type voting_result as enum('ACCEPTED', 'REJECTED', 'UNDEFINED');

create table if not exists associated (
	id UUID primary key default gen_random_uuid(),
	name VARCHAR(100) not NULL,
	email VARCHAR(100) unique not NULL,
	phone VARCHAR(20) unique not NULL
);
create INDEX associated_name_idx on associated(name);

create table if not exists assembly (
	id UUID primary key default gen_random_uuid(),
	day DATE default CURRENT_DATE NOT NULL
);
create INDEX assembly_day_idx on assembly(day);

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
create INDEX voting_status_idx on voting(status);
create INDEX voting_result_idx on voting(result);

create table if not exists vote (
	id UUID primary key default gen_random_uuid(),
	voting UUID references voting(id),
	associated UUID NULL,
	value BOOLEAN not NULL,
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


create or replace function insert_vote_checking() returns trigger AS $$
BEGIN
	-- Checks if the voting is OPEN before inserting a new vote
	IF NOT EXISTS (SELECT 1 FROM voting WHERE id = NEW.voting AND status = 'OPEN') THEN
		RAISE EXCEPTION 'Voting is not OPEN or does not exist';
	END IF;

	-- A new vote is not counted yet
	IF NEW.counted != false THEN
		raise warning 'Counted must not be set manually. It will be set to false';
		NEW.counted = false;
	END IF;

	-- Checks if the associated is registered and haven't voted yet before inserting the new vote
	IF NEW.associated IS NULL THEN
		RAISE EXCEPTION 'Associated cannot be null';
	END IF;
	IF NOT EXISTS (SELECT 1 FROM associated WHERE id = NEW.associated) THEN
		RAISE EXCEPTION 'Associated is not registered';
	END IF;
	IF EXISTS (SELECT 1 FROM associated_voting WHERE associated = NEW.associated AND voting = NEW.voting) THEN
		RAISE EXCEPTION 'Associated has already voted in this voting';
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
		RAISE EXCEPTION 'Cannot change voting of a vote';
	END IF;
	IF OLD.associated != NEW.associated THEN
		RAISE EXCEPTION 'Cannot change associated of a vote';
	END IF;
	IF OLD.value != NEW.value THEN
		RAISE EXCEPTION 'Cannot change value of a vote';
	END IF;
	IF OLD.counted = true AND NEW.counted = false THEN
		RAISE EXCEPTION 'Cannot uncount an already counted vote';
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
		RAISE EXCEPTION 'Voting interval must not be less than 1 minute';
	END IF;
	IF NEW.voting_interval > '1 day' THEN
		RAISE EXCEPTION 'Voting interval must not be greater than 1 day';
	END IF;

	-- Checks if the voting has a valid subject and it is associated with an assembly on this day
	IF NOT EXISTS (SELECT 1 FROM subject WHERE id = NEW.subject) THEN
		RAISE EXCEPTION 'Subject does not exist';
	END IF;
	IF NOT EXISTS (SELECT 1 FROM subject_assembly WHERE subject = NEW.subject AND assembly IN (SELECT id FROM assembly WHERE day = CURRENT_DATE)) THEN
		RAISE EXCEPTION 'Subject is not associated with an assembly on this day';
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
		RAISE EXCEPTION 'result value must not be manually set';
	END IF;
	IF NEW.votes_in_favor != 0 THEN
		RAISE EXCEPTION 'votes_in_favor value must not be manually set';
	END IF;
	IF NEW.votes_against != 0 THEN
		RAISE EXCEPTION 'votes_against value must not be manually set';
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
		RAISE EXCEPTION 'Cannot change voting status. It is already %', OLD.status;
	END IF;

	IF OLD.subject != NEW.subject THEN
		RAISE EXCEPTION 'Cannot change subject of a voting';
	END IF;
	IF OLD.opened_in != NEW.opened_in THEN
		RAISE EXCEPTION 'Cannot change opening time of a voting';
	END IF;

	IF OLD.voting_interval = NEW.voting_interval AND OLD.closes_in != NEW.closes_in THEN
		RAISE EXCEPTION 'Cannot manually change closing time of a voting';
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


-- Count votes of a voting
create or replace function count_votes(voting_id UUID, out new_votes_in_favor INTEGER, out new_votes_against INTEGER) as $$
BEGIN
	UPDATE vote
	SET counted = true
	WHERE voting = voting_id AND counted = false AND value = true;
	GET diagnostics new_votes_in_favor = row_count;

	UPDATE vote
	SET counted = true
	WHERE voting = voting_id AND counted = false AND value = false;
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
