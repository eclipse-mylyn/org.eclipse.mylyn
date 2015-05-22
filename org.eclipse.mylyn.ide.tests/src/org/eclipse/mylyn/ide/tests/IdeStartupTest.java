/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.IContextListener;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.team.ui.ContextActiveChangeSetManager;

/**
 * @author Mik Kersten
 */
public class IdeStartupTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		ContextTestUtil.triggerContextUiLazyStart();
	}

	public void testChangeSetsStartup() {
		List<IContextListener> listeners = ContextCorePlugin.getContextManager().getListeners();
		boolean containsManager = false;
		for (IContextListener listener : listeners) {
			if (listener instanceof ContextActiveChangeSetManager) {
				containsManager = true;
			}
		}
		assertTrue(containsManager);
	}

}
