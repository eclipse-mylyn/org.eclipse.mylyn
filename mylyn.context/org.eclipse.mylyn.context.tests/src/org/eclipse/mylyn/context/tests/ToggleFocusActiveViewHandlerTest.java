/*******************************************************************************
 * Copyright (c) 2014 Liferay, Inc.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
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

		IHandlerService hs = PlatformUI.getWorkbench().getService(IHandlerService.class);
		assertNotNull(hs);

		hs.executeCommand("org.eclipse.mylyn.context.ui.commands.toggle.focus.active.view", null);

		assertTrue(action.isChecked());

		hs.executeCommand("org.eclipse.mylyn.context.ui.commands.toggle.focus.active.view", null);
		assertTrue(!action.isChecked());
	}

}
