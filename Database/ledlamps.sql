-- MySQL dump 10.13  Distrib 5.7.34, for Linux (x86_64)
--
-- Host: localhost    Database: ledlamps
-- ------------------------------------------------------
-- Server version	5.7.34-0ubuntu0.18.04.1

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `actions`
--

DROP TABLE IF EXISTS `actions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `actions` (
  `idAutomation` int(5) NOT NULL,
  `modeId` int(3) NOT NULL,
  `position` int(3) NOT NULL,
  `custom` char(7) DEFAULT NULL,
  `fade1` char(7) DEFAULT NULL,
  `fade2` char(7) DEFAULT NULL,
  `time` int(3) NOT NULL,
  PRIMARY KEY (`idAutomation`,`position`),
  KEY `modeId` (`modeId`),
  CONSTRAINT `actions_ibfk_1` FOREIGN KEY (`idAutomation`) REFERENCES `automations` (`idAutomation`) ON DELETE CASCADE,
  CONSTRAINT `actions_ibfk_2` FOREIGN KEY (`modeId`) REFERENCES `modes` (`modeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `actions`
--

LOCK TABLES `actions` WRITE;
/*!40000 ALTER TABLE `actions` DISABLE KEYS */;
INSERT INTO `actions` VALUES (1,41,1,NULL,NULL,NULL,5),(1,44,2,'#ff0000',NULL,NULL,10),(1,42,3,NULL,NULL,NULL,10),(1,44,4,'#00ffa9','null','null',5),(2,41,1,NULL,NULL,NULL,5),(2,42,2,NULL,NULL,NULL,5),(2,49,3,NULL,'#00ff00','#0000ff',5);
/*!40000 ALTER TABLE `actions` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`lorenzodellamatera`@`%`*/ /*!50003 TRIGGER incPosition
BEFORE INSERT
ON actions
FOR EACH ROW
BEGIN
SET @pos = (SELECT MAX(position) FROM actions WHERE actions.idAutomation = NEW.idAutomation);
IF (@pos IS NOT NULL) THEN
SET NEW.position = @pos+1;
END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `aeskeys`
--

DROP TABLE IF EXISTS `aeskeys`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `aeskeys` (
  `AESkey` char(32) NOT NULL,
  `idLamp` char(40) NOT NULL,
  PRIMARY KEY (`AESkey`),
  KEY `idLamp` (`idLamp`),
  CONSTRAINT `aeskeys_ibfk_1` FOREIGN KEY (`idLamp`) REFERENCES `lamps` (`idLamp`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `aeskeys`
--

LOCK TABLES `aeskeys` WRITE;
/*!40000 ALTER TABLE `aeskeys` DISABLE KEYS */;
INSERT INTO `aeskeys` VALUES ('6D7973656372657470617373776F7264','08cd923367890009657eab812753379bdb321eeb'),('b071733fa7ef11eb9bf5005056b507de','b2ccc8fead1234b2aa40c8cfde3b0033a01b64a8');
/*!40000 ALTER TABLE `aeskeys` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'NO_AUTO_VALUE_ON_ZERO' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`lorenzodellamatera`@`%`*/ /*!50003 TRIGGER `newlamp` BEFORE INSERT ON `aeskeys` FOR EACH ROW INSERT INTO lamps (idLamp) VALUES(NEW.idlamp) */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `automations`
--

