package org.sede.servicio.perfilcontratante.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class ContratosPorAnyoServicioGestorID implements Serializable{
    @Column(name = "SERVICIO_GESTOR", nullable = false)
    @Digits(integer = 6, fraction = 0)
    private BigDecimal servicio;
    @Column(name = "anyo", nullable = false)
    private String anyo;
    public String getAnyo() {
        return anyo;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getServicio() {
        return servicio;
    }

    public void setServicio(BigDecimal idPortal) {
        this.servicio = idPortal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContratosPorAnyoServicioGestorID)) return false;

        ContratosPorAnyoServicioGestorID that = (ContratosPorAnyoServicioGestorID) o;

        if (!getServicio().equals(that.getServicio())) return false;
        return getAnyo().equals(that.getAnyo());

    }

    @Override
    public int hashCode() {
        int result = getServicio().hashCode();
        result = 31 * result + getAnyo().hashCode();
        return result;
    }
}
