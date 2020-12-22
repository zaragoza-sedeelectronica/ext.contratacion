package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;

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

    public String getDate() {
        return ConvertDate.date2String(date,ConvertDate.ISO8601_FORMAT) ;
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
        ;
    }


    //endregion
    //region Overrides

    @Override
    public String toString() {
        return "Date["+ date +
                ']';
    }

    //endregion
}
