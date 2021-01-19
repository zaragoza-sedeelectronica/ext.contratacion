package org.sede.servicio.perfilcontratante;



import java.math.BigDecimal;
import java.text.ParseException;
import java.time.Period;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.Tipocontrato;


;

public class UtilsContrato {

    //	public static final BigDecimal TIPO_ANUNCIO_INFORMACION_PREVIA = new BigDecimal(1);
    public static final BigDecimal TIPO_ANUNCIO_LICITACION = new BigDecimal(2);
    public static final BigDecimal TIPO_ANUNCIO_PLIEGOS_TECNICOS = new BigDecimal(3);
    public static final BigDecimal TIPO_ANUNCIO_PLIEGOS_ADMINISTRATIVOS = new BigDecimal(4);
    public static final BigDecimal TIPO_ANUNCIO_ADJUDICACION = new BigDecimal(6);
    public static final BigDecimal TIPO_ANUNCIO_FECHA_PRESENTACION = new BigDecimal(10);
    public static final BigDecimal TIPO_ANUNCIO_PRESUPUESTO = new BigDecimal(11);
    public static final BigDecimal TIPO_ANUNCIO_OTRAS_INFORMACIONES = new BigDecimal(9);

    public static final HashMap<String, String> tiposProcedimiento = new HashMap<String, String>();
    static{
        tiposProcedimiento.put("CompetitiveDialogue", "Diálogo competitivo");
        tiposProcedimiento.put("Minor", "Contrato Menor");
        tiposProcedimiento.put("Negotiated", "Negociado");
        tiposProcedimiento.put("NegotiatedWithoutPublicity", "Negociado sin publicidad");
        tiposProcedimiento.put("NegotiatedWithPublicity", "Negociado con publicidad");
        tiposProcedimiento.put("RegularOpen", "Abierto Ordinario");
        tiposProcedimiento.put("Restricted", "Restringido");
        tiposProcedimiento.put("SimpleOpen", "Abierto Simple");
    }

    public static String obtenerLiPlazoEjecucion(JsonNode actualObj) {
        String plazoEjecucion="";
        if (actualObj.has("pproc:contractTemporalConditions")) {
            JsonNode temporal=actualObj.get("pproc:contractTemporalConditions");
            if (temporal.has("pproc:estimatedDuration")) {
                try {
                    Integer plazo = 0;
                    if (!"".equals(temporal.get("pproc:estimatedDuration").asText()) && (temporal.get("pproc:estimatedDuration").asText()).length() > 1 && (temporal.get("pproc:estimatedDuration").asText()).length() < 10) {
                        Period period = Period.parse(temporal.get("pproc:estimatedDuration").asText());

                        if (period.getYears() > 0) {
                            plazo = period.getYears() * 365;
                        }
                        if (period.getMonths() > 0) {
                            if (plazo > 0) {
                                plazo = plazo + period.getMonths() * 30;
                            } else {
                                plazo = period.getMonths() * 30;
                            }
                        }
                        if (period.getDays() > 0) {
                            if (plazo > 0) {
                                plazo = plazo + period.getDays();
                            } else {
                                plazo = period.getDays();
                            }
                        }
                    }
                    plazoEjecucion = "<li property=\"pproc:estimatedDuration\" datatype=\"xsd:duration\" content=\"" + temporal.get("pproc:estimatedDuration").asText() + "\">"
                            + "<strong>Plazo Ejecuci&oacute;n:</strong> " + plazo + "</li>";
                } catch (IllegalArgumentException e) {
                    plazoEjecucion = "<li><strong>Plazo Ejecuci&oacute;n:</strong> " + temporal.get("pproc:estimatedDuration").asText() + "</li>";
                }
            }
        }
        return plazoEjecucion;
    }

    public static String obtenerLiTituloContrato(JsonNode actualObj) {

        return "<li><strong>T&iacute;tulo del Contrato:</strong> <span property=\"dcterms:title\">" + actualObj.get("dcterms:title").asText() + "</span></li>";
    }

    public static String obtenerLiExpediente(JsonNode actualObj) {
        return "<li><strong>N&uacute;mero de  Expediente:</strong> <span property=\"dcterms:identifier\">" + actualObj.get("dcterms:identifier").asText() + "</span></li>";
    }

