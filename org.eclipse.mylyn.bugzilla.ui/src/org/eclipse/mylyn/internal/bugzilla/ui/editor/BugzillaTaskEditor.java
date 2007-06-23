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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaReportElement;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants.BUGZILLA_OPERATION;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;
import org.eclipse.mylyn.internal.tasks.ui.TaskListColorsAndFonts;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiImages;
import org.eclipse.mylyn.tasks.core.AbstractTask;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.AbstractDuplicateDetector;
import org.eclipse.mylyn.tasks.ui.DatePicker;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.TasksUiUtil;
import org.eclipse.mylyn.tasks.ui.editors.AbstractRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.editors.TaskEditor;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.themes.IThemeManager;

/**
 * An editor used to view a bug report that exists on a server. It uses a <code>BugReport</code> object to store the
 * data.
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

		RepositoryTaskAttribute attribute = this.taskData.getAttribute(BugzillaReportElement.DEPENDSON.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = getManagedForm().getToolkit().createComposite(composite);
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
			getManagedForm().getToolkit().paintBordersFor(textFieldComposite);
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.BLOCKED.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Composite textFieldComposite = getManagedForm().getToolkit().createComposite(composite);
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
			getManagedForm().getToolkit().paintBordersFor(textFieldComposite);
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
			getManagedForm().getToolkit().createLabel(composite, "");
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
			getManagedForm().getToolkit().createLabel(composite, "");
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
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text urlField = createTextField(composite, attribute, SWT.FLAT);
			GridDataFactory.fillDefaults().hint(135, SWT.DEFAULT).applyTo(urlField);
		}

		attribute = this.taskData.getAttribute(BugzillaReportElement.STATUS_WHITEBOARD.getKeyString());
		if (attribute == null) {
			this.taskData.setAttributeValue(BugzillaReportElement.STATUS_WHITEBOARD.getKeyString(), "");
			attribute = this.taskData.getAttribute(BugzillaReportElement.STATUS_WHITEBOARD.getKeyString());
		}
		if (attribute != null && !attribute.isReadOnly()) {
			Label label = createLabel(composite, attribute);
			GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
			Text whiteboardField = createTextField(composite, attribute, SWT.FLAT);
			GridDataFactory.fillDefaults().hint(135, SWT.DEFAULT).applyTo(whiteboardField);
		}

		addVoting(composite);

		// If groups is available add roles
		if (taskData.getAttribute(BugzillaReportElement.GROUP.getKeyString()) != null) {
			addRoles(composite);
		}

		if (taskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString()) != null)
			addBugzillaTimeTracker(getManagedForm().getToolkit(), composite);

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
				BugzillaReportElement.DEADLINE.getKeyString(), BugzillaReportElement.STATUS_WHITEBOARD.getKeyString() };
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
		Composite hyperlinksComposite = getManagedForm().getToolkit().createComposite(composite);
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
				Hyperlink hyperlink = getManagedForm().getToolkit().createHyperlink(hyperlinksComposite, bugId,
						SWT.NONE);
				final AbstractTask task = TasksUiPlugin.getTaskListManager().getTaskList().getTask(repository.getUrl(),
						bugId);
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
		Section rolesSection = getManagedForm().getToolkit().createSection(parent, ExpandableComposite.SHORT_TITLE_BAR);
		rolesSection.setText("Users in the roles selected below can always view this bug");
		rolesSection.setDescription("(The assignee can always see a bug, and this section does not take effect unless the bug is restricted to at least one group.)");
		GridLayout gl = new GridLayout();
		GridData gd = new GridData(SWT.FILL, SWT.NONE, false, false);
		gd.horizontalSpan = 4;
		rolesSection.setLayout(gl);
		rolesSection.setLayoutData(gd);

		Composite rolesComposite = getManagedForm().getToolkit().createComposite(rolesSection);
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

		RepositoryTaskAttribute attribute = taskData.getAttribute(BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString());
		if (attribute == null) {
			taskData.setAttributeValue(BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString(), "0");
			attribute = taskData.getAttribute(BugzillaReportElement.REPORTER_ACCESSIBLE.getKeyString());
		}
		Button button = addButtonField(rolesComposite, attribute, SWT.CHECK);
		if (hasChanged(attribute)) {
			button.setBackground(getColorIncoming());
		}

		attribute = null;
		attribute = taskData.getAttribute(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString());
		if (attribute == null) {
			taskData.setAttributeValue(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString(), "0");
			attribute = taskData.getAttribute(BugzillaReportElement.CCLIST_ACCESSIBLE.getKeyString());
		}
		button = addButtonField(rolesComposite, attribute, SWT.CHECK);
		if (hasChanged(attribute)) {
			button.setBackground(getColorIncoming());
		}
	}

	@Override
	protected boolean hasContentAssist(RepositoryTaskAttribute attribute) {
		return BugzillaReportElement.NEWCC.getKeyString().equals(attribute.getId());
	}

	@Override
	protected boolean hasContentAssist(RepositoryOperation repositoryOperation) {
		BUGZILLA_OPERATION operation;
		try {
			operation = BUGZILLA_OPERATION.valueOf(repositoryOperation.getKnobName());
		} catch (RuntimeException e) {
			StatusManager.log(e, "Unrecognized operation: " + repositoryOperation.getKnobName());
			operation = null;
		}

		if (operation != null && operation == BUGZILLA_OPERATION.reassign) {
			return true;
		} else {
			return false;
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

		final Button button = getManagedForm().getToolkit().createButton(rolesComposite, name, style);
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
		gl = new GridLayout(4, false);
		timeComposite.setLayout(gl);
		gd = new GridData();
		gd.horizontalSpan = 5;
		timeComposite.setLayoutData(gd);

		RepositoryTaskAttribute attribute = this.taskData.getAttribute(BugzillaReportElement.ESTIMATED_TIME.getKeyString());
		if (attribute != null && !attribute.isReadOnly()) {
			createLabel(timeComposite, attribute);
			estimateText = createTextField(timeComposite, attribute, SWT.FLAT);
			estimateText.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		}

		Label label = toolkit.createLabel(timeComposite, "Current Estimate:");
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));

		Text currentEstimate = toolkit.createText(
				timeComposite,
				""
						+ (Float.parseFloat(taskData.getAttributeValue(BugzillaReportElement.ACTUAL_TIME.getKeyString())) + Float.parseFloat(taskData.getAttributeValue(BugzillaReportElement.REMAINING_TIME.getKeyString()))));
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
		final RepositoryTaskAttribute addTimeAttribute = this.taskData.getAttribute(BugzillaReportElement.WORK_TIME.getKeyString());
		if (addTimeAttribute != null) {

			createLabel(timeComposite, addTimeAttribute);
			addTimeText = toolkit.createText(timeComposite,
					taskData.getAttributeValue(BugzillaReportElement.WORK_TIME.getKeyString()), SWT.BORDER);
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

			Composite dateWithClear = toolkit.createComposite(timeComposite);
			GridLayout layout = new GridLayout(2, false);
			layout.marginWidth = 1;
			dateWithClear.setLayout(layout);

			deadlinePicker = new DatePicker(dateWithClear, /* SWT.NONE */SWT.BORDER,
					taskData.getAttributeValue(BugzillaReportElement.DEADLINE.getKeyString()));
			deadlinePicker.setFont(TEXT_FONT);
			deadlinePicker.setDatePattern("yyyy-MM-dd");
			if (hasChanged(attribute)) {
				deadlinePicker.setBackground(getColorIncoming());
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
					} else {
						taskData.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), "");
						attributeChanged(taskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()));
						deadlinePicker.setDate(null);
					}
				}
			});

			ImageHyperlink clearDeadlineDate = toolkit.createImageHyperlink(dateWithClear, SWT.NONE);
			clearDeadlineDate.setImage(TasksUiImages.getImage(TasksUiImages.REMOVE));
			clearDeadlineDate.setToolTipText("Clear");
			clearDeadlineDate.addHyperlinkListener(new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					taskData.setAttributeValue(BugzillaReportElement.DEADLINE.getKeyString(), "");
					attributeChanged(taskData.getAttribute(BugzillaReportElement.DEADLINE.getKeyString()));
					deadlinePicker.setDate(null);
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
		Label label = createLabel(attributesComposite, attribute);
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);

		// toolkit.createText(attributesComposite, keywords)
		keywordsText = createTextField(attributesComposite, attribute, SWT.FLAT | SWT.READ_ONLY);
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
		Label label = getManagedForm().getToolkit().createLabel(attributesComposite, "Votes:");
		label.setForeground(getManagedForm().getToolkit().getColors().getColor(IFormColors.TITLE));
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Composite votingComposite = getManagedForm().getToolkit().createComposite(attributesComposite);
		GridLayout layout = new GridLayout(3, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		votingComposite.setLayout(layout);

		RepositoryTaskAttribute votesAttribute = taskData.getAttribute(BugzillaReportElement.VOTES.getKeyString());

		votesText = createTextField(votingComposite, votesAttribute, SWT.FLAT | SWT.READ_ONLY);
		votesText.setFont(TEXT_FONT);

		if (votesAttribute != null && hasChanged(votesAttribute)) {
			IThemeManager themeManager = getSite().getWorkbenchWindow().getWorkbench().getThemeManager();
			Color backgroundIncoming = themeManager.getCurrentTheme().getColorRegistry().get(
					TaskListColorsAndFonts.THEME_COLOR_TASKLIST_CATEGORY);
			votesText.setBackground(backgroundIncoming);
		}
		votesText.setEditable(false);

		Hyperlink showVotesHyperlink = getManagedForm().getToolkit().createHyperlink(votingComposite, "Show votes",
				SWT.NONE);
		showVotesHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (BugzillaTaskEditor.this.getEditor() instanceof TaskEditor) {
//					TaskEditor mylarTaskEditor = (TaskEditor) BugzillaTaskEditor.this.getEditor();
					TasksUiUtil.openUrl(repository.getUrl() + IBugzillaConstants.URL_SHOW_VOTES + taskData.getId(),
							false);
				}
			}
		});

		Hyperlink voteHyperlink = getManagedForm().getToolkit().createHyperlink(votingComposite, "Vote", SWT.NONE);
		voteHyperlink.addHyperlinkListener(new HyperlinkAdapter() {
			@Override
			public void linkActivated(HyperlinkEvent e) {
				if (BugzillaTaskEditor.this.getEditor() instanceof TaskEditor) {
					TasksUiUtil.openUrl(repository.getUrl() + IBugzillaConstants.URL_VOTE + taskData.getId(), false);
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

	@Override
	protected void validateInput() {

	}

	protected String getHistoryUrl() {
		if (repository != null && taskData != null) {
			return repository.getUrl() + IBugzillaConstants.URL_BUG_ACTIVITY + taskData.getId();
		} else {
			return null;
		}
	}
	
	@Override
	/**
	 * This method is duplicated in NewBugzillaTaskEditor for now.
	 */
	public SearchHitCollector getDuplicateSearchCollector(String name) {
		String duplicateDetectorName = name.equals("default") ? "Stack Trace" : name;
		Set<AbstractDuplicateDetector> allDetectors = getDuplicateSearchCollectorsList();

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
	 * This method is duplicated in NewBugzillaTaskEditor for now.
	 */
	protected Set<AbstractDuplicateDetector> getDuplicateSearchCollectorsList() {
		return TasksUiPlugin.getDefault().getDuplicateSearchCollectorsList();
	}
}