-- noinspection SqlNoDataSourceInspectionForFile
SET @os_win='windows';
SET @os_ubu='ubuntu';
SET @b_chr='chrome';
SET @b_moz='firefox';
SET @date_one='2018-12-25';
SET @date_two='2018-12-26';
SET @date_three='2018-12-27';


insert into short_url(sequence, url, redirect, time) values ('0', 'https://www.google.es/', 'empty', -1);
insert into short_url(sequence, url, redirect, time) values ('1', 'https://www.google.es/', 'http://www.h2database.com/html/datatypes.html?highlight=datetime&search=date#firstFound', 10);

INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_one, @os_win, 10);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_one, @os_ubu, 50);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_two, @os_win, 15);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_two, @os_ubu, 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_three, @os_win, 30);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', @date_three, @os_ubu, 26);

INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_one, @os_win, 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_one, @os_ubu, 2);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_two, @os_win, 3);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_two, @os_ubu, 4);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_three, @os_win, 5);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', @date_three, @os_ubu, 6);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_one, @b_chr, 10);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_one, @b_moz, 50);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_two, @b_chr, 15);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_two, @b_moz, 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_three, @b_chr, 30);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', @date_three, @b_moz, 26);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_one, @b_chr, 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_one, @b_moz, 2);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_two, @b_chr, 3);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_two, @b_moz, 4);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_three, @b_chr, 5);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', @date_three, @b_moz, 6);