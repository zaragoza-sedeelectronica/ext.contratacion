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
package org.sede.servicio.perfilcontratante.entity;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Embeddable;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "uteapi")
@Embeddable
public class UteApi extends Empresa implements Serializable{


    @Transient
    private List<Empresa> empresa;


    public List<Empresa> getEmpresa() {
        return  empresa;
    }

    public void setEmpresa(List<Empresa> empresa) {
        this.empresa = empresa;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UteApi)) return false;

        UteApi uteApi = (UteApi) o;

        if (!getEmpresa().equals(uteApi.getEmpresa())) return false;
        return false;


    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("UteApi{");
        sb.append("empresa=").append(empresa);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = getEmpresa().hashCode();
        return result;
    }




}
