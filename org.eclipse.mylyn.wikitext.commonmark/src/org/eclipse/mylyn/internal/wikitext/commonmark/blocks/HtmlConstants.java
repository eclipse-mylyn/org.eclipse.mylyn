/*******************************************************************************
 * Copyright (c) 2015 David Green.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.commonmark.blocks;

class HtmlConstants {

	private static final String ATTRIBUTE_VALUE_QUOTED = "\"[^<\"]*\"";

	private static final String ATTRIBUTE_VALUE_SINGLEQUOTED = "'[^<']*'";

	private static final String ATTRIBUTE_VALUE_UNQUOTED = "[^\"'<>=]+";

	private static final String ATTRIBUTE_VALUE = "(?:" + ATTRIBUTE_VALUE_QUOTED + "|" + ATTRIBUTE_VALUE_SINGLEQUOTED
			+ "|" + ATTRIBUTE_VALUE_UNQUOTED + ")";

	private static final String ATTRIBUTE_NAME = "[a-zA-Z_][a-zA-Z0-9_:.-]*";

	static final String ATTRIBUTE = "(?:" + ATTRIBUTE_NAME + "(?:\\s*=\\s*" + ATTRIBUTE_VALUE + ")?)";

	static final String REPEATING_ATTRIBUTE = "(?:\\s+" + ATTRIBUTE + ")*";

	static final String HTML_TAG_NAME = "([a-zA-Z_][a-zA-Z0-9_:-]*)";

	private HtmlConstants() {
		// prevent instantiation
	}
}
