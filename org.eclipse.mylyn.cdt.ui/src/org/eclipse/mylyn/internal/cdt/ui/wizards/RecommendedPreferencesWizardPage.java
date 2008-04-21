/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui.wizards;

import org.eclipse.cdt.mylyn.internal.ui.CDTUIBridgePlugin;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.eclipse.ui.forms.widgets.Hyperlink;

/**
 * @author Shawn Minto
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class RecommendedPreferencesWizardPage extends WizardPage {

	private Button contentAssistButton;

	private Button turnOnAutoFoldingButton;

	private boolean autoFolding = true;

	private boolean openTaskList = true;

	protected RecommendedPreferencesWizardPage(String pageName) {
		super(pageName);
		setTitle(pageName);
		setDescription(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.description")); // $NON-NLS-1$
	}

	public void createControl(Composite parent) {

		Composite containerComposite = new Composite(parent, SWT.NULL);
		containerComposite.setLayout(new GridLayout());

		Composite buttonComposite = new Composite(containerComposite, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		buttonComposite.setLayout(layout);

		contentAssistButton = new Button(buttonComposite, SWT.CHECK);
		GridData gd = new GridData();
		contentAssistButton.setLayoutData(gd);
		contentAssistButton.setSelection(true);

		Label label = new Label(buttonComposite, SWT.NONE);
		label.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.contentAssist")); // $NON-NLS-1$
		label = new Label(buttonComposite, SWT.NONE);
		label = new Label(buttonComposite, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
		label.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.contentAssistWarning")); // $NON-NLS-1$

		gd = new GridData();
		label.setLayoutData(gd);

		turnOnAutoFoldingButton = new Button(buttonComposite, SWT.CHECK);
		gd = new GridData();
		turnOnAutoFoldingButton.setLayoutData(gd);
		turnOnAutoFoldingButton.setSelection(true);
		turnOnAutoFoldingButton.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				autoFolding = turnOnAutoFoldingButton.getSelection();
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// don't care about this event
			}
		});

		label = new Label(buttonComposite, SWT.NONE);
		label.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.autoFolding.text")); // $NON-NLS-1$
		gd = new GridData();
		label.setLayoutData(gd);
		label = new Label(buttonComposite, SWT.NONE);
		label = new Label(buttonComposite, SWT.NONE);
		label.setFont(JFaceResources.getFontRegistry().getItalic(JFaceResources.DEFAULT_FONT));
		label.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.autoFolding.label")); // $NON-NLS-1$

		Label spacer = new Label(buttonComposite, SWT.NONE);
		spacer.setText(" ");
		spacer = new Label(buttonComposite, SWT.NONE);
		spacer.setText(" ");

		Hyperlink hyperlink = new Hyperlink(containerComposite, SWT.NULL);
		hyperlink.setUnderlined(true);
		hyperlink.setForeground(TaskListColorsAndFonts.COLOR_HYPERLINK_WIDGET);
		hyperlink.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.hyperLink.text")); // $NON-NLS-1$

		label = new Label(containerComposite, SWT.NONE);
		label.setText(CDTUIBridgePlugin.getResourceString("MylynCDT.wizard.iconsHelp")); // $NON-NLS-1$
		gd = new GridData();
		label.setLayoutData(gd);

		hyperlink.addHyperlinkListener(new IHyperlinkListener() {

			public void linkActivated(HyperlinkEvent e) {
				TasksUiUtil.openUrl("http://eclipse.org/mylyn/start.php", false); // $NON-NLS-1$
			}

			public void linkEntered(HyperlinkEvent e) {
				// ignore
			}

			public void linkExited(HyperlinkEvent e) {
				// ignore
			}
		});

		setControl(containerComposite);
	}

	public boolean isAutoFolding() {
		return autoFolding;
	}

	public boolean isMylynContentAssistDefault() {
		return contentAssistButton.getSelection();
	}

	public boolean isOpenTaskList() {
		return openTaskList;
	}
}
