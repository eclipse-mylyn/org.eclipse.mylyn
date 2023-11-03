/*******************************************************************************
 * Copyright (c) 2023 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gitlab.ui;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.gitlab.core.GitlabCoreActivator;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

public class GitlabRepositorySettingsPage extends AbstractRepositorySettingsPage {

	private ListViewer groups;

	private ArrayList<String> groupList = new ArrayList<>();

	private Text groupInput;

	private Button groupRemoveButton;

	private Button groupAddButton;

	private ListViewer projects;

	private ArrayList<String> projectList = new ArrayList<>();

	private Text projectInput;

	private Button projectRemoveButton;

	private Button projectAddButton;

	private Button avatarSupportButton;

	private Button showActivityIconsButton;

	private Button usePersonalAccessTokenButton;

	private Text personalAccessTokenText;

	public GitlabRepositorySettingsPage(String title, String description, TaskRepository taskRepository) {
		super(title, description, taskRepository);
		setNeedsEncoding(false);
		setNeedsTimeZone(false);
		setNeedsProxy(false);
	}

	@Override
	public String getConnectorKind() {
		return GitlabCoreActivator.CONNECTOR_KIND;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		FormToolkit toolkit = new FormToolkit(TasksUiPlugin.getDefault().getFormColors(parent.getDisplay()));
		Composite aditionalContainer = new Composite(parent, SWT.NONE);
		GridLayoutFactory.fillDefaults().margins(0, 0).numColumns(3).applyTo(aditionalContainer);
		GridDataFactory.fillDefaults().grab(true, true).align(SWT.FILL, SWT.FILL).applyTo(aditionalContainer);

		Label groupsLabel = new Label(aditionalContainer, SWT.NONE);
		groupsLabel.setText("include Groups");
		GridData gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 3;
		groupsLabel.setLayoutData(gd);
		groups = new ListViewer(aditionalContainer, SWT.H_SCROLL | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		groups.getList().setLayoutData(gd);
		groups.setLabelProvider(new LabelProvider());
		groups.setContentProvider(ArrayContentProvider.getInstance());
		if (repository != null) {
			String groupsValue = repository.getProperty(GitlabCoreActivator.GROUPS);
			if (groupsValue != null)
				groupList = new ArrayList<String>(Arrays.asList(groupsValue.split(",")));
		}
		groups.setInput(groupList);
		groupInput = new Text(aditionalContainer, SWT.NONE);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 200;
		groupInput.setLayoutData(gd);
		groupInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				updateUIEnablement();
			}

		});
		groups.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					groupInput.setText((String) selection.getFirstElement());
				}
				updateUIEnablement();
			}
		});

		groupAddButton = new Button(aditionalContainer, SWT.PUSH);
		groupAddButton.setText("Add");
		groupAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				groupList.add(groupInput.getText());
				groupInput.setText("");
				groups.refresh();
				updateUIEnablement();
			}
		});

		groupRemoveButton = new Button(aditionalContainer, SWT.PUSH);
		groupRemoveButton.setText("remove");
		groupRemoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) groups.getSelection();
				int lastIdx = -1;
				if (selection != null) {
					Object firstElement = selection.getFirstElement();
					lastIdx = groupList.indexOf(firstElement);
				}
				groupList.remove(groupInput.getText());
				groupInput.setText("");
				groups.refresh();
				if (lastIdx >= 0 && lastIdx < groupList.size()) {
					groups.setSelection(new StructuredSelection(groupList.get(lastIdx)), true);
				}
				updateUIEnablement();
			}
		});

		Label projectsLabel = new Label(aditionalContainer, SWT.NONE);
		projectsLabel.setText("include Projects");
		gd = new GridData(GridData.VERTICAL_ALIGN_CENTER);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 3;
		projectsLabel.setLayoutData(gd);
		projects = new ListViewer(aditionalContainer, SWT.H_SCROLL | SWT.V_SCROLL);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 3;
		gd.grabExcessHorizontalSpace = true;
		projects.getList().setLayoutData(gd);
		projects.setLabelProvider(new LabelProvider());
		projects.setContentProvider(ArrayContentProvider.getInstance());
		if (repository != null) {
			String projectsValue = repository.getProperty(GitlabCoreActivator.PROJECTS);
			if (projectsValue != null)
				projectList = new ArrayList<String>(Arrays.asList(projectsValue.split(",")));
		}
		projects.setInput(projectList);
		projectInput = new Text(aditionalContainer, SWT.NONE);
		gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		gd.widthHint = 200;
		projectInput.setLayoutData(gd);
		projectInput.addKeyListener(new KeyAdapter() {

			@Override
			public void keyReleased(KeyEvent e) {
				super.keyReleased(e);
				updateUIEnablement();
			}

		});
		projects.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event.getSelection();
				if (selection.size() > 0) {
					projectInput.setText((String) selection.getFirstElement());
				}
				updateUIEnablement();
			}
		});

		projectAddButton = new Button(aditionalContainer, SWT.PUSH);
		projectAddButton.setText("Add");
		projectAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				projectList.add(projectInput.getText());
				projectInput.setText("");
				projects.refresh();
				updateUIEnablement();
			}
		});

		projectRemoveButton = new Button(aditionalContainer, SWT.PUSH);
		projectRemoveButton.setText("remove");
		projectRemoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IStructuredSelection selection = (IStructuredSelection) projects.getSelection();
				int lastIdx = -1;
				if (selection != null) {
					Object firstElement = selection.getFirstElement();
					lastIdx = projectList.indexOf(firstElement);
				}
				projectList.remove(projectInput.getText());
				projectInput.setText("");
				projects.refresh();
				if (lastIdx >= 0 && lastIdx < projectList.size()) {
					projects.setSelection(new StructuredSelection(projectList.get(lastIdx)), true);
				}
				updateUIEnablement();
			}
		});

		avatarSupportButton = new Button(aditionalContainer, SWT.CHECK);
		avatarSupportButton.setText("Show Avantars");
		avatarSupportButton.setSelection(getRepository() != null
				&& Boolean.parseBoolean(getRepository().getProperty(GitlabCoreActivator.AVANTAR)));

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		avatarSupportButton.setLayoutData(gd);

		showActivityIconsButton = new Button(aditionalContainer, SWT.CHECK);
		showActivityIconsButton.setText("Show Activity Icons");
		if (getRepository() != null) {
			String value = getRepository().getProperty(GitlabCoreActivator.SHOW_ACTIVITY_ICONS);
			showActivityIconsButton.setSelection(value == null ? true : Boolean.parseBoolean(value));
		}

		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 3;
		gd.verticalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		showActivityIconsButton.setLayoutData(gd);

		usePersonalAccessTokenButton = new Button(aditionalContainer, SWT.CHECK);
		usePersonalAccessTokenButton.setText("use personal access token");
		usePersonalAccessTokenButton.setSelection(getRepository() != null
				&& Boolean.parseBoolean(getRepository().getProperty(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN)));

		gd = new GridData(GridData.BEGINNING);
		gd.horizontalSpan = 1;
		gd.verticalSpan = 1;
		gd.grabExcessHorizontalSpace = false;
		usePersonalAccessTokenButton.setLayoutData(gd);
		usePersonalAccessTokenButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateUIEnablement();
			}
		});

		personalAccessTokenText = new Text(aditionalContainer, SWT.None);

		String accessTokenValue = getRepository() != null
				? getRepository().getProperty(GitlabCoreActivator.PERSONAL_ACCESS_TOKEN)
				: null;
		if (accessTokenValue == null)
			accessTokenValue = "";
		personalAccessTokenText.setText(accessTokenValue);
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 2;
		gd.verticalSpan = 1;
		gd.grabExcessHorizontalSpace = true;
		personalAccessTokenText.setLayoutData(gd);

		updateUIEnablement();
	}

	private void updateUIEnablement() {
		IStructuredSelection selection = (IStructuredSelection) groups.getSelection();
		groupRemoveButton.setEnabled(selection.size() > 0);
		groupAddButton.setEnabled(!groupInput.getText().isEmpty());

		selection = (IStructuredSelection) projects.getSelection();
		projectRemoveButton.setEnabled(selection.size() > 0);
		projectAddButton.setEnabled(!projectInput.getText().isEmpty());
		personalAccessTokenText.setEnabled(usePersonalAccessTokenButton.getSelection());
	}

	@Override
	public void applyTo(TaskRepository repository) {
		repository.setProperty(GitlabCoreActivator.GROUPS, groupList.size() == 0 ? null : String.join(",", groupList));
		repository.setProperty(GitlabCoreActivator.PROJECTS,
				projectList.size() == 0 ? null : String.join(",", projectList));
		repository.setProperty(GitlabCoreActivator.AVANTAR, Boolean.toString(avatarSupportButton.getSelection()));
		repository.setProperty(GitlabCoreActivator.SHOW_ACTIVITY_ICONS,
				Boolean.toString(showActivityIconsButton.getSelection()));
		repository.setProperty(GitlabCoreActivator.USE_PERSONAL_ACCESS_TOKEN,
				Boolean.toString(usePersonalAccessTokenButton.getSelection()));
		repository.setProperty(GitlabCoreActivator.PERSONAL_ACCESS_TOKEN, personalAccessTokenText.getText());
		super.applyTo(repository);
	}

}
