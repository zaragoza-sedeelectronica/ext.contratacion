package org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

import org.apache.commons.lang3.StringUtils;
import org.sede.core.utils.ConvertDate;
import org.sede.servicio.perfilcontratante.entity.*;
import org.slf4j.Logger;
import org.sede.servicio.perfilcontratante.dao.ContratoGenericDAO;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.*;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


public class CodiceConverterMenor {
    private static final Logger LOG = LoggerFactory.getLogger(CodiceConverterMenor.class);
    //	private static String endPoint = "http://rysvirtuosotest.red.zaragoza.es/sparql";
    private static String endPoint = "http://datos.zaragoza.es/sparql";
    private static final String PATH_RESULTADOS_PRUEBAS = "/home/documentacionweb/Escritorio/IntegracionContratacionEstado/resultadosPruebas/";
//   private static final String PATH_RESULTADOS_PRUEBAS = "C:\\Users\\piglesias\\Desktop\\Contratos\\";
    //private static final String PATH_XSD = "C:\\Users\\piglesias\\Desktop\\xsd\\";

    //private static final String PATH_RESULTADOS_PRUEBAS = "/RedAyto/F/seccionweb/Errores_contratos/resultadosPruebas/";
    private static final String PATH_XSD = "/RedAyto/F/seccionweb/Errores_contratos/xsd/";
    @Autowired
    private static ContratoGenericDAO dao;

    public static void main(String[] args) throws Exception {
        Contrato con = dao.find(new BigDecimal(4919));
        String resultado = CodiceConverterMenor.conversor(con);
        System.out.println("resultado---->" + resultado);
        validarXsd(resultado);
        //validarXsd("<?xml version=\"1.0\" encoding=\"US-ASCII\"?><entry xmlns=\"http://www.w3.org/2005/Atom\" xmlns:app=\"http://www.w3.org/2007/app\" xmlns:dcterms=\"http://purl.org/dc/terms/\"><author><name>Ayuntamiento de Zaragoza</name></author><id>https://agregacion.contrataciondelsectorpublico.gob.es/coleccion/licitaciones/401/entry/0365170-17</id><link href=\"https://www.zaragoza.es/ciudad/gestionmunicipal/contratos/ayto/contrato_Avisos?expediente=0365170-17\"/><summary type=\"text\">Id licitaci&#243;n: 0365170-17; Consejero de Servicios P&#250;blicos y Personal del Ayuntamiento de Zaragoza Importe: 53719.01EUR; Estado: Adjudicada</summary><title>Suministro veh&#237;culo furg&#243;n mixto 4x4 adaptable para personal y carga para la unidad de salvamento y rescate acu&#225;tico del servicio contra incendios, de salvamento y protecci&#243;n civil</title><updated>2018-01-24T09:59:55+01:00</updated><cac-place-ext:ContractFolderStatus xmlns:cac=\"urn:dgpe:names:draft:codice:schema:xsd:CommonAggregateComponents-2\" xmlns:cac-place-ext=\"urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonAggregateComponents-2\" xmlns:cbc=\"urn:dgpe:names:draft:codice:schema:xsd:CommonBasicComponents-2\" xmlns:cbc-place-ext=\"urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonBasicComponents-2\"><cbc:ContractFolderID>0365170-17</cbc:ContractFolderID><cbc-place-ext:ContractFolderStatusCode listURI=\"https://contrataciondelestado.es/codice/cl/2.04/SyndicationContractFolderStatusCode-2.04.gc\">ADJ</cbc-place-ext:ContractFolderStatusCode><cac-place-ext:LocatedContractingParty><cbc:BuyerProfileURIID>https://www.zaragoza.es/ciudad/gestionmunicipal/contratos/ayto/contrato_Avisos?expediente=0365170-17</cbc:BuyerProfileURIID><cac:Party><cac:PartyName><cbc:Name>Consejero de Servicios P&#250;blicos y Personal del Ayuntamiento de Zaragoza</cbc:Name></cac:PartyName></cac:Party></cac-place-ext:LocatedContractingParty><cac:ProcurementProject><cbc:Name>Suministro veh&#237;culo furg&#243;n mixto 4x4 adaptable para personal y carga para la unidad de salvamento y rescate acu&#225;tico del servicio contra incendios, de salvamento y protecci&#243;n civil</cbc:Name><cbc:TypeCode listURI=\"https://contrataciondelestado.es/codice/cl/2.04/SyndicationContractCode-2.04.gc\">1</cbc:TypeCode><cac:BudgetAmount><cbc:TaxExclusiveAmount currencyID=\"EUR\">53719.01</cbc:TaxExclusiveAmount></cac:BudgetAmount><cac:RequiredCommodityClassification><cbc:ItemClassificationCode listURI=\"http://contrataciondelestado.es/codice/cl/2.04/CPV2008-2.04.gc\">34100000</cbc:ItemClassificationCode></cac:RequiredCommodityClassification><cac:RealizedLocation><cbc:CountrySubentityCode listURI=\"http://contrataciondelestado.es/codice/cl/2.0/NUTS-2009.gc\">ES243</cbc:CountrySubentityCode></cac:RealizedLocation><cac:PlannedPeriod><cbc:DurationMeasure unitCode=\"MON\">P120</cbc:DurationMeasure></cac:PlannedPeriod></cac:ProcurementProject><cac:TenderResult><cbc:ResultCode listURI=\"http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc\">8</cbc:ResultCode><cbc:ReceivedTenderQuantity>1</cbc:ReceivedTenderQuantity><cac:WinningParty><cac:PartyIdentification><cbc:ID schemeName=\"NIF\">A50007301</cbc:ID></cac:PartyIdentification><cac:PartyName><cbc:Name>AGREDA AUTOMOVIL, S.A.</cbc:Name></cac:PartyName></cac:WinningParty><cac:AwardedTenderedProject><cac:LegalMonetaryTotal><cbc:TaxExclusiveAmount currencyID=\"EUR\">53300</cbc:TaxExclusiveAmount></cac:LegalMonetaryTotal></cac:AwardedTenderedProject></cac:TenderResult><cac:TenderingProcess><cbc:ProcedureCode listURI=\"https://contrataciondelestado.es/codice/cl/2.04/SyndicationTenderingProcessCode-2.04.gc\">1</cbc:ProcedureCode><cac:TenderSubmissionDeadlinePeriod><cbc:EndDate>2017-07-10</cbc:EndDate><cbc:EndTime>13:00:00</cbc:EndTime></cac:TenderSubmissionDeadlinePeriod></cac:TenderingProcess><cac-place-ext:ValidNoticeInfo><cbc-place-ext:NoticeIssueDate>2017-06-05</cbc-place-ext:NoticeIssueDate><cbc-place-ext:NoticeTypeCode listURI=\"http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc\">DOC_CN</cbc-place-ext:NoticeTypeCode><cac-place-ext:AdditionalPublicationStatus><cbc-place-ext:PublicationMediaName>BOA</cbc-place-ext:PublicationMediaName><cac-place-ext:AdditionalPublicationDocumentReference><cbc:IssueDate>2017-06-23+02:00</cbc:IssueDate></cac-place-ext:AdditionalPublicationDocumentReference></cac-place-ext:AdditionalPublicationStatus><cac-place-ext:AdditionalPublicationStatus><cbc-place-ext:PublicationMediaName>Perfil del contratante</cbc-place-ext:PublicationMediaName><cac-place-ext:AdditionalPublicationDocumentReference><cbc:IssueDate>2017-06-23+02:00</cbc:IssueDate></cac-place-ext:AdditionalPublicationDocumentReference></cac-place-ext:AdditionalPublicationStatus></cac-place-ext:ValidNoticeInfo><cac-place-ext:ValidNoticeInfo><cbc-place-ext:NoticeIssueDate>2017-09-12+02:00</cbc-place-ext:NoticeIssueDate><cbc-place-ext:NoticeTypeCode listURI=\"http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc\">DOC_CAN_DEF</cbc-place-ext:NoticeTypeCode><cac-place-ext:AdditionalPublicationStatus><cbc-place-ext:PublicationMediaName>Perfil de contratante</cbc-place-ext:PublicationMediaName><cac-place-ext:AdditionalPublicationDocumentReference><cbc:IssueDate>2017-09-12+02:00</cbc:IssueDate></cac-place-ext:AdditionalPublicationDocumentReference></cac-place-ext:AdditionalPublicationStatus></cac-place-ext:ValidNoticeInfo></cac-place-ext:ContractFolderStatus></entry>");
    }

