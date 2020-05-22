package org.sede.servicio.perfilcontratante;


import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;
import org.apache.commons.lang3.StringUtils;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.sede.core.anotaciones.*;
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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Gcz(servicio="PERFILCONTRATANTE", seccion="CONTRATO")
@Controller
@Description("Ayuntamiento: Contratación pública")
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ContratoController.MAPPING, method = RequestMethod.GET)
public class ContratoController {
	//region Atributtes
	private static final String SERVICIO = "contratacion-publica";
	public static final String MAPPING = "servicio/" + SERVICIO;
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
	public EntidadContratanteGenericDAO daoEntidadContratante;
	@Autowired
	public OrganigramaGenericDAO daoOrganigrama;
	@Autowired
	private IndicadorGenericDAO daoIndicadores;
	@Autowired
	private IndicadorLicitadorServicioGenericDAO daoIndicadoresLicitadorServicio;
	@Autowired
	private EmpresaGenericDAO daoEmpresa;
	@Autowired
	private ContratosPorAnyoIdPortalDAO daoContratoAnyoPortal;
	@Autowired
	private ContratosPorAnyoServicioDAO daoContratoAnyoServicio;
	@Autowired
	private IndicadoresPorAnyoServicioDAO daoIndicadoresServicio;
	@Autowired
	private  CpvGenericDAO daoCpv;
	@Autowired
	private IndicadorProcedimientoGenericDAO daoIndicadorProcedimiento;
	@Autowired
	private DatosProcedimientoGenericDAO daoDatosProcedimiento;
	@Autowired
	private ProcedimientoEntityGenericDAO daoProcedimiento;
	@Autowired
	private DatosLicitadorGenericDAO daoLicitadorCuantia;
	@Autowired
	private IndicadorProcedimientoServicioGenericDAO daoIndicadorProcServ;
	@Autowired
	private IndicadorAhorroEntidadGenericDAO daoIndicadorAhorro;
	@Autowired
	private ContratosPorAnyoOrganismoContratanteDAO daoContratoAnyoOrgano;
	@Autowired
	private IndicadorAhorroServicioGenericDAO daoIndicadorAhorroServicio;
	@Autowired
	private IndicadorTipoGenericDAO daoIndicadorTipo;
	@Autowired
	private IndicadorTipoServicioGenericDAO daoIndicadorTipoServicio;
	//endregion
	//region Redirect
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String redirect() {
		return "redirect:" + SERVICIO + "/";
	}
	//endregion
	//region Vistas
	@RequestMapping(value = "/", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String home(Model model, @Fiql SearchFiql search,@RequestParam(name="cpv",defaultValue = "",required = false)BigDecimal cpv,@RequestParam(name="licitador", required = false)BigDecimal idEmpresa) throws SearchParseException, ParseException {
		HashMap<String,String> parametros = new HashMap<String,String>();
		List<String> mapeoParametros=new ArrayList<String>();

		ResponseEntity<?> resultado=apiHome(search,cpv,idEmpresa);
		model.addAttribute(ModelAttr.RESULTADO, resultado);
		String[]inicialParametros = search.getSearchExpression().split(";");
		if(inicialParametros.length > 0 && !"".equals(inicialParametros[0])) {
			for (String item : inicialParametros) {
				if (item.contains("=g")) {
					item = item.replace("=ge", "=");
				}
				if (item.contains("=lt")) {
					item = item.replace("=lt", "=");
				}
				String[] parametrosValues = item.split("==");
				parametros.put(parametrosValues[0], parametrosValues[1]);
			}
			for (String key : parametros.keySet()) {
				if (key.equals("status.id")) {
					if (parametros.get("status.id").equals("0")) {
						mapeoParametros.add("En licitación");
					}
					if (parametros.get("status.id").equals("1")) {
						mapeoParametros.add("Pendiente de Adjudicar");
					}
					if (parametros.get("status.id").equals("2")) {
						mapeoParametros.add("Adjudicación Provisional");
					}
					if (parametros.get("status.id").equals("3")) {
						mapeoParametros.add("Adjudicación Definitiva");
					}
					if (parametros.get("status.id").equals("5")) {
						mapeoParametros.add("Adjudicación");
					}
					if (parametros.get("status.id").equals("6")) {
						mapeoParametros.add("Contrato Formalizado");
					}
					if (parametros.get("status.id").equals("4")) {
						mapeoParametros.add("Suspendida la licitación");
					}
					if (parametros.get("status.id").equals("7")) {
						mapeoParametros.add("Desierto");
					}
					if (parametros.get("status.id").equals("8")) {
						mapeoParametros.add("Renuncia");
					}
					if (parametros.get("status.id").equals("10")) {
						mapeoParametros.add("Cancelado");
					}
					if (parametros.get("status.id").equals("11")) {
						mapeoParametros.add("Desistido");
					}
					if (parametros.get("status.id").equals("9")) {
						mapeoParametros.add("Modificación de Contrato");
					}
					if (parametros.get("status.id").equals("12")) {
						mapeoParametros.add("Resuelto");
					}
				}
				if (key.equals("contratoMenor")) {
					mapeoParametros.add(parametros.get(key).equals(true) ? "Menor" : "Mayor");
				}
				if (key.equals("entity.id")) {
					String result = "";
					switch (Integer.parseInt(parametros.get(key))) {
						case 1:
							result = "Ayuntamiento de Zaragoza";
							break;
						case 2:
							result = "Zaragoza Cultural";
							break;
						case 3:
							result = "Zaragoza Vivienda";
							break;
						case 4:
							result = "Educación y Bibliotecas";
							break;
						case 5:
							result = "Audiorama Zaragoza";
							break;

						case 6:
							result = "Zaragoza Turismo";
							break;
						case 7:
							result = "Zaragoza Dinámica";
							break;
						case 8:
							result = "Zaragoza Deporte";
							break;
						case 10:
							result = "ZGZ @ Desarrollo Expo";
							break;
						case 11:
							result = "Artes Escénicas y de la Imagen";
							break;
						case 12:
							result = "Zaragoza Pirineos";
							break;
						case 14:
							result = "Ecociudad";
							break;
					}
					mapeoParametros.add(result);
				}
				if (key.equals("procedimiento.id")) {
					Procedimiento procedimiento =daoProcedimiento.find(BigDecimal.valueOf(Double.parseDouble(parametros.get(key))));
					mapeoParametros.add(procedimiento.getNombre());
				}
				if (key.equals("type.id")) {
					String result = "";
					switch (Integer.parseInt(parametros.get(key))) {
						case 1:
							result = "Obras";
							break;
						case 2:
							result = "Servicios";
							break;
						case 3:
							result = "Suministros";
							break;
						case 4:
							result = "Otros";
							break;
						case 5:
							result = "Privado";
							break;
					}
					mapeoParametros.add(result);
				}
				if (!key.equals("servicio") && !key.equals("procedimiento.id") && !key.equals("fechaContrato") && !key.equals("status.id") && !key.equals("contratoMenor") && !key.equals("type.id") && !key.equals("entity.id")) {
					mapeoParametros.add(parametros.get(key));
				}
			}


		}
		if(idEmpresa!=null){
			Empresa empresa=daoEmpresa.find(idEmpresa);
			mapeoParametros.add(empresa.getNombre());
		}
		model.addAttribute("parametros_busqueda", mapeoParametros);
		return MAPPING + "/index";
	}
	@RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalle(Model model, @PathVariable BigDecimal id) {
		ResponseEntity<?> resultado = apiDetalle(id);
		model.addAttribute(ModelAttr.RESULTADO, resultado);
		model.addAttribute("servicioGestor", ((Contrato) resultado.getBody()).getServicio());
		if (!((Contrato) resultado.getBody()).getCpv().isEmpty()) {
			model.addAttribute("contratosCpv", dao.getContratosCPV(((Contrato) resultado.getBody()).getCpv()));
		}
		return MAPPING + "/detalle-contrato2";
	}
	@RequestMapping(value = "/licitador/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleEmpresa(Model model, @Fiql SearchFiql search, @PathVariable BigDecimal id) throws SearchParseException {
		model.addAttribute(ModelAttr.RESULTADO,apiDetalleLicitador(id,search));
		return MAPPING + "/detalle-licitador";
	}
	@RequestMapping(value="/servicio-gestor/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleServicio(Model model, @Fiql SearchFiql search, @PathVariable BigDecimal id,@RequestParam(name="statusd.id",defaultValue ="0")BigDecimal estado,@RequestParam(name="year",defaultValue = "",required = false)String year) throws SearchParseException, ParseException {
		model.addAttribute(ModelAttr.RESULTADO, apiDetalleServicioGestor(id, search, year));
		return MAPPING + "/detalle-servicio";
	}
	@RequestMapping(value="/organismo-contratante/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleOrganismoContratante(Model model, @Fiql SearchFiql search, @PathVariable BigDecimal id,@RequestParam(name="statusd.id",defaultValue ="0")BigDecimal estado,@RequestParam(name="year",defaultValue = "",required = false)String year) throws SearchParseException, ParseException {
		model.addAttribute(ModelAttr.RESULTADO, apiDetalleOrganismoContratante(id, search, year));
		return MAPPING + "/detalle-organismo";
	}
	@RequestMapping(value="/mesa-contratacion", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleMesaContratacion()  {
		return MAPPING + "/mesa-contratacion";
	}
	@RequestMapping(value = "/anuncio/{idAnuncio}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE,MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleAnuncio(Model model, @Fiql SearchFiql search, @PathVariable BigDecimal idAnuncio) {
		model.addAttribute("DetalleAnuncio",apiDetalleAnuncio(idAnuncio));
		return MAPPING + "/detalle-anuncio";
	}
	@RequestMapping(value="/indicadores", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MediaType.TEXT_HTML_VALUE,"*/*"})
	public String indicadoresDetalle(Model model,@Fiql SearchFiql search,@RequestParam(name="idPortal", defaultValue = "1", required = true)BigDecimal id,@RequestParam(name="anyo")String year,@RequestParam(name="idServicio",defaultValue = "0")BigDecimal idServicio) throws Exception {
		search.setExcludeFields("idServicio");
		search.getConditions(Indicador.class);
		model.addAttribute("indicadores_Ayutamiento",apiIndicadoresAyuntamiento(search,id,year,idServicio));
		return MAPPING + "/indicadores";
	}
	@RequestMapping(value="/listado-contrato", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String listadoContratos(Model model, @Fiql SearchFiql search,@RequestParam(name="year",defaultValue = "",required = false)String year) throws Exception {
		ResponseEntity<?>result=apiListadosContratos(search,null);
		model.addAttribute(ModelAttr.RESULTADO,result);
		return MAPPING + "/listado-contratos";
	}
	@RequestMapping(value = "/entidad/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*"})
	public String detalleEntidades(Model model, @Fiql SearchFiql search, @PathVariable BigDecimal id, @RequestParam(name = "year", defaultValue = "") String year, @RequestParam(name = "status.id", defaultValue = "0") BigDecimal estado) throws Exception {
		model.addAttribute(ModelAttr.RESULTADO, apiDetalleEntidades(search, id, year, estado));
		return MAPPING + "/detalle-entidades";
	}
	//endregion
	//region apis
	@OpenData
	@ResponseClass(byte[].class)
	@Description("Acceso al documento asociado a un anuncio")
	@RequestMapping(value = "/anuncio/{id}/document", method = RequestMethod.GET, produces = {MimeTypes.PDF})
	public @ResponseBody ResponseEntity<?> apiDoc(@PathVariable BigDecimal id) {
		Anuncio registro = daoAnuncio.find(id);
		if (registro == null) {
			return null;
		} else {

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MimeTypes.getContentTypeFromFileName(registro.getFileName()));
			headers.set("Content-Disposition", "inline; filename=\"" + registro.getFileName() + "\"");
			return new ResponseEntity<byte[]>(daoAnuncio.obtenerAnexos(registro), headers, HttpStatus.OK);
		}
	}
	@SuppressWarnings("unchecked")
	@Description("Detalle de licitador")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Licitador.class)
	@RequestMapping(value = "/licitador/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleLicitador(@PathVariable(value="id") BigDecimal id, @Fiql SearchFiql search) throws SearchParseException {

		Empresa registro = daoEmpresa.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			Licitador licitador = new Licitador();
			Boolean tieneUte=false;
			licitador.setIdEmpresa(registro.getIdEmpresa());
			licitador.setNombre(registro.getNombre());
			licitador.setLibreBorme(registro.getLibreBorme());
			search.setExcludeFields("year");
			search.setSort("fechaContrato desc");
			search.setRows(-1);
			licitador.setLicitados((SearchResult<?>) apiOfertasListar(search, id, false).getBody());
			licitador.setGanados((SearchResult<?>)  apiOfertasListar(search, id,true ).getBody());
			HashMap<String,Integer> licitadosPorAnyo = new HashMap<String, Integer>();
			HashMap<String,Integer> ganadosPorAnyo = new HashMap<String, Integer>();
			SimpleDateFormat dt1 = new SimpleDateFormat("yyyy");
			Integer anyoActual = Integer.valueOf(dt1.format(Calendar.getInstance().getTime()));
			for (Integer k = anyoActual-5; k <= anyoActual; k++) {
				licitadosPorAnyo.put(k.toString(),0);
				ganadosPorAnyo.put(k.toString(),0);
				for (Oferta oferta :((SearchResult<Oferta>) licitador.getLicitados()).getResult()) {
					Integer fecha = Integer.valueOf(dt1.format(oferta.getContrato().getFechaContrato().getTime()));
					if (k.equals(fecha)) {
						licitadosPorAnyo.put(k.toString(),licitadosPorAnyo.get(k.toString()).intValue()+1 );
						if (oferta.getGanador()) {
							ganadosPorAnyo.put(k.toString(),ganadosPorAnyo.get(k.toString()).intValue()+1 );
						}
					}
					if(oferta.getTieneUte()){
						tieneUte=true;
					}
				}
			}
			if(!tieneUte) {
				licitador.setUtes((SearchResult<Empresa>) apiUteEmpresaList2(id).getBody());
			}
			Double  totalSinIva=0.0;
			Double  totalConIva=0.0;
			licitador.setLicitadosPorAnyo((HashMap<String, Integer>) Utils.sortByComparator(licitadosPorAnyo,true));
			licitador.setGanadosPorAnyo((HashMap<String, Integer>) Utils.sortByComparator(ganadosPorAnyo,true));
			Search busquedaCuantia=new Search(DatosLicitador.class);
			busquedaCuantia.addFilterEqual("idEmpresa",id);
			SearchResult<DatosLicitador>resultado=daoLicitadorCuantia.searchAndCount(busquedaCuantia);
			for(DatosLicitador item: resultado.getResult()) {
				if(item.getTotalSinIva()!=null){
					totalSinIva+=item.getTotalSinIva().doubleValue();
				}
				if(item.getTotalConIva()!=null){
					totalConIva+=item.getTotalConIva().doubleValue();
				}
			}
			licitador.setTotalSinIva(BigDecimal.valueOf(totalSinIva));
			licitador.setTotalConIva(BigDecimal.valueOf(totalConIva));
			licitador.setDatosLicitadorCuantia(resultado);
			return ResponseEntity.ok(licitador);
		}
	}
	@Description("Listado Licitadores")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Empresa.class)
	@RequestMapping(value = "/licitador", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiListadoLicitador( @Fiql SearchFiql search) throws SearchParseException {
		SearchResult<Empresa> registro = daoEmpresa.searchAndCount(search.getConditions(Empresa.class));
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			return ResponseEntity.ok(registro);
		}
	}
	@HiddenForSwagger
	@OpenData
	@ResponseClass(value = Contrato.class, entity = SearchResult.class)
	@RequestMapping( method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public	@ResponseBody ResponseEntity<?> apiHome(@Fiql SearchFiql search,@RequestParam(name="cpv",required = false)BigDecimal cpv,@RequestParam(name="licitador",required = false)BigDecimal idEmpresa) throws SearchParseException, ParseException {
		return apiListadosContratos(search,idEmpresa);
	}
	@Description("Listado de Contratos")
	@OpenData
	@ResponseClass(value = Contrato.class, entity = SearchResult.class)
	@RequestMapping( value="/contrato",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public	@ResponseBody ResponseEntity<?> apiListadosContratos(@Fiql SearchFiql search,@RequestParam(name="licitador",required = false)BigDecimal idEmpresa) throws SearchParseException, ParseException {
		search.setExcludeFields("rangoImporte","cpv","year","licitador");
		Map<String, String> contextProperties = new HashMap<String, String>();
		contextProperties.put("search.date-format", ConvertDate.DATE_FORMAT);
		search.setContextProperties(contextProperties);
		if (search.getSearchExpression().isEmpty()) {
			search.addCondition("status.id==0");
		}
		Search busqueda = search.getConditions(Contrato.class);
		if(idEmpresa!=null){
			busqueda.addFilterEqual("ofertas.empresa.idEmpresa",idEmpresa);
		}
		
		SearchResult<Object> resultado=dao.searchAndCount(busqueda);
 			return ResponseEntity.ok(resultado);
	}
	@Description("Indicador Contratos Por Procedimiento")
	@OpenData
	@ResponseClass(value = DatosIndicadoresProcedimiento.class, entity = SearchResult.class)
	@RequestMapping(value = "/indicadores/procedimiento", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiIndicadoresProceDimiento(@Fiql SearchFiql search,@RequestParam(name="idPortal",defaultValue = "1")BigDecimal idPortal,@RequestParam(name="anyo",defaultValue = "")String anyo,@RequestParam(name="idServicio",required=false)BigDecimal idServicio) throws Exception {
		if(idPortal.intValue()==0)idPortal=new BigDecimal(1);
		DatosIndicadoresProcedimiento indicador = new DatosIndicadoresProcedimiento();
		search.setRows(-1);
		if("".equals(anyo)){
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EntidadContratante registro = daoEntidadContratante.find(idPortal);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		SearchResult<BigDecimal> totales=daoIndicadorProcedimiento.totaleContratoPorAnyoPortal(idPortal,anyo);
		search.setExcludeFields("idServicio");
		Search busqueda=search.getConditions(DatosIndicadoresProcedimiento.class);
		busqueda.addFilterEqual("idPortal",idPortal);
		busqueda.addFilterEqual("anyo",anyo);
		busqueda.addSortDesc("total");

		SearchResult<DatosIndicadoresProcedimiento> indicadores=daoDatosProcedimiento.searchAndCount(busqueda);
		for (BigDecimal itemDatos:totales.getResult() ) {
			indicador.setTotalContratos(itemDatos);
		}
		for (DatosIndicadoresProcedimiento item: indicadores.getResult()) {
			item.setTipoProcedimiento(daoProcedimiento.find(item.getIdProcedimiento()));
			BigDecimal total=item.getTotal();
			item.setPorCiento(BigDecimal.valueOf(total.floatValue()/indicador.getTotalContratos().floatValue()*100));
		}
		return ResponseEntity.ok(indicadores);
	}
	@SuppressWarnings("unchecked")
	@Description("Indicador Ayuntamiento")
	@Cache(Cache.DURACION_1MIN)
	@OpenData
	@ResponseClass(value = Indicadores.class, entity = SearchResult.class)
	@RequestMapping(value = "/indicadores", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> 	apiIndicadoresAyuntamiento(@Fiql SearchFiql search,@RequestParam(name="idPortal",defaultValue = "1")BigDecimal id,@RequestParam(name="anyo")String year,@RequestParam(name="idServicio",defaultValue = "0")BigDecimal idServicio) throws Exception {
		Indicadores indicador = new Indicadores();
		Double cuantia=0.0;
		Double cuantiaCanon=0.0;
		Double cuantiaBurbuja=0.0;
		List<BigDecimal> empresas=new ArrayList<BigDecimal>();
		List<BigDecimal> empresasCanon=new ArrayList<BigDecimal>();
		List<IndicadorBurbuja> listadoBurbuja=new ArrayList<IndicadorBurbuja>();
		search.setRows(-1);
		Search busqueda=search.getConditions(Indicador.class);
		Search searchContratos=new Search(Contrato.class);
		searchContratos.addFilterNot(new Filter("type.id",6));
		Search searchContratosCanon =new Search(Contrato.class);
		Search searchExcluidos= new Search(Contrato.class);
		SearchResult<Contrato> listadoContratos;
		SearchResult<Contrato> listadoContratosCanon;
		SearchResult<Contrato> listadoContratosExcluidos;
		indicador.setAnyo(year);
		if(idServicio!=null) {
			indicador.setServicioGestor(daoOrganigrama.find(idServicio));
		}

		EntidadContratante registro = daoEntidadContratante.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			indicador.setId(registro.getId());
			indicador.setTitle(registro.getTitle());
		}
		search.setExcludeFields("year","idServicio");
		SearchResult<Indicador> indicadores=daoIndicadores.searchAndCount(busqueda);
		if (!year.isEmpty()) {
			Calendar c = Calendar.getInstance();
			c.setTime(ConvertDate.string2Date("01-01-" + year, ConvertDate.DATE_FORMAT));
			searchContratos.addFilterAnd(Filter.greaterOrEqual("fechaContrato", c.getTime()));
			searchContratosCanon.addFilterAnd(Filter.greaterOrEqual("fechaContrato", c.getTime()));
			searchExcluidos.addFilterAnd(Filter.greaterOrEqual("fechaContrato", c.getTime()));
			c.setTime(ConvertDate.string2Date("31-12-" + year, ConvertDate.DATE_FORMAT));
			searchContratos.addFilterAnd(Filter.lessOrEqual("fechaContrato", c.getTime()));
			searchContratosCanon.addFilterAnd(Filter.lessOrEqual("fechaContrato", c.getTime()));
			searchExcluidos.addFilterAnd(Filter.lessOrEqual("fechaContrato", c.getTime()));

		}
		searchContratos.addFilterEqual("entity.id",id);
		searchContratosCanon.addFilterEqual("entity.id",id);
		searchExcluidos.addFilterEqual("entity.id",id);
		searchContratos.addFilterEqual("ofertas.ganador",true);
		searchContratosCanon.addFilterEqual("ofertas.ganador",true);
		searchExcluidos.addFilterEqual("ofertas.ganador",true);

		searchContratos.addFilterEqual("ofertas.canon",false);
		searchContratosCanon.addFilterEqual("ofertas.canon",true);
		searchContratos.setDistinct(true);
		searchContratosCanon.setDistinct(true);
		if(id.intValue()==1 && idServicio.intValue()!=0) {
			Search busquedaLicitador=new Search(IndicadorLicitadorServicio.class);
			busquedaLicitador.addFilterEqual("anyo",year);
			busquedaLicitador.addFilterEqual("idServicio",idServicio);
			ResponseEntity<?> resultadoIndicadorProcSer=apiIndicadoreServicioGestor(search,idServicio,year);
			indicador.setIndicadorProcedimientoServicio((SearchResult<IndicadoresProcedimientoServicioGestor>) resultadoIndicadorProcSer.getBody());
			ResponseEntity<?> resultadoLicitadorServicio= apiIndicadoreLicitaorServicioGestor(search,idServicio,year);
			indicador.setIndicadorLicitadorServicio((SearchResult<IndicadorLicitadorServicio>) resultadoLicitadorServicio.getBody());
			searchContratosCanon.addFilterEqual("servicio.id",idServicio);
			searchContratos.addFilterEqual("servicio.id",idServicio);
			listadoContratosCanon =dao.searchAndCount(searchContratosCanon);
			listadoContratos =  dao.searchAndCount(searchContratos);
			indicador.setIndicadorTipoServicio((SearchResult<IndicadoresTipoServicioGestor>)apiIndicadoreIndicadorTipoServicio(id,idServicio,year).getBody());
		}else{
			for ( Indicador item:indicadores.getResult()) {
				item.setTotalGanados(daoIndicadores.consultaTotalGanados(item.getIdEmpresa(),year,item.getIdPortal()));

			}
			indicador.setIndicadorLicitador(indicadores);
			if(id.intValue()==1) {
				SearchResult<IndicadoresPorAnyoServicioGestor> resultadoIndicadorAyto = ((SearchResult<IndicadoresPorAnyoServicioGestor>) apiIndicadoresServicioGestorAnyo(search, year, null).getBody());
				indicador.setIndicadorServicio(resultadoIndicadorAyto);
			}
			ResponseEntity<?> resultadoTipoContrato=apiIndicadoreIndicadorTipo(id,year);
			indicador.setIndicadorTipo((SearchResult<IndicadoresTipo>)resultadoTipoContrato.getBody());
			ResponseEntity<?> resultadoIndicadoresProcedimiento= apiIndicadoresProceDimiento(search,id,year,null);
			indicador.setIndicadorProcedimiento((SearchResult<IndicadoresProcedimiento>) resultadoIndicadoresProcedimiento.getBody() );
			listadoContratosCanon =dao.searchAndCount(searchContratosCanon);
			listadoContratos =  dao.searchAndCount(searchContratos);
		}
		indicador.setContratos(listadoContratos);
		for (Contrato contrato:listadoContratosCanon.getResult()) {
			if (!contrato.getOfertas().isEmpty()) {
				for (Oferta offer : contrato.getOfertas()) {
					if(offer.getGanador() != null) {
						if (offer.getGanador()) {
							cuantiaCanon += offer.getImporteSinIVA().doubleValue();
							if (!empresasCanon.contains(offer.getEmpresa().getIdEmpresa())) {
								empresasCanon.add(offer.getEmpresa().getIdEmpresa());
							}
						}
					}
				}
			}
		}

		for (Contrato contrato:listadoContratos.getResult()){
			if (!contrato.getOfertas().isEmpty() ) {
				for (Oferta offer : contrato.getOfertas()) {
					if (offer.getGanador() != null && offer.getGanador()) {
						cuantia += offer.getImporteSinIVA().doubleValue();
						cuantiaBurbuja += offer.getImporteSinIVA().doubleValue();
						if (!empresas.contains(offer.getEmpresa().getIdEmpresa())) {
							empresas.add(offer.getEmpresa().getIdEmpresa());
						}
					}
				}
			}
			IndicadorBurbuja burbuja=new IndicadorBurbuja();
			burbuja.setCuantia(BigDecimal.valueOf(cuantiaBurbuja));
			burbuja.setIdContrato(contrato.getId());
			if(contrato.getProcedimiento()!=null){
				burbuja.setProcedimiento(contrato.getProcedimiento().getNombre());
			}else{
				burbuja.setProcedimiento("Otro");
			}
			burbuja.setTipo(contrato.getType().getTitle());
			burbuja.setTitle(contrato.getTitle());
			listadoBurbuja.add(burbuja);
			cuantiaBurbuja=0.0;
		}
		SearchResult<IndicadorBurbuja> listadoBur=new SearchResult<IndicadorBurbuja>();
		listadoBur.setRows(listadoBurbuja.size());
		listadoBur.setTotalCount(listadoBurbuja.size());
		listadoBur.setResult(listadoBurbuja);
		indicador.setIndicadorBurbuja(listadoBur);
		indicador.setContratosCanon(listadoContratosCanon);
		indicador.setIndicadorBurbuja(listadoBur);
		searchExcluidos.addFilterEqual("type.id",6);
		listadoContratosExcluidos=dao.searchAndCount(searchExcluidos);
		indicador.setContratosExcluidos(listadoContratosExcluidos);
		if(idServicio.intValue()!=0){
			indicador.setIndicadorAhorroServicio((SearchResult<IndicadorAhorroServicio>) apiIndicadoreIndicadorAhorroServicio(id, year,idServicio).getBody());
		}else {
			indicador.setIndicadorAhorro((SearchResult<IndicadorAhorro>) apiIndicadoreIndicadorAhorroPortal(search, id, year).getBody());
		}
		indicador.setNumEmpresas(empresas.size());
		indicador.setNumEmpresaCanon(empresasCanon.size());
		indicador.setCuantia(BigDecimal.valueOf(cuantia));
		indicador.setCuantiaCanon(BigDecimal.valueOf(cuantiaCanon));
			return ResponseEntity.ok(indicador);
	}
	@HiddenForSwagger
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Contrato.class)
	@RequestMapping(value = "/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalle(@PathVariable BigDecimal id) {
		return apiDetalleContrato(id);
	}
	@OpenData
	@Description("Detalle de contrato")
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Contrato.class)
	@RequestMapping(value = "/contrato/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleContrato(@PathVariable BigDecimal id) {
		Contrato registro = dao.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}else {
			return ResponseEntity.ok(registro);
		}
	}

	@Description("Detalle de servicio gestor")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(ServicioGestor.class)
	@RequestMapping(value = "/servicio-gestor/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleServicioGestor(@PathVariable(value="id") BigDecimal id, @Fiql SearchFiql search, @RequestParam(name="year", required = false) String year) throws SearchParseException, ParseException {
		String anyo=year;
		if (StringUtils.isEmpty(year)) {
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EstructuraOrganizativa registro = daoOrganigrama.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			ServicioGestor servicio = new ServicioGestor();
			servicio.setId(registro.getId());
			servicio.setTitle(registro.getTitle());
			search.setExcludeFields("year");
			search.setSort("fechaContrato desc");
			SearchResult<Contrato>result=apiContratosByServicioListar(search, id, anyo);//
			servicio.setContrato(result);
			Search busqueda = new Search(ContratosPorAnyoServicioGestor.class);
			busqueda.addFilterEqual("id.servicio",id);
			SearchResult<ContratosPorAnyoServicioGestor> res = daoContratoAnyoServicio.searchAndCount(busqueda);
			servicio.setDatos(res.getResult());
			if(!StringUtils.isEmpty(year))servicio.setYear(year);
			return ResponseEntity.ok(servicio);
		}
	}
	@Description("Indicador Servicio gestor año")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(IndicadoresPorAnyoServicioGestor.class)
	@RequestMapping(value = "/indicadores/servicio", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoresServicioGestorAnyo(@Fiql SearchFiql search,@RequestParam(name="anyo",required = true)String year,@RequestParam(name="idServicio",required = false)BigDecimal idServicio) throws SearchParseException {
		search.setRows(-1);
		List<IndicadoresPorAnyoServicioGestor> resultado2=new ArrayList<IndicadoresPorAnyoServicioGestor>();
		search.setExcludeFields("year","idServicio","anyo");
		Search busqueda=search.getConditions(IndicadoresPorAnyoServicioGestor.class);
		busqueda.addFilterEqual("anyo",year);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("total", true));
		busqueda.setSorts(sorts);
		if (idServicio !=null) {
		busqueda.addFilterEqual("idServicio",idServicio);
		}
		SearchResult<?> resultado = daoIndicadoresServicio.searchAndCount(busqueda);
		@SuppressWarnings("unchecked")
		List<IndicadoresPorAnyoServicioGestor> result = (List<IndicadoresPorAnyoServicioGestor>) resultado.getResult();
		for (IndicadoresPorAnyoServicioGestor item : result) {
			if(item !=null) {
				EstructuraOrganizativa entidad = daoOrganigrama.find(item.getIdServicio());
				if(entidad!=null) {
					item.setEntidad(entidad);
					item.setTotalLicitadores(daoContratoAnyoServicio.consultaTotalLicitadore(item.getIdServicio(),year));
					resultado2.add(item);
				}
			}
		}
		SearchResult<IndicadoresPorAnyoServicioGestor> resultadoFinal=new SearchResult<IndicadoresPorAnyoServicioGestor>();
		resultadoFinal.setResult(resultado2);
		resultadoFinal.setStart(0);
		resultadoFinal.setTotalCount(resultado2.size());
		resultadoFinal.setRows(resultado2.size());
		return ResponseEntity.ok(resultadoFinal);
	}
	@Description("Consulta por CPV")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Cpv.class, entity = SearchResult.class)
	@RequestMapping(value = "/cpv/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public	@ResponseBody ResponseEntity<?> apiRelacionadosListar(@Fiql SearchFiql search, @PathVariable BigDecimal id) throws SearchParseException {
		Search busqueda =search.getConditions(Cpv.class);
		busqueda.addFilterEqual("id",id);
		SearchResult<Cpv> resultado=daoCpv.searchAndCount(busqueda);
		return ResponseEntity.ok(resultado);
	}
	@Description("Listado de servicios gestores")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = EstructuraOrganizativa.class, entity = SearchResult.class)
	@RequestMapping(value="/servicio-gestor", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiServiciosListar(@Fiql SearchFiql search) throws SearchParseException {
		return ResponseEntity.ok(daoOrganigrama.searchAndCount(search.getConditions(EstructuraOrganizativa.class)));
	}
	@Description("Detalle de Anuncio")
	@OpenData
	@ResponseClass(Anuncio.class)
	@RequestMapping(value = "/anuncio/{idAnuncio}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleAnuncio(@PathVariable BigDecimal idAnuncio) {
		Anuncio anuncio = daoAnuncio.find(idAnuncio);
		if (anuncio == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			return ResponseEntity.ok(anuncio);
		}
	}
	@Description("Adjudicaciones por id_empresa")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Oferta.class, entity = SearchResult.class)
	@RequestMapping(value="/licitador/{id}/Empresa", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleLicitador(@PathVariable(value = "id") BigDecimal id) {
		Empresa registro = daoEmpresa.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			return ResponseEntity.ok(registro);
		}
	}
	@Description("Adjudicaciones por id_empresa")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Oferta.class, entity = SearchResult.class)
	@RequestMapping(value="/licitador/{idLicitador}/licitaciones", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiOfertasListar(@Fiql SearchFiql search, @PathVariable(value = "idLicitador") BigDecimal idLicitador, @RequestParam(name = "ganador", required = false) Boolean ganador) throws SearchParseException {

		if(!idLicitador.toString().isEmpty()) {
			search.addCondition("empresa.idEmpresa==" + idLicitador);
			search.setSort("ganador desc");
		}
		if (ganador != null && ganador) {
			search.addCondition("ganador==true");
			search.setSort("fechaAdjudicacion desc");

		}
		return ResponseEntity.ok(daoOferta.searchAndCount(search.getConditions(Oferta.class)));
	}
	@Description("Listado de contratos por servicio gestor y anyo")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Contrato.class, entity = SearchResult.class)
	@RequestMapping(value="/servicioGestor/{id}/contratos-por-anyo", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public SearchResult<Contrato> apiContratosByServicioListar(@Fiql SearchFiql search, @PathVariable(value="id") BigDecimal id,@RequestParam(name="year")String year) throws SearchParseException, ParseException {
		search.setExcludeFields("year");
		search.setSort("fechaContrato desc");
		Search busqueda=search.getConditions(Contrato.class);
		if(id.intValue()<100  && id.intValue()!=1){
			busqueda.addFilterEqual("entity.id",id);
			busqueda.addFilterEqual("status.id",0);
		}else {
			busqueda.addFilterAnd(Filter.equal("servicio.id", id));
		}
		if(id.intValue()==1) {
			busqueda =search.getConditions(Contrato.class);
			busqueda.addFilterOr(Filter.equal("entity.id",id),Filter.equal("servicio.id",id));
			if(busqueda.getFilters().size() == 2) {
				busqueda.addFilterEqual("status.id",0);
			}
		}
		if(!year.isEmpty()){
			Calendar inicio = Calendar.getInstance();
			Calendar fin = Calendar.getInstance();
			inicio.setTime(ConvertDate.string2Date("01-01-"+year, ConvertDate.DATE_FORMAT));
			fin.setTime(ConvertDate.string2Date("31-12-"+year,ConvertDate.DATE_FORMAT));
			busqueda.addFilterAnd(Filter.greaterOrEqual("fechaContrato",inicio.getTime()),Filter.lessOrEqual("fechaContrato",fin.getTime()));

		}
		return dao.searchAndCount(busqueda);
		}
	@Permisos(Permisos.DET)
	@Description("Listado de contratos por año y serviciogestor")
	@ResponseClass(value = ContratosPorAnyoServicioGestor.class, entity = SearchResult.class)
	@RequestMapping(value = "/servicioGestor/{id}/datos-licitacion-por-anyo", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV})
	public @ResponseBody ResponseEntity<?> apiContratosPorAnyoServicioGestor(@PathVariable BigDecimal id) {
		Search busqueda= new Search(ContratosPorAnyoServicioGestor.class);
		busqueda.addFilterEqual("id.servicio",id);
		return ResponseEntity.ok(daoContratoAnyoServicio.searchAndCount(busqueda));
	}
	@OpenData
	@Description("Listado de empresas adjudicatarias")
	@ResponseClass(value = Empresa.class, entity = SearchResult.class)
	@RequestMapping(value = "/adjudicador", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV})
	public @ResponseBody ResponseEntity<?> apiListadoAdjudicador() {
		return ResponseEntity.ok(daoEmpresa.findAdjudicadores());
	}
	@Description("Detalle de empresa adjudicataria")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Empresa.class)
	@RequestMapping(value = "/adjudicador/{id}", method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleAdjudicador(@PathVariable BigDecimal id) {
		Empresa registro = daoEmpresa.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			List<Contrato> resultadoContrato=daoEmpresa.findContratosAdjudicados(id);
//			for(Contrato item:resultadoContrato) {
//				EstructuraOrganizativa estructura=daoOrganigrama.find(item.getOrgano());
//				if(estructura!=null) {
//					item.setOrganoContratante(estructura);
//				}
//			}
			registro.setContratos(resultadoContrato);
			return ResponseEntity.ok(registro);
		}
	}
	@Description("Detalle ute asociado a una entidad")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Empresa.class, entity = Ute.class)
	@RequestMapping(value="/ute/{id}",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiUteEmpresa(@PathVariable BigDecimal id) {
		Empresa registro = daoEmpresa.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			registro.setEmpresaUtes(daoEmpresa.findEmpresaUte(id));
			return ResponseEntity.ok(registro);
		}
	}
	@Description("Listado utes ")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Empresa.class)
	@RequestMapping(value="/ute",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiUteEmpresaList() {
		return ResponseEntity.ok(daoEmpresa.findEmpresaUte());
	}
	@HiddenForSwagger
	@ResponseClass(value = Empresa.class)
	@RequestMapping(value="/utes/{id}",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiUteEmpresaList2(@RequestParam(name="id")BigDecimal id) {
		return ResponseEntity.ok(daoEmpresa.findEmpresaPertneceUte(id));
	}
	@Description("Listado Entidades ")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = EntidadContratante.class)
	@RequestMapping(value="/entidad",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiEntidadesListado() {
		List<EntidadContratante> listado= daoEntidadContratante.findAll();
		SearchResult<EntidadContratante> resultado=new SearchResult<EntidadContratante>();
		resultado.setResult(listado);
		resultado.setStart(0);
		resultado.setTotalCount(listado.size());
		resultado.setRows(listado.size());
		return ResponseEntity.ok(resultado);
	}
	@Description("Detalle de entidad")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = EntidadContratante.class, entity = SearchResult.class)
	@RequestMapping(value="/entidad/{id}",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiDetalleEntidades( @Fiql SearchFiql search,@PathVariable BigDecimal id,@RequestParam(name="year",defaultValue = "")String year,@RequestParam(name="status.id",defaultValue = "0")BigDecimal estado) throws SearchParseException, ParseException {
		String anyo=year;
		if (StringUtils.isEmpty(year)) {
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EntidadContratante registro = daoEntidadContratante.find(id);
		List<EstructuraOrganizativa> listaServicio=new ArrayList<EstructuraOrganizativa>();
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			search.setExcludeFields("year");
			search.setSort("fechaContrato desc");
			Search busqueda=search.getConditions(Contrato.class);
			if(year.isEmpty()){
				Calendar inicio = Calendar.getInstance();
				Calendar fin = Calendar.getInstance();
				inicio.setTime(ConvertDate.string2Date("01-01-"+anyo, ConvertDate.DATE_FORMAT));
				fin.setTime(ConvertDate.string2Date("31-12-"+anyo,ConvertDate.DATE_FORMAT));
				busqueda.addFilterAnd(Filter.greaterOrEqual("fechaContrato",inicio.getTime()),Filter.lessOrEqual("fechaContrato",fin.getTime()));

			}else {
				Calendar inicio = Calendar.getInstance();
				Calendar fin = Calendar.getInstance();
				inicio.setTime(ConvertDate.string2Date("01-01-"+anyo, ConvertDate.DATE_FORMAT));
				fin.setTime(ConvertDate.string2Date("31-12-"+anyo,ConvertDate.DATE_FORMAT));
				busqueda.addFilterAnd(Filter.greaterOrEqual("fechaContrato",inicio.getTime()),Filter.lessOrEqual("fechaContrato",fin.getTime()));

			}
			busqueda.addFilterEqual("entity.id",id);
			if(estado.intValue()==0){
				busqueda.addFilterEqual("status.id",estado);
			}
			SearchResult<Contrato> resultadoContratos=dao.searchAndCount(busqueda);
			for(Contrato item:resultadoContratos.getResult())
			{
//				if(item.getOrgano()!=null ) {
//
//						EstructuraOrganizativa struct = daoOrganigrama.find(item.getOrgano());
//						if (struct != null) {
//							item.setOrganoContratante(struct);
//						}
//				}
			}
			registro.setListadoContratoEntidad(resultadoContratos.getResult());
			List<BigDecimal> resultadoServicio=dao.getServicioGestor(id);
			for (int i=0;i<resultadoServicio.size();i++){
				if(resultadoServicio.get(i)!=null) {
					EstructuraOrganizativa resutEs=daoOrganigrama.find(resultadoServicio.get(i));
					if(resutEs!=null) {
						listaServicio.add(resutEs);
					}
				}
			}
			/*List<BigDecimal> resultadoOrgano=dao.getOrganismoContratante(id);
			for (int i=0;i<resultadoOrgano.size();i++){
				if(resultadoOrgano.get(i)!=null) {
					listaOrganos.add(daoOrganigrama.find(resultadoOrgano.get(i)));
				}
			}*/
			Search busquedaDatos= new Search(ContratosPorAnyoIdPortal.class);
			busquedaDatos.addFilterEqual("id.idPortal", id);
			ContratosPorAnyoIdPortalID entidad=new ContratosPorAnyoIdPortalID();
			entidad.setIdPortal(id);

			SearchResult<ContratosPorAnyoIdPortal> res = daoContratoAnyoPortal.searchAndCount(busquedaDatos);
			registro.setDatosPortal(res.getResult());
			busqueda.addFilterEqual("status.id",0);
			busqueda.addFilterEqual("entity.id",id);
			registro.setServicioGestores(listaServicio);
			if(!StringUtils.isEmpty(year))
				registro.setAnyo(year);
			else
				registro.setAnyo(anyo);
			SearchResult<EntidadContratante> result=new SearchResult<EntidadContratante>();
			List<EntidadContratante> datos=new ArrayList<EntidadContratante>();
			datos.add(registro);
			result.setStart(0);
			result.setResult(datos);
			result.setRows(datos.size());
			result.setTotalCount(datos.size());
			return ResponseEntity.ok(result);
		}
	}
	@Description("Listado cpv ")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Cpv.class)
	@RequestMapping(value="/cpv",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiListadoCpv(@Fiql SearchFiql search) throws SearchParseException {
		Funciones.getPeticion().setSelectedFields("id,titulo");
		return ResponseEntity.ok(daoCpv.searchAndCount(search.getConditions(Cpv.class)));
	}
	@Description("Listado Organos de Contratación ")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = EstructuraOrganizativa.class)
	@RequestMapping(value="/organo-contratacion",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiListadoOrgano(@Fiql SearchFiql search) {
		List<String> resultado=dao.getOrganismoContratante();
		List<EstructuraOrganizativa> lista=new ArrayList<EstructuraOrganizativa>();
		List<BigDecimal> ids = new ArrayList<BigDecimal>();
		for (String dato:resultado){
			ids.add(new BigDecimal(dato));
		}
		EstructuraOrganizativa[] estructuras = daoOrganigrama.find(ids.toArray(new BigDecimal[ids.size()])); 
		for (EstructuraOrganizativa estruc : estructuras) {
			if (estruc != null) {
				lista.add(estruc);
			}
		}
		SearchResult<EstructuraOrganizativa> result=new SearchResult<EstructuraOrganizativa>();
		result.setResult(lista);
		result.setTotalCount(lista.size());
		result.setStart(0);
		result.setRows(lista.size());
		return ResponseEntity.ok(result);
	}
	@Description("Detalle Organos de Contratación ")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = EstructuraOrganizativa.class)
	@RequestMapping(value="/organo-contratacion/{id}",method = {RequestMethod.GET, RequestMethod.HEAD}, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiDetalleOrgano(@PathVariable BigDecimal id) {
		EstructuraOrganizativa organo= daoOrganigrama.find(id);
		return ResponseEntity.ok(organo);
	}
	@Description("Indicadores Procedimiento por Servicio Gestor")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadoresProcedimientoServicioGestor.class)
	@RequestMapping(value="/indicador/procedimientoServicio",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreServicioGestor(@Fiql SearchFiql search,@RequestParam(name="idServicio") BigDecimal idServicio,@RequestParam(name="anyo")String anyo) throws SearchParseException {

		IndicadoresProcedimientoServicioGestor indicador = new IndicadoresProcedimientoServicioGestor();
		search.setRows(-1);
		if("".equals(anyo)){anyo = "" + Calendar.getInstance().get(Calendar.YEAR);}
		EstructuraOrganizativa registro = daoOrganigrama.find(idServicio);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		SearchResult<BigDecimal> totales=daoIndicadorProcServ.totaleContratoPorAnyoPorServicio(idServicio,anyo);
		search.setExcludeFields("idPortal","year");
		search.setSearchExpression("");

		Search busqueda=search.getConditions(IndicadoresProcedimientoServicioGestor.class);
		busqueda.addFilterEqual("idServicio",idServicio);
		busqueda.addFilterEqual("anyo",anyo);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("total", true));
		busqueda.setSorts(sorts);
		List<Contrato> listadoContratos = new ArrayList<Contrato>();
		SearchResult<IndicadoresProcedimientoServicioGestor> indicadores=daoIndicadorProcServ.searchAndCount(busqueda);
		for (BigDecimal itemDatos:totales.getResult() ) {
			indicador.setTotalContratos(itemDatos);
		}
		for (IndicadoresProcedimientoServicioGestor item: indicadores.getResult()) {
			item.setTipoProcedimiento(daoProcedimiento.find(item.getTipo()));
			item.setEntidad(daoOrganigrama.find(item.getIdServicio()));
			BigDecimal total=item.getTotal();
			item.setPorCiento(BigDecimal.valueOf(total.floatValue()/indicador.getTotalContratos().floatValue()*100));
		}
		indicador.setContratos(listadoContratos);
		return ResponseEntity.ok(indicadores);
	}
	@Description("Indicadores Licitador por Servicio Gestor")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadorLicitadorServicio.class)
	@RequestMapping(value="/indicador/licitadorServicio",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreLicitaorServicioGestor(@Fiql SearchFiql search,@RequestParam(name="idServicio") BigDecimal idServicio,@RequestParam(name="anyo")String anyo) throws SearchParseException {
		search.setRows(-1);
		if("".equals(anyo)) {
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EstructuraOrganizativa registro = daoOrganigrama.find(idServicio);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		search.setExcludeFields("idPortal","year");
		search.setSearchExpression("");

		Search busqueda=search.getConditions(IndicadorLicitadorServicio.class);
		busqueda.addFilterEqual("idServicio",idServicio);
		busqueda.addFilterEqual("anyo",anyo);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("totalGanados", true));
		busqueda.setSorts(sorts);

		SearchResult<IndicadorLicitadorServicio> indicadores=daoIndicadoresLicitadorServicio.searchAndCount(busqueda);
		for (IndicadorLicitadorServicio item :indicadores.getResult()) {
			item.setTotalicitadciones(daoIndicadoresLicitadorServicio.consultaTotalLicitaciones(item.getIdEmpresa(),item.getIdServicio(),item.getAnyo()));
		}
		return ResponseEntity.ok(indicadores);
	}
	@Description("Indicadores Ahorro por Entidad")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadorAhorro.class)
	@RequestMapping(value="/indicador/indicadorAhorro",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreIndicadorAhorroPortal(@Fiql SearchFiql search,@RequestParam(name="idPortal") BigDecimal idPortal,@RequestParam(name="anyo")String anyo) throws SearchParseException {
		search.setRows(-1);
		if("".equals(anyo)){
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EntidadContratante registro = daoEntidadContratante.find(idPortal);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		Search busqueda=search.getConditions(IndicadorAhorro.class);
		busqueda.addSortDesc("ahorro",true);
		SearchResult<IndicadorLicitadorServicio> indicadores=daoIndicadorAhorro.searchAndCount(busqueda);
		return ResponseEntity.ok(indicadores);
	}
	//@Description("Indicadores Ahorro por Servicio Gestor")
	@HiddenForSwagger
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadorAhorroServicio.class)
	@RequestMapping(value="/indicador/indicadorAhorroServicio",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreIndicadorAhorroServicio(@RequestParam(name="idPortal") BigDecimal idPortal,@RequestParam(name="anyo")String anyo,@RequestParam(name="idServicio") BigDecimal idServicio) throws SearchParseException {
		SearchFiql search=new SearchFiql();
		search.setRows(-1);
		if("".equals(anyo)){
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EntidadContratante registro = daoEntidadContratante.find(idPortal);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		search.setExcludeFields("idServicio","idPortal","year");
		Search busqueda=search.getConditions(IndicadorAhorroServicio.class);
		busqueda.addSortDesc("ahorro",true);
		busqueda.addFilterEqual("servicioGestor.id",idServicio);
		busqueda.addFilterEqual("anyo",anyo);
		SearchResult<IndicadorLicitadorServicio> indicadores=daoIndicadorAhorroServicio.searchAndCount(busqueda);
		return ResponseEntity.ok(indicadores);
	}
	//@Description("Indicadores Ahorro por Servicio Gestor")
	@HiddenForSwagger
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadoresTipo.class)
	@RequestMapping(value="/indicadores/indicadortipocontrato",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreIndicadorTipo(@RequestParam(name="idPortal") BigDecimal idPortal,@RequestParam(name="anyo")String anyo) throws SearchParseException {
		SearchFiql search=new SearchFiql();
		IndicadoresTipo indicador = new IndicadoresTipo();
		search.setRows(-1);
		if("".equals(anyo)){anyo = "" + Calendar.getInstance().get(Calendar.YEAR);}
		EntidadContratante registro = daoEntidadContratante.find(idPortal);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		SearchResult<BigDecimal> totales=daoIndicadorTipo.totaleContratoPorAnyoPortal(idPortal,anyo);
		search.setExcludeFields("idPortal","year");
		search.setSearchExpression("");
		Search busqueda=search.getConditions(IndicadoresTipo.class);
		busqueda.addFilterEqual("idPortal",idPortal);
		busqueda.addFilterEqual("anyo",anyo);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("total", true));
		busqueda.setSorts(sorts);
		List<Contrato> listadoContratos = new ArrayList<Contrato>();
		SearchResult<IndicadoresTipo> indicadores=daoIndicadorTipo.searchAndCount(busqueda);
		for (BigDecimal itemDatos:totales.getResult() ) {
			indicador.setTotalContratos(itemDatos);
		}
		for (IndicadoresTipo item: indicadores.getResult()) {
			item.setTipoContrato(daoTipoContrato.find(item.getTipo()));
			item.setEntidad(daoEntidadContratante.find(item.getIdPortal()));
			BigDecimal total=item.getTotal();
			item.setPorCiento(BigDecimal.valueOf(total.floatValue()/indicador.getTotalContratos().floatValue()*100));
		}
		indicador.setContratos(listadoContratos);
		return ResponseEntity.ok(indicadores);
	}
	@HiddenForSwagger
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = IndicadoresTipoServicioGestor.class)
	@RequestMapping(value="/indicadores/indicadortipocontratoServicio",method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD})
	public @ResponseBody ResponseEntity<?> apiIndicadoreIndicadorTipoServicio(@RequestParam(name="idPortal") BigDecimal idPortal,@RequestParam(name="idServicio")BigDecimal idServicio,@RequestParam(name="anyo")String anyo) throws SearchParseException {
		SearchFiql search=new SearchFiql();
		IndicadoresTipo indicador = new IndicadoresTipo();
		search.setRows(-1);
		if("".equals(anyo)){anyo = "" + Calendar.getInstance().get(Calendar.YEAR);}
		EstructuraOrganizativa registro = daoOrganigrama.find(idPortal);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		}
		SearchResult<BigDecimal> totales=daoIndicadorTipoServicio.totaleContratoPorAnyoPorServicio(idServicio,anyo);
		search.setExcludeFields("idPortal","year","idServicio");
		search.setSearchExpression("");
		Search busqueda=search.getConditions(IndicadoresTipoServicioGestor.class);
		busqueda.addFilterEqual("anyo",anyo);
		busqueda.addFilterEqual("idServicio",idServicio);
		List<Sort> sorts = new ArrayList<Sort>();
		sorts.add(new Sort("total", true));
		busqueda.setSorts(sorts);
		List<Contrato> listadoContratos = new ArrayList<Contrato>();
		SearchResult<IndicadoresTipoServicioGestor> indicadores=daoIndicadorTipoServicio.searchAndCount(busqueda);
		for (BigDecimal itemDatos:totales.getResult() ) {
			indicador.setTotalContratos(itemDatos);
		}
		for (IndicadoresTipoServicioGestor item: indicadores.getResult()) {
			item.setTipoContrato(daoTipoContrato.find(item.getTipo()));
			item.setEntidad(daoEntidadContratante.find(item.getIdServicio()));
			BigDecimal total=item.getTotal();
			item.setPorCiento(BigDecimal.valueOf(total.floatValue()/indicador.getTotalContratos().floatValue()*100));
		}
		indicador.setContratos(listadoContratos);
		return ResponseEntity.ok(indicadores);
	}
	@Description("Detalle de servicio gestor")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(ServicioGestor.class)
	@RequestMapping(value = "/organismo-contratante/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalleOrganismoContratante(@PathVariable(value="id") BigDecimal id, @Fiql SearchFiql search, @RequestParam(name="year", required = false) String year) throws SearchParseException, ParseException {
		String anyo=year;
		if (StringUtils.isEmpty(year)) {
			anyo = "" + Calendar.getInstance().get(Calendar.YEAR);
		}
		EstructuraOrganizativa registro = daoOrganigrama.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			OrganismoContratante servicio = new OrganismoContratante();
			servicio.setId(registro.getId());
			servicio.setTitle(registro.getTitle());
			search.setExcludeFields("year");
			search.setSort("fechaContrato desc");
			SearchResult<Contrato>result=apiContratosByOrganismoContratanteListar(search,id,anyo);
//			for (Contrato item:result.getResult()) {
//				if(item.getServicioGestor()!=null) {
//					item.setServicio(daoOrganigrama.find(item.getServicioGestor()));				}
//				if(item.getOrgano()!=null){
//					EstructuraOrganizativa struct=daoOrganigrama.find(item.getOrgano());
//					if(struct!=null) {
//						item.setOrganoContratante(struct);
//					}
//				}
//			}
			servicio.setContrato(result);
			Search busqueda = new Search(ContratosPorAnyoOrganismoContratante.class);
			busqueda.addFilterEqual("id.organo",id);
			SearchResult<ContratosPorAnyoOrganismoContratante> res = daoContratoAnyoOrgano.searchAndCount(busqueda);
			servicio.setDatos(res.getResult());
			if(!StringUtils.isEmpty(year))
				servicio.setYear(year);
			else servicio.setYear(anyo);
			return ResponseEntity.ok(servicio);
		}
	}
	@Description("Listado de contratos por Organismo Contratante y anyo")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Contrato.class, entity = SearchResult.class)
	@RequestMapping(value="/organismoContratante/{id}/contratos-por-anyo", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public SearchResult<Contrato> apiContratosByOrganismoContratanteListar(@Fiql SearchFiql search, @PathVariable(value="id") BigDecimal id,@RequestParam(name="year")String year) throws SearchParseException, ParseException {
		search.setExcludeFields("year");
		search.setSort("fechaContrato desc");
		Search busqueda=search.getConditions(Contrato.class);
		busqueda.addFilter(Filter.equal("organoContratante.id", id));
		if(!year.isEmpty()){
			Calendar inicio = Calendar.getInstance();
			Calendar fin = Calendar.getInstance();
			inicio.setTime(ConvertDate.string2Date("01-01-"+year, ConvertDate.DATE_FORMAT));
			fin.setTime(ConvertDate.string2Date("31-12-"+year,ConvertDate.DATE_FORMAT));
			busqueda.addFilterAnd(Filter.greaterOrEqual("fechaContrato",inicio.getTime()),Filter.lessOrEqual("fechaContrato",fin.getTime()));
		}else{
			year= "" + Calendar.getInstance().get(Calendar.YEAR);
			Calendar inicio = Calendar.getInstance();
			Calendar fin = Calendar.getInstance();
			inicio.setTime(ConvertDate.string2Date("01-01-"+year, ConvertDate.DATE_FORMAT));
			fin.setTime(ConvertDate.string2Date("31-12-"+year,ConvertDate.DATE_FORMAT));
			busqueda.addFilterAnd(Filter.greaterOrEqual("fechaContrato",inicio.getTime()),Filter.lessOrEqual("fechaContrato",fin.getTime()));
		}
		return dao.searchAndCount(busqueda);
	}
	//endregion
	@Permisos(Permisos.DOC)
	@NoCache
	@ResponseClass(Mensaje.class)
	@RequestMapping(value = "/check-virtuoso", method = RequestMethod.GET, produces = { MimeTypes.JSON })
	public @ResponseBody ResponseEntity<?> apiCheckContratosEnVirtuoso() throws ParseException {
		return dao.updateFromVirtuoso();

	}
}
