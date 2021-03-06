--------------------------------------------------------
-- Archivo creado  - viernes-febrero-07-2020   
--------------------------------------------------------
--------------------------------------------------------
--  DDL for View VISTA_CONTRATOS_ENTIDAD_ANYO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_CONTRATOS_ENTIDAD_ANYO" ("ID_PORTAL", "ANYO", "TOTAL", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.ID_PORTAL,TO_CHAR(CONT.GCZ_FECHAALTA, 'yyyy') as ANYO,COUNT(*) as TOTAL,SUM(CONT.IMPORTE_SINIVA) as TOTALSINIVA,SUM(CONT.IMPORTE_CONIVA) as TOTALCONIVA 
				 from PERFIL_OFERTA ADJ    
				 right join PERFIL_CONTRATO CONT 
				  on CONT.ID_CONTRATO = ADJ.ID_CONTRATO 				
				 group by TO_CHAR(CONT.GCZ_FECHAALTA, 'yyyy'),CONT.ID_PORTAL 
order by ANYO
;
--------------------------------------------------------
--  DDL for View VISTA_CONTRATOS_ORGANOC
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_CONTRATOS_ORGANOC" ("ORGANO_CONTRATACION", "ANYO", "TOTAL", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.ORGANO_CONTRATACION,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,COUNT(CONT.ID_CONTRATO) as TOTAL,SUM(CONT.IMPORTE_SINIVA) as TOTALSINIVA,SUM(CONT.IMPORTE_CONIVA) as TOTALCONIVA 
from PERFIL_CONTRATO CONT       
group by TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy'),CONT.ORGANO_CONTRATACION
order by ANYO
;
--------------------------------------------------------
--  DDL for View VISTA_CONTRATOS_SERVICIO_ANYO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_CONTRATOS_SERVICIO_ANYO" ("SERVICIO_GESTOR", "ANYO", "TOTAL", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.SERVICIO_GESTOR,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,COUNT(CONT.ID_CONTRATO) as TOTAL,SUM(CONT.IMPORTE_SINIVA) as TOTALSINIVA,SUM(CONT.IMPORTE_CONIVA) as TOTALCONIVA 
				 from PERFIL_CONTRATO CONT       
        
          group by TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy'),CONT.SERVICIO_GESTOR 
          order by ANYO
;
--------------------------------------------------------
--  DDL for View VISTA_CPV
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_CPV" ("ID", "TITLE", "VISIBLE") AS 
  (
select id,TITLE,'S'  from PERFIL_CPV where id in (select CPV from PERFIL_CONTRATO_TIENE_CPV)
) union (
select id,title,'N'  from perfil_cpv where id not in (select cpv from Perfil_contrato_tiene_cpv)
)
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADOR_AHORRO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADOR_AHORRO" ("ID_PORTAL", "ANYO", "ID_CONTRATO", "IMPORTEOFERTA", "IMPORTECONTRATO", "AHORRO", "NOMBRE", "ID_PROCEDIMIENTO", "ID_TIPOCONTRATO") AS 
  select  CONT1.ID_PORTAL,TO_CHAR(CONT1.GCZ_FECHACONTRATO,'yyyy') as ANYO,CONT1.ID_CONTRATO ,SUM(O.IMPORTE_SINIVA) as IMPORTEOFERTA,CONT1.IMPORTE_SINIVA as IMPORTECONTRATO,
            (CONT1.IMPORTE_SINIVA-SUM(O.IMPORTE_SINIVA))  *100 /  CONT1.IMPORTE_SINIVA as AHORRO,CONT1.NOMBRE,CONT1.ID_PROCEDIMIENTO,CONT1.ID_TIPOCONTRATO
    from PERFIL_CONTRATO CONT1 right join PERFIL_OFERTA O on O.ID_CONTRATO=CONT1.ID_CONTRATO
    where CONT1.GCZ_PUBLICADO ='S' and  O.GANADOR='S' and  (O.CANON='NO' or O.CANON='N' or O.CANON='No') and CONT1.IMPORTE_SINIVA is not null and O.AHORRO_VISIBLE='S'
    group by O.ID_CONTRATO,CONT1.ID_PORTAL,CONT1.ID_CONTRATO,CONT1.IMPORTE_SINIVA,TO_CHAR(CONT1.GCZ_FECHACONTRATO,'yyyy'),CONT1.NOMBRE,CONT1.ID_PROCEDIMIENTO,CONT1.ID_TIPOCONTRATO
    order by ANYO desc ,AHORRO desc
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADORES_SERVICIO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADORES_SERVICIO" ("ID_PORTAL", "SERVICIO_GESTOR", "TOTALCONTRATOS", "TOTALCONIVA", "TOTALSINIVA", "ANYO") AS 
  select CONT.ID_PORTAL,CONT.SERVICIO_GESTOR, COUNT(CONT.ID_CONTRATO) as TOTALCONTRATOS,Sum(O.IMPORTE_CONIVA) as TOTALCONIVA,Sum(O.IMPORTE_SINIVA) as TOTALSINIVA,TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy') as ANYO
