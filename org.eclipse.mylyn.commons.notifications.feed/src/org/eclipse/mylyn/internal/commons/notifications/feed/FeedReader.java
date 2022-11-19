/*******************************************************************************
 * Copyright (c) 2011, 2013 Tasktop Technologies and others.
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

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.notifications.core.IFilterable;
import org.eclipse.mylyn.commons.notifications.core.NotificationEnvironment;

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

		try {
			JAXBContext jc = JAXBContext.newInstance(RSS.class);
			Unmarshaller unmarshaller = jc.createUnmarshaller();
			JAXBElement<RSS> rss = unmarshaller.unmarshal(new StreamSource(in), RSS.class);

			for (RSSItem rssItem : rss.getValue().getItems()) {
				final FeedEntry entry = createEntry(rssItem);
				if (environment.matches(new IAdaptable() {
					public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
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
			return new Status(IStatus.ERROR, INotificationsFeed.ID_PLUGIN, IStatus.ERROR, "Failed to parse RSS feed", //$NON-NLS-1$
					e);
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				//ignore
			}
		}
	}

	protected FeedEntry createEntry(RSSItem rssItem) {
		return new FeedEntry(eventId, rssItem);
	}

	public List<FeedEntry> getEntries() {
		return entries;
	}

}
