package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "ContactPointOcds")
@ResultsOnly(xmlroot = "ContactPointOcds")
public class ContactPoint {
    public ContactPoint(){}
}
