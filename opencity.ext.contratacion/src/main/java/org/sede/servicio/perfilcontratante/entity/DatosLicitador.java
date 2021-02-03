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
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;


@XmlRootElement(name = "datosLicitador")
@Entity
@Table(name = "VISTAS_PERFIL_LICITADORES", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate

public class DatosLicitador extends EntidadBase implements Serializable{
    //region Atributtes & Columns
    @Id
    @Column(name="ANYO",insertable = false,nullable = false,updatable = false)
    private String anyo;
    @Column(name="ID_EMPRESA",insertable = false,nullable = false,updatable = false)
    private BigDecimal idEmpresa;
    @Column(name="NOMBRE",insertable = false,nullable = false,updatable = false)
    private String nombre;
    @Column(name="TOTALSINIVA",insertable = false,nullable = false,updatable = false)
    private BigDecimal totalSinIva;
    @Column(name="TOTALCONIVA",insertable = false,nullable = false,updatable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALLICITACIONES")
    private BigDecimal numContratos;
    //endregion
    //region Setters & Getters

    public BigDecimal getNumContratos() {
        return numContratos;
    }

    public void setNumContratos(BigDecimal numContratos) {
        this.numContratos = numContratos;
    }

    public String getAnyo() {
        return anyo;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(BigDecimal idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    //endregion
    //region Overrides


    @Override
    public String toString() {
        return "DatosLicitador[" +
                "anyo='" + anyo + '\'' +
                ", idEmpresa=" + idEmpresa +
                ", nombre='" + nombre + '\'' +
                ", totalSinIva=" + totalSinIva +
                ", totalConIva=" + totalConIva +
                ", numContratos=" + numContratos +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatosLicitador)) return false;

        DatosLicitador that = (DatosLicitador) o;

        if (getAnyo() != null ? !getAnyo().equals(that.getAnyo()) : that.getAnyo() != null) return false;
        if (getIdEmpresa() != null ? !getIdEmpresa().equals(that.getIdEmpresa()) : that.getIdEmpresa() != null)
            return false;
        if (getNombre() != null ? !getNombre().equals(that.getNombre()) : that.getNombre() != null) return false;
        if (getTotalSinIva() != null ? !getTotalSinIva().equals(that.getTotalSinIva()) : that.getTotalSinIva() != null)
            return false;
        return getTotalConIva() != null ? getTotalConIva().equals(that.getTotalConIva()) : that.getTotalConIva() == null;

    }

    @Override
    public int hashCode() {
        int result = getAnyo() != null ? getAnyo().hashCode() : 0;
        result = 31 * result + (getIdEmpresa() != null ? getIdEmpresa().hashCode() : 0);
        result = 31 * result + (getNombre() != null ? getNombre().hashCode() : 0);
        result = 31 * result + (getTotalSinIva() != null ? getTotalSinIva().hashCode() : 0);
        result = 31 * result + (getTotalConIva() != null ? getTotalConIva().hashCode() : 0);
        return result;
    }

    //endregion
}
