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
package org.eclipse.mylyn.internal.builds.ui.view;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

/**
 * @author Steffen Pingel
 */
public class OpenInBrowserAction extends BaseSelectionListenerAction {

	public OpenInBrowserAction() {
		super("Open with Browser");
		setToolTipText("Open with Browser");
		setImageDescriptor(CommonImages.BROWSER_OPEN_TASK);
	}

	@Override
	public void run() {
		List<URI> uris = getUris(getStructuredSelection());
		for (URI uri : uris) {
			TasksUiUtil.openUrl(uri.toString());
		}
	}

	@Override
	protected boolean updateSelection(IStructuredSelection selection) {
		setEnabled(getUris(selection).size() > 0);
		return super.updateSelection(selection);
	}

	public List<URI> getUris(IStructuredSelection selection) {
		List<URI> uris = new ArrayList<URI>(selection.size());
		for (Object object : selection.toList()) {
			if (object instanceof IBuildElement) {
				IBuildElement element = (IBuildElement) object;
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
