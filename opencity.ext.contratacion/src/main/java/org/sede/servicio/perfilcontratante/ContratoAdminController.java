package org.sede.servicio.perfilcontratante;


import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import org.sede.core.anotaciones.*;
import org.sede.core.dao.EntidadBase;
import org.sede.core.dao.SearchFiql;
import org.sede.core.rest.Mensaje;
import org.sede.core.rest.MimeTypes;
import org.sede.core.utils.ConvertDate;
import org.sede.core.utils.Funciones;
import org.sede.servicio.ModelAttr;

import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.dao.*;
import org.sede.servicio.perfilcontratante.entity.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.propertyeditors.CustomCollectionEditor;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.validation.ConstraintViolation;
import java.io.*;
import java.math.BigDecimal;

import java.text.ParseException;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;


@Gcz(servicio = "PERFILCONTRATANTE", seccion = "CONTRATO")
@Controller
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ContratoAdminController.MAPPING, method = RequestMethod.GET)
public class ContratoAdminController {
    //region Atributtes
    private static final Logger logger = LoggerFactory.getLogger(ContratoAdminController.class);
    private static final String RAIZ = "contratacion-publica/";
    private static final String SERVICIO = "admin";
    public static final String MAPPING = "servicio/" + RAIZ + SERVICIO;
    private static final String MAPPING_FORM = MAPPING + "/formulario";
    private static final String MAPPING_ANUNCIO_FORM = MAPPING + "/anuncio";
    private static final String MAPPING_OFERTA_FORM = MAPPING + "/oferta";
    private static final String MAPPING_LOTE_FORM = MAPPING + "/lote";
    private static final String MAPPING_FUNCIONALIDADES = MAPPING + "/funciones/";

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
    private ContratoController contratoController;
    @Autowired
    private CpvGenericDAO daoCpv;
    @Autowired
    private EmpresaGenericDAO daoEmpresa;
    //endregion


