/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.ide.ant;

import java.util.Collections;
import java.util.List;

import org.eclipse.ant.internal.ui.editor.AntEditor;
import org.eclipse.ant.internal.ui.editor.text.AntAnnotationModel;
import org.eclipse.ant.internal.ui.model.AntElementNode;
import org.eclipse.ant.internal.ui.model.AntModel;
import org.eclipse.ant.internal.ui.model.IProblemRequestor;
import org.eclipse.ant.internal.ui.model.LocationProvider;
import org.eclipse.core.internal.resources.File;
import org.eclipse.core.internal.resources.Marker;
import org.eclipse.core.internal.resources.Workspace;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.FileEditorInput;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class AntStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "build.xml"; //$NON-NLS-1$

	private static final char HANDLE_PATH_SEPARATOR = ';';

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getContentType()
	 */
	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getContentType(String elementHandle) {
		if (elementHandle.endsWith(".xml")) { //$NON-NLS-1$
			return parentContentType;
		} else {
			return CONTENT_TYPE;
		}
	}

	@Override
	public List<String> getChildHandles(String handle) {
		return Collections.emptyList();
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getParentHandle(java.lang.String)
	 */
	@Override
	public String getParentHandle(String handle) {
		Object o = getObjectForHandle(handle);

		// we can only get the parent if the element is an AntElementNode
		if (o instanceof AntElementNode) {

			// try to get the parent node
			AntElementNode parent = ((AntElementNode) o).getParentNode();

			if (parent != null) {
				// get the handle for the parent node
				return getHandleIdentifier(parent);
			} else {
				// if the parent is null, we just need to return the handle for
				// the file
				int delimeterIndex = handle.indexOf(";"); //$NON-NLS-1$
				if (delimeterIndex != -1) {
					String parentHandle = handle.substring(0, delimeterIndex);
					return parentHandle;
				} else {
					return null;
				}
			}
		} else if (o instanceof IFile) {
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			return parentBridge.getParentHandle(handle);
		} else {
			// return null if we can't get a parents
			return null;
		}
	}

	/**
	 * TODO: performance issue?
	 */
	@Override
	public Object getObjectForHandle(String handle) {
		try {
			if (handle == null) {
				return null;
			}
			int first = handle.indexOf(HANDLE_PATH_SEPARATOR);
			String filename = ""; //$NON-NLS-1$
			if (first == -1) {
				// we have just the filename, so return the IFile for this
				// filename
				filename = handle;
				IPath path = new Path(filename);
				IFile f = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
				return f;
			} else {
				// we have an element since there is a line number
				// get the filename from the handle
				filename = handle.substring(0, first);
			}

			// get the file and create a new FileEditorInput
			IPath path = new Path(filename);
			IFile file = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
			FileEditorInput fei = new FileEditorInput(file);

			// get the line number that the element is on
			String elementPath = handle.substring(first + 1);
			if (elementPath.equals("")) { //$NON-NLS-1$
				return file;
			} else {
				// XXX needed if the editor is the only way to get the model
				// get the active editor, which should be the ant editor so we
				// can get the AntModel
				IEditorPart editorPart = PlatformUI.getWorkbench()
						.getActiveWorkbenchWindow()
						.getActivePage()
						.getActiveEditor();
				if (editorPart instanceof AntEditor) {
					AntModel antModel = ((AntEditor) editorPart).getAntModel();
					if (antModel != null) {
						return AntEditingMonitor.getNode(antModel, elementPath);
					}
				} else {
					String content = XmlNodeHelper.getContents(file.getContents());
					IDocument d = new Document(content);
					// create the ant model and get the element from it
					IProblemRequestor p = new AntAnnotationModel(file);
					AntModel am = new AntModel(d, p, new LocationProvider(fei));
					am.reconcile();
					return AntEditingMonitor.getNode(am, elementPath);
				}
			}
		} catch (Throwable t) {
			// ignore this, means that the file or node doesn't exist
		}
		return null;
	}

	/**
	 * Handle is filename;XPath
	 * 
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getHandleIdentifier(java.lang.Object)
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		// we can only create handles for AntElementNodes and build.xml Files
		if (object instanceof XmlNodeHelper) {
			return ((XmlNodeHelper) object).getHandle();
		} else if (object instanceof AntElementNode) {
			AntElementNode node = (AntElementNode) object;
			String path = node.getElementPath();
			if (path == null || node.getIFile() == null) {
				return null;
			}
			XmlNodeHelper helper = new XmlNodeHelper(node.getIFile().getFullPath().toString(), path);
			return helper.getHandle();
		} else if (object instanceof File) {
			File file = (File) object;
			// get the handle for the build.xml file
			if (file.getFullPath().toString().endsWith("build.xml")) { //$NON-NLS-1$
				return file.getFullPath().toString();
			}
		}
		return null;
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getLabel(java.lang.Object)
	 */
	@Override
	public String getLabel(Object object) {
		if (object instanceof AntElementNode) {
			AntElementNode n = (AntElementNode) object;
			String name = n.getIFile().getName() + ": " + n.getName(); //$NON-NLS-1$
			return name;
		} else if (object instanceof File) {
			File file = (File) object;
			if (file.getFullPath().toString().endsWith("build.xml")) { //$NON-NLS-1$
				return "build.xml"; //$NON-NLS-1$
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#canBeLandmark(Object) TODO: make a non-handle
	 *      based test
	 */
	@Override
	public boolean canBeLandmark(String handle) {
		if (handle != null) {
			return handle.indexOf(';') == -1;
		} else {
			return false;
		}
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#acceptsObject(java.lang.Object)
	 */
	@Override
	public boolean acceptsObject(Object object) {
		// we accept AntElementNode and build.xml File objects
		if (object instanceof AntElementNode) {
			return true;
		} else if (object instanceof XmlNodeHelper) {
			if (((XmlNodeHelper) object).getFilename().endsWith("build.xml")) { //$NON-NLS-1$
				return true;
			}
		} else if (object instanceof File) {
			File file = (File) object;
			if (file.getFullPath().toString().endsWith("build.xml")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#canFilter(java.lang.Object)
	 */
	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#isDocument(java.lang.String)
	 */
	@Override
	public boolean isDocument(String handle) {
		return handle.indexOf(';') == -1;
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getHandleForOffsetInObject(Object, int)
	 */
	@Override
	public String getHandleForOffsetInObject(Object object, int offset) {
		if (object == null) {
			return null;
		}

		IResource markerResource = null;
		try {
			if (object instanceof ConcreteMarker) {
				markerResource = ((ConcreteMarker) object).getMarker().getResource();
			} else if (object instanceof Marker) {
				markerResource = ((Marker) object).getResource();
			} else {
				return null;
			}
		} catch (Exception e) {
			return null;
		}

		if (markerResource != null) {
			// we can only return a handle if the resource is build.xml
			try {
				if (markerResource instanceof IFile) {
					IFile file = (IFile) markerResource;
					if (file.getFullPath().toString().endsWith("build.xml")) { //$NON-NLS-1$
						return file.getFullPath().toString();
					} else {
						return null;
					}
				}
				return null;
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.ERROR, AntUiBridgePlugin.ID_PLUGIN, "Could not find element for: " //$NON-NLS-1$
						+ object, t));
				return null;
			}
		} else if (object instanceof IFile) {
			try {
				IFile file = (IFile) object;
				if (file.getFullPath().toString().endsWith("build.xml")) { //$NON-NLS-1$
					FileEditorInput fei = new FileEditorInput(file);
					String content = XmlNodeHelper.getContents(file.getContents());
					IDocument d = new Document(content);
					// create the ant model and get the element from it
					IProblemRequestor p = new AntAnnotationModel(file);
					AntModel am = new AntModel(d, p, new LocationProvider(fei));
					am.reconcile();

					AntElementNode node = am.getNode(offset, false);
//					Method method = AntElementNode.class.getDeclaredMethod("getElementPath", new Class[] {});
//					method.setAccessible(true);
//					String path = (String) method.invoke(node, new Object[] {});
					String path = node.getElementPath();
					if (path == null) {
						return null;
					}
					String handle = new XmlNodeHelper(file.getFullPath().toString(), path).getHandle();
					return handle;
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.WARNING, AntUiBridgePlugin.ID_PLUGIN,
						"Unable to get handle for offset in object", e)); //$NON-NLS-1$
			}
		}
		return null;
	}

//	public IProject getProjectForObject(Object object) {
//		while (!(object instanceof IFile)) {
//			String handle = getParentHandle(getHandleIdentifier(object));
//			if (handle == null)
//				break;
//			object = getObjectForHandle(handle);
//		}
//		if (object instanceof IFile && acceptsObject(object)) {
//			return ((IFile) object).getProject();
//		}
//		return null;
//	}
}
