/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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

	public abstract void applyTo(TaskRepository repository);

	public abstract String getRepositoryUrl();

}