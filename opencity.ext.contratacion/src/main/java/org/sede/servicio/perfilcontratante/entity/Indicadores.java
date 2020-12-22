package org.sede.servicio.perfilcontratante.entity;

import java.math.BigDecimal;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.genericdao.search.SearchResult;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;

@XmlRootElement(name = "indicadores")
@Embeddable
public class Indicadores extends EntidadContratante implements java.io.Serializable {
	//region Atributes
	@Transient
	private BigDecimal id;
	@Transient
	private String title;
	@Transient
	private EstructuraOrganizativa servicioGestor;
	@Transient
	private SearchResult<IndicadoresPorAnyoServicioGestor> indicadorServicio;
	@Transient
    private SearchResult<Indicador> indicadorLicitador;
	@Transient
	private String anyo;
	@Transient
	private SearchResult<IndicadoresTipo> indicadorTipo;
	@Transient
	private SearchResult<IndicadoresTipoServicioGestor> indicadorTipoServicio;
	@Transient
	private SearchResult<IndicadoresProcedimiento> indicadorProcedimiento;
	@Transient
	private SearchResult<IndicadoresProcedimientoServicioGestor> indicadorProcedimientoServicio;
	@Transient
	private SearchResult<IndicadorLicitadorServicio> indicadorLicitadorServicio;
	@Transient
	private SearchResult<IndicadorAhorro> indicadorAhorro;
	@Transient
	private SearchResult<IndicadorAhorroServicio> indicadorAhorroServicio;
	@Transient
	private SearchResult<Contrato> contratos;
	@Transient
	private SearchResult<IndicadorAjudicatario> indicadorAdjudicatarios;
	@Transient
	private BigDecimal cuantia;
	@Transient
	private Integer numEmpresas;
	@Transient
	private SearchResult<IndicadorBurbuja> indicadorBurbuja;
	@Transient
	private BigDecimal cuantiaCanon;
	@Transient
	private Integer numEmpresaCanon;
	@Transient
	private SearchResult<Contrato> contratosCanon;
	@Transient
	private SearchResult<Contrato> contratosExcluidos;
	//endregion
	//region getter and setter


	public SearchResult<IndicadorAjudicatario> getIndicadorAdjudicatarios() {
		return indicadorAdjudicatarios;
	}

	public void setIndicadorAdjudicatarios(SearchResult<IndicadorAjudicatario> indicadorAdjudicatarios) {
		this.indicadorAdjudicatarios = indicadorAdjudicatarios;
	}

	public EstructuraOrganizativa getServicioGestor() {
		return servicioGestor;
	}

	public void setServicioGestor(EstructuraOrganizativa servicioGestor) {
		this.servicioGestor = servicioGestor;
	}

	public SearchResult<IndicadoresTipo> getIndicadorTipo() {
		return indicadorTipo;
	}

	public void setIndicadorTipo(SearchResult<IndicadoresTipo> indicadorTipo) {
		this.indicadorTipo = indicadorTipo;
	}

	public SearchResult<IndicadoresTipoServicioGestor> getIndicadorTipoServicio() {
		return indicadorTipoServicio;
	}

	public void setIndicadorTipoServicio(SearchResult<IndicadoresTipoServicioGestor> indicadorTipoServicio) {
		this.indicadorTipoServicio = indicadorTipoServicio;
	}

	public SearchResult<IndicadorAhorroServicio> getIndicadorAhorroServicio() {
		return indicadorAhorroServicio;
	}

	public void setIndicadorAhorroServicio(SearchResult<IndicadorAhorroServicio> indicadorAhorroServicio) {
		this.indicadorAhorroServicio = indicadorAhorroServicio;
	}

	public SearchResult<Contrato> getContratosExcluidos() {
		return contratosExcluidos;
	}

	public void setContratosExcluidos(SearchResult<Contrato> contratosExcluidos) {
		this.contratosExcluidos = contratosExcluidos;
	}

	public SearchResult<Contrato> getContratosCanon() {
		return contratosCanon;
	}

	public void setContratosCanon(SearchResult<Contrato> contratosCanon) {
		this.contratosCanon = contratosCanon;
	}

	public BigDecimal getCuantiaCanon() {
		return cuantiaCanon;
	}

	public void setCuantiaCanon(BigDecimal cuantiaCanon) {
		this.cuantiaCanon = cuantiaCanon;
	}

	public Integer getNumEmpresaCanon() {
		return numEmpresaCanon;
	}

	public void setNumEmpresaCanon(Integer numEmpresaCanon) {
		this.numEmpresaCanon = numEmpresaCanon;
	}

