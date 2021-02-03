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
