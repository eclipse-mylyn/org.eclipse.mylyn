/*******************************************************************************
 * Copyright (c) 2007, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.wikitext.ui.annotation;

import static org.junit.Assert.assertEquals;

import org.eclipse.mylyn.wikitext.tests.HeadRequired;
import org.junit.Test;

/**
 * @author David Green
 */
@HeadRequired
@SuppressWarnings("nls")
public class AnchorNameAnnotationTest {
	@Test
	public void testSimple() {
		AnchorNameAnnotation annotation = new AnchorNameAnnotation("foo");
		assertEquals("foo", annotation.getText());
		assertEquals(AnchorNameAnnotation.TYPE, annotation.getType());
	}
}