from PERFIL_CONTRATO CONT
left join PERFIL_OFERTA O
  on O.ID_CONTRATO=CONT.ID_CONTRATO
  where O.GANADOR='S' and CONT.ID_PORTAL=1 
group by CONT.ID_PORTAL,CONT.SERVICIO_GESTOR,TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy')
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADORES_TIPO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADORES_TIPO" ("ID_PORTAL", "CONT_MENOR", "ANYO", "TOTAL", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.ID_PORTAL,CONT.CONT_MENOR,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,COUNT(CONT.ID_CONTRATO) as TOTAL,SUM(OFER.IMPORTE_SINIVA)as TOTALSINIVA,SUM(OFER.IMPORTE_CONIVA)as TOTALCONIVA
from PERFIL_CONTRATO CONT
inner join PERFIL_OFERTA OFER
  on CONT.ID_CONTRATO=OFER.ID_CONTRATO
  where OFER.GANADOR ='S'
  group by CONT.ID_PORTAL,CONT.CONT_MENOR,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy')
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADOR_LICITADOR
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADOR_LICITADOR" ("ID_EMPRESA", "NOMBRE", "TOTALSINIVA", "TOTALCONIVA", "ANYO", "TOTALLICITACIONES", "ID_PORTAL") AS 
  select O.ID_EMPRESA,E.NOMBRE, SUM(O.IMPORTE_SINIVA) as TOTALSINIVA, SUM(O.IMPORTE_CONIVA) as TOTALCONIVA, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy') as ANYO,COUNT(O.ID_OFER) as TOTALLICITACIONES,CONT.ID_PORTAL
 from PERFIL_CONTRATO CONT
 inner join PERFIL_OFERTA O
on CONT.ID_CONTRATO = O.ID_CONTRATO
  inner join PERFIL_EMPRESA E
  on E.ID_EMPRESA=O.ID_EMPRESA
group by O.ID_EMPRESA,E.NOMBRE, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy'),CONT.ID_PORTAL
ORDER BY ANYO asc,TOTALLICITACIONES desc
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADOR_PROC_SERVGES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADOR_PROC_SERVGES" ("SERVICIO_GESTOR", "TOTALCONTRATOS", "ID_PROCEDIMIENTO", "ANYO", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.SERVICIO_GESTOR,COUNT(CONT.ID_CONTRATO) as TOTALCONTRATOS,CONT.ID_PROCEDIMIENTO,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,SUM(OFER.IMPORTE_SINIVA) AS TOTALSINIVA,SUM(OFER.IMPORTE_CONIVA) AS TOTALCONIVA
from PERFIL_CONTRATO CONT
inner join PERFIL_OFERTA OFER
  on OFER.ID_CONTRATO=CONT.ID_CONTRATO
inner join PERFIL_PROCEDIMIENTO PROC
  on PROC.ID_PROC=CONT.ID_PROCEDIMIENTO
where CONT.ID_PORTAL=1 and OFER.GANADOR='S'
  group by CONT.ID_PROCEDIMIENTO, TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy'),CONT.SERVICIO_GESTOR
  order by ANYO desc,CONT.SERVICIO_GESTOR
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADOR_TIPO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADOR_TIPO" ("ID_PORTAL", "TOTALCONTRATOS", "ID_TIPOCONTRATO", "ANYO", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.ID_PORTAL,COUNT(CONT.ID_CONTRATO) as TOTALCONTRATOS,CONT.ID_TIPOCONTRATO,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,SUM(OFER.IMPORTE_SINIVA) AS TOTALSINIVA,SUM(OFER.IMPORTE_CONIVA) AS TOTALCONIVA
from PERFIL_CONTRATO CONT
inner join PERFIL_OFERTA OFER
  on OFER.ID_CONTRATO=CONT.ID_CONTRATO
inner join PERFIL_TIPOCONTRATO TIPO
  on TIPO.ID_TIPOCONTRATO=CONT.ID_TIPOCONTRATO
where OFER.GANADOR='S'
  group by CONT.ID_TIPOCONTRATO, TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy'),CONT.ID_PORTAL
  order by ANYO desc,CONT.ID_PORTAL,TOTALCONTRATOS desc
