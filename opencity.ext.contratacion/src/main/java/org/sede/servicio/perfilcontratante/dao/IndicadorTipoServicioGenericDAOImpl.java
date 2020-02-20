package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.entity.IndicadoresTipoServicioGestor;
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
@Transactional(ConfigPerfilContratante.TM)
public class IndicadorTipoServicioGenericDAOImpl extends GenericDAOImpl<IndicadoresTipoServicioGestor,BigDecimal> implements IndicadorTipoServicioGenericDAO {
    @PersistenceContext(unitName=ConfigPerfilContratante.ESQUEMA)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }


//    @Override
    public SearchResult<IndicadoresTipoServicioGestor> contratoMasParticipantes(BigDecimal idServicio, String anyo) {
        Query q = em().createQuery("from IndicadoresTipoServicioGestor where SERVICIO_GESTOR=? and anyo=? Order By total asc   " ,IndicadoresTipoServicioGestor.class).setParameter(1,idServicio).setParameter(2,anyo);
        @SuppressWarnings("unchecked")

        List<IndicadoresTipoServicioGestor> lista = q.getResultList();
        SearchResult<IndicadoresTipoServicioGestor> resultado = new SearchResult<IndicadoresTipoServicioGestor>();
        resultado.setResult(lista);
        resultado.setStart(0);
        resultado.setTotalCount(lista.size());
        resultado.setRows(lista.size());
        return resultado;
    }

//    @Override
    public SearchResult<BigDecimal> totaleContratoPorAnyoPorServicio(BigDecimal idServicio, String anyo) {
        Query q =em().createNativeQuery(" SELECT COUNT(ID_CONTRATO) as TOTALCONTRATO from PERFIL_CONTRATO where SERVICIO_GESTOR=? and TO_CHAR(GCZ_FECHACONTRATO, 'yyyy')=?  GROUP BY SERVICIO_GESTOR,TO_CHAR(GCZ_FECHACONTRATO, 'yyyy')")
                .setParameter(1,idServicio)
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
