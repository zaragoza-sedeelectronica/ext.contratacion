package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "RolesOcds")
@ResultsOnly(xmlroot = "RolesOcds")
public class Roles {
    public Roles(){}
}
