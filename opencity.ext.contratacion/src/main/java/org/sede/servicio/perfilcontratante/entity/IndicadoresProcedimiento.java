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

import com.googlecode.genericdao.search.SearchResult;
import org.hibernate.annotations.DynamicUpdate;
import org.sede.core.dao.EntidadBase;
import org.sede.servicio.perfilcontratante.ConfigPerfilContratante;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@XmlRootElement(name = "IndicadoresPorTipo")
@Entity
@Table(name = "VISTA_INDICADORES_TIPO", schema = ConfigPerfilContratante.ESQUEMA)
@XmlAccessorType(XmlAccessType.FIELD)
@DynamicUpdate
public class IndicadoresProcedimiento extends EntidadBase implements Serializable {

    //region Atributte Columns

    @Column(name="ID_PORTAL",nullable =false,insertable = false,updatable = false)
    private BigDecimal idPortal;
    @Column(name="TOTAL")
    private BigDecimal total;
    @Column(name="TOTALCONIVA",updatable = false,insertable = false)
    private BigDecimal totalConIva;
    @Column(name="TOTALSINIVA",updatable = false,insertable = false)
    private BigDecimal totalSinIva;
    @Column(name="ANYO",updatable = false,insertable = false)
    private String anyo;
    @Id
    @Column(name="CONT_MENOR",updatable = false,insertable = false)
    private String tipo;
    @Transient
    private EntidadContratante entidad;
    @Transient
    private List<Contrato> contratos;
    @Transient
    private BigDecimal totalContratos;
    @Transient
    private BigDecimal porcientoMenor;
    @Transient
    private BigDecimal porcientoAbiertoSim;
    @Transient
    private BigDecimal porcientoVentaDirecta;
    @Transient
    private BigDecimal porcientoAbierto;
    @Transient
    private BigDecimal porcientoRestringido;
    @Transient
    private BigDecimal porcientoNegociadoSin;
    @Transient
    private BigDecimal porcientoDirecto;
    @Transient
    private BigDecimal porcientoAbiertoConjuntamente;
    @Transient
    private BigDecimal porcientoEnajenacion;
    @Transient
    private BigDecimal porcientoNegociadoSinPubliNiCon;
    @Transient
    private BigDecimal porCiento;
    @Transient
    private SearchResult<DatosIndicadoresProcedimiento> resultado;
    @Transient Procedimiento tipoProcedimiento;
    //endregion
    //region Getter and Setter

    public Procedimiento getTipoProcedimiento() {
        return tipoProcedimiento;
    }

    public void setTipoProcedimiento(Procedimiento tipoProcedimiento) {
        this.tipoProcedimiento = tipoProcedimiento;
    }

    public BigDecimal getPorcientoMenor() {
        return porcientoMenor;
    }

    public void setPorcientoMenor(BigDecimal porcientoMenor) {
        this.porcientoMenor = porcientoMenor;
    }

    public BigDecimal getPorCiento() {
        return porCiento;
    }

    public void setPorCiento(BigDecimal porCiento) {
        this.porCiento = porCiento;
    }

    public BigDecimal getPorcientoAbiertoSim() {
        return porcientoAbiertoSim;
    }

    public void setPorcientoAbiertoSim(BigDecimal porcientoAbiertoSim) {
        this.porcientoAbiertoSim = porcientoAbiertoSim;
    }

    public BigDecimal getPorcientoVentaDirecta() {
        return porcientoVentaDirecta;
    }

    public void setPorcientoVentaDirecta(BigDecimal porcientoVentaDirecta) {
        this.porcientoVentaDirecta = porcientoVentaDirecta;
    }

    public BigDecimal getPorcientoAbierto() {
        return porcientoAbierto;
    }

    public void setPorcientoAbierto(BigDecimal porcientoAbierto) {
        this.porcientoAbierto = porcientoAbierto;
    }

    public BigDecimal getPorcientoRestringido() {
        return porcientoRestringido;
    }

    public void setPorcientoRestringido(BigDecimal porcientoRestringido) {
        this.porcientoRestringido = porcientoRestringido;
    }

    public BigDecimal getPorcientoNegociadoSin() {
        return porcientoNegociadoSin;
    }

    public void setPorcientoNegociadoSin(BigDecimal porcientoNegociadoSin) {
        this.porcientoNegociadoSin = porcientoNegociadoSin;
    }

    public BigDecimal getPorcientoDirecto() {
        return porcientoDirecto;
    }

