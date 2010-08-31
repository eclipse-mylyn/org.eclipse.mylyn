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

package org.eclipse.mylyn.internal.builds.ui.console;

import org.eclipse.mylyn.internal.provisional.commons.ui.WorkbenchUtil;
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
		WorkbenchUtil.openUrl(url);
	}

}
