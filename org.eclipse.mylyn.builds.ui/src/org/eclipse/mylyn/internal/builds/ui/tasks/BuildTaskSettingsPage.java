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

package org.eclipse.mylyn.internal.builds.ui.tasks;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.IOperationMonitor;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.util.ProgressUtil;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.internal.builds.core.BuildPlan;
import org.eclipse.mylyn.internal.builds.core.BuildServer;
import org.eclipse.mylyn.internal.builds.core.tasks.BuildTaskConnector;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryChangeListener;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryAdapter;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryChangeEvent;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryDelta;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.TasksUi;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

/**
 * @author Steffen Pingel
 */
public class BuildTaskSettingsPage extends AbstractRepositorySettingsPage {

	class BuildValidator extends Validator {

		private List<IBuildPlan> plans;

		private final IBuildServer server;

		public BuildValidator(IBuildServer server) {
			this.server = server;
		}

		public List<IBuildPlan> getPlans() {
			return plans;
		}

		public IBuildServer getServer() {
			return server;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			IOperationMonitor progress = ProgressUtil.convert(monitor, 2);
			IStatus result = server.validate(progress.newChild(1));
			if (result.isOK()) {
				plans = server.getPlans(progress.newChild(2));
			}
			setStatus(result);
		}
	}

	private Combo connectorCombo;

	private List<BuildConnector> connectors;

	private CheckboxTreeViewer planViewer;

	private IBuildServer server;

	private BuildServer workingCopy;

	private final RepositoryListener repositoryListener;

	private class RepositoryListener extends TaskRepositoryAdapter implements IRepositoryChangeListener {

		@Override
		public void repositoryAdded(TaskRepository repository) {
			updateServer();
		}

		private void updateServer() {
			if (server != null) {
				BuildsUi.getModel().getServers().remove(server);
			}
			BuildsUi.getModel().getServers().add(workingCopy);
		}

		public void repositoryChanged(TaskRepositoryChangeEvent event) {
			if (event.getDelta().getType() == TaskRepositoryDelta.Type.ALL) {
				updateServer();
			}
		}

	}

	public BuildTaskSettingsPage(TaskRepository taskRepository) {
		super("Add Continuous Integration Server", "Select a server type.", taskRepository);
		setNeedsHttpAuth(true);
		setNeedsAdvanced(false);
		setNeedsEncoding(false);
		setNeedsAnonymousLogin(true);
		repositoryListener = new RepositoryListener();
		TasksUi.getRepositoryManager().addListener(repositoryListener);
	}

	@Override
	public void dispose() {
		TasksUi.getRepositoryManager().removeListener(repositoryListener);
		super.dispose();
	}

	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY, IRepositoryConstants.CATEGORY_BUILD);
		BuildConnector buildConnector = getBuildConnector();
		if (buildConnector != null) {
			repository.setProperty(BuildTaskConnector.TASK_REPOSITORY_KEY_BUILD_CONNECTOR_KIND,
					buildConnector.getConnectorKind());
		} else {
			repository.setProperty(BuildTaskConnector.TASK_REPOSITORY_KEY_BUILD_CONNECTOR_KIND, null);
		}

		BuildServer server = getWorkingCopy();
		server.setConnectorKind(repository.getProperty(BuildTaskConnector.TASK_REPOSITORY_KEY_BUILD_CONNECTOR_KIND));
		server.setName(repository.getRepositoryLabel());
		server.setRepository(repository);
		server.setRepositoryUrl(repository.getRepositoryUrl());

