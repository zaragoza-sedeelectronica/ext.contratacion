package org.sede.servicio.perfilcontratante.ocds;


import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "ContractOcds")
@ResultsOnly(xmlroot = "ContractOcds")
public class Contract {
    //region Atributtes
    private String id;
    private String title;
    private Award award;
    private String description;
    private String status;
    private Period period;
    private Value value;
    private List<Item> items;
    private DateTime dateSigned;
    private List<Document> documents;
    private List<Milestone> milestones;
    private List<RelatedProcess> relatedPracesses=new ArrayList<RelatedProcess>();
    private List<Amendment> amendments;
    //endregion
    //region Getteres & Setters

    public List<RelatedProcess> getRelatedPracesses() {
        return relatedPracesses;
    }

    public void setRelatedPracesses(List<RelatedProcess> relatedPracesses) {
        this.relatedPracesses = relatedPracesses;
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

    public Award getAward() {
        return award;
    }

    public void setAward(Award award) {
        this.award = award;
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

            case 1: this.status="Pending";break;
            case 5:
            case 6:this.status="Active";break;
            case 4:
            case 7:
            case 8:
            case 10:
            case 11:this.status="Cancelado";break;
            default:this.status="Active";break;
        }
        for (Oferta ofer:con.getOfertas()) {
            if(ofer.getGanador()){
                if(new Date().compareTo(sumarRestarDiasFecha(ofer.getFechaFormalizacion(),con.getDuracion()==null?0:con.getDuracion().intValue()))>0){
                    this.status="Terminated";
                }
            }
        }
    }


    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public DateTime getDateSigned() {
        return dateSigned;
    }

    public void setDateSigned(DateTime dateSigned) {
        this.dateSigned = dateSigned;
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

    //endregion
    //region Constructors
    public Contract(BigDecimal id, String title, String objeto){
        this.setId("ocds-"+id.toString()+"-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado ");
    }
    public Contract(BigDecimal id,String title,String empresa,String entidad){
        this.setId("ocds-"+id.toString()+"-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado entre "+entidad+" y "+empresa);
    }
    public Contract(BigDecimal id,String title,String empresa,String entidad,Contrato con){
        this.setId("ocds-"+id.toString()+"-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado entre "+entidad+" y "+empresa);
        this.setStatus(con);
    }
    public Contract(Contrato con){
        List<Item>items=new ArrayList<Item>();
        List<Document>documents=new ArrayList<Document>();
        List<Amendment>amendments=new ArrayList<Amendment>();
        List<Milestone>milestones=new ArrayList<Milestone>();
        List<RelatedProcess>relatedProcesses=new ArrayList<RelatedProcess>();
        this.setId("ocds-"+con.getId().toString()+"-contract");
        for (Oferta ofer:con.getOfertas()) {
            if(ofer.getGanador()) {
                this.setAward(new Award(ofer.getId()));
                this.setTitle(con.getTitle());
                this.setDescription("Contrato firmado entre "+con.getEntity().getTitle() + " y "+ofer.getEmpresa().getNombre());
                this.setStatus(con);
                this.setPeriod(new Period(ofer.getFechaFormalizacion(),sumarRestarDiasFecha(ofer.getFechaFormalizacion(),con.getDuracion().intValue())));
                this.setValue(new Value(con.getCanon()?ofer.getImporteSinIVA().negate():ofer.getImporteSinIVA(),"EUR"));
                this.setItems(items);
                this.setDateSigned(new DateTime(ofer.getFechaFormalizacion()));
            }
            this.setDocuments(documents);
            this.setRelatedPracesses(relatedProcesses);
            this.setMilestones(milestones);
            this.setAmendments(amendments);
        }



    }
    //endregion

    public Date sumarRestarDiasFecha(Date fecha, int dias){
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();

    }
}
