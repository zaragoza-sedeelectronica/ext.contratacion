package org.sede.servicio.perfilcontratante.entity;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hibernate.annotations.*;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.BooleanConverter;
import org.sede.core.dao.EntidadBase;
import org.sede.core.rest.Peticion;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;
import org.sede.servicio.perfilcontratante.dao.TipoContratoGenericDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OrderBy;
import javax.persistence.Table;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Period;
import java.util.*;

@XmlRootElement(name = "contrato")
@Entity(name = "Contrato")
@Table(name = "PERFIL_CONTRATO", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
@Grafo(Contrato.GRAFO)
@Rel
public class Contrato extends EntidadBase implements Serializable {
	//region Atributtes & Columns
	@Interno
	public static final String GRAFO = "http://www.zaragoza.es/sector-publico/contrato";

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY, generator = "XyzIdGenerator")
	@GenericGenerator(name = "XyzIdGenerator", strategy = "org.sede.servicio.perfilcontratante.dao.ContratoIdGenerator")
	@Column(name = "ID_CONTRATO", unique = true, nullable = false)
	private BigDecimal id;

	@Column(name = "NOMBRE") @Size(max = 600)
	private String title;
	@Transient
	private String title2;

	@Column(name = "EXPEDIENTE") @Size(max = 600)
	private String expediente;

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contrato", cascade = CascadeType.MERGE)
	@Fetch(FetchMode.JOIN)
	@OrderBy("id ASC")
	@BatchSize(size = 200)
	private List<Lote> lotes = new ArrayList<Lote>(0);

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contrato", cascade = CascadeType.MERGE)
	//@Fetch(FetchMode.JOIN)
	@OrderBy("GANADOR DESC")
	@BatchSize(size = 50)
	@SoloEnEstaEntidad
	private List<Oferta> ofertas = new ArrayList<Oferta>(0);
	
	@Column(name = "CONT_MENOR")
	@Convert(converter = BooleanConverter.class)
	private Boolean contratoMenor;

	@Column(name = "URGENTE") @Size(max = 1)
	@Permisos(Permisos.PUB) @InList({"S", "N", "E"})
	private String urgente;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_TIPOCONTRATO")
	@BatchSize(size = 50)
	private Tipocontrato type;

	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_PROCEDIMIENTO")
	@BatchSize(size = 50)
	private Procedimiento procedimiento;
	
	@DateTimeFormat(pattern = ConvertDate.DATETIME_FORMAT_PATTERN)
	@Column(name = "FECHA_PRESENTACION")
	@Temporal(TemporalType.TIMESTAMP)
	private Date fechaPresentacion;

	@DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
	@Column(name = "FECHA_ADJ")
	private Date fechaAdjudicacion;

	@DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
	@Column(name = "FECH_FORM")
	private Date fechaFormalizacion;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_PORTAL")
	@BatchSize(size = 50)
	@SoloEnEstaEntidad
	private EntidadContratante entity;

	@Column(name = "DURACION")
	private BigDecimal duracion;

	@Column(name = "DURACION_REAL")
	private BigDecimal duracionReal;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ESTADO", referencedColumnName="ID_ESTADO")
	private Estado status;

	@Temporal(TemporalType.DATE)
	@DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
	@Column(name = "GCZ_FECHACAD", length = 7)
	private Date expiration;

	@Column(name = "GCZ_PUBLICADO")
	@Size(max = 1) @Permisos(Permisos.PUB)
	@InList({"S", "N"})
	private String visible;

	@Temporal(TemporalType.TIMESTAMP) @Column(name = "GCZ_FECHAALTA") @Permisos(Permisos.DET)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "GCZ_FECHACONTRATO")
	@DateTimeFormat(pattern = ConvertDate.DATETIME_FORMAT_PATTERN)
	private Date fechaContrato;

	@Temporal(TemporalType.TIMESTAMP) @Column(name = "GCZ_FECHAMOD")
	private Date lastUpdated;

	@Temporal(TemporalType.TIMESTAMP) @Column(name = "GCZ_FECHAPUB")
	private Date pubDate;

	@Column(name = "GCZ_USUARIOALTA") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioAlta;

	@Column(name = "GCZ_USUARIOMOD") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioMod;

	@Column(name = "GCZ_USUARIOPUB") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioPub;

	@Column(name = "OBJETO") @Size(max = 2000)
	private String objeto;

	@Column(name = "LICITADORES")
	private Integer numLicitadores;

	@Column(name = "IMPORTE_SINIVA")
	private BigDecimal importeSinIVA;

	@Column(name = "IMPORTE_CONIVA")
	private BigDecimal importeConIVA;

	@Column(name = "VALOR_ESTIMADO")
	private BigDecimal valorEstimado;

	@Column(name = "DERIVADOACUERDOMARCO")
    @Convert(converter = BooleanConverter.class)
    private Boolean derivadoAcuerdoMarco;

	@Column(name = "DERIVADOSISTEMADINAMICO")
    @Convert(converter = BooleanConverter.class)
    private Boolean derivadoSistemaDinamico;

	@Column(name = "CONSUBASTAELECTRONICA")
    @Convert(converter = BooleanConverter.class)
    private Boolean subastaElectronica;

	@Column(name = "COMPLEMENTARIO")
    @Convert(converter = BooleanConverter.class)
    private Boolean complementario;

	@Column(name = "REGULACIONARMONIZADA")
    @Convert(converter = BooleanConverter.class)
    private Boolean regulacionArmonizada;

	@Column(name = "PLAZOCONSESION")
	private Integer plazoConsesion;

	@Column(name = "CLAUSULAPRORROGA")
    @Convert(converter = BooleanConverter.class)
    private Boolean clausulaProrroga;

	@Column(name = "PERIODOPRORROGA")
	private Integer periodoProrroga;


	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "PERFIL_CONTRATO_TIENE_CPV", joinColumns = { @JoinColumn(name = "ID_CONTRATO", nullable = false) },
			inverseJoinColumns = { @JoinColumn(name = "CPV", nullable = false)})
	@Access(AccessType.FIELD)
	@BatchSize(size = 50)
	@SoloEnEstaEntidad
	private Set<Cpv> cpv=new HashSet<Cpv>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contrato", cascade = { CascadeType.ALL })
	@Permisos(Permisos.DET)
	@OrderBy("creationDate ASC")
	@BatchSize(size = 50)
	@Access(AccessType.FIELD)
	@SoloEnEstaEntidad
	private List<Anuncio> anuncios = new ArrayList<Anuncio>(0);
	
	@Column(name="IVA")
	private BigDecimal iva;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SERVICIO_GESTOR")
	@NotFound(action = NotFoundAction.IGNORE)
	@BatchSize(size = 200)
	private EstructuraOrganizativa servicio;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ORGANO_CONTRATACION")
	@NotFound(action = NotFoundAction.IGNORE)
	@BatchSize(size = 200)
	private EstructuraOrganizativa organoContratante;
	@Column(name = "CANON")
	@Convert(converter = BooleanConverter.class)
	private Boolean canon;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "contrato", cascade = { CascadeType.ALL })
	@Permisos(Permisos.DET)
	@BatchSize(size = 50)
	@Access(AccessType.FIELD)
	private Set<Criterio> criterios = new HashSet<Criterio>(0);

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_PADRE")
	@SoloEnDetalle
	@BatchSize(size = 200)
	@NotFound(action = NotFoundAction.IGNORE)
	@Interno
	private Contrato padre;

	@Transient 
	private String url;
	
	@Transient
	private String fechaPresentacionYear;
	@Transient
	private List<String[]> resultadosPorAnyo;
	//endregion
	//region Constructor
	public Contrato() {
		super();
	}
	//endregion
	//region Setter & getter

	public Set<Criterio> getCriterios() {
		return criterios;
	}

	public void setCriterios(Set<Criterio> criterios) {
		this.criterios = criterios;
	}

	public Contrato getPadre() {
		return padre;
	}

	public void setPadre(Contrato padre) {
		this.padre = padre;
	}

	public Boolean getCanon() {
		return canon;
	}

	public void setCanon(Boolean canon) {
		this.canon = canon;
	}

	public Date getFechaContrato() {
		return fechaContrato;
	}

	public void setFechaContrato(Date fechaContrato) {
		this.fechaContrato = fechaContrato;
	}

	public BigDecimal getIva() {
		return iva;
	}

	public void setIva(BigDecimal iva) {
		this.iva = iva;
	}

	public EstructuraOrganizativa getOrganoContratante() {
		return organoContratante;
	}
	public void setOrganoContratante(EstructuraOrganizativa organoContratante) {
		this.organoContratante = organoContratante;
	}
	public EstructuraOrganizativa getServicio() {
		return servicio;
	}
	public void setServicio(EstructuraOrganizativa servicio) {
		this.servicio = servicio;
	}
