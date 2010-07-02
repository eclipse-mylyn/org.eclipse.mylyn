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
 * Working copy for build plans.
 * 
 * @author Steffen Pingel
 */
public interface IBuildPlanWorkingCopy extends IBuildPlanData {

	public void setStatus(BuildStatus status);

	public void setSummary(String summary);

	public void setHealth(int health);

	public void setInfo(String info);

	public void setId(String id);

	public void setName(String name);

	public void setState(BuildState state);

	public void setUrl(String url);

}
