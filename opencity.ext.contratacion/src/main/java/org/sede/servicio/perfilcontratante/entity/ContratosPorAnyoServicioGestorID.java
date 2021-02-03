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
