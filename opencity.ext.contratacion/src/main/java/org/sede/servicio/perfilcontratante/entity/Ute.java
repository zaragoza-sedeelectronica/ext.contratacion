package org.sede.servicio.perfilcontratante.entity;

import java.math.BigDecimal;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.sede.core.anotaciones.Interno;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

@XmlRootElement(name = "ute")
@Entity
@Table(name = "PERFIL_UTE", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class Ute extends EntidadBase{

    @EmbeddedId
    @Interno
    @AttributeOverrides({
            @AttributeOverride(name = "idUte", column = @Column(name = "ID_UTE", nullable = false,precision = 6, scale = 0)),
            @AttributeOverride(name = "idEmpresa", column = @Column(name = "ID_EMPRESA", nullable = false,precision = 6, scale = 0))})
    private UteTieneID id;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name="ID_EMPRESA",referencedColumnName = "ID_EMPRESA",insertable = false,updatable = false)
    private Empresa empresa;

    @ManyToOne(fetch = FetchType.LAZY)
    @NotFound(action= NotFoundAction.IGNORE)
    @JoinColumn(name="ID_UTE", referencedColumnName = "ID_EMPRESA",insertable = false,updatable = false)
    @Interno
    private Oferta ofertaUte;

    @Column(name="POR_PAR", unique = true, nullable = false)
    private BigDecimal participacion;

    public Empresa getEmpresa() {
        return empresa;
    }
    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Oferta getOfertaUte() {
		return ofertaUte;
	}
	public void setOfertaUte(Oferta ofertaUte) {
		this.ofertaUte = ofertaUte;
	}
	public BigDecimal getParticipacion() {
        return participacion;
    }
    public void setParticipacion(BigDecimal participacion) {
        this.participacion = participacion;
    }
    public UteTieneID getId() {
        return id;
    }
    public void setId(UteTieneID id) {
        this.id = id;
    }


    @Override
    public String toString() {
        return "UTE [id="+id+", ofertaUte"+ofertaUte
                +   ", EMPRESA="
                + empresa +",% participacion="
                + participacion + "]";
    }
    /*
    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (ofertaUte != null ? ofertaUte.hashCode() : 0);
        result = 31 * result + (empresa != null ? empresa.hashCode() : 0);
        result = 31 * result + (participacion != null ? participacion.hashCode() : 0);
        return result;
    }
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Ute ute = (Ute) o;

        if (id != null ? !id.equals(ute.id) : ute.id != null) return false;
        if (ofertaUte != null ? !ofertaUte.equals(ute.ofertaUte) : ute.ofertaUte != null) return false;
        if (empresa != null ? !empresa.equals(ute.empresa) : ute.empresa != null) return false;
        if (participacion != null ? !participacion.equals(ute.participacion) : ute.participacion!= null)
            return false;
        return true;

    }

*/
}
