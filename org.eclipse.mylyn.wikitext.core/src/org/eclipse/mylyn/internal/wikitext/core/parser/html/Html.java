/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.core.parser.html;

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Element;

/**
 * @author David Green
 */
class Html {

	private static final Set<String> spanElements = new HashSet<String>();
	static {
		spanElements.add("a"); //$NON-NLS-1$
		spanElements.add("b"); //$NON-NLS-1$
		spanElements.add("cite"); //$NON-NLS-1$
		spanElements.add("i"); //$NON-NLS-1$
		spanElements.add("em"); //$NON-NLS-1$
		spanElements.add("strong"); //$NON-NLS-1$
		spanElements.add("del"); //$NON-NLS-1$
		spanElements.add("ins"); //$NON-NLS-1$
		spanElements.add("q"); //$NON-NLS-1$
		spanElements.add("u"); //$NON-NLS-1$
		spanElements.add("sup"); //$NON-NLS-1$
		spanElements.add("sub"); //$NON-NLS-1$
		spanElements.add("span"); //$NON-NLS-1$
		spanElements.add("font"); //$NON-NLS-1$
		spanElements.add("code"); //$NON-NLS-1$
		spanElements.add("tt"); //$NON-NLS-1$
		spanElements.add("font"); //$NON-NLS-1$
	}

	/**
	 * indicate if this is a span element - in that it's inline content rather than block content.
	 */
	public static boolean isSpanElement(Element element) {
		return spanElements.contains(element.tagName().toLowerCase());
	}

	public static boolean isWhitespacePreserve(Element element) {
		if (element.tagName().equalsIgnoreCase("pre") || element.tagName().equalsIgnoreCase("code")) { //$NON-NLS-1$ //$NON-NLS-2$
			return true;
		}
		if (element.parent() != null) {
			return isWhitespacePreserve(element.parent());
		}
		return false;
	}

}
