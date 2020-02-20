package org.sede.servicio.perfilcontratante.dao;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintViolation;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.ContratoHome;
import org.sede.servicio.perfilcontratante.entity.Cpv;
import org.springframework.http.ResponseEntity;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;

public interface ContratoGenericDAO extends GenericDAO<Contrato, BigDecimal> {
	public ContratoHome getResultsForHome();
	public ProcessorResult getForTag(Arguments arguments, Node node);
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
