package cz.ibm.hci;


import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import org.junit.Ignore;
import org.junit.Test;

import java.io.*;

import static cz.ibm.hci.ReportBuilderHCI.ReportFormat;
import static cz.ibm.hci.ReportBuilderHCI.ReportFormat.*;

import static org.junit.Assert.assertNotNull;

//@Ignore
public class ReportBuilderHCITest {

//    @Test
    public void testGenerateReportFile1() throws IOException, JRException {
        byte[] report = generateReport(XLSX);
        saveToFile(report, "testhci.xlsx");
    }

    @Test
    public void testGenerateReportFile2() throws IOException, JRException {
        byte[] report = generateReport(DOCX);
        saveToFile(report, "testhci.docx");
    }

    @Test
    public void testGenerateReportFile3() throws IOException, JRException {
        byte[] report = generateReport(PDF);
        saveToFile(report, "testhci.pdf");
    }
    
    @Test
    public void testGenerateReportFile4() throws IOException, JRException {
        byte[] report = generateReport(ODT);
        saveToFile(report, "testhci.odt");
    }
    
//    @Test
    public void testGenerateReportFile5() throws IOException, JRException {
        byte[] report = generateReport(HTML);
        saveToFile(report, "testhci.html");
    }
    
    @Test
	public void testGenerateReportForHciDemoB64() throws JRException, IOException {
        ReportBuilderHCI rb = new ReportBuilderHCI();
		String report = rb.generateReportForHciDemoB64("DOC", "Tomas", "Male", "876543234567", "987654567", 
				"Klems", "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
		saveToFile(report, "testhci2.docx");
	}

	@Test
	public void testGenerateReportForHciDemo() throws JRException, IOException {
	    ReportBuilderHCI rb = new ReportBuilderHCI();
		byte[] report = rb.generateReportForHciDemo("DOC", "Tomas", "Male", "876543234567", "987654567", 
				"Klems", "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
		saveToFile(report, "testhci2.docx");
	}
	
	 @Test
		public void testGenerateReportForHciDemoB64Pdf() throws JRException, IOException {
	        ReportBuilderHCI rb = new ReportBuilderHCI();     
			String report = rb.generateReportForHciDemoB64("PDF", "Tomas", "Male", "876543234567", "987654567", 
					"Klems", "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
			saveToFile(report, "testhci2.pdf");
		}

	@Test
	public void testGenerateReportForHciDemoPdf() throws JRException, IOException {
	    ReportBuilderHCI rb = new ReportBuilderHCI();
		byte[] report = rb.generateReportForHciDemo("PDF", "Tomas", "Male", "876543234567", "987654567", 
				"Klems", "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
		saveToFile(report, "testhci2.pdf");
	}


    private void saveToFile(byte[] report, String name) throws IOException {
        try (FileOutputStream fileOutputStream = new FileOutputStream(name)) {
            fileOutputStream.write(report);
            fileOutputStream.flush();
        }
    }
    
    private void saveToFile(String report, String name) throws IOException {
    	try (PrintWriter out = new PrintWriter(name + ".b64")) {
    	    out.println(report);
    	}
    }

    private byte[] generateReport(ReportFormat format) throws IOException, JRException {
        try (InputStream jrxml = getClass().getResourceAsStream("/hci_page_1_A4.jrxml")) {
            
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            JasperCompileManager.compileReportToStream(jrxml, byteArrayOutputStream);
            
            InputStream reportStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            
            ReportBuilderHCI rb = new ReportBuilderHCI();
            byte[] report = rb.generateReportFile(reportStream, null, format);
            
            assertNotNull(report);
            return report;
        }
    }

    private byte[] getSource(String name) throws IOException { 
        try (InputStream dataStream = getClass().getResourceAsStream(name);
             BufferedInputStream bufferedInputStream = new BufferedInputStream(dataStream)) {
            int i;
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            while ((i = bufferedInputStream.read()) != -1) {
                byteArrayOutputStream.write(i);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}