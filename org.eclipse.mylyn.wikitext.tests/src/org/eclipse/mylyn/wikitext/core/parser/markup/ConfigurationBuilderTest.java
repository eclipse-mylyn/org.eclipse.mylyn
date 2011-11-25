/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.core.parser.markup;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.wikitext.core.parser.markup.block.EclipseErrorDetailsBlock;
import org.eclipse.mylyn.wikitext.core.parser.markup.block.JavaStackTraceBlock;
import org.junit.Assert;

/**
 * test for {@link ConfigurationBuilder}
 * 
 * @author David Green
 */
public class ConfigurationBuilderTest extends TestCase {

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
		Assert.fail("Expected instance of " + clazz.getName() + " but instead got " + blocks);
	}
}
