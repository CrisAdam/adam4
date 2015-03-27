CREATE TABLE `UsersTable` (
  `userID` int(11) NOT NULL DEFAULT '100',
  `email` varchar(512) NOT NULL,
  `nickname` varchar(45) NOT NULL,
  `hashedpassword` varchar(1024) NOT NULL,
  `salt` varchar(24) NOT NULL DEFAULT 'SELECT MD5(RAND());',
  `lastSuccessfulLogin` timestamp(4) NULL DEFAULT NULL,
  `accountCreationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`email`),
  UNIQUE KEY `userID_UNIQUE` (`userID`),
  UNIQUE KEY `username_UNIQUE` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
