/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Red Hat, Inc - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.notifications.feed;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "rss")
public class RSS {

	private ArrayList<RSSItem> items;

	/**
	 * @return the items
	 */
	@XmlElementWrapper(name = "channel")
	@XmlElement(name = "item")
	public ArrayList<RSSItem> getItems() {
		return items;
	}

	/**
	 * @param items
	 *            the items to set
	 */
	public void setItems(ArrayList<RSSItem> items) {
		this.items = items;
	}

}