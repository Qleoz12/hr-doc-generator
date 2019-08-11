package cz.ibm.hci;

import lombok.extern.slf4j.Slf4j;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.HtmlExporter;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.oasis.JROdtExporter;
import net.sf.jasperreports.engine.export.ooxml.JRDocxExporter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimplePdfExporterConfiguration;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public final class ReportBuilderHCI {

    public enum ReportFormat {
        XLSX,
        DOCX,
        PDF,
        HTML,
        ODT
    }
    
    public static String generateReportForHciDemoB64(
    		String outputFormat,
    		String firstName, String gender, String idPassportNo, String laborCId, 
    		String lastName, String positionName, String qqNumber, String telephone, String weChatNumber, 
    		String workPlace) 
    				throws JRException, IOException {
    	
    	return Base64.getEncoder().encodeToString( ReportBuilderHCI.generateReportForHciDemo(outputFormat, firstName, gender, idPassportNo, 
    			laborCId, lastName, positionName, qqNumber, telephone, weChatNumber, workPlace));    	
    }
    
    public static byte[] generateReportForHciDemo(
    		String outputFormat,
    		String firstName, String gender, String idPassportNo, String laborCId, 
    		String lastName, String positionName, String qqNumber, String telephone, String weChatNumber, 
    		String workPlace) 
    				throws JRException, IOException {
    	
    	try (InputStream jrxml = ReportBuilderHCI.class.getClassLoader().getResourceAsStream("hci_page_1_A4.jrxml")) {
            
            DataBean dataBean = new DataBean();
            dataBean.setFirstName(firstName);
            dataBean.setGender(gender);
            dataBean.setIdPassportNumber(idPassportNo);
            dataBean.setLaborContractId(laborCId);
            dataBean.setLastName(lastName);
            dataBean.setPositionName(positionName);
            dataBean.setQqNumber(qqNumber);
            dataBean.setTelephone(telephone);
            dataBean.setWeChatNumber(weChatNumber);
            dataBean.setWorkPlace(workPlace);
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(jrxml, byteArrayOutputStream);
            
            InputStream reportStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            
            ReportBuilderHCI.ReportFormat format;
            if ("DOC".contentEquals(outputFormat)) {
				format = ReportBuilderHCI.ReportFormat.DOCX;
			} else {
				format = ReportBuilderHCI.ReportFormat.PDF;
			}
            
            return ReportBuilderHCI.generateReportFile(reportStream, dataBean, format);
            
        }
    }

    public static byte[] generateReportFile(InputStream reportStream, DataBean dataBean,
                                            ReportFormat reportFormat) throws JRException {
    	
        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        if (jasperReport == null) {
            throw new JRException("Cannot build report from stream");
        }

        ArrayList<DataBean> dataBeanList = new ArrayList<DataBean>();
        dataBeanList.add(dataBean);        
        JRBeanCollectionDataSource beanColDataSource =  new JRBeanCollectionDataSource(dataBeanList);
        Map parameters = new HashMap();
        
        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRAbstractExporter exporter = getExporter(reportFormat);
        
        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();
        
        return outputStream.toByteArray();
    }

    private static JRAbstractExporter getExporter(ReportFormat reportFormat) {
        switch (reportFormat) {
            case DOCX: {
                return new JRDocxExporter();
            }
            case XLSX: {
                return new JRXlsxExporter();
            }
            case PDF: {
                JRPdfExporter exporter = new JRPdfExporter();
                SimplePdfExporterConfiguration configuration = new SimplePdfExporterConfiguration();
                configuration.setCompressed(true);
                exporter.setConfiguration(configuration);
                return exporter;
            }
            case HTML:
            	return new HtmlExporter();
            	
            case ODT:
            	return new JROdtExporter();
            	
            default:
                throw new IllegalArgumentException("Unknown export type " + reportFormat);

        }
    }
}