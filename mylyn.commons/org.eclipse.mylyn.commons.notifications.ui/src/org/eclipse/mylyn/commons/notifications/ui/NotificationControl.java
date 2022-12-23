/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.notifications.ui;

import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.mylyn.commons.ui.CommonImages;
import org.eclipse.mylyn.commons.workbench.forms.MessageControl;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.widgets.ImageHyperlink;

/**
 * @author Steffen Pingel
 * @since 3.7
 */
public abstract class NotificationControl extends MessageControl {

	private static final String NOTIFICATIONS_PREF_PAGE = "org.eclipse.mylyn.commons.notifications.preferencePages.Notifications"; //$NON-NLS-1$

	private ImageHyperlink configureLink;

	private String preferencesPageId;

	public NotificationControl(Composite parent) {
		super(parent);
		setPreferencesPageId(NOTIFICATIONS_PREF_PAGE);
	}

	public String getPreferencesPageId() {
		return preferencesPageId;
	}

	public void setPreferencesPageId(String preferencesPageId) {
		this.preferencesPageId = preferencesPageId;
	}

	@Override
	protected void createLinkControls(Composite buttonsComp) {
		if (getPreferencesPageId() != null) {
			configureLink = new ImageHyperlink(buttonsComp, SWT.NONE);
			configureLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CONFIGURE));
			configureLink.addHyperlinkListener(new HyperlinkAdapter() {
				@Override
				public void linkActivated(HyperlinkEvent e) {
					PreferenceDialog pd = PreferencesUtil.createPreferenceDialogOn(getShell(), getPreferencesPageId(),
							new String[0], getEventId());
					// Only close the message if the did not cancel the operation 
					if (pd != null) {
						pd.open();
					}
				}

				@Override
				public void linkEntered(HyperlinkEvent e) {
					configureLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CONFIGURE_HOVER));
				}

				@Override
				public void linkExited(HyperlinkEvent e) {
					configureLink.setImage(CommonImages.getImage(CommonImages.NOTIFICATION_CONFIGURE));
				}
			});
			// Initially invisible, must have eventId for this to be of any use.
			configureLink.setVisible(getEventId() != null);
		}
		super.createLinkControls(buttonsComp);
	}

	@Override
	protected void setEventId(String eventId) {
		super.setEventId(eventId);
		if (configureLink != null) {
			configureLink.setVisible(eventId != null);
		}
	}

}
