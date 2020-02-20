package org.sede.servicio.perfilcontratante.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.EmpresaConParticipacion;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;

@Repository
@Transactional(ConfigPerfilContratante.TM)
public class EmpresaGenericDAOImpl extends GenericDAOImpl<Empresa,BigDecimal> implements EmpresaGenericDAO{
    //region Atributtes
    @PersistenceContext(unitName=ConfigPerfilContratante.ESQUEMA)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    //endregion
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
//	@Override
	public SearchResult<Empresa> findAdjudicadores() {
		Query q = em().createQuery("from Empresa where id_empresa in (select empresa.id from Oferta where ganador='S') ", Empresa.class);
		@SuppressWarnings("unchecked")
		List<Empresa> lista = q.getResultList();
		SearchResult<Empresa> resultado = new SearchResult<Empresa>();
		resultado.setResult(lista);
		resultado.setStart(0);
		resultado.setTotalCount(lista.size());
		resultado.setRows(lista.size());
		return resultado;
		
	}
//	@Override
	public List<Contrato> findContratosAdjudicados(BigDecimal id) {
		Query q = em().createQuery("from Contrato where id in (select contrato.id from Oferta where empresa.idEmpresa=? and ganador='S')", Contrato.class).setParameter(1, id);
		@SuppressWarnings("unchecked")
		List<Contrato> lista = q.getResultList();
		return lista;
	}

//	@Override
	public SearchResult<Empresa> findEmpresaUte() {
		Query q = em().createQuery("from Empresa where id_empresa in (select empresa.id from Oferta where tieneUte='S') ", Empresa.class);
		@SuppressWarnings("unchecked")
		List<Empresa> lista = q.getResultList();
		SearchResult<Empresa> resultado = new SearchResult<Empresa>();
		resultado.setResult(lista);
		resultado.setStart(0);
		resultado.setTotalCount(lista.size());
		resultado.setRows(lista.size());
		return resultado;
	}

//	@Override
	public List<Empresa> findEmpresaUte(BigDecimal id) {
		Query q = em().createQuery("from Empresa where id_empresa in (select empresa.idEmpresa from Ute where ofertaUte.empresa.id=?) ", Empresa.class).setParameter(1,id);
		@SuppressWarnings("unchecked")
		List<Empresa> lista = q.getResultList();
		return lista;
	}

//	@Override
	public SearchResult<Empresa> findEmpresaPertneceUte(BigDecimal id) {
		Query q = em().createNativeQuery("Select * from PERFIL_EMPRESA E where E.ID_EMPRESA in (select id_UTE from PERFIL_UTE U where U.ID_EMPRESA=?) ", Empresa.class).setParameter(1,id);
		@SuppressWarnings("unchecked")
		List<Empresa> lista = q.getResultList();
		SearchResult<Empresa> resultado=new SearchResult<Empresa>();
		resultado.setResult(lista);
		resultado.setTotalCount(lista.size());
		resultado.setRows(lista.size());
		resultado.setStart(0);
		return resultado;
	}
//	@Override
	public List<EmpresaConParticipacion> findEmpresaEnUte(BigDecimal id) {
		Query q = em().createNativeQuery("select E.Id_Empresa,E.Nombre,U.Por_Par "
				+ "from perfil_empresa e, perfil_ute u "
				+ "where E.Id_Empresa = U.Id_ute and u.id_empresa=?").setParameter(1,id);

		ArrayList<EmpresaConParticipacion> list = new ArrayList<EmpresaConParticipacion>();
		@SuppressWarnings("unchecked")
		List<Object> result = q.getResultList();
		for (Iterator<Object> iterador = result.iterator(); iterador.hasNext();) {
    		Object[] row = (Object[])iterador.next();
    		EmpresaConParticipacion res = new EmpresaConParticipacion();
    		Empresa e = new Empresa();
    		e.setIdEmpresa((BigDecimal)row[0]);
    		e.setNombre((String)row[1]);
    		res.setEmpresa(e);
    		res.setParticipacion((BigDecimal)row[2]);
    		list.add(res);
		}
		return list;
	}

}
