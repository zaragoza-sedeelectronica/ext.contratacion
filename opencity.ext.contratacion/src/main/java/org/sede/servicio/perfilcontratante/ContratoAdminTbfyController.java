package org.sede.servicio.perfilcontratante;


import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import com.googlecode.genericdao.search.Sort;
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
import java.io.IOException;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;

@Gcz(servicio = "PERFILCONTRATANTE", seccion = "CONTRATO")
@Controller
//@Description("Ayuntamiento: Perfil de contratante")
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ContratoAdminTbfyController.MAPPING, method = RequestMethod.GET)
public class ContratoAdminTbfyController {
    //region Atributtes
    private static final String RAIZ = "contratacion-publica/admin/";
    private static final String SERVICIO = "tbfy-funcionalidades";
    public static final String MAPPING = "servicio/" + RAIZ + SERVICIO;
    private static final String MAPPING_FORM = MAPPING + "/formulario";
    private static final String MAPPING_CONTRATO_FORM = MAPPING + "/contrato";
    private static final String MAPPING_LICITADOR_FORM = MAPPING + "/licitador";

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


    @RequestMapping(method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String redirect() {
        return "redirect:" + SERVICIO + "/";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String home(Model model, @Fiql SearchFiql search) throws SearchParseException {
        model.addAttribute("resultado", "");
        return MAPPING + "/index";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/similitud", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String similitudPliegos(Model model, @RequestParam(name = "name", defaultValue = "", required = false) String name, @RequestParam(name = "lang", defaultValue = "es", required = false) String lang) throws SearchParseException, IOException {
        model.addAttribute("resultado", apiSearchSimilitud(name, lang));
        return MAPPING + "/similitud";
    }
    @Permisos(Permisos.DET)
    @RequestMapping(path = "/detalle-similitud", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String similitudPliegosDetalle(Model model, @RequestParam(name = "id", defaultValue = "", required = false) String id, @RequestParam(name = "lang", defaultValue = "es", required = false) String lang) throws SearchParseException, IOException {
        model.addAttribute("resultado", apiDetalleSimilitud(id,lang));
        return MAPPING + "/similitudDetalle";
    }
    @Permisos(Permisos.DET)
    @RequestMapping(path = "/detalle-licitacion/{id}", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String detalleLicitacion(Model model, @PathVariable String id) throws SearchParseException, IOException {
        model.addAttribute("resultado", apiDetalleLicitacion(id));
        return MAPPING + "/licitacionDetalle";
    }


    @Permisos(Permisos.DET)
    @RequestMapping(path = "/contratos", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String busquedaContratos(Model model, @RequestParam( name="cpv.id",defaultValue = "",required = false)String cpv,
                                    @RequestParam( name="cpv",defaultValue = "false",required = false)Boolean tipo,
                                    @RequestParam( name="terms",defaultValue = "",required = false)String terms,
                                    @RequestParam( name="name",defaultValue = "",required = false)String name,
                                    @RequestParam( name="size",defaultValue = "",required = false)String size,
                                    @RequestParam( name="lang",defaultValue = "",required = false)String lang,
                                    @RequestParam( name="text",defaultValue = "",required = false)String text) throws SearchParseException, IOException {

        if(!"".equals(cpv)){
            model.addAttribute("resultado", apiCpvTbfy(cpv));
            model.addAttribute("cpv",true);
        }else {
            model.addAttribute("resultado", apiSearchContractTbfy(name, terms, size, lang,text));
            model.addAttribute("cpv", false);
        }
        return MAPPING + "/contratos";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/licitadores", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String busquedaLicitador(Model model,@RequestParam(name = "name",defaultValue = "",required = false) String name,
                                    @RequestParam(name = "size", required = false) String size,
                                    @RequestParam(name = "jurisdiction", required = false,defaultValue = "") String jurisdiction,
                                    @RequestParam(name = "status", required = false,defaultValue = "") String status,
                                    @RequestParam(name = "currency", required = false,defaultValue = "") String currency,
                                    @RequestParam(name = "title", required = false,defaultValue = "") String title,
                                    @RequestParam(name = "description", required = false,defaultValue = "") String description,
                                    @RequestParam(name = "startDate", required = false,defaultValue = "") String startDate,
                                    @RequestParam(name = "endDate", required = false,defaultValue = "") String endDate) throws Exception {
        model.addAttribute("resultado", apiSearchLicitador(name,size,jurisdiction,status,currency,title,description,startDate,endDate));
        return MAPPING + "/licitadores";
    }

    @Permisos(Permisos.DET)
    @RequestMapping(path = "/reconcilacion", method = RequestMethod.GET, produces = {
            MediaType.TEXT_HTML_VALUE, "*/*"})
    public String reconcilacionEmpresas(Model model, @Fiql SearchFiql search) throws SearchParseException {
        model.addAttribute("resultado", "");
        return MAPPING + "/reconcilacion";
    }

    /*Herramientas THBY*/
    @RequestMapping(value = "/searchCpv", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiCpvTbfy(@RequestParam(name = "cpv.id", defaultValue = "", required = true) String cpv) {
        if (!"".equals(cpv)) {
            return ResponseEntity.ok(dao.jsonTbfy(cpv));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), "Error en la api:"));
        }
    }

    @RequestMapping(value = "/searchContrato", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiSearchContractTbfy(@RequestParam(name = "name", defaultValue = "", required = true) String name,
                                            @RequestParam(name = "terms", defaultValue = "", required = true) String terms,
                                            @RequestParam(name = "size", defaultValue = "", required = true) String size,
                                            @RequestParam(name = "lang", defaultValue = "", required = true) String lang,
                                            @RequestParam( name="text",defaultValue = "",required = false)String text) throws IOException {
        if (!"".equals(text)) {
            String source="tender";
            return ResponseEntity.ok(dao.similitudPliegos(name,lang,size,terms, source,text));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @RequestMapping(value = "/searchSimilitud", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiSearchSimilitud(@RequestParam(name = "name", defaultValue = "", required = true) String name,
                                         @RequestParam(name = "lang", defaultValue = "es", required = true) String lang) throws IOException {

        if (!"".equals(name) && !"".equals(lang)) {

            return ResponseEntity.ok(dao.similitudPliegos("", lang,"10","","jrc",name));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }
    @RequestMapping(value = "/similitudDetalle", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiDetalleSimilitud(@RequestParam(name = "id", defaultValue = "", required = true) String id, @RequestParam(name = "lang", defaultValue = "es", required = true) String lang) throws IOException {

        if (!"".equals(id) && !"".equals(lang)) {

            return ResponseEntity.ok(dao.similitudPliegosDetalle(id, lang));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }  @RequestMapping(value = "/licitacionDetalle", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiDetalleLicitacion(@RequestParam(name = "id", defaultValue = "", required = true) String id) throws IOException {

        if (!"".equals(id) ) {

            return ResponseEntity.ok(dao.licitacionDetalle(id));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }

    }

    @RequestMapping(value = "/searchLicitador", method = RequestMethod.GET, produces = {MimeTypes.JSON, MimeTypes.XML})
    @Permisos(Permisos.PUB)
    @ResponseClass(value = ContratoTbfy.class)
    public @ResponseBody
    ResponseEntity<?> apiSearchLicitador(@RequestParam(name = "name", required = true,defaultValue ="") String name,
                                         @RequestParam(name = "size", required = true) String size,
                                         @RequestParam(name = "jurisdiction", required = true) String jurisdiction,
                                         @RequestParam(name = "status", required = true) String status,
                                         @RequestParam(name = "currency", required = true) String currency,
                                         @RequestParam(name = "title", required = true) String title,
                                         @RequestParam(name = "description", required = true) String description,
                                         @RequestParam(name = "startDate", required = true) String startDate,
                                         @RequestParam(name = "endDate", required = true) String endDate) throws Exception {
        if (!"".equals(name)) {
            return ResponseEntity.ok(dao.jsonTbfyQuerylicitadores(name,size, jurisdiction, status, description, title, currency, startDate, endDate));
        } else {

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }


    }

}
