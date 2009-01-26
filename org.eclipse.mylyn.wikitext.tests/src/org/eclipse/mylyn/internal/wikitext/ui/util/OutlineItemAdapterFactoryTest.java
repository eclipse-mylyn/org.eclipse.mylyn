/*******************************************************************************
 * Copyright (c) 2007, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.util;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.tests.EclipseRuntimeRequired;
import org.eclipse.ui.model.IWorkbenchAdapter;

/**
 * 
 * @author David Green
 */
@EclipseRuntimeRequired
public class OutlineItemAdapterFactoryTest extends TestCase {

	public void testAdaptsToIWorkbenchAdapter() {
		Object adapter = Platform.getAdapterManager().getAdapter(new OutlineItem(null, 0, "id", 0, 10, "ID"),
				IWorkbenchAdapter.class);
		assertNotNull(adapter);
		assertTrue(IWorkbenchAdapter.class.isAssignableFrom(adapter.getClass()));
	}
}
