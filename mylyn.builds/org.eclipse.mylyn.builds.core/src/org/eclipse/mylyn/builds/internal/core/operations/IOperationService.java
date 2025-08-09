/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.operations;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.mylyn.builds.internal.core.IBuildModelRealm;
import org.eclipse.mylyn.builds.internal.core.util.BuildScheduler;

/**
 * @author Steffen Pingel
 */
public interface IOperationService {

	BuildScheduler getScheduler();

	void handleResult(AbstractOperation operation, IStatus result);

	IBuildModelRealm getRealm();

}
