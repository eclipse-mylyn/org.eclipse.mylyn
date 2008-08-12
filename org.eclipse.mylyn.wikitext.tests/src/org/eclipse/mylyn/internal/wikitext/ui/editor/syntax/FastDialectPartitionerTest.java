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

import java.io.IOException;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.wikitext.textile.core.TextileLanguage;

/**
 * 
 * 
 * @author David Green
 */
public class FastDialectPartitionerTest extends AbstractDocumentTest {

	public void testConnectLargeDocument() throws IOException {
		IDocument document = createDocument("resources/large.textile");
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		long millis = System.currentTimeMillis();
		long nanos = System.nanoTime();

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		long nanosEnd = System.nanoTime();
		long millisEnd = System.currentTimeMillis();

		System.out.println("Elapsed Time in Nanos: " + (nanosEnd - nanos));
		System.out.println("Elapsed Time in Millis: " + (millisEnd - millis));

		assertTrue((nanosEnd - nanos) < 800000000L);
	}

	public void testTextileCausesExceptionIssue36() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		document.set("a\n" + "# a\n" + "a\n" + "# a\n" + "a\n" + "# a\n" + "\n" + "h2. a");

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}
}
