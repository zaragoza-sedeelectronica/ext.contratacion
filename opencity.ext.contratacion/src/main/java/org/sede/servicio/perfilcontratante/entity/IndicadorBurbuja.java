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

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "indicadorBurbuja")
@Embeddable
public class IndicadorBurbuja extends EntidadBase implements java.io.Serializable {
	//region Atributes
	@Transient
	private BigDecimal idContrato;
	@Transient
	private String empresa;
	@Transient
	private BigDecimal cuantia;
	@Transient
	private String procedimiento;
	@Transient
	private String tipo;
	@Transient
	private String title;

	//endregion
	//region getter and setter

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getIdContrato() {
		return idContrato;
	}

	public void setIdContrato(BigDecimal idContrato) {
		this.idContrato = idContrato;
	}

	public String getEmpresa() {
		return empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
	}

	public BigDecimal getCuantia() {
		return cuantia;
	}

	public void setCuantia(BigDecimal cuantia) {
		this.cuantia = cuantia;
	}

	public String getProcedimiento() {
		return procedimiento;
	}

	public void setProcedimiento(String procedimiento) {
		this.procedimiento = procedimiento;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}


	//endregion
	//region Override

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("IndicadorBurbuja [");
		sb.append("idContrato=").append(idContrato);
		sb.append(", Empresa='").append(empresa).append('\'');
		sb.append(", Cuantia=").append(cuantia);
		sb.append(", procedimiento='").append(procedimiento).append('\'');
		sb.append(", tipo='").append(tipo).append('\'');
		sb.append(']');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof IndicadorBurbuja)) return false;
		if (!super.equals(o)) return false;

		IndicadorBurbuja that = (IndicadorBurbuja) o;

		if (!getIdContrato().equals(that.getIdContrato())) return false;
		if (getEmpresa() != null ? !getEmpresa().equals(that.getEmpresa()) : that.getEmpresa() != null) return false;
		if (getCuantia() != null ? !getCuantia().equals(that.getCuantia()) : that.getCuantia() != null) return false;
		if (getProcedimiento() != null ? !getProcedimiento().equals(that.getProcedimiento()) : that.getProcedimiento() != null)
			return false;
		return getTipo() != null ? getTipo().equals(that.getTipo()) : that.getTipo() == null;

	}
	//endregion
}
