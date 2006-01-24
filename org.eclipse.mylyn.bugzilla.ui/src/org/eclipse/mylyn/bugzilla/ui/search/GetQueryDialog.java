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

package org.eclipse.mylar.bugzilla.ui.search;

import java.util.ArrayList;
import java.util.Iterator;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;

/**
 * Dialog to display, manage and run stored queries.
 */
public class GetQueryDialog extends Dialog {

	/** The Ok button. */
	private Button okButton;

	/** The title of the dialog. */
	private String title;

	private SavedQueryFile input;

	public GetQueryDialog(Shell parentShell, String dialogTitle, SavedQueryFile in) {
		super(parentShell);
		this.title = dialogTitle;
		input = in;
		setShellStyle(SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL);
	}

	@Override
	protected void configureShell(Shell shell) {
		super.configureShell(shell);
		shell.setText(title);
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// create OK and Details buttons
		okButton = createButton(parent, IDialogConstants.OK_ID, "Run", true);
		okButton.setEnabled(false);
		createButton(parent, IDialogConstants.CANCEL_ID, "Close", false);
	}

	/**
	 * Creates the list widget to display stored queries.
	 */
	@Override
	final protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);

		createMainDialogArea(composite);

		return composite;
	}

	protected void createMainDialogArea(Composite parent) {
		Label label = new Label(parent, SWT.LEFT);
		label.setText("Select a saved query:");
		rememberPattern = new List(parent, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
		ArrayList<String> names = input.getNames();
		int pos = 0;

		for (Iterator<String> it = names.iterator(); it.hasNext();) {
			rememberPattern.add(it.next(), pos);
			pos++;
		}

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		gd.heightHint = 60;

		rememberPattern.setLayoutData(gd);
		rememberPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				selIndex = rememberPattern.getSelectionIndex();
				okButton.setEnabled(selIndex >= 0);
			}
		});
		rememberPattern.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				okPressed();
			}
		});

		// Configure the context menu
		MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
		menuMgr.add(new RunQueryAction("&Run query"));
		menuMgr.add(new Separator());
		menuMgr.add(new RemoveAction("Remo&ve"));
		menuMgr.add(new RemoveAllAction("Remove &all"));
		Menu menu = menuMgr.createContextMenu(rememberPattern);
		rememberPattern.setMenu(menu);

	}

	final protected void setPageComplete(boolean complete) {
		if (okButton != null) {
			okButton.setEnabled(complete);
		}
	}

	private String queryNameText;

	private List rememberPattern;

	public String getText() {
		return queryNameText;
	}

	/**
	 * Deletes a selected named query.
	 */
	private void remove() {
		int index = rememberPattern.getSelectionIndex();
		if (index != -1)
			BugzillaSearchPage.getInput().remove(new int[] { index });
		rememberPattern.remove(index);
		rememberPattern.setSelection(-1);
		selIndex = -1;
		okButton.setEnabled(false);
	}

	private void removeAll() {
		BugzillaSearchPage.getInput().removeAll();
		rememberPattern.removeAll();
		rememberPattern.setSelection(-1);
		selIndex = -1;
		okButton.setEnabled(false);
	}

	/** Index of the selected query, or -1 if none. */
	int selIndex = -1;

	/**
	 * Returns index of the selected query or -1 if none are selected.
	 */
	public int getSelected() {
		return selIndex;
	}

	private class RunQueryAction extends Action {
		RunQueryAction(String text) {
			super(text);
		}

		@Override
		public void run() {
			GetQueryDialog.this.okPressed();
		}
	}

	private class RemoveAction extends Action {
		RemoveAction(String text) {
			super(text);
		}

		@Override
		public void run() {
			GetQueryDialog.this.remove();
		}
	}

	private class RemoveAllAction extends Action {
		RemoveAllAction(String text) {
			super(text);
		}

		@Override
		public void run() {
			GetQueryDialog.this.removeAll();
		}
	}
}
