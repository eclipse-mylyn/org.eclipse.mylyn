/*******************************************************************************
 * Copyright (c) 2014 Liferay, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Gregory Amerson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.tests;

import org.eclipse.mylyn.context.sdk.util.AbstractResourceContextTest;
import org.eclipse.mylyn.context.ui.AbstractFocusViewAction;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;

/**
 * @author Gregory Amerson
 */
public class ToggleFocusActiveViewHandlerTest extends AbstractResourceContextTest {

	public void testToggleFocusActiveViewHandler() throws Exception {
		AbstractFocusViewAction action = AbstractFocusViewAction.getActionForPart(navigator);
		assertNotNull(action);

		assertTrue(!action.isChecked());

		IHandlerService hs = (IHandlerService) PlatformUI.getWorkbench().getService(IHandlerService.class);
		assertNotNull(hs);

		hs.executeCommand("org.eclipse.mylyn.context.ui.commands.toggle.focus.active.view", null);

		assertTrue(action.isChecked());

		hs.executeCommand("org.eclipse.mylyn.context.ui.commands.toggle.focus.active.view", null);
		assertTrue(!action.isChecked());
	}

}
