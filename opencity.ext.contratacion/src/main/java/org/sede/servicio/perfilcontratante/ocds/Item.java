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
    private List<CpvOcds> classification=new ArrayList<CpvOcds>(0);
    private Double quantity;
    private Unit unit;


    //endregion
    //region Getters & Setters

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

    public List<CpvOcds> getClassification() {
        return classification;
    }

    public void setClassification(List<CpvOcds> classification) {
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
        this.classification.add(new CpvOcds(cpv));
        this.quantity=0.0;
        //this.unit=new Unit(con);
    }

    //endregion
    //region Overrides
    //endregion
}
