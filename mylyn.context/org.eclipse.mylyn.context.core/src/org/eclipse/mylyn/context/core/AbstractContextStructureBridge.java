/*******************************************************************************
 * Copyright (c) 2004, 2009 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.core;

import java.util.List;

/**
 * Maps between domain elements (e.g. Java) and interaction context model elements.
 * 
 * @author Mik Kersten
 * @since 2.0
 */
public abstract class AbstractContextStructureBridge {

	protected String parentContentType = null;

	/**
	 * Used for delagating to when the parent of an element is known by another bridge.
	 */
	public void setParentContentType(String contentType) {
		parentContentType = contentType;
	}

	public abstract String getContentType();

	/**
	 * A workspace-unique and robust String identifier for a structured element. For example, in Java these are the IJavaElement's handle
	 * identifier. For XML, this could be an xpath, but due to the fact that xpaths rely on element ordering for identity they are not
	 * robust to element order switching.
	 * 
	 * @return null if the given object does not participate in the task context
	 */
	public abstract String getHandleIdentifier(Object object);

	/**
	 * @since 3.2
	 * @param allowChildBridgeHandles
	 *            true if the structure bridge can return the object handle for a child bridge (for the ResourceStructureBridge)
	 */
	public String getParentHandle(String handle, boolean allowChildBridgeHandles) {
		return getParentHandle(handle);
	}

	/**
	 * @return The handle identifier of the element that is the parent of this element in the containment hierarchy, or null if no such
	 *         parent exists.
	 */
	public abstract String getParentHandle(String handle);

	public abstract Object getObjectForHandle(String handle);

	public abstract List<String> getChildHandles(String handle);

	/**
	 * @return The name or a null String(""). Can't be null since the views displaying the context can't handle null names
	 */
	public abstract String getLabel(Object object);

	public abstract boolean canBeLandmark(String handle);

	public abstract boolean acceptsObject(Object object);

	/**
	 * @return false for objects that can not be filtered
	 */
	public abstract boolean canFilter(Object element);

	/**
	 * @return true if this is a resource that can be opened by an editor (i.e. false for a directory, or a Java method)
	 */
	public abstract boolean isDocument(String handle);

	/**
	 * TODO 4.0: either remove of make marker coupling explicit
	 * 
	 * @param resource
	 *            can be anything that has an element accessible via an offset, e.g. a file with a character offset
	 */
	public abstract String getHandleForOffsetInObject(Object resource, int offset);

	/**
	 * Used for switching kinds based on parent handles
	 */
	public abstract String getContentType(String elementHandle);

	public String getParentContentType() {
		return parentContentType;
	}

	public Object getAdaptedParent(Object object) {
		return null;
	}
}
