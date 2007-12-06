/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.ui.notifications;

import java.util.Date;

import org.eclipse.swt.graphics.Image;

/**
 * @author Rob Elves
 * @author Mik Kersten
 */
public abstract class AbstractNotification implements Comparable<AbstractNotification> {

	public abstract void open();

	public abstract String getDescription();

	public abstract String getLabel();

	public abstract Image getNotificationImage();

	public abstract Image getNotificationKindImage();

	public abstract Date getDate();

	public abstract void setDate(Date date);

}
