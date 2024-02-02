/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.java.ui;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClassFile;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IParent;
import org.eclipse.jdt.core.ISourceReference;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.core.JarEntryFile;
import org.eclipse.jdt.internal.core.JarPackageFragmentRoot;
import org.eclipse.jdt.internal.ui.packageview.ClassPathContainer;
import org.eclipse.jdt.internal.ui.packageview.PackageFragmentRootContainer;
import org.eclipse.jdt.internal.ui.util.ExceptionHandler;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 */
public class JavaStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "java"; //$NON-NLS-1$

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public Object getAdaptedParent(Object object) {
		if (object instanceof IFile file) {
			return JavaCore.create(file.getParent());
		} else {
			return super.getAdaptedParent(object);
		}
	}

	@Override
	public String getParentHandle(String handle) {
		IJavaElement javaElement = (IJavaElement) getObjectForHandle(handle);
		if (javaElement != null && javaElement.getParent() != null) {
			return getHandleIdentifier(javaElement.getParent());
		} else {
			return null;
		}
	}

	@Override
	public List<String> getChildHandles(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof IJavaElement element) {
			if (element instanceof IParent parent) {
				IJavaElement[] children;
				try {
					children = parent.getChildren();
					List<String> childHandles = new ArrayList<>();
					for (IJavaElement element2 : children) {
						String childHandle = getHandleIdentifier(element2);
						if (childHandle != null) {
							childHandles.add(childHandle);
						}
					}
					AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
					if (parentBridge != null
							&& ContextCore.CONTENT_TYPE_RESOURCE.equals(parentBridge.getContentType())) {
						if (element.getElementType() < IJavaElement.TYPE) {
							List<String> resourceChildren = parentBridge.getChildHandles(handle);
							if (!resourceChildren.isEmpty()) {
								childHandles.addAll(resourceChildren);
							}
						}
					}

					return childHandles;
				} catch (JavaModelException e) {
					// NOTE: it would be better if this was not hard-wired but used the parent/child bridge mapping
					AbstractContextStructureBridge parentBridge = ContextCore
							.getStructureBridge(ContextCore.CONTENT_TYPE_RESOURCE);
					return parentBridge.getChildHandles(handle);
				} catch (Exception e) {
					StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not get children", //$NON-NLS-1$
							e));
				}
			}
		}
		return Collections.emptyList();
	}

	@Override
	public Object getObjectForHandle(String handle) {
		try {
			return JavaCore.create(handle);
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.WARNING, JavaUiBridgePlugin.ID_PLUGIN,
					"Could not create java element for handle: " + handle, t)); //$NON-NLS-1$
			return null;
		}
	}

	/**
	 * Uses resource-compatible path for projects
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof IJavaElement) {
			return ((IJavaElement) object).getHandleIdentifier();
		} else if (object instanceof IAdaptable) {
			Object adapter = ((IAdaptable) object).getAdapter(IJavaElement.class);
			if (adapter instanceof IJavaElement) {
				return ((IJavaElement) adapter).getHandleIdentifier();
			}
		} else if (isWtpClass(object)) {
			return getWtpElementHandle(object);
		}
		return null;
	}

	/**
	 * TODO: remove after WTP 1.5.1 is generally available
	 */
	private String getWtpElementHandle(Object object) {
		Class<?> objectClass = object.getClass();
		try {
			Method getProjectMethod = objectClass.getMethod("getProject"); //$NON-NLS-1$
			Object javaProject = getProjectMethod.invoke(object);
			if (javaProject instanceof IJavaProject) {
				return ((IJavaElement) javaProject).getHandleIdentifier();
			}
		} catch (Exception e) {
			// ignore
			e.printStackTrace();
		}
		return null;
	}

	private boolean isWtpClass(Object object) {
		try {
			return object != null && object.getClass().getSimpleName().equals("CompressedJavaProject"); //$NON-NLS-1$
		} catch (Throwable t) {
			// could have malformed name, see bug 165065
			return false;
		}
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof IJavaElement) {
			return ((IJavaElement) object).getElementName();
		} else {
			return ""; //$NON-NLS-1$
		}
	}

	@Override
	public boolean canBeLandmark(String handle) {
		IJavaElement element = (IJavaElement) getObjectForHandle(handle);
		if ((element instanceof IMember || element instanceof IType) && element.exists()) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * TODO: figure out if the non IJavaElement stuff is needed
	 */
	@Override
	public boolean acceptsObject(Object object) {
		if (object instanceof IResource) {
			Object adapter = ((IResource) object).getAdapter(IJavaElement.class);
			return adapter instanceof IJavaElement;
		}

		boolean accepts = object instanceof IJavaElement || object instanceof PackageFragmentRootContainer
				|| object instanceof ClassPathContainer.RequiredProjectWrapper || object instanceof JarEntryFile
				|| object instanceof IPackageFragment || isWtpClass(object);

		return accepts;
	}

	/**
	 * Uses special rules for classpath containers since these do not have an associated interest, i.e. they're not IJavaElement(s).
	 */
	@Override
	public boolean canFilter(Object object) {
		if (object instanceof ClassPathContainer.RequiredProjectWrapper) {
			return true;
		} else if (object instanceof PackageFragmentRootContainer container) {
			// since not in model, check if it contains anything interesting
			Object[] children = container.getChildren();
			for (Object element2 : children) {
				if (element2 instanceof JarPackageFragmentRoot element) {
					IInteractionElement node = ContextCore.getContextManager()
							.getElement(element.getHandleIdentifier());
					if (node != null && node.getInterest().isInteresting()) {
						return false;
					}
				}
			}
		}
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		IJavaElement element = (IJavaElement) getObjectForHandle(handle);
		return element instanceof ICompilationUnit || element instanceof IClassFile;
	}

	@Override
	public String getHandleForOffsetInObject(Object object, int offset) {
		IMarker marker;
		if (object instanceof ConcreteMarker) {
			marker = ((ConcreteMarker) object).getMarker();
		} else if (object instanceof Marker) {
			marker = (Marker) object;
		} else {
			return null;
		}

		try {
			ICompilationUnit compilationUnit = null;
			IResource resource = marker.getResource();
			if (resource instanceof IFile file) {
				// TODO: get rid of file extension check
				if (file.getFileExtension().equals("java")) { //$NON-NLS-1$
					compilationUnit = JavaCore.createCompilationUnitFrom(file);
				} else {
					return null;
				}
			}
			if (compilationUnit != null) {
				// first try to resolve the character start, then the line number if not present
				int charStart = 0;
				Object attribute = marker.getAttribute(IMarker.CHAR_START, 0);
				if (attribute instanceof Integer) {
					charStart = (Integer) attribute;
				}
				IJavaElement javaElement = null;
				if (charStart != -1) {
					javaElement = compilationUnit.getElementAt(charStart);
				} else {
					int lineNumber = 0;
					Object lineNumberAttribute = marker.getAttribute(IMarker.LINE_NUMBER, 0);
					if (lineNumberAttribute instanceof Integer) {
						lineNumber = (Integer) lineNumberAttribute;
					}
					if (lineNumber != -1) {
						// could do finer granularity by uncommenting what's below, see bug 132092
//						Document document = new Document(compilationUnit.getSource());
//						IRegion region = document.getLineInformation(lineNumber);
//						javaElement = compilationUnit.getElementAt(region.getOffset());
						javaElement = compilationUnit;
					}
				}

				if (javaElement != null) {
					if (javaElement instanceof IImportDeclaration) {
						javaElement = javaElement.getParent().getParent();
					}
					return javaElement.getHandleIdentifier();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} catch (JavaModelException ex) {
			if (!ex.isDoesNotExist()) {
				ExceptionHandler.handle(ex, "error", "could not find java element"); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return null;
		} catch (Throwable t) {
			StatusHandler.log(new Status(IStatus.ERROR, JavaUiBridgePlugin.ID_PLUGIN, "Could not find element for: " //$NON-NLS-1$
					+ marker, t));
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
			IJavaElement element = (IJavaElement) getObjectForHandle(node.getHandleIdentifier());
			switch (element.getElementType()) {
				case IJavaElement.JAVA_PROJECT:
				case IJavaElement.PACKAGE_FRAGMENT_ROOT:
					return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_INFINITE, null);
				case IJavaElement.PACKAGE_FRAGMENT:
				case IJavaElement.COMPILATION_UNIT:
				case IJavaElement.CLASS_FILE:
					return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);
				case IJavaElement.PACKAGE_DECLARATION:
				case IJavaElement.IMPORT_DECLARATION:
				case IJavaElement.IMPORT_CONTAINER:
				case IJavaElement.TYPE:
				case IJavaElement.INITIALIZER:
				case IJavaElement.METHOD:
				case IJavaElement.FIELD:
				case IJavaElement.LOCAL_VARIABLE:
					ICompilationUnit cu = (ICompilationUnit) element.getAncestor(IJavaElement.COMPILATION_UNIT);
					if (cu != null) {
						return getErrorTicksFromMarkers(element.getResource(), IResource.DEPTH_ONE, null);
					}
			}
		} catch (CoreException e) {
			// ignore
		}
		return false;
	}

	private boolean getErrorTicksFromMarkers(IResource res, int depth, ISourceReference sourceElement)
			throws CoreException {
		if (res == null || !res.isAccessible()) {
			return false;
		}
		IMarker[] markers = res.findMarkers(IMarker.PROBLEM, true, depth);
		if (markers != null) {
			for (IMarker curr : markers) {
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
