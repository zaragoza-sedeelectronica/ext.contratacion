package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PackageOcds")
@ResultsOnly(xmlroot = "PackageOcds")
public class PackageOcds {
        //region Atributtes
            private String uri;
            private Publisher publisher;
            private String publishedDate;
            private String license;
            private String publicationPolicy;
            private String version;
            private List<String> packages=new ArrayList<String>(0);
            private List<String> extensions=new ArrayList<String>();
            private Records records;
            private List<ContractingProcess> releases=new ArrayList<ContractingProcess>();

        //endregion
        //region Getters & Setters


    public List<ContractingProcess> getReleases() {
        return releases;
    }

    public void setReleases(List<ContractingProcess> releases) {
        this.releases = releases;
    }

    public List<String> getPackages() {
        return packages;
    }

    public void setPackages(List<String> packages) {
        this.packages = packages;
    }

    public String getPublicationPolicy() {
        return "https://www.zaragoza.es/contenidos/gobierno-abierto/transparencia/Politica_Publicacion_OCDS.pdf";
    }

    public void setPublicationPolicy(String publicationPolicy) {
        this.publicationPolicy = publicationPolicy;
    }



    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getVersion() {
        return "1.1";
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public List<String> getExtensions() {

        return extensions;
    }
    public void setExtensions(List<String> extensions) {
        this.extensions = extensions;
    }



    public String getPublishedDate() {
        return publishedDate;
    }

    public void setPublishedDate(String publishedDate) {
        this.publishedDate = publishedDate;
    }

    public Publisher getPublisher() {
        return publisher;
    }

    public void setPublisher(Publisher publisher) {

        this.publisher = publisher;
    }

    public String getLicense() {
        return "https://www.zaragoza.es/sede/portal/aviso-legal#condiciones";
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Records getRecords() {
        return records;
    }

    public void setRecords(Records records) {
        this.records = records;
    }

    //endregion
        //region Contructors

        //endregion
}

