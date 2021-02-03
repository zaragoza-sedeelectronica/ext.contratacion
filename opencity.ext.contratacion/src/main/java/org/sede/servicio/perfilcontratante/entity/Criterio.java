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
package org.sede.servicio.perfilcontratante.entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import org.hibernate.annotations.BatchSize;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.Rel;

@XmlRootElement(name = "criterio_awards")
@Entity(name = "Criterios")
@Table(name = "PERFIL_CRITERIOS", schema = "PERFILCONTRATANTE")
@XmlAccessorType(XmlAccessType.FIELD)
@Grafo(Contrato.GRAFO)
@Rel
@SequenceGenerator(name = "SECUENCIA_SEQ_PERFIL_CRITERIO", sequenceName = "SEQ_PERFIL_CRITERIO", allocationSize = 1)
public class Criterio implements java.io.Serializable {

	//region Atributes
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SEQ_PERFIL_CRITERIO")
	@Id
	@Column(name="ID_CRITERIO",nullable=false,unique=true)
	private BigDecimal idCriterio;

	@Column(name="DESCRIPCION",nullable=true,unique=false)
	private String description;

	@Column(name="TITULO",nullable=false,unique=false)
	private String title;
	@Column(name="PESO",nullable=false,unique=false)
	private BigDecimal peso;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "TIPO")
	@BatchSize(size = 50)
	private  TipoCriterio tipo;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_CONTRATO")
	@BatchSize(size = 50)
	@Interno
	private Contrato contrato;

	//endregion
	//region Getters & Setters

	public BigDecimal getIdCriterio() {
		return idCriterio;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public void setIdCriterio(BigDecimal idCriterio) {
		this.idCriterio = idCriterio;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public TipoCriterio getTipo() {
		return tipo;
	}

	public void setTipo(TipoCriterio tipo) {
		this.tipo = tipo;
	}

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}



	//endregion
	//region Overrides
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((contrato == null) ? 0 : contrato.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((idCriterio == null) ? 0 : idCriterio.hashCode());
		result = prime * result + ((tipo == null) ? 0 : tipo.hashCode());
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
		Criterio other = (Criterio) obj;
		if (contrato == null) {
			if (other.contrato != null)
				return false;
		} else if (!contrato.equals(other.contrato))
			return false;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		if (idCriterio == null) {
			if (other.idCriterio != null)
				return false;
		} else if (!idCriterio.equals(other.idCriterio))
			return false;
		if (tipo == null) {
			if (other.tipo != null)
				return false;
		} else if (!tipo.equals(other.tipo))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Criterio [idCriterio=" + idCriterio + ", description="
				+ description + ", title=" + title + ", tipo=" + tipo
				+ "]";
	}


	//endregion






}
