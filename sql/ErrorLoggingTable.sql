CREATE TABLE `ErrorLoggingTable` (
  `Time` datetime NOT NULL,
  `Server` varchar(45) DEFAULT NULL,
  `Application` varchar(45) DEFAULT NULL,
  `Thread` varchar(45) DEFAULT NULL,
  `Message` varchar(1000) NOT NULL,
  `Level` varchar(45) NOT NULL DEFAULT '',
  PRIMARY KEY (`Time`,`Level`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
