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

package org.eclipse.mylyn.internal.tasks.bugs.wizards;

import org.eclipse.core.runtime.IBundleGroup;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author Steffen Pingel
 */
public class FeatureStatus extends Status {

	private final IBundleGroup[] bundleGroups;

	public FeatureStatus(String id, IBundleGroup[] bundleGroups) {
		super(IStatus.INFO, id, ""); //$NON-NLS-1$
		this.bundleGroups = bundleGroups;
	}

	public IBundleGroup[] getBundleGroup() {
		return bundleGroups;
	}

}
