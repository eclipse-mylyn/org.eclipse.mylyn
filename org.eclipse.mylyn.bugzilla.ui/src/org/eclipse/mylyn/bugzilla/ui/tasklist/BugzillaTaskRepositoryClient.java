/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.bugzilla.ui.tasklist;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.tasklist.repositories.ITaskRepositoryClient;
import org.eclipse.mylar.tasklist.ui.wizards.RepositorySettingsPage;

/**
 * @author Mik Kersten
 */
public class BugzillaTaskRepositoryClient implements ITaskRepositoryClient {
	
	public String getLabel() {
		return "Bugzilla (supports uncustomized 2.16-2.20)";
	}
	
	public String toString() {
		return getLabel();
	}

	public IWizardPage getSettingsPage() {
		return new RepositorySettingsPage();
	}

	public String getKind() {
		return BugzillaPlugin.REPOSITORY_KIND;
	}

}
