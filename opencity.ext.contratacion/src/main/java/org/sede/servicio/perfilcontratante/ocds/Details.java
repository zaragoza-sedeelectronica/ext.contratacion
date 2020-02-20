package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AdressOcds")
@ResultsOnly(xmlroot = "AdressOcds")
public class Details {
    public Details(){}
}
