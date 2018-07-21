/*******************************************************************************
 * Copyright (c) 2006, 2009 Steffen Pingel and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Steffen Pingel - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.wizard;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.forms.SectionComposite;
import org.eclipse.mylyn.internal.trac.core.TracCorePlugin;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryConnector;
import org.eclipse.mylyn.internal.trac.core.client.ITracClient;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.internal.trac.core.util.TracUtil;
import org.eclipse.mylyn.internal.trac.ui.TracUiPlugin;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositoryQueryPage2;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Text;

/**
 * Trac search page.
 * 
 * @author Steffen Pingel
 */
public class TracQueryPage extends AbstractRepositoryQueryPage2 {

	private class ListSearchField extends SearchField {

		private List list;

		public ListSearchField(String fieldName) {
			super(fieldName);
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

		@Override
		public void clear() {
			list.deselectAll();
		}

//		public void selectItems(String[] items) {
//			list.deselectAll();
//			for (String item : items) {
//				int i = list.indexOf(item);
//				if (i != -1) {
//					list.select(i);
//				}
//			}
//		}

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

		public abstract void clear();

	}

	private class TextSearchField extends SearchField {

		private final CompareOperator[] compareOperators = { CompareOperator.CONTAINS, CompareOperator.CONTAINS_NOT,
				CompareOperator.BEGINS_WITH, CompareOperator.ENDS_WITH, CompareOperator.IS, CompareOperator.IS_NOT, };

		private Combo conditionCombo;

		private Label label;

		protected Text searchText;

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

		public void setFieldName(String fieldName) {
			this.fieldName = fieldName;
		}

		@Override
		public void setFilter(TracSearchFilter filter) {
			setCondition(filter.getOperator());
			java.util.List<String> values = filter.getValues();
			setSearchText(values.get(0));
		}

		public void setSearchText(String text) {
			searchText.setText(text);
		}

		@Override
		public void clear() {
			searchText.setText(""); //$NON-NLS-1$
			conditionCombo.select(0);
		}

	}

	private class UserSearchField extends SearchField {

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

			@Override
			public void clear() {
				textField.clear();
			}

		}

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

		private int getSelection() {
			return userCombo.getSelectionIndex();
		}

		private void setSelection(int index) {
			userCombo.select(index);
		}

		@Override
		public void clear() {
			textField.clear();
		}

	}

	private static final int HEIGHT_PRODUCT = 60;

	private static final int HEIGHT_STATUS = 40;

	private final static String PAGE_NAME = "TracSearchPage"; //$NON-NLS-1$

	private static final String SEARCH_URL_ID = PAGE_NAME + ".SEARCHURL"; //$NON-NLS-1$

	private ListSearchField componentField;

	private TextSearchField descriptionField;

	private TextSearchField keywordsField;

	private ListSearchField milestoneField;

	private ListSearchField priorityField;

	private ListSearchField resolutionField;

	private final Map<String, SearchField> searchFieldByName = new HashMap<String, SearchField>();

	private ListSearchField statusField;

	private TextSearchField summaryField;

	private ListSearchField typeField;

	// private UserSearchField ownerField;
	//
	// private UserSearchField reporterField;
	//
	// private UserSearchField ccField;

	private ListSearchField versionField;

	public TracQueryPage(TaskRepository repository) {
		this(repository, null);
	}

	public TracQueryPage(TaskRepository repository, IRepositoryQuery query) {
		super(PAGE_NAME, repository, query);
		setTitle(Messages.TracQueryPage_Enter_query_parameters);
		setDescription(Messages.TracQueryPage_If_attributes_are_blank_or_stale_press_the_Update_button);
		setNeedsClear(true);
	}

	@Override
	public void applyTo(IRepositoryQuery query) {
		query.setUrl(getQueryUrl(getTaskRepository().getRepositoryUrl()));
		query.setSummary(getQueryTitle());
	}

	@Override
	public boolean canFlipToNextPage() {
		return false;
	}

