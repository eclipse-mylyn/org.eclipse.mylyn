/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.css;

import java.util.Objects;

/**
 * a selector that selects elements based on their having a CSS class
 *
 * @author David Green
 * @see ElementInfo#hasCssClass(String)
 * @since 3.0
 */
public class CssClassSelector extends Selector {

	private final String cssClass;

	public CssClassSelector(String cssClass) {
		this.cssClass = cssClass;
	}

	@Override
	public boolean select(ElementInfo info) {
		return info.hasCssClass(cssClass);
	}

	public String getCssClass() {
		return cssClass;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof CssClassSelector other)) {
			return false;
		}
		return Objects.equals(cssClass, other.cssClass);
	}

	@Override
	public int hashCode() {
		return Objects.hash(cssClass);
	}

	@Override
	public String toString() {
		return "." + cssClass; //$NON-NLS-1$
	}
}