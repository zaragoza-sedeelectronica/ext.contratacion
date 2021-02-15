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
	private SearchResult<IndicadorTipoEmpresa> indicadorTipoEmpresa;
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


	public SearchResult<IndicadorTipoEmpresa> getIndicadorTipoEmpresa() {
		return indicadorTipoEmpresa;
	}

	public void setIndicadorTipoEmpresa(SearchResult<IndicadorTipoEmpresa> indicadorTipoEmpresa) {
		this.indicadorTipoEmpresa = indicadorTipoEmpresa;
	}

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
		return "Indicadores[" +
				"id=" + id +
				", title='" + title + '\'' +
				", servicioGestor=" + servicioGestor +
				", indicadorServicio=" + indicadorServicio +
				", indicadorLicitador=" + indicadorLicitador +
				", anyo='" + anyo + '\'' +
				", indicadorTipo=" + indicadorTipo +
				", indicadorTipoServicio=" + indicadorTipoServicio +
				", indicadorProcedimiento=" + indicadorProcedimiento +
				", indicadorProcedimientoServicio=" + indicadorProcedimientoServicio +
				", indicadorLicitadorServicio=" + indicadorLicitadorServicio +
				", indicadorTipoEmpresa=" + indicadorTipoEmpresa +
				", indicadorAhorro=" + indicadorAhorro +
				", indicadorAhorroServicio=" + indicadorAhorroServicio +
				", contratos=" + contratos +
				", indicadorAdjudicatarios=" + indicadorAdjudicatarios +
				", cuantia=" + cuantia +
				", numEmpresas=" + numEmpresas +
				", indicadorBurbuja=" + indicadorBurbuja +
				", cuantiaCanon=" + cuantiaCanon +
				", numEmpresaCanon=" + numEmpresaCanon +
				", contratosCanon=" + contratosCanon +
				", contratosExcluidos=" + contratosExcluidos +
				']';
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
