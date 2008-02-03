/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.internal.core.search.JavaSearchTypeNameMatch;
import org.eclipse.jdt.internal.corext.util.OpenTypeHistory;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener2;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.monitor.core.StatusHandler;

/**
 * @author Mik Kersten
 * @author Shawn Minto
 */
public class TypeHistoryManager implements IInteractionContextListener2 {

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
		IJavaElement element = JavaCore.create(node.getHandleIdentifier());
		if (element instanceof IType) {
			IType type = (IType) element;
			try {
				if (type.exists() && !type.isAnonymous() && !isAspectjType(type)) {
//					TypeInfo info = factory.create(type.getPackageFragment().getElementName().toCharArray(), type
//							.getElementName().toCharArray(), enclosingTypeNames(type), type.getFlags(), getPath(type));

					JavaSearchTypeNameMatch typeNameMatch = new JavaSearchTypeNameMatch(type, type.getFlags());

					if (add && !OpenTypeHistory.getInstance().contains(typeNameMatch)) {
						OpenTypeHistory.getInstance().accessed(typeNameMatch);
					} else {
						OpenTypeHistory.getInstance().remove(typeNameMatch);
					}
				}
			} catch (JavaModelException e) {
				StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.PLUGIN_ID, "Failed to update history for a type", e));
			}
		}
	}

	/**
	 * HACK: to avoid adding AspectJ types, for example:
	 * 
	 * class: =TJP Example/src<tjp{Demo.java[Demo aspect: =TJP Example/src<tjp*GetInfo.aj}GetInfo
	 */
	private boolean isAspectjType(IType type) {
		if (type.getHandleIdentifier().indexOf('}') != -1) {
			return true;
		} else {
			return false;
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
		TypeNameMatch[] typeInfos = OpenTypeHistory.getInstance().getTypeInfos();
		for (int i = 0; i < typeInfos.length; i++) {
			OpenTypeHistory.getInstance().remove(typeInfos[i]);
		}
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
