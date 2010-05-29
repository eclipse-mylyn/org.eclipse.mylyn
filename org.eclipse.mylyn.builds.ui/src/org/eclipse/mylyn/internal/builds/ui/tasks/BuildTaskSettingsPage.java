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
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.StyledCellLabelProvider;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.internal.builds.core.tasks.BuildTaskConnector;
import org.eclipse.mylyn.internal.builds.ui.view.BuildContentProvider;
import org.eclipse.mylyn.internal.tasks.core.IRepositoryConstants;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.wizards.AbstractRepositorySettingsPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
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

		private final TaskRepository repository;

		public BuildValidator(TaskRepository repository) {
			this.repository = repository;
		}

		@Override
		public void run(IProgressMonitor monitor) throws CoreException {
			IBuildServer server = BuildsUi.createServer(repository);
			IStatus result = server.validate(monitor);
			setStatus(result);
		}
	}

	private CheckboxTreeViewer planViewer;

	private Combo connectorCombo;

	private List<BuildConnector> connectors;

	public BuildTaskSettingsPage(TaskRepository taskRepository) {
		super("Add Continuous Integration Server", "Select a server type.", taskRepository);
		setNeedsHttpAuth(true);
		setNeedsAdvanced(false);
		setNeedsEncoding(false);
		setNeedsAnonymousLogin(true);
	}

	@Override
	public void applyTo(TaskRepository repository) {
		super.applyTo(repository);
		repository.setProperty(IRepositoryConstants.PROPERTY_CATEGORY, IRepositoryConstants.CATEGORY_BUILD);
	}

	@Override
	protected void createAdditionalControls(Composite parent) {
		// ignore
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
	}

	private void createButtons(Composite section) {
		Button button = new Button(section, SWT.PUSH);
		button.setText("Refresh");
		button.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDown(MouseEvent e) {
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

		planViewer = new CheckboxTreeViewer(composite, SWT.FULL_SELECTION | SWT.BORDER);
		GridDataFactory.fillDefaults().grab(true, true).applyTo(planViewer.getControl());

		TreeViewerColumn column1 = new TreeViewerColumn(planViewer, SWT.LEFT | SWT.FILL);
		column1.setLabelProvider(new StyledCellLabelProvider() {
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
	public String getConnectorKind() {
		return BuildTaskConnector.CONNECTOR_KIND;
	}

	@Override
	protected Validator getValidator(TaskRepository repository) {
		return new BuildValidator(repository);
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

	public BuildConnector getBuildConnector() {
		int index = connectorCombo.getSelectionIndex();
		if (index != -1) {
			return connectors.get(index);
		}
		return null;
	}

	@Override
	public boolean isPageComplete() {
		return getBuildConnector() != null && super.isPageComplete();
	}

}
