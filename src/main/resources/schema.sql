drop table if exists USERS cascade;
drop table if exists RESTAURANT cascade;
drop table if exists USER_ROLES;
drop table if exists VOTE;
drop table if exists DISH;

create table USERS
(
    id          int         primary key     auto_increment,
    name        varchar     not null,
    email       varchar     not null,
    password    varchar     not null,
    enabled     boolean     not null        default true,
    registered  timestamp   not null        default now()
);
create unique index USERS_UNIQUE_EMAIL_IDX on USERS (email);

create table USER_ROLES
(
    user_id     int         not null,
    role        varchar     not null,
    foreign key (user_id) references USERS (id) on delete cascade,
    constraint ROLES_UNIQUE_USERID_ROLE_CONSTRAINT unique (user_id, role)
);

create table RESTAURANT
(
    id          int         primary key     auto_increment,
    name        varchar     not null,
    constraint RESTAURANTS_UNIQUE_NAME unique (name)
);

create table DISH
(
    id              int         primary key     auto_increment,
    restaurant_id   int         not null,
    title           varchar     not null,
    cost            int         not null,
    foreign key (restaurant_id) references RESTAURANT (id) on delete cascade,
    constraint DISHES_UNIQUE_RESTAURANT_ID_TITLE_CONSTRAINT unique (restaurant_id, title)
);
create index DISH_RESTAURANT_ID_IDX on DISH (restaurant_id);

create table VOTE
(
    id              int         primary key     auto_increment,
    user_id         int         not null,
    voting_date     date        not null        default current_date,
    restaurant_id   int         not null,
    foreign key (user_id) references USERS (id) on delete cascade,
    foreign key (restaurant_id) references RESTAURANT (id) on delete cascade,
    constraint VOTES_UNIQUE_USERID_VOTING_DATE_CONSTRAINT unique (user_id, voting_date)
);
create unique index VOTE_UNIQUE_USER_ID_VOTING_DATE_IDX on VOTE (user_id, voting_date);
create index VOTE_VOTING_DATE_IDX on VOTE (voting_date);