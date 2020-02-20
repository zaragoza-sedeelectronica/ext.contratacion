package org.sede.servicio.perfilcontratante.entity;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;
@XmlRootElement(name = "perfil-contratante-home")
public class ContratoHome {
	
	private List<Contrato> open = new ArrayList<Contrato>();
	private List<Contrato> lastModified = new ArrayList<Contrato>();
	
	public List<Contrato> getOpen() {
		return open;
	}
	public void setOpen(List<Contrato> open) {
		this.open = open;
	}
	public List<Contrato> getLastModified() {
		return lastModified;
	}
	public void setLastModified(List<Contrato> lastUpdated) {
		this.lastModified = lastUpdated;
	}
	
	
}
