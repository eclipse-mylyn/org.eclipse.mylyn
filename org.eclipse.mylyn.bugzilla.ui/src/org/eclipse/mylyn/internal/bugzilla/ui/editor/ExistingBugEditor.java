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

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasks.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylar.internal.tasks.ui.views.DatePicker;
import org.eclipse.mylar.internal.tasks.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.RepositoryTaskData;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * An editor used to view a bug report that exists on a server. It uses a
 * <code>BugReport</code> object to store the data.
 * 
 * @author Mik Kersten (hardening of prototype)
 * @author Rob Elves (adaption to Eclipse Forms)
 * @authos Jeff Pound (Attachment work)
 */
public class ExistingBugEditor extends AbstractRepositoryTaskEditor {

	private static final String LABEL_TIME_TRACKING = "Bugzilla Time Tracking";
	
	private BugSubmissionHandler submissionHandler;	
	
	protected Set<String> removeCC = new HashSet<String>();

	// protected BugzillaCompareInput compareInput;

	// protected Button compareButton;

	protected List keyWordsList;

	protected Text keywordsText;

	protected List ccList;

	protected Text ccText;

	protected Text urlText;

	protected RepositoryTaskData taskData;

	protected AbstractRepositoryConnector connector;

	protected Text estimateText;

	protected Text actualText;

	protected Text remainingText;

	protected Text addTimeText;

	protected Text deadlineText;

	protected DatePicker deadlinePicker;

	/**
	 * Creates a new <code>ExistingBugEditor</code>.
	 */
	public ExistingBugEditor(FormEditor editor) {
		super(editor);

		// Set up the input for comparing the bug report to the server
		// CompareConfiguration config = new CompareConfiguration();
		// config.setLeftEditable(false);
		// config.setRightEditable(false);
		// config.setLeftLabel("Local Bug Report");
		// config.setRightLabel("Remote Bug Report");
		// config.setLeftImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
		// config.setRightImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
		// compareInput = new BugzillaCompareInput(config);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) {
		if (!(input instanceof ExistingBugEditorInput))
			return;// MylarStatusHandler.log("Invalid Input: Must be
		// ExistingBugEditorInput", this);

		editorInput = (AbstractBugEditorInput) input;
		taskData = editorInput.getRepositoryTaskData();
		repository = editorInput.getRepository();
		connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());
		submissionHandler = new BugSubmissionHandler(connector);
		
		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(editorInput.getRepositoryTaskData());

