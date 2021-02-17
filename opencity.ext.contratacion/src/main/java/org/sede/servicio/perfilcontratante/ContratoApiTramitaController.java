package org.sede.servicio.perfilcontratante;



import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;
import org.apache.commons.io.IOUtils;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.SearchFiql;
import org.sede.core.dao.VirtuosoDataManagement;
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
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLDecoder;
import java.time.Period;
import java.util.*;

import static org.apache.solr.client.solrj.impl.XMLResponseParser.log;


@Gcz(servicio = "PERFILCONTRATANTE", seccion = "CONTRATO")
@Controller
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ContratoApiTramitaController.MAPPING, method = RequestMethod.GET)
public class ContratoApiTramitaController {
    //region Atributtes
    private static final Logger logger = LoggerFactory.getLogger(ContratoApiTramitaController.class);
    private static final String RAIZ = "contratacion-publica/admin/";
    private static final String SERVICIO = "tramita";
    public static final String MAPPING = "  servicio/" +  RAIZ + SERVICIO;
    private static String URIWS = Propiedades.getString("contratacion.estado.ws");
    private static String USER  = "restapi";
    private static String PASS  = "aytointegr0!#";
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
    @Autowired
    private CriteriosGenericDAO daoCriterios;
    //endregion

    //region Vistas
    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String redirect() {
        return "redirect:/" + SERVICIO + "";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/tramita", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String tramita(Model model, @RequestParam(name = "carpeta", required = false, defaultValue = "") String carpeta) throws org.apache.cxf.jaxrs.ext.search.SearchParseException, IOException {
        if (!carpeta.isEmpty()) {

            model.addAttribute(ModelAttr.RESULTADO, apiTramitaJson(carpeta));
        } else {

            model.addAttribute(ModelAttr.RESULTADO, "inicio");
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
                Peticion peticion=new Peticion();
                Contrato conJson = rellenarContratoAdjudicacion(actualObj.toString(),peticion );
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


    @ResponseClass(value = Contrato.class, entity = SearchResult.class)
    @RequestMapping(method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
    public @ResponseBody  ResponseEntity<?> apiListar(@Fiql SearchFiql search) throws SearchParseException {
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
  /**  public Contrato parseadorContrato(JsonNode json, String expediente, BigDecimal id)throws Exception {

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
			}
			if(actualObj.has("pc:lot")){
				Iterator<JsonNode> iteratorlote=actualObj.get("pc:lot").elements();
				while (iteratorlote.hasNext()) {
					actualObjLote=iteratorlote.next();
					lotesBolean=actualObjLote.has("pc:tender");
				}
			}

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

				if (actualObj.has("pc:lot")){
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
				}
                Estado status = new Estado();
                status.setId(0);
                contrato.setStatus(status);
                EntidadContratante contratante = new EntidadContratante();
                contratante.setId(new BigDecimal(Contrato.obtenerPortal(Funciones.getPeticion())));
                contrato.setEntity(contratante);


				String anuncioLicitacion = UtilsContrato
						.obtenerLiTituloContrato(actualObj)
						+ UtilsContrato.obtenerLiTipoContrato(this.getType())
						+ UtilsContrato.obtenerLiGestor(actualObj)
						+ UtilsContrato.obtenerLiOrganoContratacion(actualObj)
						+ UtilsContrato.obtenerLiExpediente(actualObj)
						+ UtilsContrato.obtenerLiObjeto(actualObj)
						+ UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
						+ UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
						+UtilsContrato.obtenerLiUrgencia(contrato.getUrgente())
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
					contrato.getAnuncios().add(licitacion);
				}

				if (actualObj.has("pproc:contractProcedureSpecifications")) {
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
				}

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
                            Criterio criterio = new Criterio();
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
				if(actualObj.has("pc:lot")){
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
				}
                    Estado status = new Estado();
                    status.setId(5);
                    contrato.setStatus(status);

                    contrato.setEntity(new EntidadContratante(new BigDecimal(Contrato.obtenerPortal(Funciones.getPeticion()))));
				String anuncioAdjudicacion = UtilsContrato
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
				}
                }
            }
            return contrato;

        } catch (Exception e) {
            logger.error(e.getMessage());
            e.printStackTrace();

        }
        return null;

    }*/
	@OpenData

    @RequestMapping(value = "/crear",consumes =MimeTypes.JSON ,method = RequestMethod.POST,  produces = {MimeTypes.JSON})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody  ResponseEntity<?> apiCrear(HttpServletRequest httpRequest) throws Exception {
        Contrato contrato=new Contrato();
	    try {
            contrato= rellenarContratoLicitacion(Funciones.getPeticion().getCuerpoPeticion(), Funciones.getPeticion());
           Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
           if (!errores.isEmpty()) {
               return Funciones.generarMensajeError(errores);
           }

           if ("10".equals(contrato.getProcedimiento().getId().toString())) {
               contrato.setContratoMenor(true);
           } else {
               contrato.setContratoMenor(false);
           }
           Set<Criterio> criterios = new HashSet<Criterio>();
           List<Anuncio> anuncios = new ArrayList<Anuncio>();
           anuncios = contrato.getAnuncios();
           criterios = contrato.getCriterios();
           contrato.setAnuncios(null);
           contrato.setCriterios(null);
           dao.save(contrato);
           for (Anuncio anun : anuncios) {
               anun.setContrato(contrato);
               daoAnuncio.almacenar(anun);
           }
           for (Criterio criterio : criterios) {
               criterio.setContrato(contrato);
               daoCriterios.save(criterio);
           }
           dao.flush();
           daoAnuncio.flush();
           daoCriterios.flush();
            VirtuosoDataManagement.loadJsonLd(Funciones.getPeticion().getCuerpoPeticion(), Contrato.class.getAnnotation(Grafo.class).value());
       }catch (Exception e){
            e.printStackTrace();
            Funciones.sendMail("Error al cargar contrato", e.getMessage() + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");

       }
        return ResponseEntity.ok(contrato);
    }
    @OpenData
    @RequestMapping(value = "/guardar",consumes =MimeTypes.JSON ,method = RequestMethod.PUT,  produces = {MimeTypes.JSON})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody  ResponseEntity<?> apiModificar(HttpServletRequest httpRequest) throws Exception {

	    Contrato contrato= rellenarContratoAdjudicacion(Funciones.getPeticion().getCuerpoPeticion(),Funciones.getPeticion());
        Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
        if (!errores.isEmpty()) {
            Funciones.sendMail("Error al cargar contrato", errores + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
            return Funciones.generarMensajeError(errores);
        }else {
            Funciones.sendMail("Exito carga del contrato",contrato.toString() + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
            return ResponseEntity.ok(contrato);
        }


    }

    @Permisos(Permisos.DET)
    @RequestMapping(value = "/integracion-estado",method = RequestMethod.GET,  produces = {MimeTypes.XML})
    @ResponseClass(value = Contrato.class)
    public @ResponseBody  ResponseEntity<?> apiIntegracionPlataforma(@RequestParam(name="expediente")String expediente)  throws Exception {
        try {
            Search busqueda =new Search();
            busqueda.addFilterEqual("expediente",expediente);
            Contrato contrato=dao.searchUnique(busqueda);

        if(contrato!=null) {
            Set<ConstraintViolation<Object>> errores = dao.validar(contrato);
            if (!errores.isEmpty()) {
                //Funciones.sendMail("Error al cargar contrato", errores + ":" + Funciones.getPeticion().getCuerpoPeticion(), "bweb@zaragoza.es", "", "HTML");
                return Funciones.generarMensajeError(errores);
            }
            String menor=CodiceConverterMenor.conversor(contrato);
            CodiceConverterMenor.validarXsd(menor);

           return  ResponseEntity.ok(menor);

        }

        return ResponseEntity.ok(contrato);
        }catch (Exception e){
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(( e.getMessage()));
        }
    }


    public Contrato rellenarContratoAdjudicacion(String jsonld, Peticion peticion)throws Exception{
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
            boolean ganador = false;
            for (Oferta ofer : con.getOfertas()) {
                if (ofer.getGanador())
                    ganador = true;
            }



                if (actualObj.has("pc:tender") == true) {
                    // Estamos en la adjudicacion
                    if (actualObj.has("tbfy:numberOfTenderers") == true) {
                        con.setNumLicitadores(actualObj.get("tbfy:numberOfTenderers").asInt());
                    }
                    if (actualObj.has("pc:tender")) {
                        JsonNode jsonNode = actualObj.get("pc:tender");
                        if (jsonNode.isArray()) {
                            Iterator<JsonNode> iterator = jsonNode.elements();
                            while (iterator.hasNext()) {
                                JsonNode valor = iterator.next();
                                if (valor.has("pproc:awardDate")) {
                                    if(!ganador) {
                                        Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, true, "");
                                        con.getOfertas().add(ofertaGanadora);
                                    }
                                } else {
                                    Oferta ofertaGanadora = rellenarOferta(con, valor, peticion, false, "");
                                    con.getOfertas().add(ofertaGanadora);
                                }
                            }
                        }
                    }
                    if (actualObj.has("pc:lot")) {
                        Iterator<JsonNode> iteratorlote = actualObj.get("pc:lot").elements();
                        while (iteratorlote.hasNext()) {
                            JsonNode loteAdjudicacion = iteratorlote.next();
                            String idLote = loteAdjudicacion.get("dcterms:identifier").asText().replace("contzar:", "");
                            if (loteAdjudicacion.has("pc:tender")) {
                                JsonNode jsonNode = loteAdjudicacion.get("pc:tender");
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
                    for (Anuncio anun : con.getAnuncios()) {
                        if (anun.getType().getId().equals(new BigDecimal(6.0))) {
                            existeAnuncio = true;
                            break;
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
                            con.getAnuncios().add(adjudicacion);
                        }
                    }
                }


            return con;
        }catch (Exception e){
            logger.error(e.getMessage());
            return null;
        }
    }
    public Contrato rellenarContratoLicitacion (String jsonld, Peticion peticion)throws Exception {
        Contrato con= new Contrato();
        try{
            JsonNode actualObjLote;
            boolean lotesBolean=false;
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(jsonld);

            if (actualObj.has("dcterms:title")) {
                con.setTitle(actualObj.get("dcterms:title").asText());
            }
            con.setExpediente(actualObj.get("dcterms:identifier").asText());
            con.setType(daoTipoContrato.obtenerTipo(actualObj));
            if (actualObj.has("pproc:contractTemporalConditions")) {
                JsonNode temporal=actualObj.get("pproc:contractTemporalConditions");
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
                EstructuraOrganizativa servicio=new EstructuraOrganizativa();
                servicio.setId(BigDecimal.valueOf(Double.valueOf(idEntidad)));
                con.setServicio(servicio);
            }
            if (actualObj.has("pproc:contractingBody")) {
                JsonNode gestor = actualObj.get("pproc:contractingBody");
                String idOrgano = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
                EstructuraOrganizativa organo=new EstructuraOrganizativa();
                organo.setId(BigDecimal.valueOf(Double.valueOf(idOrgano)));
                con.setOrganoContratante(organo); ;
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
                            con.setProcedimiento (procede);
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

            if (actualObj.has("pproc:legalDocumentReference")) {

                Iterator<JsonNode> iterator = actualObj.get(
                        "pproc:legalDocumentReference").elements();
                while (iterator.hasNext()) {
                    JsonNode valor = iterator.next();
                    Anuncio pliego = new Anuncio();

                    pliego.setTitle(valor.get("dcterms:title").asText());
                    pliego.setFileName(Utils.normalizar(valor.get("name")
                            .asText()));
                    daoAnuncio.save(pliego);

                    String fileName = pliego.getFileName();

                    CustomMultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);
                    try {
                        customMultipartFile.transferTo(customMultipartFile.getFile());

                    } catch (IllegalStateException e) {
                        log.info("IllegalStateException : " + e);
                    } catch (IOException e) {
                        log.info("IOException : " + e);
                    }
                    daoAnuncio.asociarAnexos(pliego,customMultipartFile);
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio.setId(Utils.TIPO_ANUNCIO_PLIEGOS_ADMINISTRATIVOS);
                    pliego.setType(tipoAnuncio);
                    pliego.setVisible("S");
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    con.getAnuncios().add(pliego);
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

                    CustomMultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);
                    try {
                        customMultipartFile.transferTo(customMultipartFile.getFile());

                    } catch (IllegalStateException e) {
                        log.info("IllegalStateException : " + e);
                    } catch (IOException e) {
                        log.info("IOException : " + e);
                    }
                    daoAnuncio.asociarAnexos(pliego,customMultipartFile);
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio.setId(Utils.TIPO_ANUNCIO_OTRAS_INFORMACIONES);
                    pliego.setType(tipoAnuncio);
                    pliego.setVisible("S");
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    con.getAnuncios().add(pliego);
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
                    String fileName =   pliego.getFileName();

                    CustomMultipartFile customMultipartFile = new CustomMultipartFile(Utils.decodeFromString(URLDecoder.decode(
                            valor.get("body").asText(), "UTF-8")), fileName);
                    try {
                        customMultipartFile.transferTo(customMultipartFile.getFile());

                    } catch (IllegalStateException e) {
                        log.info("IllegalStateException : " + e);
                    } catch (IOException e) {
                        log.info("IOException : " + e);
                    }
                    daoAnuncio.asociarAnexos(pliego,customMultipartFile);
                    Tipoanuncio tipoAnuncio = new Tipoanuncio();
                    tipoAnuncio
                            .setId(Utils.TIPO_ANUNCIO_PLIEGOS_TECNICOS);
                    pliego.setType(tipoAnuncio);
                    pliego.setVisible("S");
                    pliego.setUsuarioAlta(peticion.getClientId());
                    pliego.setCreationDate(new Date());
                    pliego.setLastUpdated(new Date());
                    pliego.setPubDate(new Date());
                    con.getAnuncios().add(pliego);
                }
            }
			/*if(actualObj.has("pc:lot")){
				Iterator<JsonNode> iteratorlote=actualObj.get("pc:lot").getElements();
				while (iteratorlote.hasNext()) {
					 actualObjLote=iteratorlote.next();
					 lotesBolean=actualObjLote.has("pc:tender");
				}
			}*/

              // Estamos en licitación
            if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:contractEconomicConditions")
                        && actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
                    Iterator<JsonNode> iterator = actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").elements();
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        boolean conIva = valor.get("gr:valueAddedTaxIncluded").asBoolean();
                        if (conIva) {
                            con.setImporteConIVA(BigDecimal.valueOf(Double.valueOf( valor.get("gr:hasCurrencyValue").asText())));
                        } else 	{
                            con.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf( valor.get("gr:hasCurrencyValue").asText())));
                        }
                    }

                }
                if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:mainObject") ) {
                    if(!actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().contains("null")){
                        String[] cpvArray = actualObj.get("pproc:contractObject").get("pproc:mainObject").asText().split("-");
                        System.out.println(cpvArray[1]);
                        Cpv cpv=new Cpv(cpvArray[1]);
                        con.getCpv().add(cpv);
                    }
                }
                /*Zona de Lotes*/
			/*	if (actualObj.has("pc:lot")){
					Iterator<JsonNode> iterator = actualObj.get("pc:lot").getElements();
					this.setTieneLote("S");
					while (iterator.hasNext()) {
						Lote lote=new Lote();
						lote.setStatus(0);
						JsonNode valor = iterator.next();
						 String idLote=valor.get("dcterms:identifier").asText().replace("contzar:", "");
						 lote.setIdTramita(idLote);
						lote.setDescription( valor.get("dcterms:title").asText());
						if (valor.has("pproc:contractObject") && valor.get("pproc:contractObject").has("pproc:contractEconomicConditions")
								&& valor.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
							Iterator<JsonNode> iterator2 = valor.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").getElements();
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
                con.setVisible("S");
                con.setCreationDate(new Date());
                con.setPubDate(new Date());
                Estado estado=new Estado();
                estado.setId(0);
                con.setStatus(estado);
                con.setEntity(new EntidadContratante(new BigDecimal(1.0)));
                String anuncioLicitacion = UtilsContrato.obtenerLiTituloContrato(actualObj)
                        + UtilsContrato.obtenerLiTipoContrato(con.getType())
                        + UtilsContrato.obtenerLiGestor(actualObj)
                        + UtilsContrato.obtenerLiOrganoContratacion(actualObj)
                        + UtilsContrato.obtenerLiExpediente(actualObj)
                        + UtilsContrato.obtenerLiObjeto(actualObj)
                        + UtilsContrato.obtenerLiPlazoEjecucion(actualObj)
                        + UtilsContrato.obtenerLiTipoProcedimiento(actualObj)
                        +UtilsContrato.obtenerLiUrgencia(con.getUrgente())
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
                            Criterio criterio= new Criterio();
                            if(valor.has("@type")){
                                String tipo=valor.get("@type").asText();
                                if("pproc:ObjectiveAwardCriterion".contains(tipo)){
                                    TipoCriterio tipoCriterio=new TipoCriterio();
                                    tipoCriterio.setId(new BigDecimal(1));
                                    criterio.setTipo(tipoCriterio);
                                }else if("pproc:SubjectiveAwardCriterion".contains(tipo)){
                                    TipoCriterio tipoCriterio=new TipoCriterio();
                                    tipoCriterio.setId(new BigDecimal(2));
                                    criterio.setTipo(tipoCriterio);
                                }
                            }
                            if (valor.has("pc:criterionName")) {
                                criterio.setTitle( valor.get("pc:criterionName").asText());
                            }
                            if (valor.has("pproc:criterionEvaluationMode")) {
                                criterio.setDescription(valor
                                        .get("pproc:criterionEvaluationMode")
                                        .asText()) ;

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
                con.setCanon(false);
            return con;
        }catch (Exception e){
            e.printStackTrace();
          return null;
        }
    }
    public Oferta rellenarOferta(Contrato con,JsonNode objc, Peticion peticion, boolean ganador, String idLote)throws Exception {

	    Oferta ofer=new Oferta();
        try {


            JsonNode actualObj = objc;
            if (actualObj != null) {
                if (actualObj.has("pproc:awardDate")) {
                    ofer.setFechaAdjudicacion(ConvertDate.string2Date(actualObj.get("pproc:awardDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
                    ofer.setGanador(true);


                } else {
                    ofer.setGanador(false);
                }
                if (actualObj.has("pproc:formalizedDate")) {
                    ofer.setFechaFormalizacion(ConvertDate.string2Date(actualObj.get("pproc:formalizedDate").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA));
                }
                ofer.setCanon(false);
                ofer.setTieneUte(false);
                ofer.setAhorroVisible(true);
                if (actualObj.has("pc:supplier")) {
                    if (actualObj.get("pc:supplier").has("org:identifier")) {
                        Search search = new Search();
                        search.addFilterEqual("nif", actualObj.get("pc:supplier").get("org:identifier").asText());
                        Empresa licitador = daoEmpresa.searchUnique(search);
                        if (licitador != null) {
                            ofer.setEmpresa(licitador);
                        } else {
                            Empresa licitador2=new Empresa();
                            licitador2.setUte("N");
                            licitador2.setNacionalidad("es");
                            if (actualObj.get("pc:supplier").has("s:name")) {
                                licitador2.setNombre(actualObj.get("pc:supplier").get("s:name").asText());
                            }
                            if (actualObj.get("pc:supplier").has("org:identifier")) {
                                licitador2.setNif(actualObj.get("pc:supplier").get("org:identifier").asText());
                                String nif=licitador2.getNif();
                                if (Oferta.anoniCierto(nif)) {
                                    licitador2.setAutonomo("S");
                                } else {
                                    licitador2.setAutonomo("N");
                                }
                            }
                            ofer.setEmpresa(licitador2);
                        }
                    }
                    if (actualObj.has("pc:offeredPrice")) {
                        Iterator<JsonNode> iterator = actualObj.get("pc:offeredPrice").elements();
                        while (iterator.hasNext()) {
                            JsonNode valor = iterator.next();
                            String conIva = valor.get("gr:valueAddedTaxIncluded").asText();
                            if ("true".equals(conIva)) {
                                ofer.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                            } else {
                                ofer.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(valor.get("gr:hasCurrencyValue").asText())));
                            }
                        }
                    }
                }
            }
            return ofer;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
