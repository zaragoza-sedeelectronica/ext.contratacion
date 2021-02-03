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

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.entity.DatosIndicadoresProcedimiento;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class DatosProcedimientoGenericDAOImpl extends GenericDAOImpl<DatosIndicadoresProcedimiento,BigDecimal > implements DatosProcedimientoGenericDAO {
    @PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }


    @Override
    public SearchResult<DatosIndicadoresProcedimiento> contratoMasParticipantes(BigDecimal idPortal, String anyo) {
        Query q = em().createQuery("from DatosIndicadoresProcedimiento where idPortal=? and anyo=? Order By total asc   " ,DatosIndicadoresProcedimiento.class).setParameter(1,idPortal).setParameter(2,anyo);
        @SuppressWarnings("unchecked")

        List<DatosIndicadoresProcedimiento> lista = q.getResultList();
        SearchResult<DatosIndicadoresProcedimiento> resultado = new SearchResult<DatosIndicadoresProcedimiento>();
        resultado.setResult(lista);
        resultado.setStart(0);
        resultado.setTotalCount(lista.size());
        resultado.setRows(lista.size());
        return resultado;
    }

    @Override
    public SearchResult<BigDecimal> totaleContratoPorAnyoPortal(BigDecimal idPortal, String anyo) {
        Query q =em().createNativeQuery(" SELECT COUNT(ID_CONTRATO) as TOTALCONTRATO from PERFIL_CONTRATO where ID_PORTAL=? and TO_CHAR(GCZ_FECHACONTRATO, 'yyyy')=?  GROUP BY ID_PORTAL,TO_CHAR(GCZ_FECHACONTRATO, 'yyyy')")
                .setParameter(1,idPortal)
                .setParameter(2,anyo);
        @SuppressWarnings("unchecked")
                List<BigDecimal> total=q.getResultList();
        SearchResult<BigDecimal> resultado=new SearchResult<BigDecimal>();
        resultado.setResult(total);
        resultado.setStart(0);
        resultado.setTotalCount(total.size());
        resultado.setRows(total.size());
        return resultado;
    }
}
