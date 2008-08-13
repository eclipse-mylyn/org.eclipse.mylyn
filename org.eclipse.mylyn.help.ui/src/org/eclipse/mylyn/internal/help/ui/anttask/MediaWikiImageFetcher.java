/*******************************************************************************
 * Copyright (c) 2004, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.help.ui.anttask;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.taskdefs.Get;

/**
 * @author David Green
 */
public class MediaWikiImageFetcher extends Task {

	private String base;

	private File dest;

	private File src;

	@Override
	public void execute() throws BuildException {
		if (dest == null) {
			throw new BuildException("Must specify @dest");
		}
		if (!dest.exists()) {
			throw new BuildException("@dest does not exist: " + dest);
		}
		if (!dest.isDirectory()) {
			throw new BuildException("@dest is not a directory: " + dest);
		}
		if (src == null) {
			throw new BuildException("Must specify @src");
		}
		if (!src.exists()) {
			throw new BuildException("@src does not exist: " + src);
		}
		if (!src.isFile()) {
			throw new BuildException("@src is not a file: " + src);
		}
		if (base == null) {
			throw new BuildException("Must specify @base");
		}
		if (base.endsWith("/")) {
			base = base.substring(0, base.length() - 1);
		}
		Pattern fragmentUrlPattern = Pattern.compile("src=\"([^\"]+)\"");
		Pattern imagePattern = Pattern.compile("alt=\"Image:(.*)\"([^>]+)", Pattern.MULTILINE);
		String htmlSrc;
		try {
			htmlSrc = readSrc();
		} catch (IOException e) {
			throw new BuildException("Cannot read src: " + src + ": " + e.getMessage(), e);
		}
		log("Parsing " + src, Project.MSG_INFO);
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
					log("Fetching " + qualifiedUrl, Project.MSG_INFO);
					Get get = new Get();
					get.setProject(getProject());
					get.setLocation(getLocation());
					try {
						get.setSrc(new URL(qualifiedUrl));
					} catch (MalformedURLException e) {
						log("Skipping " + url + ": " + e.getMessage(), Project.MSG_WARN);
						continue;
					}
					// note: we use the alt text for the name since for some files there is a case-difference between
					//       the server URL and the text used in the image src of the markup
					String name = alt == null ? url.substring(url.lastIndexOf('/')) : alt;
					get.setDest(new File(dest, name));
					get.execute();
					++fileCount;
				}
			}
		}
		log("Fetched " + fileCount + " image files for " + src, Project.MSG_INFO);
	}

	public String getBase() {
		return base;
	}

	public File getDest() {
		return dest;
	}

	public File getSrc() {
		return src;
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

	public void setBase(String base) {
		this.base = base;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	public void setSrc(File src) {
		this.src = src;
	}

}
