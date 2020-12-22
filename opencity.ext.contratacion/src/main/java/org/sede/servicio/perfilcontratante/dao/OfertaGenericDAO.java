package org.sede.servicio.perfilcontratante.dao;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.validation.ConstraintViolation;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

public interface OfertaGenericDAO extends GenericDAO<Oferta, BigDecimal> {
    public Set<ConstraintViolation<Object>> validar(Object registro);
    public List<Oferta> loadFromXls(InputStream pathXls)throws IOException;
    public Contrato loadFromJson(String pathXls)throws IOException;
}

