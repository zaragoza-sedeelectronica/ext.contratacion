/* Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
 *
 * Este fichero es parte del "Modulo de Contratación Pública".
 *
 * "Modulo de Contratación Pública" es un software libre; usted puede utilizar esta obra respetando la licencia GNU General Public License, versión 3 o posterior, publicada por Free Software Foundation
 *
 * Salvo cuando lo exija la legislación aplicable o se acuerde por escrito, el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL», SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
 * Véase la Licencia en el idioma concreto que rige los permisos y limitaciones que establece la Licencia.
 *
 * Para más información, puede contactar con los autores en: gobiernoabierto@zaragoza.es, sedelectronica@zaragoza.es*/
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
        this.setId("ocds-1xraxc-"+con.getId()+"-ContractingProcess");
        this.setTitle(con.getTitle());
        this.setIdentifier(con.getId().toString());
        this.setScheme("ocid-1xraxc-"+con.getId());
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
