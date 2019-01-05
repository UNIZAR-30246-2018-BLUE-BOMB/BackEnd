insert into short_url(sequence, url, redirect, time) values ('0', 'https://www.google.es/', 'empty', -1);
insert into short_url(sequence, url, redirect, time) values ('1', 'https://www.google.es/', 'http://www.h2database.com/html/datatypes.html?highlight=datetime&search=date#firstFound', 10);

INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-12-25', 'windows', 10);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-11-25', 'ubuntu', 50);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-12-26', 'windows', 15);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-12-26', 'ubuntu', 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-12-27', 'windows', 30);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('0', '2018-12-27', 'ubuntu', 26);

INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-25', 'windows', 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-25', 'ubuntu', 2);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-26', 'windows', 3);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-26', 'ubuntu', 4);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-27', 'windows', 5);
INSERT INTO os_stat (seq, date, os, clicks) VALUES ('1', '2018-12-27', 'ubuntu', 6);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-25', 'chrome', 10);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-25', 'firefox', 50);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-26', 'chrome', 15);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-26', 'firefox', 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-27', 'chrome', 30);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('1', '2018-12-27', 'firefox', 26);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-25', 'chrome', 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-25', 'firefox', 2);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-26', 'chrome', 3);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-26', 'firefox', 4);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-27', 'chrome', 5);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES ('0', '2018-12-27', 'firefox', 6);