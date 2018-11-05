package ru.dantalian.copvac.persist.api.model.personal;

import java.util.UUID;

public interface Principal {

	UUID getId();

	String getName();

	String getDescription();

}
