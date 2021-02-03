/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
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
