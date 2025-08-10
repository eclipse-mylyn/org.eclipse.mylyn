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
 *******************************************************************************/

package org.eclipse.mylyn.commons.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.internal.commons.ui.Messages;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
 * @since 3.7
 */
public class ExpandAllAction extends Action {

	private final AbstractTreeViewer viewer;

	public ExpandAllAction(AbstractTreeViewer viewer) {
		Assert.isNotNull(viewer);
		this.viewer = viewer;
		setText(Messages.ExpandAllAction_Label);
		setToolTipText(Messages.ExpandAllAction_ToolTip);
		setImageDescriptor(CommonImages.EXPAND_ALL);
	}

	@Override
	public void run() {
		viewer.expandAll();
	}

}
