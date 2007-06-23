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
/*
 * Created on Dec 26, 2004
 */
package org.eclipse.mylyn.internal.tasks.core;

import org.eclipse.mylyn.tasks.core.AbstractTaskCategory;


/**
 * @author Mik Kersten
 */
public final class TaskCategory extends AbstractTaskCategory {

	public TaskCategory(String handleAndDescription) {
		super(handleAndDescription);
	}

	@Override
	public boolean isUserDefined() {
		return true;
	}
}
