/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import java.util.List;

import org.eclipse.mylyn.context.core.IContextListener;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.team.ui.ContextActiveChangeSetManager;

import junit.framework.TestCase;

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
