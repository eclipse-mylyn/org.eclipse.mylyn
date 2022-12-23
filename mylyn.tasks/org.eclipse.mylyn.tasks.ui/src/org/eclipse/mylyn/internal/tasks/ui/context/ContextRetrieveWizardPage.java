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

package org.eclipse.mylyn.internal.tasks.ui.context;

import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.osgi.util.NLS;
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

import com.ibm.icu.text.DateFormat;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public class ContextRetrieveWizardPage extends WizardPage {

	private final TaskRepository repository;

	private final ITask task;

	private final TaskElementLabelProvider labelProvider = new TaskElementLabelProvider(false);

	private ITaskAttachment selectedContextAttachment;

	protected ContextRetrieveWizardPage(TaskRepository repository, ITask task) {
		super(Messages.ContextRetrieveWizardPage_Select_context);
		this.repository = repository;
		this.task = task;
		setDescription(Messages.ContextRetrieveWizardPage_SELECT_A_CONTEXT_TO_RETTRIEVE_FROM_TABLE_BELOW);
		setTitle(Messages.ContextRetrieveWizardPage_Select_context);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout(1, false));

		Text summary = new Text(composite, SWT.NONE);
		summary.setText(NLS.bind(Messages.ContextRetrieveWizardPage_Task, labelProvider.getText(task)));
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

		Collections.sort(contextAttachments, new TaskAttachmentComparator());

		TableColumn[] columns = new TableColumn[3];
		columns[0] = new TableColumn(contextTable, SWT.LEFT);
		columns[0].setText(Messages.ContextRetrieveWizardPage_Date);
		columns[1] = new TableColumn(contextTable, SWT.LEFT);
		columns[1].setText(Messages.ContextRetrieveWizardPage_Author);
		columns[2] = new TableColumn(contextTable, SWT.CENTER);
		columns[2].setText(Messages.ContextRetrieveWizardPage_Description);

		for (ITaskAttachment attachment : contextAttachments) {
			TableItem item = new TableItem(contextTable, SWT.NONE);
			Date creationDate = attachment.getCreationDate();
			if (creationDate != null) {
				item.setText(0, DateFormat.getInstance().format(creationDate));
			}
			IRepositoryPerson author = attachment.getAuthor();
			if (author != null) {
				item.setText(1, author.toString());
			}
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
		Dialog.applyDialogFont(composite);
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
