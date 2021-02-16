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
package org.sede.servicio.perfilcontratante;

import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.sede.core.anotaciones.Cache;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.anotaciones.Fiql;
import org.sede.core.anotaciones.Gcz;
import org.sede.core.anotaciones.NoCache;
import org.sede.core.anotaciones.OpenData;
import org.sede.core.anotaciones.Permisos;
import org.sede.core.anotaciones.ResponseClass;
import org.sede.core.anotaciones.TestValue;
import org.sede.core.dao.EntidadBase;
import org.sede.core.dao.SearchFiql;
import org.sede.core.rest.Mensaje;
import org.sede.core.rest.MimeTypes;
import org.sede.core.utils.Funciones;
import org.sede.servicio.ModelAttr;
import org.sede.servicio.perfilcontratante.dao.EmpresaGenericDAO;
import org.sede.servicio.perfilcontratante.dao.UteGenericDAO;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.Licitador;
import org.sede.servicio.perfilcontratante.entity.Ute;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;

@Gcz(servicio="PERFILCONTRATANTE", seccion="CONTRATO")
@Controller
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + EmpresaAdminController.MAPPING, method = RequestMethod.GET)
public class EmpresaAdminController {
	//region Atributtes
	private static final String RAIZ = "contratacion-publica/admin/empresa/";
	public static final String MAPPING = "servicio/" + RAIZ;
	private static final String MAPPING_FORM = MAPPING + "/formulario";
	private static final String MAPPING_UTE_FORM = MAPPING + "/ute";
	//endregion
	@Autowired
	private MessageSource messageSource;
	
	@Autowired
	public EmpresaGenericDAO dao;
	@Autowired
	public UteGenericDAO daoUte;
	
	
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(value = Empresa.class, entity = SearchResult.class)
	@RequestMapping(method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiListar(@Fiql SearchFiql search) throws org.apache.cxf.jaxrs.ext.search.SearchParseException{
		Search busqueda = search.getConditions(Empresa.class);
		busqueda.addSortAsc("nombre");
		return ResponseEntity.ok(dao.searchAndCount(busqueda));
	}

    @Permisos(Permisos.DET)
	@RequestMapping(path = "/", method = RequestMethod.GET, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String home(Model model, @Fiql SearchFiql search) throws org.apache.cxf.jaxrs.ext.search.SearchParseException {

		model.addAttribute(ModelAttr.RESULTADO, apiListar(search));

		return MAPPING + "index";
	}

	
	@RequestMapping(method = RequestMethod.POST, consumes = { MimeTypes.JSON,
			MimeTypes.XML }, produces = { MimeTypes.JSON, MimeTypes.XML })
	@Permisos(Permisos.NEW)
	@Description("Crear registro")
	@ResponseClass(value = Empresa.class)
	public @ResponseBody ResponseEntity<?> apiCrear(
			@RequestBody Empresa registro) {
		Set<ConstraintViolation<Object>> errores = dao.validar(registro);
		if (!errores.isEmpty()) {
			return Funciones.generarMensajeError(errores);
		}
		if(registro.getTipoEmpresa().getId()==null){
			registro.setTipoEmpresa(null);
		}
		dao.save(registro);
		dao.flush();
		return ResponseEntity.ok(registro);
	}

	@RequestMapping(value = "/{id}", method = RequestMethod.PUT, consumes = {
			MimeTypes.JSON, MimeTypes.XML }, produces = { MimeTypes.JSON,
			MimeTypes.XML })
	@Permisos(Permisos.MOD)
	@Description("Modificar registro")
	@ResponseClass(value = Empresa.class)
	public @ResponseBody ResponseEntity<?> apiModificar(@PathVariable BigDecimal id, @RequestBody Empresa registro) {
		ResponseEntity<?> resp = apiDetalle(id);
		if (resp.getStatusCode().is2xxSuccessful()) {
			Empresa reg = (Empresa) resp.getBody();
			if(registro.getTipoEmpresa().getId()==null){
				registro.setTipoEmpresa(null);
			}
			EntidadBase.combinar(reg, registro);
			reg.setIdEmpresa(id);
			Set<ConstraintViolation<Object>> errores = dao.validar(registro);
			if (!errores.isEmpty()) {
				return Funciones.generarMensajeError(errores);
			} else {
				dao.save(reg);
			}
			return ResponseEntity.ok(reg);
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body((Mensaje) resp.getBody());
		}
	}

	@RequestMapping(value = "/{id}/remove", method = RequestMethod.DELETE, produces = { MimeTypes.JSON,
			MimeTypes.XML })
	@Permisos(Permisos.DEL)
	@Description("Eliminar registro")
	@ResponseClass(value = Mensaje.class)
	public @ResponseBody ResponseEntity<?> apiDelete(@PathVariable BigDecimal id) {
		if (dao.removeById(id)) {
			return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(),
					"Registro eliminado"));
		} else {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Registro no eliminado");
		}
	}



