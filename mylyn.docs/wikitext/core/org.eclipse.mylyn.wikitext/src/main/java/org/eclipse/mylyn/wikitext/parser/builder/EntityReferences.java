/*******************************************************************************
 * Copyright (c) 2016 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Provides a way to get a string corresponding to an entity reference.
 *
 * @since 3.0
 */
public class EntityReferences {
	private static final Pattern NUMERIC_ENTITY_PATTERN = Pattern.compile("#(?:(?:x([0-9A-Fa-f]+))|([0-9]+))"); //$NON-NLS-1$

	private static final EntityReferences instance = new EntityReferences();

	/**
	 * Provides an instance of {@link EntityReferences}.
	 *
	 * @return the instance
	 */
	public static EntityReferences instance() {
		return instance;
	}

	/**
	 * Provides a string corresponding to the given entity reference.
	 * <p>
	 * Usually the equivalent string to an entity reference consists of a single character, however in some cases an
	 * entity reference may correspond to more than one character.
	 * </p>
	 *
	 * @param entityReference
	 *            the entity reference, which may be of the form {@code "&apos;"}, {@code "&#x00027;"}, or
	 *            {@code "&#39;"}, where the leading '&' and trailing ';' may be omitted
	 * @return the equivalent string, or null if no equivalent could be provided
	 */
	public String equivalentString(String entityReference) {
		Objects.requireNonNull(entityReference, "Must provide an entityReference"); //$NON-NLS-1$
		String entity = entityReference;
		if (entityReference.startsWith("&") && entityReference.endsWith(";")) { //$NON-NLS-1$//$NON-NLS-2$
			entity = entityReference.substring(1, entityReference.length() - 1);
		}
		Matcher matcher = NUMERIC_ENTITY_PATTERN.matcher(entity);
		if (matcher.matches()) {
			return equivalentString(matcher);
		}
		return HtmlEntities.instance().nameToStringEquivalent(entity);
	}

	private String equivalentString(Matcher matcher) {
		String numberPart = matcher.group(1);
		try {
			if (numberPart != null) {
				return String.valueOf((char) Integer.parseInt(numberPart, 16));
			}
			numberPart = matcher.group(2);
			return String.valueOf((char) Integer.parseInt(numberPart));
		} catch (NumberFormatException e) {
			return null;
		}
	}

	private EntityReferences() {
		// prevent instantiation
	}
}
