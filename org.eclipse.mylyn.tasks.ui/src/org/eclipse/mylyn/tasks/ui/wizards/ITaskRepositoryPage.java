/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskRepositoryPage extends IWizardPage {

	/**
	 * Invoked to commit changes from the wizard page to the <code>repository</code> object.
	 * 
	 * @since 3.0
	 * @param repository
	 *            the task repository to persists settings to
	 */
	public abstract void applyTo(TaskRepository repository);

	/**
	 * Returns the URL currently entered on the page. This is used by the framework to detect if the URL of the
	 * repository has changed which requires a migration job to run.
	 * 
	 * @since 3.0
	 * @return the repository URL that is currently entered
	 */
	public abstract String getRepositoryUrl();

	/**
	 * Invoked when the wizard that contains page finishes. This method should commit all entered data to the
	 * <code>repository</code> object.
	 * 
	 * @since 3.6
	 * @see #applyTo(TaskRepository)
	 */
	public void performFinish(TaskRepository repository);

	/**
	 * Invoked when the wizard that contains page finishes. This method should validate all entered data to the
	 * <code>repository</code> object.
	 * 
	 * @since 3.7
	 * @return true to indicate the validation request was successful, and false to indicate that the validation request
	 *         was not successful
	 */
	public abstract boolean preFinish(TaskRepository repository);

}