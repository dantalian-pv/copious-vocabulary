package ru.dantalian.copvoc.persist.elastic.model.annotations;

import java.lang.annotation.Annotation;

import ru.dantalian.copvoc.persist.elastic.model.codecs.DefaultCodec;
import ru.dantalian.copvoc.persist.elastic.model.codecs.FieldCodec;

public final class SubFieldFactory {

	private SubFieldFactory() {
	}

	public static SubField create(final String aPathMatch, final String aPathUnmatch,
			final String aType, final boolean aIndex, final boolean aNested, final Class<? extends FieldCodec> aCodec) {
		final String pathUnmatch = aPathUnmatch == null ? "" : aPathMatch;
		final String type = aType == null ? "" : aType;
		final Class<? extends FieldCodec> codec = aCodec == null ? DefaultCodec.class : aCodec;
		return new SubField() {

			@Override
			public Class<? extends Annotation> annotationType() {
				return SubField.class;
			}

			@Override
			public String type() {
				return type;
			}

			@Override
			public String path_unmatch() {
				return pathUnmatch;
			}

			@Override
			public String path_match() {
				return aPathMatch;
			}

			@Override
			public boolean nested() {
				return aNested;
			}

			@Override
			public boolean index() {
				return aIndex;
			}

			@Override
			public Class<? extends FieldCodec> codec() {
				return codec;
			}
		};
	}

}
