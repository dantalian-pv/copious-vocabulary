package ru.dantalian.copvoc.persist.api.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public final class XmlUtils {

	private XmlUtils() {
	}

	public static Document readAsXml(final String aContent)
			throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		final Document xmlDocument = builder.parse(new ByteArrayInputStream(aContent.getBytes()));

		return xmlDocument;
	}

	public static String xmlToString(final Document aXml)
			throws TransformerFactoryConfigurationError, TransformerException {
		final DOMSource source = new DOMSource(aXml);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final StreamResult result = new StreamResult(out);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
		return new String(out.toByteArray());
	}

}
