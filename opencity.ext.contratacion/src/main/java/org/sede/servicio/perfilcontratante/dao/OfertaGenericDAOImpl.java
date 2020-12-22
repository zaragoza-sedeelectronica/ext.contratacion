package org.sede.servicio.perfilcontratante.dao;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.googlecode.genericdao.dao.jpa.GenericDAOImpl;


import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Iterator;

import com.googlecode.genericdao.search.Search;
import com.googlecode.genericdao.search.SearchResult;
import org.apache.commons.io.IOUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.sede.core.anotaciones.Esquema;
import org.sede.core.dao.BooleanConverter;
import org.sede.core.dao.BooleanSINOConverter;
import org.sede.core.dao.JPAIgnoreTraversableResolver;
import org.sede.servicio.perfilcontratante.entity.Contrato;
import org.sede.servicio.perfilcontratante.entity.Empresa;
import org.sede.servicio.perfilcontratante.entity.Estado;
import org.sede.servicio.perfilcontratante.entity.Oferta;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Query;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Repository
@Transactional(Esquema.TMPERFILCONTRATANTE)
public class OfertaGenericDAOImpl extends GenericDAOImpl<Oferta, BigDecimal> implements OfertaGenericDAO {
    private static final Logger logger = LoggerFactory.getLogger(OfertaGenericDAO.class);
    @Autowired
    public ContratoGenericDAO dao;
    @Autowired
    public EmpresaGenericDAO daoEmpresa;
    @PersistenceContext(unitName=Esquema.PERFILCONTRATANTE)
    public void setEntityManager(EntityManager entityManager) {
        this.setEm(entityManager);
    }
    
