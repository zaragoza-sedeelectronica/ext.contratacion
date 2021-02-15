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

import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name="typecompany")
@Entity
@Table(name = "PERFIL_FORMA_JURIDICA_EMPRESA", schema = ConfigPerfilContratante.ESQUEMA)
public class TipoEmpresa extends EntidadBase implements java.io.Serializable{
	//region Atributtes && Columns
	@Id
	@Column(name = "ID_FORMA", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;
	
	@Column(name = "FORMA", nullable = false, length = 500)
	private String title;
	
	@Column(name = "SKOS", nullable = false, length = 500)
	private String skos;
	//endregion
	//region Getters & Setters

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSkos() {
		return skos;
	}

	public void setSkos(String skos) {
		this.skos = skos;
	}
	//endregion
	//region Overrides

	@Override
	public String toString() {
		return "TipoEmpresa [id=" + id + ", title=" + title + ", skos=" + skos
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((skos == null) ? 0 : skos.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TipoEmpresa other = (TipoEmpresa) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (skos == null) {
			if (other.skos != null) {
				return false;
			}
		} else if (!skos.equals(other.skos)) {
			return false;
		}
		return true;
	}
	//endregion

	
}
