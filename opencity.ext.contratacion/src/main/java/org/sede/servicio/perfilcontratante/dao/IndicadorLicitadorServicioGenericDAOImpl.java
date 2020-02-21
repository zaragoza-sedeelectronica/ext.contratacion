package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
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
import org.sede.servicio.perfilcontratante.entity.IndicadorLicitadorServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;

@Repository
@Transactional(ConfigPerfilContratante.TM)
public class IndicadorLicitadorServicioGenericDAOImpl extends GenericDAOImpl<IndicadorLicitadorServicio,BigDecimal > implements IndicadorLicitadorServicioGenericDAO{
    //region Atributtes

    @PersistenceContext(unitName=ConfigPerfilContratante.ESQUEMA)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
    private static final Logger logger = LoggerFactory.getLogger(IndicadorLicitadorServicioGenericDAOImpl.class);
    //endregion
    public ResponseEntity<IndicadorLicitadorServicio> getIlicitadoresGanadoresIndicadores(Integer idEntidad, String year) {
         ResponseEntity<IndicadorLicitadorServicio> resultados = null;
        try {
            return resultados;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return resultados;
        }
    }
    public BigDecimal consultaTotalLicitaciones(BigDecimal idEmpresa,BigDecimal servicio,String year){
        Query q = em().createNativeQuery("select COUNT(O.ID_OFER) as TOTALGANADOS\n" +
                " from PERFIL_CONTRATO CONT" +
                " inner join PERFIL_OFERTA O" +
                " on CONT.ID_CONTRATO = O.ID_CONTRATO" +
                "  inner join PERFIL_EMPRESA E" +
                "  on E.ID_EMPRESA=O.ID_EMPRESA" +
                "  where CONT.ID_PORTAL=1" +
                "  and E.ID_EMPRESA=:IDEMPRESA " +
                "  and  TO_CHAR(CONT.GCZ_FECHAALTA,'yyyy')=:YEAR" +
                "  and CONT.SERVICIO_GESTOR=:IDSERVICIO")
                .setParameter("IDEMPRESA",idEmpresa)
                .setParameter("IDSERVICIO",servicio)
                .setParameter("YEAR",year);
        return (BigDecimal) q.getSingleResult();
    }
}
