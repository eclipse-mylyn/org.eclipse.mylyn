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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.ide.tests;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.eclipse.mylyn.context.core.IContextListener;
import org.eclipse.mylyn.context.sdk.util.ContextTestUtil;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.team.ui.ContextActiveChangeSetManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Mik Kersten
 */
public class IdeStartupTest {

	@BeforeEach
	void setUp() throws Exception {
		ContextTestUtil.triggerContextUiLazyStart();
	}

	@Test
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
