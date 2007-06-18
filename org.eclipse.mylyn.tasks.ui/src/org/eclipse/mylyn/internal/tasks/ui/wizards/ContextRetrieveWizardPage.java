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

package org.eclipse.mylyn.internal.tasks.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ContextRetrieveWizardPage extends WizardPage {

	private static final String DESCRIPTION = "Select a context to retrieve from table below.";

	private static final String COLUMN_COMMENT = "Description";

	private static final String COLUMN_AUTHOR = "Author";

	private static final String COLUMN_DATE = "Date";

	private TaskRepository repository;

	private AbstractTask task;

	private TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private RepositoryAttachment selectedContextAttachment = null;

	protected ContextRetrieveWizardPage(TaskRepository repository, AbstractTask task) {
		super(ContextAttachWizard.WIZARD_TITLE);
		this.repository = repository;
		this.task = task;
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		new Label(composite, SWT.NONE).setText(labelProvider.getText(task));
		// new Label(composite, SWT.NONE).setText("Repository: " +
		// repository.getUrl());
		// new Label(composite, SWT.NONE).setText("Select context below:");

		final Table contextTable = new Table(composite, SWT.FULL_SELECTION | SWT.BORDER);
		contextTable.setHeaderVisible(true);
		contextTable.setLinesVisible(true);

		contextTable.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				if (contextTable.getSelectionIndex() > -1) {
					selectedContextAttachment = (RepositoryAttachment) contextTable.getItem(
							contextTable.getSelectionIndex()).getData();
					getWizard().getContainer().updateButtons();
				}
			}
		});
		contextTable.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				selectedContextAttachment = (RepositoryAttachment) contextTable.getItem(
						contextTable.getSelectionIndex()).getData();
				getWizard().getContainer().updateButtons();
				getWizard().performFinish();
				// TODO: is there a better way of closing?
				getWizard().getContainer().getShell().close();
			}

			public void mouseDown(MouseEvent e) {
			}

			public void mouseUp(MouseEvent e) {
			}

		});

		AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
				repository.getKind());

		List<RepositoryAttachment> contextAttachments = new ArrayList<RepositoryAttachment>(
				connector.getContextAttachments(repository, task));

		Collections.sort(contextAttachments, new Comparator<RepositoryAttachment>() {

			public int compare(RepositoryAttachment attachment1, RepositoryAttachment attachment2) {
				RepositoryTaskData data = TasksUiPlugin.getDefault().getTaskDataManager().getNewTaskData(
						task.getRepositoryUrl(), task.getTaskId());

				AbstractAttributeFactory factory = null;

				Date created1 = null;
				Date created2 = null;
				if (data != null) {
					factory = data.getAttributeFactory();
				}
				if (factory != null) {
					created1 = factory.getDateForAttributeType(RepositoryTaskAttribute.ATTACHMENT_DATE,
							attachment1.getDateCreated());
					created2 = factory.getDateForAttributeType(RepositoryTaskAttribute.ATTACHMENT_DATE,
							attachment2.getDateCreated());
				}

				if (created1 != null && created2 != null) {
					return (-1) * created1.compareTo(created2);
				} else if (created1 == null && created2 != null) {
					return 1;
				} else if (created1 != null && created2 == null) {
					return -1;
				} else {
					return 0;
				}
			}

		});

		TableColumn[] columns = new TableColumn[3];
		columns[0] = new TableColumn(contextTable, SWT.LEFT);
		columns[0].setText(COLUMN_DATE);
		columns[1] = new TableColumn(contextTable, SWT.LEFT);
		columns[1].setText(COLUMN_AUTHOR);
		columns[2] = new TableColumn(contextTable, SWT.CENTER);
		columns[2].setText(COLUMN_COMMENT);

		for (RepositoryAttachment attachment : contextAttachments) {
			TableItem item = new TableItem(contextTable, SWT.NONE);
			item.setText(0, attachment.getDateCreated());
			item.setText(1, attachment.getCreator());
			item.setText(2, attachment.getDescription());
			item.setData(attachment);
		}

		for (int i = 0, n = columns.length; i < n; i++) {
			columns[i].pack();
		}

		contextTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(composite);
		if (contextAttachments.size() > 0) {
			contextTable.setSelection(0);
			selectedContextAttachment = contextAttachments.get(0);
			getWizard().getContainer().updateButtons();
		}
	}

	public RepositoryAttachment getSelectedContext() {
		return selectedContextAttachment;
	}

	@Override
	public boolean isPageComplete() {
		if (selectedContextAttachment == null) {
			return false;
		}
		return super.isPageComplete();
	}

}
