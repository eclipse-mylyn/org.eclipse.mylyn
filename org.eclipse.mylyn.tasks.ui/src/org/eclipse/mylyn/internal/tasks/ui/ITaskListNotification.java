/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui;

import java.util.Date;

import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 */
public interface ITaskListNotification extends Comparable<ITaskListNotification> {

	public void openTask();

	public String getDescription();

	public String getDetails();
	
	public String getLabel();

	public Image getNotificationIcon();

	public Image getOverlayIcon();

	public Date getDate();

	public void setDate(Date date);

}
