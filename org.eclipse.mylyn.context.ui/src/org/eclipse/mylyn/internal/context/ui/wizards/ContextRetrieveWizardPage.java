/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractAttributeFactory;
import org.eclipse.mylyn.internal.tasks.core.deprecated.AbstractLegacyRepositoryConnector;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryAttachment;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskAttribute;
import org.eclipse.mylyn.internal.tasks.core.deprecated.RepositoryTaskData;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskElementLabelProvider;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ContextRetrieveWizardPage extends WizardPage {

	private static final String WIZARD_TITLE = "Retrieve context";

	private static final String DESCRIPTION = "Select a context to retrieve from table below.";

	private static final String COLUMN_COMMENT = "Description";

	private static final String COLUMN_AUTHOR = "Author";

	private static final String COLUMN_DATE = "Date";

	private final TaskRepository repository;

	private final ITask task;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private RepositoryAttachment selectedContextAttachment;

	protected ContextRetrieveWizardPage(TaskRepository repository, ITask task) {
		super(WIZARD_TITLE);
		this.repository = repository;
		this.task = task;
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Text summary = new Text(composite, SWT.NONE);
		summary.setText("Task: " + labelProvider.getText(task));
		summary.setEditable(false);
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

		AbstractLegacyRepositoryConnector connector = (AbstractLegacyRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(repository.getConnectorKind());

		List<RepositoryAttachment> contextAttachments = new ArrayList<RepositoryAttachment>();
		if (connector.getAttachmentHandler() != null) {
			contextAttachments = new ArrayList<RepositoryAttachment>(AttachmentUtil.getLegacyContextAttachments(repository,
					task));
		}

		Collections.sort(contextAttachments, new Comparator<RepositoryAttachment>() {

			public int compare(RepositoryAttachment attachment1, RepositoryAttachment attachment2) {
				RepositoryTaskData data = TasksUiPlugin.getTaskDataStorageManager().getNewTaskData(
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

		for (TableColumn column : columns) {
			column.pack();
		}

		contextTable.setLayoutData(new GridData(GridData.FILL_BOTH));
		setControl(composite);
		if (contextAttachments.size() > 0) {
			contextTable.setSelection(0);
			selectedContextAttachment = contextAttachments.get(0);
			getWizard().getContainer().updateButtons();
		}
		contextTable.setFocus();
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
