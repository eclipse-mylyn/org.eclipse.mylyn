/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.tasks.ui;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;

/**
 * Encapsulates link from text to a URL.
 * 
 * @author Steffen Pingel
 * @since 2.0
 */
public class WebHyperlink implements IHyperlink {

	private final IRegion region;
	
	private final String url;

	public WebHyperlink(IRegion region, String url) {
		this.region = region;
		this.url = url;
	}

	public IRegion getHyperlinkRegion() {
		return region;
	}

	public String getTypeLabel() {
		return null;
	}

	public String getHyperlinkText() {
		return "Open URL " + url;
	}

	public void open() {
		TasksUiUtil.openUrl(url, true);
	}

	public String getURLString() {
		return url;
	}

}
