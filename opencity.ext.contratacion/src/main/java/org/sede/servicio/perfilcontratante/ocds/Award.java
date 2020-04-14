package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.*;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "AwardOcds")
@ResultsOnly(xmlroot = "AwardOcds")
public class Award {
    //region Atributtes
        private String id;
        private String title;
        private String description;
        private String status;
        private DateTime dateTime;
        private Value value;
        private List<Organisation> suppliers=new ArrayList<Organisation>();
        private List<Item> items=new ArrayList<Item>();
        private Period contractPeriod;
        private List<Document> documents=new ArrayList<Document>();
        private List<Amendment> amendments=new ArrayList<Amendment>();
        private List<String> relatedLots=new ArrayList<String>();


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

    public void setStatus(String status) {
        this.status = status;
    }

    public DateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public List<Organisation> getSuppliers() {
        return suppliers;
    }

    public void setSuppliers(List<Organisation> suppliers) {
        this.suppliers = suppliers;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public Period getContractPeriod() {
        return contractPeriod;
    }

    public void setContractPeriod(Period contractPeriod) {
        this.contractPeriod = contractPeriod;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Amendment> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<Amendment> amendments) {
        this.amendments = amendments;
    }

    public List<String> getRelatedLots() {
        return relatedLots;
    }

    public void setRelatedLots(List<String> relatedLots) {
        this.relatedLots = relatedLots;
    }

    //endregion
    //region Contructors
    public Award(BigDecimal id) {
        this.setId("ocds-"+id+"-award");
    }
    public Award(Oferta ofer){
        List<Organisation> supliers=new ArrayList<Organisation>();
        this.setId("ocds-"+ofer.getId()+"-award");
        this.setTitle("Award of contract to "+ofer.getContrato().getTitle());
        this.setDescription(ofer.getEmpresa().getNombre()+" has been awarded the contract "+ofer.getContrato().getTitle());
        this.setStatus(estado(ofer));
        this.setDateTime(new DateTime(ofer.getFechaAdjudicacion()));
        this.setValue(new Value(ofer.getImporteSinIVA(),"EUR"));
        supliers.add(new Organisation(ofer.getEmpresa(),true));
        this.setSuppliers(supliers);
        this.setDocuments(documents);
        for (Anuncio anun:ofer.getContrato().getAnuncios()) {
            switch (Integer.valueOf(anun.getType().getId().toString())) {
                case 5:
                case 6:
                case 21:
                    this.documents.add(new Document(anun));
                    break;
                case 31:
                case 32:
                case 33:
                    this.amendments.add(new Amendment(anun));
                    break;
                default:
                    this.amendments.add(new Amendment());
                    break;
            }
        }
        for(Cpv cpv:ofer.getContrato().getCpv()) {
            this.items.add(new Item(cpv,ofer.getContrato()));
        }
        if("10".equals(ofer.getContrato().getProcedimiento().getId().toString())){
            this.contractPeriod=new Period(ofer.getFechaAdjudicacion(),Integer.valueOf(ofer.getContrato().getDuracion()!=null?ofer.getContrato().getDuracion().toString():"0"));
        }else{
            if(ofer.getFechaFormalizacion()!=null){
                this.contractPeriod=new Period(ofer.getFechaFormalizacion(),Integer.valueOf(ofer.getContrato().getDuracion().toString()));
            }else{
                this.contractPeriod=new Period(ofer.getFechaAdjudicacion(),Integer.valueOf(ofer.getContrato().getDuracion().toString()));
            }
        }
        if(ofer.getContrato().getLotes().size()>0){
            for(Lote lot:ofer.getContrato().getLotes())
            this.relatedLots.add("lot-"+lot.getId());
        }

    }
    public Award(Oferta ofer, Contrato contrato){
        this.setId("ocds-"+contrato.getId()+"-award-"+ofer.getId());
        this.setTitle("Award of contract to "+ofer.getContrato().getTitle());
        this.setDescription(ofer.getEmpresa().getNombre()+" has been awarded the contract "+contrato.getTitle());
       if(ofer.getFechaFormalizacion()!=null) {
           this.setDateTime(new DateTime(ofer.getFechaFormalizacion()));
       }else {
           this.setDateTime(new DateTime(ofer.getFechaAdjudicacion()));
       }
    }
    private String estado(Oferta ofer) {
        Boolean todoLotes=false;
        if(ofer.getLote()!=null ) {
            for (Lote itemLote:ofer.getContrato().getLotes()) {
                if(itemLote.getStatus().getId().equals(new BigDecimal("5"))||itemLote.getStatus().getId().equals(new BigDecimal("3"))||itemLote.getStatus().getId().equals(new BigDecimal("6"))){
                    todoLotes=true;
                }else {
                    todoLotes=false;
                }

            }
            if(todoLotes)
                return "Active";
            else
                return "Unsuccessful";
        }else{
        if(ofer.getContrato().getStatus().getId().equals(new Integer("6") ) || ofer.getContrato().getStatus().getId().equals(new Integer("5")))
            return "Active";
        if(ofer.getContrato().getStatus().getId().equals(new Integer("1")))
            return "Pending";
        if(ofer.getContrato().getStatus().getId().equals(new Integer("10")))
            return "Cancelled";


        }

        return "Pending";
    }
    //endregion

}
