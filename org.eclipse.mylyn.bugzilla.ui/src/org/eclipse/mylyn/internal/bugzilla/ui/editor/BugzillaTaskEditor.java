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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylar.tasks.core.ITask;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskComment;
import org.eclipse.mylar.tasks.ui.DatePicker;
import org.eclipse.mylar.tasks.ui.TasksUiPlugin;
import org.eclipse.mylar.tasks.ui.TasksUiUtil;
import org.eclipse.mylar.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylar.tasks.ui.editors.RepositoryTaskSelection;
import org.eclipse.mylar.tasks.ui.editors.TaskEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;

/**
 * An editor used to view a bug report that exists on a server. It uses a
 * <code>BugReport</code> object to store the data.
 * 
 * @author Mik Kersten (hardening of prototype)
 * @author Rob Elves
 * @author Jeff Pound (Attachment work)
 */
public class BugzillaTaskEditor extends AbstractRepositoryTaskEditor {

	private static final String LABEL_TIME_TRACKING = "Bugzilla Time Tracking";

	// protected BugzillaCompareInput compareInput;

	// protected Button compareButton;

	protected List keyWordsList;

	protected Text keywordsText;

	protected Text urlText;

	protected Text estimateText;

	protected Text actualText;

	protected Text remainingText;

	protected Text addTimeText;

	protected Text deadlineText;

	protected DatePicker deadlinePicker;

	protected Text votesText;

