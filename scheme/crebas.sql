/*==============================================================*/
/* DBMS name:      PostgreSQL 9.x                               */
/* Created on:     3/8/2016 10:52:35 AM                         */
/*==============================================================*/


drop table BAIRROS;

drop table CIDADE;

drop table DIAS_SEMANA;

drop table DISTRIBUIDORA;

drop table DISTRIBUIDORA_BAIRRO_ENTREGA;

drop table DISTRIBUIDORA_HORARIO_DIA_ENTRE;

drop table MOTIVOS_RECUSA;

drop table PEDIDO;

drop table PEDIDO_ITEM;

drop table PEDIDO_MOTIVOS_RECUSA;

drop table PRODUTOS;

drop table PRODUTOS_DISTRIBUIDORA;

drop table USUARIO;

/*==============================================================*/
/* Table: BAIRROS                                               */
/*==============================================================*/
create table BAIRROS (
   COD_BAIRRO           INT4                 not null,
   COD_CIDADE           INT4                 null,
   DESC_BAIRRO          TEXT                 not null,
   constraint PK_BAIRROS primary key (COD_BAIRRO)
);

/*==============================================================*/
/* Table: CIDADE                                                */
/*==============================================================*/
create table CIDADE (
   COD_CIDADE           INT4                 not null,
   DESC_CIDADE          TEXT                 not null,
   constraint PK_CIDADE primary key (COD_CIDADE)
);

/*==============================================================*/
/* Table: DIAS_SEMANA                                           */
/*==============================================================*/
create table DIAS_SEMANA (
   COD_DIA              INT4                 not null,
   DESC_DIA             TEXT                 null,
   constraint PK_DIAS_SEMANA primary key (COD_DIA)
);

/*==============================================================*/
/* Table: DISTRIBUIDORA                                         */
/*==============================================================*/
create table DISTRIBUIDORA (
   ID_DISTRIBUIDORA     INT4                 not null,
   COD_CIDADE           INT4                 null,
   DESC_RAZAO_SOCIAL    TEXT                 null,
   DESC_NOME_ABREV      TEXT                 null,
   VAL_ENTREGA_MIN      NUMERIC(12,2)        null,
   DESC_TELEFONE        VARCHAR(50)          null,
   DESC_ENDERECO        TEXT                 null,
   NUM_ENDEREC          VARCHAR(10)          null,
   DESC_COMPLEMENTO     TEXT                 null,
   VAL_TELE_ENTREGA     NUMERIC(12,2)        null,
   constraint PK_DISTRIBUIDORA primary key (ID_DISTRIBUIDORA)
);

/*==============================================================*/
/* Table: DISTRIBUIDORA_BAIRRO_ENTREGA                          */
/*==============================================================*/
create table DISTRIBUIDORA_BAIRRO_ENTREGA (
   COD_BAIRRO           INT4                 not null,
   ID_DISTRIBUIDORA     INT4                 not null,
   VAL_TELE_ENTREGA     NUMERIC(12,2)        null,
   constraint PK_DISTRIBUIDORA_BAIRRO_ENTREG primary key (COD_BAIRRO, ID_DISTRIBUIDORA)
);

/*==============================================================*/
/* Table: DISTRIBUIDORA_HORARIO_DIA_ENTRE                       */
/*==============================================================*/
create table DISTRIBUIDORA_HORARIO_DIA_ENTRE (
   ID_HORARIO           INT4                 not null,
   ID_DISTRIBUIDORA     INT4                 not null,
   COD_DIA              INT4                 not null,
   COD_BAIRRO           INT4                 null,
   HORARIO_INI          TIME                 null,
   HORARIO_FIM          TIME                 null,
   constraint PK_DISTRIBUIDORA_HORARIO_DIA_E primary key (ID_HORARIO)
);

