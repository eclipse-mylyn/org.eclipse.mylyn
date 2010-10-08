/*******************************************************************************
 * Copyright (c) 2010 Itema AS and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Itema AS - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.notifications;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.commons.ui.notifications.AbstractNotification;
import org.eclipse.swt.graphics.Image;

/**
 * @author Torkild U. Resheim
 */
public class BuildsServiceNotification extends AbstractNotification {

	public static final String EVENT_ID = "org.eclipse.mylyn.builds.ui.events.BuildServiceChanged"; //$NON-NLS-1$

	private final String description;

	private final String label;

	private final String notificationKindImage;

	private final String notificationImage;

	public BuildsServiceNotification(String label, String description) {
		super(EVENT_ID);
		this.label = label;
		this.description = description;
		this.notificationImage = null;
		this.notificationKindImage = Dialog.DLG_IMG_MESSAGE_INFO;
	}

	public int compareTo(AbstractNotification o) {
		return 0;
	}

	@Override
	public void open() {
		// ignore
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public Image getNotificationImage() {
		return Dialog.getImage(notificationImage);
	}

	@Override
	public Image getNotificationKindImage() {
		return Dialog.getImage(notificationKindImage);
	}

	public Object getAdapter(Class adapter) {
		// ignore
		return null;
	}

}
