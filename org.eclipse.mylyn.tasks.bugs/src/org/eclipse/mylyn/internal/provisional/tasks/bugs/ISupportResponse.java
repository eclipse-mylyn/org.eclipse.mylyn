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

package org.eclipse.mylyn.internal.provisional.tasks.bugs;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.tasks.core.data.TaskData;

/**
 * @author Steffen Pingel
 * @since 3.2
 * @noextend This interface is not intended to be extended by clients.
 * @noimplement This interface is not intended to be implemented by clients.
 */
public interface ISupportResponse {

	public IProduct getProduct();

	public IStatus getStatus();

	public TaskData getTaskData();

}
