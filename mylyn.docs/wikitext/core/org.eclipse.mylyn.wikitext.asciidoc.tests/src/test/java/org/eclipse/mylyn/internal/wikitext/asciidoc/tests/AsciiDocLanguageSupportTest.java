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
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.asciidoc.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.wikitext.asciidoc.internal.util.LanguageSupport;
import org.eclipse.mylyn.wikitext.parser.TableCellAttributes;
import org.junit.Test;

/**
 * Unit Tests for {@link LanguageSupport}
 */
public class AsciiDocLanguageSupportTest extends AsciiDocLanguageTestBase {

	@Test
	public void testParseFormattingProperties() throws Exception {
		Map<String, String> map = LanguageSupport.parseFormattingProperties("key=\"value\"", Collections.emptyList());
		assertEquals("size", 1, map.size());
		assertEquals("key", "value", map.get("key"));
	}

	@Test
	public void testParseFormattingPropertiesWithComma() throws Exception {
		Map<String, String> map = LanguageSupport.parseFormattingProperties("key1=\"v1,v2\",key2=\"val\"",
				Collections.emptyList());
		assertEquals("size", 2, map.size());
		assertEquals("key1", "v1,v2", map.get("key1"));
		assertEquals("key1", "val", map.get("key2"));
	}

	@Test
	public void testParseFormattingPropertiesPositionalParameter() throws Exception {
		List<String> list = new ArrayList<>();
		list.add("k1");
		list.add("k2");
		list.add("k3");
		Map<String, String> map = LanguageSupport.parseFormattingProperties("10,20,30", list);
		assertEquals("size", 3, map.size());
		assertEquals("k1", "10", map.get("k1"));
		assertEquals("k2", "20", map.get("k2"));
		assertEquals("k3", "30", map.get("k3"));
	}

	@Test
	public void testComputeColumnsAttributeListSimple() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("<,^,>");
		assertEquals("size", 3, columnsAttributeList.size());
		assertEquals("(0) align", "left", columnsAttributeList.get(0).getAlign());
		assertEquals("(1) align", "center", columnsAttributeList.get(1).getAlign());
		assertEquals("(2) align", "right", columnsAttributeList.get(2).getAlign());

	}

	@Test
	public void testComputeColumnsAttributeListRepeat() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("3*");
		assertEquals("size", 3, columnsAttributeList.size());
	}

	@Test
	public void testComputeColumnsAttributeListMixed() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("2*<,.<");
		assertEquals("size", 3, columnsAttributeList.size());
		assertEquals("(0) align", "left", columnsAttributeList.get(0).getAlign());
		assertEquals("(1) align", "left", columnsAttributeList.get(1).getAlign());
		assertEquals("(2) vertical align", "top", columnsAttributeList.get(2).getValign());
	}

	@Test
	public void testComputeColumnsAttributeListProportional() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("1,2,6");
		assertEquals("size", 3, columnsAttributeList.size());
	}

	@Test
	public void testComputeColumnsAttributeListPercentage() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("10,20,70");
		assertEquals("size", 3, columnsAttributeList.size());
	}

	@Test
	public void testComputeColumnsAttributeListCustomWidthsAndAlignments() {
		List<TableCellAttributes> columnsAttributeList = LanguageSupport.computeColumnsAttributeList("<.<2,>.^5,^.>3");
		assertEquals("size", 3, columnsAttributeList.size());
		assertEquals("(0) align", "left", columnsAttributeList.get(0).getAlign());
		assertEquals("(0) vertical align", "top", columnsAttributeList.get(0).getValign());
		assertEquals("(1) align", "right", columnsAttributeList.get(1).getAlign());
		assertEquals("(1) vertical align", "middle", columnsAttributeList.get(1).getValign());
		assertEquals("(2) align", "center", columnsAttributeList.get(2).getAlign());
		assertEquals("(2) vertical align", "bottom", columnsAttributeList.get(2).getValign());
	}
}
