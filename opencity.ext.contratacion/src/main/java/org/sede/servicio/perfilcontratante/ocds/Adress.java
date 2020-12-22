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

