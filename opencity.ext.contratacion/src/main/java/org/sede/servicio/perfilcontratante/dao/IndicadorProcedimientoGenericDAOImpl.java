package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.entity.DatosIndicadoresProcedimiento;
import org.sede.servicio.perfilcontratante.entity.IndicadoresProcedimiento;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;

@Repository
@Transactional(ConfigPerfilContratante.TM)
public class IndicadorProcedimientoGenericDAOImpl extends GenericDAOImpl<IndicadoresProcedimiento,BigDecimal > implements IndicadorProcedimientoGenericDAO {
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

//    @Override
    public SearchResult<BigDecimal> totaleContratoPorAnyoPortal(BigDecimal idPortal, String anyo) {
        Query q =em().createNativeQuery(" SELECT COUNT(ID_CONTRATO) as TOTALCONTRATO from PERFIL_CONTRATO where ID_PORTAL=? and TO_CHAR(GCZ_FECHAALTA, 'yyyy')=?  GROUP BY ID_PORTAL,TO_CHAR(GCZ_FECHAALTA, 'yyyy')")
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
