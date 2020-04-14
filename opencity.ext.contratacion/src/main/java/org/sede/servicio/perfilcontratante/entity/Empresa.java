package org.sede.servicio.perfilcontratante.entity;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Rel;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "empresa")
@Entity
@Table(name = "PERFIL_EMPRESA", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
@Grafo(Contrato.GRAFO)
@Rel
@SequenceGenerator(name = "SECUENCIA_SEQ_PERFIL_EMPRESA", sequenceName = "SEQ_PERFIL_EMPRESA", allocationSize = 1)
public class Empresa extends EntidadBase implements java.io.Serializable{
    //region Atributtes & Columns
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SEQ_PERFIL_EMPRESA")
    @Id
    @Column(name="ID_EMPRESA",nullable=false,unique=true)
    @NotFound(action= NotFoundAction.IGNORE)
    private BigDecimal idEmpresa;

    @Column(name="LIBREBORME",nullable=true,unique=false)
    private String libreBorme;

    @Column(name="UTE",nullable=false,unique=false)
    private String ute;

    @Column(name="NOMBRE",nullable=false,unique=false)
    private String nombre;

    @Column(name="NIF",nullable=false,unique=false)
    private  String nif;

    @Transient
    private String nifEntidad;

    @Column(name="AUTONOMO",nullable=false,unique=false)
    private String autonomo;

    @Column(name="NACIONALIDAD",nullable=false,unique=false)
    private String nacionalidad;

    @Transient
    private List<Contrato> contratos;

    @Transient
    private List<Empresa> empresaUtes;
    
    @Transient
    private List<EmpresaConParticipacion> empresasEnUte;
    
    //endregion
    //region Getter and Setters
    public List<Empresa> getEmpresaUtes() {
        return empresaUtes;
    }

    public void setEmpresaUtes(List<Empresa> empresaUtes) {
        this.empresaUtes = empresaUtes;
    }
    public BigDecimal getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(BigDecimal idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public String getLibreBorme() {
        return libreBorme;
    }

    public void setLibreBorme(String libreBorme) {
        this.libreBorme = libreBorme;
    }

    public String getUte() {
        return ute;
    }

    public void setUte(String ute) {
        this.ute = ute;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNif() {
        return nif;
    }

    public void setNif(String nif) {
        this.nif = nif;
    }

    public String getNifEntidad() {
        return nifEntidad;
    }

    public void setNifEntidad(String nifEntidad) {
        this.nifEntidad = nifEntidad;
    }

    public String getAutonomo() {
        return autonomo;
    }

    public void setAutonomo(String autonomo) {
        this.autonomo = autonomo;
    }

    public String getNacionalidad() {
        return nacionalidad;
    }

    public void setNacionalidad(String nacionalidad) {
        this.nacionalidad = nacionalidad;
    }

    public List<Contrato> getContratos() {
		return contratos;
	}

	public void setContratos(List<Contrato> contratos) {
		this.contratos = contratos;
	}

	public List<EmpresaConParticipacion> getEmpresasEnUte() {
		return empresasEnUte;
	}

	public void setEmpresasEnUte(List<EmpresaConParticipacion> empresasEnUte) {
		this.empresasEnUte = empresasEnUte;
	}

	//endregion
    //region Overrides
    @Override
    public String toString() {
        return "Empresa [id="
                + idEmpresa + ", EMPRESA="
                + nombre + ", ID_UTE="
                + ute + ", nif="
                + nif + ", autonomo="
                + autonomo + ", nacionalidad="
                + nacionalidad + "]";
    }
    //endregion
}
