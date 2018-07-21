/*******************************************************************************
 * Copyright (c) 2014 Ericsson
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *   Jacques Bouthillier - Initial Implementation of the of the Selection repository
 ******************************************************************************/
package org.eclipse.mylyn.gerrit.dashboard.ui.internal.utils;

import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.FormDialog;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ScrolledForm;

/**
 * This class implements the initial Task Selection dialog.
 * 
 * @author Jacques Bouthillier
 */
public class SelectionDialog extends FormDialog {

	private final List<TaskRepository> fListTaskRepository;

	private String fSelection = null;

	public SelectionDialog(Shell parent, List<TaskRepository> listTaskRepository) {
		super(parent);
		this.fListTaskRepository = listTaskRepository;
	}

	@Override
	protected void createFormContent(final IManagedForm mform) {
		mform.getForm().setText(Messages.SelectionDialog_selectTitle);
		mform.getForm().getShell().setText(Messages.SelectionDialog_question);

		final ScrolledForm sform = mform.getForm();
		sform.setExpandHorizontal(true);
		final Composite composite = sform.getBody();

		final GridLayout layout = new GridLayout(1, false);
		composite.setLayout(layout);

		GridDataFactory.fillDefaults().grab(false, false).applyTo(composite);
		int size = fListTaskRepository.size();
		for (int index = 0; index < size; index++) {
			final Button button = new Button(composite, SWT.RADIO);
			button.setText(fListTaskRepository.get(index).toString());
			button.setSelection(false);

			button.addListener(SWT.Selection, new Listener() {
				@Override
				public void handleEvent(Event event) {
					setSelection(button.getText());
				}
			});

			GridDataFactory.fillDefaults().span(1, 1).applyTo(button);
		}

		setHelpAvailable(false);
	}

	private void setSelection(String selection) {
		fSelection = selection;
	}

	public String getSelection() {
		return fSelection;
	}
}
