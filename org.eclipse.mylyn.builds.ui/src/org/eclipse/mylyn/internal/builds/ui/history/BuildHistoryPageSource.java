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

package org.eclipse.mylyn.internal.builds.ui.history;

import org.eclipse.team.ui.history.HistoryPageSource;
import org.eclipse.ui.part.Page;

/**
 * @author Steffen Pingel
 */
public class BuildHistoryPageSource extends HistoryPageSource {

	private static BuildHistoryPageSource instance = new BuildHistoryPageSource();

	public static BuildHistoryPageSource getInstance() {
		return instance;
	}

	public boolean canShowHistoryFor(Object object) {
		return BuildHistoryPage.canShowHistoryFor(object);
	}

	public Page createPage(Object object) {
		BuildHistoryPage page = new BuildHistoryPage();
		page.setInput(object);
		return page;
	}

}
