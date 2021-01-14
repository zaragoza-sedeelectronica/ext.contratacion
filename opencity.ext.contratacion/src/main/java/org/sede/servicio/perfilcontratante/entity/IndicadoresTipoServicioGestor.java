package org.sede.servicio.perfilcontratante.entity;

import com.googlecode.genericdao.search.SearchResult;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.sede.core.anotaciones.SoloEnEstaEntidad;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "IndicadoresPorTipoServicioGestor")
@Entity
@Table(name = "VISTA_INDICADOR_TIPO_SERGES", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class IndicadoresTipoServicioGestor extends EntidadBase implements Serializable {

    //region Atributte Columns
    @Column(name = "SERVICIO_GESTOR",insertable = false,updatable = false,nullable = false)
    @NotFound(action= NotFoundAction.IGNORE)
    @SoloEnEstaEntidad
    private BigDecimal idServicio;
    @Column(name="TOTALCONTRATOS")
    private BigDecimal total;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Id
    @Column(name="ID_TIPOCONTRATO",updatable = false,insertable = false)
    private BigDecimal tipo;
    @Transient
    private EntidadContratante entidad;
    @Transient
    private List<Contrato> contratos;
    @Transient
    private BigDecimal totalContratos;
    @Transient
    private BigDecimal porCiento;
    @Transient
    private SearchResult<DatosIndicadoresProcedimiento> resultado;
    @Transient Tipocontrato tipoContrato;
    //endregion
    //region Getter and Setter

    public BigDecimal getIdServicio() {
        return idServicio;
    }

    public void setIdServicio(BigDecimal idServicio) {
        this.idServicio = idServicio;
    }

    public BigDecimal getPorCiento() {
        return porCiento;
    }

    public void setPorCiento(BigDecimal porCiento) {
        this.porCiento = porCiento;
    }

    public Tipocontrato getTipoContrato() {
        return tipoContrato;
    }

    public void setTipoContrato(Tipocontrato tipoContrato) {
        this.tipoContrato = tipoContrato;
    }

    public BigDecimal getTotalContratos() {
        return totalContratos;
    }

    public void setTotalContratos(BigDecimal totalContratos) {
        this.totalContratos = totalContratos;
    }
    public SearchResult<DatosIndicadoresProcedimiento>getResultado() {
        return resultado;
    }

    public void setResultado(SearchResult<DatosIndicadoresProcedimiento>resultado) {
        this.resultado = resultado;
    }
    public List<Contrato> getContratos() {return contratos;}
    public void setContratos(List<Contrato> contratos) {this.contratos = contratos;}

    public BigDecimal getTipo() {return tipo;}

    public void setTipo(BigDecimal tipo) {this.tipo = tipo;}
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }
    public BigDecimal getTotalSinIva() { return totalSinIva;}
    public EntidadContratante getEntidad() {return entidad;}
    public void setEntidad(EntidadContratante entidad) {
        this.entidad = entidad;
    }
    public void setTotalSinIva(BigDecimal totalSinIva) {
        if (totalSinIva==null) {
            this.totalSinIva = new BigDecimal(0);
        }else {
            this.totalSinIva = totalSinIva;
        }
    }
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total){

        this.total = total;
    }
    public BigDecimal getTotalConIva() {return totalConIva;}
    public void setTotalConIva(BigDecimal totalConIva) {
        if (totalConIva==null) {
            this.totalConIva = new BigDecimal(0);
        }else {
            this.totalConIva = totalConIva;
        }
    }

    //endregion
    @Override
    public int hashCode() {
        int result = total.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IndicadoresTipoServicioGestor[" +
                "idServicio=" + idServicio +
                ", total=" + total +
                ", totalConIva=" + totalConIva +
                ", totalSinIva=" + totalSinIva +
                ", anyo='" + anyo + '\'' +
                ", tipo=" + tipo +
                ", entidad=" + entidad +
                ", contratos=" + contratos +
                ", totalContratos=" + totalContratos +
                ", porCiento=" + porCiento +
                ", resultado=" + resultado +
                ", tipoContrato=" + tipoContrato +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndicadoresTipoServicioGestor)) return false;
        IndicadoresTipoServicioGestor that = (IndicadoresTipoServicioGestor) o;

        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);

    }


}
