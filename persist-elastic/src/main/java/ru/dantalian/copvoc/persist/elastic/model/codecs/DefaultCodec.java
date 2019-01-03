package ru.dantalian.copvoc.persist.elastic.model.codecs;

public class DefaultCodec<T, S> implements FieldCodec<T, S> {

	@Override
	public S serialize(final T aEntry) throws CodecException {
		return (S) aEntry;
	}

	@Override
	public T deserialize(final S aEntry) throws CodecException {
		try {
			return (T) aEntry;
		} catch (final Exception e) {
			throw new CodecException("Failed to convert " + ((aEntry == null) ? aEntry : aEntry.getClass()), e);
		}
	}

}
