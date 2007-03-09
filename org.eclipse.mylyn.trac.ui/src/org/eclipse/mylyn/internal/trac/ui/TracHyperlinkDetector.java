/*******************************************************************************
 * Copyright (c) 2004 - 2006 Mylar committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylar.internal.trac.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.mylar.internal.trac.core.ITracClient;
import org.eclipse.mylar.internal.trac.core.TracCorePlugin;
import org.eclipse.mylar.tasks.core.TaskRepository;
import org.eclipse.mylar.tasks.ui.editors.AbstractTaskHyperlinkDetector;

/**
 * @author Steffen Pingel
 */
public class TracHyperlinkDetector extends AbstractTaskHyperlinkDetector {

	Pattern taskPattern = Pattern.compile("#(\\d*)");

	Pattern wikiPattern = Pattern.compile("\\[wiki:([^\\]]*)\\]", Pattern.CASE_INSENSITIVE | Pattern.DOTALL
			| Pattern.MULTILINE);

	/**
	 * Detects:
	 * 
	 * <ul>
	 * <li>#taskid
	 * <li>[wiki:page]
	 * <li>WikiPage
	 * </ul>
	 */
	public IHyperlink[] findHyperlinks(TaskRepository repository, String text, int lineOffset, int regionOffset) {
		List<IHyperlink> links = new ArrayList<IHyperlink>();

		Matcher m = taskPattern.matcher(text);
		while (m.find()) {
			String id = m.group(1);
			if (lineOffset >= m.start() && lineOffset <= m.end()) {
				IRegion linkRegion = new Region(regionOffset + m.start(), m.end() - m.start());
				links.add(new TaskHyperlink(linkRegion, repository, id));
			}
		}

		m = wikiPattern.matcher(text);
		while (m.find()) {
			String id = m.group(1);
			if (lineOffset >= m.start() && lineOffset <= m.end()) {
				IRegion linkRegion = new Region(regionOffset + m.start(), m.end() - m.start());
				links.add(new WebHyperlink(linkRegion, repository.getUrl() + ITracClient.WIKI_URL + id));
			}
		}

		return links.isEmpty() ? null : links.toArray(new IHyperlink[0]);
	}
	
	@Override
	protected String getTargetID() {
		return TracCorePlugin.REPOSITORY_KIND;
	}

}
