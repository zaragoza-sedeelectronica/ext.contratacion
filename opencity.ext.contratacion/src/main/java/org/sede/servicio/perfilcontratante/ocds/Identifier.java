package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.entity.Empresa;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IdentifierOcds")
@ResultsOnly(xmlroot = "IdentifierOcds")
public class Identifier {
    //region Atributtes
    private String id;
    private String legalName;
    private String scheme;
    private String uri;
    //endregion
    //region Getters &s Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegalname() {
        return legalName;
    }

    public void setLegalname(String legalName) {
        this.legalName = legalName;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //endregion
    //region Contructors


    //endregion
}
