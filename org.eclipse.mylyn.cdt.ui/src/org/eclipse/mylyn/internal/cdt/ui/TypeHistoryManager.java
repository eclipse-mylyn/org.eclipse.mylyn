/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.cdt.mylyn.internal.ui;

import java.util.List;

import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IStructureDeclaration;
import org.eclipse.cdt.core.model.ITypeDef;
import org.eclipse.mylyn.context.core.AbstractContextListener;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionElement;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 * @author Jeff Johnston
 */
public class TypeHistoryManager extends AbstractContextListener {

//	private TypeInfoFactory factory = new TypeInfoFactory();

	public void contextActivated(IInteractionContext context) {
		clearTypeHistory();
		for (IInteractionElement node : context.getInteresting())
			updateTypeHistory(node, true);
	}

	/**
	 * Path has to be compatible with ITypeNameRequestor
	 */
	private void updateTypeHistory(IInteractionElement node, boolean add) {
		ICElement element = CDTStructureBridge.getElementForHandle(node.getHandleIdentifier());
		//FIXME: are there other types we care about...e.g. enumeration?
		if (element instanceof IStructureDeclaration || element instanceof ITypeDef) {
			String typename = element.getElementName();
//			try {
//				// FIXME: What do we do here?  Is this necessary?
//				if (element.exists()) {
//					JavaSearchTypeNameMatch typeNameMatch = new JavaSearchTypeNameMatch(type, type.getFlags());
//
//					if (add && !OpenTypeHistory.getInstance().contains(typeNameMatch)) {
//						OpenTypeHistory.getInstance().accessed(typeNameMatch);
//					} else {
//						OpenTypeHistory.getInstance().remove(typeNameMatch);
//					}
//				}
//			} catch (CModelException e) {
//				StatusHandler.log(e, "failed to update history for a type");
//			}
		}
	}

	public void contextDeactivated(IInteractionContext context) {
		clearTypeHistory();
	}

	public void contextCleared(IInteractionContext context) {
		clearTypeHistory();
	}

	/**
	 * Public for testing
	 */
	public void clearTypeHistory() {
		//FIXME: Is this necessary?
//		TypeNameMatch[] typeInfos = OpenTypeHistory.getInstance().getTypeInfos();
//		for (int i = 0; i < typeInfos.length; i++) {
//			OpenTypeHistory.getInstance().remove(typeInfos[i]);
//		}
	}

	public void interestChanged(List<IInteractionElement> nodes) {
		for (IInteractionElement node : nodes) {
			updateTypeHistory(node, true);
		}
	}

	public void elementDeleted(IInteractionElement node) {
		updateTypeHistory(node, false);
	}
	
	public void elementsDeleted(List<IInteractionElement> elements) {
		for(IInteractionElement element: elements){
			updateTypeHistory(element, false);
		}
	}

	public void landmarkAdded(IInteractionElement node) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}

	public void contextPreActivated(IInteractionContext context) {
		// ignore
		
	}
}
