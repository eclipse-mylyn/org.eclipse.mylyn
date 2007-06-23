/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylyn.context.core.ContextCorePlugin;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * @author Mik Kersten
 */
public class InterestInducingProblemListener implements IProblemChangedListener, IPropertyChangeListener {

	public static final String PREDICTED_INTEREST_ERRORS = "org.eclipse.mylyn.java.ui.interest.prediction.errors";
	
	// TODO: consider getting rid of this
	private JavaStructureBridge javaStructureBridge = new JavaStructureBridge();

	public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
		try {
			if (!ContextCorePlugin.getContextManager().isContextActive()) {
				return;
			} else {
				for (int i = 0; i < changedResources.length; i++) {
					IResource resource = changedResources[i];
					if (resource instanceof IFile) {
						IJavaElement javaElement = (IJavaElement) resource.getAdapter(IJavaElement.class);
						if (javaElement != null) {
							IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
									javaElement.getHandleIdentifier());
							if (!javaStructureBridge.containsProblem(element)) {
								ContextCorePlugin.getContextManager().removeErrorPredictedInterest(
										element.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE, true);
							} else {
								ContextCorePlugin.getContextManager().addErrorPredictedInterest(
										element.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE, true);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.log(e, "could not update on marker change");
		}
	}

	public void propertyChange(PropertyChangeEvent event) {
		if (PREDICTED_INTEREST_ERRORS.equals(event.getProperty())) {
			if (JavaUiBridgePlugin.getDefault().getPreferenceStore().getBoolean(
					PREDICTED_INTEREST_ERRORS)) {
				enable();
			} else {
				disable();
			}
		}
	}

	public void enable() {
		JavaPlugin.getDefault().getProblemMarkerManager().addListener(this);
	}

	public void disable() {
		JavaPlugin.getDefault().getProblemMarkerManager().removeListener(this);
	}
}
