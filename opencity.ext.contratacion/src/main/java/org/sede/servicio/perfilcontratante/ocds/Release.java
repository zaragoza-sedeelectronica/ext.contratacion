package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "RecordsOcds")
@ResultsOnly(xmlroot = "RecordsOcds")
public class Release {
    //region Atributtes
    private String url;
    private String date;
    private List<String> tag=new ArrayList<String>();
    //endregion
    //region Getters && Setters

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<String> getTag() {
        return tag;
    }

    public void setTag(List<String> tag) {
        this.tag = tag;
    }


    //endregion
    //region Contructors
    public Release(Contrato con,String id){
        switch(con.getStatus().getId()){
            case 0:
            case 1:
            case 2:
                this.setUrl("/sede/servicio/contratacion-publica/ocds/tender/"+id+"-tender.json");
                this.setDate(ConvertDate.date2String(con.getPubDate(),ConvertDate.ISO8601_FORMAT));
                this.getTag().add("tender");
                break;
            case 3:
            case 5:

                for(Oferta ofer:con.getOfertas()){
                    if(ofer.getGanador()){
                        this.setDate(ConvertDate.date2String(ofer.getFechaAdjudicacion(),ConvertDate.ISO8601_FORMAT));
                        this.setUrl("/sede/servicio/contratacion-publica/ocds/award/"+ofer.getId()+"-award.json");
                    }
                }
                this.getTag().add("award");
                break;
            case 6:
            case 9:
                for(Oferta ofer:con.getOfertas()){
                    if(ofer.getGanador()){
                        this.setDate(ConvertDate.date2String(ofer.getFechaFormalizacion(),ConvertDate.ISO8601_FORMAT));
                        this.setUrl("/sede/servicio/contratacion-publica/ocds/contract/"+con.getId()+"-contract.json");
                    }
                }
                this.getTag().add("award");
                break;
        }
    }


    //endregion

}
