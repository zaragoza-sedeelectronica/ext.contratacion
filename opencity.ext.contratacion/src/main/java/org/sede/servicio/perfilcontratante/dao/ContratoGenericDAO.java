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
package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import com.googlecode.genericdao.search.SearchResult;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.sede.servicio.perfilcontratante.entity.*;
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
	public Contrato getResultsForExpediente(String expediente);
	public IModel getForTag(ITemplateContext context, IProcessableElementTag tag, IElementTagStructureHandler structureHandler);
	public HashMap<Integer,Integer> getContratosPorAnyo(String nif);
	public List<BigDecimal> getServicioGestor(BigDecimal id);
	public List<String> getOrganismoContratante();
	public Set<ConstraintViolation<Object>> validar(Object registro);
	public long updateVisible(BigDecimal identifier, String string);
	public HSSFWorkbook generarExcelTribunalCuentas(SearchResult<Contrato> resultado,boolean busquedaAvanzada);
	public HSSFWorkbook generarExcelLicitador(Licitador resultado);
	public HSSFWorkbook generarExcelDatosAbiertos(Object resultado);
	public List<Contrato> getContratosCPV(Set<Cpv> cpv);
	public List<Contrato> loadXls(InputStream flujo)throws IOException;
	public ResponseEntity updateFromVirtuoso();
	public SearchResult<ContratoTbfy> jsonTbfy(String url);
	public SearchResult<ContratoTbfy> jsonTbfyQuery(String name);
	public SearchResult<ContratoTbfy> similitudPliegos(String name,String lang,String size,String terms,String source,String texto ) throws IOException;
	public ContratoTbfy similitudPliegosDetalle(String is, String lang) throws IOException;
	public ContratoTbfy licitacionDetalle(String id) throws IOException;
	public SearchResult<ContratoTbfy> jsonTbfyQuerylicitadores(String name,String size,String jurisdiction,String status,String description,String title,String currency,String startDate,String endDate) throws Exception;

}
