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

import java.io.File;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.core.JavaModel;
import org.eclipse.jdt.internal.core.search.HierarchyScope;
import org.eclipse.jdt.internal.core.search.indexing.IIndexConstants;
import org.eclipse.jdt.internal.corext.util.TypeInfo;
import org.eclipse.jdt.internal.corext.util.TypeInfoFactory;
import org.eclipse.jdt.internal.corext.util.TypeInfoHistory;
import org.eclipse.mylar.core.IMylarContext;
import org.eclipse.mylar.core.IMylarContextListener;
import org.eclipse.mylar.core.IMylarElement;
import org.eclipse.mylar.core.MylarPlugin;

/**
 * @author Mik Kersten
 */
public class TypeHistoryManager implements IMylarContextListener {

	private TypeInfoFactory factory = new TypeInfoFactory();
	
	public void contextActivated(IMylarContext context) {
		clearTypeHistory();		
		for (IMylarElement node : context.getInteresting()) updateTypeHistory(node, true);
	}

	/**
	 * Path has to be compatible with ITypeNameRequestor
	 */
	private void updateTypeHistory(IMylarElement node, boolean add) {
		IJavaElement element = JavaCore.create(node.getHandleIdentifier());
		if (element instanceof IType) {
			IType type = (IType)element;
			try { 
				if (type != null && type.exists() && !type.isAnonymous() && !isAspectjType(type)) {
					TypeInfo info = factory.create(
							type.getPackageFragment().getElementName().toCharArray(), 
							type.getElementName().toCharArray(),
							enclosingTypeNames(type),
							type.getFlags(), 
							getPath(type)); 
					
					if (add && !TypeInfoHistory.getInstance().contains(info)) {
						TypeInfoHistory.getInstance().accessed(info);
					} else {
						TypeInfoHistory.getInstance().remove(info);
					}
				}
			} catch (JavaModelException e) {
				MylarPlugin.log(e, "failed to update history for a type");
			}
		}
	}

	/**
	 * HACK: to avoid adding AspectJ types, for example:
	 * 
	 * class: =TJP Example/src<tjp{Demo.java[Demo 
	 * aspect: =TJP Example/src<tjp*GetInfo.aj}GetInfo 
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
		TypeInfo[] typeInfos = TypeInfoHistory.getInstance().getTypeInfos();
		for (int i = 0; i < typeInfos.length; i++) {
			TypeInfoHistory.getInstance().remove(typeInfos[i]);
		};
	}
	
	public void interestChanged(IMylarElement node) {
		updateTypeHistory(node, true);
	}

	public void interestChanged(List<IMylarElement> nodes) {
		for (IMylarElement node : nodes) interestChanged(node);
	}

	public void nodeDeleted(IMylarElement node) {
		updateTypeHistory(node, false);
	}
	
	public void presentationSettingsChanging(UpdateKind kind) {
		// ignore
	}

	public void presentationSettingsChanged(UpdateKind kind) {
		// ignore
	}

	public void landmarkAdded(IMylarElement node) {
		// ignore
	}

	public void landmarkRemoved(IMylarElement node) {
		// ignore
	}

	public void edgesChanged(IMylarElement node) {
		// ignore
	}

	/**
	 * Coped from: HierarchyScope constructor
	 */
	private String getPath(IType type) {
		String focusPath = null;
		IPackageFragmentRoot root = (IPackageFragmentRoot)type.getPackageFragment().getParent();
		if (root.isArchive()) {
			IPath jarPath = root.getPath();
			Object target = JavaModel.getTarget(ResourcesPlugin.getWorkspace().getRoot(), jarPath, true);
			String zipFileName;
			if (target instanceof IFile) {
				// internal jar
				zipFileName = jarPath.toString();
			} else if (target instanceof File) {
				// external jar
				zipFileName = ((File)target).getPath();
			} else {
				return null; // unknown target
			}
			focusPath =
				zipFileName
					+ IJavaSearchScope.JAR_FILE_ENTRY_SEPARATOR
					+ type.getFullyQualifiedName().replace('.', '/')
					+ HierarchyScope.SUFFIX_STRING_class;
		} else {
			focusPath = type.getPath().toString();
		}
		return focusPath;
	}
	
	/**
	 * Copied from: org.eclipse.java.search.SearchPattern
	 */
	private char[][] enclosingTypeNames(IType type) {
		IJavaElement parent = type.getParent();
		switch (parent.getElementType()) {
			case IJavaElement.CLASS_FILE:
				// For a binary type, the parent is not the enclosing type, but the declaring type is.
				// (see bug 20532  Declaration of member binary type not found)
				IType declaringType = type.getDeclaringType();
				if (declaringType == null) return CharOperation.NO_CHAR_CHAR;
				return CharOperation.arrayConcat(
					enclosingTypeNames(declaringType), 
					declaringType.getElementName().toCharArray());
			case IJavaElement.COMPILATION_UNIT:
				return CharOperation.NO_CHAR_CHAR;
			case IJavaElement.FIELD:
			case IJavaElement.INITIALIZER:
			case IJavaElement.METHOD:
				IType declaringClass = ((IMember) parent).getDeclaringType();
				return CharOperation.arrayConcat(
					enclosingTypeNames(declaringClass),
					new char[][] {declaringClass.getElementName().toCharArray(), IIndexConstants.ONE_STAR});
			case IJavaElement.TYPE:
				return CharOperation.arrayConcat(
					enclosingTypeNames((IType)parent), 
					parent.getElementName().toCharArray());
			default:
				return null;
		}
	}
}
