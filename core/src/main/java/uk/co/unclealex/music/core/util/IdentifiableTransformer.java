package uk.co.unclealex.music.core.util;

import org.apache.commons.collections15.Transformer;

import uk.co.unclealex.music.core.model.Identifiable;

public class IdentifiableTransformer<I extends Comparable<I>> implements Transformer<Identifiable<I>, I> {

	@Override
	public I transform(Identifiable<I> identifiable) {
		return identifiable.getIdentifier();
	}

}
