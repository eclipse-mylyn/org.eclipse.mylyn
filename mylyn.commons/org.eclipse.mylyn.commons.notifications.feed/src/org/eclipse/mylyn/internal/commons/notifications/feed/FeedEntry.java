/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *     Red Hat, Inc - Bug 412953.
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.feed;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;

/**
 * @author Steffen Pingel
 */
public class FeedEntry extends ServiceMessage {

	private final RSSItem source;

	public FeedEntry(String eventId, RSSItem source) {
		super(eventId);
		this.source = source;
		setId(source.getGuid());
		setTitle(source.getTitle());
		if (source.getDescription() != null) {
			setDescription(source.getDescription());
		}
		setUrl(source.getLink());
		setImage("dialog_messasge_info_image"); //$NON-NLS-1$
		setDate(getDate(source));
	}

	private Date getDate(RSSItem source) {
		SimpleDateFormat sdf = new SimpleDateFormat("E, dd MMM yyyy hh:mm:ss ZZZZ", Locale.US); //$NON-NLS-1$
		try {
			return sdf.parse(source.getPubDate());
		} catch (ParseException e) {
			StatusHandler.log(new Status(IStatus.ERROR, INotificationsFeed.ID_PLUGIN, "Processing pub date of \"" //$NON-NLS-1$
					+ source + "\" failed", e)); //$NON-NLS-1$
		}
		return null;
	}

	public FeedEntry(String eventId) {
		super(eventId);
		source = null;
	}

	public RSSItem getSource() {
		return source;
	}

	public List<String> getFilters(String key) {
		Assert.isNotNull(key);
		List<String> result = new ArrayList<>();

		if (source.getSubjects() != null) {
			for (String subject : source.getSubjects()) {
				String value = parseFilter(key, subject);
				if (value != null) {
					result.add(value);
				}
			}
		}
		return result;
	}

	public String getFilter(String key) {
		Assert.isNotNull(key);
		if (source.getSubjects() != null) {
			for (String subject : source.getSubjects()) {
				String value = parseFilter(key, subject);
				if (value != null) {
					return value;
				}
			}
		}
		return null;
	}

	private String parseFilter(String key, String category) {
		if (category != null) {
			int i = category.indexOf("="); //$NON-NLS-1$
			if (i != -1) {
				if (category.substring(0, i).trim().equals(key)) {
					return category.substring(i + 1).trim();
				}
			}
		}
		return null;
	}

}
