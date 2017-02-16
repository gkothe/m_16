-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: mysql05-farm68.kinghost.net    Database: tragoaqui03
-- ------------------------------------------------------
-- Server version	5.6.35-log

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
-- Current Database: `tragoaqui03`
--

CREATE DATABASE /*!32312 IF NOT EXISTS*/ `tragoaqui03` /*!40100 DEFAULT CHARACTER SET latin1 */;

USE `tragoaqui03`;

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
INSERT INTO `bairros` (`COD_BAIRRO`, `COD_CIDADE`, `DESC_BAIRRO`) VALUES (1,1,'Aliança'),(2,1,'Ana Nery'),(3,1,'Arroio Grande'),(4,1,'Avenida'),(5,1,'Belverde'),(6,1,'Bom Jesus'),(7,1,'Bonfim'),(8,1,'Castelo Branco'),(9,1,'Centro'),(10,1,'Country'),(11,1,'Dona Carlota'),(12,1,'Esmeralda'),(13,1,'Faxinal Menino Deus'),(14,1,'Germânia'),(15,1,'Goias'),(16,1,'Higienópolis'),(17,1,'Independência'),(18,1,'Jardim Europa'),(19,1,'João Alves'),(20,1,'Linha Santa Cruz'),(21,1,'Margarida'),(22,1,'Monte Verde'),(23,1,'Pedreira'),(24,1,'Progresso'),(25,1,'Rauber'),(26,1,'Renascença'),(27,1,'Santa Vitória'),(28,1,'Santo Antônio'),(29,1,'Santo Inácio'),(30,1,'Santuário'),(31,1,'São João'),(32,1,'Schulz'),(33,1,'Senai'),(34,1,'Universitário'),(35,1,'Várzea'),(36,1,'Distrito Industrial');
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
INSERT INTO `carrinho` (`ID_CARRINHO`, `ID_USUARIO`, `COD_BAIRRO`, `DATA_CRIACAO`) VALUES (1,1,NULL,'2017-02-10 14:14:52'),(2,5,NULL,'2017-02-10 17:25:49');
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
INSERT INTO `carrinho_item` (`ID_CARRINHO`, `SEQ_ITEM`, `ID_PROD_DIST`, `QTD`, `VAL_PROD`) VALUES (1,1,55,8,69.96),(1,2,98,8,2.00),(2,1,55,1,69.96),(2,2,62,1,75.35),(2,3,69,11,3.63),(2,4,52,1,9.19),(2,5,100,4,46.61),(2,6,66,3,14.98);
/*!40000 ALTER TABLE `carrinho_item` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `ID_CATEGORIA` int(11) NOT NULL,
  `DESC_CATEGORIA` text,
  PRIMARY KEY (`ID_CATEGORIA`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` (`ID_CATEGORIA`, `DESC_CATEGORIA`) VALUES (1,'Barra de Proteína'),(2,'Pasta de Amendoim'),(3,'Whey Protein'),(4,'Termogênico');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
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
INSERT INTO `dias_semana` (`COD_DIA`, `DESC_DIA`) VALUES (1,'Segunda-feira'),(2,'Terça-feira'),(3,'Quarta-feira'),(4,'Quinta-feira'),(5,'Sexta-feira'),(6,'Sábado'),(7,'Domingo'),(8,'Período Personalizado');
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
  `DESC_LOJA` text,
  `TEMPO_MINIMO_ENTREGA` time DEFAULT NULL,
  PRIMARY KEY (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_6` (`COD_CIDADE`),
  KEY `FK_REFERENCE_30` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_31` (`ID_USUARIO_INSERT`),
  CONSTRAINT `FK_REFERENCE_30` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_31` FOREIGN KEY (`ID_USUARIO_INSERT`) REFERENCES `usuario` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_6` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora`
--

LOCK TABLES `distribuidora` WRITE;
/*!40000 ALTER TABLE `distribuidora` DISABLE KEYS */;
INSERT INTO `distribuidora` (`ID_DISTRIBUIDORA`, `COD_CIDADE`, `DESC_RAZAO_SOCIAL`, `DESC_NOME_ABREV`, `VAL_ENTREGA_MIN`, `DESC_TELEFONE`, `DESC_ENDERECO`, `NUM_ENDEREC`, `DESC_COMPLEMENTO`, `VAL_TELE_ENTREGA`, `FLAG_CUSTOM`, `DESC_LOGIN`, `DESC_SENHA`, `DESC_MAIL`, `FLAG_ATIVO_MASTER`, `FLAG_ATIVO`, `FLAG_MODOPAGAMENTO`, `DATE_LASTAJAX`, `flag_entre_ret`, `PERC_PAGAMENTO`, `TXT_OBS_HORA`, `COD_BAIRRO`, `ID_USUARIO_INSERT`, `FLAG_TIPOCONTRATO`, `DESC_LOJA`, `TEMPO_MINIMO_ENTREGA`) VALUES (1,1,'Loja Teste LTDA','Loja TESTE',0.00,'','www.tragoaqui.com.br','000','',0.00,'N','b','b','suporte_fitness@tragoaqui.com.br','S','F','A','2017-02-14 09:47:05','A',0.00,'',9,1,NULL,NULL,NULL);
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
INSERT INTO `distribuidora_horario_dia_entre` (`ID_HORARIO`, `ID_DISTRIBUIDORA`, `COD_DIA`, `ID_DISTR_BAIRRO`, `HORARIO_INI`, `HORARIO_FIM`) VALUES (1,1,1,1,'00:00:00','23:59:59'),(2,1,2,1,'00:00:00','23:59:59'),(3,1,3,1,'00:00:00','23:59:59'),(4,1,4,1,'00:00:00','23:59:59'),(5,1,5,1,'00:00:00','23:59:59'),(6,1,6,1,'00:00:00','23:59:59'),(7,1,7,1,'00:00:00','23:59:59'),(8,1,1,2,'00:00:00','23:59:59'),(9,1,2,2,'00:00:00','23:59:59'),(10,1,3,2,'00:00:00','23:59:59'),(11,1,4,2,'00:00:00','23:59:59'),(12,1,5,2,'00:00:00','23:59:59'),(13,1,6,2,'00:00:00','23:59:59'),(14,1,7,2,'00:00:00','23:59:59'),(15,1,1,3,'00:00:00','23:59:59'),(16,1,2,3,'00:00:00','23:59:59'),(17,1,3,3,'00:00:00','23:59:59'),(18,1,4,3,'00:00:00','23:59:59'),(19,1,5,3,'00:00:00','23:59:59'),(20,1,6,3,'00:00:00','23:59:59'),(21,1,7,3,'00:00:00','23:59:59'),(22,1,1,4,'00:00:00','23:59:59'),(23,1,2,4,'00:00:00','23:59:59'),(24,1,3,4,'00:00:00','23:59:59'),(25,1,4,4,'00:00:00','23:59:59'),(26,1,5,4,'00:00:00','23:59:59'),(27,1,6,4,'00:00:00','23:59:59'),(28,1,7,4,'00:00:00','23:59:59'),(29,1,1,5,'00:00:00','23:59:59'),(30,1,2,5,'00:00:00','23:59:59'),(31,1,3,5,'00:00:00','23:59:59'),(32,1,4,5,'00:00:00','23:59:59'),(33,1,5,5,'00:00:00','23:59:59'),(34,1,6,5,'00:00:00','23:59:59'),(35,1,7,5,'00:00:00','23:59:59'),(36,1,1,6,'00:00:00','23:59:59'),(37,1,2,6,'00:00:00','23:59:59'),(38,1,3,6,'00:00:00','23:59:59'),(39,1,4,6,'00:00:00','23:59:59'),(40,1,5,6,'00:00:00','23:59:59'),(41,1,6,6,'00:00:00','23:59:59'),(42,1,7,6,'00:00:00','23:59:59'),(43,1,1,7,'00:00:00','23:59:59'),(44,1,2,7,'00:00:00','23:59:59'),(45,1,3,7,'00:00:00','23:59:59'),(46,1,4,7,'00:00:00','23:59:59'),(47,1,5,7,'00:00:00','23:59:59'),(48,1,6,7,'00:00:00','23:59:59'),(49,1,7,7,'00:00:00','23:59:59'),(50,1,1,8,'00:00:00','23:59:59'),(51,1,2,8,'00:00:00','23:59:59'),(52,1,3,8,'00:00:00','23:59:59'),(53,1,4,8,'00:00:00','23:59:59'),(54,1,5,8,'00:00:00','23:59:59'),(55,1,6,8,'00:00:00','23:59:59'),(56,1,7,8,'00:00:00','23:59:59'),(57,1,1,9,'00:00:00','23:59:59'),(58,1,2,9,'00:00:00','23:59:59'),(59,1,3,9,'00:00:00','23:59:59'),(60,1,4,9,'00:00:00','23:59:59'),(61,1,5,9,'00:00:00','23:59:59'),(62,1,6,9,'00:00:00','23:59:59'),(63,1,7,9,'00:00:00','23:59:59'),(64,1,1,10,'00:00:00','23:59:59'),(65,1,2,10,'00:00:00','23:59:59'),(66,1,3,10,'00:00:00','23:59:59'),(67,1,4,10,'00:00:00','23:59:59'),(68,1,5,10,'00:00:00','23:59:59'),(69,1,6,10,'00:00:00','23:59:59'),(70,1,7,10,'00:00:00','23:59:59'),(71,1,1,11,'00:00:00','23:59:59'),(72,1,2,11,'00:00:00','23:59:59'),(73,1,3,11,'00:00:00','23:59:59'),(74,1,4,11,'00:00:00','23:59:59'),(75,1,5,11,'00:00:00','23:59:59'),(76,1,6,11,'00:00:00','23:59:59'),(77,1,7,11,'00:00:00','23:59:59'),(78,1,1,12,'00:00:00','23:59:59'),(79,1,2,12,'00:00:00','23:59:59'),(80,1,3,12,'00:00:00','23:59:59'),(81,1,4,12,'00:00:00','23:59:59'),(82,1,5,12,'00:00:00','23:59:59'),(83,1,6,12,'00:00:00','23:59:59'),(84,1,7,12,'00:00:00','23:59:59'),(85,1,1,13,'00:00:00','23:59:59'),(86,1,2,13,'00:00:00','23:59:59'),(87,1,3,13,'00:00:00','23:59:59'),(88,1,4,13,'00:00:00','23:59:59'),(89,1,5,13,'00:00:00','23:59:59'),(90,1,6,13,'00:00:00','23:59:59'),(91,1,7,13,'00:00:00','23:59:59'),(92,1,1,14,'00:00:00','23:59:59'),(93,1,2,14,'00:00:00','23:59:59'),(94,1,3,14,'00:00:00','23:59:59'),(95,1,4,14,'00:00:00','23:59:59'),(96,1,5,14,'00:00:00','23:59:59'),(97,1,6,14,'00:00:00','23:59:59'),(98,1,7,14,'00:00:00','23:59:59'),(99,1,1,15,'00:00:00','23:59:59'),(100,1,2,15,'00:00:00','23:59:59'),(101,1,3,15,'00:00:00','23:59:59'),(102,1,4,15,'00:00:00','23:59:59'),(103,1,5,15,'00:00:00','23:59:59'),(104,1,6,15,'00:00:00','23:59:59'),(105,1,7,15,'00:00:00','23:59:59'),(106,1,1,16,'00:00:00','23:59:59'),(107,1,2,16,'00:00:00','23:59:59'),(108,1,3,16,'00:00:00','23:59:59'),(109,1,4,16,'00:00:00','23:59:59'),(110,1,5,16,'00:00:00','23:59:59'),(111,1,6,16,'00:00:00','23:59:59'),(112,1,7,16,'00:00:00','23:59:59'),(113,1,1,17,'00:00:00','23:59:59'),(114,1,2,17,'00:00:00','23:59:59'),(115,1,3,17,'00:00:00','23:59:59'),(116,1,4,17,'00:00:00','23:59:59'),(117,1,5,17,'00:00:00','23:59:59'),(118,1,6,17,'00:00:00','23:59:59'),(119,1,7,17,'00:00:00','23:59:59'),(120,1,1,18,'00:00:00','23:59:59'),(121,1,2,18,'00:00:00','23:59:59'),(122,1,3,18,'00:00:00','23:59:59'),(123,1,4,18,'00:00:00','23:59:59'),(124,1,5,18,'00:00:00','23:59:59'),(125,1,6,18,'00:00:00','23:59:59'),(126,1,7,18,'00:00:00','23:59:59'),(127,1,1,19,'00:00:00','23:59:59'),(128,1,2,19,'00:00:00','23:59:59'),(129,1,3,19,'00:00:00','23:59:59'),(130,1,4,19,'00:00:00','23:59:59'),(131,1,5,19,'00:00:00','23:59:59'),(132,1,6,19,'00:00:00','23:59:59'),(133,1,7,19,'00:00:00','23:59:59'),(134,1,1,20,'00:00:00','23:59:59'),(135,1,2,20,'00:00:00','23:59:59'),(136,1,3,20,'00:00:00','23:59:59'),(137,1,4,20,'00:00:00','23:59:59'),(138,1,5,20,'00:00:00','23:59:59'),(139,1,6,20,'00:00:00','23:59:59'),(140,1,7,20,'00:00:00','23:59:59'),(141,1,1,21,'00:00:00','23:59:59'),(142,1,2,21,'00:00:00','23:59:59'),(143,1,3,21,'00:00:00','23:59:59'),(144,1,4,21,'00:00:00','23:59:59'),(145,1,5,21,'00:00:00','23:59:59'),(146,1,6,21,'00:00:00','23:59:59'),(147,1,7,21,'00:00:00','23:59:59'),(148,1,1,22,'00:00:00','23:59:59'),(149,1,2,22,'00:00:00','23:59:59'),(150,1,3,22,'00:00:00','23:59:59'),(151,1,4,22,'00:00:00','23:59:59'),(152,1,5,22,'00:00:00','23:59:59'),(153,1,6,22,'00:00:00','23:59:59'),(154,1,7,22,'00:00:00','23:59:59'),(155,1,1,23,'00:00:00','23:59:59'),(156,1,2,23,'00:00:00','23:59:59'),(157,1,3,23,'00:00:00','23:59:59'),(158,1,4,23,'00:00:00','23:59:59'),(159,1,5,23,'00:00:00','23:59:59'),(160,1,6,23,'00:00:00','23:59:59'),(161,1,7,23,'00:00:00','23:59:59'),(162,1,1,24,'00:00:00','23:59:59'),(163,1,2,24,'00:00:00','23:59:59'),(164,1,3,24,'00:00:00','23:59:59'),(165,1,4,24,'00:00:00','23:59:59'),(166,1,5,24,'00:00:00','23:59:59'),(167,1,6,24,'00:00:00','23:59:59'),(168,1,7,24,'00:00:00','23:59:59'),(169,1,1,25,'00:00:00','23:59:59'),(170,1,2,25,'00:00:00','23:59:59'),(171,1,3,25,'00:00:00','23:59:59'),(172,1,4,25,'00:00:00','23:59:59'),(173,1,5,25,'00:00:00','23:59:59'),(174,1,6,25,'00:00:00','23:59:59'),(175,1,7,25,'00:00:00','23:59:59'),(176,1,1,26,'00:00:00','23:59:59'),(177,1,2,26,'00:00:00','23:59:59'),(178,1,3,26,'00:00:00','23:59:59'),(179,1,4,26,'00:00:00','23:59:59'),(180,1,5,26,'00:00:00','23:59:59'),(181,1,6,26,'00:00:00','23:59:59'),(182,1,7,26,'00:00:00','23:59:59'),(183,1,1,27,'00:00:00','23:59:59'),(184,1,2,27,'00:00:00','23:59:59'),(185,1,3,27,'00:00:00','23:59:59'),(186,1,4,27,'00:00:00','23:59:59'),(187,1,5,27,'00:00:00','23:59:59'),(188,1,6,27,'00:00:00','23:59:59'),(189,1,7,27,'00:00:00','23:59:59'),(190,1,1,28,'00:00:00','23:59:59'),(191,1,2,28,'00:00:00','23:59:59'),(192,1,3,28,'00:00:00','23:59:59'),(193,1,4,28,'00:00:00','23:59:59'),(194,1,5,28,'00:00:00','23:59:59'),(195,1,6,28,'00:00:00','23:59:59'),(196,1,7,28,'00:00:00','23:59:59'),(197,1,1,29,'00:00:00','23:59:59'),(198,1,2,29,'00:00:00','23:59:59'),(199,1,3,29,'00:00:00','23:59:59'),(200,1,4,29,'00:00:00','23:59:59'),(201,1,5,29,'00:00:00','23:59:59'),(202,1,6,29,'00:00:00','23:59:59'),(203,1,7,29,'00:00:00','23:59:59'),(204,1,1,30,'00:00:00','23:59:59'),(205,1,2,30,'00:00:00','23:59:59'),(206,1,3,30,'00:00:00','23:59:59'),(207,1,4,30,'00:00:00','23:59:59'),(208,1,5,30,'00:00:00','23:59:59'),(209,1,6,30,'00:00:00','23:59:59'),(210,1,7,30,'00:00:00','23:59:59'),(211,1,1,31,'00:00:00','23:59:59'),(212,1,2,31,'00:00:00','23:59:59'),(213,1,3,31,'00:00:00','23:59:59'),(214,1,4,31,'00:00:00','23:59:59'),(215,1,5,31,'00:00:00','23:59:59'),(216,1,6,31,'00:00:00','23:59:59'),(217,1,7,31,'00:00:00','23:59:59'),(218,1,1,32,'00:00:00','23:59:59'),(219,1,2,32,'00:00:00','23:59:59'),(220,1,3,32,'00:00:00','23:59:59'),(221,1,4,32,'00:00:00','23:59:59'),(222,1,5,32,'00:00:00','23:59:59'),(223,1,6,32,'00:00:00','23:59:59'),(224,1,7,32,'00:00:00','23:59:59'),(225,1,1,33,'00:00:00','23:59:59'),(226,1,2,33,'00:00:00','23:59:59'),(227,1,3,33,'00:00:00','23:59:59'),(228,1,4,33,'00:00:00','23:59:59'),(229,1,5,33,'00:00:00','23:59:59'),(230,1,6,33,'00:00:00','23:59:59'),(231,1,7,33,'00:00:00','23:59:59'),(232,1,1,34,'00:00:00','23:59:59'),(233,1,2,34,'00:00:00','23:59:59'),(234,1,3,34,'00:00:00','23:59:59'),(235,1,4,34,'00:00:00','23:59:59'),(236,1,5,34,'00:00:00','23:59:59'),(237,1,6,34,'00:00:00','23:59:59'),(238,1,7,34,'00:00:00','23:59:59'),(239,1,1,35,'00:00:00','23:59:59'),(240,1,2,35,'00:00:00','23:59:59'),(241,1,3,35,'00:00:00','23:59:59'),(242,1,4,35,'00:00:00','23:59:59'),(243,1,5,35,'00:00:00','23:59:59'),(244,1,6,35,'00:00:00','23:59:59'),(245,1,7,35,'00:00:00','23:59:59'),(246,1,1,36,'00:00:00','23:59:59'),(247,1,2,36,'00:00:00','23:59:59'),(248,1,3,36,'00:00:00','23:59:59'),(249,1,4,36,'00:00:00','23:59:59'),(250,1,5,36,'00:00:00','23:59:59'),(251,1,6,36,'00:00:00','23:59:59'),(252,1,7,36,'00:00:00','23:59:59');
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
-- Table structure for table `marca`
--