//		XMLMemento memento = XMLMemento.createWriteRoot("plans");
//		String[] array = getSelectedPlanIds().toArray(new String[0]);
//		for (int i = 0; i < array.length; i++) {
//			memento.putString("plan." + i, array[i]);
//		}
//		StringWriter writer = new StringWriter();
//		try {
//			memento.save(writer);
//		} catch (IOException e) {
//			// ignore
//		}
//		repository.setProperty("selected", writer.toString());
	}

	@Override
	protected void applyValidatorResult(Validator validator) {
		super.applyValidatorResult(validator);
		if (((BuildValidator) validator).getPlans() != null) {
			Set<String> selectedIds = getSelectedPlanIds();
			planViewer.refresh();
			IBuildServer server = ((BuildValidator) validator).getServer();
			List<IBuildPlan> selectedPlans = new ArrayList<IBuildPlan>();
			for (IBuildPlan plan : server.getPlans()) {
				if (selectedIds.contains(plan.getId())) {
					selectedPlans.add(plan);
					((BuildPlan) plan).setSelected(true);
				}
			}
			planViewer.setCheckedElements(selectedPlans.toArray());
			planViewer.expandAll();
		}
	}

	protected Set<String> getSelectedPlanIds() {
		Object[] checkedElements = planViewer.getCheckedElements();
		Set<String> selectedIds = new HashSet<String>();
		for (Object object : checkedElements) {
			selectedIds.add(((IBuildPlan) object).getId());
		}
		return selectedIds;
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore
	}

	private void createButtons(Composite section) {
		Button button = new Button(section, SWT.PUSH);
		button.setText("&Refresh");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent event) {
				validateSettings();
			}
		});
	}

	@Override
	protected void createContributionControls(Composite parentControl) {
		// don't call super since the build connector does not take advantage of the tasks UI extensions

		ExpandableComposite section = createSection(parentControl, "Build Plans");
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

		Composite container = new Composite(composite, SWT.None);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(container);
		TreeColumnLayout columnLayout = new TreeColumnLayout();
		container.setLayout(columnLayout);

		planViewer = new CheckboxTreeViewer(container, SWT.FULL_SELECTION | SWT.BORDER);
		planViewer.addCheckStateListener(new ICheckStateListener() {
			public void checkStateChanged(CheckStateChangedEvent event) {
				BuildPlan plan = (BuildPlan) event.getElement();
				plan.setSelected(event.getChecked());
			}
		});
//		planViewer.setCheckStateProvider(new ICheckStateProvider() {
//			public boolean isChecked(Object element) {
//				return ((IBuildPlan) element).isSelected();
//			}
//
//			public boolean isGrayed(Object element) {
//				for (IBuildPlan child : ((IBuildPlan) element).getChildren()) {
//					if (!child.isSelected()) {
//						return true;
//					}
//				}
//				return false;
//			}
//		});

		TreeViewerColumn planColumn = new TreeViewerColumn(planViewer, SWT.LEFT | SWT.FILL);
		columnLayout.setColumnData(planColumn.getColumn(), new ColumnWeightData(100, true));
		planColumn.setLabelProvider(new StyledCellLabelProvider() {
			@Override
			public void update(ViewerCell cell) {
				cell.setText(((IBuildPlan) cell.getElement()).getName());
			}
		});

		planViewer.setContentProvider(new BuildContentProvider());
		planViewer.expandAll();

		Composite buttonComposite = new Composite(composite, SWT.NONE);
		GridDataFactory.fillDefaults().align(SWT.BEGINNING, SWT.TOP).applyTo(buttonComposite);
		GridLayoutFactory.fillDefaults()
				.numColumns(1)
				.margins(0, 0)
				.extendedMargins(5, 0, 0, 0)
				.applyTo(buttonComposite);
		createButtons(buttonComposite);

		Dialog.applyDialogFont(composite);
		setControl(composite);
	}

	@Override
	protected void createSettingControls(Composite parent) {
		Label connectorLabel = new Label(parent, SWT.NONE);
		connectorLabel.setText("Type:");
		connectorCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);
		GridDataFactory.fillDefaults().grab(true, false).span(2, SWT.DEFAULT).applyTo(connectorCombo);
		connectorCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		});
		connectorCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (getWizard() != null) {
					getWizard().getContainer().updateButtons();
				}
			}
		});
		connectors = new ArrayList(BuildsUi.getConnectors());
		for (BuildConnector connector : connectors) {
			connectorCombo.add(connector.getLabel());
		}

		super.createSettingControls(parent);

		if (repository != null) {
			BuildConnector buildConnector = BuildsUi.getConnector(repository);
			if (buildConnector != null) {
				int index = connectors.indexOf(buildConnector);
				if (index != -1) {
					connectorCombo.select(index);
				}
			}
		}

		BuildServer input = getWorkingCopy();
		planViewer.setInput(input);
		for (IBuildPlan plan : input.getPlans()) {
			planViewer.setChecked(plan, plan.isSelected());
		}
		planViewer.expandAll();
	}

	public BuildConnector getBuildConnector() {
		int index = connectorCombo.getSelectionIndex();
		if (index != -1) {
			return connectors.get(index);
		}
		return null;
	}

	@Override
	public String getConnectorKind() {
		return BuildTaskConnector.CONNECTOR_KIND;
	}

	private IBuildServer getServer() {
		if (server != null) {
			return server;
		}
		if (repository != null) {
			server = BuildsUiInternal.getServer(repository);
		}
		if (server == null) {
			server = BuildsUiInternal.createServer(repository);
		}
		return server;
	}

	private BuildServer getWorkingCopy() {
		if (workingCopy == null) {
			workingCopy = ((BuildServer) getServer()).createWorkingCopy();
		}
		return workingCopy;
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new BuildValidator(getWorkingCopy());
	}

	@Override
	public boolean isPageComplete() {
		return getBuildConnector() != null && super.isPageComplete();
	}

	@Override
	protected boolean isValidUrl(String name) {
		if ((name.startsWith(URL_PREFIX_HTTPS) || name.startsWith(URL_PREFIX_HTTP)) && !name.endsWith("/")) { //$NON-NLS-1$
			try {
				new URL(name);
				return true;
			} catch (MalformedURLException e) {
				// ignore
			}
		}
		return false;
	}

}
