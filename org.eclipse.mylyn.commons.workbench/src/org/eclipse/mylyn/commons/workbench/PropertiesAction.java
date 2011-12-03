/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
