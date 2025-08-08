/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.identity.core.spi;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.commons.identity.core.IIdentity;

/**
 * @author Steffen Pingel
 * @since 0.8
 */
public abstract class IdentityConnector {

	public abstract ProfileImage getImage(IIdentity identity, int preferredWidth, int preferredHeight,
			IProgressMonitor monitor) throws CoreException;

	public abstract boolean supportsImageSize(int preferredWidth, int preferredHeight);

	public abstract void updateProfile(Profile profile, IProgressMonitor monitor) throws CoreException;

}
