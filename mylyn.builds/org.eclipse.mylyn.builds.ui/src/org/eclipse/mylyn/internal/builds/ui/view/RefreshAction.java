/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.view;

import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshOperation;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;

/**
 * @author Steffen Pingel
 */
public class RefreshAction extends Action {

	public RefreshAction() {
		setImageDescriptor(CommonImages.REFRESH);
		setToolTipText(Messages.RefreshAction_refreshTooltip);
	}

	@Override
	public void run() {
		RefreshOperation operation = BuildsUiInternal.getFactory().getRefreshOperation();
		operation.execute();
	}

}