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

    //endregion
}
