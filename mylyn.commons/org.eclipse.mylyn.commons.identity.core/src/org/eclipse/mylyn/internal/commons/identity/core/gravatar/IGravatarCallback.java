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
