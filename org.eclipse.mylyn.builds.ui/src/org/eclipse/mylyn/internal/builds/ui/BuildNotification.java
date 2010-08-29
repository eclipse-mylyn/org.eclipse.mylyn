/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.builds.ui;

import java.util.Date;

import org.eclipse.emf.common.notify.Notification;
import org.eclipse.mylyn.builds.core.IBuildElement;
import org.eclipse.mylyn.commons.ui.notifications.AbstractNotification;
import org.eclipse.mylyn.internal.builds.ui.view.BuildsView;
import org.eclipse.mylyn.internal.provisional.commons.ui.CommonImages;
import org.eclipse.swt.graphics.Image;

/**
 * @author Steffen Pingel
 */
public class BuildNotification extends AbstractNotification {

	IBuildElement element;

	private final Notification msg;

	public BuildNotification(Notification msg) {
		this.msg = msg;
		this.element = (IBuildElement) msg.getNotifier();
	}

	public int compareTo(AbstractNotification o) {
		return -1;
	}

	public Object getAdapter(Class adapter) {
		return null;
	}

	@Override
	public Date getDate() {
		return element.getRefreshDate();
	}

	@Override
	public String getDescription() {
		return element.getLabel();
	}

	@Override
	public String getLabel() {
		return "Build Completed";
	}

	@Override
	public Image getNotificationImage() {
		// ignore
		return null;
	}

	@Override
	public Image getNotificationKindImage() {
		return CommonImages.getImage(BuildImages.VIEW_BUILDS);
	}

	@Override
	public void open() {
		BuildsView.openInActivePerspective();
	}

	@Override
	public void setDate(Date date) {
		// ignore
	}

}
