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

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 */
public class InterestUpdateDeltaListener implements IElementChangedListener {

	private static boolean asyncExecMode = true;

	public void elementChanged(ElementChangedEvent event) {
		IJavaElementDelta delta = event.getDelta();
		handleDelta(delta.getAffectedChildren());
	}

	/**
	 * Only handles first addition/removal
	 */
	private void handleDelta(IJavaElementDelta[] delta) {
		try {
			IJavaElement added = null;
			IJavaElement removed = null;
			for (IJavaElementDelta child : delta) {
				if (child.getElement() instanceof ICompilationUnit) {
					if (((ICompilationUnit) child.getElement()).getOwner() != null) {
						// see bug 195361, do not reduce interest of temporary working copy
						return;
					}
				}

				if (child.getKind() == IJavaElementDelta.ADDED) {
					if (added == null) {
						added = child.getElement();
					}
				} else if (child.getKind() == IJavaElementDelta.REMOVED) {
					if (removed == null) {
						removed = child.getElement();
					}
				}
				handleDelta(child.getAffectedChildren());
			}

			if (added != null && removed != null) {
				IInteractionElement element = ContextCore.getContextManager().getElement(removed.getHandleIdentifier());
				if (element != null) {
					resetHandle(element, added.getHandleIdentifier());
				}
			} else if (removed != null) {

				IInteractionElement element = ContextCore.getContextManager().getElement(removed.getHandleIdentifier());
				if (element != null) {
					delete(element);
				}
			}
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Delta update failed", t)); //$NON-NLS-1$
		}
	}

	private void resetHandle(final IInteractionElement element, final String newHandle) {
		if (!asyncExecMode) {
			ContextCore.getContextManager().updateHandle(element, newHandle);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay().asyncExec(new Runnable() {
					public void run() {
						ContextCore.getContextManager().updateHandle(element, newHandle);
					}
				});
			}
		}
	}

	private void delete(final IInteractionElement element) {
		if (!asyncExecMode) {
			ContextCore.getContextManager().deleteElement(element);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay().asyncExec(new Runnable() {
					public void run() {
						ContextCore.getContextManager().deleteElement(element);
					}
				});
			}
		}
	}

	/**
	 * For testing
	 */
	public static void setAsyncExecMode(boolean asyncExecMode) {
		InterestUpdateDeltaListener.asyncExecMode = asyncExecMode;
	}
}