DROP TABLE IF EXISTS `marca`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `marca` (
  `ID_MARCA` int(11) NOT NULL,
  `DESC_MARCA` text,
  PRIMARY KEY (`ID_MARCA`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `marca`
--

LOCK TABLES `marca` WRITE;
/*!40000 ALTER TABLE `marca` DISABLE KEYS */;
INSERT INTO `marca` (`ID_MARCA`, `DESC_MARCA`) VALUES (1,'Probiótica'),(2,'Max Titanium'),(3,'Universal');
/*!40000 ALTER TABLE `marca` ENABLE KEYS */;
UNLOCK TABLES;

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
  `FLAG_MARCADO` char(1) DEFAULT 'N',
  PRIMARY KEY (`ID_PEDIDO`),
  KEY `FK_REFERENCE_13` (`ID_DISTRIBUIDORA`),
  KEY `FK_REFERENCE_15` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_17` (`ID_USUARIO`),
  CONSTRAINT `FK_REFERENCE_13` FOREIGN KEY (`ID_DISTRIBUIDORA`) REFERENCES `distribuidora` (`ID_DISTRIBUIDORA`),
  CONSTRAINT `FK_REFERENCE_15` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_17` FOREIGN KEY (`ID_USUARIO`) REFERENCES `usuario` (`ID_USUARIO`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido`
--

LOCK TABLES `pedido` WRITE;
/*!40000 ALTER TABLE `pedido` DISABLE KEYS */;
INSERT INTO `pedido` (`ID_PEDIDO`, `ID_DISTRIBUIDORA`, `ID_USUARIO`, `DATA_PEDIDO`, `FLAG_STATUS`, `VAL_TOTALPROD`, `VAL_ENTREGA`, `DATA_PEDIDO_RESPOSTA`, `NUM_PED`, `COD_BAIRRO`, `NUM_TELEFONECONTATO_CLIENTE`, `TEMPO_ESTIMADO_ENTREGA`, `DESC_ENDERECO_ENTREGA`, `DESC_ENDERECO_NUM_ENTREGA`, `DESC_ENDERECO_COMPLEMENTO_ENTREGA`, `flag_vizualizado`, `FLAG_MODOPAGAMENTO`, `DESC_CARTAO`, `NOME_PESSOA`, `PAG_TOKEN`, `PAG_MAIL`, `PAG_PAYID_TIPOCARTAO`, `FLAG_PEDIDO_RET_ENTRE`, `TEMPO_ESTIMADO_DESEJADO`, `NUM_TROCOPARA`, `DATA_AGENDA_ENTREGA`, `flag_modoentrega`, `FLAG_NOT_FINAL`, `flag_not_final_avisa_loja`, `flag_resposta_usuario`, `DESC_OBSERVACAO`, `FLAG_MARCADO`) VALUES (1,1,4,'2017-02-13 09:12:14','C',10.00,0.00,NULL,1,9,NULL,NULL,'teste','205','','S','D',NULL,'Gabriel Dalcin Kothe',NULL,NULL,NULL,'T','01:00:00',0.00,NULL,'T','N','N',NULL,'','N');
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
INSERT INTO `pedido_item` (`ID_PEDIDO`, `SEQ_ITEM`, `VAL_UNIT`, `ID_PROD`, `QTD_PROD`, `FLAG_RECUSADO`, `RECUSADO_DISPONIVEL`) VALUES (1,1,2.00,48,5,'N',NULL);
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
INSERT INTO `pedido_motivo_cancelamento` (`ID_PEDIDO`, `COD_MOTIVO`, `DESC_OBS`, `DATA_CANCELAMENTO`, `FLAG_CONFIRMADO_DISTRIBUIDORA`, `FLAG_POPUPINICIAL`, `FLAG_VIZUALIZADO_canc`) VALUES (1,6,'Cancelado pelo sistema','2017-02-13 10:14:27','S','S','S');
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedido_motivos_recusa`
--

LOCK TABLES `pedido_motivos_recusa` WRITE;
/*!40000 ALTER TABLE `pedido_motivos_recusa` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedido_motivos_recusa` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `prod_categoria`
--

DROP TABLE IF EXISTS `prod_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `prod_categoria` (
  `ID_CATEGORIA` int(11) NOT NULL,
  `ID_PROD` int(11) NOT NULL,
  PRIMARY KEY (`ID_CATEGORIA`,`ID_PROD`),
  KEY `FK_REFERENCE_33` (`ID_PROD`),
  CONSTRAINT `FK_REFERENCE_32` FOREIGN KEY (`ID_CATEGORIA`) REFERENCES `categoria` (`ID_CATEGORIA`),
  CONSTRAINT `FK_REFERENCE_33` FOREIGN KEY (`ID_PROD`) REFERENCES `produtos` (`ID_PROD`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `prod_categoria`
--

LOCK TABLES `prod_categoria` WRITE;
/*!40000 ALTER TABLE `prod_categoria` DISABLE KEYS */;
INSERT INTO `prod_categoria` (`ID_CATEGORIA`, `ID_PROD`) VALUES (3,3),(3,4),(1,5),(2,10),(2,11),(1,26),(3,49),(3,50);
/*!40000 ALTER TABLE `prod_categoria` ENABLE KEYS */;
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
  `ID_MARCA` int(11) DEFAULT NULL,
  `desc_key_words` text,
  PRIMARY KEY (`ID_PROD`),
  KEY `FK_REFERENCE_34` (`ID_MARCA`),
  CONSTRAINT `FK_REFERENCE_34` FOREIGN KEY (`ID_MARCA`) REFERENCES `marca` (`ID_MARCA`)
) ENGINE=InnoDB AUTO_INCREMENT=51 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`, `QTD_IMAGES`, `ID_MARCA`, `desc_key_words`) VALUES (1,'Hipercalórico de alta performance, composto de:\n\n- Proteínas de Qualidade Superior: concentrado da proteína do soro do leite, proteína isolada do soro do leite e caseína micelar.\n\n- Carboidratos de Qualidade Superior: inulina, uma fibra única que auxilia na digestão e absorção de calorias. Trata-se de um fruto-oligosacarídeo ou prebiótico que auxilia na absorção mineral, melhora a digestão e ajuda a criar um estado perfeito no estômago para uma absorção melhor de todos os nutrientes. Gerando a situação ideal de absorção quando consumimos quantidades excessivas de calorias durante o ciclo de aumento de massa muscular. Isento de carboidratos de frutose e açúcar, evitando o ganho de gordura. \n\n- Gorduras de Qualidade Superior de alta qualidade como é o caso do óleo de linho (flexseed oil), uma gordura essencial e MCTs, de altíssima importância que o corpo imediatamente queima na obtenção de energia, o consumo das gorduras \"boas\" pode ajudá-lo a obter mais massa magra. Real Gains, o hipercalórico da Universal é a melhor opção para ganhar peso com qualidade.\n','Real Gains (1727g) Universal Nutrition','S',1,NULL,NULL),(2,'Potente termogênico que age queimando e evitando que a gordura se acumule no organismo. Assim, é indicado para perda de gordura e definição muscular.\n\nA fórmula de Nutrex 6 Nutrex age em 6 diferentes receptores, acelerando o metabolismo, aumentando o gasto calórico, e disponibilizando gordura para queima.\n\nAuxilia de forma decisiva no emagrecimento, atuando em todos os pontos possíveis.','Nutrex 6 (120caps)','S',1,NULL,NULL),(3,'Fórmula tecnicamente estruturada em dois níveis de complexidade molecular disponibilizando nano-frações proteicas de rápida absorção e biodisponibilidade.\n\n2W WHEY PROTEIN é uma interação inteligente que une duas proteínas bioativas do soro do leite:\n\nÍON-EXCHANGE WHEY PROTEIN ISOLATE (proteína isolada do soro do leite por troca iônica): Isolado protéico de alto valor biológico extraído por uma reação química chamada troca iônica.\nWHEY PROTEIN CONCENTRATE (Proteína concentrada do soro do leite): Nobre proteína do soro, também conhecida por lactoalbumina contendo um balanceado perfil de aminoácidos de cadeia ramificada.','2W Whey Protein (900g) Body Action','S',1,NULL,NULL),(4,'Iron Whey Protein é uma fórmula de alta qualidade, que disponibiliza uma grande quantidade de aminoácidos essenciais ao nosso organismo. Independentemente da idade ou nível de atividade física praticado, a proteína da Whey Protein é essencial para cada regime de nutrição, pois é catalisador do corpo para o crescimento muscular e síntese proteica. Trata-se de uma proteína mais pura, superior em qualidade que irá ajudar a alimentar o seu corpo com nutrientes que farão diferença em seu desempenho físico e ao mesmo tempo contribuirão para a redução do tempo de recuperação após a atividade física praticada.       \n\nAs proteínas são essenciais, um dos nutrientes mais importantes do corpo humano. Em praticantes de atividades físicas, há uma demanda de aminoácidos extras e de proteína de muito maior do que em pessoas que são inativos ou sedentárias.       \n\nQuando os cientistas de renome mundial da MusclePharm® se uniram com o ícone de fitness e lendário Arnold Schwarzenegger, estudaram como desenvolver uma Whey Protein realmente superior. Especificamente, eles precisavam de uma fórmula de proteína para melhorar a retenção de nitrogênio, pois os músculos só se desenvolvem se o corpo retém mais nitrogênio. Uma das fontes de nitrogênio mais eficazes é a Whey Protein. Para alimentar o desenvolvimento muscular, Iron Whey é ultra-microfiltrado e disponibiliza naturalmente aminoácidos anabólicos diretamente para sua corrente sanguínea. Este pico de aminoácidos cria o ambiente rico em nitrogênio necessário para a síntese de proteína muscular. Esta é a chave para a construção de massa muscular magra.    \n\nIron Whey é uma matriz de proteína com Whey Protein Concentrada (WPC), Whey Protein Isolada (WPI) e Whey Protein Hidrolisada (WPH), numa fórmula de sabor fantástico!     \n\nCom BCAAs que aumentam a síntese de proteína, permitindo o máximo crescimento e reparação muscular, mais Glutamina que é o aminoácido mais abundante nos músculos e é fundamental para a preservação da massa muscular magra e para a restauração do músculo danificado. Contém ainda Taurina que é um impulsionador para treinos de alta intensidade, dá suporte à síntese de proteínas, metabolismo e funcionamento cardíaco e a Glicina que pode melhorar a captação de creatina e glucose, otimizando o processo de desenvolvimento muscular, recuperação e desempenho.      ','Iron Whey (908g) Arnold Schwarzenegger','S',1,NULL,NULL),(5,'Whey Bar é uma barra a base de proteínas, com destaque para WHEY PROTEIN, associada a carboidratos. Whey Bar contém 16g de Proteina por barra, adição de 12% da IDR (Ingestão Diária Recomendada) de vitaminas e minerais e em média apenas 150 calorias.','Whey Bar (24unid) Probiótica','S',1,1,NULL),(6,'O BCAA FIX é um suplemento que contém, em sua fórmula, alta concentração de aminoácidos de cadeia ramificada, Leucina, Valina e Isoleucina, na proporção 5:1:1, respectivamente.\n\nOs BCAAs (sigla para o termo, em inglês, Branched-chain Amino Acid) fazem parte dos chamados aminoácidos essenciais, ou seja, que não são produzidos naturalmente pelo organismo e, por isso, precisam ser ingeridos por meio da alimentação ou suplementação.\n\nOs aminoácidos são as unidades que bcompõem as proteínas, essas, por sua vez, estão na base da construção dos tecidos musculares.','Bcaa Fix Powder 5:1:1 (300g) Integralmedica','S',1,NULL,NULL),(7,'O Animal Pak é um suplemento multivitamínico e multimineral criado para garantir que seu corpo receba todos os micronutrientes de que necessita para se desenvolver. Se você treina pesado, tenha em mente que suas necessidades diárias não podem ser comparadas às necessidades de uma pessoa comum. Para garantir o máximo em resultados, experimente o Animal Pak.\n\nA fórmula testada do Animal Pak possui ingredientes que são facilmente absorvidos pelo organismo e melhoram a performance, garantindo sólidos ganhos em seus treinamentos e permitindo um treino mais pesado e mais intenso.','Animal Pak (30 packs) Universal Nutrition','S',1,NULL,NULL),(8,'Multivitaminico Cramp Block é formulado à base de Minerais como Sódio, Cálcio, Magnésio e Potássio, além de Vitaminas (B3, B6, C e E). Não Contém Glúten. ','Cramp Block (60caps) Atlhetica Nutrition','S',1,NULL,NULL),(9,'Multivitaminico Poli Complex (100comprimidos) New Millen ','Poli Complex (100 comprimidos) New Millen','S',1,NULL,NULL),(10,'Pasta de Amendoim com Mel (250g) El Shaddai','Pasta de Amendoim com Mel (250g) El Shaddai','S',1,NULL,NULL),(11,'Pasta de Amendoim Integral (1.05kg) Max Titanium','Pasta de Amendoim Integral (1.05kg) ','S',1,2,NULL),(12,'Óleo de Coco 1000mg (120caps) Vitafor','Óleo de Coco 1000mg (120caps) Vitafor','S',1,NULL,NULL),(13,'Proteina de Soja 100% Soy Protein Premium oferece diversos benefícios para os vegetarianos, pessoas com alergias alimentares e indivíduos preocupados com a saúde também. Cada porção de 100% Soy Protein Premium fornece 29g de proteínas com baixo teor de gordura, livre de colesterol, lactose e não GMO.\nPara os atletas, 100% Soy Protein Premium possui todos os aminoácidos essenciais e maiores quantidades de aminoácidos críticos, como o BCAA, Glutamina e Arginina. Sua fonte completa de proteína vegetal.','100% Soy Protein Premium (1kg) Body Action','S',3,NULL,NULL),(14,'Super Proteinato de Cálcio 65% Integral Médica é um suplemento que fortalece o sistema imunológico, eleva os níveis de HDL (colesterol bom), a produção dos hormônios reguladores do metabolismo e a concentração de arginina no organismo. \n\nCom Proteína isolada da soja (Optimum Supro – Dupont® - Solae®), carboidratos complexos e excelente fonte de cálcio, Super Proteinato de Cálcio 65% Integral Médica, é indicado para pessoas com a alimentação desbalanceada, idosos e/ou pessoas com falta desse nutriente no organismo, para o fortalecimento dos ossos. \n\nSuper Proteinato de Cálcio 65% Integral Médica também deve ser utilizado por praticantes de atividades físicas de alta intensidade. Seus carboidratos complexos e baixo índice glicêmico, é ideal para quem deseja bom aporte energético.','Super Proteinato de Cálcio 65% (300g) Integralmedica','S',1,NULL,NULL),(15,'THERMOSHAKE DIET® é uma bebida nutritiva, saborosa e cuidadosamente formulada para pessoas que buscam perder peso com equilíbrio através de uma refeição rápida e prática fornecendo nutrientes balanceados e essenciais ao THERMOSHAKE DIET® reúne em sua composição ingredientes como ômega 3 em pó proveniente do óleo de peixe, alginato de sódio e fibras solúveis que contribuem para uma melhor saciedade , além de proteínas de alta qualidade como whey protein e proteína isolada de soja associados a um completo blend de vitaminas e minerais. THERMOSHAKE DIET® é ideal para ser consumido em qualquer momento do seu dia e pode substituir até 2 refeições diárias em menos de 215 kcal por shake batido com leite desnatado.','Thermo Shake Sdiet (400g) Probiótica','S',1,1,NULL),(16,'Delicioso, nutritivo, saudável e prático, o MAX SHAKE é um alimento que fornece níveis adequados de proteínas, carboidratos, fibras, vitaminas e minerais para a substituição de refeições no processo de manutenção de peso.\n\nMAX SHAKE contém polidextrose, fibra prebiótica queajuda no funcionamento intestinal. Também contém fibra solúvel, encontrada na aveia, que auxilia na diminuição do colesterol LDL (colesterol ruim) e também auxilia no bom funcionamento intestinal.\n\nContém ainda 2 tipos de proteína com alto valor biológico: colágeno hidrolisado, que age no fortalecimento de unhas, cabelos, processos de cicatrização e recuperação de lesões; e a proteína isolada da soja, uma proteína vegetal de alta qualidade que ajuda na formação e manutenção dos tecidos musculares.\nSua formulação exclusiva contém waxy maize, um carboidrato especial, que mesmo complexo, é rapidamente assimilado, e ajuda na absorção mais eficaz dos nutrientes.','Max Shake (400g) Max Titanium','S',1,2,NULL),(17,'Chá Verde (200g) New Millen','Chá Verde (200g) New Millen','S',1,NULL,NULL),(18,'Além de ajudar a eliminar as toxinas do organismo, o DETOX DAY melhora o funcionamento do intestino, ativa o sistema imunológico e aumenta a hidratação do corpo. ','Detox Day (300g) Nutrilibrium Body Action','S',1,NULL,NULL),(19,'A quitosana é uma fibra, e assim como outras, auxilia na redução da absorção de gordura e colesterol. Seu consumo deve estar associado a uma dieta equilibrada e hábitos de vida saudáveis para assim promover a redução do colesterol e a perda de peso.','Quitosana (120caps) Probiótica','S',1,1,NULL),(20,'FATBLOCKER AGE contém o específico Blend FATBLOCKER, que bloqueia a absorção de gorduras e promove a saciedade, auxiliando a eliminação de gorduras pelo organismo e a diminuição do apetite.\n* Antes do consumo, sugerimos que leia atentamente a embalagem. Todas as informações sobre produtos possuem apenas propósitos educativos e não tem a intenção de substituir diagnósticos, prescrições ou orientações profissionais. Imagem meramente ilustrativa.','Fat Blocker (60caps) Nutrilatina Age','S',1,NULL,NULL),(21,'Life Booster Energy Drink é uma bebida ultra zero que contém cafeína, taurina e complexos de vitamina B, zero açucar, zero gordura, zero calorias, zero carboidratos e com baixo teor de sódio.','Energético Ultra Zero (269 ml) Life Booster','S',1,NULL,NULL),(22,'Life Booster Energy Drink é uma bebida ultra zero que contém cafeína, taurina e complexos de vitamina B, zero açucar, zero gordura, zero calorias, zero carboidratos e com baixo teor de sódio.','Energétigo Ultra Zero Maçã Verde (269 ml) Life Booster','S',1,NULL,NULL),(23,'O Colágeno Perfect Woman é uma proteína, e, entre suas principais funções, está a formação de fibras que dão sustentação à pele (para quem se exercita, contribui também na formação dos músculos). Extraído do osso e da cartilagem do boi, o colágeno passa pelo processo de hidrólise, sendo mais facilmente digerido e absorvido pelo organismo. O Colágeno Hidrolisado contém os aminoácidos essenciais glicina e prolina em concentração 20 vezes maior que outras proteínas. Ambos são componentes importantes do tecido conjuntivo e asseguram sua consistência e elasticidade. Vale ressaltar também, o seu poderoso efeito reparador, que atua nos ossos e nas articulações. Anticelulite','Colágeno (250G) New Millen','S',1,NULL,NULL),(24,'CoQ10 tem se mostrado importante na produção de energia (ATP) devido ao fato dessa coenzima ser um componente fundamental da cadeia transportadora de elétrons, diminuindo o processo de fadiga. A suplementação com essa coenzima também atenua a inflamação e o stress oxidativo provocado pelo exercício exaustivo. Antioxidante','COQ 10 (30 Caps) Integralmedica','S',1,NULL,NULL),(25,'A Maca Peruana é um tubérculo originário das Cordilheiras dos Andes e contém naturalmente vitaminas e minerais. MACA PLUS conta com uma combinação de Maca Peruana, vitaminas do Complexo B, Zinco e Magnésio (ZMA).\nMaca Peruana: É uma raiz peruana que tem sido usada como suplemento alimentar. Estuda-se a sua relação positiva com energia e vigor físico (vitalidade).\nVitaminas do Complexo B: Tem papel fundamental para o metabolismo dos carboidratos, aminoácidos e lipídios. \nMagnésio: Componente de mais de 300 enzimas, atua na contração muscular e sua deficiência tem sido associada a câimbras.\nZinco: Atua na modulação de enzimas e são importantes para o sistema imune (fazem a maturação dos linfócitos T eB, responsáveis pelo comando da produção de anticorpos). Antioxidante','Maca Plus (120 Caps) Max Titanium ','S',1,2,NULL),(26,'VO2 PROTEIN BAR é uma barra composta por proteínas, carboidratos, vitaminas, minerais e fibras. É uma excelente opção para lanches intermediários, pois tem um perfil nutricional equilibrado e é um alimento prático, ou seja, pode ser consumido em qualquer lugar e tornar uma refeição rápida mais completa. A Integralmédica aplica sua experiência em suplementação para atletas e desenvolve a linha VO2 para praticantes e adeptos aos exercícios contínuos de longa duração onde se faz necessário um consumo maior de carboidratos, proteínas, vitaminas, minerais e aminoácidos específicos para cada momento do treinamento ou competição. Para isso produzimos complexos de nutrientes com a mais alta eficiência e credibilidade para que você conquiste sua melhor performance. A VO2 disponibiliza aos praticantes de endurance, a melhor e mais completa linha de suplementos nutricionais, assinada por quem realmente conhece o que você precisa e respeita seu esporte, acreditando em seu estilo de vida ativo, saudável e competitivo.\nCada VO2 Slim Bar Integral Médica tem 30 gramas e fornece proteínas (Whey Protein), carboidratos, vitaminas e minerais. Nutricionalmente equilibrada para dar mais energia ao dia-a-dia e ajudar no desenvolvimento da massa magra.','VO2 Slim Bar (24 Unid) Integralmedica','S',1,NULL,NULL),(27,'CAFFEINE+ é um dos suplementos mais apreciados no âmbito esportivo. A cafeína, seu princípio ativo, aumenta o estado de alerta/atenção, diminui a percepção de estresse durante o exercício e aumenta a capacidade física em exercícios de resistência. CAFFEINE+ tem um efeito estimulante sobre o sistema nervoso central, sobre o sistema cardiovascular, sobre a libertação de catecolaminas, e, em geral, sobre o metabolismo. Por isso este princípio ativo é bem-sucedido no âmbito de produtos complementares para os desportistas. Termogênico','Caffeine + (100 Tabs) 4 Plus Nutrition','S',1,NULL,NULL),(28,'A batata doce é rica em fibras, tendo baixo índice glicêmico e, consequentemente, repondo energia gradualmente ao organismo, fornecendo saciedade por um prazo maior. Além disso, apresenta vitaminas, como betacaroteno e vitamína k, antioxidantes e minerais, sendo um alimento ideal para praticantes de atividades físicas, auxiliando para um treino equilibrado e uma vida saudável.','Batata Doce (Pó-600G) ','S',1,2,NULL),(29,'Colágeno Hidrolisado Body Action protege contra o aparecimento de rugas, flacidez e a fragilidade articular e óssea.\nCom o passar dos anos, o corpo deixa de produzir o colágeno na quantidade necessária. Por se tratar de uma proteína abundante no organismo, a reposição é necessária. \nColágeno Hidrolisado Body Action é preparado reduzindo drasticamente o tamanho molecular da gelatina, utilizando as enzimas. Após passar por um processo de reaquecimento, as soluções concentradas são secas em “spray driers”. \nO resultado desse processo é o Colágeno Hidrolisado Body Action em pó, contendo alto teor proteico, repondo o colágeno necessário no organismo.','Colágeno Clinical Slim (500 Mg - 90 Caps) Body Action','S',1,NULL,''),(30,'O colágeno é um tipo de proteína que, entre outras funções, é responsável por dar fi rmeza à nossa pele. A partir dos 25 anos, sua produção diminui tornando a pele mais suscetível ao aparecimento de rugas.','Collagen Nutrify (90 Tabs) Integralmedica','S',1,NULL,NULL),(31,'Chromaway Picolinate USA (90 Tabs) Midway. Cromo','Chromaway Picolinate USA (90 Tabs) Midway','S',1,NULL,NULL),(32,'Carbo Plus Universal é uma excelente fonte de energia.\nTodo atleta precisa de carboidratos, só que o excesso deles pode causar intolerância à insulina. O Carbo Plus Universal contém a dose ideal de polímeros de glicose fornecendo maior energia para máxima recuperação pós-atividade física com alta qualidade nutricional. Dextrose','Carbo Plus (1000G) Universal Nutrition','S',1,NULL,NULL),(33,'Dextrose é um monossacarídeo de rápida digestão e absorção, indicado para ser tomado próximo aos exercícios com objetivo de fornecer energia imediata ao atleta.','Dextrose (1000G) Probiótica','S',1,1,NULL),(34,'GUARANÁ da PROBIOTICA é um suplemento à base de guaraná, fruto cuja semente é fonte de cafeína, um estimulante natural. Cada cápsula contém em média 270mg de guaraná em pó.','Guaraná (120 Caps) Probiótica','S',1,1,NULL),(35,'Para atingir o objetivo de hipertrofia é preciso fornecer aos músculos um aporte adequado de carboidratos, capaz de gerar a energia necessária para um treino eficaz e para reposição do glicogênio após tal treino. Esse é o papel da perfeita combinação entre um carboidrato simples (dextrose) e outro complexo (maltodextrina) encontrada em SUPERMASS.\nAs proteínas encontradas em Super Mass oferecem aminoácidos fundamentais na construção e recuperação dos tecidos musculares – também compõem este blend nutricional que se destaca como um dos mais saborosos da categoria.\nSeja no pré ou no pós-treino, ou ainda para complementar outra refeição do dia, SUPERMASS é uma escolha inteligente e prática para uma suplementação energética. Hipercalórico','Super Mass (3KG) Integralmedica','S',1,NULL,NULL),(36,'Linolen Appetite Control contém em sua fórmula o exclusivo Total Control Complex, um complexo sinérgico de ativos que controlam o apetite e a ansiedade. Isso favorece o controle da ingestão calórica e promove o bem-estar.\nAssim, fica mais fácil manter a dieta, garantindo resultados melhores e mais eficientes no processo de emagrecimento.','Linolen Appetite Control (60 Tabs) Nutrilatina Age','S',1,NULL,NULL),(37,'O Omega EFA é um suplemento à base de óleo de linhaça: um óleo rico em ômega 3, 6 e 9, essenciais para a saúde. Independente dos seus objetivos, é sempre importante ter na dieta uma fonte de ácidos graxos essenciais (EFAs). Experimente o Omega EFA.','Omega EFA (90 Softgels) Universal Nutrition','S',1,NULL,NULL),(38,'Carb Up, o energético da Probiótica foi elaborado com uma composição especial de carboidratos com baixo e alto índice glicêmico, e whey protein, ideal para rápida reposição de glicogênio muscular e recuperação dos músculos. \nUma dieta com elevado teor de carboidratos e aminoácido BCAA’s, é essencial para aumentar os estoques de glicogênio nos músculos durante o exercício, influenciando de forma positiva o desempenho do físico.\nCarb Up Probiótica contém também vitamina C e vitaminas do complexo B, que atuam como antioxidante e cofatores dos metabolismos energético, além de minerais importantes para serem suplementados durante o exercício devido à perda através da transpiração. Maltodextrina','Carb Up (800G) Probiótica','S',1,1,NULL),(39,'Contém 100% da necessidade diária de Vitaminas e Minerais que um atleta precisa para obter seu melhor desempenho durante os treinos.\nAgora com nova fórmula, ultra concentrada e desenvolvida à partir de estudos avançados para oferecer em 1 cápsula 100% da necessidade diária da suplementação que um atleta precisa para melhorar sua performance. Multivitamínico','Master Vit (90 Caps) Power Suplements ','S',1,NULL,NULL),(40,'O Vita Rec - Integral Médica é um energético perfeito para melhorar a recuperação física após treinos intensos e desgastantes. Complexo de vitaminas e minerais que atua melhorando o desempenho e construindo um físico saudável e mais definido. Além de beneficiar o metabolismo dos carboidratos, proteínas e gorduras. Multivitamínico','Vita Recovery (60 Tabs) Integralmedica','S',1,NULL,NULL),(41,'Linolen CocoNut Duo (60caps) Nutrilatina. Óleo de Coco','Linolen Coconut Duo (60 Caps) Nutrilatina','S',1,NULL,NULL),(42,'Efa Golden Arnold Nutrition é indicado nas dietas de emagrecimento. Melhorando o metabolismo, aumentando a queima de gordura localizada e consequente ganho de massa muscular. \nOs nutrientes existentes em Efa Golden Arnold Nutrition são os ácidos graxos, muito importantes no controle dos níveis de colesterol e auxilio na redução da gordura armazenada em excesso. \nComposto por cinco óleos e ácidos graxos, Efa Golden Arnold Nutrition contém os Óleos de: Linhaça, Cártamo, Gergelim, Girassol e Borragem e os Ácidos Graxos Ômega 3, 6 e 9. Ômega','Efa Golden-8 (100 Caps) Arnold Nutrition','S',1,NULL,NULL),(43,'O Óleo de Peixe da New Millen possui como principal componente o Omega-3, ácido graxo que deve ser obtido através de alguns alimentos específicos ou complementos nutricionais. O Óleo de Peixe tem propriedades de reduzir a incidência de doenças cardiácas por adequar os níveis de colesterol no sangue, além de apresentar poderosa propriedade antiinflamatórias. O Óleo de Peixe Ômega-3 ainda conta com Vitamina E, que combate os radicais livres, retarda o envelhecimento.','Ômega 3 (60 Caps) New Millen','S',1,NULL,NULL),(44,'Ômega 3 (90cáps) Max Titanium','Ômega 3 (90 Caps) Max Titanium','S',1,2,NULL),(45,'Proteína da carne é com absoluta certeza uma das melhores fontes de proteínas existente no mundo. A Dymatize conseguiu isolar as propriedades únicas de carne e transformá-la em pó para preparo instantâneo, delicioso e conveniente para o uso a qualquer hora do dia. PRIMAL Elite é um ultra-concentrado proteíco de carne bovina sem igual.','Elite Primal (1.861G) Dymatize','S',1,NULL,NULL),(46,'Soyplex Vitafor é suplemento alimentar à base de soja, ideal para quem busca uma opção de bebida que substitua o leite de origem animal. \nSoyplex Vitafor foi elaborado com ingredientes selecionandos exclusivamente, tornando o produto excelente fonte de proteína isolada da soja, fibras prebióticas (FOS), multivitaminas e minerais.\nSoyplex Vitafor é isento de lactose, proteína animal, açúcar e não possui conservantes químicos. Fácil de preparar e disponível em vários sabores. Proteína de Soja','Soyplex (300G) Vitafor','S',1,NULL,NULL),(47,'Ideal para ser consumido durante treinos longos e intensos como: Ciclismo, natação, maratonas, lutas, futebol, e tantos outros esportes.\nOs beneficios desta formulação especialmente planejada são de fornecer energia imediatamente através de carboidratos de rápida absorção como: maltodextrina, dextrose, waxy maize e ribose; e triglicérides de cadeia média (TCM), que fornecem energia em grande densidade.\nAuxilia também no aporte de proteína, oferecendo whey protein isolado e hidrolisado, além de contar com aminoácido arginina, precursor de óxido nítrico (NO2). Ainda fornece vitaminas do complexo B, antioxidantes e minerais, auxiliando no combate aos radicais livres gerados durante o exercício. Ribose','New Up (600G) Max Titanium','S',1,2,NULL),(48,'Hemo Stack Arnold Nutrition é natural e atua mantendo níveis saudáveis de hemoglobina, ferritina e hematócrito no sangue, suprindo também a deficiência de ferro, dando mais energia ao atleta.\n<br><br>\nO Hemo Stack Arnold Nutrition é diferenciado por conter um exclusivo complexo enzimático que atua no sangue, HEMOSTACK PROPRIETARY BLEND, de excelente perfil nutricional, com aminoácidos e antioxidantes. \n<br><br>\nA combinação existente na fórmula de Hemo Stack Arnold Nutrition é anti-toxina e possui também vitamina C, proporcionando melhor absorção e fortalecimento do sistema imunológico, além de vitamina B12, B6, Biotina Ácido Fólico e Zinco, indispensáveis para maior saúde do sangue, melhorando a produção de células vermelhas, e auxiliando na rápida regeneração do sistema nervoso. ','Hemo Stack (100 Tabs) Arnold Nutrition','S',5,NULL,NULL),(49,'100% GOLD WHEY PROTEIN RED SERIES é um suplemento esportivo à base de whey protein, proteína hidrolizada do trico e colágeno. 100% GOLD WHEY PROTEIN é muito conhecido e utilizado por atletas, já que é uma proteína com elevado valor biológico. ','100% Gold Whey (900G) Redseries ','S',1,NULL,NULL),(50,'100% Pure Whey é suplemento proteico para atletas, formulado com proteína de alto valor biológico, matéria-prima importada dos EUA e extraída do soro do leite por processo de filtração. 100% Pure Whey é a combinação perfeita de proteínas, indicada para suprir necessidades proteicas, contém alto teor de Aminoácidos, os quais abastecem os tecidos musculares com os aminoácidos necessários para a máxima construção muscular. Não Contém Glúten.','100% Pure whey Pro Series (900G) Athletica Nutrition','S',1,NULL,NULL);
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
) ENGINE=InnoDB AUTO_INCREMENT=101 DEFAULT CHARSET=utf8 COMMENT='Fks: id_prod_distr ou por (id_distribuidora + id_produto). ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos_distribuidora`
--

LOCK TABLES `produtos_distribuidora` WRITE;
/*!40000 ALTER TABLE `produtos_distribuidora` DISABLE KEYS */;
INSERT INTO `produtos_distribuidora` (`ID_PROD_DIST`, `ID_PROD`, `ID_DISTRIBUIDORA`, `VAL_PROD`, `FLAG_ATIVO`) VALUES (51,1,1,167.68,'S'),(52,2,1,9.19,'S'),(53,3,1,131.99,'S'),(54,4,1,32.07,'S'),(55,5,1,69.96,'S'),(56,6,1,171.06,'S'),(57,7,1,161.57,'S'),(58,8,1,152.81,'S'),(59,9,1,71.27,'S'),(60,10,1,75.83,'S'),(61,11,1,105.61,'S'),(62,12,1,75.35,'S'),(63,13,1,128.70,'S'),(64,14,1,30.04,'S'),(65,15,1,109.05,'S'),(66,16,1,14.98,'S'),(67,17,1,30.42,'S'),(68,18,1,179.52,'S'),(69,19,1,3.63,'S'),(70,20,1,141.96,'S'),(71,21,1,23.99,'S'),(72,22,1,194.74,'S'),(73,23,1,103.61,'S'),(74,24,1,125.90,'S'),(75,25,1,88.81,'S'),(76,26,1,50.21,'S'),(77,27,1,104.53,'S'),(78,28,1,69.77,'S'),(79,29,1,17.97,'S'),(80,30,1,123.95,'S'),(81,31,1,74.96,'S'),(82,32,1,170.43,'S'),(83,33,1,54.97,'S'),(84,34,1,133.89,'S'),(85,35,1,64.71,'S'),(86,36,1,145.20,'S'),(87,37,1,131.25,'S'),(88,38,1,186.89,'S'),(89,39,1,67.07,'S'),(90,40,1,77.98,'S'),(91,41,1,66.56,'S'),(92,42,1,160.06,'S'),(93,43,1,103.52,'S'),(94,44,1,21.86,'S'),(95,45,1,164.79,'S'),(96,46,1,16.20,'S'),(97,47,1,27.16,'S'),(98,48,1,2.00,'S'),(99,49,1,192.26,'S'),(100,50,1,46.61,'S');
/*!40000 ALTER TABLE `produtos_distribuidora` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sys_parametros`
--

DROP TABLE IF EXISTS `sys_parametros`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sys_parametros` (
  `COD_CIDADE` int(11) NOT NULL DEFAULT '0',
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
  `desc_webappfolder` text,
  `IGNORAR_REGRAMAIOR18` char(1) DEFAULT NULL,
  `FACE_REDIRECT_URI_WEBAPP` text,
  `TRAGOAQUI_NUM_TELEFONE` text,
  `TRAGOAQUI_PAG_FACEBOOK` text,
  `app_versao` text,
  `applicacao` int(11) DEFAULT NULL,
  PRIMARY KEY (`COD_CIDADE`),
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
INSERT INTO `sys_parametros` (`COD_CIDADE`, `ID_USUARIO_ADMIN`, `FLAG_MANUTENCAO`, `DESC_KEY`, `SEGS_TESTE_AJAX`, `FACE_APP_ID`, `FACE_APP_SECRETKEY`, `FACE_APP_TOKEN`, `FACE_REDIRECT_URI`, `url_system`, `sys_host_name_smtp`, `sys_smtp_port`, `sys_email`, `sys_senha`, `sys_fromemail`, `sys_fromdesc`, `sys_tls`, `id_visitante`, `PED_HORASOKEY`, `NUM_TEMPOMAXCANC_MINUTO`, `COD_RECUSA`, `ONESIGNAL_KEY`, `ONESIGNAL_URL`, `ONESIGNAL_APPID`, `NUM_MINUTOS_NOT_FINAL`, `NUM_SEGS_NOT_FINAL_EXEC`, `cod_cancelamentosys`, `desc_webappfolder`, `IGNORAR_REGRAMAIOR18`, `FACE_REDIRECT_URI_WEBAPP`, `TRAGOAQUI_NUM_TELEFONE`, `TRAGOAQUI_PAG_FACEBOOK`, `app_versao`, `applicacao`) VALUES (1,1,'N','VY6nC95dK3gT5TLcCy9GKs',5,248219065608502,'43d1aa52e6d93bb358c6d03c61122b05','248219065608502|u_IdexAZ9JXkpXBZFoHkCv6NebQ','http://localhost:4400/oauthcallback.html','www.tragoaqui.com.br/tragoaqui_fit/','smtp.kinghost.net',587,'suporte_fitness@tragoaqui.com.br','caracteres1','suporte_fitness@tragoaqui.com.br','TragoAqui Fitness','N',2,1,45,1,'N2ViMWYxZjEtZjNmYy00NDExLWE1ZjMtNWJkNDg5MTAyNDI2','https://onesignal.com/api/v1/notifications','fb0bc7ee-4e99-4f2f-82a8-aca56a434ea9',20,360,6,'tragoaqui_fit','S','http://www.tragoaqui.com.br/appfit/oauthcallback.html','51 9 9677 5246','www.facebook.com/tragoaquifitness','0.0.15',2);
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
  `DATE_LASTAJAX` datetime DEFAULT NULL,
  `FLAG_LEUTERMOS` char(1) DEFAULT NULL,
  PRIMARY KEY (`ID_USUARIO`),
  KEY `FK_REFERENCE_5` (`COD_BAIRRO`),
  KEY `FK_REFERENCE_7` (`COD_CIDADE`),
  CONSTRAINT `FK_REFERENCE_5` FOREIGN KEY (`COD_BAIRRO`) REFERENCES `bairros` (`COD_BAIRRO`),
  CONSTRAINT `FK_REFERENCE_7` FOREIGN KEY (`COD_CIDADE`) REFERENCES `cidade` (`COD_CIDADE`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`ID_USUARIO`, `DESC_NOME`, `DESC_TELEFONE`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_ENDERECO_NUM`, `DESC_ENDERECO_COMPLEMENTO`, `COD_BAIRRO`, `DESC_CARTAO`, `DATA_EXP_MES`, `DATA_EXP_ANO`, `DESC_CARDHOLDERNAME`, `PAY_ID`, `DESC_CPF`, `FLAG_FACEUSER`, `ID_USER_FACE`, `FLAG_ATIVADO`, `CHAVE_ATIVACAO`, `DESC_NOVOEMAILVALIDACAO`, `CHAVE_ATIVACAO_NOVOEMAIL`, `FLAG_MAIORIDADE`, `DATE_LASTAJAX`, `FLAG_LEUTERMOS`) VALUES (1,'Admin','99999999999','admin','affsd','morratu@hotmail.com',1,'','','',NULL,'',NULL,NULL,'','','',NULL,NULL,'S',NULL,'','','S','2017-02-14 09:34:56','S'),(2,'Visitante',NULL,'v','v',NULL,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'V',NULL,NULL,NULL,'S','2017-02-14 09:35:48',NULL),(3,'Matheus Goltz',NULL,'matheusgoltz@hotmail.com','je52go9qqpcss9kj4nk9g6m6pl','matheusgoltz@hotmail.com',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',10212212710372257,'S',NULL,NULL,NULL,'N',NULL,NULL),(4,'Gabriel Dalcin Kothe',NULL,'g.kothe@hotmail.com','6i4sk0a50qqe08nn7fr5jd25l3','g.kothe@hotmail.com',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',1176533682462871,'S',NULL,NULL,NULL,NULL,'2017-02-14 11:00:34','S'),(5,'Walter Matheus Hermann',NULL,'w.matheus_@hotmail.com','2dvvpoj3hhe2vk5rcalob48ms5','w.matheus_@hotmail.com',1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,'S',1371536922918001,'S',NULL,NULL,NULL,NULL,'2017-02-13 22:10:27','S');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `tragoaqui03`
--

USE `tragoaqui03`;

--
-- Final view structure for view `generator_16`
--

/*!50001 DROP VIEW IF EXISTS `generator_16`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`tragoaqui03`@`%` SQL SECURITY DEFINER */
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
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`tragoaqui03`@`%` SQL SECURITY DEFINER */
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
/*!50001 SET character_set_client      = latin1 */;
/*!50001 SET character_set_results     = latin1 */;
/*!50001 SET collation_connection      = latin1_swedish_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013 DEFINER=`tragoaqui03`@`%` SQL SECURITY DEFINER */
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

-- Dump completed on 2017-02-14 11:27:13
