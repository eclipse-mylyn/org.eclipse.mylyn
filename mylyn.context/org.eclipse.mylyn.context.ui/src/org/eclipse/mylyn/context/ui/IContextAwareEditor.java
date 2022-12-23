/*******************************************************************************
 * Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.ui;

/**
 * Tag interface for editors that do not get closed when a context is deactivated. The editor can specify whether or not
 * it should closed based on its input.
 * 
 * @author Mik Kersten
 * @since 3.0
 */
// TODO 4.0 consider making this be based on editor inputs, not editors. See bug 208625.
public interface IContextAwareEditor {

	public boolean canClose();

}
