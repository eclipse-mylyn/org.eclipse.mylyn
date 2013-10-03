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

package org.eclipse.mylyn.internal.builds.ui.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.builds.core.IBuildPlan;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest;
import org.eclipse.mylyn.builds.core.spi.GetBuildsRequest.Scope;
import org.eclipse.mylyn.builds.internal.core.operations.GetBuildsOperation;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeEvent;
import org.eclipse.mylyn.builds.internal.core.operations.OperationChangeListener;
import org.eclipse.mylyn.builds.ui.BuildsUiConstants;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.commons.workbench.EditorHandle;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiInternal;
import org.eclipse.mylyn.internal.builds.ui.BuildsUiPlugin;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditor;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditorInput;
import org.eclipse.mylyn.internal.builds.ui.editor.BuildEditorInput.BuildInfo;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Steffen Pingel
 */
public class OpenHandler extends AbstractHandler {

	public static EditorHandle fetchAndOpen(final IWorkbenchPage page, final IBuild build) {
		final EditorHandle handle = new EditorHandle();

		openEditor(page, handle, build, true);
		final IBuildPlan plan = build.getPlan();
		final String label = build.getLabel();
		GetBuildsRequest request = new GetBuildsRequest(build.getPlan(), Collections.singletonList(label), Scope.FULL);
		GetBuildsOperation operation = BuildsUiInternal.getFactory().getGetBuildsOperation(request);
		operation.addOperationChangeListener(new OperationChangeListener() {
			@Override
			public void done(OperationChangeEvent event) {
				if (event.getStatus().isOK() && !Display.getDefault().isDisposed()) {
					final GetBuildsOperation operation = (GetBuildsOperation) event.getOperation();
					List<IBuild> builds = operation.getBuilds();
					if (builds != null && builds.size() > 0) {
						final IBuild updatedBuild = builds.get(0);
						updatedBuild.setPlan(plan);
						updatedBuild.setServer(plan.getServer());
						updateBuildInfo(updatedBuild, BuildInfo.COMPLETE);
					} else {
						IStatus status = new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, NLS.bind(
								"The requested build ''{0}'' was not found", label)); //$NON-NLS-1$
						handle.setStatus(status);
						updateBuildInfo(build, BuildInfo.ERROR);
					}
				} else {
					handle.setStatus(event.getStatus());
					updateBuildInfo(build, BuildInfo.ERROR);
				}
			}

			protected void updateBuildInfo(final IBuild updatedBuild, final BuildInfo buildInfo) {
				PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (handle.getPart() instanceof BuildEditor) {
							BuildEditor buildEditor = (BuildEditor) handle.getPart();
							if (!buildEditor.isDisposed() && !page.getWorkbenchWindow().getShell().isDisposed()) {
								buildEditor.getEditorInput().updateBuildInfo(updatedBuild, buildInfo);
								buildEditor.refresh();
							}
						}
					}
				});
			}
		});
		operation.execute();
		return handle;
	}

	protected static void openEditor(final IWorkbenchPage page, final EditorHandle handle, final IBuild build,
			final boolean partial) {
		BuildEditorInput input = new BuildEditorInput(build, partial);
		try {
			IEditorPart part = page.openEditor(input, BuildsUiConstants.ID_EDITOR_BUILDS);
			handle.setPart(part);
			handle.setStatus(Status.OK_STATUS);
		} catch (PartInitException e) {
			IStatus status = new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
					"Unexpected error while opening build", e); //$NON-NLS-1$
			StatusHandler.log(status);
			handle.setStatus(status);
		}
	}

	public static List<EditorHandle> open(IWorkbenchPage page, List<? extends IBuildElement> elements) {
		List<EditorHandle> handles = new ArrayList<EditorHandle>();
		for (IBuildElement element : elements) {
			// open last build in case of build plans
			IBuildElement openElement = null;
			if (element instanceof IBuildPlan) {
				IBuildPlan plan = (IBuildPlan) element;
				if (plan.getLastBuild() != null) {
					openElement = plan.getLastBuild();
				}
			}
			if (openElement == null) {
				openElement = element;
			}

			if (openElement instanceof IBuild && isPartial((IBuild) openElement)) {
				EditorHandle handle = fetchAndOpen(page, (IBuild) openElement);
				handles.add(handle);
			} else {
				EditorHandle handle = openInEditor(page, openElement);
				handles.add(handle);
			}
		}
		return handles;
	}

	public static EditorHandle openInEditor(IWorkbenchPage page, IBuildElement item) {
		BuildEditorInput input = null;
		if (item instanceof IBuild) {
			input = new BuildEditorInput((IBuild) item);
		}

		if (input != null) {
			try {
				IEditorPart part = page.openEditor(input, BuildsUiConstants.ID_EDITOR_BUILDS);
				EditorHandle handle = new EditorHandle(Status.OK_STATUS);
				handle.setPart(part);
				return handle;
			} catch (PartInitException e) {
				Status status = new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN,
						"Unexpected error while opening build", e);
				StatusHandler.log(status);
				EditorHandle handle = new EditorHandle(status);
				return handle;
			}
		}

		Status status = new Status(IStatus.ERROR, BuildsUiPlugin.ID_PLUGIN, "No editor available to open " + item);
		return new EditorHandle(status);
	}

	private static boolean isPartial(IBuild element) {
		return element.getName() == null;
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
		IWorkbenchPage page = window.getActivePage();
		if (page == null) {
			throw new ExecutionException("No active workbench window"); //$NON-NLS-1$
		}

		List<IBuildElement> elements = BuildsUiInternal.getElements(event);
		open(page, elements);

		return null;
	}

}
