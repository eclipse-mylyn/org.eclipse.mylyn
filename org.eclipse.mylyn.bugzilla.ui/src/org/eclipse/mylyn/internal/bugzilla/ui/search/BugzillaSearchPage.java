/*******************************************************************************
 * Copyright (c) 2003, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.internal.bugzilla.ui.search;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaRepositoryQuery;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.ui.BugzillaUiPlugin;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.search.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.help.WorkbenchHelpSystem;
import org.eclipse.ui.progress.IProgressService;

/**
 * Bugzilla search page
 * 
 * @author Mik Kersten (hardening of prototype)
 */
public class BugzillaSearchPage extends AbstractRepositoryQueryPage implements Listener {

	private static final String NUM_DAYS_POSITIVE = "Number of days must be a positive integer. ";

	private static final String TITLE_BUGZILLA_QUERY = "Bugzilla Query";

	private static final int HEIGHT_ATTRIBUTE_COMBO = 60;

	protected Combo summaryPattern = null;

	// protected Combo repositoryCombo = null;

	private static ArrayList<BugzillaSearchData> previousSummaryPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousEmailPatterns = new ArrayList<BugzillaSearchData>(20);

	private static ArrayList<BugzillaSearchData> previousCommentPatterns = new ArrayList<BugzillaSearchData>(20);

	private boolean firstTime = true;

	private IDialogSettings fDialogSettings;

	private static final String[] patternOperationText = { "all words", "any word", "regexp" };

	private static final String[] patternOperationValues = { "allwordssubstr", "anywordssubstr", "regexp" };

	private static final String[] emailOperationText = { "substring", "exact", "regexp" };

	private static final String[] emailOperationValues = { "substring", "exact", "regexp" };

	private static final String[] emailRoleValues = { "emailassigned_to1", "emailreporter1", "emailcc1",
			"emaillongdesc1" };

	// protected IPreferenceStore prefs =
	// BugzillaUiPlugin.getDefault().getPreferenceStore();

	private BugzillaRepositoryQuery originalQuery = null;

	protected boolean restoring = false;

	private boolean restoreQueryOptions = true;

