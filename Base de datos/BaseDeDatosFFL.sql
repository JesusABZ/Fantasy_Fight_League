-- MySQL dump 10.13  Distrib 8.0.42, for Win64 (x86_64)
--
-- Host: localhost    Database: fantasyfightleague
-- ------------------------------------------------------
-- Server version	8.0.42

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `event_fighters`
--

DROP TABLE IF EXISTS `event_fighters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `event_fighters` (
  `event_id` bigint NOT NULL,
  `fighter_id` bigint NOT NULL,
  PRIMARY KEY (`event_id`,`fighter_id`),
  KEY `FK6j1iudxasd0jr7e4qvarxv63n` (`fighter_id`),
  CONSTRAINT `FK6j1iudxasd0jr7e4qvarxv63n` FOREIGN KEY (`fighter_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKgwrkntmclw843n4jwmj8tt62b` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_fighters`
--

LOCK TABLES `event_fighters` WRITE;
/*!40000 ALTER TABLE `event_fighters` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_fighters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `events`
--

DROP TABLE IF EXISTS `events`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `events` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `date` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `location` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `picks_deadline` datetime(6) DEFAULT NULL,
  `start_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_events_start_date` (`start_date`),
  KEY `idx_events_status` (`status`)
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'2025-05-18 20:02:27.977000','2025-05-24 20:02:27.975000','Evento de prueba creado automáticamente',NULL,'Las Vegas, Nevada','UFC Fight Night: Event de Prueba','SCHEDULED','2025-05-18 20:02:27.977000',NULL,'2025-05-23 20:02:27.975000','2025-05-24 20:02:27.975000'),(2,'2025-05-18 20:43:28.452000','2025-05-31 20:43:28.442000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 20:43:28.452000',NULL,'2025-05-30 20:43:28.442000','2025-05-31 20:43:28.442000'),(3,'2025-05-18 20:53:57.846000','2025-05-31 20:53:57.837000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 20:53:57.846000',NULL,'2025-05-30 20:53:57.837000','2025-05-31 20:53:57.837000'),(4,'2025-05-18 21:16:34.921000','2025-05-31 21:16:34.920000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 21:16:34.921000',NULL,'2025-05-30 21:16:34.920000','2025-05-31 21:16:34.920000'),(5,'2025-05-18 21:24:31.104000','2025-05-31 21:24:31.095000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 21:24:31.104000',NULL,'2025-05-30 21:24:31.095000','2025-05-31 21:24:31.095000'),(6,'2025-05-18 21:35:08.899000','2025-05-31 21:35:08.898000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 21:35:08.899000',NULL,'2025-05-30 21:35:08.898000','2025-05-31 21:35:08.898000'),(7,'2025-05-18 21:40:20.586000','2025-05-31 21:40:20.585000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 21:40:20.586000',NULL,'2025-05-30 21:40:20.585000','2025-05-31 21:40:20.585000'),(8,'2025-05-18 21:49:57.361000','2025-05-31 21:49:57.359000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 21:49:57.361000',NULL,'2025-05-30 21:49:57.359000','2025-05-31 21:49:57.359000'),(9,'2025-05-18 22:04:50.071000','2025-05-31 22:04:50.070000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 22:04:50.071000',NULL,'2025-05-30 22:04:50.070000','2025-05-31 22:04:50.070000'),(10,'2025-05-18 22:09:48.528000','2025-05-31 22:09:48.527000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 22:09:48.528000',NULL,'2025-05-30 22:09:48.527000','2025-05-31 22:09:48.527000'),(11,'2025-05-18 23:04:42.896000','2025-05-31 23:04:42.887000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 23:04:42.896000',NULL,'2025-05-30 23:04:42.887000','2025-05-31 23:04:42.887000'),(12,'2025-05-18 23:59:00.536000','2025-05-31 23:59:00.535000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-18 23:59:00.536000',NULL,'2025-05-30 23:59:00.535000','2025-05-31 23:59:00.535000'),(13,'2025-05-19 00:36:32.582000','2025-05-31 00:36:32.580000','Evento extraído de: https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'UFC Apex, Las Vegas, Nevada','UFC Fight Night: Blanchfield vs Barber','SCHEDULED','2025-05-19 00:36:32.582000',NULL,'2025-05-30 00:36:32.580000','2025-05-31 00:36:32.580000'),(14,'2025-05-19 00:42:07.000000','2025-05-31 00:00:00.000000','Evento extraído de https://www.ufcespanol.com/event/ufc-fight-night-may-31-2025',NULL,'Las Vegas, Nevada','UFC Fight Night','SCHEDULED','2025-05-19 00:42:07.000000',NULL,'2025-05-30 00:00:00.000000','2025-05-31 00:00:00.000000'),(15,'2025-05-22 18:44:21.748000','2025-05-22 18:44:21.747000','Evento de prueba',NULL,'Las Vegas, Nevada','UFC Test Event','SCHEDULED','2025-05-22 18:44:21.748000',NULL,'2025-05-21 18:44:21.747000','2025-05-22 18:44:21.747000');
/*!40000 ALTER TABLE `events` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fighter_stats`
--

DROP TABLE IF EXISTS `fighter_stats`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fighter_stats` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `knockdowns` int DEFAULT NULL,
  `minutes_fought` int DEFAULT NULL,
  `points` int DEFAULT NULL,
  `significant_strikes` int DEFAULT NULL,
  `submissions` int DEFAULT NULL,
  `takedowns` int DEFAULT NULL,
  `total_strikes` int DEFAULT NULL,
  `fight_id` bigint NOT NULL,
  `fighter_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjcfmig70vlmbf27mqrm72xwsl` (`fight_id`),
  KEY `FKij0n8fbpo9oi491ujfu35180b` (`fighter_id`),
  CONSTRAINT `FKij0n8fbpo9oi491ujfu35180b` FOREIGN KEY (`fighter_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKjcfmig70vlmbf27mqrm72xwsl` FOREIGN KEY (`fight_id`) REFERENCES `fights` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fighter_stats`
--

LOCK TABLES `fighter_stats` WRITE;
/*!40000 ALTER TABLE `fighter_stats` DISABLE KEYS */;
/*!40000 ALTER TABLE `fighter_stats` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fighters`
--

DROP TABLE IF EXISTS `fighters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fighters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nationality` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` int DEFAULT NULL,
  `record` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weight_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `idx_fighters_active` (`active`),
  KEY `idx_fighters_price` (`price`)
) ENGINE=InnoDB AUTO_INCREMENT=48 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fighters`
--

LOCK TABLES `fighters` WRITE;
/*!40000 ALTER TABLE `fighters` DISABLE KEYS */;
INSERT INTO `fighters` VALUES (1,'2025-05-18 20:55:09.185000',NULL,'ERIN BLANCHFIELD','España',60,'11-3','2025-05-20 15:47:08.441000','Peso Wélter',_binary '\0'),(2,'2025-05-18 20:55:09.219000',NULL,'MAYCEE BARBER','España',60,'20-3-1','2025-05-20 15:47:08.469000','Peso Wélter',_binary '\0'),(3,'2025-05-18 20:55:09.244000',NULL,'MATEUSZ GAMROT','España',60,'18-1','2025-05-20 15:47:08.475000','Peso Wélter',_binary '\0'),(4,'2025-05-18 20:55:09.262000',NULL,'LUDOVIT KLEIN','España',60,'20-6','2025-05-20 15:47:08.482000','Peso Wélter',_binary '\0'),(5,'2025-05-18 20:55:09.277000',NULL,'BILLY RAY GOFF','España',60,'14-4','2025-05-20 15:47:08.488000','Peso Wélter',_binary '\0'),(6,'2025-05-18 20:55:09.307000',NULL,'SEOKHYEON KO','España',60,'15-5','2025-05-20 15:47:08.495000','Peso Wélter',_binary '\0'),(7,'2025-05-18 20:55:09.329000',NULL,'DUSTIN JACOBY','España',60,'13-4-1','2025-05-20 15:47:08.500000','Peso Wélter',_binary '\0'),(8,'2025-05-18 20:55:09.347000',NULL,'BRUNO LOPES','España',60,'18-1-1','2025-05-20 15:47:08.506000','Peso Wélter',_binary '\0'),(9,'2025-05-18 20:55:09.373000',NULL,'ZACHARY REESE','España',60,'18-0','2025-05-20 15:47:08.512000','Peso Wélter',_binary '\0'),(10,'2025-05-18 20:55:09.396000',NULL,'DUŠKO TODOROVIĆ','España',60,'14-2-1','2025-05-20 15:47:08.518000','Peso Wélter',_binary '\0'),(11,'2025-05-18 20:55:09.412000',NULL,'JAFEL FILHO','España',60,'13-5-1','2025-05-20 15:47:08.523000','Peso Wélter',_binary '\0'),(12,'2025-05-18 20:55:09.429000',NULL,'ALLAN NASCIMENTO','España',60,'20-4','2025-05-20 15:47:08.529000','Peso Wélter',_binary '\0'),(13,'2025-05-18 20:55:09.436000',NULL,'JEREMIAH WELLS','España',60,'17-2-1','2025-05-20 15:47:08.535000','Peso Wélter',_binary '\0'),(14,'2025-05-18 20:55:09.442000',NULL,'ANDREAS GUSTAFSSON','España',60,'5-5-1','2025-05-20 15:47:08.541000','Peso Wélter',_binary '\0'),(15,'2025-05-18 20:55:09.449000',NULL,'KETLEN VIEIRA','España',60,'20-1-1','2025-05-20 15:47:08.546000','Peso Wélter',_binary '\0'),(16,'2025-05-18 20:55:09.456000',NULL,'MACY CHIASSON','España',60,'8-6-1','2025-05-20 15:47:08.552000','Peso Wélter',_binary '\0'),(17,'2025-05-18 20:55:09.463000',NULL,'RAMIZ BRAHIMAJ','España',60,'15-2-1','2025-05-20 15:47:08.559000','Peso Wélter',_binary '\0'),(18,'2025-05-18 20:55:09.481000',NULL,'OBAN ELLIOTT','España',60,'12-3-1','2025-05-20 15:47:08.564000','Peso Wélter',_binary '\0'),(19,'2025-05-18 20:55:09.587000',NULL,'KURT HOLOBAUGH','España',60,'9-5-1','2025-05-20 15:47:08.569000','Peso Wélter',_binary '\0'),(20,'2025-05-18 20:55:09.617000',NULL,'JORDAN LEAVITT','España',60,'13-1','2025-05-20 15:47:08.576000','Peso Wélter',_binary '\0'),(21,'2025-05-18 20:55:09.625000',NULL,'MARQUEL MEDEROS','España',60,'9-1','2025-05-20 15:47:08.582000','Peso Wélter',_binary '\0'),(22,'2025-05-18 20:55:09.634000',NULL,'BOLAJI OKI','España',60,'9-1-1','2025-05-20 15:47:08.588000','Peso Wélter',_binary '\0'),(23,'2025-05-18 20:55:09.642000',NULL,'RAYANNE DOS SANTOS','España',60,'15-6','2025-05-20 15:47:08.592000','Peso Wélter',_binary '\0'),(24,'2025-05-18 20:55:09.839000',NULL,'ALICE ARDELEAN','España',60,'18-4-1','2025-05-20 15:47:08.596000','Peso Wélter',_binary '\0'),(25,'2025-05-18 23:04:59.179000',NULL,'Erin\n                Blanchfield','España',60,'0-0','2025-05-19 00:36:31.705000','Peso Mosca (femenino)',_binary '\0'),(26,'2025-05-18 23:04:59.384000',NULL,'Maycee\n                Barber','España',60,'0-0','2025-05-19 00:36:31.711000','Peso Mosca (femenino)',_binary '\0'),(27,'2025-05-18 23:04:59.392000',NULL,'Mateusz\n                Gamrot','España',60,'0-0','2025-05-19 00:36:31.716000','Peso Mosca (femenino)',_binary '\0'),(28,'2025-05-18 23:04:59.400000',NULL,'Ludovit\n                Klein','España',60,'0-0','2025-05-19 00:36:31.721000','Peso Mosca (femenino)',_binary '\0'),(29,'2025-05-18 23:04:59.431000',NULL,'Seokhyeon\n                Ko','España',60,'0-0','2025-05-19 00:36:31.726000','Peso Mosca (femenino)',_binary '\0'),(30,'2025-05-18 23:04:59.440000',NULL,'Dustin\n                Jacoby','España',60,'0-0','2025-05-19 00:36:31.731000','Peso Mosca (femenino)',_binary '\0'),(31,'2025-05-18 23:04:59.459000',NULL,'Bruno\n                Lopes','España',60,'0-0','2025-05-19 00:36:31.736000','Peso Mosca (femenino)',_binary '\0'),(32,'2025-05-18 23:04:59.486000',NULL,'Jafel\n                Filho','España',60,'0-0','2025-05-19 00:36:31.742000','Peso Mosca (femenino)',_binary '\0'),(33,'2025-05-18 23:04:59.494000',NULL,'Allan\n                Nascimento','España',60,'0-0','2025-05-19 00:36:31.746000','Peso Mosca (femenino)',_binary '\0'),(34,'2025-05-18 23:04:59.511000',NULL,'Andreas\n                Gustafsson','España',60,'0-0','2025-05-19 00:36:31.751000','Peso Mosca (femenino)',_binary '\0'),(35,'2025-05-18 23:04:59.519000',NULL,'Ketlen\n                Vieira','España',60,'0-0','2025-05-19 00:36:31.756000','Peso Mosca (femenino)',_binary '\0'),(36,'2025-05-18 23:04:59.527000',NULL,'Macy\n                Chiasson','España',60,'0-0','2025-05-19 00:36:31.761000','Peso Mosca (femenino)',_binary '\0'),(37,'2025-05-18 23:04:59.544000',NULL,'Ramiz\n                Brahimaj','España',60,'0-0','2025-05-19 00:36:31.767000','Peso Mosca (femenino)',_binary '\0'),(38,'2025-05-18 23:04:59.550000',NULL,'Oban\n                Elliott','España',60,'0-0','2025-05-19 00:36:31.772000','Peso Mosca (femenino)',_binary '\0'),(39,'2025-05-18 23:04:59.559000',NULL,'Kurt\n                Holobaugh','España',60,'0-0','2025-05-19 00:36:31.777000','Peso Mosca (femenino)',_binary '\0'),(40,'2025-05-18 23:04:59.567000',NULL,'Jordan\n                Leavitt','España',60,'0-0','2025-05-19 00:36:31.783000','Peso Mosca (femenino)',_binary '\0'),(41,'2025-05-18 23:04:59.575000',NULL,'MarQuel\n                Mederos','España',60,'0-0','2025-05-19 00:36:31.788000','Peso Mosca (femenino)',_binary '\0'),(42,'2025-05-18 23:04:59.582000',NULL,'Bolaji\n                Oki','España',60,'0-0','2025-05-19 00:36:31.792000','Peso Mosca (femenino)',_binary '\0'),(43,'2025-05-18 23:04:59.590000',NULL,'Rayanne\n                dos Santos','España',60,'0-0','2025-05-19 00:36:31.798000','Peso Mosca (femenino)',_binary '\0'),(44,'2025-05-18 23:04:59.596000',NULL,'Alice\n                Ardelean','España',60,'0-0','2025-05-19 00:36:31.804000','Peso Mosca (femenino)',_binary '\0'),(45,'2025-05-20 15:47:08.616000','https://dynl.mktgcdn.com/p/xnYGAGWMpbap_gSsJDIFPvBDPsTv5j_W3V0gCeAFyIQ/200x1.png','Jon Jones','USA',75000,'27-1','2025-05-22 18:46:32.382000','Peso Completo',_binary ''),(46,'2025-05-20 15:47:08.629000','https://dynl.mktgcdn.com/p/jMTyWWXdzwWDaswQzek-X6JLObRd1otuQaU4KQZElfw/200x1.png','Ilia Topuria','España',75000,'14-0','2025-05-22 18:46:32.391000','Peso Pluma',_binary ''),(47,'2025-05-21 01:18:38.000000','https://dynl.mktgcdn.com/p/HREUg-pySQqB86Xxda5JUqmFInPekYteFBioxw1yUJw/200x1.png','Ciryl Gane','France',45000,'11-2','2025-05-22 18:46:32.368000','Heavyweight',_binary '\0');
/*!40000 ALTER TABLE `fighters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `fights`
--

DROP TABLE IF EXISTS `fights`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `fights` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `end_round` int DEFAULT NULL,
  `end_time` int DEFAULT NULL,
  `is_main_event` bit(1) DEFAULT NULL,
  `is_title_fight` bit(1) DEFAULT NULL,
  `number_of_rounds` int DEFAULT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `victory_method` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `weight_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `event_id` bigint DEFAULT NULL,
  `fighter1_id` bigint NOT NULL,
  `fighter2_id` bigint NOT NULL,
  `winner_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKi1so01nnr7iedya78lm9ys7ha` (`event_id`),
  KEY `FKjh60wj0ldvbk046wghg881vs` (`fighter1_id`),
  KEY `FK8gud6d01b06ew80pm9qsc1fbf` (`fighter2_id`),
  KEY `FKtcfpi9puoiihy9iup7mqje7al` (`winner_id`),
  CONSTRAINT `FK8gud6d01b06ew80pm9qsc1fbf` FOREIGN KEY (`fighter2_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKi1so01nnr7iedya78lm9ys7ha` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `FKjh60wj0ldvbk046wghg881vs` FOREIGN KEY (`fighter1_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKtcfpi9puoiihy9iup7mqje7al` FOREIGN KEY (`winner_id`) REFERENCES `fighters` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fights`
--

LOCK TABLES `fights` WRITE;
/*!40000 ALTER TABLE `fights` DISABLE KEYS */;
/*!40000 ALTER TABLE `fights` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `league_members`
--

DROP TABLE IF EXISTS `league_members`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `league_members` (
  `league_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`league_id`,`user_id`),
  KEY `FK49dmiahiff30uwykai4cnyo1c` (`user_id`),
  CONSTRAINT `FK49dmiahiff30uwykai4cnyo1c` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  CONSTRAINT `FKoaeb1047qfp37gjwff47hswle` FOREIGN KEY (`league_id`) REFERENCES `leagues` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `league_members`
--

LOCK TABLES `league_members` WRITE;
/*!40000 ALTER TABLE `league_members` DISABLE KEYS */;
/*!40000 ALTER TABLE `league_members` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `leagues`
--

DROP TABLE IF EXISTS `leagues`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `leagues` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `active` bit(1) NOT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `description` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `initial_budget` int NOT NULL DEFAULT '100000',
  `invitation_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_fighters` int DEFAULT '10',
  `max_fighters_event` int DEFAULT '3',
  `min_fighters_event` int DEFAULT '1',
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator_id` bigint DEFAULT NULL,
  `event_id` bigint DEFAULT NULL,
  `auto_delete_date` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `FKrp0ggffrqqt1iin3sr5tql4il` (`creator_id`),
  KEY `FKdidk67h11jsf85mlxwohfygxg` (`event_id`),
  CONSTRAINT `FKdidk67h11jsf85mlxwohfygxg` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`),
  CONSTRAINT `FKrp0ggffrqqt1iin3sr5tql4il` FOREIGN KEY (`creator_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `leagues`
--

LOCK TABLES `leagues` WRITE;
/*!40000 ALTER TABLE `leagues` DISABLE KEYS */;
/*!40000 ALTER TABLE `leagues` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `market_listings`
--

DROP TABLE IF EXISTS `market_listings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `market_listings` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `current_price` int DEFAULT NULL,
  `end_date` datetime(6) DEFAULT NULL,
  `initial_price` int NOT NULL,
  `listing_date` datetime(6) NOT NULL,
  `status` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `fighter_id` bigint NOT NULL,
  `league_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKjnwv0tlsreqa7axhfoab7r96p` (`fighter_id`),
  KEY `FKm2e42hx4cbpjvvw1he6vyflru` (`league_id`),
  CONSTRAINT `FKjnwv0tlsreqa7axhfoab7r96p` FOREIGN KEY (`fighter_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKm2e42hx4cbpjvvw1he6vyflru` FOREIGN KEY (`league_id`) REFERENCES `leagues` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `market_listings`
--

LOCK TABLES `market_listings` WRITE;
/*!40000 ALTER TABLE `market_listings` DISABLE KEYS */;
/*!40000 ALTER TABLE `market_listings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pick_fighters`
--

DROP TABLE IF EXISTS `pick_fighters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pick_fighters` (
  `pick_id` bigint NOT NULL,
  `fighter_id` bigint NOT NULL,
  PRIMARY KEY (`pick_id`,`fighter_id`),
  KEY `fighter_id` (`fighter_id`),
  CONSTRAINT `pick_fighters_ibfk_1` FOREIGN KEY (`pick_id`) REFERENCES `picks` (`id`) ON DELETE CASCADE,
  CONSTRAINT `pick_fighters_ibfk_2` FOREIGN KEY (`fighter_id`) REFERENCES `fighters` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pick_fighters`
--

LOCK TABLES `pick_fighters` WRITE;
/*!40000 ALTER TABLE `pick_fighters` DISABLE KEYS */;
/*!40000 ALTER TABLE `pick_fighters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `picks`
--

DROP TABLE IF EXISTS `picks`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `picks` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `user_id` bigint NOT NULL,
  `league_id` bigint NOT NULL,
  `event_id` bigint NOT NULL,
  `total_cost` int DEFAULT '0',
  `remaining_budget` int DEFAULT '0',
  `event_points` int DEFAULT '0',
  `is_locked` bit(1) DEFAULT b'0',
  `created_at` datetime(6) DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_user_league_event` (`user_id`,`league_id`,`event_id`),
  KEY `league_id` (`league_id`),
  KEY `event_id` (`event_id`),
  KEY `idx_picks_event_points` (`event_points` DESC),
  KEY `idx_picks_user_league` (`user_id`,`league_id`),
  CONSTRAINT `picks_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `picks_ibfk_2` FOREIGN KEY (`league_id`) REFERENCES `leagues` (`id`) ON DELETE CASCADE,
  CONSTRAINT `picks_ibfk_3` FOREIGN KEY (`event_id`) REFERENCES `events` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `picks`
--

LOCK TABLES `picks` WRITE;
/*!40000 ALTER TABLE `picks` DISABLE KEYS */;
/*!40000 ALTER TABLE `picks` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `name` enum('ROLE_USER','ROLE_ADMIN') COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_ofx66keruapi6vyqpv6f2or37` (`name`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'ROLE_USER'),(2,'ROLE_ADMIN');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `team_fighters`
--

DROP TABLE IF EXISTS `team_fighters`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `team_fighters` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `acquired_at` datetime(6) DEFAULT NULL,
  `buy_price` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `selected_for_event` bit(1) DEFAULT NULL,
  `fighter_id` bigint NOT NULL,
  `team_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKeahu5w3dw23xlb2lpltsqu04u` (`fighter_id`),
  KEY `FKtbvvhtyti4j85qmk8xfcvmdxj` (`team_id`),
  CONSTRAINT `FKeahu5w3dw23xlb2lpltsqu04u` FOREIGN KEY (`fighter_id`) REFERENCES `fighters` (`id`),
  CONSTRAINT `FKtbvvhtyti4j85qmk8xfcvmdxj` FOREIGN KEY (`team_id`) REFERENCES `teams` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `team_fighters`
--

LOCK TABLES `team_fighters` WRITE;
/*!40000 ALTER TABLE `team_fighters` DISABLE KEYS */;
/*!40000 ALTER TABLE `team_fighters` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `teams`
--

DROP TABLE IF EXISTS `teams`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `teams` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `current_budget` int DEFAULT NULL,
  `event_points` int DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `total_points` int DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `league_id` bigint NOT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  KEY `FKcmnrlwu7alyse9s3x5tgvxyqj` (`league_id`),
  KEY `FKlm88j38y90erf2rum00e85gw8` (`user_id`),
  CONSTRAINT `FKcmnrlwu7alyse9s3x5tgvxyqj` FOREIGN KEY (`league_id`) REFERENCES `leagues` (`id`),
  CONSTRAINT `FKlm88j38y90erf2rum00e85gw8` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `teams`
--

LOCK TABLES `teams` WRITE;
/*!40000 ALTER TABLE `teams` DISABLE KEYS */;
/*!40000 ALTER TABLE `teams` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_roles`
--

DROP TABLE IF EXISTS `user_roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_roles` (
  `user_id` bigint NOT NULL,
  `role_id` bigint NOT NULL,
  PRIMARY KEY (`user_id`,`role_id`),
  KEY `FKh8ciramu9cc9q3qcqiv4ue8a6` (`role_id`),
  CONSTRAINT `FKh8ciramu9cc9q3qcqiv4ue8a6` FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`),
  CONSTRAINT `FKhfh9dx7w3ubf1co1vdev94g3f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_roles`
--

LOCK TABLES `user_roles` WRITE;
/*!40000 ALTER TABLE `user_roles` DISABLE KEYS */;
INSERT INTO `user_roles` VALUES (3,1),(4,1),(3,2);
/*!40000 ALTER TABLE `user_roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `created_at` datetime(6) DEFAULT NULL,
  `email` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `email_confirmed` bit(1) DEFAULT NULL,
  `first_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `last_name` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `password` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `username` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `profile_image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (1,'2025-05-22 01:04:10.195000','usuario@example.com',_binary '\0','Juan','Pérez','$2a$10$TukgotSrvuqsaHC.r5iBd.tTVF/3J4OY9ki0ngxVftHaGjz5nMuj2','usuario_prueba',NULL),(2,'2025-05-22 01:12:05.576000','usuario@yopmail.com',_binary '','Juan Carlos','Pérez García','$2a$10$hn/aGN4RJjX3OC10.fJBluZW/2XY4zfaVJq67RnJiUgLcvJcUtmF2','usuario_prueba2','http://localhost:8080/uploads/imagen_perfil.jpg'),(3,'2025-05-22 18:19:54.387000','admin@fantasyfightleague.com',_binary '','Administrador','Sistema','$2a$10$ZwpTEzGWty3ak.1jHc1F1.hDCOoG81tgxbm99SB2MT87m2Um6sq8G','admin',NULL),(4,'2025-05-22 18:40:06.782000','jesusalvarez@yopmail.com',_binary '\0','Jesus','Alvarez','$2a$10$MICj4NBG.rltfjhz8sB/kO8Jhf2vZ8GO60JBUYijKDe5uR8NU5yVG','Jesus',NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `verification_tokens`
--

DROP TABLE IF EXISTS `verification_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `verification_tokens` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `expiry_date` datetime(6) DEFAULT NULL,
  `token` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `user_id` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_dqp95ggn6gvm865km5muba2o5` (`user_id`),
  CONSTRAINT `FK54y8mqsnq1rtyf581sfmrbp4f` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `verification_tokens`
--

LOCK TABLES `verification_tokens` WRITE;
/*!40000 ALTER TABLE `verification_tokens` DISABLE KEYS */;
INSERT INTO `verification_tokens` VALUES (1,'2025-05-23 01:04:10.232000','629a56ec-5516-4c8a-a81a-539c543c259e',1),(2,'2025-05-23 01:12:05.603000','80a487d3-4005-4a3e-a5f5-7ed0fcfa58b0',2),(3,'2025-05-23 18:40:06.814000','ea52005b-764c-4d26-927a-afbcd68262a4',4);
/*!40000 ALTER TABLE `verification_tokens` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-22 19:46:14
