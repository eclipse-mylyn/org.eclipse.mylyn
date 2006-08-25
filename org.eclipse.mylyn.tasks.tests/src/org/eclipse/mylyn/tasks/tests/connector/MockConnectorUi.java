/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.tasks.tests.connector;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.internal.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.mylar.tasks.core.AbstractRepositoryQuery;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractConnectorUi;

/**
 * @author Mik Kersten
 */
public class MockConnectorUi extends AbstractConnectorUi {

	@Override
	public IWizard getNewQueryWizard(TaskRepository repository, IStructuredSelection selection) {
		// ignore
		return null;
	}

	@Override
	public IWizard getNewTaskWizard(TaskRepository taskRepository, IStructuredSelection selection) {
		// ignore
		return null;
	}

	@Override
	public String getRepositoryType() {
		return "mock";
	}

	@Override
	public AbstractRepositorySettingsPage getSettingsPage() {
		// ignore
		return null;
	}

	@Override
	public boolean hasRichEditor() {
		// ignore
		return false;
	}

	@Override
	public void openEditQueryDialog(AbstractRepositoryQuery query) {
		// ignore
	}

}
