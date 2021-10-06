/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
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
package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;

/**
 * @author David Green
 */
public abstract class AbstractDocumentTest {

	protected IDocument createDocument(String resource) throws IOException {
		Document document = new Document();

		Reader reader = new BufferedReader(
				new InputStreamReader(FastMarkupPartitionerTest.class.getResourceAsStream(resource)));
		try {
			int i;
			StringBuilder buf = new StringBuilder(4096);
			while ((i = reader.read()) != -1) {
				buf.append((char) i);
			}
			document.set(buf.toString());
		} finally {
			reader.close();
		}
		return document;
	}

}
