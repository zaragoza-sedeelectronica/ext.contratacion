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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.metamodel.source.annotations.attribute.type.LobTypeResolver;
import org.sede.core.anotaciones.InList;
import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Permisos;
import org.sede.core.dao.EntidadBase;
import org.sede.core.utils.ConvertDate;
import org.sede.core.utils.Funciones;
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
import java.util.Objects;

@XmlRootElement(name = "anuncio")
@Entity
@Table(name = "PERFIL_ANUNCIO", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING + "/anuncio")
@SequenceGenerator(name = "SECUENCIA_SEQ_PERFIL_ANUNCIO", sequenceName = "SEQ_PERFIL_ANUNCIO", allocationSize = 1)
@BatchSize(size = 50)
public class Anuncio extends EntidadBase {
	//region Atributtes & Columns
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SEQ_PERFIL_ANUNCIO")
	@Id
	@Column(name = "ID_ANUNCIO", unique = true, nullable = false, precision = 22, scale = 0)
	private BigDecimal id;
	
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ID_TIPOANUNCIO")
	@BatchSize(size = 50)
	private Tipoanuncio type;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_CONTRATO", nullable = false)
	@Interno
	private Contrato contrato;
	
	@Column(name = "TITULO", nullable = false, length = 4000)
	private String title;

	@Transient
	private String uri;
	
	@Column(name = "TEXTO")
	@Lob
	private String description;

	@Column(name = "ADJUNTO",nullable = true)
	@Lob
	@Interno
	private byte[] adjunto;

	@Column(name = "NOMBRADJUNTO")
	@Interno
	private String fileName;

	
	@Temporal(TemporalType.DATE)
	@Column(name = "GCZ_FECHACAD", length = 7)
	private Date expiration;
	
	@Column(name = "VISUALIZARPRIMERA", length = 1)
	@Permisos(Permisos.DET)
	private String visualizarprimera;
	
	@OneToOne(fetch = FetchType.EAGER, optional = true)
	@JoinColumn(name = "ID_ANUNCIO", referencedColumnName = "ID_ANUNCIO",insertable=false, updatable=false)
	@NotFound(action=NotFoundAction.IGNORE)
	@Interno
	private Sello sello;
	@Transient
	private String selladoTiempo;
	
	@Column(name = "GCZ_PUBLICADO") @Size(max = 1) @Permisos(Permisos.PUB) @InList({"S", "N"})
	private String visible;

	@Temporal(TemporalType.TIMESTAMP) @Column(name = "GCZ_FECHAALTA") @Permisos(Permisos.DET)
	private Date creationDate;

	@Temporal(TemporalType.TIMESTAMP) @Column(name = "GCZ_FECHAMOD") 
	private Date lastUpdated;

	@DateTimeFormat(pattern = ConvertDate.DATE_FORMAT)
	@Column(name = "GCZ_FECHAPUB")
	private Date pubDate;

	@Column(name = "GCZ_USUARIOALTA") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioAlta;

	@Column(name = "GCZ_USUARIOMOD") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioMod;

	@Column(name = "GCZ_USUARIOPUB") @Size(max = 100) @Permisos(Permisos.DET)
	private String usuarioPub;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ID_LENGUAJE")
	@BatchSize(size = 50)
	private Lenguaje lenguaje;
	//endregion
	//region Getters &Setters

	public byte[] getAdjunto() {
		return adjunto;
	}

	public void setAdjunto(byte[] adjunto) {
		this.adjunto = adjunto;
	}

	public Lenguaje getLenguaje() {
		return lenguaje;
	}

	public void setLenguaje(Lenguaje lenguaje) {
		this.lenguaje = lenguaje;
	}

	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public Tipoanuncio getType() {
		return type;
	}

