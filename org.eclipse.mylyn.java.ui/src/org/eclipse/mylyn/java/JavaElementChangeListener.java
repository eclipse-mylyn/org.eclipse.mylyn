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
import org.eclipse.jdt.core.IJavaElementDelta;

/**
 * @author Mik Kersten
 */
public class JavaElementChangeListener implements IElementChangedListener {

	public void elementChanged(ElementChangedEvent event) {
//		if (event.getType() != ElementChangedEvent.POST_CHANGE) return;
		IJavaElementDelta delta = event.getDelta();
		handleDelta(delta.getAffectedChildren());
		System.err.println("-----");
	}
	
	private void handleDelta(IJavaElementDelta[] delta) {
		for (int i = 0; i < delta.length; i++) {
			IJavaElementDelta child = delta[i];
			if (child.getKind() == IJavaElementDelta.REMOVED) {
				System.err.println("> removed: " + child.getElement().getElementName());
			} else if (child.getKind() == IJavaElementDelta.ADDED) {
				System.err.println("> added: " + child.getElement().getElementName());
			}
			handleDelta(child.getAffectedChildren());
		}
	}
	
}
