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

package org.eclipse.mylyn.jenkins.tests.client;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsException;
import org.eclipse.mylyn.internal.jenkins.core.client.JenkinsUrlUtil;
import org.eclipse.mylyn.internal.jenkins.core.client.RestfulJenkinsClient;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsFixture;
import org.eclipse.mylyn.jenkins.tests.support.JenkinsHarness;
import org.junit.Assert;

import junit.framework.TestCase;

/**
 * @author Christian Gaege
 */
public class JenkinsUrlUtilTest extends TestCase {

	private JenkinsUrlUtil jenkinsUrlUtil;

	private JenkinsHarness harness;

	private RepositoryLocation repositoryLocation;

	@Override
	protected void setUp() throws Exception {
		harness = JenkinsFixture.current().createHarness();
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		repositoryLocation = client.getLocation();

		jenkinsUrlUtil = new JenkinsUrlUtil(repositoryLocation);
	}

	public void testBaseUrl() {

		Assert.assertEquals(repositoryLocation.getUrl(), jenkinsUrlUtil.baseUrl());
	}

	public void testAssembleJobUrl() throws JenkinsException {

		String jobName = harness.getPlanNestedTwo();
		String folderUrl = repositoryLocation.getUrl();
		String expectedJobUrl = folderUrl + "job/" + jobName + "/";

		Assert.assertEquals(expectedJobUrl, jenkinsUrlUtil.assembleJobUrl(jobName, folderUrl));
	}

	public void testAssembleJobUrlWithWhitespace() throws JenkinsException, URISyntaxException {

		String jobName = harness.getPlanWhitespace();
		String folderUrl = repositoryLocation.getUrl();
		String encodedJobName = new URI(null, jobName, null).toASCIIString();
		String expectedJobUrl = folderUrl + "job/" + encodedJobName + "/";

		Assert.assertEquals(expectedJobUrl, jenkinsUrlUtil.assembleJobUrl(jobName, folderUrl));
	}

	public void testGroupJobNamesByFolderUrlWithoutJobUrls() throws JenkinsException {

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil
				.groupJobNamesByFolderUrl(new ArrayList<>());

		Assert.assertNotNull(jobNamesByFolderUrl);
		Assert.assertTrue(jobNamesByFolderUrl.isEmpty());
	}

	public void testGroupJobNameByFolderUrlWithJobUrlIds() throws JenkinsException {

		String baseUrl = repositoryLocation.getUrl();
		String folderUrl = baseUrl + "job/test-folder/";
		String subFolderUrl = folderUrl + "job/test-sub-folder/";

		List<String> jobIds = new ArrayList<>();
		jobIds.add(baseUrl + "job/test-succeeding/");
		jobIds.add(baseUrl + "job/test-white%20space/");
		jobIds.add(folderUrl + "job/test-nested-one/");
		jobIds.add(subFolderUrl + "job/test-nested-two/");

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(jobIds);

		Assert.assertNotNull(jobNamesByFolderUrl);
		Assert.assertEquals(3, jobNamesByFolderUrl.size());

		Assert.assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		Assert.assertNotNull(baseUrlJobs);
		Assert.assertTrue(baseUrlJobs.contains("test-succeeding"));
		Assert.assertTrue(baseUrlJobs.contains("test-white space"));

		Assert.assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> folderUrlJobs = jobNamesByFolderUrl.get(folderUrl);
		Assert.assertNotNull(folderUrlJobs);
		Assert.assertTrue(folderUrlJobs.contains("test-nested-one"));

		Assert.assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> subFolderUrlJobs = jobNamesByFolderUrl.get(subFolderUrl);
		Assert.assertNotNull(subFolderUrlJobs);
		Assert.assertTrue(subFolderUrlJobs.contains("test-nested-two"));
	}

	public void testGroupJobNameByFolderUrlWithJobNameIds() throws JenkinsException {

		String baseUrl = repositoryLocation.getUrl();

		List<String> jobIds = new ArrayList<>();
		jobIds.add(harness.getPlanGit());
		jobIds.add(harness.getPlanWhitespace());

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(jobIds);

		Assert.assertNotNull(jobNamesByFolderUrl);
		Assert.assertEquals(1, jobNamesByFolderUrl.size());
		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		Assert.assertNotNull(baseUrlJobs);
		Assert.assertTrue(baseUrlJobs.contains(harness.getPlanGit()));
		Assert.assertTrue(baseUrlJobs.contains(harness.getPlanWhitespace()));
	}

	public void testGroupJobNameByFolderUrlWithJobNameIdsAndJobUrlIds() throws JenkinsException {

		String baseUrl = repositoryLocation.getUrl();
		String folderUrl = baseUrl + "job/test-folder/";

		List<String> jobIds = new ArrayList<>();
		jobIds.add(harness.getPlanGit());
		jobIds.add(harness.getPlanWhitespace());
		jobIds.add(baseUrl + "job/test-succeeding/");
		jobIds.add(folderUrl + "job/test-nested-one/");

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(jobIds);

		Assert.assertNotNull(jobNamesByFolderUrl);
		Assert.assertEquals(2, jobNamesByFolderUrl.size());

		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		Assert.assertNotNull(baseUrlJobs);
		Assert.assertTrue(baseUrlJobs.contains(harness.getPlanGit()));
		Assert.assertTrue(baseUrlJobs.contains(harness.getPlanWhitespace()));
		Assert.assertTrue(baseUrlJobs.contains("test-succeeding"));

		List<String> folderUrlJobs = jobNamesByFolderUrl.get(folderUrl);
		Assert.assertNotNull(folderUrlJobs);
		Assert.assertTrue(folderUrlJobs.contains("test-nested-one"));
	}

	public void testGetJobUrlFromJobIdWithJobName() throws JenkinsException {

		String jobId = harness.getPlanGit();
		String expectedJobUrl = repositoryLocation.getUrl() + "job/" + jobId + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		Assert.assertEquals(expectedJobUrl, jobUrl);
	}

	public void testGetJobUrlFromJobIdWithJobUrl() throws JenkinsException {

		String jobId = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		Assert.assertEquals(jobId, jobUrl);
	}

	public void testGetJobUrlFromJobIdWithNestedJobUrl() throws JenkinsException {

		String jobId = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		Assert.assertEquals(jobId, jobUrl);
	}

	public void testIsNestedJobWithNestedJob() {

		String nestedJobId = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";

		Assert.assertTrue(jenkinsUrlUtil.isNestedJob(nestedJobId));
	}

	public void testIsNestedJobWithTopLevelJob() {

		String topLevelJobId = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";

		Assert.assertFalse(jenkinsUrlUtil.isNestedJob(topLevelJobId));
	}

	public void testGetDisplayNameWithTopLevelJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";
		String expectedDisplayName = harness.getPlanSucceeding();

		Assert.assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	public void testGetDisplayNameWithNestedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";
		String expectedDisplayName = harness.getPlanFolder() + "/" + harness.getPlanNestedOne();

		Assert.assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	public void testGetDisplayNameWithJobNamedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/job/";
		String expectedDisplayName = "job";

		Assert.assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	public void testGetDisplayNameWithFolderAndJobNamedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/job/job/job/";
		String expectedDisplayName = "job/job";

		Assert.assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	public void testGetDisplayNameWithWhitespace() throws URISyntaxException, JenkinsException {

		String jobUrl = new URI(null, repositoryLocation.getUrl() + "job/" + harness.getPlanWhitespace() + "/", null)
				.toASCIIString();
		String expectedDisplayName = harness.getPlanWhitespace();

		Assert.assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(jobUrl));
	}
}
