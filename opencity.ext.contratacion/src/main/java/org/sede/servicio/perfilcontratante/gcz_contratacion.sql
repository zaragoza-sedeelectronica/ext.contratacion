Insert into GCZ_SERVICIO (CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,CODIGO_SECCION_DEFECTO,DIRECTORIO_BASE,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOSADMIN) values ('PERFILCONTRATANTE','PERFILCONTRATANTE','Agenda de actividades',null,'CONTRATO',null,'S','admin','admin','admin',to_date('25/10/10','DD/MM/RR'),to_date('01/06/16','DD/MM/RR'),to_date('08/11/10','DD/MM/RR'),null);

Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('PORTAL','PERFILCONTRATANTE','Programas',null,'S',null,'../perfilcontratante/portal.do?operation=listar','S','admin',null,'admin',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'));
Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('CONTRATO','PERFILCONTRATANTE','Actos',null,'S',null,'../perfilcontratante/contrato.do?operation=listar','S','admin',null,'admin',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'));
Insert into GCZ_SECCION (CODIGO_SECCION,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,ESTADO,WWW_URL,INI_URL,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB) values ('SELLADO','PERFILCONTRATANTE','Temas',null,'S',null,'../perfilcontratante/sellado.do?operation=listar','S','admin','admin','admin',to_date('25/10/10','DD/MM/RR'),to_date('03/06/15','DD/MM/RR'),to_date('08/11/10','DD/MM/RR'));

Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2111','DET',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2112','DEL',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2113','DOC',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2114','MOD',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2115','NEW',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2116','PUB',null,null,'PERFILCONTRATANTE','CONTRATO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4759','LOADVIRTUOSO','Carga jsonld en virtuoso',null,'PERFILCONTRATANTE','CONTRATO','S',to_date('09/06/16','DD/MM/RR'),null,to_date('09/06/16','DD/MM/RR'),'admin',null,'admin','ESP');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2123','DET',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2124','DEL',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2125','DOC',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2126','MOD',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2127','NEW',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('2128','PUB',null,null,'PERFILCONTRATANTE','SELLADO','S',to_date('25/10/10','DD/MM/RR'),null,to_date('08/11/10','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4600','DEL',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4599','DET',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4601','DOC',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4602','MOD',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4603','NEW',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');
Insert into GCZ_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,CODIGO_GRUPO_OPERACIONES,NOMBRE,DESCRIPCION,CODIGO_SERVICIO,CODIGO_SECCION,GCZ_PUBLICADO,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,TIPO) values ('4604','PUB',null,null,'PERFILCONTRATANTE','PORTAL','S',to_date('03/12/15','DD/MM/RR'),null,to_date('03/12/15','DD/MM/RR'),'admin',null,'admin','PRE');



Insert into GCZ_PERFIL (CODIGO_PERFIL,CODIGO_SERVICIO,NOMBRE,DESCRIPCION,GCZ_PUBLICADO,GCZ_USUARIOALTA,GCZ_USUARIOMOD,GCZ_USUARIOPUB,GCZ_FECHAALTA,GCZ_FECHAMOD,GCZ_FECHAPUB,ID_PERFIL) values ('admincontratos','PERFILCONTRATANTE','Administradores de Contratos',null,'S','admin',null,'admin',to_date('15/11/13','DD/MM/RR'),null,to_date('15/11/13','DD/MM/RR'),'1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2111','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2112','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2113','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2114','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2115','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2116','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4759','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('2123','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4600','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4599','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4601','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4602','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4603','1660');
Insert into GCZ_PERFIL_GRUPO_OPERACIONES (ID_GRUPO_OPERACIONES,ID_PERFIL) values ('4604','1660');


Insert into GCZ_PERFIL_USUARIO(ID_USUARIO, ID_PERFIL) values (1,1660);
