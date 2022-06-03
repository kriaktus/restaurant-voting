drop table if exists USERS cascade;
drop table if exists RESTAURANT cascade;
drop table if exists USER_ROLES;
drop table if exists VOTE;
drop table if exists MENU_MENU_ITEM;
drop table if exists MENU_ITEM;
drop table if exists MENU;


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

create table VOTE
(
    id              int         primary key     auto_increment,
    user_id         int         not null,
    voting_date     date        not null        default current_date,
    restaurant_id   int         not null,
    foreign key (user_id) references USERS (id) on delete cascade,
    foreign key (restaurant_id) references RESTAURANT (id) on delete cascade
);
create index VOTE_VOTING_DATE_IDX on VOTE (voting_date);

create table MENU_ITEM
(
    id              int         primary key     auto_increment,
    restaurant_id   int         not null,
    name            varchar     not null,
    price           int         not null,
    foreign key (restaurant_id) references RESTAURANT (id) on delete cascade
);
create index MENU_ITEM_RESTAURANT_ID_IDX on MENU_ITEM (restaurant_id);

create table MENU
(
    id              int         primary key     auto_increment,
    menu_date       date        not null        default current_date,
    restaurant_id   int         not null,
    foreign key (restaurant_id) references RESTAURANT (id) on delete cascade,
    constraint MENU_UNIQUE_RESTAURANT_ID_MENU_DATE_CONSTRAINT unique (restaurant_id, menu_date)
);
create index MENU_DATE_RESTAURANT_ID_IDX on MENU (menu_date, restaurant_id);

create table MENU_MENU_ITEM
(
    menu_id         int         not null,
    menu_item_id    int         not null,
    primary key (menu_id, menu_item_id),
    foreign key (menu_id) references MENU (id) on delete cascade,
    foreign key (menu_item_id) references MENU_ITEM (id) on delete cascade
);