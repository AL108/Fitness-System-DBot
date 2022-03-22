begin transaction;

drop schema if exists fitness_db cascade;
create schema fitness_db;
set search_path to 'fitness_db';

drop table if exists User_ cascade;
drop table if exists Cycle_User_Aims cascade;
drop table if exists Cycle cascade;
drop table if exists Workout cascade;
drop table if exists Suggestion cascade;

create table User_ (
    discord_user_id char(18) primary key,
    in_break boolean default false,
    kicked boolean default false
);

create table Cycle_User (
    aim text,
    reflection text,
    user_ char(18) references User_(discord_user_id) on delete cascade,
    start_timestamp timestamp,
    -- end_date date
    free_days_left_in_week int default 2,
    primary key(user_, start_timestamp)
);

create table Cycle (
    start_date_ timestamp primary key,
    end_date timestamp
);

create table Workout (
    timestamp_ date,
    user_ char(18) references User_(discord_user_id) on delete cascade,
    evidence_description text,
    number_ int default 1
);

create table Suggestion (
    user_ char(18) references User_(discord_user_id) on delete cascade,
    suggestion text 
);

commit;


