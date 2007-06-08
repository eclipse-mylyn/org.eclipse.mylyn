/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredContentProvider;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public interface ITaskListPresentation {

	public abstract IStructuredContentProvider getContentProvider();

	public abstract String getPresentationName();

	public abstract ImageDescriptor getImageDescriptor();

}