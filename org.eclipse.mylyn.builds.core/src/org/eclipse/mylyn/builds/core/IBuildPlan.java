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

import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.IOperationMonitor;

/**
 * Client API for build plans.
 * 
 * @author Steffen Pingel
 * @noimplement This interface is not intended to be implemented by clients.
 * @noextend This interface is not intended to be extended by clients.
 */
public interface IBuildPlan extends IBuildPlanData, IBuildElement {

	public List<IBuildPlan> getChildren();

	public IBuildPlan getParent();

	public boolean isSelected();

	void run(IOperationMonitor monitor) throws CoreException;

}
