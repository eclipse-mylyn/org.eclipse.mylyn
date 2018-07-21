/*******************************************************************************
 * Copyright (c) 2004, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.fieldassist.FieldDecoration;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.ui.dialogs.AbstractInPlaceDialog;
import org.eclipse.mylyn.commons.ui.dialogs.IInPlaceDialogListener;
import org.eclipse.mylyn.commons.ui.dialogs.InPlaceDialogEvent;
import org.eclipse.mylyn.commons.workbench.InPlaceCheckBoxTreeDialog;
import org.eclipse.mylyn.commons.workbench.WorkbenchUtil;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCustomField;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryConnector;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaSearch;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaSearch.Entry;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Bugzilla search page
 * 
 * @author Mik Kersten (hardening of prototype)
 * @author Frank Becker
 */
public class BugzillaSearchPage extends AbstractRepositoryQueryPage2 implements Listener {

	private static final int HEIGHT_ATTRIBUTE_COMBO = 30;

	private static ArrayList<BugzillaSearchData> previousSummaryPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousEmailPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousEmailPatterns2 = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousCommentPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousKeywords = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousWhiteboardPatterns = new ArrayList<BugzillaSearchData>(20);

	private boolean firstTime = true;

	private IDialogSettings fDialogSettings;

	private static final String[] patternOperationText = { Messages.BugzillaSearchPage_OperationText_allwordssubstr,
			Messages.BugzillaSearchPage_OperationText_anywordssubstr,
			Messages.BugzillaSearchPage_OperationText_substring,
			Messages.BugzillaSearchPage_OperationText_casesubstring,
			Messages.BugzillaSearchPage_OperationText_allwords, Messages.BugzillaSearchPage_OperationText_anywords,
			Messages.BugzillaSearchPage_OperationText_regexp, Messages.BugzillaSearchPage_OperationText_notregexp };

	private static final String[] patternOperationValues = {
			"allwordssubstr", "anywordssubstr", "substring", "casesubstring", "allwords", "anywords", "regexp", "notregexp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$

	private static final String[] emailOperationText = { Messages.BugzillaSearchPage_EmailOperation_substring,
			Messages.BugzillaSearchPage_EmailOperation_exact, Messages.BugzillaSearchPage_EmailOperation_notequals,
			Messages.BugzillaSearchPage_EmailOperation_regexp, Messages.BugzillaSearchPage_EmailOperation_notregexp };

	private static final String[] emailOperationValues = { "substring", "exact", "notequals", "regexp", "notregexp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$

	private static final String[] keywordOperationText = { Messages.BugzillaSearchPage_all,
			Messages.BugzillaSearchPage_any, Messages.BugzillaSearchPage_none };

	private static final String[] keywordOperationValues = { "allwords", "anywords", "nowords" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

	private static final String[] emailRoleValues = { "emailassigned_to1", "emailreporter1", "emailcc1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			"emaillongdesc1", "emailqa_contact1" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final String[] emailRoleValues2 = { "emailassigned_to2", "emailreporter2", "emailcc2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ 
			"emaillongdesc2", "emailqa_contact2" }; //$NON-NLS-1$ //$NON-NLS-2$

	private static final ArrayList<String> chartFieldTextDefault = new ArrayList<String>() {
		private static final long serialVersionUID = 1974092160992399001L;
		{
			add(Messages.BugzillaSearchPage_Field_Noop);
			add(Messages.BugzillaSearchPage_Field_Alias);
			add(Messages.BugzillaSearchPage_Field_AssignedTo);
			add(Messages.BugzillaSearchPage_Field_Attachment_creator);
			add(Messages.BugzillaSearchPage_Field_Attachment_data);
			add(Messages.BugzillaSearchPage_Field_Attachment_description);
			add(Messages.BugzillaSearchPage_Field_Attachment_filename);
			add(Messages.BugzillaSearchPage_Field_Attachment_is_a_URL);
			add(Messages.BugzillaSearchPage_Field_Attachment_is_obsolete);
			add(Messages.BugzillaSearchPage_Field_Attachment_is_patch);
			add(Messages.BugzillaSearchPage_Field_Attachment_is_private);
			add(Messages.BugzillaSearchPage_Field_Attachment_mime_type);
			add(Messages.BugzillaSearchPage_Field_Blocks);
			add(Messages.BugzillaSearchPage_Field_Bug);
			add(Messages.BugzillaSearchPage_Field_CC);
			add(Messages.BugzillaSearchPage_Field_CC_Accessible);
			add(Messages.BugzillaSearchPage_Field_Classification);
			add(Messages.BugzillaSearchPage_Field_Comment);
			add(Messages.BugzillaSearchPage_Field_Comment_is_private);
			add(Messages.BugzillaSearchPage_Field_Commenter);
			add(Messages.BugzillaSearchPage_Field_Component);
			add(Messages.BugzillaSearchPage_Field_Content);
			add(Messages.BugzillaSearchPage_Field_Creation_date);
			add(Messages.BugzillaSearchPage_Field_Days_since_bug_changed);
			add(Messages.BugzillaSearchPage_Field_Depends_on);
			add(Messages.BugzillaSearchPage_Field_drop_down_custom_field);
			add(Messages.BugzillaSearchPage_Field_Ever_Confirmed);
			add(Messages.BugzillaSearchPage_Field_Flag);
			add(Messages.BugzillaSearchPage_Field_Flag_Requestee);
			add(Messages.BugzillaSearchPage_Field_Flag_Setter);
			add(Messages.BugzillaSearchPage_Field_free_text_custom_field);
			add(Messages.BugzillaSearchPage_Field_Group);
			add(Messages.BugzillaSearchPage_Field_Keywords);
			add(Messages.BugzillaSearchPage_Field_Last_changed_date);
			add(Messages.BugzillaSearchPage_Field_OS_Version);
			add(Messages.BugzillaSearchPage_Field_Platform);
			add(Messages.BugzillaSearchPage_Field_Priority);
			add(Messages.BugzillaSearchPage_Field_Product);
			add(Messages.BugzillaSearchPage_Field_QAContact);
			add(Messages.BugzillaSearchPage_Field_ReportedBy);
			add(Messages.BugzillaSearchPage_Field_Reporter_Accessible);
			add(Messages.BugzillaSearchPage_Field_Resolution);
			add(Messages.BugzillaSearchPage_Field_Severity);
			add(Messages.BugzillaSearchPage_Field_Status);
			add(Messages.BugzillaSearchPage_Field_Status_Whiteboard);
			add(Messages.BugzillaSearchPage_Field_Summary);
			add(Messages.BugzillaSearchPage_Field_Target_Milestone);
			add(Messages.BugzillaSearchPage_Field_Time_Since_Assignee_Touched);
			add(Messages.BugzillaSearchPage_Field_URL);
			add(Messages.BugzillaSearchPage_Field_Version);
			add(Messages.BugzillaSearchPage_Field_Votes);
		}
	};

	private static final ArrayList<String> chartFieldValuesDefault = new ArrayList<String>() {
		private static final long serialVersionUID = 9135403539678279982L;
		{
			add("noop"); //$NON-NLS-1$
			add("alias"); //$NON-NLS-1$
			add("assigned_to"); //$NON-NLS-1$
			add("attachments.submitter"); //$NON-NLS-1$
			add("attach_data.thedata"); //$NON-NLS-1$
			add("attachments.description"); //$NON-NLS-1$
			add("attachments.filename"); //$NON-NLS-1$
			add("attachments.isurl"); //$NON-NLS-1$
			add("attachments.isobsolete"); //$NON-NLS-1$
			add("attachments.ispatch"); //$NON-NLS-1$
			add("attachments.isprivate"); //$NON-NLS-1$
			add("attachments.mimetype"); //$NON-NLS-1$
			add("blocked"); //$NON-NLS-1$
			add("bug_id"); //$NON-NLS-1$
			add("cc"); //$NON-NLS-1$
			add("cclist_accessible"); //$NON-NLS-1$
			add("classification"); //$NON-NLS-1$
			add("longdesc"); //$NON-NLS-1$
			add("longdescs.isprivate"); //$NON-NLS-1$
			add("commenter"); //$NON-NLS-1$
			add("component"); //$NON-NLS-1$
			add("content"); //$NON-NLS-1$
			add("creation_ts"); //$NON-NLS-1$
			add("days_elapsed"); //$NON-NLS-1$
			add("dependson"); //$NON-NLS-1$
			add("cf_dropdown"); //$NON-NLS-1$
			add("everconfirmed"); //$NON-NLS-1$
			add("flagtypes.name"); //$NON-NLS-1$
			add("requestees.login_name"); //$NON-NLS-1$
			add("setters.login_name"); //$NON-NLS-1$
			add("cf_freetext"); //$NON-NLS-1$
			add("bug_group"); //$NON-NLS-1$
			add("keywords"); //$NON-NLS-1$
			add("delta_ts"); //$NON-NLS-1$
			add("op_sys"); //$NON-NLS-1$
			add("rep_platform"); //$NON-NLS-1$
			add("priority"); //$NON-NLS-1$
			add("product"); //$NON-NLS-1$
			add("qa_contact"); //$NON-NLS-1$
			add("reporter"); //$NON-NLS-1$
			add("reporter_accessible"); //$NON-NLS-1$
			add("resolution"); //$NON-NLS-1$
			add("bug_severity"); //$NON-NLS-1$
			add("bug_status"); //$NON-NLS-1$
			add("status_whiteboard"); //$NON-NLS-1$
			add("short_desc"); //$NON-NLS-1$
			add("target_milestone"); //$NON-NLS-1$
			add("owner_idle_time"); //$NON-NLS-1$
			add("bug_file_loc"); //$NON-NLS-1$
			add("version"); //$NON-NLS-1$
			add("votes"); //$NON-NLS-1$
		}
	};

	private static final String[] chartOperationText = { Messages.BugzillaSearchPage_Operation_Noop,
			Messages.BugzillaSearchPage_Operation_is_equal_to, Messages.BugzillaSearchPage_Operation_is_not_equal_to,
			Messages.BugzillaSearchPage_Operation_is_equal_to_any_of_the_strings,
			Messages.BugzillaSearchPage_Operation_contains_the_string,
			Messages.BugzillaSearchPage_Operation_contains_the_string_exact_case,
			Messages.BugzillaSearchPage_Operation_does_not_contain_the_string,
			Messages.BugzillaSearchPage_Operation_contains_any_of_the_strings,
			Messages.BugzillaSearchPage_Operation_contains_all_of_the_strings,
			Messages.BugzillaSearchPage_Operation_contains_none_of_the_strings,
			Messages.BugzillaSearchPage_Operation_contains_regexp,
			Messages.BugzillaSearchPage_Operation_does_not_contain_regexp,
			Messages.BugzillaSearchPage_Operation_is_less_than, Messages.BugzillaSearchPage_Operation_is_greater_than,
			Messages.BugzillaSearchPage_Operation_contains_any_of_he_words,
			Messages.BugzillaSearchPage_Operation_contains_all_of_the_words,
			Messages.BugzillaSearchPage_Operation_contains_none_of_the_words,
			Messages.BugzillaSearchPage_Operation_changed_before, Messages.BugzillaSearchPage_Operation_changed_after,
			Messages.BugzillaSearchPage_Operation_changed_from, Messages.BugzillaSearchPage_Operation_changed_to,
			Messages.BugzillaSearchPage_Operation_changed_by, Messages.BugzillaSearchPage_Operation_matches };

	private static final String[] chartOperationValues = { "noop", "equals", "notequals", "anyexact", "substring", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
			"casesubstring", "notsubstring", "anywordssubstr", "allwordssubstr", "nowordssubstr", "regexp", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$
			"notregexp", "lessthan", "greaterthan", "anywords", "allwords", "nowords", "changedbefore", "changedafter", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$ //$NON-NLS-6$ //$NON-NLS-7$ //$NON-NLS-8$
			"changedfrom", "changedto", "changedby", "matches" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private final static String REGEXP_CHART_EXPR = "(field|type|value)([0-9]+)-([0-9]+)-([0-9]+)"; //$NON-NLS-1$

	private static final Pattern PATTERN_CHART_EXPR = Pattern.compile(REGEXP_CHART_EXPR, Pattern.CASE_INSENSITIVE);

	private final static String REGEXP_CHART_NEGATE = "(negate)([0-9]+)"; //$NON-NLS-1$

	private static final Pattern PATTERN_CHART_NEGATE = Pattern.compile(REGEXP_CHART_NEGATE, Pattern.CASE_INSENSITIVE);

	private IRepositoryQuery originalQuery = null;

	protected boolean restoring = false;

	private boolean restoreQueryOptions = true;

	protected Combo summaryPattern;

	protected Combo summaryOperation;

	protected List product;

	protected List os;

	protected List hardware;

	protected List priority;

	protected List severity;

	protected List resolution;

	protected List status;

	protected Combo commentOperation;

	protected Combo commentPattern;

	protected List component;

	protected List version;

	protected List target;

	protected Combo emailOperation;

	protected Combo emailOperation2;

	protected Combo emailPattern;

	protected Combo emailPattern2;

	protected Button[] emailButtons;

	protected Button[] emailButtons2;

	private Combo keywords;

	private Combo keywordsOperation;

	protected Combo whiteboardPattern;

	private Combo whiteboardOperation;

	protected Text daysText;

	protected String[] chartFieldText;

	protected String[] chartFieldValues;

	/** Index of the saved query to run */
	protected int selIndex;

	// Dialog store taskId constants
	protected final static String PAGE_NAME = "BugzillaSearchPage"; //$NON-NLS-1$

	private final FormToolkit toolkit;

	private ExpandableComposite moreOptionsSection;

	private ExpandableComposite chartSection;

	private SectionComposite scrolledComposite;

	private final ArrayList<Chart> charts = new ArrayList<Chart>(1);

	private final ArrayList<ControlDecoration> errorDecorations = new ArrayList<ControlDecoration>();

	private class ChartControls {
		private final Combo field;

		private final Combo operation;

		private final Combo value;

		public ChartControls(Combo field, Combo operation, Combo value) {
			super();
			this.field = field;
			this.operation = operation;
			this.value = value;
		}

		public Combo getField() {
			return field;
		}

		public Combo getOperation() {
			return operation;
		}

		public Combo getValue() {
			return value;
		}
	}

	private final ArrayList<ArrayList<ArrayList<ChartControls>>> chartControls = new ArrayList<ArrayList<ArrayList<ChartControls>>>();

	private final ArrayList<Button> negateButtons = new ArrayList<Button>();

	private final SelectionAdapter updateActionSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (isControlCreated()) {
				setPageComplete(isPageComplete());
			}
		}
	};

	private final class ModifyListenerImplementation implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			if (isControlCreated()) {
				setPageComplete(isPageComplete());
			}
		}
	}

