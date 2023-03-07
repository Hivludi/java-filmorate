create table IF NOT EXISTS DIRECTORS
(
    DIRECTOR_ID INTEGER auto_increment,
    NAME        CHARACTER VARYING(50) not null,
    constraint IF NOT EXISTS DIRECTORS_pk
        primary key (DIRECTOR_ID)
);

create table IF NOT EXISTS GENRES
(
    GENRE_ID INTEGER auto_increment,
    NAME     CHARACTER VARYING(50),
    constraint IF NOT EXISTS GENRE_PK
        primary key (GENRE_ID)
);

create table IF NOT EXISTS MPA
(
    MPA_ID INTEGER auto_increment,
    NAME   CHARACTER VARYING(10),
    constraint IF NOT EXISTS MPA_PK
        primary key (MPA_ID)
);

create table IF NOT EXISTS FILMS
(
    FILM_ID      INTEGER auto_increment,
    NAME         CHARACTER VARYING(50) not null,
    DESCRIPTION  CHARACTER VARYING(200),
    RELEASE_DATE DATE,
    DURATION     INTEGER,
    MPA_ID       INTEGER,
    constraint IF NOT EXISTS FILMS_PK
        primary key (FILM_ID),
    constraint IF NOT EXISTS FILMS_MPA_MPA_ID_FK
        foreign key (MPA_ID) references MPA
);

create table IF NOT EXISTS FILM_GENRES
(
    FILM_ID  INTEGER not null REFERENCES FILMS(FILM_ID) ON DELETE CASCADE ,
    GENRE_ID INTEGER not null REFERENCES GENRES(GENRE_ID) ON DELETE CASCADE ,
    constraint IF NOT EXISTS FILM_GENRES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint IF NOT EXISTS FILM_GENRES_GENRES_GENRE_ID_FK
        foreign key (GENRE_ID) references GENRES
);

create table IF NOT EXISTS USERS
(
    USER_ID  INTEGER auto_increment,
    EMAIL    CHARACTER VARYING(50) not null,
    LOGIN    CHARACTER VARYING(50) not null,
    NAME     CHARACTER VARYING(50),
    BIRTHDAY DATE,
    constraint IF NOT EXISTS USERS_PK
        primary key (USER_ID)
);

create table IF NOT EXISTS FILM_LIKES
(
    FILM_ID INTEGER not null REFERENCES FILMS(FILM_ID) ON DELETE CASCADE ,
    USER_ID INTEGER not null REFERENCES USERS(USER_ID) ON DELETE CASCADE ,
    constraint IF NOT EXISTS FILM_LIKES_FILMS_FILM_ID_FK
        foreign key (FILM_ID) references FILMS,
    constraint IF NOT EXISTS FILM_LIKES_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS
);

create table IF NOT EXISTS FILM_DIRECTORS
(
    FILM_ID     INTEGER not null,
    DIRECTOR_ID INTEGER not null,
    constraint IF NOT EXISTS "FILM_DIRECTORS_DIRECTORS_DIRECTOR_ID_fk"
        foreign key (DIRECTOR_ID) references DIRECTORS
            on update cascade on delete cascade,
    constraint IF NOT EXISTS "FILM_DIRECTORS_FILMS_FILM_ID_fk"
        foreign key (FILM_ID) references FILMS
            on update cascade on delete cascade
);

create table IF NOT EXISTS FRIENDS_LIST
(
    USER_ID   INTEGER not null REFERENCES USERS(USER_ID) ON DELETE CASCADE ,
    FRIEND_ID INTEGER not null REFERENCES USERS(USER_ID) ON DELETE CASCADE ,
    constraint IF NOT EXISTS FRIENDS_LIST_USERS_USER_ID_FK
        foreign key (USER_ID) references USERS,
    constraint IF NOT EXISTS FRIENDS_LIST_USERS_USER_ID_FK_2
        foreign key (FRIEND_ID) references USERS
);

create table IF NOT EXISTS REVIEWS
(
    REVIEW_ID    INTEGER auto_increment,
    CONTENT      CHARACTER VARYING(1000),
    USER_ID      INTEGER not null,
    FILM_ID      INTEGER not null,
    USEFUL       INTEGER DEFAULT 0,
    IS_POSITIVE  BOOLEAN,
    constraint IF NOT EXISTS REVIEWS_PK
        primary key (REVIEW_ID),
    constraint IF NOT EXISTS REVIEWS_FK_USER_ID
        foreign key (USER_ID) references USERS on delete set null,
    constraint IF NOT EXISTS REVIEWS_FK_FILM_ID
        foreign key (FILM_ID) references FILMS on delete set null,
    constraint IF NOT EXISTS  UNIQUE_FILM_ID_USER_ID
        unique ( USER_ID, FILM_ID )
);

create table IF NOT EXISTS REVIEW_LIKES
(
    REVIEW_ID    INTEGER,
    USER_ID      INTEGER,
    IS_POSITIVE  BOOLEAN,
    constraint IF NOT EXISTS REVIEW_LIKES_PK
        primary key (REVIEW_ID, USER_ID),
    constraint IF NOT EXISTS REVIEW_LIKES_FK_USER_ID
        foreign key (USER_ID) references USERS on delete cascade,
    constraint IF NOT EXISTS REVIEW_LIKES_FK_REVIEW_ID
        foreign key (REVIEW_ID) references REVIEWS on delete cascade
);

 alter table FILMS alter column FILM_ID restart with 1;
 alter table GENRES alter column GENRE_ID restart with 1;
 alter table MPA alter column MPA_ID restart with 1;
 alter table USERS alter column USER_ID restart with 1;
 alter table REVIEWS alter column REVIEW_ID restart with 1;
 alter table DIRECTORS alter column DIRECTOR_ID restart with 1;
