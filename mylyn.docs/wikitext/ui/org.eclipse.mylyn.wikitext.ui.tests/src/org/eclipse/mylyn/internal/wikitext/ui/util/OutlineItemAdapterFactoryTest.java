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

package org.eclipse.mylyn.internal.wikitext.ui.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.wikitext.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.junit.Test;

/**
 * @author David Green
 */
@EclipseRuntimeRequired
@SuppressWarnings({ "nls", "restriction" })
public class OutlineItemAdapterFactoryTest {
	@Test
	public void testAdaptsToIWorkbenchAdapter() {
		Object adapter = Platform.getAdapterManager()
				.getAdapter(new OutlineItem(null, 0, "id", 0, 10, "ID"), IWorkbenchAdapter.class);
		assertNotNull(adapter);
		assertTrue(IWorkbenchAdapter.class.isAssignableFrom(adapter.getClass()));
	}
}
