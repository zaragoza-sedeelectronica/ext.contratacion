package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RolesOcds")
@ResultsOnly(xmlroot = "RolesOcds")
public class Roles {
    //region Atributtes
    private String rol;
    //endregion
    //region Getters & Setters

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    //endregion
    //region Constructor
    public Roles(){}
    public Roles(String rol){
        this.rol=rol;
    }
    //endregion





}
