/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskDataModel;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.ColumnSpan;
import org.eclipse.mylyn.tasks.ui.editors.LayoutHint.RowSpan;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Rob Elves
 */
public class BugzillaKeywordAttributeEditor extends AbstractAttributeEditor {

	public BugzillaKeywordAttributeEditor(TaskDataModel manager, TaskAttribute taskAttribute) {
		super(manager, taskAttribute);
		setLayoutHint(new LayoutHint(RowSpan.SINGLE, ColumnSpan.MULTIPLE));
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Composite keywordComposite = toolkit.createComposite(parent);
		GridLayout layout = new GridLayout(2, false);
		layout.marginWidth = 1;
		keywordComposite.setLayout(layout);

		final Text keywordsText = toolkit.createText(keywordComposite, getTaskAttribute().getValue());
		GridData keywordsData = new GridData(GridData.FILL_HORIZONTAL);
		keywordsText.setLayoutData(keywordsData);
		keywordsText.setEditable(false);

		Button changeKeywordsButton = toolkit.createButton(keywordComposite, "Edit...", SWT.FLAT);
		GridData keyWordsButtonData = new GridData();
		changeKeywordsButton.setLayoutData(keyWordsButtonData);
		changeKeywordsButton.addSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
			}

			public void widgetSelected(SelectionEvent e) {

				String keywords = getTaskAttribute().getValue();

				Shell shell = null;
				if (PlatformUI.getWorkbench().getActiveWorkbenchWindow() != null) {
					shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
				} else {
					shell = new Shell(PlatformUI.getWorkbench().getDisplay());
				}

				List<String> validKeywords = new ArrayList<String>();
				try {
					validKeywords = BugzillaCorePlugin.getRepositoryConfiguration(getModel().getTaskRepository(),
							false, new NullProgressMonitor()).getKeywords();
				} catch (Exception ex) {
					// ignore
				}

				KeywordsDialog keywordsDialog = new KeywordsDialog(shell, keywords, validKeywords);
				int responseCode = keywordsDialog.open();

				String newKeywords = keywordsDialog.getSelectedKeywordsString();
				if (responseCode == Window.OK && keywords != null) {
					keywordsText.setText(newKeywords);
					getAttributeMapper().setValue(getTaskAttribute(), newKeywords);
					attributeChanged();
				} else {
					return;
				}

			}

		});
		setControl(keywordComposite);
	}

}
