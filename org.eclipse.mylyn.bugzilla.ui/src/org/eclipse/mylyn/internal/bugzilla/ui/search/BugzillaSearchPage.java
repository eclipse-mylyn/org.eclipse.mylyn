/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *     Frank Becker - improvements
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.ui.search;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.DialogSettings;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.ComboContentAdapter;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposalProvider;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.internal.bugzilla.ui.editor.KeywordsDialog;
import org.eclipse.mylyn.internal.tasks.ui.util.WebBrowserDialog;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.RepositoryStatus;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Rectangle;
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
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.progress.IProgressService;
import org.eclipse.ui.texteditor.ITextEditorActionDefinitionIds;

/**
 * Bugzilla search page
 * 
 * @author Mik Kersten (hardening of prototype)
 * @author Frank Becker
 */
public class BugzillaSearchPage extends AbstractRepositoryQueryPage implements Listener {

	private static final int HEIGHT_ATTRIBUTE_COMBO = 70;

	// protected Combo repositoryCombo = null;

	private static ArrayList<BugzillaSearchData> previousSummaryPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousEmailPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousEmailPatterns2 = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousCommentPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousKeywords = new ArrayList<BugzillaSearchData>(20);

	private boolean firstTime = true;

	private IDialogSettings fDialogSettings;

	private static final String[] patternOperationText = { Messages.BugzillaSearchPage_all_words,
			Messages.BugzillaSearchPage_any_word, Messages.BugzillaSearchPage_regexp,
			Messages.BugzillaSearchPage_notregexp };

	private static final String[] patternOperationValues = { "allwordssubstr", "anywordssubstr", "regexp", "notregexp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private static final String[] emailOperationText = { Messages.BugzillaSearchPage_substring,
			Messages.BugzillaSearchPage_exact, Messages.BugzillaSearchPage_regexp,
			Messages.BugzillaSearchPage_notregexp };

	private static final String[] emailOperationValues = { "substring", "exact", "regexp", "notregexp" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	private static final String[] keywordOperationText = { Messages.BugzillaSearchPage_all,
			Messages.BugzillaSearchPage_any, Messages.BugzillaSearchPage_none };

	private static final String[] keywordOperationValues = { "allwords", "anywords", "nowords" }; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$

	private static final String[] emailRoleValues = { "emailassigned_to1", "emailreporter1", "emailcc1", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"emaillongdesc1" }; //$NON-NLS-1$

	private static final String[] emailRoleValues2 = { "emailassigned_to2", "emailreporter2", "emailcc2", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			"emaillongdesc2" }; //$NON-NLS-1$

	// dialog store id constants
	private final static String DIALOG_BOUNDS_KEY = "ResizableDialogBounds"; //$NON-NLS-1$

	private static final String X = "x"; //$NON-NLS-1$

	private static final String Y = "y"; //$NON-NLS-1$

	private static final String WIDTH = "width"; //$NON-NLS-1$

	private static final String HEIGHT = "height"; //$NON-NLS-1$

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

	protected Text daysText;

	// /** File containing saved queries */
	// protected static SavedQueryFile input;

	// /** "Remember query" button */
	// protected Button saveButton;

	// /** "Saved queries..." button */
	// protected Button loadButton;

	// /** Run a remembered query */
	// protected boolean rememberedQuery = false;

	/** Index of the saved query to run */
	protected int selIndex;

	// --------------- Configuration handling --------------

	// Dialog store taskId constants
	protected final static String PAGE_NAME = "BugzillaSearchPage"; //$NON-NLS-1$

	private static final String STORE_PRODUCT_ID = PAGE_NAME + ".PRODUCT"; //$NON-NLS-1$

	private static final String STORE_COMPONENT_ID = PAGE_NAME + ".COMPONENT"; //$NON-NLS-1$

	private static final String STORE_VERSION_ID = PAGE_NAME + ".VERSION"; //$NON-NLS-1$

	private static final String STORE_MSTONE_ID = PAGE_NAME + ".MILESTONE"; //$NON-NLS-1$

	private static final String STORE_STATUS_ID = PAGE_NAME + ".STATUS"; //$NON-NLS-1$

	private static final String STORE_RESOLUTION_ID = PAGE_NAME + ".RESOLUTION"; //$NON-NLS-1$

	private static final String STORE_SEVERITY_ID = PAGE_NAME + ".SEVERITY"; //$NON-NLS-1$

	private static final String STORE_PRIORITY_ID = PAGE_NAME + ".PRIORITY"; //$NON-NLS-1$

	private static final String STORE_HARDWARE_ID = PAGE_NAME + ".HARDWARE"; //$NON-NLS-1$

	private static final String STORE_OS_ID = PAGE_NAME + ".OS"; //$NON-NLS-1$

	private static final String STORE_SUMMARYMATCH_ID = PAGE_NAME + ".SUMMARYMATCH"; //$NON-NLS-1$

	private static final String STORE_COMMENTMATCH_ID = PAGE_NAME + ".COMMENTMATCH"; //$NON-NLS-1$

	private static final String STORE_EMAILMATCH_ID = PAGE_NAME + ".EMAILMATCH"; //$NON-NLS-1$

	private static final String STORE_EMAIL2MATCH_ID = PAGE_NAME + ".EMAIL2MATCH"; //$NON-NLS-1$

	private static final String STORE_EMAILBUTTON_ID = PAGE_NAME + ".EMAILATTR"; //$NON-NLS-1$

	private static final String STORE_EMAIL2BUTTON_ID = PAGE_NAME + ".EMAIL2ATTR"; //$NON-NLS-1$

	private static final String STORE_SUMMARYTEXT_ID = PAGE_NAME + ".SUMMARYTEXT"; //$NON-NLS-1$

	private static final String STORE_COMMENTTEXT_ID = PAGE_NAME + ".COMMENTTEXT"; //$NON-NLS-1$

	private static final String STORE_EMAILADDRESS_ID = PAGE_NAME + ".EMAILADDRESS"; //$NON-NLS-1$

	private static final String STORE_EMAIL2ADDRESS_ID = PAGE_NAME + ".EMAIL2ADDRESS"; //$NON-NLS-1$

	private static final String STORE_KEYWORDS_ID = PAGE_NAME + ".KEYWORDS"; //$NON-NLS-1$

	private static final String STORE_KEYWORDSMATCH_ID = PAGE_NAME + ".KEYWORDSMATCH"; //$NON-NLS-1$

	// private static final String STORE_REPO_ID = PAGE_NAME + ".REPO";

	private RepositoryConfiguration repositoryConfiguration;

	private final SelectionAdapter updateActionSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (isControlCreated()) {
				setPageComplete(isPageComplete());
			}
		}
	};

	private Text queryTitle;

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

	public BugzillaSearchPage(TaskRepository repository) {
		super(Messages.BugzillaSearchPage_Bugzilla_Query, repository);
		// setTitle(TITLE);
		// setDescription(DESCRIPTION);
		// setImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		// setPageComplete(false);
//		try {
//			repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository, false);
//		} catch (final CoreException e) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					MessageDialog.openError(Display.getDefault().getActiveShell(), "Bugzilla Search Page",
//							"Unable to get configuration. Ensure proper repository configuration in "
//									+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".\n\n");
//				}
//			});
//		}
	}

	public BugzillaSearchPage(TaskRepository repository, IRepositoryQuery origQuery) {
		super(Messages.BugzillaSearchPage_Bugzilla_Query, repository, origQuery);
		originalQuery = origQuery;
		setDescription(Messages.BugzillaSearchPage_Select_the_Bugzilla_query_parameters);
		// setTitle(TITLE);
		// setDescription(DESCRIPTION);
		// setPageComplete(false);
//		try {
//			repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(repository, false);
//		} catch (final CoreException e) {
//			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//				public void run() {
//					MessageDialog.openError(Display.getDefault().getActiveShell(), "Bugzilla Search Page",
//							"Unable to get configuration. Ensure proper repository configuration in "
//									+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".\n\n");
//				}
//			});
//		}
	}

	public void createControl(Composite parent) {
		readConfiguration();

		Composite control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginHeight = 0;
		control.setLayout(layout);
		control.setLayoutData(new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL));

