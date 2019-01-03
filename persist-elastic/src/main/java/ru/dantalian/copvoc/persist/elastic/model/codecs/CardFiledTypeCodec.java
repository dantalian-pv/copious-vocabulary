package ru.dantalian.copvoc.persist.elastic.model.codecs;

import ru.dantalian.copvoc.persist.api.model.CardFiledType;

public class CardFiledTypeCodec extends KnownTypeCodec<CardFiledType, String> {

	public CardFiledTypeCodec() {
		super(CardFiledType.class, String.class);
	}

}
