/*

Crea la estrucuta de base de datos para utilizar el servicio de gestión de credenciales, para acceder

/sede/servicio/credenciales/

Usuario: admin
Contraseña: prueba

*/


   CREATE SEQUENCE  "GCZ_GRUPO_OPERACIONES_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 30 CACHE 20 NOORDER  NOCYCLE ;
   CREATE SEQUENCE  "GCZ_GRUPO_USUARIO_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 760 CACHE 20 NOORDER  NOCYCLE ;
   CREATE SEQUENCE  "GCZ_PERFIL_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 4 CACHE 5 NOORDER  NOCYCLE ;
   CREATE SEQUENCE  "GCZ_USUARIO_SEQ"  MINVALUE 1 MAXVALUE 999999999999999999999999999 INCREMENT BY 1 START WITH 2 CACHE 20 NOORDER  NOCYCLE ;

  CREATE TABLE "GCZ_ACCESTOKEN" 
   (	"ID_USUARIO" NUMBER(14,0), 
	"EXPIRATION" DATE, 
	"ID_APLICACION" NUMBER, 
	"TOKEN" VARCHAR2(100)
   ) ;
/
  CREATE TABLE "GCZ_GRUPO_OPERACIONES" 
   (	"ID_GRUPO_OPERACIONES" NUMBER(14,0), 
	"CODIGO_GRUPO_OPERACIONES" VARCHAR2(100) DEFAULT '', 
	"NOMBRE" VARCHAR2(100) DEFAULT '', 
	"DESCRIPCION" VARCHAR2(1000) DEFAULT '', 
	"CODIGO_SERVICIO" VARCHAR2(30) DEFAULT '', 
	"CODIGO_SECCION" VARCHAR2(30) DEFAULT '', 
	"GCZ_PUBLICADO" VARCHAR2(1) DEFAULT 'N', 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE, 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"TIPO" VARCHAR2(3) DEFAULT 'PRE'
   ) ;
/
  CREATE TABLE "GCZ_GRUPO_USUARIO" 
   (	"ID_GRUPO_USUARIO" NUMBER(14,0), 
	"NOMBRE" VARCHAR2(100) DEFAULT '', 
	"DESCRIPCION" VARCHAR2(2000) DEFAULT '', 
	"GCZ_PUBLICADO" VARCHAR2(1) DEFAULT 'N', 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE, 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"CODIGO_GRUPO_USUARIO" VARCHAR2(50)
   ) ;
/

  CREATE TABLE "GCZ_PERFIL" 
   (	"CODIGO_PERFIL" VARCHAR2(30) DEFAULT '', 
	"CODIGO_SERVICIO" VARCHAR2(30) DEFAULT '', 
	"NOMBRE" VARCHAR2(100) DEFAULT NULL, 
	"DESCRIPCION" VARCHAR2(4000) DEFAULT NULL, 
	"GCZ_PUBLICADO" VARCHAR2(1), 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE, 
	"ID_PERFIL" NUMBER(14,0)
   ) ;
/

  CREATE TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" 
   (	"ID_GRUPO_OPERACIONES" NUMBER(14,0), 
	"ID_PERFIL" NUMBER(14,0)
   ) ;
/

  CREATE TABLE "GCZ_PERFIL_GRUPO_USUARIO" 
   (	"ID_GRUPO_USUARIO" NUMBER(14,0), 
	"ID_PERFIL" NUMBER(14,0)
   ) ;
/
  CREATE TABLE "GCZ_PERFIL_USUARIO" 
   (	"ID_USUARIO" NUMBER(14,0), 
	"ID_PERFIL" NUMBER(14,0)
   ) ;
/

  CREATE TABLE "GCZ_PROPIEDAD_USUARIO" 
   (	"ID_USUARIO" NUMBER, 
	"ETIQUETA" VARCHAR2(40), 
	"VALOR" VARCHAR2(200)
   ) ;
