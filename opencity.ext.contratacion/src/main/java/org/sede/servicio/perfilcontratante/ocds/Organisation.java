package org.sede.servicio.perfilcontratante.ocds;

import org.sede.core.anotaciones.ResultsOnly;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.EntidadContratante;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement(name = "OrganizationOcds")
@ResultsOnly(xmlroot = "OrganizationOcds")
public class Organisation {
    //region Atributtes
    private String id;
    private String idContractingprocess;
    private String name;
    private Adress address;
    private ContactPoint contactPoint;
    private List<Roles> roles;
    private Details details;
    private DateTime dateTime;

    //endregion
    //region Getters & Setters

    public void setDateTime(DateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdContractingprocess() {
        return idContractingprocess;
    }

    public void setIdContractingprocess(String idContractingprocess) {
        this.idContractingprocess = idContractingprocess;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Adress getAddress() {
        return address;
    }

    public void setAddress(Adress address) {
        this.address = address;
    }

    public ContactPoint getContactPoint() {
        return contactPoint;
    }

    public void setContactPoint(ContactPoint contactPoint) {
        this.contactPoint = contactPoint;
    }

    public List<Roles> getRoles() {
        return roles;
    }

    public void setRoles(List<Roles> roles) {
        this.roles = roles;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }

    //endregion
    //region Contructors
    public Organisation(EstructuraOrganizativa item){
        this.setId("ocds-"+item.getId()+"-buyer");
        this.setName(item.getTitle());
        Adress address=new Adress();
        address.setStreetaddress(item.getAddress().getAddress());
        address.setCountryname(item.getAddress().getCountryName() ==null ? "ESPAÑA":item.getAddress().getCountryName());
        address.setLocality(item.getAddress().getLocality() ==null ? "Zaragoza" :item.getAddress().getLocality());
        address.setPostalcode(item.getPostal_code());
        if(item.getPostal_code()!=null) {
            if ("50".equals(item.getPostal_code().substring(0, 2)))
                address.setRegion("Aragon");
            this.setAddress(address);
        }
    }
    public Organisation(EntidadContratante item){
        this.setId("ocds-"+item.getId()+"-buyer");
        this.setName(item.getTitle());
    }
    public Organisation(Empresa item, Boolean ganador){
        if(!ganador) {
            this.setId("ocds-" + item.getIdEmpresa() + "-tendered");
        }else{
            this.setId("ocds-" + item.getIdEmpresa() + "-award");
        }
        this.setName(item.getNombre());
    }
    public Organisation(Empresa item, Contrato con){
        this.setIdContractingprocess("ocds-" + con.getId() );
        this.setId("ocds-" + item.getIdEmpresa()+"-tender");
        this.setName(item.getNombre());
        this.setDateTime(new DateTime(con.getPubDate()));
    }
    public Organisation(EstructuraOrganizativa item, Contrato con){
        this.setIdContractingprocess("ocds-" + con.getId() );
        this.setId("ocds-" + item.getId()+"-buyer");
        this.setName(item.getTitle());
        this.setDateTime(new DateTime(con.getPubDate()));
    }
    public Organisation(Empresa item){
        this.setId(item.getIdEmpresa().toString());
        this.setName(item.getNombre().trim());
    }
    //endregion
}
