package ru.dantalian.copvoc.persist.elastic.model.codecs;

public class DefaultCodec<T, S> implements FieldCodec<T, S> {

	@Override
	public S serialize(final T aEntry) {
		return (S) aEntry;
	}

	@Override
	public T deserialize(final S aEntry) {
		return (T) aEntry;
	}

}
