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
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.css;

/**
 * An interface to XML element information
 *
 * @author David Green
 * @since 3.0
 */
public interface ElementInfo {
	/**
	 * get the local name of the element
	 */
	public String getLocalName();

	/**
	 * get the parent of this element
	 *
	 * @return the parent or null if this is the root element
	 */
	public ElementInfo getParent();

	/**
	 * indicate if the elemet has the given CSS class
	 */
	public boolean hasCssClass(String cssClass);

	/**
	 * indicate if the element has the given id
	 */
	public boolean hasId(String id);
}
