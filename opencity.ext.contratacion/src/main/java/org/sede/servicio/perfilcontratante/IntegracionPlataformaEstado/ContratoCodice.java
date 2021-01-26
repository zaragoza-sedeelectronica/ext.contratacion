package org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "contratocodice")
@ResultsOnly(xmlroot = "contratocodice")
public class ContratoCodice  {

    //region Atributtes
    private String uriContrato;
    private String id;
    private String title;
    private String fechaInicio;
    private List<String> types = new ArrayList<String>();
    private String contractingAuthority;
    private String contractingBody;
    private String duration;
    private String legalDocumentReference;
    //endregion
    //region Contructs
    public ContratoCodice() {}

    public ContratoCodice(String uriContrato, String id) {
        this.uriContrato = uriContrato;
        this.id = id;
    }
    //endregion

    //region Getters & Setters
    public String getUriContrato() {
        return uriContrato;
    }


    public void setUriContrato(String uriContrato) {
        this.uriContrato = uriContrato;
    }


    public String getId() {
        return id;
    }


    public void setId(String id) {
        this.id = id;
    }


    public String getTitle() {
        return title;
    }


    public void setTitle(String title) {
        this.title = title;
    }


    public List<String> getTypes() {
        return types;
    }


    public void setTypes(List<String> types) {
        this.types = types;
    }


    public String getContractingAuthority() {
        return contractingAuthority;
    }


    public void setContractingAuthority(String contractingAuthority) {
        this.contractingAuthority = contractingAuthority;
    }


    public String getDuration() {
        return duration;
    }


    public void setDuration(String duration) {
        this.duration = duration;
    }


    public String getLegalDocumentReference() {
        return legalDocumentReference;
    }


    public void setLegalDocumentReference(String legalDocumentReference) {
        this.legalDocumentReference = legalDocumentReference;
    }



    public String getContractingBody() {
        return contractingBody;
    }


    public void setContractingBody(String contractingBody) {
        this.contractingBody = contractingBody;
    }



    @SuppressWarnings("null")
    public String getFechaInicio() throws java.text.ParseException {
        System.out.println(fechaInicio);
        //SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd");
        Date fecha = null;
        fecha = ConvertDate.string2Date(fechaInicio,ConvertDate.ISO8601_FORMAT);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date date = calendar.getTime();
        String fechaIni = ConvertDate.date2String(date,ConvertDate.ISO8601_FORMAT);
        System.out.println(fechaIni);
        return fechaIni;
    }


    public void setFechaInicio(String fechaInicio) {
        this.fechaInicio = fechaInicio;
    }
    //endregion

    //region Overrides
    @Override
    public String toString() {
        return "Contrato [uriContrato=" + uriContrato + ", id=" + id
                + ", title=" + title + ", types=" + types
                + ", contractingAuthority=" + contractingAuthority
                + ", contractingBody=" + contractingBody + ", duration="
                + duration + ", legalDocumentReference="
                + legalDocumentReference + "]";
    }

    //endregion


}
