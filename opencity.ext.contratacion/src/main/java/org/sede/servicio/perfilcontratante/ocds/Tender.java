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
package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "TenderOcds")
@ResultsOnly(xmlroot = "TenderOcds")
public class Tender {
    //region Atributtes
        private String id;
        private String title;
        private String description;
        private String status;
        private Organisation procuringEntity;
        private List < Item > items = new ArrayList < Item > ();
        private Value value;
        private Value minValue;
        private String procurementMethod;
        private String procurementMethodDetails;
        private String procurementMethodRationale;
        private String mainProcurementCategory;
        private List < String > additionalProcurementCategories = new ArrayList <String>();
        private String awardCriteria;
        private String awardCriteriaDetails;
        private List < String > submissionMethod = new ArrayList <String>();
        private String submissionMethodDetails;
        private Period tenderPeriod;
        private Period enquiryPeriod;
        private Boolean hasEnquiries;
        private String eligibilityCriteria;
        private Period awardPeriod;
        private Period contractPeriod;
        private BigDecimal numberOfTenderers;
        private List< Organisation > tenderers = new ArrayList<Organisation>();
        private List <Document> documents = new ArrayList <Document> ();
        private List <Milestone> milestones = new ArrayList <Milestone> ();
        private List <Amendment> amendments = new ArrayList < Amendment > ();
        private List<Lot> lots=new ArrayList<Lot>();