		// restoreBug();
		isDirty = false;
		updateEditorTitle();
	}

	@Override
	protected String getTitleString() {
		return taskData.getLabel();// + ": " + checkText(summaryVal);
	}

	@Override
	public void submitBug() {
		if (isDirty()) {
			this.doSave(new NullProgressMonitor());
		}
		updateBug();
		submitButton.setEnabled(false);
		showBusy(true);
		BugzillaReportSubmitForm bugzillaReportSubmitForm;

		try {
			if (taskData.isLocallyCreated()) {
				boolean wrap = IBugzillaConstants.BugzillaServerVersion.SERVER_218.equals(repository.getVersion());
				bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeNewBugPost(repository.getUrl(), repository
						.getUserName(), repository.getPassword(), editorInput.getProxySettings(), repository
						.getCharacterEncoding(), taskData, wrap);
			} else {
				bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeExistingBugPost(taskData, repository.getUrl(),
						repository.getUserName(), repository.getPassword(), editorInput.getProxySettings(), removeCC,
						repository.getCharacterEncoding());
			}
		} catch (UnsupportedEncodingException e) {
			// should never get here but just in case...
			MessageDialog.openError(null, "Posting Error", "Ensure proper encoding selected in "
					+ TaskRepositoriesView.NAME + ".");
			return;
		}

		final BugzillaRepositoryConnector bugzillaRepositoryClient = (BugzillaRepositoryConnector) TasksUiPlugin
				.getRepositoryManager().getRepositoryConnector(BugzillaCorePlugin.REPOSITORY_KIND);

		JobChangeAdapter submitJobListener = new JobChangeAdapter() {

			public void done(final IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (event.getJob().getResult().getCode() == Status.OK
								&& event.getJob().getResult().getMessage() != null) {//
							// Attach context
							if (getAttachContext()) {
								ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(
										AbstractRepositoryTask.getHandle(repository.getUrl(), taskData.getId()));
								try {
									bugzillaRepositoryClient.attachContext(repository, (AbstractRepositoryTask) task,
											"", TasksUiPlugin.getDefault().getProxySettings());
								} catch (Exception e) {
									// TODO
									e.printStackTrace();
								}
							}
							close();
							return;
						} else if (event.getJob().getResult().getCode() == Status.INFO) {
							WebBrowserDialog.openAcceptAgreement(null, IBugzillaConstants.REPORT_SUBMIT_ERROR, event
									.getJob().getResult().getMessage(), event.getJob().getResult().getException()
									.getMessage());
							submitButton.setEnabled(true);
							ExistingBugEditor.this.showBusy(false);
						} else if (event.getJob().getResult().getCode() == Status.ERROR) {
							MessageDialog.openError(null, IBugzillaConstants.REPORT_SUBMIT_ERROR, event.getResult()
									.getMessage());
							submitButton.setEnabled(true);
							ExistingBugEditor.this.showBusy(false);
						}
					}
				});
			}
		};

		submissionHandler.submitBugReport(bugzillaReportSubmitForm, submitJobListener, false);
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		addCCList(toolkit, "", composite);

		try {
			addKeywordsList(toolkit, getRepositoryTaskData().getAttributeValue(RepositoryTaskAttribute.KEYWORDS),
					composite);
		} catch (IOException e) {
			MessageDialog.openInformation(null, "Attribute Display Error",
					"Could not retrieve keyword list, ensure proper configuration in " + TaskRepositoriesView.NAME
							+ "\n\nError reported: " + e.getMessage());
		}

		if (getRepositoryTaskData().getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()) != null)
			addBugzillaTimeTracker(toolkit, composite);
	}

	protected void addBugzillaTimeTracker(FormToolkit toolkit, Composite parent) {

		Section timeSection = toolkit.createSection(parent, ExpandableComposite.TREE_NODE);
		timeSection.setText(LABEL_TIME_TRACKING);
		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, true, false);
		gd.horizontalSpan = 4;
		timeSection.setLayout(gl);
		timeSection.setLayoutData(gd);

		Composite timeComposite = toolkit.createComposite(timeSection);
		gl = new GridLayout(4, true);
		timeComposite.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 5;
		timeComposite.setLayoutData(gd);

		RepositoryTaskData data = getRepositoryTaskData();

		toolkit.createLabel(timeComposite, BugzillaReportElement.ESTIMATED_TIME.toString());
		estimateText = toolkit.createText(timeComposite, data.getAttributeValue(BugzillaReportElement.ESTIMATED_TIME
				.getKeyString()), SWT.BORDER);
		estimateText.setFont(TEXT_FONT);
		estimateText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		estimateText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
				taskData.setAttributeValue(BugzillaReportElement.ESTIMATED_TIME.getKeyString(), estimateText.getText());
			}
		});

		toolkit.createLabel(timeComposite, "Current Estimate:");
		Text currentEstimate = toolkit.createText(timeComposite, ""
				+ (Float.parseFloat(data.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())) + Float
						.parseFloat(data.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString()))));
		currentEstimate.setFont(TEXT_FONT);
		currentEstimate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		currentEstimate.setEditable(false);

		toolkit.createLabel(timeComposite, BugzillaReportElement.ACTUAL_TIME.toString());
		actualText = toolkit.createText(timeComposite, data.getAttributeValue(BugzillaReportElement.ACTUAL_TIME
				.getKeyString()));
		actualText.setFont(TEXT_FONT);
		actualText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		actualText.setEditable(false);

		data.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), "0");
		toolkit.createLabel(timeComposite, BugzillaReportElement.WORK_TIME.toString());
		addTimeText = toolkit.createText(timeComposite, data.getAttributeValue(BugzillaReportElement.WORK_TIME
				.getKeyString()), SWT.BORDER);
		addTimeText.setFont(TEXT_FONT);
		addTimeText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		addTimeText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
				taskData.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), addTimeText.getText());
			}
		});

		toolkit.createLabel(timeComposite, BugzillaReportElement.REMAINING_TIME.toString());
		remainingText = toolkit.createText(timeComposite, data.getAttributeValue(BugzillaReportElement.REMAINING_TIME
				.getKeyString()), SWT.BORDER);
		remainingText.setFont(TEXT_FONT);
		remainingText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		remainingText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				markDirty(true);
				taskData
						.setAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString(), remainingText.getText());
			}
		});

		toolkit.createLabel(timeComposite, BugzillaReportElement.DEADLINE.toString());

		deadlinePicker = new DatePicker(timeComposite, /* SWT.NONE */SWT.BORDER, data
				.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
		deadlinePicker.setFont(TEXT_FONT);
		deadlinePicker.setDatePattern("yyyy-MM-dd");
		deadlinePicker.addPickerSelectionListener(new SelectionListener() {

			public void widgetDefaultSelected(SelectionEvent e) {
				// ignore
			}

			public void widgetSelected(SelectionEvent e) {
				Calendar cal = deadlinePicker.getDate();
				if (cal != null) {
					Date d = cal.getTime();
					SimpleDateFormat f = (SimpleDateFormat) SimpleDateFormat.getDateInstance();
					f.applyPattern("yyyy-MM-dd");

					taskData.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), f.format(d));
					markDirty(true); // TODO goes dirty even if user
					// presses cancel
				}
			}
		});

		timeSection.setClient(timeComposite);
	}

	protected void addKeywordsList(FormToolkit toolkit, String keywords, Composite attributesComposite)
			throws IOException {
		// newLayout(attributesComposite, 1, "Keywords:", PROPERTY);
		toolkit.createLabel(attributesComposite, "Keywords:");
		keywordsText = toolkit.createText(attributesComposite, keywords);
		keywordsText.setFont(TEXT_FONT);
		keywordsText.setEditable(false);
		// keywordsText.setForeground(foreground);
		// keywordsText.setBackground(JFaceColors.getErrorBackground(display));
		GridData keywordsData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		keywordsData.horizontalSpan = 2;
		keywordsData.widthHint = 200;
		keywordsText.setLayoutData(keywordsData);
		// keywordsText.setText(keywords);
		keywordsText.addListener(SWT.FocusIn, new GenericListener());
		keyWordsList = new List(attributesComposite, SWT.MULTI | SWT.V_SCROLL);
		keyWordsList.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		keyWordsList.setFont(TEXT_FONT);
		GridData keyWordsTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		keyWordsTextData.horizontalSpan = 1;
		keyWordsTextData.widthHint = 125;
		keyWordsTextData.heightHint = 40;
		keyWordsList.setLayoutData(keyWordsTextData);

		// initialize the keywords list with valid values

		java.util.List<String> validKeywords = new ArrayList<String>();
		try {
			validKeywords = BugzillaCorePlugin.getRepositoryConfiguration(false, repository.getUrl(),
					TasksUiPlugin.getDefault().getProxySettings(), repository.getUserName(), repository.getPassword(),
					repository.getCharacterEncoding()).getKeywords();
		} catch (Exception e) {
			// ignore
		}

		if (validKeywords != null) {
			for (Iterator<String> it = validKeywords.iterator(); it.hasNext();) {
				String keyword = it.next();
				keyWordsList.add(keyword);
			}

			// get the selected keywords for the bug
			StringTokenizer st = new StringTokenizer(keywords, ",", false);
			ArrayList<Integer> indicies = new ArrayList<Integer>();
			while (st.hasMoreTokens()) {
				String s = st.nextToken().trim();
				int index = keyWordsList.indexOf(s);
				if (index != -1)
					indicies.add(new Integer(index));
			}

			// select the keywords that were selected for the bug
			int length = indicies.size();
			int[] sel = new int[length];
			for (int i = 0; i < length; i++) {
				sel[i] = indicies.get(i).intValue();
			}
			keyWordsList.select(sel);
		}

		keyWordsList.addSelectionListener(new KeywordListener());
		keyWordsList.addListener(SWT.FocusIn, new GenericListener());
	}

	protected void addCCList(FormToolkit toolkit, String ccValue, Composite attributesComposite) {
		// newLayout(attributesComposite, 1, "Add CC:", PROPERTY);
		toolkit.createLabel(attributesComposite, "Add CC:");
		ccText = toolkit.createText(attributesComposite, ccValue);
		ccText.setFont(TEXT_FONT);
		ccText.setEditable(true);
		// ccText.setForeground(foreground);
		// ccText.setBackground(background);
		GridData ccData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ccData.horizontalSpan = 1;
		ccData.widthHint = 200;
		ccText.setLayoutData(ccData);
		// ccText.setText(ccValue);
		ccText.addListener(SWT.FocusIn, new GenericListener());
		ccText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				markDirty(true);
				taskData.setAttributeValue(BugzillaReportElement.NEWCC.getKeyString(), ccText.getText());
			}

		});

		// newLayout(attributesComposite, 1, "CC: (Select to remove)",
		// PROPERTY);
		toolkit.createLabel(attributesComposite, "CC: (Select to remove)");
		ccList = new List(attributesComposite, SWT.MULTI | SWT.V_SCROLL);// SWT.BORDER
		ccList.setData(FormToolkit.KEY_DRAW_BORDER, FormToolkit.TEXT_BORDER);
		ccList.setFont(TEXT_FONT);
		GridData ccListData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		ccListData.horizontalSpan = 1;
		ccListData.widthHint = 125;
		ccListData.heightHint = 40;
		ccList.setLayoutData(ccListData);

		java.util.List<String> ccs = taskData.getCC();
		if (ccs != null) {
			for (Iterator<String> it = ccs.iterator(); it.hasNext();) {
				String cc = it.next();
				ccList.add(cc);// HtmlStreamTokenizer.unescape(cc));
			}
		}

		ccList.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				markDirty(true);

				for (String cc : ccList.getItems()) {
					int index = ccList.indexOf(cc);
					if (ccList.isSelected(index)) {
						removeCC.add(cc);
					} else {
						removeCC.remove(cc);
					}
				}
			}

			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});
		ccList.addListener(SWT.FocusIn, new GenericListener());
	}

	@Override
	protected void updateBug() {
		taskData.setHasLocalChanges(true);
		// go through all of the attributes and update the main values to the
		// new ones
		// for (Iterator<RepositoryTaskAttribute> it =
		// bug.getAttributes().iterator(); it.hasNext();) {
		// RepositoryTaskAttribute a = it.next();
		// if (a.getNewValue() != null &&
		// a.getNewValue().compareTo(a.getValue()) != 0) {
		// bug.setHasChanged(true);
		// }
		// a.setValue(a.getNewValue());
		//
		// }
		// if (bug.getNewComment().compareTo(bug.getNewNewComment()) != 0) {
		// // TODO: Ask offline reports if this is true?
		// bug.setHasChanged(true);
		// }

		// Update some other fields as well.
		// bug.setNewComment(bug.getNewNewComment());

	}

	// @Override
	// protected void restoreBug() {
	//
	// if (taskData == null)
	// return;
	//
	// // go through all of the attributes and restore the new values to the
	// // main ones
	// // for (Iterator<RepositoryTaskAttribute> it =
	// // bug.getAttributes().iterator(); it.hasNext();) {
	// // RepositoryTaskAttribute a = it.next();
	// // a.setNewValue(a.getValue());
	// // }
	//
	// // Restore some other fields as well.
	// // bug.setNewNewComment(bug.getNewComment());
	// }

	/**
	 * This job opens a compare editor to compare the current state of the bug
	 * in the editor with the bug on the server.
	 */
	// protected class OpenCompareEditorJob extends Job {
	//
	// public OpenCompareEditorJob(String name) {
	// super(name);
	// }
	//
	// @Override
	// protected IStatus run(IProgressMonitor monitor) {
	// final RepositoryTaskData serverBug;
	// try {
	// TaskRepository repository =
	// TasksUiPlugin.getRepositoryManager().getRepository(
	// BugzillaPlugin.REPOSITORY_KIND, taskData.getRepositoryUrl());
	// serverBug = BugzillaServerFacade.getBug(repository.getUrl(),
	// repository.getUserName(), repository
	// .getPassword(), editorInput.getProxySettings(),
	// repository.getCharacterEncoding(), Integer
	// .parseInt(taskData.getId()));
	// // If no bug was found on the server, throw an exception so that
	// // the
	// // user gets the same message that appears when there is a
	// // problem reading the server.
	// if (serverBug == null)
	// throw new Exception();
	// } catch (Exception e) {
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// public void run() {
	// MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
	// "Could not open bug.", "Bug #" + taskData.getId()
	// + " could not be read from the server.");
	// }
	// });
	// return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK,
	// "Could not get the bug report from the server.", null);
	// }
	// PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
	// public void run() {
	// compareInput.setTitle("Bug #" + taskData.getId());
	// compareInput.setLeft(taskData);
	// compareInput.setRight(serverBug);
	// CompareUI.openCompareEditor(compareInput);
	// }
	// });
	// return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "",
	// null);
	// }
	//
	// }
	/**
	 * Class to handle the selection change of the keywords.
	 */
	protected class KeywordListener implements SelectionListener {

		/*
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent arg0) {
			markDirty(true);

			// get the selected keywords and create a string to submit
			StringBuffer keywords = new StringBuffer();
			String[] sel = keyWordsList.getSelection();

			// allow unselecting 1 keyword when it is the only one selected
			if (keyWordsList.getSelectionCount() == 1) {
				int index = keyWordsList.getSelectionIndex();
				String keyword = keyWordsList.getItem(index);
				if (getRepositoryTaskData().getAttributeValue(BugzillaReportElement.KEYWORDS.getKeyString()).equals(
						keyword))
					keyWordsList.deselectAll();
			}

			for (int i = 0; i < keyWordsList.getSelectionCount(); i++) {
				keywords.append(sel[i]);
				if (i != keyWordsList.getSelectionCount() - 1) {
					keywords.append(",");
				}
			}

			taskData.setAttributeValue(BugzillaReportElement.KEYWORDS.getKeyString(), keywords.toString());

			// update the keywords text field
			keywordsText.setText(keywords.toString());
		}

		/*
		 * @see org.eclipse.swt.events.SelectionListener#widgetDefaultSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetDefaultSelected(SelectionEvent arg0) {
			// no need to listen to this
		}

	}

	/**
	 * A listener for selection of a comment.
	 */
	protected class CommentListener implements Listener {

		/** The comment that this listener is for. */
		private TaskComment taskComment;

		/**
		 * Creates a new <code>CommentListener</code>.
		 * 
		 * @param taskComment
		 *            The comment that this listener is for.
		 */
		public CommentListener(TaskComment taskComment) {
			this.taskComment = taskComment;
		}

		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(),
							taskComment.getCreated(), taskComment, taskData.getSummary()))));
		}
	}

	@Override
	protected void validateInput() {
		// RepositoryOperation o = taskData.getSelectedOperation();
		// if (o != null && o.getKnobName().compareTo("resolve") == 0
		// && (addCommentsTextBox.getText() == null ||
		// addCommentsTextBox.getText().equals(""))) {
		// // TODO: Highlight (change to light red?) New Comment area to
		// // indicate need for message
		// submitButton.setEnabled(false);
		// } else {
		// submitButton.setEnabled(true);
		// }
	}

	/**
	 * Adds a text field to display and edit the bug's URL attribute.
	 * 
	 * @param url
	 *            The URL attribute of the bug.
	 * @param attributesComposite
	 *            The composite to add the text field to.
	 */
	protected void addUrlText(String url, Composite attributesComposite) {
		FormToolkit toolkit = new FormToolkit(attributesComposite.getDisplay());
		toolkit.createLabel(attributesComposite, "URL:");
		urlText = toolkit.createText(attributesComposite, url);
		urlText.setFont(TEXT_FONT);
		GridData urlTextData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		urlTextData.horizontalSpan = 3;
		urlTextData.widthHint = 200;
		urlText.setLayoutData(urlTextData);
		// urlText.setText(url);
		urlText.addListener(SWT.KeyUp, new Listener() {
			public void handleEvent(Event event) {
				String sel = urlText.getText();
				RepositoryTaskAttribute a = getRepositoryTaskData().getAttribute(
						BugzillaReportElement.BUG_FILE_LOC.getKeyString());
				if (!(a.getValue().equals(sel))) {
					a.setValue(sel);
					markDirty(true);
				}
			}
		});
		urlText.addListener(SWT.FocusIn, new GenericListener());
	}

	@Override
	public void createCustomAttributeLayout() {
		// ignore
	}

	@Override
	public RepositoryTaskData getRepositoryTaskData() {
		return editorInput.getRepositoryTaskData();
	}

	public boolean getAttachContext() {
		if (attachContextButton == null) {
			return false;
		}
		return attachContextButton.getSelection();
	}

	public void setAttachContext(boolean attachContext) {
		if (attachContextButton != null && attachContextButton.isEnabled()) {
			attachContextButton.setSelection(attachContext);
		}
	}

	// protected void createDescriptionLayout(Composite composite) {
	// // This is migration code from 0.6.1 -> 0.6.2
	// // Changes to the abstract editor causes the description
	// // field of the bugzilla editor to be editable if the offline
	// // task data hasn't been saved yet. Upon being saved it works fine but
	// // the initial load of the page would have an editable description
	// // area if this was not present. TODO: Remove post 0.6.1.
	// super.createDescriptionLayout(composite);
	// descriptionTextViewer.setEditable(false);
	// }

	// TODO used for spell checking. Add back when we want to support this
	// protected Button checkSpellingButton;
	//	
	// private void checkSpelling() {
	// SpellingContext context= new SpellingContext();
	// context.setContentType(Platform.getContentTypeManager().getContentType(IContentTypeManager.CT_TEXT));
	// IDocument document = new Document(addCommentsTextBox.getText());
	// ISpellingProblemCollector collector= new
	// SpellingProblemCollector(document);
	// EditorsUI.getSpellingService().check(document, context, collector, new
	// NullProgressMonitor());
	// }
	//	
	// private class SpellingProblemCollector implements
	// ISpellingProblemCollector {
	//
	// private IDocument document;
	//		
	// private SpellingDialog spellingDialog;
	//		
	// public SpellingProblemCollector(IDocument document){
	// this.document = document;
	// spellingDialog = new
	// SpellingDialog(Display.getCurrent().getActiveShell(), "Spell Checking",
	// document);
	// }
	//		
	// /*
	// * @see
	// org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#accept(org.eclipse.ui.texteditor.spelling.SpellingProblem)
	// */
	// public void accept(SpellingProblem problem) {
	// try {
	// int line= document.getLineOfOffset(problem.getOffset()) + 1;
	// String word= document.get(problem.getOffset(), problem.getLength());
	//				
	// spellingDialog.open(word, problem.getProposals());
	//				
	// } catch (BadLocationException x) {
	// // drop this SpellingProblem
	// }
	// }
	//
	// /*
	// * @see
	// org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#beginCollecting()
	// */
	// public void beginCollecting() {
	//			
	// }
	//
	// /*
	// * @see
	// org.eclipse.ui.texteditor.spelling.ISpellingProblemCollector#endCollecting()
	// */
	// public void endCollecting() {
	// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
	// "Spell Checking Finished", "The spell check has finished");
	// }
	// }
}