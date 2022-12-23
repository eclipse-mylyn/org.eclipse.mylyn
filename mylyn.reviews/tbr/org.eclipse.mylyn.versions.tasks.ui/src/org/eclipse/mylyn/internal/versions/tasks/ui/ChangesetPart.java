/*******************************************************************************
 * Copyright (c) 2011 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.versions.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.core.ITaskContainer;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.mylyn.versions.tasks.ui.AbstractChangesetMappingProvider;
import org.eclipse.mylyn.versions.tasks.ui.TaskChangesetUtil;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsContributionAction;
import org.eclipse.mylyn.versions.tasks.ui.spi.ITaskVersionsModel;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Kilian Matt
 */
@SuppressWarnings("restriction")
public class ChangesetPart extends AbstractFormPart {
	private TableViewer table;

	private final ChangesetModel model = new ChangesetModel();

	private ChangeSetPage page;

	public Control createControl(Composite parent, FormToolkit toolkit) {
		Section createSection = createSection(parent, toolkit);
		Composite composite = createContentComposite(toolkit, createSection);

		createTable(composite);
		return createSection;
	}

	@Override
	public void initialize(IManagedForm form) {
		super.initialize(form);
	}

	public void initialize(ChangeSetPage page) {
		this.page = page;
	}

	private Composite createContentComposite(FormToolkit toolkit, Section createSection) {
		Composite composite = toolkit.createComposite(createSection);
		createSection.setClient(composite);
		composite.setLayout(new FillLayout());
		return composite;
	}

	private Section createSection(Composite parent, FormToolkit toolkit) {
		Section createSection = toolkit.createSection(parent, ExpandableComposite.TITLE_BAR
				| ExpandableComposite.TWISTIE | ExpandableComposite.EXPANDED);
		createSection.setText("Changesets");
		GridLayout gl = new GridLayout(1, false);
		gl.marginBottom = 16;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		createSection.setLayout(gl);
		createSection.setLayoutData(gd);
		addToolBar(toolkit, createSection);
		return createSection;
	}

	private void createTable(Composite composite) {
		table = new TableViewer(composite);
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		addColumn(table, "Id");
		addColumn(table, "Message");
		addColumn(table, "Author");
		addColumn(table, "Date");
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setLabelProvider(new TaskChangesetLabelProvider());
		refreshInput();
		registerContextMenu(table);
	}

	private void addToolBar(FormToolkit toolkit, Section section) {
		ToolBarManager toolBarManager = new ToolBarManager(SWT.FLAT);
		fillToolBar(toolBarManager);

		if (toolBarManager.getSize() == 0) {
			return;
		}
		Composite toolbarComposite = toolkit.createComposite(section);
		toolbarComposite.setBackground(null);
		RowLayout rowLayout = new RowLayout();
		rowLayout.marginLeft = 0;
		rowLayout.marginRight = 0;
		rowLayout.marginTop = 0;
		rowLayout.marginBottom = 0;
		rowLayout.center = true;
		toolbarComposite.setLayout(rowLayout);

		toolBarManager.createControl(toolbarComposite);
		section.clientVerticalSpacing = 0;
		section.descriptionVerticalSpacing = 0;
		section.setTextClient(toolbarComposite);
	}

	private void fillToolBar(ToolBarManager toolBarManager) {
		toolBarManager.add(new IncludeSubTasksAction(model));
		List<ITaskVersionsContributionAction> contributions = InternalExtensionPointLoader.loadActionContributions();
		for (final ITaskVersionsContributionAction action : contributions) {
			toolBarManager.add(new ActionDelegate(action) {
				@Override
				public void runWithEvent(Event event) {
					action.run(model);
				}
			});
		}
	}

	private void registerContextMenu(TableViewer table) {
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getPage().getEditorSite()
				.registerContextMenu("org.eclipse.mylyn.versions.changesets", menuManager, table, true);
		Menu menu = menuManager.createContextMenu(table.getControl());
		table.getTable().setMenu(menu);
	}

	private void addColumn(TableViewer table, String name) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(table, SWT.LEFT);
		tableViewerColumn.getColumn().setText(name);
		tableViewerColumn.getColumn().setWidth(100);
	}

	private AbstractChangesetMappingProvider determineBestProvider(final ITask task) {
		AbstractChangesetMappingProvider bestProvider = new NullProvider();
		int score = Integer.MIN_VALUE;
		for (AbstractChangesetMappingProvider mappingProvider : TaskChangesetUtil.getMappingProviders()) {
			if (score < mappingProvider.getScoreFor(task)) {
				bestProvider = mappingProvider;
			}
		}
		return bestProvider;
	}

	private static class NullProvider extends AbstractChangesetMappingProvider {

		@Override
		public void getChangesetsForTask(IChangeSetMapping mapping, IProgressMonitor monitor) throws CoreException {
		}

		@Override
		public int getScoreFor(ITask task) {
			return 0;
		}

	}

	private IChangeSetMapping createChangeSetMapping(final ITask task, final List<TaskChangeSet> changesets) {
		return new IChangeSetMapping() {

			public ITask getTask() {
				return task;
			}

			public void addChangeSet(ChangeSet changeset) {
				changesets.add(new TaskChangeSet(task, changeset));
			}
		};
	}

	private void refreshInput() {
		table.setInput(model.getInput());
	}

	private class ChangesetModel implements ITaskVersionsModel {

		private boolean includeSubTasks;

		public void setIncludeSubTasks(boolean includeSubTasks) {
			boolean isChanged = this.includeSubTasks ^ includeSubTasks;
			this.includeSubTasks = includeSubTasks;
			if (isChanged) {
				refreshInput();
			}
		}

		public List<TaskChangeSet> getInput() {
			final ITask task = getPage().getTask();

			AbstractChangesetMappingProvider bestProvider = determineBestProvider(task);
			final List<TaskChangeSet> changesets = new ArrayList<TaskChangeSet>();

			final List<IChangeSetMapping> changesetsMapping = new ArrayList<IChangeSetMapping>();
			changesetsMapping.add(createChangeSetMapping(task, changesets));
			;
			if (includeSubTasks) {
				if (task instanceof ITaskContainer) {
					ITaskContainer taskContainer = (ITaskContainer) task;
					for (ITask subTask : taskContainer.getChildren()) {
						changesetsMapping.add(createChangeSetMapping(subTask, changesets));
					}
				}
			}
			final AbstractChangesetMappingProvider provider = bestProvider;
			BusyIndicator.showWhile(Display.getDefault(), new Runnable() {

				public void run() {
					try {
						for (IChangeSetMapping csm : changesetsMapping) {
							provider.getChangesetsForTask(csm, new NullProgressMonitor());
						}
					} catch (CoreException e) {
					}
				}

			});

			return changesets;
		}
	}

	private ChangeSetPage getPage() {
		return page;
	}
}
