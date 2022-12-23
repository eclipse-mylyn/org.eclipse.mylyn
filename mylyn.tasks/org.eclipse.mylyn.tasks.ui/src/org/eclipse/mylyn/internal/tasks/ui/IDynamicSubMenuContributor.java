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

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;

/**
 * @author Mik Kersten
 */
// TODO 4.0 replace by platform contribution mechanism
public interface IDynamicSubMenuContributor {

	public abstract MenuManager getSubMenuManager(List<IRepositoryElement> selectedElements);

}
