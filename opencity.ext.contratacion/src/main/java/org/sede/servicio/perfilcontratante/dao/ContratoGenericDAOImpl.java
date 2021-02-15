/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
package org.sede.servicio.perfilcontratante.dao;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;

import org.apache.commons.codec.CharEncoding;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.*;

import org.sede.core.anotaciones.Esquema;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.Mensaje;

import org.sede.core.rest.json.JSONObject;
import org.sede.core.tag.SedeDialect;
import org.sede.core.utils.ConvertDate;
import org.sede.core.utils.Funciones;
import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.Utils;
import org.sede.servicio.perfilcontratante.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IModelFactory;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.Table;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import java.io.*;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class ContratoGenericDAOImpl extends GenericDAOImpl<Contrato, BigDecimal> implements ContratoGenericDAO {
    private static final Logger logger = LoggerFactory.getLogger(ContratoGenericDAOImpl.class);
    private static final String URLSPARQLTBFY = "http://data.tbfy.eu/sparql?query=";
    private static final String URLCONTRACTINPROCESS = "http://tbfy.librairy.linkeddata.es/kg-api/contractingProcess?code=%s";
    private static final String URLTENDERDOCUMENT = "http://tbfy.librairy.linkeddata.es/kg-api/tender/%s/document";
    private static final String URLCONTRACTINPROCESSTENDER = "http://tbfy.librairy.linkeddata.es/kg-api/contractingProcess/%s/tender";
    private static final String URLTBFYDOCUMENT = "http://tbfy.librairy.linkeddata.es/search-api/documents/";
    private static final String URLTBFYDOCUMENTITEMS = "http://tbfy.librairy.linkeddata.es/search-api/items";
    private static final String SPARLQQUERY = "PREFIX ocds: <http://data.tbfy.eu/ontology/ocds#>\n" +
            "PREFIX dct: <http://purl.org/dc/terms/>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX tbfy: <http://data.tbfy.eu/ontology/tbfy#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rov: <http://www.w3.org/ns/regorg#>\n" +
            "PREFIX dbp: <http://dbpedia.org/ontology/>\n" +
            "PREFIX schema: <http://schema.org/>\n" +
            "SELECT ?qid ?qtitle ?qdescription ?qstatus  ?buyer  ?supplier ?qdocumenturl\n" +
            "\n" +
            "WHERE {\n" +
            "\t?qid a ocds:Tender .\n" +
            "\tOPTIONAL { ?qid dct:title ?qtitle } .\n" +
            "\tOPTIONAL { ?qid dct:description ?qdescription } .\n" +
            "\tOPTIONAL { ?qid ocds:tenderStatus ?qstatus } .\n" +
            "  \n" +
            "\tOPTIONAL { ?contproc a ocds:ContractingProcess .\n" +
            "    ?contproc ocds:hasTender ?qid .\n" +
            "    ?contproc ocds:hasRelease ?release .}\n" +
            "    ?s a ocds:Organisation .\n" +
            "  ?s ocds:isBuyerFor ?contproc .\n" +
            "  ?s ocds:legalName ?buyer . \n" +
            "  ?contproc ocds:hasAward ?hasaw.\n" +
            "  ?t a ocds:Organisation .\n" +
            "  ?t ocds:isSupplierFor ?hasaw .\n" +
            "  OPTIONAL { ?t owl:sameAs ?sas .\n" +
            "             ?sas rov:legalName ?supplier ;\n" +
            "                  dbp:jurisdiction ?qjurisdiction } .\n" +
            "  ?qid ocds:hasDocument ?hdoc .\n" +
            "\tOPTIONAL { ?hdoc ocds:documentType ?qdocumenttype ;\n" +
            "\t\t\t\t\t\t       dct:language ?qdocumentlanguage ;\n" +
            "\t\t\t\t\t\t       schema:URL ?qdocumenturl } .\n" +
            "}\n" +
            "LIMIT 10";
    private static final String SPARQLTBFYLICITADOR = "PREFIX rov: <http://www.w3.org/ns/regorg#>\n" +
            "PREFIX dbp: <http://dbpedia.org/ontology/>\n" +
            "PREFIX ocds: <http://data.tbfy.eu/ontology/ocds#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX ebg: <http://data.businessgraph.io/ontology#>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX dct: <http://purl.org/dc/terms/>\n" +
            "\n" +
            "SELECT ?ss ?supaward ?qtenderid ?qtitle ?qdescription ?qlegalname ?qjurisdiction ?qnumemployees ?qawarddate ?qawardtitle\n" +
            "?qawarddescription ?qawardstatus ?qawardstartdate ?qawardenddate ?qawardamount ?qawardcurrency\n" +
            "WHERE {\n" +
            "  ?s ocds:isSupplierFor ?supaward .\n" +
            "  ?s owl:sameAs ?ss .\n" +
            "  ?ss rov:legalName ?qlegalname .\n" +
            "  ?ss dbp:jurisdiction ?qjurisdiction .\n" +
            "  ?ss ebg:numberOfEmployees ?qnumemployees .\n" +
            "\n" +
            "  OPTIONAL { ?supaward dct:title ?qawardtitle } .\n" +
            "  OPTIONAL { ?supaward dct:description ?qawarddescription } .\n" +
            "  OPTIONAL { ?supaward ocds:awardDate ?qawarddate } .\n" +
            "  OPTIONAL { ?supaward ocds:awardStatus ?qawardstatus } .\n" +
            "\n" +
            "  #Object Property hasAwardedContractPeriod\n" +
            "  OPTIONAL { ?supaward ocds:hasAwardedContractPeriod ?cperiod .\n" +
            "  OPTIONAL { ?cperiod ocds:periodStartDate ?qawardstartdate .\n" +
            "             ?cperiod ocds:periodEndDate ?qawardenddate }} .\n" +
            "\n" +
            "  #Object Property hasAwardValue\n" +
            "  OPTIONAL { ?supaward ocds:hasAwardValue ?valr .\n" +
            "  OPTIONAL { ?valr ocds:valueAmount ?qawardamount .\n" +
            "             ?valr ocds:valueCurrency ?qawardcurrency }} .\n" +
            " optional{ ?supaward ocds:isIssuedForTender ?qtenderid .\n" +
            "  OPTIONAL { ?qtenderid dct:title ?qtitle } .\n" +
            "\tOPTIONAL { ?qtenderid dct:description ?qdescription } .\n" +
            "    OPTIONAL { ?qtenderid ocds:tenderStatus ?qstatus } }.\n%s";
    private static final String SPARQLLICITACIONTBFY = "PREFIX ocds: <http://data.tbfy.eu/ontology/ocds#>\n" +
            "PREFIX dct: <http://purl.org/dc/terms/>\n" +
            "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>\n" +
            "PREFIX tbfy: <http://data.tbfy.eu/ontology/tbfy#>\n" +
            "PREFIX owl: <http://www.w3.org/2002/07/owl#>\n" +
            "PREFIX rov: <http://www.w3.org/ns/regorg#>\n" +
            "PREFIX dbp: <http://dbpedia.org/ontology/>\n" +
            "PREFIX schema: <http://schema.org/>\n" +
            "SELECT distinct ?qid ?qtitle ?qdescription ?qstatus ?buyer ?supplier ?qjurisdiction ?qamount ?qcurrency ?qstartdate ?qenddate ?qdocumenturl\n" +
            "WHERE {\n" +
            "#Tender information\t\n" +
            "\t?qid a ocds:Tender .\n" +
            "  OPTIONAL { ?qid dct:title ?qtitle } .\n" +
            "\tOPTIONAL { ?qid dct:description ?qdescription } .\n" +
            "\tOPTIONAL { ?qid ocds:tenderStatus ?qstatus } .\n" +
            "#ContractingProcess information\t  \n" +
            "\tOPTIONAL { ?contproc a ocds:ContractingProcess .\n" +
            "    ?contproc ocds:hasTender ?qid .\n" +
            "    ?contproc ocds:hasRelease ?release }.\n" +
            "    OPTIONAL {?s a ocds:Organisation .\n" +
            "    ?s ocds:isBuyerFor ?contproc .\n" +
            "    ?s ocds:legalName ?buyer  }.\n" +
            "#Object Property hasAward \n" +
            "\tOPTIONAL {?contproc ocds:hasAward ?hasaw.\n" +
            "   ?t a ocds:Organisation .\n" +
            "   ?t ocds:isSupplierFor ?hasaw .\n" +
            "\tOPTIONAL { ?t owl:sameAs ?sas .\n" +
            "            ?sas rov:legalName ?supplier ;\n" +
            "                 dbp:jurisdiction ?qjurisdiction  }.\n" +
            "#Object Property hasAwardedContractPeriod\n" +
            "\tOPTIONAL { ?hasaw ocds:hasAwardedContractPeriod ?cperiod .\n" +
            "\tOPTIONAL { ?cperiod ocds:periodStartDate ?qstartdate .\n" +
            "             ?cperiod ocds:periodEndDate ?qenddate }}\n" +
            "#Object Property hasAwardValue\n" +
            "  OPTIONAL { ?hasaw ocds:hasAwardValue ?valr .\n" +
            "  OPTIONAL { ?valr ocds:valueAmount ?qamount .\n" +
            "             ?valr ocds:valueCurrency ?qcurrency }} .\n" +
            "  } \n" +
            "#Object Property hasDocument\n" +
            "\tOPTIONAL {?qid ocds:hasDocument ?hdoc .\n" +
            "\tOPTIONAL { ?hdoc ocds:documentType ?qdocumenttype ;\n" +
            "\t\t\t\tdct:language ?qdocumentlanguage ;\n" +
            "             \tschema:URL ?qdocumenturl }} .\n %s";
    private static final Map<BigDecimal, String> expedienteEntidad = new HashMap<BigDecimal, String>() {
        {
            put(BigDecimal.valueOf(61632), "DEP");
            put(BigDecimal.valueOf(61634), "CUL");
            put(BigDecimal.valueOf(61632), "ECO");
            put(BigDecimal.valueOf(61632), "VIV");
            put(BigDecimal.valueOf(61632), "EDUC");
            put(BigDecimal.valueOf(61632), "TUR");
            put(BigDecimal.valueOf(61632), "DIN");


        }
    };
    @Autowired
    public TipoContratoGenericDAO daoTipoContrato;

    @PersistenceContext(unitName = Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ContratoHome getResultsForHome() {
        ContratoHome home = new ContratoHome();
        Query qOpen = em().createQuery("FROM Contrato WHERE status = 0 and fecha_presentacion > sysdate order by creationDate desc", Contrato.class);
        home.setOpen(qOpen.getResultList());

        Query qLastModified = em().createQuery("FROM Contrato WHERE status != 0 and fecha_presentacion <= sysdate order by creationDate desc", Contrato.class).setMaxResults(20);
        home.setLastModified(qLastModified.getResultList());
        return home;
    }

    @Override
    public Contrato getResultsForExpediente(String expediente) {
        Query qOpen = em().createQuery("FROM Contrato WHERE expediente=?", Contrato.class).setParameter(1,expediente);

       List<Contrato> con= qOpen.getResultList();


        return con.get(0);
    }

    public IModel getForTag(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
		try {
			
			String fragmento = tag.getAttributeValue("fragment") == null ? "fragmentos/perfil-contratante/list" : tag.getAttributeValue("fragment");
			Search busqueda = new Search();
			busqueda.addFilter(Filter.some("entity", Filter.equal("id", tag.getAttributeValue("entidad"))));
			//busqueda.addFilter(Filter.lessOrEqual("startDate", new Date()));
			//busqueda.addFilter(Filter.greaterOrEqual("expirationDate", new Date()));
			busqueda.setMaxResults(tag.getAttributeValue("numero") == null ? 10 : Integer.parseInt(tag.getAttributeValue("numero")));
			List<Sort> sorts = new ArrayList<Sort>();
			sorts.add(new Sort("fechaPresentacion", true));
			busqueda.setSorts(sorts);
			List<Contrato> resultado = this.search(busqueda);
			if (resultado.size() > 0) {
				structureHandler.setLocalVariable("titulo", tag.getAttributeValue("titulo"));
			}
			structureHandler.setLocalVariable("listado", resultado);
			final IModelFactory modelFactory = context.getModelFactory();
			
			final IModel  model = modelFactory.createModel();
			model.addModel(SedeDialect.computeFragment(context, fragmento).getTemplateModel());
			return model;
		} catch (Exception e) {
			logger.error(e.getMessage());
			return context.getModelFactory().createModel();
		}
	}

    @Override
    public HashMap<Integer, Integer> getContratosPorAnyo(String nif) {
        HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
        Query query = em().createQuery("select TO_CHAR(CONT.FECHA_PRESENTACION, 'yyyy'), COUNT(*) " +
                "from PERFILCONTRATANTE.PERFIL_CONTRATO CONT " +
                "    inner join PERFILCONTRATANTE.PERFIL_ADJUDICACION ADJ on CONT.ID_CONTRATO = ADJ.ID_CONTRATO " +
                "where ADJ.NIF_ADJU like " + nif +
                " group by TO_CHAR(CONT.FECHA_PRESENTACION, 'yyyy')");
//		query.setParameter("anyo",new Timestamp(start.getTime()));
//		query.setParameter("numContratos",new Timestamp(end.getTime()));

        @SuppressWarnings("unchecked")
        List<Integer[]> resultados = query.getResultList();


        for (Object[] result : resultados) {
            map.put(Integer.valueOf(result[0].toString()), Integer.valueOf(result[1].toString()));
        }

        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<BigDecimal> getServicioGestor(BigDecimal id) {
        Query q = em().createNativeQuery("select DISTINCT SERVICIO_GESTOR from PERFIL_CONTRATO where ID_PORTAL=? and SERVICIO_GESTOR is not NUll").setParameter(1, id);
        return q.getResultList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> getOrganismoContratante() {
        Query q = em().createNativeQuery("select DISTINCT ORGANO_CONTRATACION from PERFIL_CONTRATO where ORGANO_CONTRATACION is not NUll");
        return q.getResultList();
    }


    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }

    public long updateVisible(BigDecimal id, String value) {

        Query propWeb = this.em().createNativeQuery("update " + persistentClass.getAnnotation(Table.class).name()
                + " set gcz_publicado=?, gcz_usuariopub=?, gcz_fechapub=? where ID_CONTRATO=?");

        propWeb.setParameter(1, value);
        propWeb.setParameter(2, Funciones.getPeticion().getClientId());
        propWeb.setParameter(3, new Date());
        propWeb.setParameter(4, id);
        return propWeb.executeUpdate();

    }

    @Override
    public HSSFWorkbook generarExcelTribunalCuentas(SearchResult<Contrato> resultado,boolean busquedaAvanzada) {

        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Contratos");

        CellStyle sectionStyle = workbook.createCellStyle();
        Font fontSection = workbook.createFont();
        sectionStyle.setAlignment(HorizontalAlignment.CENTER);
        fontSection.setBold(true);
        sectionStyle.setFont(fontSection);
        sectionStyle.setBorderBottom(BorderStyle.MEDIUM);
        sectionStyle.setBorderLeft(BorderStyle.MEDIUM);
        sectionStyle.setBorderRight(BorderStyle.MEDIUM);

        CellStyle updatedCell = workbook.createCellStyle();
        Font fontUpdated = workbook.createFont();
        fontUpdated.setColor(IndexedColors.RED.getIndex());
        updatedCell.setFont(fontUpdated);


        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);


//		Creamos la lista para relacionar la columna con el identificador de celda

        HSSFRow headerRow = sheet.createRow(0);
        int col = 0;
        HSSFCell cellId = headerRow.createCell(col);
        cellId.setCellStyle(headerStyle);
        cellId.setCellValue("Entidad contratante");


        HSSFCell cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Expediente");
        if(busquedaAvanzada) {
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Organismo Contratante");
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Servicio Gestor");
        }

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Contrato por lotes");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Número de lotes");
        if(!busquedaAvanzada) {
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Derivado de acuerdo marco");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Derivado sistema dinámico");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Con subasta electrónica");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Contrato complementario");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Sujeto a regulación armonizada");
        }

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Tipo de contrato");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Objeto de contrato");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Prodedimiento de adjudicación");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Tramitación");
        if(!busquedaAvanzada) {
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Tiene cláusula de prórrga");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Periodo de prórroga");
        }

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Presupuesto licitación (sin IVA)");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Precio adjudicación (sin IVA)");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Adjudicación");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Fecha adjudicación");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Formalización");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Fecha formalización");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Plazo ejecución");
        if(!busquedaAvanzada) {
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Plazo concesión");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Publicidad");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);

            cellFecha.setCellValue("Publicidad de licitación");
            cellFecha = headerRow.createCell(++col);

            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOP");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOA");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio DOUE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Contratante");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Estado");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Otros lici");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Publicidad de adjudicación");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOP");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOA");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio DOUE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Contratante");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Estado");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Otros adjud");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Publicidad de formalización");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOP");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio BOA");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio DOUE");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Contratante");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Fecha anuncio Perfil Estado");

            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Otros formalizacion");
        }
        if(busquedaAvanzada){
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Nº licitadores");
        }

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Contratistas");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Lista de lotes");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Estado");



        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Ejercicio");
        int fila = 1;
        for (Contrato r : resultado.getResult()) {

            col = 0;
            HSSFRow dataRow = sheet.createRow(fila);
            HSSFCell cellid = dataRow.createCell(col);
            cellid.setCellValue(r.getEntity() == null ? "" : r.getEntity().getTitle());

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getExpediente()==null?"":r.getExpediente());
            if(busquedaAvanzada) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(r.getOrganoContratante()!=null?r.getOrganoContratante().getTitle():"");
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(r.getServicio()!=null?r.getServicio().getTitle():"");
            }

            cellid = dataRow.createCell(++col);
            int lotes = r.getLotes().size();
            cellid.setCellValue(lotes > 0 ? "Si" : "No");

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(lotes > 0 ? "" + lotes : "");
            if(!busquedaAvanzada) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getDerivadoAcuerdoMarco()));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getDerivadoSistemaDinamico()));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getSubastaElectronica()));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getComplementario()));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getRegulacionArmonizada()));
            }

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getType().getTitle());

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getTitle());

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getProcedimiento() == null ? "" : r.getProcedimiento().getNombre());

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(Utils.getUrgente(r.getUrgente()));
            if(!busquedaAvanzada) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(Utils.getSino(r.getClausulaProrroga()));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue("" + (r.getPeriodoProrroga() == null ? "" : r.getPeriodoProrroga()));
            }
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getImporteSinIVA() == null ? "" : r.getImporteSinIVA()));

            StringBuilder contratista = new StringBuilder();
            StringBuilder txtLotes = new StringBuilder();
            String importeAdjudicacionSinIVA = "";
            for (Oferta of : r.getOfertas()) {
                if (of.getGanador().equals(Boolean.TRUE)) {

                    if (of.getLote() == null) {
                        importeAdjudicacionSinIVA = "" + of.getImporteSinIVA();
                        contratista.append(of.getEmpresa().getNif() + " - " + of.getEmpresa().getNombre() + " ");
                    } else {
                        if (StringUtils.isNotEmpty(txtLotes)) {
                            txtLotes.append(" | ");
                        }
                        txtLotes.append(of.getLote().getDescription()
                                + " - " + of.getImporteSinIVA()
                                + " - " + (of.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(of.getFechaAdjudicacion(), ConvertDate.DATE_FORMAT))
                                + " - " + (of.getFechaFormalizacion() == null ? "" : ConvertDate.date2String(of.getFechaFormalizacion(), ConvertDate.DATE_FORMAT))
                                + " - " + of.getEmpresa().getNif() + " - " + of.getEmpresa().getNombre() + " ");
                    }


                }
            }


            cellid = dataRow.createCell(++col);
            cellid.setCellValue(importeAdjudicacionSinIVA);


            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getFechaAdjudicacion() == null ? "No" : "Si"));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(r.getFechaAdjudicacion(), ConvertDate.DATETIME_FORMAT));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getFechaFormalizacion() == null ? "No" : "Si"));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getFechaFormalizacion() == null ? "" : ConvertDate.date2String(r.getFechaFormalizacion(), ConvertDate.DATETIME_FORMAT));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getDuracion() == null ? "" : r.getDuracion()));
            if(!busquedaAvanzada) {
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getPlazoConsesion() == null ? "" : r.getPlazoConsesion()));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + ("S".equals(r.getVisible()) ? "Si" : "No"));

            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + (r.getProcedimiento() == null
                    || r.getProcedimiento().getId().intValue() == 3
                    || r.getProcedimiento().getId().intValue() == 7 ? "No" : "Si"));

                String fechaAnuncioBOE = "";
                String fechaAnuncioBOP = "";
                String fechaAnuncioBOA = "";
                String fechaAnuncioDOUE = "";
                String fechaAnuncioPerfil = "";
                String fechaAnuncioPerfilEstado = "";
                String fechaAdjudicacionBOE = "";
                String fechaAdjudicacionBOP = "";
                String fechaAdjudicacionBOA = "";
                String fechaAdjudicacionDOUE = "";
                String fechaAdjudicacionPerfil = "";
                String fechaAdjudicacionPerfilEstado = "";
                String fechaFormalizacionBOE = "";
                String fechaFormalizacionBOP = "";
                String fechaFormalizacionBOA = "";
                String fechaFormalizacionDOUE = "";
                String fechaFormalizacionPerfil = "";
                String fechaFormalizacionPerfilEstado = "";

                for (Anuncio a : r.getAnuncios()) {
                    switch (a.getType().getId().intValue()) {
                        case 15:// Publicación BOE:
                            fechaAnuncioBOE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 16: // Publicación BOPZ
                            fechaAnuncioBOP = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 17: // Publicación BOA
                            fechaAnuncioBOA = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 18: // Publicación DOUE
                            fechaAnuncioDOUE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 19: // Publicación plataforma del estado
                            fechaAnuncioPerfilEstado = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 20: // Formalización
                            fechaFormalizacionPerfil = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 21: // Adjudicación plataforma del estado
                            fechaAdjudicacionPerfilEstado = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 22: // Formalización BOE
                            fechaFormalizacionBOE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 23: // Formalización BOPZ
                            fechaFormalizacionBOP = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 24: // Formalización BOA
                            fechaFormalizacionBOA = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 25: // Formalización DOUE
                            fechaFormalizacionDOUE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 26: // Formalización plataforma del estado
                            fechaFormalizacionPerfilEstado = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 27: // Adjudicación BOE
                            fechaAdjudicacionBOE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 28: // Adjudicación BOPZ
                            fechaAdjudicacionBOP = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 29: // Adjudicacion BOA
                            fechaAdjudicacionBOA = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        case 30: // Formalización plataforma del estado
                            fechaAdjudicacionDOUE = ConvertDate.date2String(a.getPubDate(), ConvertDate.DATE_FORMAT);
                            break;
                        default:
                            break;
                    }
                }

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioBOE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioBOP);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioBOA);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioDOUE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioPerfil);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAnuncioPerfilEstado);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("N/D");
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("" + ("S".equals(r.getVisible()) ? "Si" : "No"));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionBOE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionBOP);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionBOA);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionDOUE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionPerfil);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaAdjudicacionPerfilEstado);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("N/D");
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("" + ("S".equals(r.getVisible()) ? "Si" : "No"));

                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionBOE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionBOP);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionBOA);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionDOUE);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionPerfil);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(fechaFormalizacionPerfilEstado);
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("N/D");
            }
            if(busquedaAvanzada) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(r.getNumLicitadores()!=null?r.getNumLicitadores():0);

            }

            cellid = dataRow.createCell(++col);
            cellid.setCellValue(contratista.toString());

            if (lotes > 0) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(txtLotes.toString());
            } else {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("");
            }
            cellid = dataRow.createCell(++col);
            cellid.setCellValue(r.getStatus().getTitle());

            HSSFCell cell = dataRow.createCell(++col);
            cell.setCellValue(ConvertDate.date2String(r.getCreationDate(), "yyyy"));
            fila = fila + 1;
        }
        return workbook;
    }

    @Override
    public HSSFWorkbook generarExcelLicitador(Licitador resultado) {
        boolean tieneUte=false;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        workbook.setSheetName(0, "Licitador_"+resultado.getNombre());

        CellStyle sectionStyle = workbook.createCellStyle();
        Font fontSection = workbook.createFont();
        sectionStyle.setAlignment(HorizontalAlignment.CENTER);
        fontSection.setBold(true);
        sectionStyle.setFont(fontSection);
        sectionStyle.setBorderBottom(BorderStyle.MEDIUM);
        sectionStyle.setBorderLeft(BorderStyle.MEDIUM);
        sectionStyle.setBorderRight(BorderStyle.MEDIUM);

        CellStyle updatedCell = workbook.createCellStyle();
        Font fontUpdated = workbook.createFont();
        fontUpdated.setColor(IndexedColors.RED.getIndex());
        updatedCell.setFont(fontUpdated);


        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);


