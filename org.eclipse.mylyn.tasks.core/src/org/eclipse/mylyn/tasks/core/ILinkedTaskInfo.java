/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Eugene Kuleshov - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core;

/**
 * Task information linking tasks to resources.
 * 
 * @author Eugene Kuleshov
 * @since 2.0
 */
public interface ILinkedTaskInfo {

	public abstract String getTaskId();

	public abstract String getTaskUrl();

	public abstract String getRepositoryUrl();
	
	public abstract AbstractTask getTask();
	
	public abstract String getComment();
	
}
