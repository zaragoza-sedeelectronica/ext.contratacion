package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.Interno;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "ContratosPorAnyoServicioGestor")
@Entity
@Table(name = "VISTA_CONTRATOS_SERVICIO_ANYO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class ContratosPorAnyoServicioGestor implements Serializable {

    @EmbeddedId
    @Interno
    @AttributeOverrides({
            @AttributeOverride(name = "servicioGestor", column = @Column(name = "SERVICIO_GESTOR", nullable = false,precision = 6, scale = 0)),
            @AttributeOverride(name = "anyo", column = @Column(name = "ANYO", nullable = true, scale = 0))})
    private ContratosPorAnyoServicioGestorID id;
    @Column(name="TOTAL")
    private BigDecimal total;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    
    @Transient
    private String year;
    
    public String getYear() {
		return id.getAnyo();
	}

	public ContratosPorAnyoServicioGestorID getId() {
        return id;
    }

    public void setId(ContratosPorAnyoServicioGestorID id) {
        this.id = id;
    }
    public BigDecimal getTotalSinIva() {

        return totalSinIva;
    }
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
        if (!(o instanceof ContratosPorAnyoServicioGestor)) return false;
        ContratosPorAnyoServicioGestor that = (ContratosPorAnyoServicioGestor) o;

        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);

    }


}
