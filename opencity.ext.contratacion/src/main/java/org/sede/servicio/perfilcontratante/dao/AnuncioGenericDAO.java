package org.sede.servicio.perfilcontratante.dao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Set;

import javax.validation.ConstraintViolation;

import com.googlecode.genericdao.dao.jpa.GenericDAO;

import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.springframework.web.multipart.MultipartFile;

public interface AnuncioGenericDAO extends GenericDAO<Anuncio, BigDecimal> {
	public Set<ConstraintViolation<Object>> validar(Object registro);
	long updateVisible(BigDecimal identifier, String string);
	public Boolean contratoSellado(BigDecimal id);
	public void asociarAnexos(Anuncio registro, MultipartFile file) throws IOException;
	public byte[] obtenerAnexos(Anuncio registro);
	public boolean eliminar(BigDecimal id);
	public void almacenar(Anuncio registro) throws IOException ;
	
}
