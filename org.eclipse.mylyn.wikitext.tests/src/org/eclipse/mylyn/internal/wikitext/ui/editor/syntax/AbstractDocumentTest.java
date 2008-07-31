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
import java.io.InputStreamReader;
import java.io.Reader;

import junit.framework.TestCase;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

/**
 *
 *
 * @author David Green
 */
public abstract class AbstractDocumentTest extends TestCase {

	protected IDocument createDocument(String resource) throws IOException {
		Document document = new Document();

		Reader reader = new BufferedReader(new InputStreamReader(FastDialectPartitionerTest.class.getResourceAsStream(resource)));
		try {
			int i;
			StringBuilder buf = new StringBuilder(4096);
			while ((i = reader.read()) != -1) {
				buf.append((char)i);
			}
			document.set(buf.toString());
		} finally {
			reader.close();
		}
		return document;
	}

}
