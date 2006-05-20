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

import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.core.NewBugzillaReport;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.bugzilla.core.IBugzillaBug;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
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
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;

/**
 * An editor used to view a locally created bug that does not yet exist on a
 * server. It uses a <code>NewBugModel</code> object to store the data.
 */
public class NewBugEditor extends AbstractBugEditor {

	protected NewBugzillaReport bug;

	protected Text descriptionText;

	protected String newSummary = "";

	protected String newDescription = "";

	/**
	 * Creates a new <code>NewBugEditor</code>.
	 */
	public NewBugEditor(TaskRepository repository) {
		super.repository = repository;
	}

	@Override
	public IBugzillaBug getBug() {
		return bug;
	}

	@Override
	protected void addKeywordsList(FormToolkit toolkit, String keywords, Composite attributesComposite) {
		// Since NewBugModels have no keywords, there is no
		// GUI for them.
	}

	@Override
	protected void createDescriptionLayout(FormToolkit toolkit, final ScrolledForm form) {

		Section section = toolkit.createSection(form.getBody(), ExpandableComposite.TITLE_BAR | Section.TWISTIE);
		section.setText(LABEL_SECTION_DESCRIPTION);
		section.setExpanded(true);
		section.setLayout(new GridLayout());
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		section.addExpansionListener(new IExpansionListener() {
			public void expansionStateChanging(ExpansionEvent e) {
				form.reflow(true);
			}

			public void expansionStateChanged(ExpansionEvent e) {
				form.reflow(true);
			}
		});
		
		
		
		// Description Area
		Composite descriptionComposite = toolkit.createComposite(form.getBody());
		GridLayout descriptionLayout = new GridLayout();
		descriptionLayout.numColumns = 4;
		descriptionComposite.setLayout(descriptionLayout);
//		descriptionComposite.setBackground(background);
		GridData descriptionData = new GridData(GridData.FILL_BOTH);
		descriptionData.horizontalSpan = 1;
		descriptionData.grabExcessVerticalSpace = false;
		descriptionComposite.setLayoutData(descriptionData);
		// End Description Area
		section.setClient(descriptionComposite);
		
		Composite descriptionTitleComposite = toolkit.createComposite(descriptionComposite, SWT.NONE);
		GridLayout descriptionTitleLayout = new GridLayout();
		descriptionTitleLayout.horizontalSpacing = 0;
		descriptionTitleLayout.marginWidth = 0;
		descriptionTitleComposite.setLayout(descriptionTitleLayout);
//		descriptionTitleComposite.setBackground(background);
		GridData descriptionTitleData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionTitleData.horizontalSpan = 4;
		descriptionTitleData.grabExcessVerticalSpace = false;
		descriptionTitleComposite.setLayoutData(descriptionTitleData);
		newLayout(descriptionTitleComposite, 4, "Description:", HEADER).addListener(SWT.FocusIn,
				new DescriptionListener());

		descriptionText = new Text(descriptionComposite, SWT.BORDER | SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		//descriptionText.setFont(COMMENT_FONT);
		IThemeManager themeManager = PlatformUI.getWorkbench().getThemeManager();
		Font descriptionFont = themeManager.getCurrentTheme().getFontRegistry().get(AbstractBugEditor.REPOSITORY_TEXT_ID);
		descriptionText.setFont(descriptionFont);
		GridData descriptionTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		descriptionTextData.horizontalSpan = 4;
		descriptionTextData.widthHint = DESCRIPTION_WIDTH;
		descriptionTextData.heightHint = DESCRIPTION_HEIGHT;
		descriptionText.setLayoutData(descriptionTextData);
		descriptionText.setText(bug.getDescription());
		descriptionText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				String sel = descriptionText.getText() + event.character;
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
	protected void createCommentLayout(FormToolkit toolkit, final ScrolledForm form) {
		// Since NewBugModels have no comments, there is no
		// GUI for them.
	}

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		// Since NewBugModels have no special submitting actions,
		// no radio buttons are required.
	}

	@Override
	protected String getTitleString() {
		return bug.getLabel();
	}

	@Override
	protected void submitBug() {
		updateBug();
		Proxy proxySettings = MylarTaskListPlugin.getDefault().getProxySettings();
		boolean wrap = IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		try {
			bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeNewBugPost(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, repository.getCharacterEncoding(), bug, wrap);
		} catch (UnsupportedEncodingException e) {
			// should never get here but just in case...
			MessageDialog.openError(null, "Posting Error", "Ensure proper encoding selected in "+TaskRepositoriesView.NAME+".");
			return;
		}
		final BugzillaRepositoryConnector bugzillaRepositoryClient = (BugzillaRepositoryConnector) MylarTaskListPlugin
				.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);

		IJobChangeListener closeEditorListener = new IJobChangeListener() {

			public void done(IJobChangeEvent event) {
				if (event.getJob().getResult().equals(Status.OK_STATUS)) {
					close();
				} else {
					submitButton.setEnabled(true);
					NewBugEditor.this.showBusy(false);
				}
			}

			public void aboutToRun(IJobChangeEvent event) {
				// ignore
			}

			public void awake(IJobChangeEvent event) {
				// ignore
			}

			public void running(IJobChangeEvent event) {
				// ignore
			}

			public void scheduled(IJobChangeEvent event) {
				// ignore
			}

			public void sleeping(IJobChangeEvent event) {
				// ignore
			}
		};
		bugzillaRepositoryClient.submitBugReport(bug, bugzillaReportSubmitForm, closeEditorListener);
	}

