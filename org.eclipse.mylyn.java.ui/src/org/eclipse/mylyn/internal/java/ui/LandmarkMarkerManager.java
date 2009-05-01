/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
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
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

	public LandmarkMarkerManager() {
		super();
	}

	@Override
	public void contextChanged(ContextChangeEvent event) {
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
				addLandmarkMarker(element);
			}
			break;
		case LANDMARKS_REMOVED:
			for (IInteractionElement element : event.getElements()) {
				removeLandmarkMarker(element);
			}
			break;

		}
	}

	private void modelUpdated() {
		try {
			for (IInteractionElement node : markerMap.keySet()) {
				removeLandmarkMarker(node);
			}
			markerMap.clear();
			for (IInteractionElement node : ContextCore.getContextManager().getActiveLandmarks()) {
				addLandmarkMarker(node);
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not update landmark markers", t)); //$NON-NLS-1$
		}
	}

	public void addLandmarkMarker(final IInteractionElement node) {
		if (node == null || node.getContentType() == null) {
			return;
		}
		if (node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
			final IJavaElement element = JavaCore.create(node.getHandleIdentifier());
			if (!element.exists()) {
				return;
			}
			if (element instanceof IMember) {
				try {
					final ISourceRange range = ((IMember) element).getNameRange();
					final IResource resource = element.getUnderlyingResource();
					if (resource instanceof IFile) {
						IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
							public void run(IProgressMonitor monitor) throws CoreException {
								IMarker marker = resource.createMarker(ID_MARKER_LANDMARK);
								if (marker != null && range != null) {
									marker.setAttribute(IMarker.CHAR_START, range.getOffset());
									marker.setAttribute(IMarker.CHAR_END, range.getOffset() + range.getLength());
									marker.setAttribute(IMarker.MESSAGE, "Mylyn Landmark"); //$NON-NLS-1$
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									markerMap.put(node, marker.getId());
								}
							}
						};
						resource.getWorkspace().run(runnable, null);
					}
				} catch (JavaModelException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
							"Could not update marker", e)); //$NON-NLS-1$
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
							"Could not update marker", e)); //$NON-NLS-1$
				}
			}
		}
	}

	public void removeLandmarkMarker(final IInteractionElement node) {
		if (node == null) {
			return;
		}
		if (node.getContentType().equals(JavaStructureBridge.CONTENT_TYPE)) {
			IJavaElement element = JavaCore.create(node.getHandleIdentifier());
			if (!element.exists()) {
				return;
			}
			if (element.getAncestor(IJavaElement.COMPILATION_UNIT) != null // stuff
					// from .class files
					&& element instanceof ISourceReference) {
				try {
					final IResource resource = element.getUnderlyingResource();
					IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
						public void run(IProgressMonitor monitor) throws CoreException {
							if (resource != null) {
								try {
									if (markerMap.containsKey(node)) {
										long id = markerMap.get(node);
										IMarker marker = resource.getMarker(id);
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
					resource.getWorkspace().run(runnable, null);
				} catch (JavaModelException e) {
					// ignore the Java Model errors
				} catch (CoreException e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN,
							"Could not update landmark marker", e)); //$NON-NLS-1$
				}
			}
		}
	}

}
