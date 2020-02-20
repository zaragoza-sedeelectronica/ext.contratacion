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
