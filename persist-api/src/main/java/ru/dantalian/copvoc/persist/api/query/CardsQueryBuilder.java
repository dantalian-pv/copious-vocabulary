package ru.dantalian.copvoc.persist.api.query;

import java.util.UUID;

import ru.dantalian.copvoc.persist.api.Builder;

public interface CardsQueryBuilder extends Builder<CardsQuery> {

	CardsQueryBuilder setBatchId(UUID aBatchId);

}
