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

package org.eclipse.mylar.internal.tasklist.ui.wizards;

import java.text.DateFormat;
import java.util.Set;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IRemoteContextDelegate;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ContextRetrieveWizardPage extends WizardPage {

	private static final String DESCRIPTION = "Loads context from repository task into the workspace";
		
	private static final String COLUMN_COMMENT = "Comment";

	private static final String COLUMN_AUTHOR = "Author";

	private static final String COLUMN_DATE = "Date";

	private TaskRepository repository;

	private AbstractRepositoryTask task;

	private Form form;

	private IRemoteContextDelegate selectedContext = null;

	protected ContextRetrieveWizardPage(TaskRepository repository, AbstractRepositoryTask task) {
		super(ContextAttachWizard.WIZARD_TITLE);
		this.repository = repository;
		this.task = task;
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getBody().setLayout(new GridLayout(1, false));
		
		Color formBackground = form.getBackground();
		form.setBackground(parent.getBackground());
		toolkit.setBackground(parent.getBackground());
		toolkit.createLabel(form.getBody(), "Task: " + task.getDescription());
		toolkit.createLabel(form.getBody(), "Repository: " + repository.getUrl());
		toolkit.createLabel(form.getBody(), "Select context below:");
		final Table contextTable = toolkit.createTable(form.getBody(), SWT.FULL_SELECTION | SWT.SINGLE | SWT.V_SCROLL);
		contextTable.setBackground(formBackground);
		contextTable.setHeaderVisible(true);
		contextTable.setLinesVisible(true);
		contextTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				if (contextTable.getSelectionIndex() > -1) {
					selectedContext = (IRemoteContextDelegate) contextTable.getItem(contextTable.getSelectionIndex())
							.getData();
					getWizard().getContainer().updateButtons();
				}
			}
		});

		AbstractRepositoryConnector connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());

		Set<IRemoteContextDelegate> contextDelegates = connector.getAvailableContexts(repository, task);
		TableColumn[] columns = new TableColumn[3];

		columns[0] = new TableColumn(contextTable, SWT.LEFT);
		columns[0].setText(COLUMN_DATE);

		columns[1] = new TableColumn(contextTable, SWT.LEFT);
		columns[1].setText(COLUMN_AUTHOR);

		columns[2] = new TableColumn(contextTable, SWT.CENTER);
		columns[2].setText(COLUMN_COMMENT);

		for (IRemoteContextDelegate delegate : contextDelegates) {
			TableItem item = new TableItem(contextTable, SWT.NONE);
			item.setText(0, DateFormat.getDateInstance(DateFormat.MEDIUM).format(delegate.getDate()));
			item.setText(1, delegate.getAuthor());
			item.setText(2, delegate.getComment());
			item.setData(delegate);
		}

	    for (int i = 0, n = columns.length; i < n; i++) {
	      columns[i].pack();
	    }
		
		GridData tableLayoutData = new GridData(GridData.FILL_BOTH);
		contextTable.setLayoutData(tableLayoutData);
		toolkit.paintBordersFor(form.getBody());
		setControl(form.getBody());
	}

	public IRemoteContextDelegate getSelectedContext() {
		return selectedContext;
	}

	@Override
	public boolean isPageComplete() {
		if(selectedContext == null) return false;
		return super.isPageComplete();
	}

}
