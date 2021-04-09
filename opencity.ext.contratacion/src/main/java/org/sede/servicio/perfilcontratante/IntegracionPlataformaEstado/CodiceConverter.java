package org.sede.servicio.perfilcontratante.IntegracionPlataformaEstado;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.xml.XMLConstants;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;


import org.sede.core.anotaciones.ResultsOnly;

import org.sede.core.tag.Utils;
import org.sede.core.utils.Propiedades;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;


@XmlRootElement(name = "codice-converter")
@ResultsOnly(xmlroot = "codice-converter")
public class CodiceConverter {
    private static final Logger LOG = LoggerFactory.getLogger(CodiceConverter.class);
    //	private static String endPoint = "http://rysvirtuosotest.red.zaragoza.es/sparql";
    private static String endPoint = "http://datos.zaragoza.es/sparql";
//	private static final String PATH_RESULTADOS_PRUEBAS = "/home/ob544e/Escritorio/PlataformaContratacion/resultadosPruebas/";
    //private static final String PATH_XSD = "/home/ob544e/Escritorio/PlataformaContratacion/xsd/";

    private static final String PATH_RESULTADOS_PRUEBAS = "/RedAyto/F/seccionweb/Errores_contratos/resultadosPruebas/";
    private static final String PATH_XSD = "/RedAyto/F/seccionweb/Errores_contratos/xsd/";

