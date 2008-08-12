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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;

/**
 * 
 * 
 * @author David Green
 */
public class DialectDocumentProviderTest extends TestCase {

	public void testCleanUpEolMarkers() {
		String[] lines = new String[] { "one", "two", "three" };
		doTestCleanUpEolMarkers(lines, "\r");
		doTestCleanUpEolMarkers(lines, "\r\n");
		doTestCleanUpEolMarkers(lines, "\n");
	}

	private void doTestCleanUpEolMarkers(String[] lines, String documentEol) {
		StringBuilder buf = new StringBuilder();
		for (String line : lines) {
			if (buf.length() > 0) {
				buf.append(documentEol);
			}
			buf.append(line);
		}
		Document document = new Document();
		document.set(buf.toString());
		MarkupDocumentProvider.cleanUpEolMarkers(document);

		try {
			String line;
			int index = -1;
			BufferedReader reader = new BufferedReader(new StringReader(document.get()));
			while ((line = reader.readLine()) != null) {
				assertEquals(lines[++index], line);
			}
		} catch (IOException e) {
			throw new IllegalStateException(e);
		}
	}
}
