package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "Contracting-process")
@ResultsOnly(xmlroot = "Contracting-process")
public class ContractingProcess {

	//region Atributes
	private String id;
	private DateTime datetime;
	private List<Tag> tag;
	private String initationtype;
	private List<Organisation> parties ;
	private Organisation buyer;
	private Planning planing;
	private Tender tender;
	private List<Award> awards;
	private String language;
	private List<RelatedProcess> relatedprocesses;
	//endregion
	//region Getters & Setters
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public DateTime getDatetime() {
		return datetime;
	}

	public void setDatetime(DateTime datetime) {
		this.datetime = datetime;
	}

	public List<Tag> getTag() {
		return tag;
	}

	public void setTag(List<Tag> tag) {
		this.tag = tag;
	}

	public String getInitationtype() {
		return initationtype;
	}

	public void setInitationtype(String initationtype) {
		this.initationtype = initationtype;
	}

	public List<Organisation> getParties() {
		return parties;
	}

	public void setParties(List<Organisation> parties) {
		this.parties = parties;
	}

	public Organisation getBuyer() {
		return buyer;
	}

	public void setBuyer(Organisation buyer) {
		this.buyer = buyer;
	}

	public Planning getPlaning() {
		return planing;
	}

	public void setPlaning(Planning planing) {
		this.planing = planing;
	}

	public Tender getTender() {
		return tender;
	}

	public void setTender(Tender tender) {
		this.tender = tender;
	}

	public List<Award> getAwards() {
		return awards;
	}

	public void setAwards(List<Award> awards) {
		this.awards = awards;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<RelatedProcess> getRelatedprocesses() {
		return relatedprocesses;
	}

	public void setRelatedprocesses(List<RelatedProcess> relatedprocesses) {
		this.relatedprocesses = relatedprocesses;
	}

	//endregion
	//region Contructors
	public ContractingProcess(BigDecimal id) {
		this.setId("ocds-" + id + "-ContractingProcess");
		
	}
	public  ContractingProcess(Contrato con){
		this.setId("ocds-" + con.getId() + "-ContractingProcess");
		List<Tag> relaseTag=new ArrayList<Tag>();
		List<Award> awards=new ArrayList<Award>();
		List<RelatedProcess> relatedProcess=new ArrayList<RelatedProcess>();
		switch(con.getStatus().getId()){
			case 0: relaseTag.add(new Tag("Licitación"));this.setTag(relaseTag);break;
			case 1: relaseTag.add(new Tag("Pendiente adjudicar"));this.setTag(relaseTag);break;
			case 5:relaseTag.add(new Tag("Adjudicado"));this.setTag(relaseTag);break;
			case 6:relaseTag.add(new Tag("Formalizado"));this.setTag(relaseTag);break;
			case 10: relaseTag.add(new Tag("Cancelado"));this.setTag(relaseTag);break;
		}
		this.setDatetime(new DateTime(con.getPubDate()));
		this.setInitationtype("Tender");

		List<Organisation> listaOrganizaciones=new ArrayList<Organisation>();
		listaOrganizaciones.add(new Organisation(con.getEntity()));
		if(con.getOrganoContratante()!=null)
			listaOrganizaciones.add(new Organisation(con.getOrganoContratante()));
		listaOrganizaciones.add(new Organisation(con.getServicio()));
		this.setParties(listaOrganizaciones);
		this.setBuyer(new Organisation(con.getServicio()));
		this.setPlaning(new Planning());
		this.setTender(new Tender(con));
		if(con.getOfertas().size()>0){
			for (Oferta ofer:con.getOfertas()) {
				if(ofer.getGanador()){
					awards.add(new Award(ofer));
				}
			}
			this.setAwards(awards);
		}
		this.setLanguage("ES");
		relatedProcess.add(new RelatedProcess(con.getId(),con.getTitle()));
		this.setRelatedprocesses(relatedProcess);

	}
	//endregion
	
}
