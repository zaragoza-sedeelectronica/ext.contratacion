package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Anuncio;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "DocumentOcds")
@ResultsOnly(xmlroot = "DocumentOcds")
public class Document {
    //region Atributtes
    private String id;
    private String documentType;
    private String title;
    private String description;
    private String uri;
    private DateTime datePublished;
    private DateTime dateModified;
    private String format;
    private String language;
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

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public DateTime getDatePublished() {
        return datePublished;
    }

    public void setDatePublished(DateTime datePublished) {
        this.datePublished = datePublished;
    }

    public DateTime getDateModified() {
        return dateModified;
    }

    public void setDateModified(DateTime dateModified) {
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

    //endregion
    //region Contructs
    public Document(){}
    public Document(Anuncio anun){
        this.id=anun.getId()+"";
        this.title=anun.getTitle();
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
                ", uri='" + uri + '\'' +
                ", datePublished=" + datePublished +
                ", dateModified=" + dateModified +
                ", format='" + format + '\'' +
                ", language='" + language + '\'' +
                ']';
    }

    //endregion
}
