package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Criterio;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.ConstraintViolation;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

public interface CriteriosGenericDAO extends GenericDAO<Criterio, BigDecimal> {
	public Set<ConstraintViolation<Object>> validar(Object registro);

	
}