DROP TABLE IF EXISTS `automations`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `automations` (
  `idAutomation` int(5) NOT NULL AUTO_INCREMENT,
  `idUser` int(5) NOT NULL,
  `name` varchar(30) NOT NULL,
  `isActive` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`idAutomation`),
  KEY `idUser` (`idUser`),
  CONSTRAINT `automations_ibfk_1` FOREIGN KEY (`idUser`) REFERENCES `users` (`idUser`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `automations`
--

LOCK TABLES `automations` WRITE;
/*!40000 ALTER TABLE `automations` DISABLE KEYS */;
INSERT INTO `automations` VALUES (1,1,'Automation 1',0),(2,1,'Automation 2',0);
/*!40000 ALTER TABLE `automations` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `lamps`
--

DROP TABLE IF EXISTS `lamps`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `lamps` (
  `idLamp` char(40) NOT NULL,
  `fade1` char(7) DEFAULT '#ff0000',
  `fade2` char(7) DEFAULT '#00ff00',
  `custom` char(7) DEFAULT '#0000ff',
  `opMode` int(3) DEFAULT '1',
  `random` tinyint(1) DEFAULT '0',
  `brightness` int(3) DEFAULT '255',
  `ssid` varchar(30) DEFAULT '',
  `ip` varchar(15) DEFAULT '',
  `connected` tinyint(1) DEFAULT '0',
  `host` varchar(60) DEFAULT '80.211.47.247',
  PRIMARY KEY (`idLamp`),
  KEY `opMode` (`opMode`),
  CONSTRAINT `lamps_ibfk_1` FOREIGN KEY (`opMode`) REFERENCES `modes` (`modeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `lamps`
--

LOCK TABLES `lamps` WRITE;
/*!40000 ALTER TABLE `lamps` DISABLE KEYS */;
INSERT INTO `lamps` VALUES ('08cd923367890009657eab812753379bdb321eeb','#00ff00','#0000ff','#ff0000',1,0,255,'','',0,'80.211.47.247'),('b2ccc8fead1234b2aa40c8cfde3b0033a01b64a8','#ff0000','#00ff00','#0000ff',41,0,255,'','',0,'80.211.47.247');
/*!40000 ALTER TABLE `lamps` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 DEFINER=`lorenzodellamatera`@`%`*/ /*!50003 TRIGGER checkBrightness
BEFORE UPDATE
ON lamps
FOR EACH ROW
BEGIN
IF (NEW.brightness > 255) THEN
SET NEW.brightness = 255;
ELSEIF (NEW.brightness < 0) THEN
SET NEW.brightness = 0;
END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;

--
-- Table structure for table `modes`
--

DROP TABLE IF EXISTS `modes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `modes` (
  `modeId` int(3) NOT NULL,
  `modeName` varchar(20) NOT NULL,
  `modeDesc` text NOT NULL,
  `modeLinkParam` varchar(20) DEFAULT NULL,
  PRIMARY KEY (`modeId`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `modes`
--

LOCK TABLES `modes` WRITE;
/*!40000 ALTER TABLE `modes` DISABLE KEYS */;
INSERT INTO `modes` VALUES (1,'Sound reactive','The lamp lights up according to the volume of the microphone input','sound_reactive'),(2,'Chill fade','The lamp slowly fades from light pink to light blue','chill_fade'),(3,'Color wipe','The lamp LEDs light up one after the other until it is fully colored of red, green or blue','color_wipe'),(4,'Color flash','The lamp lights up alternatively in red, green and blue','color_flash'),(5,'Rainbow cycle','The lamp LEDs cycle rainbow colors','rainbow_cycle'),(6,'Rainbow fade','The lamp fades between the rainbow colors','rainbow_fade'),(7,'Rainbow chase','The lamp LEDs chase each other lighting up in the rainbow colors','rainbow_chase'),(8,'Strobe','The lamp quickly flashes the rainbow colors','strobe'),(9,'Fire','The lamp animates his LEDs simulating a fire','fire'),(10,'Bouncing balls','The lamp animates his LEDs showing four bouncing balls (red, green, blue and yellow)','balls'),(11,'Fill random','The lamp LEDs light up one after the other in a random color','fill_random'),(12,'Sound colors','The lamp changes his color when the volume of the microphone input is high','sound_colors'),(13,'Twinkle','The lamp LEDs blink in a random color','twinkle'),(14,'Sparkle','The lamp lights up one LED in a random color and switches it off right after that','sparkle'),(15,'Strobe shot','The lamp lights up in a rainbow color and switches off only when the volume of the microphone input is high','strobe_shot'),(16,'Strobe fade','The lamp slowly fades the rainbow colors, but when the volume of the microphone input is high it speeds up','strobe_fade'),(41,'On','The lamp lights up in white','on'),(42,'Off','The lamp turns off','off'),(43,'Default colors','The lamp lights up in red, green or blue','red-green-blue'),(44,'Custom color','The lamp lights up of a chosen color','custom_color'),(45,'Custom fade','The lamp fades a chosen color','custom_fade'),(46,'Fade colors','The lamp fades between two chosen colors','fade'),(47,'Double color','Half of the lamp lights up in one chosen color, the other half lights up in the other','double'),(48,'Fix colors','One lamp lights up in one chosen color, the other lamp lights up in the other','fix'),(49,'Swapping colors','The lamp lights up alternatively in two chosen colors','swapping');
/*!40000 ALTER TABLE `modes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `users` (
  `idUser` int(5) NOT NULL AUTO_INCREMENT,
  `username` varchar(20) NOT NULL,
  `password` varchar(32) NOT NULL,
  `name` varchar(30) DEFAULT NULL,
  `surname` varchar(30) DEFAULT NULL,
  `mail` varchar(50) NOT NULL,
  `idLamp` char(40) DEFAULT NULL,
  PRIMARY KEY (`idUser`),
  KEY `idLamp` (`idLamp`),
  CONSTRAINT `users_ibfk_1` FOREIGN KEY (`idLamp`) REFERENCES `lamps` (`idLamp`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'admin','21232f297a57a5a743894a0e4a801fc3','admin','admin','admin@admin.com','08cd923367890009657eab812753379bdb321eeb'),(2,'prova','189bbbb00c5f1fb7fba9ad9285f193d1','Nome','Cognome','prova@email.com','b2ccc8fead1234b2aa40c8cfde3b0033a01b64a8'),(3,'loredella','5f4dcc3b5aa765d61d8327deb882cf99','Lorenzo','Della Matera','loredm02@gmail.com','08cd923367890009657eab812753379bdb321eeb');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Dumping routines for database 'ledlamps'
--
/*!50003 DROP PROCEDURE IF EXISTS `create_automation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`lorenzodellamatera`@`%` PROCEDURE `create_automation`(IN idUserNew INT, nameNew VARCHAR(30), modeIdNew INT, positionNew INT, customNew CHAR(7), fade1New CHAR(7), fade2New CHAR(7), timeNew INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
        RESIGNAL;
    END;

    START TRANSACTION;

    INSERT INTO automations (idUser, name) VALUES (idUserNew, nameNew);

    SET @id = (SELECT idAutomation FROM automations WHERE idUser = idUserNew AND name = nameNew);

    INSERT INTO actions VALUES (@id, modeIdNew, positionNew, customNew, fade1New, fade2New, timeNew);
    
    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_action` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`lorenzodellamatera`@`%` PROCEDURE `delete_action`(IN idAutomationDel INT, positionDel INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    DELETE FROM actions
    WHERE idAutomation = idAutomationDel AND position = positionDel;

    UPDATE actions SET position = position - 1 
    WHERE position > positionDel AND idAutomation = idAutomationDel;

    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `delete_automation` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`lorenzodellamatera`@`%` PROCEDURE `delete_automation`(IN idAutomationDel INT)
BEGIN
    SET @isActive = (SELECT isActive FROM automations WHERE idAutomation = idAutomationDel);

    DELETE FROM automations WHERE idAutomation = idAutomationDel;

    SELECT @isActive result;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 DROP PROCEDURE IF EXISTS `move_action` */;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8 */ ;
/*!50003 SET character_set_results = utf8 */ ;
/*!50003 SET collation_connection  = utf8_general_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
CREATE DEFINER=`lorenzodellamatera`@`%` PROCEDURE `move_action`(IN idAutomationMove INT, fromPos INT, toPos INT)
BEGIN
    DECLARE EXIT HANDLER FOR SQLEXCEPTION 
    BEGIN
        ROLLBACK;
    END;

    START TRANSACTION;

    UPDATE actions SET position = 0
    WHERE idAutomation = idAutomationMove AND position = fromPos;

    IF (fromPos < toPos) THEN
UPDATE actions SET position = position - 1 
WHERE idAutomation = idAutomationMove AND position > fromPos AND position <= toPos;
    ELSE
UPDATE actions SET position = position + 1 
WHERE idAutomation = idAutomationMove AND position < fromPos AND position >= toPos
ORDER BY position DESC;
    END IF;

    UPDATE actions SET position = toPos 
    WHERE idAutomation = idAutomationMove AND position = 0;

    COMMIT;
END ;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2021-05-31 23:06:00
