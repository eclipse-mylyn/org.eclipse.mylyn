/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
/*
 * Created on Feb 16, 2005
 */
package org.eclipse.cdt.mylyn.internal.ui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.core.model.ISourceRange;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRunnable;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class LandmarkMarkerManager extends AbstractContextListener {

	private static final String ID_MARKER_LANDMARK = "org.eclipse.mylyn.context.ui.markers.landmark"; // $NON-NLS-1$

	private Map<IInteractionElement, Long> markerMap = new HashMap<IInteractionElement, Long>();

	public LandmarkMarkerManager() {
		super();
	}

	public void contextActivated(IInteractionContext taskscape) {
		modelUpdated();
	}

	public void contextDeactivated(IInteractionContext taskscape) {
		modelUpdated();
	}

	public void contextCleared(IInteractionContext context) {
		modelUpdated();
	}

	private void modelUpdated() {
		try {
			for (IInteractionElement node : markerMap.keySet()) {
				landmarkRemoved(node);
			}
			markerMap.clear();
			for (IInteractionElement node : ContextCorePlugin.getContextManager().getActiveLandmarks()) {
				landmarkAdded(node);
			}
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
					CDTUIBridgePlugin.getResourceString("MylynCDT.landmarkUpdateFailure"), t)); // $NON-NLS-1$
		}
	}

	public void interestChanged(List<IInteractionElement> nodes) {
		// don't care when the interest changes
	}

	public void landmarkAdded(final IInteractionElement node) {
		if (node == null || node.getContentType() == null)
			return;
		if (node.getContentType().equals(CDTStructureBridge.CONTENT_TYPE)) {
			final ICElement element = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
			if (element == null || !element.exists())
				return;
			if (element instanceof IMethod || element instanceof IFunction) {
				try {
					final ISourceRange range = ((ISourceReference) element).getSourceRange();
					final IResource resource = element.getUnderlyingResource();
					if (resource instanceof IFile) {
						IWorkspaceRunnable runnable = new IWorkspaceRunnable() {
							public void run(IProgressMonitor monitor) throws CoreException {
								IMarker marker = resource.createMarker(ID_MARKER_LANDMARK);
								if (marker != null && range != null) {
									marker.setAttribute(IMarker.CHAR_START, range.getStartPos());
									marker.setAttribute(IMarker.CHAR_END, range.getStartPos() + range.getLength());
									marker.setAttribute(IMarker.MESSAGE, CDTUIBridgePlugin.getResourceString("MylynCDT.landmark")); // $NON-NLS-1$
									marker.setAttribute(IMarker.SEVERITY, IMarker.SEVERITY_INFO);
									markerMap.put(node, marker.getId());
								}
							}
						};
						resource.getWorkspace().run(runnable, null);
					}
				} catch (CModelException e) {
					StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
							CDTUIBridgePlugin.getResourceString("MylynCDT.markerUpdateFailure"), e)); // $NON-NLS-1$
				} catch (CoreException e) {
					StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
							CDTUIBridgePlugin.getResourceString("MylynCDT.markerUpdateFailure"), e)); // $NON-NLS-1$
				}
			}
		}
	}

	public void landmarkRemoved(final IInteractionElement node) {
		if (node == null)
			return;
		if (node.getContentType().equals(CDTStructureBridge.CONTENT_TYPE)) {
			ICElement element = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
			if (element == null || !element.exists())
				return;
			if (element.getAncestor(ICElement.C_UNIT) != null // stuff
					// from
					// .class
					// files
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
										if (marker != null)
											marker.delete();
									}
								} catch (NullPointerException e) {
									StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
											CDTUIBridgePlugin.getResourceString("MylynCDT.log.markerUpdateFailure"), e)); // $NON-NLS-1$
								}
							}
						}
					};
					resource.getWorkspace().run(runnable, null);
				} catch (CModelException e) {
					// ignore the Java Model errors
				} catch (CoreException e) {
					StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
							CDTUIBridgePlugin.getResourceString("MylynCDT.landmarkUpdateFailure"), e)); // $NON-NLS-1$
				}
			}
		}
	}

	public void relationsChanged(IInteractionElement node) {
		// don't care when the relationships changed
	}

	public void elementDeleted(IInteractionElement node) {
		// don't care when a node is deleted
	}
}
