package org.sede.servicio.perfilcontratante.entity;


import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.*;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.*;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

@XmlRootElement(name="entidadContratante")
@Entity
@Table(name = "PERFIL_PORTAL", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
@Grafo(Contrato.GRAFO)
@Rel

public class EntidadContratante extends EntidadBase implements java.io.Serializable {
	//region Atributtes
	@Id
	@Column(name = "ID_PORTAL", unique = true, nullable = false)
	private BigDecimal id;
	
	@Column(name = "NOMBRE", nullable = false, length = 500)
	private String title;
	
	@Permisos(Permisos.DET)
	@Column(name = "CARPETA", length = 100)
	private String carpeta;
	
	@Column(name = "GCZ_PUBLICADO") @Size(max = 1) @Permisos(Permisos.PUB) @InList({"S", "N"})
	private String visible;

	@Temporal(TemporalType.DATE) @Column(name = "GCZ_FECHAALTA") @Permisos(Permisos.DET)
	private Date creationDate;

	@Temporal(TemporalType.DATE) @Column(name = "GCZ_FECHAMOD") 
	private Date lastUpdated;

	@Temporal(TemporalType.DATE) @Column(name = "GCZ_FECHAPUB")
	private Date pubDate;

	@Column(name = "GCZ_USUARIOALTA") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioAlta;

	@Column(name = "GCZ_USUARIOMOD") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioMod;

	@Column(name = "GCZ_USUARIOPUB") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioPub;
	@Column(name = "SERVICIO_GESTOR_ID")
	@Interno
	@SoloEnEstaEntidad
	@Size(max = 100)
	@Permisos(Permisos.DET)
	private String servicioGestorId;
	@Transient
	private String anyo;
	@Transient
	private List<EstructuraOrganizativa> organismosContratantes;
	@Transient
	private List<EstructuraOrganizativa>  ServicioGestores;
	@Transient
	private List<Contrato> listadoContratoEntidad;
	@Transient
	private List<ContratosPorAnyoIdPortal> datosPortal;
	//endregion
	//region Contructor
	public EntidadContratante() {
		super();
	}
	public EntidadContratante(BigDecimal id) {
		super();
		this.setId(id);
	}
	//endregion
	//region setter & getter


	public String getServicioGestorId() {
		return servicioGestorId;
	}

	public void setServicioGestorId(String servicioGestorId) {
		this.servicioGestorId = servicioGestorId;
	}

	public String getAnyo() {
		return anyo;
	}

	public void setAnyo(String anyo) {
		this.anyo = anyo;
	}

	public List<ContratosPorAnyoIdPortal> getDatosPortal() {
		return datosPortal;
	}

	public void setDatosPortal(List<ContratosPorAnyoIdPortal> datosPortal) {
		this.datosPortal = datosPortal;
	}

	public List<Contrato> getListadoContratoEntidad() {
		return listadoContratoEntidad;
	}

	public void setListadoContratoEntidad(List<Contrato> listadoContratoEntidad) {
		this.listadoContratoEntidad = listadoContratoEntidad;
	}

	public List<EstructuraOrganizativa> getOrganismosContratantes() {
		return organismosContratantes;
	}

	public void setOrganismosContratantes(List<EstructuraOrganizativa> organismosContratantes) {
		this.organismosContratantes = organismosContratantes;
	}

	public List<EstructuraOrganizativa> getServicioGestores() {
		return ServicioGestores;
	}

	public void setServicioGestores(List<EstructuraOrganizativa> servicioGestores) {
		ServicioGestores = servicioGestores;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getCarpeta() {
		return carpeta;
	}

	public void setCarpeta(String carpeta) {
		this.carpeta = carpeta;
	}

	public String getVisible() {
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

	public Date getLastUpdated() {
		return lastUpdated;
	}

	public void setLastUpdated(Date lastUpdated) {
		this.lastUpdated = lastUpdated;
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
	//endregion
	//region Override

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("EntidadContratante [");
		sb.append("id=").append(id);
		sb.append(", title='").append(title).append('\'');
		sb.append(", carpeta='").append(carpeta).append('\'');
		sb.append(", visible='").append(visible).append('\'');
		sb.append(", creationDate=").append(creationDate);
		sb.append(", lastUpdated=").append(lastUpdated);
		sb.append(", pubDate=").append(pubDate);
		sb.append(", usuarioAlta='").append(usuarioAlta).append('\'');
		sb.append(", usuarioMod='").append(usuarioMod).append('\'');
		sb.append(", usuarioPub='").append(usuarioPub).append('\'');
		sb.append(", anyo='").append(anyo).append('\'');
		sb.append(", organismosContratantes=").append(organismosContratantes);
		sb.append(", ServicioGestores=").append(ServicioGestores);
		sb.append(", listadoContratoEntidad=").append(listadoContratoEntidad);
		sb.append(", datosPortal=").append(datosPortal);
		sb.append(']');
		return sb.toString();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof EntidadContratante)) return false;
		EntidadContratante that = (EntidadContratante) o;
		if (!getId().equals(that.getId())) return false;
		if (!getTitle().equals(that.getTitle())) return false;
		if (getOrganismosContratantes() != null ? !getOrganismosContratantes().equals(that.getOrganismosContratantes()) : that.getOrganismosContratantes() != null)
			return false;
		if (getServicioGestores() != null ? !getServicioGestores().equals(that.getServicioGestores()) : that.getServicioGestores() != null)
			return false;
		return getListadoContratoEntidad() != null ? getListadoContratoEntidad().equals(that.getListadoContratoEntidad()) : that.getListadoContratoEntidad() == null;

	}

	//endregion
}
