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
import org.sede.servicio.perfilcontratante.entity.ContratosPorAnyoServicioGestor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;


@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class ContratosPorAnyoServicioDAOImpl extends GenericDAOImpl<ContratosPorAnyoServicioGestor, BigDecimal> implements ContratosPorAnyoServicioDAO {
    

    @PersistenceContext(unitName = Esquema.PERFILCONTRATANTE)
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
