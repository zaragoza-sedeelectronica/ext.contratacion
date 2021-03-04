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
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Anuncio;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;

@XmlRootElement(name = "AmendmentOcds")
@ResultsOnly(xmlroot = "AmendmentOcds")
public class Amendment {
    //region Atributtes
    private String id;
    private Date date;
    private String rationale ;
    private String description;
    private String amendsRelaseID;
    private String realeaseID;
    private ArrayList<String> changes=new ArrayList<String>();
    //endregion
    //region Getters && Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return ConvertDate.date2String(date,ConvertDate.ISO8601_FORMAT);
    }

    public void setDate(Date dateTime) {
        this.date = dateTime;
    }

    public String getRationale() {
        return rationale;
    }

    public void setRationale(String rationale) {
        this.rationale = rationale;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAmendsRelaseID() {
        return amendsRelaseID;
    }

    public void setAmendsRelaseID(String amendsRelaseID) {
        this.amendsRelaseID = amendsRelaseID;
    }

    public String getRealeaseID() {
        return realeaseID;
    }

    public void setRealeaseID(String realeaseID) {
        this.realeaseID = realeaseID;
    }

    public ArrayList<String> getChanges() {
        return changes;
    }

    public void setChanges(ArrayList<String> changes) {
        this.changes = changes;
    }

    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Amendment{" +
                "id=" + id +
                ", dateTime=" + date +
                ", rationale='" + rationale + '\'' +
                ", description='" + description + '\'' +
                ", amendsRelaseID='" + amendsRelaseID + '\'' +
                ", realeaseID='" + realeaseID + '\'' +
                ", changes=" + changes +
                '}';
    }
    //endregion
    //region Contructors

    public Amendment() {}
    public Amendment(Anuncio anuncio) {
        this.setId(anuncio.getId()+"-amanment");
        this.setDate(anuncio.getCreationDate());
        this.setRationale(anuncio.getTitle());
        this.setDescription(anuncio.getDescription());
    }

    //endregion

}
