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

import java.math.BigDecimal;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Rel;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;


@XmlRootElement(name = "indicador")
@Entity(name = "Indicador")
@Table(name = "VISTA_INDICADOR_LICITADOR", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING+"/indicadores")
@Rel
public class Indicador extends EntidadBase {
   //region Atributtes
    @Id
    @Column(name="ID_EMPRESA", unique = true, nullable = false)
    private BigDecimal idEmpresa;
   @Column(name = "NOMBRE", insertable = false,updatable = false,unique = false, nullable = false)
    private String licitador;
    @Column(name="TOTALSINIVA", insertable = false,updatable = false,unique = false, nullable = false)
    private BigDecimal totalsinIva;
    @Column(name="TOTALCONIVA", insertable = false,updatable = false,unique = false, nullable = false)
    private BigDecimal totalconIva;
    @Column(name="TOTALLICITACIONES", insertable = false,updatable = false,unique = false, nullable = false)
    private BigDecimal totalicitadciones;
    @Column(name="ANYO", insertable = false,updatable = false,unique = false, nullable = false)
    private String anyo;
    @Column(name="ID_PORTAL", insertable = false,updatable = false,unique = false, nullable = false)
    private String idPortal;
    @Transient
    private BigDecimal totalGanados;

   //endregion Atributes
   //region setterand getter

    public BigDecimal getTotalGanados() {
        return totalGanados;
    }

    public void setTotalGanados(BigDecimal totalGanados) {
        this.totalGanados = totalGanados;
    }

    public String getIdPortal() {
        return idPortal;
    }

    public void setIdPortal(String idPortal) {
        this.idPortal = idPortal;
    }

    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(BigDecimal idEmpresa) {
        this.idEmpresa = idEmpresa;
    }
    public BigDecimal getTotalicitadciones() {
        return totalicitadciones;
    }

    public void setTotalicitadciones(BigDecimal totalicitadciones) {
        this.totalicitadciones = totalicitadciones;
    }
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getTotalsinIva() {
        return totalsinIva;
    }

    public void setTotalsinIva(BigDecimal totalsinIva) {
        this.totalsinIva = totalsinIva;
    }

    public BigDecimal getTotalconIva() {
        return totalconIva;
    }

    public void setTotalconIva(BigDecimal totalconIva) {
        this.totalconIva = totalconIva;
    }


    public String getLicitador() {
        return licitador;
    }

    public void setLicitador(String licitador) {
        this.licitador = licitador;
    }
    //endregion
   //region Construct
    public Indicador() {
        super();
    }
    //endregion
   //region Override
    @Override
    public String toString() {
        return "Indicador [ID_EMPRESA="
                + idEmpresa + ", Total Con Iva="
                + totalconIva + ", Total sin IVA="
                + totalsinIva + ", licitador="
                + licitador + ", Total Ganadores="
                + totalicitadciones  + ", Año="
                + anyo + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Indicador indicador = (Indicador) o;
        if(idEmpresa != null ? !idEmpresa.equals(indicador.idEmpresa):indicador.idEmpresa!=null) return false;
        if (totalconIva!= null ? !totalconIva.equals(indicador.totalconIva) : indicador.totalconIva != null) return false;
        if (totalsinIva != null ? !totalconIva.equals(indicador.totalconIva) : indicador.totalconIva != null) return false;
        if (totalicitadciones != null ? !totalicitadciones.equals(indicador.totalicitadciones) : indicador.totalicitadciones != null) return false;
        if (licitador != null ? !licitador.equals(indicador.licitador) : indicador.licitador != null) return false;

        if( anyo != null ? anyo.equals(indicador.anyo) : indicador.anyo == null)return false;

        return false;

    }

    @Override
    public int hashCode() {
        int result = idEmpresa != null ? idEmpresa.hashCode() : 0;
        result = 31 * result + (totalicitadciones != null ? totalicitadciones.hashCode() : 0);
        result = 31 * result + (totalsinIva != null ? totalsinIva.hashCode() : 0);
        result = 31 * result + (totalconIva != null ? totalconIva.hashCode() : 0);
        result = 31 * result + (anyo != null ? anyo.hashCode() : 0);
        result = 31 * result + (licitador != null ? licitador.hashCode() : 0);

        return result;
    }
    //endregion
}
