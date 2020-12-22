package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "Contracting-process")
@ResultsOnly(xmlroot = "Contracting-process")
public class ContractingProcess {

	//region Atributes
	private String ocid;
	private String id;
	private String date;
	private List<String> tag;
	private String initiationType;
	private List<Organisation> parties =new ArrayList<Organisation>();
	private Organisation buyer;
	private Planning planning;
	private Tender tender;
	private List<Award> awards=new ArrayList<Award>();
	private List<Contract> contracts=new ArrayList<Contract>();
	private String language;
	private List<RelatedProcess> relatedprocesses=new ArrayList<RelatedProcess>();
	//endregion
	//region Getters & Setters

	public List<Contract> getContracts() {
		return contracts;
	}

	public void setContracts(List<Contract> contracts) {
		this.contracts = contracts;
	}

	public String getOcid() {
		return ocid;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setOcid(String ocid) {
		this.ocid = ocid;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String datetime) {
		this.date = datetime;
	}

	public List<String>  getTag() {
		return tag;
	}

	public void setTag(List<String>  tag) {
		this.tag = tag;
	}

	public String getInitiationType() {
		return initiationType;
	}

	public void setInitiationType(String initiationType) {
		this.initiationType = initiationType;
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
		return planning;
	}

	public void setPlaning(Planning planing) {
		this.planning = planing;
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
	public ContractingProcess(BigDecimal id,Contrato con) {
		this.setOcid("ocds-1xraxc-" + id + "-ContractingProcess"+"-"+ ConvertDate.date2String(con.getCreationDate(),ConvertDate.ISO8601_FORMAT));
		this.setId(id+ "-ContractingProcess");
		
	}
	public  ContractingProcess(Contrato con,int status){
		this.setOcid("ocds-1xraxc-" + con.getId() + "-ContractingProcess");
		this.setId(con.getId() + "-ContractingProcess"+"-"+ ConvertDate.date2String(con.getCreationDate(),ConvertDate.ISO8601_FORMAT));
		List<Tag> relaseTag=new ArrayList<Tag>();

		List<RelatedProcess> relatedProcess=new ArrayList<RelatedProcess>();
		List<String> tag=new ArrayList<String>(0);
		switch(con.getStatus().getId()){
			case 0:  tag.add("tender");this.setTag(tag);break;
			case 1: tag.add("tender");this.setTag(tag);break;
			case 5:tag.add("award");this.setTag(tag);break;
			case 6:tag.add("contract");this.setTag(tag);break;
			case 10:tag.add("tenderCancellation");this.setTag(tag);break;
		}

		this.setDate(ConvertDate.date2String(con.getPubDate(),ConvertDate.ISO8601_FORMAT));
		this.setInitiationType("tender");

		List<Organisation> listaOrganizaciones=new ArrayList<Organisation>();
		listaOrganizaciones.add(new Organisation(con.getEntity()));
		if(con.getOrganoContratante()!=null)
			listaOrganizaciones.add(new Organisation(con.getOrganoContratante()));
		if(con.getServicio()!=null)
			listaOrganizaciones.add(new Organisation(con.getServicio()));
		this.setParties(listaOrganizaciones);
		if(con.getServicio()!=null)this.setBuyer(new Organisation(con.getServicio()));
		//this.setPlaning(new Planning());

			this.setTender(new Tender(con));

		if(status==2){
			List<Award> awards=new ArrayList<Award>();
			if(con.getOfertas()!=null) {
				if (con.getOfertas().size() > 0) {
					for (Oferta ofer : con.getOfertas()) {
						if (ofer.getGanador()) {
							awards.add(new Award(ofer));
						}
						else{
							this.getParties().add(new Organisation(ofer.getEmpresa(),ofer.getContrato()));
						}
					}
					this.setAwards(awards);
				}
			}
		}
		if(status==3 || (con.getProcedimiento().getId().toString().equals("10") && con.getStatus().getId()==5)){
			List<Contract> contrat=new ArrayList<Contract>();
			contrat.add(new Contract(con));
			this.setContracts(contrat);

		}
		this.setLanguage("es");
		if(con.getPadre()!=null) {
			relatedProcess.add(new RelatedProcess(con.getPadre().getId(), con.getPadre().getTitle()));
		}
		this.setRelatedprocesses(relatedProcess);

	}
	//endregion
	
}
