package org.sede.servicio.perfilcontratante.entity;

import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Rel;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;


@XmlRootElement(name = "indicadorLicitadorServicio")
@Entity(name = "IndicadorLicitadorServicio")
@Table(name = "VISTA_LICITADORES_SERVICIO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING+"/indicadores")
@Rel
public class IndicadorLicitadorServicio extends EntidadBase {
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
    @Column(name="TOTALGANADOS", insertable = false,updatable = false,unique = false, nullable = false)
    private BigDecimal totalGanados;
    @Column(name="ANYO", insertable = false,updatable = false,unique = false, nullable = false)
    private String anyo;
    @Column(name="ORGANO", insertable = false,updatable = false,unique = false, nullable = false)
    private BigDecimal idServicio;
    @Transient
    private BigDecimal totalicitadciones;
   //endregion Atributes
   //region setterand getter

    public BigDecimal getTotalGanados() {
        return totalGanados;
    }

    public void setTotalGanados(BigDecimal totalGanados) {
        this.totalGanados = totalGanados;
    }

    public BigDecimal getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(BigDecimal idServicio) {
        this.idServicio = idServicio;
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
    public IndicadorLicitadorServicio() {
        super();
    }
    //endregion
   //region Override

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndicadorLicitadorServicio[");
        sb.append("idEmpresa=").append(idEmpresa);
        sb.append(", licitador='").append(licitador).append('\'');
        sb.append(", totalsinIva=").append(totalsinIva);
        sb.append(", totalconIva=").append(totalconIva);
        sb.append(", totalicitadciones=").append(totalicitadciones);
        sb.append(", anyo='").append(anyo).append('\'');
        sb.append(", idServicio=").append(idServicio);
        sb.append(", totalGanados=").append(totalGanados);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IndicadorLicitadorServicio indicador = (IndicadorLicitadorServicio) o;
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
