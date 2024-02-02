/*******************************************************************************
 * Copyright (c) 2010, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.ui.spi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.UpdateValueStrategy;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
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
import org.eclipse.mylyn.builds.internal.core.BuildPlan;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.operations.RefreshConfigurationOperation;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor.OperationFlag;
import org.eclipse.mylyn.commons.core.operations.OperationUtil;
import org.eclipse.mylyn.commons.repositories.core.RepositoryValidator;
import org.eclipse.mylyn.commons.repositories.ui.RepositoryLocationPart;
import org.eclipse.mylyn.commons.workbench.SubstringPatternFilter;
import org.eclipse.mylyn.internal.builds.ui.BuildServerValidator;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
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

	private boolean urlValid = false;

	private Button refreshButton = null;

	public class BuildServerPartUrlValidator extends UrlValidator {
		@Override
		public IStatus validate(Object value) {
			IStatus validationStatus = super.validate(value);
			urlValid = validationStatus == Status.OK_STATUS;
			if (refreshButton != null) {
				refreshButton.setEnabled(urlValid);
			}
			return validationStatus;
		}
	}

	@Override
	protected UpdateValueStrategy getUrlUpdateValueStrategy() {
		return new UpdateValueStrategy().setAfterConvertValidator(new BuildServerPartUrlValidator());
	}

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
			IOperationMonitor progress = OperationUtil.convert(monitor, "Validating repository", 3);
			progress.addFlag(OperationFlag.BACKGROUND);
			try {
				IBuildServer server = getServer();
				IStatus result = ((BuildServer) server).validate(progress.newChild(1));
				if (result.isOK()) {
					RefreshConfigurationOperation op = new RefreshConfigurationOperation(
							Collections.singletonList(server));
					op.doRefresh((BuildServer) server, progress.newChild(2));
					result = op.getStatus();
					configuration = server.getConfiguration();
				}
				return result;
			} catch (CoreException e) {
				return new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
						NLS.bind("Server validation failed: {0}", e.getMessage()), e);
			}
		}

	}

	private final IBuildServer model;

	private CheckboxTreeViewer planViewer;

	private List<IBuildPlan> selectedPlans;

	public BuildServerPart(IBuildServer model) {
		super(model.getLocation());
		this.model = model;
		selectedPlans = Collections.emptyList();
		setNeedsProxy(true);
		setNeedsHttpAuth(true);
		setNeedsCertificateAuth(true);
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
		return super.canValidate() && urlValid;
	}

	private void createButtons(Composite section) {
		refreshButton = new Button(section, SWT.PUSH);
		GridDataFactory.fillDefaults().grab(true, false).applyTo(refreshButton);
		refreshButton.setText("&Refresh");
		refreshButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				validate();
			}
		});
		refreshButton.setEnabled(urlValid);

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

		final ExpandableComposite section = new ExpandableComposite(parent, SWT.NONE, ExpandableComposite.TWISTIE
				| ExpandableComposite.CLIENT_INDENT | ExpandableComposite.COMPACT | ExpandableComposite.EXPANDED);
		section.clientVerticalSpacing = 0;
		section.setBackground(parent.getBackground());
		section.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DIALOG_FONT));
		section.addExpansionListener(new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				if (section.getLayoutData() instanceof GridData) {
					((GridData) section.getLayoutData()).grabExcessVerticalSpace = e.getState();
				}
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
		planViewer.addCheckStateListener(event -> {
			BuildPlan plan = (BuildPlan) event.getElement();
			plan.setSelected(event.getChecked());
		});
		planViewer.setCheckStateProvider(new ICheckStateProvider() {
			@Override
			public boolean isChecked(Object element) {
				return ((IBuildPlan) element).isSelected();
			}

			@Override
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

			private final Object[] EMPTY_ARRAY = {};

			@Override
			public void dispose() {
				// ignore
			}

			@Override
			public Object[] getChildren(Object parentElement) {
				return EMPTY_ARRAY;
			}

			@Override
			public Object[] getElements(Object inputElement) {
				return configuration.getPlans().toArray();
			}

			@Override
			public Object getParent(Object element) {
				return null;
			}

			@Override
			public boolean hasChildren(Object element) {
				return false;
			}

			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				configuration = (BuildServerConfiguration) newInput;
			}
		});
		planViewer.setSorter(new ViewerSorter());

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(buttonComposite);
		GridLayoutFactory.fillDefaults()
				.numColumns(1)
				.margins(0, 0)
				.extendedMargins(0, 0, 0, 0)
				.applyTo(buttonComposite);
		createButtons(buttonComposite);

		return section;
	}

	public final IBuildServer getModel() {
		return model;
	}

	public List<IBuildPlan> getSelectedPlans() {
		if (planViewer.getInput() instanceof BuildServerConfiguration) {
			BuildServerConfiguration configuration = (BuildServerConfiguration) planViewer.getInput();
			List<IBuildPlan> selectedPlans = new ArrayList<>();
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
