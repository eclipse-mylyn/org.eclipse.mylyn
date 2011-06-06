/*******************************************************************************
 * Copyright (c) 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.syntax;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.eclipse.mylyn.wikitext.tests.TestUtil;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;

@HeadRequired
public class MarkupTokenScannerTest extends AbstractDocumentTest {

	private MarkupTokenScanner tokenScanner;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		Font normalFont = new Font(null, new FontData[] { new FontData("normalFont", 12, 0) });
		Font monospaceFont = new Font(null, new FontData[] { new FontData("monoFont", 12, 0) });
		tokenScanner = new MarkupTokenScanner(normalFont, monospaceFont);
	}

	public void testTextileLinkWithStyle() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		String markup = "\"_text_\":http://example.com";
		document.set(markup);

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		partitioner.computePartitioning(0, document.getLength(), false);

		tokenScanner.setRange(document, 0, document.getLength());

		List<MarkupTokenScanner.Token> tokens = new ArrayList<MarkupTokenScanner.Token>();
		for (IToken token = tokenScanner.nextToken(); token != Token.EOF; token = tokenScanner.nextToken()) {
			TestUtil.println(token);
			tokens.add((MarkupTokenScanner.Token) token);
		}

		// expecting:
//		Token [offset=0, length=1]
//		Token [offset=1, length=6]
//		Token [offset=7, length=20]

		assertEquals(3, tokens.size());

		assertEquals(0, tokens.get(0).getOffset());
		assertEquals(1, tokens.get(0).getLength());
		assertFalse(tokens.get(0).getFontState().isItalic());

		assertEquals(1, tokens.get(1).getOffset());
		assertEquals(6, tokens.get(1).getLength());
		assertTrue(tokens.get(1).getFontState().isItalic());

		assertEquals(7, tokens.get(2).getOffset());
		assertEquals(20, tokens.get(2).getLength());
		assertFalse(tokens.get(2).getFontState().isItalic());
	}
}
