package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Tag")
@ResultsOnly(xmlroot = "Tag")
public class Tag {
    //region Atributtes
    private String relasetag;
    //endregion
    //region Getters & setters

    public String getRelasetag() {
        return relasetag;
    }

    public void setRelasetag(String relasetag) {
        this.relasetag = relasetag;
    }
    //endregion
    public Tag(String tag){
        this.setRelasetag(tag);
    }
}
