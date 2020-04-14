package org.sede.servicio.perfilcontratante.entity;

import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name="type")
@Entity
@Table(name = "PERFIL_TIPOCONTRATO", schema = ConfigPerfilContratante.ESQUEMA)
public class Tipocontrato extends EntidadBase implements java.io.Serializable{
	@Id
	@Column(name = "ID_TIPOCONTRATO", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;
	
	@Column(name = "NOMBRE", nullable = false, length = 500)
	private String title;
	
	@Column(name = "TYPE", nullable = false, length = 500)
	private String type;
	
//	@OneToMany(fetch = FetchType.LAZY, mappedBy = "type")
//	private Set<Contrato> perfilContratos = new HashSet<Contrato>(0);

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

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "Tipocontrato [id=" + id + ", title=" + title + ", type=" + type
				+ "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
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
		Tipocontrato other = (Tipocontrato) obj;
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
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		return true;
	}

	
}
