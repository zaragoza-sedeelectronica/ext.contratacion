--------------------------------------------------------
--  DDL for Index PERFIL_ADJUDICACION_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_ADJUDICACION_PK" ON "PERFIL_ADJUDICACION" ("ID_ADJU") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ID_PERFIL_ANUNCIO_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERFIL_ANUNCIO_ID" ON "PERFIL_ANUNCIO" ("ID_ANUNCIO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 131072 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index FKANUNCIO_DE_CONTRATO_IND
--------------------------------------------------------

  CREATE INDEX "FKANUNCIO_DE_CONTRATO_IND" ON "PERFIL_ANUNCIO" ("ID_CONTRATO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 196608 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index FKTIPO_DE_ANUNCIO_IND
--------------------------------------------------------

  CREATE INDEX "FKTIPO_DE_ANUNCIO_IND" ON "PERFIL_ANUNCIO" ("ID_TIPOANUNCIO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 196608 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ID_PERFIL_CONTRATO_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERFIL_CONTRATO_ID" ON "PERFIL_CONTRATO" ("ID_CONTRATO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index FKTIPO_DE_CONTRATO_IND
--------------------------------------------------------

  CREATE INDEX "FKTIPO_DE_CONTRATO_IND" ON "PERFIL_CONTRATO" ("ID_TIPOCONTRATO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index SYS_C00534239
--------------------------------------------------------

  CREATE UNIQUE INDEX "SYS_C00534239" ON "PERFIL_CPV" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_CRITERIOS_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_CRITERIOS_PK" ON "PERFIL_CRITERIOS" ("ID_CRITERIO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_EMPRESA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_EMPRESA_PK" ON "PERFIL_EMPRESA" ("ID_EMPRESA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_IDIOMA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_IDIOMA_PK" ON "PERFIL_IDIOMA" ("ID") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_LOTE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_LOTE_PK" ON "PERFIL_LOTE" ("ID_LOTE") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index LOTE_TIENE_OFERTA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "LOTE_TIENE_OFERTA_PK" ON "PERFIL_LOTE_TIENE_OFERTA" ("ID_LOTE", "ID_OFERTA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_OFERTA_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_OFERTA_PK" ON "PERFIL_OFERTA" ("ID_OFER") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ID_PERFIL_PORTAL_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERFIL_PORTAL_ID" ON "PERFIL_PORTAL" ("ID_PORTAL") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_PROCEDIMIENTO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_PROCEDIMIENTO_PK" ON "PERFIL_PROCEDIMIENTO" ("ID_PROC") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PK_ANUNCIO_SELLO
--------------------------------------------------------

  CREATE UNIQUE INDEX "PK_ANUNCIO_SELLO" ON "PERFIL_SELLO" ("ID_ANUNCIO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ID_PERFIL_TIPOANUNCIO_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERFIL_TIPOANUNCIO_ID" ON "PERFIL_TIPOANUNCIO" ("ID_TIPOANUNCIO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index ID_PERFIL_TIPOCONTRATO_ID
--------------------------------------------------------

  CREATE UNIQUE INDEX "ID_PERFIL_TIPOCONTRATO_ID" ON "PERFIL_TIPOCONTRATO" ("ID_TIPOCONTRATO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_TIPOCRITERIO_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_TIPOCRITERIO_PK" ON "PERFIL_TIPOCRITERIO" ("ID_TIPO") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Index PERFIL_UTE_PK
--------------------------------------------------------

  CREATE UNIQUE INDEX "PERFIL_UTE_PK" ON "PERFIL_UTE" ("ID_UTE", "ID_EMPRESA") 
  PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS" ;
--------------------------------------------------------
--  DDL for Trigger DISP_PERFIL_ANUNCIO
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DISP_PERFIL_ANUNCIO" 
before INSERT OR UPDATE
ON perfil_anuncio for each row
BEGIN
    if inserting then
          :new.GCZ_FECHAALTA := sysdate;
    elsif updating then
          :new.GCZ_FECHAMOD := sysdate;
    end if;
END;
/
ALTER TRIGGER "DISP_PERFIL_ANUNCIO" ENABLE;
--------------------------------------------------------
--  DDL for Trigger DISP_PERFIL_CONTRATO
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DISP_PERFIL_CONTRATO" 
before INSERT OR UPDATE
ON perfil_contrato for each row
BEGIN
    if inserting then
          :new.GCZ_FECHAALTA := sysdate;
    elsif updating then
          :new.GCZ_FECHAMOD := sysdate;
    end if;
END;
/
ALTER TRIGGER "DISP_PERFIL_CONTRATO" ENABLE;
--------------------------------------------------------
--  DDL for Trigger DISP_PERFIL_PORTAL
--------------------------------------------------------

  CREATE OR REPLACE TRIGGER "DISP_PERFIL_PORTAL" 
before INSERT OR UPDATE
ON perfil_portal for each row

BEGIN
    if inserting then
          :new.GCZ_FECHAALTA := sysdate;
    elsif updating then
          :new.GCZ_FECHAMOD := sysdate;
    end if;
END;
/
ALTER TRIGGER "DISP_PERFIL_PORTAL" ENABLE;
--------------------------------------------------------
--  Constraints for Table PERFIL_ADJUDICACION
--------------------------------------------------------

  ALTER TABLE "PERFIL_ADJUDICACION" ADD CONSTRAINT "PERFIL_ADJUDICACION_PK" PRIMARY KEY ("ID_ADJU")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_ADJUDICACION" MODIFY ("ID_ADJU" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_ANUNCIO
--------------------------------------------------------

  ALTER TABLE "PERFIL_ANUNCIO" ADD CONSTRAINT "ID_PERFIL_ANUNCIO_ID" PRIMARY KEY ("ID_ANUNCIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 131072 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_ANUNCIO" MODIFY ("ID_ANUNCIO" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_ANUNCIO" MODIFY ("TITULO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_CONTRATO
--------------------------------------------------------

  ALTER TABLE "PERFIL_CONTRATO" ADD CONSTRAINT "ID_PERFIL_CONTRATO_ID" PRIMARY KEY ("ID_CONTRATO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_CONTRATO" MODIFY ("CANON" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_CONTRATO" MODIFY ("ID_CONTRATO" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_CONTRATO" MODIFY ("NOMBRE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_CPV
--------------------------------------------------------

  ALTER TABLE "PERFIL_CPV" MODIFY ("ID" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_CPV" MODIFY ("TITLE" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_CPV" ADD PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PERFIL_CRITERIOS
--------------------------------------------------------

  ALTER TABLE "PERFIL_CRITERIOS" ADD CONSTRAINT "PERFIL_CRITERIOS_PK" PRIMARY KEY ("ID_CRITERIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_CRITERIOS" MODIFY ("ID_CRITERIO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_EMPRESA
--------------------------------------------------------

  ALTER TABLE "PERFIL_EMPRESA" ADD CONSTRAINT "PERFIL_EMPRESA_PK" PRIMARY KEY ("ID_EMPRESA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_EMPRESA" MODIFY ("ID_EMPRESA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_IDIOMA
--------------------------------------------------------

  ALTER TABLE "PERFIL_IDIOMA" ADD CONSTRAINT "PERFIL_IDIOMA_PK" PRIMARY KEY ("ID")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_IDIOMA" MODIFY ("ID" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_LOTE
--------------------------------------------------------

  ALTER TABLE "PERFIL_LOTE" ADD CONSTRAINT "PERFIL_LOTE_PK" PRIMARY KEY ("ID_LOTE")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_LOTE" MODIFY ("ID_LOTE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_LOTE_TIENE_OFERTA
--------------------------------------------------------

  ALTER TABLE "PERFIL_LOTE_TIENE_OFERTA" ADD CONSTRAINT "LOTE_TIENE_OFERTA_PK" PRIMARY KEY ("ID_LOTE", "ID_OFERTA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_LOTE_TIENE_OFERTA" MODIFY ("ID_LOTE" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_LOTE_TIENE_OFERTA" MODIFY ("ID_OFERTA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_OFERTA
--------------------------------------------------------

  ALTER TABLE "PERFIL_OFERTA" ADD CONSTRAINT "PERFIL_OFERTA_PK" PRIMARY KEY ("ID_OFER")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_OFERTA" MODIFY ("ID_OFER" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_PORTAL
--------------------------------------------------------

  ALTER TABLE "PERFIL_PORTAL" ADD CONSTRAINT "ID_PERFIL_PORTAL_ID" PRIMARY KEY ("ID_PORTAL")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_PORTAL" MODIFY ("ID_PORTAL" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_PORTAL" MODIFY ("NOMBRE" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_PORTAL" MODIFY ("GCZ_FECHAALTA" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_PROCEDIMIENTO
--------------------------------------------------------

  ALTER TABLE "PERFIL_PROCEDIMIENTO" ADD CONSTRAINT "PERFIL_PROCEDIMIENTO_PK" PRIMARY KEY ("ID_PROC")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_PROCEDIMIENTO" MODIFY ("ID_PROC" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_SELLO
--------------------------------------------------------

  ALTER TABLE "PERFIL_SELLO" ADD CONSTRAINT "PK_ANUNCIO_SELLO" PRIMARY KEY ("ID_ANUNCIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
--------------------------------------------------------
--  Constraints for Table PERFIL_TIPOANUNCIO
--------------------------------------------------------

  ALTER TABLE "PERFIL_TIPOANUNCIO" ADD CONSTRAINT "ID_PERFIL_TIPOANUNCIO_ID" PRIMARY KEY ("ID_TIPOANUNCIO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_TIPOANUNCIO" MODIFY ("ID_TIPOANUNCIO" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_TIPOANUNCIO" MODIFY ("NOMBRE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_TIPOCONTRATO
--------------------------------------------------------

  ALTER TABLE "PERFIL_TIPOCONTRATO" ADD CONSTRAINT "ID_PERFIL_TIPOCONTRATO_ID" PRIMARY KEY ("ID_TIPOCONTRATO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_TIPOCONTRATO" MODIFY ("ID_TIPOCONTRATO" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_TIPOCONTRATO" MODIFY ("NOMBRE" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_TIPOCRITERIO
--------------------------------------------------------

  ALTER TABLE "PERFIL_TIPOCRITERIO" ADD CONSTRAINT "PERFIL_TIPOCRITERIO_PK" PRIMARY KEY ("ID_TIPO")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_TIPOCRITERIO" MODIFY ("ID_TIPO" NOT NULL ENABLE);
--------------------------------------------------------
--  Constraints for Table PERFIL_UTE
--------------------------------------------------------

  ALTER TABLE "PERFIL_UTE" ADD CONSTRAINT "PERFIL_UTE_PK" PRIMARY KEY ("ID_UTE", "ID_EMPRESA")
  USING INDEX PCTFREE 10 INITRANS 2 MAXTRANS 255 COMPUTE STATISTICS 
  STORAGE(INITIAL 65536 NEXT 1048576 MINEXTENTS 1 MAXEXTENTS 2147483645
  PCTINCREASE 0 FREELISTS 1 FREELIST GROUPS 1 BUFFER_POOL DEFAULT FLASH_CACHE DEFAULT CELL_FLASH_CACHE DEFAULT)
  TABLESPACE "USERS"  ENABLE;
 
  ALTER TABLE "PERFIL_UTE" MODIFY ("ID_UTE" NOT NULL ENABLE);
 
  ALTER TABLE "PERFIL_UTE" MODIFY ("ID_EMPRESA" NOT NULL ENABLE);
--------------------------------------------------------
--  Ref Constraints for Table PERFIL_ANUNCIO
--------------------------------------------------------

  ALTER TABLE "PERFIL_ANUNCIO" ADD CONSTRAINT "FKTIPO_DE_ANUNCIO_FK" FOREIGN KEY ("ID_TIPOANUNCIO")
	  REFERENCES "PERFIL_TIPOANUNCIO" ("ID_TIPOANUNCIO") ON DELETE CASCADE ENABLE;
 
  ALTER TABLE "PERFIL_ANUNCIO" ADD CONSTRAINT "FK_PERFIL_ANUNCIO" FOREIGN KEY ("ID_CONTRATO")
	  REFERENCES "PERFIL_CONTRATO" ("ID_CONTRATO") ON DELETE CASCADE ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PERFIL_CONTRATO
--------------------------------------------------------

  ALTER TABLE "PERFIL_CONTRATO" ADD CONSTRAINT "FKPORTAL_DE_CONTRATO_FK" FOREIGN KEY ("ID_PORTAL")
	  REFERENCES "PERFIL_PORTAL" ("ID_PORTAL") ENABLE;
 
  ALTER TABLE "PERFIL_CONTRATO" ADD CONSTRAINT "FKTIPO_DE_CONTRATO_FK" FOREIGN KEY ("ID_TIPOCONTRATO")
	  REFERENCES "PERFIL_TIPOCONTRATO" ("ID_TIPOCONTRATO") ENABLE;
 
  ALTER TABLE "PERFIL_CONTRATO" ADD CONSTRAINT "FKTIPO_PROCEDIMENTO_FK1" FOREIGN KEY ("ID_PROCEDIMIENTO")
	  REFERENCES "PERFIL_PROCEDIMIENTO" ("ID_PROC") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PERFIL_LOTE
--------------------------------------------------------

  ALTER TABLE "PERFIL_LOTE" ADD CONSTRAINT "PERFIL_LOTE_PERFIL_CONTRA_FK1" FOREIGN KEY ("ID_CONTRATO")
	  REFERENCES "PERFIL_CONTRATO" ("ID_CONTRATO") ENABLE;
--------------------------------------------------------
--  Ref Constraints for Table PERFIL_SELLO
--------------------------------------------------------

  ALTER TABLE "PERFIL_SELLO" ADD CONSTRAINT "SYS_C0074854" FOREIGN KEY ("ID_ANUNCIO")
	  REFERENCES "PERFIL_ANUNCIO" ("ID_ANUNCIO") ON DELETE CASCADE ENABLE;
