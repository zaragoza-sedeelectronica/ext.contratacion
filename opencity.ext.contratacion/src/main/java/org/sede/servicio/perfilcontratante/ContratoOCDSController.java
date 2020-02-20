package org.sede.servicio.perfilcontratante;


import com.googlecode.genericdao.search.Field;
import com.googlecode.genericdao.search.Filter;
import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.apache.cxf.jaxrs.ext.search.SearchParseException;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.SearchFiql;
import org.sede.core.rest.Mensaje;
import org.sede.core.rest.MimeTypes;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.organigrama.dao.OrganigramaGenericDAO;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.dao.*;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.Oferta;
import org.sede.servicio.perfilcontratante.ocds.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Query;

@Gcz(servicio="PERFILCONTRATANTE", seccion="CONTRATO")
@Controller
//@Description("Ayuntamiento: Contratación pública OCDS")
@Transactional(ConfigPerfilContratante.TM)
@RequestMapping(value = "/" + ContratoOCDSController.MAPPING, method = RequestMethod.GET)
public class ContratoOCDSController {
	//region Atributtes
	private static final String SERVICIO = "contratacion-publica/ocds";
	public static final String MAPPING = "servicio/" + SERVICIO;
	@Autowired
	private MessageSource messageSource;
	@Autowired
	public ContratoGenericDAO dao;
	@Autowired
	private EmpresaGenericDAO daoEmpresa;
	@Autowired
	public EntidadContratanteGenericDAO daoEntidadContratante;
	@Autowired
	public OrganigramaGenericDAO daoOrganigrama;
	@Autowired
	public AnuncioGenericDAO daoAnuncio;
	@Autowired
	public OfertaGenericDAO daoOferta;
	//endregion
	//region Contracting process
	@OpenData
	@Description("Contracting process")
	@Cache(Cache.DURACION_30MIN)
	@ResponseClass(ContractingProcess.class)
	@RequestMapping(value = "/contracting-process", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoLicitador(
			@RequestParam(name = "before", required = true) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = true) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after
			) throws SearchParseException {
		
		Search busqueda = new Search(Contrato.class);
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("id"));
		fields.add(new Field("title"));
		busqueda.setFields(fields);
		busqueda.addFilterGreaterThan("fechaContrato", before);
		busqueda.addFilterLessOrEqual("fechaContrato", after);
		List<Contrato> lista =  dao.search(busqueda);
		List<ContractingProcess> resultado = new ArrayList<ContractingProcess>();
		for (Contrato c : lista) {
			resultado.add(new ContractingProcess(c.getId()));
		}
		SearchResult<ContractingProcess> retorno = new SearchResult<ContractingProcess>();
		retorno.setTotalCount(resultado.size());
		retorno.setResult(resultado);
		return ResponseEntity.ok(retorno);
		
	}
	@OpenData
	@NoCache
	@Description("Detalle Contracting process")
	@ResponseClass(ContractingProcess.class)
	@RequestMapping(value = "/contracting-process/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoLicitador(
			@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Contrato contrato =  dao.find(idParseado);
			if (contrato == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new Mensaje(HttpStatus.NOT_FOUND.value(),
								messageSource.getMessage(idParseado+" No existe",null, LocaleContextHolder.getLocale())));
			}
			List<ContractingProcess> resultado = new ArrayList<ContractingProcess>();
			resultado.add(new ContractingProcess(contrato));
			SearchResult<ContractingProcess> retorno = new SearchResult<ContractingProcess>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(id+" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado Contracting process contratos ")
	@ResponseClass(Contract.class)
	@RequestMapping(value = "/contracting-process/{id}/contract", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleContratoProcessContrato(
			@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Contrato contrato =  dao.find(idParseado);
			if (contrato == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new Mensaje(HttpStatus.NOT_FOUND.value(),
								messageSource.getMessage(idParseado+" No existe",null, LocaleContextHolder.getLocale())));
			}
			List<Contract> resultado = new ArrayList<Contract>();
			for (Oferta ofer:contrato.getOfertas() ) {
				if(ofer.getGanador()) {
					resultado.add(new Contract(contrato.getId(), contrato.getTitle(), ofer.getEmpresa().getNombre(),contrato.getEntity().getTitle()));
				}
			}
			SearchResult<Contract> retorno = new SearchResult<Contract>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(id+" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@ResponseClass(Award.class)
	@Description("Detalle de ganador Contracting process")
	@RequestMapping(value = "/contracting-process/{id}/award", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleContratoProcessAward(
			@PathVariable String id,
			@RequestParam(name = "before", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after) throws SearchParseException {
		try {
			SearchResult<Award> retorno = new SearchResult<Award>();
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Search busqueda =new SearchFiql().getConditions(Contrato.class);
			busqueda.addFilterEqual("id",idParseado);
			if(before!=null){
				busqueda.addFilterGreaterThan("oferta.fechaAdjudicacion",before);
			}
			if(before!=null){
				busqueda.addFilterLessOrEqual("oferta.fechaAdjudicacion",after);
			}
			SearchResult<Contrato> contratos =  dao.searchAndCount(busqueda);
			if (contratos == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new Mensaje(HttpStatus.NOT_FOUND.value(),
								messageSource.getMessage(idParseado + " No existe", null, LocaleContextHolder.getLocale())));
			}
			for (Contrato contrato:contratos.getResult()) {
				List<Award> resultado = new ArrayList<Award>();
				for (Oferta ofer : contrato.getOfertas()) {
					if (ofer.getGanador()) {
						resultado.add(new Award(ofer, contrato));
					}
				}
				retorno.setTotalCount(resultado.size());
				retorno.setResult(resultado);
			}
			return ResponseEntity.ok(retorno);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(id+" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	//endregion
	//region Contract
//	@OpenData
//	@NoCache
//	@Description("Listado de contratos")
//	@ResponseClass(Contract.class)
//	@RequestMapping(value = "/contract", method = RequestMethod.GET, produces = {MimeTypes.JSON})
//	public @ResponseBody ResponseEntity<?> apiListadoOdcsContrato(
//			@RequestParam(name = "status",required = true) String status,
//			@RequestParam(name = "before", required = true) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
//			@RequestParam(name = "after", required = true) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after
//	) throws SearchParseException {
//		try {
//			Search busqueda = new Search(Contrato.class);
//			if(!"".equals(status)) {
//				if ("pending".equals(status))
//					busqueda.addFilterOr(Filter.equal("status.id", 1), Filter.equal("status.id", 5));
//				if ("active".equals(status))
//					busqueda.addFilterEqual("status.id",6);
//				if ("cancelled".equals(status))
//					busqueda.addFilterOr(Filter.equal("status.id", 4), Filter.equal("status.id", 7),Filter.equal("status.id",8 ),Filter.equal("status.id", 10),Filter.equal("status.id", 11));
//				if ("terminated".equals(status))
//					busqueda.addFilterGreaterThan("oferta.fechaFormalizacion",new Date());
//			}
//			if(before!=null)
//				busqueda.addFilterGreaterThan("fechaContrato", before);
//			if(after!=null)
//				busqueda.addFilterLessOrEqual("fechaContrato", after);
//			busqueda.addSortAsc("id");
//			List<Contrato> lista =  dao.search(busqueda);
//			List<Contract> resultado = new ArrayList<Contract>();
//			for (Contrato c : lista) {
//				for (Oferta ofer : c.getOfertas()) {
//					if (ofer.getGanador()) {
//						resultado.add(new Contract(c.getId(), c.getTitle(), ofer.getEmpresa().getNombre(), c.getEntity().getTitle()));
//					}
//				}
//			}
//			SearchResult<Contract> retorno = new SearchResult<Contract>();
//			retorno.setTotalCount(resultado.size());
//			retorno.setResult(resultado);
//			return ResponseEntity.ok(retorno);
//		}catch (Exception e){
//			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
//					new Mensaje(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
//		}
//	}
	
	@OpenData
	@NoCache
	@Description("Listado de contratos")
	@ResponseClass(Contract.class)
	@RequestMapping(value = "/contract", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoOdcsContrato(
			@RequestParam(name = "status",required = false) String status,
			@RequestParam(name = "before", required = false ) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after
	) throws SearchParseException {
		try {
			String estado = "";
			if(!"".equals(status)) {
				if ("pending".equals(status)) {
					estado = " and p.estado in (1,5) ";
				} else if ("active".equals(status)) {
					estado = " and p.estado = 6 ";
				} else if ("cancelled".equals(status)) {
					estado = " and p.estado in (4,7,8,10,11) ";
				} else if ("terminated".equals(status)) {
					estado = " and o.Fecha_Form > sysdate ";
				}
			}
			
			List<Contract> resultado = new ArrayList<Contract>();
			Query query = dao.em().createNativeQuery("select P.Id_Contrato,P.Nombre as title,E.Nombre as empresa,entity.Nombre as entidad " 
					+ "from perfil_contrato p, perfil_oferta o, PERFIL_PORTAL entity, perfil_empresa e " 
					+ "where o.Id_Empresa=e.Id_Empresa and o.Id_Contrato=p.Id_Contrato and p.Id_Portal=entity.Id_Portal " 
					+ "and o.Ganador='S' " 
					+ estado
					+ (before != null ? " and p.GCZ_FECHACONTRATO >= ? " : "")
					+ (after != null ? " and p.GCZ_FECHACONTRATO <= ? " : "")
					+ "order by p.id_contrato");
			int k = 0;
			if (before != null) {
				query.setParameter(++k, before);
			}
			if (after != null) {
				query.setParameter(++k, after);
			}
			@SuppressWarnings("unchecked")
			List<Object> list = query.getResultList();
			for (Iterator<Object> iterador = list.iterator(); iterador.hasNext();) {
				Object[] row = (Object[])iterador.next();
				resultado.add(new Contract((BigDecimal)row[0], (String)row[1], (String)row[2], (String)row[3]));
			}
			SearchResult<Contract> retorno = new SearchResult<Contract>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
					new Mensaje(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
		}
	}
	
	@OpenData
	@NoCache
	@Description("Detalle de contrato")
	@ResponseClass(Contract.class)
	@RequestMapping(value = "/contract/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsContrato(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Contrato contrato = dao.find(idParseado);
			List<Contract> resultado = new ArrayList<Contract>();
			resultado.add(new Contract(contrato));
			SearchResult<Contract> retorno = new SearchResult<Contract>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST.value(),
							messageSource.getMessage(" No valido", null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de Modificaciones de un Contrato")
	@ResponseClass(Amendment.class)
	@RequestMapping(value = "/contract/{idContrato}/amendment", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsContratoAmendment(@PathVariable String idContrato,
		   @RequestParam(name = "id", required = false ) String id,
		   @RequestParam(name = "before", required = false ) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
		   @RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after) throws SearchParseException {
		try {
			String[] array = idContrato.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Search busqueda=new Search(Anuncio.class);
			busqueda.addFilterEqual("contrato.id",idParseado);
			if(id!=null) {
				busqueda.addFilterEqual("id",BigDecimal.valueOf(Double.valueOf(id)));
			}
			if(before!=null) {
				busqueda.addFilterGreaterThan("pubDate",before);
			}
			if(after!=null) {
				busqueda.addFilterLessOrEqual("pubDate",after);
			}
			List<Anuncio> anuncio = daoAnuncio.search(busqueda);
			List<Amendment> resultado = new ArrayList<Amendment>();
			List<Document> documentos=new ArrayList<Document>();
			for (Anuncio anun:anuncio) {
				switch (Integer.valueOf(anun.getType().getId().toString())) {
					case 31:
					case 32:
					case 33:
						resultado.add(new Amendment(anun));
						break;
					default:
						resultado.add(new Amendment());
						break;


				}
			}
			SearchResult<Amendment> retorno = new SearchResult<Amendment>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST.value(),
							messageSource.getMessage(" No valido", null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@ResponseClass(Item.class)
	@Description("Listado de Item de un contrato")
	@RequestMapping(value = "/contract/{id}/item", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsContratoItem(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[1].toString());
			Contrato contrato = dao.find(idParseado);

			List<Item> resultado = new ArrayList<Item>();
			resultado.add(new Item(contrato));
			SearchResult<Item> retorno = new SearchResult<Item>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST.value(),
							messageSource.getMessage(" No valido", null, LocaleContextHolder.getLocale())));
		}
	}
	//endregion
	//region Organization
	@OpenData
	@NoCache
	@Description("Listado de organizaciones")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoOdcsOganizations() throws SearchParseException {
		try {
			List<Organisation> resultado=new ArrayList<Organisation>();
			Search busquedaEmpresa=new SearchFiql().getConditions(Empresa.class);
			busquedaEmpresa.setMaxResults(-1);
			Search busquedaOrganismo=new SearchFiql().getConditions(EstructuraOrganizativa.class);
			busquedaOrganismo.setMaxResults(-1);
			SearchResult<Empresa>empresas= daoEmpresa.searchAndCount(busquedaEmpresa);
			SearchResult<EstructuraOrganizativa> estructuras=daoOrganigrama.searchAndCount(busquedaOrganismo);
			for (Empresa empresa:empresas.getResult()) {
				resultado.add(new Organisation(empresa));
			}
			for (EstructuraOrganizativa estructura:estructuras.getResult()) {
				resultado.add(new Organisation(estructura));
			}
			SearchResult<Organisation> retorno = new SearchResult<Organisation>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de organizacion")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizations(@PathVariable BigDecimal id) throws SearchParseException {
		try {
			Empresa empresa= daoEmpresa.find(id);
			if(empresa!=null) {
				return ResponseEntity.ok(new Organisation(empresa));
			}else{
				EstructuraOrganizativa estructura=daoOrganigrama.find(id);
				return ResponseEntity.ok(new Organisation(estructura));
			}

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de licitaciones de una organizacion")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation/{id}/contracting-proces", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizationsContracting(@PathVariable BigDecimal id) throws SearchParseException {
		try {
			List<Organisation> organizaciones=new ArrayList<Organisation>();
			SearchResult<Organisation> result=new SearchResult<Organisation>();
			Search busqueda2=new SearchFiql().getConditions(Contrato.class);
			busqueda2.addFilterOr(new Filter("organoContratante.id",id),new Filter("servicio.id",id),new Filter("oferta.empresa.id",id));
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con:resultado.getResult() ) {
				for (Oferta ofer:con.getOfertas()) {
					if(id==ofer.getEmpresa().getIdEmpresa())
						organizaciones.add(new Organisation(ofer.getEmpresa(),con));
				}
				if(con.getServicio().getId()==id){

					organizaciones.add(new Organisation(con.getServicio(),con));

				}
				if(con.getOrganoContratante().getId()==id){
					organizaciones.add(new Organisation(con.getOrganoContratante(),con));
				}
			}
			result.setTotalCount(organizaciones.size());
			result.setResult(organizaciones);
			result.setStart(0);
			result.setRows(organizaciones.size());
			return ResponseEntity.ok(result);

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de de ganadores de una organizacion")
	@ResponseClass(Award.class)
	@RequestMapping(value = "/organisation/{id}/awards", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizationsAward(@PathVariable BigDecimal id) throws SearchParseException {
		try {
			List<Award> ganadores=new ArrayList<Award>();
			SearchResult<Award> result=new SearchResult<Award>();
			Search busqueda2=new SearchFiql().getConditions(Contrato.class);
			busqueda2.addFilterEqual("oferta.empresa.id",id);
			busqueda2.addFilterEqual("oferta.ganador",true);
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con:resultado.getResult() ) {
				for (Oferta ofer : con.getOfertas()) {
					ganadores.add(new Award(ofer,con));
				}
			}
			result.setTotalCount(ganadores.size());
			result.setResult(ganadores);
			result.setStart(0);
			result.setRows(ganadores.size());
			return ResponseEntity.ok(result);

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	//endregion
	//region Award
	@OpenData
	@NoCache
	@Description("Listado de adjudicaciones")
	@ResponseClass(Award.class)
	@RequestMapping(value = "/award", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAward(
			@RequestParam(name = "before", required = false ) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after
	) throws SearchParseException {
		try {
			List<Award> ganadores=new ArrayList<Award>();
			SearchResult<Award> result=new SearchResult<Award>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("ganador",true);
			if(before!=null){
				busqueda2.addFilterGreaterOrEqual("fechaAdjudicacion",before);
			}
			if(after!=null){
				busqueda2.addFilterLessOrEqual("fechaAdjudicacion",after);
			}
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
					ganadores.add(new Award(ofer,ofer.getContrato()));
			}
			result.setTotalCount(ganadores.size());
			result.setResult(ganadores);
			result.setStart(0);
			result.setRows(ganadores.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de adjudicación")
	@ResponseClass(Award.class)
	@RequestMapping(value = "/award/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAward(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Award> ganadores=new ArrayList<Award>();
			SearchResult<Award> result=new SearchResult<Award>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("id",array[3]);
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
				ganadores.add(new Award(ofer));
			}
			result.setTotalCount(ganadores.size());
			result.setResult(ganadores);
			result.setStart(0);
			result.setRows(ganadores.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("listado de de Modificaciones a una adjudicación")
	@ResponseClass(Amendment.class)
	@RequestMapping(value = "/award/{id}/amendment", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardAmendment(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Amendment> amendments=new ArrayList<Amendment>();
			SearchResult<Amendment> result=new SearchResult<Amendment>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("id",array[3]);
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
				for (Anuncio anun:ofer.getContrato().getAnuncios() ) {
					switch (Integer.valueOf(anun.getType().getId().toString())) {
						case 31:
						case 32:
						case 33:
							amendments.add(new Amendment(anun));
							break;
						default:
							amendments.add(new Amendment());
							break;
					}
				}
			}
			result.setTotalCount(amendments.size());
			result.setResult(amendments);
			result.setStart(0);
			result.setRows(amendments.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de item de una adjudicación")
	@ResponseClass(Item.class)
	@RequestMapping(value = "/award/{id}/item", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardItem(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Item> items=new ArrayList<Item>();
			SearchResult<Item> result=new SearchResult<Item>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("id",array[3]);
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
				items.add(new Item(ofer.getContrato()));

			}
			result.setTotalCount(items.size());
			result.setResult(items);
			result.setStart(0);
			result.setRows(items.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de proveedores de una adjudicación")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/award/{id}/supplier", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardSupliers(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Organisation> ganadores=new ArrayList<Organisation>();
			SearchResult<Organisation> result=new SearchResult<Organisation>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("id",array[3]);
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
				ganadores.add(new Organisation(ofer.getEmpresa(),ofer.getContrato()));

			}
			result.setTotalCount(ganadores.size());
			result.setResult(ganadores);
			result.setStart(0);
			result.setRows(ganadores.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de documentos de una adjudicación")
	@ResponseClass(Document.class)
	@RequestMapping(value = "/award/{id}/document", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardDocuments(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Document> documents=new ArrayList<Document>();
			SearchResult<Document> result=new SearchResult<Document>();

			Oferta resultado=daoOferta.find(BigDecimal.valueOf(Double.valueOf(array[3])));
			for (Anuncio anun:resultado.getContrato().getAnuncios()) {
				documents.add(new Document(anun));
			}
			result.setTotalCount(documents.size());
			result.setResult(documents);
			result.setStart(0);
			result.setRows(documents.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	//endregion
	//region Tender
	@OpenData
	@NoCache
	@Description("Listado de licitadores")
	@ResponseClass(Tender.class)
	@RequestMapping(value = "/tender", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDListadoOdcsTender(
			@RequestParam(name = "status", required = false )String status) throws SearchParseException {
		try {
			List<Tender> licitaciones=new ArrayList<Tender>();
			SearchResult<Tender> result=new SearchResult<Tender>();
			Search busqueda2=new Search(Contrato.class);
			if(status!=null){
				if("active".equals(status)){
					busqueda2.addFilterEqual("status.id", BigDecimal.valueOf(0.0));
				}
				if("pending".equals(status)){
					busqueda2.addFilterOr(new Filter("status.id", BigDecimal.valueOf(1.0)),new Filter("status.id", BigDecimal.valueOf(2.0)),new Filter("status.id", BigDecimal.valueOf(3.0)));
				}
				if("cancelled".equals(status)){
					busqueda2.addFilterOr(new Filter("status.id", BigDecimal.valueOf(10.0)),new Filter("status.id", BigDecimal.valueOf(7.0)),new Filter("status.id", BigDecimal.valueOf(4.0)),new Filter("status.id", BigDecimal.valueOf(8.0)));
				}
				if("terminated".equals(status)){
					busqueda2.addFilterEqual("status.id", BigDecimal.valueOf(6.0));
				}

			}
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato contra:resultado.getResult() ) {
				licitaciones.add(new Tender(contra.getId(),contra.getTitle()));
			}
			result.setTotalCount(licitaciones.size());
			result.setResult(licitaciones);
			result.setStart(0);
			result.setRows(licitaciones.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de licitadores")
	@ResponseClass(Tender.class)
	@RequestMapping(value = "/tender/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTender(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Tender> licitacion=new ArrayList<Tender>();
			SearchResult<Tender> result=new SearchResult<Tender>();
			Search busqueda2=new Search(Contrato.class);
			busqueda2.addFilterEqual("id",array[1]);
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con:resultado.getResult() ) {
				licitacion.add(new Tender(con));
			}
			result.setTotalCount(licitacion.size());
			result.setResult(licitacion);
			result.setStart(0);
			result.setRows(licitacion.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de ganadores de una licitacion a un contrato")
	@ResponseClass(ContractingProcess.class)
	@RequestMapping(value = "/tender/{id}/contracting-process", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardContractingProcess(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<ContractingProcess> proceso=new ArrayList<ContractingProcess>();
			SearchResult<ContractingProcess> result=new SearchResult<ContractingProcess>();
			Search busqueda2=new Search(Contrato.class);
			busqueda2.addFilterEqual("id",array[1]);
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con :resultado.getResult() ) {
				proceso.add(new ContractingProcess(con));
			}
			result.setTotalCount(proceso.size());
			result.setResult(proceso);
			result.setStart(0);
			result.setRows(proceso.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de item de un licitador")
	@ResponseClass(Item.class)
	@RequestMapping(value = "/tender/{id}/item", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTenderItem(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Item> items=new ArrayList<Item>();
			SearchResult<Item> result=new SearchResult<Item>();
			Contrato con=dao.find(BigDecimal.valueOf(Double.valueOf(array[1])));
			items.add(new Item(con));
			result.setTotalCount(items.size());
			result.setResult(items);
			result.setStart(0);
			result.setRows(items.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}

	@OpenData
	@NoCache
	@Description("Listado de documentos de una licitador")
	@ResponseClass(Document.class)
	@RequestMapping(value = "/tender/{id}/document", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTenderDocuments(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Document> documents=new ArrayList<Document>();
			SearchResult<Document> result=new SearchResult<Document>();
			Contrato resultado=dao.find(BigDecimal.valueOf(Double.valueOf(array[1])));
			for (Anuncio anun:resultado.getAnuncios()) {
				documents.add(new Document(anun));
			}
			result.setTotalCount(documents.size());
			result.setResult(documents);
			result.setStart(0);
			result.setRows(documents.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(),
							messageSource.getMessage(" No valido",null, LocaleContextHolder.getLocale())));
		}
	}

	//endregion

}
