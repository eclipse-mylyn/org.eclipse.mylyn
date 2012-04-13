/*******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.commons.identity.gravatar;

/**
 * Base implementation of {@link IGravatarCallback}
 * 
 * @author Kevin Sawicki (kevin@github.com)
 * @deprecated use classes in the <code>org.eclipse.mylyn.commons.identity.ui</code> bundle instead
 */
@Deprecated
public abstract class GravatarCallbackAdapter implements IGravatarCallback {

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarCallback#loaded(org.eclipse.mylyn.internal.commons.identity.gravatar.Gravatar)
	 */
	public void loaded(Gravatar avatar) {
		// Does nothing sub-clsases should override
	}

	/**
	 * @see org.eclipse.mylyn.internal.commons.identity.gravatar.IGravatarCallback#error(java.lang.Exception)
	 */
	public void error(Exception exception) {
		// Does nothing sub-clsases should override
	}

}
