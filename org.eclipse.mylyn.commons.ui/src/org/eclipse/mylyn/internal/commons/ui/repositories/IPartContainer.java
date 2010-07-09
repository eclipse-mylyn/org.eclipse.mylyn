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

package org.eclipse.mylyn.internal.commons.ui.repositories;

import org.eclipse.jface.operation.IRunnableContext;

/**
 * @author Steffen Pingel
 */
public interface IPartContainer extends IRunnableContext {

	public void setMessage(String message, int messageType);

	public void updateButtons();

}
