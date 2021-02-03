/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
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

import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.dao.EntidadBase;

import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

@XmlRootElement(name = "ContratoTbfy")
@ResultsOnly(xmlroot = "ContratoTbfy")
@Grafo("http://www.zaragoza.es/contratacion-publica/tbfy/")
public class ContratoTbfy{
	//region Atributtes
	private String id;
	private String title;
	private String idioma;
	private String url;
	private String status;
	private BigDecimal amount;
	private String currency;
	private String buyer;
	private String supplier;
	private String description;
	private String documentUrl;
	private Date starDate;
	private Date endDate;
	private String organizationUrl;
	private String awardUrl;
	private String tenderdUrl;
	private String jurisdiccion;
	private Date awarDate;
	private String awardTitle;
	private String awardDescription;
	private Date awarStartDate;
	private Date awarEndDate;


	//endregion
	//region Getters &Setters


	public String getTenderdUrl() {
		return tenderdUrl;
	}

	public void setTenderdUrl(String tenderdUrl) {
		this.tenderdUrl = tenderdUrl;
	}

	public String getOrganizationUrl() {
		return organizationUrl;
	}

	public void setOrganizationUrl(String organizationUrl) {
		this.organizationUrl = organizationUrl;
	}

	public String getAwardUrl() {
		return awardUrl;
	}

	public void setAwardUrl(String awardUrl) {
		this.awardUrl = awardUrl;
	}

	public String getJurisdiccion() {
		return jurisdiccion;
	}

	public void setJurisdiccion(String jurisdiccion) {
		this.jurisdiccion = jurisdiccion;
	}

	public Date getAwarDate() {
		return awarDate;
	}

	public void setAwarDate(Date awarDate) {
		this.awarDate = awarDate;
	}

	public String getAwardTitle() {
		return awardTitle;
	}

	public void setAwardTitle(String awardTitle) {
		this.awardTitle = awardTitle;
	}

	public String getAwardDescription() {
		return awardDescription;
	}

	public void setAwardDescription(String awardDescription) {
		this.awardDescription = awardDescription;
	}

	public Date getAwarStartDate() {
		return awarStartDate;
	}

	public void setAwarStartDate(Date awarStartDate) {
		this.awarStartDate = awarStartDate;
	}

	public Date getAwarEndDate() {
		return awarEndDate;
	}

	public void setAwarEndDate(Date awarEndDate) {
		this.awarEndDate = awarEndDate;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getBuyer() {
		return buyer;
	}

	public void setBuyer(String buyer) {
		this.buyer = buyer;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getDocumentUrl() {
		return documentUrl;
	}

	public void setDocumentUrl(String documentUrl) {
		this.documentUrl = documentUrl;
	}

	public Date getStarDate() {
		return starDate;
	}

	public void setStarDate(Date starDate) {
		this.starDate = starDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdioma() {
		return idioma;
	}

	public void setIdioma(String idioma) {
		this.idioma = idioma;
	}
	//endregion


	@Override
	public String toString() {
		return "ContratoTbfy[" +
				"id='" + id + '\'' +
				", title='" + title + '\'' +
				", idioma='" + idioma + '\'' +
				", url='" + url + '\'' +
				", status='" + status + '\'' +
				", amount=" + amount +
				", currency='" + currency + '\'' +
				", buyer='" + buyer + '\'' +
				", supplier='" + supplier + '\'' +
				", description='" + description + '\'' +
				", documentUrl='" + documentUrl + '\'' +
				", starDate=" + starDate +
				", endDate=" + endDate +
				']';
	}
}
