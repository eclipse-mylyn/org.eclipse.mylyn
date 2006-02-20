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

package org.eclipse.mylar.provisional.core;

import java.util.List;

import org.eclipse.core.resources.IProject;

/**
 * @author Mik Kersten
 */
public interface IMylarStructureBridge {

	public static final String DOS_0_LABEL = "disabled";

	public static final String DOS_1_LABEL = "landmark resources";

	public static final String DOS_2_LABEL = "interesting resources";

	public static final String DOS_3_LABEL = "interesting projects";

	public static final String DOS_4_LABEL = "project dependencies";

	public static final String DOS_5_LABEL = "entire workspace (slow)";

	/**
	 * Used for delagating to when the parent of an element is known by another
	 * bridge.
	 */
	public abstract void setParentBridge(IMylarStructureBridge bridge);

	// public abstract void addChildBridge(IMylarStructureBridge bridge);

	public abstract String getContentType();

	public abstract String getHandleIdentifier(Object object);

	public abstract String getParentHandle(String handle);

	public abstract Object getObjectForHandle(String handle);

	public abstract List<String> getChildHandles(String handle);

	/**
	 * @return The name or a null String(""). Can't be null since the views
	 *         displaying the context can't handle null names
	 */
	public abstract String getName(Object object);

	public abstract boolean canBeLandmark(String handle);

	public abstract boolean acceptsObject(Object object);

	/**
	 * @return false for objects that can not be filtered
	 */
	public abstract boolean canFilter(Object element);

	/**
	 * @return true if this is a resource that can be opened by an editor (i.e.
	 *         false for a directory, or a Java method)
	 */
	public abstract boolean isDocument(String handle);

	/**
	 * @param resource
	 *            can be anything that has an element accessible via an offset,
	 *            e.g. a file with a character offset
	 */
	public abstract String getHandleForOffsetInObject(Object resource, int offset);

	/**
	 * TODO remove coupling to projects
	 * 
	 * @return The IProject that contains the object, or null if there is no
	 *         project
	 */
	public abstract IProject getProjectForObject(Object object);

	/**
	 * Used for switching kinds based on parent handles
	 */
	public abstract String getContentType(String elementHandle);

	public abstract List<AbstractRelationProvider> getRelationshipProviders();

	public abstract List<IDegreeOfSeparation> getDegreesOfSeparation();
}
