package ru.dantalian.copvoc.core.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactoryConfigurationError;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import ru.dantalian.copvoc.core.CoreConstants;
import ru.dantalian.copvoc.core.CoreException;
import ru.dantalian.copvoc.core.export.model.CardFieldV1;
import ru.dantalian.copvoc.core.export.model.CardV1;
import ru.dantalian.copvoc.core.export.model.CardV1Iterable;
import ru.dantalian.copvoc.core.export.model.VocabularyExport;
import ru.dantalian.copvoc.core.export.model.VocabularyV1;
import ru.dantalian.copvoc.core.export.model.VocabularyViewV1;
import ru.dantalian.copvoc.core.model.CardsIterable;
import ru.dantalian.copvoc.core.model.VocabularyImportSettings;
import ru.dantalian.copvoc.persist.api.PersistCardFieldManager;
import ru.dantalian.copvoc.persist.api.PersistCardManager;
import ru.dantalian.copvoc.persist.api.PersistException;
import ru.dantalian.copvoc.persist.api.PersistVocabularyManager;
import ru.dantalian.copvoc.persist.api.PersistVocabularyViewManager;
import ru.dantalian.copvoc.persist.api.model.Card;
import ru.dantalian.copvoc.persist.api.model.CardField;
import ru.dantalian.copvoc.persist.api.model.CardFiledType;
import ru.dantalian.copvoc.persist.api.model.CardStat;
import ru.dantalian.copvoc.persist.api.model.Language;
import ru.dantalian.copvoc.persist.api.model.Vocabulary;
import ru.dantalian.copvoc.persist.api.model.VocabularyView;
import ru.dantalian.copvoc.persist.api.query.Query;
import ru.dantalian.copvoc.persist.api.query.QueryResult;
import ru.dantalian.copvoc.persist.api.utils.LanguageUtils;
import ru.dantalian.copvoc.persist.api.utils.XmlUtils;
import ru.dantalian.copvoc.persist.impl.model.PojoVocabularyView;
import ru.dantalian.copvoc.persist.impl.query.QueryFactory;

@Service
public class VocabularyUtils {

	@Autowired
	private PersistCardManager cardManager;

	@Autowired
	private PersistVocabularyManager vocManager;

	@Autowired
	private PersistVocabularyViewManager viewManager;

	@Autowired
	private PersistCardFieldManager fieldManager;

	@Autowired
	private FieldUtils fieldUtils;

	private ObjectMapper json;

	@PostConstruct
	public void init() {
		json = new ObjectMapper();
		json.enable(SerializationFeature.INDENT_OUTPUT);
	}

	public void exportVocabulary(final String aUser, final OutputStream aStream, final UUID aVocabularyId)
			throws CoreException {
		try {
			final CardsIterable cards = new CardsIterable(aUser, aVocabularyId, cardManager);
			final Vocabulary voc = vocManager.getVocabulary(aUser, aVocabularyId);
			final VocabularyView view = viewManager.getVocabularyView(aUser, aVocabularyId);
			final List<CardField> fields = fieldManager.listFields(aUser, aVocabularyId);

			final VocabularyV1 vocV1 = new VocabularyV1(voc.getName(), voc.getDescription(),
					LanguageUtils.asString(voc.getSource()), LanguageUtils.asString(voc.getTarget()));
			final VocabularyViewV1 vocViewV1 = new VocabularyViewV1(view.getCss(), view.getFront(), view.getBack());
			final List<CardFieldV1> fieldsV1 = fields.stream()
					.map(aItem -> new CardFieldV1(aItem.getName(), aItem.getType(), aItem.getOrder(), aItem.isSystem()))
					.collect(Collectors.toList());

			final CardV1Iterable cardsV1 = new CardV1Iterable(cards);

			final VocabularyExport vocExport = new VocabularyExport(1, vocV1, vocViewV1, fieldsV1, cardsV1);
			json.writeValue(aStream, vocExport);
		} catch (final IOException | PersistException e) {
			throw new CoreException("Failed to export vocabulary: " + aVocabularyId, e);
		}
	}

	public void importVocabulary(final String aUser, final UUID aVocabularyId, final InputStream aStream,
			final VocabularyImportSettings aSettings) throws CoreException {
		try {
			final VocabularyExport export = json.readValue(aStream, VocabularyExport.class);
			final int version = export.getVersion();
			checkVocabulary(aUser, aVocabularyId, export);
			if (version >= 1) {
				importFieldsV1(aUser, aVocabularyId, export, aSettings.isAddFields(),
						aSettings.isSkipIncompatibleFields());
				importViewV1(aUser, aVocabularyId, export, aSettings.isOverwriteView());
				importCardsV1(aUser, aVocabularyId, export, aSettings.isOverwriteCards());
			}
		} catch (final IOException | PersistException e) {
			throw new CoreException("Failed to export vocabulary: " + aVocabularyId, e);
		}
	}

