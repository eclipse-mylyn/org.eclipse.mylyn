/*******************************************************************************
 * Copyright (c) 2006, 2008 Steffen Pingel and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.client.TracException;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.progress.IProgressService;

/**
 * Trac search page. Provides a form similar to the one the Bugzilla connector uses.
 * 
 * @author Steffen Pingel
 */
public class TracQueryPage extends AbstractRepositoryQueryPage {

	private Text titleText;

	private static final int PRODUCT_HEIGHT = 60;

	private static final int STATUS_HEIGHT = 40;

	protected final static String PAGE_NAME = "TracSearchPage"; //$NON-NLS-1$

	private static final String SEARCH_URL_ID = PAGE_NAME + ".SEARCHURL"; //$NON-NLS-1$

	protected Combo summaryText = null;

	protected Combo repositoryCombo = null;

	private TextSearchField summaryField;

	private TextSearchField descriptionField;

	private ListSearchField componentField;

	private ListSearchField versionField;

	private ListSearchField milestoneField;

	private ListSearchField priorityField;

	private ListSearchField typeField;

	private ListSearchField resolutionField;

	private ListSearchField statusField;

	private Button updateButton;

	private TextSearchField keywordsField;

	private final Map<String, SearchField> searchFieldByName = new HashMap<String, SearchField>();

	private boolean firstTime = true;

	// private UserSearchField ownerField;
	//
	// private UserSearchField reporterField;
	//
	// private UserSearchField ccField;

	public TracQueryPage(TaskRepository repository, IRepositoryQuery query) {
		super(Messages.TracQueryPage_Enter_query_parameters, repository, query);
		setTitle(Messages.TracQueryPage_Enter_query_parameters);
		setDescription(Messages.TracQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button);
	}

	public TracQueryPage(TaskRepository repository) {
		this(repository, null);
	}

	public void createControl(Composite parent) {
		Composite control = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_BOTH);
		control.setLayoutData(gd);
		GridLayout layout = new GridLayout(4, false);
		if (inSearchContainer()) {
			layout.marginWidth = 0;
			layout.marginHeight = 0;
		}
		control.setLayout(layout);

		createTitleGroup(control);

		summaryField = new TextSearchField("summary"); //$NON-NLS-1$
		summaryField.createControls(control, Messages.TracQueryPage_Summary);

		descriptionField = new TextSearchField("description"); //$NON-NLS-1$
		descriptionField.createControls(control, Messages.TracQueryPage_Description);

		keywordsField = new TextSearchField("keywords"); //$NON-NLS-1$
		keywordsField.createControls(control, Messages.TracQueryPage_Keywords);

		createOptionsGroup(control);

		createUserGroup(control);

		if (getQuery() != null) {
			titleText.setText(getQuery().getSummary());
			TracSearch search = TracUtil.toTracSearch(getQuery());
			if (search != null) {
				restoreWidgetValues(search);
			}
		}