	@Permisos(Permisos.NEW)
	@RequestMapping(value = "/new", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String newForm(Empresa dato, BindingResult bindingResult,
						  Model model) {
		model.addAttribute(ModelAttr.DATO, dato);
		model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Empresa()));
		return MAPPING_FORM;
	}

	@Permisos(Permisos.MOD)
	@NoCache
	@RequestMapping(value = "/{id}/edit", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String edit(@PathVariable BigDecimal id, Empresa dato,
					   BindingResult bindingResult, Model model) {
		ResponseEntity<?> registro = apiDetalle(id);
		model.addAttribute(ModelAttr.DATO, registro.getBody());
		model.addAttribute(ModelAttr.REGISTRO, registro);
		return MAPPING_FORM;
	}

	@Permisos(Permisos.NEW)
	@RequestMapping(value = "/save", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String crear(Empresa dato, BindingResult bindingResult, Model model, RedirectAttributes attr) {
		ResponseEntity<?> resultado = apiCrear(dato);
		if (resultado.getStatusCode().is2xxSuccessful()) {

			attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro creado correctamente"));
			attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
			attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
			return "redirect:/" + MAPPING + ((Empresa)resultado.getBody()).getIdEmpresa() + "/edit";
		} else {
			model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
			model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
			model.addAttribute(ModelAttr.DATO, dato);
		}
		return MAPPING_FORM;
	}

	@Permisos(Permisos.MOD)
	@RequestMapping(value = "/{id}/save", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String modificar(@PathVariable BigDecimal id, Empresa dato,
							BindingResult bindingResult, Model model, RedirectAttributes attr) {
		ResponseEntity<?> resultado = apiModificar(id, dato);
		if (resultado.getStatusCode().is2xxSuccessful()) {
			attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro modificado correctamente"));
			attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
			attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
			return "redirect:/" + MAPPING + id + "/edit";
		} else {
			model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
			model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
			model.addAttribute(ModelAttr.DATO, dato);
			return MAPPING_FORM;
		}
	}

	@Permisos(Permisos.DEL)
	@RequestMapping(value = "/{id}/delete", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String eliminar(@PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
		ResponseEntity<?> resultado = apiDelete(id);
		if (resultado.getStatusCode().is2xxSuccessful()) {
			attr.addFlashAttribute(ModelAttr.MENSAJE, new Mensaje(HttpStatus.OK.value(), "Registro eliminado correctamente"));
		} else {
			attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
		}
		return "redirect:/" + MAPPING;
	}
	
	
	
	@Permisos(Permisos.DET)
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Ute.class)
	@RequestMapping(value = "/{idEmpresa}/ute/{id}", method = RequestMethod.GET, produces = {
			MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD,
			MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3 })
	public @ResponseBody ResponseEntity<?> apiDetalleUte(
			@PathVariable BigDecimal idEmpresa,
			@PathVariable @TestValue("1") BigDecimal id) {
		Ute registro = daoUte.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.NOT_FOUND.value(),
							messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			return ResponseEntity.ok(registro);
		}
	}
	
	@Permisos(Permisos.NEW)
	@RequestMapping(value = "/{idEmpresa}/ute/new", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String newFormUte(@PathVariable BigDecimal idEmpresa, Ute dato,
			BindingResult bindingResult, Model model) {
		
		Empresa l = dao.find(idEmpresa);
		dato.setEmpresa(l);
		model.addAttribute(ModelAttr.DATO, dato);
		model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(new Ute()));
		return MAPPING_UTE_FORM;
	}

	@Permisos(Permisos.MOD)
	@NoCache
	@RequestMapping(value = "/{idEmpresa}/ute/{id}/edit", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String editUte(@PathVariable BigDecimal idEmpresa,
			@PathVariable BigDecimal id, Ute dato,
			BindingResult bindingResult, Model model) {
		ResponseEntity<?> registro = apiDetalleUte(idEmpresa, id);
		model.addAttribute(ModelAttr.DATO, registro.getBody());
		model.addAttribute(ModelAttr.REGISTRO, registro);
		return MAPPING_UTE_FORM;
	}

	@Description("Detalle de licitador")
	@OpenData
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(Licitador.class)
	@RequestMapping(value = "{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML, MimeTypes.CSV, MimeTypes.JSONLD, MimeTypes.RDF, MimeTypes.TURTLE, MimeTypes.RDF_N3})
	public @ResponseBody ResponseEntity<?> apiDetalle(@PathVariable(value="id") BigDecimal id) {
		Empresa registro = dao.find(id);
		if (registro == null) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new Mensaje(HttpStatus.NOT_FOUND.value(), messageSource.getMessage("generic.notfound", null, LocaleContextHolder.getLocale())));
		} else {
			registro.setEmpresasEnUte(dao.findEmpresaEnUte(id));
			return ResponseEntity.ok(registro);
		}
	}
	
	@RequestMapping(value = "/{idEmpresa}/ute", method = RequestMethod.POST, consumes = {
			MimeTypes.JSON, MimeTypes.XML }, produces = { MimeTypes.JSON,
			MimeTypes.XML })
	@Permisos(Permisos.NEW)
	@Description("Crear Ute")
	@ResponseClass(value = Ute.class)
	public @ResponseBody ResponseEntity<?> apiCrearUte(
			@PathVariable BigDecimal idEmpresa, @RequestParam("incluir") BigDecimal incluir, @RequestParam("participacion") BigDecimal participacion ) {
		return daoUte.asociarAEmpresa(idEmpresa, incluir, participacion);
	}
	
	@RequestMapping(value = "/{idEmpresa}/ute/{id}/remove", method = RequestMethod.DELETE, produces = {
			MimeTypes.JSON, MimeTypes.XML })
	@Permisos(Permisos.DEL)
	@Description("Eliminar registro")
	@ResponseClass(value = Mensaje.class)
	public @ResponseBody ResponseEntity<?> apiDeleteUte(
			@PathVariable BigDecimal idEmpresa, @PathVariable BigDecimal id) {
		
		return daoUte.eliminarAsociacion(idEmpresa, id);
	}

	@Permisos(Permisos.NEW)
	@RequestMapping(value = "/{idEmpresa}/ute/save", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String crearUte(@PathVariable BigDecimal idEmpresa,
			Ute dato,
			@RequestParam(name = "incluir", required=true)BigDecimal incluir,
			@RequestParam(name = "participacion")BigDecimal participacion,
			BindingResult bindingResult, Model model, RedirectAttributes attr) {

		Empresa empresa = (Empresa) apiDetalle(idEmpresa).getBody();
		dato.setEmpresa(empresa);
		
		ResponseEntity<?> resultado = apiCrearUte(idEmpresa, incluir, participacion);
		if (resultado.getStatusCode().is2xxSuccessful()) {
			daoUte.flush();
			attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
			attr.addFlashAttribute(ModelAttr.REGISTRO, resultado);
			attr.addFlashAttribute(ModelAttr.DATO, resultado.getBody());
			return "redirect:/" + MAPPING + idEmpresa + "/edit";
		} else {
			model.addAttribute(ModelAttr.MENSAJE, resultado.getBody());
			model.addAttribute(ModelAttr.REGISTRO, ResponseEntity.ok(dato));
			model.addAttribute(ModelAttr.DATO, dato);
			return MAPPING_UTE_FORM;
		}

	}

	@Permisos(Permisos.DEL)
	@RequestMapping(value = "/{idEmpresa}/ute/{id}/delete", method = RequestMethod.POST, produces = {
			MediaType.TEXT_HTML_VALUE, "*/*" })
	public String eliminarUte(@PathVariable BigDecimal idEmpresa,
			@PathVariable BigDecimal id, Model model, RedirectAttributes attr) {
		ResponseEntity<?> resultado = apiDeleteUte(idEmpresa, id);
		if (resultado.getStatusCode().is2xxSuccessful()) {
			attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
		} else {
			attr.addFlashAttribute(ModelAttr.MENSAJE, resultado.getBody());
		}
		return "redirect:/" + MAPPING + idEmpresa + "/edit";
	}


		
}
