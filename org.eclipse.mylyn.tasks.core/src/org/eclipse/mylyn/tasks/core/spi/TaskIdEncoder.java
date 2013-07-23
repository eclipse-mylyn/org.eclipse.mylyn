/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tasks.core.spi;

import static org.eclipse.core.runtime.Assert.isNotNull;

/**
 * An encoder that can encode/decode task ids. Task ids must not contain specific characters when used in the Mylyn
 * framework. This class can be used to safely encode a task id to the Mylyn-acceptable form, and decode it back to the
 * repository-specific form.
 * 
 * @since 3.10
 */
public class TaskIdEncoder {
	/**
	 * Encodes the given {@code repositoryId} to a form that is acceptable to Mylyn.
	 * 
	 * @param repositoryId
	 *            the repository id to encode
	 * @return the encoded id
	 * @see #decode(String)
	 */
	public static String encode(String repositoryId) {
		isNotNull(repositoryId);
		return repositoryId.replaceAll("%", "%25").replaceAll( //$NON-NLS-1$ //$NON-NLS-2$
				org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil.HANDLE_DELIM, "%2D"); //$NON-NLS-1$
	}

	/**
	 * Decodes the given {@code encodedForm} to it's original format.
	 * 
	 * @param encodedForm
	 *            the decoded form of the string, previously obtained from {@link #encode(String)}
	 * @return the decoded string
	 */
	public static String decode(String encodedForm) {
		isNotNull(encodedForm);
		return encodedForm.replaceAll(
				"%2D", org.eclipse.mylyn.internal.tasks.core.RepositoryTaskHandleUtil.HANDLE_DELIM) //$NON-NLS-1$
				.replaceAll("%25", "%"); //$NON-NLS-1$//$NON-NLS-2$
	}
}
