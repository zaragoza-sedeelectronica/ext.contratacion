package org.sede.servicio.perfilcontratante.dao;


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
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.core.rest.Mensaje;
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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.*;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class ContratoGenericDAOImpl extends GenericDAOImpl <Contrato, BigDecimal> implements ContratoGenericDAO {
	private static final Logger logger = LoggerFactory.getLogger(ContratoGenericDAOImpl.class);
	private static final Map<BigDecimal,String> expedienteEntidad=new HashMap<BigDecimal, String>(){
		{
			put(BigDecimal.valueOf(61632),"DEP");
			put(BigDecimal.valueOf(61634),"CUL");
			put(BigDecimal.valueOf(61632),"ECO");
			put(BigDecimal.valueOf(61632),"VIV");
			put(BigDecimal.valueOf(61632),"EDUC");
			put(BigDecimal.valueOf(61632),"TUR");
			put(BigDecimal.valueOf(61632),"DIN");




		}
	};
	@Autowired
	public TipoContratoGenericDAO daoTipoContrato;
	@PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
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
	public IModel getForTag(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler) {
		try {
			
			String fragmento = tag.getAttributeValue("fragment") == null ? "fragmentos/perfil-contratante/list" : tag.getAttributeValue("fragment");
			Search busqueda = new Search();
			busqueda.addFilter(Filter.some("entity", Filter.equal("id", tag.getAttributeValue("entidad"))));
			//busqueda.addFilter(Filter.lessOrEqual("startDate", new Date()));
			//busqueda.addFilter(Filter.greaterOrEqual("expirationDate", new Date()));
			busqueda.setMaxResults(tag.getAttributeValue("numero") == null ? 10 : Integer.parseInt(tag.getAttributeValue("numero")));
			List<Sort> sorts = new ArrayList<Sort>();
			sorts.add(new Sort("fecha_presentacion", true));
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
	public HashMap<Integer,Integer> getContratosPorAnyo(String nif) {
		HashMap<Integer,Integer> map = new HashMap<Integer,Integer>();
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
		Query q= em().createNativeQuery("select DISTINCT SERVICIO_GESTOR from PERFIL_CONTRATO where ID_PORTAL=? and SERVICIO_GESTOR is not NUll").setParameter(1,id);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<String> getOrganismoContratante() {
		Query q= em().createNativeQuery("select DISTINCT ORGANO_CONTRATACION from PERFIL_CONTRATO where ORGANO_CONTRATACION is not NUll");
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
	public HSSFWorkbook generarExcelTribunalCuentas(SearchResult<Contrato> resultado) {
		
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
		
		cellFecha = headerRow.createCell(++col);
		cellFecha.setCellStyle(headerStyle);
		cellFecha.setCellValue("Contrato por lotes");
		
		cellFecha = headerRow.createCell(++col);
		cellFecha.setCellStyle(headerStyle);
		cellFecha.setCellValue("Número de lotes");
		
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
		
		cellFecha = headerRow.createCell(++col);
		cellFecha.setCellStyle(headerStyle);
		cellFecha.setCellValue("Tiene cláusula de prórrga");
		
		cellFecha = headerRow.createCell(++col);
		cellFecha.setCellStyle(headerStyle);
		cellFecha.setCellValue("Periodo de prórroga");
		
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
			cellid.setCellValue(r.getExpediente());
			
			cellid = dataRow.createCell(++col);
			int lotes = r.getLotes().size();
			cellid.setCellValue(lotes > 0 ? "Si" : "No");
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(lotes > 0 ? "" + lotes : "");
			
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
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(r.getType().getTitle());
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(r.getTitle());
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(r.getProcedimiento() == null ? "" : r.getProcedimiento().getNombre());
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(Utils.getUrgente(r.getUrgente()));
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue(Utils.getSino(r.getClausulaProrroga()));
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue("" + (r.getPeriodoProrroga() == null ? "" : r.getPeriodoProrroga()));
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue("" + (r.getImporteSinIVA() == null ? "" : r.getImporteSinIVA()));
			
			StringBuilder contratista = new StringBuilder();
			StringBuilder txtLotes = new StringBuilder();
			String importeAdjudicacionSinIVA = "";
			for (Oferta of : r.getOfertas()) {
				if (of.getGanador().equals(Boolean.TRUE)) {
					
					if (of.getLote() == null) {
						importeAdjudicacionSinIVA = "" + of.getImporteSinIVA();
						contratista.append(of.getEmpresa().getNif()  + " - " + of.getEmpresa().getNombre() + " ");	
					} else {
						if (StringUtils.isNotEmpty(txtLotes)) {
							txtLotes.append(" | ");
						}
						txtLotes.append(of.getLote().getDescription()
								+ " - " + of.getImporteSinIVA()
								+ " - " + (of.getFechaAdjudicacion() == null ? "" : ConvertDate.date2String(of.getFechaAdjudicacion(), ConvertDate.DATE_FORMAT))
								+ " - " + (of.getFechaFormalizacion() == null ? "" : ConvertDate.date2String(of.getFechaFormalizacion(), ConvertDate.DATE_FORMAT))
								+ " - " +  of.getEmpresa().getNif()  + " - " + of.getEmpresa().getNombre() + " ");
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
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue("" + (r.getPlazoConsesion() == null ? "" : r.getPlazoConsesion()));
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue("" + ("S".equals(r.getVisible()) ? "Si" : "No"));
			
			cellid = dataRow.createCell(++col);
			cellid.setCellValue("" + (r.getProcedimiento() == null 
						|| r.getProcedimiento().getId().intValue() ==  3 
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
		for (Iterator<Object> iterador = list.iterator(); iterador.hasNext();) {
			Object[] row = (Object[])iterador.next();
			Contrato p = new Contrato();
			p.setId((BigDecimal)row[0]);
			p.setTitle(row[1].toString());
			listado.add(p);
		}
		return listado;
	}
	@SuppressWarnings("deprecation")
	@Override
	public List<Contrato> loadXls(InputStream flujo)throws IOException{
		List<Contrato> contratos=new ArrayList<Contrato>();
		/*EXPEDIENTE	NOMBRE	TIPOCONTRATO	FECHACADUCIDAD	CONT_MENOR	SERVICIO_GESTOR	ORGANO_CONTRATACION	DURACION	CPV	OBJETO	URGENTE	Canon	IMPORTE_CONIVA	IMPORTE_SINIVA	EnlaceInteres Plataforma*/
		HSSFWorkbook wb = null;
		List<Oferta> ofertas=new ArrayList<Oferta>();
		String expediente="";
		Boolean ganador=false;
		try {
			List<String> cabeceras=new ArrayList<String>();
			HashMap<String,String> datos= new HashMap<String, String>();
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
						for(int c=0;c<hssfRow.getLastCellNum();c++){
							cellValue = hssfRow.getCell(c) == null ? "" :
									(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_STRING) ? hssfRow.getCell(c).getStringCellValue() :
											(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_NUMERIC) ? "" + hssfRow.getCell(c).getNumericCellValue() :
													(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_BOOLEAN) ? "" + hssfRow.getCell(c).getBooleanCellValue() :
															(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_BLANK) ? "BLANK" :
																	(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_FORMULA) ? "FORMULA" :
																			(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_ERROR) ? "ERROR" : "";
							if(!cellValue.equals("")) {
								datos.put(cellValue.toString(),"");
							}
							System.out.println(cellValue.toString());

						}
					}
					else{
						Contrato contrato = new Contrato();
						for(int c=0;c<hssfRow.getLastCellNum();c++){
							cellValue = hssfRow.getCell(c) == null ? "" :
									(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_STRING) ? hssfRow.getCell(c).getStringCellValue() :
											(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_NUMERIC) ? "" + hssfRow.getCell(c).getNumericCellValue() :
													(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_BOOLEAN) ? "" + hssfRow.getCell(c).getBooleanCellValue() :
															(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_BLANK) ? "BLANK" :
																	(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_FORMULA) ? "FORMULA" :
																			(hssfRow.getCell(c).getCellType() == Cell.CELL_TYPE_ERROR) ? "ERROR" : "";

							if(c==0){
								EntidadContratante entity=new EntidadContratante();
								expediente=cellValue;
								expediente=expediente.replace("/", "-");
								BigDecimal entidad=Utils.obtenerEntidad(Funciones.getPeticion());
								if(entidad!=null) {
									if("S".equals(hssfRow.getCell(4).getStringCellValue()) ) {
										expediente = "MEN".concat(expediente);
									}
									if (expedienteEntidad.containsKey(entidad)) {
										if (!expediente.contains(expedienteEntidad.get(entidad))) {
											expediente = expedienteEntidad.get(entidad).toString().concat(expediente);
										}

									}
									entity.setId(entidad);
								}else{
									entity.setId(BigDecimal.valueOf(1.0));
								}
								contrato.setEntity(entity);
								contrato.setExpediente(expediente);
							}
							if(c==1){
								contrato.setTitle(cellValue.toString());
							}
							if(c==2){
								Tipocontrato tipo=new Tipocontrato();
								if(cellValue.toString().contains("Obras")){
									tipo.setId(BigDecimal.valueOf(1));
								}
								if(cellValue.toString().contains("Servicios")){
									tipo.setId(BigDecimal.valueOf(2));
								}
								if(cellValue.toString().contains("Suministros")){
									tipo.setId(BigDecimal.valueOf(3));
								}
								if(cellValue.toString().contains("Otros")){
									tipo.setId(BigDecimal.valueOf(4));
								}
								if(cellValue.toString().contains("Privado")){
									tipo.setId(BigDecimal.valueOf(5));
								}
								tipo.setTitle(cellValue);

								contrato.setType(tipo);
							}
							if(c==3 && !"".equals(cellValue.toString())){
								Date expiracion=hssfRow.getCell(c).getDateCellValue();
								contrato.setExpiration(expiracion);
							}
							if(c==4){
									Procedimiento procedimiento = new Procedimiento();
									procedimiento.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
									contrato.setProcedimiento(procedimiento);

							}
							if(c==5){
								EstructuraOrganizativa servicio=new EstructuraOrganizativa();
								servicio.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
									contrato.setServicio(servicio);
							}
							if(c==6){
								EstructuraOrganizativa organo=new EstructuraOrganizativa();
								organo.setId(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
								contrato.setOrganoContratante(organo);//
							}
							if(c==7){
								contrato.setDuracion(BigDecimal.valueOf(Double.valueOf(cellValue)));
							}
							if(c==8 && !"".equals(cellValue) && !"BLANK".equals(cellValue)){
								List<Cpv> listadoCpv=new ArrayList<Cpv>();
								String[] cpvsExcel;
								Set<Cpv> cpvs= new HashSet<Cpv>();
								if(cellValue.contains(",")) {
									cpvsExcel = cellValue.split(",");
									for (int i=0;i<cpvsExcel.length;i++){
										Cpv item=new Cpv();
										item.setId(BigDecimal.valueOf(Double.valueOf(cpvsExcel[i])));
										cpvs.add(item);
									}
								}else{
									Cpv item=new Cpv();
									item.setId(BigDecimal.valueOf(Double.valueOf(cellValue)));
									cpvs.add(item);
								}
								contrato.setCpv(cpvs);
							}
							if(c==9){
								contrato.setObjeto(cellValue.toString());
							}
							if(c==10){
								if(cellValue.toString().equals("No"))
									contrato.setUrgente("N");
								else
									contrato.setUrgente("S");
							}
							if(c==12){
								contrato.setImporteSinIVA(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
							}
							if(c==13){
								contrato.setImporteConIVA(BigDecimal.valueOf(Double.valueOf(cellValue.toString())));
							}
							if(c==14){
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
							contrato.setCreationDate( Calendar.getInstance().getTime());
							contrato.setFechaContrato( Calendar.getInstance().getTime());
							contrato.setVisible("N");
							Estado estado=new Estado();
							estado.setId(0);
							contrato.setStatus(estado);
						}
						contratos.add(contrato);
					}
				}
			}
		return contratos;
		} catch(Exception ioe) {
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
	            	
	            	Search searchContratos=new Search(Contrato.class);
	            	searchContratos.addFilter(Filter.equal("expediente", expediente));
	            	List<Contrato> lista =  this.search(searchContratos);
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

}