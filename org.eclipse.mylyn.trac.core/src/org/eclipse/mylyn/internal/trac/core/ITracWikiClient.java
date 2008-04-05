/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/

package org.eclipse.mylyn.internal.trac.core;

import java.io.InputStream;
import java.util.Date;
import java.util.Map;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPage;
import org.eclipse.mylyn.internal.trac.core.model.TracWikiPageInfo;

/**
 * Interface for the WikiRPC API provided by the Trac XML-RPC Plugin
 * 
 * @author Xiaoyang Guan
 * @since 2.1
 */
public interface ITracWikiClient {

	/**
	 * Render arbitrary wiki text as HTML
	 * 
	 * @param sourceText
	 *            wiki source text
	 * @param monitor TODO
	 * @return The HTML-formatted string of the wiki text
	 * @throws TracException
	 *             thrown in case of a connection error
	 */
	public String wikiToHtml(String sourceText, IProgressMonitor monitor) throws TracException;

	/**
	 * Validates the Trac XML-RPC WikiRPC API version of the repository
	 * @param monitor TODO
	 * 
	 * @throws TracException
	 */
	public void validateWikiRpcApi(IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the list of the names of all pages from the repository
	 * @param monitor TODO
	 * 
	 * @return The array of the names of all Wiki pages
	 * @throws TracException
	 */
	public String[] getAllWikiPageNames(IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the latest version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return the Wiki page at the latest version
	 * @throws TracException
	 */
	public TracWikiPage getWikiPage(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets a specific version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param version
	 *            the version of the Wiki page
	 * @param monitor TODO
	 * @return the Wiki page at the specified version
	 * @throws TracException
	 */
	public TracWikiPage getWikiPage(String pageName, int version, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the information about the latest version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return The information about the page at the latest version
	 * @throws TracException
	 */
	public TracWikiPageInfo getWikiPageInfo(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the information about the specified version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param version
	 *            the version of the Wiki page
	 * @param monitor TODO
	 * @return The information about the page at the specified version
	 * @throws TracException
	 */
	public TracWikiPageInfo getWikiPageInfo(String pageName, int version, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the information about all versions of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return array of TracWikiPageInfo that contains the information about all versions of the page
	 * @throws TracException
	 */
	public TracWikiPageInfo[] getWikiPageInfoAllVersions(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the raw Wiki text of the latest version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return the raw Wiki text of the page, latest version
	 * @throws TracException
	 */
	public String getWikiPageContent(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the raw Wiki text of the specified version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param version
	 *            the version of the Wiki page
	 * @param monitor TODO
	 * @return the raw Wiki text of the page, specified version
	 * @throws TracException
	 */
	public String getWikiPageContent(String pageName, int version, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the rendered HTML of the latest version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return the rendered HTML of the page, latest version
	 * @throws TracException
	 */
	public String getWikiPageHtml(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the rendered HTML of the specified version of a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param version
	 *            the version of the Wiki page
	 * @param monitor TODO
	 * @return the rendered HTML of the page, specified version
	 * @throws TracException
	 */
	public String getWikiPageHtml(String pageName, int version, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the list of information about all pages that have been modified since a given date from the repository
	 * 
	 * @param since
	 *            the date from which the changes to the Wiki pages should be retrieved
	 * @param monitor TODO
	 * @return array of TracWikiPageInfo that contains the information about the modified pages
	 * @throws TracException
	 */
	public TracWikiPageInfo[] getRecentWikiChanges(Date since, IProgressMonitor monitor) throws TracException;

	/**
	 * Writes the content of a Wiki page to the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param content
	 *            the content of the page to be written
	 * @param attributes
	 *            a Map used to set any Wiki-specific things, which the server can freely ignore or incorporate.
	 *            Standard names are:
	 *            <ul>
	 *            <li> comment (String): A comment for the page.
	 *            <li> minoredit (Boolean): This was a minor edit only.
	 *            </ul>
	 * @param monitor TODO
	 * @return <code>true</code> if successful
	 * @throws TracException
	 */
	public boolean putWikipage(String pageName, String content, Map<String, Object> attributes, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the list of the names of attachments on a given Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param monitor TODO
	 * @return an array of the names of attachments on the given page. Returns an empty array if the page has no
	 *         attachment or the page does not exist.
	 * @throws TracException
	 */
	public String[] listWikiPageAttachments(String pageName, IProgressMonitor monitor) throws TracException;

	/**
	 * Gets the content of an attachment on a Wiki page from the repository
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param fileName
	 *            the name of the attachment file
	 * @param monitor TODO
	 * @return An InputStream of the content of the attachment
	 * @throws TracException
	 */
	public InputStream getWikiPageAttachmentData(String pageName, String fileName, IProgressMonitor monitor) throws TracException;

	/**
	 * Attach a file to a Wiki page on the repository.
	 * <p>
	 * Note: The standard implementation of WikiRPC API for uploading attachments may ignore the description of the
	 * attachment and always use <code>true</code> for <code>replace</code>
	 * 
	 * @param pageName
	 *            the name of the Wiki page
	 * @param fileName
	 *            the name of the file to be attached
	 * @param description
	 *            the description of the attachment
	 * @param in
	 *            An InputStream of the content of the attachment
	 * @param replace
	 *            whether to overwrite an existing attachment with the same filename
	 * @param monitor TODO
	 * @return The (possibly transformed) filename of the attachment. If <code>replace</code> is <code>true</code>,
	 *         the returned name is always the same as the argument <code>fileName</code>; if <code>replace</code>
	 *         is <code>false</code> and an attachment with name <code>fileName</code> already exists, a different
	 *         name is generated for the new attachment by the repository server and the new name is returned.
	 * @throws TracException
	 */
	public String putWikiPageAttachmentData(String pageName, String fileName, String description, InputStream in,
			boolean replace, IProgressMonitor monitor) throws TracException;
}
