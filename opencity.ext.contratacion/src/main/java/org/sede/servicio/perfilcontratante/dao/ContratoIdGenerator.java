package org.sede.servicio.perfilcontratante.dao;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.id.IdentifierGenerator;

public class ContratoIdGenerator implements IdentifierGenerator {

    public Serializable generate(SessionImplementor session, Object object) {
        BigDecimal nextValue = new BigDecimal(-1);
        Statement st = null;
        ResultSet rs = null;
        try {
        	st = session.connection().createStatement();
        	rs = st.executeQuery("select max(id_contrato)+1 from perfil_contrato");
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