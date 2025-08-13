/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench;

import org.eclipse.mylyn.internal.commons.workbench.Messages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public class PropertiesAction extends BaseSelectionListenerAction {

	public PropertiesAction() {
		super(Messages.PropertiesAction_Properties);
	}

	@Override
	public void run() {
		WorkbenchUtil.openProperties(PlatformUI.getWorkbench());
	}

}
