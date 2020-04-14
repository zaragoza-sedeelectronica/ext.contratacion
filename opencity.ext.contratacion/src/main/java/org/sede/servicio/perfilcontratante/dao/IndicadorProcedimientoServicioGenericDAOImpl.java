package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;

import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimientoServicioGestor;
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
public class IndicadorProcedimientoServicioGenericDAOImpl extends GenericDAOImpl<IndicadoresProcedimientoServicioGestor,BigDecimal > implements IndicadorProcedimientoServicioGenericDAO {
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
    public SearchResult<IndicadoresProcedimientoServicioGestor> contratoMasParticipantes(BigDecimal idServicio, String anyo) {
        Query q = em().createQuery("from IndicadoresProcedimientoServicioGestor where SERVICIO_GESTOR=? and anyo=? Order By total asc   " ,IndicadoresProcedimientoServicioGestor.class).setParameter(1,idServicio).setParameter(2,anyo);
        @SuppressWarnings("unchecked")

        List<IndicadoresProcedimientoServicioGestor> lista = q.getResultList();
        SearchResult<IndicadoresProcedimientoServicioGestor> resultado = new SearchResult<IndicadoresProcedimientoServicioGestor>();
        resultado.setResult(lista);
        resultado.setStart(0);
        resultado.setTotalCount(lista.size());
        resultado.setRows(lista.size());
        return resultado;
    }

    @Override
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
