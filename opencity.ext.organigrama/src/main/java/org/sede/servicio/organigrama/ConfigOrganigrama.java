package org.sede.servicio.organigrama;

import org.sede.core.PropertyFileInterface;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigOrganigrama implements PropertyFileInterface {
	public static final String ESQUEMA = "GENERAL";
	public static final String TM = "transactionManagerGeneral";
	
	public String getSchema() {
		return ESQUEMA.toLowerCase();
	}
	public String getJndi() {
		return "WebGeneralDS";
	}

	public String getEntity() {
		return "org.sede.servicio.organigrama.entity";
	}

}