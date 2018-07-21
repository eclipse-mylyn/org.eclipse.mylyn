/*******************************************************************************
 * Copyright (c) 2009, 2013 Tasktop Technologies and others.
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
import java.util.Map;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.wizard.ProgressMonitorPart;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.internal.tasks.ui.util.AttachmentUtil;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskAttachment;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;

import com.ibm.icu.text.DateFormat;

/**
 * @author Steffen Pingel
 */
public class RetrieveLatestContextDialog extends MessageDialog {

	public static boolean openQuestion(Shell shell, ITask task) {
		TaskRepository repository = TasksUi.getRepositoryManager().getRepository(task.getConnectorKind(),
				task.getRepositoryUrl());
		List<ITaskAttachment> contextAttachments = AttachmentUtil.getContextAttachments(repository, task);
		Collections.sort(contextAttachments, new TaskAttachmentComparator());
		if (contextAttachments.size() > 0) {
			ITaskAttachment attachment = contextAttachments.get(0);
			String author = null;
			if (attachment.getAuthor() != null) {
				author = (attachment.getAuthor().getName()) != null
						? attachment.getAuthor().getName()
						: attachment.getAuthor().getPersonId();
			}
			if (author == null) {
				author = Messages.RetrieveLatestContextDialog_Unknown;
			}
			Date date = attachment.getCreationDate();
			String dateString = null;
			if (date != null) {
				dateString = DateFormat.getDateInstance(DateFormat.LONG).format(date);
			}
			if (dateString == null) {
				dateString = Messages.RetrieveLatestContextDialog_Unknown;
			}
			String message = NLS.bind(Messages.RetrieveLatestContextDialog_No_local_context_exists, author, dateString);
			int kind = QUESTION;
			int style = SWT.NONE;

			RetrieveLatestContextDialog dialog = new RetrieveLatestContextDialog(shell,
					Messages.RetrieveLatestContextDialog_Dialog_Title, null, message, kind, new String[] {
							IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0, task, attachment);
			style &= SWT.SHEET;
			dialog.setShellStyle(dialog.getShellStyle() | style);
			return dialog.open() == 0;
		}
		return false;
	}

	private final ITaskAttachment attachment;

	private Link link;

	private ProgressContainer progressContainer;

	private ProgressMonitorPart progressMonitorPart;

	private final ITask task;

	public RetrieveLatestContextDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
			String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex, ITask task,
			ITaskAttachment attachment) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
		this.task = task;
		this.attachment = attachment;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		if (progressContainer.isActive()) {
			return;
		}
		if (buttonId == 0) {
			if (!AttachmentUtil.downloadContext(task, attachment, progressContainer)) {
				// failed
				return;
			}
		}
		super.buttonPressed(buttonId);
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(GridLayoutFactory.fillDefaults().numColumns(3).create());
		GridDataFactory.fillDefaults().grab(true, false).span(2, 1).applyTo(composite);
		Control control = createLink(composite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(control);
		super.createButtonBar(composite);
		return composite;
	}

	@Override
	protected Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		progressContainer.setCancelButton(getButton(1));
		getButton(0).setFocus();
		return control;
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		progressMonitorPart = new ProgressMonitorPart(parent, null);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(progressMonitorPart);
		progressContainer = new ProgressContainer(getShell(), progressMonitorPart) {
			@Override
			protected void restoreUiState(Map<Object, Object> state) {
				link.setEnabled(true);
				getButton(0).setEnabled(true);
				getButton(1).setEnabled(true);
			};

			@Override
			protected void saveUiState(Map<Object, Object> savedState) {
				link.setEnabled(false);
				getButton(0).setEnabled(false);
			};
		};
		return progressMonitorPart;
	}

	protected Control createLink(Composite parent) {
		link = new Link(parent, SWT.NONE);
		link.setText(Messages.RetrieveLatestContextDialog_Show_All_Contexts_Label);
		link.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				close();

				ContextRetrieveWizard wizard = new ContextRetrieveWizard(task);
				WizardDialog dialog = new WizardDialog(WorkbenchUtil.getShell(), wizard);
				dialog.create();
				dialog.setBlockOnOpen(true);
				setReturnCode(dialog.open());
			}
		});
		return link;
	}

}
