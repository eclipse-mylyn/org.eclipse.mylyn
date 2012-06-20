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
package org.eclipse.mylyn.versions.tasks.ui;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.ui.editors.AbstractTaskEditorPart;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.tasks.core.IChangeSetMapping;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 * @author Kilian Matt
 * 
 */
@SuppressWarnings("restriction")
public class ChangesetPart extends AbstractTaskEditorPart {
	private static final class TaskChangesetLabelProvider implements
			ITableLabelProvider {
		public void addListener(ILabelProviderListener listener) {
		}

		public void dispose() {
		}

		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		public void removeListener(ILabelProviderListener listener) {
		}

		public Image getColumnImage(Object element, int columnIndex) {
			return null;
		}

		public String getColumnText(Object element, int columnIndex) {
			TaskChangeSet cs = ((TaskChangeSet) element);
			switch (columnIndex) {
			case 0:
				return cs.getChangeset().getId();
			case 1:
				return cs.getChangeset().getMessage();
			case 2:
				return cs.getChangeset().getAuthor().getEmail();
			case 3:
				return cs.getChangeset().getDate().toString();
			}
			return element.toString() + " " + columnIndex;
		}
	}

	public ChangesetPart() {
		setPartName("Changeset");
		setExpandVertically(true);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section createSection = createSection(parent, toolkit);
		Composite composite = createContentComposite(toolkit, createSection);
		
		createTable(composite);
	}

	private Composite createContentComposite(FormToolkit toolkit,
			Section createSection) {
		Composite composite = toolkit.createComposite(createSection);
		createSection.setClient(composite);
		composite.setLayout(new FillLayout());
		return composite;
	}

	private Section createSection(Composite parent, FormToolkit toolkit) {
		Section createSection = createSection(parent, toolkit, true);
		createSection.setText("Changesets");
		setSection(toolkit, createSection);
		GridLayout gl = new GridLayout(1, false);
		gl.marginBottom = 16;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		createSection.setLayout(gl);
		createSection.setLayoutData(gd);
		return createSection;
	}

	private void createTable(Composite composite) {
		TableViewer table = new TableViewer(composite);
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		addColumn(table, "Id");
		addColumn(table, "Message");
		addColumn(table, "Author");
		addColumn(table, "Date");
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setLabelProvider(new TaskChangesetLabelProvider());
		table.setInput(getInput());
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getTaskEditorPage().getEditorSite().registerContextMenu(
				"org.eclipse.mylyn.versions.changesets", menuManager, table, true);
		Menu menu = menuManager.createContextMenu(table.getControl());
		table.getTable().setMenu(menu);
	}

	private void addColumn(TableViewer table, String name) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(table,
				SWT.LEFT);
		tableViewerColumn.getColumn().setText(name);
		tableViewerColumn.getColumn().setWidth(100);
	}

	private List<TaskChangeSet> getInput() {
		int score = Integer.MIN_VALUE;
		AbstractChangesetMappingProvider bestProvider = null;
		final ITask task = getModel().getTask();

		for (AbstractChangesetMappingProvider mappingProvider : TaskChangesetUtil
				.getMappingProviders()) {
			if (score < mappingProvider.getScoreFor(task))
				;
			{
				bestProvider = mappingProvider;
			}
		}
		final List<TaskChangeSet> changesets = new ArrayList<TaskChangeSet>();
		try {

			IChangeSetMapping changesetsMapping = new IChangeSetMapping() {

				public ITask getTask() {
					return task;
				}

				public void addChangeSet(ChangeSet changeset) {
					changesets.add(new TaskChangeSet(task, changeset));
				}
			};
			// FIXME progress monitor
			bestProvider.getChangesetsForTask(changesetsMapping,
					new NullProgressMonitor());
		} catch (CoreException e) {
			// FIXME Auto-generated catch block
			e.printStackTrace();
		}
		return changesets;
	}

}
