/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util.css;

/**
 * a selector that selects elements based on their having a CSS class
 * 
 * @author David Green
 * 
 * @see ElementInfo#hasCssClass(String)
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
}