    @ResponseClass(value = Contrato.class, entity = SearchResult.class)
    @RequestMapping(method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public
    @ResponseBody
    ResponseEntity<?> apiListar(@Fiql SearchFiql search) throws org.apache.cxf.jaxrs.ext.search.SearchParseException {
        Search busqueda = search.getConditions(Contrato.class);
        SearchResult<Contrato> resultado = null;
        List<Sort> sorts = new ArrayList<Sort>();
        BigDecimal portal = Utils.obtenerPortal(Funciones.getPeticion());
        BigDecimal entidad = Utils.obtenerEntidad(Funciones.getPeticion());
        if (portal != null) {
            busqueda.addFilterEqual("entity.id", portal);
        }
        if (entidad != null) {
            busqueda.addFilterEqual("servicio.id", entidad);
        }
        sorts.add(new Sort("creationDate", true));
        busqueda.setSorts(sorts);
        resultado = dao.searchAndCount(busqueda);
        return ResponseEntity.ok(resultado);
    }

    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String redirect() {
        return "redirect:" + SERVICIO + "/";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String home(Model model, @Fiql SearchFiql search) throws org.apache.cxf.jaxrs.ext.search.SearchParseException {
        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
        return MAPPING + "/index";
    }

    @Permisos(Permisos.MOD)
    @RequestMapping(path = "/tramita", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String tramita(Model model, @RequestParam(name = "carpeta", required = false, defaultValue = "") String carpeta) throws org.apache.cxf.jaxrs.ext.search.SearchParseException, IOException {

        if (!carpeta.isEmpty()) {

            model.addAttribute(ModelAttr.RESULTADO, apiTramitaJson(carpeta));
        } else {

            model.addAttribute(ModelAttr.RESULTADO, null);
        }
        return MAPPING + "/tramita";
    }

    @Permisos(Permisos.MOD)
    private @ResponseBody
    SearchResult<Contrato> apiTramitaJson(String carpeta) throws SearchParseException, IOException {
        SearchResult<Contrato> resultado = new SearchResult<Contrato>();
        try {

            List<Contrato> lista = new ArrayList<Contrato>(0);
            resultado.setResult(lista);
            String intDir = "/home/documentacionweb/JsonTramita/" + carpeta + "/";
            File interfaceDirectory = new File(intDir);
            for (File file : interfaceDirectory.listFiles()) {
                InputStream stream = new FileInputStream(file);
                String datos = IOUtils.toString(stream);
                System.out.println(datos);
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
                Contrato conJson = parseadorContrato(actualObj, expediente, con.getId());
                /*EntidadBase.combinar(con,conJson);
                Set<ConstraintViolation<Object>> errores = dao.validar(con);
                if (!errores.isEmpty()) {
                  logger.error(errores.toString());
                }*/
                lista.add(conJson);
            }
            resultado.setRows(0);
            resultado.setTotalCount(lista.size());
            resultado.setResult(lista);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
        return resultado;
    }

    public Contrato parseadorContrato(JsonNode json, String expediente, BigDecimal id) {

        Contrato contrato = new Contrato();
        contrato.setId(id);
        contrato.setExpediente(expediente);
        try {
            JsonNode actualObjLote;
            boolean lotesBolean = false;
            ObjectMapper mapper = new ObjectMapper();
            mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
            JsonNode actualObj = json;
            if (actualObj.has("dcterms:title")) {
                contrato.setTitle(actualObj.get("dcterms:title").asText());
            }
            contrato.setExpediente(actualObj.get("dcterms:identifier").asText());
            contrato.setType(daoTipoContrato.obtenerTipo(actualObj));
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
                        contrato.setDuracion(BigDecimal.valueOf(Double.valueOf(plazoEjecucion)));
                    } catch (IllegalArgumentException e) {
                        contrato.setDuracion(BigDecimal.valueOf(temporal.get("pproc:estimatedDuration").doubleValue()));
                    }
                }
            }
            if (actualObj.has("pproc:managingDepartment")) {
                JsonNode gestor = actualObj.get("pproc:managingDepartment");
                String idEntidad = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
                contrato.setServicio(daoOrganigrama.find(BigDecimal.valueOf(Double.valueOf(idEntidad))));
            }
            if (actualObj.has("pproc:contractingBody")) {
                JsonNode gestor = actualObj.get("pproc:contractingBody");
                String idOrgano = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
                EstructuraOrganizativa organo = daoOrganigrama.find(BigDecimal.valueOf(Double.valueOf(idOrgano)));
                contrato.setOrganoContratante(organo);
            }
            if (actualObj.has("pproc:contractProcedureSpecifications")) {
                JsonNode procedimiento = actualObj.get("pproc:contractProcedureSpecifications");
                String retorno = "N";
                if (procedimiento.has("pproc:procedureType")
                        && procedimiento.get("pproc:procedureType").asText().indexOf("Minor") >= 0) {
                    retorno = "S";
                }
                contrato.setContratoMenor("S".equals(retorno) ? true : false);
                if ("S".equals(retorno)) {
                    Procedimiento proce = new Procedimiento();
                    proce.setId(new BigDecimal(10));
                    contrato.setProcedimiento(proce);
                } else {
                    JsonNode procedure = actualObj.get("pproc:contractProcedureSpecifications");
                    if (procedure.has("pproc:procedureType")) {
                        Procedimiento procede = new Procedimiento();
                        String procedureType = procedure.get("pproc:procedureType").asText();
                        if (procedureType.indexOf("pproc:RegularOpen") >= 0) {
                            procede.setId(new BigDecimal(1.0));
                            contrato.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:Negotiated") >= 0) {
                            procede.setId(new BigDecimal(7.0));
                            contrato.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:Restricted") >= 0) {
                            procede.setId(new BigDecimal(2.0));
                            contrato.setProcedimiento(procede);
                        } else if (procedureType.indexOf("pproc:SimpleOpen") >= 0) {
                            procede.setId(new BigDecimal(8.0));
                            contrato.setProcedimiento(procede);
                        }
                    }
                }
                if (actualObj.has("pproc:urgencyType")) {
                    String urgencia = actualObj.get("pproc:urgencyType").asText();
                    if ("pproc:Express".equals(urgencia)) {
                        contrato.setUrgente("S");

                    } else {
                        contrato.setUrgente("N");
                    }
                }
            }

			/*if (actualObj.has("pproc:legalDocumentReference")) {

				Iterator<JsonNode> iterator = actualObj.get("pproc:legalDocumentReference").elements();
				while (iterator.hasNext()) {
					JsonNode valor = iterator.next();
					Anuncio pliego = new Anuncio();
					pliego.setTitle(valor.get("dcterms:title").asText());
					pliego.setFileName(normalizar(valor.get("name")
							.asText()));
					pliego.setDescription((URLDecoder.decode(valor.get("body").asText(), "UTF-8")));

					Tipoanuncio tipoAnuncio = new Tipoanuncio();
					tipoAnuncio.setId(new BigDecimal(4.0));
					pliego.setType(tipoAnuncio);
					pliego.setVisible("S");

					pliego.setUsuarioAlta(Funciones.getPeticion().getCredenciales().getUsuario().getNombre());
					pliego.setCreationDate(new Date());
					pliego.setLastUpdated(new Date());
					pliego.setPubDate(new Date());
					contrato.getAnuncios().add(pliego);
				}
			}*/

			/*if (actualObj.has("pproc:additionalDocumentReference")) {
				Iterator<JsonNode> iterator = actualObj.get(
						"pproc:additionalDocumentReference").elements();
				while (iterator.hasNext()) {
					Anuncio pliego = new Anuncio();
					JsonNode valor = iterator.next();
					pliego.setTitle(valor.get("dcterms:title").asText());
					pliego.setFileName(normalizar(valor.get("name")
							.asText()));
					pliego.setDescription(URLDecoder.decode(valor.get("body").asText(), "UTF-8"));

					Tipoanuncio tipoAnuncio = new Tipoanuncio();
					tipoAnuncio.setId(new BigDecimal(9.0));
					pliego.setType(tipoAnuncio);
					pliego.setVisible("S");
					pliego.setUsuarioAlta(Funciones.getPeticion().getCredenciales().getUsuario().getNombre().getClientId());
					pliego.setCreationDate(new Date());
					pliego.setLastUpdated(new Date());
					pliego.setPubDate(new Date());
					contrato.getAnuncios().add(pliego);
				}
			}

			if (actualObj.has("pproc:technicalDocumentReference")) {
				Iterator<JsonNode> iterator = actualObj.get(
						"pproc:technicalDocumentReference").elements();
				while (iterator.hasNext()) {
					Anuncio pliego = new Anuncio();
					JsonNode valor = iterator.next();
					pliego.setTitle(valor.get("dcterms:title").asText());
					pliego.setFileName(normalizar(valor.get("name")
							.asText()));
					pliego.setDescription(URLDecoder.decode(valor.get("body").asText(), "UTF-8"));

					Tipoanuncio tipoAnuncio = new Tipoanuncio();
					tipoAnuncio
							.setId(new BigDecimal(3.0));
					pliego.setType(tipoAnuncio);
					pliego.setVisible("S");
					pliego.setUsuarioAlta(Funciones.getPeticion().getCredenciales().getUsuario().getNombre());
					pliego.setCreationDate(new Date());
					pliego.setLastUpdated(new Date());
					pliego.setPubDate(new Date());
					contrato.getAnuncios().add(pliego);
				}
			}*/
			/*if(actualObj.has("pc:lot")){
				Iterator<JsonNode> iteratorlote=actualObj.get("pc:lot").elements();
				while (iteratorlote.hasNext()) {
					actualObjLote=iteratorlote.next();
					lotesBolean=actualObjLote.has("pc:tender");
				}
			}*/

            if (actualObj.has("pc:tender") == false && lotesBolean == false) {

                // Estamos en licitaci�n
                if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:contractEconomicConditions")
                        && actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
                    Iterator<JsonNode> iterator = actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").elements();
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        boolean conIva = valor.get("gr:valueAddedTaxIncluded").asBoolean();
                        if (conIva) {
                            contrato.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                        } else {
                            contrato.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                        }
                    }

                }
                if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:mainObject")) {
                    if (!actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().contains("null")) {
                        String[] cpvArray = actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().split("-");
                        if (cpvArray.length >= 2) {
                            Cpv cpv = new Cpv();
                            cpv.setId(BigDecimal.valueOf(Double.valueOf(cpvArray[1])));
                            contrato.getCpv().add(cpv);
                        }
                    }
                }
                /*Zona de Lotes*/
				/*if (actualObj.has("pc:lot")){
					Iterator<JsonNode> iterator = actualObj.get("pc:lot").elements();
					this.setTieneLote("S");
					while (iterator.hasNext()) {
						Lote lote=new Lote();
						lote.setStatus(0);
						JsonNode valor = iterator.next();
						String idLote=valor.get("dcterms:identifier").asText().replace("contzar:", "");
						// lote.setIdTramita(idLote);
						lote.setDescription( valor.get("dcterms:title").asText());
						if (valor.has("pproc:contractObject") && valor.get("pproc:contractObject").has("pproc:contractEconomicConditions")
								&& valor.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
							Iterator<JsonNode> iterator2 = valor.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").elements();
							while (iterator2.hasNext()) {
								JsonNode valor2 = iterator2.next();
								boolean conIva = valor2.get("gr:valueAddedTaxIncluded").asBoolean();
								if (conIva) {
									lote.setImporteLicitacionConIVA((BigDecimal.valueOf(Double.valueOf( valor2.get("gr:hasCurrencyValue").asText()))));
								} else 	{
									lote.setImporteLicitacionSinIVA(BigDecimal.valueOf(Double.valueOf( valor2.get("gr:hasCurrencyValue").asText())));
								}
							}

						}
						this.getLotes().add(lote);
					}
				}*/
                Estado status = new Estado();
                status.setId(0);
                contrato.setStatus(status);
                EntidadContratante contratante = new EntidadContratante();
                contratante.setId(new BigDecimal(Contrato.obtenerPortal(Funciones.getPeticion())));
                contrato.setEntity(contratante);


				/*String anuncioLicitacion = UtilsContrato
						.obtenerLiTituloContrato(actualObj)
						+ UtilsContrato.obtenerLiTipoContrato(this.getType())
						+ UtilsContrato.obtenerLiGestor(actualObj)
						+ UtilsContrato.obtenerLiOrganoContratacion(actualObj)
						+ UtilsContrato.obtenerLiExpediente(actualObj)
						+ UtilsContrato.obtenerLiObjeto(actualObj)
						+ UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
						+ UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
						+UtilsContrato.obtenerLiUrgencia(this.getUrgente())
						+ UtilsContrato.obtenerLiPresupuesto(actualObj)
						+ UtilsContrato
						.obtenerLiPlazoPresentacionOfertas(actualObj)
						+ UtilsContrato
						.obtenerLiInformacionAdicional(actualObj);

				if (anuncioLicitacion.length() > 0) {
					Anuncio licitacion = new Anuncio();
					licitacion.setTitle("Anuncio de Licitaci�n");
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
					this.getAnuncios().add(licitacion);
				}*/

			/*	if (actualObj.has("pproc:contractProcedureSpecifications")) {
					JsonNode especificaciones = actualObj
							.get("pproc:contractProcedureSpecifications");
					if (especificaciones.has("pproc:tenderDeadline")) {
						Date fecha = ConvertDate.string2Date(especificaciones
								.get("pproc:tenderDeadline").asText(),ConvertDate.ISO8601_FORMAT_SIN_ZONA);

						Date fechapress=ConvertDate.string2Date(ConvertDate.date2String(fecha,ConvertDate.DATETIME_FORMAT),ConvertDate.DATETIME_FORMAT);
						contrato.setFechaPresentacion(new Date());
						contrato.setFechaContrato(fechapress);
						Anuncio presentacion = new Anuncio();
						presentacion.setTitle(ConvertDate.date2String(fecha,
								ConvertDate.PERFILCONTRATANTE));

						Tipoanuncio tipoAnuncio = new Tipoanuncio();
						tipoAnuncio
								.setId(new BigDecimal(10.0));
						presentacion.setType(tipoAnuncio);
						presentacion.setVisible("S");
						presentacion.setUsuarioAlta(Funciones.getPeticion().getCredenciales().getUsuario().getNombre().getClientId());
						presentacion.setCreationDate(new Date());
						presentacion.setLastUpdated(new Date());
						presentacion.setPubDate(new Date());

						contrato.getAnuncios().add(presentacion);

					}
				}*/
                if (actualObj.has("pc:awardCriteriaCombination")) {
                    JsonNode criterios = actualObj.get(
                            "pc:awardCriteriaCombination");
                    if (criterios.has("pc:awardCriterion")) {
                        Iterator<JsonNode> iterator = criterios.get(
                                "pc:awardCriterion").elements();

                        while (iterator.hasNext()) {
                            JsonNode valor = iterator.next();
                            org.sede.servicio.perfilcontratante.entity.Criterio criterio = new Criterio();
                            if (valor.has("@type")) {
                                String tipo = valor.get("@type").asText();
                                if ("pproc:ObjectiveAwardCriterion".contains(tipo)) {
                                    TipoCriterio tipoCriterio = new TipoCriterio();
                                    tipoCriterio.setId(new BigDecimal(1));
                                    contrato.getCriterios().add(criterio);
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
                            contrato.getCriterios().add(criterio);

                        }
                    }
                }
				/*if (actualObj.has("pproc:contractObject")
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
							tipoAnuncio.setId(new BigDecimal(11.0));
							presupuesto.setType(tipoAnuncio);
							presupuesto.setVisible("S");
							presupuesto.setUsuarioAlta(Funciones.getPeticion().getCredenciales().getUsuario().getNombre());
							presupuesto.setCreationDate(new Date());
							presupuesto.setLastUpdated(new Date());
							presupuesto.setPubDate(new Date());
							contrato.getAnuncios().add(presupuesto);
						}
					}
				}*/

            } else {
                // Estamos en la adjudicación
                if (actualObj.has("pc:awardCriteriaCombination")) {
                    JsonNode criterios = actualObj.get(
                            "pc:awardCriteriaCombination");
                    if (criterios.has("pc:awardCriterion")) {
                        Iterator<JsonNode> iterator = criterios.get(
                                "pc:awardCriterion").elements();

                        while (iterator.hasNext()) {
                            JsonNode valor = iterator.next();
                            org.sede.servicio.perfilcontratante.entity.Criterio criterio = new Criterio();
                            if (valor.has("@type")) {
                                String tipo = valor.get("@type").asText();
                                if ("pproc:ObjectiveAwardCriterion".contains(tipo)) {
                                    TipoCriterio tipoCriterio = new TipoCriterio();
                                    tipoCriterio.setId(new BigDecimal(1));
                                    contrato.getCriterios().add(criterio);
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
                            contrato.getCriterios().add(criterio);

                        }
                    }
                }
                if (actualObj.has("tbfy:numberOfTenderers") == true) {
                    contrato.setNumLicitadores(actualObj.get("tbfy:numberOfTenderers").intValue());
                }
                if (actualObj.has("pc:tender")) {
                    JsonNode jsonNode = actualObj.get("pc:tender");
                    if (jsonNode.isArray()) {
                        Iterator<JsonNode> iterator = jsonNode.elements();
                        while (iterator.hasNext()) {
                            JsonNode valor = iterator.next();
                            if (valor.has("@type")) {
                                JsonNode jsonNode1 = valor.get("@type");
                                if (jsonNode1.isArray()) {
                                    try {
                                        Oferta ofertaGanadora = Utils.crearOferta(valor, true, daoEmpresa);
                                        contrato.getOfertas().add(ofertaGanadora);
                                    } catch (Exception e) {
                                        logger.error(e.getMessage());
                                    }

                                } else {
                                    Oferta oferta = Utils.crearOferta(valor, false, daoEmpresa);
                                    contrato.getOfertas().add(oferta);
                                }

                            }

                        }
                    }
				/*if(actualObj.has("pc:lot")){
					Iterator<JsonNode> iteratorlote=actualObj.get("pc:lot").elements();
					while (iteratorlote.hasNext()) {
						JsonNode loteAdjudicacion=iteratorlote.next();
						String idLote=loteAdjudicacion.get("dcterms:identifier").asText().replace("contzar:", "");
						if(loteAdjudicacion.has("pc:tender")){
							JsonNode jsonNode=loteAdjudicacion.get("pc:tender");
							if (jsonNode.isArray()) {
								Iterator<JsonNode> iterator = jsonNode.elements();
								while (iterator.hasNext()) {
									JsonNode valor = iterator.next();
									if (valor.has("pproc:awardDate")) {
										Oferta ofertaGanadora= new Oferta(valor,peticion,true,idLote);
										this.getOfertas().add(ofertaGanadora);
										this.setFechaContrato(ofertaGanadora.getFechaAdjudicacion());
									}else{
										Oferta ofertaGanadora= new Oferta(valor,peticion,false,idLote);
										this.getOfertas().add(ofertaGanadora);
									}
								}
							}
						}


					}
				}*/
                    Estado status = new Estado();
                    status.setId(5);
                    contrato.setStatus(status);

                    contrato.setEntity(new EntidadContratante(new BigDecimal(Contrato.obtenerPortal(Funciones.getPeticion()))));
				/*String anuncioAdjudicacion = UtilsContrato
						.obtenerLiTituloContrato(actualObj)
						+ UtilsContrato.obtenerLiTipoContrato(this.getType())
						+ UtilsContrato.obtenerLiGestor(actualObj)
						+ UtilsContrato.obtenerLiOrganoContratacion(actualObj)
						+ UtilsContrato.obtenerLiExpediente(actualObj)
						+ UtilsContrato.obtenerLiObjeto(actualObj)
						+ UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
						+ UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
						+ UtilsContrato.obtenerLiAdjudicacion(actualObj);
				if (anuncioAdjudicacion.length() > 0) {
					Anuncio adjudicacion = new Anuncio();
					adjudicacion.setTitle("Adjudicaci�n");
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
					this.getAnuncios().add(adjudicacion);
				}*/
                }
            }
            return contrato;

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

        }
        return null;

    }


    @Permisos(Permisos.NEW)
    @RequestMapping(path = "/oferta/carga", headers = ("content-type=multipart/*"), method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String cargaOfertas(Model model, @RequestParam("file") MultipartFile file, RedirectAttributes attr) throws SearchParseException {
        if (!file.isEmpty()) {
            try {
                String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'), file.getOriginalFilename().length());
                if (extension.equals(".xls")) {
                    List<Oferta> ofer = daoOferta.loadFromXls(file.getInputStream());
                    if (ofer != null) {
                        for (int i = 0; i < ofer.size(); i++) {
                            Oferta item = ofer.get(i);
                            ResponseEntity<?> respuesta = apiCrearOferta(item.getContrato().getId(), item);
                            if (respuesta.getStatusCode().is2xxSuccessful()) {
                                model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Ofertas creadas correctamente"));
                            }
                        }
                        SearchFiql search = new SearchFiql();
                        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));

                        return "redirect:/" + MAPPING + "/";
                    } else {
                        model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Error en carga de ofertas."));
                        SearchFiql search = new SearchFiql();
                        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                        return "redirect:/" + MAPPING + "/";
                    }
                } else {
                    model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Fichero no soportado."));
                    SearchFiql search = new SearchFiql();
                    model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                    return "redirect:/" + MAPPING + "/index";
                }
            } catch (IOException e) {
                model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Error en la carga fichero."));
                SearchFiql search = new SearchFiql();
                model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                return "redirect:/" + MAPPING + "/index";
            }
        } else {
            model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Fichero no soportado."));
            SearchFiql search = new SearchFiql();
            model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
            return "redirect:/" + MAPPING + "/index";
        }

    }


    @RequestMapping(method = RequestMethod.POST, consumes = {MimeTypes.JSON,
            MimeTypes.XML}, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.NEW)
    @Description("Crear registro")
    @ResponseClass(value = Contrato.class)
    public
    @ResponseBody
    ResponseEntity<?> apiCrear(
            @RequestBody Contrato registro) {
        Set<ConstraintViolation<Object>> errores = dao.validar(registro);
        if (!errores.isEmpty()) {
            return Funciones.generarMensajeError(errores);
        }
        registro.setVisible("S");
        registro.setCreationDate(new Date());
        registro.setPubDate(new Date());
        //registro.setFechaContrato(registro.getCreationDate());
        if ("10".equals(registro.getProcedimiento().getId().toString())) {
            registro.setContratoMenor(true);
        } else {
            registro.setContratoMenor(false);
        }
        dao.save(registro);
        return ResponseEntity.ok(registro);
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.MOD)
    @Description("Modificar registro")
    @ResponseClass(value = Contrato.class)
    public
    @ResponseBody
    ResponseEntity<?> apiModificar(@PathVariable BigDecimal id, @RequestBody Contrato registro) {
        ResponseEntity<?> resp = contratoController.apiDetalle(id);
        if (resp.getStatusCode().is2xxSuccessful()) {
            Contrato reg = (Contrato) resp.getBody();
            BigDecimal entidad = Utils.obtenerEntidad(Funciones.getPeticion());
            if (entidad != null && !reg.getServicio().getId().equals(entidad)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), "No puede editar el contrato"));
            } else {
                EntidadBase.combinar(reg, registro);
                reg.setId(id);
                reg.setUsuarioMod(Funciones.getPeticion().getClientId());
                reg.setLastUpdated(new Date());
                Set<ConstraintViolation<Object>> errores = dao.validar(registro);
                if (!errores.isEmpty()) {
                    return Funciones.generarMensajeError(errores);
                } else {
                    dao.save(reg);
                }
                return ResponseEntity.ok(reg);
            }
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Mensaje) resp.getBody());
        }
    }

    @RequestMapping(value = "/{id}/remove", method = RequestMethod.DELETE, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.DEL)
    @Description("Eliminar registro")
    @ResponseClass(value = Mensaje.class)
    public
    @ResponseBody
    ResponseEntity<?> apiDelete(@PathVariable BigDecimal id) {
        if (dao.removeById(id)) {
            return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(),
                    "Registro eliminado"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registro no eliminado");
        }
    }


    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/new", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String newForm(Contrato dato, BindingResult bindingResult,
                          Model model) {
        model.addAttribute(ModelAttr.DATO, dato);
        model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Contrato()));
        return MAPPING_FORM;
    }

    @Permisos(Permisos.MOD)
    @NoCache
    @RequestMapping(value = "/{id}/edit", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String edit(@PathVariable BigDecimal id, Contrato dato,
                       BindingResult bindingResult, Model model) {
        ResponseEntity<?> registro = contratoController.apiDetalle(id);

        BigDecimal entidad = Utils.obtenerEntidad(Funciones.getPeticion());
        if (entidad != null && !entidad.equals(((Contrato) registro.getBody()).getServicio().getId())) {
            model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "No puede editar el contrato"));
        } else {
            model.addAttribute(Utils.CONTRATOSELLADO, daoAnuncio.contratoSellado(id));
            model.addAttribute(ModelAttr.DATO, registro.getBody());
            model.addAttribute(ModelAttr.REGISTRO, registro);
        }
        return MAPPING_FORM;
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/funciones/", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String homeFuncionalidades(Model model, @Fiql SearchFiql search, @RequestParam(defaultValue = "", required = false) String cpv) throws org.apache.cxf.jaxrs.ext.search.SearchParseException {

        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));


        return MAPPING_FUNCIONALIDADES + "index";
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String crear(Contrato dato, BindingResult bindingResult, Model model, RedirectAttributes attr) {
        ResponseEntity<?> resultado = apiCrear(dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro creado correctamente"));
            attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
            attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
            return "redirect:/" + MAPPING + "/" + ((Contrato) resultado.getBody()).getId() + "/edit";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
        }
        return MAPPING_FORM;
    }


    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(Set.class, "cpv", new CustomCollectionEditor(Set.class) {
            @Override
            protected Object convertElement(Object element) {
                if (element instanceof Cpv) {
                    return element;
                }
                if (element instanceof String) {
                    return daoCpv.find(new BigDecimal((String) element));
                }
                return null;
            }
        });
    }

    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/{id}/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String modificar(@PathVariable BigDecimal id, Contrato dato,
                            BindingResult bindingResult, Model model, RedirectAttributes attr) {
        if (dato.getOrganoContratante().getId() == null) {
            EstructuraOrganizativa organo = new EstructuraOrganizativa();
            organo.setId(BigDecimal.valueOf(1));
            dato.setOrganoContratante(organo);
        }
        ResponseEntity<?> resultado = apiModificar(id, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro modificado correctamente"));
            attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
            attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
            return "redirect:/" + MAPPING + "/" + id + "/edit";
        } else {

            model.addAttribute(Utils.CONTRATOSELLADO, daoAnuncio.contratoSellado(id));
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_FORM;
        }
    }

    @Permisos(Permisos.DEL)
    @RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String eliminar(@PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
        ResponseEntity<?> resultado = apiDelete(id);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro eliminado correctamente"));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
        }
        return "redirect:/" + MAPPING;
    }

    @Permisos(Permisos.PUB)
    @RequestMapping(value = "/{identifier}/lock", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String lock(@PathVariable BigDecimal identifier, Model model, RedirectAttributes attr) {
        long resultado = dao.updateVisible(identifier, "N");
        if (resultado > 0) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "" + resultado + " Registro desbloqueado correctamente."));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "No se ha desbloqueado el registro."));
        }
        return "redirect:/" + MAPPING + "/" + identifier + "/edit";
    }

    @Permisos(Permisos.PUB)
    @RequestMapping(value = "/{identifier}/unlock", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String unlock(@PathVariable BigDecimal identifier, Model model, RedirectAttributes attr) {
        long resultado = dao.updateVisible(identifier, "S");
        if (resultado > 0) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "" + resultado + " Registro desbloqueado correctamente."));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "No se ha desbloqueado el registro."));
        }
        return "redirect:/" + MAPPING + "/" + identifier + "/edit";
    }

    @Permisos(Permisos.DET)
    @Cache(Cache.DURACION_30MIN)
    @ResponseClass(Anuncio.class)
    @RequestMapping(value = "/{idContrato}/anuncio/{id}", method = RequestMethod.GET, produces = {
            MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD,
            MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public
    @ResponseBody
    ResponseEntity<?> apiDetalleAnuncio(
            @PathVariable BigDecimal idContrato,
            @PathVariable @TestValue("1") BigDecimal id) {
        Anuncio registro = daoAnuncio.find(id);
        if (registro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Mensaje(HttpStatus.NOT_FOUND.value(),
                            messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
        } else {
            return ResponseEntity.ok(registro);
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/anuncio/new", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String newFormAnuncio(@PathVariable BigDecimal idContrato, Anuncio dato,
                                 BindingResult bindingResult, Model model) {

        Contrato l = dao.find(idContrato);
        dato.setContrato(l);
        model.addAttribute(ModelAttr.DATO, dato);
        model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Anuncio()));
        return MAPPING_ANUNCIO_FORM;
    }

    @Permisos(Permisos.MOD)
    @NoCache
    @RequestMapping(value = "/{idContrato}/anuncio/{id}/edit", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String editAnuncio(@PathVariable BigDecimal idContrato,
                              @PathVariable BigDecimal id, Anuncio dato,
                              BindingResult bindingResult, Model model) {
        ResponseEntity<?> registro = apiDetalleAnuncio(idContrato, id);
        model.addAttribute(ModelAttr.DATO, registro.getBody());
        model.addAttribute(ModelAttr.REGISTRO, registro);
        return MAPPING_ANUNCIO_FORM;
    }

    @RequestMapping(value = "/{idContrato}/anuncio", method = RequestMethod.POST, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.NEW)
    @Description("Crear Anuncio")
    @ResponseClass(value = Anuncio.class)
    public
    @ResponseBody
    ResponseEntity<?> apiCrearAnuncio(
            @PathVariable BigDecimal idContrato, @RequestBody Anuncio registro) throws IOException {


        Set<ConstraintViolation<Object>> errores = daoAnuncio.validar(registro);
        if (!errores.isEmpty()) {
            return Funciones.generarMensajeError(errores);
        }
        registro.setVisible("S");
        if (registro.getPubDate() == null) {
            registro.setPubDate(new Date());
        }
        daoAnuncio.almacenar(registro);


        return ResponseEntity.ok(registro);
    }

    @RequestMapping(value = "/{idContrato}/anuncio/{id}", method = RequestMethod.PUT, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.MOD)
    @Description("Modificar registro")
    @ResponseClass(value = Anuncio.class)
    public
    @ResponseBody
    ResponseEntity<?> apiModificarAnuncio(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id,
            @RequestBody Anuncio registro) throws IOException {
        ResponseEntity<?> resp = apiDetalleAnuncio(idContrato, id);
        if (resp.getStatusCode().is2xxSuccessful()) {
            Anuncio reg = (Anuncio) resp.getBody();
            EntidadBase.combinar(reg, registro);
            reg.setId(id);
            Set<ConstraintViolation<Object>> errores = daoAnuncio.validar(registro);
            if (!errores.isEmpty()) {
                return Funciones.generarMensajeError(errores);
            } else {
                daoAnuncio.almacenar(reg);
            }
            return ResponseEntity.ok(reg);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    (Mensaje) resp.getBody());
        }
    }

    @RequestMapping(value = "/{idContrato}/anuncio/{id}/remove", method = RequestMethod.DELETE, produces = {
            MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.DEL)
    @Description("Eliminar registro")
    @ResponseClass(value = Mensaje.class)
    public
    @ResponseBody
    ResponseEntity<?> apiDeleteAnuncio(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id) {

        if (daoAnuncio.eliminar(id)) {
            return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(),
                    "Anuncio eliminado"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Anuncio no eliminado");
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/anuncio/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String crearAnuncio(@PathVariable BigDecimal idContrato,
                               Anuncio dato,
                               @RequestParam("file") MultipartFile files,
                               BindingResult bindingResult, Model model, RedirectAttributes attr) throws IOException {

        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        dato.setContrato(contrato);
        if (dato.getPubDate() == null) {
            dato.setPubDate(new Date());
        }
        ResponseEntity<?> resultado = apiCrearAnuncio(idContrato, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            daoAnuncio.flush();
            daoAnuncio.asociarAnexos((Anuncio) resultado.getBody(), files);
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro creado correctamente"));
            attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
            attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#anuncios";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_ANUNCIO_FORM;
        }

    }

    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/{idContrato}/anuncio/{id}/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String modificarAnuncio(@PathVariable BigDecimal idContrato,
                                   @PathVariable BigDecimal id, Anuncio dato, @RequestParam("file") MultipartFile files,
                                   BindingResult bindingResult, Model model, RedirectAttributes attr) throws IOException {
        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        dato.setContrato(contrato);
        if (dato.getPubDate() == null) {
            dato.setPubDate(new Date());
        }
        ResponseEntity<?> resultado = apiModificarAnuncio(idContrato, id, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            daoAnuncio.asociarAnexos((Anuncio) resultado.getBody(), files);
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro modificado correctamente"));
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#anuncios";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_ANUNCIO_FORM;
        }
    }

    @Permisos(Permisos.DEL)
    @RequestMapping(value = "/{idContrato}/anuncio/{id}/delete", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String eliminarAnuncio(@PathVariable BigDecimal idContrato,
                                  @PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
        ResponseEntity<?> resultado = apiDeleteAnuncio(idContrato, id);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Anuncio eliminado correctamente"));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
        }
        return "redirect:/" + MAPPING + "/" + idContrato + "/edit#anuncios";
    }

    @Permisos(Permisos.PUB)
    @RequestMapping(value = "/{idContrato}/anuncio/{identifier}/lock", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String lockAnuncio(@PathVariable BigDecimal idContrato,
                              @PathVariable BigDecimal identifier, Model model,
                              RedirectAttributes attr) {
        long resultado = daoAnuncio.updateVisible(identifier, "N");
        if (resultado > 0) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "" + resultado
                    + " Registro desbloqueado correctamente."));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se ha desbloqueado el registro."));
        }
        return "redirect:/" + MAPPING + "/" + idContrato + "/edit#anuncios";
    }

    @Permisos(Permisos.PUB)
    @RequestMapping(value = "/{idContrato}/anuncio/{identifier}/unlock", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String unlockAnuncio(@PathVariable BigDecimal idContrato,
                                @PathVariable BigDecimal identifier, Model model,
                                RedirectAttributes attr) {
        long resultado = daoAnuncio.updateVisible(identifier, "S");
        if (resultado > 0) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "" + resultado
                    + " Registro desbloqueado correctamente."));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.BAD_REQUEST.value(),
                    "No se ha desbloqueado el registro."));
        }
        return "redirect:/" + MAPPING + "/" + idContrato + "/edit#anuncios";
    }


    @Permisos(Permisos.DET)
    @Cache(Cache.DURACION_30MIN)
    @ResponseClass(Oferta.class)
    @RequestMapping(value = "/{idContrato}/oferta/{id}", method = RequestMethod.GET, produces = {
            MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD,
            MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public
    @ResponseBody
    ResponseEntity<?> apiDetalleOferta(
            @PathVariable BigDecimal idContrato,
            @PathVariable @TestValue("1") BigDecimal id) {
        Oferta registro = daoOferta.find(id);
        if (registro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Mensaje(HttpStatus.NOT_FOUND.value(),
                            messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
        } else {
            return ResponseEntity.ok(registro);
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/oferta/new", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String newFormOferta(@PathVariable BigDecimal idContrato, Oferta dato,
                                BindingResult bindingResult, Model model) {

        Contrato l = dao.find(idContrato);
        dato.setContrato(l);
        model.addAttribute(ModelAttr.DATO, dato);
        model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Oferta()));
        return MAPPING_OFERTA_FORM;
    }

    @Permisos(Permisos.MOD)
    @NoCache
    @RequestMapping(value = "/{idContrato}/oferta/{id}/edit", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String editOferta(@PathVariable BigDecimal idContrato,
                             @PathVariable BigDecimal id, Oferta dato,
                             BindingResult bindingResult, Model model) {
        ResponseEntity<?> registro = apiDetalleOferta(idContrato, id);
        model.addAttribute(ModelAttr.DATO, registro.getBody());
        model.addAttribute(ModelAttr.REGISTRO, registro);
        return MAPPING_OFERTA_FORM;
    }

    @RequestMapping(value = "/{idContrato}/oferta", method = RequestMethod.POST, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.NEW)
    @Description("Crear Oferta")
    @ResponseClass(value = Oferta.class)
    public
    @ResponseBody
    ResponseEntity<?> apiCrearOferta(
            @PathVariable BigDecimal idContrato, @RequestBody Oferta registro) {
        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        if (registro.getGanador() == true) {
            Estado ganador = new Estado();
            if (registro.getFechaFormalizacion() == null) {
                ganador.setId(5);
                contrato.setFechaContrato(registro.getFechaAdjudicacion());
            } else {
                ganador.setId(6);
                contrato.setFechaContrato(registro.getFechaFormalizacion());
            }
            contrato.setStatus(ganador);
        }
        registro.setContrato(contrato);
        Set<ConstraintViolation<Object>> errores = daoOferta.validar(registro);
        if (!errores.isEmpty()) {
            return Funciones.generarMensajeError(errores);
        }
        daoOferta.save(registro);
        return ResponseEntity.ok(registro);
    }

    @RequestMapping(value = "/{idContrato}/oferta/{id}", method = RequestMethod.PUT, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.MOD)
    @Description("Modificar registro")
    @ResponseClass(value = Oferta.class)
    public
    @ResponseBody
    ResponseEntity<?> apiModificarOferta(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id,
            @RequestBody Oferta registro) {
        ResponseEntity<?> resp = apiDetalleOferta(idContrato, id);
        if (resp.getStatusCode().is2xxSuccessful()) {
            Oferta reg = (Oferta) resp.getBody();
            EntidadBase.combinar(reg, registro);
            reg.setId(id);
            if (registro.getGanador() == true) {
                Estado ganador = new Estado();
                if (registro.getFechaFormalizacion() == null) {
                    ganador.setId(5);
                    reg.getContrato().setFechaContrato(registro.getFechaAdjudicacion());
                } else {
                    ganador.setId(6);
                    reg.getContrato().setFechaContrato(registro.getFechaFormalizacion());
                }
                reg.getContrato().setStatus(ganador);
            }

            Set<ConstraintViolation<Object>> errores = daoOferta.validar(registro);
            if (!errores.isEmpty()) {
                return Funciones.generarMensajeError(errores);
            } else {
                daoOferta.save(reg);
            }
            return ResponseEntity.ok(reg);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    (Mensaje) resp.getBody());
        }
    }

    @RequestMapping(value = "/{idContrato}/oferta/{id}/remove", method = RequestMethod.DELETE, produces = {
            MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.DEL)
    @Description("Eliminar registro")
    @ResponseClass(value = Mensaje.class)
    public
    @ResponseBody
    ResponseEntity<?> apiDeleteOferta(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id) {
        if (daoOferta.removeById(id)) {
            return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(),
                    "Oferta eliminada"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Oferta no eliminada");
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/oferta/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String crearOferta(@PathVariable BigDecimal idContrato,
                              Oferta dato,
                              BindingResult bindingResult, Model model, RedirectAttributes attr) {

        ResponseEntity<?> resultado = apiCrearOferta(idContrato, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            daoOferta.flush();
            dao.flush();
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro creado correctamente"));
            attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
            attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#ofertas";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_OFERTA_FORM;
        }

    }

    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/{idContrato}/oferta/{id}/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String modificarOferta(@PathVariable BigDecimal idContrato,
                                  @PathVariable BigDecimal id, Oferta dato,
                                  BindingResult bindingResult, Model model, RedirectAttributes attr) {
        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        if (dato.getGanador() == true) {
            Estado ganador = new Estado();
            ganador.setId(5);
            contrato.setStatus(ganador);
            if (dato.getFechaFormalizacion() == null)
                contrato.setFechaContrato(dato.getFechaAdjudicacion());
            else
                contrato.setFechaContrato(dato.getFechaFormalizacion());
        }
        dato.setContrato(contrato);
        ResponseEntity<?> resultado = apiModificarOferta(idContrato, id, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro modificado correctamente"));
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#ofertas";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_OFERTA_FORM;
        }
    }

    @Permisos(Permisos.DEL)
    @RequestMapping(value = "/{idContrato}/oferta/{id}/delete", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String eliminarOferta(@PathVariable BigDecimal idContrato,
                                 @PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
        ResponseEntity<?> resultado = apiDeleteOferta(idContrato, id);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Oferta eliminada correctamente"));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
        }
        return "redirect:/" + MAPPING + "/" + idContrato + "/edit#ofertas";
    }

    @Permisos(Permisos.DET)
    @Cache(Cache.DURACION_30MIN)
    @ResponseClass(Lote.class)
    @RequestMapping(value = "/{idContrato}/lote/{id}", method = RequestMethod.GET, produces = {
            MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD,
            MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public @ResponseBody
    ResponseEntity<?> apiDetalleLote(
            @PathVariable BigDecimal idContrato,
            @PathVariable @TestValue("1") BigDecimal id) {
        Lote registro = daoLote.find(id);
        if (registro == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                    new Mensaje(HttpStatus.NOT_FOUND.value(),
                            messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
        } else {
            return ResponseEntity.ok(registro);
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/lote/new", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String newFormLote(@PathVariable BigDecimal idContrato, Lote dato,
                              BindingResult bindingResult, Model model) {

        Contrato l = dao.find(idContrato);
        dato.setContrato(l);
        model.addAttribute(ModelAttr.DATO, dato);
        model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Lote()));
        return MAPPING_LOTE_FORM;
    }

    @Permisos(Permisos.MOD)
    @NoCache
    @RequestMapping(value = "/{idContrato}/lote/{id}/edit", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String editLote(@PathVariable BigDecimal idContrato,
                           @PathVariable BigDecimal id, Lote dato,
                           BindingResult bindingResult, Model model) {
        ResponseEntity<?> registro = apiDetalleLote(idContrato, id);
        model.addAttribute(ModelAttr.DATO, registro.getBody());
        model.addAttribute(ModelAttr.REGISTRO, registro);
        return MAPPING_LOTE_FORM;
    }

    @RequestMapping(value = "/{idContrato}/lote", method = RequestMethod.POST, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.NEW)
    @Description("Crear Lote")
    @ResponseClass(value = Lote.class)
    public @ResponseBody
    ResponseEntity<?> apiCrearLote(
            @PathVariable BigDecimal idContrato, @RequestBody Lote registro) {
        Set<ConstraintViolation<Object>> errores = daoLote.validar(registro);
        if (!errores.isEmpty()) {
            return Funciones.generarMensajeError(errores);
        }
        daoLote.save(registro);
        return ResponseEntity.ok(registro);
    }

    @RequestMapping(value = "/{idContrato}/lote/{id}", method = RequestMethod.PUT, consumes = {
            MimeTypes.JSON, MimeTypes.XML}, produces = {MimeTypes.JSON,
            MimeTypes.XML})
    @Permisos(Permisos.MOD)
    @Description("Modificar registro")
    @ResponseClass(value = Lote.class)
    public @ResponseBody
    ResponseEntity<?> apiModificarLote(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id,
            @RequestBody Lote registro) {
        ResponseEntity<?> resp = apiDetalleLote(idContrato, id);
        if (resp.getStatusCode().is2xxSuccessful()) {
            Lote reg = (Lote) resp.getBody();
            EntidadBase.combinar(reg, registro);
            reg.setId(id);
            Set<ConstraintViolation<Object>> errores = daoLote.validar(registro);
            if (!errores.isEmpty()) {
                return Funciones.generarMensajeError(errores);
            } else {
                daoLote.save(reg);
            }
            return ResponseEntity.ok(reg);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    (Mensaje) resp.getBody());
        }
    }

    @RequestMapping(value = "/{idContrato}/lote/{id}/remove", method = RequestMethod.DELETE, produces = {
            MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.DEL)
    @Description("Eliminar registro")
    @ResponseClass(value = Mensaje.class)
    public @ResponseBody
    ResponseEntity<?> apiDeleteLote(
            @PathVariable BigDecimal idContrato, @PathVariable BigDecimal id) {
        if (daoLote.removeById(id)) {
            return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(),
                    "Lote eliminado"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    "Lote no eliminado");
        }
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(value = "/{idContrato}/lote/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String crearLote(@PathVariable BigDecimal idContrato,
                            Lote dato,
                            BindingResult bindingResult, Model model, RedirectAttributes attr) {

        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        dato.setContrato(contrato);
        ResponseEntity<?> resultado = apiCrearLote(idContrato, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            daoLote.flush();
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro creado correctamente"));
            attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
            attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#lotes";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_LOTE_FORM;
        }

    }

    @Permisos(Permisos.MOD)
    @RequestMapping(value = "/{idContrato}/lote/{id}/save", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String modificarLote(@PathVariable BigDecimal idContrato,
                                @PathVariable BigDecimal id, Lote dato,
                                BindingResult bindingResult, Model model, RedirectAttributes attr) {
        Contrato contrato = (Contrato) contratoController.apiDetalle(idContrato).getBody();
        dato.setContrato(contrato);
        ResponseEntity<?> resultado = apiModificarLote(idContrato, id, dato);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Registro modificado correctamente"));
            return "redirect:/" + MAPPING + "/" + idContrato + "/edit#lotes";
        } else {
            model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
            model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
            model.addAttribute(ModelAttr.DATO, dato);
            return MAPPING_LOTE_FORM;
        }
    }

    @Permisos(Permisos.DEL)
    @RequestMapping(value = "/{idContrato}/lote/{id}/delete", method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String eliminarLote(@PathVariable BigDecimal idContrato,
                               @PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
        ResponseEntity<?> resultado = apiDeleteLote(idContrato, id);
        if (resultado.getStatusCode().is2xxSuccessful()) {
            attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(
                    HttpStatus.OK.value(), "Lote eliminado correctamente"));
        } else {
            attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
        }
        return "redirect:/" + MAPPING + "/" + idContrato + "/edit#lotes";
    }

    @Permisos(Permisos.NEW)
    @RequestMapping(path = "/contrato/carga", headers = ("content-type=multipart/*"), method = RequestMethod.POST, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String cargaContratos(Model model, @RequestParam("file") MultipartFile file, RedirectAttributes attr) throws SearchParseException {
        if (!file.isEmpty()) {
            try {
                String extension = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf('.'), file.getOriginalFilename().length());
                if (extension.equals(".xls") || extension.equals(".xlsx")) {
                    List<Contrato> contrato = dao.loadXls(file.getInputStream());
                    if (contrato.size() > 0) {
                        for (int i = 0; i < contrato.size(); i++) {
                            Contrato item = contrato.get(i);
                            ResponseEntity<?> respuesta = apiCrear(item);
                            if (respuesta.getStatusCode().is2xxSuccessful()) {
                                dao.flush();
                                model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Ofertas creadas correctamente"));
                            }
                        }
                        SearchFiql search = new SearchFiql();
                        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));

                        return "redirect:/" + MAPPING + "/";
                    } else {
                        model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Error en carga de ofertas."));
                        SearchFiql search = new SearchFiql();
                        model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                        return "redirect:/" + MAPPING + "/";
                    }
                } else {
                    model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Fichero no soportado."));
                    SearchFiql search = new SearchFiql();
                    model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                    return "redirect:/" + MAPPING + "/index";
                }
            } catch (IOException e) {
                model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Error en la carga fichero."));
                SearchFiql search = new SearchFiql();
                model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
                return "redirect:/" + MAPPING + "/index";
            }
        } else {
            model.addAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.BAD_REQUEST.value(), "Fichero no soportado."));
            SearchFiql search = new SearchFiql();
            model.addAttribute(ModelAttr.RESULTADO, apiListar(search));
            return "redirect:/" + MAPPING + "/index";
        }

    }

    @Permisos(Permisos.DOC)
    @NoCache
    @ResponseClass(byte[].class)
    @RequestMapping(value = "/tribunal-cuentas", method = RequestMethod.GET, produces = {MimeTypes.XLS})
    @Transactional(Esquema.TMPARTICIPACION)
    public @ResponseBody
    ResponseEntity<?> apiTribunalCuentas(@RequestParam(name = "year", required = true) BigDecimal year) throws ParseException {

        Search busqueda = new Search(Contrato.class);
        Calendar c = Calendar.getInstance();
        c.setTime(ConvertDate.string2Date("01-01-" + year, ConvertDate.DATE_FORMAT));
        busqueda.addFilterAnd(Filter.greaterOrEqual("fechaPresentacion", c.getTime()));
        c.setTime(ConvertDate.string2Date("31-12-" + year, ConvertDate.DATE_FORMAT));
        busqueda.addFilterAnd(Filter.lessOrEqual("fechaPresentacion", c.getTime()));

        SearchResult<Contrato> resultado = dao.searchAndCount(busqueda);
        HSSFWorkbook workbook = dao.generarExcelTribunalCuentas(resultado, false);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(MimeTypes.XLS));
        headers.set("Content-Disposition",
                "inline; filename=\"tribunal-cuentas.xls\"");
        return new ResponseEntity<byte[]>(workbook.getBytes(), headers, HttpStatus.OK);
    }

    @Permisos(Permisos.DOC)
    @NoCache
    @ResponseClass(Mensaje.class)
    @RequestMapping(value = "/check-virtuoso", method = RequestMethod.GET, produces = {MimeTypes.JSON})
    @Transactional(Esquema.TMPARTICIPACION)
    public @ResponseBody
    ResponseEntity<?> apiCheckContratosEnVirtuoso() throws ParseException {
        return null;
    }

}
