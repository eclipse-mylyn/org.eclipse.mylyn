/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.core;

/**
 * @author Steffen Pingel
 */
public interface IBuildWorkingCopy extends IBuild {

	public abstract void setBuildNumber(int newBuildNumber);

	public abstract void setChangeSet(IChangeSet newChangeSet);

	public abstract void setDisplayName(String newDisplayName);

	public abstract void setDuration(long newDuration);

	public abstract void setId(String newId);

	public abstract void setLabel(String string);

	public abstract void setState(BuildState newState);

	public abstract void setStatus(BuildStatus newStatus);

	public abstract void setTimestamp(long newTimestamp);

	public abstract void setUrl(String url);

}