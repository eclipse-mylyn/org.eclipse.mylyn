/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.ISourceRange;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.ContextChangeEvent;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 */
public class LandmarkMarkerManager extends AbstractContextListener {

	private static final String ID_MARKER_LANDMARK = "org.eclipse.mylyn.context.ui.markers.landmark"; //$NON-NLS-1$

	private final Map<IInteractionElement, Long> markerMap = new HashMap<IInteractionElement, Long>();

	private final LandmarkUpdateJob updateJob = new LandmarkUpdateJob(
			Messages.LandmarkMarkerManager_Updating_Landmark_Markers);

	public LandmarkMarkerManager() {
		super();
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
		LinkedHashSet<LandmarkUpdateOperation> runnables = new LinkedHashSet<LandmarkUpdateOperation>();
		switch (event.getEventKind()) {
		case ACTIVATED:
		case DEACTIVATED:
			modelUpdated();
			break;
		case CLEARED:
			if (event.isActiveContext()) {
				modelUpdated();
			}
			break;
		case LANDMARKS_ADDED:
			for (IInteractionElement element : event.getElements()) {
				LandmarkUpdateOperation runnable = createAddLandmarkMarkerOperation(element);
				if (runnable != null) {
					runnables.add(runnable);
				}
			}
			break;
		case ELEMENTS_DELETED:
		case LANDMARKS_REMOVED:
			for (IInteractionElement element : event.getElements()) {
				LandmarkUpdateOperation runnable = createRemoveLandmarkMarkerOperation(element);
				if (runnable != null) {
					runnables.add(runnable);
				}
			}
			break;

		}
		updateJob.updateMarkers(runnables);
	}

	private void modelUpdated() {
		try {
			LinkedHashSet<LandmarkUpdateOperation> runnables = new LinkedHashSet<LandmarkUpdateOperation>();
			for (IInteractionElement node : markerMap.keySet()) {
				LandmarkUpdateOperation runnable = createRemoveLandmarkMarkerOperation(node);
				if (runnable != null) {
					runnables.add(runnable);
				}
			}
			markerMap.clear();
			for (IInteractionElement node : ContextCore.getContextManager().getActiveLandmarks()) {
				LandmarkUpdateOperation runnable = createAddLandmarkMarkerOperation(node);
				if (runnable != null) {
					runnables.add(runnable);
				}
			}
			updateJob.updateMarkers(runnables);
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not update landmark markers", t)); //$NON-NLS-1$
		}
	}

	private LandmarkUpdateOperation createAddLandmarkMarkerOperation(final IInteractionElement node) {
		if (node == null || node.getContentType() == null) {
			return null;
		}
		if (node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
			final IJavaElement element = JavaCore.create(node.getHandleIdentifier());
			if (!element.exists()) {
				return null;
			}
			if (element instanceof IMember) {
				try {
					final ISourceRange range = ((IMember) element).getNameRange();
					IResource resource = element.getUnderlyingResource();
					if (resource instanceof IFile) {
						LandmarkUpdateOperation runnable = new LandmarkUpdateOperation(resource) {
							public void run(IProgressMonitor monitor) throws CoreException {
								IMarker marker = getResource().createMarker(ID_MARKER_LANDMARK);
								if (marker != null && range != null) {
									marker.setAttribute(IMarker.CHAR_START, range.getOffset());
									marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
									marker.setAttribute(IMarker.MESSAGE, "Mylyn Landmark"); //$NON-NLS-1$
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									markerMap.put(node, marker.getId());
								}
							}
						};
						return runnable;
					}
				} catch (JavaModelException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
							"Could not update marker", e)); //$NON-NLS-1$
				}
			}
		}
		return null;
	}

	private LandmarkUpdateOperation createRemoveLandmarkMarkerOperation(final IInteractionElement node) {
		if (node == null) {
			return null;
		}
		if (node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
			IJavaElement element = JavaCore.create(node.getHandleIdentifier());
			if (!element.exists()) {
				return null;
			}
			if (element.getAncestor(IJavaElement.COMPILATION_UNIT) != null // stuff
					// from .class files
					&& element instanceof ISourceReference) {
				try {
					IResource resource = element.getUnderlyingResource();
					LandmarkUpdateOperation runnable = new LandmarkUpdateOperation(resource) {
						public void run(IProgressMonitor monitor) throws CoreException {
							if (getResource() != null) {
								try {
									if (markerMap.containsKey(node)) {
										long id = markerMap.get(node);
										IMarker marker = getResource().getMarker(id);
										if (marker != null) {
											marker.delete();
										}
									}
								} catch (NullPointerException e) {
									// FIXME avoid NPE
									StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
											"Could not update marker", e)); //$NON-NLS-1$
								}
							}
						}
					};
					return runnable;
				} catch (JavaModelException e) {
					// ignore the Java Model errors
				}
			}
		}
		return null;
	}

	/**
	 * IWorkspaceRunnable that has a reference to the resource that it is operating on
	 */
	private abstract class LandmarkUpdateOperation implements IWorkspaceRunnable {

		private final IResource resource;

		public LandmarkUpdateOperation(IResource resource) {
			Assert.isNotNull(resource);
			this.resource = resource;
		}

		public IResource getResource() {
			return resource;
		}

	}

	/**
	 * Job to handle updating the landmark markers in the background
	 */
	private class LandmarkUpdateJob extends Job {

		private static final int NOT_SCHEDULED = -1;

		private final LinkedHashSet<LandmarkUpdateOperation> queue = new LinkedHashSet<LandmarkUpdateOperation>();

		private long scheduleTime = NOT_SCHEDULED;

		public LandmarkUpdateJob(String name) {
			super(name);
			setSystem(true);
		}

		public synchronized void updateMarkers(LinkedHashSet<LandmarkUpdateOperation> operations) {
			queue.addAll(operations);
			if (queue.size() > 0) {
				if (scheduleTime == NOT_SCHEDULED) {
					scheduleTime = System.currentTimeMillis();
					schedule();
				}
			}
		}

		@Override
		public IStatus run(IProgressMonitor monitor) {
			IWorkspace workspace = ResourcesPlugin.getWorkspace();
			if (workspace == null) {
				return Status.CANCEL_STATUS;
			}
			LinkedHashSet<LandmarkUpdateOperation> operations = null;
			synchronized (this) {
				operations = new LinkedHashSet<LandmarkUpdateOperation>(queue);
				queue.clear();
				scheduleTime = NOT_SCHEDULED;
			}
			if (operations != null) {
				try {
					monitor.beginTask(Messages.LandmarkMarkerManager_Updating_Landmark_Markers, operations.size());
					for (LandmarkUpdateOperation runnable : operations) {
						try {
							workspace.run(runnable, runnable.getResource(), IWorkspace.AVOID_UPDATE, monitor);
						} catch (CoreException e) {
							StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
									"Could not update landmark marker", e)); //$NON-NLS-1$
						} catch (OperationCanceledException e) {
							return Status.CANCEL_STATUS;
						}
						monitor.worked(1);
					}
				} finally {
					monitor.done();
				}
			}
			return Status.OK_STATUS;
		}
	}
}
