package org.sede.servicio.perfilcontratante.ocds;


import org.sede.core.anotaciones.ResultsOnly;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Anuncio;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Cpv;
import org.sede.servicio.perfilcontratante.entity.Oferta;

import javax.xml.bind.annotation.XmlRootElement;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@XmlRootElement(name = "ContractOcds")
@ResultsOnly(xmlroot = "ContractOcds")
public class Contract {
    //region Atributtes
    private String id;
    private String title;
    private String awardID;
    private String description;
    private String status;
    private Period period;
    private Value value;
    private List<Item> items = new ArrayList<Item>();
    private String dateSigned;
    private List<Document> documents = new ArrayList<Document>();
    private List<Milestone> milestones;
    private List<RelatedProcess> relatedProcesses = new ArrayList<RelatedProcess>();
    private List<Amendment> amendments = new ArrayList<Amendment>();
    //endregion
    //region Getteres & Setters

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<RelatedProcess> getRelatedPracesses() {
        return relatedProcesses;
    }

    public void setRelatedPracesses(List<RelatedProcess> relatedProcesses) {
        this.relatedProcesses = relatedProcesses;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAwardID() {
        return awardID;
    }

    public void setAwardID(String awardID) {
        this.awardID = awardID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(Contrato con) {
        switch (con.getStatus().getId()) {

            case 1:
            case 5:
                this.status = "active";
                break;
            case 6:
                this.status = "terminated”";
                break;
            case 4:
            case 7:
            case 8:
            case 10:
            case 11:
                this.status = "cancelled";
                break;
            default:
                this.status = "active";
                break;
        }
        for (Oferta ofer : con.getOfertas()) {
            if (ofer.getGanador()) {
                if (new Date().compareTo(sumarRestarDiasFecha(ofer.getFechaFormalizacion(), con.getDuracion() == null ? 0 : con.getDuracion().intValue())) > 0) {
                    this.status = "terminated";
                }
            }
        }
    }


    public Period getPeriod() {
        return period;
    }

    public void setPeriod(Period period) {
        this.period = period;
    }

    public Value getValue() {
        return value;
    }

    public void setValue(Value value) {
        this.value = value;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public String getDateSigned() {
        return dateSigned;
    }

    public void setDateSigned(String dateSigned) {
        this.dateSigned = dateSigned;
    }

    public List<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(List<Document> documents) {
        this.documents = documents;
    }

    public List<Milestone> getMilestones() {
        return milestones;
    }

    public void setMilestones(List<Milestone> milestones) {
        this.milestones = milestones;
    }

    public List<Amendment> getAmendments() {
        return amendments;
    }

    public void setAmendments(List<Amendment> amendments) {
        this.amendments = amendments;
    }

    //endregion
    //region Constructors
    public Contract(BigDecimal id, String title, String objeto) {

        this.setId(id.toString() + "-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado ");
    }

    public Contract(BigDecimal id, String title, String empresa, String entidad) {

        this.setId(id.toString() + "-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado entre " + entidad + " y " + empresa);
    }

    public Contract(BigDecimal id, String title, String empresa, String entidad, Contrato con) {

        this.setId(id.toString() + "-contract");
        this.setTitle(title);
        this.setDescription("Contrato firmado entre " + entidad + " y " + empresa);
        this.setStatus(con);
    }

    public Contract(Contrato con) throws ParseException {
        List<Item> items = new ArrayList<Item>();
        List<Document> documents = new ArrayList<Document>();
        List<Amendment> amendments = new ArrayList<Amendment>();
        List<Milestone> milestones = new ArrayList<Milestone>();
        List<RelatedProcess> relatedProcesses = new ArrayList<RelatedProcess>();

        this.setId(con.getId().toString() + "-contract");
        for (Oferta ofer : con.getOfertas()) {
            if (ofer.getGanador()) {
                this.setAwardID(ofer.getId() + "-award");
                this.setTitle(con.getTitle());
                this.setDescription("Contrato firmado entre " + con.getEntity().getTitle() + " y " + ofer.getEmpresa().getNombre());
                this.setStatus(con);
                this.setPeriod(new Period(ofer.getFechaFormalizacion() == null ? ofer.getFechaAdjudicacion() : ofer.getFechaFormalizacion(), sumarRestarDiasFecha(ofer.getFechaFormalizacion(), con.getDuracion().intValue()), con.getDuracion().intValue()));
                this.setValue(new Value(con.getCanon() ? ofer.getImporteSinIVA().negate() : ofer.getImporteSinIVA(), "EUR"));
                if (con.getProcedimiento().getId().equals(new BigDecimal(10.0))) {
                    this.setDateSigned(ConvertDate.date2String(ofer.getFechaAdjudicacion(), ConvertDate.ISO8601_FORMAT));
                } else {
                    if (ofer.getFechaFormalizacion() != null) {
                        this.setDateSigned(ConvertDate.date2String(ofer.getFechaFormalizacion(), ConvertDate.ISO8601_FORMAT));
                    } else {
                        this.setDateSigned(ConvertDate.date2String(ofer.getFechaAdjudicacion(), ConvertDate.ISO8601_FORMAT));
                    }
                }
            }
        }
        if (con.getCpv().size() != 0) {
            if (con.getCpv().size() == 1) {
                for (Cpv cpv : con.getCpv()) {
                    this.items.add(new Item(cpv, con));
                }
            } else {
                this.items.add(new Item(con, true));
            }
        }
        for (Anuncio anun : con.getAnuncios()) {
            documents.add(new Document(anun));
        }
        this.setDocuments(documents);
        this.setRelatedPracesses(relatedProcesses);
        this.setMilestones(milestones);
        this.setAmendments(amendments);


    }

    public Contract(Contrato con, Oferta ofer) throws ParseException {
        List<Item> items = new ArrayList<Item>();
        List<Document> documents = new ArrayList<Document>();
        List<Amendment> amendments = new ArrayList<Amendment>();
        List<Milestone> milestones = new ArrayList<Milestone>();
        List<RelatedProcess> relatedProcesses = new ArrayList<RelatedProcess>();

        this.setId(con.getId().toString() + "-contract");
        if (ofer.getGanador()) {
            this.setAwardID(ofer.getId() + "-award");
            this.setTitle(con.getTitle());
            this.setDescription("Contrato para el " + ofer.getLote().getDescription() + " firmado entre " + con.getEntity().getTitle() + " y " + ofer.getEmpresa().getNombre());
            this.status = statusLote(ofer);
            this.setPeriod(new Period(ofer.getFechaFormalizacion(), sumarRestarDiasFecha(ofer.getFechaFormalizacion(), con.getDuracion().intValue()), con.getDuracion().intValue()));
            this.setValue(new Value(con.getCanon() ? ofer.getImporteSinIVA().negate() : ofer.getImporteSinIVA(), "EUR"));
            if (ofer.getContrato().getCpv().size() == 1) {
                for (Cpv cpv : ofer.getContrato().getCpv()) {
                    this.items.add(new Item(cpv, ofer.getContrato()));
                }
            } else {

                this.items.add(new Item(ofer.getContrato(), true));


            }
            if (con.getProcedimiento().getId().equals(new BigDecimal(10.0))) {
                this.setDateSigned(ConvertDate.date2String(ofer.getFechaAdjudicacion(), ConvertDate.ISO8601_FORMAT));
            } else {
                if (ofer.getFechaFormalizacion() != null) {
                    this.setDateSigned(ConvertDate.date2String(ofer.getFechaFormalizacion(), ConvertDate.ISO8601_FORMAT));
                } else {
                    this.setDateSigned(ConvertDate.date2String(ofer.getFechaAdjudicacion(), ConvertDate.ISO8601_FORMAT));
                }
            }

        }
        for (Anuncio anun : con.getAnuncios()) {
            this.documents.add(new Document(anun));
            if (anun.getType().getId().equals(new BigDecimal(31)) || anun.getType().getId().equals(new BigDecimal(32)) || anun.getType().getId().equals(new BigDecimal(33)))
                this.amendments.add(new Amendment(anun));
        }
        if (con.getPadre() != null) {
            this.relatedProcesses.add(new RelatedProcess(con.getPadre()));
        }
        this.setMilestones(milestones);


    }
    //endregion

    public Date sumarRestarDiasFecha(Date fecha, int dias) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.add(Calendar.DAY_OF_YEAR, dias);
        return calendar.getTime();

    }

    public String statusLote(Oferta ofer) {
        String status;
        switch (Integer.valueOf(ofer.getLote().getId().toString())) {

            case 1:
            case 5:
            case 6:
                status = "active";
                break;
            case 4:
            case 7:
            case 8:
            case 10:
            case 11:
                status = "cancelled";
                break;
            default:
                status = "active";
                break;
        }
        if (ofer.getGanador()) {
            if (new Date().compareTo(sumarRestarDiasFecha(ofer.getFechaFormalizacion(), ofer.getContrato().getDuracion() == null ? 0 : ofer.getContrato().getDuracion().intValue())) > 0) {
                status = "complete";
            }
        }
        return status;

    }
}
