/*******************************************************************************
 * Copyright (c) 2010, 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Itema AS - Corrected lazy initialisation of fields
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildConnector;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.internal.core.BuildModel;
import org.eclipse.mylyn.builds.internal.core.BuildServer;
import org.eclipse.mylyn.builds.internal.core.IBuildLoader;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;
import org.eclipse.mylyn.builds.internal.core.operations.IOperationService;
import org.eclipse.mylyn.builds.internal.core.util.BuildModelManager;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.internal.builds.ui.console.BuildConsoleManager;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 * @author Torkild U. Resheim
 */
public class BuildsUiInternal {

	private static IBuildLoader buildLoader = new IBuildLoader() {

		private volatile IBuildModelRealm realm;

		@Override
		public IBuildModelRealm getRealm() {
			if (realm == null) {
				Display.getDefault().syncExec(() -> realm = new IBuildModelRealm() {

					Display display = Display.getDefault();

					@Override
					public void asyncExec(Runnable runnable) {
						checkDisplay();
						display.asyncExec(runnable);
					}

					@Override
					public void exec(Runnable runnable) {
						checkDisplay();
						if (Display.getCurrent() != null) {
							runnable.run();
						} else {
							display.asyncExec(runnable);
						}
					}

					@Override
					public void syncExec(Runnable runnable) {
						checkDisplay();
						if (Display.getCurrent() != null) {
							runnable.run();
						} else {
							display.syncExec(runnable);
						}
					}

					protected void checkDisplay() {
						if (display.isDisposed()) {
							throw new OperationCanceledException();
						}
					}
				});
			}
			return realm;
		}

		@Override
		public BuildServerBehaviour loadBehaviour(BuildServer server) throws CoreException {
			String connectorKind = server.getConnectorKind();
			if (connectorKind == null) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
						NLS.bind("Loading of connector for server ''{0}'' failed. No connector kind was specified.",
								server.getName())));
			}
			BuildConnector connector = BuildsUi.getConnector(connectorKind);
			if (connector == null) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
						NLS.bind("Loading of connector for server ''{0}'' failed. Connector kind ''{1}'' is not known.",
								server.getName(), connectorKind)));
			}
			BuildServerBehaviour behaviour;
			try {
				behaviour = connector.getBehaviour(server.getLocation());
			} catch (Exception | LinkageError | AssertionError e) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed.",
						server.getName(), connectorKind), e));

			}
			if (behaviour == null) {
				throw new CoreException(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
						"Unexpected error during loading of connector for server ''{0}'' with connector kind ''{1}'' failed, returned behaviour object is null.",
						server.getName(), connectorKind)));
			}
			return behaviour;
		}
	};

	private static volatile BuildConsoleManager consoleManager;

	/**
	 * Refresh every 15 minutes by default.
	 */
	public static final int DEFAULT_REFRESH_INTERVAL = 15 * 60 * 1000;

	public static final String ID_PREFERENCE_PAGE_BUILDS = "org.eclipse.mylyn.builds.preferences.BuildsPage"; //$NON-NLS-1$

	private static BuildModelManager manager;

	private static OperationServiceUi operationService;

	private static volatile OperationFactory operationFactory;

	public static final int MIN_REFRESH_INTERVAL = 1 * 60 * 1000;

	public static final String PREF_AUTO_REFRESH_ENABLED = "refresh.enabled"; //$NON-NLS-1$

	public static final String PREF_AUTO_REFRESH_INTERVAL = "refresh.interval"; //$NON-NLS-1$

	public static final String PREF_REFRESH_ON_FOCUS = "refresh.onfocus"; //$NON-NLS-1$

	public static final String PREF_SHOW_TEST_FAILURES_ONLY = "editor.testResults.failuresOnly"; //$NON-NLS-1$

	public static IBuildServer createServer(String connectorKind, RepositoryLocation location) {
		return getManager().createServer(connectorKind, location);
	}

	public static BuildConsoleManager getConsoleManager() {
		if (consoleManager == null) {
			consoleManager = new BuildConsoleManager();
		}
		return consoleManager;
	}

	protected static synchronized BuildModelManager getManager() {
		if (manager == null) {
			manager = new BuildModelManager(BuildsUiPlugin.getDefault().getBuildsFile().toFile(), buildLoader);
			manager.getModel().setLoader(buildLoader);
			manager.getModel().setScheduler(getOperationService().getScheduler());
		}
		return manager;
	}

	public static synchronized IOperationService getOperationService() {
		if (operationService == null) {
			operationService = new OperationServiceUi();
		}
		return operationService;
	}

	public static synchronized BuildModel getModel() {
		return getManager().getModel();
	}

	public static synchronized void save() throws IOException {
		if (manager != null) {
			manager.save();
		}
	}

	public static Set<String> toSetOfIds(Collection<IBuildPlan> plans) {
		Set<String> ids = new HashSet<>();
		for (IBuildPlan plan : plans) {
			if (plan.isSelected()) {
				ids.add(plan.getId());
			}
		}
		return ids;
	}

	public static OperationFactory getFactory() {
		if (operationFactory == null) {
			operationFactory = new OperationFactory(getOperationService());
		}
		return operationFactory;
	}

	public static List<IBuildElement> getElements(ExecutionEvent event) {
		String selector = event.getParameter("element"); //$NON-NLS-1$

		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			List<?> items = ((IStructuredSelection) selection).toList();
			List<IBuildElement> result = new ArrayList<>(items.size());
			for (Object item : items) {
				if (item instanceof IBuildElement element) {
					if ("lastBuild".equals(selector)) { //$NON-NLS-1$
						if (element instanceof IBuildPlan) {
							element = ((IBuildPlan) element).getLastBuild();
						} else {
							element = null;
						}
					}
					result.add(element);
				}
			}
			return result;
		}
		return Collections.emptyList();
	}

}