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

package org.eclipse.mylyn.internal.hudson.ui;

import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizard;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;

/**
 * @author Steffen Pingel
 */
public class NewHudsonServerWizard extends BuildServerWizard {

	public NewHudsonServerWizard(IBuildServer model) {
		super(model);
	}

	public NewHudsonServerWizard() {
		super(BuildsUi.createServer(HudsonCorePlugin.CONNECTOR_KIND));
	}

}
