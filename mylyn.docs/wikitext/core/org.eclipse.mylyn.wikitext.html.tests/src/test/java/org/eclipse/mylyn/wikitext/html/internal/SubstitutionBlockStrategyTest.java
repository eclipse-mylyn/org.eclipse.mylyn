/*******************************************************************************
 * Copyright (c) 2013, 2024 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.html.internal;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.BlockType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

public class SubstitutionBlockStrategyTest {

	private HtmlDocumentBuilder builder;

	private StringWriter writer;

	@Before
	public void before() {
		writer = new StringWriter();
		builder = new HtmlDocumentBuilder(writer);
		builder.setEmitAsDocument(false);
	}

	@Test
	public void substitution() {
		SubstitutionBlockStrategy strategy = new SubstitutionBlockStrategy(BlockType.PARAGRAPH);
		strategy.beginBlock(builder, BlockType.CODE, new Attributes());
		builder.characters("test");
		strategy.endBlock(builder);
		assertEquals("<p>test</p>", writer.toString());
	}
}
