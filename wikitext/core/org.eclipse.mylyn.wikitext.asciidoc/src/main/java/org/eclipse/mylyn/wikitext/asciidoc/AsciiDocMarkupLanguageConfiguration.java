/*******************************************************************************
 * Copyright (c) 2017, 2022 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;

/**
 * Extended configuration for the AsciiDoc markup language
 *
 * @author Jeremie Bresson
 * @since 3.0.0
 */
public class AsciiDocMarkupLanguageConfiguration extends MarkupLanguageConfiguration {

	private Map<String, String> initialAttributes = Collections.emptyMap();

	/**
	 * @since 3.0.0
	 * @return initial attributes (key, values)
	 */
	public Map<String, String> getInitialAttributes() {
		return initialAttributes;
	}

	/**
	 * @since 3.0.0
	 * @param initialAttributes
	 *            initial attributes (key, values)
	 */
	public void setInitialAttributes(Map<String, String> initialAttributes) {
		Objects.requireNonNull(initialAttributes, "initialAttributes can not be null");
		this.initialAttributes = Map.copyOf(initialAttributes);
	}
}
