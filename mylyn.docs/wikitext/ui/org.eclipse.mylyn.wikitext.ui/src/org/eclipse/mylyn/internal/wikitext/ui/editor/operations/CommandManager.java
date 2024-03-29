/*******************************************************************************
 * Copyright (c) 2009, 2011 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.operations;

import org.eclipse.core.runtime.CoreException;

/**
 * @author David Green
 */
public interface CommandManager {

	/**
	 * perform the given command
	 * 
	 * @param command
	 *            the command to perform
	 * @throws CoreException
	 */
	void perform(AbstractDocumentCommand command) throws CoreException;
}
