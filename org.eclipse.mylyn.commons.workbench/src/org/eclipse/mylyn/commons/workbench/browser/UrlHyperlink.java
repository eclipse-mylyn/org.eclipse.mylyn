/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.workbench.browser;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.URLHyperlink;
import org.eclipse.osgi.util.NLS;

/**
 * A link to a url that opens in a rich editor, if available, or browser, otherwise.
 * 
 * @author Steffen Pingel
 * @see BrowserUtil#openUrl(String)
 */
public class UrlHyperlink extends URLHyperlink {

	private final String tooltip;

	/**
	 * Constructs a hyperlink with a custom tooltip.
	 * 
	 * @param region
	 *            the region to highlight
	 * @param url
	 *            the URL to open
	 * @param tooltip
	 *            a tooltip, maybe <code>null</code>
	 * @see URLHyperlink#URLHyperlink(IRegion, String)
	 */
	public UrlHyperlink(IRegion region, String url, String tooltip) {
		super(region, url);
		this.tooltip = tooltip;
	}

	/**
	 * Constructs a hyperlink
	 * 
	 * @param region
	 *            the region to highlight
	 * @param url
	 *            the URL to open
	 * @see URLHyperlink#URLHyperlink(IRegion, String)
	 */
	public UrlHyperlink(IRegion region, String url) {
		this(region, url, null);
	}

	@Override
	public void open() {
		BrowserUtil.openUrl(getURLString());
	}

	@Override
	public String getHyperlinkText() {
		if (tooltip != null) {
			return tooltip;
		}
		return NLS.bind("Open ''{0}''", getURLString()); //$NON-NLS-1$
	}

}
