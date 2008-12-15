/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.text.ParseException;

import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer;
import org.eclipse.mylyn.commons.net.HtmlTag;
import org.eclipse.mylyn.commons.net.HtmlStreamTokenizer.Token;

/**
 * This is in place to escape & characters within the resource and rdf:about attributes. Currently the values are not
 * escaped which causes sax parser errors. This bug has been filed and can be found here:
 * https://bugzilla.mozilla.org/show_bug.cgi?id=264785
 * 
 * @author Rob Elves
 */
public class XmlCleaner {

	public static BufferedReader clean(Reader in, File tempFile) {

		HtmlStreamTokenizer tokenizer = new HtmlStreamTokenizer(in, null);
		try {
			BufferedWriter content = new BufferedWriter(new FileWriter(tempFile));
			// Hack since HtmlStreamTokenizer not familiar with xml tag.
			content.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>"); //$NON-NLS-1$
			for (Token token = tokenizer.nextToken(); token.getType() != Token.EOF; token = tokenizer.nextToken()) {

				if (token.getType() == Token.TAG) {
					HtmlTag tag = (HtmlTag) token.getValue();
					if (tag.getAttribute("resource") != null) { //$NON-NLS-1$
						String resourceID = tag.getAttribute("resource"); //$NON-NLS-1$
						tag.setAttribute("resource", resourceID.replace("&", "&amp;")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
					if (tag.getAttribute("rdf:about") != null) { //$NON-NLS-1$
						String resourceID = tag.getAttribute("rdf:about"); //$NON-NLS-1$
						tag.setAttribute("rdf:about", resourceID.replace("&", "&amp;")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					}
				}
				if (!token.toString().startsWith("<?xml")) { //$NON-NLS-1$
					content.append(token.toString());
				}
			}
			content.flush();
			content.close();
			return new BufferedReader(new FileReader(tempFile));
		} catch (IOException e) {

		} catch (ParseException e) {

		}
		return null;
	}

}
