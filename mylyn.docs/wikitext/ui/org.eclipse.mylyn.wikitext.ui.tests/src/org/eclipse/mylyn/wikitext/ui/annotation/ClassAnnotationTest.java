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
package org.eclipse.mylyn.wikitext.ui.annotation;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.junit.Test;

/**
 * @author David Green
 */
@HeadRequired
@SuppressWarnings({ "nls", "restriction" })
public class ClassAnnotationTest {
	@Test
	public void testSimple() {
		ClassAnnotation annotation = new ClassAnnotation("foo");
		assertEquals("foo", annotation.getCssClass());
		assertEquals(ClassAnnotation.TYPE, annotation.getType());
	}
}
