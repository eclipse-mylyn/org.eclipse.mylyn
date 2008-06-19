/*******************************************************************************
 * Copyright (c) 2004, 2008 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.cdt.mylyn.internal.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.cdt.core.model.CModelException;
import org.eclipse.cdt.core.model.CoreModel;
import org.eclipse.cdt.core.model.IBinary;
import org.eclipse.cdt.core.model.ICElement;
import org.eclipse.cdt.core.model.IFunction;
import org.eclipse.cdt.core.model.IInclude;
import org.eclipse.cdt.core.model.IMethod;
import org.eclipse.cdt.core.model.IParent;
import org.eclipse.cdt.core.model.ISourceReference;
import org.eclipse.cdt.core.model.ITranslationUnit;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.context.core.ContextCorePlugin;
import org.eclipse.mylyn.internal.resources.ui.ResourceStructureBridge;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 * @author Jeff Johnston
 */
public class CDTStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "c/c++"; // $NON-NLS-1$
	public final static int C_SOURCEROOT = 1000;

	public CDTStructureBridge() {
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public Object getAdaptedParent(Object object) {
		if (object instanceof IFile) {
			IFile file = (IFile) object;
			return CoreModel.getDefault().create(file.getParent());
		} else {
			return super.getAdaptedParent(object);
		}
	}

	@Override
	public String getParentHandle(String handle) {
		ICElement cElement = (ICElement) getObjectForHandle(handle);
		if (cElement != null && cElement.getParent() != null) {
			return getHandleForElement(cElement.getParent());
		} else {
			return null;
		}
	}

	@Override
	public List<String> getChildHandles(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof ICElement) {
			ICElement element = (ICElement) object;
			if (element instanceof IParent) {
				IParent parent = (IParent) element;
				ICElement[] children;
				try {
					children = parent.getChildren();
					List<String> childHandles = new ArrayList<String>();
					for (int i = 0; i < children.length; i++) {
						String childHandle = getHandleIdentifier(children[i]);
						if (childHandle != null)
							childHandles.add(childHandle);
					}
					AbstractContextStructureBridge parentBridge = ContextCorePlugin.getDefault().getStructureBridge(
							parentContentType);
					if (parentBridge != null && parentBridge instanceof ResourceStructureBridge) {
						// TODO: Make sure line below is correct
						if (element.getElementType() < ICElement.C_NAMESPACE) {
							List<String> resourceChildren = parentBridge.getChildHandles(handle);
							if (!resourceChildren.isEmpty())
								childHandles.addAll(resourceChildren);
						}
					}

					return childHandles;
				} catch (CModelException e) {
					// ignore these, usually indicate no-existent element
				}
			}
		}
		return Collections.emptyList();
	}
	
	public static ICElement getElementForHandle(String handle) {
		return CoreModel.create(handle);
	}


	@Override
	public Object getObjectForHandle(String handle) {
		return getElementForHandle(handle);
	}

	// In the CDT, an ICElement does not have a handle interface like a
	// JavaElement.  So, to find an element again, we save its CProject name,
	// plus its path, plus its element name.  The CProject name allows us
	// to find the CProject.  The path allows us to find the ITranslationUnit.
	// The element name (optional) allows us to find an element within the
	// TranslationUnit.
	public static String getHandleForElement(ICElement element) {
		return element.getHandleIdentifier();
	}
	
	/**
	 * Uses resource-compatible path for projects
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof ICElement) {
			return getHandleForElement((ICElement) object);
		} else {
			if (object instanceof IAdaptable) {
				Object adapter = ((IAdaptable) object).getAdapter(ICElement.class);
				if (adapter instanceof ICElement) {
					return getHandleForElement((ICElement) adapter);
				}
			}
		}
		return null;
	}


	@Override
	public String getLabel(Object object) {
		if (object instanceof ICElement) {
			return ((ICElement) object).getElementName();
		} else {
			return "";
		}
	}

	@Override
	public boolean canBeLandmark(String handle) {
		ICElement element = (ICElement) getObjectForHandle(handle);
		if ((element instanceof IMethod || element instanceof IFunction) && element.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * TODO: figure out if the non ICElement stuff is needed
	 */
	@Override
	public boolean acceptsObject(Object object) {
		if (object instanceof IResource) {
			object = ((IResource) object).getAdapter(ICElement.class);
		}

		boolean accepts = (object instanceof ICElement && !(object instanceof IBinary))
				|| object instanceof IWorkingSet;

		return accepts;
	}

	@Override
	public boolean canFilter(Object object) {
		// FIXME: Removed some logic from JavaStructureBridge...is it necessary?
		if (object instanceof IWorkingSet) {
			try {
				IWorkingSet workingSet = (IWorkingSet) object;
				IAdaptable[] elements = workingSet.getElements();
				for (int i = 0; i < elements.length; i++) {
					IAdaptable adaptable = elements[i];
					IInteractionElement element = ContextCorePlugin.getContextManager().getElement(
							getHandleIdentifier(adaptable));
					if (element.getInterest().isInteresting())
						return false;
				}
			} catch (Exception e) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		ICElement element = (ICElement) getObjectForHandle(handle);
		return element instanceof ITranslationUnit;
	}

	@Override
	public String getHandleForOffsetInObject(Object object, int offset) {
		IMarker marker;
		int charStart = 0;
		if (object instanceof ConcreteMarker)
			marker = ((ConcreteMarker)object).getMarker();
		if (object instanceof IMarker) {
			marker = (IMarker)object;
		} else {
			return null;
		}
		
		Object attribute = marker.getAttribute(IMarker.CHAR_START, 0);
		if (attribute instanceof Integer) {
			charStart = ((Integer)attribute).intValue();
		}
		
		try {
			ITranslationUnit translationUnit = null;
			IResource resource = marker.getResource();
			if (resource instanceof IFile) {
				IFile file = (IFile)resource;
				if (CoreModel.isValidTranslationUnitName(null, file.getName())) {
					ICElement element = CoreModel.getDefault().create(file);
					translationUnit = CoreModel.getDefault().createTranslationUnitFrom(element.getCProject(), element.getPath());
				} else {
					return null;
				}
			}
			if (translationUnit != null) {
				ICElement cElement = translationUnit.getElementAtOffset(charStart);
				if (cElement != null) {
					if (cElement instanceof IInclude)
						cElement = cElement.getParent().getParent();
					return cElement.getElementName();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (CModelException ex) {
			if (ex.doesNotExist())
				StatusHandler.fail(ex.getStatus());
			return null;
		}
	}

	@Override
	public String getContentType(String elementHandle) {
		return getContentType();
	}

	/**
	 * Some copying from:
	 * 
	 * @see org.eclipse.jdt.ui.ProblemsLabelDecorator
	 */
	public boolean containsProblem(IInteractionElement node) {
		try {
			ICElement element = (ICElement) getObjectForHandle(node.getHandleIdentifier());
			switch (element.getElementType()) {
			case ICElement.C_PROJECT:
			case ICElement.C_CCONTAINER:
			case ICElement.C_VCONTAINER:
				return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_INFINITE, null);
			case ICElement.C_UNIT:
				return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);
			case ICElement.C_USING:
			case ICElement.C_NAMESPACE:
			case ICElement.C_INCLUDE:
			case ICElement.C_TYPEDEF:
			case ICElement.C_MACRO:
			case ICElement.C_FUNCTION:
			case ICElement.C_METHOD:
			case ICElement.C_FIELD:
			case ICElement.C_VARIABLE_LOCAL:
			case ICElement.C_CLASS:
				ITranslationUnit cu = (ITranslationUnit) element.getAncestor(ICElement.C_UNIT);
				if (cu != null)
					return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);
			}
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}

	private boolean getErrorTicksFromMarkers(IResource res, int depth, ISourceReference sourceElement)
			throws CoreException {
		if (res == null || !res.isAccessible())
			return false;
		IMarker[] markers = res.findMarkers(IMarker.PROBLEM, true, depth);
		if (markers != null) {
			for (int i = 0; i < markers.length; i++) {
				IMarker curr = markers[i];
				if (sourceElement == null) {
					int priority = curr.getAttribute(IMarker.SEVERITY, -1);
					if (priority == IMarker.SEVERITY_ERROR) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
