package org.sede.servicio.organigrama.entity;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.Context;
import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.InList;
import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.IsUri;
import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Permisos;
import org.sede.core.anotaciones.Rdf;
import org.sede.core.anotaciones.RdfMultiple;
import org.sede.core.dao.EntidadBase;
import org.sede.core.utils.Funciones;
import org.sede.core.utils.Propiedades;
import org.sede.servicio.organigrama.ConfigOrganigrama;
import org.sede.servicio.organigrama.OrganigramaController;

@XmlRootElement(name = "organismo")
@Entity
@DynamicUpdate
@Table(name = "V00010_JERARQUIA_ORGANIGRAMA", schema = ConfigOrganigrama.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + OrganigramaController.MAPPING)
//FIXME arreglar uri para que funcione
@RdfMultiple({@Rdf(uri = "/kos/sector-publico/organismo/", prefijo = "organo"), @Rdf(contexto = Context.ORG, propiedad = "Organization")})
@Grafo("http://www.zaragoza.es/sector-publico/organismo/")
@BatchSize(size = 100)
public class EstructuraOrganizativa extends EntidadBase implements java.io.Serializable {

	@Id
	@Column(name = "ID", unique = true, nullable = false)
	@Rdf(contexto = Context.DCT, propiedad = "identifier")
	private BigDecimal id;
	
	@Column(name = "CODIGO_DIR3")
	@Rdf(contexto = Context.ORGES, propiedad = "code-dir3")
	@Size(max = 255)
	private String dir3;
	
	/*@Column(name = "NOMBRE_ESTR", unique = true, nullable = false)
	@Permisos(Permisos.NEW)
	private String nombre_estructura;*/

	@Column(name = "ID_CEN", unique = true, nullable = false)
	@Permisos(Permisos.NEW)
	private Integer id_recurso;
	@Column(name = "NOMBRE_CEN", unique = true, nullable = false)
	@Permisos(Permisos.NEW)
	private String nombre_recurso;
	
	@Column(name = "NOMBRE_ESTR", nullable = false)
	@RdfMultiple({@Rdf(contexto = Context.RDFS, propiedad = "label"), 
		@Rdf(contexto = Context.DCT, propiedad = "title"),
		@Rdf(contexto = Context.VCARD, propiedad = "organization-name")})
	@Size(max = 255)
	@NotNull
	private String title;
	
