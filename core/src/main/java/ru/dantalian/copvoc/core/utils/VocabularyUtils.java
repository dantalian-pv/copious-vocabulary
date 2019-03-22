package ru.dantalian.copvoc.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

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
import ru.dantalian.copvoc.persist.api.utils.XmlUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;

@Service
public class VocabularyUtils {

	public VocabularyView getDefaultView(final UUID aVocabularyId,
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
			final Document frontXml = XmlUtils.readAsXml(front);
			replaceFieldsPlaceholder(frontXml, Collections.emptyList());
			final String frontResult = XmlUtils.xmlToString(frontXml);
			// Back
			final Document backXml = XmlUtils.readAsXml(back);
			replaceFieldsPlaceholder(backXml, aTargetLangFields);
			final String backResult = XmlUtils.xmlToString(backXml);

			return new PojoVocabularyView(aVocabularyId, css, frontResult, backResult);
		} catch (final IOException | ParserConfigurationException | SAXException
				| TransformerFactoryConfigurationError | TransformerException e) {
			throw new CoreException("Failed to init view", e);
		}
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

}
