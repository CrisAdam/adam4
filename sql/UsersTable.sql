CREATE TABLE `UsersTable` (
  `userID` int(11) NOT NULL AUTO_INCREMENT,
  `email` varchar(512) NOT NULL,
  `nickname` varchar(45) NOT NULL,
  `hashedpassword` varchar(256) NOT NULL,
  `salt` varchar(32) NOT NULL,
  `lastSuccessfulLogin` timestamp(4) NULL DEFAULT NULL,
  `accountCreationDate` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`userID`,`email`),
  UNIQUE KEY `userID_UNIQUE` (`userID`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=latin1;
