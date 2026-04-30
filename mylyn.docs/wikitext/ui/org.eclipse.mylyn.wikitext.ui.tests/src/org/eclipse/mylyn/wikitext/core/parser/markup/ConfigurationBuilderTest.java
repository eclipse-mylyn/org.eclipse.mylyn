/*******************************************************************************
 * Copyright (c) 2011, 2024 Tasktop Technologies and others.
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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;


import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.List;

import org.eclipse.mylyn.wikitext.parser.markup.Block;
import org.eclipse.mylyn.wikitext.parser.markup.ConfigurationBuilder;
import org.eclipse.mylyn.wikitext.parser.markup.MarkupLanguageConfiguration;
import org.eclipse.mylyn.wikitext.parser.markup.block.EclipseErrorDetailsBlock;
import org.eclipse.mylyn.wikitext.parser.markup.block.JavaStackTraceBlock;
import org.junit.jupiter.api.Test;

/**
 * test for {@link ConfigurationBuilder}
 *
 * @author David Green
 */
@SuppressWarnings({ "nls", "restriction" })
public class ConfigurationBuilderTest {
	@Test
	public void testRepositorySettings() {

		MarkupLanguageConfiguration configuration = ConfigurationBuilder.create().repositorySettings().configuration();

		assertFalse(configuration.isEnableUnwrappedParagraphs());
		assertTrue(configuration.isEscapingHtmlAndXml());
		assertTrue(configuration.isNewlinesMustCauseLineBreak());
		assertTrue(configuration.isOptimizeForRepositoryUsage());

		assertHasInstance(configuration.getBlocks(), EclipseErrorDetailsBlock.class);
		assertHasInstance(configuration.getBlocks(), JavaStackTraceBlock.class);

	}

	private void assertHasInstance(List<Block> blocks, Class<? extends Block> clazz) {
		for (Block block : blocks) {
			if (clazz.isAssignableFrom(block.getClass())) {
				return;
			}
		}
		fail("Expected instance of " + clazz.getName() + " but instead got " + blocks);
	}
}
