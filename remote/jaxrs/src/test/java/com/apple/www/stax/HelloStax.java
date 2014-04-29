package com.apple.www.stax;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

public class HelloStax {
	
	public static void main(String[] args) throws Exception {
		System.out.println(getAsPsfRequest(HelloStax.class.getResourceAsStream("psfRequest.xml")));
	}

	public static String getAsPsfRequest(InputStream in) throws Exception {
		StringBuilder builder = new StringBuilder();
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLStreamReader reader = inputFactory.createXMLStreamReader(in);

		List<String> requiredFieldNames = new ArrayList<String>();
		try {
			StringBuilder productType = new StringBuilder();
			int pageId = 0;
			int positionId = 0;
			int dealCount = 0;
			while (reader.hasNext()) {
	
				switch(reader.getEventType()){
				case XMLStreamReader.START_ELEMENT:
					if ("psfRequest".equals(reader.getLocalName())) {
						builder.append(reader.getAttributeValue(null, "clientId")).append("\n");
					}else if ("dealGroup".equals(reader.getLocalName())) {
						pageId = parseInt(reader.getAttributeValue(null, "pageId"));
						positionId = parseInt(reader.getAttributeValue(null, "positionId"));
						dealCount = parseInt(reader.getAttributeValue(null, "dealCount"));
						productType = new StringBuilder();
					} else if ("productType".equals(reader.getName().toString())) {
						productType.append("," + reader.getElementText());
					}else if ("sort".equals(reader.getLocalName())) {
						String fieldName = reader.getAttributeValue(null, "fieldName");
						String direction = reader.getAttributeValue(null, "direction");
						builder.append(String.format("%s %s", fieldName, direction)).append("\n");;
					} else if ("requiredFieldName".equals(reader.getLocalName())) {
						requiredFieldNames.add(reader.getElementText());
					}
					break;
				case XMLStreamReader.END_ELEMENT:
					if ("dealGroup".equals(reader.getLocalName())) {
						builder.append(String.format("%d %d %s %d", pageId, positionId, productType, dealCount)).append("\n");
					} 
					break;
				}
				System.out.println(reader.next());
			}
		} finally {
			reader.close();
		}
		builder.append(requiredFieldNames);
		return builder.toString();
	}

	private static int parseInt(String val) {
		try {
			return Integer.parseInt(val);
		} catch (Exception ex) {
			return 0;
		}
	}

}
