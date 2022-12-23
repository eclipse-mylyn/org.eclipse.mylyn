/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core;

/**
 * A container that stores tasks from any repository. A task can only have a single AbstractTaskCategory parent (only be
 * in one category at a time).
 * 
 * @author Mik Kersten
 */
public abstract class AbstractTaskCategory extends AbstractTaskContainer {

	public AbstractTaskCategory(String handleAndDescription) {
		super(handleAndDescription);
	}

}
