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
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.internal.builds.ui.view;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class OpenWithBrowserAction extends BaseSelectionListenerAction {

	public OpenWithBrowserAction() {
		super(Messages.OpenWithBrowserAction_openWithBrowser);
		setToolTipText(Messages.OpenWithBrowserAction_openWithBrowserToolTip);
		setImageDescriptor(CommonImages.BROWSER_OPEN_TASK);
	}

	@Override
	public void run() {
		List<URI> uris = getUris(getStructuredSelection());
		for (URI uri : uris) {
			BrowserUtil.openUrl(uri.toString());
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		return getUris(selection).size() > 0;
	}

	public List<URI> getUris(IStructuredSelection selection) {
		List<URI> uris = new ArrayList<>(selection.size());
		for (Object object : selection.toList()) {
			if (object instanceof IBuildElement element) {
				if (element.getUrl() != null) {
					try {
						uris.add(new URI(element.getUrl()));
					} catch (URISyntaxException e) {
						// ignore
					}
				}
			}
		}
		return uris;
	}

}
