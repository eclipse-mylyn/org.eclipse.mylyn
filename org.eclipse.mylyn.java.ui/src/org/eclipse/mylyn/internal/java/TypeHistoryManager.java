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

package org.eclipse.mylar.internal.java;

import java.util.List;

import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.search.TypeNameMatch;
import org.eclipse.jdt.internal.core.search.JavaSearchTypeNameMatch;
import org.eclipse.jdt.internal.corext.util.OpenTypeHistory;
import org.eclipse.mylar.context.core.IMylarContext;
import org.eclipse.mylar.context.core.IMylarContextListener;
import org.eclipse.mylar.context.core.IMylarElement;
import org.eclipse.mylar.core.MylarStatusHandler;

/**
 * @author Mik Kersten
 */
public class TypeHistoryManager implements IMylarContextListener {

//	private TypeInfoFactory factory = new TypeInfoFactory();

	public void contextActivated(IMylarContext context) {
		clearTypeHistory();
		for (IMylarElement node : context.getInteresting())
			updateTypeHistory(node, true);
	}

	/**
	 * Path has to be compatible with ITypeNameRequestor
	 */
	private void updateTypeHistory(IMylarElement node, boolean add) {
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
				MylarStatusHandler.log(e, "failed to update history for a type");
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

	public void contextDeactivated(IMylarContext context) {
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

	public void interestChanged(List<IMylarElement> nodes) {
		for (IMylarElement node : nodes) {
			updateTypeHistory(node, true);
		}
	}

	public void elementDeleted(IMylarElement node) {
		updateTypeHistory(node, false);
	}

	public void landmarkAdded(IMylarElement node) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement node) {
		// ignore
	}

	public void relationsChanged(IMylarElement node) {
		// ignore
	}
}
