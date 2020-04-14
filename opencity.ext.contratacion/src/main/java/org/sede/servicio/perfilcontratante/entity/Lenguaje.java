package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.PathId;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "lenguaje")
@Entity
@Table(name = "PERFIL_IDIOMA", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING + "/idioma")


public class Lenguaje extends EntidadBase implements java.io.Serializable{
	//region Atributtes & Columns
	@Id
	@Column(name = "ID", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;
	@Column(name="CODIGO" ,unique = true, nullable = false, precision = 22, scale = 0)
	private String codigo;
	@Column(name="IDIOMA")
	private String idioma;
	//endregion
	//region Getters &Setters

	public BigDecimal getId() {return id;}
	public void setId(BigDecimal id) {this.id = id;}
	public String getCodigo() {return codigo;}
	public void setCodigo(String codigo) {this.codigo = codigo;}
	public String getIdioma() {return idioma;}
	public void setIdioma(String idioma) {this.idioma = idioma;}
	//endregion
	//region Overrides

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Lenguaje)) return false;

		Lenguaje lenguaje = (Lenguaje) o;

		if (getId() != null ? !getId().equals(lenguaje.getId()) : lenguaje.getId() != null) return false;
		if (getCodigo() != null ? !getCodigo().equals(lenguaje.getCodigo()) : lenguaje.getCodigo() != null)
			return false;
		return getIdioma() != null ? getIdioma().equals(lenguaje.getIdioma()) : lenguaje.getIdioma() == null;

	}

	@Override
	public int hashCode() {
		int result = getId() != null ? getId().hashCode() : 0;
		result = 31 * result + (getCodigo() != null ? getCodigo().hashCode() : 0);
		result = 31 * result + (getIdioma() != null ? getIdioma().hashCode() : 0);
		return result;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Lenguaje[");
		sb.append(", codigo='").append(codigo).append('\'');
		sb.append(']');
		return sb.toString();
	}
	//endregion

}
