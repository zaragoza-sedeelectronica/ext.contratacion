package org.sede.servicio.perfilcontratante;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.ciudadesabiertas.utils.Util;
import org.hibernate.Hibernate;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.Mensaje;
import org.sede.core.rest.MimeTypes;
import org.sede.core.rest.Peticion;
import org.sede.core.utils.ConvertDate;
import org.sede.core.utils.Funciones;
import org.sede.core.utils.Propiedades;
import org.sede.servicio.ModelAttr;
import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado.CodiceConverterMenor;
import org.sede.servicio.perfilcontratante.dao.*;
import org.sede.servicio.perfilcontratante.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigInteger;
import java.nio.file.StandardCopyOption;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import java.io.*;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.nio.file.*;
import java.time.Period;
import java.util.*;

import static org.apache.solr.client.solrj.impl.XMLResponseParser.log;
import static org.sede.core.utils.Propiedades.*;


//@Gcz(servicio = "ADMIN", seccion = "ADMIN")
@Gcz(servicio = "PERFILCONTRATANTE", seccion = "CONTRATO")
@Controller
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ApiTramitaController.MAPPING, method = RequestMethod.GET)
public class ApiTramitaController {
    //region Atributtes
    private static final Logger logger = LoggerFactory.getLogger(ApiTramitaController.class);
    private static final String SERVICIO = "apitramita";
    public static final String MAPPING = "servicio/" + SERVICIO;
    private static String URIWS = getString("contratacion.estado.ws");
    private static String USER = "restapi";
    private static String PASS = "aytointegr0!#";
    @Autowired
    private MessageSource messageSource;
    @Autowired
    public ContratoGenericDAO dao;
    @Autowired
    public AnuncioGenericDAO daoAnuncio;
    @Autowired
    public TipoContratoGenericDAO daoTipoContrato;
    @Autowired
    public EstadoGenericDAO daoEstado;
    @Autowired
    public OfertaGenericDAO daoOferta;
    @Autowired
    public LoteGenericDAO daoLote;
    @Autowired
    public EntidadContratanteGenericDAO daoEntidadContratante;
    @Autowired
    public OrganigramaGenericDAO daoOrganigrama;
    @Autowired
    private CpvGenericDAO daoCpv;
    @Autowired
    private EmpresaGenericDAO daoEmpresa;
    @Autowired
    private CriteriosGenericDAO daoCriterios;
    @Autowired
    private TipoEmpresaGenericDAO daoTipoEmpresa;
    //endregion

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = {MediaType.TEXT_HTML_VALUE, "*/*"})
    public String home(Model model, @RequestParam(name = "carpeta", required = false, defaultValue = "") String carpeta) throws IOException, SearchParseException {
        if (!carpeta.isEmpty()) {
            model.addAttribute(ModelAttr.RESULTADO, apiTramitaJson(carpeta));
        } else {
            model.addAttribute(ModelAttr.RESULTADO, null);
        }
        return MAPPING + "/index";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/carga-json", method = RequestMethod.POST, produces = {MediaType.TEXT_HTML_VALUE, "*/*"})
    public String carga(Model model, @RequestParam(name = "json", defaultValue = "", required = false) String json) throws Exception, IOException {
        if (!json.isEmpty()) {
            Contrato con = rellenarContratoAdjudicacion(json, Funciones.getPeticion());
            SearchResult<Contrato> resultado = new SearchResult<Contrato>();
            List<Contrato> listado = new ArrayList<Contrato>();
            listado.add(con);
            resultado.setResult(listado);
            resultado.setTotalCount(listado.size());
            resultado.setStart(0);
            resultado.setRows(listado.size());
            model.addAttribute(ModelAttr.RESULTADO, resultado);
        } else {
            model.addAttribute(ModelAttr.RESULTADO, null);
        }
        return MAPPING + "/index";
    }

/*FIXME borrar el metodo apitramitaJSON*/
    @Permisos(Permisos.MOD)
    @ResponseClass(value = Contrato.class, entity = SearchResult.class)
    @RequestMapping(value = "/api-carga-ofertas", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public @ResponseBody
    ResponseEntity<?> apiTramitaJson(String carpeta) throws SearchParseException, IOException {
        SearchResult<Contrato> resultado = new SearchResult<Contrato>();
        try {

            List<Contrato> lista = new ArrayList<Contrato>(0);
            resultado.setResult(lista);
           // String intDir = "C:\\Users\\piglesias\\Desktop\\Contratos\\Jsons CM\\" + carpeta + "/";
            String intDir = "/home/documentacionweb/Escritorio/Pablo/Jsons CM/" + carpeta + "/";
            File interfaceDirectory = new File(intDir);
            for (File file : interfaceDirectory.listFiles()) {
                InputStream stream = new FileInputStream(file);
                String datos = IOUtils.toString(stream);
                //System.out.println(datos);
                stream.close();
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
                JsonNode actualObj = mapper.readTree(datos);
                String expediente = actualObj.get("dcterms:identifier").asText();
                Search busqueda = new Search(Contrato.class);
                busqueda.addFilterEqual("expediente", expediente);
                Search search = new Search(Contrato.class);
                search.addFilterEqual("expediente", expediente);
                Contrato con = dao.getResultsForExpediente(expediente);
                logger.info("Expediente---->:" + con.getExpediente());
                Peticion peticion = new Peticion();
                Contrato conJson = rellenarContratoAdjudicacion(actualObj.toString(), peticion);
                lista.add(conJson);
                File ficheroDestino = new File("/home/documentacionweb/Escritorio/Pablo/Jsons CM/tramitados" + carpeta,file.getName());

                try {
                    if (file.exists()) {
                        Files.copy(Paths.get(file.getAbsolutePath()), Paths.get(ficheroDestino.getAbsolutePath()), StandardCopyOption.REPLACE_EXISTING);

                    } else {
                        System.out.println("El fichero " + file + " no existe en el directorio ");
                    }
                    file.delete();
                } catch (IOException e) {
                    System.err.println(e);
                }
            }


            resultado.setRows(0);
            resultado.setTotalCount(lista.size());
            resultado.setResult(lista);

        } catch (Exception e) {

            logger.error(e.getMessage());
        }
        return ResponseEntity.ok(resultado);
    }

    @OpenData
    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/", consumes = MimeTypes.JSON, method = RequestMethod.POST, produces = {MimeTypes.JSON})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody
    ResponseEntity<?> apiCrear(HttpServletRequest httpRequest) throws Exception {
        Contrato contrato = new Contrato();
        try {
            contrato = rellenarContratoLicitacion(Funciones.getPeticion().getCuerpoPeticion(), Funciones.getPeticion());
            Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
            if (!errores.isEmpty()) {
                return Funciones.generarMensajeError(errores);
            }

            if ("10".equals(contrato.getProcedimiento().getId().toString())) {
                contrato.setContratoMenor(true);
            } else {
                contrato.setContratoMenor(false);
            }
            VirtuosoDataManagement.loadJsonLd(Funciones.getPeticion().getCuerpoPeticion(), Contrato.class.getAnnotation(Grafo.class).value());
        } catch (Exception e) {
            e.printStackTrace();
            Funciones.sendMail("Error al cargar contrato", e.getMessage() + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(400, "No creado correctamente origen Tramita"));
        }
        return ResponseEntity.status(HttpStatus.OK).body(new Mensaje(200, "Registro modificado correctamente origen Tramita"));
    }

    @OpenData
    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/", consumes = MimeTypes.JSON, method = RequestMethod.PUT, produces = {MimeTypes.JSON})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody
    ResponseEntity<?> apiModificar(HttpServletRequest httpRequest) throws Exception {

        Contrato contrato = rellenarContratoAdjudicacion(Funciones.getPeticion().getCuerpoPeticion(), Funciones.getPeticion());
        Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
        if (!errores.isEmpty()) {
            Funciones.sendMail("Error al cargar contrato", errores + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
            return Funciones.generarMensajeError(errores);
        } else {
            Funciones.sendMail("Exito carga del contrato", contrato.toString() + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
            return ResponseEntity.status(HttpStatus.OK).body(new Mensaje(200, "Registro modificado correctamente origen Tramita"));
        }
    }

    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/integracion-estado", method = RequestMethod.GET, produces = {MimeTypes.XML})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody
    ResponseEntity<?> apiIntegracionPlataforma(@RequestParam(name = "expediente") String expediente) throws Exception {
        try {
            Search busqueda = new Search();
            busqueda.addFilterEqual("expediente", expediente);
            Contrato contrato = dao.searchUnique(busqueda);

            if (contrato != null) {
                Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
                if (!errores.isEmpty()) {
                    //Funciones.sendMail("Error al cargar contrato", errores + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
                    return Funciones.generarMensajeError(errores);
                }
                String menor = CodiceConverterMenor.conversor(contrato);
                CodiceConverterMenor.validarXsd(menor);

                return ResponseEntity.ok(menor);

            }

            return ResponseEntity.ok(contrato);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((e.getMessage()));
        }
    }

    public Contrato rellenarContratoAdjudicacion(String jsonld, Peticion peticion) throws Exception {
        try {
            JsonNode actualObjLote;
            boolean lotesBolean = false;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonld);
            Search busqueda = new Search();
            busqueda.addFilterILike("expediente", (actualObj.get("dcterms:identifier").asText()));
            Contrato con = dao.searchUnique(busqueda);
            if (con == null) {
                con = rellenarContratoLicitacion(jsonld, peticion);

            }
            // Estamos en la adjudicacion
            if (actualObj.has("pc:tender")) {
                if (actualObj.has("tbfy:numberOfTenderers")) {
                    con.setNumLicitadores(actualObj.get("tbfy:numberOfTenderers").asInt());
                }

                boolean ganador = false;
                for (Oferta ofer : con.getOfertas()) {
                    if (ofer.getGanador())
                        ganador = true;
                }
                JsonNode jsonNode = actualObj.get("pc:tender");
                if (jsonNode.isArray()) {
                    Iterator<JsonNode> iterator = jsonNode.elements();
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        if (valor.has("pproc:awardDate") || valor.has("pproc:formalizedDate") ) {
                            if (!ganador) {
                                Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, true, "");
                                con.getOfertas().add(ofertaGanadora);
                            }
                        } else {
                            Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, false, "");
                            con.getOfertas().add(ofertaGanadora);
                        }
                    }
                }

                if (actualObj.has("pc:lot")) {
                    Iterator<JsonNode> iteratorlote = actualObj.get("pc:lot").elements();
                    while (iteratorlote.hasNext()) {
                        JsonNode loteAdjudicacion = iteratorlote.next();
                        String idLote = loteAdjudicacion.get("dcterms:identifier").asText().replace("contzar:", "");
                        if (loteAdjudicacion.has("pc:tender")) {
                            JsonNode jsonNodeLote = loteAdjudicacion.get("pc:tender");
                            if (jsonNode.isArray()) {
                                Iterator<JsonNode> iterator = jsonNode.elements();
                                while (iterator.hasNext()) {
                                    JsonNode valor = iterator.next();
                                    if (valor.has("pproc:awardDate")) {
                                        Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, true, idLote);
                                        con.getOfertas().add(ofertaGanadora);
                                        con.setFechaContrato(ofertaGanadora.getFechaAdjudicacion());
                                    } else {
                                        Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, false, idLote);
                                        con.getOfertas().add(ofertaGanadora);
                                    }
                                }
                            }
                        }
                    }
                }


                Estado estado = new Estado();
                estado.setId(5);
                con.setStatus(estado);
                boolean existeAnuncio = false;
                if(con.getAnuncios()!=null) {
                    for (Anuncio anun : con.getAnuncios()) {
                        if (anun.getType().getId().equals(new BigDecimal(6.0))) {
                            existeAnuncio = true;
                            break;
                        }
                    }
                }
                if (!existeAnuncio) {
                    String anuncioAdjudicacion = UtilsContrato
                            .obtenerLiTituloContrato(actualObj)
                            + UtilsContrato.obtenerLiTipoContrato(con.getType())
                            + UtilsContrato.obtenerLiGestor(actualObj)
                            + UtilsContrato.obtenerLiOrganoContratacion(actualObj)
                            + UtilsContrato.obtenerLiExpediente(actualObj)
                            + UtilsContrato.obtenerLiObjeto(actualObj)
                            + UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
                            + UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
                            + UtilsContrato.obtenerLiAdjudicacion(actualObj);
                    if (anuncioAdjudicacion.length() > 0) {
                        Anuncio adjudicacion = new Anuncio();
                        adjudicacion.setTitle("Adjudicación");
                        adjudicacion.setDescription("<ol>" + anuncioAdjudicacion
                                + "</ol>");
                        Tipoanuncio tipoAnuncio = new Tipoanuncio();
                        tipoAnuncio.setId(UtilsContrato.TIPO_ANUNCIO_ADJUDICACION);
                        adjudicacion.setType(tipoAnuncio);
                        adjudicacion.setVisible("S");
                        adjudicacion.setUsuarioAlta(peticion.getClientId());
                        adjudicacion.setCreationDate(new Date());
                        adjudicacion.setLastUpdated(new Date());
                        adjudicacion.setPubDate(new Date());
                        adjudicacion.setContrato(con);
                        daoAnuncio.almacenar(adjudicacion);

//                       if(con.getAnuncios()==null){
//                              List<Anuncio>anun=new ArrayList<Anuncio>(0);
//                              anun.add(adjudicacion);
//                              con.setAnuncios(anun);
//                       }else{
//                           con.getAnuncios().add(adjudicacion);
//                       }
                    }
                }
            }


            List<Oferta> ofertas = new ArrayList<Oferta>();
            List<Anuncio> anuncios = new ArrayList<Anuncio>();

            ofertas = con.getOfertas();
            con.setOfertas(null);
            con.setCriterios(null);
            dao.save(con);
            dao.flush();
            for (Oferta oferta : ofertas) {
                oferta.setContrato(con);
                daoOferta.save(oferta);
                daoOferta.flush();
            }


            return con;
        } catch (Exception e) {

             logger.error(e.getMessage());
            return null;
        }
    }

    public Contrato rellenarContratoLicitacion(String jsonld, Peticion peticion) throws Exception {
        Contrato con = new Contrato();
        try {
            JsonNode actualObjLote;
            boolean lotesBolean = false;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonld);

            if (actualObj.has("dcterms:title")) {
                con.setTitle(actualObj.get("dcterms:title").asText());
            }
            con.setExpediente(actualObj.get("dcterms:identifier").asText());
            con.setType(daoTipoContrato.obtenerTipo(actualObj));
            if (actualObj.has("pproc:contractTemporalConditions")) {
                JsonNode temporal = actualObj.get("pproc:contractTemporalConditions");
                if (temporal.has("pproc:estimatedDuration")) {
                    try {
                        Integer plazoEjecucion = 0;
                        if (!"".equals(temporal.get("pproc:estimatedDuration").asText()) && (temporal.get("pproc:estimatedDuration").asText()).length() > 1 && (temporal.get("pproc:estimatedDuration").asText()).length() < 10) {
                            Period period = Period.parse(temporal.get("pproc:estimatedDuration").asText());

                            if (period.getYears() > 0) {
                                plazoEjecucion = period.getYears() * 365;
                            }
                            if (period.getMonths() > 0) {
                                if (plazoEjecucion > 0) {
                                    plazoEjecucion = plazoEjecucion + period.getMonths() * 30;
                                } else {
                                    plazoEjecucion = period.getMonths() * 30;
                                }
                            }
                            if (period.getDays() > 0) {
                                if (plazoEjecucion > 0) {
                                    plazoEjecucion = plazoEjecucion + period.getDays();
                                } else {
                                    plazoEjecucion = period.getDays();
                                }
                            }
                        }
                        con.setDuracion(BigDecimal.valueOf(Double.valueOf(plazoEjecucion)));
                    } catch (IllegalArgumentException e) {
                        con.setDuracion(BigDecimal.valueOf(temporal.get("pproc:estimatedDuration").doubleValue()));
                    }
                }
            }

            if (actualObj.has("pproc:managingDepartment")) {
                JsonNode gestor = actualObj.get("pproc:managingDepartment");
                String idEntidad = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
                EstructuraOrganizativa servicio = daoOrganigrama.find(BigDecimal.valueOf(Double.valueOf(idEntidad)));

                con.setServicio(servicio);
            }
            if (actualObj.has("pproc:contractingBody")) {
                JsonNode gestor = actualObj.get("pproc:contractingBody");
                String idOrgano = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
                EstructuraOrganizativa organo = daoOrganigrama.find(BigDecimal.valueOf(Double.valueOf(idOrgano)));
                con.setOrganoContratante(organo);
            }
            if (actualObj.has("pproc:contractProcedureSpecifications")) {
                JsonNode procedimiento = actualObj.get("pproc:contractProcedureSpecifications");
                String retorno = "N";
                if (procedimiento.has("pproc:procedureType")
                        && procedimiento.get("pproc:procedureType").asText().indexOf("Minor") >= 0) {
                    retorno = "S";
                }
                con.setContratoMenor("S".equals(retorno) ? true : false);
                if ("S".equals(retorno)) {
                    Procedimiento proce = new Procedimiento();
                    proce.setId(new BigDecimal(10));
                    con.setProcedimiento(proce);
                } else {
                    JsonNode procedure = actualObj.get("pproc:contractProcedureSpecifications");
                    if (procedure.has("pproc:procedureType")) {
                        Procedimiento procede = new Procedimiento();
                        String procedureType = procedure.get("pproc:procedureType").asText();
                        if (procedureType.indexOf("pproc:RegularOpen") >= 0) {
                            procede.setId(new BigDecimal(1.0));
                            con.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:Negotiated") >= 0) {
                            procede.setId(new BigDecimal(7.0));
                            con.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:Restricted") >= 0) {
                            procede.setId(new BigDecimal(2.0));
                            con.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:SimpleOpen") >= 0) {
                            procede.setId(new BigDecimal(8.0));
                            con.setProcedimiento(procede);
                        }
                        if (actualObj.has("pproc:urgencyType")) {
                            String urgencia = actualObj.get("pproc:urgencyType").asText();
                            if ("pproc:Express".equals(urgencia)) {
                                con.setUrgente("S");

                            } else {
                                con.setUrgente("N");
                            }
                        }
                    }
                }
            }
            con.setCanon(false);
            con.setVisible("S");
            con.setCreationDate(new Date());
            con.setPubDate(new Date());
            Estado estado = new Estado();
            estado.setId(0);
            con.setStatus(estado);
            con.setEntity(new EntidadContratante(new BigDecimal(1.0)));
            dao.save(con);
            dao.flush();

            if (actualObj.has("pproc:legalDocumentReference")) {
                Iterator<JsonNode> iterator = actualObj.get(
                        "pproc:legalDocumentReference").elements();
                while (iterator.hasNext()) {
                    JsonNode valor = iterator.next();
                    Anuncio pliego = new Anuncio();
                    pliego.setTitle(valor.get("dcterms:title").asText());
                    pliego.setFileName(Utils.normalizar(valor.get("name").asText()));
                    String fileName = pliego.getFileName();
                  /*  CustomMultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);*/
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio.setId(Utils.TIPO_ANUNCIO_PLIEGOS_ADMINISTRATIVOS);
                    pliego.setType(tipoAnuncio);
                    pliego.setContrato(con);
                    pliego.setVisible("S");
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    pliego.setAdjunto(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")));
                    //pliego.setAdjunto(customMultipartFile.getBytes());
                    daoAnuncio.almacenar(pliego);

                }
            }
            if (actualObj.has("pproc:additionalDocumentReference")) {
                Iterator<JsonNode> iterator = actualObj.get(
                        "pproc:additionalDocumentReference").elements();
                while (iterator.hasNext()) {
                    Anuncio pliego = new Anuncio();
                    JsonNode valor = iterator.next();
                    pliego.setTitle(valor.get("dcterms:title").asText());
                    pliego.setFileName(Utils.normalizar(valor.get("name")
                            .asText()));
                    String fileName = pliego.getFileName();
                  /*  CustomMultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);*/

                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio.setId(Utils.TIPO_ANUNCIO_OTRAS_INFORMACIONES);
                    pliego.setType(tipoAnuncio);
                    pliego.setVisible("S");
                    pliego.setContrato(con);
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    pliego.setAdjunto(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")));
                    //pliego.setAdjunto(customMultipartFile.getBytes());
                    daoAnuncio.almacenar(pliego);
                }
            }

            if (actualObj.has("pproc:technicalDocumentReference")) {
                Iterator<JsonNode> iterator = actualObj.get(
                        "pproc:technicalDocumentReference").elements();
                while (iterator.hasNext()) {
                    Anuncio pliego = new Anuncio();
                    JsonNode valor = iterator.next();
                    pliego.setTitle(valor.get("dcterms:title").asText());
                    pliego.setFileName(Utils.normalizar(valor.get("name")
                            .asText()));
                    String fileName = pliego.getFileName();

                  /*  MultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);*/
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio
                            .setId(Utils.TIPO_ANUNCIO_PLIEGOS_TECNICOS);
                    pliego.setType(tipoAnuncio);
                    pliego.setContrato(con);
                    pliego.setVisible("S");
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    pliego.setAdjunto(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")));
                   // pliego.setAdjunto(customMultipartFile.getBytes());
                    daoAnuncio.almacenar(pliego);
                }
            }

            // Estamos en licitación
            if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:contractEconomicConditions")
                    && actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
                Iterator<JsonNode> iterator = actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").elements();
                while (iterator.hasNext()) {
                    JsonNode valor = iterator.next();
                    boolean conIva = valor.get("gr:valueAddedTaxIncluded").asBoolean();
                    if (conIva) {
                        con.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                    } else {
                        con.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                    }
                }

            }
            if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:mainObject")) {
                if (!actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().contains("null")) {
                    String[] cpvArray = actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().split("-");
                    Cpv cpv = new Cpv(cpvArray[1]);
                    con.getCpv().add(cpv);
                }
            }
            String anuncioLicitacion = UtilsContrato.obtenerLiTituloContrato(actualObj)
                    + UtilsContrato.obtenerLiTipoContrato(con.getType())
                    + UtilsContrato.obtenerLiGestor(actualObj)
                    + UtilsContrato.obtenerLiOrganoContratacion(actualObj)
                    + UtilsContrato.obtenerLiExpediente(actualObj)
                    + UtilsContrato.obtenerLiObjeto(actualObj)
                    + UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
                    + UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
                    + UtilsContrato.obtenerLiUrgencia(con.getUrgente())
                    + UtilsContrato.obtenerLiPresupuesto(actualObj)
                    + UtilsContrato
                    .obtenerLiPlazoPresentacionOfertas(actualObj)
                    + UtilsContrato
                    .obtenerLiInformacionAdicional(actualObj);
            if (anuncioLicitacion.length() > 0) {
                Anuncio licitacion = new Anuncio();
                licitacion.setTitle("Anuncio de Licitación");
                licitacion.setDescription("<ol>" + anuncioLicitacion
                        + "</ol>");
                Tipoanuncio tipoAnuncio = new Tipoanuncio();
                tipoAnuncio.setId(UtilsContrato.TIPO_ANUNCIO_LICITACION);
                licitacion.setType(tipoAnuncio);
                licitacion.setVisible("S");
                licitacion.setUsuarioAlta(peticion.getClientId());
                licitacion.setCreationDate(new Date());
                licitacion.setLastUpdated(new Date());
                licitacion.setPubDate(new Date());
                con.getAnuncios().add(licitacion);
            }
            if (actualObj.has("pproc:contractProcedureSpecifications")) {
                JsonNode especificaciones = actualObj
                        .get("pproc:contractProcedureSpecifications");
                if (especificaciones.has("pproc:tenderDeadline")) {
                    Date fecha = ConvertDate.string2Date(especificaciones
                                    .get("pproc:tenderDeadline").asText(),
                            ConvertDate.ISO8601_FORMAT_SIN_ZONA);

                    Anuncio presentacion = new Anuncio();
                    presentacion.setTitle(ConvertDate.date2String(fecha,
                            ConvertDate.PERFILCONTRATANTE));
                    con.setFechaPresentacion(fecha);
                    con.setFechaContrato(fecha);
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio.setId(UtilsContrato.TIPO_ANUNCIO_FECHA_PRESENTACION);
                    presentacion.setType(tipoAnuncio);
                    presentacion.setVisible("S");
                    presentacion.setUsuarioAlta(peticion.getClientId());
                    presentacion.setCreationDate(new Date());
                    presentacion.setLastUpdated(new Date());
                    presentacion.setPubDate(new Date());
                    con.getAnuncios().add(presentacion);
                }
            }
            if (actualObj.has("pc:awardCriteriaCombination")) {
                JsonNode criterios = actualObj.get(
                        "pc:awardCriteriaCombination");
                if (criterios.has("pc:awardCriterion")) {
                    Iterator<JsonNode> iterator = criterios.get(
                            "pc:awardCriterion").elements();
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        Criterio criterio = new Criterio();
                        if (valor.has("@type")) {
                            String tipo = valor.get("@type").asText();
                            if ("pproc:ObjectiveAwardCriterion".contains(tipo)) {
                                TipoCriterio tipoCriterio = new TipoCriterio();
                                tipoCriterio.setId(new BigDecimal(1));
                                criterio.setTipo(tipoCriterio);
                            } else if ("pproc:SubjectiveAwardCriterion".contains(tipo)) {
                                TipoCriterio tipoCriterio = new TipoCriterio();
                                tipoCriterio.setId(new BigDecimal(2));
                                criterio.setTipo(tipoCriterio);
                            }
                        }
                        if (valor.has("pc:criterionName")) {
                            criterio.setTitle(valor.get("pc:criterionName").asText());
                        }
                        if (valor.has("pproc:criterionEvaluationMode")) {
                            criterio.setDescription(valor
                                    .get("pproc:criterionEvaluationMode")
                                    .asText());

                        }
                        if (valor.has("pc:criterionWeight")) {
                            criterio.setPeso(new BigDecimal(valor.get("pc:criterionWeight").asText()));
                        }
                        con.getCriterios().add(criterio);
                    }
                }
            }
            if (actualObj.has("pproc:contractObject")
                    && actualObj.get("pproc:contractObject").has(
                    "pproc:contractEconomicConditions")
                    && actualObj.get("pproc:contractObject")
                    .get("pproc:contractEconomicConditions")
                    .has("pproc:budgetPrice")) {
                Iterator<JsonNode> iterator = actualObj
                        .get("pproc:contractObject")
                        .get("pproc:contractEconomicConditions")
                        .get("pproc:budgetPrice").elements();
                while (iterator.hasNext()) {
                    JsonNode valor = iterator.next();
                    boolean conIva = valor.get("gr:valueAddedTaxIncluded")
                            .asBoolean();
                    if (conIva) {
                        Anuncio presupuesto = new Anuncio();
                        presupuesto
                                .setTitle((valor.get("gr:hasCurrencyValue")
                                        .asText()
                                        + ((valor.has("gr:hasCurrency") && valor
                                        .get("gr:hasCurrency")
                                        .asText().indexOf("EUR") >= 0) ? " euros"
                                        : " "
                                        + valor.get(
                                        "gr:hasCurrency")
                                        .asText()) + " I.V.A. incluido"));
                        Tipoanuncio tipoAnuncio = new Tipoanuncio();
                        tipoAnuncio
                                .setId(UtilsContrato.TIPO_ANUNCIO_PRESUPUESTO);
                        presupuesto.setType(tipoAnuncio);
                        presupuesto.setVisible("S");
                        presupuesto.setUsuarioAlta(peticion.getClientId());
                        presupuesto.setCreationDate(new Date());
                        presupuesto.setLastUpdated(new Date());
                        presupuesto.setPubDate(new Date());
                        con.getAnuncios().add(presupuesto);
                    }
                }
            }

            Set<Criterio> criterios = new HashSet<Criterio>();
            List<Anuncio> anuncios = new ArrayList<Anuncio>();
            anuncios = con.getAnuncios();
            criterios = con.getCriterios();
            con.setAnuncios(null);
            con.setCriterios(null);
            dao.save(con);
            dao.flush();
            for (Anuncio anun : anuncios) {
                anun.setContrato(con);
                daoAnuncio.almacenar(anun);
            }
            for (Criterio criterio : criterios) {
                criterio.setContrato(con);
                daoCriterios.save(criterio);
            }
            daoCriterios.flush();

            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public Oferta rellenarOferta(Contrato con, JsonNode objc, Peticion peticion, boolean ganador, String idLote) throws Exception {

        Oferta ofer = new Oferta();
        try {
            JsonNode actualObj = objc;
            if (actualObj != null) {
                if (actualObj.has("@type") && actualObj.get("@type").isArray()) {
                    Iterator<JsonNode> iterator = actualObj.get("@type").elements();
                    while (iterator.hasNext()) {
                        String valor = iterator.next().asText();
                        ofer.setExcluida(false);
                        if ("pproc:AcceptedTender".equals(valor)) {
                            ofer.setExcluida(false);
                        }
                        if ("pproc:ExcludedTender".equals(valor)) {
                            ofer.setExcluida(true);
                        }
                    }
                }
                if (actualObj.has("pproc:awardDate")) {
                    ofer.setFechaAdjudicacion(ConvertDate.string2Date(actualObj.get("pproc:awardDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
                    ofer.setGanador(true);
                } else {
                    ofer.setGanador(false);
                }
                if (actualObj.has("pproc:formalizedDate")) {
                    if(ofer.getFechaAdjudicacion()==null) {
                        ofer.setFechaFormalizacion(ConvertDate.string2Date(actualObj.get("pproc:formalizedDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
                        ofer.setFechaAdjudicacion(ConvertDate.string2Date(actualObj.get("pproc:formalizedDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
                        ofer.setGanador(true);
                    }
                }
                ofer.setCanon(false);
                ofer.setAhorroVisible(true);
                if (actualObj.has("pc:supplier")) {
                    if (actualObj.get("pc:supplier").has("org:identifier")) {
                        String nifActual = actualObj.get("pc:supplier").get("org:identifier").asText();
                        if (con.getOfertas().size()>0) {
                            for (Oferta oferta : con.getOfertas()) {
                                if (!oferta.getEmpresa().getNif().equals(nifActual)) {
                                    Search search = new Search();
                                    search.addFilterEqual("nif", actualObj.get("pc:supplier").get("org:identifier").asText());
                                    Empresa licitador;
                                    try {
                                        licitador = daoEmpresa.searchUnique(search);
                                    } catch (Exception e) {
                                        licitador = null;
                                    }
                                    if (licitador != null) {
                                        ofer.setEmpresa(licitador);
                                    } else {
                                        Empresa licitador2 = new Empresa();
                                        licitador2.setEsUte(false);
                                        licitador2.setNacionalidad("es");
                                        if (actualObj.get("pc:supplier").has("s:name")) {
                                            licitador2.setNombre(actualObj.get("pc:supplier").get("s:name").asText());
                                        }
                                        if (actualObj.get("pc:supplier").has("org:identifier")) {
                                            licitador2.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());
                                            String nif = licitador2.getNif();
                                            if (Oferta.anoniCierto(nif)) {
                                                licitador2.setAutonomo("S");
                                            } else {
                                                licitador2.setAutonomo("N");
                                            }
                                        }
                                        daoEmpresa.save(licitador2);
                                        daoEmpresa.flush();
                                        ofer.setEmpresa(licitador2);
                                    }
                                }
                            }
                        } else {
                            Search search = new Search();
                            search.addFilterEqual("nif", actualObj.get("pc:supplier").get("org:identifier").asText());
                            Empresa licitador;
                            try {
                                licitador = daoEmpresa.searchUnique(search);
                            } catch (Exception e) {
                                licitador = null;
                            }
                            if (licitador != null) {
                                ofer.setEmpresa(licitador);
                            } else {
                                Empresa licitador2 = new Empresa();
                                licitador2.setEsUte(false);
                                licitador2.setNacionalidad("es");
                                if (actualObj.get("pc:supplier").has("s:name")) {
                                    licitador2.setNombre(actualObj.get("pc:supplier").get("s:name").asText());
                                }
                                if (actualObj.get("pc:supplier").has("org:identifier")) {
                                    licitador2.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());
                                    String nif = licitador2.getNif();
                                    if (Oferta.anoniCierto(nif)) {
                                        licitador2.setAutonomo("S");
                                    } else {
                                        licitador2.setAutonomo("N");
                                    }
                                    Search busqueda=new Search();
                                    if(Character.isAlphabetic(nif.charAt(0))) {
                                        busqueda.addFilterEqual("skos", "http://vocab.linkeddata.es/datosabiertos/kos/sector-publico/organizacion/clave-nif-forma-juridica/" + nif.charAt(0));
                                        TipoEmpresa tipo = daoTipoEmpresa.searchUnique(busqueda);
                                        licitador2.setTipoEmpresa(tipo);
                                    }else{
                                        licitador2.setTipoEmpresa(null);
                                    }
                                }
                                daoEmpresa.save(licitador2);
                                daoEmpresa.flush();
                                ofer.setEmpresa(licitador2);
                            }
                        }
                    }
                }


                if (actualObj.has("pc:offeredPrice")) {
                    Iterator<JsonNode> iterator = actualObj.get("pc:offeredPrice").elements();
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        String conIva = valor.get("gr:valueAddedTaxIncluded").asText();
                        if ("true".equals(conIva)) {
                            ofer.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText().contains(" ") ? valor.get("gr:hasCurrencyValue").asText().substring(0, valor.get("gr:hasCurrencyValue").asText().indexOf(" ")) : valor.get("gr:hasCurrencyValue").asText())));
                        } else {
                            if("0".equals(valor.get("gr:hasCurrencyValue").asText())) {
                                ofer.setImporteSinIVA(new BigDecimal(1.0));
                            }else{
                                ofer.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText().contains(" ") ? valor.get("gr:hasCurrencyValue").asText().substring(0, valor.get("gr:hasCurrencyValue").asText().indexOf(" ")) : valor.get("gr:hasCurrencyValue").asText())));
                            }
                        }
                    }
                }

        }
                if ( ofer.getGanador()==true && ofer.getImporteSinIVA().equals(new BigDecimal(1.0))){
                    ofer.setImporteSinIVA(ofer.getImporteConIVA());
                }
            return ofer;
} catch(Exception e){
        e.printStackTrace();
        return null;
        }
        }
        }
