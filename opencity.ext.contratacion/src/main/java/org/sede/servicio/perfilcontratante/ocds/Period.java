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

import org.apache.commons.lang3.time.DateUtils;
import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;
import org.springframework.format.annotation.DateTimeFormat;

import javax.xml.bind.annotation.XmlRootElement;
import java.text.ParseException;
import java.util.Calendar;
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
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startdate);
            this.startDate =calendar.getTime();
            this.endDate = endDate;


        }
        public Period(Date startdate,Integer days) {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(startdate);
            this.startDate =calendar.getTime();
            this.endDate=DateUtils.addDays(startdate,days);
            this.durationInDays=days;
            this.maxExtentDate=this.endDate;
        }
        public Period(Date startdate, Date endDate,Integer days)  {
            Calendar calendar = Calendar.getInstance();
           calendar.setTime(startdate);
            this.startDate =calendar.getTime();
            this.endDate =endDate;
            this.durationInDays=days;
            this.maxExtentDate=DateUtils.addDays(endDate,days);
        }
    //endregion
}
