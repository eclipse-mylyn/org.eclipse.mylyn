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
package org.eclipse.mylyn.internal.bugzilla.ui.editor;

import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * An editor used to view a locally created bug that does not yet exist on a repository.
 * 
 * @author Rob Elves
 */
public class NewBugzillaTaskEditor extends AbstractNewRepositoryTaskEditor {

	private static final int WRAP_LENGTH = 90;

	public NewBugzillaTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		super.init(site, input);

		setExpandAttributeSection(true);
	}

	@Override
	protected void saveTaskOffline(IProgressMonitor progressMonitor) {
		String text = descriptionTextViewer.getTextWidget().getText();
		if (repository.getVersion().startsWith("2.18")) {
			text = formatTextToLineWrap(text, true);
			descriptionTextViewer.getTextWidget().setText(text);
		}
		super.saveTaskOffline(progressMonitor);
	}

	@Override
	protected void createPeopleLayout(Composite composite) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleSection);
		Composite peopleComposite = toolkit.createComposite(peopleSection);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		peopleComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleComposite);

		Label label = toolkit.createLabel(peopleComposite, "Assign to:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Composite textFieldComposite = toolkit.createComposite(peopleComposite);
		GridLayout textLayout = new GridLayout();
		textFieldComposite.setLayout(textLayout);

		RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);

		Text textField = createTextField(textFieldComposite, attribute, SWT.FLAT);
		toolkit.paintBordersFor(textFieldComposite);
		GridDataFactory.fillDefaults().hint(150, SWT.DEFAULT).applyTo(textField);
		peopleSection.setClient(peopleComposite);

		ContentAssistCommandAdapter adapter = applyContentAssist(textField, createContentProposalProvider(attribute));

		ILabelProvider propsalLabelProvider = createProposalLabelProvider(attribute);
		if (propsalLabelProvider != null) {
			adapter.setLabelProvider(propsalLabelProvider);
		}
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		toolkit.paintBordersFor(peopleComposite);
	}

	@Override
	/**
	 * This method is duplicated in BugzillaTaskEditor for now.
	 */
	public SearchHitCollector getDuplicateSearchCollector(String name) {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name;
		List<AbstractDuplicateDetector> allDetectors = getDuplicateSearchCollectorsList();

		for (AbstractDuplicateDetector detector : allDetectors) {
			if (detector.getName().equals(duplicateDetectorName)) {
				return detector.getSearchHitCollector(repository, taskData);
			}
		}
		// didn't find it
		return null;
	}

	@Override
	/**
	 * This method is duplicated in BugzillaTaskEditor for now.
	 */
	protected List<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		return TasksUiPlugin.getDefault().getDuplicateSearchCollectorsList();
	}

	@Override
	public void submitToRepository() {
		if (summaryText.getText().equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please provide a brief summary with new reports.");
			summaryText.setFocus();
			return;
		} else if (descriptionTextViewer.getTextWidget().getText().equals("")) {
			MessageDialog.openInformation(this.getSite().getShell(), "Submit Error",
					"Please proved a detailed summary with new reports");
			descriptionTextViewer.getTextWidget().setFocus();
			return;
		}
		super.submitToRepository();
	}

	/**
	 * Break text up into lines so that it is displayed properly in bugzilla
	 */
	private static String formatTextToLineWrap(String origText, boolean hardWrap) {
		// BugzillaServerVersion bugzillaServerVersion =
		// IBugzillaConstants.BugzillaServerVersion.fromString(repository
		// .getVersion());
		// if (bugzillaServerVersion != null &&
		// bugzillaServerVersion.compareTo(BugzillaServerVersion.SERVER_220) >=
		// 0) {
		// return origText;
		if (!hardWrap) {
			return origText;
		} else {
			String[] textArray = new String[(origText.length() / WRAP_LENGTH + 1) * 2];
			for (int i = 0; i < textArray.length; i++)
				textArray[i] = null;
			int j = 0;
			while (true) {
				int spaceIndex = origText.indexOf(" ", WRAP_LENGTH - 5);
				if (spaceIndex == origText.length() || spaceIndex == -1) {
					textArray[j] = origText;
					break;
				}
				textArray[j] = origText.substring(0, spaceIndex);
				origText = origText.substring(spaceIndex + 1, origText.length());
				j++;
			}

			String newText = "";

			for (int i = 0; i < textArray.length; i++) {
				if (textArray[i] == null)
					break;
				newText += textArray[i] + "\n";
			}
			return newText;
		}
	}
}
