/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.context.ui;

/**
 * Tag interface for editors that do not get closed when a context is deactivated. The editor can specify whether or not
 * it should closed based on its input.
 * 
 * API-3.0: consider making this be based on editor inputs, not editors. See bug 208625.
 * 
 * @author Mik Kersten
 */
public interface IContextAwareEditor {

	public boolean canClose();

}
