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

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITypedRegion;
import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.textile.TextileLanguage;
import org.junit.Test;

/**
 * @author David Green
 */
public class FastMarkupPartitionerTest extends AbstractDocumentTest {
	@Test
	public void testConnectLargeDocument() throws IOException {
		IDocument document = createDocument("resources/large.textile");
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	@Test
	public void testTextileCausesExceptionIssue36() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		document.set("""
				a
				# a
				a
				# a
				a
				# a

				h2. a""");

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	@Test
	public void testTextileNestedPhraseModifiersException() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		document.set("a _sample *bold*_");

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	/**
	 * test for bug 273100
	 */
	@Test
	public void testConfluenceTipException_bug273100() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new ConfluenceLanguage());

		document.set("{tip}\ntext1\n\ntext2\n\ntext3\n{tip}\ntext4\n");

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
	}

	/**
	 * test color
	 */
	@Test
	public void testConfluenceColor() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new ConfluenceLanguage());

		document.set("{color:red}\ntext1\n\ntext2{color}\ntext3\n");
		// ...........012345678901.234567.8.9012345678901.234567.
		// .....................10...........20.........30.......

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

//		MarkupPartition(type=DIV,offset=0,length=12,end=12)
//		MarkupPartition(type=PARAGRAPH,offset=12,length=7,end=19)
//		MarkupPartition(type=PARAGRAPH,offset=19,length=13,end=32)
//		MarkupPartition(type=PARAGRAPH,offset=32,length=6,end=38)

		int[][] expected = { //
				{ 0, 12 }, //
				{ 12, 7 }, //
				{ 19, 13 }, //
				{ 32, 6 }, //
		};

		ITypedRegion[] partitioning = partitioner.computePartitioning(0, document.getLength(), false);
		assertPartitioningAsExpected(expected, partitioning);
	}

	@Test
	public void testConfluenceColor2() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new ConfluenceLanguage());

		document.set("{color:blue}\n{tip}\ntip\n{tip}\ntext\n{note}\nnote\n{note}\n");
		// ...........0123456789012.345678.9012.345678.90123.4567890.12345.6789012.
		// .....................10..........20..........30.........40..........50..
		//
		// MarkupPartition(type=PARAGRAPH,offset=19,length=10,end=29) cannot start before partition
		// MarkupPartition(type=PARAGRAPH,offset=29,length=5,end=34).

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);
//
//		expect:
//
//			MarkupPartition(type=DIV,offset=0,length=13,end=13)
//			MarkupPartition(type=TIP,offset=13,length=6,end=19)
//			MarkupPartition(type=PARAGRAPH,offset=19,length=10,end=29)
//			MarkupPartition(type=PARAGRAPH,offset=29,length=5,end=34)
//			MarkupPartition(type=NOTE,offset=34,length=7,end=41)
//			MarkupPartition(type=PARAGRAPH,offset=41,length=12,end=53)

		int[][] expected = { //
				{ 0, 13 }, //
				{ 13, 6 }, //
				{ 19, 10 }, //
				{ 29, 5 }, //
				{ 34, 7 }, //
				{ 41, 12 } //
		};

		ITypedRegion[] partitioning = partitioner.computePartitioning(0, document.getLength(), false);
		assertPartitioningAsExpected(expected, partitioning);
	}

	@Test
	public void testConfluenceColor3() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new ConfluenceLanguage());

		document.set(
				"views to help SOA  development. These includes [SOA Services Explorer](in green) for the list of all available SOA services,\n[Types Explorer](in {color:#0000ff}blue{color}) for searching and browsing all available ");

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		partitioner.computePartitioning(0, document.getLength(), false);
	}

	/**
	 * bug 314131
	 */
	@Test
	public void testTextileLinkInBold() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		document.set("*\"text\":url*");
		// ...........012345678901.234567.8.9012345678901.234567.
		// .....................10...........20.........30.......

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		int[][] expected = { //
				{ 0, 12 }, //
		};

		ITypedRegion[] partitioning = partitioner.computePartitioning(0, document.getLength(), false);
		assertPartitioningAsExpected(expected, partitioning);
	}

	private void assertPartitioningAsExpected(int[][] expected, ITypedRegion[] partitioning) {
		assertEquals(expected.length, partitioning.length);
		for (int x = 0; x < expected.length; ++x) {
			ITypedRegion region = partitioning[x];
			assertEquals(
					String.format("partition %s offset expected %s but got %s", x, expected[x][0], region.getOffset()),
					expected[x][0], region.getOffset());
			assertEquals(
					String.format("partition %s length expected %s but got %s", x, expected[x][1], region.getLength()),
					expected[x][1], region.getLength());
		}
	}

	@Test
	public void testTextileLinkWithStyle() {
		IDocument document = new Document();
		FastMarkupPartitioner partitioner = new FastMarkupPartitioner();
		partitioner.setMarkupLanguage(new TextileLanguage());

		String markup = "\"_text_\":http://example.com";
		document.set(markup);

		partitioner.connect(document);
		document.setDocumentPartitioner(partitioner);

		int[][] expected = { //
				{ 0, markup.length() }, //
		};

		ITypedRegion[] partitioning = partitioner.computePartitioning(0, document.getLength(), false);
		assertPartitioningAsExpected(expected, partitioning);
	}

}
