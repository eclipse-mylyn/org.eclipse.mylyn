/*******************************************************************************
 * Copyright (c) 2004, 2006 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     Tasktop Technologies - improvements for Mylyn
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.repositories.ui.wizards;

import org.eclipse.mylyn.internal.commons.repositories.ui.RepositoriesUiPlugin;
import org.eclipse.ui.internal.wizards.AbstractExtensionWizardRegistry;
import org.eclipse.ui.internal.wizards.NewWizardRegistry;

/**
 * Based on {@link NewWizardRegistry}.
 * 
 * @author Steffen Pingel
 */
@SuppressWarnings("restriction")
public final class NewRepositoryWizardRegistry extends AbstractExtensionWizardRegistry {

	private static NewRepositoryWizardRegistry singleton;

	/**
	 * Return the singleton instance of this class.
	 * 
	 * @return the singleton instance of this class
	 */
	public static synchronized NewRepositoryWizardRegistry getInstance() {
		if (singleton == null) {
			singleton = new NewRepositoryWizardRegistry();
		}
		return singleton;
	}

	/**
	 * Private constructor.
	 */
	private NewRepositoryWizardRegistry() {
	}

	@Override
	protected String getExtensionPoint() {
		return "newWizards"; //$NON-NLS-1$
	}

	@Override
	protected String getPlugin() {
		return RepositoriesUiPlugin.ID_PLUGIN;
	}

}