	@Override
	public void setPageComplete(boolean complete) {
		super.setPageComplete(complete);
		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(complete);
		}
	}

	private static class BugzillaSearchData {
		/** Pattern to match on */
		String pattern;

		/** Pattern matching criterion */
		int operation;

		BugzillaSearchData(String pattern, int operation) {
			this.pattern = pattern;
			this.operation = operation;
		}
	}

	@Override
	protected void createButtons(Composite control) {
		if (originalQuery != null) {
			return;
		}
		super.createButtons(control);
	}

	@Override
	public void doClearControls() {
		product.deselectAll();
		component.deselectAll();
		version.deselectAll();
		target.deselectAll();
		status.deselectAll();
		resolution.deselectAll();
		severity.deselectAll();
		priority.deselectAll();
		hardware.deselectAll();
		os.deselectAll();
		summaryOperation.select(0);
		commentOperation.select(0);
		emailOperation.select(1);

		for (Button emailButton : emailButtons) {
			emailButton.setSelection(false);
		}
		summaryPattern.setText(""); //$NON-NLS-1$
		commentPattern.setText(""); //$NON-NLS-1$
		emailPattern.setText(""); //$NON-NLS-1$
		emailOperation2.select(1);
		for (Button element : emailButtons2) {
			element.setSelection(false);
		}
		emailPattern2.setText(""); //$NON-NLS-1$
		keywords.setText(""); //$NON-NLS-1$
		keywordsOperation.select(0);
		whiteboardPattern.setText(""); //$NON-NLS-1$
		whiteboardOperation.select(0);
		daysText.setText(""); //$NON-NLS-1$

		charts.clear();
		charts.add(0, new Chart());
		recreateChartControls();
	}

	@Override
	protected void createPageContent(SectionComposite parent) {
		this.scrolledComposite = parent;

		Composite scrolledBodyComposite = scrolledComposite.getContent();
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 0;
		scrolledBodyComposite.setLayout(layout);

		basicCompositeCreate(scrolledBodyComposite);
		createMoreOptionsSection(scrolledBodyComposite);
		createChartSection(scrolledBodyComposite);

		Point p = scrolledBodyComposite.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);
		scrolledComposite.setMinSize(p);
	}

	private void basicCompositeCreate(Composite parent) {
		final Composite basicComposite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		//layout.marginRight = 5;
		basicComposite.setLayout(layout);
		GridData g = new GridData(GridData.FILL, GridData.FILL, true, true);
		g.widthHint = 500;
		basicComposite.setLayoutData(g);
		Dialog.applyDialogFont(basicComposite);

		// Info text
		Label labelSummary = new Label(basicComposite, SWT.LEFT);
		labelSummary.setText(Messages.BugzillaSearchPage_Summary);

		// Pattern combo
		summaryPattern = new Combo(basicComposite, SWT.SINGLE | SWT.BORDER);
		summaryPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		summaryPattern.addModifyListener(new ModifyListenerImplementation());
		summaryPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(summaryPattern, summaryOperation, previousSummaryPatterns);
			}
		});

		summaryOperation = new Combo(basicComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		summaryOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		summaryOperation.setItems(patternOperationText);
		summaryOperation.setText(patternOperationText[0]);
		summaryOperation.select(0);
		Label labelEmail = new Label(basicComposite, SWT.LEFT);
		labelEmail.setText(Messages.BugzillaSearchPage_Email);

		// pattern combo
		emailPattern = new Combo(basicComposite, SWT.SINGLE | SWT.BORDER);
		emailPattern.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		emailPattern.addModifyListener(new ModifyListenerImplementation());
		emailPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(emailPattern, emailOperation, previousEmailPatterns);
			}
		});
		IContentProposalProvider proposalProvider = TasksUi.getUiFactory().createPersonContentProposalProvider(
				getTaskRepository());
		ILabelProvider proposalLabelProvider = TasksUi.getUiFactory().createPersonContentProposalLabelProvider(
				getTaskRepository());

		ContentAssistCommandAdapter adapter = new ContentAssistCommandAdapter(emailPattern, new ComboContentAdapter(),
				proposalProvider, ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS, new char[0], true);
		adapter.setLabelProvider(proposalLabelProvider);
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		// operation combo
		emailOperation = new Combo(basicComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		emailOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		emailOperation.setItems(emailOperationText);
		emailOperation.setText(emailOperationText[1]);
		emailOperation.select(1);

		new Label(basicComposite, SWT.NONE);
		Composite emailComposite = new Composite(basicComposite, SWT.NONE);
		emailComposite.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		GridLayout emailLayout = new GridLayout();
		emailLayout.marginWidth = 0;
		emailLayout.marginHeight = 0;
		emailLayout.horizontalSpacing = 2;
		emailLayout.numColumns = 5;
		emailComposite.setLayout(emailLayout);

		Button button0 = new Button(emailComposite, SWT.CHECK);
		button0.setText(Messages.BugzillaSearchPage_owner);

		Button button1 = new Button(emailComposite, SWT.CHECK);
		button1.setText(Messages.BugzillaSearchPage_reporter);

		Button button2 = new Button(emailComposite, SWT.CHECK);
		button2.setText(Messages.BugzillaSearchPage_cc);

		Button button3 = new Button(emailComposite, SWT.CHECK);
		button3.setText(Messages.BugzillaSearchPage_commenter);

		Button button4 = new Button(emailComposite, SWT.CHECK);
		button4.setText(Messages.BugzillaSearchPage_qacontact);
		button0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		button3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		button4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		emailButtons = new Button[] { button0, button1, button2, button3, button4 };
		new Label(basicComposite, SWT.NONE);
		GridLayout sashFormLayout = new GridLayout();
		sashFormLayout.numColumns = 4;
		sashFormLayout.marginHeight = 5;
		sashFormLayout.marginWidth = 5;
		sashFormLayout.horizontalSpacing = 5;

		SashForm sashForm = new SashForm(basicComposite, SWT.VERTICAL);
		sashForm.setLayout(sashFormLayout);
		final GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		gd_sashForm.widthHint = 400;
		gd_sashForm.heightHint = 80;
		sashForm.setLayoutData(gd_sashForm);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 4;
		SashForm topForm = new SashForm(sashForm, SWT.NONE);
		GridData topLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		topLayoutData.widthHint = 00;
		topLayoutData.heightHint = 60;
		topForm.setLayoutData(topLayoutData);
		topForm.setLayout(topLayout);

		GridLayout productLayout = new GridLayout();
		productLayout.marginWidth = 0;
		productLayout.marginHeight = 0;
		productLayout.horizontalSpacing = 0;
		Composite productComposite = new Composite(topForm, SWT.NONE);
		productComposite.setLayout(productLayout);

		Label productLabel = new Label(productComposite, SWT.LEFT);
		productLabel.setText(Messages.BugzillaSearchPage_Product);
		productLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		GridData productLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		productLayoutData.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		product = new List(productComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		product.setLayoutData(productLayoutData);
		product.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateAttributesBasedOnProductSelection(product.getSelection(), getRepositoryConfiguration());
				setPageComplete(isPageComplete());
			}
		});

		GridLayout componentLayout = new GridLayout();
		componentLayout.marginWidth = 0;
		componentLayout.marginHeight = 0;
		componentLayout.horizontalSpacing = 0;
		Composite componentComposite = new Composite(topForm, SWT.NONE);
		componentComposite.setLayout(componentLayout);

		Label componentLabel = new Label(componentComposite, SWT.LEFT);
		componentLabel.setText(Messages.BugzillaSearchPage_Component);
		componentLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		component = new List(componentComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData componentLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		componentLayoutData.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		component.setLayoutData(componentLayoutData);
		component.addSelectionListener(updateActionSelectionAdapter);

		Composite statusComposite = new Composite(topForm, SWT.NONE);
		GridLayout statusLayout = new GridLayout();
		statusLayout.marginWidth = 0;
		statusLayout.horizontalSpacing = 0;
		statusLayout.marginHeight = 0;
		statusComposite.setLayout(statusLayout);

		Label statusLabel = new Label(statusComposite, SWT.LEFT);
		statusLabel.setText(Messages.BugzillaSearchPage_Status);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		status = new List(statusComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_status = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_status.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		status.setLayoutData(gd_status);
		status.addSelectionListener(updateActionSelectionAdapter);

		Composite severityComposite = new Composite(topForm, SWT.NONE);
		GridLayout severityLayout = new GridLayout();
		severityLayout.marginWidth = 0;
		severityLayout.marginHeight = 0;
		severityLayout.horizontalSpacing = 0;
		severityComposite.setLayout(severityLayout);

		Label severityLabel = new Label(severityComposite, SWT.LEFT);
		severityLabel.setText(Messages.BugzillaSearchPage_Severity);
		severityLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		severity = new List(severityComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_severity = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_severity.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		severity.setLayoutData(gd_severity);
		severity.addSelectionListener(updateActionSelectionAdapter);
	}

	private void createMoreOptionsSection(Composite parent) {
		moreOptionsSection = scrolledComposite.createSection(Messages.BugzillaSearchPage_More_Options,
				ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, true);
		GridData g = new GridData(GridData.FILL, GridData.CENTER, true, false);
		g.horizontalSpan = 4;
		moreOptionsSection.setLayoutData(g);

		Composite moreOptionsComposite = new Composite(moreOptionsSection, SWT.NONE);
		GridLayout optionsLayout = new GridLayout(4, false);
		optionsLayout.marginHeight = 0;
		optionsLayout.marginWidth = 0;
		moreOptionsComposite.setLayout(optionsLayout);
		moreOptionsSection.setClient(moreOptionsComposite);

		createMoreOptionsContent(moreOptionsComposite);
		createMoreOptionsChangedInFilter(moreOptionsComposite);
	}

	private void createChartSection(Composite parent) {
		chartFieldText = chartFieldTextDefault.toArray(new String[chartFieldTextDefault.size()]);
		chartFieldValues = chartFieldValuesDefault.toArray(new String[chartFieldValuesDefault.size()]);

		chartSection = scrolledComposite.createSection(Messages.BugzillaSearchPage_BooleanChart,
				ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE | ExpandableComposite.TITLE_BAR, false);
		GridData g = new GridData(GridData.FILL, GridData.BEGINNING, true, false);
		g.horizontalSpan = 4;
		chartSection.setLayoutData(g);

		charts.add(0, new Chart());
		recreateChartControls();
	}

	private void createMoreOptionsContent(Composite advancedComposite) {

		// Info text
		Label labelComment = new Label(advancedComposite, SWT.LEFT);
		labelComment.setText(Messages.BugzillaSearchPage_Comment);

		// Comment pattern combo
		commentPattern = new Combo(advancedComposite, SWT.SINGLE | SWT.BORDER);
		commentPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		commentPattern.addModifyListener(new ModifyListenerImplementation());
		commentPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(commentPattern, commentOperation, previousCommentPatterns);
			}
		});

		commentOperation = new Combo(advancedComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		commentOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		commentOperation.setItems(patternOperationText);
		commentOperation.setText(patternOperationText[0]);
		commentOperation.select(0);

		Label labelEmail2 = new Label(advancedComposite, SWT.LEFT);
		labelEmail2.setText(Messages.BugzillaSearchPage_Email_2);

		// pattern combo
		emailPattern2 = new Combo(advancedComposite, SWT.SINGLE | SWT.BORDER);
		emailPattern2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		emailPattern2.addModifyListener(new ModifyListenerImplementation());
		emailPattern2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(emailPattern2, emailOperation2, previousEmailPatterns2);
			}
		});
		IContentProposalProvider proposalProvider = TasksUi.getUiFactory().createPersonContentProposalProvider(
				getTaskRepository());
		ILabelProvider proposalLabelProvider = TasksUi.getUiFactory().createPersonContentProposalLabelProvider(
				getTaskRepository());
		ContentAssistCommandAdapter adapter2 = new ContentAssistCommandAdapter(emailPattern2,
				new ComboContentAdapter(), proposalProvider, ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
				new char[0], true);
		adapter2.setLabelProvider(proposalLabelProvider);
		adapter2.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		// operation combo
		emailOperation2 = new Combo(advancedComposite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		emailOperation2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		emailOperation2.setItems(emailOperationText);
		emailOperation2.setText(emailOperationText[1]);
		emailOperation2.select(1);

		new Label(advancedComposite, SWT.NONE);
		Composite emailComposite2 = new Composite(advancedComposite, SWT.NONE);
		emailComposite2.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false, 2, 1));
		GridLayout emailLayout2 = new GridLayout();
		emailLayout2.marginWidth = 0;
		emailLayout2.marginHeight = 0;
		emailLayout2.horizontalSpacing = 2;
		emailLayout2.numColumns = 5;
		emailComposite2.setLayout(emailLayout2);

		Button e2button0 = new Button(emailComposite2, SWT.CHECK);
		e2button0.setText(Messages.BugzillaSearchPage_owner);

		Button e2button1 = new Button(emailComposite2, SWT.CHECK);
		e2button1.setText(Messages.BugzillaSearchPage_reporter);

		Button e2button2 = new Button(emailComposite2, SWT.CHECK);
		e2button2.setText(Messages.BugzillaSearchPage_cc);

		Button e2button3 = new Button(emailComposite2, SWT.CHECK);
		e2button3.setText(Messages.BugzillaSearchPage_commenter);

		Button e2button4 = new Button(emailComposite2, SWT.CHECK);
		e2button4.setText(Messages.BugzillaSearchPage_qacontact);
		e2button0.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		e2button1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		e2button2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		e2button3.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});
		e2button4.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setPageComplete(isPageComplete());
			}
		});

		emailButtons2 = new Button[] { e2button0, e2button1, e2button2, e2button3, e2button4 };

		new Label(advancedComposite, SWT.NONE);
		Label whiteboardLabel = new Label(advancedComposite, SWT.NONE);
		whiteboardLabel.setText(Messages.BugzillaSearchPage_Whiteboard);

		// whiteboard pattern combo
		whiteboardPattern = new Combo(advancedComposite, SWT.SINGLE | SWT.BORDER);
		whiteboardPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		whiteboardPattern.addModifyListener(new ModifyListenerImplementation());
		whiteboardPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(whiteboardPattern, whiteboardOperation, previousWhiteboardPatterns);
			}
		});

		whiteboardOperation = new Combo(advancedComposite, SWT.READ_ONLY);
		whiteboardOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		whiteboardOperation.setItems(patternOperationText);
		whiteboardOperation.setText(patternOperationText[0]);
		whiteboardOperation.select(0);

		Label keywordsLabel = new Label(advancedComposite, SWT.NONE);
		keywordsLabel.setText(Messages.BugzillaSearchPage_Keywords);

		Composite keywordsComposite = new Composite(advancedComposite, SWT.NONE);
		keywordsComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 3, 1));
		GridLayout keywordsLayout = new GridLayout();
		keywordsLayout.marginWidth = 0;
		keywordsLayout.marginHeight = 0;
		keywordsLayout.numColumns = 3;
		keywordsComposite.setLayout(keywordsLayout);

		keywordsOperation = new Combo(keywordsComposite, SWT.READ_ONLY);
		keywordsOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		keywordsOperation.setItems(keywordOperationText);
		keywordsOperation.setText(keywordOperationText[0]);
		keywordsOperation.select(0);

		keywords = new Combo(keywordsComposite, SWT.NONE);
		keywords.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		keywords.addModifyListener(new ModifyListenerImplementation());
		keywords.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(keywords, keywordsOperation, previousKeywords);
			}
		});

		final Button keywordsSelectButton = new Button(keywordsComposite, SWT.NONE);
		keywordsSelectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final java.util.List<String> values = new ArrayList<String>();
				for (String string : keywords.getText().split(",")) { //$NON-NLS-1$
					values.add(string.trim());
				}

				Map<String, String> validValues = new HashMap<String, String>();
				if (getRepositoryConfiguration() != null) {
					for (String string : getRepositoryConfiguration().getOptionValues(BugzillaAttribute.KEYWORDS)) {
						validValues.put(string, string);
					}
				}
				final InPlaceCheckBoxTreeDialog selectionDialog = new InPlaceCheckBoxTreeDialog(
						WorkbenchUtil.getShell(), keywordsSelectButton, values, validValues, ""); //$NON-NLS-1$
				selectionDialog.addEventListener(new IInPlaceDialogListener() {

					public void buttonPressed(InPlaceDialogEvent event) {
						if (event.getReturnCode() == Window.OK) {
							Set<String> newValues = selectionDialog.getSelectedValues();
							if (!new HashSet<String>(values).equals(newValues)) {
								String erg = ""; //$NON-NLS-1$
								for (String string : newValues) {
									if (erg.equals("")) { //$NON-NLS-1$
										erg = string;
									} else {
										erg += (", " + string); //$NON-NLS-1$
									}
								}
								keywords.setText(erg);
							}
						} else if (event.getReturnCode() == AbstractInPlaceDialog.ID_CLEAR) {
							keywords.setText(""); //$NON-NLS-1$
						}
					}
				});
				selectionDialog.open();

			}
		});
		keywordsSelectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		keywordsSelectButton.setText(Messages.BugzillaSearchPage_Select_);

		SashForm bottomForm = new SashForm(advancedComposite, SWT.NONE);
		GridLayout bottomLayout = new GridLayout();
		bottomLayout.numColumns = 6;
		bottomForm.setLayout(bottomLayout);
		GridData bottomLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		bottomLayoutData.heightHint = 80;
		bottomLayoutData.widthHint = 400;
		bottomForm.setLayoutData(bottomLayoutData);

		Composite priorityComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout priorityLayout = new GridLayout();
		priorityLayout.marginWidth = 0;
		priorityLayout.marginHeight = 0;
		priorityLayout.horizontalSpacing = 0;
		priorityComposite.setLayout(priorityLayout);

		Label priorityLabel = new Label(priorityComposite, SWT.LEFT);
		priorityLabel.setText(Messages.BugzillaSearchPage_PROORITY);
		priorityLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		priority = new List(priorityComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_priority = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_priority.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		priority.setLayoutData(gd_priority);
		priority.addSelectionListener(updateActionSelectionAdapter);

		Composite resolutionComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout resolutionLayout = new GridLayout();
		resolutionLayout.marginWidth = 0;
		resolutionLayout.marginHeight = 0;
		resolutionLayout.horizontalSpacing = 0;
		resolutionComposite.setLayout(resolutionLayout);

		Label resolutionLabel = new Label(resolutionComposite, SWT.LEFT);
		resolutionLabel.setText(Messages.BugzillaSearchPage_Resolution);
		resolutionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		resolution = new List(resolutionComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_resolution = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_resolution.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		resolution.setLayoutData(gd_resolution);
		resolution.addSelectionListener(updateActionSelectionAdapter);

		Composite versionComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout versionLayout = new GridLayout();
		versionLayout.marginWidth = 0;
		versionLayout.marginHeight = 0;
		versionLayout.horizontalSpacing = 0;
		versionComposite.setLayout(versionLayout);

		Label versionLabel = new Label(versionComposite, SWT.LEFT);
		versionLabel.setText(Messages.BugzillaSearchPage_Version);
		versionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		version = new List(versionComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData versionLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		versionLayoutData.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		version.setLayoutData(versionLayoutData);
		version.addSelectionListener(updateActionSelectionAdapter);

		Composite milestoneComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout milestoneLayout = new GridLayout();
		milestoneLayout.marginWidth = 0;
		milestoneLayout.marginHeight = 0;
		milestoneLayout.horizontalSpacing = 0;
		milestoneComposite.setLayout(milestoneLayout);

		Label milestoneLabel = new Label(milestoneComposite, SWT.LEFT);
		milestoneLabel.setText(Messages.BugzillaSearchPage_Milestone);
		milestoneLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		target = new List(milestoneComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		GridData targetLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true);
		targetLayoutData.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		target.setLayoutData(targetLayoutData);
		target.addSelectionListener(updateActionSelectionAdapter);

		Composite hardwareComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout hardwareLayout = new GridLayout();
		hardwareLayout.marginWidth = 0;
		hardwareLayout.marginHeight = 0;
		hardwareLayout.horizontalSpacing = 0;
		hardwareComposite.setLayout(hardwareLayout);

		Label hardwareLabel = new Label(hardwareComposite, SWT.LEFT);
		hardwareLabel.setText(Messages.BugzillaSearchPage_Hardware);
		hardwareLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		hardware = new List(hardwareComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_hardware = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_hardware.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		hardware.setLayoutData(gd_hardware);
		hardware.addSelectionListener(updateActionSelectionAdapter);

		Composite osComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout osLayout = new GridLayout();
		osLayout.marginWidth = 0;
		osLayout.marginHeight = 0;
		osLayout.horizontalSpacing = 0;
		osComposite.setLayout(osLayout);

		Label osLabel = new Label(osComposite, SWT.LEFT);
		osLabel.setText(Messages.BugzillaSearchPage_Operating_System);
		osLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		os = new List(osComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_os = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_os.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		os.setLayoutData(gd_os);
		os.addSelectionListener(updateActionSelectionAdapter);
	}

	private void createMoreOptionsChangedInFilter(Composite control) {
		Composite composite = new Composite(control, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.horizontalSpan = 4;
		composite.setLayoutData(gd);
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 7;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 3;
		composite.setLayout(gridLayout);

		Label changedInTheLabel = new Label(composite, SWT.LEFT);
		changedInTheLabel.setLayoutData(new GridData());
		changedInTheLabel.setText(Messages.BugzillaSearchPage_Changed_in);

		Composite updateComposite = new Composite(composite, SWT.NONE);
		updateComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout updateLayout = new GridLayout(2, false);
		updateLayout.marginWidth = 0;
		updateLayout.horizontalSpacing = 0;
		updateLayout.marginHeight = 0;
		updateComposite.setLayout(updateLayout);

		daysText = new Text(updateComposite, SWT.BORDER);
		daysText.setLayoutData(new GridData(40, SWT.DEFAULT));
		daysText.setTextLimit(5);
		daysText.addListener(SWT.Modify, this);

		Label label = new Label(updateComposite, SWT.LEFT);
		label.setText(Messages.BugzillaSearchPage_days);

	}

	/**
	 * Creates the buttons for remembering a query and accessing previously saved queries.
	 */
	protected Control createSaveQuery(Composite control) {
		GridLayout layout;
		GridData gd;

		Group group = new Group(control, SWT.NONE);
		layout = new GridLayout(3, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);
		return group;
	}

	private void handleWidgetSelected(Combo widget, Combo operation, ArrayList<BugzillaSearchData> history) {
		if (widget.getSelectionIndex() < 0) {
			return;
		}
		int index = history.size() - 1 - widget.getSelectionIndex();
		BugzillaSearchData patternData = history.get(index);
		if (patternData == null || !widget.getText().equals(patternData.pattern)) {
			return;
		}
		widget.setText(patternData.pattern);
		operation.setText(operation.getItem(patternData.operation));
	}

	// TODO: avoid overriding?
	@Override
	public boolean performSearch() {
		if (restoreQueryOptions) {
			saveState();
		}

		getPatternData(summaryPattern, summaryOperation, previousSummaryPatterns);
		getPatternData(commentPattern, commentOperation, previousCommentPatterns);
		getPatternData(emailPattern, emailOperation, previousEmailPatterns);
		getPatternData(emailPattern2, emailOperation2, previousEmailPatterns2);
		getPatternData(keywords, keywordsOperation, previousKeywords);
		getPatternData(whiteboardPattern, whiteboardOperation, previousWhiteboardPatterns);

		String summaryText = summaryPattern.getText();
		BugzillaUiPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MOST_RECENT_QUERY, summaryText);

		return super.performSearch();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && summaryPattern != null) {
			if (firstTime) {
				firstTime = false;
				// restore items from history here to prevent page from resizing
				for (String searchPattern : getPreviousPatterns(previousSummaryPatterns)) {
					summaryPattern.add(searchPattern);
				}
				for (String comment : getPreviousPatterns(previousCommentPatterns)) {
					commentPattern.add(comment);
				}
				for (String email : getPreviousPatterns(previousEmailPatterns)) {
					emailPattern.add(email);
				}
				for (String email : getPreviousPatterns(previousEmailPatterns2)) {
					emailPattern2.add(email);
				}
				for (String keyword : getPreviousPatterns(previousKeywords)) {
					keywords.add(keyword);
				}

			}
		}
		super.setVisible(visible);
	}

	/**
	 * Returns <code>true</code> if at least some parameter is given to query on.
	 */
	private boolean canQuery() {
		if (isControlCreated()) {
			return product.getSelectionCount() > 0 || component.getSelectionCount() > 0
					|| version.getSelectionCount() > 0 || target.getSelectionCount() > 0
					|| status.getSelectionCount() > 0 || resolution.getSelectionCount() > 0
					|| severity.getSelectionCount() > 0 || priority.getSelectionCount() > 0
					|| hardware.getSelectionCount() > 0 || os.getSelectionCount() > 0
					|| summaryPattern.getText().length() > 0 || commentPattern.getText().length() > 0
					|| emailPattern.getText().length() > 0 || emailPattern2.getText().length() > 0
					|| keywords.getText().length() > 0;
		} else {
			return false;
		}
	}

	@Override
	public boolean isPageComplete() {
		setMessage(""); //$NON-NLS-1$
		if (errorDecorations.size() > 0) {
			for (ControlDecoration decoration : errorDecorations) {
				decoration.hide();
				decoration.dispose();
			}
			errorDecorations.clear();
		}
		if (daysText != null) {
			String days = daysText.getText();
			if (days.length() > 0) {
				try {
					if (Integer.parseInt(days) < 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException ex) {
					if (getContainer() != null) {
						setMessage(
								NLS.bind(Messages.BugzillaSearchPage_Number_of_days_must_be_a_positive_integer, days),
								IMessageProvider.ERROR);
					} else {
						ErrorDialog.openError(
								getShell(),
								Messages.BugzillaSearchPage_ValidationTitle,
								Messages.BugzillaSearchPage_Number_of_days_is_invalid,
								new Status(IStatus.ERROR, BugzillaUiPlugin.ID_PLUGIN, NLS.bind(
										Messages.BugzillaSearchPage_days_must_be_an_positve_integer_value_but_is, days)));

					}
					return false;
				}
			}
		}
		if (emailPattern != null) {
			String email = emailPattern.getText();
			if (email.length() > 0) {
				boolean selectionMade = false;
				for (Button button : emailButtons) {
					if (button.getSelection()) {
						selectionMade = true;
						break;
					}
				}
				if (!selectionMade) {
					FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
					FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
					final ControlDecoration decoration = new ControlDecoration(emailPattern, SWT.LEFT | SWT.DOWN);
					decoration.setImage(fieldDecoration.getImage());
					decoration.setDescriptionText(NLS.bind(Messages.BugzillaSearchPage_ValidationMessage, new String[] {
							Messages.BugzillaSearchPage_Email.replace('&', ' '), Messages.BugzillaSearchPage_owner,
							Messages.BugzillaSearchPage_reporter, Messages.BugzillaSearchPage_cc,
							Messages.BugzillaSearchPage_commenter, Messages.BugzillaSearchPage_qacontact }));
					errorDecorations.add(decoration);
					if (getContainer() != null) {
						setMessage(
								NLS.bind(Messages.BugzillaSearchPage_ValidationMessage, new String[] {
										Messages.BugzillaSearchPage_Email.replace('&', ' '),
										Messages.BugzillaSearchPage_owner, Messages.BugzillaSearchPage_reporter,
										Messages.BugzillaSearchPage_cc, Messages.BugzillaSearchPage_commenter,
										Messages.BugzillaSearchPage_qacontact }), IMessageProvider.ERROR);
					}
					return false;
				}
			}
		}
		if (emailPattern2 != null) {
			String email2 = emailPattern2.getText();
			if (email2.length() > 0) {
				boolean selectionMade = false;
				for (Button button : emailButtons2) {
					if (button.getSelection()) {
						selectionMade = true;
						break;
					}
				}
				if (!selectionMade) {
					FieldDecorationRegistry registry = FieldDecorationRegistry.getDefault();
					FieldDecoration fieldDecoration = registry.getFieldDecoration(FieldDecorationRegistry.DEC_ERROR);
					final ControlDecoration decoration = new ControlDecoration(emailPattern, SWT.LEFT | SWT.DOWN);
					decoration.setImage(fieldDecoration.getImage());
					decoration.setDescriptionText(NLS.bind(Messages.BugzillaSearchPage_ValidationMessage, new String[] {
							Messages.BugzillaSearchPage_Email_2.replace('&', ' '), Messages.BugzillaSearchPage_owner,
							Messages.BugzillaSearchPage_reporter, Messages.BugzillaSearchPage_cc,
							Messages.BugzillaSearchPage_commenter, Messages.BugzillaSearchPage_qacontact }));
					errorDecorations.add(decoration);
					if (getContainer() != null) {
						setMessage(
								NLS.bind(Messages.BugzillaSearchPage_ValidationMessage, new String[] {
										Messages.BugzillaSearchPage_Email_2.replace('&', ' '),
										Messages.BugzillaSearchPage_owner, Messages.BugzillaSearchPage_reporter,
										Messages.BugzillaSearchPage_cc, Messages.BugzillaSearchPage_commenter,
										Messages.BugzillaSearchPage_qacontact }), IMessageProvider.ERROR);
					}
					return false;
				}
			}
		}
		if (getWizard() == null) {
			return canQuery();
		} else {
			if (super.isPageComplete()) {
				if (canQuery()) {
					return true;
				}
			}
			return false;
		}
	}

	/**
	 * Return search pattern data and update search history list. An existing entry will be updated or a new one
	 * created.
	 */
	private BugzillaSearchData getPatternData(Combo widget, Combo operation,
			ArrayList<BugzillaSearchData> previousSearchQueryData) {
		String pattern = widget.getText();
		if (pattern == null || pattern.trim().equals("")) { //$NON-NLS-1$
			return null;
		}
		BugzillaSearchData match = null;
		int i = previousSearchQueryData.size() - 1;
		while (i >= 0) {
			match = previousSearchQueryData.get(i);
			if (pattern.equals(match.pattern)) {
				break;
			}
			i--;
		}
		if (i >= 0 && match != null) {
			match.operation = operation.getSelectionIndex();
			// remove - will be added last (see below)
			previousSearchQueryData.remove(match);
		} else {
			match = new BugzillaSearchData(widget.getText(), operation.getSelectionIndex());
		}
		previousSearchQueryData.add(match);
		return match;
	}

	/**
	 * Returns an array of previous summary patterns
	 */
	private String[] getPreviousPatterns(ArrayList<BugzillaSearchData> patternHistory) {
		int size = patternHistory.size();
		String[] patterns = new String[size];
		for (int i = 0; i < size; i++) {
			patterns[i] = (patternHistory.get(size - 1 - i)).pattern;
		}
		return patterns;
	}

	public String getSearchURL(TaskRepository repository) {
		return getQueryURL(repository, getQueryParameters());
	}

	protected String getQueryURL(TaskRepository repository, StringBuilder params) {
		StringBuilder url = new StringBuilder(getQueryURLStart(repository).toString());
		url.append(params);

		// HACK make sure that the searches come back sorted by priority. This
		// should be a search option though
		url.append("&order=Importance"); //$NON-NLS-1$
		// url.append(BugzillaRepositoryUtil.contentTypeRDF);
		return url.toString();
	}

	/**
	 * Creates the bugzilla query URL start. Example: https://bugs.eclipse.org/bugs/buglist.cgi?
	 */
	private StringBuilder getQueryURLStart(TaskRepository repository) {
		StringBuilder sb = new StringBuilder(repository.getRepositoryUrl());

		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}
		sb.append("buglist.cgi?"); //$NON-NLS-1$
		return sb;
	}

	/**
	 * Goes through the query form and builds up the query parameters. Example:
	 * short_desc_type=substring&amp;short_desc=bla&amp; ... TODO: The encoding here should match
	 * TaskRepository.getCharacterEncoding()
	 * 
	 * @throws UnsupportedEncodingException
	 */
	protected StringBuilder getQueryParameters() {
		StringBuilder sb = new StringBuilder();

		sb.append("short_desc_type="); //$NON-NLS-1$
		sb.append(patternOperationValues[summaryOperation.getSelectionIndex()]);
		appendToBuffer(sb, "&short_desc=", summaryPattern.getText()); //$NON-NLS-1$

		int[] selected = product.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&product=", product.getItem(element)); //$NON-NLS-1$
		}

		selected = component.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&component=", component.getItem(element)); //$NON-NLS-1$
		}

		selected = version.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&version=", version.getItem(element)); //$NON-NLS-1$
		}

		selected = target.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&target_milestone=", target.getItem(element)); //$NON-NLS-1$
		}

		sb.append("&long_desc_type="); //$NON-NLS-1$
		sb.append(patternOperationValues[commentOperation.getSelectionIndex()]);
		appendToBuffer(sb, "&long_desc=", commentPattern.getText()); //$NON-NLS-1$

		selected = status.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&bug_status=", status.getItem(element)); //$NON-NLS-1$
		}

		selected = resolution.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&resolution=", resolution.getItem(element)); //$NON-NLS-1$
		}

		selected = severity.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&bug_severity=", severity.getItem(element)); //$NON-NLS-1$
		}

		selected = priority.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&priority=", priority.getItem(element)); //$NON-NLS-1$
		}

		selected = hardware.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&rep_platform=", hardware.getItem(element)); //$NON-NLS-1$
		}

		selected = os.getSelectionIndices();
		for (int element : selected) {
			appendToBuffer(sb, "&op_sys=", os.getItem(element)); //$NON-NLS-1$
		}

		if (emailPattern.getText() != null && !emailPattern.getText().trim().equals("")) { //$NON-NLS-1$
			boolean selectionMade = false;
			for (Button button : emailButtons) {
				if (button.getSelection()) {
					selectionMade = true;
					break;
				}
			}
			if (selectionMade) {
				for (int i = 0; i < emailButtons.length; i++) {
					if (emailButtons[i].getSelection()) {
						sb.append("&"); //$NON-NLS-1$
						sb.append(emailRoleValues[i]);
						sb.append("=1"); //$NON-NLS-1$
					}
				}
				sb.append("&emailtype1="); //$NON-NLS-1$
				sb.append(emailOperationValues[emailOperation.getSelectionIndex()]);
				appendToBuffer(sb, "&email1=", emailPattern.getText()); //$NON-NLS-1$
			}
		}

		if (emailPattern2.getText() != null && !emailPattern2.getText().trim().equals("")) { //$NON-NLS-1$
			boolean selectionMade = false;
			for (Button button : emailButtons2) {
				if (button.getSelection()) {
					selectionMade = true;
					break;
				}
			}
			if (selectionMade) {
				for (int i = 0; i < emailButtons2.length; i++) {
					if (emailButtons2[i].getSelection()) {
						sb.append("&"); //$NON-NLS-1$
						sb.append(emailRoleValues2[i]);
						sb.append("=1"); //$NON-NLS-1$
					}
				}
				sb.append("&emailtype2="); //$NON-NLS-1$
				sb.append(emailOperationValues[emailOperation2.getSelectionIndex()]);
				appendToBuffer(sb, "&email2=", emailPattern2.getText()); //$NON-NLS-1$
			}
		}

		if (daysText.getText() != null && !daysText.getText().equals("")) { //$NON-NLS-1$
			try {
				Integer.parseInt(daysText.getText());
				appendToBuffer(sb, "&changedin=", daysText.getText()); //$NON-NLS-1$
			} catch (NumberFormatException ignored) {
				// this means that the days is not a number, so don't worry
			}
		}

		if (keywords.getText() != null && !keywords.getText().trim().equals("")) { //$NON-NLS-1$
			sb.append("&keywords_type="); //$NON-NLS-1$
			sb.append(keywordOperationValues[keywordsOperation.getSelectionIndex()]);
			appendToBuffer(sb, "&keywords=", keywords.getText().replace(',', ' ')); //$NON-NLS-1$
		}
		sb.append("&status_whiteboard_type="); //$NON-NLS-1$
		sb.append(patternOperationValues[whiteboardOperation.getSelectionIndex()]);
		appendToBuffer(sb, "&status_whiteboard=", whiteboardPattern.getText()); //$NON-NLS-1$
		int indexMax = charts.size();
		for (int index = 0; index < indexMax; index++) {
			Chart chart = charts.get(index);
			if (chart.isNegate()) {
				sb.append("&negate" + index + "=1"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			int rowMax = chart.getRowSize();
			for (int row = 0; row < rowMax; row++) {
				int columnMax = chart.getColumnSize(row);
				for (int column = 0; column < columnMax; column++) {
					ChartExpression chartExpression = chart.getChartExpression(row, column);
					if (chartExpression.getFieldName() == 0) {
						continue;
					}
					sb.append("&field" + index + "-" + row + "-" + column + "=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							+ chartFieldValues[chartExpression.getFieldName()]);
					sb.append("&type" + index + "-" + row + "-" + column + "=" //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
							+ chartOperationValues[chartExpression.getOperation()]);
					sb.append("&value" + index + "-" + row + "-" + column + "=" + chartExpression.getValue()); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
				}
			}
		}
		return sb;
	}

	private void appendToBuffer(StringBuilder sb, String key, String value) {
		sb.append(key);
		try {
			sb.append(URLEncoder.encode(value, getTaskRepository().getCharacterEncoding()));
		} catch (UnsupportedEncodingException e) {
			sb.append(value);
		}
	}

	@Override
	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = BugzillaUiPlugin.getDefault().getDialogSettings();
		fDialogSettings = settings.getSection(PAGE_NAME);
		if (fDialogSettings == null) {
			fDialogSettings = settings.addNewSection(PAGE_NAME);
		}
		return fDialogSettings;
	}

	@SuppressWarnings("unchecked")
	private void updateAttributesFromConfiguration(String[] selectedProducts) {
		RepositoryConfiguration repositoryConfiguration = getRepositoryConfiguration();
		if (repositoryConfiguration != null) {
			String[] saved_status = status.getSelection();
			String[] saved_resolution = resolution.getSelection();
			String[] saved_severity = severity.getSelection();
			String[] saved_priority = priority.getSelection();
			String[] saved_hardware = hardware.getSelection();
			String[] saved_os = os.getSelection();

			java.util.List<String> products = repositoryConfiguration.getOptionValues(BugzillaAttribute.PRODUCT);
			String[] productsList = products.toArray(new String[products.size()]);
			Arrays.sort(productsList, String.CASE_INSENSITIVE_ORDER);
			product.setItems(productsList);

			updateAttributesBasedOnProductSelection(selectedProducts, repositoryConfiguration);

			status.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_STATUS)));
			resolution.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.RESOLUTION)));
			severity.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.BUG_SEVERITY)));
			priority.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.PRIORITY)));
			hardware.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.REP_PLATFORM)));
			os.setItems(convertStringListToArray(repositoryConfiguration.getOptionValues(BugzillaAttribute.OP_SYS)));

			setSelection(product, selectedProducts);
			setSelection(status, saved_status);
			setSelection(resolution, saved_resolution);
			setSelection(severity, saved_severity);
			setSelection(priority, saved_priority);
			setSelection(hardware, saved_hardware);
			setSelection(os, saved_os);

			ArrayList<String> fieldText = (ArrayList<String>) chartFieldTextDefault.clone();
			ArrayList<String> fieldValue = (ArrayList<String>) chartFieldValuesDefault.clone();

			for (BugzillaCustomField bugzillaCustomField : repositoryConfiguration.getCustomFields()) {
				fieldValue.add(bugzillaCustomField.getName());
				fieldText.add(bugzillaCustomField.getDescription());
			}
			chartFieldText = fieldText.toArray(new String[fieldText.size()]);
			chartFieldValues = fieldValue.toArray(new String[fieldValue.size()]);
			recreateChartControls();
		}
	}

	private void updateAttributesBasedOnProductSelection(String[] selectedProducts,
			RepositoryConfiguration repositoryConfiguration) {
		if (repositoryConfiguration == null) {
			return;
		}

		// show everything if no product is selected
		if (selectedProducts != null && selectedProducts.length == 0) {
			selectedProducts = null;
		}

		String[] saved_component = component.getSelection();
		String[] saved_version = version.getSelection();
		String[] saved_target = target.getSelection();

		String[] componentsList = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_COMPONENT,
				selectedProducts, repositoryConfiguration);
		Arrays.sort(componentsList, String.CASE_INSENSITIVE_ORDER);
		component.setItems(componentsList);
		version.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_VERSION, selectedProducts,
				repositoryConfiguration));
		target.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_TARGET, selectedProducts,
				repositoryConfiguration));

		setSelection(component, saved_component);
		setSelection(version, saved_version);
		setSelection(target, saved_target);
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	public void handleEvent(Event event) {
		if (getWizard() != null) {
			getWizard().getContainer().updateButtons();
		} else {
			isPageComplete();
		}
	}

	public void restoreStateFromUrl(String queryUrl) throws UnsupportedEncodingException {
		BugzillaSearch search = new BugzillaSearch(getTaskRepository(), queryUrl);

		// set product first to initialize dependent fields
		for (Entry entry : search.getParameters("product")) { //$NON-NLS-1$
			String value = entry.value;
			String[] sel = product.getSelection();
			java.util.List<String> selList = Arrays.asList(sel);
			selList = new ArrayList<String>(selList);
			selList.add(value);
			sel = new String[selList.size()];
			product.setSelection(selList.toArray(sel));
		}

		updateAttributesBasedOnProductSelection(product.getSelection(), getRepositoryConfiguration());

		boolean adjustChart = false;
		for (Entry entry : search.getParameters()) {
			String key = entry.key;
			String value = entry.value;

			if (key.equals("short_desc")) { //$NON-NLS-1$
				summaryPattern.setText(value);
			} else if (key.equals("short_desc_type")) { //$NON-NLS-1$
				int index = 0;
				for (String item : patternOperationValues) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
				}
				if (index < summaryOperation.getItemCount()) {
					summaryOperation.select(index);
				}
			} else if (key.equals("product")) { //$NON-NLS-1$
				// ignore, see above
			} else if (key.equals("component")) { //$NON-NLS-1$
				String[] sel = component.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				component.setSelection(selList.toArray(sel));
			} else if (key.equals("version")) { //$NON-NLS-1$
				String[] sel = version.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				version.setSelection(selList.toArray(sel));
			} else if (key.equals("target_milestone")) { // XXX //$NON-NLS-1$
				String[] sel = target.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				target.setSelection(selList.toArray(sel));
			} else if (key.equals("version")) { //$NON-NLS-1$
				String[] sel = version.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				version.setSelection(selList.toArray(sel));
			} else if (key.equals("long_desc_type")) { //$NON-NLS-1$
				int index = 0;
				for (String item : patternOperationValues) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
				}
				if (index < commentOperation.getItemCount()) {
					commentOperation.select(index);
				}
			} else if (key.equals("long_desc")) { //$NON-NLS-1$
				commentPattern.setText(value);
			} else if (key.equals("bug_status")) { //$NON-NLS-1$
				String[] sel = status.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				status.setSelection(selList.toArray(sel));
			} else if (key.equals("resolution")) { //$NON-NLS-1$
				String[] sel = resolution.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				resolution.setSelection(selList.toArray(sel));
			} else if (key.equals("bug_severity")) { //$NON-NLS-1$
				String[] sel = severity.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				severity.setSelection(selList.toArray(sel));
			} else if (key.equals("priority")) { //$NON-NLS-1$
				String[] sel = priority.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				priority.setSelection(selList.toArray(sel));
			} else if (key.equals("rep_platform")) { //$NON-NLS-1$
				String[] sel = hardware.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				hardware.setSelection(selList.toArray(sel));
			} else if (key.equals("op_sys")) { //$NON-NLS-1$
				String[] sel = os.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				os.setSelection(selList.toArray(sel));
			} else if (key.equals("emailassigned_to1")) { // HACK: email //$NON-NLS-1$
				// buttons
				// assumed to be
				// in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons[0].setSelection(true);
				} else {
					emailButtons[0].setSelection(false);
				}
			} else if (key.equals("emailreporter1")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons[1].setSelection(true);
				} else {
					emailButtons[1].setSelection(false);
				}
			} else if (key.equals("emailcc1")) { // HACK: email buttons //$NON-NLS-1$
				// assumed to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons[2].setSelection(true);
				} else {
					emailButtons[2].setSelection(false);
				}
			} else if (key.equals("emaillongdesc1")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons[3].setSelection(true);
				} else {
					emailButtons[3].setSelection(false);
				}
			} else if (key.equals("emailqa_contact1")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons[4].setSelection(true);
				} else {
					emailButtons[4].setSelection(false);
				}
			} else if (key.equals("emailtype1")) { //$NON-NLS-1$
				int index = 0;
				for (; index < emailOperationValues.length; index++) {
					String item = emailOperationValues[index];
					if (item.compareTo(value) == 0) {
						break;
					}
				}
				if (index < emailOperation.getItemCount()) {
					emailOperation.select(index);
				}
			} else if (key.equals("email1")) { //$NON-NLS-1$
				emailPattern.setText(value);
			} else if (key.equals("emailassigned_to2")) { // HACK: email //$NON-NLS-1$
				// buttons
				// assumed to be
				// in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons2[0].setSelection(true);
				} else {
					emailButtons2[0].setSelection(false);
				}
			} else if (key.equals("emailreporter2")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons2[1].setSelection(true);
				} else {
					emailButtons2[1].setSelection(false);
				}
			} else if (key.equals("emailcc2")) { // HACK: email buttons //$NON-NLS-1$
				// assumed to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons2[2].setSelection(true);
				} else {
					emailButtons2[2].setSelection(false);
				}
			} else if (key.equals("emaillongdesc2")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons2[3].setSelection(true);
				} else {
					emailButtons2[3].setSelection(false);
				}
			} else if (key.equals("emailqa_contact2")) { // HACK: email //$NON-NLS-1$
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1")) { //$NON-NLS-1$
					emailButtons2[4].setSelection(true);
				} else {
					emailButtons2[4].setSelection(false);
				}
			} else if (key.equals("emailtype2")) { //$NON-NLS-1$
				int index = 0;
				for (; index < emailOperationValues.length; index++) {
					String item = emailOperationValues[index];
					if (item.compareTo(value) == 0) {
						break;
					}
				}
				if (index < emailOperation2.getItemCount()) {
					emailOperation2.select(index);
				}
			} else if (key.equals("email2")) { //$NON-NLS-1$
				emailPattern2.setText(value);
			} else if (key.equals("changedin")) { //$NON-NLS-1$
				daysText.setText(value);
			} else if (key.equals("keywords")) { //$NON-NLS-1$
				keywords.setText(value.replace(' ', ','));
			} else if (key.equals("keywords_type")) { //$NON-NLS-1$
				int index = 0;
				for (String item : keywordOperationValues) {
					if (item.equals(value)) {
						keywordsOperation.select(index);
						break;
					}
					index++;
				}
			} else if (key.equals("status_whiteboard_type")) { //$NON-NLS-1$
				int index = 0;
				for (String item : patternOperationValues) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
				}
				if (index < whiteboardOperation.getItemCount()) {
					whiteboardOperation.select(index);
				}
			} else if (key.equals("status_whiteboard")) { //$NON-NLS-1$
				whiteboardPattern.setText(value);
			} else if (key.matches(REGEXP_CHART_EXPR)) {
				Matcher mb = PATTERN_CHART_EXPR.matcher(key);
				if (mb.find()) {
					String g1 = mb.group(1);
					String g2 = mb.group(2);
					String g3 = mb.group(3);
					String g4 = mb.group(4);
					int chartNumber, row, column;
					try {
						chartNumber = Integer.parseInt(g2);
						row = Integer.parseInt(g3);
						column = Integer.parseInt(g4);
					} catch (Exception E) {
						chartNumber = -1;
						row = -1;
						column = -1;
					}
					for (int i = charts.size(); i <= chartNumber; i++) {
						charts.add(new Chart());
						adjustChart = true;
					}
					for (int i = charts.get(chartNumber).getRowSize(); i <= row; i++) {
						charts.get(chartNumber).addExpression(i, 0);
						adjustChart = true;
					}
					for (int i = charts.get(chartNumber).getColumnSize(row); i <= column; i++) {
						charts.get(chartNumber).addExpression(row, i);
						adjustChart = true;
					}
					ChartExpression ex = charts.get(chartNumber).getChartExpression(row, column);
					if ("field".equals(g1)) { //$NON-NLS-1$
						int index1 = 0;
						for (String item : chartFieldValues) {
							if (item.compareTo(value) == 0) {
								break;
							}
							index1++;
						}
						if (index1 < chartFieldValues.length) {
							ex.setFieldName(index1);
						}
					} else if ("type".equals(g1)) { //$NON-NLS-1$
						int index1 = 0;
						for (String item : chartOperationValues) {
							if (item.compareTo(value) == 0) {
								break;
							}
							index1++;
						}
						if (index1 < chartOperationValues.length) {
							ex.setOperation(index1);
						}
					} else if ("value".equals(g1)) { //$NON-NLS-1$
						ex.setValue(value);
					}

				}
			} else if (key.matches(REGEXP_CHART_NEGATE)) {
				Matcher mb = PATTERN_CHART_NEGATE.matcher(key);
				if (mb.find()) {
					String g2 = mb.group(2);
					int index;
					try {
						index = Integer.parseInt(g2);
					} catch (Exception E) {
						index = -1;
					}
					Chart ch = charts.get(index);
					ch.setNegate("1".equals(value)); //$NON-NLS-1$
				}
			}
		}
		if (adjustChart) {
			recreateChartControls();
		}
	}

	/* Testing hook to see if any products are present */
	public int getProductCount() throws Exception {
		return product.getItemCount();
	}

	public boolean isRestoreQueryOptions() {
		return restoreQueryOptions;
	}

	public void setRestoreQueryOptions(boolean restoreQueryOptions) {
		this.restoreQueryOptions = restoreQueryOptions;
	}

	private String[] convertStringListToArray(java.util.List<String> stringList) {
		return stringList.toArray(new String[stringList.size()]);
	}

	private void setSelection(List listControl, String[] selection) {
		listControl.setSelection(selection);
	}

	@Override
	public void dispose() {
		if (toolkit != null) {
			if (toolkit.getColors() != null) {
				toolkit.dispose();
			}
		}
		super.dispose();
	}

	private void refreshChartControls() {
		int chartNumMax = chartControls.size();
		for (int chartNum = 0; chartNum < chartNumMax; chartNum++) {
			int chartRowMax = chartControls.get(chartNum).size();
			for (int chartRow = 0; chartRow < chartRowMax; chartRow++) {
				int chartColumnMax = chartControls.get(chartNum).get(chartRow).size();
				for (int chartColumn = 0; chartColumn < chartColumnMax; chartColumn++) {
					ChartExpression expression = charts.get(chartNum).getChartExpression(chartRow, chartColumn);
					ChartControls controls = chartControls.get(chartNum).get(chartRow).get(chartColumn);
					controls.getField().setText(chartFieldText[expression.getFieldName()]);
					controls.getOperation().setText(chartOperationText[expression.getOperation()]);
					controls.getValue().setText(expression.getValue());
				}
			}
		}
		int negButtonMax = negateButtons.size();
		for (int chartNum = 0; chartNum < negButtonMax; chartNum++) {
			Button b = negateButtons.get(chartNum);
			Chart c = charts.get(chartNum);
			b.setSelection(c.isNegate());
		}
		scrolledComposite.reflow(true);
	}

	private void recreateChartControls() {
		GridLayout layout;
		GridData gd;

		if (chartSection.getClient() != null) {
			chartSection.getClient().dispose();
		}

		chartControls.clear();
		negateButtons.clear();

		Composite chartGroup = new Composite(chartSection, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		chartGroup.setLayout(layout);
		chartSection.setClient(chartGroup);

		int chartNumMax = charts.size();
		for (int chartNumber = 0; chartNumber < chartNumMax; chartNumber++) {
			final int chartNum = chartNumber;
			final Composite chartGroup0 = new Composite(chartGroup, SWT.NONE);
			if (chartNum > 0) {
				// separator
				Label sep = new Label(chartGroup0, SWT.NONE);
				sep.setText(" "); //$NON-NLS-1$
				gd = new GridData(GridData.FILL, GridData.CENTER, true, false, 3, 1);
				sep.setLayoutData(gd);
			}
			layout = new GridLayout(3, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			chartGroup0.setLayout(layout);
			gd = new GridData(GridData.FILL, GridData.FILL, true, true, 3, 1);
			chartGroup0.setLayoutData(gd);
			final int chartRowMax = charts.get(chartNum).getRowSize();
			for (int chartRowNumber = 0; chartRowNumber < chartRowMax; chartRowNumber++) {
				final int chartRow = chartRowNumber;
				int chartColumnMax = charts.get(chartNum).getColumnSize(chartRow);
				final Group chartGroup1 = new Group(chartGroup0, SWT.NONE);
				layout = new GridLayout(4, false);
				chartGroup1.setLayout(layout);
				gd = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
				chartGroup1.setLayoutData(gd);
				for (int chartColumnNumber = 0; chartColumnNumber < chartColumnMax; chartColumnNumber++) {
					final int chartColumn = chartColumnNumber;
					final Combo comboField = new Combo(chartGroup1, SWT.SINGLE | SWT.BORDER);
					comboField.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					comboField.addModifyListener(new ModifyListenerImplementation());
					comboField.setItems(chartFieldText);
					comboField.setText(chartFieldText[0]);
					comboField.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							ChartExpression chartExpression = charts.get(chartNum).getChartExpression(chartRow,
									chartColumn);
							chartExpression.setFieldName(comboField.getSelectionIndex());
							comboField.getShell().layout(true);
							comboField.getShell().redraw();
						}
					});
					comboField.setToolTipText(Messages.BugzillaSearchPage_Tooltip_Custom_fields_at_end);

					final Combo comboOperation = new Combo(chartGroup1, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
					comboOperation.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
					comboOperation.setItems(chartOperationText);
					comboOperation.setText(chartOperationText[0]);
					comboOperation.select(0);
					comboOperation.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							ChartExpression chartExpression = charts.get(chartNum).getChartExpression(chartRow,
									chartColumn);
							chartExpression.setOperation(comboOperation.getSelectionIndex());
						}
					});
					final Combo comboValue = new Combo(chartGroup1, SWT.SINGLE | SWT.BORDER);
					gd = new GridData(GridData.FILL, GridData.CENTER, true, false);
					gd.widthHint = 150;
					comboValue.setLayoutData(gd);
					comboValue.addModifyListener(new ModifyListener() {

						public void modifyText(ModifyEvent e) {
							ChartExpression chartExpression = charts.get(chartNum).getChartExpression(chartRow,
									chartColumn);
							chartExpression.setValue(comboValue.getText());
							if (isControlCreated()) {
								setPageComplete(isPageComplete());
							}
						}
					});
					Button orButton = new Button(chartGroup1, SWT.PUSH);
					orButton.setText(Messages.BugzillaSearchPage_OR_Button);
					gd = new GridData(SWT.LEFT, SWT.CENTER, false, false);
					orButton.setLayoutData(gd);
					orButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							if (e.stateMask == SWT.SHIFT) {
								if (charts.size() == 1 && charts.get(0).getRowSize() == 1
										&& charts.get(0).getColumnSize(0) == 1) {
									return;
								}
								charts.get(chartNum).removeColumn(chartRow, chartColumn);
								if (charts.get(chartNum).getRowSize() == 0) {
									if (chartNum != 0) {
										charts.remove(chartNum);
									}
								}
							} else {
								charts.get(chartNum).addExpression(chartRow, chartColumn + 1);
							}
							recreateChartControls();
						}
					});
					orButton.setToolTipText(Messages.BugzillaSearchPage_Tooltip_remove_row);
					ChartControls chartControl = new ChartControls(comboField, comboOperation, comboValue);
					int chart1 = chartControls.size();
					if (chart1 < chartNum + 1) {
						chartControls.add(new ArrayList<ArrayList<ChartControls>>());
					}
					int chart2 = chartControls.get(chartNum).size();
					if (chart2 < chartRow + 1) {
						chartControls.get(chartNum).add(new ArrayList<BugzillaSearchPage.ChartControls>());
					}
					chartControls.get(chartNum).get(chartRow).add(chartControl);
				}
				if (chartRowNumber < chartRowMax - 1) {
					Label lable = new Label(chartGroup0, SWT.NONE);
					lable.setText(Messages.BugzillaSearchPage_AND_Button);
					GridData g = new GridData(SWT.LEFT, SWT.CENTER, false, false, 4, 1);
					lable.setLayoutData(g);
				} else {
					final Button andButton = new Button(chartGroup0, SWT.PUSH);
					andButton.setText(Messages.BugzillaSearchPage_AND_Button);
					gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					andButton.setLayoutData(gd);
					final Button newButton = new Button(chartGroup0, SWT.PUSH);
					newButton.setText(Messages.BugzillaSearchPage_Add_Chart_Button);
					gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					newButton.setLayoutData(gd);
					final Button negateButton = new Button(chartGroup0, SWT.CHECK);
					negateButton.setText(Messages.BugzillaSearchPage_Negate_Button);
					negateButtons.add(negateButton);
					gd = new GridData(SWT.LEFT, SWT.CENTER, false, false, 1, 1);
					negateButton.setLayoutData(gd);
					negateButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							Chart chart = charts.get(chartNum);
							chart.setNegate(negateButton.getSelection());
						}
					});
					newButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							newButton.setVisible(false);
							newButton.dispose();
							charts.add(chartNum + 1, new Chart());
							recreateChartControls();
						}
					});
					andButton.addSelectionListener(new SelectionAdapter() {
						@Override
						public void widgetSelected(SelectionEvent e) {
							charts.get(chartNum).addExpression(chartRow + 1, 0);
							recreateChartControls();

						}
					});
				}
			}

		}
		chartSection.layout(true);
		scrolledComposite.reflow(true);
		refreshChartControls();
		Dialog.applyDialogFont(chartSection);
	}

	public BugzillaSearchPage(TaskRepository repository) {
		this(repository, null);
	}

	public BugzillaSearchPage(TaskRepository repository, IRepositoryQuery origQuery) {
		super(Messages.BugzillaSearchPage_Bugzilla_Query, repository, origQuery);
		this.originalQuery = origQuery;

		setNeedsClear(true);
		setDescription(Messages.BugzillaSearchPage_Select_the_Bugzilla_query_parameters);
		setMessage(Messages.BugzillaSearchPage_Enter_search_option);

		toolkit = new FormToolkit(Display.getCurrent());
	}

	@Override
	protected boolean hasRepositoryConfiguration() {
		return getRepositoryConfiguration() != null;
	}

	public RepositoryConfiguration getRepositoryConfiguration() {
		return ((BugzillaRepositoryConnector) getConnector()).getRepositoryConfiguration(getTaskRepository().getUrl());
	}

	@Override
	protected void doRefreshControls() {
		updateAttributesFromConfiguration(product.getSelection());
	}

	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		if (query != null) {
			try {
				restoreStateFromUrl(query.getUrl());
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}

		boolean reflow = false;
		if (commentPattern.getText().length() > 0 || emailPattern2.getText().length() > 0
				|| keywords.getText().length() > 0 || whiteboardPattern.getText().length() > 0
				|| priority.getSelection().length > 0 || resolution.getSelection().length > 0
				|| version.getSelection().length > 0 || target.getSelection().length > 0
				|| hardware.getSelection().length > 0 || os.getSelection().length > 0) {
			moreOptionsSection.setExpanded(true);
			reflow = true;
		}

		if (charts.size() > 0 && charts.get(0).getChartExpression(0, 0).getFieldName() > 0) {
			chartSection.setExpanded(true);
			reflow = true;
		}

		if (reflow) {
			scrolledComposite.reflow(true);
		}
		refreshChartControls();

		setPageComplete(isPageComplete());

		return true;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setUrl(getQueryURL(getTaskRepository(), getQueryParameters()));
		query.setSummary(getQueryTitle());
	}

}
