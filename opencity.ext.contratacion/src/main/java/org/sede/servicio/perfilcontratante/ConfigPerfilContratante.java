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