/

  CREATE TABLE "GCZ_SECCION" 
   (	"CODIGO_SECCION" VARCHAR2(30) DEFAULT '', 
	"CODIGO_SERVICIO" VARCHAR2(30) DEFAULT '', 
	"NOMBRE" VARCHAR2(100) DEFAULT NULL, 
	"DESCRIPCION" VARCHAR2(4000) DEFAULT NULL, 
	"ESTADO" VARCHAR2(1) DEFAULT NULL, 
	"WWW_URL" VARCHAR2(400) DEFAULT NULL, 
	"INI_URL" VARCHAR2(400) DEFAULT NULL, 
	"GCZ_PUBLICADO" VARCHAR2(1), 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE
   ) ;
/

  CREATE TABLE "GCZ_SERVICIO" 
   (	"CODIGO_SERVICIO" VARCHAR2(30) DEFAULT '', 
	"NOMBRE" VARCHAR2(100) DEFAULT NULL, 
	"DESCRIPCION" VARCHAR2(4000) DEFAULT NULL, 
	"ESTADO" VARCHAR2(1) DEFAULT NULL, 
	"CODIGO_SECCION_DEFECTO" VARCHAR2(30), 
	"DIRECTORIO_BASE" VARCHAR2(400) DEFAULT NULL, 
	"GCZ_PUBLICADO" VARCHAR2(1), 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE, 
	"GCZ_USUARIOSADMIN" VARCHAR2(300) DEFAULT ''
   ) ;
/

  CREATE TABLE "GCZ_SERVICIO_AUDITOR" 
   (	"CODIGO_SERVICIO" VARCHAR2(30) DEFAULT NULL, 
	"ID_USUARIO" NUMBER(14,0) DEFAULT NULL
   ) ;
/

  CREATE TABLE "GCZ_USUARIO" 
   (	"ID_USUARIO" NUMBER(14,0), 
	"LOGIN" VARCHAR2(100) DEFAULT '', 
	"NOMBRE" VARCHAR2(100) DEFAULT '', 
	"APELLIDO_1" VARCHAR2(100) DEFAULT '', 
	"APELLIDO_2" VARCHAR2(100), 
	"CONTRASENNA" VARCHAR2(100) DEFAULT '', 
	"CORREO_ELECTRONICO" VARCHAR2(100) DEFAULT '', 
	"ESTADO" VARCHAR2(1) DEFAULT 'B', 
	"BLOQUEADO" VARCHAR2(1) DEFAULT 'N', 
	"CODIGO_SERVICIO_DEFECTO" VARCHAR2(30) DEFAULT '', 
	"CODIGO_SECCION_DEFECTO" VARCHAR2(30) DEFAULT '', 
	"NUM_INTENTOS_FALLIDOS" NUMBER(*,0) DEFAULT 0, 
	"FECHA_ULTIMO_ACCESO" DATE DEFAULT NULL, 
	"GCZ_PUBLICADO" VARCHAR2(1) DEFAULT 'N', 
	"GCZ_FECHAALTA" DATE, 
	"GCZ_FECHAMOD" DATE, 
	"GCZ_FECHAPUB" DATE, 
	"GCZ_USUARIOALTA" VARCHAR2(100), 
	"GCZ_USUARIOMOD" VARCHAR2(100), 
	"GCZ_USUARIOPUB" VARCHAR2(100), 
	"ADMINISTRADOR" VARCHAR2(1) DEFAULT 'S', 
	"SECRETKEY" VARCHAR2(200)
   ) ;
/

  CREATE TABLE "GCZ_USUARIO_GRUPO_USUARIO" 
   (	"ID_GRUPO_USUARIO" NUMBER(14,0), 
	"ID_USUARIO" NUMBER(14,0)
   ) ;
/

  CREATE TABLE "GCZ_USUARIO_RECUPERAR_PASS" 
   (	"ID_USUARIO" NUMBER, 
	"TOKEN" VARCHAR2(400)
   ) ;
/

  CREATE TABLE "LIDERES" 
   (	"ID_LIDER" NUMBER NOT NULL ENABLE, 
	"ID_ASOCIADO" NUMBER NOT NULL ENABLE, 
	"TIPO_ASOCIADO" VARCHAR2(300 BYTE) NOT NULL ENABLE, 
	"ID_USUARIO" NUMBER NOT NULL ENABLE, 
	"TIPO_USUARIO" VARCHAR2(300 BYTE) NOT NULL ENABLE,
	CONSTRAINT "LIDERES_PK" PRIMARY KEY ("ID_LIDER")
   );
