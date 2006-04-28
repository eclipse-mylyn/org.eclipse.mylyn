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

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Rob Elves
 */
public class ContextAttachWizardPage extends WizardPage {

	private static final String DESCRIPTION = "Attaches local context to repository task";
		
	private TaskRepository repository;

	private AbstractRepositoryTask task;

	private Text commentText;

	private Form form;

	protected ContextAttachWizardPage(TaskRepository repository, AbstractRepositoryTask task) {
		super(ContextAttachWizard.WIZARD_TITLE);
		this.repository = repository;
		this.task = task; 
		setDescription(DESCRIPTION);
	}

	public void createControl(Composite parent) {
		FormToolkit toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createForm(parent);
		form.getBody().setLayout(new GridLayout(1, false));
		form.setBackground(parent.getBackground());
		toolkit.setBackground(parent.getBackground());
		toolkit.createLabel(form.getBody(), "Task: " + task.getDescription());
		toolkit.createLabel(form.getBody(), "Repository: " + repository.getUrl());
		toolkit.createLabel(form.getBody(), "Comment: ");
		commentText = toolkit.createText(form.getBody(), "", SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	
		commentText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}

			public void keyReleased(KeyEvent e) {
				getWizard().getContainer().updateButtons();
			}
		});

		commentText.setLayoutData(new GridData(GridData.FILL_BOTH));

		toolkit.paintBordersFor(form.getBody());

		setControl(form.getBody());
	}

	public String getComment() {
		return commentText.getText();
	}

	@Override
	public boolean isPageComplete() {
//		if (commentText.getText().equals(""))
//			return false;
		return super.isPageComplete();
	}

}
