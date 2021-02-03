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

import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.entity.IndicadorLicitadorServicio;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.math.BigDecimal;
import java.util.Set;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class IndicadorLicitadorServicioGenericDAOImpl extends GenericDAOImpl<IndicadorLicitadorServicio,BigDecimal > implements IndicadorLicitadorServicioGenericDAO{
    //region Atributtes

    @PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
    private static final Logger logger = LoggerFactory.getLogger(IndicadorLicitadorServicioGenericDAOImpl.class);
    //endregion
    public ResponseEntity<IndicadorLicitadorServicio> getIlicitadoresGanadoresIndicadores(Integer idEntidad, String year) {
         ResponseEntity<IndicadorLicitadorServicio> resultados = null;
        try {
            return resultados;
        } catch (Exception e) {
            logger.error(e.getMessage());
            return resultados;
        }
    }
    public BigDecimal consultaTotalLicitaciones(BigDecimal idEmpresa,BigDecimal servicio,String year){
        Query q = em().createNativeQuery("select COUNT(O.ID_OFER) as TOTALGANADOS\n" +
                " from PERFILCONTRATANTE.PERFIL_CONTRATO CONT" +
                " inner join PERFILCONTRATANTE.PERFIL_OFERTA O" +
                " on CONT.ID_CONTRATO = O.ID_CONTRATO" +
                "  inner join PERFILCONTRATANTE.PERFIL_EMPRESA E" +
                "  on E.ID_EMPRESA=O.ID_EMPRESA" +
                "  where CONT.ID_PORTAL=1" +
                "  and E.ID_EMPRESA=:IDEMPRESA " +
                "  and  TO_CHAR(CONT.GCZ_FECHACONTRATO,'yyyy')=:YEAR" +
                "  and CONT.SERVICIO_GESTOR=:IDSERVICIO")
                .setParameter("IDEMPRESA",idEmpresa)
                .setParameter("IDSERVICIO",servicio)
                .setParameter("YEAR",year);
        return (BigDecimal) q.getSingleResult();
    }
}
