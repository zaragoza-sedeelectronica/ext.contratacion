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
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Lote;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;


@XmlRootElement(name = "DocumentOcds")
@ResultsOnly(xmlroot = "DocumentOcds")
public class Document {
    //region Atributtes
    private String id;
    private String documentType;
    private String title;
    private String description;
    private String url;
    private String datePublished;
    private String dateModified;
    private String format;
    private String language;
    private List<String> relatedLots=new ArrayList<String>();
    //endregion
    //region Getters &Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(String datePublished) {
        this.datePublished = datePublished;
    }

    public String getDateModified() {
        return dateModified;
    }

    public void setDateModified(String dateModified) {
        this.dateModified = dateModified;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public List<String> getRelatedLots() {
        return relatedLots;
    }

    public void setRelatedLots(List<String> relatedLots) {
        this.relatedLots = relatedLots;
    }

    //endregion
    //region Contructs
    public Document(){}
    public Document(Anuncio anun){
        if(anun.getContrato().getLotes().size()>0){
            List<String> lotes=new ArrayList<String>();
            for (Lote item:anun.getContrato().getLotes()) {
                this.relatedLots.add("lot-"+item.getId());
            }

        }
        this.id=anun.getId()+"";
        if(anun.getType().equals(31)|| anun.getType().equals(31) || anun.getType().equals(31))
            this.documentType="enquiryResponses";
        else
            this.documentType="notice";
        this.description=anun.getType().getTitle();
        this.datePublished=ConvertDate.date2String(anun.getPubDate(),ConvertDate.ISO8601_FORMAT);

        this.title=anun.getTitle();
        if(anun.getSello()!=null) {
            this.setUrl("https://www.zaragoza.es"+anun.getSelladoTiempo());
        }else{
            if((anun.getUri()!=null)) {
                this.setUrl("https://www.zaragoza.es" + anun.getUri());
            }else{
                this.setUrl("https://www.zaragoza.es/sede/servicio/contratacion-publica/anuncio/"+anun.getId());
            }
        }
        switch(Integer.valueOf(anun.getType().getId().toString())){
            case 3:
            case 4:
            case 8:this.format="application/pdf";break;
            default:this.format="text/html";break;
        }
        this.language="es";

    }
    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Document[" +
                "id='" + id + '\'' +
                ", documentType='" + documentType + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", uri='" + url + '\'' +
                ", datePublished=" + datePublished +
                ", dateModified=" + dateModified +
                ", format='" + format + '\'' +
                ", language='" + language + '\'' +
                ", relatedLots=" + relatedLots +
                ']';
    }


    //endregion
}
