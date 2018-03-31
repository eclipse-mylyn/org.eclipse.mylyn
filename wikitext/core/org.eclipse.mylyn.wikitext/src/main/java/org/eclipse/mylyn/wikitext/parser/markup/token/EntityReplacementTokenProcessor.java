/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.wikitext.parser.markup.token;

import org.eclipse.mylyn.wikitext.parser.DocumentBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.PatternBasedElementProcessor;

/**
 * A token processor that emits a specified XML entity reference.
 *
 * @see DocumentBuilder#entityReference(String)
 * @author David Green
 * @since 3.0
 */
public class EntityReplacementTokenProcessor extends PatternBasedElementProcessor {

	private final String entity;

	/**
	 * @param entity
	 *            the entity to emit
	 */
	public EntityReplacementTokenProcessor(String entity) {
		this.entity = entity;
	}

	@Override
	public void emit() {
		getBuilder().entityReference(entity);
	}

}
