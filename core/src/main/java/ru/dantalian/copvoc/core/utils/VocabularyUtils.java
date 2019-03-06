package ru.dantalian.copvoc.core.utils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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

import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class VocabularyUtils {

	public VocabularyView getDefaultView(final UUID aVocabularyId,
			final List<CardField> aSourceLangFields,
			final List<CardField> aTargetLangFields) throws CoreException {
		try (InputStream cssStream = this.getClass().getClassLoader()
				.getResourceAsStream(CoreConstants.DEFAULT_CARD_VIEW_CSS);
				InputStream frontStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_VIEW_FRONT);
				InputStream backStream = this.getClass().getClassLoader()
						.getResourceAsStream(CoreConstants.DEFAULT_CARD_VIEW_BACK)) {
			final String css = new BufferedReader(new InputStreamReader(cssStream))
				  .lines().collect(Collectors.joining("\n"));
			final String front = new BufferedReader(new InputStreamReader(frontStream))
				  .lines().collect(Collectors.joining("\n"));
			final String back = new BufferedReader(new InputStreamReader(backStream))
				  .lines().collect(Collectors.joining("\n"));

			// Front
			final Document frontXml = readAsXml(front);
			replaceFieldsPlaceholder(frontXml, aSourceLangFields);
			final String frontResult = xmlToString(frontXml);
			// Back
			final Document backXml = readAsXml(back);
			replaceFieldsPlaceholder(backXml, aTargetLangFields);
			final String backResult = xmlToString(backXml);

			return new PojoVocabularyView(aVocabularyId, css, frontResult, backResult);
		} catch (final IOException | ParserConfigurationException | SAXException
				| TransformerFactoryConfigurationError | TransformerException e) {
			throw new CoreException("Failed to init view", e);
		}
	}

	private String xmlToString(final Document aXml)
			throws TransformerFactoryConfigurationError, TransformerException {
		final DOMSource source = new DOMSource(aXml);
		final ByteArrayOutputStream out = new ByteArrayOutputStream();
		final StreamResult result = new StreamResult(out);
		final TransformerFactory transformerFactory = TransformerFactory.newInstance();
		final Transformer transformer = transformerFactory.newTransformer();
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.METHOD, "xml");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
		transformer.transform(source, result);
		return new String(out.toByteArray());
	}

	private void replaceFieldsPlaceholder(final Document aDoc, final List<CardField> aFields) {
		final Element element = aDoc.getDocumentElement();
		final Element placeHolder = seekFieldsPlaceholder(element);
		if (placeHolder != null) {
			for (final CardField field: aFields) {
				final Element fieldElement = aDoc.createElement(placeHolder.getNodeName());
				fieldElement.setTextContent(placeHolder.getTextContent());
				placeHolder.getParentNode().appendChild(fieldElement);
				final NamedNodeMap attributes = placeHolder.getAttributes();
				for (int i = 0; i < attributes.getLength(); i++) {
					final Node attr = attributes.item(i);
					if ("data-bind".equals(attr.getNodeName())) {
						fieldElement.setAttribute("data-bind", "text: " + field.getName());
					} else {
						fieldElement.setAttribute(attr.getNodeName(), attr.getNodeValue());
					}
				}
			}
			placeHolder.getParentNode().removeChild(placeHolder);
		}
	}

	private Element seekFieldsPlaceholder(final Element element) {
		if (element.hasAttribute("data-bind") && "__FIELDS__".equals(element.getAttribute("data-bind"))) {
			return element;
		}
		final NodeList nodeList = element.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			final Node item = nodeList.item(i);
			if (item.getNodeType() != Node.ELEMENT_NODE) {
				continue;
			}
			Element child = (Element) item;
			child = seekFieldsPlaceholder((Element) item);
			if (child != null) {
				return child;
			}
		}
		return null;
	}

	private Document readAsXml(final String aContent)
			throws ParserConfigurationException, SAXException, IOException {
		final DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		final DocumentBuilder builder = builderFactory.newDocumentBuilder();
		final Document xmlDocument = builder.parse(new ByteArrayInputStream(aContent.getBytes()));

		return xmlDocument;
	}

}
