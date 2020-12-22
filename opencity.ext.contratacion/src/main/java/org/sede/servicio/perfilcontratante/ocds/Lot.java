package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Lote;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by documentacionweb on 26/02/20.
 */
@XmlRootElement(name = "LotsOcds")
@ResultsOnly(xmlroot = "LotsOcds")
public class Lot {

    //region atributtes
    private String id;
    private String title;
    private String description;
    private String status;
    private Value value;
    //endregion
    //region Contructors
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

    public void setStatus(Lote lot) {
        switch(lot.getStatus().getId()){

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
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    //endregion
    //region Overrides


    public Lot() {
    }
    public Lot(Lote lot,int i) {
        this.id="lot-1xraxc-"+lot.getId();
        this.title="Lote "+i;
        this.description=lot.getDescription();
        this.value=new Value(lot.getImporteLicitacionSinIVA(),"EUR");
        this.setStatus(lot);
    }

    @Override
    public String toString() {
        return "Lot[" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", value=" + value +
                ']';
    }

    //endregion
}
