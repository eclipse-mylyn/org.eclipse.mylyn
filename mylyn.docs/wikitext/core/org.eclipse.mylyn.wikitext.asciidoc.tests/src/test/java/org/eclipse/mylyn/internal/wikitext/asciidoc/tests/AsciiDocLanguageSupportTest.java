/*******************************************************************************
 * Copyright (c) 2016, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;
import org.junit.jupiter.api.Test;

/**
 * Unit Tests for {@link LanguageSupport}
 */
@SuppressWarnings("nls")
public class AsciiDocLanguageSupportTest extends AsciiDocLanguageTestBase {

	@Test
	public void testParseFormattingProperties() throws Exception {
		Map<String, String> map = LanguageSupport.parseFormattingProperties("key=\"value\"", Collections.emptyList());
		assertEquals(1, map.size(), "size");
		assertEquals("value", map.get("key"), "key");
	}

	@Test
	public void testParseFormattingPropertiesWithComma() throws Exception {
		Map<String, String> map = LanguageSupport.parseFormattingProperties("key1=\"v1,v2\",key2=\"val\"",
				Collections.emptyList());
		assertEquals(2, map.size(), "size");
		assertEquals("v1,v2", map.get("key1"), "key1");
		assertEquals("val", map.get("key2"), "key1");
	}

	@Test
	public void testParseFormattingPropertiesPositionalParameter() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("k1");
		list.add("k2");
		list.add("k3");
		Map<String, String> map = LanguageSupport.parseFormattingProperties("10,20,30", list);
		assertEquals(3, map.size(), "size");
		assertEquals("10", map.get("k1"), "k1");
		assertEquals("20", map.get("k2"), "k2");
		assertEquals("30", map.get("k3"), "k3");
	}

	@Test
	public void testComputeColumnsAttributeListSimple() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("<,^,>");
		assertEquals(3, columnsAttributeList.size(), "size");
		assertEquals("left", columnsAttributeList.get(0).getAlign(), "(0) align");
		assertEquals("center", columnsAttributeList.get(1).getAlign(), "(1) align");
		assertEquals("right", columnsAttributeList.get(2).getAlign(), "(2) align");

	}

	@Test
	public void testComputeColumnsAttributeListRepeat() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("3*");
		assertEquals(3, columnsAttributeList.size(), "size");
	}

	@Test
	public void testComputeColumnsAttributeListMixed() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("2*<,.<");
		assertEquals(3, columnsAttributeList.size(), "size");
		assertEquals("left", columnsAttributeList.get(0).getAlign(), "(0) align");
		assertEquals("left", columnsAttributeList.get(1).getAlign(), "(1) align");
		assertEquals("top", columnsAttributeList.get(2).getValign(), "(2) vertical align");
	}

	@Test
	public void testComputeColumnsAttributeListProportional() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("1,2,6");
		assertEquals(3, columnsAttributeList.size(), "size");
	}

	@Test
	public void testComputeColumnsAttributeListPercentage() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("10,20,70");
		assertEquals(3, columnsAttributeList.size(), "size");
	}

	@Test
	public void testComputeColumnsAttributeListCustomWidthsAndAlignments() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("<.<2,>.^5,^.>3");
		assertEquals(3, columnsAttributeList.size(), "size");
		assertEquals("left", columnsAttributeList.get(0).getAlign(), "(0) align");
		assertEquals("top", columnsAttributeList.get(0).getValign(), "(0) vertical align");
		assertEquals("right", columnsAttributeList.get(1).getAlign(), "(1) align");
		assertEquals("middle", columnsAttributeList.get(1).getValign(), "(1) vertical align");
		assertEquals("center", columnsAttributeList.get(2).getAlign(), "(2) align");
		assertEquals("bottom", columnsAttributeList.get(2).getValign(), "(2) vertical align");
	}
}
