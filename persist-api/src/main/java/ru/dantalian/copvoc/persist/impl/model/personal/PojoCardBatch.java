package ru.dantalian.copvoc.persist.impl.model.personal;

import java.util.List;
import java.util.UUID;

import ru.dantalian.copvoc.persist.api.model.CardBatch;
import ru.dantalian.copvoc.persist.api.model.Language;

public class PojoCardBatch implements CardBatch {

	private UUID id;

	private UUID userId;

	private Language source;

	private Language target;

	private List<UUID> fieldIds;

	public PojoCardBatch() {
	}

	public PojoCardBatch(final UUID aId, final UUID aUserId, final Language aSource,
			final Language aTarget, final List<UUID> aFieldIds) {
		id = aId;
		userId = aUserId;
		source = aSource;
		target = aTarget;
		fieldIds = aFieldIds;
	}

	@Override
	public UUID getId() {
		return id;
	}

	public void setId(final UUID aId) {
		id = aId;
	}

	@Override
	public UUID getUserId() {
		return userId;
	}

	public void setUserId(final UUID aUserId) {
		userId = aUserId;
	}

	@Override
	public Language getSource() {
		return source;
	}

	public void setSource(final Language aSource) {
		source = aSource;
	}

	@Override
	public Language getTarget() {
		return target;
	}

	public void setTarget(final Language aTarget) {
		target = aTarget;
	}

	@Override
	public List<UUID> getFieldIds() {
		return fieldIds;
	}

	public void setFieldIds(final List<UUID> aFieldIds) {
		fieldIds = aFieldIds;
	}

	@Override
	public String toString() {
		return "PojoCardBatch [id=" + id + ", userId=" + userId + ", source=" + source
				+ ", target=" + target + ", fieldIds=" + fieldIds + "]";
	}

}
