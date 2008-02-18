/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.ide;

import java.util.Map;

import org.eclipse.core.runtime.IStatus;

/**
 * Note: This API is likely to change for 3.0.
 * 
 * @author Steffen Pingel
 * @since 2.3
 */
public abstract class AbstractTaskContributor {

	public abstract Map<String, String> getAttributes(IStatus status);
		
	public abstract String getDescription(IStatus status);
	
}
