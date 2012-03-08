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

package org.eclipse.mylyn.internal.builds.ui.util;

import java.util.List;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.commons.workbench.browser.AbstractUrlHandler;
import org.eclipse.mylyn.internal.builds.ui.commands.OpenHandler;
import org.eclipse.ui.IWorkbenchPage;

/**
 * @author Steffen Pingel
 */
public class BuildsUrlHandler extends AbstractUrlHandler {

	public BuildsUrlHandler() {
		// ignore
	}

	@Override
	public EditorHandle openUrl(IWorkbenchPage page, String location, int customFlags) {
		List<IBuildServer> servers = BuildsUi.getModel().getServers();
		for (IBuildServer server : servers) {
			BuildConnector connector = BuildsUi.getConnector(server);
			if (connector != null) {
				IBuildElement element = connector.getBuildElementFromUrl(server, location);
				if (element instanceof IBuild) {
					return OpenHandler.fetchAndOpen(page, (IBuild) element);
				}
			}
		}
		return null;
	}

}
