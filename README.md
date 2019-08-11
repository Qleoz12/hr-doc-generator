# hr-doc-generator

Demo generator pro HCI. Vygeneruje Labor Contract ze sablony a zadanych parametru.
Zatim generuje pouze prvni stranku Contractu.
DOC funguje. Ale PDF nefunguje, protoze neobsahuje cinske znaky.

Priklad - viz. Unit Test:
```Java
@Test
	public void testGenerateReportForHciDemoB64() throws JRException, IOException {
		String report = ReportBuilderHCI.generateReportForHciDemoB64("DOC", "Tomas", "Male", "876543234567", "987654567", 
				"Klems", "IT Architect", "qq98765", "987654345678", "we9876543", "Shanghai");
		saveToFile(report, "testhci2.docx");
	}
```

## Pouzij volani metody: 
 - generateReportForHciDemoB64()
 - generateReportForHciDemo()
 
## Parametry:
 - outputFormat - "DOC" nebo "PDF"
 - firstName
 - gender 
 - idPassportNo
 - laborCId
 - lastName
 - positionName
 - qqNumber
 - telephone
 - weChatNumber
 - workPlace 