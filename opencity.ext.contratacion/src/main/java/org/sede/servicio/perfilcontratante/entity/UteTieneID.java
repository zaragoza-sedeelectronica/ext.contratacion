package org.sede.servicio.perfilcontratante.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import java.math.BigDecimal;

@Embeddable
public class UteTieneID implements java.io.Serializable  {

    //region Atributtes & Columns
    @Column(name = "ID_UTE", nullable = false)
    @Digits(integer = 6, fraction = 0)
    private BigDecimal idUte;

    @Column(name = "ID_EMPRESA", nullable = false)
    @Digits(integer = 6, fraction = 0)
    private BigDecimal idEmpresa;
    //endregion
    //region Setters & Getters
    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(BigDecimal idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public BigDecimal getIdUte() {
        return idUte;
    }

    public void setIdUte(BigDecimal idUte) {
        this.idUte = idUte;
    }
    //endregion
    //region Overrides
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + idUte.hashCode();
        result = prime * result + idEmpresa.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
        	return true;
        }
        if (obj == null) {
        	return false;
        }
        if (getClass() != obj.getClass()) {
        	return false;
        }
        UteTieneID other = (UteTieneID) obj;
        if (idUte != other.idUte) {
        	return false;
        }
        if (idEmpresa != other.idEmpresa) {
        	return false;
        }
        return true;
    }
    /*@Override
    public String toString() {
        return "UteTieneID [idUte=" + idUte + ", idEmpresa=" + idEmpresa + "]";
    }*/
    //endregion
}
