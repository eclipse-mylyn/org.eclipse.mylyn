/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.pde.ui;

import java.util.Collections;
import java.util.List;

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
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.internal.ide.ui.XmlNodeHelper;
import org.eclipse.pde.internal.core.text.build.BuildEntry;
import org.eclipse.pde.internal.core.text.plugin.PluginNode;
import org.eclipse.pde.internal.core.text.plugin.PluginObjectNode;
import org.eclipse.pde.internal.ui.editor.PDEFormPage;
import org.eclipse.pde.internal.ui.editor.plugin.ManifestEditor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.views.markers.internal.ConcreteMarker;

/**
 * @author Mik Kersten
 */
@SuppressWarnings("restriction")
public class PdeStructureBridge extends AbstractContextStructureBridge {

	public final static String CONTENT_TYPE = "plugin.xml"; //$NON-NLS-1$

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

	@Override
	public String getParentHandle(String handle) {
		// we can only get the parent if we have a PluginObjectNode

		Object object = getObjectForHandle(handle);
		if (object instanceof PluginObjectNode) {
			PluginObjectNode parent = (PluginObjectNode) ((PluginObjectNode) object).getParentNode();
			if (parent != null) {
				return getHandleIdentifier(parent);
			} else {
				// the parent is the plugin.xml file, so return that handle
				int delimeterIndex = handle.indexOf(";"); //$NON-NLS-1$
				if (delimeterIndex != -1) {
					String parentHandle = handle.substring(0, delimeterIndex);
					return parentHandle;
				} else {
					return null;
				}
			}
		} else if (object instanceof PluginNode) {
			PluginNode parent = (PluginNode) ((PluginNode) object).getParentNode();
			if (parent != null) {
				return getHandleIdentifier(parent);
			} else {
				// the parent is the plugin.xml file, so return that handle
				int delimeterIndex = handle.indexOf(";"); //$NON-NLS-1$
				if (delimeterIndex != -1) {
					String parentHandle = handle.substring(0, delimeterIndex);
					return parentHandle;
				} else {
					return null;
				}
			}
		} else if (object instanceof IFile) {
			// String fileHandle = parentBridge.getParentHandle(handle);
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			return parentBridge.getParentHandle(handle);
		} else {
			return null;
		}
	}

	/**
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getObjectForHandle(java.lang.String)
	 */
	@Override
	public Object getObjectForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		int first = handle.indexOf(";"); //$NON-NLS-1$
		String filename = ""; //$NON-NLS-1$
		if (first == -1) {
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			return parentBridge.getObjectForHandle(handle);
		} else {
			// extract the filename from the handle since it represents a node
			filename = handle.substring(0, first);
		}

		try {
			// get the file and create a FileEditorInput
			IPath path = new Path(filename);
			IFile f = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);

			// get the start line for the element
			int start = Integer.parseInt(handle.substring(first + 1));

			// // get the content and the document so that we can get the offset
			// String content = XmlNodeHelper.getContents(f.getContents());
			// IDocument d = new Document(content);

			// get the offsets for the element
			// make sure that we are on a character and not whitespace
			// int offset = d.getLineOffset(start);
			// while(d.getChar(offset) == ' ')
			// offset++;

