create table short_url
(
   id integer auto_increment,
   sequence varchar2(255),
   url varchar2(1024) not null,
   redirect varchar2(1024),
   time integer,
   primary key(id)
);

create unique index urls_index on short_url(url,redirect,time);

create table browser_stat
(
    seq integer not null,
    date date not null,
    browser varchar2(128) not null,
    clicks integer DEFAULT 0,
    primary key (seq, date, browser),
    foreign key (seq) references short_url(sequence)
);

create table os_stat
(
    seq integer not null,
    date date not null,
    os varchar2(128) not null,
    clicks integer DEFAULT 0,
    primary key (seq, date, os),
    foreign key (seq) references short_url(sequence)
);