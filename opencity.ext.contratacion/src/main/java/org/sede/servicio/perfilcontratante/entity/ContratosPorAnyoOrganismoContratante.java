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

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.Interno;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "ContratosPorAnyoOrnismoContratante")
@Entity
@Table(name = "VISTA_CONTRATOS_ORGANOC", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class ContratosPorAnyoOrganismoContratante implements Serializable {
    //region Atributtes & Columns
    @EmbeddedId
    @Interno
    @AttributeOverrides({
            @AttributeOverride(name = "organo", column = @Column(name = "ORGANO_CONTRATACION", nullable = false,precision = 6, scale = 0)),
            @AttributeOverride(name = "anyo", column = @Column(name = "ANYO", nullable = true, scale = 0))})
    private ContratosPorAnyoOrganismoContratanteID id;
    @Column(name="TOTAL")
    private BigDecimal total;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    @Transient
    private String year;
    //endregion
    //region Getters &Setters
    public ContratosPorAnyoOrganismoContratanteID getId() {return id;}
    public void setId(ContratosPorAnyoOrganismoContratanteID id) {this.id = id; }
    public BigDecimal getTotal() {return total;}
    public void setTotal(BigDecimal total) {this.total = total;}
    public BigDecimal getTotalConIva() {return totalConIva;}
    public void setTotalConIva(BigDecimal totalConIva) {this.totalConIva = totalConIva;}
    public BigDecimal getTotalSinIva() {return totalSinIva;}
    public void setTotalSinIva(BigDecimal totalSinIva) {this.totalSinIva = totalSinIva;}
    public String getYear() {return year;}
    public void setYear(String year) {this.year = year;}
    //endregion
    //region Overrides
    @Override
    public int hashCode() {
        int result = total.hashCode();
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ContratosPorAnyoOrganismoContratante)) return false;
        ContratosPorAnyoOrganismoContratante that = (ContratosPorAnyoOrganismoContratante) o;

        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);

    }
    //endregion


}
