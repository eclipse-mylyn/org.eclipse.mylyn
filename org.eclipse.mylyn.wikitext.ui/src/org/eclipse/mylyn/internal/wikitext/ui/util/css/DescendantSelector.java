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
 * 
 * @author David Green
 */
class DescendantSelector extends Selector {

	private final Selector ancestorSelector;

	public DescendantSelector(Selector ancestorSelector) {
		super();
		this.ancestorSelector = ancestorSelector;
	}

	@Override
	public boolean select(ElementInfo info) {
		ElementInfo ancestor = info.getParent();
		while (ancestor != null) {
			if (ancestorSelector.select(ancestor)) {
				return true;
			}
			ancestor = ancestor.getParent();
		}
		return false;
	}

	public Selector getAncestorSelector() {
		return ancestorSelector;
	}

}
