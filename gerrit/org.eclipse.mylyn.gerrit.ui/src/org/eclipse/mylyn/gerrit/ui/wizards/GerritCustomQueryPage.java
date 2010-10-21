/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *********************************************************************/
package org.eclipse.mylyn.gerrit.ui.wizards;

import org.eclipse.mylyn.gerrit.core.GerritQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * @author Mikael Kober, Sony Ericsson
 * @author Tomas Westling, Sony Ericsson -
 *         thomas.westling@sonyericsson.com
 */
public class GerritCustomQueryPage extends AbstractRepositoryQueryPage {

	private static final String TITLE_QUERY_TITLE = "Query Title :";

	private final IRepositoryQuery query;

	private Button radio1 = null;
	private Button radio2 = null;
	private Text titleText = null;

	/**
	 * Constructor.
	 * @param repository
	 * @param pageName
	 * @param query
	 */
	public GerritCustomQueryPage(TaskRepository repository, String pageName,
			IRepositoryQuery query) {
		super(pageName, repository, query);
		this.query = query;
		setDescription("Enter title and type of the query.");
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.dialogs.IDialogPage#createControl(org.eclipse.swt.widgets.Composite)
	 */
	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		GridLayout layout = new GridLayout(3, false);
		control.setLayout(layout);

		Label titleLabel = new Label(control, SWT.NONE);
		titleLabel.setText(TITLE_QUERY_TITLE);

		titleText = new Text(control, SWT.BORDER);
		GridData gd2 = new GridData(GridData.FILL_HORIZONTAL
				| GridData.GRAB_HORIZONTAL);
		gd2.horizontalSpan = 2;
		titleText.setLayoutData(gd2);
		titleText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// ignore
			}

			public void keyReleased(KeyEvent e) {
				getContainer().updateButtons();
			}
		});

		Label typeLabel = new Label(control, SWT.NONE);
		typeLabel.setText("Query type :");
		// radio button to select query type
		radio1 = new Button(control, SWT.RADIO);
		radio1.setText("My open changes");

		radio2 = new Button(control, SWT.RADIO);
		radio2.setText("All open changes");

		if (query != null) {
			titleText.setText(query.getSummary());
			if(GerritQuery.MY_OPEN_CHANGES.equals(query.getAttribute(GerritQuery.TYPE))){
			    radio1.setSelection(true);
			}
			else{
			    radio2.setSelection(true);
			}
		}

		setControl(control);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.wizard.WizardPage#canFlipToNextPage()
	 */
	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#isPageComplete()
	 */
	@Override
	public boolean isPageComplete() {
		return (titleText != null && titleText.getText().length() > 0);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#applyTo(org.eclipse.mylyn.tasks.core.IRepositoryQuery)
	 */
	@Override
	public void applyTo(IRepositoryQuery query) {
		// TODO: set URL ????
		// query.setUrl(getQueryUrl());
		query.setSummary(getTitleText());
		query.setAttribute(GerritQuery.TYPE,
				radio1.getSelection() ? GerritQuery.MY_OPEN_CHANGES
						: GerritQuery.ALL_OPEN_CHANGES);
	}

	private String getTitleText() {
		return (titleText != null) ? titleText.getText() : "<search>";
	}

	/* (non-Javadoc)
	 * @see org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage#getQueryTitle()
	 */
	@Override
	public String getQueryTitle() {
		return "Gerrit Query";
	}
	
	
}
