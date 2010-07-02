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
 * Transfer object for build plans that are not attached to a model.
 * 
 * @author Steffen Pingel
 */
public interface IBuildPlanData {

	public abstract int getHealth();

	public abstract String getId();

	public abstract String getInfo();

	public abstract String getName();

	public abstract BuildState getState();

	public abstract BuildStatus getStatus();

	public abstract String getSummary();

	public abstract String getUrl();

}
