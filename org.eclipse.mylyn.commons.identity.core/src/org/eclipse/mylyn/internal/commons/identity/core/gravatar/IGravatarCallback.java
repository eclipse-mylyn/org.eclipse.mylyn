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

package org.eclipse.mylyn.internal.commons.identity.core.gravatar;

/**
 * Callback interface for when gravatar loading completes or fails.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IGravatarCallback {

	/**
	 * Gravatar loaded successfully
	 * 
	 * @param avatar
	 */
	void loaded(Gravatar avatar);

	/**
	 * Gravatar loading failed
	 * 
	 * @param exception
	 */
	void error(Exception exception);

}
