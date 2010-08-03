/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.commons.repositories.RepositoryValidator;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildServerValidator;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider;
import org.eclipse.mylyn.internal.commons.ui.SectionComposite;
import org.eclipse.mylyn.internal.commons.ui.team.RepositoryLocationPart;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

public class BuildServerPart extends RepositoryLocationPart {

	private class CheckboxFilteredTree extends FilteredTree {

		public CheckboxFilteredTree(Composite parent, int treeStyle, PatternFilter filter) {
			super(parent, treeStyle, filter, true);
		}

		@Override
		protected TreeViewer doCreateTreeViewer(Composite parent, int style) {
			return new CheckboxTreeViewer(parent, style);
		}

		public CheckboxTreeViewer getCheckboxTreeViewer() {
			return getViewer();
		}

		@Override
		public CheckboxTreeViewer getViewer() {
			return (CheckboxTreeViewer) super.getViewer();
		}

	}

	private final IBuildServer model;

	private CheckboxTreeViewer planViewer;

	public BuildServerPart(IBuildServer model) {
		super(model.getLocation());
		this.model = model;
	}

	@Override
	public boolean canValidate() {
		return true;
	}

	private void createButtons(Composite section) {
		Button refreshButton = new Button(section, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(refreshButton);
		refreshButton.setText("&Refresh");
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				validate();
			}
		});

		Button selectAllButton = new Button(section, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(selectAllButton);
		selectAllButton.setText("&Select All");
		selectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] items = planViewer.getTree().getItems();
				for (TreeItem item : items) {
					if (item.getData() instanceof BuildPlan) {
						((BuildPlan) item.getData()).setSelected(true);
					}
				}
				planViewer.refresh();
			}
		});

		Button deselectAllButton = new Button(section, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(deselectAllButton);
		deselectAllButton.setText("&Deselect All");
		deselectAllButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				TreeItem[] items = planViewer.getTree().getItems();
				for (TreeItem item : items) {
					if (item.getData() instanceof BuildPlan) {
						((BuildPlan) item.getData()).setSelected(false);
					}
				}
				planViewer.refresh();
			}
		});
	}

	@Override
	protected void createSections(SectionComposite sectionComposite) {
		ExpandableComposite section = sectionComposite.createSection("Build Plans");
		section.setExpanded(true);
		if (section.getLayoutData() instanceof GridData) {
			GridData gd = ((GridData) section.getLayoutData());
			gd.grabExcessVerticalSpace = true;
			gd.verticalAlignment = SWT.FILL;
			gd.minimumHeight = 150;
		}

		Composite composite = new Composite(section, SWT.NONE);
		section.setClient(composite);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 5).applyTo(composite);

		CheckboxFilteredTree filteredTree = new CheckboxFilteredTree(composite, SWT.FULL_SELECTION | SWT.BORDER,
				new SubstringPatternFilter());
		planViewer = filteredTree.getCheckboxTreeViewer();//new CheckboxTreeViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);
		planViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				BuildPlan plan = (BuildPlan) event.getElement();
				plan.setSelected(event.getChecked());
			}
		});
		planViewer.setCheckStateProvider(new ICheckStateProvider() {
			public boolean isChecked(Object element) {
				return ((IBuildPlan) element).isSelected();
			}

			public boolean isGrayed(Object element) {
				for (IBuildPlan child : ((IBuildPlan) element).getChildren()) {
					if (!child.isSelected()) {
						return true;
					}
				}
				return false;
			}
		});

		planViewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IBuildPlan) element).getName();
			}
		});

		planViewer.setContentProvider(new BuildContentProvider());
		planViewer.expandAll();

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(buttonComposite);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).extendedMargins(5, 0, 0, 0).applyTo(
				buttonComposite);
		createButtons(buttonComposite);
	}

	public final IBuildServer getModel() {
		return model;
	}

	@Override
	protected RepositoryValidator getValidator() {
		return new BuildServerValidator(getModel());
	}

}