/*==============================================================*/
/* Table: MOTIVOS_RECUSA                                        */
/*==============================================================*/
create table MOTIVOS_RECUSA (
   COD_MOTIVO           INT4                 not null,
   DESC_MOTIVO          TEXT                 not null,
   constraint PK_MOTIVOS_RECUSA primary key (COD_MOTIVO)
);

/*==============================================================*/
/* Table: PEDIDO                                                */
/*==============================================================*/
create table PEDIDO (
   ID_PEDIDO            INT8                 not null,
   ID_DISTRIBUIDORA     INT4                 null,
   ID_USUARIO           INT8                 null,
   DATA_PEDIDO          DATE                 null,
   FLAG_STATUS          CHAR(1)              null,
   VAL_TOTALPROD        NUMERIC(12,2)        null,
   VAL_ENTREGA          NUMERIC(12,2)        null,
   DATA_PEDIDO_RESPOSTA DATE                 null,
   NUM_PED              INT8                 null,
   COD_BAIRRO           INT4                 null,
   DESC_ENDERECO_ENTREGA TEXT                 null,
   NUM_TELEFONECONTATO_CLIENTE VARCHAR(50)          null,
   TEMPO_ESTIMADO_ENTREGA TIME                 null,
   constraint PK_PEDIDO primary key (ID_PEDIDO)
);

/*==============================================================*/
/* Table: PEDIDO_ITEM                                           */
/*==============================================================*/
create table PEDIDO_ITEM (
   ID_PEDIDO            INT8                 not null,
   SEQ_ITEM             INT4                 not null,
   VAL_UNIT             NUMERIC(12,2)        null,
   ID_PROD              INT4                 not null,
   QTD_PROD             INT8                 null,
   constraint PK_PEDIDO_ITEM primary key (ID_PEDIDO, SEQ_ITEM)
);

/*==============================================================*/
/* Table: PEDIDO_MOTIVOS_RECUSA                                 */
/*==============================================================*/
create table PEDIDO_MOTIVOS_RECUSA (
   ID_PEDIDO            INT8                 not null,
   COD_MOTIVO           INT4                 not null,
   constraint PK_PEDIDO_MOTIVOS_RECUSA primary key (ID_PEDIDO, COD_MOTIVO)
);

/*==============================================================*/
/* Table: PRODUTOS                                              */
/*==============================================================*/
create table PRODUTOS (
   ID_PROD              INT4                 not null,
   DESC_PROD            TEXT                 not null,
   DESC_ABREVIADO       VARCHAR(100)         not null,
   IMG_IMAGEM           CHAR(10)             null,
   FLAG_ATIVO           CHAR(1)              not null,
   constraint PK_PRODUTOS primary key (ID_PROD)
);

/*==============================================================*/
/* Table: PRODUTOS_DISTRIBUIDORA                                */
/*==============================================================*/
create table PRODUTOS_DISTRIBUIDORA (
   ID_PROD              INT4                 not null,
   ID_DISTRIBUIDORA     INT4                 not null,
   VAL_PROD             NUMERIC(12,2)        null,
   IMG_IMAGEM           CHAR(10)             null,
   FLAG_ATIVO           CHAR(1)              null,
   constraint PK_PRODUTOS_DISTRIBUIDORA primary key (ID_PROD, ID_DISTRIBUIDORA)
);

/*==============================================================*/
/* Table: USUARIO                                               */
/*==============================================================*/
create table USUARIO (
   ID_USUARIO           INT8                 not null,
   DESC_NOME            TEXT                 null,
   DESC_TELEFONE        VARCHAR(50)          null,
   COD_BAIRRO           INT4                 null,
   COD_CIDADE           INT4                 null,
   DESC_ENDERECO        TEXT                 null,
   DESC_USER            VARCHAR(50)          null,
   DESC_SENHA           VARCHAR(50)          null,
   DESC_EMAIL           VARCHAR(150)         null,
   DESC_CARTAO          VARCHAR(20)          null,
   constraint PK_USUARIO primary key (ID_USUARIO)
);

