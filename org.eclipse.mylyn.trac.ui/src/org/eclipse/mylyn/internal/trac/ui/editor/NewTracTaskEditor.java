/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.ui.editor;

import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.mylyn.internal.trac.core.ITracClient;
import org.eclipse.mylyn.internal.trac.core.TracRepositoryQuery;
import org.eclipse.mylyn.internal.trac.core.model.TracSearch;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter;
import org.eclipse.mylyn.internal.trac.core.model.TracSearchFilter.CompareOperator;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.ui.TaskFactory;
import org.eclipse.mylyn.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.ui.editors.AbstractNewRepositoryTaskEditor;
import org.eclipse.mylyn.tasks.ui.search.SearchHitCollector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.fieldassist.ContentAssistCommandAdapter;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Steffen Pingel
 */
public class NewTracTaskEditor extends AbstractNewRepositoryTaskEditor {

	public NewTracTaskEditor(FormEditor editor) {
		super(editor);
	}

	@Override
	public SearchHitCollector getDuplicateSearchCollector(String name) {
		TracSearchFilter filter = new TracSearchFilter("description");
		filter.setOperator(CompareOperator.CONTAINS);

		String searchString = AbstractNewRepositoryTaskEditor.getStackTraceFromDescription(taskData.getDescription());

		filter.addValue(searchString);

		TracSearch search = new TracSearch();
		search.addFilter(filter);

		// TODO copied from TracCustomQueryPage.getQueryUrl()
		StringBuilder sb = new StringBuilder();
		sb.append(repository.getUrl());
		sb.append(ITracClient.QUERY_URL);
		sb.append(search.toUrl());

		TracRepositoryQuery query = new TracRepositoryQuery(repository.getUrl(), sb.toString(), "<Duplicate Search>");

		SearchHitCollector collector = new SearchHitCollector(TasksUiPlugin.getTaskListManager().getTaskList(),
				repository, query, new TaskFactory(repository, false, false));
		return collector;
	}

	@Override
	protected void createPeopleLayout(Composite composite) {
		FormToolkit toolkit = getManagedForm().getToolkit();
		Section peopleSection = createSection(composite, getSectionLabel(SECTION_NAME.PEOPLE_SECTION));
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleSection);
		Composite peopleComposite = toolkit.createComposite(peopleSection);
		GridLayout layout = new GridLayout(2, false);
		layout.marginRight = 5;
		peopleComposite.setLayout(layout);
		GridDataFactory.fillDefaults().align(SWT.FILL, SWT.FILL).grab(true, true).applyTo(peopleComposite);

		Label label = toolkit.createLabel(peopleComposite, "Assign to:");
		GridDataFactory.fillDefaults().align(SWT.RIGHT, SWT.CENTER).applyTo(label);
		Composite textFieldComposite = toolkit.createComposite(peopleComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textFieldComposite);
		GridLayout textLayout = new GridLayout();
		textFieldComposite.setLayout(textLayout);

		RepositoryTaskAttribute attribute = taskData.getAttribute(RepositoryTaskAttribute.USER_ASSIGNED);

		Text textField = createTextField(textFieldComposite, attribute, SWT.FLAT);
		toolkit.paintBordersFor(textFieldComposite);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(textField);
		peopleSection.setClient(peopleComposite);

		ContentAssistCommandAdapter adapter = applyContentAssist(textField, createContentProposalProvider(attribute));

		ILabelProvider propsalLabelProvider = createProposalLabelProvider(attribute);
		if (propsalLabelProvider != null) {
			adapter.setLabelProvider(propsalLabelProvider);
		}
		adapter.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);

		toolkit.paintBordersFor(peopleComposite);
	}

}
