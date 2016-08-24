-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: localhost    Database: bebidas_v2
-- ------------------------------------------------------
-- Server version	5.6.31-log

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
-- Current Database: `bebidas_v2`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `bebidas_v2` /*!40100 DEFAULT CHARACTER SET utf8 */;

USE `bebidas_v2`;

--
-- Table structure for table `autocomplete`
--

DROP TABLE IF EXISTS `autocomplete`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `autocomplete` (
  `ID_AUTO` bigint(20) NOT NULL AUTO_INCREMENT,
  `TERMO` varchar(100) DEFAULT NULL,
  `NUM_RANK` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID_AUTO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `autocomplete`
--

LOCK TABLES `autocomplete` WRITE;
/*!40000 ALTER TABLE `autocomplete` DISABLE KEYS */;
/*!40000 ALTER TABLE `autocomplete` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `bairros`
--

DROP TABLE IF EXISTS `bairros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `bairros` (
  `COD_BAIRRO` int(11) NOT NULL AUTO_INCREMENT,
  `COD_CIDADE` int(11) DEFAULT NULL,
  `DESC_BAIRRO` text NOT NULL,
  PRIMARY KEY (`COD_BAIRRO`),
  KEY `FK_REFERENCE_21` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_21` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bairros`
--

LOCK TABLES `bairros` WRITE;
/*!40000 ALTER TABLE `bairros` DISABLE KEYS */;
INSERT INTO `bairros` (`COD_BAIRRO`, `COD_CIDADE`, `DESC_BAIRRO`) VALUES (1,1,'Várzea'),(2,1,'Universitário'),(3,1,'Senai'),(4,1,'Schulz'),(5,1,'São João'),(6,1,'Santuário'),(7,1,'Santo Inácio'),(8,1,'Santo Antônio'),(9,1,'Santa Vitória'),(10,1,'Renascença'),(11,1,'Rauber'),(12,1,'Progresso'),(13,1,'Pedreira'),(14,1,'Monte Verde'),(15,1,'Margarida'),(16,1,'Linha Santa Cruz'),(17,1,'João Alves'),(18,1,'Jardim Europa'),(19,1,'Independência'),(20,1,'Higienópolis'),(21,1,'Goiás'),(22,1,'Germânia'),(23,1,'Faxinal Menino Deus'),(24,1,'Esmeralda'),(25,1,'Dona Carlota'),(26,1,'Distrito Industrial'),(27,1,'Country'),(28,1,'Centro'),(29,1,'Castelo Branco'),(30,1,'Bonfim'),(31,1,'Bom Jesus'),(32,1,'Belvedere'),(33,1,'Avenida'),(34,1,'Arroio Grande'),(35,1,'Ana Nery'),(36,1,'Aliança');
/*!40000 ALTER TABLE `bairros` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrinho`
--

DROP TABLE IF EXISTS `carrinho`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrinho` (
  `ID_CARRINHO` bigint(20) NOT NULL,
  `ID_USUARIO` bigint(20) DEFAULT NULL,
  `COD_BAIRRO` int(11) DEFAULT NULL,
  `DATA_CRIACAO` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_CARRINHO`),
  KEY `FK_REFERENCE_22` (`ID_USUARIO`),
  KEY `FK_REFERENCE_25` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_22` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_25` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrinho`
--

LOCK TABLES `carrinho` WRITE;
/*!40000 ALTER TABLE `carrinho` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrinho` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `carrinho_item`
--

DROP TABLE IF EXISTS `carrinho_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `carrinho_item` (
  `ID_CARRINHO` bigint(20) NOT NULL,
  `SEQ_ITEM` int(11) NOT NULL,
  `ID_PROD_DIST` bigint(20) DEFAULT NULL,
  `QTD` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_CARRINHO`,`SEQ_ITEM`),
  KEY `FK_REFERENCE_24` (`ID_PROD_DIST`),
  CONSTRAINT `FK_REFERENCE_23` FOREIGN KEY (`ID_CARRINHO`) REFERENCES `carrinho` (`ID_CARRINHO`),
  CONSTRAINT `FK_REFERENCE_24` FOREIGN KEY (`ID_PROD_DIST`) REFERENCES `produtos_distribuidora` (`ID_PROD_DIST`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrinho_item`
--

LOCK TABLES `carrinho_item` WRITE;
/*!40000 ALTER TABLE `carrinho_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrinho_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cidade`
--

DROP TABLE IF EXISTS `cidade`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cidade` (
  `COD_CIDADE` int(11) NOT NULL AUTO_INCREMENT,
  `DESC_CIDADE` text NOT NULL,
  PRIMARY KEY (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cidade`
--

LOCK TABLES `cidade` WRITE;
/*!40000 ALTER TABLE `cidade` DISABLE KEYS */;
INSERT INTO `cidade` (`COD_CIDADE`, `DESC_CIDADE`) VALUES (1,'Santa Cruz do Sul');
/*!40000 ALTER TABLE `cidade` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `dias_semana`
--

DROP TABLE IF EXISTS `dias_semana`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dias_semana` (
  `COD_DIA` int(11) NOT NULL AUTO_INCREMENT,
  `DESC_DIA` text,
  PRIMARY KEY (`COD_DIA`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `dias_semana`
--

LOCK TABLES `dias_semana` WRITE;
/*!40000 ALTER TABLE `dias_semana` DISABLE KEYS */;
INSERT INTO `dias_semana` (`COD_DIA`, `DESC_DIA`) VALUES (1,'Segunda-feira'),(2,'Terça-feira'),(3,'Quarta-feira'),(4,'Quinta-feira'),(5,'Sexta-feira'),(6,'Sábado'),(7,'Domingo'),(8,'Feriado');
/*!40000 ALTER TABLE `dias_semana` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribuidora`
--

DROP TABLE IF EXISTS `distribuidora`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribuidora` (
  `ID_DISTRIBUIDORA` int(11) NOT NULL AUTO_INCREMENT,
  `COD_CIDADE` int(11) DEFAULT NULL,
  `DESC_RAZAO_SOCIAL` text,
  `DESC_NOME_ABREV` text,
  `VAL_ENTREGA_MIN` decimal(12,2) DEFAULT NULL,
  `DESC_TELEFONE` varchar(50) DEFAULT NULL,
  `DESC_ENDERECO` text,
  `NUM_ENDEREC` varchar(10) DEFAULT NULL,
  `DESC_COMPLEMENTO` text,
  `VAL_TELE_ENTREGA` decimal(12,2) DEFAULT NULL,
  `FLAG_CUSTOM` char(1) DEFAULT NULL,
  `DESC_LOGIN` varchar(45) DEFAULT NULL,
  `DESC_SENHA` varchar(45) DEFAULT NULL,
  `DESC_MAIL` text,
  `FLAG_ATIVO_MASTER` char(1) DEFAULT NULL,
  `FLAG_ATIVO` char(1) DEFAULT NULL,
  `FLAG_MODOPAGAMENTO` char(1) DEFAULT NULL,
  `DATE_LASTAJAX` datetime DEFAULT NULL,
  PRIMARY KEY (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_6` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_6` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora`
--

LOCK TABLES `distribuidora` WRITE;
/*!40000 ALTER TABLE `distribuidora` DISABLE KEYS */;
INSERT INTO `distribuidora` (`ID_DISTRIBUIDORA`, `COD_CIDADE`, `DESC_RAZAO_SOCIAL`, `DESC_NOME_ABREV`, `VAL_ENTREGA_MIN`, `DESC_TELEFONE`, `DESC_ENDERECO`, `NUM_ENDEREC`, `DESC_COMPLEMENTO`, `VAL_TELE_ENTREGA`, `FLAG_CUSTOM`, `DESC_LOGIN`, `DESC_SENHA`, `DESC_MAIL`, `FLAG_ATIVO_MASTER`, `FLAG_ATIVO`, `FLAG_MODOPAGAMENTO`, `DATE_LASTAJAX`) VALUES (1,1,'Distribuidora Bêbados de Cair LTDA','Bêbeados de Cair',35.00,'051-999-8888','Rua Fundo da Garrafa','667','Fundos',5.00,'N','b','b','g.kothe@hotmail.com','S','F','A','2016-08-23 21:29:01'),(2,1,'Distribuidora Ela Robou meu Caminhão LTDA','Roubou meu Caminhão',25.00,'051-883821234','Rua Sem o caminhão','143','Na árvore',6.50,'N','a','a',NULL,'S','S',NULL,NULL);
/*!40000 ALTER TABLE `distribuidora` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribuidora_bairro_entrega`
--

DROP TABLE IF EXISTS `distribuidora_bairro_entrega`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribuidora_bairro_entrega` (
  `ID_DISTR_BAIRRO` int(11) NOT NULL AUTO_INCREMENT,
  `COD_BAIRRO` int(11) NOT NULL,
  `ID_DISTRIBUIDORA` int(11) NOT NULL,
  `VAL_TELE_ENTREGA` decimal(12,2) DEFAULT NULL,
  `FLAG_TELEBAIRRO` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_DISTR_BAIRRO`),
  KEY `FK_REFERENCE_3` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_4` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_3` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_4` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`)
) ENGINE=InnoDB AUTO_INCREMENT=37 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_bairro_entrega`
--

LOCK TABLES `distribuidora_bairro_entrega` WRITE;
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` DISABLE KEYS */;
INSERT INTO `distribuidora_bairro_entrega` (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (1,1,1,0.00,'N'),(2,2,1,0.00,'N'),(3,3,1,0.00,'N'),(4,4,1,0.00,'N'),(5,5,1,0.00,'N'),(6,6,1,0.00,'N'),(7,7,1,0.00,'N'),(8,8,1,0.00,'N'),(9,9,1,0.00,'N'),(10,10,1,0.00,'N'),(11,11,1,0.00,'N'),(12,12,1,0.00,'N'),(13,13,1,0.00,'N'),(14,14,1,0.00,'N'),(15,15,1,0.00,'N'),(16,16,1,0.00,'N'),(17,17,1,0.00,'N'),(18,18,1,0.00,'N'),(19,19,1,0.00,'N'),(20,20,1,0.00,'N'),(21,21,1,0.00,'N'),(22,22,1,0.00,'N'),(23,23,1,0.00,'N'),(24,24,1,0.00,'N'),(25,25,1,0.00,'N'),(26,26,1,0.00,'N'),(27,27,1,0.00,'N'),(28,28,1,0.00,'N'),(29,29,1,0.00,'N'),(30,30,1,0.00,'N'),(31,31,1,0.00,'N'),(32,32,1,0.00,'N'),(33,33,1,0.00,'N'),(34,34,1,0.00,'N'),(35,35,1,0.00,'N'),(36,36,1,0.00,'N');
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribuidora_horario_dia_entre`
--

DROP TABLE IF EXISTS `distribuidora_horario_dia_entre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribuidora_horario_dia_entre` (
  `ID_HORARIO` int(11) NOT NULL AUTO_INCREMENT,
  `ID_DISTRIBUIDORA` int(11) NOT NULL,
  `COD_DIA` int(11) NOT NULL,
  `ID_DISTR_BAIRRO` int(11) DEFAULT NULL,
  `HORARIO_INI` time DEFAULT NULL,
  `HORARIO_FIM` time DEFAULT NULL,
  PRIMARY KEY (`ID_HORARIO`),
  KEY `FK_REFERENCE_20` (`ID_DISTR_BAIRRO`),
  KEY `FK_REFERENCE_8` (`COD_DIA`),
  CONSTRAINT `FK_REFERENCE_20` FOREIGN KEY (`ID_DISTR_BAIRRO`) REFERENCES `distribuidora_bairro_entrega` (`ID_DISTR_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_8` FOREIGN KEY (`COD_DIA`) REFERENCES `dias_semana` (`COD_DIA`)
) ENGINE=InnoDB AUTO_INCREMENT=505 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_horario_dia_entre`
--

LOCK TABLES `distribuidora_horario_dia_entre` WRITE;
/*!40000 ALTER TABLE `distribuidora_horario_dia_entre` DISABLE KEYS */;
INSERT INTO `distribuidora_horario_dia_entre` (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (1,1,1,1,'09:00:00','15:00:00'),(2,1,1,1,'13:25:00','20:00:00'),(3,1,2,1,'09:00:00','15:00:00'),(4,1,2,1,'13:25:00','20:00:00'),(5,1,3,1,'09:00:00','15:00:00'),(6,1,3,1,'13:25:00','20:00:00'),(7,1,4,1,'09:00:00','15:00:00'),(8,1,4,1,'13:25:00','20:00:00'),(9,1,5,1,'09:00:00','15:00:00'),(10,1,5,1,'13:25:00','20:00:00'),(11,1,6,1,'09:00:00','15:00:00'),(12,1,6,1,'13:25:00','20:00:00'),(13,1,7,1,'09:00:00','15:00:00'),(14,1,7,1,'13:25:00','20:00:00'),(15,1,1,2,'09:00:00','15:00:00'),(16,1,1,2,'13:25:00','20:00:00'),(17,1,2,2,'09:00:00','15:00:00'),(18,1,2,2,'13:25:00','20:00:00'),(19,1,3,2,'09:00:00','15:00:00'),(20,1,3,2,'13:25:00','20:00:00'),(21,1,4,2,'09:00:00','15:00:00'),(22,1,4,2,'13:25:00','20:00:00'),(23,1,5,2,'09:00:00','15:00:00'),(24,1,5,2,'13:25:00','20:00:00'),(25,1,6,2,'09:00:00','15:00:00'),(26,1,6,2,'13:25:00','20:00:00'),(27,1,7,2,'09:00:00','15:00:00'),(28,1,7,2,'13:25:00','20:00:00'),(29,1,1,3,'09:00:00','15:00:00'),(30,1,1,3,'13:25:00','20:00:00'),(31,1,2,3,'09:00:00','15:00:00'),(32,1,2,3,'13:25:00','20:00:00'),(33,1,3,3,'09:00:00','15:00:00'),(34,1,3,3,'13:25:00','20:00:00'),(35,1,4,3,'09:00:00','15:00:00'),(36,1,4,3,'13:25:00','20:00:00'),(37,1,5,3,'09:00:00','15:00:00'),(38,1,5,3,'13:25:00','20:00:00'),(39,1,6,3,'09:00:00','15:00:00'),(40,1,6,3,'13:25:00','20:00:00'),(41,1,7,3,'09:00:00','15:00:00'),(42,1,7,3,'13:25:00','20:00:00'),(43,1,1,4,'09:00:00','15:00:00'),(44,1,1,4,'13:25:00','20:00:00'),(45,1,2,4,'09:00:00','15:00:00'),(46,1,2,4,'13:25:00','20:00:00'),(47,1,3,4,'09:00:00','15:00:00'),(48,1,3,4,'13:25:00','20:00:00'),(49,1,4,4,'09:00:00','15:00:00'),(50,1,4,4,'13:25:00','20:00:00'),(51,1,5,4,'09:00:00','15:00:00'),(52,1,5,4,'13:25:00','20:00:00'),(53,1,6,4,'09:00:00','15:00:00'),(54,1,6,4,'13:25:00','20:00:00'),(55,1,7,4,'09:00:00','15:00:00'),(56,1,7,4,'13:25:00','20:00:00'),(57,1,1,5,'09:00:00','15:00:00'),(58,1,1,5,'13:25:00','20:00:00'),(59,1,2,5,'09:00:00','15:00:00'),(60,1,2,5,'13:25:00','20:00:00'),(61,1,3,5,'09:00:00','15:00:00'),(62,1,3,5,'13:25:00','20:00:00'),(63,1,4,5,'09:00:00','15:00:00'),(64,1,4,5,'13:25:00','20:00:00'),(65,1,5,5,'09:00:00','15:00:00'),(66,1,5,5,'13:25:00','20:00:00'),(67,1,6,5,'09:00:00','15:00:00'),(68,1,6,5,'13:25:00','20:00:00'),(69,1,7,5,'09:00:00','15:00:00'),(70,1,7,5,'13:25:00','20:00:00'),(71,1,1,6,'09:00:00','15:00:00'),(72,1,1,6,'13:25:00','20:00:00'),(73,1,2,6,'09:00:00','15:00:00'),(74,1,2,6,'13:25:00','20:00:00'),(75,1,3,6,'09:00:00','15:00:00'),(76,1,3,6,'13:25:00','20:00:00'),(77,1,4,6,'09:00:00','15:00:00'),(78,1,4,6,'13:25:00','20:00:00'),(79,1,5,6,'09:00:00','15:00:00'),(80,1,5,6,'13:25:00','20:00:00'),(81,1,6,6,'09:00:00','15:00:00'),(82,1,6,6,'13:25:00','20:00:00'),(83,1,7,6,'09:00:00','15:00:00'),(84,1,7,6,'13:25:00','20:00:00'),(85,1,1,7,'09:00:00','15:00:00'),(86,1,1,7,'13:25:00','20:00:00'),(87,1,2,7,'09:00:00','15:00:00'),(88,1,2,7,'13:25:00','20:00:00'),(89,1,3,7,'09:00:00','15:00:00'),(90,1,3,7,'13:25:00','20:00:00'),(91,1,4,7,'09:00:00','15:00:00'),(92,1,4,7,'13:25:00','20:00:00'),(93,1,5,7,'09:00:00','15:00:00'),(94,1,5,7,'13:25:00','20:00:00'),(95,1,6,7,'09:00:00','15:00:00'),(96,1,6,7,'13:25:00','20:00:00'),(97,1,7,7,'09:00:00','15:00:00'),(98,1,7,7,'13:25:00','20:00:00'),(99,1,1,8,'09:00:00','15:00:00'),(100,1,1,8,'13:25:00','20:00:00'),(101,1,2,8,'09:00:00','15:00:00'),(102,1,2,8,'13:25:00','20:00:00'),(103,1,3,8,'09:00:00','15:00:00'),(104,1,3,8,'13:25:00','20:00:00'),(105,1,4,8,'09:00:00','15:00:00'),(106,1,4,8,'13:25:00','20:00:00'),(107,1,5,8,'09:00:00','15:00:00'),(108,1,5,8,'13:25:00','20:00:00'),(109,1,6,8,'09:00:00','15:00:00'),(110,1,6,8,'13:25:00','20:00:00'),(111,1,7,8,'09:00:00','15:00:00'),(112,1,7,8,'13:25:00','20:00:00'),(113,1,1,9,'09:00:00','15:00:00'),(114,1,1,9,'13:25:00','20:00:00'),(115,1,2,9,'09:00:00','15:00:00'),(116,1,2,9,'13:25:00','20:00:00'),(117,1,3,9,'09:00:00','15:00:00'),(118,1,3,9,'13:25:00','20:00:00'),(119,1,4,9,'09:00:00','15:00:00'),(120,1,4,9,'13:25:00','20:00:00'),(121,1,5,9,'09:00:00','15:00:00'),(122,1,5,9,'13:25:00','20:00:00'),(123,1,6,9,'09:00:00','15:00:00'),(124,1,6,9,'13:25:00','20:00:00'),(125,1,7,9,'09:00:00','15:00:00'),(126,1,7,9,'13:25:00','20:00:00'),(127,1,1,10,'09:00:00','15:00:00'),(128,1,1,10,'13:25:00','20:00:00'),(129,1,2,10,'09:00:00','15:00:00'),(130,1,2,10,'13:25:00','20:00:00'),(131,1,3,10,'09:00:00','15:00:00'),(132,1,3,10,'13:25:00','20:00:00'),(133,1,4,10,'09:00:00','15:00:00'),(134,1,4,10,'13:25:00','20:00:00'),(135,1,5,10,'09:00:00','15:00:00'),(136,1,5,10,'13:25:00','20:00:00'),(137,1,6,10,'09:00:00','15:00:00'),(138,1,6,10,'13:25:00','20:00:00'),(139,1,7,10,'09:00:00','15:00:00'),(140,1,7,10,'13:25:00','20:00:00'),(141,1,1,11,'09:00:00','15:00:00'),(142,1,1,11,'13:25:00','20:00:00'),(143,1,2,11,'09:00:00','15:00:00'),(144,1,2,11,'13:25:00','20:00:00'),(145,1,3,11,'09:00:00','15:00:00'),(146,1,3,11,'13:25:00','20:00:00'),(147,1,4,11,'09:00:00','15:00:00'),(148,1,4,11,'13:25:00','20:00:00'),(149,1,5,11,'09:00:00','15:00:00'),(150,1,5,11,'13:25:00','20:00:00'),(151,1,6,11,'09:00:00','15:00:00'),(152,1,6,11,'13:25:00','20:00:00'),(153,1,7,11,'09:00:00','15:00:00'),(154,1,7,11,'13:25:00','20:00:00'),(155,1,1,12,'09:00:00','15:00:00'),(156,1,1,12,'13:25:00','20:00:00'),(157,1,2,12,'09:00:00','15:00:00'),(158,1,2,12,'13:25:00','20:00:00'),(159,1,3,12,'09:00:00','15:00:00'),(160,1,3,12,'13:25:00','20:00:00'),(161,1,4,12,'09:00:00','15:00:00'),(162,1,4,12,'13:25:00','20:00:00'),(163,1,5,12,'09:00:00','15:00:00'),(164,1,5,12,'13:25:00','20:00:00'),(165,1,6,12,'09:00:00','15:00:00'),(166,1,6,12,'13:25:00','20:00:00'),(167,1,7,12,'09:00:00','15:00:00'),(168,1,7,12,'13:25:00','20:00:00'),(169,1,1,13,'09:00:00','15:00:00'),(170,1,1,13,'13:25:00','20:00:00'),(171,1,2,13,'09:00:00','15:00:00'),(172,1,2,13,'13:25:00','20:00:00'),(173,1,3,13,'09:00:00','15:00:00'),(174,1,3,13,'13:25:00','20:00:00'),(175,1,4,13,'09:00:00','15:00:00'),(176,1,4,13,'13:25:00','20:00:00'),(177,1,5,13,'09:00:00','15:00:00'),(178,1,5,13,'13:25:00','20:00:00'),(179,1,6,13,'09:00:00','15:00:00'),(180,1,6,13,'13:25:00','20:00:00'),(181,1,7,13,'09:00:00','15:00:00'),(182,1,7,13,'13:25:00','20:00:00'),(183,1,1,14,'09:00:00','15:00:00'),(184,1,1,14,'13:25:00','20:00:00'),(185,1,2,14,'09:00:00','15:00:00'),(186,1,2,14,'13:25:00','20:00:00'),(187,1,3,14,'09:00:00','15:00:00'),(188,1,3,14,'13:25:00','20:00:00'),(189,1,4,14,'09:00:00','15:00:00'),(190,1,4,14,'13:25:00','20:00:00'),(191,1,5,14,'09:00:00','15:00:00'),(192,1,5,14,'13:25:00','20:00:00'),(193,1,6,14,'09:00:00','15:00:00'),(194,1,6,14,'13:25:00','20:00:00'),(195,1,7,14,'09:00:00','15:00:00'),(196,1,7,14,'13:25:00','20:00:00'),(197,1,1,15,'09:00:00','15:00:00'),(198,1,1,15,'13:25:00','20:00:00'),(199,1,2,15,'09:00:00','15:00:00'),(200,1,2,15,'13:25:00','20:00:00'),(201,1,3,15,'09:00:00','15:00:00'),(202,1,3,15,'13:25:00','20:00:00'),(203,1,4,15,'09:00:00','15:00:00'),(204,1,4,15,'13:25:00','20:00:00'),(205,1,5,15,'09:00:00','15:00:00'),(206,1,5,15,'13:25:00','20:00:00'),(207,1,6,15,'09:00:00','15:00:00'),(208,1,6,15,'13:25:00','20:00:00'),(209,1,7,15,'09:00:00','15:00:00'),(210,1,7,15,'13:25:00','20:00:00'),(211,1,1,16,'09:00:00','15:00:00'),(212,1,1,16,'13:25:00','20:00:00'),(213,1,2,16,'09:00:00','15:00:00'),(214,1,2,16,'13:25:00','20:00:00'),(215,1,3,16,'09:00:00','15:00:00'),(216,1,3,16,'13:25:00','20:00:00'),(217,1,4,16,'09:00:00','15:00:00'),(218,1,4,16,'13:25:00','20:00:00'),(219,1,5,16,'09:00:00','15:00:00'),(220,1,5,16,'13:25:00','20:00:00'),(221,1,6,16,'09:00:00','15:00:00'),(222,1,6,16,'13:25:00','20:00:00'),(223,1,7,16,'09:00:00','15:00:00'),(224,1,7,16,'13:25:00','20:00:00'),(225,1,1,17,'09:00:00','15:00:00'),(226,1,1,17,'13:25:00','20:00:00'),(227,1,2,17,'09:00:00','15:00:00'),(228,1,2,17,'13:25:00','20:00:00'),(229,1,3,17,'09:00:00','15:00:00'),(230,1,3,17,'13:25:00','20:00:00'),(231,1,4,17,'09:00:00','15:00:00'),(232,1,4,17,'13:25:00','20:00:00'),(233,1,5,17,'09:00:00','15:00:00'),(234,1,5,17,'13:25:00','20:00:00'),(235,1,6,17,'09:00:00','15:00:00'),(236,1,6,17,'13:25:00','20:00:00'),(237,1,7,17,'09:00:00','15:00:00'),(238,1,7,17,'13:25:00','20:00:00'),(239,1,1,18,'09:00:00','15:00:00'),(240,1,1,18,'13:25:00','20:00:00'),(241,1,2,18,'09:00:00','15:00:00'),(242,1,2,18,'13:25:00','20:00:00'),(243,1,3,18,'09:00:00','15:00:00'),(244,1,3,18,'13:25:00','20:00:00'),(245,1,4,18,'09:00:00','15:00:00'),(246,1,4,18,'13:25:00','20:00:00'),(247,1,5,18,'09:00:00','15:00:00'),(248,1,5,18,'13:25:00','20:00:00'),(249,1,6,18,'09:00:00','15:00:00'),(250,1,6,18,'13:25:00','20:00:00'),(251,1,7,18,'09:00:00','15:00:00'),(252,1,7,18,'13:25:00','20:00:00'),(253,1,1,19,'09:00:00','15:00:00'),(254,1,1,19,'13:25:00','20:00:00'),(255,1,2,19,'09:00:00','15:00:00'),(256,1,2,19,'13:25:00','20:00:00'),(257,1,3,19,'09:00:00','15:00:00'),(258,1,3,19,'13:25:00','20:00:00'),(259,1,4,19,'09:00:00','15:00:00'),(260,1,4,19,'13:25:00','20:00:00'),(261,1,5,19,'09:00:00','15:00:00'),(262,1,5,19,'13:25:00','20:00:00'),(263,1,6,19,'09:00:00','15:00:00'),(264,1,6,19,'13:25:00','20:00:00'),(265,1,7,19,'09:00:00','15:00:00'),(266,1,7,19,'13:25:00','20:00:00'),(267,1,1,20,'09:00:00','15:00:00'),(268,1,1,20,'13:25:00','20:00:00'),(269,1,2,20,'09:00:00','15:00:00'),(270,1,2,20,'13:25:00','20:00:00'),(271,1,3,20,'09:00:00','15:00:00'),(272,1,3,20,'13:25:00','20:00:00'),(273,1,4,20,'09:00:00','15:00:00'),(274,1,4,20,'13:25:00','20:00:00'),(275,1,5,20,'09:00:00','15:00:00'),(276,1,5,20,'13:25:00','20:00:00'),(277,1,6,20,'09:00:00','15:00:00'),(278,1,6,20,'13:25:00','20:00:00'),(279,1,7,20,'09:00:00','15:00:00'),(280,1,7,20,'13:25:00','20:00:00'),(281,1,1,21,'09:00:00','15:00:00'),(282,1,1,21,'13:25:00','20:00:00'),(283,1,2,21,'09:00:00','15:00:00'),(284,1,2,21,'13:25:00','20:00:00'),(285,1,3,21,'09:00:00','15:00:00'),(286,1,3,21,'13:25:00','20:00:00'),(287,1,4,21,'09:00:00','15:00:00'),(288,1,4,21,'13:25:00','20:00:00'),(289,1,5,21,'09:00:00','15:00:00'),(290,1,5,21,'13:25:00','20:00:00'),(291,1,6,21,'09:00:00','15:00:00'),(292,1,6,21,'13:25:00','20:00:00'),(293,1,7,21,'09:00:00','15:00:00'),(294,1,7,21,'13:25:00','20:00:00'),(295,1,1,22,'09:00:00','15:00:00'),(296,1,1,22,'13:25:00','20:00:00'),(297,1,2,22,'09:00:00','15:00:00'),(298,1,2,22,'13:25:00','20:00:00'),(299,1,3,22,'09:00:00','15:00:00'),(300,1,3,22,'13:25:00','20:00:00'),(301,1,4,22,'09:00:00','15:00:00'),(302,1,4,22,'13:25:00','20:00:00'),(303,1,5,22,'09:00:00','15:00:00'),(304,1,5,22,'13:25:00','20:00:00'),(305,1,6,22,'09:00:00','15:00:00'),(306,1,6,22,'13:25:00','20:00:00'),(307,1,7,22,'09:00:00','15:00:00'),(308,1,7,22,'13:25:00','20:00:00'),(309,1,1,23,'09:00:00','15:00:00'),(310,1,1,23,'13:25:00','20:00:00'),(311,1,2,23,'09:00:00','15:00:00'),(312,1,2,23,'13:25:00','20:00:00'),(313,1,3,23,'09:00:00','15:00:00'),(314,1,3,23,'13:25:00','20:00:00'),(315,1,4,23,'09:00:00','15:00:00'),(316,1,4,23,'13:25:00','20:00:00'),(317,1,5,23,'09:00:00','15:00:00'),(318,1,5,23,'13:25:00','20:00:00'),(319,1,6,23,'09:00:00','15:00:00'),(320,1,6,23,'13:25:00','20:00:00'),(321,1,7,23,'09:00:00','15:00:00'),(322,1,7,23,'13:25:00','20:00:00'),(323,1,1,24,'09:00:00','15:00:00'),(324,1,1,24,'13:25:00','20:00:00'),(325,1,2,24,'09:00:00','15:00:00'),(326,1,2,24,'13:25:00','20:00:00'),(327,1,3,24,'09:00:00','15:00:00'),(328,1,3,24,'13:25:00','20:00:00'),(329,1,4,24,'09:00:00','15:00:00'),(330,1,4,24,'13:25:00','20:00:00'),(331,1,5,24,'09:00:00','15:00:00'),(332,1,5,24,'13:25:00','20:00:00'),(333,1,6,24,'09:00:00','15:00:00'),(334,1,6,24,'13:25:00','20:00:00'),(335,1,7,24,'09:00:00','15:00:00'),(336,1,7,24,'13:25:00','20:00:00'),(337,1,1,25,'09:00:00','15:00:00'),(338,1,1,25,'13:25:00','20:00:00'),(339,1,2,25,'09:00:00','15:00:00'),(340,1,2,25,'13:25:00','20:00:00'),(341,1,3,25,'09:00:00','15:00:00'),(342,1,3,25,'13:25:00','20:00:00'),(343,1,4,25,'09:00:00','15:00:00'),(344,1,4,25,'13:25:00','20:00:00'),(345,1,5,25,'09:00:00','15:00:00'),(346,1,5,25,'13:25:00','20:00:00'),(347,1,6,25,'09:00:00','15:00:00'),(348,1,6,25,'13:25:00','20:00:00'),(349,1,7,25,'09:00:00','15:00:00'),(350,1,7,25,'13:25:00','20:00:00'),(351,1,1,26,'09:00:00','15:00:00'),(352,1,1,26,'13:25:00','20:00:00'),(353,1,2,26,'09:00:00','15:00:00'),(354,1,2,26,'13:25:00','20:00:00'),(355,1,3,26,'09:00:00','15:00:00'),(356,1,3,26,'13:25:00','20:00:00'),(357,1,4,26,'09:00:00','15:00:00'),(358,1,4,26,'13:25:00','20:00:00'),(359,1,5,26,'09:00:00','15:00:00'),(360,1,5,26,'13:25:00','20:00:00'),(361,1,6,26,'09:00:00','15:00:00'),(362,1,6,26,'13:25:00','20:00:00'),(363,1,7,26,'09:00:00','15:00:00'),(364,1,7,26,'13:25:00','20:00:00'),(365,1,1,27,'09:00:00','15:00:00'),(366,1,1,27,'13:25:00','20:00:00'),(367,1,2,27,'09:00:00','15:00:00'),(368,1,2,27,'13:25:00','20:00:00'),(369,1,3,27,'09:00:00','15:00:00'),(370,1,3,27,'13:25:00','20:00:00'),(371,1,4,27,'09:00:00','15:00:00'),(372,1,4,27,'13:25:00','20:00:00'),(373,1,5,27,'09:00:00','15:00:00'),(374,1,5,27,'13:25:00','20:00:00'),(375,1,6,27,'09:00:00','15:00:00'),(376,1,6,27,'13:25:00','20:00:00'),(377,1,7,27,'09:00:00','15:00:00'),(378,1,7,27,'13:25:00','20:00:00'),(379,1,1,28,'09:00:00','15:00:00'),(380,1,1,28,'13:25:00','20:00:00'),(381,1,2,28,'09:00:00','15:00:00'),(382,1,2,28,'13:25:00','20:00:00'),(383,1,3,28,'09:00:00','15:00:00'),(384,1,3,28,'13:25:00','20:00:00'),(385,1,4,28,'09:00:00','15:00:00'),(386,1,4,28,'13:25:00','20:00:00'),(387,1,5,28,'09:00:00','15:00:00'),(388,1,5,28,'13:25:00','20:00:00'),(389,1,6,28,'09:00:00','15:00:00'),(390,1,6,28,'13:25:00','20:00:00'),(391,1,7,28,'09:00:00','15:00:00'),(392,1,7,28,'13:25:00','20:00:00'),(393,1,1,29,'09:00:00','15:00:00'),(394,1,1,29,'13:25:00','20:00:00'),(395,1,2,29,'09:00:00','15:00:00'),(396,1,2,29,'13:25:00','20:00:00'),(397,1,3,29,'09:00:00','15:00:00'),(398,1,3,29,'13:25:00','20:00:00'),(399,1,4,29,'09:00:00','15:00:00'),(400,1,4,29,'13:25:00','20:00:00'),(401,1,5,29,'09:00:00','15:00:00'),(402,1,5,29,'13:25:00','20:00:00'),(403,1,6,29,'09:00:00','15:00:00'),(404,1,6,29,'13:25:00','20:00:00'),(405,1,7,29,'09:00:00','15:00:00'),(406,1,7,29,'13:25:00','20:00:00'),(407,1,1,30,'09:00:00','15:00:00'),(408,1,1,30,'13:25:00','20:00:00'),(409,1,2,30,'09:00:00','15:00:00'),(410,1,2,30,'13:25:00','20:00:00'),(411,1,3,30,'09:00:00','15:00:00'),(412,1,3,30,'13:25:00','20:00:00'),(413,1,4,30,'09:00:00','15:00:00'),(414,1,4,30,'13:25:00','20:00:00'),(415,1,5,30,'09:00:00','15:00:00'),(416,1,5,30,'13:25:00','20:00:00'),(417,1,6,30,'09:00:00','15:00:00'),(418,1,6,30,'13:25:00','20:00:00'),(419,1,7,30,'09:00:00','15:00:00'),(420,1,7,30,'13:25:00','20:00:00'),(421,1,1,31,'09:00:00','15:00:00'),(422,1,1,31,'13:25:00','20:00:00'),(423,1,2,31,'09:00:00','15:00:00'),(424,1,2,31,'13:25:00','20:00:00'),(425,1,3,31,'09:00:00','15:00:00'),(426,1,3,31,'13:25:00','20:00:00'),(427,1,4,31,'09:00:00','15:00:00'),(428,1,4,31,'13:25:00','20:00:00'),(429,1,5,31,'09:00:00','15:00:00'),(430,1,5,31,'13:25:00','20:00:00'),(431,1,6,31,'09:00:00','15:00:00'),(432,1,6,31,'13:25:00','20:00:00'),(433,1,7,31,'09:00:00','15:00:00'),(434,1,7,31,'13:25:00','20:00:00'),(435,1,1,32,'09:00:00','15:00:00'),(436,1,1,32,'13:25:00','20:00:00'),(437,1,2,32,'09:00:00','15:00:00'),(438,1,2,32,'13:25:00','20:00:00'),(439,1,3,32,'09:00:00','15:00:00'),(440,1,3,32,'13:25:00','20:00:00'),(441,1,4,32,'09:00:00','15:00:00'),(442,1,4,32,'13:25:00','20:00:00'),(443,1,5,32,'09:00:00','15:00:00'),(444,1,5,32,'13:25:00','20:00:00'),(445,1,6,32,'09:00:00','15:00:00'),(446,1,6,32,'13:25:00','20:00:00'),(447,1,7,32,'09:00:00','15:00:00'),(448,1,7,32,'13:25:00','20:00:00'),(449,1,1,33,'09:00:00','15:00:00'),(450,1,1,33,'13:25:00','20:00:00'),(451,1,2,33,'09:00:00','15:00:00'),(452,1,2,33,'13:25:00','20:00:00'),(453,1,3,33,'09:00:00','15:00:00'),(454,1,3,33,'13:25:00','20:00:00'),(455,1,4,33,'09:00:00','15:00:00'),(456,1,4,33,'13:25:00','20:00:00'),(457,1,5,33,'09:00:00','15:00:00'),(458,1,5,33,'13:25:00','20:00:00'),(459,1,6,33,'09:00:00','15:00:00'),(460,1,6,33,'13:25:00','20:00:00'),(461,1,7,33,'09:00:00','15:00:00'),(462,1,7,33,'13:25:00','20:00:00'),(463,1,1,34,'09:00:00','15:00:00'),(464,1,1,34,'13:25:00','20:00:00'),(465,1,2,34,'09:00:00','15:00:00'),(466,1,2,34,'13:25:00','20:00:00'),(467,1,3,34,'09:00:00','15:00:00'),(468,1,3,34,'13:25:00','20:00:00'),(469,1,4,34,'09:00:00','15:00:00'),(470,1,4,34,'13:25:00','20:00:00'),(471,1,5,34,'09:00:00','15:00:00'),(472,1,5,34,'13:25:00','20:00:00'),(473,1,6,34,'09:00:00','15:00:00'),(474,1,6,34,'13:25:00','20:00:00'),(475,1,7,34,'09:00:00','15:00:00'),(476,1,7,34,'13:25:00','20:00:00'),(477,1,1,35,'09:00:00','15:00:00'),(478,1,1,35,'13:25:00','20:00:00'),(479,1,2,35,'09:00:00','15:00:00'),(480,1,2,35,'13:25:00','20:00:00'),(481,1,3,35,'09:00:00','15:00:00'),(482,1,3,35,'13:25:00','20:00:00'),(483,1,4,35,'09:00:00','15:00:00'),(484,1,4,35,'13:25:00','20:00:00'),(485,1,5,35,'09:00:00','15:00:00'),(486,1,5,35,'13:25:00','20:00:00'),(487,1,6,35,'09:00:00','15:00:00'),(488,1,6,35,'13:25:00','20:00:00'),(489,1,7,35,'09:00:00','15:00:00'),(490,1,7,35,'13:25:00','20:00:00'),(491,1,1,36,'09:00:00','15:00:00'),(492,1,1,36,'13:25:00','20:00:00'),(493,1,2,36,'09:00:00','15:00:00'),(494,1,2,36,'13:25:00','20:00:00'),(495,1,3,36,'09:00:00','15:00:00'),(496,1,3,36,'13:25:00','20:00:00'),(497,1,4,36,'09:00:00','15:00:00'),(498,1,4,36,'13:25:00','20:00:00'),(499,1,5,36,'09:00:00','15:00:00'),(500,1,5,36,'13:25:00','20:00:00'),(501,1,6,36,'09:00:00','15:00:00'),(502,1,6,36,'13:25:00','20:00:00'),(503,1,7,36,'09:00:00','15:00:00'),(504,1,7,36,'13:25:00','20:00:00');
/*!40000 ALTER TABLE `distribuidora_horario_dia_entre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `motivos_recusa`
--

DROP TABLE IF EXISTS `motivos_recusa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `motivos_recusa` (
  `COD_MOTIVO` int(11) NOT NULL AUTO_INCREMENT,
  `DESC_MOTIVO` text NOT NULL,
  PRIMARY KEY (`COD_MOTIVO`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motivos_recusa`
--

LOCK TABLES `motivos_recusa` WRITE;
/*!40000 ALTER TABLE `motivos_recusa` DISABLE KEYS */;
INSERT INTO `motivos_recusa` (`COD_MOTIVO`, `DESC_MOTIVO`) VALUES (1,'Falta de estoque'),(2,'Falta de motoboy'),(3,'Bairro não acessivel'),(4,'Chuva de sapos'),(5,'Zumbis'),(6,'To comendo gente');
/*!40000 ALTER TABLE `motivos_recusa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido`
--

DROP TABLE IF EXISTS `pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido` (
  `ID_PEDIDO` bigint(20) NOT NULL AUTO_INCREMENT,
  `ID_DISTRIBUIDORA` int(11) DEFAULT NULL,
  `ID_USUARIO` bigint(20) DEFAULT NULL,
  `DATA_PEDIDO` datetime DEFAULT NULL,
  `FLAG_STATUS` char(1) DEFAULT NULL,
  `VAL_TOTALPROD` decimal(12,2) DEFAULT NULL,
  `VAL_ENTREGA` decimal(12,2) DEFAULT NULL,
  `DATA_PEDIDO_RESPOSTA` datetime DEFAULT NULL,
  `NUM_PED` bigint(20) DEFAULT NULL,
  `COD_BAIRRO` int(11) DEFAULT NULL,
  `NUM_TELEFONECONTATO_CLIENTE` varchar(50) DEFAULT NULL,
  `TEMPO_ESTIMADO_ENTREGA` time DEFAULT NULL,
  `DESC_ENDERECO_ENTREGA` text,
  `DESC_ENDERECO_NUM_ENTREGA` varchar(20) DEFAULT NULL,
  `DESC_ENDERECO_COMPLEMENTO_ENTREGA` varchar(20) DEFAULT NULL,
  `flag_vizualizado` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_PEDIDO`),
  KEY `FK_REFERENCE_13` (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_15` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_17` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_13` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_15` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_17` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`, `DATA_PEDIDO_RESPOSTA`, `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`, `TEMPO_ESTIMADO_ENTREGA`, `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, `flag_vizualizado`) VALUES (1,1,1,'2016-08-02 13:10:00','A',49.00,5.00,'2016-08-18 20:50:03',1,2,'9999','01:00:00','Treta',NULL,NULL,NULL),(2,1,1,'2016-05-06 16:35:00','R',85.00,5.00,'2016-08-18 20:50:14',2,2,'999999934','02:06:00','Treta2',NULL,NULL,NULL);
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_item`
--

DROP TABLE IF EXISTS `pedido_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido_item` (
  `ID_PEDIDO_ITEM` int(8) NOT NULL AUTO_INCREMENT,
  `ID_PEDIDO` bigint(20) NOT NULL,
  `SEQ_ITEM` int(11) NOT NULL,
  `VAL_UNIT` decimal(12,2) DEFAULT NULL,
  `ID_PROD` int(11) NOT NULL,
  `QTD_PROD` bigint(20) DEFAULT NULL,
  PRIMARY KEY (`ID_PEDIDO_ITEM`),
  KEY `FK_REFERENCE_14` (`ID_PEDIDO`),
  KEY `FK_REFERENCE_16` (`ID_PROD`),
  CONSTRAINT `FK_REFERENCE_14` FOREIGN KEY (`ID_PEDIDO`) REFERENCES `pedido` (`ID_PEDIDO`),
  CONSTRAINT `FK_REFERENCE_16` FOREIGN KEY (`ID_PROD`) REFERENCES `produtos` (`ID_PROD`)
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_item`
--

LOCK TABLES `pedido_item` WRITE;
/*!40000 ALTER TABLE `pedido_item` DISABLE KEYS */;
INSERT INTO `pedido_item` (`ID_PEDIDO_ITEM`, `ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) VALUES (1,1,1,3.50,1,14),(2,2,1,3.50,1,14),(3,2,2,4.00,2,7);
/*!40000 ALTER TABLE `pedido_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_motivos_recusa`
--

DROP TABLE IF EXISTS `pedido_motivos_recusa`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido_motivos_recusa` (
  `ID_PEDIDO_MOTIVO_RECUSA` bigint(20) NOT NULL AUTO_INCREMENT,
  `ID_PEDIDO` bigint(20) NOT NULL,
  `COD_MOTIVO` int(11) NOT NULL,
  PRIMARY KEY (`ID_PEDIDO_MOTIVO_RECUSA`),
  KEY `FK_REFERENCE_18` (`COD_MOTIVO`),
  KEY `FK_REFERENCE_19` (`ID_PEDIDO`),
  CONSTRAINT `FK_REFERENCE_18` FOREIGN KEY (`COD_MOTIVO`) REFERENCES `motivos_recusa` (`COD_MOTIVO`),
  CONSTRAINT `FK_REFERENCE_19` FOREIGN KEY (`ID_PEDIDO`) REFERENCES `pedido` (`ID_PEDIDO`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_motivos_recusa`
--

LOCK TABLES `pedido_motivos_recusa` WRITE;
/*!40000 ALTER TABLE `pedido_motivos_recusa` DISABLE KEYS */;
INSERT INTO `pedido_motivos_recusa` (`ID_PEDIDO_MOTIVO_RECUSA`, `ID_PEDIDO`, `COD_MOTIVO`) VALUES (1,2,3);
/*!40000 ALTER TABLE `pedido_motivos_recusa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos`
--

DROP TABLE IF EXISTS `produtos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `produtos` (
  `ID_PROD` int(11) NOT NULL AUTO_INCREMENT,
  `DESC_PROD` text NOT NULL,
  `DESC_ABREVIADO` varchar(100) NOT NULL,
  `FLAG_ATIVO` char(1) NOT NULL,
  PRIMARY KEY (`ID_PROD`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`) VALUES (1,'Polar Lata 357ml Pilsen','Polar lata','S'),(2,'Brahma Lata 357ml Pilsen','Brahma lata','S'),(3,'Brahma Latão 473ml Pilsen','Brahma Latão','S'),(4,'Dado Bier Latão 473ml Weissbier','Dado Bier Latão Weiss','S'),(5,'Polar Garrafa 1l Pilsen','Polar Litro','S'),(6,'Skol Beats Longneck 357ml','Skol Beats','S'),(7,'Bavária Lata 357ml','Bavária Lata','N');
/*!40000 ALTER TABLE `produtos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `produtos_distribuidora`
--

DROP TABLE IF EXISTS `produtos_distribuidora`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `produtos_distribuidora` (
  `ID_PROD_DIST` bigint(20) NOT NULL AUTO_INCREMENT,
  `ID_PROD` int(11) NOT NULL,
  `ID_DISTRIBUIDORA` int(11) NOT NULL,
  `VAL_PROD` decimal(12,2) DEFAULT NULL,
  `FLAG_ATIVO` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_PROD_DIST`),
  KEY `FK_REFERENCE_1` (`ID_PROD`),
  KEY `FK_REFERENCE_2` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_1` FOREIGN KEY (`ID_PROD`) REFERENCES `produtos` (`ID_PROD`),
  CONSTRAINT `FK_REFERENCE_2` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`)
) ENGINE=InnoDB AUTO_INCREMENT=9 DEFAULT CHARSET=utf8 COMMENT='Fks: id_prod_distr ou por (id_distribuidora + id_produto). ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos_distribuidora`
--

LOCK TABLES `produtos_distribuidora` WRITE;
/*!40000 ALTER TABLE `produtos_distribuidora` DISABLE KEYS */;
INSERT INTO `produtos_distribuidora` (`ID_PROD_DIST`, `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES (1,1,1,3.50,'S'),(2,2,1,4.00,'S'),(3,7,1,6.00,'N'),(4,1,2,3.00,'S'),(5,3,2,7.00,'S'),(6,5,2,6.00,'N'),(7,3,1,2.00,'S'),(8,5,1,6.00,'N');
/*!40000 ALTER TABLE `produtos_distribuidora` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_parametros`
--

DROP TABLE IF EXISTS `sys_parametros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_parametros` (
  `COD_CIDADE` int(11) DEFAULT NULL,
  `ID_USUARIO_ADMIN` bigint(20) DEFAULT NULL,
  `FLAG_MANUTENCAO` char(1) DEFAULT NULL,
  `DESC_KEY` text,
  `SEGS_TESTE_AJAX` bigint(20) DEFAULT NULL,
  KEY `FK_REFERENCE_26` (`COD_CIDADE`),
  KEY `FK_REFERENCE_27` (`ID_USUARIO_ADMIN`),
  CONSTRAINT `FK_REFERENCE_26` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_27` FOREIGN KEY (`ID_USUARIO_ADMIN`) REFERENCES `usuario` (`ID_USUARIO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sys_parametros`
--

LOCK TABLES `sys_parametros` WRITE;
/*!40000 ALTER TABLE `sys_parametros` DISABLE KEYS */;
INSERT INTO `sys_parametros` (`COD_CIDADE`, `ID_USUARIO_ADMIN`, `FLAG_MANUTENCAO`, `DESC_KEY`, `SEGS_TESTE_AJAX`) VALUES (1,1,'N','LxLnKYU3QbR6HmLHCyAGKQ',30);
/*!40000 ALTER TABLE `sys_parametros` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuario` (
  `ID_USUARIO` bigint(20) NOT NULL AUTO_INCREMENT,
  `DESC_NOME` text,
  `DESC_TELEFONE` varchar(50) DEFAULT NULL,
  `DESC_USER` varchar(50) DEFAULT NULL,
  `DESC_SENHA` varchar(50) DEFAULT NULL,
  `DESC_EMAIL` varchar(150) DEFAULT NULL,
  `COD_CIDADE` int(11) DEFAULT NULL,
  `DESC_ENDERECO` text,
  `DESC_ENDERECO_NUM` varchar(20) DEFAULT NULL,
  `DESC_ENDERECO_COMPLEMENTO` varchar(20) DEFAULT NULL,
  `COD_BAIRRO` int(11) DEFAULT NULL,
  `DESC_CARTAO` varchar(20) DEFAULT NULL,
  `DATA_EXP_MES` int(11) DEFAULT NULL,
  `DATA_EXP_ANO` int(11) DEFAULT NULL,
  `DESC_CARDHOLDERNAME` text,
  `PAY_ID` text,
  `DESC_CPF` varchar(15) DEFAULT NULL,
  PRIMARY KEY (`ID_USUARIO`),
  KEY `FK_REFERENCE_5` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_7` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_5` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_7` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`ID_USUARIO`, `DESC_NOME`, `DESC_TELEFONE`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_ENDERECO_NUM`, `DESC_ENDERECO_COMPLEMENTO`, `COD_BAIRRO`, `DESC_CARTAO`, `DATA_EXP_MES`, `DATA_EXP_ANO`, `DESC_CARDHOLDERNAME`, `PAY_ID`, `DESC_CPF`) VALUES (1,'Teste da Silvasssss','999999','f','d','werty@werty.comssss',1,'Rua alguma do bairro',NULL,NULL,3,'',NULL,NULL,NULL,NULL,NULL);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2016-08-24  0:49:09
