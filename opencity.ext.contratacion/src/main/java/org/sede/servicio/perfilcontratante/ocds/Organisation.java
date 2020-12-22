package org.sede.servicio.perfilcontratante.ocds;

import com.googlecode.genericdao.search.SearchResult;
import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.organigrama.entity.EstructuraOrganizativa;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.EntidadContratante;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "OrganizationOcds")
@ResultsOnly(xmlroot = "OrganizationOcds")
public class Organisation {
    //region Atributtes
    public static final String DIRTRES="L01502973";
    public static final String SCHEME="DIR3";
    private String id;
    private String ocds;
    private Identifier identifier;
    private String name;
    private Adress address;
    private ContactPoint contactPoint;
    private List<String> roles=new ArrayList<String>(0);
    private Details details;


    //endregion
    //region Getters & Setters


    public String getOcds() {
        return ocds;
    }

    public void setOcds(String ocds) {
        this.ocds = ocds;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Identifier getIdentifier() {
        return identifier;
    }

    public void setIdentifier(Identifier identifier) {
        this.identifier = identifier;
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

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
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
        List<Roles> roles=new ArrayList<Roles>();
        this.setId(item.getId()+"-buyer");
        this.setName(item.getTitle());
    }
    public Organisation(EntidadContratante item){
        List<String> roles=new ArrayList<String>();
        this.setId(item.getId()+"-"+this.SCHEME+"-"+this.DIRTRES);
        this.setName(item.getTitle());
        Identifier identifier = new Identifier();
        identifier.setId(item.getIdSchema());
        identifier.setLegalname(item.getTitle());
        identifier.setScheme(item.getSchema());
        identifier.setUri("https://www.zaragoza.es");
        this.setIdentifier(identifier);
        roles.add("procuringEntity");
        this.setRoles(roles);
    }
    public Organisation(Empresa item, Boolean ganador){
        if(!ganador) {
            this.setId( item.getIdEmpresa() + "-"+item.getNombre());
        }else{
            this.setId(item.getIdEmpresa() + "-award");
        }
        this.setName(item.getNombre());
    }
    public Organisation(Empresa item, Contrato con) {
        List<String> roles = new ArrayList<String>();
        this.setOcds("ocds-1xraxc-" + con.getId());
        this.setId(item.getIdEmpresa() + "-" + item.getNombre());
        this.setName(item.getNombre());
        Identifier identifier = new Identifier();
        identifier.setId(item.getNifEntidad());
        identifier.setLegalname(item.getNombre());
        identifier.setScheme("NIF");
        identifier.setUri(item.getOpenCorporateUrl());
        this.setIdentifier(identifier);

        for (Oferta ofer : con.getOfertas()) {
            if (ofer.getEmpresa().getIdEmpresa() == item.getIdEmpresa()) {
                if (ofer.getGanador()) {
                    roles.add("Supplier");
                    this.setRoles(roles);
                } else {
                    roles.add("Tenderer");
                    this.setRoles(roles);
                }
            }
        }
    }
    public Organisation(Empresa item, SearchResult<Contrato> con){
        List<String> roles = new ArrayList<String>();
        Boolean tender=false;
        Boolean supplier=false;
        this.setId(item.getIdEmpresa()+"-"+item.getNombre());
        this.setName(item.getNombre());
        Identifier identifier = new Identifier();
        identifier.setId(item.getNifEntidad());
        identifier.setLegalname(item.getNombre());
        identifier.setScheme("NIF");
        identifier.setUri(item.getOpenCorporateUrl());
        this.setIdentifier(identifier);
        for(Contrato contrato:con.getResult()) {
            for (Oferta ofer : contrato.getOfertas()) {
                if (ofer.getEmpresa().getIdEmpresa() == item.getIdEmpresa()) {
                    if (ofer.getGanador() && !supplier) {
                        roles.add("supplier");
                        this.setRoles(roles);
                        supplier=true;
                    } else if(!ofer.getGanador() && !tender){
                        roles.add("tenderer");
                        this.setRoles(roles);
                        tender=true;
                    }
                }
            }
        }

    }
    public Organisation(EstructuraOrganizativa item, Contrato con){
        List<String> roles = new ArrayList<String>();
        this.setId(item.getId()+"-"+item.getTitle());
        this.setName(item.getTitle());
        roles.add("buyer");
        this.setRoles(roles);
    }
    public Organisation(Empresa item){
        this.setId(item.getIdEmpresa().toString());
        this.setName(item.getNombre().trim());
    }
    public Organisation(EntidadContratante item ,Contrato con){
        List<String> roles=new ArrayList<String>();
        this.setId(item.getId()+"-"+item.getTitle());
        this.setName(item.getTitle());
        roles.add("procuringEntity");
        this.setRoles(roles);
    }
    //endregion
}