    public static String obtenerLiGestor(JsonNode actualObj) {
        String retorno = "";
        if (actualObj.has("pproc:managingDepartment")) {
            JsonNode gestor = actualObj.get("pproc:managingDepartment");
            String uriGestor =  gestor.has("@id") ? " about=\"" + gestor.get("@id").asText() + "\"" : "";

            String idEntidad = gestor.has("@id") ? gestor.get("@id").asText().replace("orgzar:", "") : "";
            String entidad;
            if ("".equals(idEntidad)) {
                entidad = "<span property=\"dcterms:title\"" + uriGestor + ">" + gestor.get("dcterms:title").asText() + "</span>";
            } else {
                entidad = "<a href=\"https://www.zaragoza.es/sede/servicio/organigrama/" + idEntidad + "\"><span property=\"dcterms:title\"" + uriGestor + ">" + gestor.get("dcterms:title").asText() + "</span></a>";
            }

            retorno = "<li rel=\"pproc:managingDepartment\"> <span typeof=\"org:Organization\"><strong>Servicio Gestor:</strong> " + entidad + "</span></li>";
        }
        return retorno;
    }

    public static String obtenerLiOrganoContratacion(JsonNode actualObj) {
        String retorno = "";
        if (actualObj.has("pproc:contractingBody")) {
            JsonNode gestor = actualObj.get("pproc:contractingBody");
            String uriGestor =  gestor.has("@id") ? " about=\"" + gestor.get("@id").asText() + "\"" : "";
            retorno = "<li rel=\"pproc:contractingBody\"> <span typeof=\"org:Organization\"><strong>Organo de contrataci&oacute;n:</strong> <span property=\"dcterms:title\"" + uriGestor + ">" + gestor.get("dcterms:title").asText() + "</span></span></li>";
        }
        return retorno;
    }

