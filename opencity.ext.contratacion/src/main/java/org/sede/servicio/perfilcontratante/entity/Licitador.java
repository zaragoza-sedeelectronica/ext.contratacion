package org.sede.servicio.perfilcontratante.entity;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.HashMap;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

import com.googlecode.genericdao.search.SearchResult;

@XmlRootElement(name = "licitador")
@Embeddable
public class Licitador extends Empresa implements Serializable {

    //region Atributtes
    @Transient
    private BigDecimal idEmpresa;
    @Transient
    private String nombre;
    @Transient
    private String libreBorme;
    @Transient
    private SearchResult<Empresa> utes;
    @Transient
    private SearchResult<Contrato> contrato;
    @Transient
    private HashMap<String,Integer> licitadosPorAnyo;
    @Transient
    private HashMap<String,Integer> ganadosPorAnyo;
    @Transient
    private SearchResult<?> licitados;
    @Transient
    private SearchResult<?> ganados;
    @Transient
    private SearchResult<DatosLicitador> datosLicitadorCuantia;
    @Transient
    private BigDecimal totalSinIva;
    @Transient
    private BigDecimal totalConIva;
    //endregion
    //region Setters & Getters
    public BigDecimal getTotalSinIva() {
        return totalSinIva;
    }
    public void setTotalSinIva(BigDecimal totalSinIva) {
        this.totalSinIva = totalSinIva;
    }
    public BigDecimal getTotalConIva() {
        return totalConIva;
    }
    public void setTotalConIva(BigDecimal totalConIva) {
        this.totalConIva = totalConIva;
    }
    public SearchResult<Empresa> getUtes() {
        return utes;
    }

    public void setUtes(SearchResult<Empresa> utes) {
        this.utes = utes;
    }
    public SearchResult<Contrato> getContrato() {
        return contrato;
    }
    public void setContrato(SearchResult<Contrato> contrato) {
        this.contrato = contrato;
    }
    public SearchResult<?> getLicitados() {
        return licitados;
    }
    public void setLicitados(SearchResult<?> licitados) {
        this.licitados = licitados;
    }
    public SearchResult<?> getGanados() {
        return ganados;
    }
    public void setGanados(SearchResult<?> ganados) {
        this.ganados = ganados;
    }
    public HashMap<String, Integer> getLicitadosPorAnyo() {
        return licitadosPorAnyo;
    }
    public void setLicitadosPorAnyo(HashMap<String, Integer> licitadosPorAnyo) {
        this.licitadosPorAnyo = licitadosPorAnyo;
    }

    public HashMap<String, Integer> getGanadosPorAnyo() {
        return ganadosPorAnyo;
    }
    public void setGanadosPorAnyo(HashMap<String, Integer> ganadosPorAnyo) {
        this.ganadosPorAnyo = ganadosPorAnyo;
    }
    public SearchResult<DatosLicitador> getDatosLicitadorCuantia() {
        return datosLicitadorCuantia;
    }
    public void setDatosLicitadorCuantia(SearchResult<DatosLicitador> datosLicitadorCuantia) {
        this.datosLicitadorCuantia = datosLicitadorCuantia;
    }
    //endregion
    //region Overrides
    @Override
    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }
    @Override
    public void setIdEmpresa(BigDecimal id) {
        this.idEmpresa = id;
    }
    @Override
    public String getNombre() {
        return nombre;
    }
    @Override
    public void setNombre(String title) {
        this.nombre = title;
    }
    @Override
    public String getLibreBorme() {
        return libreBorme;
    }
    @Override
    public void setLibreBorme(String libreBorme) {
        this.libreBorme = libreBorme;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ServicioGestor)) return false;
        ServicioGestor that = (ServicioGestor) o;
        if (!getIdEmpresa().equals(that.getId())) return false;
        if (!getNombre().equals(that.getTitle())) return false;
        return false;
    }
    @Override
    public int hashCode() {
        int result = getIdEmpresa().hashCode();
        result = 31 * result + getNombre().hashCode();

        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Licitador [");
        sb.append(", totalSinIva=").append(totalSinIva);
        sb.append(", totalConIva=").append(totalConIva);
        sb.append(']');
        return sb.toString();
    }
    //endregion

}
