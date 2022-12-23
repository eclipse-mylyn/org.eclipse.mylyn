/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.actions;

import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.internal.team.ui.LinkedTaskInfo;
import org.eclipse.ui.IWorkbenchPage;

public class TaskFinderUrlHandler extends AbstractUrlHandler {

	@Override
	public EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags) {
		TaskFinder finder = new TaskFinder(new LinkedTaskInfo(location));
		return finder.openTaskByKey(page);
	}

	@Override
	public int getPriority() {
		return 50;
	}
}
