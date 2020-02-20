package org.sede.servicio.perfilcontratante;

import org.sede.core.PropertyFileInterface;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigPerfilContratante implements PropertyFileInterface {
	public static final String ESQUEMA = "GENERAL";
	public static final String TM = "transactionManagerGeneral";
	
	public String getSchema() {
		return ESQUEMA.toLowerCase();
	}
	public String getJndi() {
		return "WebGeneralDS";
	}

	public String getEntity() {
		return "org.sede.servicio.perfilcontratante.entity";
	}

}