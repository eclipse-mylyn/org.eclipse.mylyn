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

package org.eclipse.mylyn.internal.bugzilla.ui.tasklist;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * 
 * @author Shawn Minto
 * @author Mik Kersten
 */
public class BugzillaCustomQueryDialog extends Dialog {
	private String url;

	private String name = ""; //$NON-NLS-1$

	private String startingUrl = null;

	private String maxHits;

	private Text maxHitsText;

	private Text nameText;

	private Text queryText;

	public BugzillaCustomQueryDialog(Shell parentShell, String queryString, String description, String maxHits) {
		super(parentShell);
		this.startingUrl = queryString;
		this.maxHits = maxHits;
		this.name = description;
	}

	public String getName() {
		return name;
	}

	public String getUrl() {
		return url;
	}

	public String getMaxHits() {
		return maxHits;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite custom = new Composite(parent, SWT.NONE);
		GridLayout gl = new GridLayout(2, false);
		custom.setLayout(gl);

		Label l = new Label(custom, SWT.NONE);
		l.setText(Messages.BugzillaCustomQueryDialog_Bugzilla_Query_Category_Name);

		nameText = new Text(custom, SWT.BORDER | SWT.SINGLE);
		if (name != null) {
			nameText.setText(name);
		}
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		nameText.setLayoutData(gd);

		l = new Label(custom, SWT.NONE);
		l.setText(Messages.BugzillaCustomQueryDialog_Max_Hits_Returned__1_means_all_);

		maxHitsText = new Text(custom, SWT.BORDER | SWT.SINGLE);
		if (maxHits != null) {
			maxHitsText.setText(maxHits);
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		maxHitsText.setLayoutData(gd);

		l = new Label(custom, SWT.NONE);
		l.setText(Messages.BugzillaCustomQueryDialog_Query_URL);

		queryText = new Text(custom, SWT.BORDER | SWT.SINGLE);
		if (startingUrl != null) {
			queryText.setText(startingUrl);
		}
		gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.widthHint = 300;
		queryText.setLayoutData(gd);

		Control c = super.createContents(parent);

		return c;
	}

	@Override
	protected void okPressed() {
		// TODO validate the values
		url = queryText.getText();
		name = nameText.getText();
		maxHits = maxHitsText.getText();
		super.okPressed();
	}

}
