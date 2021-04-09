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
import org.sede.core.rest.Mensaje;
import org.sede.servicio.perfilcontratante.entity.Ute;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import com.googlecode.genericdao.search.SearchResult;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class UteGenericDAOImpl extends GenericDAOImpl<Ute,BigDecimal> implements UteGenericDAO{
    @PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }

    @Override
    public SearchResult<?> findUte() {
        Query q = em().createQuery("from Ute where id_ute in (select empresa.id from Oferta where tieneUte='S') ", Ute.class);
        @SuppressWarnings("unchecked")
        List<Ute> lista = q.getResultList();
        SearchResult<Ute> resultado = new SearchResult<Ute>();
        resultado.setResult(lista);
        resultado.setStart(0);
        resultado.setTotalCount(lista.size());
        resultado.setRows(lista.size());
        return resultado;
    }
	@Override
	public ResponseEntity<?> asociarAEmpresa(BigDecimal idEmpresa, BigDecimal incluir, BigDecimal participacion) {
		try {
			Query insert = this.em().createNativeQuery("insert into PERFIL_UTE (ID_EMPRESA,ID_UTE,POR_PAR) values (?,?,?)");
			insert.setParameter(1, incluir);
			insert.setParameter(2,idEmpresa );
			insert.setParameter(3, participacion);
			if (insert.executeUpdate() > 0) {
				return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(), "Empresa asociada a la ute correctamente"));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), "No se ha asociado la empresa a la ute"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), e.getMessage()));		
		}
		
	}
	@Override
	public ResponseEntity<?> eliminarAsociacion(BigDecimal idEmpresa, BigDecimal excluir) {
		try {
			Query insert = this.em().createNativeQuery("delete from PERFIL_UTE where ID_EMPRESA=? and ID_UTE=?");
			insert.setParameter(1, excluir);
			insert.setParameter(2, idEmpresa);

			if (insert.executeUpdate() > 0) {
				return ResponseEntity.ok(new Mensaje(HttpStatus.OK.value(), "Empresa excluida de la ute correctamente"));
			} else {
				return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), "No se ha excluido la empresa de la ute"));
			}
		} catch (Exception e) {
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Mensaje(HttpStatus.BAD_REQUEST.value(), e.getMessage()));		
		}
	}

}
