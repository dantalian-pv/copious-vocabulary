package ru.dantalian.copvoc.persist.elastic.model.codecs;

public interface FieldCodec<T, S> {

	S serialize(T aEntry);

	T deserialize(S aEntry);

}
