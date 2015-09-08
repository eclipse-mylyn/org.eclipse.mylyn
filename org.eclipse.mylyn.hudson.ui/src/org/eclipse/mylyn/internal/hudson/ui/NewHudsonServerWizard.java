/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Minor adjustments and string externalisation
 *******************************************************************************/

package org.eclipse.mylyn.internal.hudson.ui;

import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizard;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizardPage;
import org.eclipse.mylyn.internal.hudson.core.HudsonCorePlugin;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class NewHudsonServerWizard extends BuildServerWizard {

	/**
	 * Creates a new instance using the supplied build server model. It is assumed that this describes a Hudson server.
	 */
	public NewHudsonServerWizard(IBuildServer model) {
		super(model);
	}

	/**
	 * Creates a new instance using the Hudson server connector.
	 */
	public NewHudsonServerWizard() {
		super(BuildsUi.createServer(HudsonCorePlugin.CONNECTOR_KIND));
	}

	@Override
	protected void initPage(BuildServerWizardPage page) {
		page.setTitle(Messages.NewServerWizard_Title);
		page.setMessage(Messages.NewServerWizard_Message);
	}

}
