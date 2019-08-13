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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Base64;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

@Slf4j
public final class ReportBuilderHCI {

    public enum ReportFormat {
        XLSX, DOCX, PDF, HTML, ODT
    }

//    public static void main(String[] args) throws JRException, IOException {
//        ReportBuilderHCI rb = new ReportBuilderHCI();
//
//        byte[] report = rb.generateReportForHciDemo("DOC", "Tomas", "Male", "876543234567", "987654567", "Klems",
//                "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
//        rb.saveToFile(report, "main_test_testhci2dy.DOCX");
//
//        byte[] report2 = rb.generateReportForHciDemo("PDF", "Tomas", "Male", "876543234567", "987654567", "Klems",
//                "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
//        rb.saveToFile(report2, "main_test_testhci2dy.PDF");
//    }
    
    /**
     * Vygeneruje ukazkovou smlouvu "Labor Contract". Vraci binarni soubor zakodovany jako Base64 string.
     * @param outputFormat String, "PDF" nebo "DOC"
     * @param firstName String
     * @param gender String
     * @param idPassportNo String
     * @param laborCId String
     * @param lastName String
     * @param positionName String
     * @param qqNumber String
     * @param telephone String
     * @param weChatNumber String
     * @param workPlace String
     * @return String, Base64 zakodovany Binary array, finalni PDF nebo DOC  
     * @throws JRException
     * @throws IOException
     */
    public String generateReportForHciDemoB64(String outputFormat, String firstName, String gender, String idPassportNo,
            String laborCId, String lastName, String positionName, String qqNumber, String telephone,
            String weChatNumber, String workPlace) throws JRException, IOException {

        return Base64.getEncoder().encodeToString(generateReportForHciDemo(outputFormat, firstName, gender,
                idPassportNo, laborCId, lastName, positionName, qqNumber, telephone, weChatNumber, workPlace));
    }

    /**
     * Vygeneruje ukazkovou smlouvu "Labor Contract". Vraci binarni soubor jako byte[].
     * @param outputFormat String, "PDF" nebo "DOC"
     * @param firstName String
     * @param gender String
     * @param idPassportNo String
     * @param laborCId String
     * @param lastName String
     * @param positionName String
     * @param qqNumber String
     * @param telephone String
     * @param weChatNumber String
     * @param workPlace String
     * @return byte[] Binary array, finalni PDF nebo DOC  
     * @throws JRException
     * @throws IOException
     */
    public byte[] generateReportForHciDemo(String outputFormat, String firstName, String gender, String idPassportNo,
            String laborCId, String lastName, String positionName, String qqNumber, String telephone,
            String weChatNumber, String workPlace) throws JRException, IOException {

        String jrxmlName = "";
        if ("DOC".contentEquals(outputFormat)) {
            jrxmlName = "hci_page_1_A4.jrxml";
        } else {
            jrxmlName = "hci_page_1_A4_bg.jrxml";
        }
        try (InputStream jrxml = ReportBuilderHCI.class.getClassLoader().getResourceAsStream(jrxmlName)) {

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

            return generateReportFile(reportStream, dataBean, format);

        }
    }

    public byte[] generateReportFile(InputStream reportStream, DataBean dataBean, ReportFormat reportFormat)
            throws JRException {

        JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reportStream);
        if (jasperReport == null) {
            throw new JRException("Cannot build report from stream");
        }

        ArrayList<DataBean> dataBeanList = new ArrayList<DataBean>();
        dataBeanList.add(dataBean);
        JRBeanCollectionDataSource beanColDataSource = new JRBeanCollectionDataSource(dataBeanList);
        Map parameters = new HashMap();
        Locale locale = new Locale("zh", "CN");
        parameters.put(JRParameter.REPORT_LOCALE, locale);

        JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, beanColDataSource);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        JRAbstractExporter exporter = getExporter(reportFormat);

        exporter.setExporterInput(new SimpleExporterInput(jasperPrint));

        exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputStream));
        exporter.exportReport();

        return outputStream.toByteArray();
    }

    private JRAbstractExporter getExporter(ReportFormat reportFormat) {
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

//    private void saveToFile(byte[] report, String name) throws IOException {
//        try (FileOutputStream fileOutputStream = new FileOutputStream(name)) {
//            fileOutputStream.write(report);
//            fileOutputStream.flush();
//        }
//    }
//
//    private void saveToFile(String report, String name) throws IOException {
//        try (PrintWriter out = new PrintWriter(name + ".b64")) {
//            out.println(report);
//        }
//    }
}