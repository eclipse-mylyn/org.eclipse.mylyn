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
import org.eclipse.mylyn.builds.core.BuildStatus;
import org.eclipse.swt.widgets.Event;

/**
 * @author Steffen Pingel
 */
public class FilterByStatusAction extends Action {

	private final BuildStatus status;

	private final BuildsView view;

	public FilterByStatusAction(BuildsView view, BuildStatus status) {
		this.view = view;
		this.status = status;
		setText("Hide Disabled Plans");
		update();
	}

	@Override
	public void runWithEvent(Event event) {
		if (isChecked()) {
			view.getBuildStatusFilter().addFiltered(status);
		} else {
			view.getBuildStatusFilter().removeFiltered(status);
		}
		view.getViewer().refresh();
	}

	public void update() {
		setChecked(view.getBuildStatusFilter().getFiltered().contains(status));
	}

}