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

package org.eclipse.mylyn.internal.tasks.core.notifications;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.runtime.Assert;

import com.sun.syndication.feed.module.DCModule;
import com.sun.syndication.feed.module.DCSubject;
import com.sun.syndication.feed.synd.SyndEntry;

/**
 * @author Steffen Pingel
 */
public class FeedEntry extends ServiceMessage {

	private final SyndEntry source;

	public FeedEntry(SyndEntry source) {
		this.source = source;
		setId(source.getUri());
		setTitle(source.getTitle());
		if (source.getDescription() != null) {
			setDescription(source.getDescription().getValue());
		}
		setUrl(source.getLink());
		setImage("dialog_messasge_info_image"); //$NON-NLS-1$
	}

	public FeedEntry() {
		this.source = null;
	}

	public SyndEntry getSource() {
		return source;
	}

	public List<String> getFilters(String key) {
		Assert.isNotNull(key);
		List<String> result = new ArrayList<String>();
		DCModule module = (DCModule) source.getModule(DCModule.URI);
		if (module != null && module.getSubjects() != null) {
			for (Iterator<?> it = module.getSubjects().iterator(); it.hasNext();) {
				DCSubject category = (DCSubject) it.next();
				String value = parseFilter(key, category.getValue());
				if (value != null) {
					result.add(value);
				}
			}
		}
//		for (Iterator<?> it = source.getCategories().iterator(); it.hasNext();) {
//			SyndCategory category = (SyndCategory) it.next();
//			String value = parseFilter(key, category.getName());
//			if (value != null) {
//				result.add(value);
//			}
//		}
		return result;
	}

	public String getFilter(String key) {
		Assert.isNotNull(key);
		DCModule module = (DCModule) source.getModule(DCModule.URI);
		if (module != null && module.getSubjects() != null) {
			for (Iterator<?> it = module.getSubjects().iterator(); it.hasNext();) {
				DCSubject category = (DCSubject) it.next();
				String value = parseFilter(key, category.getValue());
				if (value != null) {
					return value;
				}
			}
		}
//		for (Iterator<?> it = source.getCategories().iterator(); it.hasNext();) {
//			SyndCategory category = (SyndCategory) it.next();
//			String value = parseCategory(key, category.getName());
//			if (value != null) {
//				return value;
//			}
//		}
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