			// get the current editor which should be the ManifestEditor so that
			// we can get the element that we want
			IEditorPart editorPart = null;
			try {
				editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
			} catch (NullPointerException e) {
				// do nothing, this just means that there is no active page
			}
			if (editorPart != null && editorPart instanceof ManifestEditor) {
				PluginObjectNode node = PdeEditingMonitor.getNode((ManifestEditor) editorPart, start, true);
				// get the element based on the offset
				return node;
			} else {
				String content = XmlNodeHelper.getContents(f.getContents());
				IDocument d = new Document(content);
				PluginObjectNode node = PdeEditingMonitor.getNode(d, f, start, true);
				return node;
			}
		} catch (Exception e) {
			// ignore, means file doesn't exist
			// ContextCorePlugin.log(e, "handle failed");
		}
		return null;
	}

	/**
	 * Handle is filename;hashcodeOfElementAndAttributes
	 * 
	 * @see org.eclipse.mylyn.context.core.AbstractContextStructureBridge#getHandleIdentifier(java.lang.Object)
	 */
	@Override
	public String getHandleIdentifier(Object object) {
		// we can only create handles for PluginObjectNodes and plugin.xml files
		if (object instanceof XmlNodeHelper) {
			return ((XmlNodeHelper) object).getHandle();
		} else if (object instanceof PluginObjectNode node) {
			try {
				// get the handle for the PluginObjectNode
				if (node.getModel() == null || node.getModel().getUnderlyingResource() == null
						|| node.getModel().getUnderlyingResource().getFullPath() == null) {
					// ContextCorePlugin.log("PDE xml node's resource or model is
					// null: " + node.getName(), this);
					return null;
				}
				IPath path = new Path(node.getModel().getUnderlyingResource().getFullPath().toString());
				IFile file = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
				String handle = new XmlNodeHelper(file.getFullPath().toString(),
						PdeEditingMonitor.getStringOfNode(node).hashCode()).getHandle();
				return handle;
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN, "Could not get handle", e)); //$NON-NLS-1$
			}
		} else if (object instanceof PluginNode node) {
			try {
				if (node.getModel() == null || node.getModel().getUnderlyingResource() == null
						|| node.getModel().getUnderlyingResource().getFullPath() == null) {
					// ContextCorePlugin.log("PDE xml node's resource or model is
					// null: " + node.getName(), this);
					return null;
				}
				IPath path = new Path(node.getModel().getUnderlyingResource().getFullPath().toString());
				IFile file = (IFile) ((Workspace) ResourcesPlugin.getWorkspace()).newResource(path, IResource.FILE);
				String handle = new XmlNodeHelper(file.getFullPath().toString(),
						PdeEditingMonitor.getStringOfNode(node).hashCode()).getHandle();
				return handle;
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN, "Could not get handle", e)); //$NON-NLS-1$
			}

		} else if (object instanceof File file) {
			// get the handle for the file if it is plugin.xml
			if (file.getFullPath().toString().endsWith("plugin.xml")) { //$NON-NLS-1$
				return file.getFullPath().toString();
			}
		}
		return null;
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof PluginObjectNode node) {
			String name = node.getXMLAttributeValue("name"); //$NON-NLS-1$
			if (name == null) {
				name = node.getXMLTagName();
			}
			name = node.getModel().getUnderlyingResource().getName() + ": " + name; //$NON-NLS-1$
			return name;
		} else if (object instanceof File file) {
			if (file.getFullPath().toString().endsWith("plugin.xml")) { //$NON-NLS-1$
				return "plugin.xml"; //$NON-NLS-1$
			}
		}
		return ""; //$NON-NLS-1$
	}

	/**
	 * TODO: make a non-handle based test
	 */
	@Override
	public boolean canBeLandmark(String handle) {
		if (handle == null) {
			return false;
		} else {
			return handle.indexOf(';') == -1;
		}
	}

	@Override
	public boolean acceptsObject(Object object) {
		// we only accept PluginObjectNodes and plugin.xml Files
		if (object instanceof PluginNode || object instanceof PluginObjectNode || object instanceof BuildEntry
				|| object instanceof PDEFormPage) {
			return true;
		} else if (object instanceof XmlNodeHelper) {
			if (((XmlNodeHelper) object).getFilename().endsWith("plugin.xml")) { //$NON-NLS-1$
				return true;
			}
		} else if (object instanceof File file) {
			if (file.getFullPath().toString().endsWith("plugin.xml")) { //$NON-NLS-1$
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public boolean isDocument(String handle) {
		return handle.indexOf(';') == -1;
	}

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
			}
		} catch (Exception e) {
			return null;
		}

		if (markerResource != null) {
			// we can only get a handle for a marker with the resource
			// plugin.xml
			try {
				if (markerResource instanceof IFile file) {
					if (file.getFullPath().toString().endsWith("plugin.xml")) { //$NON-NLS-1$
						return file.getFullPath().toString();
					} else {
						return null;
					}
				}
				return null;
			} catch (Throwable t) {
				StatusHandler.log(new Status(IStatus.WARNING, PdeUiBridgePlugin.ID_PLUGIN,
						"Could not find element for: " + object)); //$NON-NLS-1$
				return null;
			}
		} else if (object instanceof IFile) {
			try {
				IFile file = (IFile) object;
				if (file.getFullPath().toString().endsWith("plugin.xml")) { //$NON-NLS-1$
					String content = XmlNodeHelper.getContents(file.getContents());
					IDocument d = new Document(content);
					PluginObjectNode node = PdeEditingMonitor.getNode(d, file, offset, false);
					String handle = new XmlNodeHelper(file.getFullPath().toString(),
							PdeEditingMonitor.getStringOfNode(node).hashCode()).getHandle();
					return handle;
				}
			} catch (Exception e) {
				StatusHandler.log(new Status(IStatus.ERROR, PdeUiBridgePlugin.ID_PLUGIN,
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

	public boolean containsProblem(IInteractionElement node) {
		// TODO Auto-generated method stub
		return false;
	}
}
