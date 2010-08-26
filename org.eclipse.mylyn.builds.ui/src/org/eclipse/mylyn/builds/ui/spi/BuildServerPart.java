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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ICheckStateProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IBuildServerConfiguration;
import org.eclipse.mylyn.builds.core.spi.BuildServerConfiguration;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.commons.core.IOperationMonitor;
import org.eclipse.mylyn.commons.core.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.repositories.RepositoryValidator;
import org.eclipse.mylyn.commons.ui.team.RepositoryLocationPart;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.ui.BuildServerValidator;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.provisional.commons.ui.SubstringPatternFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Steffen Pingel
 */
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

	private class Validator extends BuildServerValidator {

		private IBuildServerConfiguration configuration;

		public Validator(IBuildServer server) {
			super(server);
		}

		public IBuildServerConfiguration getConfiguration() {
			return configuration;
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			IOperationMonitor progress = ProgressUtil.convert(monitor, "Validating repository", 2);
			progress.addFlag(OperationFlag.BACKGROUND);
			try {
				IStatus result = getServer().validate(progress.newChild(1));
				if (result.isOK()) {
					configuration = getServer().refreshConfiguration(progress.newChild(2));
				}
				return result;
			} catch (CoreException e) {
				return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "Server validation failed", e);
			}
		}

	}

	private final IBuildServer model;

	private CheckboxTreeViewer planViewer;

	private List<IBuildPlan> selectedPlans;

	public BuildServerPart(IBuildServer model) {
		super(model.getLocation());
		this.model = model;
		this.selectedPlans = Collections.emptyList();
	}

	@Override
	protected void applyValidatorResult(RepositoryValidator validator) {
		super.applyValidatorResult(validator);
		if (!validator.getResult().isOK()) {
			StatusHandler.log(validator.getResult());
		}
		IBuildServerConfiguration configuration = ((Validator) validator).getConfiguration();
		if (configuration != null) {
			// update available plans
			setInput(configuration, getSelectedPlans());
		}
	}

	protected void setInput(IBuildServerConfiguration configuration, Collection<IBuildPlan> selectedPlans) {
		Set<String> selectedIds = BuildsUiInternal.toSetOfIds(selectedPlans);
		for (IBuildPlan plan : configuration.getPlans()) {
			((BuildPlan) plan).setSelected(selectedIds.contains(plan.getId()));
		}
		planViewer.setInput(configuration);
		planViewer.expandAll();
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

		// XXX spacer to make layout consistent
		Button dummyButton = new Button(section, SWT.CHECK);
		dummyButton.setVisible(false);
		dummyButton.setText("Save Password");
	}

	@Override
	public Control createContents(Composite parent) {
		Control control = super.createContents(parent);
		try {
			IBuildServerConfiguration configuration = getModel().getConfiguration();
			setInput(configuration, selectedPlans);
		} catch (CoreException e) {
			// ignore
		}
		return control;
	}

	@Override
	protected Control createAdditionalContents(final Composite parent) {
//		SectionComposite sectionComposite = new SectionComposite(parent, SWT.NONE);
//		//sectionComposite.setMinHeight(150);
//		sectionComposite.setExpandVertical(false);

		ExpandableComposite section = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT | ExpandableComposite.COMPACT | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = 0;
		section.setBackground(parent.getBackground());
		section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				parent.layout(true);
			}
		});
		section.setText("Build Plans");
//		ExpandableComposite section = section.createSection("Build Plans");
//		section.setExpanded(true);
//		if (section.getLayoutData() instanceof GridData) {
//			GridData gd = ((GridData) section.getLayoutData());
//			gd.grabExcessVerticalSpace = true;
//			gd.verticalAlignment = SWT.FILL;
//			gd.minimumHeight = 150;
//		}

		Composite composite = new Composite(section, SWT.NONE);
		section.setClient(composite);
		GridLayoutFactory.fillDefaults().numColumns(2).margins(0, 5).applyTo(composite);

		CheckboxFilteredTree filteredTree = new CheckboxFilteredTree(composite, SWT.FULL_SELECTION | SWT.BORDER,
				new SubstringPatternFilter());
		GridDataFactory.fillDefaults().grab(true, true).hint(SWT.DEFAULT, 200).applyTo(filteredTree);
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
		planViewer.setContentProvider(new ITreeContentProvider() {
			private BuildServerConfiguration configuration;

			private final Object[] EMPTY_ARRAY = new Object[0];

			public void dispose() {
				// ignore
			}

			public Object[] getChildren(Object parentElement) {
				return EMPTY_ARRAY;
			}

			public Object[] getElements(Object inputElement) {
				return configuration.getPlans().toArray();
			}

			public Object getParent(Object element) {
				return null;
			}

			public boolean hasChildren(Object element) {
				return false;
			}

			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				configuration = (BuildServerConfiguration) newInput;
			}
		});
		planViewer.setSorter(new ViewerSorter());

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(buttonComposite);
		GridLayoutFactory.fillDefaults().numColumns(1).margins(0, 0).extendedMargins(0, 0, 0, 0).applyTo(
				buttonComposite);
		createButtons(buttonComposite);

		return section;
	}

	public final IBuildServer getModel() {
		return model;
	}

	public List<IBuildPlan> getSelectedPlans() {
		if (planViewer.getInput() instanceof BuildServerConfiguration) {
			BuildServerConfiguration configuration = (BuildServerConfiguration) planViewer.getInput();
			List<IBuildPlan> selectedPlans = new ArrayList<IBuildPlan>();
			for (IBuildPlan plan : configuration.getPlans()) {
				if (plan.isSelected()) {
					selectedPlans.add(plan);
				}
			}
			return selectedPlans;
		}
		return Collections.emptyList();
	}

	@Override
	protected RepositoryValidator getValidator() {
		return new Validator(getModel());
	}

	public void initSelectedPlans(List<IBuildPlan> selectedPlans) {
		Assert.isNotNull(selectedPlans);
		this.selectedPlans = selectedPlans;
	}

}
