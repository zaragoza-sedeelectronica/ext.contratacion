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

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class AnuncioIdGenerator implements IdentifierGenerator {

    public Serializable generate(SessionImplementor session, Object object) {
        BigDecimal nextValue = new BigDecimal(-1);
        Statement st = null;
        ResultSet rs = null;
        try {
        	st = session.connection().createStatement();
        	rs = st.executeQuery("select max(id_anuncio)+1 from perfil_anuncio");
        	if (rs.next()) {
        		nextValue = new BigDecimal(rs.getLong(1));
        	}
        	return nextValue;
        } catch (SQLException e) {
        	throw new HibernateException(e);
		} finally {
			if (rs != null) {
        		try {
					rs.close();
				} catch (SQLException e) {
					;
				}
        	}
        	if (st != null) {
        		try {
					st.close();
				} catch (SQLException e) {
					;
				}
        	}
        }
    }
}