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

package org.eclipse.mylyn.internal.provisional.commons.ui.actions;

import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 * @deprecated use {@link org.eclipse.mylyn.commons.ui.workbench.PropertiesAction} instead
 */
@Deprecated
public class PropertiesAction extends BaseSelectionListenerAction {

	public PropertiesAction() {
		super(Messages.PropertiesAction_Properties);
	}

	@Override
	public void run() {
		WorkbenchUtil.openProperties(PlatformUI.getWorkbench());
	}

}
