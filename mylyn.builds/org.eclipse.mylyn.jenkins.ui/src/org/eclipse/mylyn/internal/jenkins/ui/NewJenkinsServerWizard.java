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
 *     Itema AS - Minor adjustments and string externalisation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.ui;

import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizard;
import org.eclipse.mylyn.builds.ui.spi.BuildServerWizardPage;
import org.eclipse.mylyn.internal.jenkins.core.JenkinsCorePlugin;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class NewJenkinsServerWizard extends BuildServerWizard {

	/**
	 * Creates a new instance using the supplied build server model. It is assumed that this describes a Jenkins server.
	 */
	public NewJenkinsServerWizard(IBuildServer model) {
		super(model);
	}

	/**
	 * Creates a new instance using the Jenkins server connector.
	 */
	public NewJenkinsServerWizard() {
		super(BuildsUi.createServer(JenkinsCorePlugin.CONNECTOR_KIND));
	}

	@Override
	protected void initPage(BuildServerWizardPage page) {
		page.setTitle(Messages.NewServerWizard_Title);
		page.setMessage(Messages.NewServerWizard_Message);
	}

}