    public void setPorcientoDirecto(BigDecimal porcientoDirecto) {
        this.porcientoDirecto = porcientoDirecto;
    }

    public BigDecimal getPorcientoAbiertoConjuntamente() {
        return porcientoAbiertoConjuntamente;
    }

    public void setPorcientoAbiertoConjuntamente(BigDecimal porcientoAbiertoConjuntamente) {
        this.porcientoAbiertoConjuntamente = porcientoAbiertoConjuntamente;
    }

    public BigDecimal getPorcientoEnajenacion() {
        return porcientoEnajenacion;
    }

    public void setPorcientoEnajenacion(BigDecimal porcientoEnajenacion) {
        this.porcientoEnajenacion = porcientoEnajenacion;
    }

    public BigDecimal getPorcientoNegociadoSinPubliNiCon() {
        return porcientoNegociadoSinPubliNiCon;
    }

    public void setPorcientoNegociadoSinPubliNiCon(BigDecimal porcientoNegociadoSinPubliNiCon) {
        this.porcientoNegociadoSinPubliNiCon = porcientoNegociadoSinPubliNiCon;
    }
    public BigDecimal getTotalContratos() {
        return totalContratos;
    }

    public void setTotalContratos(BigDecimal totalContratos) {
        this.totalContratos = totalContratos;
    }
    public SearchResult<DatosIndicadoresProcedimiento>getResultado() {
        return resultado;
    }

    public void setResultado(SearchResult<DatosIndicadoresProcedimiento>resultado) {
        this.resultado = resultado;
    }
    public List<Contrato> getContratos() {return contratos;}
    public void setContratos(List<Contrato> contratos) {this.contratos = contratos;}

    public String getTipo() {return tipo;}

    public void setTipo(String tipo) {this.tipo = tipo;}
    public String getAnyo() {
        return anyo;
    }
    public void setAnyo(String anyo) {
        this.anyo = anyo;
    }
    public BigDecimal getTotalSinIva() { return totalSinIva;}
    public EntidadContratante getEntidad() {return entidad;}
    public void setEntidad(EntidadContratante entidad) {
        this.entidad = entidad;
    }
    public void setTotalSinIva(BigDecimal totalSinIva) {
        if (totalSinIva==null) {
            this.totalSinIva = new BigDecimal(0);
        }else {
            this.totalSinIva = totalSinIva;
        }
    }
    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total){

        this.total = total;
    }
    public BigDecimal getTotalConIva() {return totalConIva;}
    public void setTotalConIva(BigDecimal totalConIva) {
        if (totalConIva==null) {
            this.totalConIva = new BigDecimal(0);
        }else {
            this.totalConIva = totalConIva;
        }
    }
    public BigDecimal getIdPortal() {
        return idPortal;
    }
    public void setIdPortal(BigDecimal idPortal) {
        this.idPortal = idPortal;

    }
    //endregion
    @Override
    public int hashCode() {
        int result = total.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "IndicadoresProcedimiento[" +
                "idPortal=" + idPortal +
                ", total=" + total +
                ", totalConIva=" + totalConIva +
                ", totalSinIva=" + totalSinIva +
                ", anyo='" + anyo + '\'' +
                ", tipo='" + tipo + '\'' +
                ", entidad=" + entidad +
                ", contratos=" + contratos +
                ", totalContratos=" + totalContratos +
                ", porcientoMenor=" + porcientoMenor +
                ", porcientoAbiertoSim=" + porcientoAbiertoSim +
                ", porcientoVentaDirecta=" + porcientoVentaDirecta +
                ", porcientoAbierto=" + porcientoAbierto +
                ", porcientoRestringido=" + porcientoRestringido +
                ", porcientoNegociadoSin=" + porcientoNegociadoSin +
                ", porcientoDirecto=" + porcientoDirecto +
                ", porcientoAbiertoConjuntamente=" + porcientoAbiertoConjuntamente +
                ", porcientoEnajenacion=" + porcientoEnajenacion +
                ", porcientoNegociadoSinPubliNiCon=" + porcientoNegociadoSinPubliNiCon +
                ", porCiento=" + porCiento +
                ", resultado=" + resultado +
                ", tipoProcedimiento=" + tipoProcedimiento +
                ']';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IndicadoresProcedimiento)) return false;
        IndicadoresProcedimiento that = (IndicadoresProcedimiento) o;

        if (!total.equals(that.total)) return false;
        if (!totalConIva.equals(that.totalConIva)) return false;
        return totalSinIva.equals(that.totalSinIva);

    }


}
