/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
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

package org.eclipse.mylyn.wikitext.textile.internal.block.tests;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.eclipse.mylyn.wikitext.textile.internal.block.CommentBlock;
import org.junit.Test;

public class CommentBlockTest {

	@Test
	public void mustStartAtLineStart() {
		assertTrue(new CommentBlock().canStart("###. comment", 0));
		assertFalse(new CommentBlock().canStart(" ###. comment", 0));
		assertFalse(new CommentBlock().canStart(" ###. comment", 1));
	}
}
