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

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;

/**
 * @author Steffen Pingel
 */
public class RefreshAction extends Action {

	public RefreshAction() {
		setImageDescriptor(CommonImages.REFRESH);
		setToolTipText("Refresh");
	}

	@Override
	public void run() {
		RefreshOperation operation = new RefreshOperation(BuildsUiInternal.getModel());
		operation.execute();
	}

}