/* Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
package org.sede.servicio.perfilcontratante.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "perfil-contratante-home")
public class ContratoHome {
	
	private List<Contrato> open = new ArrayList<Contrato>();
	private List<Contrato> lastModified = new ArrayList<Contrato>();
	
	public List<Contrato> getOpen() {
		return open;
	}
	public void setOpen(List<Contrato> open) {
		this.open = open;
	}
	public List<Contrato> getLastModified() {
		return lastModified;
	}
	public void setLastModified(List<Contrato> lastUpdated) {
		this.lastModified = lastUpdated;
	}
	
	
}