	public SearchResult<IndicadoresPorAnyoServicioGestor> getIndicadorServicio() {return indicadorServicio;	}
	public SearchResult<IndicadorBurbuja> getIndicadorBurbuja() {
		return indicadorBurbuja;
	}
	public void setIndicadorBurbuja(SearchResult<IndicadorBurbuja> indicadorBurbuja) {this.indicadorBurbuja = indicadorBurbuja;	}
	public Integer getNumEmpresas() {
		return numEmpresas;
	}
	public void setNumEmpresas(Integer numEmpresas) {
		this.numEmpresas = numEmpresas;
	}
	public BigDecimal getCuantia() {
		return cuantia;
	}
	public void setCuantia(BigDecimal cuantia) {
		this.cuantia = cuantia;
	}
	public SearchResult<Contrato> getContratos() {
		return contratos;
	}
	public void setContratos(SearchResult<Contrato> contratos) {
		this.contratos = contratos;
	}
	public SearchResult<IndicadorAhorro> getIndicadorAhorro() {
		return indicadorAhorro;
	}
	public void setIndicadorAhorro(SearchResult<IndicadorAhorro> indicadorAhorro) {	this.indicadorAhorro = indicadorAhorro;}
	public SearchResult<IndicadorLicitadorServicio> getIndicadorLicitadorServicio() {
		return indicadorLicitadorServicio;
	}
	public void setIndicadorLicitadorServicio(SearchResult<IndicadorLicitadorServicio> indicadorLicitadorServicio) {
		this.indicadorLicitadorServicio = indicadorLicitadorServicio;}
	public SearchResult<IndicadoresProcedimientoServicioGestor> getIndicadorProcedimientoServicio() {
		return indicadorProcedimientoServicio;
	}
	public void setIndicadorProcedimientoServicio(SearchResult<IndicadoresProcedimientoServicioGestor> indicadorProcedimientoServicio) {
		this.indicadorProcedimientoServicio = indicadorProcedimientoServicio;
	}
	public SearchResult<IndicadoresProcedimiento> getIndicadorProcedimiento() {
		return indicadorProcedimiento;
	}

	public void setIndicadorProcedimiento(SearchResult<IndicadoresProcedimiento> indicadorProcedimiento) {
		this.indicadorProcedimiento = indicadorProcedimiento;
	}
	public SearchResult<IndicadoresPorAnyoServicioGestor> getIndicadorAyuntamiento() {
		return indicadorServicio;
	}

	public void setIndicadorServicio(SearchResult<IndicadoresPorAnyoServicioGestor> indicadorServicio) {
		this.indicadorServicio = indicadorServicio;
	}
	public SearchResult<Indicador> getIndicadorLicitador() {
		return indicadorLicitador;
	}
	public void setIndicadorLicitador(SearchResult<Indicador> indicadorLicitador) {
		this.indicadorLicitador = indicadorLicitador;
	}
	public String getAnyo() {
		return anyo;
	}
	public void setAnyo(String anyo) {
		this.anyo = anyo;
	}
	//endregion
	//region Override


	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("Indicadores[");
		sb.append("id=").append(id);
		sb.append(", title='").append(title).append('\'');
		sb.append(", Servicio='").append(servicioGestor).append('\'');
		sb.append(", indicadorServicio=").append(indicadorServicio);
		sb.append(", indicadorLicitador=").append(indicadorLicitador);
		sb.append(", indicadorAdjudicatario=").append(indicadorAdjudicatarios);
		sb.append(", anyo='").append(anyo).append('\'');
		sb.append(", indicadorTipo=").append(indicadorTipo);
		sb.append(", indicadorTipoServicio=").append(indicadorTipoServicio);
		sb.append(", indicadorProcedimiento=").append(indicadorProcedimiento);
		sb.append(", indicadorProcedimientoServicio=").append(indicadorProcedimientoServicio);
		sb.append(", indicadorLicitadorServicio=").append(indicadorLicitadorServicio);
		sb.append(", indicadorAhorro=").append(indicadorAhorro);
		sb.append(", indicadorAhorroServicio=").append(indicadorAhorroServicio);
		sb.append(", contratos=").append(contratos);
		sb.append(", cuantia=").append(cuantia);
		sb.append(", numEmpresas=").append(numEmpresas);
		sb.append(", indicadorBurbuja=").append(indicadorBurbuja);
		sb.append(", cuantiaCanon=").append(cuantiaCanon);
		sb.append(", numEmpresaCanon=").append(numEmpresaCanon);
		sb.append(", contratosCanon=").append(contratosCanon);
		sb.append(", contratosExcluidos=").append(contratosExcluidos);
		sb.append(']');
		return sb.toString();
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
	@Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Indicadores)) return false;

        Indicadores that = (Indicadores) o;

        if (!getId().equals(that.getId())) return false;
        if (!getTitle().equals(that.getTitle())) return false;
        return true;

    }
	@Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + getTitle().hashCode();
        return result;
    }
	//endregion
}
