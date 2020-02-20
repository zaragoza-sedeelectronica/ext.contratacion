package org.sede.servicio.perfilcontratante.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.sede.core.anotaciones.Description;
import org.sede.core.anotaciones.PathId;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

@XmlRootElement(name = "contrato-estados")
@Entity
@Table(name = "PERFIL_ESTADOS", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
public class Estado extends EntidadBase {

    @Id
    @Column(name = "ID_ESTADO", nullable = false, unique = true)
    @Description("Por defecto = 0")
    private Integer id;

    @Column(name="NOMBRE")
    private String title;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @Override
    public String toString() {
        return "Estado [id=" + id + ", title=" + title + "]";
    }
}
