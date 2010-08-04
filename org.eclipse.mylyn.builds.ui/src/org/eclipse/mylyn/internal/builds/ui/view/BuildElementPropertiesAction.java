/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class BuildElementPropertiesAction extends BaseSelectionListenerAction {

	protected BuildElementPropertiesAction() {
		super("Properties");
		setToolTipText("Properties");
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return (selection.getFirstElement() instanceof IBuildElement);
	}

	@Override
	public void run() {
		Object firstElement = getStructuredSelection().getFirstElement();
		if (firstElement instanceof IBuildElement) {
			IBuildServer server = ((IBuildElement) firstElement).getServer();
			BuildsUiUtil.openPropertiesDialog(server);
		}
	}

}
