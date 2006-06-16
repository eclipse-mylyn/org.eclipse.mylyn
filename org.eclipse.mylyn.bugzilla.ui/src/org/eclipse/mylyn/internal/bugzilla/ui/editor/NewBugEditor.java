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
package org.eclipse.mylar.internal.bugzilla.ui.editor;

import java.io.UnsupportedEncodingException;
import java.net.Proxy;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.TaskUiUtil;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskListView;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskCategory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;

/**
 * An editor used to view a locally created bug that does not yet exist on a
 * server.
 * 
 * @author Rob Elves (modifications)
 */
public class NewBugEditor extends AbstractRepositoryTaskEditor {

	protected RepositoryTaskData taskData;

	protected Text descriptionText;

	protected String newSummary = "";

	protected String newDescription = "";

	private String submittedBugId = null;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof NewBugEditorInput))
			throw new PartInitException("Invalid Input: Must be NewBugEditorInput");
		NewBugEditorInput ei = (NewBugEditorInput) input;
		setSite(site);
		setInput(input);
		editorInput = ei;
		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(editorInput.getRepositoryTaskData());
		taskData = ei.getRepositoryTaskData();
		newSummary = taskData.getSummary();
		newDescription = taskData.getDescription();
		repository = editorInput.getRepository();
		isDirty = false;
		updateEditorTitle();
	}

	@Override
	public RepositoryTaskData getRepositoryTaskData() {
		return taskData;
	}

	@Override
	protected void createDescriptionLayout(Composite composite) {
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		Section section = toolkit.createSection(composite, ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_DESCRIPTION);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Composite descriptionComposite = toolkit.createComposite(section);
		GridLayout descriptionLayout = new GridLayout();
		descriptionLayout.numColumns = 4;
		descriptionComposite.setLayout(descriptionLayout);
		// descriptionComposite.setBackground(background);
		GridData descriptionData = new GridData(GridData.FILL_BOTH);
		descriptionData.horizontalSpan = 1;
		descriptionData.grabExcessVerticalSpace = false;
		descriptionComposite.setLayoutData(descriptionData);
		// End Description Area
		section.setClient(descriptionComposite);

		descriptionText = new Text(descriptionComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		// descriptionText.setFont(COMMENT_FONT);
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font descriptionFont = themeManager.getCurrentTheme().getFontRegistry().get(
				AbstractRepositoryTaskEditor.REPOSITORY_TEXT_ID);
		descriptionText.setFont(descriptionFont);
		GridData descriptionTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionTextData.horizontalSpan = 4;
		descriptionTextData.widthHint = DESCRIPTION_WIDTH;
		descriptionTextData.heightHint = DESCRIPTION_HEIGHT;
		descriptionText.setLayoutData(descriptionTextData);
		descriptionText.setText(taskData.getDescription());
		descriptionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				String sel = descriptionText.getText();
				if (!(newDescription.equals(sel))) {
					newDescription = sel;
					changeDirtyStatus(true);
				}
			}
		});
		descriptionText.addListener(SWT.FocusIn, new DescriptionListener());

		super.descriptionTextBox = descriptionText;

		this.createSeparatorSpace(descriptionComposite);
	}

	@Override
	protected void createReportHeaderLayout(Composite comp) {
		addSummaryText(comp);
	}

	// @Override
	// protected void createReportHeaderLayout(Composite comp) {
	// FormToolkit toolkit = new FormToolkit(comp.getDisplay());
	// Composite headerComposite = toolkit.createComposite(editorComposite);
	// headerComposite.setLayout(new GridLayout(2, false));
	// GridDataFactory.fillDefaults().grab(true,
	// false).applyTo(headerComposite);
	// toolkit.createLabel(headerComposite, "Posting To:").setFont(TITLE_FONT);
	// Text target = toolkit.createText(headerComposite, repository.getUrl(),
	// SWT.FLAT);
	// target.setFont(TITLE_FONT);
	// target.setEditable(false);
	// addSummaryText(headerComposite);
	// toolkit.paintBordersFor(headerComposite);
	// }

	@Override
	protected void createAttachmentLayout(Composite comp) {
		// currently can't attach while creating new bug
	}

	@Override
	protected void createCommentLayout(Composite comp, final ScrolledForm form) {
		// Since NewBugModels have no comments, there is no
		// GUI for them.
	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		// Since NewBugModels have no special submitting actions,
		// no radio buttons are required.
	}

	@Override
	public void createCustomAttributeLayout() {
		// ignore

	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		// ignore
	}

	@Override
	protected String getTitleString() {
		return taskData.getLabel();
	}

	@Override
	protected void submitBug() {
		updateBug();
		Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
		boolean wrap = IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		try {
			bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeNewBugPost(repository.getUrl(), repository
					.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(),
					taskData, wrap);
			submittedBugId = bugzillaReportSubmitForm.submitReportToRepository();
			if (submittedBugId != null) {
				close();			
				int bugId = -1;				
				bugId = Integer.parseInt(submittedBugId);				
				BugzillaTask newTask = new BugzillaTask(AbstractRepositoryTask.getHandle(repository.getUrl(), bugId),
						"<bugzilla info>", true);
				Object selectedObject = null;
				if (TaskListView.getFromActivePerspective() != null)
					selectedObject = ((IStructuredSelection) TaskListView.getFromActivePerspective().getViewer()
							.getSelection()).getFirstElement();

				if (selectedObject instanceof TaskCategory) {
					MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask,
							((TaskCategory) selectedObject));
				} else {
					MylarTaskListPlugin.getTaskListManager().getTaskList().addTask(newTask,
							MylarTaskListPlugin.getTaskListManager().getTaskList().getRootCategory());
				}

				TaskUiUtil.refreshAndOpenTaskListElement(newTask);
				MylarTaskListPlugin.getSynchronizationManager().synchNow(0);
				return;
			}

		} catch (UnsupportedEncodingException e) {
			// should never get here but just in case...
			MessageDialog.openError(null, "Posting Error", "Ensure proper encoding selected in "
					+ TaskRepositoriesView.NAME + ".");
		} catch (Exception e) {
			// TODO: Handle errors more appropriately (perhaps CoreException)
			MessageDialog.openError(null, "Posting Error", "Ensure proper configuration in "
					+ TaskRepositoriesView.NAME + ".");
		}
		submitButton.setEnabled(true);
		NewBugEditor.this.showBusy(false);
		// final BugzillaRepositoryConnector bugzillaRepositoryClient =
		// (BugzillaRepositoryConnector) MylarTaskListPlugin
		// .getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);
		//
		// IJobChangeListener closeEditorListener = new JobChangeAdapter() {
		// public void done(IJobChangeEvent event) {
		// if (event.getJob().getResult().equals(Status.OK_STATUS)) {
		// close();
		// if(submittedBugId != null) {
		// TaskUiUtil.openRepositoryTask(repository.getUrl(), submittedBugId,
		// AbstractRepositoryTask.getHandle(repository.getUrl(),
		// submittedBugId));
		// }
		// } else {
		// submitButton.setEnabled(true);
		// NewBugEditor.this.showBusy(false);
		// }
		// }
		// };
		// submittedBugId = bugzillaRepositoryClient.submitBugReport(taskData,
		// bugzillaReportSubmitForm, closeEditorListener);
	}

	@Override
	protected void updateBug() {
		taskData.setSummary(newSummary);
		taskData.setDescription(newDescription);
	}

	/**
	 * A listener for selection of the description textbox.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(), "New Description",
							false, taskData.getSummary()))));
		}
	}

	@Override
	public void handleSummaryEvent() {
		String sel = summaryText.getText();
		if (!(newSummary.equals(sel))) {
			newSummary = sel;
			changeDirtyStatus(true);
		}
	}

	@Override
	protected void validateInput() {
		// ignore
	}

	
	
	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}
}
