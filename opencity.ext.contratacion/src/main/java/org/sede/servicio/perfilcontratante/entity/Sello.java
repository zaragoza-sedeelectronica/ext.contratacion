package org.sede.servicio.perfilcontratante.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.BatchSize;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

@XmlRootElement(name="sello")
@Entity
@Table(name = "PERFIL_SELLO", schema = ConfigPerfilContratante.ESQUEMA)
@BatchSize(size = 100)
public class Sello extends EntidadBase implements java.io.Serializable {

	@Id
	@Column(name = "ID_ANUNCIO", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;
	
	@Column(name = "PERMALINK")
	private String permalink;
	
	@Column(name = "IDPUBLICACIONFEHACIENTE", precision = 22, scale = 0)
	private String idfehaciente;

	@Override
	public String toString() {
		return "Sello [id=" + id
				+ ", permalink="
				+ permalink 
				+ ", idfehaciente=" + idfehaciente + "]";
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getPermalink() {
		return permalink;
	}

	public void setPermalink(String permalink) {
		this.permalink = permalink;
	}

	public String getIdfehaciente() {
		if (idfehaciente != null) {
		return  idfehaciente;
		} else {
			return null;
		}
	}

	public void setIdfehaciente(String idfehaciente) {
		this.idfehaciente = idfehaciente;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((idfehaciente == null) ? 0 : idfehaciente.hashCode());
		result = prime * result
				+ ((permalink == null) ? 0 : permalink.hashCode());
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
		Sello other = (Sello) obj;
		
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (idfehaciente == null) {
			if (other.idfehaciente != null) {
				return false;
			}
		} else if (!idfehaciente.equals(other.idfehaciente)) {
			return false;
		}
		if (permalink == null) {
			if (other.permalink != null) {
				return false;
			}
		} else if (!permalink.equals(other.permalink)) {
			return false;
		}
		return true;
	}



}
