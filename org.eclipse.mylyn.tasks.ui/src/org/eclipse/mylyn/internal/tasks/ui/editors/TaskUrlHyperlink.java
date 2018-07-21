/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.editors;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.mylyn.commons.workbench.browser.UrlHyperlink;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.osgi.util.NLS;

/**
 * @author Rob Elves
 * @deprecated use {@link UrlHyperlink} instead
 */
@Deprecated
public class TaskUrlHyperlink extends URLHyperlink {

	private final String hyperlinkText;

	public TaskUrlHyperlink(IRegion region, String urlString, String hyperlinkText) {
		super(region, urlString);
		this.hyperlinkText = hyperlinkText;
	}

	public TaskUrlHyperlink(IRegion region, String urlString) {
		this(region, urlString, null);
	}

	@Override
	public void open() {
		TasksUiUtil.openTask(getURLString());
	}

	@Override
	public String getHyperlinkText() {
		if (hyperlinkText != null) {
			return hyperlinkText;
		}
		return NLS.bind(Messages.TaskUrlHyperlink_Open_URL_in_Task_Editor, getURLString());
	}

}
