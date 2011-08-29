/*******************************************************************************
 * Copyright (c) 2007, 2010 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Get;

class HtmlSourceImageFetchingStrategy extends ImageFetchingStrategy {

	private String base;

	private File src;

	@Override
	public Set<String> fetchImages() {
		if (!src.exists()) {
			throw new BuildException("@src does not exist: " + src); //$NON-NLS-1$
		}
		if (!src.isFile()) {
			throw new BuildException("@src is not a file: " + src); //$NON-NLS-1$
		}

		if (base == null) {
			throw new BuildException("Must specify @base"); //$NON-NLS-1$
		}
		if (base.endsWith("/")) { //$NON-NLS-1$
			base = base.substring(0, base.length() - 1);
		}
		Set<String> filenames = new HashSet<String>();

		Pattern fragmentUrlPattern = Pattern.compile("src=\"([^\"]+)\""); //$NON-NLS-1$
		Pattern imagePattern = Pattern.compile("alt=\"Image:([^\"]*)\"([^>]+)", Pattern.MULTILINE); //$NON-NLS-1$
		String htmlSrc;
		try {
			htmlSrc = readSrc();
		} catch (IOException e) {
			throw new BuildException("Cannot read src: " + src + ": " + e.getMessage(), e); //$NON-NLS-1$ //$NON-NLS-2$
		}
		log("Parsing " + src, Project.MSG_INFO); //$NON-NLS-1$
		int fileCount = 0;
		Matcher imagePatternMatcher = imagePattern.matcher(htmlSrc);
		while (imagePatternMatcher.find()) {
			String alt = imagePatternMatcher.group(1);
			String imageFragment = imagePatternMatcher.group(2);
			if (imageFragment != null) {
				Matcher fragmentUrlMatcher = fragmentUrlPattern.matcher(imageFragment);
				if (fragmentUrlMatcher.find()) {
					String url = fragmentUrlMatcher.group(1);
					String qualifiedUrl = base + url;
					log("Fetching " + qualifiedUrl, Project.MSG_INFO); //$NON-NLS-1$
					Get get = new Get();
					get.setProject(getProject());
					get.setLocation(getLocation());
					try {
						get.setSrc(new URL(qualifiedUrl));
					} catch (MalformedURLException e) {
						log("Skipping " + url + ": " + e.getMessage(), Project.MSG_WARN); //$NON-NLS-1$ //$NON-NLS-2$
						continue;
					}
					// note: we use the alt text for the name since for some files there is a case-difference between
					//       the server URL and the text used in the image src of the markup
					String name = alt == null ? url.substring(url.lastIndexOf('/')) : alt;
					name = name.replace(' ', '_');
					get.setDest(new File(dest, name));
					get.execute();
					filenames.add(name);
					++fileCount;
				}
			}
		}
		log("Fetched " + fileCount + " image files for " + src, Project.MSG_INFO); //$NON-NLS-1$ //$NON-NLS-2$

		return filenames;
	}

	private String readSrc() throws IOException {
		StringBuilder buf = new StringBuilder((int) src.length());
		Reader reader = new BufferedReader(new FileReader(src));
		try {
			int i;
			while ((i = reader.read()) != -1) {
				buf.append((char) i);
			}
		} finally {
			reader.close();
		}
		return buf.toString();
	}

	public String getBase() {
		return base;
	}

	public void setBase(String base) {
		this.base = base;
	}

	public File getSrc() {
		return src;
	}

	public void setSrc(File src) {
		this.src = src;
	}

}
