package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.BooleanConverter;
import org.sede.core.dao.EntidadBase;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

@XmlRootElement(name = "oferta")
@Entity(name = "Oferta")
@Table(name = "PERFIL_OFERTA", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
@Rel
@SequenceGenerator(name = "SECUENCIA_SEQ_PERFIL_OFERTA", sequenceName = "SEQ_PERFIL_OFERTA", allocationSize = 1)
public class Oferta extends EntidadBase implements java.io.Serializable{
    //region Atributtes
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SEQ_PERFIL_OFERTA")
    @Id
    @Column(name = "ID_OFER", unique = true, nullable = false)
    private BigDecimal id;

    @Column(name = "NIF") @Size(max = 600)
    private String nif;

    @Transient
    private String nifEntidad;

    @Column(name = "OFER") @Size(max = 600)
    private String adjudicatario;

    @DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
    @Column(name = "FECHA_ADJ") @Temporal(TemporalType.DATE)
    private Date fechaAdjudicacion;
    @DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
    @Column(name = "FECHA_FORM") @Temporal(TemporalType.DATE)
    private Date fechaFormalizacion;

    @Column(name = "IMPORTE_SINIVA")
    private BigDecimal importeSinIVA;

    @Column(name = "EJECUCION") @Size(max = 600)
    private String ejecucion;

    @Column(name = "CANON")
    @Convert(converter=BooleanConverter.class)
    private Boolean canon;

    @Column(name = "AUTONOMO")
    @Convert(converter = BooleanConverter.class)
    private Boolean autonomo;

    @Column(name = "GANADOR")
    @Convert(converter = BooleanConverter.class)
    private Boolean ganador;

    @Column(name = "UTE")
    @Convert(converter = BooleanConverter.class)
    private Boolean  tieneUte;


    @Column(name = "IMPORTE_CONIVA")
    private BigDecimal importeConIVA;

    //@SoloEnEstaEntidad
    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name = "ID_EMPRESA",nullable = false)

    private Empresa empresa;
    
    @OneToMany(fetch = FetchType.LAZY,mappedBy = "ofertaUte")
    @NotFound(action= NotFoundAction.IGNORE)
    @BatchSize(size = 50)
    @SoloEnEstaEntidad
    @Access(AccessType.FIELD)
    private Set<Ute> ute ;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinTable(name = "PERFIL_LOTE_TIENE_OFERTA",
            joinColumns = { @JoinColumn(name = "ID_OFERTA", nullable = false, updatable = false) },
            inverseJoinColumns = { @JoinColumn(name = "ID_LOTE", nullable = false)})
    @Access(AccessType.FIELD)
    @BatchSize(size = 50)
    private Lote lote;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTRATO")
    @Interno
    private Contrato contrato;

    @Permisos(Permisos.DET)
    @Column(name="AHORRO_VISIBLE")
    @Convert(converter = BooleanConverter.class)
    private Boolean ahorroVisible;
    @Column(name="IVA")
    private BigDecimal iva;
    @Transient
    private String tituloContato;
    @Transient
    private BigDecimal idContrato;

    //trasient id_contrato y title
    //endregion
    //region Setters & Getters


    public String getTituloContato() {
        return tituloContato;
    }

    public void setTituloContato(String tituloContato) {
        this.tituloContato = tituloContato;
    }

    public BigDecimal getIdContrato() {
        return idContrato;
    }

    public void setIdContrato(BigDecimal idContrato) {
        this.idContrato = idContrato;
    }

    public BigDecimal getIva() {
        return iva;
    }

    public void setIva(BigDecimal iva) {
        this.iva = iva;
    }

    public Boolean getTieneUte() {
        return tieneUte;
    }
    public void setTieneUte(Boolean tieneUte) {
        this.tieneUte = tieneUte;
    }
    public Boolean getAhorroVisible() {
        return ahorroVisible;
    }
    public void setAhorroVisible(Boolean ahorroVisible) {
        this.ahorroVisible = ahorroVisible;
    }
    public String getNifEntidad() {
        return nifEntidad;
    }
    public void setNifEntidad(String nifEntidad) {
        this.nifEntidad = nifEntidad;
    }
    public BigDecimal getId() {
        return id;
    }
    public void setId(BigDecimal id) {
        this.id = id;
    }
    public Contrato getContrato() {
        return contrato;
    }
    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }
    public Lote getLote() {
        return lote;
    }
    public void setLote(Lote lote) {
        this.lote = lote;
    }
    
    public String getNif() {
        return nif;
    }
    public void setNif(String nif) {
        this.nif = nif;
    }
    public String getAdjudicatario() {
        return adjudicatario;
    }
    public void setAdjudicatario(String adjudicatario) {
        this.adjudicatario = adjudicatario;
    }
    public Date getFechaAdjudicacion() {
        return fechaAdjudicacion;
    }
    public void setFechaAdjudicacion(Date fechaAdjudicacion) {
        this.fechaAdjudicacion = fechaAdjudicacion;
    }
    public Date getFechaFormalizacion() {
        return fechaFormalizacion;
    }
    public void setFechaFormalizacion(Date fechaFormalizacion) {
        this.fechaFormalizacion = fechaFormalizacion;
    }
    public BigDecimal getImporteSinIVA() {
        return importeSinIVA;
    }
    public void setImporteSinIVA(BigDecimal importeSinIVA) {
        this.importeSinIVA = importeSinIVA;
    }
    public String getEjecucion() {
        return ejecucion;
    }
    public void setEjecucion(String ejecucion) {
        this.ejecucion = ejecucion;
    }
    public Boolean getCanon() {return canon;}
    public void setCanon(Boolean canon) {
        this.canon = canon;
    }
    public Boolean getAutonomo() {
        return autonomo;
    }
    public void setAutonomo(Boolean autonomo) {
        this.autonomo = autonomo;
    }
    public Boolean getGanador() {
        return ganador;
    }
    public void setGanador(Boolean ganador) {
        this.ganador = ganador;
    }
    public BigDecimal getImporteConIVA() {
        return importeConIVA;
    }
    public void setImporteConIVA(BigDecimal importeConIVA) {
        this.importeConIVA = importeConIVA;
    }
    public Empresa getEmpresa() {
        return empresa;
    }
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }
    public Set<Ute> getUte() {
		return ute;
	}
	public void setUte(Set<Ute> ute) {
		this.ute = ute;
	}
    //endregion
    //region Overrides
	@Override
    public String toString() {
        return "Oferta [id=" + id + ", contrato=" + contrato
                + ", nif=" + nif + ", adjudicatario=" + adjudicatario + ", lote=" + lote
                + ", fechaAdjudicacion=" + fechaAdjudicacion + ", fechaFormalizacion=" + fechaFormalizacion
                + ", importeSinIVA=" + importeSinIVA + ", ejecucion=" + ejecucion
                + ", canon=" + canon + ", autonomo=" + autonomo+", Empresa="+empresa
                +", ganador=" +ganador+", importeConIVA=" + importeConIVA + ", IVA=" + iva+  ", AhorroVisible=" + ahorroVisible +"]";
    }
    //endregion
}