;
--------------------------------------------------------
--  DDL for View VISTA_INDICADOR_TIPO_SERGES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_INDICADOR_TIPO_SERGES" ("SERVICIO_GESTOR", "TOTALCONTRATOS", "ID_TIPOCONTRATO", "ANYO", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.SERVICIO_GESTOR,COUNT(CONT.ID_CONTRATO) as TOTALCONTRATOS,CONT.ID_TIPOCONTRATO,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,SUM(OFER.IMPORTE_SINIVA) AS TOTALSINIVA,SUM(OFER.IMPORTE_CONIVA) AS TOTALCONIVA
from PERFIL_CONTRATO CONT
inner join PERFIL_OFERTA OFER
  on OFER.ID_CONTRATO=CONT.ID_CONTRATO
inner join PERFIL_TIPOCONTRATO TIPO
  on TIPO.ID_TIPOCONTRATO=CONT.ID_TIPOCONTRATO
where CONT.ID_PORTAL=1 and OFER.GANADOR='S'
  group by CONT.ID_TIPOCONTRATO, TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy'),CONT.SERVICIO_GESTOR
  order by ANYO desc,CONT.SERVICIO_GESTOR
;
--------------------------------------------------------
--  DDL for View VISTA_LICITADORES_SERVICIO
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTA_LICITADORES_SERVICIO" ("ID_EMPRESA", "NOMBRE", "ORGANO", "ANYO", "TOTALSINIVA", "TOTALCONIVA", "TOTALLICITACIONES") AS 
  select O.ID_EMPRESA,E.NOMBRE,CONT.SERVICIO_GESTOR as ORGANO, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy') as ANYO, SUM(O.IMPORTE_SINIVA) as TOTALSINIVA, SUM(O.IMPORTE_CONIVA) as TOTALCONIVA,COUNT(O.ID_OFER) as TOTALLICITACIONES
 from PERFIL_CONTRATO CONT
 inner join PERFIL_OFERTA O
on CONT.ID_CONTRATO = O.ID_CONTRATO
  inner join PERFIL_EMPRESA E
  on E.ID_EMPRESA=O.ID_EMPRESA
  where CONT.ID_PORTAL=1 and O.GANADOR='S'
group by O.ID_EMPRESA,E.NOMBRE, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy'),CONT.SERVICIO_GESTOR 
ORDER BY ANYO asc,TOTALLICITACIONES desc
;
--------------------------------------------------------
--  DDL for View VISTAS_PERFIL_LICITADORES
--------------------------------------------------------

  CREATE OR REPLACE FORCE VIEW "VISTAS_PERFIL_LICITADORES" ("ID_EMPRESA", "NOMBRE", "TOTALSINIVA", "TOTALCONIVA", "ANYO", "TOTALLICITACIONES") AS 
  select O.ID_EMPRESA,E.NOMBRE, SUM(O.IMPORTE_SINIVA) as TOTALSINIVA, SUM(O.IMPORTE_CONIVA) as TOTALCONIVA, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy') as ANYO,COUNT(O.ID_OFER) as TOTALLICITACIONES
 from PERFIL_CONTRATO CONT
 inner join PERFIL_OFERTA O
on CONT.ID_CONTRATO = O.ID_CONTRATO
  inner join PERFIL_EMPRESA E
  on E.ID_EMPRESA=O.ID_EMPRESA
group by O.ID_EMPRESA,E.NOMBRE, TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy')
ORDER BY ANYO asc,TOTALLICITACIONES desc
;

CREATE OR REPLACE FORCE VIEW "VISTA_DATOS_PROCEDIMIENTO" ("ID_PORTAL", "TOTALCONTRATOS", "ID_PROCEDIMIENTO", "ANYO", "TOTALSINIVA", "TOTALCONIVA") AS 
  select CONT.ID_PORTAL,COUNT(CONT.ID_CONTRATO) as TOTALCONTRATOS,CONT.ID_PROCEDIMIENTO,TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy') as ANYO,SUM(OFER.IMPORTE_SINIVA) AS TOTALSINIVA,SUM(OFER.IMPORTE_CONIVA) AS TOTALCONIVA
from PERFIL_CONTRATO CONT
inner join PERFIL_OFERTA OFER
  on OFER.ID_CONTRATO=CONT.ID_CONTRATO
inner join PERFIL_PROCEDIMIENTO PROC
  on PROC.ID_PROC=CONT.ID_PROCEDIMIENTO
where OFER.GANADOR='S'
  group by CONT.ID_PROCEDIMIENTO, TO_CHAR(CONT.GCZ_FECHACONTRATO, 'yyyy'),CONT.ID_PORTAL
  order by ANYO desc,CONT.ID_PORTAL,TOTALCONTRATOS desc;