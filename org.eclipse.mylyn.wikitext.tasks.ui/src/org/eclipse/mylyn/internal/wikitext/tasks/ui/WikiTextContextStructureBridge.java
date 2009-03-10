/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.tasks.ui;

import java.io.BufferedInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.content.IContentDescription;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.mylyn.context.core.AbstractContextStructureBridge;
import org.eclipse.mylyn.context.core.ContextCore;
import org.eclipse.mylyn.wikitext.core.WikiText;
import org.eclipse.mylyn.wikitext.core.parser.markup.MarkupLanguage;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineItem;
import org.eclipse.mylyn.wikitext.core.parser.outline.OutlineParser;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.PlatformUI;

/**
 * A Mylyn {@link AbstractContextStructureBridge context structure bridge} for wikitext files.
 * 
 * @author David Green
 */
public class WikiTextContextStructureBridge extends AbstractContextStructureBridge {

	private static final char HANDLE_FILE_SEPARATOR = ';';

	public static final String CONTENT_TYPE = WikiText.CONTENT_TYPE;

	@Override
	public boolean acceptsObject(Object object) {
		if (object instanceof OutlineItem) {
			return true;
		} else if (object instanceof IFile) {
			IFile file = (IFile) object;

			try {
				IContentDescription description = file.getContentDescription();
				if (description != null) {
					IContentType contentType = description.getContentType();
					if (contentType != null) {
						if (isWikiText(contentType)) {
							return true;
						}
					}
				}
			} catch (CoreException e) {
				// ignore
			}

			String languageName = WikiText.getMarkupLanguageNameForFilename(file.getName());
			return languageName != null;
		}
		return false;
	}

	private boolean isWikiText(IContentType contentType) {
		if (WikiText.CONTENT_TYPE.equals(contentType.getId())) {
			return true;
		}
		IContentType baseType = contentType.getBaseType();
		if (baseType != null) {
			return isWikiText(baseType);
		}
		return false;
	}

	@Override
	public boolean canBeLandmark(String handle) {
		return handle != null;
	}

	@Override
	public boolean canFilter(Object element) {
		return true;
	}

	@Override
	public List<String> getChildHandles(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) object;
			if (!item.getChildren().isEmpty()) {
				List<String> handles = new ArrayList<String>(item.getChildren().size());
				for (OutlineItem child : item.getChildren()) {
					handles.add(getHandleIdentifier(child));
				}
				return handles;
			}
		}
		return Collections.emptyList();
	}

	@Override
	public String getContentType() {
		return CONTENT_TYPE;
	}

	@Override
	public String getContentType(String elementHandle) {
		if (elementHandle.indexOf(HANDLE_FILE_SEPARATOR) == -1) {
			return parentContentType;
		}
		return CONTENT_TYPE;
	}

	@Override
	public String getHandleForOffsetInObject(Object object, int offset) {
		IResource resource = null;
		try {
			if (object instanceof IResource) {
				resource = (IResource) object;
			} else if (object instanceof IMarker) {
				resource = ((IMarker) object).getResource();
			} else {
				try {
					// works with ConcreteMarker without creating a compile-time dependency on internals
					IMarker marker = (IMarker) object.getClass().getMethod("getMarker").invoke(object); //$NON-NLS-1$
					resource = marker.getResource();
				} catch (Exception e) {
					// ignore
				}
			}
		} catch (Exception e) {
			return null;
		}
		if (resource instanceof IFile) {
			IFile file = (IFile) resource;
			if (acceptsObject(file)) {
				OutlineItem outline = getOutline(file);
				if (outline != null) {
					OutlineItem item = outline.findNearestMatchingOffset(offset);
					if (item != null) {
						return getHandleIdentifier(item);
					}
				}
			}
		}
		return null;
	}

	@Override
	public String getLabel(Object object) {
		if (object instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) object;
			if (item.getParent() == null) {
				return getFile(item).getName();
			} else {
				return item.getLabel();
			}
		}
		return ""; //$NON-NLS-1$
	}

	@Override
	public Object getObjectForHandle(String handle) {
		if (handle == null) {
			return null;
		}
		int idxOfSeparator = handle.indexOf(HANDLE_FILE_SEPARATOR);
		String filename = handle;
		if (idxOfSeparator != -1) {
			filename = handle.substring(0, idxOfSeparator);
		}
		IFile file;
		try {
			file = ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(filename));
		} catch (Exception e) {
			// be error-tolerant since we don't know much about the handle at this point
			return null;
		}
		if (file != null) {
			if (idxOfSeparator != -1) {
				String headingId = handle.substring(idxOfSeparator + 1);
				OutlineItem outline = getOutline(file);
				if (outline != null) {
					OutlineItem item = outline.findItemById(headingId);
					return item;
				}
			} else {
				return file;
			}
		}
		return null;
	}

	@Override
	public String getParentHandle(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) object;
			if (item.getParent() != null) {
				return getHandleIdentifier(item.getParent());
			} else {
				return getHandleIdentifier(getFile(item));
			}
		} else if (object instanceof IFile) {
			AbstractContextStructureBridge parentBridge = ContextCore.getStructureBridge(parentContentType);
			return parentBridge.getParentHandle(handle);
		}
		return null;
	}

	@Override
	public boolean isDocument(String handle) {
		Object object = getObjectForHandle(handle);
		if (object instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) object;
			return item.getParent() == null;
		}
		return false;
	}

	@Override
	public String getHandleIdentifier(Object object) {
		if (object instanceof OutlineItem) {
			OutlineItem item = (OutlineItem) object;
			return item.getResourcePath() + HANDLE_FILE_SEPARATOR + item.getId();
		} else if (object instanceof IFile && acceptsObject(object)) {
			return ((IFile) object).getFullPath().toString();
		}
		return null;
	}

	private IPath getFullPath(OutlineItem item) {
		String resourcePath = item.getResourcePath();
		return resourcePath == null ? null : new Path(resourcePath);
	}

	private OutlineItem getOutline(IFile file) {
		// FIXME: is editor integration the way to go?? we probably need some kind of core model
		IEditorPart editorPart = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActiveEditor();
		if (editorPart != null) {
			OutlineItem outline = (OutlineItem) editorPart.getAdapter(OutlineItem.class);
			if (outline != null) {
				return outline;
			}
		}
		MarkupLanguage markupLanguage = WikiText.getMarkupLanguageForFilename(file.getName());
		if (markupLanguage != null) {
			OutlineParser parser = new OutlineParser(markupLanguage);
			try {
				String contents = getContents(file);
				OutlineItem outline = parser.parse(contents);
				outline.setResourcePath(file.getFullPath().toString());
				return outline;
			} catch (Exception e) {
				// ignore
				return null;
			}
		}
		return null;
	}

	private String getContents(IFile file) throws Exception {
		String charset = file.getCharset();
		StringWriter writer = new StringWriter();
		Reader reader = new InputStreamReader(new BufferedInputStream(file.getContents()), charset);
		int i;
		while ((i = reader.read()) != -1) {
			writer.write(i);
		}
		return writer.toString();
	}

	IFile getFile(OutlineItem item) {
		IPath fullPath = getFullPath(item);

		return fullPath == null ? null : ResourcesPlugin.getWorkspace().getRoot().getFile(fullPath);
	}
}