//	public BigDecimal getServicioGestor() {
//		return servicioGestor;
//	}
//	public void setServicioGestor(BigDecimal servicioGestor) {
//		this.servicioGestor = servicioGestor;
//	}
	public List<String[]> getResultadosPorAnyo() {
		return resultadosPorAnyo;
	}
	public void setResultadosPorAnyo(List<String[]> resultadosPorAnyo) {
		this.resultadosPorAnyo = resultadosPorAnyo;
	}
	public static String obtenerEntidad(Peticion peticion) {
		if (peticion.getCredenciales() != null && peticion.getCredenciales().getUsuario().getPropiedades() != null) {
			return peticion.getCredenciales().getUsuario().getPropiedades().get("contrato_entidad") == null ? "1" : peticion.getCredenciales().getUsuario().getPropiedades().get("contrato_entidad");
		} else {
			return null;
		}
	}
	public static String obtenerPortal(Peticion peticion) {
		if (peticion.getCredenciales() != null && peticion.getCredenciales().getUsuario().getPropiedades() != null) {
			return peticion.getCredenciales().getUsuario().getPropiedades().get("contrato_portal") == null ? "1" : peticion.getCredenciales().getUsuario().getPropiedades().get("contrato_portal");
		} else {
			return null;
		}
	}
	public String getFechaPresentacionYear() {
		return fechaPresentacionYear;
	}
	public void setFechaPresentacionYear(String fechaPresentacionYear) {
		this.fechaPresentacionYear = fechaPresentacionYear;
	}
	public BigDecimal getId() {
		return id;
	}
	public void setId(BigDecimal id) {
		this.id = id;
	}
	public String getTitle() {
		if (title != null) {
			return (title.trim().toUpperCase());
		} else {
			return title;
		}
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getExpediente() {
		return expediente;
	}
	public void setExpediente(String expediente) {
		this.expediente = expediente;
	}
	public List<Lote> getLotes() {
		return this.lotes;
	}
	public void setLotes(List<Lote> lotes) {
		this.lotes = lotes;
	}
	public List<Oferta> getOfertas() {
		return ofertas;
	}
	public void setOfertas(List<Oferta> ofertas) {
		this.ofertas = ofertas;
	}
	public Procedimiento getProcedimiento() {
		return procedimiento;
	}
	public void setProcedimiento(Procedimiento procedimiento) {
		this.procedimiento = procedimiento;
	}
	public Date getFechaPresentacion() {
		return fechaPresentacion;
	}
	public void setFechaPresentacion(Date fechaPresentacion) {
		this.fechaPresentacion = fechaPresentacion;
	}
	public Date getFechaAdjudicacion() {
		return fechaAdjudicacion;
	}
	public void setFechaAdjudicacion(Date fechaAdjudicacion) {
		this.fechaAdjudicacion = fechaAdjudicacion;
	}
	public Estado getStatus() {
		return status;
	}
	public void setStatus(Estado status) {
		this.status = status;
	}
	public String getUrl() {
		if (this.getEntity() != null) {
			return "https://www.zaragoza.es/ciudad/gestionmunicipal/contratos/" +  this.getEntity().getCarpeta() + "/contrato_Avisos?id=" + this.getId();
		} else {
			return null;
		}
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public Tipocontrato getType() {
		return type;
	}
	public void setType(Tipocontrato type) {
		this.type = type;
	}
	public EntidadContratante getEntity() {
		return entity;
	}
	public void setEntity(EntidadContratante entity) {
		this.entity = entity;
	}
	public List<Anuncio> getAnuncios() {
		return anuncios;
	}
	public void setAnuncios(List<Anuncio> anuncios) {
		this.anuncios = anuncios;
	}
	public Date getExpiration() {
		return expiration;
	}
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}
	public  String getVisible() {
		return visible;
	}
	public void setVisible(String visible) {
		this.visible = visible;
	}
	public Date getCreationDate() {
		return creationDate;
	}
	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}
	public Date getPubDate() {
		return pubDate;
	}
	public void setPubDate(Date pubDate) {
		this.pubDate = pubDate;
	}
	public String getUsuarioAlta() {
		return usuarioAlta;
	}
	public void setUsuarioAlta(String usuarioAlta) {
		this.usuarioAlta = usuarioAlta;
	}
	public String getUsuarioMod() {
		return usuarioMod;
	}
	public void setUsuarioMod(String usuarioMod) {
		this.usuarioMod = usuarioMod;
	}
	public String getUsuarioPub() {
		return usuarioPub;
	}
	public void setUsuarioPub(String usuarioPub) {
		this.usuarioPub = usuarioPub;
	}
	public Boolean getContratoMenor() {
		return contratoMenor;
	}
	public void setContratoMenor(Boolean contratoMenor) {
		this.contratoMenor = contratoMenor;
	}
	public Date getLastUpdated() {
		return lastUpdated;
	}
	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
	}
	public String getObjeto() {
		return objeto;
	}
	public void setObjeto(String objeto) {
		this.objeto = objeto;
	}
	public BigDecimal getImporteSinIVA() {
		return importeSinIVA;
	}
	public void setImporteSinIVA(BigDecimal importeSinIVA) {
		this.importeSinIVA = importeSinIVA;
	}
	public BigDecimal getImporteConIVA() {
		return importeConIVA;
	}
	public void setImporteConIVA(BigDecimal importeConIVA) {
		this.importeConIVA = importeConIVA;
	}
	public BigDecimal getValorEstimado() {
		return valorEstimado;
	}
	public void setValorEstimado(BigDecimal valorEstimado) {
		this.valorEstimado = valorEstimado;
	}
