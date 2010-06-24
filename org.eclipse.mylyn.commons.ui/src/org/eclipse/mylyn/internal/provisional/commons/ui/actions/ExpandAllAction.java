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

package org.eclipse.mylyn.internal.provisional.commons.ui.actions;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;

/**
 * @author Mik Kersten
 * @author Steffen Pingel
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