    public static void validarXsd(String xml) {
        System.out.println(xml);
        File schemaFile = new File(PATH_XSD + "CODICE-PLACE-EXT-CommonAggregateComponents-1.0.xsd"); // etc.
        Source xmlFile = new StreamSource(new StringReader(xml));
        SchemaFactory schemaFactory = SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        try {
            Schema schema = schemaFactory.newSchema(schemaFile);
            Validator validator = schema.newValidator();
            validator.validate(xmlFile);
            System.out.println("xml is valid");
        } catch (SAXException e) {
            System.out.println(xmlFile.getSystemId() + " is NOT valid reason:" + e);
        } catch (IOException e) {
        }
    }

    public static String getStringFromDocument(Document doc) throws TransformerException, UnsupportedEncodingException {
        DOMSource domSource = new DOMSource(doc);
        StringWriter writer = new StringWriter();
        StreamResult result = new StreamResult(writer);
        TransformerFactory tf = TransformerFactory.newInstance();
        Transformer transformer = tf.newTransformer();
        // Para que funcione el envio en UTF-8
        transformer.setOutputProperty(OutputKeys.ENCODING, "US-ASCII");
        transformer.transform(domSource, result);
        return writer.toString();
    }

    private static void escribirEnFichero(String expediente, Document document, int fase) throws TransformerException {
        // Write XML
        Source source = new DOMSource(document);
        Result result = new StreamResult(new java.io.File(PATH_RESULTADOS_PRUEBAS + expediente + ".xml"));
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);

    }

    public static String conversor(Contrato contrato) throws Exception {

        ContratoCodice contratoObj = new ContratoCodice(("https://www.zaragoza.es/sede/servicio/contratación-publica/" + contrato.getId()), (contrato.getId().toString()));
        contratoObj.setTitle(contrato.getTitle());
        contratoObj.setFechaInicio(contrato.getPubDate().toString());
        contratoObj.setDuration(contrato.getDuracion().toString());
        contratoObj.setContractingAuthority(contrato.getEntity().getTitle());
        contratoObj.setContractingBody(contrato.getOrganoContratante().getTitle());
        System.out.println("processContrat-->" + contratoObj.toString());
        for (Anuncio anun : contrato.getAnuncios()) {
            if (anun.getType().getId().equals(new BigDecimal(3.0))) {
                contratoObj.setLegalDocumentReference(anun.getUri());
            }
        }
        return processContract(contratoObj, contrato);

    }


    private static String processContract(ContratoCodice cont, Contrato con) throws Exception {
        System.out.println("processContrat-->" + con.getExpediente());
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();
        Document document = implementation.createDocument("http://www.w3.org/2005/Atom", "entry", null);
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);
        String fecha = null;

        // Main Node
        Element raiz = document.getDocumentElement();
        raiz.setAttribute("xmlns:app", "http://www.w3.org/2007/app");
        raiz.setAttribute("xmlns:dcterms", "http://purl.org/dc/terms/");

        // Metadata

        //name
        Element name = document.createElement("name");
        Text nameValue = document.createTextNode("Ayuntamiento de Zaragoza");
        name.appendChild(nameValue);

        //autor
        Element author = document.createElement("author");
        author.appendChild(name);
        raiz.appendChild(author);

        //id
        Element id = document.createElement("id");
        Text idValue = document.createTextNode("https://agregacion.contrataciondelsectorpublico.gob.es/coleccion/licitaciones/401/entry/" + con.getExpediente());
        id.appendChild(idValue);
        raiz.appendChild(id);

        //link
        Element linkNode = document.createElement("link");
        linkNode.setAttribute("href", "https://www.zaragoza.es/sede/servicio/contratacion-publica/" + con.getId());
        //Text linkValue = document.createTextNode("href=\"https://www.zaragoza.es/ciudad/gestionmunicipal/contratos/ayto/contrato_Avisos?expediente="+cont.getId().replace('/', '-')+"\"");
        //linkNode.appendChild(linkValue);
        raiz.appendChild(linkNode);

        //summary
        Element summaryNode = document.createElement("summary");
        summaryNode.setAttribute("type", "text");
        Text summaryValue = document.createTextNode(getResumen(cont, con).toString());
        summaryNode.appendChild(summaryValue);
        raiz.appendChild(summaryNode);

        //title
        Element titleMetNode = document.createElement("title");
        Text titleMetValue = document.createTextNode(con.getTitle());
        titleMetNode.appendChild(titleMetValue);
        raiz.appendChild(titleMetNode);

        //updated
        Element updatedNode = document.createElement("updated");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Text updatedValue = document.createTextNode((dateFormat.format(Calendar.getInstance().getTime())).replace("+0200", "+02:00").replace("+0100", "+01:00"));
        updatedNode.appendChild(updatedValue);
        raiz.appendChild(updatedNode);

        Element contractFolderStatusNode = document.createElement("cac-place-ext:ContractFolderStatus");
        contractFolderStatusNode.setAttribute("xmlns:cac", "urn:dgpe:names:draft:codice:schema:xsd:CommonAggregateComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cbc", "urn:dgpe:names:draft:codice:schema:xsd:CommonBasicComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cbc-place-ext", "urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonBasicComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cac-place-ext", "urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonAggregateComponents-2");
        raiz.appendChild(contractFolderStatusNode);
        Element idNode = document.createElement("cbc:ContractFolderID");
        Text nodeKeyValue = document.createTextNode(con.getExpediente());
        idNode.appendChild(nodeKeyValue);
        contractFolderStatusNode.appendChild(idNode);

        //status
        Element statusNode = document.createElement("cbc-place-ext:ContractFolderStatusCode");
        statusNode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.04/SyndicationContractFolderStatusCode-2.04.gc");
        Text statusValue = document.createTextNode(calcularEstado(con));
        statusNode.appendChild(statusValue);
        contractFolderStatusNode.appendChild(statusNode);

        //DIR3 - LocatedContractingParty
        Element locatedContractingPartyNode = document.createElement("cac-place-ext:LocatedContractingParty");
        contractFolderStatusNode.appendChild(locatedContractingPartyNode);

        // URL - Perfil COntratante
        Element buyerProfileNode = document.createElement("cbc:BuyerProfileURIID");
        Text buyerProfileValue = document.createTextNode("https://www.zaragoza.es/sede/servicio/contratacion-publica/" + con.getId());
        buyerProfileNode.appendChild(buyerProfileValue);
        locatedContractingPartyNode.appendChild(buyerProfileNode);

        // DIR3 - Party
        Element partyNode = document.createElement("cac:Party");
        locatedContractingPartyNode.appendChild(partyNode);

        // DIR3 - PartyIdentification
        Element partyNameNode = document.createElement("cac:PartyName");
        partyNode.appendChild(partyNameNode);
        Element idNameNode = document.createElement("cbc:Name");
        //idNameNode.setAttribute("schemeName", "DIR3");
        Text idDIRValue = document.createTextNode(con.getOrganoContratante().getTitle());
        idNameNode.appendChild(idDIRValue);
        partyNameNode.appendChild(idNameNode);



        //ObjetoContrato--
        Element procurementProject = document.createElement("cac:ProcurementProject");
        contractFolderStatusNode.appendChild(procurementProject);

        if (!"".equals(con.getObjeto())) {
            //dcterms:title
            Element titleNode = document.createElement("cbc:Name");
            //Nombre del contrato en minisculas
            Text titleValue = document.createTextNode(con.getObjeto().toLowerCase());
            titleNode.appendChild(titleValue);
            procurementProject.appendChild(titleNode);
        }else {
            //dcterms:title
            Element titleNode = document.createElement("cbc:Name");
            //Nombre del contrato en minisculas
            Text titleValue = document.createTextNode(con.getTitle().toLowerCase());
            titleNode.appendChild(titleValue);
            procurementProject.appendChild(titleNode);
        }
        //TyeCode rdf:type contrato
        Element typeCodeNode = document.createElement("cbc:TypeCode");
        typeCodeNode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.08/ContractCode-2.08.gc");
        typeCodeNode.setAttribute("listVersionID", "2.08");
        typeCodeNode.setAttribute("languageID", "es");
        typeCodeNode.setAttribute("name", StringUtils.capitalize(con.getType().getTitle()));
        Text typeCodeValue = document.createTextNode(calcularTipoContrato(Integer.valueOf(con.getType().getId().toString())));
        typeCodeNode.appendChild(typeCodeValue);
        procurementProject.appendChild(typeCodeNode);
        //pproc:contractEconomicConditions


        Element budgetNode = document.createElement("cac:BudgetAmount");
        if (con.getValorEstimado() != null) {
            Element estimedAmountNode = document.createElement("cbc:EstimatedOverallContractAmount");
            estimedAmountNode.setAttribute("currencyID", "EUR");
            Text valorEstimado = document.createTextNode(con.getValorEstimado().toString());
            estimedAmountNode.appendChild(valorEstimado);
            budgetNode.appendChild(estimedAmountNode);
        }
        Element totalAmountNode = document.createElement("cbc:TotalAmount");
        totalAmountNode.setAttribute("currencyID", "EUR");
        Text totalAmountValue;
        if (con.getImporteConIVA() != null) {
            totalAmountValue = document.createTextNode(con.getImporteConIVA().toString());
        } else {
            totalAmountValue = document.createTextNode(con.getImporteSinIVA().toString());
        }
        totalAmountNode.appendChild(totalAmountValue);
        Element taxExclusiveAmountNode = document.createElement("cbc:TaxExclusiveAmount");
        taxExclusiveAmountNode.setAttribute("currencyID", "EUR");
        Text taxExclusiveAmountValue = document.createTextNode(con.getImporteSinIVA().toString());
        taxExclusiveAmountNode.appendChild(taxExclusiveAmountValue);
        budgetNode.appendChild(totalAmountNode);
        budgetNode.appendChild(taxExclusiveAmountNode);
        procurementProject.appendChild(budgetNode);
        //CPV
        for (Cpv cpv : con.getCpv()) {
            Element reCoClaNode = document.createElement("cac:RequiredCommodityClassification");
            procurementProject.appendChild(reCoClaNode);
            Element typeNode = document.createElement("cbc:ItemClassificationCode");
            typeNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.04/CPV2008-2.04.gc");
            typeNode.setAttribute("listVersionID", "2.04");
            typeNode.setAttribute("name", cpv.getTitulo());
            Text typeValue = document.createTextNode(cpv.getId().toString().substring(cpv.getId().toString().lastIndexOf("-") + 1));
            typeNode.appendChild(typeValue);
            reCoClaNode.appendChild(typeNode);
            procurementProject.appendChild(reCoClaNode);
        }
        Element nutsNode = document.createElement("cac:RealizedLocation");
        Element nombreCiudad = document.createElement("cbc:CountrySubentity");
        Text ciudad = document.createTextNode("Zaragoza");
        nombreCiudad.appendChild(ciudad);
        nutsNode.appendChild(nombreCiudad);
        Element nutsbCountryNode = document.createElement("cbc:CountrySubentityCode");
        nutsbCountryNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.06/NUTS-2016.gc");
        nutsbCountryNode.setAttribute("listVersionID", "2009");
        nutsbCountryNode.setAttribute("name", "Zaragoza");
        Text nutsbValue = document.createTextNode("ES243");
        nutsbCountryNode.appendChild(nutsbValue);
        nutsNode.appendChild(nutsbCountryNode);
        Element nutAdressbNode = document.createElement("cac:Address");
        Element nutCountrybNode = document.createElement("cac:Country");
        Element nutIdenCountrybNode = document.createElement("cbc:IdentificationCode");
        nutIdenCountrybNode.setAttribute("listURI", "http://docs.oasis-open.org/ubl/os-ubl-2.0/cl/gc/default/CountryIdentificationCode-2.0.gc");
        nutIdenCountrybNode.setAttribute("listVersionID", "0.3");
        nutIdenCountrybNode.setAttribute("name", "España");
        nutIdenCountrybNode.setAttribute("languageID", "es");
        Text nutTexrCountrybNode = document.createTextNode("ES");
        nutIdenCountrybNode.appendChild(nutTexrCountrybNode);
        nutCountrybNode.appendChild(nutIdenCountrybNode);
        Element nutAdressNameNode = document.createElement("cbc:Name");
        Text nutTexrCountryNameNode = document.createTextNode("España");
        nutAdressNameNode.appendChild(nutTexrCountryNameNode);
        nutCountrybNode.appendChild(nutIdenCountrybNode);
        nutCountrybNode.appendChild(nutAdressNameNode);
        nutAdressbNode.appendChild(nutCountrybNode);
        nutsNode.appendChild(nutAdressbNode);
        procurementProject.appendChild(nutsNode);
        //pproc:estimatedDuration
        Element durationNode = document.createElement("cbc:DurationMeasure");
        java.lang.String unidad;
        java.lang.String duracion;
        if (con.getDuracion() != null) {
            duracion = con.getDuracion().toString();
            unidad = "DAY";
            Text durationValue = document.createTextNode(duracion);

            durationNode.setAttribute("unitCode", unidad);
            durationNode.appendChild(durationValue);
            Element plannedPeriodNode = document.createElement("cac:PlannedPeriod");
            plannedPeriodNode.appendChild(durationNode);
            procurementProject.appendChild(plannedPeriodNode);
        }
        if(con.getLotes().size()==0) {
            if (con.getStatus().getId() == 0) {
                Element tenderingTerms = document.createElement("cac:TenderingTerms");
                contractFolderStatusNode.appendChild(tenderingTerms);
                Element awardingMethodTypeCode = document.createElement("cbc:AwardingMethodTypeCode");
                awardingMethodTypeCode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/1.04/AwardingTypeCode-1.04.gc");
                awardingMethodTypeCode.setAttribute("listVersionID", "2006");
                Text typeValue = document.createTextNode("1");
                awardingMethodTypeCode.appendChild(typeValue);
                tenderingTerms.appendChild(awardingMethodTypeCode);

                Element tenderingProcess = document.createElement("cac:TenderingProcess");
                tenderingTerms.appendChild(tenderingProcess);
                Element procedureNode = document.createElement("cbc:ProcedureCode");
                procedureNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.08/TenderingProcessCode-2.08.g");
                procedureNode.setAttribute("languageID", "es");
                procedureNode.setAttribute("listVersionID", "2.08");
                procedureNode.setAttribute("name", con.getType().getTitle());
                int tipoProcedimiento = 0;
                tipoProcedimiento = calcularTipoProcedimiento(con);
                Text procedureValue = document.createTextNode(Integer.toString(tipoProcedimiento));
                procedureNode.appendChild(procedureValue);
                tenderingProcess.appendChild(procedureNode);
                //TenderSubmissionDeadlinePeriod
                Element deadlinePeriodNode = document.createElement("cac:TenderSubmissionDeadlinePeriod");
                tenderingProcess.appendChild(deadlinePeriodNode);
                Element endDateNode = document.createElement("cbc:EndDate");
                String fechaFin = ConvertDate.date2String(con.getFechaPresentacion(), ConvertDate.ISO8601_FORMAT);
                Text endDateValue = document.createTextNode(fechaFin.substring(0, fechaFin.indexOf("T")));
                endDateNode.appendChild(endDateValue);
                deadlinePeriodNode.appendChild(endDateNode);

                Element endTimeNode = document.createElement("cbc:EndTime");
                Text endTimeValue = document.createTextNode("13:00:00");
                endTimeNode.appendChild(endTimeValue);
                deadlinePeriodNode.appendChild(endTimeNode);
                //pproc:urgencyType
                String urgencyType = parseoUrgencia(con.getUrgente());
                if (!urgencyType.equals("")) {
                    Element urgencyNode = document.createElement("cbc:UrgencyCode");
                    urgencyNode.setAttribute("languageID", "es");
                    urgencyNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/1.04/DiligenceTypeCode-1.04.gc");
                    urgencyNode.setAttribute("listVersionID", "2006");
                    urgencyNode.setAttribute("name", urgencyType);
                    Text urgencyValue = document.createTextNode(parseoUrgenciaTitle(urgencyType));
                    urgencyNode.appendChild(urgencyValue);
                    tenderingProcess.appendChild(urgencyNode);
                }
                Element validNoticeInfo = document.createElement("cac-place-ext:ValidNoticeInfo");
                for (Anuncio anun : con.getAnuncios()) {
                    if (anun.getType().getId().equals(new BigDecimal(2.0))) {
                        Element noticeTypeCode = document.createElement("cbc-place-ext:NoticeTypeCode");
                        noticeTypeCode.setAttribute("languageID", "es");
                        noticeTypeCode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.04/TenderingNoticeTypeCode-2.04.gc");
                        noticeTypeCode.setAttribute("listVersionID", "2.04");
                        noticeTypeCode.setAttribute("name", anun.getType().getTitle());
                        Text textNoticeTypeCode = document.createTextNode(tipoAnuncio(1));
                        noticeTypeCode.appendChild(textNoticeTypeCode);
                        Element noticeStatusCode = document.createElement("cbc-place-ext:NoticeStatusCode");
                        Text textNoticeStatusCode = document.createTextNode("DOC_PUB");
                        noticeStatusCode.appendChild(textNoticeStatusCode);

                    }
                }
                if (con.getCriterios().size() > 0) {
                    for (Criterio cri : con.getCriterios()) {
                        if (cri.getTipo().getId().equals(new BigDecimal(1.0))) {

                            Element awardingTerms = document.createElement("cac:AwardingTerms");
                            Element awardingCriteria = document.createElement("cac:AwardingCriteria");
                            Element awardingCriteriaID = document.createElement("cbc:ID");
                            Text awardingCriteriaIDText = document.createTextNode(cri.getTipo().getId().toString());
                            awardingCriteriaID.appendChild(awardingCriteriaIDText);
                            awardingCriteria.appendChild(awardingCriteriaID);
                            Element awardingCriteriaTypeCode = document.createElement("cbc:AwardingCriteriaTypeCode");
                            awardingCriteriaTypeCode.setAttribute("listUri", "https://contrataciondelestado.es/codice/cl/2.08/AwardingCriteriaCode-2.0.gc");
                            awardingCriteriaTypeCode.setAttribute("listVersionID", "2.0");
                            Text awardingCriteriaTypeCodeText = document.createTextNode("OBJ");
                            awardingCriteriaTypeCode.appendChild(awardingCriteriaTypeCodeText);
                            awardingCriteria.appendChild(awardingCriteriaTypeCode);
                            Element awardingCriteriaDescription = document.createElement("cbc:Description");
                            Text awardingCriteriaDescriptionText = document.createTextNode(cri.getTitle());
                            awardingCriteriaDescription.appendChild(awardingCriteriaDescriptionText);
                            awardingCriteria.appendChild(awardingCriteriaDescription);
                            Element awardingCriteriaWight = document.createElement("cbc:WeightNumeric");
                            Text awardingCriteriaWightText = document.createTextNode(cri.getPeso().toString());
                            awardingCriteriaWight.appendChild(awardingCriteriaWightText);
                            awardingCriteria.appendChild(awardingCriteriaWight);
                            awardingTerms.appendChild(awardingCriteria);
                            tenderingProcess.appendChild(awardingTerms);
                        }
                    }
                }
            }else if(con.getStatus().getId()==5 ||con.getStatus().getId()==3 || con.getStatus().getId()==6) {
                Element resultadoNode = document.createElement("cac:TenderResult");
                Integer status = con.getStatus().getId();
                // Estados que devuelve:
                // 0: error, no hay fechas asociadas
                // 1: anuncio de licitacion
                // 2: pendiente de adjudicar
                // 3: adjudicacion
                // 4: formalizacion
                // 5: desierta
                // 6: renuncia
                // 7: desistimiento

                Element resultCodeNode = document.createElement("cbc:ResultCode");
                resultCodeNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                Text resultCodeValue = document.createTextNode(calcularResultCode(con));
                resultCodeNode.appendChild(resultCodeValue);
                resultadoNode.appendChild(resultCodeNode);

                Element numLicitadoresNode = document.createElement("cbc:ReceivedTenderQuantity");
                Text numLicitadoresValue = document.createTextNode(con.getNumLicitadores().toString());
                numLicitadoresNode.appendChild(numLicitadoresValue);
                resultadoNode.appendChild(numLicitadoresNode);
                if (status == 3 || status == 5 || status == 6) {
                    for (Oferta offer : con.getOfertas()) {
                        if (offer.getGanador()) {
                            Element winningPartyNode = document
                                    .createElement("cac:WinningParty");
                            Element partyIdentificationAdNode = document
                                    .createElement("cac:PartyIdentification");

                            Element idAdNode = document.createElement("cbc:ID");
                            idAdNode.setAttribute("schemeName", "NIF");
                            Text idAdValue = document.createTextNode(offer.getEmpresa().getNif());
                            idAdNode.appendChild(idAdValue);
                            partyIdentificationAdNode.appendChild(idAdNode);
                            Element partyNameAdNode = document
                                    .createElement("cac:PartyName");

                            Element nameAdNode = document.createElement("cbc:Name");
                            Text nameAdValue = document.createTextNode(offer.getEmpresa().getNombre());
                            nameAdNode.appendChild(nameAdValue);
                            partyNameAdNode.appendChild(nameAdNode);

                            winningPartyNode.appendChild(partyIdentificationAdNode);
                            winningPartyNode.appendChild(partyNameAdNode);
                            resultadoNode.appendChild(winningPartyNode);

                            Element AwardedTenderedNode = document
                                    .createElement("cac:AwardedTenderedProject");
                            Element LegalMonetaryNode = document
                                    .createElement("cac:LegalMonetaryTotal");
                            Element taxAdjAmountNode = document
                                    .createElement("cbc:TaxExclusiveAmount");
                            taxAdjAmountNode.setAttribute("currencyID", "EUR");
                            Text taxExclusiveAmountOfferValue = document.createTextNode(offer.getImporteSinIVA().toString());
                            taxAdjAmountNode.appendChild(taxExclusiveAmountOfferValue);
                            AwardedTenderedNode.appendChild(LegalMonetaryNode);
                            LegalMonetaryNode.appendChild(taxAdjAmountNode);
                            resultadoNode.appendChild(AwardedTenderedNode);
                        }
                    }
                    contractFolderStatusNode.appendChild(resultadoNode);
                }
            }else if(con.getStatus().getId()==4){

            }else{

            }
        }
        try {
            escribirEnFichero(con.getExpediente(), document, con.getStatus().getId());
        } catch (TransformerException tfe) {
            tfe.printStackTrace();
        } catch (Exception e) {
            LOG.error("Error al guardar fichero: " + e.getMessage());
        }
        return getStringFromDocument(document);
    }

    private static String parseoUrgencia(String urgente) {
        if("N".equals(urgente)){
            return "1";
        }else if("E".equals(urgente)){
            return "3";
        }else{
            return "2";
        }
    }
    private static String parseoUrgenciaTitle(String urgente) {
        if("1".equals(urgente)){
            return "Ordinaria";
        }else if("E".equals(urgente)){
            return "Emergencia";
        }else{
            return "Urgente";
        }
    }

    private static String calcularResultCode(Contrato con) {
        if (con.getStatus().getId() == 3)
            return "8";
        else {
            if (con.getStatus().getId() == 4)
                return "9";
            else {
                if (con.getStatus().getId() == 5)
                    return "3";
                else {
                    if (con.getStatus().getId() == 6)
                        return "5";
                    else {
                        if (con.getStatus().getId() == 7)
                            return "4";
                        else {
                            if (con.getStatus().getId() == 8)
                                return "9";
                            else {
                                return null;
                            }
                        }
                    }
                }
            }
        }
    }

    private static String calcularEstado(Contrato con) {
        if (con.getStatus().getId() == 0)
            return "PUB";
        else {
            if (con.getStatus().getId() == 1)
                return "EV";
            else {
                if (con.getStatus().getId() == 5)
                    return "ADJ";
                else {
                    if ((con.getStatus().getId() == 4) ||  (con.getStatus().getId() == 6) || (con.getStatus().getId() == 7) || (con.getStatus().getId() == 8))
                        return "RES";
                    else {
                        return null;
                    }
                }
            }
        }
    }

    private static String tipoAnuncio(int fase) {
        if (fase == 1)
            return "DOC_CN";
        else {
            if ((fase == 3) || (fase == 5))
                return "DOC_CAN_DEF";
            else {
                if (fase == 4)
                    return "DOC_FORM";
                else {
                    if (fase == 6)
                        return "RENUNCIA";
                    else {
                        if (fase == 7)
                            return "DESISTIMIENTO";
                        else {
                            return null;
                        }
                    }
                }
            }
        }
    }

    private static int calcularLotes(String uriCont) {

        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + " SELECT DISTINCT ?type WHERE {"
                + "<"
                + uriCont
                + "> a ?type.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        int tieneLotes = 0;
        while (results.hasNext() && tieneLotes == 0) {
            QuerySolution iter = results.next();
            if (iter.get("type") != null) {
                System.out.println(iter.get("type").toString());
                if ((iter.get("type").toString()).indexOf("ContractWithLots") != -1)
                    tieneLotes = 1;
                else
                    tieneLotes = 0;
            }

        }
        return tieneLotes;
    }

    private static String calcularFechaAdjudicacion(String uriCont) {

        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "select distinct ?awardDate where {<"
                + uriCont
                + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                + "pc:tender ?tender."
                + "?tender pproc:awardDate ?awardDate.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        String fecha = null;
        if (results.hasNext()) {
            QuerySolution iter = results.next();
            if (iter.get("awardDate") != null)
                fecha = remove(iter.get("awardDate").toString());
        }
        return fecha.substring(0, 10);
    }

    private static String calcularFechaFormalizacion(String uriCont) {

        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "select distinct ?formalizedDate where {<"
                + uriCont
                + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                + "pc:tender ?tender."
                + "?tender pproc:formalizedDate ?formalizedDate.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        String fecha = null;
        if (results.hasNext()) {
            QuerySolution iter = results.next();
            if (iter.get("formalizedDate") != null)
                fecha = remove(iter.get("formalizedDate").toString());
        }
        return fecha;
    }

    private static int calcularFase(String uriCont, int tieneLotes)
            throws java.text.ParseException {
        // Estados que devuelve:
        // 0: error, no hay fechas asociadas
        // 1: anuncio de licitacipn
        // 2: pendiente de adjudicar
        // 3: adjudicacion
        // 4: formalizacion
        // 5: desierta
        // 6: renuncia
        // 7: desistimiento
//        System.out.println(uriCont);
        if (tieneLotes == 0) {
            String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                    + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                    + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                    + "select distinct ?voidDate where {<"
                    + uriCont
                    + "> pproc:procedureVoid ?procedureVoid."
                    + "?procedureVoid pproc:contractingBodyAgreement ?BodyAgreement."
                    + "?BodyAgreement dcterms:date ?voidDate.}";
            QueryExecution x = QueryExecutionFactory.sparqlService(endPoint,
                    query);
            ResultSet results = x.execSelect();
            if (results.hasNext()) {
                return 5;
            } else {
                query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                        + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                        + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                        + "select distinct ?resignationDate where {<"
                        + uriCont
                        + "> pproc:procedureResignation ?procedureResignation."
                        + "?procedureResignation pproc:contractingBodyAgreement ?BodyAgreement."
                        + "?BodyAgreement dcterms:date ?resignationDate.}";
                x = QueryExecutionFactory.sparqlService(endPoint, query);
                results = x.execSelect();
                if (results.hasNext()) {
                    return 6;
                } else {
                    query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                            + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                            + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                            + "select distinct ?waiveDate where {<"
                            + uriCont
                            + "> pproc:procedureWaive ?procedureWaive."
                            + "?procedureWaive pproc:contractingBodyAgreement ?BodyAgreement."
                            + "?BodyAgreement dcterms:date ?waiveDate.}";
                    x = QueryExecutionFactory.sparqlService(endPoint, query);
                    results = x.execSelect();
                    if (results.hasNext()) {
                        return 7;
                    } else {
                        query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                + "select distinct ?formalizedDate where {<"
                                + uriCont
                                + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                                + "pc:tender ?tender."
                                + "?tender pproc:formalizedDate ?formalizedDate.}";
                        x = QueryExecutionFactory
                                .sparqlService(endPoint, query);
                        results = x.execSelect();

                        if (results.hasNext()) {
                            return 4;
                        } else {
                            query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                    + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                    + "select distinct ?awardDate where {<"
                                    + uriCont
                                    + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                                    + "pc:tender ?tender."
                                    + "?tender pproc:awardDate ?awardDate.}";
                            x = QueryExecutionFactory.sparqlService(endPoint,
                                    query);
                            results = x.execSelect();

                            if (results.hasNext()) {
                                return 3;
                            } else {
                                query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                        + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                        + "select distinct ?tenderDossierStartDate ?fechaDeadline where {<"
                                        + uriCont
                                        + ">  a pproc:Contract;"
                                        + "pproc:contractProcedureSpecifications ?procedimiento."
                                        + "?procedimiento pproc:tenderDeadline ?fechaDeadline;"
                                        + "pproc:tenderDossierStartDate ?tenderDossierStartDate.}";
                                x = QueryExecutionFactory.sparqlService(
                                        endPoint, query);
                                results = x.execSelect();

                                if (results.hasNext()) {
                                    QuerySolution iter = results.next();
                                    if (iter.get("fechaDeadline") != null) {
                                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
                                                "yyyy-MM-dd");
                                        Date fecha = null;
                                        fecha = formatoDelTexto.parse(iter.get(
                                                "fechaDeadline").toString());
                                        if (fecha.before(new Date())) {
                                            return 2;
                                        } else
                                            return 1;
                                    }
                                } else {
                                    return 0;
                                }
                            }
                        }
                    }
                }
            }
            return 0;
        } else { // TIENE LOTES

            int numLotes = calcularNumLotes(uriCont);
            if (numLotes == 0) {
                String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                        + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                        + "select distinct ?tenderDossierStartDate ?fechaDeadline where {<"
                        + uriCont
                        + ">  a pproc:Contract;"
                        + "pproc:contractProcedureSpecifications ?procedimiento."
                        + "?procedimiento pproc:tenderDeadline ?fechaDeadline;"
                        + "pproc:tenderDossierStartDate ?tenderDossierStartDate.}";
                QueryExecution x = QueryExecutionFactory.sparqlService(
                        endPoint, query);
                ResultSet results = x.execSelect();

                if (results.hasNext()) {
                    QuerySolution iter = results.next();
                    if (iter.get("fechaDeadline") != null) {
                        SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
                                "yyyy-MM-dd");
                        Date fecha = null;
                        fecha = formatoDelTexto.parse(iter.get(
                                "fechaDeadline").toString());
                        if (fecha.before(new Date())) {
                            return 2;
                        } else
                            return 1;
                    } else return 0;
                } else {
                    return 0;
                }
            } else {
                int numVoid = 0;
                int numWaive = 0;
                int numResignation = 0;
                int numFormalizada = 0;
                String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                        + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                        + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                        + "select distinct ?voidDate ?lote where {<"
                        + uriCont
                        + "> pc:lot ?lote. "
                        + "?lote pproc:procedureVoid ?procedureVoid."
                        + "?procedureVoid pproc:contractingBodyAgreement ?BodyAgreement."
                        + "?BodyAgreement dcterms:date ?voidDate.}";
                QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
                ResultSet results = x.execSelect();
                while (results.hasNext()) {
                    QuerySolution iter = results.next();
                    numVoid++;
                }
                if (numVoid == numLotes) {
                    return 5;
                } else {
                    query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                            + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                            + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                            + "select distinct ?lote ?resignationDate where {<"
                            + uriCont
                            + "> pc:lot ?lote."
                            + "?lote pproc:procedureResignation ?procedureResignation."
                            + "?procedureResignation pproc:contractingBodyAgreement ?BodyAgreement."
                            + "?BodyAgreement dcterms:date ?resignationDate.}";
                    x = QueryExecutionFactory.sparqlService(endPoint, query);
                    results = x.execSelect();
                    while (results.hasNext()) {
                        QuerySolution iter = results.next();
                        numResignation++;
                    }
                    if (numResignation == numLotes) {
                        return 6;
                    } else {
                        query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                                + "select distinct ?waiveDate ?lote where {<"
                                + uriCont
                                + "> pc:lot ?lote."
                                + "?lote pproc:procedureWaive ?procedureWaive."
                                + "?procedureWaive pproc:contractingBodyAgreement ?BodyAgreement."
                                + "?BodyAgreement dcterms:date ?waiveDate.}";
                        x = QueryExecutionFactory
                                .sparqlService(endPoint, query);
                        results = x.execSelect();
                        while (results.hasNext()) {
                            QuerySolution iter = results.next();
                            numWaive++;
                        }
                        if (numWaive == numLotes) {
                            return 7;
                        } else {
                            query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                    + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                    + "select distinct ?formalizedDate ?lote where {<"
                                    + uriCont
                                    + "> pc:lot ?lote."
                                    + "?lote pc:tender ?tender."
                                    + "?tender pproc:formalizedDate ?formalizedDate.}";
                            x = QueryExecutionFactory.sparqlService(endPoint,
                                    query);
                            results = x.execSelect();
                            while (results.hasNext()) {
                                QuerySolution iter = results.next();
                                numFormalizada++;
                            }
                            if (numFormalizada == numLotes) {
                                return 4;
                            }
                            if (numFormalizada + numWaive + numResignation + numVoid == numLotes) {
                                return 8;
                            } else {
                                query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                        + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                        + "select distinct ?awardDate ?lote where {<"
                                        + uriCont
                                        + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                                        + "pc:lot ?lote."
                                        + "?lote pc:tender ?tender."
                                        + "?tender pproc:awardDate ?awardDate.}";
                                x = QueryExecutionFactory.sparqlService(
                                        endPoint, query);
                                results = x.execSelect();

                                if (results.hasNext()) {
                                    return 3;
                                } else {
                                    query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                                            + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                                            + "select distinct ?tenderDossierStartDate ?fechaDeadline where {<"
                                            + uriCont
                                            + ">  a pproc:Contract;"
                                            + "pproc:contractProcedureSpecifications ?procedimiento."
                                            + "?procedimiento pproc:tenderDeadline ?fechaDeadline;"
                                            + "pproc:tenderDossierStartDate ?tenderDossierStartDate.}";
                                    x = QueryExecutionFactory.sparqlService(
                                            endPoint, query);
                                    results = x.execSelect();

                                    if (results.hasNext()) {
                                        QuerySolution iter = results.next();
                                        if (iter.get("fechaDeadline") != null) {
                                            SimpleDateFormat formatoDelTexto = new SimpleDateFormat(
                                                    "yyyy-MM-dd");
                                            Date fecha = null;
                                            fecha = formatoDelTexto.parse(iter
                                                    .get("fechaDeadline")
                                                    .toString());
                                            if (fecha.before(new Date())) {
                                                return 2;
                                            } else
                                                return 1;
                                        }
                                    } else {
                                        return 0;
                                    }
                                }
                            }
                        }
                    }
                }
                return 0;
            }
        }
    }

    private static List<String> getNoticeInfo(String uriCont) {

        List<String> output = new ArrayList<String>();
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + " SELECT DISTINCT ?nombre ?fecha WHERE {"
                + "<" + uriCont + "> pproc:contractProcedureSpecifications ?procedureType."
                + " OPTIONAL {?procedureType pproc:notice ?instrumentos."
                + " ?instrumentos pproc:noticeSite ?nombre;"
                + " pproc:noticeDate ?fecha}."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        while (results.hasNext()) {
            QuerySolution iter = results.next();
            if (iter.get("nombre") != null)
                output.add(remove(iter.get("nombre").toString()));
            if (iter.get("fecha") != null)
                output.add(remove(iter.get("fecha").toString()));
        }
        return output;
    }

    private static List<String> getAdjudicatarioInfo(String uriCont) {
        System.out.println("URI: " + uriCont);
        List<String> output = new ArrayList<String>();
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX org: <http://www.w3.org/ns/org#>"
                + "PREFIX s: <http://schema.org/>"
                + " SELECT DISTINCT ?adjudicatario ?cif  ?impAdjudicacionSinIVA WHERE {"
                + "<" + uriCont + "> a pproc:Contract;"
                + " pc:tender ?tender."
                + " ?tender pc:supplier ?supplier."
                + " ?supplier s:name ?adjudicatario;"
                //+ " org:classification ?clasificacion;"
                + " org:identifier ?cif."
                + " ?tender pc:offeredPrice ?offeredPriceVAT."
                + " ?offeredPriceVAT gr:hasCurrencyValue ?impAdjudicacionSinIVA;"
                + " gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + "}";
        String queryLicitadores = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> " +
                "PREFIX pc: <http://purl.org/procurement/public-contracts#>" +
                "SELECT DISTINCT ?tender WHERE {<" + uriCont + ">a pproc:Contract;"
                + "pc:tender ?tender.}";
        System.out.println("query " + query);
        System.out.println("Licitadores " + queryLicitadores);
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        QueryExecution x2 = QueryExecutionFactory.sparqlService(endPoint, queryLicitadores);
        ResultSet results2 = x2.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            // if(iter.get("formalizedDate")!=null)
            // output.add(remove(iter.get("formalizedDate").toString()));
            if (iter.get("impAdjudicacionSinIVA") != null)
                output.add(remove(iter.get("impAdjudicacionSinIVA").toString()));
            if (iter.get("adjudicatario") != null)
                output.add(remove(iter.get("adjudicatario").toString()));
            if (iter.get("cif") != null)
                output.add(remove(iter.get("cif").toString()));
            if (iter.get("clasificacion") != null)
                output.add(remove(iter.get("clasificacion").toString()));
        }
        if (results2.hasNext()) {
            output.add(Integer.toString(count(endPoint, queryLicitadores, "tender")));
        } else output.add(Integer.toString(0));
        return output;
    }

    private static ArrayList<ArrayList<ArrayList<String>>> getAdjudicatarioInfoLotes(String uriCont) {
        System.out.println("URI: " + uriCont);
        String importe = "";
        //List<List> output = new ArrayList<List>();
        ArrayList<ArrayList<ArrayList<String>>> output = new ArrayList<ArrayList<ArrayList<String>>>();
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX org: <http://www.w3.org/ns/org#>"
                + "PREFIX s: <http://schema.org/>"
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + " SELECT DISTINCT ?adjudicatario ?id ?cif ?clasificacion ?impAdjudicacionSinIVA ?awardDate WHERE {"
                + "<"
                + uriCont
                + "> a pproc:Contract;"
                + " pc:lot ?lote."
                + " ?lote pc:tender ?tender."
                + " ?lote dcterms:identifier ?id."
                + " ?tender pc:supplier ?supplier."
                + " ?tender pproc:awardDate ?awardDate."
                + " ?supplier s:name ?adjudicatario;"
                + " org:classification ?clasificacion;"
                + " org:identifier ?cif."
                + " ?tender pc:offeredPrice ?offeredPriceVAT."
                + " ?offeredPriceVAT gr:hasCurrencyValue ?impAdjudicacionSinIVA;"
                + " gr:valueAddedTaxIncluded \"false\"^^xsd:boolean." + "}";
        String queryLicitadores = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX s: <http://schema.org/>"
                + "SELECT DISTINCT ?id ?adjudicatario WHERE {<"
                + uriCont
                + "> a pproc:Contract;"
                + " pc:lot ?lote."
                + " ?lote pc:tender ?tender."
                + " ?lote dcterms:identifier ?id."
                + " ?tender pc:supplier ?supplier."
                + " ?supplier s:name ?adjudicatario.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        QueryExecution x2 = QueryExecutionFactory.sparqlService(endPoint,
                queryLicitadores);
        ResultSet results2 = x2.execSelect();

        while (results.hasNext()) {
            ArrayList<ArrayList<String>> lote = new ArrayList<ArrayList<String>>();
            ArrayList<String> licitador = new ArrayList<String>();
            QuerySolution iter = results.next();
            if (!importe.equals(remove(iter.get("impAdjudicacionSinIVA").toString()))) {
                if (iter.get("impAdjudicacionSinIVA") != null) {
                    licitador.add(remove(iter.get("impAdjudicacionSinIVA").toString()));
                    importe = remove(iter.get("impAdjudicacionSinIVA").toString());
                }
                if (iter.get("adjudicatario") != null)
                    licitador.add(remove(iter.get("adjudicatario").toString()));
                if (iter.get("cif") != null)
                    licitador.add(remove(iter.get("cif").toString()));
                if (iter.get("clasificacion") != null)
                    licitador.add(remove(iter.get("clasificacion").toString()));
                if (iter.get("id") != null)
                    licitador.add(remove(iter.get("id").toString()));
                licitador.add("1"); //marco que este licitador es adjudicatario
                lote.add(licitador);
                output.add(lote);
            }
        }
        while (results2.hasNext()) {
            QuerySolution iter = results2.next();
            for (int i = 1; i <= output.size(); i++) {
                ArrayList<String> licitador = new ArrayList<String>();
                if (output.get(i - 1).size() >= 0) {
                    if ((remove(iter.get("id").toString()).equals(output.get(i - 1).get(0).get(4))) && (!remove(iter.get("adjudicatario").toString()).equals(output.get(i - 1).get(0).get(1)))) {
                        licitador.add(remove(iter.get("adjudicatario").toString()));
                        licitador.add(remove(iter.get("id").toString()));
                        licitador.add("0");
                        output.get(i - 1).add(licitador);
                    }

                } else {
                    licitador.add(remove(iter.get("adjudicatario").toString()));
                    licitador.add(remove(iter.get("id").toString()));
                    licitador.add("0");
                    output.get(i - 1).add(licitador);
                }
            }
        }
        return output;
    }

    private static String calcularTipoContrato(Integer tipo) {
        switch(tipo){
            case 1:
            case 6:return "3";
            case 2:return "2";
            case 3:return "1";
            case 5:return "8";
            case 7:return "31";
            case 8:return "21";
            default:return "999";
        }
    }

    private static int calcularTipoProcedimiento(Contrato con) {
        switch (Integer.valueOf(con.getProcedimiento().getId().toString())) {
            case 1:
                return 1;
            case 2:
                return 2;

            case 3:
                return 3;

            case 4:
                return 4;

            case 5:
                return 5;

            case 10:
                return 6;

            default:
                return 999;
        }

    }









    private static String getFee(String uriCont) {

        String output = "";
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?impCanon WHERE {"
                + "<" + uriCont + "> pproc:contractObject ?contractObject."
                + "?contractObject pproc:contractEconomicConditions ?contractEconomicConditions."
                + "?contractEconomicConditions pproc:feePrice ?feePrice."
                + "?feePrice gr:hasCurrencyValue ?impCanon;"
                + "gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output = remove(iter.get("impCanon").toString());
        }
        return output;
    }











    private static List<String> getRequiredClassification(String uriCont) {

        List<String> output = new ArrayList<String>();
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "select ?requiredClassification where {<" + uriCont + "> pproc:tenderersRequirements ?z. ?z pproc:requiredClassification ?requiredClassification}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        while (results.hasNext()) {
            QuerySolution iter = results.next();
            output.add(remove(iter.get("requiredClassification").toString()));
        }
        return output;
    }

    private static AwardCriterion getAwardCriterion(String uriCont) {
        AwardCriterion output = null;
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#> "
                + "select * where {<" + uriCont + "> pc:awardCriteriaCombination ?acc. ?acc pc:awardCriterion ?ac. ?ac pproc:criterionEvaluationMode ?criterionEvaluationMode; pc:criterionWeight ?criterionWeight;pc:criterionName ?criterionName.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            output = new AwardCriterion();
            QuerySolution iter = results.next();
            output.setCriterionEvaluationMode(remove(iter.get("criterionEvaluationMode").toString()));
            output.setCriterionName(remove(iter.get("criterionName").toString()));
            output.setCriterionWeight(remove(iter.get("criterionWeight").toString()));
        }
        return output;
    }

    private static int calcularNumLotes(String uriCont) {
        int numLotes = 0;
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#>"
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX dcterms: <http://purl.org/dc/terms/>"
                + "select distinct ?lote where {<"
                + uriCont
                + "> pproc:contractProcedureSpecifications ?contractProcedureSpecifications;"
                + "pc:lot ?lote.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint,
                query);
        ResultSet results = x.execSelect();
        while (results.hasNext()) {
            QuerySolution iter = results.next();
            numLotes++;
        }
        return numLotes;
    }

    private static FinancialGuarantee getGuaranteeInformation(String uriCont) {
        FinancialGuarantee output = null;
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "select * where {"
                + "<" + uriCont + "> pproc:contractProcedureSpecifications ?contPS."
                + "?contPS pproc:contractAdditionalObligations ?contAO."
                + "?contAO pproc:advertisementAmount ?advAm."
                + "OPTIONAL {?contAO pproc:finalFinancialGuarantee ?fFG. ?contAO pproc:finalFinancialGuaranteeDuration ?fFGDuration}."
                + "OPTIONAL {?contAO pproc:provisionalFinancialGuarantee ?pFG}."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            output = new FinancialGuarantee();
            QuerySolution iter = results.next();
            if (iter.get("advAm") != null)
                output.setAdvertisementAmount(remove(iter.get("advAm").toString()));
            if (iter.get("pFG") != null)
                output.setProvisionalFinancialGuarantee(remove(iter.get("pFG").toString()));
            else if (iter.get("fFG") != null) {
                output.setFinalFinancialGuarantee(remove(iter.get("fFG").toString()));
                output.setFinalFinancialGuaranteeDuration(remove(iter.get("fFGDuration").toString()));
            }
        }
        return output;
    }

    private static StringBuffer getResumen(ContratoCodice cont, Contrato con) {

        StringBuffer resumen = new StringBuffer("Id licitación: " + cont.getId() + "; " + cont.getContractingBody());
        if (con.getImporteSinIVA() != null) {
            resumen.append(" Importe: " + con.getImporteSinIVA() + "EUR; ");
        }
        String estado = "";
        if (con.getStatus().getId() == 1)
            estado = "En plazo";
        else {
            if (con.getStatus().getId() == 2)
                estado = "Pendiente de adjudicación";
            else {
                if (con.getStatus().getId() == 3)
                    estado = "Adjudicada";
                else {
                    if (con.getStatus().getId() == 4 || con.getStatus().getId() == 5 || con.getStatus().getId() == 6 || con.getStatus().getId() == 7 || con.getStatus().getId() == 8)
                        estado = "Resuelta";
//					else {
//						if (fase == 5)
//							estado = "Desierta";
//						else {
//							if (fase == 6)
//								estado = "Renuncia";
//							else {
//								if (fase == 7)
//									estado = "Desistimiento";
//							}
//						}
//					}
                }
            }
        }
        resumen.append("Estado: " + estado);
        return resumen;
    }

    private static ArrayList<ArrayList<String>> calcularInfoFinLote(String uriCont) {

        ArrayList<ArrayList<String>> output = new ArrayList<ArrayList<String>>();
        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "select distinct ?lote ?voidDate ?resignationDate ?waiveDate ?awardDate ?formalizedDate where {<"
                + uriCont
                + "> pc:lot ?lote. "
                + "OPTIONAL{?lote pproc:procedureVoid ?procedureVoid. "
                + "?procedureVoid pproc:contractingBodyAgreement ?BodyAgreementV. "
                + "?BodyAgreementV dcterms:date ?voidDate.} "
                + "OPTIONAL{?lote pproc:procedureResignation ?procedureResignation. "
                + "?procedureResignation pproc:contractingBodyAgreement ?BodyAgreementR. "
                + "?BodyAgreementR dcterms:date ?resignationDate.} "
                + "OPTIONAL{?lote pproc:procedureWaive ?procedureWaive. "
                + "?procedureWaive pproc:contractingBodyAgreement ?BodyAgreementW. "
                + "?BodyAgreementW dcterms:date ?waiveDate.} "
                + "OPTIONAL{?lote pc:tender ?tender. "
                + "?tender pproc:awardDate ?awardDate.} "
                + "OPTIONAL{?lote pc:tender ?tender. "
                + "?tender pproc:formalizedDate ?formalizedDate.}} ";

        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        int i = 0;
        while (results.hasNext()) {
            QuerySolution iter = results.next();
            ArrayList<String> lote = new ArrayList<String>();
            if (iter.get("lote") != null) {
                lote.add(remove(iter.get("lote").toString()));
            }
            if (iter.get("voidDate") != null) {
                lote.add(remove(iter.get("voidDate").toString()));
            } else lote.add("0");
            if (iter.get("resignationDate") != null) {
                lote.add(remove(iter.get("resignationDate").toString()));
            } else lote.add("0");
            if (iter.get("waiveDate") != null) {
                lote.add(remove(iter.get("waiveDate").toString()));
            } else lote.add("0");
            if (iter.get("awardDate") != null) {
                lote.add(remove(iter.get("awardDate").toString()));
            } else lote.add("0");
            if (iter.get("formalizedDate") != null) {
                lote.add(remove(iter.get("formalizedDate").toString()));
            } else lote.add("0");

            output.add(lote);
        }
        return output;
    }

    private static int calcularFaseLote(String idLote, ArrayList<ArrayList<String>> lotes) {

        for (int i = 0; i < lotes.size(); i++) {
            if ((lotes.get(i).get(0).toString()).substring(lotes.get(i).get(0).toString().indexOf("trato/") + 6).equals(idLote.substring(idLote.indexOf(":") + 1))) {
                if (!lotes.get(i).get(1).equals("0")) {
                    return 5;
                } else {
                    if (!lotes.get(i).get(2).equals("0")) {
                        return 6;
                    } else {
                        if (!lotes.get(i).get(3).equals("0")) {
                            return 7;
                        } else {
                            if (!lotes.get(i).get(5).equals("0")) {
                                return 4;
                            } else {
                                if (!lotes.get(i).get(4).equals("0")) {
                                    return 3;
                                }
                            }
                        }
                    }
                }
            }
        }
        return 0;
    }

    private static int count(String endPoint, String query, String fieldId) {
        List<String> fieldList = new ArrayList<String>();
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();
        while (results.hasNext()) {
            QuerySolution iter = results.next();
            Object element = iter.get(fieldId);
            if (!fieldList.contains(element.toString()))
                fieldList.add(element.toString());
        }
        return fieldList.size();
    }


    // Remove the ^^xsd:*** sufix of the data when it's present
    private static String remove(String obj) {
        String output = obj;
        if (obj.contains("^^"))
            output = obj.substring(0, obj.indexOf("^^"));
        System.out.println(output);
        return output;
    }

}
