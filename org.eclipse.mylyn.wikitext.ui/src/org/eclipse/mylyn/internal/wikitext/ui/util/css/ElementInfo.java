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
 * An interface to XML element information
 * 
 * @author David Green
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
