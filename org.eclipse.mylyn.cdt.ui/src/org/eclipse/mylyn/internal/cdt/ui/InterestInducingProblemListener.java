/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.cdt.ui;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.internal.ui.util.IProblemChangedListener;
import org.eclipse.cdt.ui.CUIPlugin;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.InteractionContextManager;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
//XXX REMOVE THIS CLASS?
public class InterestInducingProblemListener implements IProblemChangedListener, IPropertyChangeListener {

	public static final String PREDICTED_INTEREST_ERRORS = "org.eclipse.cdt.mylyn.ui.interest.prediction.errors"; //$NON-NLS-1$

	private final CDTStructureBridge cdtStructureBridge = new CDTStructureBridge();

	@SuppressWarnings("restriction")
	public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
		try {
			if (!ContextCore.getContextManager().isContextActive()) {
				return;
			} else {
				for (IResource resource : changedResources) {
					if (resource instanceof IFile) {
						ICElement cdtElement = (ICElement) resource.getAdapter(ICElement.class);
						if (cdtElement != null) {
							IInteractionElement element = ContextCore.getContextManager().getElement(
									CDTStructureBridge.getHandleForElement(cdtElement));
							if (!cdtStructureBridge.containsProblem(element)) {
								((InteractionContextManager) ContextCore.getContextManager()).removeErrorPredictedInterest(
										element.getHandleIdentifier(), CDTStructureBridge.CONTENT_TYPE, true);
							} else {
								((InteractionContextManager) ContextCore.getContextManager()).addErrorPredictedInterest(
										element.getHandleIdentifier(), CDTStructureBridge.CONTENT_TYPE, true);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(new Status(IStatus.ERROR, CDTUIBridgePlugin.ID_PLUGIN,
					"could not update on marker change", e)); //$NON-NLS-1$
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (PREDICTED_INTEREST_ERRORS.equals(event.getProperty())) {
			if (CDTUIBridgePlugin.getDefault().getPreferenceStore().getBoolean(PREDICTED_INTEREST_ERRORS)) {
				enable();
			} else {
				disable();
			}
		}
	}

	public void enable() {
		CUIPlugin.getDefault().getProblemMarkerManager().addListener(this);
	}

	public void disable() {
		CUIPlugin.getDefault().getProblemMarkerManager().removeListener(this);
	}
}
