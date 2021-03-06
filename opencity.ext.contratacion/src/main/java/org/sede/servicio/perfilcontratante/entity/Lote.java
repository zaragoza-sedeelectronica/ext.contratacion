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
import org.sede.core.anotaciones.Grafo;
import org.sede.core.anotaciones.Interno;
import org.sede.core.anotaciones.PathId;
import org.sede.core.anotaciones.Rel;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;
import org.sede.servicio.perfilcontratante.ContratoController;

import javax.persistence.*;
import javax.validation.constraints.Size;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "lote")
@Entity(name = "Lote")
@Table(name = "PERFIL_LOTE", schema = ConfigPerfilContratante.ESQUEMA)
@DynamicUpdate
@XmlAccessorType(XmlAccessType.FIELD)
@PathId("/" + ContratoController.MAPPING)
@Grafo(Contrato.GRAFO)
@Rel
@SequenceGenerator(name = "SECUENCIA_SEQ_PERFIL_LOTE", sequenceName = "SEQ_PERFIL_LOTE", allocationSize = 1)
public class Lote extends EntidadBase implements java.io.Serializable{
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "SECUENCIA_SEQ_PERFIL_LOTE")
    @Id
    @Column(name = "ID_LOTE", unique = true, nullable = false)
    private BigDecimal id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ID_CONTRATO")
    @Interno
    private Contrato contrato;

    @Column(name = "DESCRIPCION") @Size(max = 600)
    private String description;

    @Column(name = "IMPORTE_LICITACION_LOTE_SINIVA")
	private BigDecimal importeLicitacionSinIVA;
    
    @Column(name = "IMPORTE_LICITACION_LOTE_CONIVA")
	private BigDecimal importeLicitacionConIVA;
    
    @ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "ESTADO", referencedColumnName="ID_ESTADO")
	private Estado status;
    
    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public Contrato getContrato() {
        return contrato;
    }

    public void setContrato(Contrato contrato) {
        this.contrato = contrato;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getImporteLicitacionSinIVA() {
		return importeLicitacionSinIVA;
	}

	public void setImporteLicitacionSinIVA(BigDecimal importeLicitacionSinIVA) {
		this.importeLicitacionSinIVA = importeLicitacionSinIVA;
	}

	public BigDecimal getImporteLicitacionConIVA() {
		return importeLicitacionConIVA;
	}

	public void setImporteLicitacionConIVA(BigDecimal importeLicitacionConIVA) {
		this.importeLicitacionConIVA = importeLicitacionConIVA;
	}

	public Estado getStatus() {
		return status;
	}

	public void setStatus(Estado status) {
		this.status = status;
	}

	@Override
    public String toString() {
        return "Lote [id=" + id + ", description=" + description /*+ ", contrato=" + contrato */+"]";
    }
}
