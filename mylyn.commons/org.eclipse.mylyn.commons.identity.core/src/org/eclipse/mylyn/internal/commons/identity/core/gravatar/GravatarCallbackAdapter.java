/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

/**
 * Base implementation of {@link IGravatarCallback}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public abstract class GravatarCallbackAdapter implements IGravatarCallback {

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.core.gravatar.IGravatarCallback#loaded(org.eclipse.mylyn.internal.commons.identity.core.gravatar.Gravatar)
	 */
	public void loaded(Gravatar avatar) {
		// Does nothing sub-clsases should override
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.core.gravatar.IGravatarCallback#error(java.lang.Exception)
	 */
	public void error(Exception exception) {
		// Does nothing sub-clsases should override
	}

}