	private void importCardsV1(final String aUser, final UUID aVocabularyId,
			final VocabularyExport aExport, final boolean aOverwriteCards) throws PersistException {

		final Iterator<CardV1> cards = aExport.getCards().iterator();
		while(cards.hasNext()) {
			final CardV1 card = cards.next();
			final Map<String, String> importContent = card.getContent();
			final Map<String, String> content = new HashMap<>();
			for (final Entry<String, String> entry: content.entrySet()) {
				final String fieldName = entry.getKey();
				final String fieldValue = entry.getValue();
				content.put(fieldName, fieldValue);
			}
			final String word = importContent.get("word");
			if (word == null) {
				throw new PersistException("field 'word' is missing in card");
			}
			final Query query = QueryFactory.newCardsQuery()
					.from(0)
					.limit(1)
					.setVocabularyId(aVocabularyId)
					.where(QueryFactory.eq("content.word_keyword", word, false))
					.build();
			final QueryResult<Card> result = cardManager.queryCards(aUser, query);
			if (!result.getItems().isEmpty()) {
				if (!aOverwriteCards) {
					continue;
				}
				final Card originalCard = result.getItems().iterator().next();
				cardManager.updateCard(aUser, aVocabularyId, originalCard.getId(), content);
			} else {
				// Add new card
				final Map<String, CardStat> stats = StatsUtils.defaultStats();
				cardManager.createCard(aUser, aVocabularyId, content, stats);
			}
		}
	}

	private void importViewV1(final String aUser, final UUID aVocabularyId, final VocabularyExport aExport,
			final boolean aOverwriteView) throws PersistException {
		if (!aOverwriteView) {
			return;
		}
		final VocabularyView originalView = viewManager.getVocabularyView(aUser, aVocabularyId);
		final VocabularyViewV1 view = aExport.getView();
		String css = view.getCss();
		if (css == null || css.isEmpty()) {
			css = originalView.getCss();
		}
		String front = view.getFront();
		if (front == null || front.isEmpty()) {
			front = originalView.getFront();
		}
		String back = view.getBack();
		if (back == null || back.isEmpty()) {
			back = originalView.getFront();
		}
		viewManager.updateVocabularyView(aUser, aVocabularyId, css, front, back);
	}

	private void checkVocabulary(final String aUser, final UUID aVocabularyId, final VocabularyExport aExport)
			throws PersistException, CoreException {
		final VocabularyV1 voc = aExport.getVocabulary();
		final String src = voc.getSource();
		final String tgt = voc.getTarget();

		final Vocabulary originalVoc = vocManager.getVocabulary(aUser, aVocabularyId);
		final Language source = originalVoc.getSource();
		final Language target = originalVoc.getTarget();

		final Language importSrc = LanguageUtils.asLanguage(src);
		final Language importTgt = LanguageUtils.asLanguage(tgt);
		if (!source.equals(importSrc) || !target.equals(importTgt)) {
			throw new CoreException("Languages are not compatible in vocabulary");
		}
	}

	private void importFieldsV1(final String aUser, final UUID aVocabularyId, final VocabularyExport aExport,
			final boolean aAddFields, final boolean aSkipIncompatibleFields) throws PersistException, CoreException {
		final Map<String, CardField> fields = fieldManager.listFields(aUser, aVocabularyId)
				.stream()
				.collect(Collectors.toMap(CardField::getName, aItem -> aItem));
		for (final CardFieldV1 importField: aExport.getFields()) {
			final String fieldName = importField.getName();
			final boolean systemField = importField.isSystem();
			final CardFiledType fieldType = importField.getType();
			final CardField originalField = fields.get(fieldName);
			if (originalField != null) {
				if (fieldType != originalField.getType() && !aSkipIncompatibleFields) {
					throw new CoreException("Incompatible field type in: " + fieldName + ", type: " + fieldType
							+ ", original: " + originalField.getType());
				}
			} else {
				if (aAddFields) {
					// Add missing field
					final CardField lastField = fieldUtils.getLastField(aUser, aVocabularyId);
					fieldManager.createField(aUser, aVocabularyId, fieldName, fieldType,
							lastField.getOrder() + 1, systemField);
				}
			}
		}
	}

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
