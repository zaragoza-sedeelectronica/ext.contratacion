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
import org.sede.servicio.perfilcontratante.entity.ContratosPorAnyoServicioGestor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;


@Repository
@Transactional(ConfigPerfilContratante.TM)
public class ContratosPorAnyoServicioDAOImpl extends GenericDAOImpl<ContratosPorAnyoServicioGestor, BigDecimal> implements ContratosPorAnyoServicioDAO {
    

    @PersistenceContext(unitName = ConfigPerfilContratante.ESQUEMA)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }

    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
    public BigDecimal consultaTotalLicitadore(BigDecimal servicio,String year){
        Query q = em().createNativeQuery("select count(id_ofer) from perfil_oferta where id_contrato in(select id_contrato from perfil_contrato where servicio_gestor=? and TO_CHAR(GCZ_FECHACONTRATO,'yyyy')=?)").setParameter(1,servicio).setParameter(2,year);
        return (BigDecimal) q.getSingleResult();
    }

}
