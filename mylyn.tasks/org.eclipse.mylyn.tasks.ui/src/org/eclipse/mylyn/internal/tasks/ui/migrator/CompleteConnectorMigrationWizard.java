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

import static com.google.common.collect.Iterables.any;
import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.MINUTES;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isQueryForConnector;
import static org.eclipse.mylyn.internal.tasks.ui.migrator.TaskPredicates.isSynchronizing;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerSorter;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.internal.tasks.ui.TasksUiPlugin;
import org.eclipse.mylyn.internal.tasks.ui.util.TasksUiInternal;
import org.eclipse.mylyn.internal.tasks.ui.views.TaskRepositoryLabelProvider;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.ui.AbstractRepositoryConnectorUi;
import org.eclipse.mylyn.tasks.ui.TaskElementLabelProvider;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;
import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;

public class CompleteConnectorMigrationWizard extends Wizard {
	final class MapContentProvider implements ITreeContentProvider {
		private final Map<?, ? extends Collection<?>> map;

		private MapContentProvider(Map<?, ? extends Collection<?>> map) {
			this.map = map;
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean hasChildren(Object element) {
			return map.containsKey(element);
		}

		@Override
		public Object getParent(Object element) {
			return null;
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof Map) {
				return ((Map<?, ?>) inputElement).keySet().toArray();
			}
			return null;
		}

		@Override
		public Object[] getChildren(Object parentElement) {
			if (map.get(parentElement) == null) {
				return null;
			}
			return map.get(parentElement).toArray();
		}
	}

	private final ConnectorMigrator migrator;

	public CompleteConnectorMigrationWizard(ConnectorMigrator migrator) {
		this.migrator = migrator;
	}

	@Override
	public void addPages() {
		setWindowTitle(Messages.CompleteConnectorMigrationWizard_Complete_Connector_Migration);
		if (!migrator.allQueriesMigrated()) {
			addPage(new WizardPage(Messages.CompleteConnectorMigrationWizard_Migrate_Queries) {

				@Override
				public void createControl(Composite parent) {
					setTitle(Messages.CompleteConnectorMigrationWizard_Have_You_Recreated_Your_Queries);
					String message = Messages.CompleteConnectorMigrationWizard_ensure_created_queries;
					if (migrator.anyQueriesMigrated()) {
						message = Messages.CompleteConnectorMigrationWizard_Queries_not_migrated;
					}
					setMessage(NLS.bind(Messages.CompleteConnectorMigrationWizard_first_page_message, message),
							IMessageProvider.INFORMATION);
					Composite c = new Composite(parent, SWT.NONE);
					GridLayoutFactory.fillDefaults().numColumns(2).equalWidth(true).applyTo(c);
					Label oldQueriesLabel = new Label(c, SWT.NONE);
					oldQueriesLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
					oldQueriesLabel.setText(Messages.CompleteConnectorMigrationWizard_Queries_Using_Old_Connectors);
					Label newQueriesLabel = new Label(c, SWT.NONE);
					newQueriesLabel.setFont(JFaceResources.getFontRegistry().getBold(JFaceResources.DEFAULT_FONT));
					newQueriesLabel.setText(Messages.CompleteConnectorMigrationWizard_Queries_Using_New_Connectors);
					createQueryTree(c, createRepositoryQueryMap(migrator.getSelectedConnectors().keySet()));
					createQueryTree(c, createRepositoryQueryMap(migrator.getSelectedConnectors().values()));
					setControl(c);
				}

			});
		}
		addPage(new WizardPage(Messages.CompleteConnectorMigrationWizard_Complete_Migration) {

			@Override
			public void createControl(Composite parent) {
				setTitle(Messages.CompleteConnectorMigrationWizard_Complete_Migration);
				setMessage(Messages.CompleteConnectorMigrationWizard_second_page_message, IMessageProvider.INFORMATION);
				Composite c = new Composite(parent, SWT.NONE);
				GridLayoutFactory.fillDefaults().applyTo(c);
				Text text = new Text(c, SWT.READ_ONLY | SWT.MULTI | SWT.WRAP);
				text.setText(Messages.CompleteConnectorMigrationWizard_second_page_text);
				GridDataFactory.fillDefaults()
						.align(SWT.FILL, SWT.FILL)
						.grab(true, true)
						.hint(600, SWT.DEFAULT)
						.applyTo(text);
				setControl(c);
			}

			@Override
			public boolean isPageComplete() {
				return super.isPageComplete() && isCurrentPage();
			}
		});
	}

