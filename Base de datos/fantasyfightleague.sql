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
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `events`
--

LOCK TABLES `events` WRITE;
/*!40000 ALTER TABLE `events` DISABLE KEYS */;
INSERT INTO `events` VALUES (1,'2025-05-18 20:02:27.977000','2025-05-24 20:02:27.975000','Evento de prueba creado automáticamente',NULL,'Las Vegas, Nevada','UFC Fight Night: Event de Prueba','SCHEDULED','2025-05-18 20:02:27.977000');
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
  `age` int DEFAULT NULL,
  `base_price` int DEFAULT NULL,
  `created_at` datetime(6) DEFAULT NULL,
  `height` double DEFAULT NULL,
  `image_url` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `nationality` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `price` int DEFAULT NULL,
  `record` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `updated_at` datetime(6) DEFAULT NULL,
  `weight` double DEFAULT NULL,
  `weight_class` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `active` bit(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=439 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `fighters`
--

LOCK TABLES `fighters` WRITE;
/*!40000 ALTER TABLE `fighters` DISABLE KEYS */;
INSERT INTO `fighters` VALUES (415,36,100,'2025-05-18 20:02:27.766000',1.93,NULL,'Jon Jones','USA',100,'27-1','2025-05-18 20:02:27.766000',112,'Heavyweight',_binary ''),(416,33,100,'2025-05-18 20:02:27.805000',1.96,NULL,'Ciryl Gane','France',100,'11-2','2025-05-18 20:02:27.805000',112,'Heavyweight',_binary ''),(417,37,100,'2025-05-18 20:02:27.812000',1.93,NULL,'Francis Ngannou','Cameroon',100,'17-3','2025-05-18 20:02:27.812000',117,'Heavyweight',_binary ''),(418,36,98,'2025-05-18 20:02:27.820000',1.93,NULL,'Alex Pereira','Brazil',98,'9-2','2025-05-18 20:02:27.820000',93,'Light Heavyweight',_binary ''),(419,33,100,'2025-05-18 20:02:27.826000',1.93,NULL,'Jamahal Hill','USA',100,'12-1','2025-05-18 20:02:27.826000',93,'Light Heavyweight',_binary ''),(420,31,100,'2025-05-18 20:02:27.840000',1.93,NULL,'Jiri Prochazka','Czech Republic',100,'29-4-1','2025-05-18 20:02:27.840000',93,'Light Heavyweight',_binary ''),(421,34,100,'2025-05-18 20:02:27.851000',1.93,NULL,'Israel Adesanya','Nigeria',100,'24-3','2025-05-18 20:02:27.851000',84,'Middleweight',_binary ''),(422,33,100,'2025-05-18 20:02:27.858000',1.85,NULL,'Sean Strickland','USA',100,'28-5','2025-05-18 20:02:27.858000',84,'Middleweight',_binary ''),(423,30,100,'2025-05-18 20:02:27.863000',1.83,NULL,'Dricus Du Plessis','South Africa',100,'21-2','2025-05-18 20:02:27.863000',84,'Middleweight',_binary ''),(424,32,100,'2025-05-18 20:02:27.872000',1.83,NULL,'Leon Edwards','Jamaica',100,'21-3','2025-05-18 20:02:27.872000',77,'Welterweight',_binary ''),(425,35,100,'2025-05-18 20:02:27.879000',1.75,NULL,'Belal Muhammad','USA',100,'23-3','2025-05-18 20:02:27.879000',77,'Welterweight',_binary ''),(426,36,100,'2025-05-18 20:02:27.887000',1.83,NULL,'Kamaru Usman','Nigeria',100,'20-3','2025-05-18 20:02:27.887000',77,'Welterweight',_binary ''),(427,32,100,'2025-05-18 20:02:27.894000',1.78,NULL,'Islam Makhachev','Russia',100,'25-1','2025-05-18 20:02:27.894000',70,'Lightweight',_binary ''),(428,34,100,'2025-05-18 20:02:27.902000',1.78,NULL,'Charles Oliveira','Brazil',100,'34-9','2025-05-18 20:02:27.902000',70,'Lightweight',_binary ''),(429,35,100,'2025-05-18 20:02:27.907000',1.75,NULL,'Dustin Poirier','USA',100,'29-8','2025-05-18 20:02:27.907000',70,'Lightweight',_binary ''),(430,35,100,'2025-05-18 20:02:27.913000',1.68,NULL,'Alexander Volkanovski','Australia',100,'26-3','2025-05-18 20:02:27.913000',66,'Featherweight',_binary ''),(431,27,100,'2025-05-18 20:02:27.922000',1.7,NULL,'Ilia Topuria','Spain',100,'14-0','2025-05-18 20:02:27.922000',66,'Featherweight',_binary ''),(432,32,100,'2025-05-18 20:02:27.928000',1.8,NULL,'Max Holloway','USA',100,'25-7','2025-05-18 20:02:27.928000',66,'Featherweight',_binary ''),(433,29,100,'2025-05-18 20:02:27.936000',1.8,NULL,'Sean O\'Malley','USA',100,'17-1','2025-05-18 20:02:27.936000',61,'Bantamweight',_binary ''),(434,33,97,'2025-05-18 20:02:27.942000',1.68,NULL,'Merab Dvalishvili','Georgia',97,'16-4','2025-05-18 20:02:27.942000',61,'Bantamweight',_binary ''),(435,34,100,'2025-05-18 20:02:27.951000',1.7,NULL,'Aljamain Sterling','USA',100,'23-4','2025-05-18 20:02:27.951000',61,'Bantamweight',_binary ''),(436,30,100,'2025-05-18 20:02:27.957000',1.65,NULL,'Alexa Grasso','Mexico',100,'16-3-1','2025-05-18 20:02:27.957000',57,'Women\'s Flyweight',_binary ''),(437,36,100,'2025-05-18 20:02:27.963000',1.65,NULL,'Valentina Shevchenko','Kyrgyzstan',100,'23-4-1','2025-05-18 20:02:27.963000',57,'Women\'s Flyweight',_binary ''),(438,34,100,'2025-05-18 20:02:27.972000',1.6,NULL,'Zhang Weili','China',100,'24-3','2025-05-18 20:02:27.972000',52,'Women\'s Strawweight',_binary '');
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
  `initial_budget` int NOT NULL,
  `invitation_code` varchar(255) COLLATE utf8mb4_unicode_ci DEFAULT NULL,
  `max_fighters` int DEFAULT NULL,
  `max_fighters_event` int DEFAULT NULL,
  `min_fighters_event` int DEFAULT NULL,
  `name` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `type` varchar(255) COLLATE utf8mb4_unicode_ci NOT NULL,
  `creator_id` bigint DEFAULT NULL,
  `event_id` bigint DEFAULT NULL,
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
  PRIMARY KEY (`id`),
  UNIQUE KEY `UK_6dotkott2kjsp8vw4d0m25fb7` (`email`),
  UNIQUE KEY `UK_r43af9ap4edm43mmtq01oddj6` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-05-18 20:34:35
