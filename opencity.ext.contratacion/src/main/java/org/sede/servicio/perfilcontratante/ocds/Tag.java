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

@XmlRootElement(name = "Tag")
@ResultsOnly(xmlroot = "Tag")
public class Tag {
    //region Atributtes
    private String relasetag;
    //endregion
    //region Getters & setters

    public String getRelasetag() {
        return relasetag;
    }

    public void setRelasetag(String relasetag) {
        this.relasetag = relasetag;
    }
    //endregion
    public Tag(String tag){
        this.setRelasetag(tag);
    }
}
