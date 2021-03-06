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

import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.anotaciones.Permisos;
import org.sede.core.dao.BooleanConverter;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;

@XmlRootElement(name = "cpv")
@Entity
@Table(name = "VISTA_CPV", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class Cpv extends EntidadBase implements Serializable {
    //region Atributtes & Columns
    @Id
    @Column(name = "ID",nullable = false)
    private BigDecimal id;
    @Column(name = "TITLE",nullable = false)
    private String titulo;
    
    @Column(name = "VISIBLE")
    @Permisos(Permisos.DET)
    private String visible;
    //endregion
    //region Setters & Getters

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getVisible() {
        return visible;
    }

    public void setVisible(String visible) {
        this.visible = visible;
    }

    //endregion
    //region Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Cpv)) return false;

        Cpv cpv = (Cpv) o;

        if (!getId().equals(cpv.getId())) return false;
        if (getTitulo() != null ? !getTitulo().equals(cpv.getTitulo()) : cpv.getTitulo() != null) return false;
        return getVisible() != null ? getVisible().equals(cpv.getVisible()) : cpv.getVisible() == null;

    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Cpv [");
        sb.append("id=").append(id);
        sb.append(", titulo='").append(titulo).append('\'');
        sb.append(", visible='").append(visible).append('\'');
        sb.append(']');
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int result = getId().hashCode();
        result = 31 * result + (getTitulo() != null ? getTitulo().hashCode() : 0);
        result = 31 * result + (getVisible() != null ? getVisible().hashCode() : 0);
        return result;
    }


    //endregion
    //region Contructs
    public Cpv(){}
    public Cpv(String id){
        this.id=BigDecimal.valueOf(Double.valueOf(id));
    }

    //endregion
}
