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
INSERT INTO `carrinho` (`ID_CARRINHO`, `ID_USUARIO`, `COD_BAIRRO`, `DATA_CRIACAO`) VALUES (1,1,1,NULL);
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
  `VAL_PROD` decimal(12,2) DEFAULT NULL,
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
INSERT INTO `carrinho_item` (`ID_CARRINHO`, `SEQ_ITEM`, `ID_PROD_DIST`, `QTD`, `VAL_PROD`) VALUES (1,1,1,4,3.50),(1,2,2,3,4.00),(1,3,3,5,6.00);
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
INSERT INTO `dias_semana` (`COD_DIA`, `DESC_DIA`) VALUES (1,'Segunda-feira'),(2,'Terça-feira'),(3,'Quarta-feira'),(4,'Quinta-feira'),(5,'Sexta-feira'),(6,'Sábado'),(7,'Domingo'),(8,'Feriado/Especial');
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
  `flag_entre_ret` char(1) DEFAULT NULL,
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
INSERT INTO `distribuidora` (`ID_DISTRIBUIDORA`, `COD_CIDADE`, `DESC_RAZAO_SOCIAL`, `DESC_NOME_ABREV`, `VAL_ENTREGA_MIN`, `DESC_TELEFONE`, `DESC_ENDERECO`, `NUM_ENDEREC`, `DESC_COMPLEMENTO`, `VAL_TELE_ENTREGA`, `FLAG_CUSTOM`, `DESC_LOGIN`, `DESC_SENHA`, `DESC_MAIL`, `FLAG_ATIVO_MASTER`, `FLAG_ATIVO`, `FLAG_MODOPAGAMENTO`, `DATE_LASTAJAX`, `flag_entre_ret`) VALUES (1,1,'Distribuidora Bêbados de Cair LTDA','Bêbeados de Cair',35.00,'051-999-8888','Rua Fundo da Garrafa','667','Fundos',5.00,'N','b','b','g.kothe@hotmail.com','S','S','A','2016-10-11 09:59:02','A'),(2,1,'Distribuidora Ela Robou meu Caminhão LTDA','Roubou meu Caminhão',25.00,'051-883821234','Rua Sem o caminhão','143','Na árvore',6.50,'S','a','a','','S','S','A','2016-10-11 09:59:02','A');
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
) ENGINE=InnoDB AUTO_INCREMENT=73 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_bairro_entrega`
--

LOCK TABLES `distribuidora_bairro_entrega` WRITE;
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` DISABLE KEYS */;
INSERT INTO `distribuidora_bairro_entrega` (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (1,36,1,0.00,'N'),(2,35,1,0.00,'N'),(3,34,1,0.00,'N'),(4,33,1,0.00,'N'),(5,32,1,0.00,'N'),(6,31,1,0.00,'N'),(7,30,1,0.00,'N'),(8,29,1,0.00,'N'),(9,28,1,0.00,'N'),(10,27,1,0.00,'N'),(11,26,1,0.00,'N'),(12,25,1,0.00,'N'),(13,24,1,0.00,'N'),(14,23,1,0.00,'N'),(15,22,1,0.00,'N'),(16,21,1,0.00,'N'),(17,20,1,0.00,'N'),(18,19,1,0.00,'N'),(19,18,1,0.00,'N'),(20,17,1,0.00,'N'),(21,16,1,0.00,'N'),(22,15,1,0.00,'N'),(23,14,1,0.00,'N'),(24,13,1,0.00,'N'),(25,12,1,0.00,'N'),(26,11,1,0.00,'N'),(27,10,1,0.00,'N'),(28,9,1,0.00,'N'),(29,8,1,0.00,'N'),(30,7,1,0.00,'N'),(31,6,1,0.00,'N'),(32,5,1,0.00,'N'),(33,4,1,0.00,'N'),(34,3,1,0.00,'N'),(35,2,1,0.00,'N'),(36,1,1,0.00,'N'),(37,36,2,0.00,'N'),(38,35,2,0.00,'N'),(39,34,2,0.00,'N'),(40,33,2,0.00,'N'),(41,32,2,0.00,'N'),(42,31,2,0.00,'N'),(43,30,2,0.00,'N'),(44,29,2,0.00,'N'),(45,28,2,0.00,'N'),(46,27,2,0.00,'N'),(47,26,2,0.00,'N'),(48,25,2,0.00,'N'),(49,24,2,0.00,'N'),(50,23,2,0.00,'N'),(51,22,2,0.00,'N'),(52,21,2,0.00,'N'),(53,20,2,0.00,'N'),(54,19,2,0.00,'N'),(55,18,2,0.00,'N'),(56,17,2,0.00,'N'),(57,16,2,0.00,'N'),(58,15,2,0.00,'N'),(59,14,2,0.00,'N'),(60,13,2,0.00,'N'),(61,12,2,0.00,'N'),(62,11,2,0.00,'N'),(63,10,2,0.00,'N'),(64,9,2,0.00,'N'),(65,8,2,0.00,'N'),(66,7,2,0.00,'N'),(67,6,2,0.00,'N'),(68,5,2,0.00,'N'),(69,4,2,0.00,'N'),(70,3,2,0.00,'N'),(71,2,2,0.00,'N'),(72,1,2,0.00,'N');
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `distribuidora_horario_dia_entre`
--

DROP TABLE IF EXISTS `distribuidora_horario_dia_entre`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `distribuidora_horario_dia_entre` (
  `ID_HORARIO` bigint(20) NOT NULL,
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_horario_dia_entre`
--

LOCK TABLES `distribuidora_horario_dia_entre` WRITE;
/*!40000 ALTER TABLE `distribuidora_horario_dia_entre` DISABLE KEYS */;
INSERT INTO `distribuidora_horario_dia_entre` (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (1,1,1,1,'08:00:00','20:00:59'),(2,1,2,1,'08:00:00','20:00:59'),(3,1,3,1,'08:00:00','20:00:59'),(4,1,4,1,'08:00:00','20:00:59'),(5,1,5,1,'08:00:00','20:00:59'),(6,1,6,1,'08:00:00','20:00:59'),(7,1,7,1,'08:00:00','20:00:59'),(8,1,8,1,'08:00:00','20:00:59'),(9,1,1,2,'08:00:00','20:00:59'),(10,1,2,2,'08:00:00','20:00:59'),(11,1,3,2,'08:00:00','20:00:59'),(12,1,4,2,'08:00:00','20:00:59'),(13,1,5,2,'08:00:00','20:00:59'),(14,1,6,2,'08:00:00','20:00:59'),(15,1,7,2,'08:00:00','20:00:59'),(16,1,8,2,'08:00:00','20:00:59'),(17,1,1,3,'08:00:00','20:00:59'),(18,1,2,3,'08:00:00','20:00:59'),(19,1,3,3,'08:00:00','20:00:59'),(20,1,4,3,'08:00:00','20:00:59'),(21,1,5,3,'08:00:00','20:00:59'),(22,1,6,3,'08:00:00','20:00:59'),(23,1,7,3,'08:00:00','20:00:59'),(24,1,8,3,'08:00:00','20:00:59'),(25,1,1,4,'08:00:00','20:00:59'),(26,1,2,4,'08:00:00','20:00:59'),(27,1,3,4,'08:00:00','20:00:59'),(28,1,4,4,'08:00:00','20:00:59'),(29,1,5,4,'08:00:00','20:00:59'),(30,1,6,4,'08:00:00','20:00:59'),(31,1,7,4,'08:00:00','20:00:59'),(32,1,8,4,'08:00:00','20:00:59'),(33,1,1,5,'08:00:00','20:00:59'),(34,1,2,5,'08:00:00','20:00:59'),(35,1,3,5,'08:00:00','20:00:59'),(36,1,4,5,'08:00:00','20:00:59'),(37,1,5,5,'08:00:00','20:00:59'),(38,1,6,5,'08:00:00','20:00:59'),(39,1,7,5,'08:00:00','20:00:59'),(40,1,8,5,'08:00:00','20:00:59'),(41,1,1,6,'08:00:00','20:00:59'),(42,1,2,6,'08:00:00','20:00:59'),(43,1,3,6,'08:00:00','20:00:59'),(44,1,4,6,'08:00:00','20:00:59'),(45,1,5,6,'08:00:00','20:00:59'),(46,1,6,6,'08:00:00','20:00:59'),(47,1,7,6,'08:00:00','20:00:59'),(48,1,8,6,'08:00:00','20:00:59'),(49,1,1,7,'08:00:00','20:00:59'),(50,1,2,7,'08:00:00','20:00:59'),(51,1,3,7,'08:00:00','20:00:59'),(52,1,4,7,'08:00:00','20:00:59'),(53,1,5,7,'08:00:00','20:00:59'),(54,1,6,7,'08:00:00','20:00:59'),(55,1,7,7,'08:00:00','20:00:59'),(56,1,8,7,'08:00:00','20:00:59'),(57,1,1,8,'08:00:00','20:00:59'),(58,1,2,8,'08:00:00','20:00:59'),(59,1,3,8,'08:00:00','20:00:59'),(60,1,4,8,'08:00:00','20:00:59'),(61,1,5,8,'08:00:00','20:00:59'),(62,1,6,8,'08:00:00','20:00:59'),(63,1,7,8,'08:00:00','20:00:59'),(64,1,8,8,'08:00:00','20:00:59'),(65,1,1,9,'08:00:00','20:00:59'),(66,1,2,9,'08:00:00','20:00:59'),(67,1,3,9,'08:00:00','20:00:59'),(68,1,4,9,'08:00:00','20:00:59'),(69,1,5,9,'08:00:00','20:00:59'),(70,1,6,9,'08:00:00','20:00:59'),(71,1,7,9,'08:00:00','20:00:59'),(72,1,8,9,'08:00:00','20:00:59'),(73,1,1,10,'08:00:00','20:00:59'),(74,1,2,10,'08:00:00','20:00:59'),(75,1,3,10,'08:00:00','20:00:59'),(76,1,4,10,'08:00:00','20:00:59'),(77,1,5,10,'08:00:00','20:00:59'),(78,1,6,10,'08:00:00','20:00:59'),(79,1,7,10,'08:00:00','20:00:59'),(80,1,8,10,'08:00:00','20:00:59'),(81,1,1,11,'08:00:00','20:00:59'),(82,1,2,11,'08:00:00','20:00:59'),(83,1,3,11,'08:00:00','20:00:59'),(84,1,4,11,'08:00:00','20:00:59'),(85,1,5,11,'08:00:00','20:00:59'),(86,1,6,11,'08:00:00','20:00:59'),(87,1,7,11,'08:00:00','20:00:59'),(88,1,8,11,'08:00:00','20:00:59'),(89,1,1,12,'08:00:00','20:00:59'),(90,1,2,12,'08:00:00','20:00:59'),(91,1,3,12,'08:00:00','20:00:59'),(92,1,4,12,'08:00:00','20:00:59'),(93,1,5,12,'08:00:00','20:00:59'),(94,1,6,12,'08:00:00','20:00:59'),(95,1,7,12,'08:00:00','20:00:59'),(96,1,8,12,'08:00:00','20:00:59'),(97,1,1,13,'08:00:00','20:00:59'),(98,1,2,13,'08:00:00','20:00:59'),(99,1,3,13,'08:00:00','20:00:59'),(100,1,4,13,'08:00:00','20:00:59'),(101,1,5,13,'08:00:00','20:00:59'),(102,1,6,13,'08:00:00','20:00:59'),(103,1,7,13,'08:00:00','20:00:59'),(104,1,8,13,'08:00:00','20:00:59'),(105,1,1,14,'08:00:00','20:00:59'),(106,1,2,14,'08:00:00','20:00:59'),(107,1,3,14,'08:00:00','20:00:59'),(108,1,4,14,'08:00:00','20:00:59'),(109,1,5,14,'08:00:00','20:00:59'),(110,1,6,14,'08:00:00','20:00:59'),(111,1,7,14,'08:00:00','20:00:59'),(112,1,8,14,'08:00:00','20:00:59'),(113,1,1,15,'08:00:00','20:00:59'),(114,1,2,15,'08:00:00','20:00:59'),(115,1,3,15,'08:00:00','20:00:59'),(116,1,4,15,'08:00:00','20:00:59'),(117,1,5,15,'08:00:00','20:00:59'),(118,1,6,15,'08:00:00','20:00:59'),(119,1,7,15,'08:00:00','20:00:59'),(120,1,8,15,'08:00:00','20:00:59'),(121,1,1,16,'08:00:00','20:00:59'),(122,1,2,16,'08:00:00','20:00:59'),(123,1,3,16,'08:00:00','20:00:59'),(124,1,4,16,'08:00:00','20:00:59'),(125,1,5,16,'08:00:00','20:00:59'),(126,1,6,16,'08:00:00','20:00:59'),(127,1,7,16,'08:00:00','20:00:59'),(128,1,8,16,'08:00:00','20:00:59'),(129,1,1,17,'08:00:00','20:00:59'),(130,1,2,17,'08:00:00','20:00:59'),(131,1,3,17,'08:00:00','20:00:59'),(132,1,4,17,'08:00:00','20:00:59'),(133,1,5,17,'08:00:00','20:00:59'),(134,1,6,17,'08:00:00','20:00:59'),(135,1,7,17,'08:00:00','20:00:59'),(136,1,8,17,'08:00:00','20:00:59'),(137,1,1,18,'08:00:00','20:00:59'),(138,1,2,18,'08:00:00','20:00:59'),(139,1,3,18,'08:00:00','20:00:59'),(140,1,4,18,'08:00:00','20:00:59'),(141,1,5,18,'08:00:00','20:00:59'),(142,1,6,18,'08:00:00','20:00:59'),(143,1,7,18,'08:00:00','20:00:59'),(144,1,8,18,'08:00:00','20:00:59'),(145,1,1,19,'08:00:00','20:00:59'),(146,1,2,19,'08:00:00','20:00:59'),(147,1,3,19,'08:00:00','20:00:59'),(148,1,4,19,'08:00:00','20:00:59'),(149,1,5,19,'08:00:00','20:00:59'),(150,1,6,19,'08:00:00','20:00:59'),(151,1,7,19,'08:00:00','20:00:59'),(152,1,8,19,'08:00:00','20:00:59'),(153,1,1,20,'08:00:00','20:00:59'),(154,1,2,20,'08:00:00','20:00:59'),(155,1,3,20,'08:00:00','20:00:59'),(156,1,4,20,'08:00:00','20:00:59'),(157,1,5,20,'08:00:00','20:00:59'),(158,1,6,20,'08:00:00','20:00:59'),(159,1,7,20,'08:00:00','20:00:59'),(160,1,8,20,'08:00:00','20:00:59'),(161,1,1,21,'08:00:00','20:00:59'),(162,1,2,21,'08:00:00','20:00:59'),(163,1,3,21,'08:00:00','20:00:59'),(164,1,4,21,'08:00:00','20:00:59'),(165,1,5,21,'08:00:00','20:00:59'),(166,1,6,21,'08:00:00','20:00:59'),(167,1,7,21,'08:00:00','20:00:59'),(168,1,8,21,'08:00:00','20:00:59'),(169,1,1,22,'08:00:00','20:00:59'),(170,1,2,22,'08:00:00','20:00:59'),(171,1,3,22,'08:00:00','20:00:59'),(172,1,4,22,'08:00:00','20:00:59'),(173,1,5,22,'08:00:00','20:00:59'),(174,1,6,22,'08:00:00','20:00:59'),(175,1,7,22,'08:00:00','20:00:59'),(176,1,8,22,'08:00:00','20:00:59'),(177,1,1,23,'08:00:00','20:00:59'),(178,1,2,23,'08:00:00','20:00:59'),(179,1,3,23,'08:00:00','20:00:59'),(180,1,4,23,'08:00:00','20:00:59'),(181,1,5,23,'08:00:00','20:00:59'),(182,1,6,23,'08:00:00','20:00:59'),(183,1,7,23,'08:00:00','20:00:59'),(184,1,8,23,'08:00:00','20:00:59'),(185,1,1,24,'08:00:00','20:00:59'),(186,1,2,24,'08:00:00','20:00:59'),(187,1,3,24,'08:00:00','20:00:59'),(188,1,4,24,'08:00:00','20:00:59'),(189,1,5,24,'08:00:00','20:00:59'),(190,1,6,24,'08:00:00','20:00:59'),(191,1,7,24,'08:00:00','20:00:59'),(192,1,8,24,'08:00:00','20:00:59'),(193,1,1,25,'08:00:00','20:00:59'),(194,1,2,25,'08:00:00','20:00:59'),(195,1,3,25,'08:00:00','20:00:59'),(196,1,4,25,'08:00:00','20:00:59'),(197,1,5,25,'08:00:00','20:00:59'),(198,1,6,25,'08:00:00','20:00:59'),(199,1,7,25,'08:00:00','20:00:59'),(200,1,8,25,'08:00:00','20:00:59'),(201,1,1,26,'08:00:00','20:00:59'),(202,1,2,26,'08:00:00','20:00:59'),(203,1,3,26,'08:00:00','20:00:59'),(204,1,4,26,'08:00:00','20:00:59'),(205,1,5,26,'08:00:00','20:00:59'),(206,1,6,26,'08:00:00','20:00:59'),(207,1,7,26,'08:00:00','20:00:59'),(208,1,8,26,'08:00:00','20:00:59'),(209,1,1,27,'08:00:00','20:00:59'),(210,1,2,27,'08:00:00','20:00:59'),(211,1,3,27,'08:00:00','20:00:59'),(212,1,4,27,'08:00:00','20:00:59'),(213,1,5,27,'08:00:00','20:00:59'),(214,1,6,27,'08:00:00','20:00:59'),(215,1,7,27,'08:00:00','20:00:59'),(216,1,8,27,'08:00:00','20:00:59'),(217,1,1,28,'08:00:00','20:00:59'),(218,1,2,28,'08:00:00','20:00:59'),(219,1,3,28,'08:00:00','20:00:59'),(220,1,4,28,'08:00:00','20:00:59'),(221,1,5,28,'08:00:00','20:00:59'),(222,1,6,28,'08:00:00','20:00:59'),(223,1,7,28,'08:00:00','20:00:59'),(224,1,8,28,'08:00:00','20:00:59'),(225,1,1,29,'08:00:00','20:00:59'),(226,1,2,29,'08:00:00','20:00:59'),(227,1,3,29,'08:00:00','20:00:59'),(228,1,4,29,'08:00:00','20:00:59'),(229,1,5,29,'08:00:00','20:00:59'),(230,1,6,29,'08:00:00','20:00:59'),(231,1,7,29,'08:00:00','20:00:59'),(232,1,8,29,'08:00:00','20:00:59'),(233,1,1,30,'08:00:00','20:00:59'),(234,1,2,30,'08:00:00','20:00:59'),(235,1,3,30,'08:00:00','20:00:59'),(236,1,4,30,'08:00:00','20:00:59'),(237,1,5,30,'08:00:00','20:00:59'),(238,1,6,30,'08:00:00','20:00:59'),(239,1,7,30,'08:00:00','20:00:59'),(240,1,8,30,'08:00:00','20:00:59'),(241,1,1,31,'08:00:00','20:00:59'),(242,1,2,31,'08:00:00','20:00:59'),(243,1,3,31,'08:00:00','20:00:59'),(244,1,4,31,'08:00:00','20:00:59'),(245,1,5,31,'08:00:00','20:00:59'),(246,1,6,31,'08:00:00','20:00:59'),(247,1,7,31,'08:00:00','20:00:59'),(248,1,8,31,'08:00:00','20:00:59'),(249,1,1,32,'08:00:00','20:00:59'),(250,1,2,32,'08:00:00','20:00:59'),(251,1,3,32,'08:00:00','20:00:59'),(252,1,4,32,'08:00:00','20:00:59'),(253,1,5,32,'08:00:00','20:00:59'),(254,1,6,32,'08:00:00','20:00:59'),(255,1,7,32,'08:00:00','20:00:59'),(256,1,8,32,'08:00:00','20:00:59'),(257,1,1,33,'08:00:00','20:00:59'),(258,1,2,33,'08:00:00','20:00:59'),(259,1,3,33,'08:00:00','20:00:59'),(260,1,4,33,'08:00:00','20:00:59'),(261,1,5,33,'08:00:00','20:00:59'),(262,1,6,33,'08:00:00','20:00:59'),(263,1,7,33,'08:00:00','20:00:59'),(264,1,8,33,'08:00:00','20:00:59'),(265,1,1,34,'08:00:00','20:00:59'),(266,1,2,34,'08:00:00','20:00:59'),(267,1,3,34,'08:00:00','20:00:59'),(268,1,4,34,'08:00:00','20:00:59'),(269,1,5,34,'08:00:00','20:00:59'),(270,1,6,34,'08:00:00','20:00:59'),(271,1,7,34,'08:00:00','20:00:59'),(272,1,8,34,'08:00:00','20:00:59'),(273,1,1,35,'08:00:00','20:00:59'),(274,1,2,35,'08:00:00','20:00:59'),(275,1,3,35,'08:00:00','20:00:59'),(276,1,4,35,'08:00:00','20:00:59'),(277,1,5,35,'08:00:00','20:00:59'),(278,1,6,35,'08:00:00','20:00:59'),(279,1,7,35,'08:00:00','20:00:59'),(280,1,8,35,'08:00:00','20:00:59'),(281,1,1,36,'08:00:00','20:00:59'),(282,1,2,36,'08:00:00','20:00:59'),(283,1,3,36,'08:00:00','20:00:59'),(284,1,4,36,'08:00:00','20:00:59'),(285,1,5,36,'08:00:00','20:00:59'),(286,1,6,36,'08:00:00','20:00:59'),(287,1,7,36,'08:00:00','20:00:59'),(288,1,8,36,'08:00:00','20:00:59'),(289,2,1,37,'08:00:00','22:00:59'),(290,2,2,37,'08:00:00','22:00:59'),(291,2,3,37,'08:00:00','22:00:59'),(292,2,4,37,'08:00:00','22:00:59'),(293,2,5,37,'08:00:00','22:00:59'),(294,2,6,37,'08:00:00','22:00:59'),(295,2,7,37,'08:00:00','22:00:59'),(296,2,8,37,'08:00:00','22:00:59'),(297,2,1,38,'08:00:00','22:00:59'),(298,2,2,38,'08:00:00','22:00:59'),(299,2,3,38,'08:00:00','22:00:59'),(300,2,4,38,'08:00:00','22:00:59'),(301,2,5,38,'08:00:00','22:00:59'),(302,2,6,38,'08:00:00','22:00:59'),(303,2,7,38,'08:00:00','22:00:59'),(304,2,8,38,'08:00:00','22:00:59'),(305,2,1,39,'08:00:00','22:00:59'),(306,2,2,39,'08:00:00','22:00:59'),(307,2,3,39,'08:00:00','22:00:59'),(308,2,4,39,'08:00:00','22:00:59'),(309,2,5,39,'08:00:00','22:00:59'),(310,2,6,39,'08:00:00','22:00:59'),(311,2,7,39,'08:00:00','22:00:59'),(312,2,8,39,'08:00:00','22:00:59'),(313,2,1,40,'08:00:00','22:00:59'),(314,2,2,40,'08:00:00','22:00:59'),(315,2,3,40,'08:00:00','22:00:59'),(316,2,4,40,'08:00:00','22:00:59'),(317,2,5,40,'08:00:00','22:00:59'),(318,2,6,40,'08:00:00','22:00:59'),(319,2,7,40,'08:00:00','22:00:59'),(320,2,8,40,'08:00:00','22:00:59'),(321,2,1,41,'08:00:00','22:00:59'),(322,2,2,41,'08:00:00','22:00:59'),(323,2,3,41,'08:00:00','22:00:59'),(324,2,4,41,'08:00:00','22:00:59'),(325,2,5,41,'08:00:00','22:00:59'),(326,2,6,41,'08:00:00','22:00:59'),(327,2,7,41,'08:00:00','22:00:59'),(328,2,8,41,'08:00:00','22:00:59'),(329,2,1,42,'08:00:00','22:00:59'),(330,2,2,42,'08:00:00','22:00:59'),(331,2,3,42,'08:00:00','22:00:59'),(332,2,4,42,'08:00:00','22:00:59'),(333,2,5,42,'08:00:00','22:00:59'),(334,2,6,42,'08:00:00','22:00:59'),(335,2,7,42,'08:00:00','22:00:59'),(336,2,8,42,'08:00:00','22:00:59'),(337,2,1,43,'08:00:00','22:00:59'),(338,2,2,43,'08:00:00','22:00:59'),(339,2,3,43,'08:00:00','22:00:59'),(340,2,4,43,'08:00:00','22:00:59'),(341,2,5,43,'08:00:00','22:00:59'),(342,2,6,43,'08:00:00','22:00:59'),(343,2,7,43,'08:00:00','22:00:59'),(344,2,8,43,'08:00:00','22:00:59'),(345,2,1,44,'08:00:00','22:00:59'),(346,2,2,44,'08:00:00','22:00:59'),(347,2,3,44,'08:00:00','22:00:59'),(348,2,4,44,'08:00:00','22:00:59'),(349,2,5,44,'08:00:00','22:00:59'),(350,2,6,44,'08:00:00','22:00:59'),(351,2,7,44,'08:00:00','22:00:59'),(352,2,8,44,'08:00:00','22:00:59'),(353,2,1,45,'08:00:00','22:00:59'),(354,2,2,45,'08:00:00','22:00:59'),(355,2,3,45,'08:00:00','22:00:59'),(356,2,4,45,'08:00:00','22:00:59'),(357,2,5,45,'08:00:00','22:00:59'),(358,2,6,45,'08:00:00','22:00:59'),(359,2,7,45,'08:00:00','22:00:59'),(360,2,8,45,'08:00:00','22:00:59'),(361,2,1,46,'08:00:00','22:00:59'),(362,2,2,46,'08:00:00','22:00:59'),(363,2,3,46,'08:00:00','22:00:59'),(364,2,4,46,'08:00:00','22:00:59'),(365,2,5,46,'08:00:00','22:00:59'),(366,2,6,46,'08:00:00','22:00:59'),(367,2,7,46,'08:00:00','22:00:59'),(368,2,8,46,'08:00:00','22:00:59'),(369,2,1,47,'08:00:00','22:00:59'),(370,2,2,47,'08:00:00','22:00:59'),(371,2,3,47,'08:00:00','22:00:59'),(372,2,4,47,'08:00:00','22:00:59'),(373,2,5,47,'08:00:00','22:00:59'),(374,2,6,47,'08:00:00','22:00:59'),(375,2,7,47,'08:00:00','22:00:59'),(376,2,8,47,'08:00:00','22:00:59'),(377,2,1,48,'08:00:00','22:00:59'),(378,2,2,48,'08:00:00','22:00:59'),(379,2,3,48,'08:00:00','22:00:59'),(380,2,4,48,'08:00:00','22:00:59'),(381,2,5,48,'08:00:00','22:00:59'),(382,2,6,48,'08:00:00','22:00:59'),(383,2,7,48,'08:00:00','22:00:59'),(384,2,8,48,'08:00:00','22:00:59'),(385,2,1,49,'08:00:00','22:00:59'),(386,2,2,49,'08:00:00','22:00:59'),(387,2,3,49,'08:00:00','22:00:59'),(388,2,4,49,'08:00:00','22:00:59'),(389,2,5,49,'08:00:00','22:00:59'),(390,2,6,49,'08:00:00','22:00:59'),(391,2,7,49,'08:00:00','22:00:59'),(392,2,8,49,'08:00:00','22:00:59'),(393,2,1,50,'08:00:00','22:00:59'),(394,2,2,50,'08:00:00','22:00:59'),(395,2,3,50,'08:00:00','22:00:59'),(396,2,4,50,'08:00:00','22:00:59'),(397,2,5,50,'08:00:00','22:00:59'),(398,2,6,50,'08:00:00','22:00:59'),(399,2,7,50,'08:00:00','22:00:59'),(400,2,8,50,'08:00:00','22:00:59'),(401,2,1,51,'08:00:00','22:00:59'),(402,2,2,51,'08:00:00','22:00:59'),(403,2,3,51,'08:00:00','22:00:59'),(404,2,4,51,'08:00:00','22:00:59'),(405,2,5,51,'08:00:00','22:00:59'),(406,2,6,51,'08:00:00','22:00:59'),(407,2,7,51,'08:00:00','22:00:59'),(408,2,8,51,'08:00:00','22:00:59'),(409,2,1,52,'08:00:00','22:00:59'),(410,2,2,52,'08:00:00','22:00:59'),(411,2,3,52,'08:00:00','22:00:59'),(412,2,4,52,'08:00:00','22:00:59'),(413,2,5,52,'08:00:00','22:00:59'),(414,2,6,52,'08:00:00','22:00:59'),(415,2,7,52,'08:00:00','22:00:59'),(416,2,8,52,'08:00:00','22:00:59'),(417,2,1,53,'08:00:00','22:00:59'),(418,2,2,53,'08:00:00','22:00:59'),(419,2,3,53,'08:00:00','22:00:59'),(420,2,4,53,'08:00:00','22:00:59'),(421,2,5,53,'08:00:00','22:00:59'),(422,2,6,53,'08:00:00','22:00:59'),(423,2,7,53,'08:00:00','22:00:59'),(424,2,8,53,'08:00:00','22:00:59'),(425,2,1,54,'08:00:00','22:00:59'),(426,2,2,54,'08:00:00','22:00:59'),(427,2,3,54,'08:00:00','22:00:59'),(428,2,4,54,'08:00:00','22:00:59'),(429,2,5,54,'08:00:00','22:00:59'),(430,2,6,54,'08:00:00','22:00:59'),(431,2,7,54,'08:00:00','22:00:59'),(432,2,8,54,'08:00:00','22:00:59'),(433,2,1,55,'08:00:00','22:00:59'),(434,2,2,55,'08:00:00','22:00:59'),(435,2,3,55,'08:00:00','22:00:59'),(436,2,4,55,'08:00:00','22:00:59'),(437,2,5,55,'08:00:00','22:00:59'),(438,2,6,55,'08:00:00','22:00:59'),(439,2,7,55,'08:00:00','22:00:59'),(440,2,8,55,'08:00:00','22:00:59'),(441,2,1,56,'08:00:00','22:00:59'),(442,2,2,56,'08:00:00','22:00:59'),(443,2,3,56,'08:00:00','22:00:59'),(444,2,4,56,'08:00:00','22:00:59'),(445,2,5,56,'08:00:00','22:00:59'),(446,2,6,56,'08:00:00','22:00:59'),(447,2,7,56,'08:00:00','22:00:59'),(448,2,8,56,'08:00:00','22:00:59'),(449,2,1,57,'08:00:00','22:00:59'),(450,2,2,57,'08:00:00','22:00:59'),(451,2,3,57,'08:00:00','22:00:59'),(452,2,4,57,'08:00:00','22:00:59'),(453,2,5,57,'08:00:00','22:00:59'),(454,2,6,57,'08:00:00','22:00:59'),(455,2,7,57,'08:00:00','22:00:59'),(456,2,8,57,'08:00:00','22:00:59'),(457,2,1,58,'08:00:00','22:00:59'),(458,2,2,58,'08:00:00','22:00:59'),(459,2,3,58,'08:00:00','22:00:59'),(460,2,4,58,'08:00:00','22:00:59'),(461,2,5,58,'08:00:00','22:00:59'),(462,2,6,58,'08:00:00','22:00:59'),(463,2,7,58,'08:00:00','22:00:59'),(464,2,8,58,'08:00:00','22:00:59'),(465,2,1,59,'08:00:00','22:00:59'),(466,2,2,59,'08:00:00','22:00:59'),(467,2,3,59,'08:00:00','22:00:59'),(468,2,4,59,'08:00:00','22:00:59'),(469,2,5,59,'08:00:00','22:00:59'),(470,2,6,59,'08:00:00','22:00:59'),(471,2,7,59,'08:00:00','22:00:59'),(472,2,8,59,'08:00:00','22:00:59'),(473,2,1,60,'08:00:00','22:00:59'),(474,2,2,60,'08:00:00','22:00:59'),(475,2,3,60,'08:00:00','22:00:59'),(476,2,4,60,'08:00:00','22:00:59'),(477,2,5,60,'08:00:00','22:00:59'),(478,2,6,60,'08:00:00','22:00:59'),(479,2,7,60,'08:00:00','22:00:59'),(480,2,8,60,'08:00:00','22:00:59'),(481,2,1,61,'08:00:00','22:00:59'),(482,2,2,61,'08:00:00','22:00:59'),(483,2,3,61,'08:00:00','22:00:59'),(484,2,4,61,'08:00:00','22:00:59'),(485,2,5,61,'08:00:00','22:00:59'),(486,2,6,61,'08:00:00','22:00:59'),(487,2,7,61,'08:00:00','22:00:59'),(488,2,8,61,'08:00:00','22:00:59'),(489,2,1,62,'08:00:00','22:00:59'),(490,2,2,62,'08:00:00','22:00:59'),(491,2,3,62,'08:00:00','22:00:59'),(492,2,4,62,'08:00:00','22:00:59'),(493,2,5,62,'08:00:00','22:00:59'),(494,2,6,62,'08:00:00','22:00:59'),(495,2,7,62,'08:00:00','22:00:59'),(496,2,8,62,'08:00:00','22:00:59'),(497,2,1,63,'08:00:00','22:00:59'),(498,2,2,63,'08:00:00','22:00:59'),(499,2,3,63,'08:00:00','22:00:59'),(500,2,4,63,'08:00:00','22:00:59'),(501,2,5,63,'08:00:00','22:00:59'),(502,2,6,63,'08:00:00','22:00:59'),(503,2,7,63,'08:00:00','22:00:59'),(504,2,8,63,'08:00:00','22:00:59'),(505,2,1,64,'08:00:00','22:00:59'),(506,2,2,64,'08:00:00','22:00:59'),(507,2,3,64,'08:00:00','22:00:59'),(508,2,4,64,'08:00:00','22:00:59'),(509,2,5,64,'08:00:00','22:00:59'),(510,2,6,64,'08:00:00','22:00:59'),(511,2,7,64,'08:00:00','22:00:59'),(512,2,8,64,'08:00:00','22:00:59'),(513,2,1,65,'08:00:00','22:00:59'),(514,2,2,65,'08:00:00','22:00:59'),(515,2,3,65,'08:00:00','22:00:59'),(516,2,4,65,'08:00:00','22:00:59'),(517,2,5,65,'08:00:00','22:00:59'),(518,2,6,65,'08:00:00','22:00:59'),(519,2,7,65,'08:00:00','22:00:59'),(520,2,8,65,'08:00:00','22:00:59'),(521,2,1,66,'08:00:00','22:00:59'),(522,2,2,66,'08:00:00','22:00:59'),(523,2,3,66,'08:00:00','22:00:59'),(524,2,4,66,'08:00:00','22:00:59'),(525,2,5,66,'08:00:00','22:00:59'),(526,2,6,66,'08:00:00','22:00:59'),(527,2,7,66,'08:00:00','22:00:59'),(528,2,8,66,'08:00:00','22:00:59'),(529,2,1,67,'08:00:00','22:00:59'),(530,2,2,67,'08:00:00','22:00:59'),(531,2,3,67,'08:00:00','22:00:59'),(532,2,4,67,'08:00:00','22:00:59'),(533,2,5,67,'08:00:00','22:00:59'),(534,2,6,67,'08:00:00','22:00:59'),(535,2,7,67,'08:00:00','22:00:59'),(536,2,8,67,'08:00:00','22:00:59'),(537,2,1,68,'08:00:00','22:00:59'),(538,2,2,68,'08:00:00','22:00:59'),(539,2,3,68,'08:00:00','22:00:59'),(540,2,4,68,'08:00:00','22:00:59'),(541,2,5,68,'08:00:00','22:00:59'),(542,2,6,68,'08:00:00','22:00:59'),(543,2,7,68,'08:00:00','22:00:59'),(544,2,8,68,'08:00:00','22:00:59'),(545,2,1,69,'08:00:00','22:00:59'),(546,2,2,69,'08:00:00','22:00:59'),(547,2,3,69,'08:00:00','22:00:59'),(548,2,4,69,'08:00:00','22:00:59'),(549,2,5,69,'08:00:00','22:00:59'),(550,2,6,69,'08:00:00','22:00:59'),(551,2,7,69,'08:00:00','22:00:59'),(552,2,8,69,'08:00:00','22:00:59'),(553,2,1,70,'08:00:00','22:00:59'),(554,2,2,70,'08:00:00','22:00:59'),(555,2,3,70,'08:00:00','22:00:59'),(556,2,4,70,'08:00:00','22:00:59'),(557,2,5,70,'08:00:00','22:00:59'),(558,2,6,70,'08:00:00','22:00:59'),(559,2,7,70,'08:00:00','22:00:59'),(560,2,8,70,'08:00:00','22:00:59'),(561,2,1,71,'08:00:00','22:00:59'),(562,2,2,71,'08:00:00','22:00:59'),(563,2,3,71,'08:00:00','22:00:59'),(564,2,4,71,'08:00:00','22:00:59'),(565,2,5,71,'08:00:00','22:00:59'),(566,2,6,71,'08:00:00','22:00:59'),(567,2,7,71,'08:00:00','22:00:59'),(568,2,8,71,'08:00:00','22:00:59'),(569,2,1,72,'08:00:00','22:00:59'),(570,2,2,72,'08:00:00','22:00:59'),(571,2,3,72,'08:00:00','22:00:59'),(572,2,4,72,'08:00:00','22:00:59'),(573,2,5,72,'08:00:00','22:00:59'),(574,2,6,72,'08:00:00','22:00:59'),(575,2,7,72,'08:00:00','22:00:59'),(576,2,8,72,'08:00:00','22:00:59');
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
  `FLAG_MODOPAGAMENTO` char(1) DEFAULT NULL,
  `DESC_CARTAO` varchar(20) DEFAULT NULL,
  `NOME_PESSOA` text,
  `PAG_TOKEN` text,
  `PAG_MAIL` text,
  `PAG_PAYID_TIPOCARTAO` text,
  `FLAG_PEDIDO_RET_ENTRE` char(1) DEFAULT NULL,
  `TEMPO_ESTIMADO_DESEJADO` time DEFAULT NULL,
  PRIMARY KEY (`ID_PEDIDO`),
  KEY `FK_REFERENCE_13` (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_15` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_17` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_13` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_15` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_17` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`)
) ENGINE=InnoDB AUTO_INCREMENT=59 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`, `DATA_PEDIDO_RESPOSTA`, `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`, `TEMPO_ESTIMADO_ENTREGA`, `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, `flag_vizualizado`, `FLAG_MODOPAGAMENTO`, `DESC_CARTAO`, `NOME_PESSOA`, `PAG_TOKEN`, `PAG_MAIL`, `PAG_PAYID_TIPOCARTAO`, `FLAG_PEDIDO_RET_ENTRE`, `TEMPO_ESTIMADO_DESEJADO`) VALUES (1,2,17,'2016-10-10 15:21:09','E',105.00,6.50,'2016-10-10 16:11:10',1,10,'5184970310','01:00:00','borges de medeiros 715','999','sobrado','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'T','01:00:00'),(2,2,17,'2016-10-10 15:21:47','R',150.00,0.00,'2016-10-10 16:20:28',2,10,'5184970310',NULL,'','','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'L','00:00:00'),(3,2,17,'2016-10-10 16:18:24','A',90.00,6.50,NULL,3,10,'5184970310',NULL,'borges de medeiros 715','999','sobrado','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'T','01:30:00'),(4,2,17,'2016-10-10 16:21:21','R',150.00,0.00,'2016-10-10 16:21:35',4,10,'5184970310',NULL,'','','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'L','00:00:00'),(5,2,17,'2016-10-10 16:22:22','R',90.00,6.50,'2016-10-10 16:22:41',5,10,'5184970310',NULL,'borges de medeiros 715','999','sobrado','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'T','01:00:00'),(6,1,17,'2016-10-10 16:23:36','R',105.00,0.00,'2016-10-10 16:24:46',6,10,'5184970310',NULL,'','','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'L','00:00:00'),(7,1,17,'2016-10-10 16:29:27','A',105.00,0.00,NULL,7,10,'5184970310',NULL,'','','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'L','00:00:00'),(8,2,17,'2016-10-11 09:23:52','A',90.00,0.00,NULL,8,10,'5184970310',NULL,'','','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'L','00:00:00');
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
) ENGINE=InnoDB AUTO_INCREMENT=136 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_item`
--

LOCK TABLES `pedido_item` WRITE;
/*!40000 ALTER TABLE `pedido_item` DISABLE KEYS */;
INSERT INTO `pedido_item` (`ID_PEDIDO_ITEM`, `ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`) VALUES (128,1,1,3.00,1,35),(129,2,1,3.00,1,50),(130,3,1,3.00,1,30),(131,4,1,3.00,1,50),(132,5,1,3.00,1,30),(133,6,1,3.50,1,30),(134,7,1,3.50,1,30),(135,8,1,3.00,1,30);
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
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_motivos_recusa`
--

LOCK TABLES `pedido_motivos_recusa` WRITE;
/*!40000 ALTER TABLE `pedido_motivos_recusa` DISABLE KEYS */;
INSERT INTO `pedido_motivos_recusa` (`ID_PEDIDO_MOTIVO_RECUSA`, `ID_PEDIDO`, `COD_MOTIVO`) VALUES (6,2,2),(7,4,1),(8,5,4),(9,6,5);
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
INSERT INTO `produtos` (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`) VALUES (1,'Polar Lata 357ml Pilsen cerveja','Polar lata','S'),(2,'Brahma Lata 357ml Pilsen','Brahma lata','S'),(3,'Brahma Latão 473ml Pilsen','Brahma Latão','S'),(4,'Dado Bier Latão 473ml Weissbier','Dado Bier Latão Weiss','S'),(5,'Polar Garrafa 1l Pilsen','Polar Litro','S'),(6,'Skol Beats Longneck 357ml','Skol Beats','S'),(7,'Bavária Lata 357ml','Bavária Lata','N');
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
INSERT INTO `produtos_distribuidora` (`ID_PROD_DIST`, `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES (1,1,1,3.50,'S'),(2,2,1,4.00,'S'),(3,7,1,6.00,'S'),(4,1,2,3.00,'S'),(5,3,2,7.00,'S'),(6,5,2,6.00,'N'),(7,3,1,2.00,'S'),(8,5,1,6.00,'N');
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
  `FACE_APP_ID` bigint(20) DEFAULT NULL,
  `FACE_APP_SECRETKEY` text,
  `FACE_APP_TOKEN` text,
  `FACE_REDIRECT_URI` text,
  `url_system` text,
  `sys_host_name_smtp` text,
  `sys_smtp_port` int(11) DEFAULT NULL,
  `sys_email` text,
  `sys_senha` text,
  `sys_fromemail` text,
  `sys_fromdesc` text,
  `sys_tls` char(1) DEFAULT NULL,
  `id_visitante` bigint(20) DEFAULT NULL,
  `PED_HORASOKEY` int(11) DEFAULT NULL,
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
INSERT INTO `sys_parametros` (`COD_CIDADE`, `ID_USUARIO_ADMIN`, `FLAG_MANUTENCAO`, `DESC_KEY`, `SEGS_TESTE_AJAX`, `FACE_APP_ID`, `FACE_APP_SECRETKEY`, `FACE_APP_TOKEN`, `FACE_REDIRECT_URI`, `url_system`, `sys_host_name_smtp`, `sys_smtp_port`, `sys_email`, `sys_senha`, `sys_fromemail`, `sys_fromdesc`, `sys_tls`, `id_visitante`, `PED_HORASOKEY`) VALUES (1,1,'N','LxLnKYU3QbR6HmLHCyAGKQ',30,175654116193796,'f8e7da8769ea82db5ddf015cb911bd9d','175654116193796|63Vwiy3C7K38l2rkyxTawKs30k4','http://localhost:4400/oauthcallback.html','http://localhost:8080/m_16/','smtp.live.com',587,'g.kothe@hotmail.com','','g.kothe@hotmail.com','TragoAqui','S',2,NULL);
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
  `FLAG_FACEUSER` char(1) DEFAULT NULL,
  `ID_USER_FACE` bigint(20) DEFAULT NULL,
  `FLAG_ATIVADO` char(1) DEFAULT NULL,
  `CHAVE_ATIVACAO` varchar(100) DEFAULT NULL,
  `DESC_NOVOEMAILVALIDACAO` varchar(150) DEFAULT NULL,
  `CHAVE_ATIVACAO_NOVOEMAIL` varchar(100) DEFAULT NULL,
  `FLAG_MAIORIDADE` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_USUARIO`),
  KEY `FK_REFERENCE_5` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_7` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_5` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_7` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`ID_USUARIO`, `DESC_NOME`, `DESC_TELEFONE`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_ENDERECO_NUM`, `DESC_ENDERECO_COMPLEMENTO`, `COD_BAIRRO`, `DESC_CARTAO`, `DATA_EXP_MES`, `DATA_EXP_ANO`, `DESC_CARDHOLDERNAME`, `PAY_ID`, `DESC_CPF`, `FLAG_FACEUSER`, `ID_USER_FACE`, `FLAG_ATIVADO`, `CHAVE_ATIVACAO`, `DESC_NOVOEMAILVALIDACAO`, `CHAVE_ATIVACAO_NOVOEMAIL`, `FLAG_MAIORIDADE`) VALUES (1,'Teste da Silvasssss','999999','f','ty','',1,'Rua alguma do bairro',NULL,NULL,3,'',NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',NULL,'g.kothe@hotmail.com','15b3pdcibe1o24e67kemfn7gvar981idnd8e5or6kscmuu2olf3jdcig6b4d0ejec0n9bc37trilnd7kth5ervfmbee0geam431',NULL),(2,'Visitante',NULL,'v','v',NULL,1,NULL,NULL,NULL,31,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'V',NULL,NULL,NULL,''),(16,'Gabriel Baierle',NULL,'bielbaierle@hotmail.com','gpici799d5ajsfoptdeujot4f3','bielbaierle@hotmail.com',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',1098082656936831,'S',NULL,NULL,NULL,NULL),(17,'Gabriel Dalcin Kothe','5184970310','g.kothe@hotmail.com','gg','h2015929@mvrht.com',1,'borges de medeiros 715','999','sobrado',10,'4235647728025682',9,2016,'APRO','master','55909483200','S',1044925118957062,'S',NULL,NULL,NULL,'S');
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

-- Dump completed on 2016-10-11  9:59:06
