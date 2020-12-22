package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Cpv;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ItemOcds")
@ResultsOnly(xmlroot = "ItemOcds")
public class Item {
    //region Atributtes
    private String id;
    private String description;
    private CpvOcds classification;
    private List<CpvOcds> additionalClassifications=new ArrayList<CpvOcds>();
    private Double quantity;
    private Unit unit;


    //endregion
    //region Getters & Setters


    public List<CpvOcds> getAdditionalClassifications() {
        return additionalClassifications;
    }

    public void setAdditionalClassifications(List<CpvOcds> additionalClassifications) {
        this.additionalClassifications = additionalClassifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CpvOcds getClassification() {
        return classification;
    }

    public void setClassification(CpvOcds classification) {
        this.classification = classification;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnits() {
        return unit;
    }

    public void setUnits(Unit unit) {
        this.unit = unit;
    }

    //endregion
    //region Constructors
    public Item(Cpv cpv, Contrato con){
        this.id=con.getId()+"-item";
        this.description=con.getTitle();
        this.classification=new CpvOcds(cpv);
        this.quantity=0.0;
        //this.unit=new Unit(con);
    }
    public Item(Contrato con,Boolean additionalCpv){
        int i=0;
        for(Cpv cpv:con.getCpv()){
            if(i==0){
                this.id=con.getId()+"-item";
                this.description=con.getTitle();
                this.classification=new CpvOcds(cpv);
                this.quantity=0.0;
                i++;

            }else{
                this.getAdditionalClassifications().add(new CpvOcds(cpv));
            }
        }
    }

    //endregion
    //region Overrides
    //endregion
}
