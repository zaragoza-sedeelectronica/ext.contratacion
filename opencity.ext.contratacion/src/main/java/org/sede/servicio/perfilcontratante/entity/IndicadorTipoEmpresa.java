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

import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Rel;
import org.sede.core.dao.EntidadBase;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;


@XmlRootElement(name = "indicadorTipoEmpresa")
@Entity(name = "indicadorTipoEmpresa")
@Table(name = "VISTA_INDICADOR_TIPOEMPRESA", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING+"/indicadores")
@Rel
public class IndicadorTipoEmpresa extends EntidadBase implements java.io.Serializable {
    // region Atributtes
    @Column(name="ID_PORTAL",nullable =false,insertable = false,updatable = false)
    private String idPortal;
    @Column(name="FORMA",nullable =false,insertable = false,updatable = false)
    private String forma;
    @Id
    @Column(name="ID_FORMA",insertable=false ,updatable=false)
    private BigDecimal idForma;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Column(name="TOTALLICITACIONES")
    private BigDecimal totalLicitaciones;





    //endregion
    //region Setters & Getters

    public String getIdPortal() {
        return idPortal;
    }

    public void setIdPortal(String idPortal) {
        this.idPortal = idPortal;
    }

    public String getForma() {
        return forma;
    }

    public void setForma(String forma) {
        this.forma = forma;
    }

    public BigDecimal getIdForma() {
        return idForma;
    }

    public void setIdForma(BigDecimal idForma) {
        this.idForma = idForma;
    }

    public BigDecimal getTotalSinIva() {
        return totalSinIva;
    }

    public void setTotalSinIva(BigDecimal totalSinIva) {
        this.totalSinIva = totalSinIva;
    }

    public BigDecimal getTotalConIva() {
        return totalConIva;
    }

    public void setTotalConIva(BigDecimal totalConIva) {
        this.totalConIva = totalConIva;
    }

    public String getAnyo() {
        return anyo;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getTotalLicitaciones() {
        return totalLicitaciones;
    }

    public void setTotalLicitaciones(BigDecimal totalLicitaciones) {
        this.totalLicitaciones = totalLicitaciones;
    }


    //endregion
    //region Methods

    // endRegion
    //region Overrides


    @Override
    public String toString() {
        return "IndicadorTipoEmpresa[" +
                "idPortal='" + idPortal + '\'' +
                ", forma='" + forma + '\'' +
                ", idForma=" + idForma +
                ", totalSinIva=" + totalSinIva +
                ", totalConIva=" + totalConIva +
                ", anyo='" + anyo + '\'' +
                ", totalLicitaciones=" + totalLicitaciones +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        IndicadorTipoEmpresa that = (IndicadorTipoEmpresa) o;
        return idPortal.equals(that.idPortal) &&
                forma.equals(that.forma) &&
                idForma.equals(that.idForma) &&
                totalSinIva.equals(that.totalSinIva) &&
                totalConIva.equals(that.totalConIva) &&
                anyo.equals(that.anyo) &&
                totalLicitaciones.equals(that.totalLicitaciones);
    }

    //endregion
}
