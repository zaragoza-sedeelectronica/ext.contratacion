package org.sede.servicio.perfilcontratante;

import org.sede.core.PropertyFileInterface;
import org.sede.core.anotaciones.Esquema;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigPerfilContratante implements PropertyFileInterface {
	public static final String ESQUEMA = Esquema.PERFILCONTRATANTE;
	public static final String TM = Esquema.TMPERFILCONTRATANTE;
	
	public String getSchema() {
		return ESQUEMA.toLowerCase();
	}
	public String getJndi() {
		return "WebPerfilDS";
	}

	public String getEntity() {
		return "org.sede.servicio.perfilcontratante.entity";
	}

}
