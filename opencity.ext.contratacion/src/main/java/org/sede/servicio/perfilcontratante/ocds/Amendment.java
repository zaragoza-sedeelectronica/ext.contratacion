package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Anuncio;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;

@XmlRootElement(name = "AmendmentOcds")
@ResultsOnly(xmlroot = "AmendmentOcds")
public class Amendment {
    //region Atributtes
    private String id;
    private DateTime date;
    private String rationale ;
    private String description;
    private String amendsRelaseID;
    private String realeaseID;
    private ArrayList<String> changes=new ArrayList<String>();
    //endregion
    //region Getters && Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DateTime getDate() {
        return date;
    }

    public void setDate(DateTime dateTime) {
        this.date = dateTime;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmendsRelaseID() {
        return amendsRelaseID;
    }

    public void setAmendsRelaseID(String amendsRelaseID) {
        this.amendsRelaseID = amendsRelaseID;
    }

    public String getRealeaseID() {
        return realeaseID;
    }

    public void setRealeaseID(String realeaseID) {
        this.realeaseID = realeaseID;
    }

    public ArrayList<String> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<String> changes) {
        this.changes = changes;
    }

    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Amendment{" +
                "id=" + id +
                ", dateTime=" + date +
                ", rationale='" + rationale + '\'' +
                ", description='" + description + '\'' +
                ", amendsRelaseID='" + amendsRelaseID + '\'' +
                ", realeaseID='" + realeaseID + '\'' +
                ", changes=" + changes +
                '}';
    }
    //endregion
    //region Contructors

    public Amendment() {}
    public Amendment(Anuncio anuncio) {
        this.setId(anuncio.getId()+"amanment");
        this.setDate(new DateTime(anuncio.getPubDate()));
        this.setRationale(anuncio.getTitle());
        this.setDescription(anuncio.getDescription());
    }

    //endregion

}