alter table BAIRROS
   add constraint FK_BAIRROS_REFERENCE_CIDADE foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE)
      on delete restrict on update restrict;

alter table DISTRIBUIDORA
   add constraint FK_DISTRIBU_REFERENCE_CIDADE foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE)
      on delete restrict on update restrict;

alter table DISTRIBUIDORA_BAIRRO_ENTREGA
   add constraint FK_DISTRIBU_REFERENCE_BAIRROS foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO)
      on delete restrict on update restrict;

alter table DISTRIBUIDORA_BAIRRO_ENTREGA
   add constraint FK_DISTRIBU_REFERENCE_DISTRIBU foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA)
      on delete restrict on update restrict;

alter table DISTRIBUIDORA_HORARIO_DIA_ENTRE
   add constraint FK_DISTRIBU_REFERENCE_DISTRIBU foreign key (COD_BAIRRO, ID_DISTRIBUIDORA)
      references DISTRIBUIDORA_BAIRRO_ENTREGA (COD_BAIRRO, ID_DISTRIBUIDORA)
      on delete restrict on update restrict;

alter table DISTRIBUIDORA_HORARIO_DIA_ENTRE
   add constraint FK_DISTRIBU_REFERENCE_DIAS_SEM foreign key (COD_DIA)
      references DIAS_SEMANA (COD_DIA)
      on delete restrict on update restrict;

alter table PEDIDO
   add constraint FK_PEDIDO_REFERENCE_DISTRIBU foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA)
      on delete restrict on update restrict;

alter table PEDIDO
   add constraint FK_PEDIDO_REFERENCE_BAIRROS foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO)
      on delete restrict on update restrict;

alter table PEDIDO
   add constraint FK_PEDIDO_REFERENCE_USUARIO foreign key (ID_USUARIO)
      references USUARIO (ID_USUARIO)
      on delete restrict on update restrict;

alter table PEDIDO_ITEM
   add constraint FK_PEDIDO_I_REFERENCE_PEDIDO foreign key (ID_PEDIDO)
      references PEDIDO (ID_PEDIDO)
      on delete restrict on update restrict;

alter table PEDIDO_ITEM
   add constraint FK_PEDIDO_I_REFERENCE_PRODUTOS foreign key (ID_PROD)
      references PRODUTOS (ID_PROD)
      on delete restrict on update restrict;

alter table PEDIDO_MOTIVOS_RECUSA
   add constraint FK_PEDIDO_M_REFERENCE_MOTIVOS_ foreign key (COD_MOTIVO)
      references MOTIVOS_RECUSA (COD_MOTIVO)
      on delete restrict on update restrict;

alter table PEDIDO_MOTIVOS_RECUSA
   add constraint FK_PEDIDO_M_REFERENCE_PEDIDO foreign key (ID_PEDIDO)
      references PEDIDO (ID_PEDIDO)
      on delete restrict on update restrict;

alter table PRODUTOS_DISTRIBUIDORA
   add constraint FK_PRODUTOS_REFERENCE_PRODUTOS foreign key (ID_PROD)
      references PRODUTOS (ID_PROD)
      on delete restrict on update restrict;

alter table PRODUTOS_DISTRIBUIDORA
   add constraint FK_PRODUTOS_REFERENCE_DISTRIBU foreign key (ID_DISTRIBUIDORA)
      references DISTRIBUIDORA (ID_DISTRIBUIDORA)
      on delete restrict on update restrict;

alter table USUARIO
   add constraint FK_USUARIO_REFERENCE_BAIRROS foreign key (COD_BAIRRO)
      references BAIRROS (COD_BAIRRO)
      on delete restrict on update restrict;

alter table USUARIO
   add constraint FK_USUARIO_REFERENCE_CIDADE foreign key (COD_CIDADE)
      references CIDADE (COD_CIDADE)
      on delete restrict on update restrict;

