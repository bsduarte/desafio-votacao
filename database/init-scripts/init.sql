create type voting_status as enum('open', 'closed', 'canceled');
create type voting_result as enum('accepted', 'rejected', 'undefined');

create table if not exists associated (
	id UUID primary key default gen_random_uuid(),
	name VARCHAR(100) not NULL,
	email VARCHAR(100) unique not NULL,
	phone VARCHAR(20)
);
create INDEX associated_name_idx on associated(name);

create table if not exists assembly (
	id UUID primary key default gen_random_uuid(),
	day DATE not NULL
);
create INDEX assembly_day_idx on assembly(day);

create table if not exists subject (
	id UUID primary key default gen_random_uuid(),
	headline VARCHAR(100) not null,
	description TEXT not NULL
);
create INDEX subject_headline_idx on subject(headline);

create table if not exists voting (
	id UUID primary key default gen_random_uuid(),
	subject UUID references subject(id),
	voting_interval INTERVAL default '1 minute',
	opened_in TIMESTAMP default now(),
	closes_in TIMESTAMP,
	status voting_status default 'open',
	result voting_result,
	votes_in_favor INTEGER default 0,
	votes_against INTEGER default 0
);
create INDEX voting_status_idx on voting(status);
create INDEX voting_result_idx on voting(result);

create table if not exists vote (
	id UUID primary key default gen_random_uuid(),
	voting UUID references voting(id),
	value BOOLEAN not NULL
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