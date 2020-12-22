package org.sede.servicio.perfilcontratante.ocds;

import org.apache.commons.lang3.time.DateUtils;
import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.Date;

@XmlRootElement(name = "PeriodOcds")
@ResultsOnly(xmlroot = "PeriodOcds")
public class Period {
    //region Atributtes
        private Date startDate;
        private Date endDate;
        private Date maxExtentDate;
        private int durationInDays;
    //endregion
    //region Getters & Setters
        public String getStartDate() {
              return ConvertDate.date2String(startDate,ConvertDate.ISO8601_FORMAT);
        }

        public void setStartDate(Date startdate) {
            this.startDate = startdate;
        }

        public String getEndDate() {
            return ConvertDate.date2String(endDate,ConvertDate.ISO8601_FORMAT);
        }

        public void setEndDate(Date endDate) {
            this.endDate = endDate;
        }

    public String getMaxExtentDate() {
        return  ConvertDate.date2String(maxExtentDate,ConvertDate.ISO8601_FORMAT);
    }

    public void setMaxExtentDate(Date maxExtendedDate) {
        this.maxExtentDate = maxExtendedDate;
    }

    public int getDurationInDays() {
        return durationInDays;
    }

    public void setDurationInDays(int durationInDays) {
        this.durationInDays = durationInDays;
    }

    //endregion
    //region Constructors
        public Period(Date startdate, Date endDate) {
            this.startDate = startdate;
            this.endDate = endDate;


        }
        public Period(Date startdate) {
                this.startDate = startdate;
            }
        public Period(Date startdate,Integer days) {
            this.startDate = startdate;
            this.endDate=DateUtils.addDays(startdate,days);
            this.durationInDays=days;
            this.maxExtentDate=this.endDate;
        }
        public Period(Date startdate, Date endDate,Integer days) {
            this.startDate = startdate;
            this.endDate =endDate;
            this.durationInDays=days;
            this.maxExtentDate=DateUtils.addDays(endDate,days);
        }
    //endregion
}