//		Creamos la lista para relacionar la columna con el identificador de celda

        HSSFRow headerRow = sheet.createRow(0);
        int col = 0;
        HSSFCell cellId = headerRow.createCell(col);
        cellId.setCellStyle(headerStyle);
        cellId.setCellValue("Licitador ID");
        HSSFCell cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Nombre");
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("openCorporate");

        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("LibreBorme");
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("NIF/CIF");
        for (Oferta ofer : ((List<Oferta>) resultado.getLicitados().getResult())) {
            if (ofer.getTieneUte()) {
                tieneUte=true;
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Tiene Ute "+ofer.getId());
                for (Ute ute : ((Set<Ute>) ofer.getUte())) {
                        cellFecha = headerRow.createCell(++col);
                        cellFecha.setCellStyle(headerStyle);
                        cellFecha.setCellValue("empresa Ute");
                        cellFecha = headerRow.createCell(++col);
                        cellFecha.setCellStyle(headerStyle);
                        cellFecha.setCellValue("Participacion %");


               }
            }

        }
        if(!tieneUte){
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Tiene Ute ");
        }
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Nº Licitados");
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Nº Ganados");
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Total Sin Iva €");
        cellFecha = headerRow.createCell(++col);
        cellFecha.setCellStyle(headerStyle);
        cellFecha.setCellValue("Total Con Iva €");
        for (DatosLicitador datos : ((List<DatosLicitador>) resultado.getDatosLicitadorCuantia().getResult())) {
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Año");
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Nº Contratos Licitados");
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Importe Sin Iva €");
            cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Importe Con Iva €");
        }

        int fila = 1;
        col = 0;
        HSSFRow dataRow = sheet.createRow(fila);
        HSSFCell cellid = dataRow.createCell(col);
        cellid.setCellValue("" + resultado.getIdEmpresa());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue("" + resultado.getNombre());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue("" + resultado.getOpenCorporate() == null?"":resultado.getOpenCorporate());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue("" + resultado.getLibreBorme()== null?"":resultado.getLibreBorme());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue("" + resultado.getNif());
        for (Oferta ofer : ((List<Oferta>) resultado.getLicitados().getResult())) {

            if (ofer.getTieneUte()) {
                tieneUte = true;
                cellid = dataRow.createCell(++col);
                cellid.setCellValue("Sí");
                for (Ute ute : ((Set<Ute>) ofer.getUte())) {
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue("" + ute.getEmpresa().getNombre());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue("" + (ute.getParticipacion() != null ? ute.getParticipacion() : "Desconocido"));
                }

            }
        }
        if(!tieneUte){
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("No");
        }
        cellid = dataRow.createCell(++col);
        cellid.setCellValue(""+resultado.getLicitados().getResult().size());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue(""+(resultado.getGanados().getResult()!=null?resultado.getGanados().getResult().size():"0"));
        cellid = dataRow.createCell(++col);
        cellid.setCellValue(""+resultado.getTotalSinIva());
        cellid = dataRow.createCell(++col);
        cellid.setCellValue(""+resultado.getTotalConIva());
        for (DatosLicitador datos : ((List<DatosLicitador>) resultado.getDatosLicitadorCuantia().getResult())) {
            cellid = dataRow.createCell(++col);
            cellid.setCellValue(""+datos.getAnyo());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue(""+datos.getNumContratos());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue(""+datos.getTotalSinIva());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue(""+datos.getTotalConIva());
        }

        fila=fila+1;
        col=1;


        HSSFRow dataRow4 = sheet.createRow(++fila);
        HSSFCell cellid3 = dataRow4.createCell(col);
        cellid3.setCellStyle(sectionStyle);
        cellid3.setCellValue("Nombre del Contrato");
        cellid3 = dataRow4.createCell(++col);
        cellid3.setCellStyle(sectionStyle);
        cellid3.setCellValue("Importe de Adjudicación");
        cellid3 = dataRow4.createCell(++col);
        cellid3.setCellStyle(sectionStyle);
        cellid3.setCellValue("Fecha de Adjudicación");
        cellid3 = dataRow4.createCell(++col);
        cellid3.setCellStyle(sectionStyle);
        cellid3.setCellValue("Fecha de Publicacion");
        cellid3 = dataRow4.createCell(++col);
        cellid3.setCellStyle(sectionStyle);
        cellid3.setCellValue("Ganador");



        for (Oferta o : ((List<Oferta>) resultado.getLicitados().getResult())) {
            col = 1;
            HSSFRow dataRow2 = sheet.createRow(++fila);
            HSSFCell cellid2 = dataRow2.createCell(col);
            cellid2.setCellValue(o.getContrato().getTitle());

            StringBuilder txtLotes = new StringBuilder();
            String importeAdjudicacionSinIVA = "";

            if (o.getGanador().equals(Boolean.TRUE)) {
                if (o.getLote() == null) {
                    importeAdjudicacionSinIVA = "" + o.getImporteSinIVA();

                } else {
                    if (StringUtils.isNotEmpty(txtLotes)) {
                        txtLotes.append(" | ");
                    }
                    txtLotes.append(o.getLote().getDescription()
                            + " - " + o.getImporteSinIVA()
                            + " - " + (o.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(o.getFechaAdjudicacion(), ConvertDate.DATE_FORMAT))
                            + " - " + (o.getFechaFormalizacion() == null ? "" : ConvertDate.date2String(o.getFechaFormalizacion(), ConvertDate.DATE_FORMAT))
                            + " - " + o.getEmpresa().getNif() + " - " + o.getEmpresa().getNombre() + " ");
                }
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(importeAdjudicacionSinIVA);
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(o.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(o.getFechaAdjudicacion(), ConvertDate.DATETIME_FORMAT));
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(o.getFechaFormalizacion() == null ? "" : ConvertDate.date2String(o.getFechaFormalizacion(), ConvertDate.DATETIME_FORMAT));
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(o.getGanador()?"Si":"No");


            }









        }

        return workbook;
    }
    @Override
    public HSSFWorkbook generarExcelDatosAbiertos(Object datos) {



        boolean tieneUte=false;
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet();
        CellStyle sectionStyle = workbook.createCellStyle();
        Font fontSection = workbook.createFont();
        sectionStyle.setAlignment(HorizontalAlignment.CENTER);
        fontSection.setBold(true);
        sectionStyle.setFont(fontSection);
        sectionStyle.setBorderBottom(BorderStyle.MEDIUM);
        sectionStyle.setBorderLeft(BorderStyle.MEDIUM);
        sectionStyle.setBorderRight(BorderStyle.MEDIUM);

        CellStyle updatedCell = workbook.createCellStyle();
        Font fontUpdated = workbook.createFont();
        fontUpdated.setColor(IndexedColors.RED.getIndex());
        updatedCell.setFont(fontUpdated);


        CellStyle headerStyle = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);
        headerStyle.setBorderBottom(BorderStyle.MEDIUM);
        headerStyle.setBorderLeft(BorderStyle.MEDIUM);
        headerStyle.setBorderRight(BorderStyle.MEDIUM);
        HSSFRow headerRow = sheet.createRow(0);
        int col = 0;
        if(datos.getClass().equals(ServicioGestor.class)) {
            ServicioGestor resultado=(ServicioGestor)datos;
            workbook.setSheetName(0, "Servicio Gestor " + resultado.getTitle());
            HSSFCell cellId = headerRow.createCell(col);
            cellId.setCellStyle(headerStyle);
            cellId.setCellValue("Servicio Gestor ID");
            HSSFCell cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Nombre");
            for (ContratosPorAnyoServicioGestor dato : resultado.getDatos() ) {
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Año");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Nº Contratos Licitados");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Sin Iva €");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Con Iva €");
            }
            int fila = 1;
            col = 0;
            HSSFRow dataRow = sheet.createRow(fila);
            HSSFCell cellid = dataRow.createCell(col);
            cellid.setCellValue("" + resultado.getId());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + resultado.getTitle());

                for (ContratosPorAnyoServicioGestor dato : resultado.getDatos()) {
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getId().getAnyo());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotal());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotalSinIva());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotalConIva());
                }

            fila=fila+1;

            col=1;
            int lotes=0;
            int lotesFinal=0;
            HSSFRow dataRow4 = sheet.createRow(++fila);
            HSSFCell cellid3 = dataRow4.createCell(col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nombre del Contrato");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Entidad Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Organismo Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Estado");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Importe de licitación");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nº Licitadores");
            for(Contrato cont:resultado.getContrato().getResult()) {
                for (Oferta o : cont.getOfertas()) {
                    if (o.getGanador().equals(Boolean.TRUE)) {
                        if (o.getLote() != null) {
                            lotes++;
                        }
                    }

                }
                if(lotes>lotesFinal)
                    lotesFinal=lotes;
                lotes=0;
            }
            for(int i=0;i<=lotesFinal;i++) {
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Importe de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Fecha de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Empresa");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Ganador");

            }
            for (Contrato cont : ((List<Contrato>) resultado.getContrato().getResult())) {
                col = 1;
                HSSFRow dataRow2 = sheet.createRow(++fila);
                HSSFCell cellid2 = dataRow2.createCell(col);
                cellid2.setCellValue(cont.getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getEntity().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getOrganoContratante().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getStatus().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getImporteSinIVA());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getNumLicitadores()!=null?cont.getNumLicitadores():0);
                for(Oferta o :cont.getOfertas()){
                    if(o.getGanador().equals(Boolean.TRUE)) {
                        StringBuilder txtLotes = new StringBuilder();
                        String importeAdjudicacionSinIVA = "";
                        if (o.getGanador().equals(Boolean.TRUE)) {
                            if (o.getLote() == null) {
                                importeAdjudicacionSinIVA = "" + o.getImporteSinIVA();

                            } else {
                                if (StringUtils.isNotEmpty(txtLotes)) {
                                    txtLotes.append(" | ");
                                }
                                txtLotes.append(o.getLote().getDescription()
                                        + " - " + o.getImporteSinIVA()
                                        + " ");
                            }
                            if(!"".equals(importeAdjudicacionSinIVA)) {
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(importeAdjudicacionSinIVA);
                            }else{
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(""+txtLotes);
                            }

                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(o.getFechaAdjudicacion(), ConvertDate.DATETIME_FORMAT));
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getEmpresa().getNombre());
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getGanador() ? "Si" : "No");

                        }
                    }

                }
            }
        }
        if(datos.getClass().equals(EntidadContratante.class)) {
            EntidadContratante resultado=(EntidadContratante)datos;
            workbook.setSheetName(0, "Entidad Contratante " + resultado.getTitle());
            HSSFCell cellId = headerRow.createCell(col);
            cellId.setCellStyle(headerStyle);
            cellId.setCellValue("Entidad  ID");
            HSSFCell cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Nombre");
            for (ContratosPorAnyoIdPortal dato : resultado.getDatosPortal() ) {
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Año");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Nº Contratos Licitados");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Sin Iva €");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Con Iva €");
            }
            int fila = 1;
            col = 0;
            HSSFRow dataRow = sheet.createRow(fila);
            HSSFCell cellid = dataRow.createCell(col);
            cellid.setCellValue("" + resultado.getId());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + resultado.getTitle());
            for (ContratosPorAnyoIdPortal dato : resultado.getDatosPortal()) {
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getId().getAnyo());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotal());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotalSinIva());
                    cellid = dataRow.createCell(++col);
                    cellid.setCellValue(""+dato.getTotalConIva());
                }

            fila=fila+1;

            col=1;
            int lotes=0;
            int lotesFinal=0;
            HSSFRow dataRow4 = sheet.createRow(++fila);
            HSSFCell cellid3 = dataRow4.createCell(col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nombre del Contrato");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Entidad Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Organismo Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Estado");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Importe de licitación");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nº Licitadores");
            for(Contrato cont:resultado.getListadoContratoEntidad()) {
                for (Oferta o : cont.getOfertas()) {
                    if (o.getGanador().equals(Boolean.TRUE)) {
                        if (o.getLote() != null) {
                            lotes++;
                        }
                    }

                }
                if(lotes>lotesFinal)
                    lotesFinal=lotes;
                lotes=0;
            }
            for(int i=0;i<=lotesFinal;i++) {
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Importe de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Fecha de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Empresa");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Ganador");

            }

            for (Contrato cont :  resultado.getListadoContratoEntidad()) {
                col = 1;
                HSSFRow dataRow2 = sheet.createRow(++fila);
                HSSFCell cellid2 = dataRow2.createCell(col);
                cellid2.setCellValue(cont.getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getEntity().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getOrganoContratante().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getStatus().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getImporteSinIVA());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getNumLicitadores()!=null?cont.getNumLicitadores():0);
                for(Oferta o :cont.getOfertas()){
                    if(o.getGanador().equals(Boolean.TRUE)) {
                        StringBuilder txtLotes = new StringBuilder();
                        String importeAdjudicacionSinIVA = "";
                        if (o.getGanador().equals(Boolean.TRUE)) {
                            if (o.getLote() == null) {
                                importeAdjudicacionSinIVA = "" + o.getImporteSinIVA();

                            } else {
                                if (StringUtils.isNotEmpty(txtLotes)) {
                                    txtLotes.append(" | ");
                                }
                                txtLotes.append(o.getLote().getDescription()
                                        + " - " + o.getImporteSinIVA()
                                        + " ");
                            }
                            if(!"".equals(importeAdjudicacionSinIVA)) {
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(importeAdjudicacionSinIVA);
                            }else{
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(""+txtLotes);
                            }

                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(o.getFechaAdjudicacion(), ConvertDate.DATETIME_FORMAT));
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getEmpresa().getNombre());
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getGanador() ? "Si" : "No");

                        }
                    }

                }
            }
        }
        if(datos.getClass().equals(OrganismoContratante.class)) {
            OrganismoContratante resultado=(OrganismoContratante)datos;
            workbook.setSheetName(0, "Organismo Contratante " + resultado.getTitle());
            HSSFCell cellId = headerRow.createCell(col);
            cellId.setCellStyle(headerStyle);
            cellId.setCellValue("Organismo Contratante ID");
            HSSFCell cellFecha = headerRow.createCell(++col);
            cellFecha.setCellStyle(headerStyle);
            cellFecha.setCellValue("Nombre");
            for (ContratosPorAnyoOrganismoContratante dato : resultado.getDatos() ) {
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Año");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Nº Contratos Licitados");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Sin Iva €");
                cellFecha = headerRow.createCell(++col);
                cellFecha.setCellStyle(headerStyle);
                cellFecha.setCellValue("Importe Con Iva €");
            }
            int fila = 1;
            col = 0;
            HSSFRow dataRow = sheet.createRow(fila);
            HSSFCell cellid = dataRow.createCell(col);
            cellid.setCellValue("" + resultado.getId());
            cellid = dataRow.createCell(++col);
            cellid.setCellValue("" + resultado.getTitle());
            for (ContratosPorAnyoOrganismoContratante dato : resultado.getDatos()) {
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(""+dato.getId().getAnyo());
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(""+dato.getTotal());
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(""+dato.getTotalSinIva());
                cellid = dataRow.createCell(++col);
                cellid.setCellValue(""+dato.getTotalConIva());
            }

            fila=fila+1;

            col=1;
            int lotes=0;
            int lotesFinal=0;
            HSSFRow dataRow4 = sheet.createRow(++fila);
            HSSFCell cellid3 = dataRow4.createCell(col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nombre del Contrato");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Entidad Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Organismo Contratante");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Estado");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Importe de licitación");
            cellid3 = dataRow4.createCell(++col);
            cellid3.setCellStyle(sectionStyle);
            cellid3.setCellValue("Nº Licitadores");
            for(Contrato cont:resultado.getContrato().getResult()) {
                for (Oferta o : cont.getOfertas()) {
                    if (o.getGanador().equals(Boolean.TRUE)) {
                        if (o.getLote() != null) {
                            lotes++;
                        }
                    }

                }
                if(lotes>lotesFinal)
                    lotesFinal=lotes;
                lotes=0;
            }
            for(int i=0;i<=lotesFinal;i++) {
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Importe de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Fecha de Adjudicación");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Empresa");
                cellid3 = dataRow4.createCell(++col);
                cellid3.setCellStyle(sectionStyle);
                cellid3.setCellValue("Ganador");

            }
            for (Contrato cont : ((List<Contrato>) resultado.getContrato().getResult())) {
                col = 1;
                HSSFRow dataRow2 = sheet.createRow(++fila);
                HSSFCell cellid2 = dataRow2.createCell(col);
                cellid2.setCellValue(cont.getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getEntity().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getOrganoContratante().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(cont.getStatus().getTitle());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getImporteSinIVA());
                cellid2 = dataRow2.createCell(++col);
                cellid2.setCellValue(""+cont.getNumLicitadores()!=null?cont.getNumLicitadores():0);
                for(Oferta o :cont.getOfertas()){
                    if(o.getGanador().equals(Boolean.TRUE)) {
                        StringBuilder txtLotes = new StringBuilder();
                        String importeAdjudicacionSinIVA = "";
                        if (o.getGanador().equals(Boolean.TRUE)) {
                            if (o.getLote() == null) {
                                importeAdjudicacionSinIVA = "" + o.getImporteSinIVA();

                            } else {
                                if (StringUtils.isNotEmpty(txtLotes)) {
                                    txtLotes.append(" | ");
                                }
                                txtLotes.append(o.getLote().getDescription()
                                        + " - " + o.getImporteSinIVA()
                                        + " ");
                            }
                            if(!"".equals(importeAdjudicacionSinIVA)) {
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(importeAdjudicacionSinIVA);
                            }else{
                                cellid2 = dataRow2.createCell(++col);
                                cellid2.setCellValue(""+txtLotes);
                            }

                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(o.getFechaAdjudicacion(), ConvertDate.DATETIME_FORMAT));
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getEmpresa().getNombre());
                            cellid2 = dataRow2.createCell(++col);
                            cellid2.setCellValue(o.getGanador() ? "Si" : "No");

                        }
                    }

                }
            }
        }

        return workbook;
    }

    @Override
    public List<Contrato> getContratosCPV(Set<Cpv> cpv) {
        StringBuilder b = new StringBuilder();
        for (Cpv c : cpv) {
            b.append(c.getId() + ",");
        }

        String sql = " select c.id_contrato,c.nombre from perfil_contrato c, perfil_contrato_tiene_cpv cpv "
                + "where c.id_contrato=cpv.id_contrato and cpv.cpv in (" + b.substring(0, b.length() - 1) + ")";

        Query query = this.em().createNativeQuery(sql);
        @SuppressWarnings("unchecked")
        List<Object> list = query.getResultList();
        List<Contrato> listado = new ArrayList<Contrato>();
        for (Iterator<Object> iterador = list.iterator(); iterador.hasNext(); ) {
            Object[] row = (Object[]) iterador.next();
            Contrato p = new Contrato();
            p.setId((BigDecimal) row[0]);
            p.setTitle(row[1].toString());
            listado.add(p);
        }
        return listado;
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<Contrato> loadXls(InputStream flujo) throws IOException {
        List<Contrato> contratos = new ArrayList<Contrato>();
        /*EXPEDIENTE	NOMBRE	TIPOCONTRATO	FECHACADUCIDAD	CONT_MENOR	SERVICIO_GESTOR	ORGANO_CONTRATACION	DURACION	CPV	OBJETO	URGENTE	Canon	IMPORTE_CONIVA	IMPORTE_SINIVA	EnlaceInteres Plataforma*/
        HSSFWorkbook wb = null;
        List<Oferta> ofertas = new ArrayList<Oferta>();
        String expediente = "";
        Boolean ganador = false;
        try {
            List<String> cabeceras = new ArrayList<String>();
            HashMap<String, String> datos = new HashMap<String, String>();
            POIFSFileSystem fs = new POIFSFileSystem(flujo);
            wb = new HSSFWorkbook(fs);
            HSSFSheet hssfSheet = wb.getSheetAt(0);
            HSSFRow hssfRow;
            HSSFCell cell;
            String cellValue;
            int rows;
            rows = hssfSheet.getLastRowNum();
            int cols = 0;
            int tmp = 0;
            for (int r = 0; r <= rows; r++) {
                hssfRow = hssfSheet.getRow(r);

                if (hssfRow == null) {
                    break;
                } else {
                    if (r == 0) {
                        for (int c = 0; c < hssfRow.getLastCellNum(); c++) {
                            cellValue = hssfRow.getCell(c) == null ? "" :
                                    (hssfRow.getCell(c).getCellType() == CellType.STRING) ? hssfRow.getCell(c).getStringCellValue() :
                                            (hssfRow.getCell(c).getCellType() == CellType.NUMERIC) ? "" + hssfRow.getCell(c).getNumericCellValue() :
                                                    (hssfRow.getCell(c).getCellType() == CellType.BOOLEAN) ? "" + hssfRow.getCell(c).getBooleanCellValue() :
                                                            (hssfRow.getCell(c).getCellType() == CellType.BLANK) ? "BLANK" :
                                                                    (hssfRow.getCell(c).getCellType() == CellType.FORMULA) ? "FORMULA" :
                                                                            (hssfRow.getCell(c).getCellType() == CellType.ERROR) ? "ERROR" : "";
                            if (!cellValue.equals("")) {
                                datos.put(cellValue.toString(), "");
                            }
                            System.out.println(cellValue.toString());

                        }
                    } else {
                        Contrato contrato = new Contrato();
                        for (int c = 0; c < hssfRow.getLastCellNum(); c++) {
                            cellValue = hssfRow.getCell(c) == null ? "" :
                                    (hssfRow.getCell(c).getCellType() == CellType.STRING) ? hssfRow.getCell(c).getStringCellValue() :
                                            (hssfRow.getCell(c).getCellType() == CellType.NUMERIC) ? "" + hssfRow.getCell(c).getNumericCellValue() :
                                                    (hssfRow.getCell(c).getCellType() == CellType.BOOLEAN) ? "" + hssfRow.getCell(c).getBooleanCellValue() :
                                                            (hssfRow.getCell(c).getCellType() == CellType.BLANK) ? "BLANK" :
                                                                    (hssfRow.getCell(c).getCellType() == CellType.FORMULA) ? "FORMULA" :
                                                                            (hssfRow.getCell(c).getCellType() == CellType.ERROR) ? "ERROR" : "";

                            if (c == 0) {
                                EntidadContratante entity = new EntidadContratante();
                                expediente = cellValue;
                                expediente = expediente.replace("/", "-");
                                BigDecimal entidad = Utils.obtenerEntidad(Funciones.getPeticion());
                                if (entidad != null) {
                                    if ("S".equals(hssfRow.getCell(4).getStringCellValue())) {
                                        expediente = "MEN".concat(expediente);
                                    }
                                    if (expedienteEntidad.containsKey(entidad)) {
                                        if (!expediente.contains(expedienteEntidad.get(entidad))) {
                                            expediente = expedienteEntidad.get(entidad).toString().concat(expediente);
                                        }

                                    }
                                    entity.setId(entidad);
                                } else {
                                    entity.setId(BigDecimal.valueOf(1.0));
                                }
                                contrato.setEntity(entity);
                                contrato.setExpediente(expediente);
                            }
                            if (c == 1) {
                                contrato.setTitle(cellValue.toString());
                            }
                            if (c == 2) {
                                Tipocontrato tipo = new Tipocontrato();
                                if (cellValue.toString().contains("Obras")) {
                                    tipo.setId(BigDecimal.valueOf(1));
                                }
                                if (cellValue.toString().contains("Servicios")) {
                                    tipo.setId(BigDecimal.valueOf(2));
                                }
                                if (cellValue.toString().contains("Suministros")) {
                                    tipo.setId(BigDecimal.valueOf(3));
                                }
                                if (cellValue.toString().contains("Otros")) {
                                    tipo.setId(BigDecimal.valueOf(4));
                                }
                                if (cellValue.toString().contains("Privado")) {
                                    tipo.setId(BigDecimal.valueOf(5));
                                }
                                tipo.setTitle(cellValue);

                                contrato.setType(tipo);
                            }
                            if (c == 3 && !"".equals(cellValue.toString())) {
                                Date expiracion = hssfRow.getCell(c).getDateCellValue();
                                contrato.setExpiration(expiracion);
                            }
                            if (c == 4) {
                                Procedimiento procedimiento = new Procedimiento();
                                procedimiento.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
                                contrato.setProcedimiento(procedimiento);

                            }
                            if (c == 5) {
                                EstructuraOrganizativa servicio = new EstructuraOrganizativa();
                                servicio.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
                                contrato.setServicio(servicio);
                            }
                            if (c == 6) {
                                EstructuraOrganizativa organo = new EstructuraOrganizativa();
                                organo.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
                                contrato.setOrganoContratante(organo);//
                            }
                            if (c == 7) {
                                contrato.setDuracion(BigDecimal.valueOf(Double.valueOf(cellValue)));
                            }
                            if (c == 8 && !"".equals(cellValue) && !"BLANK".equals(cellValue)) {
                                List<Cpv> listadoCpv = new ArrayList<Cpv>();
                                String[] cpvsExcel;
                                Set<Cpv> cpvs = new HashSet<Cpv>();
                                if (cellValue.contains(",")) {
                                    cpvsExcel = cellValue.split(",");
                                    for (int i = 0; i < cpvsExcel.length; i++) {
                                        Cpv item = new Cpv();
                                        item.setId(BigDecimal.valueOf(Double.valueOf(cpvsExcel[i])));
                                        cpvs.add(item);
                                    }
                                } else {
                                    Cpv item = new Cpv();
                                    item.setId(BigDecimal.valueOf(Double.valueOf(cellValue)));
                                    cpvs.add(item);
                                }
                                contrato.setCpv(cpvs);
                            }
                            if (c == 9) {
                                contrato.setObjeto(cellValue.toString());
                            }
                            if (c == 10) {
                                if (cellValue.toString().equals("No"))
                                    contrato.setUrgente("N");
                                else
                                    contrato.setUrgente("S");
                            }
                            if (c == 12) {
                                contrato.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
                            }
                            if (c == 13) {
                                contrato.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
                            }
                            if (c == 14) {
                                if (!cellValue.isEmpty()) {
                                    Anuncio anuncio = new Anuncio();
                                    anuncio.setContrato(contrato);
                                    Tipoanuncio tipo = new Tipoanuncio();
                                    tipo.setId(BigDecimal.valueOf(9.0));
                                    anuncio.setType(tipo);
                                    String enlace = "<p><a href=\"" + cellValue + "\">&nbsp;Acceso a la informaci&oacute;n publicada en la Plataforma del Estado</a></p>";
                                    anuncio.setTitle("Anuncio Licitación");
                                    anuncio.setDescription(enlace);
                                    List<Anuncio> anuncios = new ArrayList<Anuncio>();
                                    anuncios.add(anuncio);
                                    contrato.setAnuncios(anuncios);
                                }
                            }

                        }
                        contrato.setCreationDate(Calendar.getInstance().getTime());
                        contrato.setFechaContrato(Calendar.getInstance().getTime());
                        contrato.setVisible("N");
                        Estado estado = new Estado();
                        estado.setId(0);
                        contrato.setStatus(estado);
                        contrato.setCanon(false);
                        contratos.add(contrato);
                    }
                }
            }
            return contratos;
        } catch (Exception ioe) {
            logger.error(ioe.getMessage());
        } finally {
            try {
                wb.close();
            } catch (IOException ex) {
                logger.error("Error al procesar el fichero después de cerrarlo: " + ex);
            }
        }
        return contratos;
    }


    @Autowired
    public OrganigramaGenericDAO daoOrganigrama;

    @Override
    public ResponseEntity updateFromVirtuoso() {
        try {

            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(FileUtils.readFileToString(new File("/home/ob544e/Descargas/2017.json"), CharEncoding.UTF_8));
            for (JsonNode nodo : actualObj.get("results").get("bindings")) {
                String expediente = nodo.has("identifier") ? nodo.get("identifier").get("value").asText() : "";
//            	String duracion = nodo.has("duracion") ? nodo.get("duracion").get("value").asText() : "";
                String cpv = nodo.has("cpv") ? nodo.get("cpv").get("value").asText().replace("http://purl.org/cpv/2008/code-", "") : "";
                String impLicitacionSinIVA = nodo.has("impLicitacionSinIVA") ? nodo.get("impLicitacionSinIVA").get("value").asText() : "";
                String impLicitacionConIVA = nodo.has("impLicitacionConIVA") ? nodo.get("impLicitacionConIVA").get("value").asText() : "";
                String servicio = nodo.has("servicio") ? nodo.get("servicio").get("value").asText() : "";
                String organo = nodo.has("organo") ? nodo.get("organo").get("value").asText() : "";
                String nombreContrato = nodo.has("nombreContrato") ? nodo.get("nombreContrato").get("value").asText() : "";
                String objeto = nodo.has("objeto") ? nodo.get("objeto").get("value").asText() : "";
                try {

                    Search searchContratos = new Search(Contrato.class);
                    searchContratos.addFilter(Filter.equal("expediente", expediente));
                    List<Contrato> lista = this.search(searchContratos);
                    if (lista.size() == 1) {
                        Contrato c = lista.get(0);

                        if (StringUtils.isEmpty(c.getObjeto())) {
                            c.setObjeto(objeto);
                        }
                        if (!"".equals(cpv) && (c.getCpv() == null || c.getCpv().isEmpty())) {
                            Cpv cpvNew = new Cpv();
                            cpvNew.setId(new BigDecimal(cpv));
                            c.getCpv().add(cpvNew);
                        }
                        if (!"".equals(impLicitacionSinIVA) && c.getImporteSinIVA() == null) {
                            c.setImporteSinIVA(new BigDecimal(impLicitacionSinIVA));
                        }
                        if (!"".equals(impLicitacionConIVA) && c.getImporteConIVA() == null) {
                            c.setImporteConIVA(new BigDecimal(impLicitacionConIVA));
                        }
                        if (!"".equals(servicio) && c.getServicio() == null) {
                            EstructuraOrganizativa e = daoOrganigrama.find(new BigDecimal(servicio));
                            if (e != null) {
                                c.setServicio(e);
                            } else {
                                System.out.println("servicio " + servicio + " no encontrada");
                            }
                        }
                        if (!"".equals(organo) && c.getOrganoContratante() == null) {
                            EstructuraOrganizativa e = daoOrganigrama.find(new BigDecimal(organo));
                            if (e != null) {
                                c.setOrganoContratante(e);
                            } else {
                                System.out.println("organo " + organo + " no encontrada");
                            }
                        }
                        System.out.println(c.getId() + ":" + expediente + ":" + cpv + ":" + impLicitacionSinIVA + ":" + impLicitacionConIVA + ":" + servicio + ":" + organo + ":" + nombreContrato + ":" + objeto);

                    } else {
                        System.out.println(expediente + " No encontrado");
                    }
                } catch (Exception e) {
                    System.out.println("Error expediente: " + expediente + " " + e.getMessage());
                }
            }
            return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(), "Ok"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
        }
    }
    @Override
    public SearchResult<ContratoTbfy> similitudPliegos(String name,String lang,String size,String terms,String source,String texto) throws IOException {

        List<ContratoTbfy> intermedio = new ArrayList<ContratoTbfy>(0);
        SearchResult<ContratoTbfy> result = new SearchResult<ContratoTbfy>();
        List<String> ids = new ArrayList<String>(0);
        if ("jrc".equals(source)) {
            try {
                StringBuilder jsonStringBuilder = this.peticionesTbfyPost(URLTBFYDOCUMENTITEMS, "", lang, texto, source, terms, Integer.valueOf(size));
                ObjectMapper mapper = new ObjectMapper();
                JsonNode actualObj = mapper.readTree(jsonStringBuilder.toString());
                Iterator<JsonNode> iterator = actualObj.elements();
                while (iterator.hasNext()) {
                    JsonNode valor = iterator.next();
                    ids.add(valor.get("id").asText());
                }
                for (String ocds : ids) {
                    StringBuilder tender = peticionesTbfy(URLTBFYDOCUMENT + ocds + "?text=true", ocds);
                    ContratoTbfy contrato = new ContratoTbfy();
                    ObjectMapper mapper1 = new ObjectMapper();
                    JsonNode actualObj2 = mapper1.readTree(tender.toString());
                    if (actualObj2.has("id")) {
                        contrato.setId(actualObj2.get("id").asText());
                    }
                    if (actualObj2.has("name")) {
                        contrato.setTitle(actualObj2.get("name").asText());
                    }
                    if (actualObj2.has("text")) {
                        contrato.setDescription(actualObj2.get("text").asText());
                    }
                    contrato.setIdioma(lang);
                    intermedio.add(contrato);
                }
                result.setStart(0);
                result.setTotalCount(intermedio.size());
                result.setResult(intermedio);
                result.setRows(intermedio.size());

            } catch (IOException ex) {
                ex.printStackTrace();
                return null;
            }
        } else {
            StringBuilder jsonStringBuilder = this.peticionesTbfyPost(URLTBFYDOCUMENTITEMS, name, lang, texto, source, terms, Integer.valueOf(size));
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonStringBuilder.toString());
            Iterator<JsonNode> iterator = actualObj.elements();
            while (iterator.hasNext()) {
                JsonNode valor = iterator.next();
                ids.add(valor.get("id").asText());
            }
            for (String id : ids) {
                String query = String.format(SPARQLLICITACIONTBFY, "filter (?qid= <http://data.tbfy.eu/tender/" + id + ">) }");
                ObjectMapper mapper2 = new ObjectMapper();
                JsonNode actualObj2 = null;
                try {
                    actualObj2 = mapper2.readTree(VirtuosoDataManagement.queryExternoTbfy(URLSPARQLTBFY, query, ContratoTbfy.class.getAnnotation(Grafo.class).value()));

                    if (actualObj2.get("results").get("bindings").size() > 0) {
                        for (JsonNode datos : actualObj2.get("results").get("bindings")) {
                            ContratoTbfy contrato = new ContratoTbfy();
                            contrato.setId(datos.has("qid") ? datos.get("qid").get("value").asText() : null);
                            contrato.setAwardUrl(datos.has("qdescription") ? datos.get("qdescription").get("value").asText() : null);
                            contrato.setStatus(datos.has("qstatus") ? datos.get("qstatus").get("value").asText() : null);
                            contrato.setTitle(datos.has("qtitle") ? datos.get("qtitle").get("value").asText() : null);
                            contrato.setBuyer(datos.has("buyer") ? datos.get("buyer").get("value").asText() : null);
                            contrato.setSupplier(datos.has("supplier") ? datos.get("supplier").get("value").asText() : null);
                            contrato.setAmount(datos.has("qamount") ? BigDecimal.valueOf(Double.valueOf(datos.get("qamount").get("value").asText())) : new BigDecimal("0.0"));
                            contrato.setCurrency(datos.has("qcurrency") ? datos.get("qcurrency").get("value").asText() : null);
                            contrato.setStarDate(datos.has("qstartdate") ? ConvertDate.string2Date(datos.get("qstartdate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                            contrato.setEndDate(datos.has("qenddate") ? ConvertDate.string2Date(datos.get("qenddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                            contrato.setDocumentUrl(datos.has("qdocumenturl") ? datos.get("qdocumenturl").get("value").asText() : null);
                            intermedio.add(contrato);
                        }
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            result.setStart(0);
            result.setTotalCount(intermedio.size());
            result.setResult(intermedio);
            result.setRows(intermedio.size());
        }
        return result;
    }
    @Override
    public ContratoTbfy similitudPliegosDetalle(String id, String lang) throws IOException {
        ContratoTbfy detalle = new ContratoTbfy();
        try {
                StringBuilder tender = peticionesTbfy(URLTBFYDOCUMENT+id+"?text=true", id);

                ObjectMapper mapper1 = new ObjectMapper();
                JsonNode actualObj2 = mapper1.readTree(tender.toString());
                if (actualObj2.has("id")) {
                    detalle.setId(actualObj2.get("id").asText());
                }
                if (actualObj2.has("name")) {
                    detalle.setTitle(actualObj2.get("name").asText());
                }
                if (actualObj2.has("text")) {
                    detalle.setDescription(actualObj2.get("text").asText());
                }


        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }

        return detalle;
    }

    public SearchResult<ContratoTbfy> jsonTbfy(String id) {
        SearchResult<ContratoTbfy> resultado = new SearchResult<ContratoTbfy>();
        List<ContratoTbfy> intermedio = new ArrayList<ContratoTbfy>(0);
        try {

            StringBuilder jsonStringBuilder = peticionesTbfy(URLCONTRACTINPROCESS, id);
            List<String> ids = new ArrayList<String>(0);
            List<String> idsTender = new ArrayList<String>(0);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonStringBuilder.toString());
            Iterator<JsonNode> iterator = actualObj.elements();
            while (iterator.hasNext()) {
                JsonNode valor = iterator.next();
                ids.add(valor.get("id").asText());
            }
            for (String ocds : ids) {
                StringBuilder tender = peticionesTbfy(URLCONTRACTINPROCESSTENDER, ocds);
                ContratoTbfy contrato = new ContratoTbfy();
                ObjectMapper mapper1 = new ObjectMapper();
                JsonNode actualObj2 = mapper1.readTree(tender.toString());
                Iterator<JsonNode> iterator2 = actualObj2.elements();
                while (iterator2.hasNext()) {
                    JsonNode valor2 = iterator2.next();
                    if (valor2.has("id")) {
                        contrato.setId(valor2.get("id").asText());
                    }
                    if (valor2.has("title")) {
                        contrato.setTitle(valor2.get("title").asText());
                    }
                    if (valor2.has("status")) {
                        contrato.setStatus(valor2.get("status").asText());
                    }
                    StringBuilder urlDocument = peticionesTbfy(URLTENDERDOCUMENT, contrato.getId());
                    if (urlDocument.capacity()!=0) {
                        ObjectMapper mapper2 = new ObjectMapper();

                        JsonNode actualObj3 = mapper2.readTree(urlDocument.toString());
                        Iterator<JsonNode> iterator3 = actualObj3.elements();
                        while (iterator3.hasNext()) {
                            JsonNode valor3 = iterator3.next();
                            if (valor3.has("url")) {
                                contrato.setUrl(valor3.get("url").asText());
                            }
                        }
                    }
                }
                intermedio.add(contrato);

            }
            resultado.setStart(0);
            resultado.setTotalCount(intermedio.size());
            resultado.setResult(intermedio);
            resultado.setRows(intermedio.size());

        } catch (JsonProcessingException ex) {
            ex.printStackTrace();
            return null;
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return resultado;

    }
    public StringBuilder peticionesTbfyPost(String urlTbfy, String name,String lang,String text,String source,String terms,Integer size) {
        StringBuilder resultado = new StringBuilder();
        try {
            JSONObject parameters   = new JSONObject();

            if(!"".equals(lang))parameters.put("lang",lang);
            if(!"".equals(name))parameters.put("name",name);
            if(!"".equals(text))parameters.put("text",text);
            parameters.put("size",size);
            if(!"".equals(terms))parameters.put("terms",terms);
            if(!"".equals(source))parameters.put("source",source);
            Funciones.setProxy();
            String urlFormat = String.format(urlTbfy);
            logger.info("PeticionPOST: " + urlFormat);
            URL url = new URL(urlFormat);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setRequestProperty("Content-Type"," application/json");
            conn.setRequestProperty("Accept", "application/json");
            conn.setDoOutput(true);
            OutputStream os = conn.getOutputStream();
            os.write(parameters.toString().getBytes());
            os.flush();
            os.close();
            int responseCode = conn.getResponseCode();
            if(responseCode==200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));

                StringBuilder jsonStringBuilder = new StringBuilder();
                String s = null;
                while ((s = reader.readLine()) != null) {
                    jsonStringBuilder.append(s);
                }
                reader.close();
                return jsonStringBuilder;
            }else
                return new StringBuilder();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultado;

    }

    public StringBuilder peticionesTbfy(String urlTbfy, String parametro) {
        StringBuilder resultado = new StringBuilder();
        try {
            String urlFormat = String.format(urlTbfy, parametro);
            URL url = new URL(urlFormat);
            
            logger.info("Petición TBFY: " + urlFormat);
            Funciones.setProxy();
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setUseCaches(false);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            if(responseCode==200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                StringBuilder jsonStringBuilder = new StringBuilder();
                String s = null;
                while ((s = reader.readLine()) != null) {
                    jsonStringBuilder.append(s);
                }
                reader.close();
                return jsonStringBuilder;
            }else
                return new StringBuilder();

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return resultado;

    }
    public SearchResult<ContratoTbfy> jsonTbfyQuery(String name) {
        SearchResult<ContratoTbfy> resultado=new SearchResult<ContratoTbfy>();
        List<ContratoTbfy> intermedio=new ArrayList<ContratoTbfy>();
        String query = String.format(SPARLQQUERY, "filter(regex(qtitle" +name +"))");
        logger.info("Petición TBFY SPARQL: " + query);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(VirtuosoDataManagement.queryExternoTbfy(URLSPARQLTBFY, query, ContratoTbfy.class.getAnnotation(Grafo.class).value()));
            if (actualObj.get("results").get("bindings").size() > 0) {
                for (JsonNode datos : actualObj.get("results").get("bindings")) {
                    ContratoTbfy contrato=new ContratoTbfy();
                    contrato.setId(datos.has("qid") ? datos.get("qid").get("value").asText() : null);
                    contrato.setTitle(datos.has("qtitle") ? datos.get("qtitle").get("value").asText() : null);
                    contrato.setDescription(datos.has("qdescription") ? datos.get("qdescription").get("value").asText() : null);
                    contrato.setStatus(datos.has("qstatus") ? datos.get("qstatus").get("value").asText() : null);
                    contrato.setBuyer(datos.has("buyer") ? datos.get("buyer").get("value").asText() : null);
                    contrato.setSupplier(datos.has("supplier") ? datos.get("supplier").get("value").asText() : null);
                    contrato.setAmount(datos.has("qamount") ? BigDecimal.valueOf(Double.valueOf(datos.get("qamount").get("value").asText())) : new BigDecimal("0.0"));
                    contrato.setCurrency(datos.has("qcurrency") ? datos.get("qcurrency").get("value").asText() : null);
                    contrato.setStarDate(datos.has("qstartdate") ? ConvertDate.string2Date(datos.get("qstartdate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                    contrato.setEndDate(datos.has("qenddate") ? ConvertDate.string2Date(datos.get("qenddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                    contrato.setDocumentUrl(datos.has("qdocumenturl") ? datos.get("qdocumenturl").get("value").asText() : null);
                    intermedio.add(contrato);
                }
            }
            resultado.setRows(intermedio.size());
            resultado.setStart(0);
            resultado.setTotalCount(intermedio.size());
            resultado.setResult(intermedio);

            return resultado;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    @Override
    public SearchResult<ContratoTbfy> jsonTbfyQuerylicitadores(String name,String size,String jurisdiction,String status,String description,String title,String currency,String startDate,String endDate) throws Exception {
        SearchResult<ContratoTbfy> resultado=new SearchResult<ContratoTbfy>();
        List<ContratoTbfy> intermedio=new ArrayList<ContratoTbfy>();
        List<String> idsOrganization=new ArrayList<String>();
        String urlLicitador="http://tbfy.librairy.linkeddata.es/api/supplier?";
        if(!"".equals(name)) {
             urlLicitador = urlLicitador + "name=" + name.replace(" ","+") ;
        }
        if(!"".equals(size)) {
             urlLicitador = urlLicitador + "&size=" + size ;
        }
        if(!"".equals(jurisdiction)) {
             urlLicitador = urlLicitador + "&jurisdiction=" + jurisdiction ;
        }
        if(!"".equals(status)) {
             urlLicitador = urlLicitador + "&status=" + status ;
        }
        if(!"".equals(description)) {
             urlLicitador = urlLicitador + "&description=" + description ;
        }
        if(!"".equals(title)) {
             urlLicitador = urlLicitador + "&title=" + title ;
        }
        if(!"".equals(currency)) {
             urlLicitador = urlLicitador + "&currency=" + currency ;
        }
        if(!"".equals(startDate)) {
             urlLicitador = urlLicitador + "&start_dt=" + startDate+"T00:00:00" ;
        }
        if(!"".equals(endDate)) {
             urlLicitador = urlLicitador + "&end_dt=" + endDate +"T00:00:00";
        }
        try {
            StringBuilder licitador = peticionesTbfy(urlLicitador, "");
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(licitador.toString());
            Iterator<JsonNode> iterator = actualObj.elements();
            while (iterator.hasNext()) {
                JsonNode valor = iterator.next();
                if (!idsOrganization.contains(valor.get("organisation").asText())) {
                    idsOrganization.add(valor.get("organisation").asText());
                }
            }
            for (String id : idsOrganization) {
                String query = String.format(SPARQLTBFYLICITADOR, " filter (?ss=<http://data.tbfy.eu/organisation/"+id+">) } limit "+size);
                ObjectMapper mapper2 = new ObjectMapper();
                JsonNode actualObj2 = mapper2.readTree(VirtuosoDataManagement.queryExternoTbfy(URLSPARQLTBFY, query, ContratoTbfy.class.getAnnotation(Grafo.class).value()));
                if (actualObj2.get("results").get("bindings").size() > 0) {
                    for (JsonNode datos : actualObj2.get("results").get("bindings")) {
                        ContratoTbfy contrato = new ContratoTbfy();
                        contrato.setOrganizationUrl(datos.has("ss") ? datos.get("ss").get("value").asText() : null);
                        contrato.setAwardUrl(datos.has("supaward") ? datos.get("supaward").get("value").asText() : null);
                        contrato.setTenderdUrl(datos.has("qtenderid") ? datos.get("qtenderid").get("value").asText() : null);
                        contrato.setTitle(datos.has("qtitle") ? datos.get("qtitle").get("value").asText() : null);
                        contrato.setDescription(datos.has("qdescription") ? datos.get("qdescription").get("value").asText() : null);
                        contrato.setSupplier(datos.has("qlegalname") ? datos.get("qlegalname").get("value").asText() : null);
                        contrato.setAmount(datos.has("qawardamount") ? BigDecimal.valueOf(Double.valueOf(datos.get("qawardamount").get("value").asText())) : new BigDecimal("0.0"));
                        contrato.setCurrency(datos.has("qawardcurrency") ? datos.get("qawardcurrency").get("value").asText() : null);
                        contrato.setAwarDate(datos.has("qawarddate") ? ConvertDate.string2Date(datos.get("qawarddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                        contrato.setAwarStartDate(datos.has("qawardstartdate") ? ConvertDate.string2Date(datos.get("qawardstartdate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                        contrato.setAwarEndDate(datos.has("qawardenddate") ? ConvertDate.string2Date(datos.get("qawardenddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                        contrato.setStatus(datos.has("qawardstatus") ? datos.get("qawardstatus").get("value").asText() : null);
                        contrato.setAwardTitle(datos.has("qawardtitle") ? datos.get("qawardtitle").get("value").asText() : null);
                        contrato.setAwardDescription(datos.has("qawarddescription") ? datos.get("qawarddescription").get("value").asText() : null);
                        contrato.setJurisdiccion(datos.has("qjurisdiction") ? datos.get("qjurisdiction").get("value").asText() : null);

                        intermedio.add(contrato);
                    }

                }

            }

            resultado.setRows(intermedio.size());
            resultado.setStart(0);
            resultado.setTotalCount(intermedio.size());
            resultado.setResult(intermedio);

            return resultado;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    public ContratoTbfy licitacionDetalle(String id) throws IOException{
        ContratoTbfy contrato = new ContratoTbfy();
        try {
            String query=String.format(SPARQLLICITACIONTBFY,"filter (?qid=<http://data.tbfy.eu/tender/"+id+">) }");
            ObjectMapper mapper2 = new ObjectMapper();
            JsonNode actualObj2 = mapper2.readTree(VirtuosoDataManagement.queryExternoTbfy(URLSPARQLTBFY, query, ContratoTbfy.class.getAnnotation(Grafo.class).value()));
            if (actualObj2.get("results").get("bindings").size() > 0) {
                for (JsonNode datos : actualObj2.get("results").get("bindings")) {

                    contrato.setId(datos.has("qid") ? datos.get("qid").get("value").asText() : null);
                    contrato.setAwardUrl(datos.has("qdescription") ? datos.get("qdescription").get("value").asText() : null);
                    contrato.setStatus(datos.has("qstatus") ? datos.get("qstatus").get("value").asText() : null);
                    contrato.setTitle(datos.has("qtitle") ? datos.get("qtitle").get("value").asText() : null);
                    contrato.setBuyer(datos.has("buyer") ? datos.get("buyer").get("value").asText() : null);
                    contrato.setSupplier(datos.has("supplier") ? datos.get("supplier").get("value").asText() : null);
                    contrato.setAmount(datos.has("qamount") ? BigDecimal.valueOf(Double.valueOf(datos.get("qamount").get("value").asText())) : new BigDecimal("0.0"));
                    contrato.setCurrency(datos.has("qcurrency") ? datos.get("qcurrency").get("value").asText() : null);
                    contrato.setAwarDate(datos.has("qawarddate") ? ConvertDate.string2Date(datos.get("qawarddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                    contrato.setStarDate(datos.has("qstartdate") ? ConvertDate.string2Date(datos.get("qstartdate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                    contrato.setEndDate(datos.has("qenddate") ? ConvertDate.string2Date(datos.get("qenddate").get("value").asText().replace("T", " "), ConvertDate.DATETIMEEN_FORMAT) : null);
                    contrato.setDocumentUrl(datos.has("qdocumenturl") ? datos.get("qdocumenturl").get("value").asText() : null);

                }
            }

                } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

        return contrato;
    }
    }

