package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.Interno;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;


@XmlRootElement(name = "ContratosPorAnyoIdPortal")
@Entity
@Table(name = "VISTA_CONTRATOS_ENTIDAD_ANYO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class ContratosPorAnyoIdPortal implements java.io.Serializable{


    @EmbeddedId
    @Interno
    @AttributeOverrides({
            @AttributeOverride(name = "idPortal", column = @Column(name = "ID_PORTAL", nullable = false,precision = 6, scale = 0)),
            @AttributeOverride(name = "anyo", column = @Column(name = "ANYO", nullable = true,precision = 6, scale = 0))})
    private ContratosPorAnyoIdPortalID id;
    @Column(name="TOTAL")
    private BigDecimal total;
    @Column(name="TOTALCONIVA")
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA")
    private BigDecimal totalSinIva;
    @Transient
    private String year;
    public void setYear(String year){this.year=year;}
    public String getYear() {
        return id.getAnyo();
    }
    public BigDecimal getTotalSinIva() {

        return totalSinIva;
    }
    public void setTotalSinIva(BigDecimal totalSinIva) {

        this.totalSinIva = totalSinIva;
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

        this.totalConIva = totalConIva;
    }
    public ContratosPorAnyoIdPortalID getId() {
        return id;
    }
    public void setId(ContratosPorAnyoIdPortalID id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        int result = total.hashCode();
        //result = 31 * result + totalConIva.hashCode();
        //result = 31 * result + totalSinIva.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContratosPorAnyoIdPortal)) return false;
        ContratosPorAnyoIdPortal that = (ContratosPorAnyoIdPortal) o;
        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);

    }

}
