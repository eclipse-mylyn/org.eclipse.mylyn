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
