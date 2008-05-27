/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import org.eclipse.cdt.core.model.ElementChangedEvent;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.ICElementDelta;
import org.eclipse.cdt.core.model.IElementChangedListener;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class InterestUpdateDeltaListener implements IElementChangedListener {

	private static boolean asyncExecMode = true;

	public void elementChanged(ElementChangedEvent event) {
		ICElementDelta delta = event.getDelta();
		handleDelta(delta.getAffectedChildren());
	}

	/**
	 * Only handles first addition/removal
	 */
	private void handleDelta(ICElementDelta[] delta) {
		try {
			ICElement added = null;
			ICElement removed = null;
			for (int i = 0; i < delta.length; i++) {
				ICElementDelta child = delta[i];
				if (child.getElement() instanceof ITranslationUnit) {
					// FIXME: not sure I modified this correctly from the java version
					if (((ITranslationUnit)child.getElement()).getParent() != null) {
						// see bug 195361, do not reduce interest of temporary working copy
						return;
					} 
				}
				
				if (child.getKind() == ICElementDelta.ADDED) {
					if (added == null) {
						added = child.getElement();	
					}
				} else if (child.getKind() == ICElementDelta.REMOVED) {
					if (removed == null) {
						removed = child.getElement();
					}
				}
				handleDelta(child.getAffectedChildren());
			}

			if (added != null && removed != null) {
				IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
						CDTStructureBridge.getHandleForElement(removed));
				if (element != null) {
					resetHandle(element, CDTStructureBridge.getHandleForElement(added));
				}
			} else if (removed != null) {
				
				IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
						CDTStructureBridge.getHandleForElement(removed));
				if (element != null) {
					delete(element);
				}
			}
		} catch (Throwable t) {
			StatusHandler.fail(new Status(IStatus.ERROR, CDTUIBridgePlugin.PLUGIN_ID,
					CDTUIBridgePlugin.getResourceString("MylynCDT.deltaUpdateFailure"), t)); // $NON-NLS-1$
		}
	}

	private void resetHandle(final IInteractionElement element, final String newHandle) {
		if (!asyncExecMode) {
			ContextCorePlugin.getContextManager().updateHandle(element, newHandle);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay().asyncExec(new Runnable() {
					public void run() {
						ContextCorePlugin.getContextManager().updateHandle(element, newHandle);
					}
				});
			}
		}
	}

	private void delete(final IInteractionElement element) {
		if (!asyncExecMode) {
			ContextCorePlugin.getContextManager().deleteElement(element);
		} else {
			IWorkbench workbench = PlatformUI.getWorkbench();
			if (workbench != null) {
				workbench.getDisplay().asyncExec(new Runnable() {
					public void run() {
						ContextCorePlugin.getContextManager().deleteElement(element);
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
