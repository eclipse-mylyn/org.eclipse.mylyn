/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
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
