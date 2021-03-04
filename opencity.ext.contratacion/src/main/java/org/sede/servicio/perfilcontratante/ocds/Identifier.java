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
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.entity.Empresa;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "IdentifierOcds")
@ResultsOnly(xmlroot = "IdentifierOcds")
public class Identifier {
    //region Atributtes
    private String id;
    private String legalName;
    private String scheme;
    private String uri;
    //endregion
    //region Getters &s Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLegalname() {
        return legalName;
    }

    public void setLegalname(String legalName) {
        this.legalName = legalName;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    //endregion
    //region Contructors
    public Identifier(){};
    public Identifier(Empresa empresa){
        this.scheme="NIF";
        this.id=empresa.getNif();
        this.legalName=empresa.getNombre();
        this.uri=empresa.getOpenCorporateUrl()!=null?empresa.getOpenCorporateUrl():null;
    }
    //endregion
}
