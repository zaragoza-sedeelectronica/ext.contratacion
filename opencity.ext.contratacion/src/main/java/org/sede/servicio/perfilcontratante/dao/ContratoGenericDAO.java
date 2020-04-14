package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.ContratoHome;
import org.sede.servicio.perfilcontratante.entity.Cpv;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.context.ITemplateContext;
import org.thymeleaf.model.IModel;
import org.thymeleaf.model.IProcessableElementTag;
import org.thymeleaf.processor.element.IElementTagStructureHandler;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

public interface ContratoGenericDAO extends GenericDAO<Contrato, BigDecimal> {
	public ContratoHome getResultsForHome();
	public IModel getForTag(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler);
	public HashMap<Integer,Integer> getContratosPorAnyo(String nif);
	public List<BigDecimal> getServicioGestor(BigDecimal id);
	public List<String> getOrganismoContratante();
	public Set<ConstraintViolation<Object>> validar(Object registro);
	public long updateVisible(BigDecimal identifier, String string);
	public HSSFWorkbook generarExcelTribunalCuentas(SearchResult<Contrato> resultado);
	public List<Contrato> getContratosCPV(Set<Cpv> cpv);
	public List<Contrato> loadXls(InputStream flujo)throws IOException;
	
	public ResponseEntity updateFromVirtuoso();
	
}
