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

import org.hibernate.annotations.BatchSize;
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


@XmlRootElement(name = "indicadorAhorro")
@Entity(name = "indicadorAhorro")
@Table(name = "VISTA_INDICADOR_AHORRO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING+"/indicadores")
@Rel
public class IndicadorAhorro  extends EntidadBase implements java.io.Serializable {
    // region Atributtes

    @Column(name="ID_PORTAL",nullable =false,insertable = false,updatable = false)
    private BigDecimal idPortal;
    @Id
    @Column(name="ID_CONTRATO")
    private BigDecimal id;
    @Column(name="IMPORTEOFERTA",updatable = false,insertable = false)
    private BigDecimal importeOferta;
    @Column(name="IMPORTECONTRATO",updatable = false,insertable = false)
    private BigDecimal importeContrato;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Column(name="AHORRO")
    private BigDecimal ahorro;
    @Column(name="NOMBRE")
    private String title;
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_TIPOCONTRATO")
    @BatchSize(size = 50)
    private Tipocontrato type;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "ID_PROCEDIMIENTO")
    @BatchSize(size = 50)
    private Procedimiento procedimiento;

    //endregion
    //region Setters & Getters


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Tipocontrato getType() {
        return type;
    }

    public void setType(Tipocontrato type) {
        this.type = type;
    }

    public Procedimiento getProcedimiento() {
        return procedimiento;
    }

    public void setProcedimiento(Procedimiento procedimiento) {
        this.procedimiento = procedimiento;
    }

    public BigDecimal getIdPortal() {
        return idPortal;
    }

    public void setIdPortal(BigDecimal idPortal) {
        this.idPortal = idPortal;
    }

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public BigDecimal getImporteOferta() {
        return importeOferta;
    }

    public void setImporteOferta(BigDecimal importeOferta) {
        this.importeOferta = importeOferta;
    }

    public BigDecimal getImporteContrato() {
        return importeContrato;
    }

    public void setImporteContrato(BigDecimal importeContrato) {
        this.importeContrato = importeContrato;
    }

    public String getAnyo() {
        return anyo;
    }

    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }

    public BigDecimal getAhorro() {
        return ahorro;
    }

    public void setAhorro(BigDecimal ahorro) {
        this.ahorro = ahorro;
    }

    //endregion
    //region Methods

    // endRegion
    //region Overrides


    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("IndicadorAhorro [");
        sb.append("idPortal=").append(idPortal);
        sb.append(", id=").append(id);
        sb.append(", importeOferta=").append(importeOferta);
        sb.append(", importeContrato=").append(importeContrato);
        sb.append(", anyo='").append(anyo).append('\'');
        sb.append(", ahorro=").append(ahorro);
        sb.append(", title='").append(title).append('\'');
        sb.append(", type=").append(type);
        sb.append(", procedimiento=").append(procedimiento);
        sb.append(']');
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndicadorAhorro)) return false;

        IndicadorAhorro that = (IndicadorAhorro) o;

        if (getIdPortal() != null ? !getIdPortal().equals(that.getIdPortal()) : that.getIdPortal() != null)
            return false;
        if (!getId().equals(that.getId())) return false;
        if (getImporteOferta() != null ? !getImporteOferta().equals(that.getImporteOferta()) : that.getImporteOferta() != null)
            return false;
        if (getImporteContrato() != null ? !getImporteContrato().equals(that.getImporteContrato()) : that.getImporteContrato() != null)
            return false;
        if (getAnyo() != null ? !getAnyo().equals(that.getAnyo()) : that.getAnyo() != null) return false;
        return getAhorro() != null ? getAhorro().equals(that.getAhorro()) : that.getAhorro() == null;

    }

    @Override
    public int hashCode() {
        int result = getIdPortal() != null ? getIdPortal().hashCode() : 0;
        result = 31 * result + getId().hashCode();
        result = 31 * result + (getImporteOferta() != null ? getImporteOferta().hashCode() : 0);
        result = 31 * result + (getImporteContrato() != null ? getImporteContrato().hashCode() : 0);
        result = 31 * result + (getAnyo() != null ? getAnyo().hashCode() : 0);
        result = 31 * result + (getAhorro() != null ? getAhorro().hashCode() : 0);
        return result;
    }


    //endregion
}
