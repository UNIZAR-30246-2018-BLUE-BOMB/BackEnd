insert into short_url(sequence, url, redirect, time) values ('0', 'https://www.google.es/', 'empty', -1);
insert into short_url(sequence, url, redirect, time) values ('1', 'https://www.google.es/', 'http://www.h2database.com/html/datatypes.html?highlight=datetime&search=date#firstFound', 10);

INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-13', 'windows', 10);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-13', 'ubuntu', 50);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-14', 'windows', 15);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-14', 'ubuntu', 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-15', 'windows', 30);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (1, '2018-11-15', 'ubuntu', 26);

INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-13', 'windows', 1);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-13', 'ubuntu', 2);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-14', 'windows', 3);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-14', 'ubuntu', 4);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-15', 'windows', 5);
INSERT INTO os_stat (seq, date, os, clicks) VALUES (2, '2018-11-15', 'ubuntu', 6);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-13', 'chrome', 10);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-13', 'firefox', 50);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-14', 'chrome', 15);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-14', 'firefox', 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-15', 'chrome', 30);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (2, '2018-11-15', 'firefox', 26);

INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-13', 'chrome', 1);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-13', 'firefox', 2);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-14', 'chrome', 3);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-14', 'firefox', 4);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-15', 'chrome', 5);
INSERT INTO browser_stat (seq, date, browser, clicks) VALUES (1, '2018-11-15', 'firefox', 6);