	public void setType(Tipoanuncio type) {
		this.type = type;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Date getExpiration() {
		return expiration;
	}

	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getVisualizarprimera() {
		return visualizarprimera;
	}

	public void setVisualizarprimera(String visualizarprimera) {
		this.visualizarprimera = visualizarprimera;
	}

	
	public String getUri() {
		if (this.getFileName() != null) {
			return Funciones.getPathSecureWithoutContext() + "/sede/servicio/contratacion-publica/anuncio/" + this.getId() + "/document";
		} else {
			return null;
		}
	}

	public Sello getSello() {
		return sello;
	}

	public void setSello(Sello sello) {
		this.sello = sello;
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
		return this.pubDate;
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

	public Contrato getContrato() {
		return contrato;
	}

	public void setContrato(Contrato contrato) {
		this.contrato = contrato;
	}

	public String getSelladoTiempo() {
		if (this.getSello() == null) {
			return null;
		} else if (this.getSello().getPermalink() == null) {
			return "/sede/portal/contratacion-publica/servicio/fehaciente/" + this.getSello().getIdfehaciente();
		} else {
			return this.getSello().getPermalink();
		}
			
	}

	public void setSelladoTiempo(String selladoTiempo) {
		this.selladoTiempo = selladoTiempo;
	}
	//endregion
	//region Overrides
	@Override
	public String toString() {
		return "Anuncio [id=" + id + ", type=" + type + ", title=" + title + ", description="
				+ description 
//				+ ", fichero=" + fichero 
				+ ", expiration=" + expiration
				+ ", file_name=" + fileName + ", visualizarprimera="
				+ visualizarprimera + ", sello=" + sello + ", selladoTiempo="
				+ selladoTiempo + ", visible=" + visible + ", creationDate="
				+ creationDate + ", lastUpdated=" + lastUpdated + ", pubDate="
				+ pubDate + ", usuarioAlta=" + usuarioAlta + ", usuarioMod="
				+ usuarioMod + ", usuarioPub=" + usuarioPub
				+ ", lenguaje=" + lenguaje
				+ "]";
	}

	public boolean necesitaSellado() {
		boolean retorno = false;
		if (this.getType() != null &&
				(new BigDecimal(2).equals(this.getType().getId()) 
				|| new BigDecimal(5).equals(this.getType().getId()) 
				|| new BigDecimal(6).equals(this.getType().getId()))
				|| new BigDecimal(31).equals(this.getType().getId())
				|| new BigDecimal(32).equals(this.getType().getId())
				|| new BigDecimal(33).equals(this.getType().getId())) {
			retorno = true;
		}
		return retorno;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Anuncio anuncio = (Anuncio) o;
		return Objects.equals(id, anuncio.id) &&
				Objects.equals(type, anuncio.type) &&
				Objects.equals(contrato, anuncio.contrato) &&
				Objects.equals(title, anuncio.title) &&
				Objects.equals(uri, anuncio.uri) &&
				Objects.equals(description, anuncio.description) &&
				Objects.equals(adjunto, anuncio.adjunto) &&
				Objects.equals(fileName, anuncio.fileName) &&
				Objects.equals(expiration, anuncio.expiration) &&
				Objects.equals(visualizarprimera, anuncio.visualizarprimera) &&
				Objects.equals(sello, anuncio.sello) &&
				Objects.equals(selladoTiempo, anuncio.selladoTiempo) &&
				Objects.equals(visible, anuncio.visible) &&
				Objects.equals(creationDate, anuncio.creationDate) &&
				Objects.equals(lastUpdated, anuncio.lastUpdated) &&
				Objects.equals(pubDate, anuncio.pubDate) &&
				Objects.equals(usuarioAlta, anuncio.usuarioAlta) &&
				Objects.equals(usuarioMod, anuncio.usuarioMod) &&
				Objects.equals(usuarioPub, anuncio.usuarioPub) &&
				Objects.equals(lenguaje, anuncio.lenguaje);
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, type, contrato, title, uri, description, adjunto, fileName, expiration, visualizarprimera, sello, selladoTiempo, visible, creationDate, lastUpdated, pubDate, usuarioAlta, usuarioMod, usuarioPub, lenguaje);
	}

	//endregion

}
