/*******************************************************************************
 * Copyright (c) 2004, 2010 Tasktop Technologies and others.
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

package org.eclipse.mylyn.tasks.bugs;

import org.eclipse.core.runtime.IStatus;

/**
 * @author Steffen Pingel
 * @since 3.4
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ITaskContribution {

	/**
	 * Appends <code>text</code> to the description of the task.
	 */
	void appendToDescription(String text);

	String getAttribute(String name);

	IProduct getProduct();

	IStatus getStatus();

	boolean isHandled();

	void setAttribute(String name, String value);

	void setHandled(boolean handled);

}
