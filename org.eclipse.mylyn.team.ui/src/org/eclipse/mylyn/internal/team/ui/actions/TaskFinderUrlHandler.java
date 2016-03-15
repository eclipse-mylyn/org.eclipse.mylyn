/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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
