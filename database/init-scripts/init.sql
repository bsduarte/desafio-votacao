create type voting_status as enum('created', 'open', 'closed', 'canceled');
create type voting_result as enum('accepted', 'rejected', 'undefined');

create table if not exists associated (
	id UUID primary key default gen_random_uuid(),
	name VARCHAR(100) not NULL,
	email VARCHAR(100) unique not NULL,
	phone VARCHAR(20)
);

create table if not exists assembly (
	id UUID primary key default gen_random_uuid(),
	day DATE not NULL
);

create table if not exists subject (
	id UUID primary key default gen_random_uuid(),
	headline VARCHAR(100) not null,
	description TEXT not NULL
);

create table if not exists voting (
	id UUID primary key default gen_random_uuid(),
	subject UUID references subject(id),
	created_in TIMESTAMP not null,
	opened_in TIMESTAMP,
	closed_in TIMESTAMP,
	status voting_status not null,
	result voting_result
);

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