    public static String obtenerLiObjeto(JsonNode actualObj) {
        String retorno = "";
        if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:provision")) {
            JsonNode gestor = actualObj.get("pproc:contractObject").get("pproc:provision");
            retorno = "<li rel=\"pproc:contractObject\"><strong>Objeto del contrato:</strong> <span property=\"dcterms:title\">" + gestor.get("dcterms:title").asText() + "</span></li>";
        }
        return retorno;
    }

    public static String obtenerLiTipoContrato(Tipocontrato type) {
        return "<li><strong>Tipo de Contrato:</strong> <span rel=\"rdf:type\" resource=\"pproc:" + type.getType() + "\">" + type.getTitle() + "</span></li>";
    }

    public static String obtenerLiTipoProcedimiento(JsonNode actualObj) {
        JsonNode procedimiento = actualObj.get("pproc:contractProcedureSpecifications");
        String retorno = "";
        if (tiposProcedimiento.get(procedimiento.get("pproc:procedureType").asText().substring(procedimiento.get("pproc:procedureType").asText().indexOf(":") + 1, procedimiento.get("pproc:procedureType").asText().length())) != null) {
            retorno = "<li><strong>Procedimiento:</strong> <span rel=\"pproc:procedureType\" resource=\"" + procedimiento.get("pproc:procedureType").asText() + "\">" + tiposProcedimiento.get(procedimiento.get("pproc:procedureType").asText().substring(procedimiento.get("pproc:procedureType").asText().indexOf(":") + 1, procedimiento.get("pproc:procedureType").asText().length())) + "</span></li>";
        }
        return retorno;
    }

    public static String setContratoMenor(JsonNode actualObj) {
        JsonNode procedimiento = actualObj.get("pproc:contractProcedureSpecifications");
        String retorno = "N";
        if (procedimiento.has("pproc:procedureType")
                && procedimiento.get("pproc:procedureType").asText().indexOf("Minor") >= 0) {
            retorno = "S";
        }
        return retorno;
    }
    public static String obtenerLiUrgencia(String urgente){
        if("S".equals(urgente)){
            return"<li><strong>Tramitación:</strong> <span rel=\"rdf:pproc:urgencyType\" resource=\"pproc:Express\">Urgente</span></li>";
        }else{
            return"<li><strong>Tramitación:</strong> <span rel=\"rdf:pproc:urgencyType\" resource=\"pproc:Express\">Ordinario</span></li>";
        }
    }

    public static String obtenerLiPresupuesto(JsonNode actualObj) {
        String retorno = "";
        if (actualObj.has("pproc:contractObject") && actualObj.get("pproc:contractObject").has("pproc:contractEconomicConditions")
                && actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").has("pproc:budgetPrice")) {
            Iterator<JsonNode> iterator = actualObj.get("pproc:contractObject").get("pproc:contractEconomicConditions").get("pproc:budgetPrice").elements();
            String presupuestoSinIva = "";
            String presupuestoConIva = "";
            while (iterator.hasNext()) {
                JsonNode valor = iterator.next();
                boolean conIva = valor.get("gr:valueAddedTaxIncluded").asBoolean();
                if (conIva) {
                    presupuestoConIva = "<span property=\"gr:hasCurrencyValue\" datatype=\"xsd:float\">" + valor.get("gr:hasCurrencyValue").asText() + "</span>" + ((valor.has("gr:hasCurrency") && valor.get("gr:hasCurrency").asText().indexOf("EUR") >= 0) ? " <span property=\"gr:hasCurrency\" content=\"EUR\">euros</span>" : " " + valor.get("gr:hasCurrency").asText());
                } else {
                    presupuestoSinIva = "<span property=\"gr:hasCurrencyValue\" datatype=\"xsd:float\">" + valor.get("gr:hasCurrencyValue").asText() + "</span>" + ((valor.has("gr:hasCurrency") && valor.get("gr:hasCurrency").asText().indexOf("EUR") >= 0) ? " <span property=\"gr:hasCurrency\" content=\"EUR\">euros</span>" : " " + valor.get("gr:hasCurrency").asText());
                }
            }
            retorno = "<li typeof=\"pproc:BudgetPriceSpecification\"><strong>Presupuesto:</strong> " + presupuestoConIva + " <span property=\"gr:valueAddedTaxIncluded\" datatype=\"xsd:boolean\" content=\"true\">I.V.A. incluido</span></li>";

        }
        return retorno;
    }

    public static String obtenerLiPlazoPresentacionOfertas(JsonNode actualObj) throws ParseException {
        String retorno = "";
        if (actualObj.has ("pproc:contractProcedureSpecifications")) {
            JsonNode especificaciones = actualObj.get("pproc:contractProcedureSpecifications");
            String liPlazoPresentacion = "";
            String liLugarPresentacion = "";
            if (especificaciones.has("pproc:tenderDeadline")) {
                Date fecha = ConvertDate.string2Date(especificaciones.get("pproc:tenderDeadline").asText(), ConvertDate.ISO8601_FORMAT_SIN_ZONA);
                liPlazoPresentacion = "<li>Plazo de presentación. <span property=\"pproc:tenderDeadline\" content=\"" + especificaciones.get("pproc:tenderDeadline").asText() + "\">"
                        + ConvertDate.date2String(fecha, ConvertDate.PERFILCONTRATANTE) + "</span>. No obstante, si el &uacute;ltimo d&iacute;a del plazo fuera inh&aacute;bil, &eacute;ste se entender&aacute; prorrogado al primer d&iacute;a h&aacute;bil siguiente. DISPOSICI&Oacute;N ADICIONAL DECIMOQUINTA. Ley de Contratos del Sector P&uacute;blico</li>";
            }

            if (especificaciones.has("pproc:tenderSubmissionLocation")) {
                String nombreLugar = "";
                if (especificaciones.get("pproc:tenderSubmissionLocation").has("s:name")) {
                    nombreLugar = "<span property=\"s:name\">" + especificaciones.get("pproc:tenderSubmissionLocation").get("s:name").asText() + "</span>";
                }
                String direccionLugar = "";
                if (especificaciones.get("pproc:tenderSubmissionLocation").has("s:address")) {
                    direccionLugar = "<span property=\"s:address\">";
                    if (especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").has("s:streetAddress")) {
                        direccionLugar = direccionLugar + "<span property=\"s:streetAddress\">" + especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").get("s:streetAddress").asText() + "</span>";
                    }
                    if (especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").has("s:postalCode")) {
                        direccionLugar = direccionLugar + ", <span property=\"s:postalCode\">" + especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").get("s:postalCode").asText() + "</span>";
                    }
                    if (especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").has("s:addressLocality")) {
                        direccionLugar = direccionLugar + ", <span property=\"s:addressLocality\">" + especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").get("s:addressLocality").asText() + "</span>";
                    }
                    if (especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").has("s:addressCountry")) {
                        direccionLugar = direccionLugar + ", <span property=\"s:addressCountry\">" + especificaciones.get("pproc:tenderSubmissionLocation").get("s:address").get("s:addressCountry").asText() + "</span>";
                    }
                    direccionLugar = direccionLugar + "</span>";
                }
                if ("".equals(nombreLugar) && "".equals(direccionLugar)) {
                    liLugarPresentacion = "<li>Documentaci&oacute;n a presentar: la indicada en la Memoria Descriptiva o Ficha T&eacute;cnica</li>"
                            + "<li>Lugar de presentaci&oacute;n: En el Servicio Gestor indicado o a trav&eacute;s del Servicio de Correos.</li>";
                } else {
                    liLugarPresentacion = "<li property=\"pproc:tenderSubmissionLocation\">" + nombreLugar + ". " + direccionLugar + "</li>";
                }
            }

            retorno = "<li property=\"pproc:contractProcedureSpecifications\"><strong>Presentaci&oacute;n de las ofertas:</strong> <ul>" + liPlazoPresentacion + liLugarPresentacion + "</ul></li>";
        }
        return retorno;
    }

    public static String obtenerLiAdjudicacion(JsonNode actualObj) throws ParseException {
        String retorno = "";
        if (actualObj.has("pc:tender")) {
            JsonNode especificaciones = obtenerAdjudicatario(actualObj.get("pc:tender"));
            if (especificaciones != null) {
                String liFechaAdjudicacion = "";
                String liAdjudicatario = "";
                String liImporte = "";
                if (especificaciones.has("pproc:awardDate")) {
                    Date fecha = ConvertDate.string2Date(especificaciones.get("pproc:awardDate").asText(), ConvertDate.DATEEN_FORMAT);
                    liFechaAdjudicacion = "<li>Fecha de Adjudicaci&oacute;n. <span property=\"pproc:awardDate\" content=\"" + especificaciones.get("pproc:awardDate").asText() + "\">"
                            + ConvertDate.date2String(fecha, ConvertDate.DATE_FORMAT) + "</span></li>";
                }

                if (especificaciones.has("pc:supplier")) {
                    String nombreAdjudicatario = "";
                    String cifAdjudicatario = "";

                    if (especificaciones.get("pc:supplier").has("s:name")) {
                        nombreAdjudicatario = "<span property=\"s:name\">" + especificaciones.get("pc:supplier").get("s:name").asText() + "</span>";
                    }
                    if (especificaciones.get("pc:supplier").has("org:identifier")) {
                        cifAdjudicatario = "<span property=\"org:identifier\">" + especificaciones.get("pc:supplier").get("org:identifier").asText() + "</span>";
                    }
                    liAdjudicatario = "<li property=\"pc:supplier\">" + nombreAdjudicatario + " - " + cifAdjudicatario + "</li>";
                }

                if (especificaciones.has("pc:offeredPrice")) {
                    Iterator<JsonNode> iterator = especificaciones.get("pc:offeredPrice").elements();
                    String importeSinIva = "";
                    String importeConIva = "";
                    while (iterator.hasNext()) {
                        JsonNode valor = iterator.next();
                        boolean conIva = valor.get("gr:valueAddedTaxIncluded").asBoolean();
                        if (conIva) {
                            importeConIva = "<span property=\"gr:hasCurrencyValue\" datatype=\"xsd:float\">" + valor.get("gr:hasCurrencyValue").asText() + "</span>" + ((valor.has("gr:hasCurrency") && valor.get("gr:hasCurrency").asText().indexOf("EUR") >= 0) ? " euros" : " " + valor.get("gr:hasCurrency").asText());
                        } else {
                            importeSinIva = "<span property=\"gr:hasCurrencyValue\" datatype=\"xsd:float\">" + valor.get("gr:hasCurrencyValue").asText() + "</span>" + ((valor.has("gr:hasCurrency") && valor.get("gr:hasCurrency").asText().indexOf("EUR") >= 0) ? " euros" : " " + valor.get("gr:hasCurrency").asText());
                        }
                    }
                    liImporte = "<li typeof=\"pproc:BudgetPriceSpecification\"><strong>Importe de Adjudicaci&oacute;n:</strong> <span property=\"gr:hasCurrencyValue\">" + importeConIva + "</span> <span property=\"gr:valueAddedTaxIncluded\" datatype=\"xsd:boolean\" content=\"true\">I.V.A. incluido</span></li>";
                }

                retorno = "<li property=\"pc:tender\"><strong>Adjudicaci&oacute;n del contrato:</strong> <ul>" + liFechaAdjudicacion + liAdjudicatario + liImporte + "</ul></li>";
            }
        }
        return retorno;
    }

    private static JsonNode obtenerAdjudicatario(JsonNode jsonNode) {
        JsonNode retorno = null;
        if (jsonNode.isArray()) {
            Iterator<JsonNode> iterator = jsonNode.elements();
            while (iterator.hasNext()) {
                JsonNode valor = iterator.next();
                if (valor.has("@type")) {
                    Iterator<JsonNode> tipos = valor.get("@type").elements();
                    while (tipos.hasNext()) {
                        JsonNode tipoAdj = tipos.next();
                        if ("pproc:AwardedTender".equals(tipoAdj.asText())) {
                            retorno = valor;
                        }
                    }
                }
            }

        } else {
            retorno = jsonNode;
        }
        return retorno;
    }

    public static String obtenerLiInformacionAdicional(JsonNode actualObj) {
        String retorno = "";
        if (actualObj.has("contzar:additionalInfo")) {
            retorno = "<li><strong>Informaci&oacute;n adicional:</strong>" + actualObj.get("contzar:additionalInfo").asText() + "</li>";
        }
        return retorno;
    }

}