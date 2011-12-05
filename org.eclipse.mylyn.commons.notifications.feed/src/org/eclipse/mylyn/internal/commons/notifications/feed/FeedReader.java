/*******************************************************************************
 * Copyright (c) 2011 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.feed;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.notifications.core.IFilterable;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;

import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;

/**
 * @author Steffen Pingel
 */
public class FeedReader {

	private final NotificationEnvironment environment;

	private final List<FeedEntry> entries;

	private final String eventId;

	public FeedReader(String eventId, NotificationEnvironment environment) {
		this.eventId = eventId;
		this.environment = environment;
		this.entries = new ArrayList<FeedEntry>();
	}

	public IStatus parse(InputStream in, IProgressMonitor monitor) {
		SyndFeedInput input = new SyndFeedInput();
		try {
			SyndFeed feed = input.build(new XmlReader(in));
			for (Iterator<?> it = feed.getEntries().iterator(); it.hasNext();) {
				SyndEntry syndEntry = (SyndEntry) it.next();
				final FeedEntry entry = createEntry(syndEntry);
				if (environment.matches(new IAdaptable() {
					public Object getAdapter(@SuppressWarnings("rawtypes")
					Class adapter) {
						if (adapter == IFilterable.class) {
							return new IFilterable() {
								public List<String> getFilters(String key) {
									return entry.getFilters(key);
								}

								public String getFilter(String key) {
									return entry.getFilter(key);
								}
							};
						} else if (adapter == FeedEntry.class) {
							return entry;
						}
						return null;
					}
				}, monitor)) {
					entries.add(entry);
				}
			}
			return Status.OK_STATUS;
		} catch (Exception e) {
			return new Status(IStatus.ERROR, INotificationsFeed.ID_PLUGIN, IStatus.ERROR, "Failed to parse RSS feed", e); //$NON-NLS-1$ 
		}
	}

	protected FeedEntry createEntry(SyndEntry syndEntry) {
		return new FeedEntry(eventId, syndEntry);
	}

	public List<FeedEntry> getEntries() {
		return entries;
	}

}
