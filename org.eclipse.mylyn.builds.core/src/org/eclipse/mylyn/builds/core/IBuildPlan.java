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

/**
 * @author Steffen Pingel
 */
public interface IBuildPlan extends IBuildElement {

	public List<IBuildPlan> getChildren();

	public String getStatus();
	
	public String getSummary();
	
	public String getInfo();
	
}
