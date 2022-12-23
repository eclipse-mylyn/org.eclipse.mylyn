/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.team.ui.history;

import org.eclipse.team.ui.history.HistoryPageSource;
import org.eclipse.ui.part.Page;

/**
 * @author Steffen Pingel
 */
public class TaskHistoryPageSource extends HistoryPageSource {

	private static TaskHistoryPageSource instance = new TaskHistoryPageSource();

	public static TaskHistoryPageSource getInstance() {
		return instance;
	}

	public boolean canShowHistoryFor(Object object) {
		return TaskHistoryPage.canShowHistoryFor(object);
	}

	public Page createPage(Object object) {
		TaskHistoryPage page = new TaskHistoryPage();
		page.setInput(object);
		return page;
	}

}