		Dialog.applyDialogFont(control);
		setControl(control);
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	private void restoreWidgetValues(TracSearch search) {
		java.util.List<TracSearchFilter> filters = search.getFilters();
		for (TracSearchFilter filter : filters) {
			SearchField field = searchFieldByName.get(filter.getFieldName());
			if (field != null) {
				field.setFilter(filter);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TracUiPlugin.ID_PLUGIN,
						"Ignoring invalid search filter: " + filter)); //$NON-NLS-1$
			}
		}
	}

	private void createTitleGroup(Composite control) {
		if (inSearchContainer()) {
			return;
		}

		Label titleLabel = new Label(control, SWT.NONE);
		titleLabel.setText(Messages.TracQueryPage_Query_Title);

		titleText = new Text(control, SWT.BORDER);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL | GridData.GRAB_HORIZONTAL);
		gd.horizontalSpan = 3;
		titleText.setLayoutData(gd);
		titleText.addKeyListener(new KeyListener() {
			public void keyPressed(KeyEvent e) {
				// ignore
			}

			public void keyReleased(KeyEvent e) {
				getContainer().updateButtons();
			}
		});
	}

	protected Control createOptionsGroup(Composite control) {
		Group group = new Group(control, SWT.NONE);
		// group.setText("Ticket Attributes");
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		group.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		group.setLayoutData(gd);

		createProductAttributes(group);
		createTicketAttributes(group);
		createUpdateButton(group);

		return group;
	}

	protected void createUserGroup(Composite control) {
		UserSearchField userField = new UserSearchField();
		userField.createControls(control);
	}

	/**
	 * Creates the area for selection on product attributes component/version/milestone.
	 */
	protected Control createProductAttributes(Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		group.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Component);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Version);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Milestone);

		componentField = new ListSearchField("component"); //$NON-NLS-1$
		componentField.createControls(group, PRODUCT_HEIGHT);

		versionField = new ListSearchField("version"); //$NON-NLS-1$
		versionField.createControls(group, PRODUCT_HEIGHT);

		milestoneField = new ListSearchField("milestone"); //$NON-NLS-1$
		milestoneField.createControls(group, PRODUCT_HEIGHT);

		return group;
	}

	/**
	 * Creates the area for selection of ticket attributes status/resolution/priority.
	 */
	protected Control createTicketAttributes(Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 4;
		group.setLayout(layout);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 1;
		group.setLayoutData(gd);

		Label label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Status);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Resolution);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Type);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Priority);

		statusField = new ListSearchField("status"); //$NON-NLS-1$
		statusField.createControls(group, STATUS_HEIGHT);

		resolutionField = new ListSearchField("resolution"); //$NON-NLS-1$
		resolutionField.createControls(group, STATUS_HEIGHT);

		typeField = new ListSearchField("type"); //$NON-NLS-1$
		typeField.createControls(group, STATUS_HEIGHT);

		priorityField = new ListSearchField("priority"); //$NON-NLS-1$
		priorityField.createControls(group, STATUS_HEIGHT);

		return group;
	}

	protected Control createUpdateButton(final Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		updateButton = new Button(group, SWT.PUSH);
		updateButton.setText(Messages.TracQueryPage_Update_Attributes_from_Repository);
		updateButton.setLayoutData(new GridData());
		updateButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getTaskRepository() != null) {
					updateAttributesFromRepository(true);
				} else {
					MessageDialog.openInformation(Display.getCurrent().getActiveShell(),
							Messages.TracQueryPage_Update_Attributes_Failed,
							Messages.TracQueryPage_No_repository_available);
				}
			}
		});

		return group;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);

		if (getSearchContainer() != null) {
			getSearchContainer().setPerformActionEnabled(true);
		}

		if (visible && firstTime) {
			firstTime = false;
			if (!hasAttributes()) {
				// delay the execution so the dialog's progress bar is visible
				// when the attributes are updated
				Display.getDefault().asyncExec(new Runnable() {
					public void run() {
						if (getControl() != null && !getControl().isDisposed()) {
							initializePage();
						}
					}

				});
			} else {
				// no remote connection is needed to get attributes therefore do
				// not use delayed execution to avoid flickering
				initializePage();
			}
		}
	}

	private void initializePage() {
		updateAttributesFromRepository(false);
		boolean restored = (getQuery() != null);
		if (inSearchContainer()) {
			restored |= restoreWidgetValues();
		}
		if (!restored) {
			// initialize with default values
		}
	}

	private boolean hasAttributes() {
		TracRepositoryConnector connector = (TracRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		ITracClient client = connector.getClientManager().getTracClient(getTaskRepository());
		return client.hasAttributes();
	}

	private void updateAttributesFromRepository(final boolean force) {
		TracRepositoryConnector connector = (TracRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		final ITracClient client = connector.getClientManager().getTracClient(getTaskRepository());

		if (!client.hasAttributes() || force) {
			try {
				IRunnableWithProgress runnable = new IRunnableWithProgress() {
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						try {
							client.updateAttributes(monitor, force);
						} catch (TracException e) {
							throw new InvocationTargetException(e);
						}
					}
				};

				if (getContainer() != null) {
					getContainer().run(true, true, runnable);
				} else if (getSearchContainer() != null) {
					getSearchContainer().getRunnableContext().run(true, true, runnable);
				} else {
					IProgressService service = PlatformUI.getWorkbench().getProgressService();
					service.busyCursorWhile(runnable);
				}
			} catch (InvocationTargetException e) {
				setErrorMessage(TracCorePlugin.toStatus(e.getCause(), getTaskRepository()).getMessage());
				return;
			} catch (InterruptedException e) {
				return;
			}
		}

		statusField.setValues(client.getTicketStatus());
		resolutionField.setValues(client.getTicketResolutions());
		typeField.setValues(client.getTicketTypes());
		priorityField.setValues(client.getPriorities());

		componentField.setValues(client.getComponents());
		versionField.setValues(client.getVersions());
		milestoneField.setValues(client.getMilestones());
	}

	@Override
	public boolean isPageComplete() {
		if (titleText != null && titleText.getText().length() > 0) {
			return true;
		}
		return false;
	}

	public String getQueryUrl(String repsitoryUrl) {
		TracSearch search = getTracSearch();

		StringBuilder sb = new StringBuilder();
		sb.append(repsitoryUrl);
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());
		return sb.toString();
	}

	private TracSearch getTracSearch() {
		TracSearch search = new TracSearch();
		for (SearchField field : searchFieldByName.values()) {
			TracSearchFilter filter = field.getFilter();
			if (filter != null) {
				search.addFilter(filter);
			}
		}
		return search;
	}

	// public boolean performAction() {
	//
	// Proxy proxySettings = TasksUiPlugin.getDefault().getProxySettings();
	// SearchHitCollector collector = new
	// SearchHitCollector(TasksUiPlugin.getTaskList(),
	// repository, getQuery(), proxySettings);
	// NewSearchUI.runQueryInBackground(collector);
	//
	// return true;
	// }

	@Override
	public boolean performSearch() {
		if (inSearchContainer()) {
			saveState();
		}

		return super.performSearch();
	}

	@Override
	public IDialogSettings getDialogSettings() {
		IDialogSettings settings = TracUiPlugin.getDefault().getDialogSettings();
		IDialogSettings dialogSettings = settings.getSection(PAGE_NAME);
		if (dialogSettings == null) {
			dialogSettings = settings.addNewSection(PAGE_NAME);
		}
		return dialogSettings;
	}

	private boolean restoreWidgetValues() {
		IDialogSettings settings = getDialogSettings();
		String repoId = "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$

		String searchUrl = settings.get(SEARCH_URL_ID + repoId);
		if (searchUrl == null) {
			return false;
		}

		restoreWidgetValues(new TracSearch(searchUrl));
		return true;
	}

	@Override
	public void saveState() {
		String repoId = "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
		IDialogSettings settings = getDialogSettings();
		settings.put(SEARCH_URL_ID + repoId, getTracSearch().toUrl());
	}

	private abstract class SearchField {

		protected String fieldName;

		public SearchField(String fieldName) {
			this.fieldName = fieldName;

			if (fieldName != null) {
				assert !searchFieldByName.containsKey(fieldName);
				searchFieldByName.put(fieldName, this);
			}
		}

		public String getFieldName() {
			return fieldName;
		}

		public abstract TracSearchFilter getFilter();

		public abstract void setFilter(TracSearchFilter filter);

	}

	private class TextSearchField extends SearchField {

		private Combo conditionCombo;

		protected Text searchText;

		private Label label;

		private final CompareOperator[] compareOperators = { CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT,
				CompareOperator.BEGINS_WITH, CompareOperator.ENDS_WITH, CompareOperator.IS, CompareOperator.IS_NOT, };

		public TextSearchField(String fieldName) {
			super(fieldName);
		}

		public void createControls(Composite parent, String labelText) {
			if (labelText != null) {
				label = new Label(parent, SWT.LEFT);
				label.setText(labelText);
			}

			conditionCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			for (CompareOperator op : compareOperators) {
				conditionCombo.add(op.toString());
			}
			conditionCombo.setText(compareOperators[0].toString());

			searchText = new Text(parent, SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
			// the user search field has additional controls and no fieldName
			if (fieldName != null) {
				gd.horizontalSpan = 2;
			}
			searchText.setLayoutData(gd);
		}

		public CompareOperator getCondition() {
			return compareOperators[conditionCombo.getSelectionIndex()];
		}

		public String getSearchText() {
			return searchText.getText();
		}

		public boolean setCondition(CompareOperator operator) {
			if (conditionCombo != null) {
				int i = conditionCombo.indexOf(operator.toString());
				if (i != -1) {
					conditionCombo.select(i);
					return true;
				}
			}
			return false;
		}

		public void setSearchText(String text) {
			searchText.setText(text);
		}

		@Override
		public TracSearchFilter getFilter() {
			String text = getSearchText();
			if (text.length() == 0) {
				return null;
			}

			TracSearchFilter newFilter = new TracSearchFilter(getFieldName());
			newFilter.setOperator(getCondition());
			newFilter.addValue(getSearchText());
			return newFilter;
		}

		@Override
		public void setFilter(TracSearchFilter filter) {
			setCondition(filter.getOperator());
			java.util.List<String> values = filter.getValues();
			setSearchText(values.get(0));
		}

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

	}

	private class ListSearchField extends SearchField {

		private List list;

		public ListSearchField(String fieldName) {
			super(fieldName);
		}

		public void setValues(Object[] items) {
			// preserve selected values
			TracSearchFilter filter = getFilter();

			list.removeAll();
			if (items != null) {
				list.setEnabled(true);
				for (Object item : items) {
					list.add(item.toString());
				}

				// restore selected values
				if (filter != null) {
					setFilter(filter);
				}
			} else {
				list.setEnabled(false);
			}
		}

		public void createControls(Composite parent, int height) {
			list = new List(parent, SWT.MULTI | SWT.V_SCROLL | SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = height;
			list.setLayoutData(gd);
		}

		@Override
		public TracSearchFilter getFilter() {
			int[] indicies = list.getSelectionIndices();
			if (indicies.length == 0) {
				return null;
			}

			TracSearchFilter newFilter = new TracSearchFilter(getFieldName());
			newFilter.setOperator(CompareOperator.IS);
			for (int i : indicies) {
				newFilter.addValue(list.getItem(i));
			}
			return newFilter;
		}

		@Override
		public void setFilter(TracSearchFilter filter) {
			list.deselectAll();
			java.util.List<String> values = filter.getValues();
			for (String item : values) {
				int i = list.indexOf(item);
				if (i != -1) {
					list.select(i);
				} else {
					list.add(item, 0);
					list.select(0);
				}
			}
		}

		public void selectItems(String[] items) {
			list.deselectAll();
			for (String item : items) {
				int i = list.indexOf(item);
				if (i != -1) {
					list.select(i);
				}
			}
		}

	}

	private class UserSearchField extends SearchField {

		private final TextSearchField textField;

		private Combo userCombo;

		public UserSearchField() {
			super(null);

			textField = new TextSearchField(null);

			new UserSelectionSearchField("owner", 0); //$NON-NLS-1$

			new UserSelectionSearchField("reporter", 1); //$NON-NLS-1$

			new UserSelectionSearchField("cc", 2); //$NON-NLS-1$
		}

		public void createControls(Composite parent) {
			userCombo = new Combo(parent, SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY);
			userCombo.add(Messages.TracQueryPage_Owner);
			userCombo.add(Messages.TracQueryPage_Reporter);
			userCombo.add(Messages.TracQueryPage_CC);
			userCombo.select(0);

			textField.createControls(parent, null);
		}

		@Override
		public TracSearchFilter getFilter() {
			return null;
		}

		@Override
		public void setFilter(TracSearchFilter filter) {
		}

		private void setSelection(int index) {
			userCombo.select(index);
		}

		private int getSelection() {
			return userCombo.getSelectionIndex();
		}

		class UserSelectionSearchField extends SearchField {

			private final int index;

			public UserSelectionSearchField(String fieldName, int index) {
				super(fieldName);

				this.index = index;
			}

			@Override
			public TracSearchFilter getFilter() {
				if (index == getSelection()) {
					textField.setFieldName(fieldName);
					return textField.getFilter();
				}
				return null;
			}

			@Override
			public void setFilter(TracSearchFilter filter) {
				textField.setFieldName(fieldName);
				textField.setFilter(filter);
				setSelection(index);
			}

		}

	}

	@Override
	public String getQueryTitle() {
		return (titleText != null) ? titleText.getText() : null;
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setUrl(getQueryUrl(getTaskRepository().getRepositoryUrl()));
		query.setSummary(getQueryTitle());
	}

}
