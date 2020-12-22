package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
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
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((creationDate == null) ? 0 : creationDate.hashCode());
		result = prime * result
				+ ((description == null) ? 0 : description.hashCode());
		result = prime * result
				+ ((expiration == null) ? 0 : expiration.hashCode());
		result = prime * result
				+ ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result
				+ ((lastUpdated == null) ? 0 : lastUpdated.hashCode());
		result = prime * result + ((pubDate == null) ? 0 : pubDate.hashCode());
		result = prime * result
				+ ((selladoTiempo == null) ? 0 : selladoTiempo.hashCode());
		result = prime * result + ((sello == null) ? 0 : sello.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((type == null) ? 0 : type.hashCode());
		result = prime * result
				+ ((usuarioAlta == null) ? 0 : usuarioAlta.hashCode());
		result = prime * result
				+ ((usuarioMod == null) ? 0 : usuarioMod.hashCode());
		result = prime * result
				+ ((usuarioPub == null) ? 0 : usuarioPub.hashCode());
		result = prime * result + ((visible == null) ? 0 : visible.hashCode());
		result = prime
				* result
				+ ((visualizarprimera == null) ? 0 : visualizarprimera
						.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Anuncio other = (Anuncio) obj;
		if (contrato == null) {
			if (other.contrato != null)
				return false;
		} else if (!contrato.equals(other.contrato)) {
			return false;
		}
		if (creationDate == null) {
			if (other.creationDate != null) {
				return false;
			}
		} else if (!creationDate.equals(other.creationDate)) {
			return false;
		}
		if (description == null) {
			if (other.description != null) {
				return false;
			}
		} else if (!description.equals(other.description)) {
			return false;
		}
		
		if (expiration == null) {
			if (other.expiration != null) {
				return false;
			}
		} else if (!expiration.equals(other.expiration)) {
			return false;
		}
		if (fileName == null) {
			if (other.fileName != null) {
				return false;
			}
		} else if (!fileName.equals(other.fileName)) {
			return false;
		}
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		if (lastUpdated == null) {
			if (other.lastUpdated != null) {
				return false;
			}
		} else if (!lastUpdated.equals(other.lastUpdated)) {
			return false;
		}
		if (pubDate == null) {
			if (other.pubDate != null) {
				return false;
			}
		} else if (!pubDate.equals(other.pubDate)) {
			return false;
		}
		if (selladoTiempo == null) {
			if (other.selladoTiempo != null) {
				return false;
			}
		} else if (!selladoTiempo.equals(other.selladoTiempo)) {
			return false;
		}
		if (sello == null) {
			if (other.sello != null) {
				return false;
			}
		} else if (!sello.equals(other.sello)) {
			return false;
		}
		if (title == null) {
			if (other.title != null) {
				return false;
			}
		} else if (!title.equals(other.title)) {
			return false;
		}
		if (type == null) {
			if (other.type != null) {
				return false;
			}
		} else if (!type.equals(other.type)) {
			return false;
		}
		if (usuarioAlta == null) {
			if (other.usuarioAlta != null) {
				return false;
			}
		} else if (!usuarioAlta.equals(other.usuarioAlta)) {
			return false;
		}
		if (usuarioMod == null) {
			if (other.usuarioMod != null) {
				return false;
			}
		} else if (!usuarioMod.equals(other.usuarioMod)) {
			return false;
		}
		if (usuarioPub == null) {
			if (other.usuarioPub != null) {
				return false;
			}
		} else if (!usuarioPub.equals(other.usuarioPub)) {
			return false;
		}
		if (visible == null) {
			if (other.visible != null) {
				return false;
			}
		} else if (!visible.equals(other.visible)) {
			return false;
		}
		if (visualizarprimera == null) {
			if (other.visualizarprimera != null) {
				return false;
			}
		} else if (!visualizarprimera.equals(other.visualizarprimera)) {
			return false;
		}
		return true;
	}
	//endregion

}
