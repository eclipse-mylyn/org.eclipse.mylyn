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

import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.emf.common.notify.impl.NotificationImpl;
import org.eclipse.jface.action.Action;
import org.eclipse.mylyn.internal.builds.core.operations.RefreshOperation;
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
		operation.schedule();
		// FIXME use model events
		operation.addJobChangeListener(new JobChangeAdapter() {
			@Override
			public void done(IJobChangeEvent event) {
				BuildsUiInternal.getModel().eNotify(new NotificationImpl(0, false, true));
			}
		});
	}

}