/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.core.parser;

import org.eclipse.mylyn.wikitext.core.parser.builder.HtmlDocumentBuilder;

/**
 * Attributes for links (hyperlinks)
 * 
 * @author David Green
 * @author draft
 * @since 1.0
 */
public class LinkAttributes extends Attributes {
	private String target;

	private String rel;

	/**
	 * The target of a link, as defined by the HTML spec.
	 * 
	 * @param target
	 *            the target or null if there should be none
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

	/**
	 * The 'rel' of a link, as defined by the HTML spec.
	 * 
	 * @return the ref or null if there should be none
	 * 
	 * @see HtmlDocumentBuilder#getLinkRel()
	 */
	public String getRel() {
		return rel;
	}

	/**
	 * The 'rel' of a link, as defined by the HTML spec.
	 * 
	 * @param rel
	 *            the rel or null if there should be none
	 * 
	 * @see HtmlDocumentBuilder#setLinkRel(String)
	 */
	public void setRel(String rel) {
		this.rel = rel;
	}

}
