package org.sede.servicio.perfilcontratante.entity;

import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "ayuntamientoEntidad")
@Embeddable
public class AYuntamientoEntidad extends EstructuraOrganizativa implements java.io.Serializable {

	@Transient
	private BigDecimal id;
	
	@Transient
	private String title;
	
	
    @Transient
    private SearchResult<Contrato> contrato;
    
    @Transient
    private List<ContratosPorAnyoServicioGestor> datos;
    
    @Transient
    private List<ContratosPorAnyoIdPortal> datosPortal;
    
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

	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AYuntamientoEntidad)) return false;

        AYuntamientoEntidad that = (AYuntamientoEntidad) o;

        if (!getId().equals(that.getId())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        return getUri().equals(that.getUri());

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

	public void setDatos(List<ContratosPorAnyoServicioGestor> datos) {
		this.datos = datos;
	}

	public List<ContratosPorAnyoIdPortal> getDatosPortal() {
		return datosPortal;
	}

	public void setDatosPortal(List<ContratosPorAnyoIdPortal> datosPortal) {
		this.datosPortal = datosPortal;
	}

	@Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTitle().hashCode();
        result = 31 * result + getUri().hashCode();
        return result;
    }

   

}
