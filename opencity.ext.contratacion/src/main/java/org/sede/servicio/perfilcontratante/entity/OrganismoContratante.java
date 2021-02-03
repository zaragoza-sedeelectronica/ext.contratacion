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

import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "organismoContratante")
@Embeddable
public class OrganismoContratante extends EstructuraOrganizativa implements java.io.Serializable {

	//region Atributtes
	@Transient
	private BigDecimal id;
	@Transient
	private String title;
	@Transient
	private BigDecimal organismoContratante;
    @Transient
    private SearchResult<Contrato> contrato;
    @Transient
    private List<ContratosPorAnyoOrganismoContratante> datos;
	@Transient
	private String year;
	//endregion
	//region Getters & Setters
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	@Override
	public BigDecimal getId() {
		return id;
	}
	@Override
	public void setId(BigDecimal id) {
		this.id = id;
	}
	@Override
	public String getTitle() {
		return title;
	}
	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	public BigDecimal getOrganismoContratante() {return organismoContratante;}
	public void setOrganismoContratante(BigDecimal organismoContratante) {	this.organismoContratante = organismoContratante;}
	public SearchResult<Contrato> getContrato() {
		return contrato;
	}
	public void setContrato(SearchResult<Contrato> contrato) {
		this.contrato = contrato;
	}
	public List<ContratosPorAnyoOrganismoContratante> getDatos() {
		return datos;
	}
	public void setDatos(List<ContratosPorAnyoOrganismoContratante> datos) {this.datos = datos;}
	//endregion
   	//region Overrides
   	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrganismoContratante)) return false;

        OrganismoContratante that = (OrganismoContratante) o;

        if (!getId().equals(that.getId())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        return getUri().equals(that.getUri());
    }
	@Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getUri().hashCode();
        return result;
    }
	//endregion
}
