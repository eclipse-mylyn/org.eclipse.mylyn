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
 * Interface for providing a gravatar hash.
 * 
 * @author Kevin Sawicki (kevin@github.com)
 */
public interface IGravatarHashProvider {

	/**
	 * Get hash for gravatar lookup
	 * 
	 * @return gravatar hash
	 */
	String getGravatarHash();

}
