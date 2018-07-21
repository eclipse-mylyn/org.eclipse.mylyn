/*******************************************************************************
 * Copyright (c) 2015 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.IBuildServer;
import org.eclipse.mylyn.builds.core.spi.BuildServerBehaviour;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.ui.BuildsUi;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.browser.BrowserUtil;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowBuildOutputAction;
import org.eclipse.mylyn.internal.builds.ui.actions.ShowTestResultsAction;
import org.eclipse.mylyn.internal.builds.ui.view.NewBuildServerAction;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

public class BuildUrlCommandHandler extends AbstractHandler {
	public static class ShowTestResultsUrlHandler extends BuildUrlCommandHandler {
		public ShowTestResultsUrlHandler() {
			super(new ShowTestResultsAction());
		}
	}

	public static class ShowBuildOutputUrlHandler extends BuildUrlCommandHandler {
		public ShowBuildOutputUrlHandler() {
			super(new ShowBuildOutputAction());
		}
	}

	public static class OpenWithBrowserUrlHandler extends BuildUrlCommandHandler {
		public OpenWithBrowserUrlHandler() {
			super(new BaseSelectionListenerAction(Messages.BuildUrlCommandHandler_Open_with_Browser) {
				@Override
				public void run() {
					Object selection = getStructuredSelection().getFirstElement();
					if (selection instanceof String) {
						BrowserUtil.openUrl((String) selection, BrowserUtil.NO_RICH_EDITOR);
					}
				}
			});
			setNeedsDownload(false);
		}
	}

	private static class BuildCache {
		private WeakReference<IBuild> lastBuild = new WeakReference<IBuild>(null);

		private BuildUrlCommandHandler lastHandler;

		public void put(BuildUrlCommandHandler handler, IBuild build) {
			lastBuild = new WeakReference<IBuild>(build);
			lastHandler = handler;
		}

		public IBuild get(BuildUrlCommandHandler handler, String buildUrl) {
			if (lastHandler != handler) {
				// same handler should never get the build back from the cache; clicking same button twice re-downloads
				IBuild build = lastBuild.get();
				if (build != null && buildUrl.equals(build.getUrl())) {
					lastBuild.clear();// can only retrieve build once
					return build;
				}
			}
			return null;
		}
	}

	private static BuildCache buildCache = new BuildCache();

	private final BaseSelectionListenerAction action;

	private boolean needsDownload = true;

	public BuildUrlCommandHandler(BaseSelectionListenerAction action) {
		this.action = action;
	}

	protected void setNeedsDownload(boolean needsDownload) {
		this.needsDownload = needsDownload;
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (event.getTrigger() instanceof Event) {
			Object data = ((Event) event.getTrigger()).widget.getData();
			if (data instanceof String) {
				String buildUrl = (String) data;
				if (needsDownload) {
					final IBuildServer buildServer = findServerForBuild(buildUrl);
					if (buildServer != null) {
						downloadBuildAndRunAction(buildServer, buildUrl);
					} else {
						new NewBuildServerAction().run();
					}
				} else {
					action.selectionChanged(new StructuredSelection(buildUrl));
					action.run();
				}
			}
		}
		return null;
	}

	private IBuildServer findServerForBuild(String buildUrl) {
		for (IBuildServer server : BuildsUiInternal.getModel().getServers()) {
			if (buildUrl.startsWith(server.getUrl())) {
				return server;
			}
		}
		return null;
	}

	protected void downloadBuildAndRunAction(final IBuildServer buildServer, final String buildUrl) {
		IBuild build = buildCache.get(this, buildUrl);
		if (build != null) {
			action.selectionChanged(new StructuredSelection(build));
			action.run();
			return;
		}
		Job job = new Job(NLS.bind(Messages.BuildUrlCommandHandler_Downloading_Build_X, buildUrl)) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					BuildServerBehaviour behaviour = BuildsUi.getConnector(buildServer.getConnectorKind())
							.getBehaviour(buildServer.getLocation());
					String buildUrl2 = removeEnd(buildUrl, "/"); //$NON-NLS-1$
					String buildId = substringAfterLast(buildUrl2, "/"); //$NON-NLS-1$
					String planId = substringAfterLast(substringBeforeLast(buildUrl2, "/"), "/"); //$NON-NLS-1$ //$NON-NLS-2$
					IBuildPlan plan = BuildFactory.eINSTANCE.createBuildPlan();
					plan.setId(planId);
					plan.setName(planId);
					GetBuildsRequest request = new GetBuildsRequest(plan, Collections.singletonList(buildId),
							Scope.FULL);
					@SuppressWarnings("restriction")
					final List<IBuild> builds = behaviour.getBuilds(request,
							new org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor());
					if (!builds.isEmpty()) {
						IBuild populatedBuild = builds.get(0);
						populatedBuild.setServer(buildServer);
						populatedBuild.setPlan(plan);
						buildCache.put(BuildUrlCommandHandler.this, populatedBuild);
						action.selectionChanged(new StructuredSelection(populatedBuild));
						PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
							public void run() {
								action.run();
							}
						});
					}
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, e.getMessage(), e));
				}
				return Status.OK_STATUS;
			}
		};
		job.setUser(true);
		job.schedule();
	}

	protected String substringBeforeLast(String s, String last) {
		int i = s.lastIndexOf(last);
		if (i != -1) {
			return s.substring(0, i);
		}
		return s;
	}

	private String substringAfterLast(String s, String last) {
		int i = s.lastIndexOf(last);
		if (i != -1) {
			return s.substring(i + last.length());
		}
		return s;
	}

	private String removeEnd(final String s, String end) {
		if (s.endsWith(end)) {
			return s.substring(0, s.length() - end.length());
		}
		return s;
	}
}
