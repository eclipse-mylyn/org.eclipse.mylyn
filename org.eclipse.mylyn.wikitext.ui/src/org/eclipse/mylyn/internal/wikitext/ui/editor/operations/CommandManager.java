/*******************************************************************************
 * Copyright (c) 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.ui.editor.operations;

import org.eclipse.core.runtime.CoreException;

/**
 * 
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
	public void perform(AbstractDocumentCommand command) throws CoreException;
}
