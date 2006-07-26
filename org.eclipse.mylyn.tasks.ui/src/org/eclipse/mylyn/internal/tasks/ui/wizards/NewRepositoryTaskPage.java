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
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;

/**
 * @author Mik Kersten
 * @author Euegene Kuleshov
 */
public class NewRepositoryTaskPage extends SelectRepositoryPage {

	private Button searchForDuplicatesButton;

	private DuplicateDetectionData duplicateData;

	private boolean initUseStackTrace = false;

	private IWizard newWizard;

	private boolean dupPagesAdded = false;

	public NewRepositoryTaskPage(List<String> kinds) {
		super(kinds);
	}

	@Override
	protected IWizard createWizard(TaskRepository taskRepository) {
		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				taskRepository.getKind());
		newWizard = connector.getNewTaskWizard(taskRepository, getSelection());
		if (newWizard instanceof AbstractDuplicateDetectingReportWizard && getUseStackTrace()) {
			AbstractDuplicateDetectingReportWizard dupWizard = (AbstractDuplicateDetectingReportWizard) newWizard;
			// queue the duplicate detection pages to be added to the
			// wizard when it gets created
			dupWizard.queuePage(new FindRelatedReportsPage(duplicateData));
			dupWizard.queuePage(new DisplayRelatedReportsPage());
			dupPagesAdded = true;
		}
		return newWizard;
	}

	public IWizardPage getNextPage() {
		// ensure the dup pages are added (in the case of going "back" in the
		// wizard)
		if (!dupPagesAdded && newWizard instanceof AbstractDuplicateDetectingReportWizard && getUseStackTrace()) {
			AbstractDuplicateDetectingReportWizard dupWizard = (AbstractDuplicateDetectingReportWizard) newWizard;
			dupWizard.addPage(new FindRelatedReportsPage(duplicateData));
			dupWizard.addPage(new DisplayRelatedReportsPage());
			dupPagesAdded = true;
		}
		return super.getNextPage();
	}

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout());
		// super.createControl(container);
		createTableViewer(container).setLayoutData(new GridData(GridData.FILL_BOTH));

		searchForDuplicatesButton = new Button(container, SWT.CHECK);
		searchForDuplicatesButton.setText("Search for related stack traces before creating");
		searchForDuplicatesButton.setSelection(initUseStackTrace);

		Link link = new Link(container, SWT.NONE);
		link.setText("<A>Close wizard and search for related reports before continuing</A>");
		final IWizardPage thisPage = this;
		link.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
				// ignore
			}

			public void widgetSelected(SelectionEvent arg0) {
				thisPage.getWizard().performCancel();
			}
		});

		setControl(container);
	}

	public DuplicateDetectionData getDuplicateData() {
		return duplicateData;
	}

	public void setDuplicateData(DuplicateDetectionData duplicateData) {
		this.duplicateData = duplicateData;
	}

	public void setUseStackTrace(boolean use) {
		if (searchForDuplicatesButton != null) {
			searchForDuplicatesButton.setSelection(use);
		}
		initUseStackTrace = use;
	}

	public boolean getUseStackTrace() {
		if (searchForDuplicatesButton != null) {
			return searchForDuplicatesButton.getSelection();
		}
		return initUseStackTrace;
	}
}