//	public BigDecimal getOrgano() {
//		return organo;
//	}
//	public void setOrgano(BigDecimal organo) {
//		this.organo = organo;
//	}
	public Set<Cpv> getCpv() {
		return cpv;
	}
	public void setCpv(Set<Cpv> cpv) {
		this.cpv = cpv;
	}
	public BigDecimal getDuracion() {
		return duracion;
	}
	public void setDuracion(BigDecimal duracion) {
		this.duracion = duracion;
	}
	public Integer getNumLicitadores() {
		return numLicitadores;
	}
	public void setNumLicitadores(Integer numLicitadores) {
		this.numLicitadores = numLicitadores;
	}
	public Boolean getDerivadoAcuerdoMarco() {
		return derivadoAcuerdoMarco;
	}
	public void setDerivadoAcuerdoMarco(Boolean derivadoAcuerdoMarco) {
		this.derivadoAcuerdoMarco = derivadoAcuerdoMarco;
	}
	public Boolean getDerivadoSistemaDinamico() {
		return derivadoSistemaDinamico;
	}
	public void setDerivadoSistemaDinamico(Boolean derivadoSistemaDinamico) {
		this.derivadoSistemaDinamico = derivadoSistemaDinamico;
	}
	public Boolean getSubastaElectronica() {
		return subastaElectronica;
	}
	public void setSubastaElectronica(Boolean subastaElectronica) {
		this.subastaElectronica = subastaElectronica;
	}
	public Boolean getComplementario() {
		return complementario;
	}
	public void setComplementario(Boolean complementario) {
		this.complementario = complementario;
	}
	public Boolean getRegulacionArmonizada() {
		return regulacionArmonizada;
	}
	public void setRegulacionArmonizada(Boolean regulacionArmonizada) {
		this.regulacionArmonizada = regulacionArmonizada;
	}
	public Integer getPlazoConsesion() {
		return plazoConsesion;
	}
	public void setPlazoConsesion(Integer plazoConsesion) {
		this.plazoConsesion = plazoConsesion;
	}
	public Boolean getClausulaProrroga() {
		return clausulaProrroga;
	}
	public void setClausulaProrroga(Boolean clausulaProrroga) {
		this.clausulaProrroga = clausulaProrroga;
	}
	public Integer getPeriodoProrroga() {
		return periodoProrroga;
	}
	public void setPeriodoProrroga(Integer periodoProrroga) {
		this.periodoProrroga = periodoProrroga;
	}
	public String getUrgente() {
		return urgente;
	}
	public void setUrgente(String urgente) {
		this.urgente = urgente;
	}
	public Date getFechaFormalizacion() {
		return fechaFormalizacion;
	}
	public void setFechaFormalizacion(Date fechaFormalizacion) {
		this.fechaFormalizacion = fechaFormalizacion;
	}

	public BigDecimal getDuracionReal() {
		return duracionReal;
	}

	public void setDuracionReal(BigDecimal duracionReal) {
		this.duracionReal = duracionReal;
	}

	//endregion
	//region Override
	@Override
	public String toString() {
		return "Contrato [id=" + id + ", title=" + title + ", expediente=" + expediente
				//+ ", lotes=" + lotes
				/*+ ", ofertas=" + ofertas */+ ", contratoMenor=" + contratoMenor + ", type="
				+ type /*+ ", publicacion=" + publicacion*/ + ", fechaAdjudicacion=" + fechaAdjudicacion
				+ ", fechaPresentacion=" + fechaPresentacion + ", fechaPresentacionYear=" + fechaPresentacionYear
				/*+ ", entity=" + entity */+ ", status=" + status
				+ ", url=" + url + ", expiration=" + expiration 
//				+ ", organo=" + organo 
				+ ", duracion=" + duracion + ", duracionReal=" + duracionReal
				+ ", visible=" + visible + ", creationDate=" + creationDate + ", pubDate=" + pubDate
				+ ", usuarioAlta=" + usuarioAlta + ", usuarioMod=" + usuarioMod + ", usuarioPub=" + usuarioPub
//				+ ", anuncios=" + anuncios 
				+ ", valorEstimado=" + valorEstimado
				//+ ", cpv=" + cpv
				+ ", objeto=" + objeto 
//				+ ", servicioGestor=" + servicioGestor
				+ ", servicio=" + servicio
				+ ", numLicitadores=" + numLicitadores + ", importeSinIVA=" + importeSinIVA
				+ ", derivadoAcuerdoMarco=" + derivadoAcuerdoMarco
				+ ", derivadoSistemaDinamico=" + derivadoSistemaDinamico
				+ ", subastaElectronica=" + subastaElectronica
				+ ", complementario=" + complementario
				+ ", regulacionArmonizada=" + regulacionArmonizada
				+ ", urgente=" + urgente
				+ ", fechaFormalizacion=" + fechaFormalizacion
				+ ", procedimiento=" + procedimiento
				+ ", plazoConsesion=" + plazoConsesion
				+ ", clausulaProrroga=" + clausulaProrroga
				+ ", periodoProrroga=" + periodoProrroga
				+ ", importeConIVA=" + importeConIVA
				+ ", IVA=" + iva
				+ ", Fecha Contrato=" + fechaContrato
				+ ", OrganismoContratante=" + organoContratante +"]";
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		Contrato contrato = (Contrato) o;

		if (id != null ? !id.equals(contrato.id) : contrato.id != null) return false;
		if (title != null ? !title.equals(contrato.title) : contrato.title != null) return false;
		if (ofertas != null ? !ofertas.equals(contrato.ofertas) : contrato.ofertas != null) return false;
		if (contratoMenor != null ? !contratoMenor.equals(contrato.contratoMenor) : contrato.contratoMenor != null)
			return false;
		if (type != null ? !type.equals(contrato.type) : contrato.type != null) return false;
		if (fechaPresentacion != null ? !fechaPresentacion.equals(contrato.fechaPresentacion) : contrato.fechaPresentacion != null)
			return false;
		if (entity != null ? !entity.equals(contrato.entity) : contrato.entity != null) return false;
		if (status != null ? !status.equals(contrato.status) : contrato.status != null) return false;
		
		if (url != null ? !url.equals(contrato.url) : contrato.url != null) return false;
		if (expiration != null ? !expiration.equals(contrato.expiration) : contrato.expiration != null) return false;
		
		if (urgente != null ? !urgente.equals(contrato.urgente) : contrato.urgente != null) return false;
		if (fechaFormalizacion != null ? !fechaFormalizacion.equals(contrato.fechaFormalizacion) : contrato.fechaFormalizacion != null) return false;
		
		if (derivadoAcuerdoMarco != null ? !derivadoAcuerdoMarco.equals(contrato.derivadoAcuerdoMarco) : contrato.derivadoAcuerdoMarco != null) return false;
		if (derivadoAcuerdoMarco != null ? !derivadoAcuerdoMarco.equals(contrato.derivadoAcuerdoMarco) : contrato.derivadoAcuerdoMarco != null) return false;
		if (derivadoSistemaDinamico != null ? !derivadoSistemaDinamico.equals(contrato.derivadoSistemaDinamico) : contrato.derivadoSistemaDinamico != null) return false;
		if (subastaElectronica != null ? !subastaElectronica.equals(contrato.subastaElectronica) : contrato.subastaElectronica != null) return false;
		if (complementario != null ? !complementario.equals(contrato.complementario) : contrato.complementario != null) return false;
		if (regulacionArmonizada != null ? !regulacionArmonizada.equals(contrato.regulacionArmonizada) : contrato.regulacionArmonizada != null) return false;
		if (plazoConsesion != null ? !plazoConsesion.equals(contrato.plazoConsesion) : contrato.plazoConsesion != null) return false;
		if (clausulaProrroga != null ? !clausulaProrroga.equals(contrato.clausulaProrroga) : contrato.clausulaProrroga != null) return false;
		if (procedimiento != null ? !procedimiento.equals(contrato.procedimiento) : contrato.procedimiento != null) return false;
		if (periodoProrroga != null ? !periodoProrroga.equals(contrato.periodoProrroga) : contrato.periodoProrroga != null) return false;
		if (visible != null ? !visible.equals(contrato.visible) : contrato.visible != null) return false;
		if (creationDate != null ? !creationDate.equals(contrato.creationDate) : contrato.creationDate != null)
			return false;
		if (lastUpdated != null ? !lastUpdated.equals(contrato.lastUpdated) : contrato.lastUpdated != null)
			return false;
		if (pubDate != null ? !pubDate.equals(contrato.pubDate) : contrato.pubDate != null) return false;
		if (usuarioAlta != null ? !usuarioAlta.equals(contrato.usuarioAlta) : contrato.usuarioAlta != null)
			return false;
		if (usuarioMod != null ? !usuarioMod.equals(contrato.usuarioMod) : contrato.usuarioMod != null) return false;
		if (usuarioPub != null ? !usuarioPub.equals(contrato.usuarioPub) : contrato.usuarioPub != null) return false;
		if (anuncios != null ? !anuncios.equals(contrato.anuncios) : contrato.anuncios != null) return false;
		
		return true;

	}

	@Override
	public int hashCode() {
		int result = id != null ? id.hashCode() : 0;
		result = 31 * result + (title != null ? title.hashCode() : 0);
		result = 31 * result + (ofertas != null ? ofertas.hashCode() : 0);
		result = 31 * result + (contratoMenor != null ? contratoMenor.hashCode() : 0);
		result = 31 * result + (type != null ? type.hashCode() : 0);
		result = 31 * result + (fechaPresentacion != null ? fechaPresentacion.hashCode() : 0);
		result = 31 * result + (status != null ? status.hashCode() : 0);
		result = 31 * result + (url != null ? url.hashCode() : 0);
		result = 31 * result + (expiration != null ? expiration.hashCode() : 0);
		result = 31 * result + (visible != null ? visible.hashCode() : 0);
		result = 31 * result + (creationDate != null ? creationDate.hashCode() : 0);
		result = 31 * result + (lastUpdated != null ? lastUpdated.hashCode() : 0);
		result = 31 * result + (pubDate != null ? pubDate.hashCode() : 0);
		result = 31 * result + (usuarioAlta != null ? usuarioAlta.hashCode() : 0);
		result = 31 * result + (usuarioMod != null ? usuarioMod.hashCode() : 0);
		result = 31 * result + (usuarioPub != null ? usuarioPub.hashCode() : 0);
		result = 31 * result + (anuncios != null ? anuncios.hashCode() : 0);
		result = 31 * result + (derivadoAcuerdoMarco != null ? derivadoAcuerdoMarco.hashCode() : 0);
		result = 31 * result + (procedimiento != null && procedimiento.getId() != null ? procedimiento.hashCode() : 0);
		result = 31 * result + (derivadoSistemaDinamico != null ? derivadoSistemaDinamico.hashCode() : 0);
		result = 31 * result + (subastaElectronica != null ? subastaElectronica.hashCode() : 0);
		result = 31 * result + (complementario != null ? complementario.hashCode() : 0);
		result = 31 * result + (regulacionArmonizada != null ? regulacionArmonizada.hashCode() : 0);
		result = 31 * result + (urgente != null ? urgente.hashCode() : 0);
		result = 31 * result + (fechaFormalizacion != null ? fechaFormalizacion.hashCode() : 0);
		result = 31 * result + (plazoConsesion != null ? plazoConsesion.hashCode() : 0);
		result = 31 * result + (clausulaProrroga != null ? clausulaProrroga.hashCode() : 0);
		result = 31 * result + (periodoProrroga != null ? periodoProrroga.hashCode() : 0);
		return result;
	}
	//endregion
	//region Contructor

	//endregion
}
