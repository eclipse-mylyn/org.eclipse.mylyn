/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.html;

import static org.junit.Assert.assertEquals;

import java.io.StringWriter;

import org.eclipse.mylyn.internal.wikitext.html.SubstitutionSpanStrategy;
import org.eclipse.mylyn.wikitext.parser.Attributes;
import org.eclipse.mylyn.wikitext.parser.DocumentBuilder.SpanType;
import org.eclipse.mylyn.wikitext.parser.builder.HtmlDocumentBuilder;
import org.junit.Before;
import org.junit.Test;

public class SubstitutionSpanStrategyTest {

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
		SubstitutionSpanStrategy strategy = new SubstitutionSpanStrategy(SpanType.BOLD);
		strategy.beginSpan(builder, SpanType.CODE, new Attributes());
		builder.characters("test");
		strategy.endSpan(builder);
		assertEquals("<b>test</b>", writer.toString());
	}
}
