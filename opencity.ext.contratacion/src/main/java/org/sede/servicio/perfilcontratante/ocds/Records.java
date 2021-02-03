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
import org.sede.servicio.perfilcontratante.entity.Anuncio;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "RecordsOcds")
@ResultsOnly(xmlroot = "RecordsOcds")
public class Records {
    //region Atributtes
    private String ocid;
    private List<Release> releases=new ArrayList<Release>(0);
    private ContractingProcess compiledRelease;
    private List<ContractingProcess> versionedRelease=new ArrayList<ContractingProcess>();
    //endregion
    //region Getters && Setters

    public String getOcid() {
        return ocid;
    }

    public void setOcid(String ocid) {
        this.ocid = ocid;
    }

    public ContractingProcess getCompiledRelease() {
        return compiledRelease;
    }

    public void setCompiledRelease(ContractingProcess  compiledRelease) {
        this.compiledRelease = compiledRelease;
    }

    public List<ContractingProcess> getVersionedRelease() {
        return versionedRelease;
    }

    public void setVersionedRelease(List<ContractingProcess> versionedRelease) {
        this.versionedRelease = versionedRelease;
    }

    public List<Release> getReleases() {
        return releases;
    }

    public void setReleases(List<Release> releases) {
        this.releases = releases;
    }
    //endregion
    //region Contructors



    //endregion

}
