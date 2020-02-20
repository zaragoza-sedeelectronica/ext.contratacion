package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.perfilcontratante.entity.Cpv;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "CpvOcds")
@ResultsOnly(xmlroot = "CpvOcds")
public class CpvOcds {
    //region Atributtes
    private BigDecimal id;
    private String scheme;
    private String description;
    private  String uri;
    private Integer quantity;
    //endregion
    //region Getters & Setters

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
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

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
    //endregion
    //regions Contructors
    public CpvOcds(Cpv cpv){
        this.scheme="CPV";
        this.id=cpv.getId();
        this.description=cpv.getTitulo();
        this.uri="https://www.zaragoza.es/sede/servicio/contratacion-publica/cpv/"+cpv.getId()+".json";

    }
    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Cpv[" +
                "id=" + id +
                ", description='" + description + '\'' +
                ", uri='" + uri + '\'' +
                ", quantity=" + quantity +
                ']';
    }

    //endregion
}