    public static void main(String[] args) throws Exception {
        String resultado = CodiceConverter.conversor("0267402-17");
        System.out.println(resultado);
        validarXsd(resultado);
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
        } catch (IOException e) {}
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
        Result result = new StreamResult(new java.io.File(PATH_RESULTADOS_PRUEBAS + remove(expediente.replace("/", "-")) + ".xml"));

        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.transform(source, result);

    }
    public static String conversor(String expediente) throws Exception {

//		FileInputStream fisTargetFile = new FileInputStream(new File("/home/ob544e/Escritorio/0055575-16.xml"));
//
//		return IOUtils.toString(fisTargetFile, "UTF-8");

        String query = "PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>"
                + "SELECT DISTINCT ?contrato ?fechainicio ?nombreContrato ?id ?duracion ?authorityName ?bodyName ?legalDocumentReference "
                + "WHERE { ?contrato a pproc:Contract; dcterms:title ?nombreContrato; dcterms:identifier ?id; "
                + "pproc:contractingBody ?contractingBody; "
                + "pc:contractingAuthority ?contractingAuthority. "
                +  "OPTIONAL{?contrato pproc:contractTemporalConditions ?duracionest."
                + "?duracionest pproc:estimatedDuration ?duracion}."
                + "OPTIONAL{?contrato pproc:contractProcedureSpecifications ?procedimiento."
                + " ?procedimiento pproc:tenderDossierStartDate ?fechainicio}."
                + "OPTIONAL{?contrato pproc:duration ?duracion}. OPTIONAL{?contrato pproc:legalDocumentReference ?legalDocumentReference}. "
                + "?contractingAuthority dcterms:title ?authorityName."
                + "?contractingBody dcterms:title ?bodyName." +
                "FILTER regex(?contrato, \"" + expediente + "\", \"i\")}";
        Propiedades.getProxyHost();
        System.out.println(query);
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);

        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            Object contrato = iter.get("contrato");
            Object fechainicio = iter.get("fechainicio");
            Object nombre = iter.get("nombreContrato");
            Object id = iter.get("id");
            Object authorityName = iter.get("authorityName");
            Object bodyName = iter.get("bodyName");
            Object duration = iter.get("duracion");
            Object legalDocumentReference = iter.get("legalDocumentReference");

            ContratoCodice contratoObj = new ContratoCodice(remove(contrato.toString()),remove(id.toString()));
            contratoObj.setTitle(remove(nombre.toString()));
            contratoObj.setFechaInicio(fechainicio.toString());
            contratoObj.setDuration(remove(duration.toString()));
            contratoObj.setContractingAuthority(remove(authorityName.toString()));
            contratoObj.setContractingBody(remove(bodyName.toString()));
            if(legalDocumentReference!=null){
                contratoObj.setLegalDocumentReference(remove(legalDocumentReference.toString()));}
            return processContract(contratoObj);

        } else {
            return null;
        }

    }


    private static String processContract(ContratoCodice cont) throws Exception{
        DocumentBuilderFactory factory = DocumentBuilderFactory
                .newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        DOMImplementation implementation = builder.getDOMImplementation();
        Document document = implementation.createDocument("http://www.w3.org/2005/Atom", "entry", null);
        document.setXmlVersion("1.0");
        document.setXmlStandalone(true);
        int tieneLotes = calcularLotes(cont.getUriContrato());
        int fase = calcularFase(cont.getUriContrato(), tieneLotes);
        ArrayList<ArrayList<String>> infoFinLote = new ArrayList<ArrayList<String>>();
        String fecha=null;
        if(tieneLotes==1){
            infoFinLote=calcularInfoFinLote(cont.getUriContrato());
        }

        String idContrato = cont.getId();
//		String idContrato = "001111/16";

        // Main Node
        Element raiz = document.getDocumentElement();
        raiz.setAttribute("xmlns:app", "http://www.w3.org/2007/app");

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
        Text idValue = document.createTextNode("https://agregacion.contrataciondelsectorpublico.gob.es/coleccion/licitaciones/401/entry/" + idContrato.replace('/', '-'));
        id.appendChild(idValue);
        raiz.appendChild(id);

        //link
        Element linkNode = document.createElement("link");
        linkNode.setAttribute("href", "https://www.zaragoza.es/sede/servicio/contratacion-publica/?expedienteContains="+idContrato.replace('/', '-'));

        raiz.appendChild(linkNode);

        //summary
        Element summaryNode = document.createElement("summary");
        summaryNode.setAttribute("type", "text");
        Text summaryValue = document.createTextNode(getResumen(cont,fase).toString());
        summaryNode.appendChild(summaryValue);
        raiz.appendChild(summaryNode);

        //title
        Element titleMetNode = document.createElement("title");
        Text titleMetValue = document.createTextNode(cont.getTitle());
        titleMetNode.appendChild(titleMetValue);
        raiz.appendChild(titleMetNode);

        //updated
        Element updatedNode = document.createElement("updated");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ssZ");
        Text updatedValue = document.createTextNode((dateFormat.format(Calendar.getInstance().getTime())).replace("+0200", "+02:00").replace("+0100", "+01:00"));
        updatedNode.appendChild(updatedValue);
        raiz.appendChild(updatedNode);

        //ContractFolderStatus
        Element contractFolderStatusNode = document.createElement("cac-place-ext:ContractFolderStatus");
        contractFolderStatusNode.setAttribute("xmlns:cac", "urn:dgpe:names:draft:codice:schema:xsd:CommonAggregateComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cbc", "urn:dgpe:names:draft:codice:schema:xsd:CommonBasicComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cbc-place-ext", "urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonBasicComponents-2");
        contractFolderStatusNode.setAttribute("xmlns:cac-place-ext", "urn:dgpe:names:draft:codice-place-ext:schema:xsd:CommonAggregateComponents-2");
        raiz.appendChild(contractFolderStatusNode);

        raiz.setAttribute("xmlns:dcterms", "http://purl.org/dc/terms/");

        //dcterms:id
        Element idNode = document.createElement("cbc:ContractFolderID");
        Text nodeKeyValue = document.createTextNode(idContrato);
        idNode.appendChild(nodeKeyValue);
        contractFolderStatusNode.appendChild(idNode);

        //status
        Element statusNode = document.createElement("cbc-place-ext:ContractFolderStatusCode");
        statusNode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.04/SyndicationContractFolderStatusCode-2.04.gc");
        Text statusValue = document.createTextNode(calcularEstado(fase));
        statusNode.appendChild(statusValue);
        contractFolderStatusNode.appendChild(statusNode);

        //DIR3 - LocatedContractingParty
        Element locatedContractingPartyNode = document.createElement("cac-place-ext:LocatedContractingParty");
        contractFolderStatusNode.appendChild(locatedContractingPartyNode);

        // URL - Perfil COntratante
        Element buyerProfileNode = document.createElement("cbc:BuyerProfileURIID");
        Text buyerProfileValue = document.createTextNode("https://www.zaragoza.es/ciudad/gestionmunicipal/contratos/ayto/contrato_Avisos?expediente="+cont.getId().replace('/', '-'));
        buyerProfileNode.appendChild(buyerProfileValue);
        locatedContractingPartyNode.appendChild(buyerProfileNode);

        // DIR3 - Party
        Element partyNode = document.createElement("cac:Party");
        locatedContractingPartyNode.appendChild(partyNode);

        // DIR3 - PartyIdentification
        Element partyNameNode = document.createElement("cac:PartyName");
        partyNode.appendChild(partyNameNode);

        // DIR3 - idDIR (preguntar)
        Element idNameNode = document.createElement("cbc:Name");
        //idDIRNode.setAttribute("schemeName", "DIR3");
        Text idDIRValue = document.createTextNode(cont.getContractingBody());
        idNameNode.appendChild(idDIRValue);
        partyNameNode.appendChild(idNameNode);

        Element procurementProject = document.createElement("cac:ProcurementProject");
        contractFolderStatusNode.appendChild(procurementProject);

        //dcterms:title
        Element titleNode = document.createElement("cbc:Name");
        //Nombre del contrato en minusculas
        Text titleValue = document.createTextNode(cont.getTitle());
        titleNode.appendChild(titleValue);
        procurementProject.appendChild(titleNode);

        //TyeCode rdf:type contrato
        Element typeCodeNode = document.createElement("cbc:TypeCode");
        typeCodeNode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.08/ContractCode-2.08.gc");
        cont.setTypes(getTypes(cont.getUriContrato()));
        int tipoContrato=0;
        for (String type:cont.getTypes()){
            tipoContrato=calcularTipoContrato(type.substring(type.indexOf("#")+1));
            if (tipoContrato!=999) break;

        }
        Text typeCodeValue = document.createTextNode(Integer.toString(tipoContrato));
        typeCodeNode.appendChild(typeCodeValue);
        procurementProject.appendChild(typeCodeNode);

        //pproc:contractEconomicConditions
        List<String> amounts = getPresupuesto(cont.getUriContrato());
        if (amounts.size()>0){
//			Element totalAmountNode = document.createElement("cbc:TotalAmount");
//			Text totalAmountValue = document.createTextNode(amounts.get(1));
//			totalAmountNode.appendChild(totalAmountValue);
            Element taxExclusiveAmountNode = document.createElement("cbc:TaxExclusiveAmount");
            taxExclusiveAmountNode.setAttribute("currencyID", "EUR");
            Text taxExclusiveAmountValue = document.createTextNode(amounts.get(0));
            taxExclusiveAmountNode.appendChild(taxExclusiveAmountValue);
            Element budgetNode = document.createElement("cac:BudgetAmount");
//			budgetNode.appendChild(totalAmountNode);
            budgetNode.appendChild(taxExclusiveAmountNode);
            procurementProject.appendChild(budgetNode);
        }

        //pproc:feePrice ¿Que hacemos?
//		String feePrice = getFee(cont.getUriContrato());
//		if (!feePrice.equals("")){
//			System.out.println("FEEEEE:::"+cont.getId()+"::::"+feePrice);
//			Element requiredFeeNode = document.createElement("cbc:RequiredFeeAmount");
//			Text requiredFeeValue = document.createTextNode(feePrice);
//			requiredFeeNode.appendChild(requiredFeeValue);
//			procurementProject.appendChild(requiredFeeNode);
//		}
        //CPV
        String mainObject = getMainObject(cont.getUriContrato());
        if (!mainObject.equals("")){
            //System.out.println("MAINOBJECT::"+mainObject+"||"+cont.getUriContrato());
            Element reCoClaNode = document.createElement("cac:RequiredCommodityClassification");
            procurementProject.appendChild(reCoClaNode);
            Element typeNode = document.createElement("cbc:ItemClassificationCode");
            typeNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.04/CPV2008-2.04.gc");
            Text typeValue = document.createTextNode(mainObject.substring(mainObject.lastIndexOf("-")+1));
            typeNode.appendChild(typeValue);
            reCoClaNode.appendChild(typeNode);
        }
        //NUTS
        Element nutsNode = document.createElement("cac:RealizedLocation");
        procurementProject.appendChild(nutsNode);

        Element nutsbNode = document.createElement("cbc:CountrySubentityCode");
        nutsbNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.0/NUTS-2009.gc");
        Text nutsbValue = document.createTextNode("ES243");
        nutsbNode.appendChild(nutsbValue);
        nutsNode.appendChild(nutsbNode);

        //pproc:estimatedDuration
        Element durationNode = document.createElement("cbc:DurationMeasure");
        String unidad;
        String duracion;
        if (cont.getDuration().indexOf("D") > 0) {
            duracion = cont.getDuration().replace("D", "");
            unidad = "DAY";
        } else if (cont.getDuration().indexOf("Y") > 0) {
            duracion = cont.getDuration().replace("Y", "");
            unidad = "YEAR";
        } else {
            unidad = "MON";
            duracion = cont.getDuration();
        }
        Text durationValue = document.createTextNode(duracion);

        durationNode.setAttribute("unitCode", unidad);
        durationNode.appendChild(durationValue);
        Element plannedPeriodNode = document.createElement("cac:PlannedPeriod");
        plannedPeriodNode.appendChild(durationNode);
        procurementProject.appendChild(plannedPeriodNode);

        //quitar
        Element tenderingTerms = document.createElement("cac:TenderingTerms");
        //contractFolderStatusNode.appendChild(tenderingTerms);

//		//pc:contractingAuthority
//		Element contAutNode = document.createElement("cac:ContractingParty");
//		Text contAutValue = document.createTextNode(cont.getContractingAuthority());
//		contAutNode.appendChild(contAutValue);
//		raiz.appendChild(contAutNode);

        if (tieneLotes == 0) {
            if (fase == 3 || fase == 4 || fase == 5 || fase == 6 || fase == 7) {
                List<String> adjudicatario = getAdjudicatarioInfo(cont
                        .getUriContrato());
                Element resultadoNode = document
                        .createElement("cac:TenderResult");

                Element resultCodeNode = document
                        .createElement("cbc:ResultCode");
                resultCodeNode
                        .setAttribute("listURI",
                                "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                Text resultCodeValue = document
                        .createTextNode(calcularResultCode(fase));
                resultCodeNode.appendChild(resultCodeValue);
                resultadoNode.appendChild(resultCodeNode);

                Element numLicitadoresNode = document
                        .createElement("cbc:ReceivedTenderQuantity");
                Text numLicitadoresValue = document
                        .createTextNode(adjudicatario.size() == 1 ? adjudicatario
                                .get(0) : adjudicatario.get(4));
                numLicitadoresNode.appendChild(numLicitadoresValue);
                resultadoNode.appendChild(numLicitadoresNode);

                if ((fase != 5) && (fase != 6) && (fase != 7)) {
                    Element winningPartyNode = document
                            .createElement("cac:WinningParty");
                    Element partyIdentificationAdNode = document
                            .createElement("cac:PartyIdentification");

                    Element idAdNode = document.createElement("cbc:ID");
                    idAdNode.setAttribute("schemeName", adjudicatario.get(3)
                            .substring(7));
                    Text idAdValue = document.createTextNode(adjudicatario
                            .get(2));
                    idAdNode.appendChild(idAdValue);
                    partyIdentificationAdNode.appendChild(idAdNode);
                    Element partyNameAdNode = document
                            .createElement("cac:PartyName");

                    Element nameAdNode = document.createElement("cbc:Name");
                    Text nameAdValue = document.createTextNode(adjudicatario
                            .get(1));
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
                    Text taxExclusiveAmountValue = document
                            .createTextNode(adjudicatario.get(0));
                    taxAdjAmountNode.appendChild(taxExclusiveAmountValue);
                    AwardedTenderedNode.appendChild(LegalMonetaryNode);
                    LegalMonetaryNode.appendChild(taxAdjAmountNode);

                    resultadoNode.appendChild(AwardedTenderedNode);
                }
                contractFolderStatusNode.appendChild(resultadoNode);
            }
        } else {
            if (fase == 3 || fase == 4 || fase == 5 || fase == 6 || fase == 7 || fase == 8) {
                ArrayList<ArrayList<ArrayList<String>>> adjudicatario = getAdjudicatarioInfoLotes(cont.getUriContrato());
                for (int i=1;i<=adjudicatario.size();i++){
                    //(adjudicatario[i][j].get(0) importe sin iva
                    //(adjudicatario[i][j].get(1) nombre
                    //(adjudicatario[i][j].get(2) cif
                    //(adjudicatario[i][j].get(3) clasificacion
                    //(adjudicatario[i][j].get(4) id
                    int faseLote = calcularFaseLote(adjudicatario.get(i-1).get(0).get(4).toString(),infoFinLote);
                    Element resultadoNode = document
                            .createElement("cac:TenderResult");

                    Element resultCodeNode = document
                            .createElement("cbc:ResultCode");
                    resultCodeNode
                            .setAttribute("listURI",
                                    "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                    Text resultCodeValue = document
                            .createTextNode(calcularResultCode(faseLote));
                    resultCodeNode.appendChild(resultCodeValue);
                    resultadoNode.appendChild(resultCodeNode);

                    Element numLicitadoresNode = document
                            .createElement("cbc:ReceivedTenderQuantity");
                    Text numLicitadoresValue = document
                            .createTextNode(String.valueOf(adjudicatario.get(i-1).size()));
                    numLicitadoresNode.appendChild(numLicitadoresValue);
                    resultadoNode.appendChild(numLicitadoresNode);
                    if ((faseLote != 5) && (faseLote != 6) && (faseLote != 7)) {
                        Element winningPartyNode = document
                                .createElement("cac:WinningParty");
                        Element partyIdentificationAdNode = document
                                .createElement("cac:PartyIdentification");

                        Element idAdNode = document.createElement("cbc:ID");
                        idAdNode.setAttribute("schemeName", (adjudicatario.get(i-1).get(0).get(3).toString()).substring(7));
                        Text idAdValue = document.createTextNode(adjudicatario.get(i-1).get(0).get(2).toString());
                        idAdNode.appendChild(idAdValue);
                        partyIdentificationAdNode.appendChild(idAdNode);
                        Element partyNameAdNode = document
                                .createElement("cac:PartyName");

                        Element nameAdNode = document.createElement("cbc:Name");
                        Text nameAdValue = document.createTextNode(adjudicatario.get(i-1).get(0).get(1).toString());
                        nameAdNode.appendChild(nameAdValue);
                        partyNameAdNode.appendChild(nameAdNode);

                        winningPartyNode.appendChild(partyIdentificationAdNode);
                        winningPartyNode.appendChild(partyNameAdNode);
                        resultadoNode.appendChild(winningPartyNode);

                        Element AwardedTenderedNode = document
                                .createElement("cac:AwardedTenderedProject");
                        Element ProcurementProjectLotIDNode = document
                                .createElement("cbc:ProcurementProjectLotID");
                        Text ProcurementProjectLotIDValue = document.createTextNode((adjudicatario.get(i-1).get(0).get(4).toString()).substring((adjudicatario.get(i-1).get(0).get(4).toString()).indexOf("_L")+2));
                        ProcurementProjectLotIDNode.appendChild(ProcurementProjectLotIDValue);
                        Element LegalMonetaryNode = document
                                .createElement("cac:LegalMonetaryTotal");
                        Element taxAdjAmountNode = document
                                .createElement("cbc:TaxExclusiveAmount");
                        taxAdjAmountNode.setAttribute("currencyID", "EUR");
                        Text taxExclusiveAmountValue = document
                                .createTextNode(adjudicatario.get(i-1).get(0).get(0).toString());
                        taxAdjAmountNode.appendChild(taxExclusiveAmountValue);
                        AwardedTenderedNode.appendChild(ProcurementProjectLotIDNode);
                        AwardedTenderedNode.appendChild(LegalMonetaryNode);
                        LegalMonetaryNode.appendChild(taxAdjAmountNode);

                        resultadoNode.appendChild(AwardedTenderedNode);
                    }else{
                        Element AwardedTenderedNode = document
                                .createElement("cac:AwardedTenderedProject");
                        Element ProcurementProjectLotIDNode = document
                                .createElement("cbc:ProcurementProjectLotID");
                        Text ProcurementProjectLotIDValue = document.createTextNode((adjudicatario.get(i-1).get(0).get(4).toString()).substring((adjudicatario.get(i-1).get(0).get(4).toString()).indexOf("_L")+2));
                        ProcurementProjectLotIDNode.appendChild(ProcurementProjectLotIDValue);
                        AwardedTenderedNode.appendChild(ProcurementProjectLotIDNode);
                        resultadoNode.appendChild(AwardedTenderedNode);
                    }
                    contractFolderStatusNode.appendChild(resultadoNode);
                }
                if(((adjudicatario.size()==0)||((fase==3)&&(adjudicatario.size()<calcularNumLotes(cont.getUriContrato()))))||fase==8){
                    if(fase==5||fase==6||fase==7){
                        Element resultadoNode=null;
                        for(int i=0;i<calcularNumLotes(cont.getUriContrato());i++){
                            resultadoNode = document
                                    .createElement("cac:TenderResult");

                            Element resultCodeNode = document
                                    .createElement("cbc:ResultCode");
                            resultCodeNode
                                    .setAttribute("listURI",
                                            "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                            Text resultCodeValue = document
                                    .createTextNode(calcularResultCode(fase));
                            resultCodeNode.appendChild(resultCodeValue);
                            resultadoNode.appendChild(resultCodeNode);

                            Element numLicitadoresNode = document
                                    .createElement("cbc:ReceivedTenderQuantity");
                            Text numLicitadoresValue = document
                                    .createTextNode("0");
                            numLicitadoresNode.appendChild(numLicitadoresValue);
                            resultadoNode.appendChild(numLicitadoresNode);
                            Element AwardedTenderedNode = document
                                    .createElement("cac:AwardedTenderedProject");
                            Element ProcurementProjectLotIDNode = document
                                    .createElement("cbc:ProcurementProjectLotID");
                            Text ProcurementProjectLotIDValue = document.createTextNode(String.valueOf(i+1));
                            ProcurementProjectLotIDNode.appendChild(ProcurementProjectLotIDValue);
                            AwardedTenderedNode.appendChild(ProcurementProjectLotIDNode);
                            resultadoNode.appendChild(AwardedTenderedNode);
                            contractFolderStatusNode.appendChild(resultadoNode);
                        }
                    }
                    if (fase==8){
                        Element resultadoNode=null;
                        for(int i=0;i<calcularNumLotes(cont.getUriContrato());i++){
                            int faseLote = calcularFaseLote(cont.getId()+"_L"+Integer.toString(i+1),infoFinLote);
                            if (faseLote==5||faseLote==6||faseLote==7){
                                resultadoNode = document
                                        .createElement("cac:TenderResult");

                                Element resultCodeNode = document
                                        .createElement("cbc:ResultCode");
                                resultCodeNode
                                        .setAttribute("listURI",
                                                "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                                Text resultCodeValue = document
                                        .createTextNode(calcularResultCode(faseLote));
                                resultCodeNode.appendChild(resultCodeValue);
                                resultadoNode.appendChild(resultCodeNode);

                                Element numLicitadoresNode = document
                                        .createElement("cbc:ReceivedTenderQuantity");
                                Text numLicitadoresValue = document
                                        .createTextNode("0");
                                numLicitadoresNode.appendChild(numLicitadoresValue);
                                resultadoNode.appendChild(numLicitadoresNode);
                                Element AwardedTenderedNode = document
                                        .createElement("cac:AwardedTenderedProject");
                                Element ProcurementProjectLotIDNode = document
                                        .createElement("cbc:ProcurementProjectLotID");
                                Text ProcurementProjectLotIDValue = document.createTextNode(String.valueOf(i+1));
                                ProcurementProjectLotIDNode.appendChild(ProcurementProjectLotIDValue);
                                AwardedTenderedNode.appendChild(ProcurementProjectLotIDNode);
                                resultadoNode.appendChild(AwardedTenderedNode);
                                contractFolderStatusNode.appendChild(resultadoNode);
                            }
                        }
                    }
                    if(fase==3){
                        Element resultadoNode=null;
                        for(int i=0;i<infoFinLote.size();i++){
                            for (int j=1;j<infoFinLote.get(i).size();j++){
                                if((!(infoFinLote.get(i).get(j).toString()).equals("0"))&&((j!=4)&&(j!=5))){
                                    resultadoNode = document
                                            .createElement("cac:TenderResult");

                                    Element resultCodeNode = document
                                            .createElement("cbc:ResultCode");
                                    resultCodeNode
                                            .setAttribute("listURI",
                                                    "http://contrataciondelestado.es/codice/cl/2.02/TenderResultCode-2.02.gc");
                                    Text resultCodeValue = document
                                            .createTextNode(calcularResultCode(calcularFaseLote("contzar:"+((infoFinLote.get(i).get(0).toString()).substring(infoFinLote.get(i).get(0).toString().indexOf("contrato/")+9)),infoFinLote)));
                                    resultCodeNode.appendChild(resultCodeValue);
                                    resultadoNode.appendChild(resultCodeNode);

                                    Element numLicitadoresNode = document
                                            .createElement("cbc:ReceivedTenderQuantity");
                                    Text numLicitadoresValue = document
                                            .createTextNode("0");
                                    numLicitadoresNode.appendChild(numLicitadoresValue);
                                    resultadoNode.appendChild(numLicitadoresNode);
                                    Element AwardedTenderedNode = document
                                            .createElement("cac:AwardedTenderedProject");
                                    Element ProcurementProjectLotIDNode = document
                                            .createElement("cbc:ProcurementProjectLotID");
                                    Text ProcurementProjectLotIDValue = document.createTextNode((infoFinLote.get(i).get(0).toString()).substring((infoFinLote.get(i).get(0).toString()).indexOf("_L")+2));
                                    ProcurementProjectLotIDNode.appendChild(ProcurementProjectLotIDValue);
                                    AwardedTenderedNode.appendChild(ProcurementProjectLotIDNode);
                                    resultadoNode.appendChild(AwardedTenderedNode);
                                    contractFolderStatusNode.appendChild(resultadoNode);
                                }
                            }
                        }
                    }
                }
            }
        }

        Element tenderingProcess = document
                .createElement("cac:TenderingProcess");
        contractFolderStatusNode.appendChild(tenderingProcess);

        //pproc:urgencyType
//		String urgencyType = getUrgencyType(cont.getUriContrato());
//		if (!urgencyType.equals("")){
//			Element urgencyNode = document.createElement("cbc:UrgencyCode");
//			urgencyNode.setAttribute("languageID", "es");
//			urgencyNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/1.04/DiligenceTypeCode-1.04.gc");
//			urgencyNode.setAttribute("listVersionID", "2006");
//			urgencyNode.setAttribute("name", urgencyType.substring(urgencyType.indexOf("#")+1));
//			Text urgencyValue = document.createTextNode(urgencyType.substring(urgencyType.indexOf("#")+1));
//			urgencyNode.appendChild(urgencyValue);
//			tenderingProcess.appendChild(urgencyNode);
//		}

        //pproc:procedureType
        String procedureType = getProcedureType(cont.getUriContrato());
        if (!procedureType.equals("")){
            Element procedureNode = document.createElement("cbc:ProcedureCode");
            procedureNode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.08/TenderingProcessCode-2.08.gc");
            int tipoProcedimiento=0;
            for (String type:cont.getTypes()){
                tipoProcedimiento=calcularTipoProcedimiento(procedureType.substring(procedureType.indexOf("#")+1));
                if (tipoProcedimiento!=999) break;

            }
            Text procedureValue = document.createTextNode(Integer.toString(tipoProcedimiento));
            procedureNode.appendChild(procedureValue);
            if(tipoProcedimiento==6){
                procedureNode.setAttribute("languageID", "es");
                procedureNode.setAttribute("name", "Contrato Menor");
                procedureNode.setAttribute("listVersionID","2.08");
            }
            tenderingProcess.appendChild(procedureNode);
        }

        //TenderSubmissionDeadlinePeriod
        Element deadlinePeriodNode = document.createElement("cac:TenderSubmissionDeadlinePeriod");
        tenderingProcess.appendChild(deadlinePeriodNode);

        Element endDateNode = document.createElement("cbc:EndDate");
        String fechaFin=getTenderDeadline(cont.getUriContrato());
        Text endDateValue = document.createTextNode(fechaFin.substring(0,fechaFin.indexOf("T")));
        endDateNode.appendChild(endDateValue);
        deadlinePeriodNode.appendChild(endDateNode);

        Element endTimeNode = document.createElement("cbc:EndTime");
        Text endTimeValue = document.createTextNode("13:00:00");
        endTimeNode.appendChild(endTimeValue);
        deadlinePeriodNode.appendChild(endTimeNode);
        //notice
        List<String> noticeInfo = getNoticeInfo(cont.getUriContrato());
        //mirar si quitar if ((noticeInfo.size() > 0) && (fase != 2) && (tieneLotes==0)||((fase != 2) && (tieneLotes==1))) {// anyadido para evitar
        Element noticeInfoNode=null;
        if(tieneLotes==0){
            if(calcularTipoProcedimiento(procedureType.substring(procedureType.indexOf("#") + 1))!=3){
                noticeInfoNode = document
                        .createElement("cac-place-ext:ValidNoticeInfo");

                Element noticeIsusueNode = document
                        .createElement("cbc-place-ext:NoticeIssueDate");
                Text noticeIsusueValue = document.createTextNode(cont.getFechaInicio());
                noticeIsusueNode.appendChild(noticeIsusueValue);
                noticeInfoNode.appendChild(noticeIsusueNode);

                Element noticeTypeNode = document
                        .createElement("cbc-place-ext:NoticeTypeCode");
                noticeTypeNode
                        .setAttribute(
                                "listURI",
                                "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                Text noticeTypeValue = document.createTextNode(tipoAnuncio(1));
                noticeTypeNode.appendChild(noticeTypeValue);
                noticeInfoNode.appendChild(noticeTypeNode);
                for (int i = 0; i < noticeInfo.size(); i = i + 2) {
                    Element addPublicationsNode = document
                            .createElement("cac-place-ext:AdditionalPublicationStatus");
                    noticeInfoNode.appendChild(addPublicationsNode);
                    Element publicationMediaNode = document
                            .createElement("cbc-place-ext:PublicationMediaName");
                    Text publicationMediaValue = document
                            .createTextNode(noticeInfo.get(i));
                    publicationMediaNode.appendChild(publicationMediaValue);
                    addPublicationsNode.appendChild(publicationMediaNode);

                    Element addPubDocNode = document
                            .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                    addPublicationsNode.appendChild(addPubDocNode);

                    Element issueDateNode = document
                            .createElement("cbc:IssueDate");
                    Text issueDateValue = document.createTextNode(noticeInfo
                            .get(i + 1));
                    issueDateNode.appendChild(issueDateValue);
                    addPubDocNode.appendChild(issueDateNode);
                }
                contractFolderStatusNode.appendChild(noticeInfoNode);///MIRAR DONDE UBICARLO
            }
            if ((fase==3)||(fase==4)){
                fecha=calcularFechaAdjudicacion(cont.getUriContrato());
                noticeInfoNode = document
                        .createElement("cac-place-ext:ValidNoticeInfo");

                Element noticeIsusueNode = document
                        .createElement("cbc-place-ext:NoticeIssueDate");
                dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Text noticeIsusueValue = document.createTextNode(fecha);
                noticeIsusueNode.appendChild(noticeIsusueValue);
                noticeInfoNode.appendChild(noticeIsusueNode);

                Element noticeTypeNode = document
                        .createElement("cbc-place-ext:NoticeTypeCode");
                noticeTypeNode.setAttribute(
                        "listURI",
                        "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                Text noticeTypeValue = document.createTextNode(tipoAnuncio(3));
                noticeTypeNode.appendChild(noticeTypeValue);
                noticeInfoNode.appendChild(noticeTypeNode);

                Element addPublicationsNode = document
                        .createElement("cac-place-ext:AdditionalPublicationStatus");
                noticeInfoNode.appendChild(addPublicationsNode);
                Element publicationMediaNode = document
                        .createElement("cbc-place-ext:PublicationMediaName");
                Text publicationMediaValue = document
                        .createTextNode("Perfil de contratante");
                publicationMediaNode.appendChild(publicationMediaValue);
                addPublicationsNode.appendChild(publicationMediaNode);

                Element addPubDocNode = document
                        .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                addPublicationsNode.appendChild(addPubDocNode);

                Element issueDateNode = document
                        .createElement("cbc:IssueDate");
                Text issueDateValue = document.createTextNode(fecha);
                issueDateNode.appendChild(issueDateValue);
                addPubDocNode.appendChild(issueDateNode);

                contractFolderStatusNode.appendChild(noticeInfoNode);///MIRAR DONDE UBICARLO
                if (fase==4){
                    fecha=calcularFechaFormalizacion(cont.getUriContrato());
                    noticeInfoNode = document
                            .createElement("cac-place-ext:ValidNoticeInfo");

                    noticeIsusueNode = document
                            .createElement("cbc-place-ext:NoticeIssueDate");
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    noticeIsusueValue = document.createTextNode(fecha);
                    noticeIsusueNode.appendChild(noticeIsusueValue);
                    noticeInfoNode.appendChild(noticeIsusueNode);

                    noticeTypeNode = document
                            .createElement("cbc-place-ext:NoticeTypeCode");
                    noticeTypeNode
                            .setAttribute(
                                    "listURI",
                                    "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                    noticeTypeValue = document.createTextNode(tipoAnuncio(4));
                    noticeTypeNode.appendChild(noticeTypeValue);
                    noticeInfoNode.appendChild(noticeTypeNode);

                    addPublicationsNode = document
                            .createElement("cac-place-ext:AdditionalPublicationStatus");
                    noticeInfoNode.appendChild(addPublicationsNode);
                    publicationMediaNode = document
                            .createElement("cbc-place-ext:PublicationMediaName");
                    publicationMediaValue = document
                            .createTextNode("Perfil de contratante");
                    publicationMediaNode.appendChild(publicationMediaValue);
                    addPublicationsNode.appendChild(publicationMediaNode);

                    addPubDocNode = document
                            .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                    addPublicationsNode.appendChild(addPubDocNode);

                    issueDateNode = document
                            .createElement("cbc:IssueDate");
                    issueDateValue = document.createTextNode(fecha);
                    issueDateNode.appendChild(issueDateValue);
                    addPubDocNode.appendChild(issueDateNode);

                    contractFolderStatusNode.appendChild(noticeInfoNode);///MIRAR DONDE UBICARLO

                }
            }else{
                if ((fase==5)||(fase==6)||(fase==7)){
                    noticeInfoNode = document
                            .createElement("cac-place-ext:ValidNoticeInfo");

                    Element noticeIsusueNode = document
                            .createElement("cbc-place-ext:NoticeIssueDate");
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    Text noticeIsusueValue = document.createTextNode(dateFormat
                            .format(Calendar.getInstance().getTime()));
                    noticeIsusueNode.appendChild(noticeIsusueValue);
                    noticeInfoNode.appendChild(noticeIsusueNode);

                    Element noticeTypeNode = document
                            .createElement("cbc-place-ext:NoticeTypeCode");
                    noticeTypeNode
                            .setAttribute(
                                    "listURI",
                                    "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                    Text noticeTypeValue = document.createTextNode(tipoAnuncio(fase));
                    noticeTypeNode.appendChild(noticeTypeValue);
                    noticeInfoNode.appendChild(noticeTypeNode);

                    Element addPublicationsNode = document
                            .createElement("cac-place-ext:AdditionalPublicationStatus");
                    noticeInfoNode.appendChild(addPublicationsNode);
                    Element publicationMediaNode = document
                            .createElement("cbc-place-ext:PublicationMediaName");
                    Text publicationMediaValue = document
                            .createTextNode("Perfil de contratante");
                    publicationMediaNode.appendChild(publicationMediaValue);
                    addPublicationsNode.appendChild(publicationMediaNode);

                    Element addPubDocNode = document
                            .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                    addPublicationsNode.appendChild(addPubDocNode);

                    Element issueDateNode = document
                            .createElement("cbc:IssueDate");
                    Text issueDateValue = document.createTextNode(dateFormat
                            .format(Calendar.getInstance().getTime()));
                    issueDateNode.appendChild(issueDateValue);
                    addPubDocNode.appendChild(issueDateNode);
                    contractFolderStatusNode.appendChild(noticeInfoNode);///MIRAR DONDE UBICARLO

                }
            }

        }else{
            noticeInfoNode = document
                    .createElement("cac-place-ext:ValidNoticeInfo");

            Element noticeIsusueNode = document
                    .createElement("cbc-place-ext:NoticeIssueDate");
            Text noticeIsusueValue = document.createTextNode(cont.getFechaInicio());
            noticeIsusueNode.appendChild(noticeIsusueValue);
            noticeInfoNode.appendChild(noticeIsusueNode);

            Element noticeTypeNode = document
                    .createElement("cbc-place-ext:NoticeTypeCode");
            noticeTypeNode
                    .setAttribute(
                            "listURI",
                            "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

            Text noticeTypeValue = document.createTextNode(tipoAnuncio(1));
            noticeTypeNode.appendChild(noticeTypeValue);
            noticeInfoNode.appendChild(noticeTypeNode);
            for (int i = 0; i < noticeInfo.size(); i = i + 2) {
                Element addPublicationsNode = document
                        .createElement("cac-place-ext:AdditionalPublicationStatus");
                noticeInfoNode.appendChild(addPublicationsNode);
                Element publicationMediaNode = document
                        .createElement("cbc-place-ext:PublicationMediaName");
                Text publicationMediaValue = document
                        .createTextNode(noticeInfo.get(i));
                publicationMediaNode.appendChild(publicationMediaValue);
                addPublicationsNode.appendChild(publicationMediaNode);

                Element addPubDocNode = document
                        .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                addPublicationsNode.appendChild(addPubDocNode);

                Element issueDateNode = document
                        .createElement("cbc:IssueDate");
                Text issueDateValue = document.createTextNode(noticeInfo
                        .get(i + 1));
                issueDateNode.appendChild(issueDateValue);
                addPubDocNode.appendChild(issueDateNode);
            }
            if (noticeInfo.size()==0){
                Element addPublicationsNode = document
                        .createElement("cac-place-ext:AdditionalPublicationStatus");
                noticeInfoNode.appendChild(addPublicationsNode);
                Element publicationMediaNode = document
                        .createElement("cbc-place-ext:PublicationMediaName");
                Text publicationMediaValue = document
                        .createTextNode("Perfil de contratante");
                publicationMediaNode.appendChild(publicationMediaValue);
                addPublicationsNode.appendChild(publicationMediaNode);

                Element addPubDocNode = document
                        .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                addPublicationsNode.appendChild(addPubDocNode);

                Element issueDateNode = document
                        .createElement("cbc:IssueDate");
                Text issueDateValue = document.createTextNode(cont.getFechaInicio());
                issueDateNode.appendChild(issueDateValue);
                addPubDocNode.appendChild(issueDateNode);
            }
            contractFolderStatusNode.appendChild(noticeInfoNode);///MIRAR DONDE UBICARLO
            for(int i=0;i<infoFinLote.size();i++){
                int faseLote=calcularFaseLote((infoFinLote.get(i).get(0).toString()).substring(infoFinLote.get(i).get(0).toString().indexOf("trato/")+6),infoFinLote);
                if (!(faseLote == 0)) {
                    noticeInfoNode = document
                            .createElement("cac-place-ext:ValidNoticeInfo");

                    noticeIsusueNode = document
                            .createElement("cbc-place-ext:NoticeIssueDate");
                    dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    for (int j=1;j<infoFinLote.get(i).size();j++){
                        if(!(infoFinLote.get(i).get(j).toString()).equals("0"))
                            fecha=infoFinLote.get(i).get(j).toString();
                    }
                    noticeIsusueValue = document
                            .createTextNode(fecha);
                    noticeIsusueNode.appendChild(noticeIsusueValue);
                    noticeInfoNode.appendChild(noticeIsusueNode);

                    noticeTypeNode = document
                            .createElement("cbc-place-ext:NoticeTypeCode");
                    noticeTypeNode
                            .setAttribute(
                                    "listURI",
                                    "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                    noticeTypeValue = document
                            .createTextNode(tipoAnuncio(faseLote));
                    noticeTypeNode.appendChild(noticeTypeValue);
                    noticeInfoNode.appendChild(noticeTypeNode);

                    Element addPublicationsNode = document
                            .createElement("cac-place-ext:AdditionalPublicationStatus");
                    noticeInfoNode.appendChild(addPublicationsNode);
                    Element publicationMediaNode = document
                            .createElement("cbc-place-ext:PublicationMediaName");
                    Text publicationMediaValue = document
                            .createTextNode("Perfil de contratante");
                    publicationMediaNode.appendChild(publicationMediaValue);
                    addPublicationsNode.appendChild(publicationMediaNode);

                    Element addPubDocNode = document
                            .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                    addPublicationsNode.appendChild(addPubDocNode);

                    Element issueDateNode = document
                            .createElement("cbc:IssueDate");
                    Text issueDateValue = document.createTextNode(fecha);
                    issueDateNode.appendChild(issueDateValue);
                    addPubDocNode.appendChild(issueDateNode);

                    contractFolderStatusNode.appendChild(noticeInfoNode);

                    if(faseLote == 4){
                        noticeInfoNode = document
                                .createElement("cac-place-ext:ValidNoticeInfo");

                        noticeIsusueNode = document
                                .createElement("cbc-place-ext:NoticeIssueDate");
                        dateFormat = new SimpleDateFormat("yyyy-MM-dd");

                        fecha=infoFinLote.get(i).get(4).toString();
                        LOG.error("fecha;;"+fecha);
                        noticeIsusueValue = document
                                .createTextNode(fecha);
                        noticeIsusueNode.appendChild(noticeIsusueValue);
                        noticeInfoNode.appendChild(noticeIsusueNode);

                        noticeTypeNode = document
                                .createElement("cbc-place-ext:NoticeTypeCode");
                        noticeTypeNode
                                .setAttribute(
                                        "listURI",
                                        "http://contrataciondelestado.es/codice/cl/2.03/TenderingNoticeTypeCode-2.03.gc");

                        noticeTypeValue = document
                                .createTextNode(tipoAnuncio(3));
                        noticeTypeNode.appendChild(noticeTypeValue);
                        noticeInfoNode.appendChild(noticeTypeNode);

                        addPublicationsNode = document
                                .createElement("cac-place-ext:AdditionalPublicationStatus");
                        noticeInfoNode.appendChild(addPublicationsNode);
                        publicationMediaNode = document
                                .createElement("cbc-place-ext:PublicationMediaName");
                        publicationMediaValue = document
                                .createTextNode("Perfil de contratante");
                        publicationMediaNode.appendChild(publicationMediaValue);
                        addPublicationsNode.appendChild(publicationMediaNode);

                        addPubDocNode = document
                                .createElement("cac-place-ext:AdditionalPublicationDocumentReference");
                        addPublicationsNode.appendChild(addPubDocNode);

                        issueDateNode = document
                                .createElement("cbc:IssueDate");
                        issueDateValue = document.createTextNode(fecha);
                        issueDateNode.appendChild(issueDateValue);
                        addPubDocNode.appendChild(issueDateNode);

                        contractFolderStatusNode.appendChild(noticeInfoNode);
                    }
                }
            }
        }

//		//rdf:type contrato
//		for (String type:cont.getTypes()){
//			Element typeNode = document.createElement("cbc:ContractingSystemCode");
//			typeNode.setAttribute("languageID", "es");
//			typeNode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/2.02/ContractCode-2.02.gc");
//			typeNode.setAttribute("listVersionID", "2.02");
//			typeNode.setAttribute("name", type.substring(type.indexOf("#")+1));
//			Text typeValue = document.createTextNode(type.substring(type.indexOf("#")+1));
//			typeNode.appendChild(typeValue);
//			tenderingProcess.appendChild(typeNode);
//		}

//		pproc:provision
        List<String> provision = getProvision(cont.getUriContrato());
        if (provision.size()>0){

            Element reqTenderNode = document.createElement("cac:RequestForTenderLine");
            //procurementProject.appendChild(reqTenderNode);
            Element typeNode = document.createElement("cbc:ID");
            Text typeValue = document.createTextNode(provision.get(0));
            typeNode.appendChild(typeValue);
            reqTenderNode.appendChild(typeNode);
            Element itemNode = document.createElement("cac:Item");
            reqTenderNode.appendChild(itemNode);
            Text itemValue = document.createTextNode(provision.get(1));
            Element cbcNameNode = document.createElement("cbc:Name");
            cbcNameNode.appendChild(itemValue);
            itemNode.appendChild(cbcNameNode);
        }

//		pproc:requiredClassification
        List<String> reqClassification = getRequiredClassification(cont.getUriContrato());
        if (reqClassification.size()>0){

            Element tenderQuaReqNode = document.createElement("cac:TendererQualificationRequest");
            tenderingTerms.appendChild(tenderQuaReqNode);
            Element reqBusClaNode = document.createElement("cac:RequiredBusinessClassificationScheme");
            tenderQuaReqNode.appendChild(reqBusClaNode);
            for (int i=0; i<reqClassification.size();i++){
                Element codeValueNode = document.createElement("cbc:CodeValue");
                Text typeValue = document.createTextNode(reqClassification.get(i));
                codeValueNode.appendChild(typeValue);
                reqBusClaNode.appendChild(codeValueNode);
            }
        }

//		pc:awardCriteriaCombination
        AwardCriterion aw = getAwardCriterion(cont.getUriContrato());
        if (aw!=null){
            Element awardMethodTypeCode = document.createElement("cbc:AwardingMethodTypeCode");
            awardMethodTypeCode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/1.04/AwardingTypeCode-1.04.gc");
            awardMethodTypeCode.setAttribute("listVersionID", "2006");
            Element priceEvaluationTypeCode = document.createElement("cbc:PriceEvaluationTypeCode");
            priceEvaluationTypeCode.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/1.04/ValorationTypeCode-1.04.gc");
            priceEvaluationTypeCode.setAttribute("listVersionID", "2006");
            tenderingTerms.appendChild(awardMethodTypeCode);
            tenderingTerms.appendChild(priceEvaluationTypeCode);
            Element awardingTerms = document.createElement("cac:AwardingTerms");
            Element awardingCriteria = document.createElement("cac:AwardingCriteria");
            awardingCriteria.setAttribute("laguageID", "es");
            awardingCriteria.setAttribute("listURI", "https://contrataciondelestado.es/codice/cl/2.0/AwardingCriteriaCode-2.0.gc");
            awardingCriteria.setAttribute("listVersionID", "2.0");
            tenderingTerms.appendChild(awardingTerms);
            awardingTerms.appendChild(awardingCriteria);

            Element weightingAlgorithmCodeNode = document.createElement("cbc:WeightingAlgorithmCode");
            Text weightingAlgoritihmCodeValue = document.createTextNode(aw.getCriterionEvaluationMode());
            weightingAlgorithmCodeNode.appendChild(weightingAlgoritihmCodeValue);
            awardingTerms.appendChild(weightingAlgorithmCodeNode);
            Element descriptionNode = document.createElement("cbc:Description");
            Text descValue = document.createTextNode(aw.getCriterionName());
            descriptionNode.appendChild(descValue);
            awardingCriteria.appendChild(descriptionNode);
            Element weightNode = document.createElement("cbc:WeightNumeric");
            Text weightValue = document.createTextNode(aw.getCriterionWeight());
            weightNode.appendChild(weightValue);
            awardingCriteria.appendChild(weightNode);
            Element calcExpressionNode = document.createElement("cbc:CalculationExpression");
            Text calcExpressionValue = document.createTextNode(aw.getCriterionEvaluationMode());
            calcExpressionNode.appendChild(calcExpressionValue);
            awardingCriteria.appendChild(calcExpressionNode);
        }

        //GuaranteeInformation
        FinancialGuarantee finGu = getGuaranteeInformation(cont.getUriContrato());
        if (finGu != null) {
            if (!finGu.getFinalFinancialGuarantee().equals("") || !finGu.getProvisionalFinancialGuarantee().equals("") ){

                Element requiredFinancialGuarantee = document.createElement("cac:RequiredFinancialGuarantee");
                Element guaranteeTypeCode = document.createElement("cbc:GuaranteeTypeCode");
                guaranteeTypeCode.setAttribute("languageID", "es");
                guaranteeTypeCode.setAttribute("listURI", "http://contrataciondelestado.es/codice/cl/1.04/GuaranteeTypeCode-1.04.gc");
                guaranteeTypeCode.setAttribute("listVersionID", "2006");
                requiredFinancialGuarantee.appendChild(guaranteeTypeCode);
                tenderingTerms.appendChild(requiredFinancialGuarantee);
                Element maxAdvAmo = document.createElement("cbc:MaximumAdvertisementAmount");
                maxAdvAmo.setAttribute("currencyID", "EUR");
                Text maxAdvAmoValue = document.createTextNode(finGu.getAdvertisementAmount());
                maxAdvAmo.appendChild(maxAdvAmoValue);

                Element financialGuarantee = document.createElement("cac:FinancialGuarantee");
                tenderingTerms.appendChild(financialGuarantee);
                if (!finGu.getProvisionalFinancialGuarantee().equals("")){
                    LOG.error("FINGU PROV------->"+cont.getUriContrato());
                    guaranteeTypeCode.setAttribute("name", "Provisional");
                    Text value = document.createTextNode("1");
                    guaranteeTypeCode.appendChild(value);
                    Element liabilityAmount = document.createElement("cbc:LiabilityAmount");
                    financialGuarantee.appendChild(liabilityAmount);
                    liabilityAmount.setAttribute("currencyID", "EUR");
                    Text liaValue = document.createTextNode(finGu.getProvisionalFinancialGuarantee());
                    liabilityAmount.appendChild(liaValue);
                } else {
                    LOG.error("FINGU DEF------->"+cont.getUriContrato());
                    guaranteeTypeCode.setAttribute("name", "Definitiva");
                    Text value = document.createTextNode("2");
                    guaranteeTypeCode.appendChild(value);
                    Element amountRate = document.createElement("cbc:AmountRate");
                    Element constitutionPeriod = document.createElement("cac:ConstitutionPeriod");
                    financialGuarantee.appendChild(amountRate);
                    financialGuarantee.appendChild(constitutionPeriod);
                    Text amRaValue = document.createTextNode(finGu.getFinalFinancialGuarantee());
                    Text consPeValue = document.createTextNode(finGu.getFinalFinancialGuaranteeDuration());
                    amountRate.appendChild(amRaValue);
                    constitutionPeriod.appendChild(consPeValue);
                }
            }
            if (!finGu.getAdvertisementAmount().equals("")){
                LOG.error("FINGU ADVER------->"+cont.getUriContrato());
                Element maximumAdvertisementAmount = document.createElement("cbc:MaximumAdvertisementAmount");
                tenderingTerms.appendChild(maximumAdvertisementAmount);
                maximumAdvertisementAmount.setAttribute("currencyID", "EUR");
                Text value = document.createTextNode(finGu.getAdvertisementAmount());
                maximumAdvertisementAmount.appendChild(value);
            }
        }
        try {
            escribirEnFichero(cont.getId(), document, fase);
        } catch (Exception e) {
            LOG.error("Error al guardar fichero: " + e.getMessage());
        }
        return getStringFromDocument(document);
    }

    private static String calcularResultCode(int fase) {
        if (fase == 3)
            return "8";
        else {
            if (fase == 4)
                return "9";
            else {
                if (fase == 5)
                    return "3";
                else {
                    if (fase == 6)
                        return "5";
                    else {
                        if (fase == 7)
                            return "4";
                        else {
                            if (fase == 8)
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

    private static String calcularEstado(int fase) {
        if (fase == 1)
            return "PUB";
        else {
            if (fase == 2)
                return "EV";
            else {
                if (fase == 3)
                    return "ADJ";
                else {
                    if ((fase == 4) || (fase == 5) || (fase == 6)|| (fase == 7)|| (fase==8))
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
                fecha=remove(iter.get("awardDate").toString());
        }
        return fecha;
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
                fecha=remove(iter.get("formalizedDate").toString());
        }
        return fecha;
    }

    private static int calcularFase(String uriCont, int tieneLotes)
            throws java.text.ParseException {
        // Estados que devuelve:
        // 0: error, no hay fechas asociadas
        // 1: anuncio de licitacion
        // 2: pendiente de adjudicar
        // 3: adjudicacion
        // 4: formalizacion
        // 5: desierta
        // 6: renuncia
        // 7: desistimiento
        System.out.println(uriCont);
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

            int numLotes=calcularNumLotes(uriCont);
            if(numLotes==0){
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
                    }else return 0;
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
                            if(numFormalizada+numWaive+numResignation+numVoid==numLotes){
                                return 8;
                            }else {
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
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + " SELECT DISTINCT ?nombre ?fecha WHERE {"
                + "<"+uriCont+"> pproc:contractProcedureSpecifications ?procedureType."
                + " OPTIONAL {?procedureType pproc:notice ?instrumentos."
                + " ?instrumentos pproc:noticeSite ?nombre;"
                + " pproc:noticeDate ?fecha}."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        while (results.hasNext()) {
            QuerySolution iter = results.next();
            if(iter.get("nombre")!=null)
                output.add(remove(iter.get("nombre").toString()));
            if (iter.get("fecha")!=null)
                output.add(remove(iter.get("fecha").toString()));
        }
        return output;
    }

    private static List<String> getAdjudicatarioInfo(String uriCont) {
        System.out.println("URI: "+uriCont);
        List<String> output = new ArrayList<String>();
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "PREFIX org: <http://www.w3.org/ns/org#>"
                + "PREFIX s: <http://schema.org/>"
                + " SELECT DISTINCT ?adjudicatario ?cif ?clasificacion ?impAdjudicacionSinIVA WHERE {"
                + "<"+uriCont+"> a pproc:Contract;"
                + " pc:tender ?tender."
                + " ?tender pc:supplier ?supplier."
                + " ?supplier s:name ?adjudicatario;"
                + " org:classification ?clasificacion;"
                + " org:identifier ?cif."
                + " ?tender pc:offeredPrice ?offeredPriceVAT."
                + " ?offeredPriceVAT gr:hasCurrencyValue ?impAdjudicacionSinIVA;"
                + " gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + "}";
        String queryLicitadores="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> " +
                "PREFIX pc: <http://purl.org/procurement/public-contracts#>" +
                "SELECT DISTINCT ?tender WHERE {<"+uriCont+">a pproc:Contract;"
                +"pc:tender ?tender.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        QueryExecution x2 = QueryExecutionFactory.sparqlService(endPoint, queryLicitadores);
        ResultSet results2 = x2.execSelect();

        if (results.hasNext()){
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
        if(results2.hasNext()){
            output.add(Integer.toString(count(endPoint, queryLicitadores,"tender")));
        }
        else output.add(Integer.toString(0));
        return output;
    }

    private static ArrayList<ArrayList<ArrayList<String>>> getAdjudicatarioInfoLotes(String uriCont) {
        System.out.println("URI: " + uriCont);
        String importe="";
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
            if(!importe.equals(remove(iter.get("impAdjudicacionSinIVA").toString()))){
                if (iter.get("impAdjudicacionSinIVA") != null){
                    licitador.add(remove(iter.get("impAdjudicacionSinIVA").toString()));
                    importe=remove(iter.get("impAdjudicacionSinIVA").toString());
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
            for (int i=1;i<=output.size();i++){
                ArrayList<String> licitador = new ArrayList<String>();
                if(output.get(i-1).size()>=0){
                    if ((remove(iter.get("id").toString()).equals(output.get(i-1).get(0).get(4)))&&(!remove(iter.get("adjudicatario").toString()).equals(output.get(i-1).get(0).get(1)))){
                        licitador.add(remove(iter.get("adjudicatario").toString()));
                        licitador.add(remove(iter.get("id").toString()));
                        licitador.add("0");
                        output.get(i-1).add(licitador);
                    }

                }else{
                    licitador.add(remove(iter.get("adjudicatario").toString()));
                    licitador.add(remove(iter.get("id").toString()));
                    licitador.add("0");
                    output.get(i-1).add(licitador);
                }
            }
        }
        return output;
    }

    private static int calcularTipoContrato(String tipo) {
        if(tipo.equals("SuppliesContract")){
            return 1;
        }else if (tipo.equals("ServicesContract")){
            return 2;
        }else if (tipo.equals("WorksContract")){
            return 3;
        }else if (tipo.equals("PublicServicesManagementContract")){
            return 21;
        }else if (tipo.equals("PublicWorksConcessionContract")){
            return 31;
        }else if (tipo.equals("PublicPrivatePartnershipContract")){
            return 40;
        }else if (tipo.equals("SpecialAdministrativeContract")){
            return 7;
        }else if (tipo.equals("PrivateContract")){
            return 8;
        }else {
            return 999;
        }
    }

    private static int calcularTipoProcedimiento(String tipo) {
        if(tipo.equals("RegularOpen")){
            return 1;
        }else if (tipo.equals("SimpleOpen")){
            return 999;
        }else if (tipo.equals("Restricted")){
            return 2;
        }else if (tipo.equals("NegotiatedWithPublicity")){
            return 4;
        }else if (tipo.equals("NegotiatedWithoutPublicity")){
            return 3;
        }else if (tipo.equals("Negotiated")){
            return 4;
        }else if (tipo.equals("Minor")){
            return 6;
        }else if (tipo.equals("CompetitiveDialogue")){
            return 5;
        }else {
            return 999;
        }
    }


    private static List<String> getTypes(String uriCont){

        List<String> output =  new ArrayList<String>();

        String query = "SELECT DISTINCT ?type WHERE { <"+uriCont+"> a ?type.}";

        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        while (results.hasNext()) {
            QuerySolution iter = results.next();
            output.add(iter.get("type").toString());
        }

        return output;
    }

    private static String getDuration(String uriCont){

        String output = "";

        String query = "SELECT DISTINCT ?duration WHERE { <"+uriCont+"> pproc:contractTemporalConditions ?z. ?z pproc:estimatedDuration ?duration.} order by asc (?duration)";

        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output = iter.get("duration").toString();
        }

        return output;
    }

    private static List<String> getAmmounts(String uriCont){

        List<String> output = new ArrayList<String>();
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + " SELECT DISTINCT ?impAdjudicacionSinIVA"
                + " ?impAdjudicacionConIVA WHERE {"
                + "<"+uriCont+"> pc:tender ?tender."
                + " ?tender a pproc:FormalizedTender."
                + " ?tender pc:offeredPrice ?offeredPriceNOVAT;"
                + " pc:offeredPrice ?offeredPriceVAT."
                + " ?offeredPriceNOVAT gr:hasCurrencyValue ?impAdjudicacionSinIVA;"
                + " gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + " ?offeredPriceVAT gr:hasCurrencyValue ?impAdjudicacionConIVA;"
                + " gr:valueAddedTaxIncluded \"true\"^^xsd:boolean."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output.add(remove(iter.get("impAdjudicacionSinIVA").toString()));
            output.add(remove(iter.get("impAdjudicacionConIVA").toString()));
        }
        return output;
    }

    private static List<String> getPresupuesto(String uriCont){

        List<String> output = new ArrayList<String>();
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + " SELECT DISTINCT ?presupuestoSinIVA"
                + " ?presupuestoConIVA WHERE {"
                + "<"+uriCont+"> a pproc:Contract;"
                + " pproc:contractObject ?contractObject."
                + " ?contractObject pproc:contractEconomicConditions ?contractEconomicConditions."
                + " OPTIONAL{?contractEconomicConditions pproc:budgetPrice ?budgetPriceNOVAT;"
                + " pproc:budgetPrice ?budgetPriceVAT."
                + " ?budgetPriceNOVAT gr:hasCurrencyValue ?presupuestoSinIVA;"
                + " gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + " ?budgetPriceVAT gr:hasCurrencyValue ?presupuestoConIVA;"
                + " gr:valueAddedTaxIncluded \"true\"^^xsd:boolean.}"
                + "}order by desc(?presupuestoSinIVA)";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output.add(remove(iter.get("presupuestoSinIVA").toString()));
            output.add(remove(iter.get("presupuestoConIVA").toString()));
        }
        return output;
    }

    private static String getFee(String uriCont){

        String output = "";
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "SELECT ?impCanon WHERE {"
                + "<"+uriCont+"> pproc:contractObject ?contractObject."
                + "?contractObject pproc:contractEconomicConditions ?contractEconomicConditions."
                + "?contractEconomicConditions pproc:feePrice ?feePrice."
                + "?feePrice gr:hasCurrencyValue ?impCanon;"
                + "gr:valueAddedTaxIncluded \"false\"^^xsd:boolean."
                + "}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output= remove(iter.get("impCanon").toString());
        }
        return output;
    }

    private static String getUrgencyType(String uriCont){

        String output = "";
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "select distinct ?urgencyType where {<"+uriCont+"> pproc:contractProcedureSpecifications ?procedureType. ?procedureType pproc:urgencyType ?urgencyType.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output= remove(iter.get("urgencyType").toString());
        }
        return output;
    }

    private static String getTenderDeadline(String uriCont){

        String output = "";
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "select distinct ?fecha where {<"+uriCont+"> pproc:contractProcedureSpecifications ?procedureType.?procedureType pproc:tenderDeadline ?fecha.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {
            QuerySolution iter = results.next();
            output= remove(iter.get("fecha").toString());
        }
        return output;
    }

    private static String getProcedureType(String uriCont){

        String output = "";
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX gr: <http://purl.org/goodrelations/v1#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#>"
                + "PREFIX xsd: <http://www.w3.org/2001/XMLSchema#>"
                + "select distinct ?procedureValue where {<"+uriCont+"> pproc:contractProcedureSpecifications ?procedureType. ?procedureType pproc:procedureType ?procedureValue.}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {

            QuerySolution iter = results.next();
            output= remove(iter.get("procedureValue").toString());
        }
        return output;
    }

    private static String getMainObject(String uriCont){

        String output = "";
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "select distinct ?mainObject where {<"+uriCont+"> pproc:contractObject ?contractObject. ?contractObject pproc:mainObject ?mainObject}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {

            QuerySolution iter = results.next();
            output= remove(iter.get("mainObject").toString());
        }
        return output;
    }

    private static List<String> getProvision(String uriCont){

        List<String> output = new ArrayList<String>();
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX dcterms: <http://purl.org/dc/terms/> "
                + "select distinct ?provision ?title where {<"+uriCont+"> pproc:contractObject ?contractObject. ?contractObject pproc:provision  ?provision . ?provision  dcterms:title ?title}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        if (results.hasNext()) {

            QuerySolution iter = results.next();
            output.add(remove(iter.get("provision").toString()));
            output.add(remove(iter.get("title").toString()));
        }
        return output;
    }

    private static List<String> getRequiredClassification(String uriCont){

        List<String> output = new ArrayList<String>();
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "select ?requiredClassification where {<"+uriCont+"> pproc:tenderersRequirements ?z. ?z pproc:requiredClassification ?requiredClassification}";
        QueryExecution x = QueryExecutionFactory.sparqlService(endPoint, query);
        ResultSet results = x.execSelect();

        while (results.hasNext()) {
            QuerySolution iter = results.next();
            output.add(remove(iter.get("requiredClassification").toString()));
        }
        return output;
    }

    private static AwardCriterion getAwardCriterion(String uriCont){
        AwardCriterion output = null;
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "PREFIX pc: <http://purl.org/procurement/public-contracts#> "
                + "select * where {<"+uriCont+"> pc:awardCriteriaCombination ?acc. ?acc pc:awardCriterion ?ac. ?ac pproc:criterionEvaluationMode ?criterionEvaluationMode; pc:criterionWeight ?criterionWeight;pc:criterionName ?criterionName.}";
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
        int numLotes=0;
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

    private static FinancialGuarantee getGuaranteeInformation(String uriCont){
        FinancialGuarantee output = null;
        String query ="PREFIX pproc: <http://contsem.unizar.es/def/sector-publico/pproc#> "
                + "select * where {"
                + "<"+uriCont+"> pproc:contractProcedureSpecifications ?contPS."
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
            if (iter.get("advAm")!=null)
                output.setAdvertisementAmount(remove(iter.get("advAm").toString()));
            if (iter.get("pFG")!=null)
                output.setProvisionalFinancialGuarantee(remove(iter.get("pFG").toString()));
            else if (iter.get("fFG")!=null) {
                output.setFinalFinancialGuarantee(remove(iter.get("fFG").toString()));
                output.setFinalFinancialGuaranteeDuration(remove(iter.get("fFGDuration").toString()));
            }
        }
        return output;
    }

    private static StringBuffer getResumen(ContratoCodice cont, int fase) {

        StringBuffer resumen= new StringBuffer("Id licitación: "+cont.getId()+"; "+cont.getContractingBody());
        if (getPresupuesto(cont.getUriContrato()).size()!=0){
            resumen.append(" Importe: "+getPresupuesto(cont.getUriContrato()).get(0)+"EUR; ");
        }
        String estado = "";
        if (fase == 1)
            estado = "En plazo";
        else {
            if (fase == 2)
                estado = "Pendiente de adjudicación";
            else {
                if (fase == 3)
                    estado = "Adjudicada";
                else {
                    if (fase == 4 || fase==5 || fase==6 || fase==7 || fase==8)
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
        int i=0;
        while (results.hasNext()) {
            QuerySolution iter = results.next();
            ArrayList<String> lote = new ArrayList<String>();
            if (iter.get("lote") != null){
                lote.add(remove(iter.get("lote").toString()));
            }
            if (iter.get("voidDate") != null){
                lote.add(remove(iter.get("voidDate").toString()));
            }else lote.add("0");
            if (iter.get("resignationDate") != null){
                lote.add(remove(iter.get("resignationDate").toString()));
            }else lote.add("0");
            if (iter.get("waiveDate") != null){
                lote.add(remove(iter.get("waiveDate").toString()));
            }else lote.add("0");
            if (iter.get("awardDate") != null){
                lote.add(remove(iter.get("awardDate").toString()));
            }else lote.add("0");
            if (iter.get("formalizedDate") != null){
                lote.add(remove(iter.get("formalizedDate").toString()));
            }else lote.add("0");

            output.add(lote);
        }
        return output;
    }

    private static int calcularFaseLote(String idLote,ArrayList<ArrayList<String>> lotes) {

        for (int i=0; i<lotes.size();i++){
            if((lotes.get(i).get(0).toString()).substring(lotes.get(i).get(0).toString().indexOf("trato/")+6).equals(idLote.substring(idLote.indexOf(":")+1))){
                if(!lotes.get(i).get(1).equals("0")){
                    return 5;
                }else{
                    if(!lotes.get(i).get(2).equals("0")){
                        return 6;
                    }else{
                        if(!lotes.get(i).get(3).equals("0")){
                            return 7;
                        }else{
                            if(!lotes.get(i).get(5).equals("0")){
                                return 4;
                            }else{
                                if(!lotes.get(i).get(4).equals("0")){
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
        //System.out.println(output);
        return output;
    }

}