	protected TreeViewer createQueryTree(Composite parent,
			final Map<TaskRepository, ? extends Set<RepositoryQuery>> queries) {
		final TreeViewer viewer = new TreeViewer(parent);
		GridDataFactory.fillDefaults().grab(false, true).hint(500, SWT.DEFAULT).applyTo(viewer.getControl());
		viewer.setContentProvider(new MapContentProvider(queries));
		viewer.setInput(queries);
		viewer.setSorter(new ViewerSorter());
		viewer.setLabelProvider(new TaskElementLabelProvider() {
			private final TaskRepositoryLabelProvider repositoryLabelProvider = new TaskRepositoryLabelProvider();

			@Override
			public Image getImage(Object element) {
				if (element instanceof TaskRepository) {
					return repositoryLabelProvider.getImage(element);
				}
				return super.getImage(element);
			}

			@Override
			public String getText(Object object) {
				if (object instanceof TaskRepository) {
					return repositoryLabelProvider.getText(object);
				}
				return super.getText(object);
			}
		});
		viewer.addDoubleClickListener(new IDoubleClickListener() {

			@Override
			public void doubleClick(DoubleClickEvent event) {
				if (viewer.getSelection() instanceof IStructuredSelection) {
					Object element = ((IStructuredSelection) viewer.getSelection()).getFirstElement();
					if (element instanceof IRepositoryQuery) {
						IRepositoryQuery query = (IRepositoryQuery) element;
						AbstractRepositoryConnectorUi connectorUi = TasksUiPlugin
								.getConnectorUi(query.getConnectorKind());
						TasksUiInternal.openEditQueryDialog(connectorUi, query);
					}
				}
			}
		});
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				viewer.expandAll();
			}
		});
		return viewer;
	}

	@Override
	public boolean performFinish() {
		Job job = new Job(Messages.CompleteConnectorMigrationWizard_Migrating_Tasks_and_Private_Data) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask(Messages.CompleteConnectorMigrationWizard_Completing_connector_migration,
						IProgressMonitor.UNKNOWN);
				Collection<String> newConnectors = migrator.getSelectedConnectors().values();
				waitForQueriesToSynchronize(newConnectors, monitor);
				migrator.migrateTasks(monitor);
				return Status.OK_STATUS;
			}

		};
		job.setUser(true);
		job.setSystem(false);
		job.schedule();
		return true;
	}

	protected void waitForQueriesToSynchronize(Collection<String> newConnectors, IProgressMonitor monitor) {
		monitor.subTask(Messages.CompleteConnectorMigrationWizard_Waiting_for_queries_to_synchronize);
		Iterable<RepositoryQuery> queries = Iterables.concat(createRepositoryQueryMap(newConnectors).values());
		long start = System.currentTimeMillis();
		while (any(queries, isSynchronizing())
				&& System.currentTimeMillis() - start < MILLISECONDS.convert(20, MINUTES)) {
			try {
				Thread.sleep(MILLISECONDS.convert(3, SECONDS));
			} catch (InterruptedException e) {// NOSONAR
			}
		}
	}

	protected Map<TaskRepository, Set<RepositoryQuery>> createRepositoryQueryMap(Collection<String> kinds) {
		Builder<TaskRepository, Set<RepositoryQuery>> repositories = ImmutableMap.builder();
		for (final String kind : kinds) {
			for (TaskRepository repository : migrator.getRepositoryManager().getRepositories(kind)) {
				Set<RepositoryQuery> queriesForUrl = TasksUiPlugin.getTaskList()
						.getRepositoryQueries(repository.getRepositoryUrl());
				Set<RepositoryQuery> queries = Sets.filter(queriesForUrl, isQueryForConnector(kind));
				if (!queries.isEmpty()) {
					repositories.put(repository, queries);
				}
			}
		}
		return repositories.build();
	}
}
