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
package org.eclipse.mylar.monitor.ui.wizards;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;


/**
 * A wizard for uploading the Mylar statistics to a website
 * 
 * @author Ken Sueda
 */
public class QuestionnaireWizardPage extends WizardPage{

	public static final String FEEDBACK_REQUEST = "Fill out the following form to help us improve Mylar based on your input.\n";
//					+ "based on your usage and feedback.";

	private static final String NOT_GOOD = "Not Good";
	private static final String GOOD = "Good";
	private static final String NOT_WELL = "Not well";
	private static final String OK = "OK";
	private static final String WELL = "Well";
	private static final String UNSURE = "Unsure";
	private static final String SELECT = "";
	private static final String EXPLORED = "Explored";
	private static final String DOCUMENTED = "Documented";
	private static final String TESTED = "Tested";
	private static final String ADDED_NEW_FEATURES = "Added new features";
	private static final String MADE_ENHANCEMENTS = "Made enhancements";
	private static final String FIXED_BUGS = "Fixed bugs";

	private Combo taskCombo;
	private Combo mentalModelCombo;
	private Combo integrateCombo;
	private Combo doiCombo;
	
	private String taskPerformedFeedback = null;
	
	private String mentalModelFeedback = null;
	
	private String doiModelFeedback = null;
	
	private String integrateFeedback = null;
	
	private String positiveFeedback = null;
	
	private String negativeFeedback = null;
	
	private Text positiveText = null;
	
	private Text negativeText = null;
	
	public QuestionnaireWizardPage() {
		super("Questionnaire Wizard");
		setTitle("Questionnaire");
		setDescription(FEEDBACK_REQUEST);
	}
	
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 1;
		layout.verticalSpacing = 9;
		
		// drop down box, choose from: 
		// fixed bugs, made enhancements, added new features, tested, documented, explored
		Label label = new Label(container, SWT.NULL);
		label.setText("What task did you primarily work on this week?");
		
		taskCombo = new Combo(container, SWT.READ_ONLY);
		taskCombo.setText(SELECT);
		taskCombo.add(FIXED_BUGS);
		taskCombo.add(MADE_ENHANCEMENTS);
		taskCombo.add(ADDED_NEW_FEATURES);
		taskCombo.add(TESTED);
		taskCombo.add(DOCUMENTED);
		taskCombo.add(EXPLORED);
		taskCombo.add(UNSURE);
			
		taskCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				taskPerformedFeedback = taskCombo.getText();
				getContainer().updateButtons();
			}
		});

		// drop down box, choose from:
		// well, ok, not well
		label = new Label(container, SWT.NULL);
		label.setText("How accurately did the Mylar model match your mental model of interesting elements?");
		mentalModelCombo = new Combo(container, SWT.READ_ONLY);
		mentalModelCombo.setText(SELECT);
		mentalModelCombo.add(WELL);
		mentalModelCombo.add(OK);
		mentalModelCombo.add(NOT_WELL);
		mentalModelCombo.add(UNSURE);
				
		mentalModelCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				mentalModelFeedback = mentalModelCombo.getText();
				getContainer().updateButtons();
			}
		});

		// drop down box
		// chose from: good, ok, not good
		label = new Label(container, SWT.NULL);
		label.setText("How effective are the Mylar views at exposing what you are working on?");
		doiCombo = new Combo(container, SWT.READ_ONLY);
		doiCombo.setText(SELECT);
		doiCombo.add(GOOD);
		doiCombo.add(OK);
		doiCombo.add(NOT_GOOD);
		doiCombo.add(UNSURE);

		doiCombo.addSelectionListener(new SelectionAdapter() {
			
			@Override
			public void widgetSelected(SelectionEvent e) {
				doiModelFeedback = doiCombo.getText();
				getContainer().updateButtons();
			}
		});
		
		// drop down box
		// chose from: well, ok, not well
		label = new Label(container, SWT.NULL);
		label.setText("How well did Mylar integrate with your workspace and environment?");
		integrateCombo = new Combo(container, SWT.READ_ONLY);
		integrateCombo.setText(SELECT);
		integrateCombo.add(WELL);
		integrateCombo.add(OK);
		integrateCombo.add(NOT_WELL);
		integrateCombo.add(UNSURE);
				
		integrateCombo.addSelectionListener(new SelectionAdapter() {
		
			@Override
			public void widgetSelected(SelectionEvent e) {
				integrateFeedback = integrateCombo.getText();	
				getContainer().updateButtons();
			}
		});
		
		positiveText = createTextWithLabel(container, "What worked well?");
		negativeText = createTextWithLabel(container, "What does not work well?");		
		setControl(container);
	}

	private Text createTextWithLabel(Composite parent, String label) {
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.FILL;
		gd.verticalAlignment = GridData.FILL;
        gd.verticalSpan = 10;
		Label searchLabel = new Label(parent, SWT.NONE);
		searchLabel.setText(label);
		Text t = new Text(parent, SWT.BORDER | SWT.MULTI | SWT.WRAP | SWT.V_SCROLL);
		t.setLayoutData(gd); 
		return t;
	}

	private void finishedSelected() {
		if (this.taskPerformedFeedback == null) {
			this.taskPerformedFeedback = FIXED_BUGS;
		}
		if (this.mentalModelFeedback == null) {
			this.mentalModelFeedback = WELL;
		}
		if (this.doiModelFeedback == null) {
			this.doiModelFeedback = GOOD;
		}
		if (this.integrateFeedback == null) {
			this.integrateFeedback = WELL;
		}
		positiveFeedback = positiveText.getText();
		if (this.positiveFeedback == null) {
			positiveFeedback = "none";
		}
		negativeFeedback = negativeText.getText();
		if (this.negativeFeedback == null) {
			negativeFeedback = "none";
		}
	}
	
	public File createFeedbackFile() {
		finishedSelected();
		IPath rootPath = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		String path = rootPath.toString() + File.separator
				+ "questionnaire.txt";
		File questionnaireFile = new File(path);
		
		if (questionnaireFile.exists()) {
			questionnaireFile.delete();
		}
		
		OutputStream outputStream;
		try {
			outputStream = new FileOutputStream(questionnaireFile);

			String buffer = "Tasks: " + getTaskPerformedFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "MentalModel: " + getMentalModelFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "DOI: " + getDoiModelFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "Integrate: " + getIntegrateFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "Positive: " + getPositiveFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			buffer = "Negative: " + getNegativeFeedback() + "\r\n";
			outputStream.write(buffer.getBytes());
			outputStream.close();
			return questionnaireFile;
		} catch (FileNotFoundException e) {
			MylarPlugin.log(e, "failed to submit");
		} catch (IOException e) {
			MylarPlugin.log(e, "failed to submit");
		}
		return null;
	}
	
	/***************************************************************************
	 * getters Methods
	 **************************************************************************/
	public String getDoiModelFeedback() {
		return doiModelFeedback;
	}

	public String getIntegrateFeedback() {
		return integrateFeedback;
	}

	public String getMentalModelFeedback() {
		return mentalModelFeedback;
	}

	public String getNegativeFeedback() {
		return negativeFeedback;
	}

	public String getPositiveFeedback() {
		return positiveFeedback;
	}

	public String getTaskPerformedFeedback() {
		return taskPerformedFeedback;
	}

	
	@Override
	public boolean isPageComplete() {
	if (taskCombo.getText().equals(SELECT)
			|| doiCombo.getText().equals(SELECT)
			|| mentalModelCombo.getText().equals(SELECT)
			|| integrateCombo.getText().equals(SELECT)) {
//				setErrorMessage("Must complete answers");
			return false;
		} else {
//				setErrorMessage(null);
			return true;
		}
	}
}
