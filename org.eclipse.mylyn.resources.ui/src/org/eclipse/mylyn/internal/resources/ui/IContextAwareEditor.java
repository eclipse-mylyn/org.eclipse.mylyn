/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.resources.ui;

/**
 * Tag interface for editors that do not get closed when a context is deactivated. The editor can specify whether or not
 * it should closed based on its input.
 * 
 * @author Mik Kersten
 */
public interface IContextAwareEditor {

	public boolean canClose();

}
