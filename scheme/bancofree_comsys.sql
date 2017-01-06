-- MySQL dump 10.13  Distrib 5.7.9, for Win64 (x86_64)
--
-- Host: mysql05-farm68.kinghost.net    Database: tragoaqui01
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
-- Current Database: `tragoaqui01`
--



USE `tragoaqui02`;

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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora`
--

LOCK TABLES `distribuidora` WRITE;
/*!40000 ALTER TABLE `distribuidora` DISABLE KEYS */;
INSERT INTO `distribuidora` (`ID_DISTRIBUIDORA`, `COD_CIDADE`, `DESC_RAZAO_SOCIAL`, `DESC_NOME_ABREV`, `VAL_ENTREGA_MIN`, `DESC_TELEFONE`, `DESC_ENDERECO`, `NUM_ENDEREC`, `DESC_COMPLEMENTO`, `VAL_TELE_ENTREGA`, `FLAG_CUSTOM`, `DESC_LOGIN`, `DESC_SENHA`, `DESC_MAIL`, `FLAG_ATIVO_MASTER`, `FLAG_ATIVO`, `FLAG_MODOPAGAMENTO`, `DATE_LASTAJAX`, `flag_entre_ret`, `PERC_PAGAMENTO`, `TXT_OBS_HORA`, `COD_BAIRRO`, `ID_USUARIO_INSERT`, `FLAG_TIPOCONTRATO`) VALUES (1,1,'TragoAqui Admin','TragoAqui Admin',0.00,'','www.tragoaqui.com.br','','',0.00,'N','admin','affsd','suporte@tragoaqui.com.br','S','F','A','2017-01-04 18:35:20','A',0.00,'',NULL,1,NULL);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `distribuidora_bairro_entrega`
--

LOCK TABLES `distribuidora_bairro_entrega` WRITE;
/*!40000 ALTER TABLE `distribuidora_bairro_entrega` DISABLE KEYS */;
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos`
--

LOCK TABLES `produtos` WRITE;
/*!40000 ALTER TABLE `produtos` DISABLE KEYS */;
INSERT INTO `produtos` (`ID_PROD`, `DESC_PROD`, `DESC_ABREVIADO`, `FLAG_ATIVO`, `QTD_IMAGES`) VALUES (1,'Produto de teste tragoaqui','teste','S',1);
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='Fks: id_prod_distr ou por (id_distribuidora + id_produto). ';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `produtos_distribuidora`
--

LOCK TABLES `produtos_distribuidora` WRITE;
/*!40000 ALTER TABLE `produtos_distribuidora` DISABLE KEYS */;
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
  `desc_webappfolder` text,
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
INSERT INTO `sys_parametros` (`COD_CIDADE`, `ID_USUARIO_ADMIN`, `FLAG_MANUTENCAO`, `DESC_KEY`, `SEGS_TESTE_AJAX`, `FACE_APP_ID`, `FACE_APP_SECRETKEY`, `FACE_APP_TOKEN`, `FACE_REDIRECT_URI`, `url_system`, `sys_host_name_smtp`, `sys_smtp_port`, `sys_email`, `sys_senha`, `sys_fromemail`, `sys_fromdesc`, `sys_tls`, `id_visitante`, `PED_HORASOKEY`, `NUM_TEMPOMAXCANC_MINUTO`, `COD_RECUSA`, `ONESIGNAL_KEY`, `ONESIGNAL_URL`, `ONESIGNAL_APPID`, `NUM_MINUTOS_NOT_FINAL`, `NUM_SEGS_NOT_FINAL_EXEC`, `cod_cancelamentosys`, `desc_webappfolder`) VALUES (1,1,'N','VxLnKYc3Pba6HTLcCy9GKs',5,1318779544860496,'8363c050fed8b9d86a41d4b48ff77478','1318779544860496|pjg3iSnM4GkCLRtvb2i3CPg5nZA','http://localhost:4400/oauthcallback.html','www.tragoaqui.com.br/tragoaqui','smtp.kinghost.net',587,'suporte@tragoaqui.com.br','caracteres1','suporte@tragoaqui.com.br','TragoAqui','N',NULL,0,45,1,'NmJmN2VhMmQtNjJkMi00OTRkLTg4M2UtODhhNjkzNzcyYTg5','https://onesignal.com/api/v1/notifications','6b1103df-ebeb-43e1-860b-e2903a9b12ae',20,360,6,'tragoaqui');
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
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` (`ID_USUARIO`, `DESC_NOME`, `DESC_TELEFONE`, `DESC_USER`, `DESC_SENHA`, `DESC_EMAIL`, `COD_CIDADE`, `DESC_ENDERECO`, `DESC_ENDERECO_NUM`, `DESC_ENDERECO_COMPLEMENTO`, `COD_BAIRRO`, `DESC_CARTAO`, `DATA_EXP_MES`, `DATA_EXP_ANO`, `DESC_CARDHOLDERNAME`, `PAY_ID`, `DESC_CPF`, `FLAG_FACEUSER`, `ID_USER_FACE`, `FLAG_ATIVADO`, `CHAVE_ATIVACAO`, `DESC_NOVOEMAILVALIDACAO`, `CHAVE_ATIVACAO_NOVOEMAIL`, `FLAG_MAIORIDADE`) VALUES (1,'Admin','99999999999','admin','Metal4@ll','morratu@hotmail.com',1,'','','',NULL,'',NULL,NULL,'','','',NULL,NULL,'S',NULL,'','','S');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Current Database: `tragoaqui01`
--

USE `tragoaqui02`;

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
/*!50013 DEFINER=`tragoaqui01`@`%` SQL SECURITY DEFINER */
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
/*!50013 DEFINER=`tragoaqui01`@`%` SQL SECURITY DEFINER */
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
/*!50013 DEFINER=`tragoaqui01`@`%` SQL SECURITY DEFINER */
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

-- Dump completed on 2017-01-06 15:08:31
