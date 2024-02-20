/*******************************************************************************
 * Copyright (c) 2010, 2013 Itema AS and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Itema AS - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui.notifications;

import java.util.Date;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.AbstractUiNotification;
import org.eclipse.swt.graphics.Image;

/**
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author Torkild U. Resheim
 */
public class BuildsServiceNotification extends AbstractUiNotification {

	public static final String EVENT_ID = "org.eclipse.mylyn.builds.ui.events.BuildServiceChanged"; //$NON-NLS-1$

	private final String description;

	private final String label;

	private final String notificationKindImage;

	private final String notificationImage;

	public BuildsServiceNotification(String label, String description) {
		super(EVENT_ID);
		this.label = label;
		this.description = description;
		notificationImage = null;
		notificationKindImage = Dialog.DLG_IMG_MESSAGE_INFO;
	}

	@Override
	public int compareTo(AbstractNotification o) {
		if (o != null && o.getLabel() != null) {
			return o.getLabel().compareTo(label);
		}
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

	@Override
	public <T> T getAdapter(Class<T> adapter) {
		return adapter.cast(Platform.getAdapterManager().getAdapter(this, adapter));
	}

	@Override
	public Date getDate() {
		// ignore
		return null;
	}

}
