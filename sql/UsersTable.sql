CREATE TABLE `UsersTable` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(512) NOT NULL,
  `nickname` varchar(45) NOT NULL,
  `hashedpassword` varchar(1024) NOT NULL,
  `salt` varchar(24) NOT NULL,
  `lastSuccessfulLogin` timestamp(4) NULL DEFAULT NULL,
  `accountCreationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`email`),
  UNIQUE KEY `userID_UNIQUE` (`userID`),
  UNIQUE KEY `username_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=latin1;
