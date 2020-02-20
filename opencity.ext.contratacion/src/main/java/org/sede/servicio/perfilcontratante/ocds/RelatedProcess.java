package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Contrato;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "RelatedProcessOcds")
@ResultsOnly(xmlroot = "RelatedProcessOcds")
public class RelatedProcess {
    //region Atributtes
    private String id;
    private List<String> relationship;
    private String title;
    private String scheme;
    private String identifier;
    private String uri;
    //endregion
    //region Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<String> getRelationship() {
        return relationship;
    }

    public void setRelationship(List<String> relationship) {
        this.relationship = relationship;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //endregion
    //region Contructors
    public RelatedProcess(){}
    public RelatedProcess(Contrato con){
        List<String> relationShip=new ArrayList<String>();
        this.setId("ocds-"+con.getId()+"-ContractingProcess");
        this.setTitle(con.getTitle());
        this.setIdentifier(con.getId().toString());
        this.setScheme("ocid-"+con.getId());
        if(con.getDerivadoAcuerdoMarco()) {
            relationShip.add("Framework agreement procedure first stage");
        }else{
            relationShip.add("");
        }
        this.setRelationship(relationShip);



    }
    public RelatedProcess(BigDecimal id,String title){
        this.setId("ocds-"+id+"-ContractingProcess");
        this.setTitle(title);
    }

    //endregion
}
