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

package org.eclipse.mylyn.internal.wikitext.core.util.css;

/**
 * A CSS selector that selects any element
 * 
 * @author David Green
 */
public class AnySelector extends Selector {

	@Override
	public boolean select(ElementInfo info) {
		return true;
	}

}
