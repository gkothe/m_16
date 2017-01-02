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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `bairros`
--

LOCK TABLES `bairros` WRITE;
/*!40000 ALTER TABLE `bairros` DISABLE KEYS */;
INSERT INTO `bairros` (`COD_BAIRRO`, `COD_CIDADE`, `DESC_BAIRRO`) VALUES (1,1,'Aliança'),(2,1,'Ana Nery'),(3,1,'Arroio Grande'),(4,1,'Avenida'),(5,1,'Belvedere'),(6,1,'Bom Jesus'),(7,1,'Bonfim'),(8,1,'Centro'),(9,1,'Cohab'),(10,1,'Country'),(11,1,'Distrito Industrial'),(12,1,'Dona Carlota'),(13,1,'Esmeralda'),(14,1,'Faxinal Menino Deus'),(15,1,'Germânia'),(16,1,'Goiás'),(17,1,'Harmonia'),(18,1,'Higienópolis'),(19,1,'Independência'),(20,1,'Jardim Europa'),(21,1,'Linha Joao Alves'),(22,1,'Linha Santa Cruz'),(23,1,'Margarida'),(24,1,'Monte Verde'),(25,1,'Pedreira'),(26,1,'Progresso'),(27,1,'Rauber'),(28,1,'Renascença'),(29,1,'Santa Vitória'),(30,1,'Santo Antônio'),(31,1,'Santo Inácio'),(32,1,'Santuário'),(33,1,'São João'),(34,1,'Schulz'),(35,1,'Senai'),(36,1,'Universitário'),(37,1,'Várzea'),(38,1,'Vila Nova ');
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
INSERT INTO `dias_semana` (`COD_DIA`, `DESC_DIA`) VALUES (1,'Segunda-feira'),(2,'Terça-feira'),(3,'Quarta-feira'),(4,'Quinta-feira'),(5,'Sexta-feira'),(6,'Sábado'),(7,'Domingo'),(8,'Feriado/Especial/Customizavel');
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
  `PERC_PAGAMENTO` decimal(12,2) DEFAULT NULL,
  `TXT_OBS_HORA` text,
  `COD_BAIRRO` int(11) DEFAULT NULL,
  `ID_USUARIO_INSERT` bigint(20) DEFAULT NULL,
  `FLAG_TIPOCONTRATO` text,
  PRIMARY KEY (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_6` (`COD_CIDADE`),
  KEY `FK_REFERENCE_30` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_31` (`ID_USUARIO_INSERT`),
  CONSTRAINT `FK_REFERENCE_30` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_31` FOREIGN KEY (`ID_USUARIO_INSERT`) REFERENCES `usuario` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_6` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora`
--

LOCK TABLES `distribuidora` WRITE;
/*!40000 ALTER TABLE `distribuidora` DISABLE KEYS */;
INSERT INTO `distribuidora` (`ID_DISTRIBUIDORA`, `COD_CIDADE`, `DESC_RAZAO_SOCIAL`, `DESC_NOME_ABREV`, `VAL_ENTREGA_MIN`, `DESC_TELEFONE`, `DESC_ENDERECO`, `NUM_ENDEREC`, `DESC_COMPLEMENTO`, `VAL_TELE_ENTREGA`, `FLAG_CUSTOM`, `DESC_LOGIN`, `DESC_SENHA`, `DESC_MAIL`, `FLAG_ATIVO_MASTER`, `FLAG_ATIVO`, `FLAG_MODOPAGAMENTO`, `DATE_LASTAJAX`, `flag_entre_ret`, `PERC_PAGAMENTO`, `TXT_OBS_HORA`, `COD_BAIRRO`, `ID_USUARIO_INSERT`, `FLAG_TIPOCONTRATO`) VALUES (1,1,'Distribuidora Bêbados de Cair LTDA s','Bêbeados de Cair',15.00,'051-999-8888','Rua Fundo da Garrafa','667','Fundos',5.00,'N','b','b','g.kothe@hotmail.com','S','S','A','2017-01-02 14:48:19','A',5.00,'Trabalhamos nos horarios da 14:00 ate as 20:00 dias de semana, e sexta a noite ate domingo a noite estamos abertos 24h.\n',NULL,20,NULL),(2,1,'Distribuidora Ela Robou meu Caminhão LTDA','Roubou meu Caminhão',25.00,'051-883821234','Rua Sem o caminhão','143','Na árvore',6.50,'S','a','a','','S','F','A','2016-12-27 17:53:53','A',5.00,NULL,NULL,21,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_bairro_entrega`
--

LOCK TABLES `distribuidora_bairro_entrega` WRITE;
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` DISABLE KEYS */;
INSERT INTO `distribuidora_bairro_entrega` (`ID_DISTR_BAIRRO`, `COD_BAIRRO`, `ID_DISTRIBUIDORA`, `VAL_TELE_ENTREGA`, `FLAG_TELEBAIRRO`) VALUES (1,1,1,0.00,'N'),(2,2,1,0.00,'N'),(3,3,1,0.00,'N'),(4,4,1,0.00,'N'),(5,5,1,0.00,'N'),(6,6,1,0.00,'N'),(7,7,1,0.00,'N'),(8,8,1,0.00,'N'),(9,9,1,0.00,'N'),(10,10,1,0.00,'N'),(11,11,1,0.00,'N'),(12,12,1,0.00,'N'),(13,13,1,0.00,'N'),(14,14,1,0.00,'N'),(15,15,1,0.00,'N'),(16,16,1,0.00,'N'),(17,17,1,0.00,'N'),(18,18,1,0.00,'N'),(19,19,1,0.00,'N'),(20,20,1,0.00,'N'),(21,21,1,0.00,'N'),(22,22,1,0.00,'N'),(23,23,1,0.00,'N'),(24,24,1,0.00,'N'),(25,25,1,0.00,'N'),(26,26,1,0.00,'N'),(27,27,1,0.00,'N'),(28,28,1,0.00,'N'),(29,29,1,0.00,'N'),(30,30,1,0.00,'N'),(31,31,1,0.00,'N'),(32,32,1,0.00,'N'),(33,33,1,0.00,'N'),(34,34,1,0.00,'N'),(35,35,1,0.00,'N'),(36,36,1,0.00,'N'),(37,37,1,0.00,'N'),(38,38,1,0.00,'N');
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
INSERT INTO `distribuidora_horario_dia_entre` (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (1,1,1,1,'08:00:00','22:00:59'),(2,1,2,1,'08:00:00','22:00:59'),(3,1,3,1,'08:00:00','22:00:59'),(4,1,4,1,'08:00:00','22:00:59'),(5,1,5,1,'08:00:00','22:00:59'),(6,1,6,1,'08:00:00','22:00:59'),(7,1,7,1,'08:00:00','22:00:59'),(8,1,1,2,'08:00:00','22:00:59'),(9,1,2,2,'08:00:00','22:00:59'),(10,1,3,2,'08:00:00','22:00:59'),(11,1,4,2,'08:00:00','22:00:59'),(12,1,5,2,'08:00:00','22:00:59'),(13,1,6,2,'08:00:00','22:00:59'),(14,1,7,2,'08:00:00','22:00:59'),(15,1,1,3,'08:00:00','22:00:59'),(16,1,2,3,'08:00:00','22:00:59'),(17,1,3,3,'08:00:00','22:00:59'),(18,1,4,3,'08:00:00','22:00:59'),(19,1,5,3,'08:00:00','22:00:59'),(20,1,6,3,'08:00:00','22:00:59'),(21,1,7,3,'08:00:00','22:00:59'),(22,1,1,4,'08:00:00','22:00:59'),(23,1,2,4,'08:00:00','22:00:59'),(24,1,3,4,'08:00:00','22:00:59'),(25,1,4,4,'08:00:00','22:00:59'),(26,1,5,4,'08:00:00','22:00:59'),(27,1,6,4,'08:00:00','22:00:59'),(28,1,7,4,'08:00:00','22:00:59'),(29,1,1,5,'08:00:00','22:00:59'),(30,1,2,5,'08:00:00','22:00:59'),(31,1,3,5,'08:00:00','22:00:59'),(32,1,4,5,'08:00:00','22:00:59'),(33,1,5,5,'08:00:00','22:00:59'),(34,1,6,5,'08:00:00','22:00:59'),(35,1,7,5,'08:00:00','22:00:59'),(36,1,1,6,'08:00:00','22:00:59'),(37,1,2,6,'08:00:00','22:00:59'),(38,1,3,6,'08:00:00','22:00:59'),(39,1,4,6,'08:00:00','22:00:59'),(40,1,5,6,'08:00:00','22:00:59'),(41,1,6,6,'08:00:00','22:00:59'),(42,1,7,6,'08:00:00','22:00:59'),(43,1,1,7,'08:00:00','22:00:59'),(44,1,2,7,'08:00:00','22:00:59'),(45,1,3,7,'08:00:00','22:00:59'),(46,1,4,7,'08:00:00','22:00:59'),(47,1,5,7,'08:00:00','22:00:59'),(48,1,6,7,'08:00:00','22:00:59'),(49,1,7,7,'08:00:00','22:00:59'),(50,1,1,8,'08:00:00','22:00:59'),(51,1,2,8,'08:00:00','22:00:59'),(52,1,3,8,'08:00:00','22:00:59'),(53,1,4,8,'08:00:00','22:00:59'),(54,1,5,8,'08:00:00','22:00:59'),(55,1,6,8,'08:00:00','22:00:59'),(56,1,7,8,'08:00:00','22:00:59'),(57,1,1,9,'08:00:00','22:00:59'),(58,1,2,9,'08:00:00','22:00:59'),(59,1,3,9,'08:00:00','22:00:59'),(60,1,4,9,'08:00:00','22:00:59'),(61,1,5,9,'08:00:00','22:00:59'),(62,1,6,9,'08:00:00','22:00:59'),(63,1,7,9,'08:00:00','22:00:59'),(64,1,1,10,'08:00:00','22:00:59'),(65,1,2,10,'08:00:00','22:00:59'),(66,1,3,10,'08:00:00','22:00:59'),(67,1,4,10,'08:00:00','22:00:59'),(68,1,5,10,'08:00:00','22:00:59'),(69,1,6,10,'08:00:00','22:00:59'),(70,1,7,10,'08:00:00','22:00:59'),(71,1,1,11,'08:00:00','22:00:59'),(72,1,2,11,'08:00:00','22:00:59'),(73,1,3,11,'08:00:00','22:00:59'),(74,1,4,11,'08:00:00','22:00:59'),(75,1,5,11,'08:00:00','22:00:59'),(76,1,6,11,'08:00:00','22:00:59'),(77,1,7,11,'08:00:00','22:00:59'),(78,1,1,12,'08:00:00','22:00:59'),(79,1,2,12,'08:00:00','22:00:59'),(80,1,3,12,'08:00:00','22:00:59'),(81,1,4,12,'08:00:00','22:00:59'),(82,1,5,12,'08:00:00','22:00:59'),(83,1,6,12,'08:00:00','22:00:59'),(84,1,7,12,'08:00:00','22:00:59'),(85,1,1,13,'08:00:00','22:00:59'),(86,1,2,13,'08:00:00','22:00:59'),(87,1,3,13,'08:00:00','22:00:59'),(88,1,4,13,'08:00:00','22:00:59'),(89,1,5,13,'08:00:00','22:00:59'),(90,1,6,13,'08:00:00','22:00:59'),(91,1,7,13,'08:00:00','22:00:59'),(92,1,1,14,'08:00:00','22:00:59'),(93,1,2,14,'08:00:00','22:00:59'),(94,1,3,14,'08:00:00','22:00:59'),(95,1,4,14,'08:00:00','22:00:59'),(96,1,5,14,'08:00:00','22:00:59'),(97,1,6,14,'08:00:00','22:00:59'),(98,1,7,14,'08:00:00','22:00:59'),(99,1,1,15,'08:00:00','22:00:59'),(100,1,2,15,'08:00:00','22:00:59'),(101,1,3,15,'08:00:00','22:00:59'),(102,1,4,15,'08:00:00','22:00:59'),(103,1,5,15,'08:00:00','22:00:59'),(104,1,6,15,'08:00:00','22:00:59'),(105,1,7,15,'08:00:00','22:00:59'),(106,1,1,16,'08:00:00','22:00:59'),(107,1,2,16,'08:00:00','22:00:59'),(108,1,3,16,'08:00:00','22:00:59'),(109,1,4,16,'08:00:00','22:00:59'),(110,1,5,16,'08:00:00','22:00:59'),(111,1,6,16,'08:00:00','22:00:59'),(112,1,7,16,'08:00:00','22:00:59'),(113,1,1,17,'08:00:00','22:00:59'),(114,1,2,17,'08:00:00','22:00:59'),(115,1,3,17,'08:00:00','22:00:59'),(116,1,4,17,'08:00:00','22:00:59'),(117,1,5,17,'08:00:00','22:00:59'),(118,1,6,17,'08:00:00','22:00:59'),(119,1,7,17,'08:00:00','22:00:59'),(120,1,1,18,'08:00:00','22:00:59'),(121,1,2,18,'08:00:00','22:00:59'),(122,1,3,18,'08:00:00','22:00:59'),(123,1,4,18,'08:00:00','22:00:59'),(124,1,5,18,'08:00:00','22:00:59'),(125,1,6,18,'08:00:00','22:00:59'),(126,1,7,18,'08:00:00','22:00:59'),(127,1,1,19,'08:00:00','22:00:59'),(128,1,2,19,'08:00:00','22:00:59'),(129,1,3,19,'08:00:00','22:00:59'),(130,1,4,19,'08:00:00','22:00:59'),(131,1,5,19,'08:00:00','22:00:59'),(132,1,6,19,'08:00:00','22:00:59'),(133,1,7,19,'08:00:00','22:00:59'),(134,1,1,20,'08:00:00','22:00:59'),(135,1,2,20,'08:00:00','22:00:59'),(136,1,3,20,'08:00:00','22:00:59'),(137,1,4,20,'08:00:00','22:00:59'),(138,1,5,20,'08:00:00','22:00:59'),(139,1,6,20,'08:00:00','22:00:59'),(140,1,7,20,'08:00:00','22:00:59'),(141,1,1,21,'08:00:00','22:00:59'),(142,1,2,21,'08:00:00','22:00:59'),(143,1,3,21,'08:00:00','22:00:59'),(144,1,4,21,'08:00:00','22:00:59'),(145,1,5,21,'08:00:00','22:00:59'),(146,1,6,21,'08:00:00','22:00:59'),(147,1,7,21,'08:00:00','22:00:59'),(148,1,1,22,'08:00:00','22:00:59'),(149,1,2,22,'08:00:00','22:00:59'),(150,1,3,22,'08:00:00','22:00:59'),(151,1,4,22,'08:00:00','22:00:59'),(152,1,5,22,'08:00:00','22:00:59'),(153,1,6,22,'08:00:00','22:00:59'),(154,1,7,22,'08:00:00','22:00:59'),(155,1,1,23,'08:00:00','22:00:59'),(156,1,2,23,'08:00:00','22:00:59'),(157,1,3,23,'08:00:00','22:00:59'),(158,1,4,23,'08:00:00','22:00:59'),(159,1,5,23,'08:00:00','22:00:59'),(160,1,6,23,'08:00:00','22:00:59'),(161,1,7,23,'08:00:00','22:00:59'),(162,1,1,24,'08:00:00','22:00:59'),(163,1,2,24,'08:00:00','22:00:59'),(164,1,3,24,'08:00:00','22:00:59'),(165,1,4,24,'08:00:00','22:00:59'),(166,1,5,24,'08:00:00','22:00:59'),(167,1,6,24,'08:00:00','22:00:59'),(168,1,7,24,'08:00:00','22:00:59'),(169,1,1,25,'08:00:00','22:00:59'),(170,1,2,25,'08:00:00','22:00:59'),(171,1,3,25,'08:00:00','22:00:59'),(172,1,4,25,'08:00:00','22:00:59'),(173,1,5,25,'08:00:00','22:00:59'),(174,1,6,25,'08:00:00','22:00:59'),(175,1,7,25,'08:00:00','22:00:59'),(176,1,1,26,'08:00:00','22:00:59'),(177,1,2,26,'08:00:00','22:00:59'),(178,1,3,26,'08:00:00','22:00:59'),(179,1,4,26,'08:00:00','22:00:59'),(180,1,5,26,'08:00:00','22:00:59'),(181,1,6,26,'08:00:00','22:00:59'),(182,1,7,26,'08:00:00','22:00:59'),(183,1,1,27,'08:00:00','22:00:59'),(184,1,2,27,'08:00:00','22:00:59'),(185,1,3,27,'08:00:00','22:00:59'),(186,1,4,27,'08:00:00','22:00:59'),(187,1,5,27,'08:00:00','22:00:59'),(188,1,6,27,'08:00:00','22:00:59'),(189,1,7,27,'08:00:00','22:00:59'),(190,1,1,28,'08:00:00','22:00:59'),(191,1,2,28,'08:00:00','22:00:59'),(192,1,3,28,'08:00:00','22:00:59'),(193,1,4,28,'08:00:00','22:00:59'),(194,1,5,28,'08:00:00','22:00:59'),(195,1,6,28,'08:00:00','22:00:59'),(196,1,7,28,'08:00:00','22:00:59'),(197,1,1,29,'08:00:00','22:00:59'),(198,1,2,29,'08:00:00','22:00:59'),(199,1,3,29,'08:00:00','22:00:59'),(200,1,4,29,'08:00:00','22:00:59'),(201,1,5,29,'08:00:00','22:00:59'),(202,1,6,29,'08:00:00','22:00:59'),(203,1,7,29,'08:00:00','22:00:59'),(204,1,1,30,'08:00:00','22:00:59'),(205,1,2,30,'08:00:00','22:00:59'),(206,1,3,30,'08:00:00','22:00:59'),(207,1,4,30,'08:00:00','22:00:59'),(208,1,5,30,'08:00:00','22:00:59'),(209,1,6,30,'08:00:00','22:00:59'),(210,1,7,30,'08:00:00','22:00:59'),(211,1,1,31,'08:00:00','22:00:59'),(212,1,2,31,'08:00:00','22:00:59'),(213,1,3,31,'08:00:00','22:00:59'),(214,1,4,31,'08:00:00','22:00:59'),(215,1,5,31,'08:00:00','22:00:59'),(216,1,6,31,'08:00:00','22:00:59'),(217,1,7,31,'08:00:00','22:00:59'),(218,1,1,32,'08:00:00','22:00:59'),(219,1,2,32,'08:00:00','22:00:59'),(220,1,3,32,'08:00:00','22:00:59'),(221,1,4,32,'08:00:00','22:00:59'),(222,1,5,32,'08:00:00','22:00:59'),(223,1,6,32,'08:00:00','22:00:59'),(224,1,7,32,'08:00:00','22:00:59'),(225,1,1,33,'08:00:00','22:00:59'),(226,1,2,33,'08:00:00','22:00:59'),(227,1,3,33,'08:00:00','22:00:59'),(228,1,4,33,'08:00:00','22:00:59'),(229,1,5,33,'08:00:00','22:00:59'),(230,1,6,33,'08:00:00','22:00:59'),(231,1,7,33,'08:00:00','22:00:59'),(232,1,1,34,'08:00:00','22:00:59'),(233,1,2,34,'08:00:00','22:00:59'),(234,1,3,34,'08:00:00','22:00:59'),(235,1,4,34,'08:00:00','22:00:59'),(236,1,5,34,'08:00:00','22:00:59'),(237,1,6,34,'08:00:00','22:00:59'),(238,1,7,34,'08:00:00','22:00:59'),(239,1,1,35,'08:00:00','22:00:59'),(240,1,2,35,'08:00:00','22:00:59'),(241,1,3,35,'08:00:00','22:00:59'),(242,1,4,35,'08:00:00','22:00:59'),(243,1,5,35,'08:00:00','22:00:59'),(244,1,6,35,'08:00:00','22:00:59'),(245,1,7,35,'08:00:00','22:00:59'),(246,1,1,36,'08:00:00','22:00:59'),(247,1,2,36,'08:00:00','22:00:59'),(248,1,3,36,'08:00:00','22:00:59'),(249,1,4,36,'08:00:00','22:00:59'),(250,1,5,36,'08:00:00','22:00:59'),(251,1,6,36,'08:00:00','22:00:59'),(252,1,7,36,'08:00:00','22:00:59'),(253,1,1,37,'08:00:00','22:00:59'),(254,1,2,37,'08:00:00','22:00:59'),(255,1,3,37,'08:00:00','22:00:59'),(256,1,4,37,'08:00:00','22:00:59'),(257,1,5,37,'08:00:00','22:00:59'),(258,1,6,37,'08:00:00','22:00:59'),(259,1,7,37,'08:00:00','22:00:59'),(260,1,1,38,'08:00:00','22:00:59'),(261,1,2,38,'08:00:00','22:00:59'),(262,1,3,38,'08:00:00','22:00:59'),(263,1,4,38,'08:00:00','22:00:59'),(264,1,5,38,'08:00:00','22:00:59'),(265,1,6,38,'08:00:00','22:00:59'),(266,1,7,38,'08:00:00','22:00:59');
/*!40000 ALTER TABLE `distribuidora_horario_dia_entre` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Temporary view structure for view `generator_16`
--

DROP TABLE IF EXISTS `generator_16`;
/*!50001 DROP VIEW IF EXISTS `generator_16`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `generator_16` AS SELECT 
 1 AS `n`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `generator_256`
--

DROP TABLE IF EXISTS `generator_256`;
/*!50001 DROP VIEW IF EXISTS `generator_256`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `generator_256` AS SELECT 
 1 AS `n`*/;
SET character_set_client = @saved_cs_client;

--
-- Temporary view structure for view `generator_4k`
--

DROP TABLE IF EXISTS `generator_4k`;
/*!50001 DROP VIEW IF EXISTS `generator_4k`*/;
SET @saved_cs_client     = @@character_set_client;
SET character_set_client = utf8;
/*!50001 CREATE VIEW `generator_4k` AS SELECT 
 1 AS `n`*/;
SET character_set_client = @saved_cs_client;

--
-- Table structure for table `motivos_cancelamento`
--

DROP TABLE IF EXISTS `motivos_cancelamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `motivos_cancelamento` (
  `COD_MOTIVO` int(11) NOT NULL AUTO_INCREMENT,
  `DESC_MOTIVO` text NOT NULL,
  PRIMARY KEY (`COD_MOTIVO`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motivos_cancelamento`
--

LOCK TABLES `motivos_cancelamento` WRITE;
/*!40000 ALTER TABLE `motivos_cancelamento` DISABLE KEYS */;
INSERT INTO `motivos_cancelamento` (`COD_MOTIVO`, `DESC_MOTIVO`) VALUES (1,'Loja cancelou'),(2,'Demora na entrega'),(3,'Ninguém no local da entrega'),(4,'Falta de dinheiro'),(5,'Outros'),(6,'Expirado');
/*!40000 ALTER TABLE `motivos_cancelamento` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `motivos_recusa`
--

LOCK TABLES `motivos_recusa` WRITE;
/*!40000 ALTER TABLE `motivos_recusa` DISABLE KEYS */;
INSERT INTO `motivos_recusa` (`COD_MOTIVO`, `DESC_MOTIVO`) VALUES (1,'Falta de estoque'),(2,'Falta de motoboy'),(3,'Bairro não acessivel'),(4,'Outros');
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
  `NUM_TROCOPARA` decimal(12,2) DEFAULT NULL,
  `DATA_AGENDA_ENTREGA` datetime DEFAULT NULL,
  `flag_modoentrega` char(1) DEFAULT NULL,
  `FLAG_NOT_FINAL` char(1) DEFAULT 'N',
  `flag_not_final_avisa_loja` char(1) DEFAULT 'N',
  `flag_resposta_usuario` char(1) DEFAULT NULL,
  `DESC_OBSERVACAO` text,
  PRIMARY KEY (`ID_PEDIDO`),
  KEY `FK_REFERENCE_13` (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_15` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_17` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_13` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_15` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_17` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_item`
--

DROP TABLE IF EXISTS `pedido_item`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido_item` (
  `ID_PEDIDO` bigint(20) NOT NULL,
  `SEQ_ITEM` int(11) NOT NULL,
  `VAL_UNIT` decimal(12,2) DEFAULT NULL,
  `ID_PROD` int(11) NOT NULL,
  `QTD_PROD` bigint(20) DEFAULT NULL,
  `FLAG_RECUSADO` char(1) DEFAULT 'N',
  `RECUSADO_DISPONIVEL` int(11) DEFAULT NULL,
  PRIMARY KEY (`ID_PEDIDO`,`SEQ_ITEM`),
  KEY `FK_REFERENCE_16` (`ID_PROD`),
  CONSTRAINT `FK_REFERENCE_14` FOREIGN KEY (`ID_PEDIDO`) REFERENCES `pedido` (`ID_PEDIDO`),
  CONSTRAINT `FK_REFERENCE_16` FOREIGN KEY (`ID_PROD`) REFERENCES `produtos` (`ID_PROD`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_item`
--

LOCK TABLES `pedido_item` WRITE;
/*!40000 ALTER TABLE `pedido_item` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedido_motivo_cancelamento`
--

DROP TABLE IF EXISTS `pedido_motivo_cancelamento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `pedido_motivo_cancelamento` (
  `ID_PEDIDO` bigint(20) NOT NULL,
  `COD_MOTIVO` int(11) DEFAULT NULL,
  `DESC_OBS` text,
  `DATA_CANCELAMENTO` datetime DEFAULT NULL,
  `FLAG_CONFIRMADO_DISTRIBUIDORA` char(1) DEFAULT NULL,
  `FLAG_POPUPINICIAL` char(1) DEFAULT NULL,
  `FLAG_VIZUALIZADO_canc` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_PEDIDO`),
  KEY `FK_REFERENCE_29` (`COD_MOTIVO`),
  CONSTRAINT `FK_REFERENCE_28` FOREIGN KEY (`ID_PEDIDO`) REFERENCES `pedido` (`ID_PEDIDO`),
  CONSTRAINT `FK_REFERENCE_29` FOREIGN KEY (`COD_MOTIVO`) REFERENCES `motivos_cancelamento` (`COD_MOTIVO`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_motivo_cancelamento`
--

LOCK TABLES `pedido_motivo_cancelamento` WRITE;
/*!40000 ALTER TABLE `pedido_motivo_cancelamento` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido_motivo_cancelamento` ENABLE KEYS */;
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
) ENGINE=InnoDB AUTO_INCREMENT=4 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_motivos_recusa`
--

LOCK TABLES `pedido_motivos_recusa` WRITE;
/*!40000 ALTER TABLE `pedido_motivos_recusa` DISABLE KEYS */;
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
  `QTD_IMAGES` int(11) DEFAULT '1',
  PRIMARY KEY (`ID_PROD`)
) ENGINE=InnoDB AUTO_INCREMENT=120 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`, `QTD_IMAGES`) VALUES (1,'Água Mineral Da Pedra 2L C/Gás Pet','Água Da Pedra 2L Gás','S',1),(2,'Água Mineral Da Pedra 500Ml C/Gás Pet','Água Mineral Da Pedra 500Ml C/Gás Pet','S',1),(3,'Água Mineral Da Pedra 1L','Água Mineral Da Pedra 1L','S',1),(4,'Água Mineral Da Pedra 500Ml S/Gás Pet','Água Mineral Da Pedra 500Ml S/Gás Pet','S',1),(5,'Água Mineral Da Pedra 5L S/Gás Pet','Água Mineral Da Pedra 5L S/Gás Pet','S',1),(6,'Energético Frukito 2L Laranja Pet','Energético Frukito 2L Laranja Pet','S',1),(7,'Energético Frukito 500Ml Laranja Pet','Energético Frukito 500Ml Laranja Pet','S',2),(8,'Refrigerante Fruki 600Ml Guaraná Pet','Refrigerante Fruki 600Ml Guaraná Pet','S',1),(9,'Refrigerante Fruki 2L Laranja Pet','Fruki 2L Laranja','S',1),(10,'Refrigerante Fruki 2L Cola Pet','Refrigerante Fruki 2L Cola Pet','S',1),(11,'Refrigerante Fruki 600Ml Cola Pet','Refrigerante Fruki 600Ml Cola Pet','S',1),(12,'Refrigerante Fruki 600Ml Laranja','Fruki 600Ml','S',1),(13,'Refrigerante Fruki Zero 2L Cola Pet','Refrigerante Fruki Zero 2L Cola Pet','S',1),(14,'Refrigerante Fruki 350Ml Laranja Lata','Refrigerante Fruki 350Ml Laranja Lata','S',1),(15,'Refrigerante Fruki Zero 350Ml Cola Lata','Refrigerante Fruki Zero 350Ml Cola Lata','S',1),(16,'Refrigerante Fruki 600Ml Laranja Pet','Refrigerante Fruki 600Ml Laranja Pet','S',1),(17,'Refrigerante Fruki 2L Limão Pet','Refrigerante Fruki 2L Limão Pet','S',1),(18,'Refrigerante Fruki 350Ml Cola Lata','Refrigerante Fruki 350Ml Cola Lata','S',1),(19,'Refrigerante Fruki 600Ml Limão','Refrigerante Fruki 600Ml Limão','S',1),(20,'Refrigerante Fruki 2L Guaraná Pet','Refrigerante Fruki 2L Guaraná Pet','S',1),(21,'Refrigerante Fruki 350Ml Limão Lata','Fruki 350Ml Limão ','S',1),(22,'Refrigerante Fruki 2L Uva Pet Pet Batman  na feira da fruta','Refrigerante Fruki 2L Uva ','S',1),(23,'Refrigerante Fruki Zero 2L Guaraná Pet','Refrigerante Fruki Zero 2L Guaraná Pet','S',1),(24,'Refrigerante Fruki Zero 350Ml Guaraná Lata','Refrigerante Fruki Zero 350Ml Guaraná Lata','S',1),(25,'Refrigerante Fruki 350Ml Guaraná Lata','Refrigerante Fruki 350Ml Guaraná Lata','S',1),(26,'Refrigerante Fruki 600Ml Guaraná','Refrigerante Fruki 600Ml Guaraná','S',1),(27,'Águardente 7 Campos 966Ml','Águardente 7 Campos 966Ml','S',1),(28,'Cerveja Miller 355Ml Ln','Cerveja Miller 355Ml Ln','S',1),(29,'Vinho Collina D Sole 750Ml Branco Seco','Vinho Collina D Sole 750Ml Branco Seco','S',1),(30,'Vinho Collina D Sole 750Ml Tinto Seco','Vinho Collina D Sole 750Ml Tinto Seco','S',1),(31,'Vinho Collina D Sole 750Ml Tinto Suave','Vinho Collina D Sole 750Ml Tinto Suave','S',1),(32,'Vinho Cabernet Franc G União 750Ml Tinto Seco','Vinho Cabernet Franc G União 750Ml Tinto Seco','S',1),(33,'Vinho Cabernet G União 750Ml Tinto Suave','Vinho Cabernet G União 750Ml Tinto Suave','S',1),(34,'Vinho Malvasia G União 750Ml Suave','Vinho Malvasia G União 750Ml Suave','S',1),(35,'Vinho Merlot G União 750Ml Seco','Vinho Merlot G União 750Ml Seco','S',1),(36,'Vinho Riesling G União 750Ml Branco Seco','Vinho Riesling G União 750Ml Branco Seco','S',1),(37,'Vinho Cab Sauv M James 750Ml','Vinho Cab Sauv M James 750Ml','S',1),(38,'Vinho Chardonay M James 750Ml','Vinho Chardonay M James 750Ml','S',1),(39,'Vinho Pinot M James 750Ml','Vinho Pinot M James 750Ml','S',1),(40,'Vinho Riesling M James 750Ml','Vinho Riesling M James 750Ml','S',1),(41,'Vinho Tannat M James 750Ml','Vinho Tannat M James 750Ml','S',1),(42,'Vinho Sauv Blanc Res M James 750Ml','Vinho Sauv Blanc Res M James 750Ml','S',1),(43,'Vinho Collina D Sole 750Ml Tinto D Sec','Vinho Collina D Sole 750Ml Tinto D Sec','S',1),(44,'Energético Frukito 2L Frut Tropi Pet','Energético Frukito 2L Frut Tropi Pet','S',1),(45,'Energético Frukito 500Ml Frut Tropi Pet','Energético Frukito 500Ml Frut Tropi Pet','S',1),(46,'Vinho Merlot M James 750Ml Tinto','Vinho Merlot M James 750Ml Tinto','S',1),(47,'Energético Frukito 500Ml Frut Verm Pet','Energético Frukito 500Ml Frut Verm Pet','S',1),(48,'Energético Frukito 2L Frut Verm Pet','Energético Frukito 2L Frut Verm Pet','S',1),(49,'Vodka Perestroika 980Ml','Vodka Perestroika 980Ml','S',1),(50,'Refrigerante Fruki Zero 600Ml Guaraná Pet','Refrigerante Fruki Zero 600Ml Guaraná Pet','S',1),(51,'Vodka Smirnoff 998Ml Red','Vodka Smirnoff 998Ml Red','S',1),(52,'Refrigerante Fruki 600Ml Limão Pet','Refrigerante Fruki 600Ml Limão Pet','S',1),(53,'Refrigerante Fruki Zero 600Ml Cola Pet','Refrigerante Fruki Zero 600Ml Cola Pet','S',1),(54,'Vinho Cab Sauv S Colina 750Ml Seco','Vinho Cab Sauv S Colina 750Ml Seco','S',1),(55,'Vinho Merlot S Colina 750Ml','Vinho Merlot S Colina 750Ml','S',1),(57,'Refrigerante Fruki 600Ml Uva Pet','Refrigerante Fruki 600Ml Uva Pet','S',1),(58,'Vinho Chardonnay Santa Colina 750Ml Branco','Vinho Chardonnay Santa Colina 750Ml Branco','S',1),(59,'Vinho Pinot Noir S Colina 750Ml','Vinho Pinot Noir S Colina 750Ml','S',1),(64,'Vinho Cab Sauv G União 750Ml','Vinho Cab Sauv G União 750Ml','S',1),(65,'Vinho Tannat S Colina 750Ml','Vinho Tannat S Colina 750Ml','S',1),(66,'Vinho Del Grano Gold 1L Tto Suave','Vinho Del Grano Gold 1L Tto Suave','S',1),(67,'Vinho Niagara Del Grano Gold 1L Branc Seco','Vinho Niagara Del Grano Gold 1L Branc Seco','S',1),(68,'Vinho Del Grano Gold 1L Bco Suave','Vinho Del Grano Gold 1L Bco Suave','S',1),(69,'Vinho Del Grano Gold 1L Tinto Seco','Vinho Del Grano Gold 1L Tinto Seco','S',1),(72,'Energético Frukito 2L Uva Pet','Energético Frukito 2L Uva Pet','S',1),(73,'Energético Frukito 500Ml Uva Pet','Energético Frukito 500Ml Uva Pet','S',1),(74,'Energético Baly 2L','Energético Baly 2L','S',1),(76,'Fruki Tônica 1,5L','Fruki Tônica 1,5L','S',1),(77,'Fruki Citrus 1,5L','Fruki Citrus 1,5L','S',1),(78,'Fruki Tônica 350Ml Lata','Fruki Tônica 350Ml Lata','S',1),(79,'Fruki Citrus 350Ml Lata','Fruki Citrus 350Ml Lata','S',1),(86,'Vinho Cabernet G7 750Ml Tinto','Vinho Cabernet G7 750Ml Tinto','S',1),(87,'Vinho Carmenere G7 750Ml Tinto','Vinho Carmenere G7 750Ml Tinto','S',1),(88,'Vinho Merlot G7 750Ml Tinto','Vinho Merlot G7 750Ml Tinto','S',1),(89,'Cerveja Oktoberfest Heilige 500Ml','Cerveja Oktoberfest Heilige 500Ml','S',1),(90,'Cerveja Weissbier Heilige 500Ml','Cerveja Weissbier Heilige 500Ml','S',1),(91,'Cerveja Porter Heilige 500Ml','Cerveja Porter Heilige 500Ml','S',1),(92,'Cerveja Pilsen Heilige 500Ml','Cerveja Pilsen Heilige 500Ml','S',1),(93,'Cerveja Pilsen Extra Heilige 500Ml','Cerveja Pilsen Extra Heilige 500Ml','S',1),(94,'Cerveja Pale Ale Heilige 500Ml','Cerveja Pale Ale Heilige 500Ml','S',1),(96,'Cerveja Red Ale Heilige 500Ml','Cerveja Red Ale Heilige 500Ml','S',1),(98,'Água Mineral Da Pedra 1,5L S/Gás Pet','Água Mineral Da Pedra 1,5L S/Gás Pet','S',1),(100,'Cerveja APA Heilige 500Ml','Cerveja APA Heilige 500Ml','S',1),(101,'Cerveja Bohemian Pilsner Heilige 500Ml','Cerveja Bohemian Pilsner Heilige 500Ml','S',1),(102,'Cerveja Polar 473Ml Lata','Cerveja Polar 473Ml Lata','S',1),(103,'Cerveja Polar 600Ml','Cerveja Polar 600Ml','S',1),(104,'Cerveja Polar 355Ml Long Neck','Cerveja Polar 355Ml Long Neck','S',1),(105,'Cerveja Polar 1L','Cerveja Polar 1L','S',1),(106,'Cerveja Polar 350Ml Lata','Cerveja Polar 350Ml Lata','S',1),(107,'Cerveja Brahma 350Ml Lata','Cerveja Brahma 350Ml Lata','S',1),(108,'Cerveja Brahma 355Ml Long Neck','Cerveja Brahma 355Ml Long Neck','S',1),(109,'Cerveja Brahma 473Ml Lata','Cerveja Brahma 473Ml Lata','S',1),(110,'Cerveja Brahma 600Ml','Cerveja Brahma 600Ml','S',1),(111,'Cerveja Brahma 1L','Cerveja Brahma 1L','S',1),(112,'Cerveja Budweiser 350Ml Lata','Cerveja Budweiser 350Ml Lata','S',1),(113,'Cerveja Budweiser 355Ml Long Neck','Cerveja Budweiser 355Ml Long Neck','S',1),(114,'Cerveja Budweiser 600Ml','Cerveja Budweiser 600Ml','S',1),(115,'Cerveja Budweiser 473Ml Lata','Cerveja Budweiser 473Ml Lata','S',1),(116,'Cerveja Heiniken 350Ml Lata','Cerveja Heiniken 350Ml Lata','S',1),(117,'Cerveja Heniken 355Ml Long Neck','Cerveja Heniken 355Ml Long Neck','S',1),(118,'Cerveja Heiniken 600Ml','Cerveja Heiniken 600Ml','S',1),(119,'teste','teste','S',1);
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
) ENGINE=InnoDB AUTO_INCREMENT=903 DEFAULT CHARSET=utf8 COMMENT='Fks: id_prod_distr ou por (id_distribuidora + id_produto). ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos_distribuidora`
--

LOCK TABLES `produtos_distribuidora` WRITE;
/*!40000 ALTER TABLE `produtos_distribuidora` DISABLE KEYS */;
INSERT INTO `produtos_distribuidora` (`ID_PROD_DIST`, `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES (700,1,1,16.48,'N'),(701,2,1,13.95,'S'),(702,3,1,12.95,'S'),(703,4,1,5.86,'S'),(704,5,1,21.93,'S'),(705,6,1,21.06,'S'),(706,7,1,1.00,'S'),(707,8,1,1.29,'S'),(708,9,1,15.44,'S'),(709,10,1,3.29,'S'),(710,11,1,27.99,'S'),(711,12,1,16.04,'S'),(712,13,1,17.33,'S'),(713,14,1,29.04,'S'),(714,15,1,29.80,'S'),(715,16,1,19.28,'S'),(716,17,1,22.43,'S'),(717,18,1,12.97,'S'),(718,19,1,27.57,'S'),(719,20,1,9.82,'S'),(720,21,1,4.40,'S'),(721,22,1,10.17,'S'),(722,23,1,17.03,'S'),(723,24,1,29.58,'S'),(724,25,1,26.33,'S'),(725,26,1,1.25,'S'),(726,27,1,18.12,'S'),(727,28,1,10.88,'S'),(728,29,1,22.18,'S'),(729,30,1,29.84,'S'),(730,31,1,19.39,'S'),(731,32,1,20.49,'S'),(732,33,1,14.19,'S'),(733,34,1,20.93,'S'),(734,35,1,4.26,'S'),(735,36,1,19.87,'S'),(736,37,1,2.12,'S'),(737,38,1,12.78,'S'),(738,39,1,13.06,'S'),(739,40,1,2.00,'S'),(740,41,1,14.30,'S'),(741,42,1,13.03,'S'),(742,43,1,26.18,'N'),(743,44,1,13.09,'S'),(744,45,1,3.01,'S'),(745,46,1,21.84,'S'),(746,47,1,6.87,'S'),(747,48,1,16.16,'S'),(748,49,1,7.33,'S'),(749,50,1,5.65,'S'),(750,51,1,5.98,'S'),(751,52,1,5.44,'S'),(752,53,1,5.13,'S'),(753,54,1,5.57,'S'),(754,55,1,24.21,'S'),(755,57,1,29.60,'S'),(756,58,1,26.94,'S'),(757,59,1,3.36,'S'),(758,64,1,8.52,'S'),(759,65,1,15.81,'S'),(760,66,1,7.30,'S'),(761,67,1,28.57,'S'),(762,68,1,14.65,'S'),(763,69,1,7.29,'S'),(764,72,1,3.46,'S'),(765,73,1,26.20,'S'),(766,74,1,24.81,'S'),(767,76,1,14.99,'S'),(768,77,1,8.46,'S'),(769,78,1,5.03,'S'),(770,79,1,10.49,'S'),(771,86,1,17.91,'S'),(772,87,1,20.06,'S'),(773,88,1,2.16,'S'),(774,89,1,8.54,'S'),(775,90,1,25.59,'S'),(776,91,1,14.92,'S'),(777,92,1,1.81,'S'),(778,93,1,12.86,'S'),(779,94,1,9.65,'S'),(780,96,1,11.55,'S'),(781,98,1,8.99,'S'),(782,100,1,4.47,'S'),(783,101,1,6.49,'S'),(784,102,1,10.10,'S'),(785,103,1,5.21,'S'),(786,104,1,14.88,'S'),(787,105,1,23.62,'S'),(788,106,1,29.80,'S'),(789,107,1,24.05,'S'),(790,108,1,18.01,'S'),(791,109,1,6.06,'S'),(792,110,1,10.34,'S'),(793,111,1,8.67,'S'),(794,112,1,19.29,'S'),(795,113,1,1.61,'S'),(796,114,1,25.08,'S'),(797,115,1,20.07,'S'),(798,116,1,25.20,'S'),(799,117,1,21.12,'S'),(800,118,1,2.76,'S'),(801,1,2,25.66,'S'),(802,2,2,15.63,'S'),(803,3,2,25.23,'S'),(804,4,2,6.37,'S'),(805,5,2,20.74,'S'),(806,6,2,18.40,'S'),(807,7,2,22.77,'S'),(808,8,2,19.84,'S'),(809,9,2,14.91,'S'),(810,10,2,22.74,'S'),(811,11,2,13.88,'S'),(812,12,2,28.22,'S'),(813,13,2,11.44,'S'),(814,14,2,13.99,'S'),(815,15,2,14.51,'S'),(816,16,2,22.07,'S'),(817,17,2,8.88,'S'),(818,18,2,15.11,'S'),(819,19,2,12.71,'S'),(820,20,2,18.35,'S'),(821,21,2,7.81,'S'),(822,22,2,4.25,'S'),(823,23,2,29.37,'S'),(824,24,2,12.21,'S'),(825,25,2,7.33,'S'),(826,26,2,3.32,'S'),(827,27,2,12.99,'S'),(828,28,2,6.58,'S'),(829,29,2,26.26,'S'),(830,30,2,4.04,'S'),(831,31,2,26.00,'S'),(832,32,2,12.63,'S'),(833,33,2,25.52,'S'),(834,34,2,8.96,'S'),(835,35,2,26.38,'S'),(836,36,2,19.33,'S'),(837,37,2,29.20,'S'),(838,38,2,1.55,'S'),(839,39,2,24.42,'S'),(840,40,2,15.18,'S'),(841,41,2,4.73,'S'),(842,42,2,7.19,'S'),(843,43,2,5.94,'S'),(844,44,2,3.49,'S'),(845,45,2,23.60,'S'),(846,46,2,24.18,'S'),(847,47,2,15.17,'S'),(848,48,2,22.35,'S'),(849,49,2,22.95,'S'),(850,50,2,6.42,'S'),(851,51,2,8.60,'S'),(852,52,2,11.69,'S'),(853,53,2,17.71,'S'),(854,54,2,19.67,'S'),(855,55,2,18.31,'S'),(856,57,2,14.25,'S'),(857,58,2,19.25,'S'),(858,59,2,21.60,'S'),(859,64,2,29.07,'S'),(860,65,2,14.01,'S'),(861,66,2,27.81,'S'),(862,67,2,6.28,'S'),(863,68,2,21.68,'S'),(864,69,2,26.47,'S'),(865,72,2,9.30,'S'),(866,73,2,25.42,'S'),(867,74,2,28.55,'S'),(868,76,2,11.38,'S'),(869,77,2,26.07,'S'),(870,78,2,20.44,'S'),(871,79,2,27.15,'S'),(872,86,2,1.86,'S'),(873,87,2,29.54,'S'),(874,88,2,20.04,'S'),(875,89,2,4.56,'S'),(876,90,2,19.36,'S'),(877,91,2,7.08,'S'),(878,92,2,17.32,'S'),(879,93,2,20.27,'S'),(880,94,2,12.46,'S'),(881,96,2,3.04,'S'),(882,98,2,24.98,'S'),(883,100,2,13.79,'S'),(884,101,2,19.43,'S'),(885,102,2,23.16,'S'),(886,103,2,7.72,'S'),(887,104,2,18.48,'S'),(888,105,2,14.60,'S'),(889,106,2,2.43,'S'),(890,107,2,20.14,'S'),(891,108,2,20.82,'S'),(892,109,2,6.21,'S'),(893,110,2,4.11,'S'),(894,111,2,14.49,'S'),(895,112,2,3.66,'S'),(896,113,2,27.70,'S'),(897,114,2,26.98,'S'),(898,115,2,28.19,'S'),(899,116,2,8.24,'S'),(900,117,2,11.14,'S'),(901,118,2,28.12,'S'),(902,119,1,5.00,'N');
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
  `NUM_TEMPOMAXCANC_MINUTO` int(11) DEFAULT NULL,
  `COD_RECUSA` int(11) DEFAULT NULL,
  `ONESIGNAL_KEY` text,
  `ONESIGNAL_URL` text,
  `ONESIGNAL_APPID` text,
  `NUM_MINUTOS_NOT_FINAL` int(11) DEFAULT NULL,
  `NUM_SEGS_NOT_FINAL_EXEC` int(11) DEFAULT NULL,
  `cod_cancelamentosys` int(11) DEFAULT NULL,
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
INSERT INTO `sys_parametros` (`COD_CIDADE`, `ID_USUARIO_ADMIN`, `FLAG_MANUTENCAO`, `DESC_KEY`, `SEGS_TESTE_AJAX`, `FACE_APP_ID`, `FACE_APP_SECRETKEY`, `FACE_APP_TOKEN`, `FACE_REDIRECT_URI`, `url_system`, `sys_host_name_smtp`, `sys_smtp_port`, `sys_email`, `sys_senha`, `sys_fromemail`, `sys_fromdesc`, `sys_tls`, `id_visitante`, `PED_HORASOKEY`, `NUM_TEMPOMAXCANC_MINUTO`, `COD_RECUSA`, `ONESIGNAL_KEY`, `ONESIGNAL_URL`, `ONESIGNAL_APPID`, `NUM_MINUTOS_NOT_FINAL`, `NUM_SEGS_NOT_FINAL_EXEC`, `cod_cancelamentosys`) VALUES (1,1,'N','LxLnKYU3QbR6HmLHCyAGKQ',5,1318779544860496,'8363c050fed8b9d86a41d4b48ff77478','1318779544860496|pjg3iSnM4GkCLRtvb2i3CPg5nZA','http://localhost:4400/oauthcallback.html','http://localhost:8080/m_16/','smtp.kinghost.net',587,'suporte@tragoaqui.com.br','caracteres1','suporte@tragoaqui.com.br','TragoAqui','N',2,0,45,1,'NmJmN2VhMmQtNjJkMi00OTRkLTg4M2UtODhhNjkzNzcyYTg5','https://onesignal.com/api/v1/notifications','6b1103df-ebeb-43e1-860b-e2903a9b12ae',20,360,6);
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
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`ID_USUARIO`, `DESC_NOME`, `DESC_TELEFONE`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_ENDERECO_NUM`, `DESC_ENDERECO_COMPLEMENTO`, `COD_BAIRRO`, `DESC_CARTAO`, `DATA_EXP_MES`, `DATA_EXP_ANO`, `DESC_CARDHOLDERNAME`, `PAY_ID`, `DESC_CPF`, `FLAG_FACEUSER`, `ID_USER_FACE`, `FLAG_ATIVADO`, `CHAVE_ATIVACAO`, `DESC_NOVOEMAILVALIDACAO`, `CHAVE_ATIVACAO_NOVOEMAIL`, `FLAG_MAIORIDADE`) VALUES (1,'Admin','99999999999','admin','Metal4@ll','morratu@hotmail.com',1,'','','',NULL,'',NULL,NULL,'','','',NULL,NULL,'S',NULL,'','','S'),(2,'Visitante',NULL,'v','v',NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'V',NULL,NULL,NULL,''),(20,'Distribuidora Bebados de cair',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(21,'Roubou meu caminhão',NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL),(27,'Gabriel Dalcin Kothe',NULL,'g.kothe@hotmail.com','i45fe9ja1h7ldprsc524s7dkog','g.kothe@hotmail.com',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',1166874703428769,'S',NULL,NULL,NULL,'S');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `bebidas_v2`
--

USE `bebidas_v2`;

--
-- Final view structure for view `generator_16`
--

/*!50001 DROP VIEW IF EXISTS `generator_16`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `generator_16` AS select 0 AS `n` union all select 1 AS `1` union all select 2 AS `2` union all select 3 AS `3` union all select 4 AS `4` union all select 5 AS `5` union all select 6 AS `6` union all select 7 AS `7` union all select 8 AS `8` union all select 9 AS `9` union all select 10 AS `10` union all select 11 AS `11` union all select 12 AS `12` union all select 13 AS `13` union all select 14 AS `14` union all select 15 AS `15` */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `generator_256`
--

/*!50001 DROP VIEW IF EXISTS `generator_256`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `generator_256` AS select ((`hi`.`n` << 4) | `lo`.`n`) AS `n` from (`generator_16` `lo` join `generator_16` `hi`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;

--
-- Final view structure for view `generator_4k`
--

/*!50001 DROP VIEW IF EXISTS `generator_4k`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8 */;
/*!50001 SET character_set_results     = utf8 */;
/*!50001 SET collation_connection      = utf8_general_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`root`@`localhost` SQL SECURITY DEFINER */
/*!50001 VIEW `generator_4k` AS select ((`hi`.`n` << 8) | `lo`.`n`) AS `n` from (`generator_256` `lo` join `generator_16` `hi`) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2017-01-02 14:48:24
