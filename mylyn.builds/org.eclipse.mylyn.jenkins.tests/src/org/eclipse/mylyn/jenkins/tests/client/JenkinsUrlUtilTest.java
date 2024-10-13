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
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.client;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * @author Christian Gaege
 */
@SuppressWarnings("nls")
public class JenkinsUrlUtilTest {

	private JenkinsUrlUtil jenkinsUrlUtil;

	private JenkinsHarness harness;

	private RepositoryLocation repositoryLocation;

	@BeforeEach
	public void setUp() throws Exception {
		harness = JenkinsFixture.current().createHarness();
		RestfulJenkinsClient client = harness.connect(PrivilegeLevel.ANONYMOUS);
		repositoryLocation = client.getLocation();

		jenkinsUrlUtil = new JenkinsUrlUtil(repositoryLocation);
	}

	@Test
	public void testBaseUrl() {

		assertEquals(repositoryLocation.getUrl(), jenkinsUrlUtil.baseUrl());
	}

	@Test
	public void testAssembleJobUrl() throws JenkinsException {

		String jobName = harness.getPlanNestedTwo();
		String folderUrl = repositoryLocation.getUrl();
		String expectedJobUrl = folderUrl + "job/" + jobName + "/";

		assertEquals(expectedJobUrl, jenkinsUrlUtil.assembleJobUrl(jobName, folderUrl));
	}

	@Test
	public void testAssembleJobUrlWithWhitespace() throws JenkinsException, URISyntaxException {

		String jobName = harness.getPlanWhitespace();
		String folderUrl = repositoryLocation.getUrl();
		String encodedJobName = new URI(null, jobName, null).toASCIIString();
		String expectedJobUrl = folderUrl + "job/" + encodedJobName + "/";

		assertEquals(expectedJobUrl, jenkinsUrlUtil.assembleJobUrl(jobName, folderUrl));
	}

	@Test
	public void testGroupJobNamesByFolderUrlWithoutJobUrls() throws JenkinsException {

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil
				.groupJobNamesByFolderUrl(new ArrayList<>());

		assertNotNull(jobNamesByFolderUrl);
		assertTrue(jobNamesByFolderUrl.isEmpty());
	}

	@Test
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

		assertNotNull(jobNamesByFolderUrl);
		assertEquals(3, jobNamesByFolderUrl.size());

		assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		assertNotNull(baseUrlJobs);
		assertTrue(baseUrlJobs.contains("test-succeeding"));
		assertTrue(baseUrlJobs.contains("test-white space"));

		assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> folderUrlJobs = jobNamesByFolderUrl.get(folderUrl);
		assertNotNull(folderUrlJobs);
		assertTrue(folderUrlJobs.contains("test-nested-one"));

		assertTrue(jobNamesByFolderUrl.containsKey(baseUrl));
		List<String> subFolderUrlJobs = jobNamesByFolderUrl.get(subFolderUrl);
		assertNotNull(subFolderUrlJobs);
		assertTrue(subFolderUrlJobs.contains("test-nested-two"));
	}

	@Test
	public void testGroupJobNameByFolderUrlWithJobNameIds() throws JenkinsException {

		String baseUrl = repositoryLocation.getUrl();

		List<String> jobIds = new ArrayList<>();
		jobIds.add(harness.getPlanGit());
		jobIds.add(harness.getPlanWhitespace());

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(jobIds);

		assertNotNull(jobNamesByFolderUrl);
		assertEquals(1, jobNamesByFolderUrl.size());
		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		assertNotNull(baseUrlJobs);
		assertTrue(baseUrlJobs.contains(harness.getPlanGit()));
		assertTrue(baseUrlJobs.contains(harness.getPlanWhitespace()));
	}

	@Test
	public void testGroupJobNameByFolderUrlWithJobNameIdsAndJobUrlIds() throws JenkinsException {

		String baseUrl = repositoryLocation.getUrl();
		String folderUrl = baseUrl + "job/test-folder/";

		List<String> jobIds = new ArrayList<>();
		jobIds.add(harness.getPlanGit());
		jobIds.add(harness.getPlanWhitespace());
		jobIds.add(baseUrl + "job/test-succeeding/");
		jobIds.add(folderUrl + "job/test-nested-one/");

		Map<String, List<String>> jobNamesByFolderUrl = jenkinsUrlUtil.groupJobNamesByFolderUrl(jobIds);

		assertNotNull(jobNamesByFolderUrl);
		assertEquals(2, jobNamesByFolderUrl.size());

		List<String> baseUrlJobs = jobNamesByFolderUrl.get(baseUrl);
		assertNotNull(baseUrlJobs);
		assertTrue(baseUrlJobs.contains(harness.getPlanGit()));
		assertTrue(baseUrlJobs.contains(harness.getPlanWhitespace()));
		assertTrue(baseUrlJobs.contains("test-succeeding"));

		List<String> folderUrlJobs = jobNamesByFolderUrl.get(folderUrl);
		assertNotNull(folderUrlJobs);
		assertTrue(folderUrlJobs.contains("test-nested-one"));
	}

	@Test
	public void testGetJobUrlFromJobIdWithJobName() throws JenkinsException {

		String jobId = harness.getPlanGit();
		String expectedJobUrl = repositoryLocation.getUrl() + "job/" + jobId + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		assertEquals(expectedJobUrl, jobUrl);
	}

	@Test
	public void testGetJobUrlFromJobIdWithJobUrl() throws JenkinsException {

		String jobId = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		assertEquals(jobId, jobUrl);
	}

	@Test
	public void testGetJobUrlFromJobIdWithNestedJobUrl() throws JenkinsException {

		String jobId = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";

		String jobUrl = jenkinsUrlUtil.getJobUrlFromJobId(jobId);

		assertEquals(jobId, jobUrl);
	}

	@Test
	public void testIsNestedJobWithNestedJob() {

		String nestedJobId = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";

		assertTrue(jenkinsUrlUtil.isNestedJob(nestedJobId));
	}

	@Test
	public void testIsNestedJobWithTopLevelJob() {

		String topLevelJobId = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";

		assertFalse(jenkinsUrlUtil.isNestedJob(topLevelJobId));
	}

	@Test
	public void testGetDisplayNameWithTopLevelJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/" + harness.getPlanSucceeding() + "/";
		String expectedDisplayName = harness.getPlanSucceeding();

		assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	@Test
	public void testGetDisplayNameWithNestedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/" + harness.getPlanFolder() + "/job/"
				+ harness.getPlanNestedOne() + "/";
		String expectedDisplayName = harness.getPlanFolder() + "/" + harness.getPlanNestedOne();

		assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	@Test
	public void testGetDisplayNameWithJobNamedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/job/";
		String expectedDisplayName = "job";

		assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	@Test
	public void testGetDisplayNameWithFolderAndJobNamedJob() throws JenkinsException {

		String nestedJobUrl = repositoryLocation.getUrl() + "job/job/job/job/";
		String expectedDisplayName = "job/job";

		assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(nestedJobUrl));
	}

	@Test
	public void testGetDisplayNameWithWhitespace() throws URISyntaxException, JenkinsException {

		String jobUrl = new URI(null, repositoryLocation.getUrl() + "job/" + harness.getPlanWhitespace() + "/", null)
				.toASCIIString();
		String expectedDisplayName = harness.getPlanWhitespace();

		assertEquals(expectedDisplayName, jenkinsUrlUtil.getDisplayName(jobUrl));
	}
}
