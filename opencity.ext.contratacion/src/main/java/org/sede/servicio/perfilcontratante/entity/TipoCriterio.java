package org.sede.servicio.perfilcontratante.entity;



import java.math.BigDecimal;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;



@XmlRootElement(name="type_criterio")
@Entity
@Table(name = "PERFIL_TIPOCRITERIO", schema = "PERFILCONTRATANTE")
public class TipoCriterio implements java.io.Serializable {
	//region Atributtes & Columns
	@Id
	@Column(name = "ID_TIPO", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;

	@Column(name = "NOMBRE", nullable = false, length = 500)
	private String title;
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
	//endregion
	//region Overrides
	@Override
	public String toString() {
		return "Tipo Criterio [id=" + id + ", title=" + title + "]";
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
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
		TipoCriterio other = (TipoCriterio) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}
	//endregion
}
