/** Copyright (C) 2020 Oficina Técnica de Participación, Transparenica y Gobierno Abierto del Ayuntamiento de Zaragoza
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
import org.sede.servicio.perfilcontratante.entity.Cpv;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "ItemOcds")
@ResultsOnly(xmlroot = "ItemOcds")
public class Item {
    //region Atributtes
    private String id;
    private String description;
    private CpvOcds classification;
    private List<CpvOcds> additionalClassifications=new ArrayList<CpvOcds>();
    private Double quantity;
    private Unit unit;


    //endregion
    //region Getters & Setters


    public List<CpvOcds> getAdditionalClassifications() {
        return additionalClassifications;
    }

    public void setAdditionalClassifications(List<CpvOcds> additionalClassifications) {
        this.additionalClassifications = additionalClassifications;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public CpvOcds getClassification() {
        return classification;
    }

    public void setClassification(CpvOcds classification) {
        this.classification = classification;
    }

    public Double getQuantity() {
        return quantity;
    }

    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }

    public Unit getUnits() {
        return unit;
    }

    public void setUnits(Unit unit) {
        this.unit = unit;
    }

    //endregion
    //region Constructors
    public Item(Cpv cpv, Contrato con){
        this.id=con.getId()+"-item-"+cpv.getId();
        this.description=con.getTitle();
        this.classification=new CpvOcds(cpv);
        this.quantity=0.0;
        //this.unit=new Unit(con);
    }
    public Item(Contrato con,Boolean additionalCpv){
        int i=0;
        for(Cpv cpv:con.getCpv()){
            if(i==0){
                this.id=con.getId()+"-item"+cpv.getId();
                this.description=con.getTitle();
                this.classification=new CpvOcds(cpv);
                this.quantity=0.0;
                i++;

            }else{
                this.getAdditionalClassifications().add(new CpvOcds(cpv));
            }
        }
    }

    //endregion
    //region Overrides
    //endregion
}
