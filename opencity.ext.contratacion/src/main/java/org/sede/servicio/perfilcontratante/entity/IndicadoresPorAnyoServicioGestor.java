package org.sede.servicio.perfilcontratante.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DynamicUpdate;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

@XmlRootElement(name = "IndicadoresPorAnyoServicioGestor")
@Entity
@Table(name = "VISTA_INDICADORES_SERVICIO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class IndicadoresPorAnyoServicioGestor  implements Serializable {
    //region Attributtes & Columns
    @Id
    @Column(name="SERVICIO_GESTOR",nullable =false,insertable = false,updatable = false)
    private BigDecimal idServicio;
    @Column(name="TOTALCONTRATOS")
    private BigDecimal total;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Column(name="ID_PORTAL",updatable = false,insertable = false)
    private String idPortal;
    @Transient
    private EstructuraOrganizativa entidad;
    @Transient
    private BigDecimal totalLicitadores;

    //endregion
    //region Setters & Getters

    public BigDecimal getTotalLicitadores() {
        return totalLicitadores;
    }

    public void setTotalLicitadores(BigDecimal totalLicitadores) {
        this.totalLicitadores = totalLicitadores;
    }

    public String getIdPortal() {return idPortal;}
    public void setIdPortal(String idPortal) {this.idPortal = idPortal;}
    public EstructuraOrganizativa getEntidad() {
        return entidad;
    }
    public void setEntidad(EstructuraOrganizativa entidad) {
        this.entidad = entidad;
    }
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }
    public BigDecimal getTotalSinIva() {return totalSinIva;}
    public void setTotalSinIva(BigDecimal totalSinIva) {
        if (totalSinIva==null) {
            this.totalSinIva = new BigDecimal(0);
        }else {
            this.totalSinIva = totalSinIva;
        }
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total){
        this.total = total;
    }
    public BigDecimal getTotalConIva() {
        return totalConIva;
    }
    public void setTotalConIva(BigDecimal totalConIva) {
        if (totalConIva==null) {
            this.totalConIva = new BigDecimal(0);
        }else {
            this.totalConIva = totalConIva;
        }
    }
    public BigDecimal getIdServicio() {
        return idServicio;
    }
    public void setIdServicio(BigDecimal idServicio) {
        this.idServicio = idServicio;
    }
    //endregion
    //region Overrides
    @Override
    public int hashCode() {
        int result = total.hashCode();
       // result = 31 * result + totalConIva.hashCode();
       // result = 31 * result + totalSinIva.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndicadoresPorAnyoServicioGestor)) return false;
        IndicadoresPorAnyoServicioGestor that = (IndicadoresPorAnyoServicioGestor) o;

        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndicadoresPorAnyoServicioGestor[");
        sb.append("idServicio=").append(idServicio);
        sb.append(", total=").append(total);
        sb.append(", totalConIva=").append(totalConIva);
        sb.append(", totalSinIva=").append(totalSinIva);
        sb.append(", anyo='").append(anyo).append('\'');
        sb.append(", idPortal='").append(idPortal).append('\'');
        sb.append(", entidad=").append(entidad);
        sb.append(']');
        return sb.toString();
    }
    //endregion
}
