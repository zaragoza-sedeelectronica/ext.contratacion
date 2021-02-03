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

@XmlRootElement(name = "AdressOcds")
@ResultsOnly(xmlroot = "AdressOcds")
public class Adress {
        //region Atributtes
        private String streetAddress;
        private String locality;
        private String region;
        private String postalCode;
        private String countryName;

        //endregion
        //region Getters & Setters
        public String getStreetAddress() {
            return streetAddress;
        }

        public String getLocality() {
            return locality;
        }

        public String getRegion() {
            return region;
        }

        public String getPostalCode() {
            return postalCode;
        }

        public String getCountryName() {
            return countryName;
        }
        public void setStreetAddress(String streetaddress) {
            this.streetAddress = streetaddress;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public void setPostalCode(String postalcode) {
            this.postalCode = postalcode;
        }

        public void setCountryName(String countryname) {
            this.countryName = countryname;
        }
    //endregion
    //region Contructors
        public Adress(){}
    //endregion
}

