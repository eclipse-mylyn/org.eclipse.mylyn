/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
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

/**
 * A CSS selector that selects any element
 *
 * @author David Green
 * @since 3.0
 */
public class AnySelector extends Selector {

	@Override
	public boolean select(ElementInfo info) {
		return true;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof AnySelector;
	}

	@Override
	public int hashCode() {
		return AnySelector.class.hashCode();
	}

	@Override
	public String toString() {
		return "*"; //$NON-NLS-1$
	}
}