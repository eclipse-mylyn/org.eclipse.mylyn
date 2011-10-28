/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

import org.eclipse.ui.IEditorInput;

/**
 * Adapter interface that specifies how editor inputs should be handled when a context is deactivated.
 * 
 * @author Steffen Pingel
 * @see IEditorInput#getAdapter(Class)
 * @see IContextAwareEditor
 * @since 3.7
 */
public abstract class ContextAwareEditorInput {

	/**
	 * Returns true, if the editor is not tracked as part of the task context and a close is forced on context
	 * deactivation.
	 * 
	 * @param contextHandle
	 *            handle of the context that is being deactivated or cleared
	 */
	public abstract boolean forceClose(String contextHandle);

}
