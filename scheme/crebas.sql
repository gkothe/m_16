/*==============================================================*/
/* DBMS name:      MySQL 5.0                                    */
/* Created on:     19/8/2016 4:24:30 PM                         */
/*==============================================================*/


drop table if exists AUTOCOMPLETE;

drop table if exists BAIRROS;

drop table if exists CARRINHO;

drop table if exists CARRINHO_ITEM;

drop table if exists CIDADE;

drop table if exists DIAS_SEMANA;

drop table if exists DISTRIBUIDORA;

drop table if exists DISTRIBUIDORA_BAIRRO_ENTREGA;

drop table if exists DISTRIBUIDORA_HORARIO_DIA_ENTRE;

drop table if exists MOTIVOS_RECUSA;

drop table if exists PEDIDO;

drop table if exists PEDIDO_ITEM;

drop table if exists PEDIDO_MOTIVOS_RECUSA;

drop table if exists PRODUTOS;

drop table if exists PRODUTOS_DISTRIBUIDORA;

drop table if exists USUARIO;

/*==============================================================*/
/* Table: AUTOCOMPLETE                                          */
/*==============================================================*/
create table AUTOCOMPLETE
(
   ID_AUTO              bigint not null auto_increment,
   TERMO                Varchar(100),
   NUM_RANK             bigint,
   primary key (ID_AUTO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: BAIRROS                                               */
/*==============================================================*/
create table BAIRROS
(
   COD_BAIRRO           INT4 not null auto_increment,
   COD_CIDADE           INT4,
   DESC_BAIRRO          TEXT not null,
   primary key (COD_BAIRRO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: CARRINHO                                              */
/*==============================================================*/
create table CARRINHO
(
   ID_CARRINHO          INT8 not null,
   ID_USUARIO           INT8,
   COD_BAIRRO           INT4,
   DATA_CRIACAO         datetime,
   primary key (ID_CARRINHO)
);

/*==============================================================*/
/* Table: CARRINHO_ITEM                                         */
/*==============================================================*/
create table CARRINHO_ITEM
(
   ID_CARRINHO          INT8 not null,
   SEQ_ITEM             INT4 not null,
   ID_PROD_DIST         INT4,
   QTD                  INT4,
   primary key (ID_CARRINHO, SEQ_ITEM)
);

/*==============================================================*/
/* Table: CIDADE                                                */
/*==============================================================*/
create table CIDADE
(
   COD_CIDADE           INT4 not null auto_increment,
   DESC_CIDADE          TEXT not null,
   primary key (COD_CIDADE)
)
auto_increment = 1;

/*==============================================================*/
/* Table: DIAS_SEMANA                                           */
/*==============================================================*/
create table DIAS_SEMANA
(
   COD_DIA              INT4 not null auto_increment,
   DESC_DIA             TEXT,
   primary key (COD_DIA)
)
auto_increment = 1;

/*==============================================================*/
/* Table: DISTRIBUIDORA                                         */
/*==============================================================*/
create table DISTRIBUIDORA
(
   ID_DISTRIBUIDORA     INT4 not null auto_increment,
   COD_CIDADE           INT4,
   DESC_RAZAO_SOCIAL    TEXT,
   DESC_NOME_ABREV      TEXT,
   VAL_ENTREGA_MIN      NUMERIC(12,2),
   DESC_TELEFONE        VARCHAR(50),
   DESC_ENDERECO        TEXT,
   NUM_ENDEREC          VARCHAR(10),
   DESC_COMPLEMENTO     TEXT,
   VAL_TELE_ENTREGA     NUMERIC(12,2),
   FLAG_CUSTOM          char(1),
   FLAG_ATIVO           char(1),
   DESC_LOGIN           varchar(45),
   DESC_SENHA           varchar(45),
   FLAG_ATIVO_MASTER    char(1),
   DESC_MAIL            text,
   primary key (ID_DISTRIBUIDORA)
)
auto_increment = 1;

/*==============================================================*/
/* Table: DISTRIBUIDORA_BAIRRO_ENTREGA                          */
/*==============================================================*/
create table DISTRIBUIDORA_BAIRRO_ENTREGA
(
   ID_DISTR_BAIRRO      INT4 not null auto_increment,
   COD_BAIRRO           INT4 not null,
   ID_DISTRIBUIDORA     INT4 not null,
   VAL_TELE_ENTREGA     NUMERIC(12,2),
   FLAG_TELEBAIRRO      char(1),
   primary key (ID_DISTR_BAIRRO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: DISTRIBUIDORA_HORARIO_DIA_ENTRE                       */
/*==============================================================*/
create table DISTRIBUIDORA_HORARIO_DIA_ENTRE
(
   ID_HORARIO           INT4 not null auto_increment,
   ID_DISTRIBUIDORA     INT4 not null,
   COD_DIA              INT4 not null,
   ID_DISTR_BAIRRO      INT4,
   HORARIO_INI          TIME,
   HORARIO_FIM          TIME,
   primary key (ID_HORARIO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: MOTIVOS_RECUSA                                        */
/*==============================================================*/
create table MOTIVOS_RECUSA
(
   COD_MOTIVO           INT4 not null auto_increment,
   DESC_MOTIVO          TEXT not null,
   primary key (COD_MOTIVO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: PEDIDO                                                */
/*==============================================================*/
create table PEDIDO
(
   ID_PEDIDO            INT8 not null auto_increment,
   ID_DISTRIBUIDORA     INT4,
   ID_USUARIO           INT8,
   DATA_PEDIDO          datetime,
   FLAG_STATUS          CHAR(1),
   VAL_TOTALPROD        NUMERIC(12,2),
   VAL_ENTREGA          NUMERIC(12,2),
   DATA_PEDIDO_RESPOSTA datetime,
   NUM_PED              INT8,
   COD_BAIRRO           INT4,
   NUM_TELEFONECONTATO_CLIENTE VARCHAR(50),
   TEMPO_ESTIMADO_ENTREGA TIME,
   DESC_ENDERECO_ENTREGA TEXT,
   DESC_ENDERECO_NUM_ENTREGA VARCHAR(20),
   DESC_ENDERECO_COMPLEMENTO_ENTREGA VARCHAR(20),
   primary key (ID_PEDIDO)
)
auto_increment = 1;

/*==============================================================*/
/* Table: PEDIDO_ITEM                                           */
/*==============================================================*/
create table PEDIDO_ITEM
(
   ID_PEDIDO_ITEM       int(8) not null auto_increment,
   ID_PEDIDO            INT8 not null,
   SEQ_ITEM             INT4 not null,
   VAL_UNIT             NUMERIC(12,2),
   ID_PROD              INT4 not null,
   QTD_PROD             INT8,
   primary key (ID_PEDIDO_ITEM)
)
auto_increment = 1;

/*==============================================================*/
/* Table: PEDIDO_MOTIVOS_RECUSA                                 */
/*==============================================================*/
create table PEDIDO_MOTIVOS_RECUSA
(
   ID_PEDIDO_MOTIVO_RECUSA INT8 not null auto_increment,
   ID_PEDIDO            INT8 not null,
   COD_MOTIVO           INT4 not null,
   primary key (ID_PEDIDO_MOTIVO_RECUSA)
)
auto_increment = 1;

/*==============================================================*/
/* Table: PRODUTOS                                              */
/*==============================================================*/
create table PRODUTOS
(
   ID_PROD              INT4 not null auto_increment,
   DESC_PROD            TEXT not null,
   DESC_ABREVIADO       VARCHAR(100) not null,
   FLAG_ATIVO           CHAR(1) not null,
   primary key (ID_PROD)
)
auto_increment = 1;

/*==============================================================*/
/* Table: PRODUTOS_DISTRIBUIDORA                                */
/*==============================================================*/
create table PRODUTOS_DISTRIBUIDORA
(
   ID_PROD_DIST         int(4) not null auto_increment,
   ID_PROD              INT4 not null,
   ID_DISTRIBUIDORA     INT4 not null,
   VAL_PROD             NUMERIC(12,2),
   FLAG_ATIVO           CHAR(1),
   primary key (ID_PROD_DIST)
)
auto_increment = 1;

/*==============================================================*/
/* Table: USUARIO                                               */
/*==============================================================*/
create table USUARIO
(
   ID_USUARIO           INT8 not null auto_increment,
   DESC_NOME            TEXT,
   DESC_TELEFONE        VARCHAR(50),
   DESC_USER            VARCHAR(50),
   DESC_SENHA           VARCHAR(50),
   DESC_EMAIL           VARCHAR(150),
   COD_CIDADE           INT4,
   DESC_ENDERECO        TEXT,
   DESC_ENDERECO_NUM    VARCHAR(20),
   DESC_ENDERECO_COMPLEMENTO VARCHAR(20),
   COD_BAIRRO           INT4,
   DESC_CARTAO          VARCHAR(20),
   DATA_EXP_MES         INT4,
   DATA_EXP_ANO         INT4,
   DESC_CARDHOLDERNAME  Text,
   PAY_ID               TEXT,
   DESC_CPF             varchar(15),
   primary key (ID_USUARIO)
)
auto_increment = 1;

alter table BAIRROS add constraint FK_REFERENCE_21 foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE) on delete restrict on update restrict;

alter table CARRINHO add constraint FK_REFERENCE_22 foreign key (ID_USUARIO)
      references USUARIO (ID_USUARIO) on delete restrict on update restrict;

alter table CARRINHO add constraint FK_REFERENCE_25 foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO) on delete restrict on update restrict;

alter table CARRINHO_ITEM add constraint FK_REFERENCE_23 foreign key (ID_CARRINHO)
      references CARRINHO (ID_CARRINHO) on delete restrict on update restrict;

alter table CARRINHO_ITEM add constraint FK_REFERENCE_24 foreign key (ID_PROD_DIST)
      references PRODUTOS_DISTRIBUIDORA (ID_PROD_DIST) on delete restrict on update restrict;

alter table DISTRIBUIDORA add constraint FK_REFERENCE_6 foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE) on delete restrict on update restrict;

alter table DISTRIBUIDORA_BAIRRO_ENTREGA add constraint FK_REFERENCE_3 foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO) on delete restrict on update restrict;

alter table DISTRIBUIDORA_BAIRRO_ENTREGA add constraint FK_REFERENCE_4 foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA) on delete restrict on update restrict;

alter table DISTRIBUIDORA_HORARIO_DIA_ENTRE add constraint FK_REFERENCE_20 foreign key (ID_DISTR_BAIRRO)
      references DISTRIBUIDORA_BAIRRO_ENTREGA (ID_DISTR_BAIRRO) on delete restrict on update restrict;

alter table DISTRIBUIDORA_HORARIO_DIA_ENTRE add constraint FK_REFERENCE_8 foreign key (COD_DIA)
      references DIAS_SEMANA (COD_DIA) on delete restrict on update restrict;

alter table PEDIDO add constraint FK_REFERENCE_13 foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA) on delete restrict on update restrict;

alter table PEDIDO add constraint FK_REFERENCE_15 foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO) on delete restrict on update restrict;

alter table PEDIDO add constraint FK_REFERENCE_17 foreign key (ID_USUARIO)
      references USUARIO (ID_USUARIO) on delete restrict on update restrict;

alter table PEDIDO_ITEM add constraint FK_REFERENCE_14 foreign key (ID_PEDIDO)
      references PEDIDO (ID_PEDIDO) on delete restrict on update restrict;

alter table PEDIDO_ITEM add constraint FK_REFERENCE_16 foreign key (ID_PROD)
      references PRODUTOS (ID_PROD) on delete restrict on update restrict;

alter table PEDIDO_MOTIVOS_RECUSA add constraint FK_REFERENCE_18 foreign key (COD_MOTIVO)
      references MOTIVOS_RECUSA (COD_MOTIVO) on delete restrict on update restrict;

alter table PEDIDO_MOTIVOS_RECUSA add constraint FK_REFERENCE_19 foreign key (ID_PEDIDO)
      references PEDIDO (ID_PEDIDO) on delete restrict on update restrict;

alter table PRODUTOS_DISTRIBUIDORA add constraint FK_REFERENCE_1 foreign key (ID_PROD)
      references PRODUTOS (ID_PROD) on delete restrict on update restrict;

alter table PRODUTOS_DISTRIBUIDORA add constraint FK_REFERENCE_2 foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA) on delete restrict on update restrict;

alter table USUARIO add constraint FK_REFERENCE_5 foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO) on delete restrict on update restrict;

alter table USUARIO add constraint FK_REFERENCE_7 foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE) on delete restrict on update restrict;

