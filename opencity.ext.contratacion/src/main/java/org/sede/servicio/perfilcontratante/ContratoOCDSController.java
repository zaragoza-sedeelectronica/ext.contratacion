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
import org.sede.servicio.perfilcontratante.entity.*;
import org.sede.servicio.perfilcontratante.ocds.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.persistence.Query;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@Gcz(servicio="PERFILCONTRATANTE", seccion="CONTRATO")
@Controller
@Description("Ayuntamiento: Contratación pública OCDS")
@Transactional(Esquema.TMPERFILCONTRATANTE)
@RequestMapping(value = "/" + ContratoOCDSController.MAPPING, method = RequestMethod.GET)
public class ContratoOCDSController {
	//region Atributtes
	private static final String SERVICIO = "contratacion-publica/ocds";
	public static final String MAPPING = "servicio/" + SERVICIO;
	
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
			@RequestParam(name = "before", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows
			) throws SearchParseException, ParseException {
		
		Search busqueda = new Search(Contrato.class);
		
		List<Field> fields = new ArrayList<Field>();
		fields.add(new Field("id"));
		fields.add(new Field("title"));
		fields.add(new Field("creationDate"));
			busqueda.setFields(fields);
		if(after==null) {
			busqueda.addFilterGreaterThan("fechaContratoTill", ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT));
		}else{
			Date limite= ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT);
			if(after.compareTo(limite)<0){
				busqueda.addFilterGreaterThan("fechaContrato", ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT));
			}else if(after.compareTo(limite)==0)
				busqueda.addFilterGreaterThan("fechaContrato", ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT));
		}
		if(before!=null) {
			Date limite= ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT);
			if(before.compareTo(limite)<0){
				busqueda.addFilterLessOrEqual("fechaContrato", ConvertDate.string2Date("01-01-2017",ConvertDate.DATE_FORMAT));
			}else{
				busqueda.addFilterLessOrEqual("fechaContrato", before);
			}
		}

		busqueda.setMaxResults(rows);
		busqueda.addSort("fechaContrato",true);
		List<Contrato> lista =  dao.search(busqueda);
		List<ContractingProcess> resultado = new ArrayList<ContractingProcess>();
		for (Contrato c : lista) {
			resultado.add(new ContractingProcess(c.getId(),c));
		}
		SearchResult<ContractingProcess> retorno = new SearchResult<ContractingProcess>();
		retorno.setTotalCount(resultado.size());
		retorno.setResult(resultado);
		return ResponseEntity.ok(retorno);
		
	}
	@OpenData
	@NoCache
	@Description("Detalle Contracting process")
	@ResponseClass(PackageOcds.class)
	@RequestMapping(value = "/contracting-process/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoLicitador(
			@PathVariable String id)  {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[2].toString());
			Contrato contrato =  dao.find(idParseado);
			if (contrato == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new Mensaje(HttpStatus.NOT_FOUND.value(), idParseado + " No existe"));
			}
			PackageOcds pack=new PackageOcds();
			pack.getExtensions().add("https://raw.githubusercontent.com/open-contracting-extensions/ocds_lots_extension/v1.1.5/extension.json");
			pack.setUri("https://www.zaragoza.es/sede/"+MAPPING+id);
			Publisher publisher = new Publisher();
			publisher.setUid("L01502973");
			publisher.setName("Ayuntamiento de Zaragoza");
			publisher.setScheme("ES-DIR3");
			pack.setPublishedDate(ConvertDate.date2String(contrato.getPubDate(),ConvertDate.ISO8601_FORMAT));
			publisher.setUri("https://www.zaragoza.es");
			pack.setPublisher(publisher);
			Records record=new Records();
			List<ContractingProcess> resultado = new ArrayList<ContractingProcess>();
			List<Release> releases = new ArrayList<Release>();
			if(contrato.getStatus().getId().equals(0)|| contrato.getStatus().getId().equals(1) || contrato.getStatus().getId().equals(7) || contrato.getStatus().getId().equals(4) || contrato.getStatus().getId().equals(11) || contrato.getStatus().getId().equals(8)){
				releases.add(new Release(contrato,id));
				resultado.add(new ContractingProcess(contrato,1));

			}else if(contrato.getStatus().getId().equals(5) || contrato.getStatus().getId().equals(3) || contrato.getStatus().getId().equals(3)) {
				releases.add(new Release(contrato,id));
				resultado.add(new ContractingProcess(contrato,2));

			}else if(contrato.getStatus().getId().equals(6)) {
				releases.add(new Release(contrato,id));
				resultado.add(new ContractingProcess(contrato,3));

			}

			record.setOcid(id);
			record.setReleases(releases);
			pack.setReleases(resultado);
			pack.setVersion("1.1");

			return ResponseEntity.ok(pack);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado Contracting process contratos ")
	@ResponseClass(Contract.class)
	@RequestMapping(value = "/contracting-process/{id}/contract", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleContratoProcessContrato(
			@PathVariable String id) {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[2].toString());
			Contrato contrato =  dao.find(idParseado);
			if (contrato == null) {
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
						new Mensaje(HttpStatus.NOT_FOUND.value(), idParseado + " No existe"));
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			BigDecimal idParseado = new BigDecimal(array[2].toString());
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
						new Mensaje(HttpStatus.NOT_FOUND.value(), idParseado + " No existe"));
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	//endregion
	//region Contract
	@OpenData
	@NoCache
	@Description("Listado de contratos")
	@ResponseClass(Contract.class)
	@RequestMapping(value = "/contract", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoOdcsContrato(
			@RequestParam(name = "status",required = false) String status,
			@RequestParam(name = "before", required = false ) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows
	) throws SearchParseException {
		try {
			if(rows==null){
				rows=50;
			}
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
			Query query = dao.em().createNativeQuery("select * from (select P.Id_Contrato,P.Nombre as title,E.Nombre as empresa,entity.Nombre as entidad "
					+ "from perfil_contrato p, perfil_oferta o, PERFIL_PORTAL entity, perfil_empresa e " 
					+ "where o.Id_Empresa=e.Id_Empresa and o.Id_Contrato=p.Id_Contrato and p.Id_Portal=entity.Id_Portal " 
					+ "and o.Ganador='S' " 
					+ estado
					+ (before != null ? " and p.GCZ_FECHACONTRATO >= ? " : "")
					+ (after != null ? " and p.GCZ_FECHACONTRATO <= ? " : "")
					+ "order by p.id_contrato ) where rownum <=?");
			int k = 0;
			if (before != null) {
				query.setParameter(++k, before);
			}
			if (after != null) {
				query.setParameter(++k, after);
			}
			if(rows!=null){
				query.setParameter(++k,rows);
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
			BigDecimal idParseado = new BigDecimal(array[2].toString());
			Contrato contrato = dao.find(idParseado);
			List<Contract> resultado = new ArrayList<Contract>();
			if(contrato.getLotes().size()>0) {
				for (Oferta ofer:contrato.getOfertas()) {
					resultado.add(new Contract(contrato,ofer));
				}

			}else{
				resultado.add(new Contract(contrato));
			}
			SearchResult<Contract> retorno = new SearchResult<Contract>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de Modificaciones de un Contrato")
	@ResponseClass(Amendment.class)
	@RequestMapping(value = "/contract/{id}/amendment", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsContratoAmendment(@PathVariable String id,
		   @RequestParam(name = "before", required = false ) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date before,
		   @RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after) throws SearchParseException {
		try {
			String[] array = id.split("-");
			BigDecimal idParseado = new BigDecimal(array[2].toString());
			Search busqueda=new Search(Anuncio.class);
			busqueda.addFilterEqual("contrato.id",idParseado);
			if(before!=null) {
				busqueda.addFilterGreaterThan("pubDate",before);
			}
			if(after!=null) {
				busqueda.addFilterLessOrEqual("pubDate",after);
			}
			List<Anuncio> anuncio = daoAnuncio.search(busqueda);
			List<Amendment> resultado = new ArrayList<Amendment>();

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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			BigDecimal idParseado = new BigDecimal(array[2].toString());
			Contrato contrato = dao.find(idParseado);

			List<Item> resultado = new ArrayList<Item>();
			if(contrato.getCpv().size()==1){
				for(Cpv cpv:contrato.getCpv()) {
					resultado.add(new Item(cpv, contrato));
				}
			}else {

				resultado.add(new Item( contrato,true));


			}

			SearchResult<Item> retorno = new SearchResult<Item>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	//endregion
	//region Organization
	@OpenData
	@NoCache
	@Description("Listado de organizaciones")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiListadoOdcsOganizations(
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows
	) throws SearchParseException {
		try {
			List<Organisation> resultado=new ArrayList<Organisation>();
			Search busquedaEmpresa=new SearchFiql().getConditions(Empresa.class);
			busquedaEmpresa.setMaxResults(rows);
			Search busquedaOrganismo=new SearchFiql().getConditions(EstructuraOrganizativa.class);
			busquedaOrganismo.setMaxResults(rows);
			SearchResult<Empresa>empresas= daoEmpresa.searchAndCount(busquedaEmpresa);
			SearchResult<EstructuraOrganizativa> estructuras=daoOrganigrama.searchAndCount(busquedaOrganismo);
			for (Empresa empresa:empresas.getResult()) {
				resultado.add(new Organisation(empresa));
			}
			for (EstructuraOrganizativa estructura:estructuras.getResult()) {
				resultado.add(new Organisation(estructura,false));
			}
			SearchResult<Organisation> retorno = new SearchResult<Organisation>();
			retorno.setTotalCount(resultado.size());
			retorno.setResult(resultado);
			return ResponseEntity.ok(retorno);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), "No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de organizacion")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizations(@PathVariable String id) throws SearchParseException {
		try {
			if(!id.contains("-")){
				Empresa empresa= daoEmpresa.find(new BigDecimal(id));
				Search busqueda=new SearchFiql().getConditions(Contrato.class);
				busqueda.addFilterEqual("ofertas.empresa.idEmpresa",id);
				SearchResult<Contrato>result=dao.searchAndCount(busqueda);
				if(result!=null) {
					return ResponseEntity.ok(new Organisation(empresa,result));

				}else {
					return ResponseEntity.ok(new Organisation(empresa));
				}


			}else{
				String[] array = id.toString().split("-");
				BigDecimal idParseado = new BigDecimal(array[0]);
				EstructuraOrganizativa estructura=daoOrganigrama.find(idParseado);
				return ResponseEntity.ok(new Organisation(estructura,false));
			}
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), "Organizacion No encontrada: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de licitaciones de una organizacion")
	@ResponseClass(Organisation.class)
	@RequestMapping(value = "/organisation/{id}/contracting-process", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizationsContracting(
			@PathVariable String id,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows) throws SearchParseException {
		try {
			List<Organisation> organizaciones=new ArrayList<Organisation>();
			SearchResult<Organisation> result=new SearchResult<Organisation>();
			Search busqueda2=new SearchFiql().getConditions(Contrato.class);
			busqueda2.setMaxResults(rows);
			busqueda2.setDistinct(true);
			BigDecimal idParseado=new BigDecimal("0");
			if(!id.contains("-")){
				idParseado=new BigDecimal(id);
				busqueda2.addFilterEqual("ofertas.empresa.id",idParseado);
				SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
				for (Contrato con:resultado.getResult() ) {
					for (Oferta ofer : con.getOfertas()) {
						if (idParseado.equals(ofer.getEmpresa().getIdEmpresa()))
							organizaciones.add(new Organisation(ofer.getEmpresa(), con));
					}
				}
			}else {
				String[] array = id.toString().split("-");
				 idParseado = new BigDecimal(array[2]);
				busqueda2.addFilterOr(new Filter("entity.id",idParseado),new Filter("organoContratante.id",idParseado),new Filter("servicio.id",idParseado));
				SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
				for (Contrato con:resultado.getResult() ) {
					if (con.getEntity() != null) {
						if(con.getEntity().getId().equals(idParseado)){
							organizaciones.add(new Organisation(con.getEntity(),con));

						}
					}
					if (con.getServicio() != null) {
						if(con.getServicio().getId().equals( idParseado)){
							organizaciones.add(new Organisation(con.getServicio(),con));

						}
					}
					if(con.getOrganoContratante()!=null) {
						if (con.getOrganoContratante().getId().equals(idParseado)) {
							organizaciones.add(new Organisation(con.getOrganoContratante(), con));
						}
					}
			}

			}
			result.setTotalCount(organizaciones.size());
			result.setResult(organizaciones);
			result.setStart(0);
			result.setRows(organizaciones.size());
			return ResponseEntity.ok(result);

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de adjudicaciones ganadoras de una organización")
	@ResponseClass(Award.class)
	@RequestMapping(value = "/organisation/{id}/award", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsOganizationsAward(
			@PathVariable String id,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows
			) throws SearchParseException {
		BigDecimal idParseado=new BigDecimal("0");
		List<Award> ganadores=new ArrayList<Award>();
		SearchResult<Award> result=new SearchResult<Award>();
		Search busqueda2=new SearchFiql().getConditions(Contrato.class);
		busqueda2.setMaxResults(rows);
		busqueda2.setDistinct(true);
		try {
			if(!id.contains("-")) {
				idParseado = new BigDecimal(id);
				busqueda2.addFilterEqual("ofertas.empresa.idEmpresa",idParseado);
				busqueda2.addFilterEqual("ofertas.ganador",true);
			}else {
				String[] array = id.toString().split("-");
				idParseado = new BigDecimal(array[2]);
				busqueda2.addFilterOr(new Filter("entity.id",idParseado),new Filter("organoContratante.id",idParseado),new Filter("servicio.id",idParseado));
				busqueda2.addFilterEqual("ofertas.ganador", true);
			}
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con:resultado.getResult() ) {
				for (Oferta ofer : con.getOfertas()) {
					if(ofer.getGanador()) {
						ganadores.add(new Award(ofer, con));
					}
				}
			}
			result.setTotalCount(ganadores.size());
			result.setResult(ganadores);
			result.setStart(0);
			result.setRows(ganadores.size());
			return ResponseEntity.ok(result);

		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			@RequestParam(name = "after", required = false) @DateTimeFormat(pattern=ConvertDate.ISO8601_FORMAT) Date after,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows
	) throws SearchParseException {
		try {
			List<Award> ganadores=new ArrayList<Award>();
			SearchResult<Award> result=new SearchResult<Award>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.setMaxResults(rows);
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), "No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de adjudicación")
	@ResponseClass(Award.class)
	@RequestMapping(value = "/award/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAward(
			@PathVariable String id,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Award> ganadores=new ArrayList<Award>();
			SearchResult<Award> result=new SearchResult<Award>();
			Search busqueda2=new Search(Oferta.class);
			busqueda2.addFilterEqual("id",array[0]);
			busqueda2.setMaxResults(rows);
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			busqueda2.addFilterEqual("id",array[0]);
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			busqueda2.addFilterEqual("id",array[0]);
			SearchResult<Oferta> resultado=daoOferta.searchAndCount(busqueda2);
			for (Oferta ofer:resultado.getResult() ) {
				if(ofer.getContrato().getCpv().size()==1){
					for(Cpv cpv:ofer.getContrato().getCpv()) {
						items.add(new Item(cpv, ofer.getContrato()));
					}
				}else {

					items.add(new Item( ofer.getContrato(),true));


				}
			}
			result.setTotalCount(items.size());
			result.setResult(items);
			result.setStart(0);
			result.setRows(items.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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
			busqueda2.addFilterEqual("id",array[0]);
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
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

			Oferta resultado=daoOferta.find(BigDecimal.valueOf(Double.valueOf(array[0])));
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	//endregion
	//region Tender
	@OpenData
	@NoCache
	@Description("Listado de licitaciones")
	@ResponseClass(Tender.class)
	@RequestMapping(value = "/tender", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDListadoOdcsTender(
			@RequestParam(name = "status", required = false )String status,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows) throws SearchParseException {
		try {
			List<Tender> licitaciones=new ArrayList<Tender>();
			SearchResult<Tender> result=new SearchResult<Tender>();
			if(rows==null){
				rows=50;
			}
			String estado = "";
			if(!"".equals(status)) {
				if ("pending".equals(status)) {
					estado = "where  p.estado in (1,2,3) ";
				} else if ("active".equals(status)) {
					estado = "where  p.estado = 0";
				} else if ("cancelled".equals(status)) {
					estado = "where  p.estado in (4,7,8,10,11) ";
				} else if ("terminated".equals(status)) {
					estado = "where p.estado =6 ";
				}
			}

			Query query = dao.em().createNativeQuery(" select * from(select P.Id_Contrato,P.Nombre as title"
					+ " from perfil_contrato p "
					+ estado
					+ "order by p.id_contrato) where rownum <=? ");
			int k=0;
			query.setParameter(++k,rows);
			@SuppressWarnings("unchecked")
			List<Object> list = query.getResultList();
			for (Iterator<Object> iterador = list.iterator(); iterador.hasNext();) {
				Object[] row = (Object[]) iterador.next();
				licitaciones.add(new Tender((BigDecimal) row[0], (String) row[1]));
			}
			result.setTotalCount(licitaciones.size());
			result.setResult(licitaciones);
			result.setStart(0);
			result.setRows(licitaciones.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), "No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Detalle de una licitación")
	@ResponseClass(Tender.class)
	@RequestMapping(value = "/tender/{id}", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTender(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Tender> licitacion=new ArrayList<Tender>();
			SearchResult<Tender> result=new SearchResult<Tender>();
			Search busqueda2=new Search(Contrato.class);
			busqueda2.addFilterEqual("id",array[0]);
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Procesos de contratación asociados a una licitación")
	@ResponseClass(ContractingProcess.class)
	@RequestMapping(value = "/tender/{id}/contracting-process", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsAwardContractingProcess(
			@PathVariable String id,
			@RequestParam(name = "rows", required = false,defaultValue = "50")  Integer rows) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<ContractingProcess> proceso=new ArrayList<ContractingProcess>();
			SearchResult<ContractingProcess> result=new SearchResult<ContractingProcess>();
			Search busqueda2=new Search(Contrato.class);
			busqueda2.addFilterEqual("id",array[0]);
			busqueda2.setMaxResults(rows);
			SearchResult<Contrato> resultado=dao.searchAndCount(busqueda2);
			for (Contrato con :resultado.getResult() ) {
				proceso.add(new ContractingProcess(con,1));
			}
			result.setTotalCount(proceso.size());
			result.setResult(proceso);
			result.setStart(0);
			result.setRows(proceso.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}
	@OpenData
	@NoCache
	@Description("Listado de item de una licitación")
	@ResponseClass(Item.class)
	@RequestMapping(value = "/tender/{id}/item", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTenderItem(@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Item> items=new ArrayList<Item>();
			SearchResult<Item> result=new SearchResult<Item>();
			Contrato con=dao.find(BigDecimal.valueOf(Double.valueOf(array[0])));
			if(con.getCpv().size()==1){
				for(Cpv cpv:con.getCpv()) {
					items.add(new Item(cpv, con));
				}
			}else {

				items.add(new Item( con,true));


			}
			result.setTotalCount(items.size());
			result.setResult(items);
			result.setStart(0);
			result.setRows(items.size());
			return ResponseEntity.ok(result);
		}catch (Exception e){
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}

	@OpenData
	@NoCache
	@Description("Listado de documentos de una licitación")
	@ResponseClass(Document.class)
	@RequestMapping(value = "/tender/{id}/document", method = RequestMethod.GET, produces = {MimeTypes.JSON})
	public @ResponseBody ResponseEntity<?> apiDetalleOdcsTenderDocuments(
			@PathVariable String id) throws SearchParseException {
		try {
			String[] array = id.split("-");
			List<Document> documents=new ArrayList<Document>();
			SearchResult<Document> result=new SearchResult<Document>();
			Contrato resultado=dao.find(BigDecimal.valueOf(Double.valueOf(array[0])));
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
					new Mensaje(HttpStatus.BAD_REQUEST .value(), id + " No valido: " + e.getMessage()));
		}
	}

	//endregion

}
