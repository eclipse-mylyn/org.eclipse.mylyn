/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.java;

import org.eclipse.core.internal.resources.ResourceException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaModelMarker;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jdt.internal.ui.viewsupport.IProblemChangedListener;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class JavaProblemListener implements IProblemChangedListener, IPropertyChangeListener {
  
	public void problemsChanged(IResource[] changedResources, boolean isMarkerChange) {
        try {
        	if (!MylarPlugin.getContextManager().hasActiveContext()) return;
            for (int i = 0; i < changedResources.length; i++) {
                IResource resource = changedResources[i];
                try {
                    IMarker[] markers = resource.findMarkers(
                            IJavaModelMarker.JAVA_MODEL_PROBLEM_MARKER,
                            true, IResource.DEPTH_INFINITE);
                    IJavaElement element = (IJavaElement)resource.getAdapter(IJavaElement.class);
                    boolean hasError = false; 
                    for (int j = 0; j < markers.length; j++) {
                        if (markers[j] != null
                        	&& markers[j].getAttribute(IMarker.SEVERITY) != null
                        	&& markers[j].getAttribute(IMarker.SEVERITY).equals(IMarker.SEVERITY_ERROR)) {
                            hasError = true;
                        } 
                    }
                    if (element != null && resource instanceof IFile && !resource.getFileExtension().equals("class")) {
                        if (!hasError) {
                            MylarPlugin.getContextManager().removeErrorPredictedInterest(element.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE, true);
                        } else {
                            MylarPlugin.getContextManager().addErrorPredictedInterest(element.getHandleIdentifier(), JavaStructureBridge.CONTENT_TYPE, true);
                        }
                    }
                } catch (ResourceException e) {
                    // ignore missing resources
                }
            }
        } catch (Exception e) {
        	MylarPlugin.log(e, "could not update on marker change");
        }
    }

	public void propertyChange(PropertyChangeEvent event) {
		if (MylarJavaPlugin.PREDICTED_INTEREST_ERRORS.equals(event.getProperty())) {
			if (MylarJavaPlugin.getDefault().getPreferenceStore().getBoolean(MylarJavaPlugin.PREDICTED_INTEREST_ERRORS)) {
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