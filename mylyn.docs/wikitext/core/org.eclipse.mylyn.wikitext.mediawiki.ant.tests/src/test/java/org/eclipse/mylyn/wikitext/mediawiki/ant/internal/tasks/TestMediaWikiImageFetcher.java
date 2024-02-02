/*******************************************************************************
 * Copyright (c) 2017, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import java.util.Map;

public class TestMediaWikiImageFetcher extends MediaWikiImageFetcher {
	private Map<String, String> imageServerContent;

	public void setImageServerContent(Map<String, String> serverContent) {
		imageServerContent = Map.copyOf(serverContent);
	}

	@Override
	protected MediaWikiApiImageFetchingStrategy createImageFetchingStrategy() {
		return new TestMediaWikiApiImageFetchingStrategy(imageServerContent);
	}
}
