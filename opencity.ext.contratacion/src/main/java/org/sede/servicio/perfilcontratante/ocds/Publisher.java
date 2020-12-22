package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PublisherOcds")
@ResultsOnly(xmlroot = "PublisherOcds")
public class Publisher {
        //region Atributtes
        private String scheme;
        private String uid;
        private String name;
        private String uri;




        //endregion
        //region Getters & Setters


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    //endregion
    //region Contructors
        public Publisher(){}
    //endregion
}

