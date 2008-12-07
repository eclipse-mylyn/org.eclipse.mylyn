/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Rob Elves
 */
public class ContextAttachWizardPage extends WizardPage {

	private final TaskRepository repository;

	private final ITask task;

	private Text commentText;

	protected ContextAttachWizardPage(TaskRepository repository, ITask task) {
		super(Messages.ContextAttachWizardPage_Enter_comment);
		this.repository = repository;
		this.task = task;
		setTitle(Messages.ContextAttachWizardPage_Enter_comment);
		setDescription(Messages.ContextAttachWizardPage_Attaches_local_context_to_repository_task);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		Text summary = new Text(composite, SWT.NONE);
		summary.setText(NLS.bind(Messages.ContextAttachWizardPage_Task, task.getSummary()));
		summary.setEditable(false);
		Text repositoryText = new Text(composite, SWT.NONE);
		repositoryText.setText(Messages.ContextAttachWizardPage_Repository_ + repository.getRepositoryUrl());
		repositoryText.setEditable(false);

		new Label(composite, SWT.NONE).setText(Messages.ContextAttachWizardPage_Comment_);
		commentText = new Text(composite, SWT.V_SCROLL | SWT.MULTI | SWT.BORDER | SWT.WRAP);
		commentText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		commentText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}

			public void keyReleased(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(composite);
		commentText.setFocus();
		Dialog.applyDialogFont(composite);
	}

	public String getComment() {
		return commentText.getText();
	}

}
