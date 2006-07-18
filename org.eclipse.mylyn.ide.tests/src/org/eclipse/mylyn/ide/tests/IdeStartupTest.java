/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.ide.tests;

import java.util.List;

import junit.framework.TestCase;

import org.eclipse.mylar.context.core.ContextCorePlugin;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.internal.team.ContextChangeSetManager;

/**
 * @author Mik Kersten
 */
public class IdeStartupTest extends TestCase {

	public void testChangeSetsStartup() {
		List<IMylarContextListener> listeners = ContextCorePlugin.getContextManager().getListeners();
		boolean containsManager = false;
		for (IMylarContextListener listener : listeners) {
			if (listener instanceof ContextChangeSetManager) {
				containsManager = true;
			}
		}
		assertTrue(containsManager);
	}
	
}
