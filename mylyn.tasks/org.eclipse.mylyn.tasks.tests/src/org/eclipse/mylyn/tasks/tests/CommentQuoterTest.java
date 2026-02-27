/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;
import org.junit.jupiter.api.Test;

/**
 * Test many quoting scenarios
 *
 * @author Willian Mitsuda
 */
@SuppressWarnings("nls")
public class CommentQuoterTest {

	@Test
	public void testNoWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa");
		assertEquals("> bababa\n", quotedText);
	}

	@Test
	public void testSimpleWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa bobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	@Test
	public void testNoWayToWrap() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("babababababa");
		assertEquals("> babababababa\n", quotedText);
	}

	@Test
	public void testExactWrap() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababababa");
		assertEquals("> bababababa\n", quotedText);
	}

	@Test
	public void testMultiLineNoWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa\nbobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	@Test
	public void testMultiLineWithWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa bebebe\nbibibibibibi bibi\nbobobo bububu");
		assertEquals("> bababa\n> bebebe\n> bibibibibibi\n> bibi\n> bobobo\n> bububu\n", quotedText);
	}

	@Test
	public void testExcessiveSpacingWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa        bobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	@Test
	public void testBlankLineQuoting() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa\n\nbobobo");
		assertEquals("> bababa\n> \n> bobobo\n", quotedText);
	}

}
