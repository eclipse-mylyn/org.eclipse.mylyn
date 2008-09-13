/*******************************************************************************
 * Copyright (c) 2004, 2008 Willian Mitsuda and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Willian Mitsuda - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.tasks.core.CommentQuoter;

/**
 * Test many quoting scenarios
 * 
 * @author Willian Mitsuda
 */
public class CommentQuoterTest extends TestCase {

	public void testNoWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa");
		assertEquals("> bababa\n", quotedText);
	}

	public void testSimpleWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa bobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	public void testNoWayToWrap() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("babababababa");
		assertEquals("> babababababa\n", quotedText);
	}

	public void testExactWrap() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababababa");
		assertEquals("> bababababa\n", quotedText);
	}

	public void testMultiLineNoWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa\nbobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	public void testMultiLineWithWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa bebebe\nbibibibibibi bibi\nbobobo bububu");
		assertEquals("> bababa\n> bebebe\n> bibibibibibi\n> bibi\n> bobobo\n> bububu\n", quotedText);
	}

	public void testExcessiveSpacingWrapping() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa        bobobo");
		assertEquals("> bababa\n> bobobo\n", quotedText);
	}

	public void testBlankLineQuoting() {
		CommentQuoter quoter = new CommentQuoter(10);
		String quotedText = quoter.quote("bababa\n\nbobobo");
		assertEquals("> bababa\n> \n> bobobo\n", quotedText);
	}

}