	@Override
	protected void updateBug() {
		// go through all of the attributes and update the main values to the
		// new ones
//		for (Iterator<AbstractRepositoryReportAttribute> it = bug.getAttributes().iterator(); it.hasNext();) {
//			AbstractRepositoryReportAttribute a = it.next();
//			a.setValue(a.getNewValue());
//		}

		// Update some other fields as well.
		bug.setSummary(newSummary);
		bug.setDescription(newDescription);
	}

	@Override
	protected void restoreBug() {
		// go through all of the attributes and restore the new values to the
		// main ones
//		for (Iterator<AbstractRepositoryReportAttribute> it = bug.getAttributes().iterator(); it.hasNext();) {
//			AbstractRepositoryReportAttribute a = it.next();
//			a.setNewValue(a.getValue());
//		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof NewBugEditorInput))
			throw new PartInitException("Invalid Input: Must be NewBugEditorInput");
		NewBugEditorInput ei = (NewBugEditorInput) input;
		setSite(site);
		setInput(input);
		bugzillaInput = ei;
		bugzillaOutlineModel = BugzillaOutlineNode.parseBugReport(bugzillaInput.getBug());
		bug = ei.getBug();
		newSummary = bug.getSummary();
		newDescription = bug.getDescription();
		restoreBug();
		isDirty = false;
		updateEditorTitle();
	}

	/**
	 * A listener for selection of the description textbox.
	 */
	protected class DescriptionListener implements Listener {
		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new BugzillaReportSelection(bug.getId(), bug.getRepositoryUrl(), "New Description", false, bug
							.getSummary()))));
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
	protected void addCCList(FormToolkit toolkit, String value, Composite attributesComposite) {
		// do nothing here right now
	}
}

//final WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
//protected void execute(final IProgressMonitor monitor) throws CoreException {
//	PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
//		public void run() {
//			// update the bug on the server
//			try {
//
//				bugzillaRepositoryClient.submitBugReport(bug, bugReportPostHandler);
//
//				// If the bug was successfully sent...
//				if (NewBugEditor.this != null && !NewBugEditor.this.isDisposed()) {
//					changeDirtyStatus(false);
//					close();
//					// BugzillaPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow().getActivePage()
//					// .closeEditor(NewBugEditor.this, false);
//				}
//			} catch (BugzillaException e) {
//				MessageDialog.openError(null, "I/O Error", "Bugzilla could not post your bug.");
//				BugzillaPlugin.log(e);
//			} catch (PossibleBugzillaFailureException e) {
//				WebBrowserDialog.openAcceptAgreement(null, "Possible Bugzilla Client Failure",
//						"Bugzilla may not have posted your bug.\n" + e.getMessage(), bugReportPostHandler
//								.getError());
//				BugzillaPlugin.log(e);
//			} catch (LoginException e) {
//				e.printStackTrace();
//				// if we had an error with logging in, display an
//				// error
//				MessageDialog.openError(null, "Posting Error",
//						"Bugzilla could not post your bug since your login name or password is incorrect."
//								+ "\nPlease check your settings in the bugzilla preferences. ");
//			}
//		}
//	});
//}
//};
//Job job = new Job("Submitting New Bug") {
//
//@Override
//protected IStatus run(IProgressMonitor monitor) {
//	try {
//		op.run(monitor);
//	} catch (Exception e) {
//		MylarStatusHandler.log(e, "Failed to submit bug");
//		return new Status(Status.ERROR, "org.eclipse.mylar.internal.bugzilla.ui", Status.ERROR,
//				"Failed to submit bug", e);
//	}
//
//	BugzillaRepositoryClient client = (BugzillaRepositoryClient) MylarTaskListPlugin.getRepositoryManager()
//			.getRepositoryClient(BugzillaPlugin.REPOSITORY_KIND);
//	if (client != null) {
//		client.synchronize();
//	}
//	return Status.OK_STATUS;
//}
//
//};
//job.schedule();
