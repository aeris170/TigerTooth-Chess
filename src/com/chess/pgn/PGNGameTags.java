package com.chess.pgn;

import java.util.Map;
import java.util.HashMap;

import com.google.common.collect.ImmutableMap;

/**
 * PGN Tags. Google it.
 * 
 * @author Doða Oruç
 * @version 06.08.2017
 */
public class PGNGameTags {

	/** The game tags. */
	private final Map<String, String> gameTags;

	/**
	 * Instantiates a new PGN game tags.
	 *
	 * @param builder
	 *            the builder
	 */
	private PGNGameTags(final TagsBuilder builder) {
		this.gameTags = ImmutableMap.copyOf(builder.gameTags);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return this.gameTags.toString();
	}

	/**
	 * The Class TagsBuilder.
	 */
	public static class TagsBuilder {

		/** The game tags. */
		final Map<String, String> gameTags;

		/**
		 * Instantiates a new tags builder.
		 */
		public TagsBuilder() {
			this.gameTags = new HashMap<>();
		}

		/**
		 * Adds the tag.
		 *
		 * @param tagKey
		 *            the tag key
		 * @param tagValue
		 *            the tag value
		 * @return the tags builder
		 */
		public TagsBuilder addTag(final String tagKey, final String tagValue) {
			this.gameTags.put(tagKey, tagValue);
			return this;
		}

		/**
		 * Builds the.
		 *
		 * @return the PGN game tags
		 */
		public PGNGameTags build() {
			return new PGNGameTags(this);
		}
	}
}