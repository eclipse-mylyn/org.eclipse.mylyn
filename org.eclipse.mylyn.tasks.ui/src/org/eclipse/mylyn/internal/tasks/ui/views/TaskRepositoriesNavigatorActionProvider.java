/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.mylyn.commons.workbench.PropertiesAction;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.ICommonActionConstants;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;

/**
 * @author Steffen Pingel
 */
public class TaskRepositoriesNavigatorActionProvider extends CommonActionProvider {

	private PropertiesAction propertiesAction;

	public TaskRepositoriesNavigatorActionProvider() {
	}

	@Override
	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);

		this.propertiesAction = new PropertiesAction();
	}

	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		actionBars.setGlobalActionHandler(ICommonActionConstants.OPEN, propertiesAction);
	}

}
