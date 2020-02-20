package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "DateTimeOcds")
@ResultsOnly(xmlroot = "DateTimeOcds")
public class DateTime {
    //region Atributtes
    private Date date;
    private int time;
    //endregion
    //region Getters & Setters

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    //endregion
    //region Contructs
    public DateTime(Date date){
        this.setDate(date);
        this.setTime(0);
    }


    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "DateTime[" +
                "date=" + date +
                ", time=" + time +
                ']';
    }

    //endregion
}
