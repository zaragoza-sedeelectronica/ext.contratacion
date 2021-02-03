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

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "datosIndicadores")
@Entity
@Table(name = "VISTA_DATOS_PROCEDIMIENTO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class DatosIndicadoresProcedimiento extends EntidadBase implements java.io.Serializable {
   //region Atributte & Columns
    @Id
    @Column(name="ID_PROCEDIMIENTO",nullable = false,insertable=false,updatable = false)
    private BigDecimal idProcedimiento;
    @Column(name="ID_PORTAL",insertable = false)
    private BigDecimal idPortal;
    @Column(name="TOTALCONTRATOS",insertable = false)
    private BigDecimal total;
    @Column(name="TOTALSINIVA",insertable = false)
    private String totalSinIva ;
    @Column(name="TOTALCONIVA",insertable = false)
    private String totalConIva ;
    @Column(name="ANYO",insertable = false)
    private String anyo ;
    @Transient
    Procedimiento tipoProcedimiento;
    @Transient
    private BigDecimal porCiento;

    //endregion
    //region Setter & getter
      public BigDecimal getPorCiento() {
        return porCiento;
    }
    public void setPorCiento(BigDecimal porCiento) {
        this.porCiento = porCiento;
    }
    public String getTotalSinIva() {
        return totalSinIva;
    }
    public void setTotalSinIva(String totalSinIva) {
        this.totalSinIva = totalSinIva;
    }
    public String getTotalConIva() {
        return totalConIva;
    }
    public void setTotalConIva(String totalConIva) {
        this.totalConIva = totalConIva;
    }
    public Procedimiento getTipoProcedimiento() {
        return tipoProcedimiento;
    }
    public void setTipoProcedimiento(Procedimiento tipoProcedimiento) {
        this.tipoProcedimiento = tipoProcedimiento;
    }
    public BigDecimal getIdProcedimiento() {
        return idProcedimiento;
    }
    public void setIdProcedimiento(BigDecimal idProcedimiento) {
        this.idProcedimiento = idProcedimiento;
    }
    public BigDecimal getTotal() {
        return total;
    }
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }
    public BigDecimal getIdPortal() {
        return idPortal;
    }
    public void setIdPortal(BigDecimal idPortal) {
        this.idPortal = idPortal;
    }
    public BigDecimal getTotalContratos() {return total;}
    public void setTotalContratos(BigDecimal total) {
        this.total = total;
    }
    //endregion
    //region Overrides
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DatosIndicadoresProcedimiento)) return false;

        DatosIndicadoresProcedimiento that = (DatosIndicadoresProcedimiento) o;

        if (!getIdProcedimiento().equals(that.getIdProcedimiento())) return false;
        if (!getIdPortal().equals(that.getIdPortal())) return false;
        if (!getTotal().equals(that.getTotal())) return false;
        if (!getTotalSinIva().equals(that.getTotalSinIva())) return false;
        if (!getTotalConIva().equals(that.getTotalConIva())) return false;
        if (!getAnyo().equals(that.getAnyo())) return false;
        if (!getTipoProcedimiento().equals(that.getTipoProcedimiento())) return false;

        return getPorCiento() != null ? getPorCiento().equals(that.getPorCiento()) : that.getPorCiento() == null;
    }
    @Override
    public int hashCode() {
        int result = getIdProcedimiento().hashCode();
        result = 31 * result + getIdPortal().hashCode();
        result = 31 * result + getTotal().hashCode();
        result = 31 * result + getTotalSinIva().hashCode();
        return result;
    }
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DatosIndicadoresProcedimiento[");
        sb.append("idProcedimiento=").append(idProcedimiento);
        sb.append(", idPortal=").append(idPortal);
        sb.append(", total=").append(total);
        sb.append(", totalSinIva='").append(totalSinIva).append('\'');
        sb.append(", totalConIva='").append(totalConIva).append('\'');
        sb.append(", anyo='").append(anyo).append('\'');
        sb.append(", tipoProcedimiento=").append(tipoProcedimiento);
        sb.append(", porcientoMayor=").append(porCiento);
        sb.append(']');
        return sb.toString();
    }


    //endregion
}