//		if (scontainer == null) {
		// Not presenting in search pane so want query title
//			super.createControl(control);
//			Label lblName = new Label(control, SWT.NONE);
//			final GridData gridData = new GridData();
//			lblName.setLayoutData(gridData);
//			lblName.setText("Query Title:");
//
//			title = new Text(control, SWT.BORDER);
//			title.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
//			title.addModifyListener(new ModifyListener() {
//				public void modifyText(ModifyEvent e) {
//					setPageComplete(isPageComplete());
//				}
//			});
//		}

		// else {
		// // if (repository == null) {
		// // search pane so add repository selection
		//      createRepositoryGroup(control);
		//    }
		createOptionsGroup(control);
		createSearchGroup(control);

		// createSaveQuery(control);
		// createMaxHits(control);
		// input = new SavedQueryFile(BugzillaPlugin.getDefault().getStateLocation().toString(), "/queries");
		// createUpdate(control);
		// if (originalQuery != null) {
		// try {
		//   updateDefaults(originalQuery.getQueryUrl(), String.valueOf(originalQuery.getMaxHits()));
		// } catch (UnsupportedEncodingException e) {
		//   // ignore
		// }
		// }
		Dialog.applyDialogFont(control);
		setControl(control);
		PlatformUI.getWorkbench().getHelpSystem().setHelp(control, BugzillaUiPlugin.SEARCH_PAGE_CONTEXT);
		restoreBounds();
	}

	protected void createOptionsGroup(Composite control) {
		GridLayout sashFormLayout = new GridLayout();
		sashFormLayout.numColumns = 4;
		sashFormLayout.marginHeight = 5;
		sashFormLayout.marginWidth = 5;
		sashFormLayout.horizontalSpacing = 5;

		final Composite composite = new Composite(control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		final GridLayout gridLayout = new GridLayout();
		gridLayout.marginBottom = 8;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 4;
		composite.setLayout(gridLayout);

		if (!inSearchContainer()) {
			final Label queryTitleLabel = new Label(composite, SWT.NONE);
			queryTitleLabel.setText(Messages.BugzillaSearchPage_Query_Title);

			queryTitle = new Text(composite, SWT.BORDER);
			queryTitle.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 3, 1));
			if (originalQuery != null) {
				queryTitle.setText(originalQuery.getSummary());
			}
			queryTitle.addModifyListener(new ModifyListenerImplementation());
			queryTitle.setFocus();
		}

		// Info text
		Label labelSummary = new Label(composite, SWT.LEFT);
		labelSummary.setText(Messages.BugzillaSearchPage_Summary);
