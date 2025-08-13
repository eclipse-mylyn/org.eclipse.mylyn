/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;

/**
 * @author Mik Kersten
 */
public class ResourceTestUtil {

	public static void deleteProject(IProject project) throws CoreException {
		if (project.exists() && !project.isOpen()) { // force opening so that
			// project can be
			// deleted without
			// logging (see bug
			// 23629)
			project.open(null);
		}
		deleteResource(project);
	}

	public static void deleteResource(IResource resource) throws CoreException {
		CoreException lastException = null;
		try {
			resource.delete(true, null);
		} catch (CoreException e) {
			lastException = e;
			// just print for info
			System.out.println("(CoreException): " + e.getMessage() + ", resource " + resource.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		} catch (IllegalArgumentException iae) {
			// just print for info
			System.out.println(
					"(IllegalArgumentException): " + iae.getMessage() + ", resource " + resource.getFullPath()); //$NON-NLS-1$ //$NON-NLS-2$
		}
		int retryCount = 60; // wait 1 minute at most
		while (resource.isAccessible() && --retryCount >= 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
			try {
				resource.delete(true, null);
			} catch (CoreException e) {
				lastException = e;
				// just print for info
				System.out.println("(CoreException) Retry " + retryCount + ": " + e.getMessage() + ", resource " //$NON-NLS-1$//$NON-NLS-2$//$NON-NLS-3$
						+ resource.getFullPath());
			} catch (IllegalArgumentException iae) {
				// just print for info
				System.out.println("(IllegalArgumentException) Retry " + retryCount + ": " + iae.getMessage() //$NON-NLS-1$//$NON-NLS-2$
						+ ", resource " + resource.getFullPath()); //$NON-NLS-1$
			}
		}
		if (!resource.isAccessible()) {
			return;
		}
		System.err.println("Failed to delete " + resource.getFullPath()); //$NON-NLS-1$
		if (lastException != null) {
			throw lastException;
		}
	}
}
