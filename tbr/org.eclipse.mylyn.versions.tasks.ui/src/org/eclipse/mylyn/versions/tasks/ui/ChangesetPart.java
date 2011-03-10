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
import org.eclipse.mylyn.versions.core.ScmCore;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.spi.ScmConnector;
import org.eclipse.mylyn.versions.tasks.core.TaskChangeSet;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;

/**
 * 
 * @author Kilian Matt
 * 
 */
public class ChangesetPart extends AbstractTaskEditorPart {
	public ChangesetPart() {
		setPartName("Changeset");
		setExpandVertically(true);
	}

	@Override
	public void createControl(Composite parent, FormToolkit toolkit) {
		Section createSection = createSection(parent, toolkit, true);
		createSection.setText("Changesets");
		setSection(toolkit, createSection);
		GridLayout gl = new GridLayout(1, false);
		gl.marginBottom = 16;
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.horizontalSpan = 4;
		createSection.setLayout(gl);
		createSection.setLayoutData(gd);
		Composite composite = toolkit.createComposite(createSection);
		createSection.setClient(composite);
		composite.setLayout(new FillLayout());

		TableViewer table = new TableViewer(composite);
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);
		TableViewerColumn tableViewerColumn = new TableViewerColumn(table,
				SWT.LEFT);
		tableViewerColumn.getColumn().setText("Id");
		tableViewerColumn.getColumn().setWidth(100);
		tableViewerColumn = new TableViewerColumn(table, SWT.LEFT);
		tableViewerColumn.getColumn().setText("Message");
		tableViewerColumn.getColumn().setWidth(100);
		tableViewerColumn = new TableViewerColumn(table, SWT.LEFT);
		tableViewerColumn.getColumn().setText("Author");
		tableViewerColumn.getColumn().setWidth(100);
		tableViewerColumn = new TableViewerColumn(table, SWT.LEFT);
		tableViewerColumn.getColumn().setText("Date");
		tableViewerColumn.getColumn().setWidth(100);
		table.setContentProvider(ArrayContentProvider.getInstance());
		table.setLabelProvider(new ITableLabelProvider() {

			
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
		});
		table.setInput(getInput());
		MenuManager menuManager = new MenuManager();
		menuManager.setRemoveAllWhenShown(true);
		getTaskEditorPage().getEditorSite().registerContextMenu(
				"org.eclipse.mylyn.versions.changesets", menuManager, table,
				true);
		org.eclipse.swt.widgets.Menu menu = menuManager.createContextMenu(table
				.getControl());
		table.getTable().setMenu(menu);

	}

	private List<TaskChangeSet> getInput() {
		List<ScmConnector> connectors = ScmCore.getAllRegisteredConnectors();
		for (ScmConnector c : connectors) {
			try {
				List<ScmRepository> repositories = c
						.getRepositories(new NullProgressMonitor());
				for (ScmRepository r : repositories) {
					ITask task = getModel().getTask();
					List<TaskChangeSet> changes = new ArrayList<TaskChangeSet>();
					List<ChangeSet> changeSets = c.getChangeSets(r,
							new NullProgressMonitor());
					if (changeSets == null)
						continue;
					for (ChangeSet cs : changeSets) {
						if (changeSetMatches(cs))
							changes.add(new TaskChangeSet(task, cs));
					}
					if (!changes.isEmpty())
						return changes;
				}
			} catch (CoreException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return null;
	}

	private boolean changeSetMatches(ChangeSet cs) {
		return cs.getMessage().contains(getModel().getTask().getTaskKey())
				|| cs.getMessage().contains(getModel().getTask().getUrl());
	}

}
