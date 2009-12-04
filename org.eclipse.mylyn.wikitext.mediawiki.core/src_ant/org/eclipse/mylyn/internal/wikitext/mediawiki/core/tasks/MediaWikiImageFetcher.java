/*******************************************************************************
 * Copyright (c) 2004, 2009 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.internal.wikitext.mediawiki.core.tasks;

import java.io.File;
import java.net.URL;

import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.Task;

/**
 * Fetch images from a MediaWiki-generated HTML page source. Usage:
 * 
 * <pre>
 * &lt;mediawiki-fetch-images dest="tmp" url="http://wiki.eclipse.org/" pageName="Mylyn/User_Guide"/&gt;
 * </pre>
 * 
 * @author David Green
 */
public class MediaWikiImageFetcher extends Task {

	private String base;

	private File dest;

	private File src;

	private URL url;

	private String pageName;

	@Override
	public void execute() throws BuildException {
		if (dest == null) {
			throw new BuildException("Must specify @dest"); //$NON-NLS-1$
		}
		if (!dest.exists()) {
			throw new BuildException("@dest does not exist: " + dest); //$NON-NLS-1$
		}
		if (!dest.isDirectory()) {
			throw new BuildException("@dest is not a directory: " + dest); //$NON-NLS-1$
		}
		if (src == null && url == null) {
			throw new BuildException("Must specify @src or @url"); //$NON-NLS-1$
		}
		ImageFetchingStrategy strategy;
		if (src != null) {
			log("@src is deprecated, please use @url instead", Project.MSG_WARN); //$NON-NLS-1$
			HtmlSourceImageFetchingStrategy srcStrategy = new HtmlSourceImageFetchingStrategy();
			srcStrategy.setBase(base);
			srcStrategy.setSrc(src);
			strategy = srcStrategy;
		} else {
			if (base != null) {
				throw new BuildException("When @url is specified @base cannot be specified"); //$NON-NLS-1$
			}
			MediaWikiApiImageFetchingStrategy apiStrategy = new MediaWikiApiImageFetchingStrategy();
			apiStrategy.setUrl(url);
			apiStrategy.setPageName(pageName);
			strategy = apiStrategy;
		}
		strategy.setDest(dest);
		strategy.setTask(this);
		strategy.fetchImages();
	}

	/**
	 * @deprecated use {@link #getUrl() url strategy} with {@link #getPageName() page name} instead
	 */
	@Deprecated
	public String getBase() {
		return base;
	}

	public File getDest() {
		return dest;
	}

	/**
	 * @deprecated use {@link #getUrl() url strategy} with {@link #getPageName() page name} instead
	 */
	@Deprecated
	public File getSrc() {
		return src;
	}

	/**
	 * @deprecated use {@link #getUrl() url strategy} with {@link #getPageName() page name} instead
	 */
	@Deprecated
	public void setBase(String base) {
		this.base = base;
	}

	public void setDest(File dest) {
		this.dest = dest;
	}

	/**
	 * @deprecated use {@link #getUrl() url strategy} with {@link #getPageName() page name} instead
	 */
	@Deprecated
	public void setSrc(File src) {
		this.src = src;
	}

	/**
	 * The URL of the wiki, for example: http://wiki.eclipse.org
	 * 
	 * @see #getPageName()
	 */
	public URL getUrl() {
		return url;
	}

	/**
	 * The URL of the wiki, for example: http://wiki.eclipse.org
	 * 
	 * @see #getPageName()
	 */
	public void setUrl(URL url) {
		this.url = url;
	}

	/**
	 * The name of the wiki page, for example "Mylyn/User_Guide"
	 */
	public String getPageName() {
		return pageName;
	}

	/**
	 * The name of the wiki page, for example "Mylyn/User_Guide"
	 */
	public void setPageName(String pageName) {
		this.pageName = pageName;
	}
}
