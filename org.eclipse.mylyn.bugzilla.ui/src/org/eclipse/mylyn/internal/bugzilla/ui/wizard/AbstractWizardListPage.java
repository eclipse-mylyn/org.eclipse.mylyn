/*******************************************************************************
 * Copyright (c) 2003 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylar.internal.bugzilla.ui.wizard;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.IWorkbench;

/**
 * Class that contains shared functions for the first page of the wizards that
 * submit bug reports. This page provides the user with a list of items to
 * choose from.
 * 
 * TODO: get rid of this unused abstraction?
 * 
 * @author Mik Kersten (hardening of prototype)
 */
public abstract class AbstractWizardListPage extends WizardPage implements Listener {

	/** The instance of the workbench */
	protected IWorkbench workbench;

	/** The list box for the list of items to choose from */
	protected List listBox;

	/** Status variable for the possible errors on this page */
	protected IStatus listStatus;

	public AbstractWizardListPage(String pageName, String title, String description, IWorkbench workbench) {
		super(pageName);
		setTitle(title);
		setDescription(description);
		this.workbench = workbench;

		// set the status for the page
		listStatus = new Status(IStatus.OK, "not_used", 0, "", null);
	}

	public abstract void createAdditionalControls(Composite parent);

	public void createControl(Composite parent) {
		// create the composite to hold the widgets
		GridData gd;
		Composite composite = new Composite(parent, SWT.NULL);

		// create the desired layout for this wizard page
		GridLayout gl = new GridLayout();
		int ncol = 1;
		gl.numColumns = ncol;
		composite.setLayout(gl);

		// create the bug report label
//		Label label = new Label(composite, SWT.NONE);
//		label.setText(getTableName());
//		FontData fontData = label.getFont().getFontData()[0];
//		fontData.setStyle(SWT.BOLD | fontData.getStyle());
//		int height = (int) Math.abs(fontData.getHeight() * 1.25);
//		fontData.setHeight(height);
//		Font font = new Font(null, fontData);
//		label.setFont(font);

		// create the list of bug reports
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.heightHint = 200;
		listBox = new List(composite, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY | SWT.V_SCROLL);
		listBox.setLayoutData(gd);

		createLine(composite, ncol);

		// Each wizard has different types of items to add to the list
		populateList(true);

		createAdditionalControls(composite);

		// set the composite as the control for this page
		setControl(composite);
		addListeners();
	}

	/**
	 * Populate the list of items
	 */
	abstract protected void populateList(boolean init);

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.Event)
	 */
	abstract public void handleEvent(Event event);

	/**
	 * A helper function for "handleEvent"
	 * 
	 * @param event
	 *            the event which occurred
	 * @param errorMessage
	 *            the error message unique to the wizard calling this function
	 */
	protected void handleEventHelper(Event event, String errorMessage) {
		// Initialize a variable with the no error status
		Status status = new Status(IStatus.OK, "not_used", 0, "", null);

		// If the event is triggered by the list of items, respond with the
		// corresponding status
		if (event.widget == listBox) {
			if (listBox.getSelectionIndex() == -1)
				status = new Status(IStatus.ERROR, "not_used", 0, errorMessage, null);
			listStatus = status;
		}

		// Show the most serious error
		applyToStatusLine(listStatus);
		getWizard().getContainer().updateButtons();
	}

	/**
	 * Applies the status to the status line of a dialog page.
	 * 
	 * @param status
	 *            The status to apply to the status line
	 */
	protected void applyToStatusLine(IStatus status) {
		String message = status.getMessage();
		if (message.length() == 0)
			message = null;
		switch (status.getSeverity()) {
		case IStatus.OK:
			setErrorMessage(null);
			setMessage(message);
			break;
		case IStatus.WARNING:
			setErrorMessage(null);
			setMessage(message, WizardPage.WARNING);
			break;
		case IStatus.INFO:
			setErrorMessage(null);
			setMessage(message, WizardPage.INFORMATION);
			break;
		default:
			setErrorMessage(null);
			setMessage(message, WizardPage.ERROR);
			break;
		}
	}

	/**
	 * Create a separator line in the dialog
	 * 
	 * @param parent
	 *            The composite to create the line on
	 * @param ncol
	 *            The number of columns to span
	 */
	protected void createLine(Composite parent, int ncol) {
		Label line = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL | SWT.BOLD);
		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
		gridData.horizontalSpan = ncol;
		line.setLayoutData(gridData);
	}

	@Override
	public boolean canFlipToNextPage() {
		if (getErrorMessage() != null)
			return false;
		if (listBox.getSelectionIndex() != -1)
			return true;
		return false;
	}

	/**
	 * Add any listeners that we need
	 */
	protected void addListeners() {
		listBox.addListener(SWT.Selection, this);
	}
}
