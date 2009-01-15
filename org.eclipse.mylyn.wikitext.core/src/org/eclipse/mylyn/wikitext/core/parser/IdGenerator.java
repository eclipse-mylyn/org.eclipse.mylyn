/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.mylyn.wikitext.core.parser.markup.DefaultIdGenerationStrategy;
import org.eclipse.mylyn.wikitext.core.parser.markup.IdGenerationStrategy;

/**
 * @author David Green
 * @since 1.0
 */
public class IdGenerator {

	private static final DefaultIdGenerationStrategy DEFAULT_ID_GENERATION_STRATEGY = new DefaultIdGenerationStrategy();

	private final Map<String, Integer> idGenerators = new HashMap<String, Integer>();

	private final Set<String> anchorNames = new HashSet<String>();

	private IdGenerationStrategy generationStrategy = DEFAULT_ID_GENERATION_STRATEGY;

	public IdGenerator() {
	}

	/**
	 * reserve the given id, ensuring that the generator will not generate the same id. An id can only be reserved if it
	 * has not already been reserved and if it has not already been {@link #newId(String,String) generated}.
	 * 
	 * @param id
	 *            the id to reserve
	 * 
	 * @return true if the id was reserved, otherwise false
	 */
	public boolean reserveId(String id) {
		return anchorNames.add(id);
	}

	/**
	 * create a new ID based on the given type and label text. Guarantees to return an id once and only once; duplicates
	 * are never created.
	 * 
	 * @param type
	 *            the type of id to produce, usually an indication of what the id is created for. For example, 'h1', or
	 *            'h2'. may be null.
	 * @param text
	 *            the label text for which the id is being produced. may be null.
	 * 
	 * @return a unique id
	 */
	public String newId(String type, String text) {
		if (type == null) {
			type = ""; //$NON-NLS-1$
		}
		Integer current = idGenerators.get(type);
		if (current == null) {
			current = 0;
		}
		current = current + 1;

		idGenerators.put(type, current);

		String id = null;
		if (text != null) {
			id = generationStrategy.generateId(text.trim());
			if (id == null || id.length() == 0) {
				id = type + '-' + current;
			}
		} else {
			id = type + '-' + current;
		}
		String template = id;
		int suffix = 1;
		while (!anchorNames.add(id)) {
			id = template + (++suffix);
		}
		return id;
	}

	/**
	 * get the set of anchor names that were either {@link #reserveId(String) reserved} or
	 * {@link #newId(String, String) created}.
	 * 
	 * @return the set of names
	 */
	public Set<String> getAnchorNames() {
		return Collections.unmodifiableSet(anchorNames);
	}

	public IdGenerationStrategy getGenerationStrategy() {
		return generationStrategy;
	}

	public void setGenerationStrategy(IdGenerationStrategy generationStrategy) {
		if (generationStrategy == null) {
			throw new IllegalArgumentException();
		}
		this.generationStrategy = generationStrategy;
	}
}
