/*******************************************************************************
 * Copyright (c) 2015, 2016 Christian Gaege and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Christian Gaege - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.jenkins.core.client;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;

/**
 * Utility class for working with job URLs
 *
 * @author Christian Gaege
 */
public class JenkinsUrlUtil {

	private final RepositoryLocation repositoryLocation;

	private final Pattern jobNamePattern = Pattern.compile("[^/]+/$"); //$NON-NLS-1$

	private final Pattern jobUrlPattern = Pattern.compile("job/[^/]+/$"); //$NON-NLS-1$

	private final static String SLASH = "/"; //$NON-NLS-1$

	public JenkinsUrlUtil(RepositoryLocation repositoryLocation) {

		this.repositoryLocation = repositoryLocation;
	}

	/**
	 * Assembles a job URL for the given jobName and folderUrl
	 *
	 * @param jobName
	 * @param folderUrl
	 * @return jobUrl
	 * @throws JenkinsException
	 */
	public String assembleJobUrl(String jobName, String folderUrl) throws JenkinsException {

		String encodedJobname;

		try {
			encodedJobname = new URI(null, jobName, null).toASCIIString();
		} catch (URISyntaxException e) {
			throw new JenkinsException(e);
		}

		if (!folderUrl.endsWith(SLASH)) {
			folderUrl += SLASH;
		}
		if (!encodedJobname.endsWith(SLASH)) {
			encodedJobname += SLASH;
		}
		return folderUrl + "job" + SLASH + encodedJobname; //$NON-NLS-1$
	}

	/**
	 * Converts the given list of jobIds into a Map with job folder URL as key and list of job names within the folder
	 * as value
	 *
	 * @param jobIds
	 * @return map with folderUrl as key and list of job names as value
	 * @throws JenkinsException
	 */
	public Map<String, List<String>> groupJobNamesByFolderUrl(List<String> jobIds) throws JenkinsException {

		Map<String, List<String>> jobNamesByFolderUrl = new HashMap<String, List<String>>();

		for (String jobId : jobIds) {

			String folderUrl = this.getFolderUrlFromJobId(jobId);
			String jobName = getJobNameFromJobId(jobId);

			if (jobNamesByFolderUrl.containsKey(folderUrl)) {

				jobNamesByFolderUrl.get(folderUrl).add(jobName);

			} else {

				List<String> jobNames = new ArrayList<String>();
				jobNames.add(jobName);
				jobNamesByFolderUrl.put(folderUrl, jobNames);
			}
		}

		return jobNamesByFolderUrl;
	}

	/**
	 * Extracts the jobName from the job identified by the given jobId
	 *
	 * @param jobId
	 * @return jobName
	 * @throws JenkinsException
	 */
	private String getJobNameFromJobId(String jobId) throws JenkinsException {

		Matcher matcher = this.jobNamePattern.matcher(jobId);

		if (matcher.find()) {

			String jobName = jobId.substring(matcher.start(), jobId.length());

			try {
				jobName = URLDecoder.decode(jobName, "UTF-8"); //$NON-NLS-1$

			} catch (UnsupportedEncodingException e) {
				throw new JenkinsException(e);
			}

			if (jobName.endsWith(SLASH)) {
				return jobName.substring(0, jobName.length() - 1);
			}

			return jobName;
		}

		return jobId;
	}

	/**
	 * Returns the folder URL of the job identified by the given jobId. Returns the base URL if the given jobId
	 * identifies a top level job
	 *
	 * @param jobId
	 * @return folderUrl
	 */
	private String getFolderUrlFromJobId(String jobId) {

		Matcher matcher = this.jobUrlPattern.matcher(jobId);

		if (matcher.find()) {

			return jobId.substring(0, matcher.start());
		}

		return baseUrl();
	}

	/**
	 * Returns the URL of the job identified by the given jobId
	 *
	 * @param jobId
	 * @return jobUrl
	 * @throws JenkinsException
	 */
	public String getJobUrlFromJobId(String jobId) throws JenkinsException {

		Matcher matcher = this.jobUrlPattern.matcher(jobId);

		if (matcher.find()) {

			return jobId;
		}

		return this.assembleJobUrl(jobId, baseUrl());
	}

	/**
	 * Returns the URL of the repository location
	 *
	 * @return baseUrl
	 */
	public String baseUrl() {

		String url = repositoryLocation.getUrl();
		if (!url.endsWith(SLASH)) {
			url += SLASH;
		}
		return url;
	}

	/**
	 * Determines if the job identified by the given jobId is as nested job or a top level job
	 *
	 * @param jobId
	 * @return true if nested job, otherwise false
	 */
	public boolean isNestedJob(String jobId) {

		return !this.getFolderUrlFromJobId(jobId).equals(baseUrl());
	}

	/**
	 * Returns a unique display name for the given jobURL
	 *
	 * @param jobUrl
	 * @return display name
	 * @throws JenkinsException
	 */
	public String getDisplayName(String jobUrl) throws JenkinsException {

		String displayName = jobUrl.substring(repositoryLocation.getUrl().length(), jobUrl.length());
		if (!displayName.startsWith(SLASH)) {
			displayName = SLASH + displayName;
		}

		displayName = displayName.replaceAll("/job/", SLASH); //$NON-NLS-1$

		try {
			displayName = URLDecoder.decode(displayName, "UTF-8"); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			throw new JenkinsException(e);
		}

		if (displayName.startsWith(SLASH)) {
			displayName = displayName.substring(SLASH.length());
		}

		if (displayName.endsWith(SLASH)) {
			displayName = displayName.substring(0, displayName.length() - SLASH.length());
		}

		return displayName;
	}
}
