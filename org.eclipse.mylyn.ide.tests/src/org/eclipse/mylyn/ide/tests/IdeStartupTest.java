/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.internal.team.ui.ContextActiveChangeSetManager;

/**
 * @author Mik Kersten
 */
public class IdeStartupTest extends TestCase {

	@Override
	protected void setUp() throws Exception {
		TestUtil.triggerContextUiLazyStart();
	}

	public void testChangeSetsStartup() {
		List<IInteractionContextListener> listeners = ContextCorePlugin.getContextManager().getListeners();
		boolean containsManager = false;
		for (IInteractionContextListener listener : listeners) {
			if (listener instanceof ContextActiveChangeSetManager) {
				containsManager = true;
			}
		}
		assertTrue(containsManager);
	}

}
