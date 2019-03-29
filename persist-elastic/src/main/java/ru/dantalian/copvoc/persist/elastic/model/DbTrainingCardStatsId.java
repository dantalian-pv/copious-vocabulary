package ru.dantalian.copvoc.persist.elastic.model;

import java.util.UUID;

public class DbTrainingCardStatsId {

	private UUID trainingId;

	private UUID cardId;

	public DbTrainingCardStatsId() {
	}

	public DbTrainingCardStatsId(final UUID aTrainingId, final UUID aCardId) {
		trainingId = aTrainingId;
		cardId = aCardId;
	}

	public UUID getTrainingId() {
		return trainingId;
	}

	public void setTrainingId(final UUID aTrainingId) {
		trainingId = aTrainingId;
	}

	public UUID getCardId() {
		return cardId;
	}

	public void setCardId(final UUID aCardId) {
		cardId = aCardId;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cardId == null) ? 0 : cardId.hashCode());
		result = prime * result + ((trainingId == null) ? 0 : trainingId.hashCode());
		return result;
	}

	@Override
	public boolean equals(final Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DbTrainingCardStatsId)) {
			return false;
		}
		final DbTrainingCardStatsId other = (DbTrainingCardStatsId) obj;
		if (cardId == null) {
			if (other.cardId != null) {
				return false;
			}
		} else if (!cardId.equals(other.cardId)) {
			return false;
		}
		if (trainingId == null) {
			if (other.trainingId != null) {
				return false;
			}
		} else if (!trainingId.equals(other.trainingId)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "DbTrainingCardStatsId [trainingId=" + trainingId + ", cardId=" + cardId + "]";
	}

}
