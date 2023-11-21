/*******************************************************************************
 * Copyright (c) 2017 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.asciidoc.internal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class AsciiDocIdGenerationStrategyTest {

	@Test
	public void computeHeadingId() {
		assertEquals("", AsciiDocIdGenerationStrategy.computeHeadingId("", "", "_"));
		assertEquals("lorem ipsum", AsciiDocIdGenerationStrategy.computeHeadingId("lorem ipsum", null, null));
		assertEquals("some_h2", AsciiDocIdGenerationStrategy.computeHeadingId("SOME    H2", "", "_"));
		assertEquals("_this-is-an-h2", AsciiDocIdGenerationStrategy.computeHeadingId("This is an H2", "_", "-"));
		assertEquals("_test_2", AsciiDocIdGenerationStrategy.computeHeadingId("test 2", "_", "_"));
	}

}
