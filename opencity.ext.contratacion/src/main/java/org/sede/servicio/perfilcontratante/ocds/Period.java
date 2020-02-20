package org.sede.servicio.perfilcontratante.ocds;

import org.apache.commons.lang3.time.DateUtils;
import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "PeriodOcds")
@ResultsOnly(xmlroot = "PeriodOcds")
public class Period {
    //region Atributtes
        private DateTime startdate;
        private DateTime endDate;
        private DateTime maxExtendedDate;
        private int duarationInDays;
    //endregion
    //region Getters & Setters
        public DateTime getStartdate() {
            return startdate;
        }

        public void setStartdate(DateTime startdate) {
            this.startdate = startdate;
        }

        public DateTime getEndDate() {
            return endDate;
        }

        public void setEndDate(DateTime endDate) {
            this.endDate = endDate;
        }

    public DateTime getMaxExtendedDate() {
        return maxExtendedDate;
    }

    public void setMaxExtendedDate(DateTime maxExtendedDate) {
        this.maxExtendedDate = maxExtendedDate;
    }

    public int getDuarationInDays() {
        return duarationInDays;
    }

    public void setDuarationInDays(int duarationInDays) {
        this.duarationInDays = duarationInDays;
    }

    //endregion
    //region Constructors
        public Period(Date startdate, Date endDate) {
            this.startdate = new DateTime(startdate);
            this.endDate = new DateTime(endDate);
        }
        public Period(Date startdate) {
            this.startdate = new DateTime(startdate);
        }
    public Period(Date startdate,Integer days) {
        this.startdate = new DateTime(startdate);
        this.endDate=new DateTime(DateUtils.addDays(startdate,days));
        this.duarationInDays=days;
    }
    public Period(Date startdate, Date endDate,Integer days) {
        this.startdate = new DateTime(startdate);
        this.endDate = new DateTime(endDate);
        this.duarationInDays=days;
    }
    //endregion
}
