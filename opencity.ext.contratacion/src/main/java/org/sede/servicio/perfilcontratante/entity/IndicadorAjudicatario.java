package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.common.util.StringHelper;
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


@XmlRootElement(name = "indicadorAdjudicatario")
@Entity(name = "indicadorAdjudicatario")
@Table(name = "VISTA_INDICADOR_ADJUDICATARIO2", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING+"/indicadores")
@Rel
public class IndicadorAjudicatario extends EntidadBase implements java.io.Serializable {
    // region Atributtes

    @EmbeddedId
    @Interno
    @AttributeOverrides({
            @AttributeOverride(name = "empresa", column = @Column(name = "ID_EMPRESA", nullable = false,precision = 6, scale = 0)),
            @AttributeOverride(name = "oferta", column = @Column(name = "OFERTA", nullable = true, scale = 0))})
    private IndicadorLicitadorID id;
    @Column(name="PORTAL",nullable =false,insertable = false,updatable = false)
    private String nombrePortal;
    @Column(name="ID_PORTAL",nullable =false,insertable = false,updatable = false)
    private String idPortal;
    @Column(name="NOMEMPRESA",nullable =false,insertable = false,updatable = false)
    private String nombreEmpresa;
    @Column(name="ID_EMPRESA",insertable=false ,updatable=false)
    private BigDecimal idEmpresa;
    @Column(name="ID_CONTRATO",insertable=false ,updatable=false)
    private BigDecimal contrato;
    @Column(name="TITULO")
    private String title;
    @Column(name="DESCRIPCION")
    private String lote;
    @Column(name="TIPO")
    private String type;
    @Column(name="PROCEDIMIENTO")
    private String procedimiento;
    @Column(name="IMPORTELICISINIVA",updatable = false,insertable = false)
    private BigDecimal importeLicitacionSinIva;
    @Column(name="IMPORTELICICONIVA",updatable = false,insertable = false)
    private BigDecimal importeLicitacionConIva;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Column(name="IMPORTEADJUSINIVA")
    private BigDecimal importeAdjuducacioSinIva;
    @Column(name="IMPORTEADJUCONIVA")
    private BigDecimal importeAdjuducacioConIva;
    @DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
    @Column(name = "FECHA_ADJ")
    private Date fechaAdjudicacion;
    @DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
    @Column(name = "FECHA_FOR")
    private Date fechaFormalicacion;




    //endregion
    //region Setters & Getters

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(String procedimiento) {
        this.procedimiento = procedimiento;
    }

    public IndicadorLicitadorID getId() {
        return id;
    }

    public void setId(IndicadorLicitadorID id) {
        this.id = id;
    }

    public String getNombrePortal() {
        return nombrePortal;
    }

    public void setNombrePortal(String nombrePortal) {
        this.nombrePortal = nombrePortal;
    }

    public String getIdPortal() {
        return idPortal;
    }

    public void setIdPortal(String idPortal) {
        this.idPortal = idPortal;
    }

    public String getNombreEmpresa() {
        return nombreEmpresa;
    }

    public void setNombreEmpresa(String nombreEmpresa) {
        this.nombreEmpresa = nombreEmpresa;
    }

    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(BigDecimal idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public BigDecimal getContrato() {
        return contrato;
    }

    public void setContrato(BigDecimal contrato) {
        this.contrato = contrato;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLote() {
        return lote;
    }

    public void setLote(String lote) {
        this.lote = lote;
    }





    public BigDecimal getImporteLicitacionSinIva() {
        return importeLicitacionSinIva;
    }

    public void setImporteLicitacionSinIva(BigDecimal importeLicitacionSinIva) {
        this.importeLicitacionSinIva = importeLicitacionSinIva;
    }

    public BigDecimal getImporteLicitacionConIva() {
        return importeLicitacionConIva;
    }

    public void setImporteLicitacionConIva(BigDecimal importeLicitacionConIva) {
        this.importeLicitacionConIva = importeLicitacionConIva;
    }

    public String getAnyo() {
        return anyo;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getImporteAdjuducacioSinIva() {
        return importeAdjuducacioSinIva;
    }

    public void setImporteAdjuducacioSinIva(BigDecimal importeAdjuducacioSinIva) {
        this.importeAdjuducacioSinIva = importeAdjuducacioSinIva;
    }

    public BigDecimal getImporteAdjuducacioConIva() {
        return importeAdjuducacioConIva;
    }

    public void setImporteAdjuducacioConIva(BigDecimal importeAdjuducacioConIva) {
        this.importeAdjuducacioConIva = importeAdjuducacioConIva;
    }

    public Date getFechaAdjudicacion() {
        return fechaAdjudicacion;
    }

    public void setFechaAdjudicacion(Date fechaAdjudicacion) {
        this.fechaAdjudicacion = fechaAdjudicacion;
    }

    public Date getFechaFormalicacion() {
        return fechaFormalicacion;
    }

    public void setFechaFormalicacion(Date fechaFormalicacion) {
        this.fechaFormalicacion = fechaFormalicacion;
    }


    //endregion
    //region Methods

    // endRegion
    //region Overrides

    @Override
    public String toString() {
        return "IndicadorAjudicatario[" +
                "id=" + id +
                ", nombrePortal='" + nombrePortal + '\'' +
                ", idPortal='" + idPortal + '\'' +
                ", nombreEmpresa='" + nombreEmpresa + '\'' +
                ", idEmpresa=" + idEmpresa +
                ", contrato=" + contrato +
                ", title='" + title + '\'' +
                ", lote='" + lote + '\'' +
                ", type=" + type +
                ", procedimiento=" + procedimiento +
                ", importeLicitacionSinIva=" + importeLicitacionSinIva +
                ", importeLicitacionConIva=" + importeLicitacionConIva +
                ", anyo='" + anyo + '\'' +
                ", importeAdjuducacioSinIva=" + importeAdjuducacioSinIva +
                ", importeAdjuducacioConIva=" + importeAdjuducacioConIva +
                ", fechaAdjudicacion=" + fechaAdjudicacion +
                ", fechaFormalicacion=" + fechaFormalicacion +
                ']';
    }



    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (nombrePortal != null ? nombrePortal.hashCode() : 0);
        result = 31 * result + (idPortal != null ? idPortal.hashCode() : 0);
        result = 31 * result + (nombreEmpresa != null ? nombreEmpresa.hashCode() : 0);
        result = 31 * result + (idEmpresa != null ? idEmpresa.hashCode() : 0);
        result = 31 * result + (contrato != null ? contrato.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (lote != null ? lote.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (procedimiento != null ? procedimiento.hashCode() : 0);
        result = 31 * result + (importeLicitacionSinIva != null ? importeLicitacionSinIva.hashCode() : 0);
        result = 31 * result + (importeLicitacionConIva != null ? importeLicitacionConIva.hashCode() : 0);
        result = 31 * result + (anyo != null ? anyo.hashCode() : 0);
        result = 31 * result + (importeAdjuducacioSinIva != null ? importeAdjuducacioSinIva.hashCode() : 0);
        result = 31 * result + (importeAdjuducacioConIva != null ? importeAdjuducacioConIva.hashCode() : 0);
        result = 31 * result + (fechaAdjudicacion != null ? fechaAdjudicacion.hashCode() : 0);
        result = 31 * result + (fechaFormalicacion != null ? fechaFormalicacion.hashCode() : 0);
        return result;
    }


    //endregion
}
