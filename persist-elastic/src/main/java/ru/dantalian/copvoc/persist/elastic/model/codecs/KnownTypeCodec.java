package ru.dantalian.copvoc.persist.elastic.model.codecs;

public class KnownTypeCodec<T, S> implements FieldCodec<T, S> {

	private final Class<T> typeClass;

	private final Class<S> serializedClass;

	public KnownTypeCodec(final Class<T> aTypeClass, final Class<S> aSerializedClass) {
		typeClass = aTypeClass;
		serializedClass = aSerializedClass;
	}

	@Override
	public S serialize(final T aEntry) throws CodecException {
		return serialize(serializedClass, aEntry);
	}

	protected S serialize(final Class<S> aClass, final T aEntry) throws CodecException {
		if (aEntry != null && aEntry.getClass().isEnum()) {
			return (S) ((Enum) aEntry).name();
		}
		return (S) aEntry;
	}

	@Override
	public T deserialize(final S aEntry) throws CodecException {
		return deserialize(typeClass, aEntry);
	}

	protected T deserialize(final Class<T> aClass, final S aEntry) throws CodecException {
		try {
			if (aEntry != null && aClass.isEnum()) {
				return (T) Enum.valueOf((Class<? extends Enum>) aClass, (String) aEntry);
			}
			return (T) aEntry;
		} catch (final Exception e) {
			throw new CodecException("Failed to convert " + aClass.getName(), e);
		}
	}

}