    //endregion
    //region Getters & Setters
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Contrato con) {
        switch(con.getStatus().getId()){
            case 0:
            case 1:
            case 2:
                this.status="active";
                break;
            case 10:
                this.status="cancelled";
                break;
            case 3:
            case 5:
            case 6:
                this.status="complete";
                break;
            case 7:
            case 4:
            case 8:
            case 11:
                this.status="unsuccessful";
                break;
            default:
                this.status="withdrawn";
                break;
        }

    }

    public Organisation getProcuringEntity() {
        return procuringEntity;
    }

    public void setProcuringEntity(Organisation procuringEntity) {
        this.procuringEntity = procuringEntity;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public Value getMinValue() {
        return minValue;
    }

    public void setMinValue(Value minValue) {
        this.minValue = minValue;
    }

    public String getProcurementMethod() {
        return procurementMethod;
    }

    public void setProcurementMethod(String procurementMethod) {
        this.procurementMethod = procurementMethod;
    }

    public String getProcurementMethodDetails() {
        return procurementMethodDetails;
    }

    public void setProcurementMethodDetails(String procurementMethodDetails) {
        this.procurementMethodDetails = procurementMethodDetails;
    }

    public String getProcurementMethodRationale() {
        return procurementMethodRationale;
    }

    public void setProcurementMethodRationale(String procurementMethodRationale) {
        this.procurementMethodRationale = procurementMethodRationale;
    }

    public String getMainProcurementCategory() {
        return mainProcurementCategory;
    }

    public void setMainProcurementCategory(BigDecimal mainProcurementCategory) {
        if(mainProcurementCategory.equals(new BigDecimal(2.0))||mainProcurementCategory.equals(new BigDecimal(8.0))) {
            this.mainProcurementCategory = "services";
        }
        else if(mainProcurementCategory.equals(new BigDecimal(3.0))) {
            this.mainProcurementCategory = "goods";
        }else if(mainProcurementCategory.equals(new BigDecimal(1.0)) || mainProcurementCategory.equals(new BigDecimal(7.0))) {
            this.mainProcurementCategory = "works";
        }else {
            this.mainProcurementCategory = "works";
        }
    }

    public List<String> getAdditionalProcurementCategories() {
        return additionalProcurementCategories;
    }

    public void setAdditionalProcurementCategories(ArrayList<String> additionalProcurementCategories) {
        this.additionalProcurementCategories = additionalProcurementCategories;
    }

    public String getAwardCriteria() {
        return awardCriteria;
    }

    public void setAwardCriteria(Contrato con) {
        if(con.getProcedimiento().getId().intValue()==7){
            this.awardCriteria="qualityOnly";
        }else {
            if(!con.getCanon())
                this.awardCriteria = "priceOnly";
            else
                this.awardCriteria="ratedCriteria";
        }
    }

    public String getAwardCriteriaDetails() {
        return awardCriteriaDetails;
    }

    public void setAwardCriteriaDetails(String awardCriteriaDetails) {
        this.awardCriteriaDetails = awardCriteriaDetails;
    }

    public List<String> getSubmissionMethod() {
        return submissionMethod;
    }

    public void setSubmissionMethod(List<String> submissionMethod) {
        this.submissionMethod = submissionMethod;
    }

    public String getSubmissionMethodDetails() {
        return submissionMethodDetails;
    }

    public void setSubmissionMethodDetails(String submissionMethodDetails) {
        this.submissionMethodDetails = submissionMethodDetails;
    }

    public Period getTenderPeriod() {
        return tenderPeriod;
    }

    public void setTenderPeriod(Period tenderPeriod) {
        this.tenderPeriod = tenderPeriod;
    }

    public Period getEnquiryPeriod() {
        return enquiryPeriod;
    }

    public void setEnquiryPeriod(Period enquiryPeriod) {
        this.enquiryPeriod = enquiryPeriod;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setAdditionalProcurementCategories(List<String> additionalProcurementCategories) {
        this.additionalProcurementCategories = additionalProcurementCategories;
    }

    public void setAwardCriteria(String awardCriteria) {
        this.awardCriteria = awardCriteria;
    }

    public Boolean getHasEnquiries() {
        return hasEnquiries;
    }

    public void setHasEnquiries(Boolean hasEnquiries) {
        this.hasEnquiries = hasEnquiries;
    }

    public String getEligibilityCriteria() {
        return eligibilityCriteria;
    }

    public void setEligibilityCriteria(String eligibilityCriteria) {
        this.eligibilityCriteria = eligibilityCriteria;
    }

    public Period getAwardPeriod() {
        return awardPeriod;
    }

    public void setAwardPeriod(Period awardPeriod) {
        this.awardPeriod = awardPeriod;
    }

    public Period getContractPeriod() {
        return contractPeriod;
    }

    public void setContractPeriod(Period contractPeriod) {

        this.contractPeriod = contractPeriod;
    }

    public BigDecimal getNumberOfTenderers() {
        return numberOfTenderers;
    }

    public void setNumberOfTenderers(BigDecimal numberOfTenderers) {
        this.numberOfTenderers = numberOfTenderers;
    }

    public List<Organisation> getTenderers() {
        return tenderers;
    }

    public void setTenderers(List<Organisation> tenderers) {
        this.tenderers = tenderers;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public List<Amendment> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<Amendment> amendments) {
        this.amendments = amendments;
    }

    public List<Lot> getLots() {
        return lots;
    }

    public void setLots(List<Lot> lots) {
        this.lots = lots;
    }

    //endregion
    //region Constructors
    public Tender(BigDecimal id,String title) {
        this.setId(id+"-tender");
        this.setTitle(title);
        this.setDescription("Licitacion del contrato "+title);
    }
    public Tender(Contrato con) {
        Boolean siEnquieres=false;
        List<Organisation> licitadores=new ArrayList<Organisation>();
        List<Milestone> milestones=new ArrayList<Milestone>();
        this.setId(con.getId()+"-tender");
        this.setTitle(con.getTitle());
        if(con.getObjeto()!=null) {
            this.setDescription(con.getObjeto());
        }else{
            this.setDescription("Licitacion del contrato "+con.getTitle());
        }
        this.setStatus(con);
        this.setProcuringEntity(new Organisation(con.getEntity()));
        if(con.getCpv().size()!=0) {
            if (con.getCpv().size() == 1) {
                for (Cpv cpv : con.getCpv()) {
                    this.items.add(new Item(cpv, con));
                }
            } else {
                this.items.add(new Item(con, true));
            }
        }
        this.setValue(new Value(con.getValorEstimado()==null?con.getImporteSinIVA():con.getValorEstimado(),"EUR"));
        if(!con.getCanon())
            this.setMinValue(new Value(con.getImporteSinIVA()==null?new BigDecimal(0):con.getImporteSinIVA(),"EUR"));
        else
            this.setMinValue(new Value(con.getImporteSinIVA().negate(),"EUR"));
        if(con.getProcedimiento()!=null){
            switch(Integer.valueOf(con.getProcedimiento().getId().toString())){
                case 1: case 5: case 8: case 10: case 12: this.setProcurementMethod("open");break;
                case 13:  this.setProcurementMethod("selective");break;
                case 2: case 7: case 3: this.setProcurementMethod("limited");break;
                case 4:case 6: case 9: case 11: case 15:this.setProcurementMethod("direct");break;

            }

        }
       // this.setProcurementMethodDetails(con.);
        //this.setProcurementMethodRationale("");
        this.setMainProcurementCategory(con.getType().getId());
       // this.setAdditionalProcurementCategories(new ArrayList<String>());
        if(con.getProcedimiento()!=null){this.setAwardCriteria(con);}
        //this.setAwardCriteriaDetails("");
        this.setSubmissionMethod(new ArrayList<String>());
        //this.setSubmissionMethodDetails("");
        for(Anuncio item:con.getAnuncios()) {
            if(!siEnquieres)
                if (item.getType().getId().equals(BigDecimal.valueOf(31.0)) || item.getType().getId().equals(BigDecimal.valueOf(32.0))||item.getType().getId().equals(BigDecimal.valueOf(32.0))) {
                    this.hasEnquiries=true;
                    siEnquieres=true;
                }else{
                    this.hasEnquiries=false;
                }
        }
        if(con.getFechaPresentacion()!=null) {
            this.setAwardPeriod(new Period(con.getFechaPresentacion(),con.getFechaPresentacion(),0));
            this.setTenderPeriod(new Period(con.getPubDate(), con.getFechaPresentacion(), Integer.valueOf(diferenciaEnDias(con.getFechaPresentacion(), con.getPubDate()))));
            this.setEnquiryPeriod(new Period(con.getPubDate(), con.getFechaPresentacion(), Integer.valueOf(diferenciaEnDias(con.getFechaPresentacion(), con.getPubDate()))));
        }
        if(con.getFechaAdjudicacion()!=null && con.getFechaPresentacion()!=null) {
            if(con.getClausulaProrroga()) {
                this.setContractPeriod(new Period(con.getFechaAdjudicacion(), fechaFinContrato(con),con.getPeriodoProrroga()));

            }else{
                this.setContractPeriod(new Period(con.getFechaAdjudicacion(), fechaFinContrato(con),0));
            }
        }else{
            for (Oferta ofer:con.getOfertas()) {
                if (ofer.getGanador()) {
                    if (ofer.getFechaFormalizacion() != null) {
                        this.setContractPeriod(new Period(ofer.getFechaFormalizacion(), fechaFinContrato(con), 0));
                    } else {
                        this.setContractPeriod(new Period(ofer.getFechaAdjudicacion(), fechaFinContrato(con), 0));
                    }
                }
            }

        }
        this.setNumberOfTenderers((BigDecimal.valueOf(con.getOfertas().size())));
        for (Oferta ofer:con.getOfertas()) {
            if(ofer.getGanador()) {
                licitadores.add(new Organisation(ofer.getEmpresa(), ofer.getGanador(), false, ofer.getId()));
            }else
                licitadores.add(new Organisation(ofer.getEmpresa(), ofer.getGanador(), true, ofer.getId()));
        }
        this.setTenderers(licitadores);
        for (Anuncio anun:con.getAnuncios()) {
            switch (Integer.valueOf(anun.getType().getId().toString())) {
                case 1:
                case 2:
                case 3:
                case 4:
                case 5:
                case 9:
                case 13:
                case 19:
                    this.documents.add(new Document(anun));
                    break;
                case 32:
                    this.amendments.add(new Amendment(anun));
                    break;
            }
        }

        this.setMilestones(milestones);
        for (Lote item:con.getLotes()) {
            int i=1;
            this.lots.add(new Lot(item,i));
            i++;
        }


    }
    public int diferenciaEnDias(Date fechaMayor, Date fechaMenor) {
        long diferenciaEn_ms = (fechaMayor.getTime() - fechaMenor.getTime());
        long horas = diferenciaEn_ms / (1000 * 60 * 60);
        return  Math.round(horas/24);
    }

    private Date fechaFinContrato(Contrato con) {
        if (con.getProcedimiento().getId().equals(new BigDecimal("10")) || con.getProcedimiento().getId().equals(new BigDecimal("15") )) {
            for (Oferta item : con.getOfertas()) {
                if (item.getGanador()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(item.getFechaAdjudicacion());
                    if(con.getDuracion()!=null)
                        calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(con.getDuracion().toString()));
                    else
                        calendar.add(Calendar.DAY_OF_YEAR, 0);
                    return calendar.getTime();
                }
            }

        } else {
            for (Oferta item : con.getOfertas()) {
                if (item.getGanador()) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(item.getFechaFormalizacion());
                    if(con.getDuracion()!=null)
                        calendar.add(Calendar.DAY_OF_YEAR, Integer.valueOf(con.getDuracion().toString()));
                    else
                        calendar.add(Calendar.DAY_OF_YEAR, 0);
                    return calendar.getTime();
                }
            }
        }
        return null;
    }
    //endregion

    //region Overrides

    @Override
    public String toString() {
        return "Tender[" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", procuringEntity=" + procuringEntity +
                ", items=" + items +
                ", lots=" + lots +
                ", value=" + value +
                ", minValue=" + minValue +
                ", procurementMethod='" + procurementMethod + '\'' +
                ", procurementMethodDetails='" + procurementMethodDetails + '\'' +
                ", procurementMethodRationale='" + procurementMethodRationale + '\'' +
                ", mainProcurementCategory='" + mainProcurementCategory + '\'' +
                ", additionalProcurementCategories=" + additionalProcurementCategories +
                ", awardCriteria='" + awardCriteria + '\'' +
                ", awardCriteriaDetails='" + awardCriteriaDetails + '\'' +
                ", submissionMethod=" + submissionMethod +
                ", submissionMethodDetails='" + submissionMethodDetails + '\'' +
                ", tenderPeriod=" + tenderPeriod +
                ", enquiryPeriod=" + enquiryPeriod +
                ", hasEnquiries=" + hasEnquiries +
                ", eligibilityCriteria='" + eligibilityCriteria + '\'' +
                ", awardPeriod=" + awardPeriod +
                ", contractPeriod=" + contractPeriod +
                ", numberOfTenderers=" + numberOfTenderers +
                ", tenderers=" + tenderers +
                ", documents=" + documents +
                ", milestones=" + milestones +
                ", amendments=" + amendments +
                ']';
    }

    //endregion

}
