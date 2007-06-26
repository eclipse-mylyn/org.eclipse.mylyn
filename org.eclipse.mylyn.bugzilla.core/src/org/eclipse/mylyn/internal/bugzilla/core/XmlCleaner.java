/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

import org.eclipse.mylyn.web.core.HtmlStreamTokenizer;
import org.eclipse.mylyn.web.core.HtmlTag;
import org.eclipse.mylyn.web.core.HtmlStreamTokenizer.Token;

/**
 * This is in place to escape & characters within the resource and rdf:about attributes. Currently the values are not
 * escaped which causes sax parser errors. This bug has been filed and can be found here:
 * https://bugzilla.mozilla.org/show_bug.cgi?id=264785
 * 
 * @author Rob Elves
 */
public class XmlCleaner {

	public static StringBuffer clean(Reader in) {

		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);
		StringBuffer content = new StringBuffer();

		// Hack since HtmlStreamTokenizer not familiar with xml tag.
		content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		try {
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getAttribute("resource") != null) {
						String resourceID = tag.getAttribute("resource");
						tag.setAttribute("resource", resourceID.replace("&", "&amp;"));
					}
					if (tag.getAttribute("rdf:about") != null) {
						String resourceID = tag.getAttribute("rdf:about");
						tag.setAttribute("rdf:about", resourceID.replace("&", "&amp;"));
					}
				}
				if (!token.toString().startsWith("<?xml")) {
					content.append(token.toString());
				}
			}
		} catch (IOException e) {

		} catch (ParseException e) {

		}
		return content;
	}

}
