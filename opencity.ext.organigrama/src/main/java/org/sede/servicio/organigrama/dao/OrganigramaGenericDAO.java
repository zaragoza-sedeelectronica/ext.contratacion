package org.sede.servicio.organigrama.dao;

import java.math.BigDecimal;
import java.util.List;

import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;

import com.googlecode.genericdao.dao.jpa.GenericDAO;
import org.thymeleaf.Arguments;
import org.thymeleaf.dom.Node;
import org.thymeleaf.processor.ProcessorResult;

public interface OrganigramaGenericDAO extends GenericDAO<EstructuraOrganizativa, BigDecimal> {
	public List<EstructuraOrganizativa> listado(Integer id);
	public List<EstructuraOrganizativa> listadoHijos(Integer id);
	public List<EstructuraOrganizativa> query(String consulta);
	public String obtenerNombreUnidadSuperior(EstructuraOrganizativa registro);
	public ProcessorResult getForTag(Arguments arguments, Node node);
}