	@Column(name = "email", nullable = false)
	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "hasEmail"), @Rdf(contexto = Context.SCHEMA, propiedad = "email")})
	@Size(max = 255)
	private String email;
	
	@Column(name = "phone", nullable = false)
	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "tel"), @Rdf(contexto = Context.SCHEMA, propiedad = "telephone")})
	@Size(max = 255)
	private String phone;
	
	@Column(name = "fax", nullable = false)
	@Rdf(contexto = Context.VCARD, propiedad = "faxNumber")
	@Size(max = 255)
	private String fax;
	
	@Column(name = "postal_code", nullable = false)
	@RdfMultiple({ @Rdf(contexto = Context.VCARD, propiedad = "hasPostalCode"),
			@Rdf(contexto = Context.SCHEMA, propiedad = "postalCode"),
			@Rdf(contexto = Context.LOCN, propiedad = "postCode") })
	@Size(max = 255)
	private String postal_code;
	
	@Column(name = "address", nullable = false)
	@Size(max = 255)
	@Interno
	private String address_interno;
		
	@Interno @Column(name = "X", nullable = true, precision = 10, scale = 2)
	private BigDecimal x;

	@Interno @Column(name = "Y", nullable = true, precision = 10, scale = 2)
	private BigDecimal y;
	
	@Interno @Column(name = "PORTAL", nullable = true, precision = 10, scale = 2)
	private Integer portal;
	
	@Column(name = "nivel_administracion", nullable = false)
	@Rdf(contexto = Context.ORGES, propiedad = "governmentalLevel")
	@NotNull
	private String nivelAdministracion;

	@Column(name = "tipo_entidad_publica", nullable = false)
	@Rdf(contexto = Context.ORG, propiedad = "classification")
	@NotNull
	private String tipoEntidadPublica;
	
	@Column(name = "nivel_jerarquico", nullable = false)
	@Rdf(contexto = Context.ORGES, propiedad = "hierarchicalLevel")
	@NotNull
	private Integer nivelJerarquico;
	
	
	@Column(name = "unidad_superior", nullable = false)
	@Rdf(contexto = Context.ORG, propiedad = "unitOf")
	@IsUri
	@NotNull
	private String unidadSuperior;
	
	@Transient
	private String nombreUnidadSuperior;
	
	@Column(name = "unidad_raiz", nullable = false)
	@Rdf(contexto = Context.ORGES, propiedad = "hasTopSuperOrganization")
	@IsUri
	@NotNull
	private String unidadRaiz;
	
	@Column(name = "estado", nullable = false)
	@Rdf(contexto = Context.ORGES, propiedad = "status")
	@NotNull
	@InList({"V","E","A","T"})
	@Description("V = Vigente, E = Extinguido, A = Anulado, T = Transitorio")
	private String status;
	
	// no hay datos??
	/*@Column(name = "id_persona_responsable", nullable = false)
	private String idPersonaResponsable;*/
	
	@Rdf(contexto = Context.ORGES, propiedad = "headOf")
	@Column(name = "empleado_responsable", nullable = false)
	private String headOf;

	
	@Temporal(TemporalType.DATE) @Column(name = "FECHA_ALTA")
	@Rdf(contexto = Context.DCT, propiedad = "created")
	private Date creationDate;
	
	@Temporal(TemporalType.DATE) @Column(name = "FECHA_BAJA")
	@Rdf(contexto = Context.DCT, propiedad = "valid")
	private Date valid;
	
	@Column(name = "COMPETENCIAS", nullable = false)
	@Rdf(contexto = Context.ORG, propiedad = "purpose")
	private String purpose;
	
	
	@Transient
	@Rdf(contexto = Context.VCARD, propiedad = "address")
	private Address address = new Address();
	
	
	public BigDecimal getId() {
		return id;
	}

	public void setId(BigDecimal id) {
		this.id = id;
	}

	public String getDir3() {
		return dir3;
	}

	public void setDir3(String dir3) {
		this.dir3 = dir3;
	}

	public Integer getId_recurso() {
		return id_recurso;
	}

	public String getNombre_recurso() {
		return nombre_recurso;
	}

	public void setNombre_recurso(String nombre_recurso) {
		this.nombre_recurso = nombre_recurso;
	}

	public void setId_recurso(Integer id_recurso) {
		this.id_recurso = id_recurso;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getHeadOf() {
		return headOf;
	}

	public void setHeadOf(String headOf) {
		this.headOf = headOf;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getFax() {
		return fax;
	}

	public void setFax(String fax) {
		this.fax = fax;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
	}

	public String getAddress_interno() {
		return address_interno;
	}

	public void setAddress_interno(String address_interno) {
		this.address_interno = address_interno;
	}

	public BigDecimal getX() {
		return x;
	}

	public void setX(BigDecimal x) {
		this.x = x;
	}

	public BigDecimal getY() {
		return y;
	}

	public void setY(BigDecimal y) {
		this.y = y;
	}

	public Address getAddress() {
		
		if (this.postal_code != null) {
			address.setPostal_code(this.postal_code);
		}
		if (this.address_interno != null) {
			address.setAddress(this.address_interno);
		}
		if (this.x != null) {
			address.setX(this.x);
		}
		if (this.y != null) {
			address.setY(this.y);
		}
		if (this.portal != null) {
			address.setId(this.portal);
		}
		return address;
	}

	public void setAddress(Address address) {
		if (address != null) {
			this.address_interno = address.getAddress();
			this.postal_code = address.getPostal_code();
			this.x = address.getX();
			this.y = address.getY();
			this.portal = address.getId();
		}
		this.address = address;
	}

	
	public String getNivelAdministracion() {
		return nivelAdministracion == null ? null : "http://datos.gob.es/kos/sector-publico/org/NivelAdminEntidad/" + nivelAdministracion;
	}

	public void setNivelAdministracion(String nivelAdministracion) {
		this.nivelAdministracion = nivelAdministracion;
	}

	public String getTipoEntidadPublica() {
		return tipoEntidadPublica == null ? "" : "http://datos.gob.es/kos/sector-publico/org/TipoEntidad/" + tipoEntidadPublica;
	}

	public void setTipoEntidadPublica(String tipoEntidadPublica) {
		this.tipoEntidadPublica = tipoEntidadPublica;
	}

	public Integer getNivelJerarquico() {
		return nivelJerarquico;
	}

	public void setNivelJerarquico(Integer nivelJerarquico) {
		this.nivelJerarquico = nivelJerarquico;
	}

	public String getUnidadSuperior() {
		if (unidadSuperior != null) {
			return "http://" + Propiedades.getPath() + Propiedades.getContexto() + EstructuraOrganizativa.class.getAnnotation(PathId.class).value() + "/" + unidadSuperior; 
		} else {
			return unidadSuperior;	
		}
	}

	public void setUnidadSuperior(String unidadSuperior) {
		this.unidadSuperior = unidadSuperior;
	}

	public String getUnidadRaiz() {
		if (unidadRaiz != null) {
			return "http://" + Propiedades.getPath() + Propiedades.getContexto() + EstructuraOrganizativa.class.getAnnotation(PathId.class).value() + "/" + unidadRaiz; 
		} else {
			return unidadRaiz;	
		}
	}

	public void setUnidadRaiz(String unidadRaiz) {
		this.unidadRaiz = unidadRaiz;
	}

	public String getStatus() {
		return status == null ? null : "http://datos.gob.es/kos/sector-publico/org/EstadoEntidad/" + status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	/*public String getIdPersonaResponsable() {
		return idPersonaResponsable;
	}

	public void setIdPersonaResponsable(String idPersonaResponsable) {
		this.idPersonaResponsable = idPersonaResponsable;
	}

	public String getPersonaResponsable() {
		return personaResponsable;
	}

	public void setPersonaResponsable(String personaResponsable) {
		this.personaResponsable = personaResponsable;
	}*/

	@Override
	public String toString() {
		return "EstructuraOrganizativa [id=" + id
				+ ", dir3=" + dir3
				+ ", id_recurso=" + id_recurso
				+ ", nombre_recurso=" + nombre_recurso
				+ ", title=" + title + ", email=" + email + ", phone=" + phone
				+ ", fax=" + fax + ", postal_code=" + postal_code
				+ ", address_interno=" + address_interno + ", x=" + x + ", y="
				+ y + ", portal=" + portal + ", nivelAdministracion="
				+ nivelAdministracion + ", tipoEntidadPublica="
				+ tipoEntidadPublica + ", nivelJerarquico=" + nivelJerarquico
				+ ", unidadSuperior=" + unidadSuperior + ", unidadRaiz="
				+ unidadRaiz + ", status=" + status + ", creationDate="
				+ creationDate + ", valid=" + valid + ", purpose=" + purpose
				+ ", address=" + address + "]";
	}
	
	public Integer getPortal() {
		return portal;
	}

	public void setPortal(Integer portal) {
		this.portal = portal;
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getValid() {
		return valid;
	}

	public void setValid(Date valid) {
		this.valid = valid;
	}

	public String getPurpose() {
		return purpose;
	}

	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	public String getNombreUnidadSuperior() {
		return nombreUnidadSuperior;
	}

	public void setNombreUnidadSuperior(String nombreUnidadSuperior) {
		this.nombreUnidadSuperior = nombreUnidadSuperior;
	}	
	
	public String getUri() {
		return Funciones.obtenerPath(this.getClass()) + getId();
	}
}
