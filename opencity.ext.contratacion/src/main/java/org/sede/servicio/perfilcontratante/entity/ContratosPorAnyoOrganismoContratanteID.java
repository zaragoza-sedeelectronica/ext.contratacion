package org.sede.servicio.perfilcontratante.entity;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.validation.constraints.Digits;
import java.io.Serializable;
import java.math.BigDecimal;

@Embeddable
public class ContratosPorAnyoOrganismoContratanteID implements Serializable{
    //region Atributtes & Columns
    @Column(name = "ORGANO_CONTRATACION", nullable = false)
    @Digits(integer = 6, fraction = 0)
    private BigDecimal organo;
    @Column(name = "anyo", nullable = false)
    private String anyo;
    //endregion
    //rregion Getters & Stters
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }
    public BigDecimal getOrgano() {
        return organo;
    }
    public void setOrgano(BigDecimal organo) {
        this.organo = organo;
    }
    //endregion
    //region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContratosPorAnyoOrganismoContratanteID)) return false;
        ContratosPorAnyoOrganismoContratanteID that = (ContratosPorAnyoOrganismoContratanteID) o;
        return getOrgano().equals(that.getOrgano()) && getAnyo().equals(that.getAnyo());
    }
    @Override
    public int hashCode() {
        int result = getOrgano().hashCode();
        result = 31 * result + getAnyo().hashCode();
        return result;
    }
    //endregion
}
