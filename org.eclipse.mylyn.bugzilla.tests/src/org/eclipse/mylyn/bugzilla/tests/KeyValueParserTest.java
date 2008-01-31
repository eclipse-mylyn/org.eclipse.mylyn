/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests;

import java.util.Map;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.bugzilla.ide.KeyValueParser;

/**
 * @author Steffen Pingel
 */
public class KeyValueParserTest extends TestCase {

	public void testParse() throws Exception {
		KeyValueParser parser = new KeyValueParser("foo=bar");
		Map<String, String> values = parser.parse();
		assertEquals(1, values.size());
		assertEquals("bar", values.get("foo"));
	}

}
