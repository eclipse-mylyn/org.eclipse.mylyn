/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.tasklist.planner.ui;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * @author Mik Kersten
 * @author Ken Sueda (original prototype)
 */
public class MylarTaskPlannerWizardPage extends WizardPage {

	private static final int DEFAULT_DAYS = 7;
	private static final String TITLE = "Mylar Task Planner";
	private static final String DESCRIPTION = 
		"Summarizes task activity and assists planning future tasks.";
	
	private Text numDays;
	private int num = 0;
	
	public MylarTaskPlannerWizardPage() {
		super(TITLE);
		setTitle(TITLE);
		setDescription(DESCRIPTION);
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 2;		

		GridData gd = new GridData();
		gd.widthHint = 50;
		
		Label l = new Label(container, SWT.NULL);
		l.setText("Number of past days to report on: ");
		numDays = new Text(container, SWT.BORDER);
		numDays.setLayoutData(gd);
		numDays.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				try{
					num = Integer.parseInt(numDays.getText());
					setErrorMessage(null);
				} catch (Exception ex) {
					setErrorMessage("Must be integer");
					num = 0;
				}
			}			
		});		
		numDays.setText("" + DEFAULT_DAYS);
		num = DEFAULT_DAYS;
		setControl(container);
	}
	
	public int getNumDays() {
		return num;
	}
}
