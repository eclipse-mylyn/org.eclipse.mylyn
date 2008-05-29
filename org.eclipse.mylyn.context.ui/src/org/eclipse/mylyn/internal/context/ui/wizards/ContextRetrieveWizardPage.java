/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import java.text.DateFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
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
@SuppressWarnings( { "deprecation", "restriction" })
public class ContextRetrieveWizardPage extends WizardPage {

	private static final String WIZARD_TITLE = "Retrieve context";

	private static final String DESCRIPTION = "Select a context to retrieve from table below.";

	private static final String COLUMN_COMMENT = "Description";

	private static final String COLUMN_AUTHOR = "Author";

	private static final String COLUMN_DATE = "Date";

	private final TaskRepository repository;

	private final ITask task;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private ITaskAttachment selectedContextAttachment;

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
					selectedContextAttachment = (ITaskAttachment) contextTable.getItem(contextTable.getSelectionIndex())
							.getData();
					getWizard().getContainer().updateButtons();
				}
			}
		});
		contextTable.addMouseListener(new MouseListener() {

			public void mouseDoubleClick(MouseEvent e) {
				selectedContextAttachment = (ITaskAttachment) contextTable.getItem(contextTable.getSelectionIndex())
						.getData();
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

		List<ITaskAttachment> contextAttachments = AttachmentUtil.getContextAttachments(repository, task);

		Collections.sort(contextAttachments, new Comparator<ITaskAttachment>() {

			public int compare(ITaskAttachment attachment1, ITaskAttachment attachment2) {

				Date created1 = null;
				Date created2 = null;
				created1 = attachment1.getCreationDate();
				created2 = attachment2.getCreationDate();
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

		for (ITaskAttachment attachment : contextAttachments) {
			TableItem item = new TableItem(contextTable, SWT.NONE);
			item.setText(0, DateFormat.getInstance().format(attachment.getCreationDate()));
			item.setText(1, attachment.getAuthor().getName());
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

	public ITaskAttachment getSelectedContext() {
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
