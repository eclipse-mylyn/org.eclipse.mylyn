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
 * A selector that selects elements having an id equal to a specific value.
 * 
 * @author David Green
 */
public class IdSelector extends Selector {

	private final String id;

	public IdSelector(String id) {
		this.id = id;
	}

	@Override
	public boolean select(ElementInfo info) {
		return info.hasId(id);
	}

	public String getId() {
		return id;
	}

}