	private SelectionAdapter updateActionSelectionAdapter = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (scontainer != null) {
				scontainer.setPerformActionEnabled(canQuery());
			}
		}
	};

	// private TaskRepository selectedRepository = null;

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

	public BugzillaSearchPage() {
		super(TITLE_BUGZILLA_QUERY);
		// setTitle(TITLE);
		// setDescription(DESCRIPTION);
		// setPageComplete(false);
	}

	public BugzillaSearchPage(TaskRepository repository) {
		super(TITLE_BUGZILLA_QUERY);
		this.repository = repository;
		// setTitle(TITLE);
		// setDescription(DESCRIPTION);
		// setImageDescriptor(TaskListImages.BANNER_REPOSITORY);
		// setPageComplete(false);
	}

	public BugzillaSearchPage(TaskRepository repository, BugzillaRepositoryQuery origQuery) {
		super(TITLE_BUGZILLA_QUERY, origQuery.getSummary());
		originalQuery = origQuery;
		this.repository = repository;
		setDescription("Select the Bugzilla query parameters.  Use the Update Attributes button to retrieve "
				+ "updated values from the repository.");
		// setTitle(TITLE);
		// setDescription(DESCRIPTION);
		// setPageComplete(false);
	}

	public void createControl(Composite parent) {
		readConfiguration();

		Composite control = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		control.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL);
		control.setLayoutData(gd);

		if (scontainer == null) {
			// Not presenting in search pane so want query title
			super.createControl(control);
		}
		// else {
		// // if (repository == null) {
		// // search pane so add repository selection
		// createRepositoryGroup(control);
		// }
		createSearchGroup(control);
		createOptionsGroup(control);

		createEmail(control);
		createLastDays(control);

		// createSaveQuery(control);
		// createMaxHits(control);
		// input = new
		// SavedQueryFile(BugzillaPlugin.getDefault().getStateLocation().toString(),
		// "/queries");
		// createUpdate(control);
		// if (originalQuery != null) {
		// try {
		// updateDefaults(originalQuery.getQueryUrl(),
		// String.valueOf(originalQuery.getMaxHits()));
		// } catch (UnsupportedEncodingException e) {
		// // ignore
		// }
		// }
		setControl(control);
		WorkbenchHelpSystem.getInstance().setHelp(control, BugzillaUiPlugin.SEARCH_PAGE_CONTEXT);
	}

	private void createSearchGroup(Composite control) {
		Group group = new Group(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = 5;
		group.setLayoutData(gd);

		createTextSearchComposite(group);
		createComment(group);
	}

	protected Control createTextSearchComposite(Composite control) {
		GridData gd;
		Label label;

		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		// Info text
		label = new Label(group, SWT.LEFT);
		label.setText("Summary: ");
		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 1;
		label.setLayoutData(gd);

		// Pattern combo
		summaryPattern = new Combo(group, SWT.SINGLE | SWT.BORDER);
		summaryPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (scontainer != null) {
					scontainer.setPerformActionEnabled(canQuery());
				}
			}
		});
		summaryPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(summaryPattern, summaryOperation, previousSummaryPatterns);
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		summaryPattern.setLayoutData(gd);

		summaryOperation = new Combo(group, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		summaryOperation.setItems(patternOperationText);
		summaryOperation.setText(patternOperationText[0]);
		summaryOperation.select(0);

		return group;
	}

	private Control createComment(Composite control) {
		GridData gd;
		Label label;

		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(3, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		// Info text
		label = new Label(group, SWT.LEFT);
		label.setText("Comment: ");
		gd = new GridData(GridData.BEGINNING);
		label.setLayoutData(gd);

		// Comment pattern combo
		commentPattern = new Combo(group, SWT.SINGLE | SWT.BORDER);
		commentPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (scontainer != null) {
					scontainer.setPerformActionEnabled(canQuery());
				}
			}
		});
		commentPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(commentPattern, commentOperation, previousCommentPatterns);
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		commentPattern.setLayoutData(gd);

		commentOperation = new Combo(group, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		commentOperation.setItems(patternOperationText);
		commentOperation.setText(patternOperationText[0]);
		commentOperation.select(0);

		return group;
	}

	protected Control createOptionsGroup(Composite control) {
		Group group = new Group(control, SWT.NONE);
		// group.setText("Bug Attributes");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 5;
		group.setLayoutData(gd);

		createProductAttributes(group);
		createLists(group);
		createUpdate(group);

		return group;
	}

	/**
	 * Creates the area for selection on product/component/version.
	 */
	protected Control createProductAttributes(Composite control) {

		GridData gd;
		GridLayout layout;

		// Search expression
		Composite group = new Composite(control, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 5;
		group.setLayoutData(gd);

		// Labels
		Label label = new Label(group, SWT.LEFT);
		label.setText("Product");

		label = new Label(group, SWT.LEFT);
		label.setText("Component");

		label = new Label(group, SWT.LEFT);
		label.setText("Version");

		label = new Label(group, SWT.LEFT);
		label.setText("Milestone");

		// Lists
		product = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		product.setLayoutData(gd);
		product.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (product.getSelectionIndex() != -1) {
					String[] selectedProducts = product.getSelection();
					updateAttributesFromRepository(repository.getUrl(), selectedProducts, false);
				} else {
					updateAttributesFromRepository(repository.getUrl(), null, false);
				}
				if (restoring) {
					restoring = false;
					restoreWidgetValues();
				}
				if (scontainer != null) {
					scontainer.setPerformActionEnabled(canQuery());
				}
			}
		});

		component = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		component.setLayoutData(gd);
		component.addSelectionListener(updateActionSelectionAdapter);

		version = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		version.setLayoutData(gd);
		version.addSelectionListener(updateActionSelectionAdapter);

		target = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = HEIGHT_ATTRIBUTE_COMBO;
		target.setLayoutData(gd);
		target.addSelectionListener(updateActionSelectionAdapter);

		return group;
	}

	/**
	 * Creates the area for selection of bug attributes (status, etc.)
	 */
	protected Control createLists(Composite control) {
		GridData gd;
		GridLayout layout;

		// Search expression
		Composite group = new Composite(control, SWT.NONE);
		layout = new GridLayout();
		layout.numColumns = 6;
		group.setLayout(layout);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 5;
		group.setLayoutData(gd);

		// Labels
		Label label = new Label(group, SWT.LEFT);
		label.setText("Status");

		label = new Label(group, SWT.LEFT);
		label.setText("Resolution");

		label = new Label(group, SWT.LEFT);
		label.setText("Severity");

		label = new Label(group, SWT.LEFT);
		label.setText("Priority");

		label = new Label(group, SWT.LEFT);
		label.setText("Hardware");

		label = new Label(group, SWT.LEFT);
		label.setText("OS");

		// Lists
		status = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		status.setLayoutData(gd);
		status.addSelectionListener(updateActionSelectionAdapter);

		resolution = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		resolution.setLayoutData(gd);
		resolution.addSelectionListener(updateActionSelectionAdapter);

		severity = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		severity.setLayoutData(gd);
		severity.addSelectionListener(updateActionSelectionAdapter);

		priority = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		priority.setLayoutData(gd);
		priority.addSelectionListener(updateActionSelectionAdapter);

		hardware = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		hardware.setLayoutData(gd);
		hardware.addSelectionListener(updateActionSelectionAdapter);

		os = new List(group, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 40;
		os.setLayoutData(gd);
		os.addSelectionListener(updateActionSelectionAdapter);

		return group;
	}

	protected Text daysText;

	protected Control createLastDays(Composite control) {
		GridLayout layout;
		GridData gd;

		Group group = new Group(control, SWT.NONE);
		layout = new GridLayout(6, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.LEFT);
		label.setText("Only bugs changed in the last ");

		// operation combo
		daysText = new Text(group, SWT.BORDER);
		daysText.setTextLimit(5);
		GridData daysLayoutData = new GridData();
		daysLayoutData.widthHint = 30;
		daysText.setLayoutData(daysLayoutData);
		daysText.addListener(SWT.Modify, this);
		label = new Label(group, SWT.LEFT);
		label.setText(" days.");
		return group;
	}

	private static final String[] emailText = { "bug owner", "reporter", "CC list", "commenter" };

	protected Control createEmail(Composite control) {
		GridLayout layout;
		GridData gd;

		Group group = new Group(control, SWT.NONE);
		layout = new GridLayout(7, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		gd = new GridData(GridData.BEGINNING | GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.LEFT);
		label.setText("Email: ");

		// pattern combo
		emailPattern = new Combo(group, SWT.SINGLE | SWT.BORDER);
		emailPattern.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (scontainer != null) {
					scontainer.setPerformActionEnabled(canQuery());
				}
			}
		});
		emailPattern.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				handleWidgetSelected(emailPattern, emailOperation, previousEmailPatterns);
			}
		});
		gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.widthHint = 110;
		emailPattern.setLayoutData(gd);

		// operation combo
		emailOperation = new Combo(group, SWT.SINGLE | SWT.READ_ONLY | SWT.BORDER);
		emailOperation.setItems(emailOperationText);
		emailOperation.setText(emailOperationText[0]);
		emailOperation.select(0);

		// Composite buttons = new Composite(group, SWT.NONE);
		// layout = new GridLayout(4, false);
		// buttons.setLayout(layout);
		// buttons.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// gd = new GridData(GridData.BEGINNING);
		// gd.horizontalSpan = 3;
		// buttons.setLayoutData(gd);

		emailButtons = new Button[emailText.length];
		for (int i = 0; i < emailButtons.length; i++) {
			Button button = new Button(group, SWT.CHECK);
			button.setText(emailText[i]);
			emailButtons[i] = button;
		}

		return group;
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
		// loadButton.setLayoutData(new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		//
		// saveButton = new Button(group, SWT.PUSH | SWT.LEFT);
		// saveButton.setText("Remember...");
		// saveButton.addSelectionListener(new SelectionAdapter() {
		//
		// @Override
		// public void widgetSelected(SelectionEvent event) {
		// SaveQueryDialog qd = new SaveQueryDialog(getShell(), "Remember
		// Query");
		// if (qd.open() == InputDialog.OK) {
		// String qName = qd.getText();
		// if (qName != null && qName.compareTo("") != 0) {
		// try {
		// input.add(getQueryParameters().toString(), qName,
		// summaryPattern.getText());
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
		// saveButton.setLayoutData(new
		// GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));

		return group;
	}

	// public static SavedQueryFile getInput() {
	// return input;
	// }

	protected Control createUpdate(final Composite control) {
		GridData gd;
		// Label label;

		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 2;
		group.setLayoutData(gd);

		// Info text
		// label = new Label(group, SWT.LEFT);
		// label.setText("Update search options from server:");
		// gd = new GridData(GridData.BEGINNING);
		// label.setLayoutData(gd);

		updateButton = new Button(group, SWT.PUSH);
		updateButton.setText("Update Attributes from Repository");

		updateButton.setLayoutData(new GridData());

		updateButton.addSelectionListener(new SelectionAdapter() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				if (repository != null) {
					updateAttributesFromRepository(repository.getUrl(), null, true);
				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							IBugzillaConstants.TITLE_MESSAGE_DIALOG, TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
				}
			}
		});

		return group;
	}

	private void handleWidgetSelected(Combo widget, Combo operation, ArrayList<BugzillaSearchData> history) {
		if (widget.getSelectionIndex() < 0)
			return;
		int index = history.size() - 1 - widget.getSelectionIndex();
		BugzillaSearchData patternData = history.get(index);
		if (patternData == null || !widget.getText().equals(patternData.pattern))
			return;
		widget.setText(patternData.pattern);
		operation.setText(operation.getItem(patternData.operation));
	}

	// TODO: avoid overriding?
	public boolean performAction() {
		if (restoreQueryOptions) {
			saveState();
		}

		getPatternData(summaryPattern, summaryOperation, previousSummaryPatterns);
		getPatternData(commentPattern, commentOperation, previousCommentPatterns);
		getPatternData(this.emailPattern, emailOperation, previousEmailPatterns);

		String summaryText = summaryPattern.getText();
		BugzillaUiPlugin.getDefault().getPreferenceStore().setValue(IBugzillaConstants.MOST_RECENT_QUERY, summaryText);

		return super.performAction();
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible && summaryPattern != null) {
			if (firstTime) {
				if (repository == null) {
					repository = TasksUiPlugin.getRepositoryManager().getDefaultRepository(
							BugzillaCorePlugin.REPOSITORY_KIND);
				}
				// Set<TaskRepository> repositories =
				// TasksUiPlugin.getRepositoryManager().getRepositories(
				// BugzillaCorePlugin.REPOSITORY_KIND);
				// String[] repositoryUrls = new String[repositories.size()];
				// int i = 0;
				// int indexToSelect = 0;
				// for (Iterator<TaskRepository> iter = repositories.iterator();
				// iter.hasNext();) {
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
				// MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
				// IBugzillaConstants.TITLE_MESSAGE_DIALOG,
				// TaskRepositoryManager.MESSAGE_NO_REPOSITORY);
				// } else {
				// String selectRepo = settings.get(STORE_REPO_ID);
				// if (selectRepo != null && repositoryCombo.indexOf(selectRepo)
				// > -1) {
				// repositoryCombo.setText(selectRepo);
				// repository =
				// TasksUiPlugin.getRepositoryManager().getRepository(
				// BugzillaCorePlugin.REPOSITORY_KIND,
				// repositoryCombo.getText());
				// if (repository == null) {
				// repository =
				// TasksUiPlugin.getRepositoryManager().getDefaultRepository(
				// BugzillaCorePlugin.REPOSITORY_KIND);
				// }
				// } else {
				// repositoryCombo.select(indexToSelect);
				// }
				// updateAttributesFromRepository(repositoryCombo.getText(),
				// null, false);
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
				// emailPattern.setItems(getPreviousPatterns(previousEmailPatterns));

				// TODO: update status, resolution, severity etc if possible...
				if (repository != null) {
					updateAttributesFromRepository(repository.getUrl(), null, false);
					if (product.getItemCount() == 0) {
						updateAttributesFromRepository(repository.getUrl(), null, true);
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
			if (repository != null) {
				IDialogSettings settings = getDialogSettings();
				String repoId = "." + repository.getUrl();
				if (getWizard() == null && restoreQueryOptions && settings.getArray(STORE_PRODUCT_ID + repoId) != null
						&& product != null) {
					product.setSelection(nonNullArray(settings, STORE_PRODUCT_ID + repoId));
					if (product.getSelection().length > 0) {
						updateAttributesFromRepository(repository.getUrl(), product.getSelection(), false);
					}
					restoreWidgetValues();
				}
			}

			if (scontainer != null) {
				scontainer.setPerformActionEnabled(canQuery());
			}
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
		return product.getSelectionCount() > 0 || component.getSelectionCount() > 0 || version.getSelectionCount() > 0
				|| target.getSelectionCount() > 0 || status.getSelectionCount() > 0
				|| resolution.getSelectionCount() > 0 || severity.getSelectionCount() > 0
				|| priority.getSelectionCount() > 0 || hardware.getSelectionCount() > 0 || os.getSelectionCount() > 0
				|| summaryPattern.getText().length() > 0 || commentPattern.getText().length() > 0
				|| emailPattern.getText().length() > 0;
	}

	/**
	 * Return search pattern data and update search history list. An existing entry will be updated or a new one
	 * created.
	 */
	private BugzillaSearchData getPatternData(Combo widget, Combo operation,
			ArrayList<BugzillaSearchData> previousSearchQueryData) {
		String pattern = widget.getText();
		if (pattern == null || pattern.trim().equals("")) {
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
		for (int i = 0; i < size; i++)
			patterns[i] = (patternHistory.get(size - 1 - i)).pattern;
		return patterns;
	}

	public String getSearchURL(TaskRepository repository) {
		try {
			// if (rememberedQuery) {
			// return getQueryURL(repository, new
			// StringBuffer(input.getQueryParameters(selIndex)));
			// } else {
			return getQueryURL(repository, getQueryParameters());
			// }
		} catch (UnsupportedEncodingException e) {
			// ignore
		}
		return "";
	}

	protected String getQueryURL(TaskRepository repository, StringBuffer params) {
		StringBuffer url = new StringBuffer(getQueryURLStart(repository).toString());
		url.append(params);

		// HACK make sure that the searches come back sorted by priority. This
		// should be a search option though
		url.append("&order=Importance");
		// url.append(BugzillaRepositoryUtil.contentTypeRDF);
		return url.toString();
	}

	/**
	 * Creates the bugzilla query URL start.
	 * 
	 * Example: https://bugs.eclipse.org/bugs/buglist.cgi?
	 */
	private StringBuffer getQueryURLStart(TaskRepository repository) {
		StringBuffer sb = new StringBuffer(repository.getUrl());

		if (sb.charAt(sb.length() - 1) != '/') {
			sb.append('/');
		}
		sb.append("buglist.cgi?");
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
	protected StringBuffer getQueryParameters() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();

		sb.append("short_desc_type=");
		sb.append(patternOperationValues[summaryOperation.getSelectionIndex()]);

		sb.append("&short_desc=");
		sb.append(URLEncoder.encode(summaryPattern.getText(), repository.getCharacterEncoding()));

		int[] selected = product.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&product=");
			sb.append(URLEncoder.encode(product.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = component.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&component=");
			sb.append(URLEncoder.encode(component.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = version.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&version=");
			sb.append(URLEncoder.encode(version.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = target.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&target_milestone=");
			sb.append(URLEncoder.encode(target.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		sb.append("&long_desc_type=");
		sb.append(patternOperationValues[commentOperation.getSelectionIndex()]);
		sb.append("&long_desc=");
		sb.append(URLEncoder.encode(commentPattern.getText(), repository.getCharacterEncoding()));

		selected = status.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&bug_status=");
			sb.append(URLEncoder.encode(status.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = resolution.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&resolution=");
			sb.append(URLEncoder.encode(resolution.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = severity.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&bug_severity=");
			sb.append(URLEncoder.encode(severity.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = priority.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&priority=");
			sb.append(URLEncoder.encode(priority.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = hardware.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&ref_platform=");
			sb.append(URLEncoder.encode(hardware.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		selected = os.getSelectionIndices();
		for (int i = 0; i < selected.length; i++) {
			sb.append("&op_sys=");
			sb.append(URLEncoder.encode(os.getItem(selected[i]), repository.getCharacterEncoding()));
		}

		if (emailPattern.getText() != null && !emailPattern.getText().trim().equals("")) {
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
						sb.append("&");
						sb.append(emailRoleValues[i]);
						sb.append("=1");
					}
				}
				sb.append("&emailtype1=");
				sb.append(emailOperationValues[emailOperation.getSelectionIndex()]);
				sb.append("&email1=");
				sb.append(URLEncoder.encode(emailPattern.getText(), repository.getCharacterEncoding()));
			}
		}

		if (daysText.getText() != null && !daysText.getText().equals("")) {
			try {
				Integer.parseInt(daysText.getText());
				sb.append("&changedin=");
				sb.append(URLEncoder.encode(daysText.getText(), repository.getCharacterEncoding()));
			} catch (NumberFormatException ignored) {
				// this means that the days is not a number, so don't worry
			}
		}

		return sb;
	}

	// --------------- Configuration handling --------------

	// Dialog store taskId constants
	protected final static String PAGE_NAME = "BugzillaSearchPage"; //$NON-NLS-1$

	private static final String STORE_PRODUCT_ID = PAGE_NAME + ".PRODUCT";

	private static final String STORE_COMPONENT_ID = PAGE_NAME + ".COMPONENT";

	private static final String STORE_VERSION_ID = PAGE_NAME + ".VERSION";

	private static final String STORE_MSTONE_ID = PAGE_NAME + ".MILESTONE";

	private static final String STORE_STATUS_ID = PAGE_NAME + ".STATUS";

	private static final String STORE_RESOLUTION_ID = PAGE_NAME + ".RESOLUTION";

	private static final String STORE_SEVERITY_ID = PAGE_NAME + ".SEVERITY";

	private static final String STORE_PRIORITY_ID = PAGE_NAME + ".PRIORITY";

	private static final String STORE_HARDWARE_ID = PAGE_NAME + ".HARDWARE";

	private static final String STORE_OS_ID = PAGE_NAME + ".OS";

	private static final String STORE_SUMMARYMATCH_ID = PAGE_NAME + ".SUMMARYMATCH";

	private static final String STORE_COMMENTMATCH_ID = PAGE_NAME + ".COMMENTMATCH";

	private static final String STORE_EMAILMATCH_ID = PAGE_NAME + ".EMAILMATCH";

	private static final String STORE_EMAILBUTTON_ID = PAGE_NAME + ".EMAILATTR";

	private static final String STORE_SUMMARYTEXT_ID = PAGE_NAME + ".SUMMARYTEXT";

	private static final String STORE_COMMENTTEXT_ID = PAGE_NAME + ".COMMENTTEXT";

	private static final String STORE_EMAILADDRESS_ID = PAGE_NAME + ".EMAILADDRESS";

	// private static final String STORE_REPO_ID = PAGE_NAME + ".REPO";

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

	protected Combo emailPattern;

	protected Button[] emailButtons;

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

	protected Button updateButton;

	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = BugzillaUiPlugin.getDefault().getDialogSettings();
		fDialogSettings = settings.getSection(PAGE_NAME);
		if (fDialogSettings == null)
			fDialogSettings = settings.addNewSection(PAGE_NAME);
		return fDialogSettings;
	}

	/**
	 * Initializes itself from the stored page settings.
	 */
	private void readConfiguration() {
		getDialogSettings();
	}

	private void updateAttributesFromRepository(String repositoryUrl, String[] selectedProducts, boolean connect) {

		if (connect) {
			final AbstractRepositoryConnector connector = TasksUiPlugin.getRepositoryManager().getRepositoryConnector(
					repository.getConnectorKind());

			IRunnableWithProgress updateRunnable = new IRunnableWithProgress() {
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					if (monitor == null) {
						monitor = new NullProgressMonitor();
					}
					try {
						monitor.beginTask("Updating search options...", IProgressMonitor.UNKNOWN);
						connector.updateAttributes(repository, monitor);
						BugzillaUiPlugin.updateQueryOptions(repository, monitor);
					} catch (final CoreException ce) {
						StatusHandler.displayStatus("Update failed", ce.getStatus());
					} finally {
						monitor.done();
					}
				}
			};

			try {
				// TODO: make cancelable (bug 143011)
				if (getContainer() != null) {
					getContainer().run(true, false, updateRunnable);
				} else {
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					service.run(true, false, updateRunnable);
				}

			} catch (InvocationTargetException e) {
				MessageDialog.openError(null, "Error updating search options", "Error was : "
						+ e.getCause().getMessage());
			} catch (InterruptedException e) {
				// Was cancelled...
			}
		}

		if (selectedProducts == null) {
			String[] productsList = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRODUCT, null,
					repositoryUrl);
			Arrays.sort(productsList, String.CASE_INSENSITIVE_ORDER);
			product.setItems(productsList);
		}

		String[] componentsList = BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_COMPONENT,
				selectedProducts, repositoryUrl);
		Arrays.sort(componentsList, String.CASE_INSENSITIVE_ORDER);
		component.setItems(componentsList);

		version.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_VERSION, selectedProducts,
				repositoryUrl));

		target.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_TARGET, selectedProducts,
				repositoryUrl));

		if (selectedProducts == null) {
			status.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_STATUS, selectedProducts,
					repositoryUrl));

			// status.setSelection(BugzillaRepositoryUtil.getQueryOptions(IBugzillaConstants.VALUSE_STATUS_PRESELECTED,
			// repositoryUrl));

			resolution.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_RESOLUTION,
					selectedProducts, repositoryUrl));

			severity.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_SEVERITY, selectedProducts,
					repositoryUrl));

			priority.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_PRIORITY, selectedProducts,
					repositoryUrl));

			hardware.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_HARDWARE, selectedProducts,
					repositoryUrl));

			os.setItems(BugzillaUiPlugin.getQueryOptions(IBugzillaConstants.VALUES_OS, selectedProducts, repositoryUrl));
		}
	}

	public TaskRepository getRepository() {
		return repository;
	}

	public void setRepository(TaskRepository repository) {
		this.repository = repository;
	}

	public boolean canFlipToNextPage() {
		// if (getErrorMessage() != null)
		// return false;
		//
		// return true;
		return false;
	}

	public void handleEvent(Event event) {
		String message = null;
		if (event.widget == daysText) {
			String days = daysText.getText();
			if (days.length() > 0) {
				try {
					if (Integer.parseInt(days) < 0) {
						message = NUM_DAYS_POSITIVE + days + " is invalid.";
					}
				} catch (NumberFormatException ex) {
					message = NUM_DAYS_POSITIVE + days + " is invalid.";
				}
			}
		}

		setPageComplete(message == null);
		setErrorMessage(message);
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

		startingUrl = startingUrl.substring(startingUrl.indexOf("?") + 1);
		String[] options = startingUrl.split("&");
		for (String option : options) {
			String key = option.substring(0, option.indexOf("="));
			String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1),
					repository.getCharacterEncoding());
			if (key == null)
				continue;

			if (key.equals("short_desc")) {
				summaryPattern.setText(value);
			} else if (key.equals("short_desc_type")) {
				if (value.equals("allwordssubstr"))
					value = "all words";
				else if (value.equals("anywordssubstr"))
					value = "any word";
				int index = 0;
				for (String item : summaryOperation.getItems()) {
					if (item.compareTo(value) == 0)
						break;
					index++;
				}
				if (index < summaryOperation.getItemCount()) {
					summaryOperation.select(index);
				}
			} else if (key.equals("product")) {
				String[] sel = product.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				product.setSelection(selList.toArray(sel));
				updateAttributesFromRepository(repository.getUrl(), selList.toArray(sel), false);
			} else if (key.equals("component")) {
				String[] sel = component.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				component.setSelection(selList.toArray(sel));
			} else if (key.equals("version")) {
				String[] sel = version.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				version.setSelection(selList.toArray(sel));
			} else if (key.equals("target_milestone")) { // XXX
				String[] sel = target.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				target.setSelection(selList.toArray(sel));
			} else if (key.equals("version")) {
				String[] sel = version.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				version.setSelection(selList.toArray(sel));
			} else if (key.equals("long_desc_type")) {
				if (value.equals("allwordssubstr"))
					value = "all words";
				else if (value.equals("anywordssubstr"))
					value = "any word";
				int index = 0;
				for (String item : commentOperation.getItems()) {
					if (item.compareTo(value) == 0)
						break;
					index++;
				}
				if (index < commentOperation.getItemCount()) {
					commentOperation.select(index);
				}
			} else if (key.equals("long_desc")) {
				commentPattern.setText(value);
			} else if (key.equals("bug_status")) {
				String[] sel = status.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				status.setSelection(selList.toArray(sel));
			} else if (key.equals("resolution")) {
				String[] sel = resolution.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				resolution.setSelection(selList.toArray(sel));
			} else if (key.equals("bug_severity")) {
				String[] sel = severity.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				severity.setSelection(selList.toArray(sel));
			} else if (key.equals("priority")) {
				String[] sel = priority.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				priority.setSelection(selList.toArray(sel));
			} else if (key.equals("ref_platform")) {
				String[] sel = hardware.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				hardware.setSelection(selList.toArray(sel));
			} else if (key.equals("op_sys")) {
				String[] sel = os.getSelection();
				java.util.List<String> selList = Arrays.asList(sel);
				selList = new ArrayList<String>(selList);
				selList.add(value);
				sel = new String[selList.size()];
				os.setSelection(selList.toArray(sel));
			} else if (key.equals("emailassigned_to1")) { // HACK: email
				// buttons
				// assumed to be
				// in same
				// position
				if (value.equals("1"))
					emailButtons[0].setSelection(true);
				else
					emailButtons[0].setSelection(false);
			} else if (key.equals("emailreporter1")) { // HACK: email
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1"))
					emailButtons[1].setSelection(true);
				else
					emailButtons[1].setSelection(false);
			} else if (key.equals("emailcc1")) { // HACK: email buttons
				// assumed to be in same
				// position
				if (value.equals("1"))
					emailButtons[2].setSelection(true);
				else
					emailButtons[2].setSelection(false);
			} else if (key.equals("emaillongdesc1")) { // HACK: email
				// buttons assumed
				// to be in same
				// position
				if (value.equals("1"))
					emailButtons[3].setSelection(true);
				else
					emailButtons[3].setSelection(false);
			} else if (key.equals("emailtype1")) {
				int index = 0;
				for (String item : emailOperation.getItems()) {
					if (item.compareTo(value) == 0)
						break;
					index++;
				}
				if (index < emailOperation.getItemCount()) {
					emailOperation.select(index);
				}
			} else if (key.equals("email1")) {
				emailPattern.setText(value);
			} else if (key.equals("changedin")) {
				daysText.setText(value);
			}
		}
	}

	@Override
	public BugzillaRepositoryQuery getQuery() {
		if (originalQuery == null) {
			try {
				originalQuery = new BugzillaRepositoryQuery(repository.getUrl(), getQueryURL(repository,
						getQueryParameters()), getQueryTitle());
			} catch (UnsupportedEncodingException e) {
				return null;
			}

		} else {
			try {
				originalQuery.setUrl(getQueryURL(repository, getQueryParameters()));
				// originalQuery.setMaxHits(Integer.parseInt(getMaxHits()));
				originalQuery.setHandleIdentifier(getQueryTitle());
			} catch (UnsupportedEncodingException e) {
				return null;
			}
		}
		return originalQuery;
	}

	private String[] nonNullArray(IDialogSettings settings, String id) {
		String[] value = settings.getArray(id);
		if (value == null) {
			return new String[] {};
		}
		return value;
	}

	private void restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		String repoId = "." + repository.getUrl();
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
	}

	public void saveState() {
		String repoId = "." + repository.getUrl();
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
		// settings.put(STORE_REPO_ID, repositoryCombo.getText());
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
}
