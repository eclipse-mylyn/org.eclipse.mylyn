/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylyn.tasks.core.TaskRepository;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskRepositoryPage extends IWizardPage {

	public abstract TaskRepository createTaskRepository();

	public abstract void applyTo(TaskRepository repository);

	public abstract String getRepositoryUrl();

}