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
