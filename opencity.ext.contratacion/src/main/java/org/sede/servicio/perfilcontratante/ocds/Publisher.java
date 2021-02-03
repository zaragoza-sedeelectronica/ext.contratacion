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

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "PublisherOcds")
@ResultsOnly(xmlroot = "PublisherOcds")
public class Publisher {
        //region Atributtes
        private String scheme;
        private String uid;
        private String name;
        private String uri;




        //endregion
        //region Getters & Setters


    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    //endregion
    //region Contructors
        public Publisher(){}
    //endregion
}

