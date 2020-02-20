package org.sede.servicio.organigrama;

import java.math.BigDecimal;
import java.util.List;

import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.sede.core.anotaciones.Cache;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.anotaciones.Fiql;
import org.sede.core.anotaciones.Gcz;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.OpenData;
import org.sede.core.anotaciones.Permisos;
import org.sede.core.anotaciones.ResponseClass;
import org.sede.core.anotaciones.TestValue;
import org.sede.core.dao.SearchFiql;
import org.sede.core.dao.VirtuosoDataManagement;
import org.sede.core.rest.Formato;
import org.sede.core.rest.Mensaje;
import org.sede.core.rest.MimeTypes;
import org.sede.core.rest.Peticion;
import org.sede.core.rest.view.TransformadorRdf;
import org.sede.core.utils.Funciones;
import org.sede.servicio.ModelAttr;
import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.googlecode.genericdao.search.SearchResult;

@Controller
@Gcz(servicio="ORGA",seccion="CENTRO")
@Transactional(ConfigOrganigrama.TM)
@RequestMapping(value = "/" + OrganigramaController.MAPPING, method = RequestMethod.GET)
@Description("Ayuntamiento: Organización municipal")
public class OrganigramaController {
	private static final String SERVICIO = "organigrama";
	private static final Logger logger = LoggerFactory.getLogger(OrganigramaController.class);
	public static final String MAPPING = "servicio/" + SERVICIO;

	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	private OrganigramaGenericDAO dao;
	
	@RequestMapping(method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String redirect() {
		return "redirect:" + SERVICIO + "/";
	}
	
	@OpenData
	@Cache(Cache.DURACION_1DIA)
	@Description("Listado de estructuras organizativas")
	@ResponseClass(value = EstructuraOrganizativa.class, entity = SearchResult.class)
	@RequestMapping(method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiListar(
			@Fiql SearchFiql search
    		) throws SearchParseException {
		return ResponseEntity.ok(dao.searchAndCount(search.getConditions(EstructuraOrganizativa.class)));
    }
	@OpenData
	@Cache(Cache.DURACION_1DIA)
	@Description("Detalle de una estructura organizativa")
	@ResponseClass(EstructuraOrganizativa.class)
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalle(@PathVariable @TestValue("1") Integer id) {
		EstructuraOrganizativa registro = dao.find(new BigDecimal(id));
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			registro.setNombreUnidadSuperior(dao.obtenerNombreUnidadSuperior(registro));
			return ResponseEntity.ok(registro);
		}
	}
	@RequestMapping(path ="/", method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String home(Model model) {
		model.addAttribute(ModelAttr.REGISTRO, this.apiDetalle(1));
		model.addAttribute("resultadoHijos", dao.listadoHijos(1));
		return MAPPING + "/index";
	}
	@RequestMapping(value="/list", method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String list(Model model, @RequestParam(name="q") String q) {
		model.addAttribute(ModelAttr.RESULTADO, dao.query(q));
		return MAPPING + "/list";
	}
	@RequestMapping(value = "/{id}", method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String detalle(@PathVariable Integer id, Model model) {
		model.addAttribute(ModelAttr.REGISTRO, this.apiDetalle(id));
		model.addAttribute("resultadoHijos", dao.listadoHijos(id));
		return MAPPING + "/index";
	}
	
	@RequestMapping(value = "/virtuoso", method = RequestMethod.PUT, produces = {MimeTypes.JSON})
    @Permisos(Permisos.PUB)
    public @ResponseBody ResponseEntity<?> actualizarVirtuoso() {
		Peticion peticion = Funciones.getPeticion();
		Formato original = peticion.getFormato();
		int rows = VirtuosoDataManagement.ROWS_CARGA;
		int maxrowscarga = VirtuosoDataManagement.TOTAL_CARGA;
    	try {
    		peticion.setCargandoEnVirtuoso(true);
    		peticion.setFormato(MimeTypes.TURTLE_FORMATO);
			int i = rows;
			int start = 0;			
			VirtuosoDataManagement.deleteGrafo(EstructuraOrganizativa.class.getAnnotation(Grafo.class).value());
			while (i < maxrowscarga) {
				SearchFiql search = new SearchFiql();
				search.setStart(start);
				search.setRows(rows);
				search.addCondition("statusInt==V");
				List<EstructuraOrganizativa> lista = dao.search(search.getConditions(EstructuraOrganizativa.class));
				SearchResult<EstructuraOrganizativa> listado = new SearchResult<EstructuraOrganizativa>();
				listado.setResult(lista);
				i = i + rows;
				start = start + rows;
				if (!listado.getResult().isEmpty()) {
					TransformadorRdf transformador = new TransformadorRdf();
					StringBuilder respuesta = new StringBuilder();
					transformador.transformarObjeto(respuesta, listado, peticion, true, "");
					VirtuosoDataManagement.addElements(EstructuraOrganizativa.class.getAnnotation(Grafo.class).value(), respuesta.toString());
				} else {
					i = maxrowscarga;
				}
			}
			return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(), "Informacion actualizada correctamente"));
    	} catch (Exception e) {
    		logger.error(e.getMessage());
    		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(),
					e.getMessage()));
    	} finally {
    		peticion.setCargandoEnVirtuoso(false);
    		peticion.setFormato(original);
    	}
    }
	
}
