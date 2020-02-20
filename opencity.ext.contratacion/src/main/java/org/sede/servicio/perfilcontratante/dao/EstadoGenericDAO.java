package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.Estado;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface EstadoGenericDAO extends GenericDAO<Estado, Integer> {

    public Set<ConstraintViolation<Object>> validar(Object registro);
}
