package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.entity.Indicador;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.Set;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class IndicadorGenericDAOImpl extends GenericDAOImpl<Indicador,BigDecimal > implements IndicadorGenericDAO{
    //region Atributtes

    @PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
    private static final Logger logger = LoggerFactory.getLogger(IndicadorGenericDAOImpl.class);
    //endregion
    public BigDecimal consultaTotalGanados(BigDecimal idEmpresa,String year,String idPortal){
        Query q = em().createNativeQuery("select COUNT(O.ID_OFER) as TOTALGANADOS\n" +
                " from PERFILCONTRATANTE.PERFIL_CONTRATO CONT" +
                " inner join PERFILCONTRATANTE.PERFIL_OFERTA O" +
                " on CONT.ID_CONTRATO = O.ID_CONTRATO" +
                " inner join PERFILCONTRATANTE.PERFIL_EMPRESA E" +
                " on E.ID_EMPRESA=O.ID_EMPRESA" +
                " where CONT.ID_PORTAL=:IDPORTAL and O.GANADOR='S'"+
                " and E.ID_EMPRESA=:IDEMPRESA " +
                " and  TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy')=:YEAR")
                .setParameter("IDEMPRESA",idEmpresa)
                .setParameter("YEAR",year)
                .setParameter("IDPORTAL",BigDecimal.valueOf(Double.valueOf(idPortal)));

        return (BigDecimal) q.getSingleResult();

    }
}
