/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests.web;

import java.util.regex.Matcher;

import org.eclipse.mylyn.internal.web.tasks.NamedPattern;

import junit.framework.TestCase;

/**
 * @author Eugene Kuleshov
 */
public class NamedPatternTest extends TestCase {

	public void testNamedGroups() {
		NamedPattern p = new NamedPattern("({Hour}\\d\\d):({Minute}\\d\\d):({Second}\\d\\d)", 0);
		assertEquals("(\\d\\d):(\\d\\d):(\\d\\d)", p.getPattern().pattern());
		assertEquals(3, p.getGroups().size());
		assertEquals("Hour", p.groupName(0));
		assertEquals("Minute", p.groupName(1));
		assertEquals("Second", p.groupName(2));
		
		Matcher m = p.matcher("01:02:03");
		assertTrue(m.find());
		assertEquals("01", p.group("Hour", m));
		assertEquals("02", p.group("Minute", m));
		assertEquals("03", p.group("Second", m));
	}

	public void testUnnamedGroups() {
		NamedPattern p = new NamedPattern("(\\d\\d):(\\d\\d):(\\d\\d)", 0);
		assertEquals("(\\d\\d):(\\d\\d):(\\d\\d)", p.getPattern().pattern());
		assertEquals(0, p.getGroups().size());
		
		Matcher m = p.matcher("01:02:03");
		assertTrue(m.find());
		assertEquals("01", m.group(1));
		assertEquals("02", m.group(2));
		assertEquals("03", m.group(3));
	}
	
	public void testNestedGroups() {
		NamedPattern p = new NamedPattern(":({a}:({b}:({c}foo)boo)doo)", 0);
		assertEquals(":(:(:(foo)boo)doo)", p.getPattern().pattern());
		assertEquals(3, p.getGroups().size());
		
		Matcher m = p.matcher(":::fooboodoo");
		assertTrue(m.find());
		assertEquals("::fooboodoo", p.group("a", m));
		assertEquals(":fooboo", p.group("b", m));
		assertEquals("foo", p.group("c", m));
	}
	
}
