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
package org.eclipse.mylar.core.resources;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylar.core.IMylarStructureBridge;
import org.eclipse.mylar.core.MylarPlugin;
import org.eclipse.ui.PlatformUI;


public class ResourceMarkerListener implements IResourceChangeListener {


	public void resourceChanged(IResourceChangeEvent event) {
		//we are only interested in POST_CHANGE events
		if (event.getType() != IResourceChangeEvent.POST_CHANGE)
			return;
		IResourceDelta rootDelta = event.getDelta();

		IResourceDeltaVisitor visitor = new IResourceDeltaVisitor() {
			public boolean visit(IResourceDelta delta) {
				IMarkerDelta[] markers = delta.getMarkerDeltas();
        	  
				for(IMarkerDelta markerDelta: markers){
					try{
						final IMarker marker = markerDelta.getMarker();
						if(marker == null || !marker.exists()){
							final IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(marker.getResource());
							if(bridge != null){
								if(PlatformUI.getWorkbench().getDisplay().isDisposed())
									return true;
								
								PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						            public void run() {
						            	MylarPlugin.getTaskscapeManager().removeErrorPredictedInterest(bridge.getHandleIdentifier(marker.getResource()), bridge.getResourceExtension(), true);
						            }});
							}
							return true;
						}
							
						if(markerDelta.getMarker().isSubtypeOf(IMarker.PROBLEM)){
							final IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(marker.getResource());
							if(bridge != null){
								if(PlatformUI.getWorkbench().getDisplay().isDisposed())
									return true;
								
								PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						            public void run() {
						            	MylarPlugin.getTaskscapeManager().addErrorPredictedInterest(bridge.getHandleIdentifier(marker.getResource()), bridge.getResourceExtension(), true);
						            }});
							}
						} else {//if(!markerDelta.getMarker().getType().equals("org.eclipse.jdt.core.problem")){
							final IMylarStructureBridge bridge = MylarPlugin.getDefault().getStructureBridge(marker.getResource());
							if(bridge != null){
								if(PlatformUI.getWorkbench().getDisplay().isDisposed())
									return true;
								
								PlatformUI.getWorkbench().getDisplay().syncExec(new Runnable() {
						            public void run() {
						            	MylarPlugin.getTaskscapeManager().removeErrorPredictedInterest(bridge.getHandleIdentifier(marker.getResource()), bridge.getResourceExtension(), true);
						            }});
							}
						}
					}catch (Exception e){
						MylarPlugin.log(this.getClass().toString() + " could not update marker", e);
					}
				}
				
				return true;
			}
		};
		try {
			rootDelta.accept(visitor);
		} catch (CoreException e) {
			MylarPlugin.log(this.getClass().toString(), e);
		}
		
	}
}