/*******************************************************************************
 * Copyright (c) 2013 Red Hat, Inc and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
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