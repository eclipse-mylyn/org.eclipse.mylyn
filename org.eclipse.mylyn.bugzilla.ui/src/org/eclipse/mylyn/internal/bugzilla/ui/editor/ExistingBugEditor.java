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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareUI;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.IJobChangeListener;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportSubmitForm;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaRepositoryUtil;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaCompareInput;
import org.eclipse.mylar.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaRepositoryConnector;
import org.eclipse.mylar.internal.tasklist.Comment;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;
import org.eclipse.mylar.internal.tasklist.RepositoryOperation;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskAttribute;
import org.eclipse.mylar.internal.tasklist.RepositoryTaskData;
import org.eclipse.mylar.internal.tasklist.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.internal.tasklist.ui.editors.ExistingBugEditorInput;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskOutlineNode;
import org.eclipse.mylar.internal.tasklist.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.internal.tasklist.ui.views.TaskRepositoriesView;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryConnector;
import org.eclipse.mylar.provisional.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * An editor used to view a bug report that exists on a server. It uses a
 * <code>BugReport</code> object to store the data.
 * 
 * @author Mik Kersten (hardening of prototype)
 * @author Rob Elves (adaption to Eclipse Forms)
 */
public class ExistingBugEditor extends AbstractRepositoryTaskEditor {

	private static final String REASSIGN_BUG_TO = "Reassign  bug to";

	private static final String LABEL_COMPARE_BUTTON = "Compare";

	protected Set<String> removeCC = new HashSet<String>();

	protected BugzillaCompareInput compareInput;

	protected Button compareButton;

	protected Button[] radios;

	protected Control[] radioOptions;

	protected List keyWordsList;

	protected Text keywordsText;

	protected List ccList;

	protected Text ccText;

	protected Text urlText;

	// protected Text addCommentsText;

	protected RepositoryTaskData taskData;

	protected AbstractRepositoryConnector connector;

	// public String getNewCommentText() {
	// return addCommentsTextBox.getText();
	// }

