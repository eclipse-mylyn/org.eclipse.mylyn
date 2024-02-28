/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.block;

import static org.junit.Assert.assertTrue;

import java.util.regex.Pattern;

import org.junit.Test;

@SuppressWarnings("nls")
public class TableBlockTest {

	private final Pattern cellContentPattern = Pattern.compile(TableBlock.CELL_CONTENT_REGEX);

	@Test
	public void cellContent() {
		assertCellContent("some cell \\| content with pipe escaped");
		assertCellContent("some cell \\[ content with bracket escaped");
		assertCellContent("some cell \\\\ content with escape escaped");
		assertCellContent("some cell \\ content with backslash");
	}

	private void assertCellContent(String string) {
		assertTrue(cellContentPattern.matcher(string).matches());
	}
}
