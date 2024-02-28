/*******************************************************************************
 * Copyright (c) 2018, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License 2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Kevin de Vlaming - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.creole.tests.documentbuilder;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.junit.Test;

/**
 * @see http://www.wikicreole.org/wiki/Elements
 * @author Kevin de Vlaming
 */
@SuppressWarnings({ "nls", "restriction" })
public class CreoleDocumentBuilderTest extends AbstractCreoleDocumentBuilderTest {
	@Test
	public void testLineBreak() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("line\\\\break\n\n");
	}

	@Test
	public void testLineBreakImplicitParagraph() {
		builder.beginDocument();
		builder.characters("line");
		builder.lineBreak();
		builder.characters("break");
		builder.endDocument();
		assertMarkup("line\\\\break\n\n");
	}

	@Test
	public void testHeadings() {
		builder.beginDocument();
		builder.beginHeading(1, new Attributes());
		builder.characters("This is an H1");
		builder.endHeading();
		builder.beginHeading(2, new Attributes());
		builder.characters("This is an H2");
		builder.endHeading();
		builder.beginHeading(6, new Attributes());
		builder.characters("This is an H6");
		builder.endHeading();
		builder.endDocument();
		assertMarkup("= This is an H1\n\n== This is an H2\n\n====== This is an H6\n\n");
	}

	@Test
	public void testHorizontalRule() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("horizontal");
		builder.horizontalRule();
		builder.characters("rule");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("horizontal\n----\nrule\n\n");
	}

	@Test
	public void testEscapedTilde() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("this ~ is interpreted as an escape");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("this ~~ is interpreted as an escape\n\n");
	}

	@Test
	public void testEntityReference() {
		builder.beginDocument();
		builder.beginBlock(BlockType.PARAGRAPH, new Attributes());
		builder.characters("5 ");
		builder.entityReference("gt");
		builder.characters(" 4");
		builder.endBlock();
		builder.endDocument();
		assertMarkup("5 > 4\n\n");
	}

	@Test
	public void testEntityReferenceImplicitParagraph() {
		builder.beginDocument();
		builder.characters("4 ");
		builder.entityReference("lt");
		builder.characters(" 5");
		builder.endDocument();
		assertMarkup("4 < 5\n\n");
	}

	@Test
	public void testUnknownEntityReference() {
		builder.beginDocument();
		builder.entityReference("unknown");
		builder.characters(" reference");
		builder.endDocument();
		assertMarkup("&unknown; reference\n\n");
	}

}