	/**
	 * Creates a new <code>ExistingBugEditor</code>.
	 */
	public ExistingBugEditor() {
		super();

		// Set up the input for comparing the bug report to the server
		CompareConfiguration config = new CompareConfiguration();
		config.setLeftEditable(false);
		config.setRightEditable(false);
		config.setLeftLabel("Local Bug Report");
		config.setRightLabel("Remote Bug Report");
		config.setLeftImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
		config.setRightImage(PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT));
		compareInput = new BugzillaCompareInput(config);
	}

	// @SuppressWarnings("deprecation")
	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof ExistingBugEditorInput))
			throw new PartInitException("Invalid Input: Must be ExistingBugEditorInput");
		editorInput = (ExistingBugEditorInput) input;
		taskData = editorInput.getRepositoryTaskData();
		repository = editorInput.getRepository();
		connector = MylarTaskListPlugin.getRepositoryManager().getRepositoryConnector(repository.getKind());

		setSite(site);
		setInput(input);

		taskOutlineModel = RepositoryTaskOutlineNode.parseBugReport(editorInput.getRepositoryTaskData());

		// restoreBug();
		isDirty = false;
		updateEditorTitle();
	}

	// /**
	// * This overrides the existing implementation in order to add an "add to
	// * favorites" option to the context menu.
	// *
	// * @see
	// org.eclipse.mylar.internal.bugzilla.ui.AbstractRepositoryTaskEditor#createContextMenu()
	// */
	// @Override
	// protected void createContextMenu() {
	// contextMenuManager = new MenuManager(CONTEXT_MENU_ID);
	// contextMenuManager.setRemoveAllWhenShown(true);
	// contextMenuManager.addMenuListener(new IMenuListener() {
	// public void menuAboutToShow(IMenuManager manager) {
	// // manager.add(new
	// // AddToFavoritesAction(ExistingBugEditor.this));
	// // manager.add(new Separator());
	// manager.add(cutAction);
	// manager.add(copyAction);
	// manager.add(pasteAction);
	// manager.add(new Separator());
	// manager.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));
	// if (currentSelectedText == null ||
	// currentSelectedText.getSelectionText().length() == 0) {
	//
	// copyAction.setEnabled(false);
	// } else {
	// copyAction.setEnabled(true);
	// }
	// }
	// });
	// getSite().registerContextMenu("#BugEditor", contextMenuManager,
	// getSite().getSelectionProvider());
	// }

	@Override
	protected void addRadioButtons(Composite buttonComposite) {
		FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());
		int i = 0;
		Button selected = null;
		radios = new Button[taskData.getOperations().size()];
		radioOptions = new Control[taskData.getOperations().size()];
		for (Iterator<RepositoryOperation> it = taskData.getOperations().iterator(); it.hasNext();) {
			RepositoryOperation o = it.next();
			radios[i] = toolkit.createButton(buttonComposite, "", SWT.RADIO);
			radios[i].setFont(TEXT_FONT);
			GridData radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
			if (!o.hasOptions() && !o.isInput())
				radioData.horizontalSpan = 4;
			else
				radioData.horizontalSpan = 3;
			radioData.heightHint = 20;
			String opName = o.getOperationName();
			opName = opName.replaceAll("</.*>", "");
			opName = opName.replaceAll("<.*>", "");
			radios[i].setText(opName);
			radios[i].setLayoutData(radioData);
			// radios[i].setBackground(background);
			radios[i].addSelectionListener(new RadioButtonListener());
			radios[i].addListener(SWT.FocusIn, new GenericListener());

			if (o.hasOptions()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 1;
				radioData.heightHint = 20;
				radioData.widthHint = AbstractRepositoryTaskEditor.WRAP_LENGTH;
				// radioOptions[i] = new Combo(buttonComposite, SWT.NULL);
				radioOptions[i] = new CCombo(buttonComposite, SWT.FLAT | SWT.READ_ONLY);
				toolkit.adapt(radioOptions[i], true, true);
				// radioOptions[i] = new Combo(buttonComposite, SWT.MULTI |
				// SWT.V_SCROLL | SWT.READ_ONLY);
				// radioOptions[i].setData(FormToolkit.KEY_DRAW_BORDER,
				// FormToolkit.TEXT_BORDER);
				// radioOptions[i] = new Combo(buttonComposite,
				// SWT.NO_BACKGROUND | SWT.MULTI | SWT.V_SCROLL
				// | SWT.READ_ONLY);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);

				Object[] a = o.getOptionNames().toArray();
				Arrays.sort(a);
				for (int j = 0; j < a.length; j++) {
					((CCombo) radioOptions[i]).add((String) a[j]);
				}
				((CCombo) radioOptions[i]).select(0);
				((CCombo) radioOptions[i]).addSelectionListener(new RadioButtonListener());
			} else if (o.isInput()) {
				radioData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
				radioData.horizontalSpan = 1;
				radioData.widthHint = 120;

				// TODO: add condition for if opName = reassign to...
				String assignmentValue = "";
				if (opName.equals(REASSIGN_BUG_TO)) {
					assignmentValue = repository.getUserName();
				}
				radioOptions[i] = toolkit.createText(buttonComposite, assignmentValue);// ,
				// SWT.SINGLE);
				radioOptions[i].setFont(TEXT_FONT);
				radioOptions[i].setLayoutData(radioData);
				// radioOptions[i].setBackground(background);
				((Text) radioOptions[i]).setText(o.getInputValue());
				((Text) radioOptions[i]).addModifyListener(new RadioButtonListener());
			}

			if (i == 0 || o.isChecked()) {
				if (selected != null)
					selected.setSelection(false);
				selected = radios[i];
				radios[i].setSelection(true);
				if (o.hasOptions() && o.getOptionSelection() != null) {
					int j = 0;
					for (String s : ((CCombo) radioOptions[i]).getItems()) {
						if (s.compareTo(o.getOptionSelection()) == 0) {
							((CCombo) radioOptions[i]).select(j);
						}
						j++;
					}
				}
				taskData.setSelectedOperation(o);
			}

			i++;
		}
		toolkit.paintBordersFor(buttonComposite);
	}

	@Override
	protected void addActionButtons(Composite buttonComposite) {
		FormToolkit toolkit = new FormToolkit(buttonComposite.getDisplay());
		super.addActionButtons(buttonComposite);

		compareButton = toolkit.createButton(buttonComposite, LABEL_COMPARE_BUTTON, SWT.NONE);
		// compareButton.setFont(TEXT_FONT);
		GridData compareButtonData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// compareButtonData.widthHint = 100;
		// compareButtonData.heightHint = 20;
		// // compareButton.setText("Compare");
		compareButton.setLayoutData(compareButtonData);
		compareButton.addListener(SWT.Selection, new Listener() {
			public void handleEvent(Event e) {
				OpenCompareEditorJob compareJob = new OpenCompareEditorJob("Comparing bug with remote server...");
				compareJob.schedule();
			}
		});
		compareButton.addListener(SWT.FocusIn, new GenericListener());

		// Button expandAll = toolkit.createButton(buttonComposite,
		// LABEL_EXPAND_ALL_BUTTON, SWT.NONE);
		// expandAll.setLayoutData(new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		// expandAll.addSelectionListener(new SelectionListener() {
		//
		// public void widgetDefaultSelected(SelectionEvent e) {
		// // ignore
		//
		// }
		//
		// public void widgetSelected(SelectionEvent e) {
		// revealAllComments();
		//
		// }
		// });

		// TODO used for spell checking. Add back when we want to support this
		// checkSpellingButton = new Button(buttonComposite, SWT.NONE);
		// checkSpellingButton.setFont(TEXT_FONT);
		// compareButtonData = new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		// compareButtonData.widthHint = 100;
		// compareButtonData.heightHint = 20;
		// checkSpellingButton.setText("CheckSpelling");
		// checkSpellingButton.setLayoutData(compareButtonData);
		// checkSpellingButton.addListener(SWT.Selection, new Listener() {
		// public void handleEvent(Event e) {
		// checkSpelling();
		// }
		// });
		// checkSpellingButton.addListener(SWT.FocusIn, new GenericListener());
	}

	/**
	 * @return Returns the compareInput.
	 */
	public BugzillaCompareInput getCompareInput() {
		return compareInput;
	}

	// @Override
	// public RepositoryTaskData getBug() {
	// return taskData;
	// }

	@Override
	protected String getTitleString() {
		// Attribute summary = bug.getAttribute(ATTR_SUMMARY);
		// String summaryVal = ((null != summary) ? summary.getNewValue() :
		// null);
		return taskData.getLabel();// + ": " + checkText(summaryVal);
	}

	@Override
	public void submitBug() {
		if (isDirty()) {
			this.doSave(new NullProgressMonitor());
		}
		updateBug();
		submitButton.setEnabled(false);
		ExistingBugEditor.this.showBusy(true);
		BugzillaReportSubmitForm bugzillaReportSubmitForm;
		LocalAttachment att = taskData.getNewAttachment();
		if (att != null && !"".equals(att.getFilePath())) {
			att.setComment(attachmentComment.getText());
			att.setDescription(attachmentDesc.getText());
		}

		try {
			bugzillaReportSubmitForm = BugzillaReportSubmitForm.makeExistingBugPost(taskData, repository.getUrl(),
					repository.getUserName(), repository.getPassword(), editorInput.getProxySettings(), removeCC,
					repository.getCharacterEncoding());
		} catch (UnsupportedEncodingException e) {
			// should never get here but just in case...
			MessageDialog.openError(null, "Posting Error", "Ensure proper encoding selected in "
					+ TaskRepositoriesView.NAME + ".");
			return;
		}

		final BugzillaRepositoryConnector bugzillaRepositoryClient = (BugzillaRepositoryConnector) MylarTaskListPlugin
				.getRepositoryManager().getRepositoryConnector(BugzillaPlugin.REPOSITORY_KIND);

		IJobChangeListener closeEditorListener = new IJobChangeListener() {

			public void done(final IJobChangeEvent event) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (event.getJob().getResult().equals(Status.OK_STATUS)) {
							close();
						} else {
							submitButton.setEnabled(true);
							ExistingBugEditor.this.showBusy(false);
						}
					}
				});
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
		bugzillaRepositoryClient.submitBugReport(taskData, bugzillaReportSubmitForm, closeEditorListener);
	}

	@Override
	protected void createCustomAttributeLayout(Composite composite) {
		FormToolkit toolkit = new FormToolkit(composite.getDisplay());
		// Composite customAttributesComposite =
		// toolkit.createComposite(form.getBody());
		// GridLayout attributesLayout = new GridLayout();
		// attributesLayout.numColumns = 4;
		// attributesLayout.horizontalSpacing = 14;
		// attributesLayout.verticalSpacing = 6;
		// composite.setLayout(attributesLayout);
		// GridData attributesData = new GridData(GridData.FILL_BOTH);
		// attributesData.horizontalSpan = 1;
		// attributesData.grabExcessVerticalSpace = false;
		// composite.setLayoutData(attributesData);
		addCCList(toolkit, "", composite);

		// URL field
		// addUrlText(getReport().getAttributeValue(BugzillaReportElement.BUG_FILE_LOC.getKeyString()),
		// customAttributesComposite);

		// keywords text field (not editable)
		try {
			addKeywordsList(toolkit, getRepositoryTaskData().getAttributeValue(RepositoryTaskAttribute.KEYWORDS),
					composite);
		} catch (IOException e) {
			MessageDialog.openInformation(null, "Attribute Display Error",
					"Could not retrieve keyword list, ensure proper configuration in " + TaskRepositoriesView.NAME
							+ "\n\nError reported: " + e.getMessage());
		}

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
			validKeywords = BugzillaPlugin.getRepositoryConfiguration(repository.getUrl(),
					MylarTaskListPlugin.getDefault().getProxySettings(), repository.getUserName(),
					repository.getPassword(), repository.getCharacterEncoding()).getKeywords();
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
		newLayout(attributesComposite, 1, "Add CC:", PROPERTY);
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
				changeDirtyStatus(true);
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
				changeDirtyStatus(true);

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
		taskData.setHasChanged(true);
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
	protected class OpenCompareEditorJob extends Job {

		public OpenCompareEditorJob(String name) {
			super(name);
		}

		@Override
		protected IStatus run(IProgressMonitor monitor) {
			final RepositoryTaskData serverBug;
			try {
				TaskRepository repository = MylarTaskListPlugin.getRepositoryManager().getRepository(
						BugzillaPlugin.REPOSITORY_KIND, taskData.getRepositoryUrl());
				serverBug = BugzillaRepositoryUtil.getBug(repository.getUrl(), repository.getUserName(), repository
						.getPassword(), editorInput.getProxySettings(), repository.getCharacterEncoding(), taskData
						.getId());
				// If no bug was found on the server, throw an exception so that
				// the
				// user gets the same message that appears when there is a
				// problem reading the server.
				if (serverBug == null)
					throw new Exception();
			} catch (Exception e) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
								"Could not open bug.", "Bug #" + taskData.getId()
										+ " could not be read from the server.");
					}
				});
				return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK,
						"Could not get the bug report from the server.", null);
			}
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				public void run() {
					compareInput.setTitle("Bug #" + taskData.getId());
					compareInput.setLeft(taskData);
					compareInput.setRight(serverBug);
					CompareUI.openCompareEditor(compareInput);
				}
			});
			return new Status(IStatus.OK, BugzillaUiPlugin.PLUGIN_ID, IStatus.OK, "", null);
		}

	}

	/**
	 * Class to handle the selection change of the keywords.
	 */
	protected class KeywordListener implements SelectionListener {

		/*
		 * @see org.eclipse.swt.events.SelectionListener#widgetSelected(org.eclipse.swt.events.SelectionEvent)
		 */
		public void widgetSelected(SelectionEvent arg0) {
			changeDirtyStatus(true);

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
		private Comment comment;

		/**
		 * Creates a new <code>CommentListener</code>.
		 * 
		 * @param comment
		 *            The comment that this listener is for.
		 */
		public CommentListener(Comment comment) {
			this.comment = comment;
		}

		public void handleEvent(Event event) {
			fireSelectionChanged(new SelectionChangedEvent(selectionProvider, new StructuredSelection(
					new RepositoryTaskSelection(taskData.getId(), taskData.getRepositoryUrl(), comment.getCreated()
							.toString(), comment, taskData.getSummary()))));
		}
	}

	/**
	 * Class to handle the selection change of the radio buttons.
	 */
	protected class RadioButtonListener implements SelectionListener, ModifyListener {

		public void widgetDefaultSelected(SelectionEvent e) {
			widgetSelected(e);
		}

		public void widgetSelected(SelectionEvent e) {
			Button selected = null;
			for (int i = 0; i < radios.length; i++) {
				if (radios[i].getSelection())
					selected = radios[i];
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					taskData.setSelectedOperation(o);
					ExistingBugEditor.this.changeDirtyStatus(true);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					o.setOptionSelection(((CCombo) radioOptions[i]).getItem(((CCombo) radioOptions[i])
							.getSelectionIndex()));

					if (taskData.getSelectedOperation() != null)
						taskData.getSelectedOperation().setChecked(false);
					o.setChecked(true);

					taskData.setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					ExistingBugEditor.this.changeDirtyStatus(true);
				}
			}
			validateInput();
		}

		public void modifyText(ModifyEvent e) {
			Button selected = null;
			for (int i = 0; i < radios.length; i++) {
				if (radios[i].getSelection())
					selected = radios[i];
			}
			// determine the operation to do to the bug
			for (int i = 0; i < radios.length; i++) {
				if (radios[i] != e.widget && radios[i] != selected) {
					radios[i].setSelection(false);
				}

				if (e.widget == radios[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					taskData.setSelectedOperation(o);
					ExistingBugEditor.this.changeDirtyStatus(true);
				} else if (e.widget == radioOptions[i]) {
					RepositoryOperation o = taskData.getOperation(radios[i].getText());
					o.setInputValue(((Text) radioOptions[i]).getText());

					if (taskData.getSelectedOperation() != null)
						taskData.getSelectedOperation().setChecked(false);
					o.setChecked(true);

					taskData.setSelectedOperation(o);
					radios[i].setSelection(true);
					if (selected != null && selected != radios[i]) {
						selected.setSelection(false);
					}
					ExistingBugEditor.this.changeDirtyStatus(true);
				}
			}
			validateInput();
		}
	}

	protected void validateInput() {
		RepositoryOperation o = taskData.getSelectedOperation();
		if (o != null && o.getKnobName().compareTo("resolve") == 0
				&& (addCommentsText.getText() == null || addCommentsText.getText().equals(""))) {
			// TODO: Highlight (change to light red?) New Comment area to
			// indicate need for message
			submitButton.setEnabled(false);
		} else {
			submitButton.setEnabled(true);
		}
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
					changeDirtyStatus(true);
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

	// @Override
	// public AbstractRepositoryTask getRepositoryTask() {
	// // ignore
	// return null;
	// }

	// @Override
	// public void handleSummaryEvent() {
	// String sel = summaryText.getText();
	// RepositoryTaskAttribute a =
	// getReport().getAttribute(BugzillaReportElement.SHORT_DESC.getKeyString());
	// if (!(a.getValue().equals(sel))) {
	// a.setValue(sel);
	// changeDirtyStatus(true);
	// }
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
