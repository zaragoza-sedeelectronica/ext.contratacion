package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.entity.IndicadoresPorAnyoServicioGestor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;


@Repository
@Transactional(ConfigPerfilContratante.TM)
public class IndicadoresPorAnyoServicioDAOImpl extends GenericDAOImpl<IndicadoresPorAnyoServicioGestor, BigDecimal> implements IndicadoresPorAnyoServicioDAO {

    @PersistenceContext(unitName = ConfigPerfilContratante.ESQUEMA)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }

    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
}
