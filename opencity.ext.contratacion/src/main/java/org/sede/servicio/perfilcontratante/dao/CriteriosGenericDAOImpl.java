package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.entity.Criterio;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class CriteriosGenericDAOImpl extends GenericDAOImpl <Criterio, BigDecimal> implements CriteriosGenericDAO {
	
	@PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
	public void setEntityManager(EntityManager entityManager) {
		this.setEm(entityManager);
	}
	public Set<ConstraintViolation<Object>> validar(Object registro) {
		ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
		Validator validator = factory.getValidator();
		return validator.validate(registro);
	}
}