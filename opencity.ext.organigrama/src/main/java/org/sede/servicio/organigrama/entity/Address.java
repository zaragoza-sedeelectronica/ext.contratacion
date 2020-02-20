package org.sede.servicio.organigrama.entity;

import java.math.BigDecimal;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlRootElement;

import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.Rdf;
import org.sede.core.anotaciones.RdfMultiple;
import org.sede.core.geo.Geometria;
import org.sede.core.geo.Punto;
import org.sede.core.anotaciones.Context;
import org.sede.core.dao.EntidadBase;

@XmlRootElement(name = "address")
@Entity
public class Address extends EntidadBase {
	@Id
	@Rdf(contexto = Context.DCT, propiedad = "identifier")
	private Integer id;
	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "street-adr"),
		@Rdf(contexto = Context.SCHEMA, propiedad = "streetAddress"),
		@Rdf(contexto = Context.LOCN, propiedad = "fullAddress")})
	@Size(max = 255)
	private String address;

	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "hasPostalCode"),
		@Rdf(contexto = Context.SCHEMA, propiedad = "postalCode"),
		@Rdf(contexto = Context.LOCN, propiedad = "postCode")})
	@Size(max = 255)
	private String postal_code;

	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "locality"),
		@Rdf(contexto = Context.SCHEMA, propiedad = "addressLocality"),
		@Rdf(contexto = Context.LOCN, propiedad = "addressArea")})
	private String locality;
	
	@RdfMultiple({@Rdf(contexto = Context.VCARD, propiedad = "country-name"),
		@Rdf(contexto = Context.SCHEMA, propiedad = "addressCountry"),
		@Rdf(contexto = Context.LOCN, propiedad = "adminUnitL1")})
	private String countryName;
	
	@Transient
	private Punto geometry;
	
	@Interno @Column(name = "X", nullable = true, precision = 10, scale = 2)
	private BigDecimal x;

	@Interno @Column(name = "Y", nullable = true, precision = 10, scale = 2)
	private BigDecimal y;
	
	public String getLocality() {
		return "Zaragoza";
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCountryName() {
		return "España";
	}

	public void setCountryName(String countryName) {
		this.countryName = countryName;
	}
	
	public Punto getGeometry() {
		if (getX() != null) {
			geometry = new Punto(Punto.POINT, new Double[] {
					getX().doubleValue(), getY().doubleValue() });
		}
		return geometry;
	}

	public void setGeometry(Punto geometry) {
		if (geometry != null) {
			if (geometry.formatoWgs84()) {
				geometry.setCoordinates(Geometria.pasarAUTM30(
						geometry.getCoordinates()[0],
						geometry.getCoordinates()[1]));
			}
			this.x = BigDecimal.valueOf(geometry.getCoordinates()[0]);
			this.y = BigDecimal.valueOf(geometry.getCoordinates()[1]);
		}
		this.geometry = geometry;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getPostal_code() {
		return postal_code;
	}

	public void setPostal_code(String postal_code) {
		this.postal_code = postal_code;
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

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	@Override
	public String toString() {
		return "Address [id=" + id + ", address=" + address + ", postal_code="
				+ postal_code + ", locality=" + locality + ", countryName="
				+ countryName + ", geometry=" + geometry + ", x=" + x + ", y="
				+ y + "]";
	}	
	
	
}