//		labelSummary.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
		//labelSummary.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		// Pattern combo
		summaryPattern = new Combo(composite, SWT.SINGLE | SWT.BORDER);
		summaryPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		summaryPattern.addModifyListener(new ModifyListenerImplementation());
		summaryPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(summaryPattern, summaryOperation, previousSummaryPatterns);
			}
		});

		summaryOperation = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		summaryOperation.setItems(patternOperationText);
		summaryOperation.setText(patternOperationText[0]);
		summaryOperation.select(0);

		// Info text
		Label labelComment = new Label(composite, SWT.LEFT);
		labelComment.setText(Messages.BugzillaSearchPage_Comment);
		//labelComment.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		// Comment pattern combo
		commentPattern = new Combo(composite, SWT.SINGLE | SWT.BORDER);
		commentPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 2, 1));
		commentPattern.addModifyListener(new ModifyListenerImplementation());
		commentPattern.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(commentPattern, commentOperation, previousCommentPatterns);
			}
		});

		commentOperation = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		commentOperation.setItems(patternOperationText);
		commentOperation.setText(patternOperationText[0]);
		commentOperation.select(0);

		Label labelEmail = new Label(composite, SWT.LEFT);
		labelEmail.setText(Messages.BugzillaSearchPage_Email);
		//labelEmail.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		// pattern combo
		emailPattern = new Combo(composite, SWT.SINGLE | SWT.BORDER);
		emailPattern.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
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

		Composite emailComposite = new Composite(composite, SWT.NONE);
		emailComposite.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		GridLayout emailLayout = new GridLayout();
		emailLayout.marginWidth = 0;
		emailLayout.marginHeight = 0;
		emailLayout.horizontalSpacing = 2;
		emailLayout.numColumns = 4;
		emailComposite.setLayout(emailLayout);

		Button button0 = new Button(emailComposite, SWT.CHECK);
		button0.setText(Messages.BugzillaSearchPage_owner);

		Button button1 = new Button(emailComposite, SWT.CHECK);
		button1.setText(Messages.BugzillaSearchPage_reporter);

		Button button2 = new Button(emailComposite, SWT.CHECK);
		button2.setText(Messages.BugzillaSearchPage_cc);

		Button button3 = new Button(emailComposite, SWT.CHECK);
		button3.setText(Messages.BugzillaSearchPage_commenter);

		emailButtons = new Button[] { button0, button1, button2, button3 };

		// operation combo
		emailOperation = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		emailOperation.setItems(emailOperationText);
		emailOperation.setText(emailOperationText[0]);
		emailOperation.select(0);

		// Email2

		Label labelEmail2 = new Label(composite, SWT.LEFT);
		labelEmail2.setText(Messages.BugzillaSearchPage_Email_2);
		//labelEmail.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		// pattern combo
		emailPattern2 = new Combo(composite, SWT.SINGLE | SWT.BORDER);
		emailPattern2.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		emailPattern2.addModifyListener(new ModifyListenerImplementation());
		emailPattern2.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(emailPattern2, emailOperation2, previousEmailPatterns2);
			}
		});
		ContentAssistCommandAdapter adapter2 = new ContentAssistCommandAdapter(emailPattern2,
				new ComboContentAdapter(), proposalProvider, ITextEditorActionDefinitionIds.CONTENT_ASSIST_PROPOSALS,
				new char[0], true);
		adapter.setLabelProvider(proposalLabelProvider);
		adapter2.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		Composite emailComposite2 = new Composite(composite, SWT.NONE);
		emailComposite2.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		GridLayout emailLayout2 = new GridLayout();
		emailLayout2.marginWidth = 0;
		emailLayout2.marginHeight = 0;
		emailLayout2.horizontalSpacing = 2;
		emailLayout2.numColumns = 4;
		emailComposite2.setLayout(emailLayout2);

		Button e2button0 = new Button(emailComposite2, SWT.CHECK);
		e2button0.setText(Messages.BugzillaSearchPage_owner);

		Button e2button1 = new Button(emailComposite2, SWT.CHECK);
		e2button1.setText(Messages.BugzillaSearchPage_reporter);

		Button e2button2 = new Button(emailComposite2, SWT.CHECK);
		e2button2.setText(Messages.BugzillaSearchPage_cc);

		Button e2button3 = new Button(emailComposite2, SWT.CHECK);
		e2button3.setText(Messages.BugzillaSearchPage_commenter);

		emailButtons2 = new Button[] { e2button0, e2button1, e2button2, e2button3 };

		// operation combo
		emailOperation2 = new Combo(composite, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		emailOperation2.setItems(emailOperationText);
		emailOperation2.setText(emailOperationText[0]);
		emailOperation2.select(0);

		/////

		Label labelKeywords = new Label(composite, SWT.NONE);
		labelKeywords.setText(Messages.BugzillaSearchPage_Keywords);
//		labelKeywords.setLayoutData(new GridData(LABEL_WIDTH, SWT.DEFAULT));
		//labelKeywords.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));

		Composite keywordsComposite = new Composite(composite, SWT.NONE);
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

		Button keywordsSelectButton = new Button(keywordsComposite, SWT.NONE);
		keywordsSelectButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (repositoryConfiguration != null && getShell() != null) {
					KeywordsDialog dialog = new KeywordsDialog(getShell(), keywords.getText(), //
							repositoryConfiguration.getKeywords());
					if (dialog.open() == Window.OK) {
						keywords.setText(dialog.getSelectedKeywordsString());
					}
				}
			}
		});
		keywordsSelectButton.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false));
		keywordsSelectButton.setText(Messages.BugzillaSearchPage_Select_);
		SashForm sashForm = new SashForm(control, SWT.VERTICAL);
		sashForm.setLayout(sashFormLayout);
		final GridData gd_sashForm = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_sashForm.widthHint = 500;
		sashForm.setLayoutData(gd_sashForm);

		GridLayout topLayout = new GridLayout();
		topLayout.numColumns = 4;
		SashForm topForm = new SashForm(sashForm, SWT.NONE);
		GridData topLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		topLayoutData.widthHint = 500;
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
				if (product.getSelectionIndex() != -1) {
					String[] selectedProducts = product.getSelection();
					updateAttributesFromConfiguration(selectedProducts);
				} else {
					updateAttributesFromConfiguration(null);
				}
				if (restoring) {
					restoring = false;
					restoreWidgetValues();
				}
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

		Composite versionComposite = new Composite(topForm, SWT.NONE);
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

		Composite milestoneComposite = new Composite(topForm, SWT.NONE);
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

		SashForm bottomForm = new SashForm(sashForm, SWT.NONE);
		GridLayout bottomLayout = new GridLayout();
		bottomLayout.numColumns = 6;
		bottomForm.setLayout(bottomLayout);
		GridData bottomLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 4, 1);
		bottomLayoutData.heightHint = 119;
		bottomLayoutData.widthHint = 400;
		bottomForm.setLayoutData(bottomLayoutData);

		Composite statusComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout statusLayout = new GridLayout();
		statusLayout.marginTop = 7;
		statusLayout.marginWidth = 0;
		statusLayout.horizontalSpacing = 0;
		statusLayout.marginHeight = 0;
		statusComposite.setLayout(statusLayout);

		Label statusLabel = new Label(statusComposite, SWT.LEFT);
		statusLabel.setText(Messages.BugzillaSearchPage_Status);
		statusLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		status = new List(statusComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_status = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_status.heightHint = 60;
		status.setLayoutData(gd_status);
		status.addSelectionListener(updateActionSelectionAdapter);

		Composite resolutionComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout resolutionLayout = new GridLayout();
		resolutionLayout.marginTop = 7;
		resolutionLayout.marginWidth = 0;
		resolutionLayout.marginHeight = 0;
		resolutionLayout.horizontalSpacing = 0;
		resolutionComposite.setLayout(resolutionLayout);

		Label resolutionLabel = new Label(resolutionComposite, SWT.LEFT);
		resolutionLabel.setText(Messages.BugzillaSearchPage_Resolution);
		resolutionLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		resolution = new List(resolutionComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_resolution = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_resolution.heightHint = 60;
		resolution.setLayoutData(gd_resolution);
		resolution.addSelectionListener(updateActionSelectionAdapter);

		Composite priorityComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout priorityLayout = new GridLayout();
		priorityLayout.marginTop = 7;
		priorityLayout.marginWidth = 0;
		priorityLayout.marginHeight = 0;
		priorityLayout.horizontalSpacing = 0;
		priorityComposite.setLayout(priorityLayout);

		Label priorityLabel = new Label(priorityComposite, SWT.LEFT);
		priorityLabel.setText(Messages.BugzillaSearchPage_PROORITY);
		priorityLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		priority = new List(priorityComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_priority = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_priority.heightHint = 60;
		priority.setLayoutData(gd_priority);
		priority.addSelectionListener(updateActionSelectionAdapter);

		Composite severityComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout severityLayout = new GridLayout();
		severityLayout.marginTop = 7;
		severityLayout.marginWidth = 0;
		severityLayout.marginHeight = 0;
		severityLayout.horizontalSpacing = 0;
		severityComposite.setLayout(severityLayout);

		Label severityLabel = new Label(severityComposite, SWT.LEFT);
		severityLabel.setText(Messages.BugzillaSearchPage_Severity);
		severityLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		severity = new List(severityComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_severity = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_severity.heightHint = 60;
		severity.setLayoutData(gd_severity);
		severity.addSelectionListener(updateActionSelectionAdapter);

		Composite hardwareComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout hardwareLayout = new GridLayout();
		hardwareLayout.marginTop = 7;
		hardwareLayout.marginWidth = 0;
		hardwareLayout.marginHeight = 0;
		hardwareLayout.horizontalSpacing = 0;
		hardwareComposite.setLayout(hardwareLayout);

		Label hardwareLabel = new Label(hardwareComposite, SWT.LEFT);
		hardwareLabel.setText(Messages.BugzillaSearchPage_Hardware);
		hardwareLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		hardware = new List(hardwareComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_hardware = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_hardware.heightHint = 60;
		hardware.setLayoutData(gd_hardware);
		hardware.addSelectionListener(updateActionSelectionAdapter);

		Composite osComposite = new Composite(bottomForm, SWT.NONE);
		GridLayout osLayout = new GridLayout();
		osLayout.marginTop = 7;
		osLayout.marginWidth = 0;
		osLayout.marginHeight = 0;
		osLayout.horizontalSpacing = 0;
		osComposite.setLayout(osLayout);

		Label osLabel = new Label(osComposite, SWT.LEFT);
		osLabel.setText(Messages.BugzillaSearchPage_Operating_System);
		osLabel.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		os = new List(osComposite, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		final GridData gd_os = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd_os.heightHint = 60;
		os.setLayoutData(gd_os);
		os.addSelectionListener(updateActionSelectionAdapter);
		bottomForm.setWeights(new int[] { 88, 90, 50, 77, 88, 85 });
	}

	private void createSearchGroup(Composite control) {
		Composite composite = new Composite(control, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout gridLayout = new GridLayout();
		gridLayout.marginTop = 7;
		gridLayout.marginHeight = 0;
		gridLayout.marginWidth = 0;
		gridLayout.numColumns = 2;
		composite.setLayout(gridLayout);

		Label changedInTheLabel = new Label(composite, SWT.LEFT);
		changedInTheLabel.setLayoutData(new GridData());
		changedInTheLabel.setText(Messages.BugzillaSearchPage_Changed_in);

		Composite updateComposite = new Composite(composite, SWT.NONE);
		updateComposite.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		GridLayout updateLayout = new GridLayout(3, false);
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

		Button updateButton = new Button(updateComposite, SWT.PUSH);
		updateButton.setText(Messages.BugzillaSearchPage_Update_Attributes_from_Repository);
		updateButton.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, true, false));
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getTaskRepository() != null) {
//					try {

					updateConfiguration(true);

//					} catch (final CoreException e1) {
//						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
//							public void run() {
//								MessageDialog.openError(Display.getDefault().getActiveShell(), "Bugzilla Search Page",
//										"Unable to get configuration. Ensure proper repository configuration in "
//												+ TasksUiPlugin.LABEL_VIEW_REPOSITORIES + ".\n\n");
//							}
//						});
//					}
				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							IBugzillaConstants.TITLE_MESSAGE_DIALOG,
							Messages.BugzillaSearchPage_No_repository_available);
				}
			}
		});
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

		// loadButton = new Button(group, SWT.PUSH | SWT.LEFT);
		// loadButton.setText("Saved Queries...");
		// final BugzillaSearchPage bsp = this;
		// loadButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent event) {
		// GetQueryDialog qd = new GetQueryDialog(getShell(), "Saved Queries",
		// input);
		// if (qd.open() == InputDialog.OK) {
		// selIndex = qd.getSelected();
		// if (selIndex != -1) {
		// rememberedQuery = true;
		// performAction();
		// bsp.getShell().close();
		// }
		// }
		// }
		// });
		// loadButton.setEnabled(true);
		// loadButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		//
		// saveButton = new Button(group, SWT.PUSH | SWT.LEFT);
		// saveButton.setText("Remember...");
		// saveButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent event) {
		// SaveQueryDialog qd = new SaveQueryDialog(getShell(), "Remember Query");
		// if (qd.open() == InputDialog.OK) {
		// String qName = qd.getText();
		// if (qName != null && qName.compareTo("") != 0) {
		// try {
		// input.add(getQueryParameters().toString(), qName, summaryPattern.getText());
		// } catch (UnsupportedEncodingException e) {
		// /*
		// * Do nothing. Every implementation of the Java
		// * platform is required to support the standard
		// * charset "UTF-8"
		// */
		// }
		// }
		// }
		// }
		// });
		// saveButton.setEnabled(true);
		// saveButton.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		return group;
	}

	// public static SavedQueryFile getInput() {
	// return input;
	// }

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

		String summaryText = summaryPattern.getText();
		BugzillaUiPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MOST_RECENT_QUERY, summaryText);

		return super.performSearch();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && summaryPattern != null) {
			if (firstTime) {
				// Set<TaskRepository> repositories = TasksUiPlugin.getRepositoryManager().getRepositories(BugzillaCorePlugin.REPOSITORY_KIND);
				// String[] repositoryUrls = new String[repositories.size()];
				// int i = 0;
				// int indexToSelect = 0;
				// for (Iterator<TaskRepository> iter = repositories.iterator(); iter.hasNext();) {
				// TaskRepository currRepsitory = iter.next();
				// // if (i == 0 && repository == null) {
				// // repository = currRepsitory;
				// // indexToSelect = 0;
				// // }
				// if (repository != null && repository.equals(currRepsitory)) {
				// indexToSelect = i;
				// }
				// repositoryUrls[i] = currRepsitory.getUrl();
				// i++;
				// }

				// IDialogSettings settings = getDialogSettings();
				// if (repositoryCombo != null) {
				// repositoryCombo.setItems(repositoryUrls);
				// if (repositoryUrls.length == 0) {
				// MessageDialog.openInformation(Display.getCurrent().getActiveShell(), IBugzillaConstants.TITLE_MESSAGE_DIALOG,
				// TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
				// } else {
				// String selectRepo = settings.get(STORE_REPO_ID);
				// if (selectRepo != null && repositoryCombo.indexOf(selectRepo) > -1) {
				// repositoryCombo.setText(selectRepo);
				// repository = TasksUiPlugin.getRepositoryManager().getRepository(
				// BugzillaCorePlugin.REPOSITORY_KIND, repositoryCombo.getText());
				// if (repository == null) {
				// repository = TasksUiPlugin.getRepositoryManager().getDefaultRepository( BugzillaCorePlugin.REPOSITORY_KIND);
				// }
				// } else {
				// repositoryCombo.select(indexToSelect);
				// }
				// updateAttributesFromRepository(repositoryCombo.getText(), null, false);
				// }
				// }

				firstTime = false;
				// Set item and text here to prevent page from resizing
				for (String searchPattern : getPreviousPatterns(previousSummaryPatterns)) {
					summaryPattern.add(searchPattern);
				}
				// summaryPattern.setItems(getPreviousPatterns(previousSummaryPatterns));
				for (String comment : getPreviousPatterns(previousCommentPatterns)) {
					commentPattern.add(comment);
				}
				// commentPattern.setItems(getPreviousPatterns(previousCommentPatterns));
				for (String email : getPreviousPatterns(previousEmailPatterns)) {
					emailPattern.add(email);
				}

				for (String email : getPreviousPatterns(previousEmailPatterns2)) {
					emailPattern2.add(email);
				}

				// emailPattern.setItems(getPreviousPatterns(previousEmailPatterns));
				for (String keyword : getPreviousPatterns(previousKeywords)) {
					keywords.add(keyword);
				}

				// TODO: update status, resolution, severity etc if possible...
				if (getTaskRepository() != null) {
					updateAttributesFromConfiguration(null);
					if (product.getItemCount() == 0) {
						try {
							repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(
									getTaskRepository(), true, new NullProgressMonitor());
							updateAttributesFromConfiguration(null);
						} catch (final CoreException e1) {
							PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
								public void run() {
									MessageDialog.openError(Display.getDefault().getActiveShell(),
											Messages.BugzillaSearchPage_Bugzilla_Search_Page, MessageFormat.format(
													Messages.BugzillaSearchPage_Unable_to_get_configuration_X,
													Messages.BugzillaSearchPage_Task_Repositories)
													+ "\n\n"); //$NON-NLS-1$
								}
							});
						}
					}
				}
				if (originalQuery != null) {
					try {
						updateDefaults(originalQuery.getUrl());
					} catch (UnsupportedEncodingException e) {
						// ignore
					}
				}
			}

			/*
			 * hack: we have to select the correct product, then update the
			 * attributes so the component/version/milestone lists have the
			 * proper values, then we can restore all the widget selections.
			 */
			if (getTaskRepository() != null) {
				IDialogSettings settings = getDialogSettings();
				String repoId = "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
				if (getWizard() == null && restoreQueryOptions && settings.getArray(STORE_PRODUCT_ID + repoId) != null
						&& product != null) {
					product.setSelection(nonNullArray(settings, STORE_PRODUCT_ID + repoId));
					if (product.getSelection().length > 0) {
						updateAttributesFromConfiguration(product.getSelection());
					}
					restoreWidgetValues();
				}
			}
			setPageComplete(isPageComplete());
			if (getWizard() == null) {
				// TODO: wierd check
				summaryPattern.setFocus();
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
		if (daysText != null) {
			String days = daysText.getText();
			if (days.length() > 0) {
				try {
					if (Integer.parseInt(days) < 0) {
						throw new NumberFormatException();
					}
				} catch (NumberFormatException ex) {
					setErrorMessage(NLS.bind(Messages.BugzillaSearchPage_Number_of_days_must_be_a_positive_integer,
							days));
					return false;
				}
			}
		}
		return getWizard() == null ? canQuery() : canQuery() && super.isPageComplete();
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
	 * Creates the bugzilla query URL start.
	 * 
	 * Example: https://bugs.eclipse.org/bugs/buglist.cgi?
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
	 * Goes through the query form and builds up the query parameters.
	 * 
	 * Example: short_desc_type=substring&amp;short_desc=bla&amp; ... TODO: The encoding here should match
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
			appendToBuffer(sb, "&ref_platform=", hardware.getItem(element)); //$NON-NLS-1$
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

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		getDialogSettings();
	}

	private void updateAttributesFromConfiguration(String[] selectedProducts) {
		if (repositoryConfiguration == null) {
			updateConfiguration(false);
		}
		if (repositoryConfiguration != null) {
			String[] saved_product = product.getSelection();
			String[] saved_component = component.getSelection();
			String[] saved_version = version.getSelection();
			String[] saved_target = target.getSelection();
			String[] saved_status = status.getSelection();
			String[] saved_resolution = resolution.getSelection();
			String[] saved_severity = severity.getSelection();
			String[] saved_priority = priority.getSelection();
			String[] saved_hardware = hardware.getSelection();
			String[] saved_os = os.getSelection();

			if (selectedProducts == null) {
				java.util.List<String> products = repositoryConfiguration.getProducts();
				String[] productsList = products.toArray(new String[products.size()]);
				Arrays.sort(productsList, String.CASE_INSENSITIVE_ORDER);
				product.setItems(productsList);
			}

			String[] componentsList = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_COMPONENT,
					selectedProducts, repositoryConfiguration);
			Arrays.sort(componentsList, String.CASE_INSENSITIVE_ORDER);
			component.setItems(componentsList);

			version.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_VERSION, selectedProducts,
					repositoryConfiguration));
			target.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_TARGET, selectedProducts,
					repositoryConfiguration));
			status.setItems(convertStringListToArray(repositoryConfiguration.getStatusValues()));
			resolution.setItems(convertStringListToArray(repositoryConfiguration.getResolutions()));
			severity.setItems(convertStringListToArray(repositoryConfiguration.getSeverities()));
			priority.setItems(convertStringListToArray(repositoryConfiguration.getPriorities()));
			hardware.setItems(convertStringListToArray(repositoryConfiguration.getPlatforms()));
			os.setItems(convertStringListToArray(repositoryConfiguration.getOSs()));

			setSelection(product, saved_product);
			setSelection(component, saved_component);
			setSelection(version, saved_version);
			setSelection(target, saved_target);
			setSelection(status, saved_status);
			setSelection(resolution, saved_resolution);
			setSelection(severity, saved_severity);
			setSelection(priority, saved_priority);
			setSelection(hardware, saved_hardware);
			setSelection(os, saved_os);
		}
	}

	@Override
	public boolean canFlipToNextPage() {
		// if (getErrorMessage() != null)
		// return false;
		//
		// return true;
		return false;
	}

	public void handleEvent(Event event) {
		if (getWizard() != null) {
			getWizard().getContainer().updateButtons();
		}
	}

	/**
	 * TODO: get rid of this?
	 */
	public void updateDefaults(String startingUrl) throws UnsupportedEncodingException {
		// String serverName = startingUrl.substring(0,
		// startingUrl.indexOf("?"));

		startingUrl = startingUrl.substring(startingUrl.indexOf("?") + 1); //$NON-NLS-1$
		String[] options = startingUrl.split("&"); //$NON-NLS-1$
		for (String option : options) {
			String key = option.substring(0, option.indexOf("=")); //$NON-NLS-1$
			String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1), //$NON-NLS-1$
					getTaskRepository().getCharacterEncoding());
			if (key == null) {
				continue;
			}

			if (key.equals("short_desc")) { //$NON-NLS-1$
				summaryPattern.setText(value);
			} else if (key.equals("short_desc_type")) { //$NON-NLS-1$
				if (value.equals("allwordssubstr")) { //$NON-NLS-1$
					value = "all words"; //$NON-NLS-1$
				} else if (value.equals("anywordssubstr")) { //$NON-NLS-1$
					value = "any word"; //$NON-NLS-1$
				}
				int index = 0;
				for (String item : summaryOperation.getItems()) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
				}
				if (index < summaryOperation.getItemCount()) {
					summaryOperation.select(index);
				}
			} else if (key.equals("product")) { //$NON-NLS-1$
				String[] sel = product.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				product.setSelection(selList.toArray(sel));
				updateAttributesFromConfiguration(selList.toArray(sel));
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
				if (value.equals("allwordssubstr")) { //$NON-NLS-1$
					value = "all words"; //$NON-NLS-1$
				} else if (value.equals("anywordssubstr")) { //$NON-NLS-1$
					value = "any word"; //$NON-NLS-1$
				}
				int index = 0;
				for (String item : commentOperation.getItems()) {
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
			} else if (key.equals("ref_platform")) { //$NON-NLS-1$
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
			} else if (key.equals("emailtype1")) { //$NON-NLS-1$
				int index = 0;
				for (String item : emailOperation.getItems()) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
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
			} else if (key.equals("emailtype2")) { //$NON-NLS-1$
				int index = 0;
				for (String item : emailOperation2.getItems()) {
					if (item.compareTo(value) == 0) {
						break;
					}
					index++;
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
			}
		}
	}

	private String[] nonNullArray(IDialogSettings settings, String id) {
		String[] value = settings.getArray(id);
		if (value == null) {
			return new String[] {};
		}
		return value;
	}

	private void restoreWidgetValues() {
		try {
			IDialogSettings settings = getDialogSettings();
			String repoId = "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
			if (!restoreQueryOptions || settings.getArray(STORE_PRODUCT_ID + repoId) == null || product == null) {
				return;
			}

			// set widgets to stored values
			product.setSelection(nonNullArray(settings, STORE_PRODUCT_ID + repoId));
			component.setSelection(nonNullArray(settings, STORE_COMPONENT_ID + repoId));
			version.setSelection(nonNullArray(settings, STORE_VERSION_ID + repoId));
			target.setSelection(nonNullArray(settings, STORE_MSTONE_ID + repoId));
			status.setSelection(nonNullArray(settings, STORE_STATUS_ID + repoId));
			resolution.setSelection(nonNullArray(settings, STORE_RESOLUTION_ID + repoId));
			severity.setSelection(nonNullArray(settings, STORE_SEVERITY_ID + repoId));
			priority.setSelection(nonNullArray(settings, STORE_PRIORITY_ID + repoId));
			hardware.setSelection(nonNullArray(settings, STORE_HARDWARE_ID + repoId));
			os.setSelection(nonNullArray(settings, STORE_OS_ID + repoId));
			summaryOperation.select(settings.getInt(STORE_SUMMARYMATCH_ID + repoId));
			commentOperation.select(settings.getInt(STORE_COMMENTMATCH_ID + repoId));
			emailOperation.select(settings.getInt(STORE_EMAILMATCH_ID + repoId));
			for (int i = 0; i < emailButtons.length; i++) {
				emailButtons[i].setSelection(settings.getBoolean(STORE_EMAILBUTTON_ID + i + repoId));
			}
			summaryPattern.setText(settings.get(STORE_SUMMARYTEXT_ID + repoId));
			commentPattern.setText(settings.get(STORE_COMMENTTEXT_ID + repoId));
			emailPattern.setText(settings.get(STORE_EMAILADDRESS_ID + repoId));
			try {
				emailOperation2.select(settings.getInt(STORE_EMAIL2MATCH_ID + repoId));
			} catch (Exception e) {
				//ignore
			}
			for (int i = 0; i < emailButtons2.length; i++) {
				emailButtons2[i].setSelection(settings.getBoolean(STORE_EMAIL2BUTTON_ID + i + repoId));
			}
			emailPattern2.setText(settings.get(STORE_EMAIL2ADDRESS_ID + repoId));
			if (settings.get(STORE_KEYWORDS_ID + repoId) != null) {
				keywords.setText(settings.get(STORE_KEYWORDS_ID + repoId));
				keywordsOperation.select(settings.getInt(STORE_KEYWORDSMATCH_ID + repoId));
			}
		} catch (IllegalArgumentException e) {
			//ignore
		}
	}

	@Override
	public void saveState() {
		String repoId = "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
		IDialogSettings settings = getDialogSettings();
		settings.put(STORE_PRODUCT_ID + repoId, product.getSelection());
		settings.put(STORE_COMPONENT_ID + repoId, component.getSelection());
		settings.put(STORE_VERSION_ID + repoId, version.getSelection());
		settings.put(STORE_MSTONE_ID + repoId, target.getSelection());
		settings.put(STORE_STATUS_ID + repoId, status.getSelection());
		settings.put(STORE_RESOLUTION_ID + repoId, resolution.getSelection());
		settings.put(STORE_SEVERITY_ID + repoId, severity.getSelection());
		settings.put(STORE_PRIORITY_ID + repoId, priority.getSelection());
		settings.put(STORE_HARDWARE_ID + repoId, hardware.getSelection());
		settings.put(STORE_OS_ID + repoId, os.getSelection());
		settings.put(STORE_SUMMARYMATCH_ID + repoId, summaryOperation.getSelectionIndex());
		settings.put(STORE_COMMENTMATCH_ID + repoId, commentOperation.getSelectionIndex());
		settings.put(STORE_EMAILMATCH_ID + repoId, emailOperation.getSelectionIndex());
		for (int i = 0; i < emailButtons.length; i++) {
			settings.put(STORE_EMAILBUTTON_ID + i + repoId, emailButtons[i].getSelection());
		}
		settings.put(STORE_SUMMARYTEXT_ID + repoId, summaryPattern.getText());
		settings.put(STORE_COMMENTTEXT_ID + repoId, commentPattern.getText());
		settings.put(STORE_EMAILADDRESS_ID + repoId, emailPattern.getText());
		settings.put(STORE_EMAIL2ADDRESS_ID + repoId, emailPattern2.getText());
		settings.put(STORE_EMAIL2MATCH_ID + repoId, emailOperation2.getSelectionIndex());
		for (int i = 0; i < emailButtons2.length; i++) {
			settings.put(STORE_EMAIL2BUTTON_ID + i + repoId, emailButtons2[i].getSelection());
		}

		settings.put(STORE_KEYWORDS_ID + repoId, keywords.getText());
		settings.put(STORE_KEYWORDSMATCH_ID + repoId, keywordsOperation.getSelectionIndex());
		// settings.put(STORE_REPO_ID, repositoryCombo.getText());
	}

	private void saveBounds(Rectangle bounds) {
		if (inSearchContainer()) {
			return;
		}

		IDialogSettings settings = getDialogSettings();
		IDialogSettings dialogBounds = settings.getSection(DIALOG_BOUNDS_KEY);
		if (dialogBounds == null) {
			dialogBounds = new DialogSettings(DIALOG_BOUNDS_KEY);
			settings.addSection(dialogBounds);
		}
		dialogBounds.put(X, bounds.x);
		dialogBounds.put(Y, bounds.y);
		dialogBounds.put(WIDTH, bounds.width);
		dialogBounds.put(HEIGHT, bounds.height);
	}

	private void restoreBounds() {
		if (inSearchContainer()) {
			return;
		}

		IDialogSettings settings = getDialogSettings();
		IDialogSettings dialogBounds = settings.getSection(DIALOG_BOUNDS_KEY);
		Shell shell = getShell();
		if (shell != null) {
			Rectangle bounds = shell.getBounds();

			if (bounds != null && dialogBounds != null) {
				try {
					bounds.x = dialogBounds.getInt(X);
					bounds.y = dialogBounds.getInt(Y);
					bounds.height = dialogBounds.getInt(HEIGHT);
					bounds.width = dialogBounds.getInt(WIDTH);
					shell.setBounds(bounds);
				} catch (NumberFormatException e) {
					// silently ignored
				}
			}
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

	private void updateConfiguration(final boolean force) {
		if (getTaskRepository() != null) {
			IRunnableWithProgress updateRunnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					try {
						monitor.beginTask(Messages.BugzillaSearchPage_Updating_search_options_,
								IProgressMonitor.UNKNOWN);
						repositoryConfiguration = BugzillaCorePlugin.getRepositoryConfiguration(getTaskRepository(),
								force, monitor);
					} catch (final Exception e) {
						throw new InvocationTargetException(e);
					} finally {
						monitor.done();
					}
				}
			};

			try {
				// TODO: make cancelable (bug 143011)
				if (getContainer() != null) {
					getContainer().run(true, true, updateRunnable);
				} else if (getSearchContainer() != null) {
					getSearchContainer().getRunnableContext().run(true, true, updateRunnable);
				} else {
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					service.busyCursorWhile(updateRunnable);
				}

			} catch (InvocationTargetException ex) {
				Shell shell = null;
				shell = getShell();
				if (ex.getCause() instanceof CoreException) {
					CoreException cause = ((CoreException) ex.getCause());
					if (cause.getStatus() instanceof RepositoryStatus
							&& ((RepositoryStatus) cause.getStatus()).isHtmlMessage()) {
						// TOOD: use StatusManager

//									this.setControlsEnabled(false);
//									scontainer.setPerformActionEnabled(false);
						if (shell != null) {
							shell.setEnabled(false);
						}
						// TODO: eliminate use of internal api
						WebBrowserDialog dialog = new WebBrowserDialog(shell,
								Messages.BugzillaSearchPage_Error_updating_search_options, null, cause.getStatus()
										.getMessage(), NONE, new String[] { IDialogConstants.OK_LABEL }, 0,
								((RepositoryStatus) cause.getStatus()).getHtmlMessage());
						dialog.setBlockOnOpen(true);
						dialog.open();
						if (shell != null) {
							shell.setEnabled(true);
						}
						return;
//									this.setPageComplete(this.isPageComplete());
//									this.setControlsEnabled(true);
					} else {
						StatusHandler.log(new Status(IStatus.ERROR, BugzillaUiPlugin.ID_PLUGIN, cause.getMessage(),
								cause));
					}
				}
				if (ex.getCause() instanceof OperationCanceledException) {
					return;
				}

				MessageDialog.openError(shell, Messages.BugzillaSearchPage_Error_updating_search_options,
						MessageFormat.format(Messages.BugzillaSearchPage_Error_was_X, ex.getCause().getMessage()));
				return;

			} catch (InterruptedException ex) {
				return;
			}

			updateAttributesFromConfiguration(null);
		}
	}

	@Override
	public Shell getShell() {
		Shell shell = null;
		if (getWizard() != null && getWizard().getContainer() != null) {
			shell = getWizard().getContainer().getShell();
		}
		if (shell == null && getControl() != null) {
			shell = getControl().getShell();
		}
		return shell;
	}

	@Override
	public String getQueryTitle() {
		return (queryTitle != null) ? queryTitle.getText() : ""; //$NON-NLS-1$
	}

	private void setSelection(List listControl, String[] selection) {
		for (String item : selection) {
			int index = listControl.indexOf(item);
			if (index > -1) {
				listControl.select(index);
			}
		}
		if (listControl.getSelectionCount() > 0) {
			listControl.showSelection();
		} else {
			listControl.select(0);
			listControl.showSelection();
			listControl.deselectAll();
		}

	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setUrl(getQueryURL(getTaskRepository(), getQueryParameters()));
		query.setSummary(getQueryTitle());
		Shell shell = getShell();
		if (shell != null) {
			saveBounds(shell.getBounds());
		}
	}

}