	@Override
	public void doRefreshControls() {
		TracRepositoryConnector connector = (TracRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		ITracClient client = connector.getClientManager().getTracClient(getTaskRepository());

		statusField.setValues(client.getTicketStatus());
		resolutionField.setValues(client.getTicketResolutions());
		typeField.setValues(client.getTicketTypes());
		priorityField.setValues(client.getPriorities());

		componentField.setValues(client.getComponents());
		versionField.setValues(client.getVersions());
		milestoneField.setValues(client.getMilestones());
	}

	@Override
	protected void doClearControls() {
		restoreWidgetValues(new TracSearch());
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

	public String getQueryUrl(String repsitoryUrl) {
		TracSearch search = getTracSearch();

		StringBuilder sb = new StringBuilder();
		sb.append(repsitoryUrl);
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());
		return sb.toString();
	}

	@Override
	public void saveState() {
		IDialogSettings settings = getDialogSettings();
		settings.put(getSavedStateSettingsKey(), getTracSearch().toUrl());
	}

	private String getSavedStateSettingsKey() {
		return SEARCH_URL_ID + "." + getTaskRepository().getRepositoryUrl(); //$NON-NLS-1$
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

	private void restoreWidgetValues(TracSearch search) {
		Set<SearchField> allFields = new HashSet<SearchField>(searchFieldByName.values());

		java.util.List<TracSearchFilter> filters = search.getFilters();
		for (TracSearchFilter filter : filters) {
			SearchField field = searchFieldByName.get(filter.getFieldName());
			if (field != null) {
				field.setFilter(filter);
				allFields.remove(field);
			} else {
				StatusHandler.log(new Status(IStatus.WARNING, TracUiPlugin.ID_PLUGIN,
						"Ignoring invalid search filter: " + filter)); //$NON-NLS-1$
			}
		}

		for (SearchField field : allFields) {
			field.clear();
		}
	}

	@Override
	protected void createPageContent(SectionComposite composite) {
		Composite control = composite.getContent();

		GridLayout layout = new GridLayout(4, false);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		control.setLayout(layout);

		createDefaultGroup(control);
		createProductGroup(control);
		createTicketGroup(control);
		createUserGroup(control);
	}

	private void createDefaultGroup(Composite control) {
		summaryField = new TextSearchField("summary"); //$NON-NLS-1$
		summaryField.createControls(control, Messages.TracQueryPage_Summary);

		descriptionField = new TextSearchField("description"); //$NON-NLS-1$
		descriptionField.createControls(control, Messages.TracQueryPage_Description);

		keywordsField = new TextSearchField("keywords"); //$NON-NLS-1$
		keywordsField.createControls(control, Messages.TracQueryPage_Keywords);
	}

	/**
	 * Creates the area for selection on product attributes component/version/milestone.
	 */
	protected Control createProductGroup(Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(4, 1).applyTo(group);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 3;
		group.setLayout(layout);

		Label label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Component);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Version);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Milestone);

		componentField = new ListSearchField("component"); //$NON-NLS-1$
		componentField.createControls(group, HEIGHT_PRODUCT);

		versionField = new ListSearchField("version"); //$NON-NLS-1$
		versionField.createControls(group, HEIGHT_PRODUCT);

		milestoneField = new ListSearchField("milestone"); //$NON-NLS-1$
		milestoneField.createControls(group, HEIGHT_PRODUCT);

		return group;
	}

	/**
	 * Creates the area for selection of ticket attributes status/resolution/priority.
	 */
	protected Control createTicketGroup(Composite control) {
		Composite group = new Composite(control, SWT.NONE);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).span(4, 1).applyTo(group);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.numColumns = 4;
		group.setLayout(layout);

		Label label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Status);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Resolution);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Type);

		label = new Label(group, SWT.LEFT);
		label.setText(Messages.TracQueryPage_Priority);

		statusField = new ListSearchField("status"); //$NON-NLS-1$
		statusField.createControls(group, HEIGHT_STATUS);

		resolutionField = new ListSearchField("resolution"); //$NON-NLS-1$
		resolutionField.createControls(group, HEIGHT_STATUS);

		typeField = new ListSearchField("type"); //$NON-NLS-1$
		typeField.createControls(group, HEIGHT_STATUS);

		priorityField = new ListSearchField("priority"); //$NON-NLS-1$
		priorityField.createControls(group, HEIGHT_STATUS);

		return group;
	}

	protected void createUserGroup(Composite control) {
		UserSearchField userField = new UserSearchField();
		userField.createControls(control);
	}

	@Override
	protected boolean hasRepositoryConfiguration() {
		TracRepositoryConnector connector = (TracRepositoryConnector) TasksUi.getRepositoryManager()
				.getRepositoryConnector(TracCorePlugin.CONNECTOR_KIND);
		ITracClient client = connector.getClientManager().getTracClient(getTaskRepository());
		return client.hasAttributes();
	}

	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		TracSearch search = TracUtil.toTracSearch(query);
		if (search != null) {
			restoreWidgetValues(search);
			return true;
		}
		return false;
	}

	@Override
	protected boolean restoreSavedState() {
		IDialogSettings settings = getDialogSettings();
		String searchUrl = settings.get(getSavedStateSettingsKey());
		if (searchUrl != null) {
			restoreWidgetValues(new TracSearch(searchUrl));
			return true;
		}
		return false;
	}

}
