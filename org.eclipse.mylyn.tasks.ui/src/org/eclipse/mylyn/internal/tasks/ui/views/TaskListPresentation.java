/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.views;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * @author Eugene Kuleshov
 */
public class TaskListPresentation implements ITaskListPresentation {

	private final String id;

	private final String name;

	private final ImageDescriptor imageDescriptor;

	private final AbstractTaskListContentProvider contentProvider;

	public TaskListPresentation(String id, String name, ImageDescriptor imageDescriptor,
			AbstractTaskListContentProvider contentProvider) {
		this.id = id;
		this.name = name;
		this.imageDescriptor = imageDescriptor;
		this.contentProvider = contentProvider;
	}

	public String getId() {
		return id;
	}

	public String getPresentationName() {
		return name;
	}

	public ImageDescriptor getImageDescriptor() {
		return imageDescriptor;
	}

	public AbstractTaskListContentProvider getContentProvider() {
		return contentProvider;
	}

}