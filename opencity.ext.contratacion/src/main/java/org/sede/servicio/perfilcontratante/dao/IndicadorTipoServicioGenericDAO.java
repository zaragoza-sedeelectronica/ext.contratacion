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
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimientoServicioGestor;
import org.sede.servicio.perfilcontratante.entity.IndicadoresTipoServicioGestor;

import javax.validation.ConstraintViolation;
import java.math.BigDecimal;
import java.util.Set;

public interface IndicadorTipoServicioGenericDAO extends GenericDAO<IndicadoresTipoServicioGestor,BigDecimal>{
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public SearchResult<IndicadoresTipoServicioGestor> contratoMasParticipantes(BigDecimal idServicio, String anyo);
    public SearchResult<BigDecimal> totaleContratoPorAnyoPorServicio(BigDecimal IdServicio, String anyo);


}
