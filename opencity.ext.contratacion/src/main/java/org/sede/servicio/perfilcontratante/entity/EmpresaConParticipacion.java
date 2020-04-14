package org.sede.servicio.perfilcontratante.entity;

import org.sede.core.dao.EntidadBase;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;

@XmlRootElement(name = "empresa-en-ute")
public class EmpresaConParticipacion extends EntidadBase implements java.io.Serializable{
	
	private Empresa empresa;
	
	private BigDecimal participacion;
	
	public Empresa getEmpresa() {
		return empresa;
	}
	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	public BigDecimal getParticipacion() {
		return participacion;
	}
	public void setParticipacion(BigDecimal participacion) {
		this.participacion = participacion;
	}
	
	
	
}
