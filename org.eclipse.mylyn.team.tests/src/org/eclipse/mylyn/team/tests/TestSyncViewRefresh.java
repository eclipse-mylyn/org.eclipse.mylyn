/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.team.tests;

import junit.framework.TestCase;

import org.eclipse.mylyn.internal.commons.ui.WorkbenchUtil;
import org.eclipse.team.internal.ui.synchronize.SynchronizeView;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;

/**
 * @author Mik Kersten
 */
public class TestSyncViewRefresh extends TestCase {

	public void testInitialPage() throws PartInitException {
		String ID = "org.eclipse.team.sync.views.SynchronizeView";
		IViewPart view = WorkbenchUtil.openInActivePerspective(ID);
		assertTrue(view instanceof SynchronizeView);
		SynchronizeView syncView = (SynchronizeView) view;
		IPage page = syncView.getCurrentPage();
		assertTrue(page instanceof MessagePage);

		// TODO: get the AbstractSynchronizePage and call getViewer() for contents
	}
}
