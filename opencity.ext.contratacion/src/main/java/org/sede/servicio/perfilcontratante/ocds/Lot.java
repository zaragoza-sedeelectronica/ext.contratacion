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
import org.sede.servicio.perfilcontratante.entity.Lote;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by documentacionweb on 26/02/20.
 */
@XmlRootElement(name = "LotsOcds")
@ResultsOnly(xmlroot = "LotsOcds")
public class Lot {

    //region atributtes
    private String id;
    private String title;
    private String description;
    private String status;
    private Value value;
    //endregion
    //region Contructors
    //endregion
    //region Getters & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Lote lot) {
        switch(lot.getStatus().getId()){

            case 1: this.status="pending";break;
            case 5:
            case 6:this.status="active";break;
            case 4:
            case 7:
            case 8:
            case 10:
            case 11:this.status="cancelled";break;
            default:this.status="active";break;
        }
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    //endregion
    //region Overrides


    public Lot() {
    }
    public Lot(Lote lot,int i) {
        this.id="lot-1xraxc-"+lot.getId();
        this.title="Lote "+i;
        this.description=lot.getDescription();
        this.value=new Value(lot.getImporteLicitacionSinIVA(),"EUR");
        this.setStatus(lot);
    }

    @Override
    public String toString() {
        return "Lot[" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status='" + status + '\'' +
                ", value=" + value +
                ']';
    }

    //endregion
}
