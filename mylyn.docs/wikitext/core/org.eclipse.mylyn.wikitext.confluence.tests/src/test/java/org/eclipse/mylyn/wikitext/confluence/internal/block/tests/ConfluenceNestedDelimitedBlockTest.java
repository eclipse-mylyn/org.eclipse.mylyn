/*******************************************************************************
 * Copyright (c) 2007, 2024 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.confluence.internal.block.tests;

import org.eclipse.mylyn.wikitext.confluence.ConfluenceLanguage;
import org.eclipse.mylyn.wikitext.toolkit.AbstractMarkupGenerationTest;
import org.junit.Test;

@SuppressWarnings({ "nls", "restriction" })
public class ConfluenceNestedDelimitedBlockTest extends AbstractMarkupGenerationTest<ConfluenceLanguage> {

	@Override
	protected ConfluenceLanguage createMarkupLanguage() {
		return new ConfluenceLanguage();
	}

	@Test
	public void quoteBlockContainingTable() {
		assertMarkup(
				"""
				<blockquote><table><tr><th><strong>Column 1</strong></th><th><strong>Column 2</strong></th><th><strong>Column 3</strong></th></tr>\
				<tr><td>Cell 1</td><td>Cell 2</td><td>Cell 3</td></tr>\
				<tr><td>Cell 4</td><td>Cell 5</td><td>Cell 6</td></tr></table>\
				<p>Thank you</p></blockquote>""",
				"""
				{quote}\
				||*Column 1*||*Column 2*||*Column 3*||
				| Cell 1 | Cell 2| Cell 3|
				| Cell 4| Cell 5| Cell 6|

				Thank you
				{quote}""");
	}

	@Test
	public void quoteBlockContainingTableContainingList() {
		assertMarkup(
				"""
				<blockquote><table><tr><th><strong>Column 1</strong></th><th><strong>Column 2</strong></th><th><strong>Column 3</strong></th></tr>\
				<tr><td><ul><li>Bullet 1 </li></ul></td><td><ul><li>Bullet 2</li></ul></td><td><ul><li>Bullet 3</li></ul></td></tr>\
				<tr><td><ul><li>Bullet 4</li></ul></td><td><ul><li>Bullet 5</li></ul></td><td><ul><li>Bullet 6</li></ul></td></tr></table>\
				<p>Thank you</p></blockquote>""",
				"""
				{quote}\
				||*Column 1*||*Column 2*||*Column 3*||
				| * Bullet 1 | * Bullet 2| * Bullet 3|
				| * Bullet 4| * Bullet 5| * Bullet 6|

				Thank you
				{quote}""");
	}

	@Test
	public void quoteBlockContainingTableContainingMultiLineList() {
		assertMarkup(
				"""
				<blockquote><table><tr><th><strong>Column 1</strong></th><th><strong>Column 2</strong></th></tr>\
				<tr><td><ul><li>1.1</li><li>1.2<ul><li>1.2.1</li><li>1.2.2</li></ul></li></ul></td><td><ul><li>2.1</li><li>2.2</li><li>2.3</li></ul></td></tr>\
				<tr><td><ul><li>3.1</li></ul></td><td><ul><li>4.1</li><li>4.2</li><li>4.3</li><li>4.4</li></ul></td></tr></table>\
				<p>Thank you</p></blockquote>""",
				"""
				{quote}\
				||*Column 1*||*Column 2*||
				|* 1.1
				* 1.2
				** 1.2.1
				** 1.2.2\
				|* 2.1
				* 2.2
				* 2.3|
				|* 3.1\
				|* 4.1
				* 4.2
				* 4.3
				* 4.4|

				Thank you
				{quote}""");
	}

}