    public Set<ConstraintViolation<Object>> validar(Object registro) {
        ValidatorFactory factory = Validation.byDefaultProvider().configure().traversableResolver(new JPAIgnoreTraversableResolver()).buildValidatorFactory();
        Validator validator = factory.getValidator();
        return validator.validate(registro);
    }
    @SuppressWarnings("deprecation")
    @Override
    public List<Oferta> loadFromXls( InputStream pathXls) throws IOException {
        HSSFWorkbook wb = null;
        List<Oferta> ofertas=new ArrayList<Oferta>();
        String expediente="";
        Boolean ganador=false;
        try {
            List<String> cabeceras=new ArrayList<String>();
            HashMap<String,String> datos= new HashMap<String, String>();
            POIFSFileSystem fs = new POIFSFileSystem(pathXls);
            wb = new HSSFWorkbook(fs);
            HSSFSheet hssfSheet = wb.getSheetAt(0);
            HSSFRow hssfRow;
            HSSFCell cell;
            String cellValue;
            int rows; // No of rows
            rows = hssfSheet.getLastRowNum();
            int cols = 0; // No of columns
            int tmp = 0;
            // This trick ensures that we get the data properly even if it doesn't start from first few rows
            for (int r = 0; r <= rows; r++) {
                hssfRow = hssfSheet.getRow(r);
                Oferta ofer = new Oferta();
                if (hssfRow == null) {
                    break;
                } else {
                    if (r == 0) {
                        for (int c = 0; c < hssfRow.getLastCellNum(); c++) {
                            cellValue = hssfRow.getCell(c) == null ? "" :
                                    (hssfRow.getCell(c).getCellType() == CellType.STRING) ? hssfRow.getCell(c).getStringCellValue() :
                                            (hssfRow.getCell(c).getCellType() == CellType.NUMERIC) ? "" + hssfRow.getCell(c).getNumericCellValue() :
                                                    (hssfRow.getCell(c).getCellType() == CellType.BOOLEAN) ? "" + hssfRow.getCell(c).getBooleanCellValue() :
                                                            (hssfRow.getCell(c).getCellType() == CellType.BLANK) ? "BLANK" :
                                                                    (hssfRow.getCell(c).getCellType() == CellType.FORMULA) ? "FORMULA" :
                                                                            (hssfRow.getCell(c).getCellType() == CellType.ERROR) ? "ERROR" : "";
                            if(!cellValue.equals("BLANK"))
                                datos.put(cellValue.toString(),String.valueOf(c));
                        }

                    } else {
                        for (int c = 0; c < 8; c++) {
                            if (c == 0) {
                                expediente = hssfRow.getCell(c).getStringCellValue();
                                Search busqueda = new Search(Contrato.class);
                                busqueda.addFilterEqual("expediente", expediente);
                                SearchResult<Contrato> contrato = dao.searchAndCount(busqueda);
                                for (Contrato item : contrato.getResult()) {
                                    ofer.setContrato(item);
                                }

                            }else if(c==1) {
                                String nif=hssfRow.getCell(c).getStringCellValue();
                                Search busquedaEmpresa=new Search(Empresa.class);
                                busquedaEmpresa.addFilterEqual("nif",nif);
                                SearchResult<Empresa> empresa=daoEmpresa.searchAndCount(busquedaEmpresa);
                                if(empresa.getTotalCount()>0) {
                                    for (Empresa item : empresa.getResult()) {
                                        ofer.setEmpresa(item);
                                    }
                                }else{
                                    Empresa nuevaEmpresa=new Empresa();
                                    nuevaEmpresa.setNif((nif));
                                    nuevaEmpresa.setNombre(hssfRow.getCell(2).getStringCellValue());
                                    daoEmpresa.save(nuevaEmpresa);
                                    daoEmpresa.flush();
                                    ofer.setEmpresa(nuevaEmpresa);
                                }
                            }else if(c==2) {
                                String adjudicatario=hssfRow.getCell(c).getStringCellValue();
                                if("".equals(adjudicatario)){
                                    ofer.setAdjudicatario(adjudicatario);

                                }
                            }else if(c==3) {
                                Date fechaAdjudicacion=hssfRow.getCell(c).getDateCellValue();
                                if(fechaAdjudicacion!=null) {
                                    ofer.setFechaAdjudicacion(fechaAdjudicacion);
                                    Estado estado = new Estado();
                                    estado.setId(5);
                                    ofer.getContrato().setStatus(estado);
                                }


                            }else if(c==4) {
                                Date fechaFormalizacion=hssfRow.getCell(c).getDateCellValue();
                                if(fechaFormalizacion!=null) {
                                    ofer.setFechaFormalizacion(fechaFormalizacion);
                                    ofer.getContrato().setFechaContrato(fechaFormalizacion);
                                    Estado estado = new Estado();
                                    estado.setId(6);
                                    ofer.getContrato().setStatus(estado);
                                }
                            }else if(c==5) {
                                Double importeSinIva=hssfRow.getCell(c).getNumericCellValue();
                                if(importeSinIva!=null) {
                                    ofer.setImporteSinIVA(BigDecimal.valueOf(importeSinIva));
                                }
                            }else if(c==6) {
                                Double importeConIva=hssfRow.getCell(c).getNumericCellValue();
                                if(importeConIva!=null) {
                                        ofer.setImporteConIVA(BigDecimal.valueOf(importeConIva));
                                }
                            }else if(c==7) {
                                String value = hssfRow.getCell(c).getStringCellValue();
                                if (value.equals("Si")) {
                                    ganador=true;
                                }
                                ofer.setGanador(ganador);
                                ganador=false;
                            }
                        }
                    }
                }
                if(r!=0) {
                    if(ofer.getFechaFormalizacion()==null)
                        ofer.getContrato().setFechaContrato(ofer.getFechaAdjudicacion());
                    else
                        ofer.getContrato().setFechaContrato(ofer.getFechaFormalizacion());
                    ofertas.add(ofer);
                }
            }


            return ofertas;

        } catch(Exception ioe) {
            logger.error(ioe.getMessage());
        } finally {
            try {
                wb.close();
            } catch (IOException ex) {
                logger.error("Error al procesar el fichero después de cerrarlo: " + ex);
            }
        }

        return ofertas;
    }
    @Override
    public Contrato loadFromJson(String input) throws IOException {
        JsonNode actualObjLote;
        boolean lotesBolean=false;

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        JsonNode actualObj = mapper.readTree(input);
        System.out.println(actualObj.get("dcterms:identifier").asText());
        Search busqueda=new Search();
        busqueda.addFilterEqual("expediente",actualObj.get("dcterms:identifier").asText());
        SearchResult<Contrato> contratos=dao.searchAndCount(busqueda);


        return contratos.getResult().get(0);




    }
}