	/**
	 * Creates a new <code>ExistingBugEditor</code>.
	 */
	public BugzillaTaskEditor(FormEditor editor) {
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
	protected void createCustomAttributeLayout(Composite composite) {
		// FormToolkit toolkit = getManagedForm().getToolkit();

		RepositoryTaskAttribute attribute = this.taskData.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label;
			if (hasOutgoingChange(attribute)) {
				label = toolkit.createLabel(composite, "*" + BugzillaReportElement.DEPENDSON.toString());
			} else {
				label = toolkit.createLabel(composite, BugzillaReportElement.DEPENDSON.toString());
			}

			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = toolkit.createComposite(composite);
			GridLayout textLayout = new GridLayout();
			textLayout.marginWidth = 1;
			textLayout.marginHeight = 3;
			textLayout.verticalSpacing = 3;
			textFieldComposite.setLayout(textLayout);
			GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			textData.horizontalSpan = 1;
			textData.widthHint = 135;

			final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
			text.setLayoutData(textData);
			toolkit.paintBordersFor(textFieldComposite);
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.BLOCKED.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label;
			if (hasOutgoingChange(attribute)) {
				label = toolkit.createLabel(composite, "*" + BugzillaReportElement.BLOCKED.toString());
			} else {
				label = toolkit.createLabel(composite, BugzillaReportElement.BLOCKED.toString());
			}
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = toolkit.createComposite(composite);
			GridLayout textLayout = new GridLayout();
			textLayout.marginWidth = 1;
			textLayout.marginHeight = 3;
			textLayout.verticalSpacing = 3;
			textFieldComposite.setLayout(textLayout);
			GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			textData.horizontalSpan = 1;
			textData.widthHint = 135;
			final Text text = createTextField(textFieldComposite, attribute, SWT.FLAT);
			text.setLayoutData(textData);
			toolkit.paintBordersFor(textFieldComposite);
		}

		String dependson = taskData.getAttributeValue(BugzillaReportElement.DEPENDSON.getKeyString());
		String blocked = taskData.getAttributeValue(BugzillaReportElement.BLOCKED.getKeyString());
		boolean addHyperlinks = (dependson != null && dependson.length() > 0)
				|| (blocked != null && blocked.length() > 0);

		// Hyperlink showDependencyTree = toolkit.createHyperlink(composite,
		// "Show dependency tree", SWT.NONE);
		// showDependencyTree.addHyperlinkListener(new HyperlinkAdapter() {
		// public void linkActivated(HyperlinkEvent e) {
		// if (ExistingBugEditor.this.getEditor() instanceof TaskEditor) {
		// TaskEditor mylarTaskEditor = (TaskEditor)
		// ExistingBugEditor.this.getEditor();
		// mylarTaskEditor.displayInBrowser(repository.getUrl() +
		// IBugzillaConstants.DEPENDENCY_TREE_URL
		// + taskData.getId());
		// }
		// }
		// });

		if (addHyperlinks) {
			toolkit.createLabel(composite, "");
			addBugHyperlinks(composite, BugzillaReportElement.DEPENDSON.getKeyString());
		}

		// Hyperlink showDependencyGraph = toolkit.createHyperlink(composite,
		// "Show dependency graph", SWT.NONE);
		// showDependencyGraph.addHyperlinkListener(new HyperlinkAdapter() {
		// public void linkActivated(HyperlinkEvent e) {
		// if (ExistingBugEditor.this.getEditor() instanceof TaskEditor) {
		// TaskEditor mylarTaskEditor = (TaskEditor)
		// ExistingBugEditor.this.getEditor();
		// mylarTaskEditor.displayInBrowser(repository.getUrl() +
		// IBugzillaConstants.DEPENDENCY_GRAPH_URL
		// + taskData.getId());
		// }
		// }
		// });

		if (addHyperlinks) {
			toolkit.createLabel(composite, "");
			addBugHyperlinks(composite, BugzillaReportElement.BLOCKED.getKeyString());
		}

		try {
			addKeywordsList(composite);
		} catch (IOException e) {
			MessageDialog.openInformation(null, "Attribute Display Error",
					"Could not retrieve keyword list, ensure proper configuration in "
							+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + "\n\nError reported: " + e.getMessage());
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label;
			if (hasOutgoingChange(attribute)) {
				label = toolkit.createLabel(composite, "*" + BugzillaReportElement.BUG_FILE_LOC.toString());
			} else {
				label = toolkit.createLabel(composite, BugzillaReportElement.BUG_FILE_LOC.toString());
			}
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text urlField = createTextField(composite, attribute, SWT.FLAT);
			GridDataFactory.fillDefaults().hint(135, SWT.DEFAULT).applyTo(urlField);
		}

		addVoting(composite);

		// If groups is available add roles
		if (taskData.getAttribute(BugzillaReportElement.GROUP.getKeyString()) != null) {
			addRoles(composite);
		}

		if (taskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()) != null)
			addBugzillaTimeTracker(toolkit, composite);

	}

	private boolean hasCustomAttributeChanges() {
		if (taskData == null)
			return false;
		String customAttributeKeys[] = { BugzillaReportElement.BUG_FILE_LOC.getKeyString(),
				BugzillaReportElement.DEPENDSON.getKeyString(), BugzillaReportElement.BLOCKED.getKeyString(),
				BugzillaReportElement.KEYWORDS.getKeyString(), BugzillaReportElement.VOTES.getKeyString(),
				BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString(),
				BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString(),
				BugzillaReportElement.ESTIMATED_TIME.getKeyString(),
				BugzillaReportElement.REMAINING_TIME.getKeyString(), BugzillaReportElement.ACTUAL_TIME.getKeyString(),
				BugzillaReportElement.DEADLINE.getKeyString() };
		for (String key : customAttributeKeys) {
			RepositoryTaskAttribute attribute = taskData.getAttribute(key);
			if (hasChanged(attribute)) {
				return true;
			}
		}
		return false;
	}

	@Override
	protected boolean hasVisibleAttributeChanges() {
		return super.hasVisibleAttributeChanges() || this.hasCustomAttributeChanges();

	}

	private void addBugHyperlinks(Composite composite, String key) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Composite hyperlinksComposite = toolkit.createComposite(composite);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginBottom = 0;
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginTop = 0;
		rowLayout.spacing = 0;
		hyperlinksComposite.setLayout(new RowLayout());
		String values = taskData.getAttributeValue(key);

		if (values != null && values.length() > 0) {
			for (String bugNumber : values.split(",")) {
				final String bugId = bugNumber.trim();
				Hyperlink hyperlink = toolkit.createHyperlink(hyperlinksComposite, bugId, SWT.NONE);
				final ITask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(), bugId);
				if (task != null) {
					hyperlink.setToolTipText(task.getSummary());
				}
				hyperlink.addHyperlinkListener(new HyperlinkAdapter() {
					@Override
					public void linkActivated(HyperlinkEvent e) {
						if (task != null) {
							TasksUiUtil.refreshAndOpenTaskListElement(task);
						} else {
							TasksUiUtil.openRepositoryTask(repository.getUrl(), bugId, repository.getUrl()
									+ IBugzillaConstants.URL_GET_SHOW_BUG + bugId);
						}
					}
				});
			}
		}
	}

	protected void addRoles(Composite parent) {
		Section rolesSection = toolkit.createSection(parent, ExpandableComposite.SHORT_TITLE_BAR);
		rolesSection.setText("Users in the roles selected below can always view this bug");
		rolesSection
				.setDescription("(The assignee can always see a bug, and this section does not take effect unless the bug is restricted to at least one group.)");
		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		rolesSection.setLayout(gl);
		rolesSection.setLayoutData(gd);

		Composite rolesComposite = toolkit.createComposite(rolesSection);
		GridLayout attributesLayout = new GridLayout();
		attributesLayout.numColumns = 4;
		attributesLayout.horizontalSpacing = 5;
		attributesLayout.verticalSpacing = 4;
		rolesComposite.setLayout(attributesLayout);
		GridData attributesData = new GridData(GridData.FILL_BOTH);
		attributesData.horizontalSpan = 1;
		attributesData.grabExcessVerticalSpace = false;
		rolesComposite.setLayoutData(attributesData);
		rolesSection.setClient(rolesComposite);

		RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.REPORTER_ACCESSIBLE
				.getKeyString());
		if (attribute == null) {
			taskData.setAttributeValue(BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString(), "0");
			attribute = taskData.getAttribute(BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString());
		}
		Button button = addButtonField(rolesComposite, attribute, SWT.CHECK);
		if (hasChanged(attribute)) {
			button.setBackground(backgroundIncoming);
		}

		attribute = null;
		attribute = taskData.getAttribute(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString());
		if (attribute == null) {
			taskData.setAttributeValue(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString(), "0");
			attribute = taskData.getAttribute(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString());
		}
		button = addButtonField(rolesComposite, attribute, SWT.CHECK);
		if (hasChanged(attribute)) {
			button.setBackground(backgroundIncoming);
		}
	}

	private Button addButtonField(Composite rolesComposite, RepositoryTaskAttribute attribute, int style) {
		if (attribute == null) {
			return null;
		}
		String name = attribute.getName();
		if (hasOutgoingChange(attribute)) {
			name += "*";
		}

		final Button button = toolkit.createButton(rolesComposite, name, style);
		if (!attribute.isReadOnly()) {
			button.setData(attribute);
			button.setSelection(attribute.getValue().equals("1"));
			button.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			button.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					String sel = "1";
					if (!button.getSelection()) {
						sel = "0";
					}
					RepositoryTaskAttribute a = (RepositoryTaskAttribute) button.getData();
					a.setValue(sel);
					attributeChanged(a);
				}
			});
		}
		return button;
	}

	protected void addBugzillaTimeTracker(FormToolkit toolkit, Composite parent) {

		Section timeSection = toolkit.createSection(parent, ExpandableComposite.SHORT_TITLE_BAR);
		timeSection.setText(LABEL_TIME_TRACKING);
		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		timeSection.setLayout(gl);
		timeSection.setLayoutData(gd);

		Composite timeComposite = toolkit.createComposite(timeSection);
		gl = new GridLayout(4, true);
		timeComposite.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 5;
		timeComposite.setLayoutData(gd);

		RepositoryTaskAttribute attribute = this.taskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME
				.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			createLabel(timeComposite, attribute);
			estimateText = createTextField(timeComposite, attribute, SWT.FLAT);
			estimateText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}

		toolkit.createLabel(timeComposite, "Current Estimate:");
		Text currentEstimate = toolkit.createText(timeComposite,
				""
						+ (Float.parseFloat(taskData
								.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())) + Float
								.parseFloat(taskData.getAttributeValue(BugzillaReportElement.REMAINING_TIME
										.getKeyString()))));
		currentEstimate.setFont(TEXT_FONT);
		currentEstimate.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		currentEstimate.setEditable(false);

		attribute = this.taskData.getAttribute(BugzillaReportElement.ACTUAL_TIME.getKeyString());
		if (attribute != null) {

			createLabel(timeComposite, attribute);
			Text actualText = createTextField(timeComposite, attribute, SWT.FLAT);
			actualText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			actualText.setEditable(false);
		}

		// Add Time
		taskData.setAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString(), "0");
		final RepositoryTaskAttribute addTimeAttribute = this.taskData.getAttribute(BugzillaReportElement.WORK_TIME
				.getKeyString());
		if (addTimeAttribute != null) {

			createLabel(timeComposite, addTimeAttribute);
			addTimeText = toolkit.createText(timeComposite, taskData.getAttributeValue(BugzillaReportElement.WORK_TIME
					.getKeyString()), SWT.BORDER);
			addTimeText.setFont(TEXT_FONT);
			addTimeText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
			addTimeText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					addTimeAttribute.setValue(addTimeText.getText());
					attributeChanged(addTimeAttribute);
				}
			});
		}
		attribute = this.taskData.getAttribute(BugzillaReportElement.REMAINING_TIME.getKeyString());
		if (attribute != null) {
			createLabel(timeComposite, attribute);
			createTextField(timeComposite, attribute, SWT.FLAT);
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString());
		if (attribute != null) {
			createLabel(timeComposite, attribute);

			deadlinePicker = new DatePicker(timeComposite, /* SWT.NONE */SWT.BORDER, taskData
					.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
			deadlinePicker.setFont(TEXT_FONT);
			deadlinePicker.setDatePattern("yyyy-MM-dd");
			if (hasChanged(attribute)) {
				deadlinePicker.setBackground(backgroundIncoming);
			}
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
						attributeChanged(taskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()));
						// TODO goes dirty even if user presses cancel
						// markDirty(true);
					}
				}
			});
		}

		timeSection.setClient(timeComposite);
	}

	protected void addKeywordsList(Composite attributesComposite) throws IOException {
		// newLayout(attributesComposite, 1, "Keywords:", PROPERTY);
		RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.KEYWORDS);
		if (attribute == null)
			return;
		String keywords = attribute.getValue();
		FormToolkit toolkit = getManagedForm().getToolkit();
		Label label = toolkit.createLabel(attributesComposite, "Keywords:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);

		// toolkit.createText(attributesComposite, keywords)
		keywordsText = createTextField(attributesComposite, attribute, SWT.FLAT);
		keywordsText.setFont(TEXT_FONT);
		keywordsText.setEditable(false);
		// keywordsText.setForeground(foreground);
		// keywordsText.setBackground(JFaceColors.getErrorBackground(display));
		GridData keywordsData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		keywordsData.horizontalSpan = 2;
		keywordsData.widthHint = 200;
		keywordsText.setLayoutData(keywordsData);
		// keywordsText.setText(keywords);
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
			validKeywords = BugzillaCorePlugin.getRepositoryConfiguration(repository, false).getKeywords();
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
	}

	protected void addVoting(Composite attributesComposite) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Label label = toolkit.createLabel(attributesComposite, "Votes:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Composite votingComposite = toolkit.createComposite(attributesComposite);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		votingComposite.setLayout(layout);
		// GridDataFactory.fillDefaults().span(2, 1).applyTo(votingComposite);
		RepositoryTaskAttribute votesAttribute = taskData.getAttribute(BugzillaReportElement.VOTES.getKeyString());
		// String voteValue = votesAttribute != null ? votesAttribute.getValue()
		// : "0";
		votesText = createTextField(votingComposite, votesAttribute, SWT.FLAT);
		votesText.setFont(TEXT_FONT);

		if (votesAttribute != null && hasChanged(votesAttribute)) {
			IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
			Color backgroundIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
					TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);
			keywordsText.setBackground(backgroundIncoming);
		}
		votesText.setEditable(false);

		Hyperlink showVotesHyperlink = toolkit.createHyperlink(votingComposite, "Show votes for this bug", SWT.NONE);
		showVotesHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (BugzillaTaskEditor.this.getEditor() instanceof TaskEditor) {
					TaskEditor mylarTaskEditor = (TaskEditor) BugzillaTaskEditor.this.getEditor();
					mylarTaskEditor.displayInBrowser(repository.getUrl() + IBugzillaConstants.URL_SHOW_VOTES
							+ taskData.getId());
				}
			}
		});

		Hyperlink voteHyperlink = toolkit.createHyperlink(votingComposite, "Vote for this bug", SWT.NONE);
		voteHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (BugzillaTaskEditor.this.getEditor() instanceof TaskEditor) {
					TaskEditor mylarTaskEditor = (TaskEditor) BugzillaTaskEditor.this.getEditor();
					mylarTaskEditor.displayInBrowser(repository.getUrl() + IBugzillaConstants.URL_VOTE
							+ taskData.getId());
				}
			}
		});
	}

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
				if (taskData.getAttributeValue(BugzillaReportElement.KEYWORDS.getKeyString()).equals(keyword))
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
				RepositoryTaskAttribute a = taskData.getAttribute(BugzillaReportElement.BUG_FILE_LOC.getKeyString());
				if (!(a.getValue().equals(sel))) {
					a.setValue(sel);
					markDirty(true);
				}
			}
		});
	}

	protected String getActivityUrl() {
		return repository.getUrl() + IBugzillaConstants.URL_BUG_ACTIVITY + taskData.getId();
	}

}