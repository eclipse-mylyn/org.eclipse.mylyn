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

import org.eclipse.jdt.core.ElementChangedEvent;
import org.eclipse.jdt.core.IElementChangedListener;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaElementDelta;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class InterestUpdateDeltaListener implements IElementChangedListener {

	public void elementChanged(ElementChangedEvent event) {
		IJavaElementDelta delta = event.getDelta();
		handleDelta(delta.getAffectedChildren());
	}
	
	/**
	 * Only handles first addition/removal
	 */
	private void handleDelta(IJavaElementDelta[] delta) {
		IJavaElement added = null;
		IJavaElement removed = null;
		for (int i = 0; i < delta.length; i++) {
			IJavaElementDelta child = delta[i];
			if (child.getKind() == IJavaElementDelta.ADDED) {
				if (added == null) added = child.getElement();
			} else if (child.getKind() == IJavaElementDelta.REMOVED) {
				if (removed == null) removed = child.getElement();
			} 			
			handleDelta(child.getAffectedChildren());
		}
		
		if (added != null && removed != null) { 
//			System.err.println("> : " + removed.getElementName() + " to " + added.getElementName()); 			
			IMylarElement element = MylarPlugin.getContextManager().getElement(removed.getHandleIdentifier());
			if (element != null) {
				MylarPlugin.getContextManager().updateElementHandle(element, added.getHandleIdentifier());
			}
		} else if (removed != null) {
			IMylarElement element = MylarPlugin.getContextManager().getElement(removed.getHandleIdentifier());
			MylarPlugin.getContextManager().getActiveContext().remove(element);
		} 
	}
}
