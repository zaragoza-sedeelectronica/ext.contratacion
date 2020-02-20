package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "AdressOcds")
@ResultsOnly(xmlroot = "AdressOcds")
public class Adress {
        //region Atributtes
        private String streetaddress;
        private String locality;
        private String region;
        private String postalcode;
        private String countryname;

        //endregion
        //region Getters & Setters
        public String getStreetaddress() {
            return streetaddress;
        }

        public String getLocality() {
            return locality;
        }

        public String getRegion() {
            return region;
        }

        public String getPostalcode() {
            return postalcode;
        }

        public String getCountryname() {
            return countryname;
        }
        public void setStreetaddress(String streetaddress) {
            this.streetaddress = streetaddress;
        }

        public void setLocality(String locality) {
            this.locality = locality;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public void setPostalcode(String postalcode) {
            this.postalcode = postalcode;
        }

        public void setCountryname(String countryname) {
            this.countryname = countryname;
        }
    //endregion
    //region Contructors
        public Adress(){}
    //endregion
}

