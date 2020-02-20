package org.sede.servicio.perfilcontratante.entity;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;

import com.googlecode.genericdao.search.SearchResult;

@XmlRootElement(name = "servicioGestor")
@Embeddable
public class ServicioGestor extends EstructuraOrganizativa implements java.io.Serializable {

	//region Atributtes
	@Transient
	private BigDecimal id;
	@Transient
	private String title;
	@Transient
	private BigDecimal servicioGestor;
    @Transient
    private SearchResult<Contrato> contrato;
    @Transient
    private List<ContratosPorAnyoServicioGestor> datos;
    @Transient
    private List<ContratosPorAnyoIdPortal> datosPortal;
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
	public BigDecimal getServicioGestor() {
		return servicioGestor;
	}
	public void setServicioGestor(BigDecimal servicioGestor) {
		this.servicioGestor = servicioGestor;
	}
	public SearchResult<Contrato> getContrato() {
		return contrato;
	}
	public void setContrato(SearchResult<Contrato> contrato) {
		this.contrato = contrato;
	}
	public List<ContratosPorAnyoServicioGestor> getDatos() {
		return datos;
	}
	public void setDatos(List<ContratosPorAnyoServicioGestor> datos) {this.datos = datos;}
	public List<ContratosPorAnyoIdPortal> getDatosPortal() {
		return datosPortal;
	}
	public void setDatosPortal(List<ContratosPorAnyoIdPortal> datosPortal) {
		this.datosPortal = datosPortal;
	}

	//endregion
   	//region Overrides
   	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicioGestor)) return false;

        ServicioGestor that = (ServicioGestor) o;

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
