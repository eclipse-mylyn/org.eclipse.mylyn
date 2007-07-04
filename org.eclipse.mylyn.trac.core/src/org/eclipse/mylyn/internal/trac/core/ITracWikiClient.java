/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

/**
 * @author Xiaoyang Guan
 * 
 */
public interface ITracWikiClient {

	/**
	 * Render arbitrary wiki text as HTML
	 * 
	 * @param sourceText
	 *            wiki source text
	 * @return The HTML-formatted string of the wiki text
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	public String wikiToHtml(String sourceText) throws TracException;

}