/
REM INSERTING into GCZ_ACCESTOKEN
SET DEFINE OFF;
REM INSERTING into GCZ_GRUPO_OPERACIONES
SET DEFINE OFF;
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('1','DEL',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2','DET',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('3','PUB',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4','MOD',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('5','NEW',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('6','DOC',null,null,'ADMIN','ADMIN','S',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('7','DEL',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('8','DET',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('9','DOC',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('10','MOD',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('11','NEW',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('12','PUB',null,null,'REUTILIZADOR','APLICACION','S',to_date('15/07/13','DD/MM/RR'),null,to_date('16/07/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('13','PUB',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('14','DEL',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('15','DET',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('16','DOC',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('17','MOD',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('18','NEW',null,null,'REUTILIZADOR','DATASET','S',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('19','ADMIN','Permiso para ver todos los registros',null,'REUTILIZADOR','DATASET','S',to_date('12/05/14','DD/MM/RR'),null,to_date('12/05/14','DD/MM/RR'),'admin',null,'admin','ESP');
REM INSERTING into GCZ_GRUPO_USUARIO
SET DEFINE OFF;
Insert into GCZ_GRUPO_USUARIO (ID_GRUPO_USUARIO,NOMBRE,DESCRIPCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,CODIGO_GRUPO_USUARIO) values ('750','Reutilizadores',null,'S',null,null,null,null,null,null,'reutilizadores');
REM INSERTING into GCZ_PERFIL
SET DEFINE OFF;
Insert into GCZ_PERFIL (CODIGO_PERFIL,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,ID_PERFIL) values ('ADMIN','ADMIN','Administrador',null,'S','admin','admin','admin',to_date('09/05/16','DD/MM/RR'),to_date('23/08/16','DD/MM/RR'),to_date('09/05/16','DD/MM/RR'),'1');
Insert into GCZ_PERFIL (CODIGO_PERFIL,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,ID_PERFIL) values ('ADMINDATASETREUTILIZADOR','REUTILIZADOR','Administradores de datasets de reutilizadores',null,'S','admin','admin','admin',to_date('12/05/14','DD/MM/RR'),to_date('12/05/14','DD/MM/RR'),to_date('12/05/14','DD/MM/RR'),'2');
Insert into GCZ_PERFIL (CODIGO_PERFIL,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,ID_PERFIL) values ('REUTILIZADORES','REUTILIZADOR','Reutilizadores',null,'S','admin',null,'admin',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),'3');
REM INSERTING into GCZ_PERFIL_GRUPO_OPERACIONES
SET DEFINE OFF;
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('1','1');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2','1');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('3','1');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4','1');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('5','1');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('7','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('8','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('10','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('11','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('14','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('15','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('17','3');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('18','3');
REM INSERTING into GCZ_PERFIL_GRUPO_USUARIO
SET DEFINE OFF;
Insert into GCZ_PERFIL_GRUPO_USUARIO (ID_GRUPO_USUARIO,ID_PERFIL) values ('750','3');
REM INSERTING into GCZ_PERFIL_USUARIO
SET DEFINE OFF;
Insert into GCZ_PERFIL_USUARIO (ID_USUARIO,ID_PERFIL) values ('1','1');
REM INSERTING into GCZ_PROPIEDAD_USUARIO
SET DEFINE OFF;
REM INSERTING into GCZ_SECCION
SET DEFINE OFF;
Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('ADMIN','ADMIN','Administracion',null,'S',null,'../admin/admin.do?operation=listar','S','admin',null,'admin',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'));
Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('APLICACION','REUTILIZADOR','Aplicaciones desarrolladas por terceros',null,'S',null,'../reutilizador/aplicacion.do?operation=listar','S','admin',null,'admin',to_date('15/07/13','DD/MM/RR'),null,to_date('15/07/13','DD/MM/RR'));
Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('DATASET','REUTILIZADOR','Conjuntos de datos utilizados por reutilizadores',null,'S',null,'../reutilizador/dataset.do?operation=listar','S','admin',null,'admin',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'));
REM INSERTING into GCZ_SERVICIO
SET DEFINE OFF;
Insert into GCZ_SERVICIO (CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,CODIGO_SECCION_DEFECTO,DIRECTORIO_BASE,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOSADMIN) values ('ADMIN','Administración',null,null,'ADMIN',null,'S','admin',null,'admin',to_date('09/05/16','DD/MM/RR'),null,to_date('09/05/16','DD/MM/RR'),null);
Insert into GCZ_SERVICIO (CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,CODIGO_SECCION_DEFECTO,DIRECTORIO_BASE,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOSADMIN) values ('REUTILIZADOR','Servicios para reutilzadores',null,null,'DATASET',null,'S','admin',null,'admin',to_date('12/06/13','DD/MM/RR'),null,to_date('12/06/13','DD/MM/RR'),null);
REM INSERTING into GCZ_SERVICIO_AUDITOR
SET DEFINE OFF;
REM INSERTING into GCZ_USUARIO
SET DEFINE OFF;
Insert into GCZ_USUARIO (ID_USUARIO,LOGIN,NOMBRE,APELLIDO_1,APELLIDO_2,CONTRASENNA,CORREO_ELECTRONICO,ESTADO,BLOQUEADO,CODIGO_SERVICIO_DEFECTO,CODIGO_SECCION_DEFECTO,NUM_INTENTOS_FALLIDOS,FECHA_ULTIMO_ACCESO,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,ADMINISTRADOR,SECRETKEY) values ('1','admin','Administraador','Sistema','General','c893bad68927b457dbed39460e6afd62','correo@prueba.es','B','N','ADMIN','ADMIN','0',null,'S',null,to_date('23/08/16','DD/MM/RR'),null,'admin','admin',null,'S','A6s4dPYSh/d3BWCK7vHwbw==');
REM INSERTING into GCZ_USUARIO_GRUPO_USUARIO
SET DEFINE OFF;
Insert into GCZ_USUARIO_GRUPO_USUARIO (ID_GRUPO_USUARIO,ID_USUARIO) values ('750','1');
REM INSERTING into GCZ_USUARIO_RECUPERAR_PASS
SET DEFINE OFF;
--------------------------------------------------------
--  Constraints for Table GCZ_ACCESTOKEN
--------------------------------------------------------

  ALTER TABLE "GCZ_ACCESTOKEN" ADD CONSTRAINT "GCZ_ACCESTOKEN_PK" PRIMARY KEY ("ID_USUARIO", "ID_APLICACION") ENABLE;
 
  ALTER TABLE "GCZ_ACCESTOKEN" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_ACCESTOKEN" MODIFY ("EXPIRATION" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_ACCESTOKEN" MODIFY ("TOKEN" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_ACCESTOKEN" MODIFY ("ID_APLICACION" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_GRUPO_OPERACIONES
--------------------------------------------------------

  ALTER TABLE "GCZ_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_GRUPO_OPE_PK71179234019546" PRIMARY KEY ("ID_GRUPO_OPERACIONES") ENABLE;
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_GRUPO_OPE_UK21180465747718" UNIQUE ("CODIGO_SERVICIO", "CODIGO_SECCION", "CODIGO_GRUPO_OPERACIONES") ENABLE;
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("ID_GRUPO_OPERACIONES" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("CODIGO_GRUPO_OPERACIONES" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("CODIGO_SERVICIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("CODIGO_SECCION" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("GCZ_PUBLICADO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_OPERACIONES" MODIFY ("TIPO" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_GRUPO_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_GRUPO_USU_UK21179214900671" UNIQUE ("CODIGO_GRUPO_USUARIO") ENABLE;
 
  ALTER TABLE "GCZ_GRUPO_USUARIO" MODIFY ("ID_GRUPO_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_USUARIO" MODIFY ("GCZ_PUBLICADO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_USUARIO" MODIFY ("CODIGO_GRUPO_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_GRUPO_USUARIO" ADD PRIMARY KEY ("ID_GRUPO_USUARIO") ENABLE;
/
--------------------------------------------------------
--  Constraints for Table GCZ_PERFIL
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL" ADD CONSTRAINT "GCZ_PERFIL_PK41179484554078" PRIMARY KEY ("ID_PERFIL") ENABLE;
 
  ALTER TABLE "GCZ_PERFIL" ADD CONSTRAINT "GCZ_PERFIL_UK101179484588015" UNIQUE ("CODIGO_PERFIL", "CODIGO_SERVICIO") ENABLE;
 
  ALTER TABLE "GCZ_PERFIL" MODIFY ("CODIGO_PERFIL" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PERFIL" MODIFY ("CODIGO_SERVICIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PERFIL" MODIFY ("ID_PERFIL" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_PERFIL_GRUPO_OPERACIONES
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_PERFIL_GR_PK31180467894281" PRIMARY KEY ("ID_GRUPO_OPERACIONES", "ID_PERFIL") ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" MODIFY ("ID_GRUPO_OPERACIONES" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" MODIFY ("ID_PERFIL" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_PERFIL_GRUPO_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_GR_PK31180423768921" PRIMARY KEY ("ID_GRUPO_USUARIO", "ID_PERFIL") ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_USUARIO" MODIFY ("ID_GRUPO_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_USUARIO" MODIFY ("ID_PERFIL" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_PERFIL_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_US_PK71180423581375" PRIMARY KEY ("ID_USUARIO", "ID_PERFIL") ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_USUARIO" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PERFIL_USUARIO" MODIFY ("ID_PERFIL" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_PROPIEDAD_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PROPIEDAD_USUARIO" ADD CONSTRAINT "GCZ_PROPIEDAD_USUARIO_PK" PRIMARY KEY ("ID_USUARIO", "ETIQUETA") ENABLE;
 
  ALTER TABLE "GCZ_PROPIEDAD_USUARIO" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_PROPIEDAD_USUARIO" MODIFY ("ETIQUETA" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_SECCION
--------------------------------------------------------

  ALTER TABLE "GCZ_SECCION" MODIFY ("CODIGO_SECCION" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_SECCION" MODIFY ("CODIGO_SERVICIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_SECCION" ADD PRIMARY KEY ("CODIGO_SECCION", "CODIGO_SERVICIO") ENABLE;
/
--------------------------------------------------------
--  Constraints for Table GCZ_SERVICIO
--------------------------------------------------------

  ALTER TABLE "GCZ_SERVICIO" MODIFY ("CODIGO_SERVICIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_SERVICIO" ADD PRIMARY KEY ("CODIGO_SERVICIO") ENABLE;
/
--------------------------------------------------------
--  Constraints for Table GCZ_SERVICIO_AUDITOR
--------------------------------------------------------

  ALTER TABLE "GCZ_SERVICIO_AUDITOR" ADD CONSTRAINT "GCZ_SERVICIO__PK41179691043984" PRIMARY KEY ("CODIGO_SERVICIO", "ID_USUARIO") ENABLE;
 
  ALTER TABLE "GCZ_SERVICIO_AUDITOR" MODIFY ("CODIGO_SERVICIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_SERVICIO_AUDITOR" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_USUARIO" ADD CONSTRAINT "AK_GCZ_USUARIO_1" UNIQUE ("LOGIN") ENABLE;
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("LOGIN" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("CONTRASENNA" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("CORREO_ELECTRONICO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("CODIGO_SERVICIO_DEFECTO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("CODIGO_SECCION_DEFECTO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("NUM_INTENTOS_FALLIDOS" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" MODIFY ("GCZ_PUBLICADO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO" ADD PRIMARY KEY ("ID_USUARIO") ENABLE;
/
--------------------------------------------------------
--  Constraints for Table GCZ_USUARIO_GRUPO_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_USUARIO_GRUPO_USUARIO" ADD CONSTRAINT "SYS_C003430_1" PRIMARY KEY ("ID_GRUPO_USUARIO", "ID_USUARIO") ENABLE;
 
  ALTER TABLE "GCZ_USUARIO_GRUPO_USUARIO" MODIFY ("ID_GRUPO_USUARIO" NOT NULL ENABLE);
 
  ALTER TABLE "GCZ_USUARIO_GRUPO_USUARIO" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Constraints for Table GCZ_USUARIO_RECUPERAR_PASS
--------------------------------------------------------

  ALTER TABLE "GCZ_USUARIO_RECUPERAR_PASS" ADD CONSTRAINT "GCZ_USUARIO_RECUPERAR_PAS_PK" PRIMARY KEY ("ID_USUARIO") ENABLE;
 
  ALTER TABLE "GCZ_USUARIO_RECUPERAR_PASS" MODIFY ("ID_USUARIO" NOT NULL ENABLE);
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_ACCESTOKEN
--------------------------------------------------------

  ALTER TABLE "GCZ_ACCESTOKEN" ADD CONSTRAINT "GCZ_ACCESTOKEN_GCZ_USUARI_FK1" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_GRUPO_OPERACIONES
--------------------------------------------------------

  ALTER TABLE "GCZ_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_GRUPO_OPE_FK91183362038062" FOREIGN KEY ("CODIGO_SECCION", "CODIGO_SERVICIO")
	  REFERENCES "GCZ_SECCION" ("CODIGO_SECCION", "CODIGO_SERVICIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_PERFIL
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL" ADD CONSTRAINT "FK_GCZ_PERFIL_1_1" FOREIGN KEY ("CODIGO_SERVICIO")
	  REFERENCES "GCZ_SERVICIO" ("CODIGO_SERVICIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_PERFIL_GRUPO_OPERACIONES
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_PERFIL_GR_FK81183362205000" FOREIGN KEY ("ID_PERFIL")
	  REFERENCES "GCZ_PERFIL" ("ID_PERFIL") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_OPERACIONES" ADD CONSTRAINT "GCZ_PERFIL_GR_FK91183362205000" FOREIGN KEY ("ID_GRUPO_OPERACIONES")
	  REFERENCES "GCZ_GRUPO_OPERACIONES" ("ID_GRUPO_OPERACIONES") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_PERFIL_GRUPO_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_GR_FK21183362537718" FOREIGN KEY ("ID_PERFIL")
	  REFERENCES "GCZ_PERFIL" ("ID_PERFIL") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_GR_FK31183362637937" FOREIGN KEY ("ID_GRUPO_USUARIO")
	  REFERENCES "GCZ_GRUPO_USUARIO" ("ID_GRUPO_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_PERFIL_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PERFIL_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_US_FK21183362775843" FOREIGN KEY ("ID_PERFIL")
	  REFERENCES "GCZ_PERFIL" ("ID_PERFIL") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "GCZ_PERFIL_USUARIO" ADD CONSTRAINT "GCZ_PERFIL_US_FK31183362974890" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_PROPIEDAD_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_PROPIEDAD_USUARIO" ADD CONSTRAINT "GCZ_PROPIEDAD_USUARIO_GCZ_FK1" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_SECCION
--------------------------------------------------------

  ALTER TABLE "GCZ_SECCION" ADD CONSTRAINT "GCZ_SECCION_FK21183363092968" FOREIGN KEY ("CODIGO_SERVICIO")
	  REFERENCES "GCZ_SERVICIO" ("CODIGO_SERVICIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_SERVICIO_AUDITOR
--------------------------------------------------------

  ALTER TABLE "GCZ_SERVICIO_AUDITOR" ADD CONSTRAINT "GCZ_SERVICIO__FK21183363275609" FOREIGN KEY ("CODIGO_SERVICIO")
	  REFERENCES "GCZ_SERVICIO" ("CODIGO_SERVICIO") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "GCZ_SERVICIO_AUDITOR" ADD CONSTRAINT "GCZ_SERVICIO__FK31183363430453" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_USUARIO_GRUPO_USUARIO
--------------------------------------------------------

  ALTER TABLE "GCZ_USUARIO_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_USUARIO_G_FK21183363574312" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "GCZ_USUARIO_GRUPO_USUARIO" ADD CONSTRAINT "GCZ_USUARIO_G_FK31183363686968" FOREIGN KEY ("ID_GRUPO_USUARIO")
	  REFERENCES "GCZ_GRUPO_USUARIO" ("ID_GRUPO_USUARIO") ON DELETE CASCADE ENABLE;
/
--------------------------------------------------------
--  Ref Constraints for Table GCZ_USUARIO_RECUPERAR_PASS
--------------------------------------------------------

  ALTER TABLE "GCZ_USUARIO_RECUPERAR_PASS" ADD CONSTRAINT "GCZ_USUARIO_RECUPERAR_PAS_FK1" FOREIGN KEY ("ID_USUARIO")
	  REFERENCES "GCZ_USUARIO" ("ID_USUARIO") ON DELETE CASCADE ENABLE;
/
