/*******************************************************************************
 * Copyright (c) 2017 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import java.util.Map;

import com.google.common.collect.ImmutableMap;

public class TestMediaWikiImageFetcher extends MediaWikiImageFetcher {
	private Map<String, String> imageServerContent;

	public void setImageServerContent(Map<String, String> serverContent) {
		this.imageServerContent = ImmutableMap.copyOf(serverContent);
	}

	@Override
	protected MediaWikiApiImageFetchingStrategy createImageFetchingStrategy() {
		return new TestMediaWikiApiImageFetchingStrategy(imageServerContent);
	}
}
