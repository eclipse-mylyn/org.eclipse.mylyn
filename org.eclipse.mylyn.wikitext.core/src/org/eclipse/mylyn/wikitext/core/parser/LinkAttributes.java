/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

/**
 * Attributes for links (hyperlinks)
 * 
 * @author David Green
 * @author draft
 */
public class LinkAttributes extends Attributes {
	private String target;

	/**
	 * The target of a link, as defined by the HTML spec.
	 * 
	 * @param target the target or null if there should be none
	 */
	public void setTarget(String target) {
		this.target = target;
	}

	/**
	 * The target of a link, as defined by the HTML spec.
	 * 
	 * @return the target or null if there should be none
	 */
	public String getTarget() {
		return target;
	}
}
