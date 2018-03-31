/*******************************************************************************
 * Copyright (c) 2007, 2008 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile;

import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.junit.Test;

public class TextileNoTextileTest extends AbstractMarkupGenerationTest<TextileLanguage> {

	@Override
	protected TextileLanguage createMarkupLanguage() {
		return new TextileLanguage();
	}

	@Test
	public void noTextileWithNestedMarkup() {
		assertMarkup("*MyType==[1]==*", "<notextile>*MyType==[1]==*</notextile>");
	}

	@Test
	public void noTextileWithNestedMarkupInParagraph() {
		assertMarkup("<p>asdf *MyType==[1]==*</p>", "asdf <notextile>*MyType==[1]==*</notextile>");
	}

	@Test
	public void midParagraph() {
		assertMarkup("<p>text with <b>no</b> textile</p>", "text with <notextile><b>no</b></notextile> textile");
	}

	@Test
	public void withoutClosingTag() {
		assertMarkup("<b>no</b> textile", "<notextile><b>no</b> textile");
	}

	@Test
	public void multiLine() {
		assertMarkup("  one\n<em>two</em>\n", "<notextile>  \none\n<em>two</em>\n</notextile>");
	}
}
