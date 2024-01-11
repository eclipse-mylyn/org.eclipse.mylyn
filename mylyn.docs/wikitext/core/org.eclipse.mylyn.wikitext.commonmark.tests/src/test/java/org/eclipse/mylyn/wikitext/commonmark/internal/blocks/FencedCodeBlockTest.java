/*******************************************************************************
 * Copyright (c) 2015, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.commonmark.internal.blocks;

import static org.eclipse.mylyn.wikitext.commonmark.internal.CommonMarkAsserts.assertContent;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.commonmark.internal.LineSequence;
import org.junit.Test;

public class FencedCodeBlockTest {

	private final FencedCodeBlock block = new FencedCodeBlock();

	@Test
	public void canStart() {
		assertCanStart("```");
		assertCanStart(" ```");
		assertCanStart("  ```");
		assertCanStart("   ```");
		assertCanStart("````````````````");
		assertCanStart("    ```");
		assertCanStartFalse("     ```");
		assertCanStart("~~~");
		assertCanStart(" ~~~");
		assertCanStart("  ~~~");
		assertCanStart("   ~~~");
		assertCanStart("    ~~~");
		assertCanStartFalse("     ~~~");
		assertCanStart("~~~~~~~~~~~~~~~~~");
		assertCanStartFalse("``` one ``");
	}

	@Test
	public void canStartWithInfoText() {
		assertCanStart("```````````````` some info text");
		assertCanStart("~~~~~~~~~ some info text");
	}

	@Test
	public void basic() {
		assertContent(
				"<p>first para</p><pre><code class=\"language-java\">public void foo() {\n\n}\n</code></pre><p>text</p>",
				"first para\n\n```` java and stuff\npublic void foo() {\n\n}\n````\ntext");
	}

	@Test
	public void encodedCharacters() {
		assertContent("<pre><code>&lt;\n &gt;\n</code></pre>", "```\n<\n >\n```");
	}

	@Test
	public void infoString() {
		assertContent("<pre class=\"language-info\"><code class=\"language-info\">code here\n</code></pre>",
				"``` info\ncode here\n```");
	}

	private void assertCanStartFalse(String content) {
		assertFalse(content, block.canStart(LineSequence.create(content)));
	}

	private void assertCanStart(String content) {
		assertTrue(content, block.canStart(LineSequence.create(content)));
	}
}
