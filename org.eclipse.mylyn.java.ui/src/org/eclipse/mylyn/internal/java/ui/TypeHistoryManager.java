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

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.internal.core.search.JavaSearchTypeNameMatch;
import org.eclipse.jdt.internal.corext.util.OpenTypeHistory;
import org.eclipse.mylyn.context.core.IInteractionContext;
import org.eclipse.mylyn.context.core.IInteractionContextListener;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.monitor.core.util.StatusManager;

/**
 * @author Mik Kersten
 */
public class TypeHistoryManager implements IInteractionContextListener {

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
				if (type != null && type.exists() && !type.isAnonymous() && !isAspectjType(type)) {
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
				StatusManager.log(e, "failed to update history for a type");
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

	public void landmarkAdded(IInteractionElement node) {
		// ignore
	}

	public void landmarkRemoved(IInteractionElement node) {
		// ignore
	}

	public void relationsChanged(IInteractionElement node) {
		// ignore
	}
}
