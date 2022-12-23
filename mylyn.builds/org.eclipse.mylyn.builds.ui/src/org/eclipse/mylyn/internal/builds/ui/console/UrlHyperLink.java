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

package org.eclipse.mylyn.internal.builds.ui.console;

import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.ui.console.IHyperlink;

/**
 * @author Steffen Pingel
 */
public class UrlHyperLink implements IHyperlink {

	private final String url;

	public UrlHyperLink(String url) {
		this.url = url;
	}

	public void linkEntered() {
		// ignore
	}

	public void linkExited() {
		// ignore
	}

	public void linkActivated() {
		BrowserUtil.openUrl(url);
	}

}
