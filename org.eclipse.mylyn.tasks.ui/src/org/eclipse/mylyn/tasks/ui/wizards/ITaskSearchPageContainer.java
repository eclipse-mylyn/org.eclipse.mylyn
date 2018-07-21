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

package org.eclipse.mylyn.tasks.ui.wizards;

import org.eclipse.jface.operation.IRunnableContext;

/**
 * @author Steffen Pingel
 * @since 3.0
 */
public interface ITaskSearchPageContainer {

	public abstract IRunnableContext getRunnableContext();

	public abstract void setPerformActionEnabled(boolean enabled);

}
