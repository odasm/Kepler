BEGIN TRANSACTION;
CREATE TABLE IF NOT EXISTS `users` (
	`id`	INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT,
	`username`	TEXT DEFAULT NULL,
	`password`	TEXT DEFAULT NULL,
	`figure`	TEXT NOT NULL,
	`sex`	TEXT NOT NULL DEFAULT 'M',
	`motto`	TEXT NOT NULL DEFAULT 'de kepler whey',
	`credits`	int ( 11 ) NOT NULL DEFAULT '200',
	`tickets`	int ( 11 ) NOT NULL DEFAULT '0',
	`film`	int ( 11 ) NOT NULL DEFAULT '0'
);
INSERT INTO `users` VALUES (1,'alex','123','1000118001270012900121001','M','the kepler way',1337,0,0);
COMMIT;
