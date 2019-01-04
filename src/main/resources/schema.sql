create table short_url
(
   id integer auto_increment,
   sequence varchar(255),
   url varchar(1024) not null,
   redirect varchar(1024),
   time integer,
   primary key(id)
);

create unique index urls_index on short_url(url,redirect,time);

create table browser_stat
(
    seq integer not null,
    date date not null,
    browser varchar(128) not null,
    clicks integer DEFAULT 0,
    primary key (seq, date, browser),
    foreign key (seq) references short_url(id)
);

create table os_stat
(
    seq integer not null,
    date date not null,
    os varchar(128) not null,
    clicks integer DEFAULT 0,
    primary key (seq, date, os),
    foreign key (seq) references short_url(id)
);