/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui.wizards;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask.SynchronizationState;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.TaskRepository;
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

	private static final String WIZARD_TITLE = "Attach context";

	private static final String DESCRIPTION = "Attaches local context to repository task";

	private final TaskRepository repository;

	private final ITask task;

	private Text commentText;

	private boolean complete = true;

	protected ContextAttachWizardPage(TaskRepository repository, ITask task) {
		super(WIZARD_TITLE);
		this.repository = repository;
		this.task = task;
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new GridLayout());

		Text summary = new Text(composite, SWT.NONE);
		summary.setText("Task: " + task.getSummary());
		summary.setEditable(false);
		Text repositoryText = new Text(composite, SWT.NONE);
		repositoryText.setText("Repository: " + repository.getRepositoryUrl());
		repositoryText.setEditable(false);

		new Label(composite, SWT.NONE).setText("Comment: ");
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

		if (task.getSynchronizationState() != SynchronizationState.SYNCHRONIZED) {
			setErrorMessage("Task must be synchronized before attaching context");
			complete = false;
			getWizard().getContainer().updateButtons();
		}
		commentText.setFocus();
	}

	public String getComment() {
		return commentText.getText();
	}

	@Override
	public boolean isPageComplete() {
		return complete;
//		if (commentText.getText().equals(""))
//			return false;
//		return super.isPageComplete();
	}

}
