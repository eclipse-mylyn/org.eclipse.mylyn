/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.tasks.ui.wizards;

import java.util.List;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Mik Kersten
 * @author Euegene Kuleshov
 */
public class NewRepositoryTaskPage extends SelectRepositoryPage {

	public NewRepositoryTaskPage(List<String> kinds) {
		super(kinds);
	}

	@Override
	protected IWizard createWizard(TaskRepository taskRepository) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskRepository.getKind());
		return connector.getNewTaskWizard(taskRepository, getSelection());
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());

//		Link link = new Link(container, SWT.NONE);
//		link.setText("<A>Close wizard and search for related reports before continuing</A>");
//		link.addSelectionListener(new SelectionListener() {
//			public void widgetDefaultSelected(SelectionEvent arg0) {
//				// ignore
//			}
//
//			public void widgetSelected(SelectionEvent arg0) {
//				getWizard().performCancel();
//				getWizard().getContainer().getShell().dispose();
//				NewSearchUI.openSearchDialog(NewSearchUI.getSearchResultView().getSite().getWorkbenchWindow(), "");
//
//			}
//		});

		createTableViewer(container).setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(container);
	}
}