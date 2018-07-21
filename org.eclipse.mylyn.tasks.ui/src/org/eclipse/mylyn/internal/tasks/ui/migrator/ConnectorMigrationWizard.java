/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.migrator;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.tasks.core.AbstractRepositoryConnector;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;

public class ConnectorMigrationWizard extends Wizard {
	private static class CollectionContentProvider implements ITreeContentProvider {
		@Override
		public Object[] getChildren(Object parentElement) {
			return new Object[0];
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
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Collection<?>) {
				return ((Collection<?>) inputElement).toArray();
			}
			return new Object[0];
		}

		@Override
		public void dispose() {
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	private Object[] selectedConnectors = new Object[0];

	private final ConnectorMigrator migrator;

	public ConnectorMigrationWizard(ConnectorMigrator migrator) {
		this.migrator = migrator;
		setNeedsProgressMonitor(true);
	}

	@Override
	public void addPages() {
		setWindowTitle(Messages.ConnectorMigrationWizard_Connector_Migration);
		addPage(new WizardPage(Messages.ConnectorMigrationWizard_End_of_Connector_Support) {

			@Override
			public void createControl(Composite parent) {
				setTitle(Messages.ConnectorMigrationWizard_End_of_Connector_Support);
				setMessage(Messages.ConnectorMigrationWizard_Message, IMessageProvider.INFORMATION);
				Composite c = new Composite(parent, SWT.NONE);
				GridLayoutFactory.fillDefaults().applyTo(c);
				Link text = new Link(c, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
				text.addSelectionListener(new SelectionAdapter() {
					@Override
					public void widgetSelected(SelectionEvent e) {
						BrowserUtil.openUrl(e.text, IWorkbenchBrowserSupport.AS_EXTERNAL);
					}
				});
				text.setText(NLS.bind(Messages.ConnectorMigrationWizard_Body, migrator.getExplanatoryText()));
				GridDataFactory.fillDefaults()
						.align(SWT.FILL, SWT.FILL)
						.grab(true, true)
						.hint(600, SWT.DEFAULT)
						.applyTo(text);
				setControl(c);
			}
		});
		addPage(new WizardPage(Messages.ConnectorMigrationWizard_Select_Connectors) {

			@Override
			public void createControl(Composite parent) {
				setTitle(Messages.ConnectorMigrationWizard_Select_Connectors);
				setDescription(Messages.ConnectorMigrationWizard_Select_the_connectors_to_migrate);
				Composite c = new Composite(parent, SWT.NONE);
				GridLayoutFactory.fillDefaults().applyTo(c);
				List<String> kinds = getRelevantConnectorKinds(migrator.getConnectorKinds().keySet());
				final CheckboxTreeViewer viewer = createConnectorList(c, kinds);
				selectedConnectors = kinds.toArray();
				viewer.setCheckedElements(selectedConnectors);
				viewer.addCheckStateListener(new ICheckStateListener() {

					@Override
					public void checkStateChanged(CheckStateChangedEvent event) {
						selectedConnectors = viewer.getCheckedElements();
						setPageComplete(selectedConnectors.length > 0);
					}
				});
				GridDataFactory.fillDefaults().grab(true, true).applyTo(viewer.getControl());
				setControl(c);
			}

			@Override
			public boolean isPageComplete() {
				return super.isPageComplete() && isCurrentPage();
			}

		});
	}

	protected CheckboxTreeViewer createConnectorList(Composite parent, List<String> kinds) {
		final CheckboxTreeViewer viewer = new CheckboxTreeViewer(parent);
		viewer.setContentProvider(new CollectionContentProvider());
		viewer.setInput(kinds);
		viewer.setLabelProvider(new LabelProvider() {
			@Override
			public String getText(Object element) {
				if (element instanceof String) {
					String kind = (String) element;
					IRepositoryManager manager = migrator.getRepositoryManager();
					AbstractRepositoryConnector connector = manager.getRepositoryConnector(kind);
					if (connector != null) {
						return connector.getLabel()
								+ NLS.bind(Messages.ConnectorMigrationWizard_used_by_X_repositories, manager.getRepositories(kind).size());
					}
				}
				return super.getText(element);
			}
		});
		return viewer;
	}

	private List<String> getRelevantConnectorKinds(Set<String> connectorKinds) {
		IRepositoryManager manager = migrator.getRepositoryManager();
		List<String> relevantConnectorKinds = new LinkedList<>();
		for (String connectorKind : connectorKinds) {
			if (!manager.getRepositories(connectorKind).isEmpty()) {
				relevantConnectorKinds.add(connectorKind);
			}
		}
		return relevantConnectorKinds;
	}

	@Override
	public boolean performFinish() {
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					ImmutableList<String> connectors = FluentIterable.from(ImmutableList.copyOf(selectedConnectors))
							.filter(String.class)
							.toList();
					try {
						migrator.setConnectorsToMigrate(connectors);
						migrator.migrateConnectors(monitor);
					} catch (IOException e) {
						throw new InvocationTargetException(e);
					}
				}
			});
		} catch (InvocationTargetException | InterruptedException e) {
			StatusHandler.log(new Status(IStatus.ERROR, TasksUiPlugin.ID_PLUGIN, e.getMessage(), e));
			IWizardPage page = getContainer().getCurrentPage();
			if (page instanceof WizardPage && e.getCause() != null) {
				((WizardPage) page).setErrorMessage(e.getCause().getMessage());
			}
			return false;
		}
		return true;
	}
}
