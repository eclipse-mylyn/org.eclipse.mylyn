/*******************************************************************************
 * Copyright (c) 2009 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.bugzilla.tests.support;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.mylyn.bugzilla.tests.BugzillaTestConstants;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClientManager;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.osgi.framework.Bundle;

/**
 * @author Steffen Pingel
 */
public class BugzillaFixture extends TestFixture {

	private static BugzillaFixture current;

	public static BugzillaFixture BUGS_2_18 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_218_URL,//
			"2.18.6", "");

	public static BugzillaFixture BUGS_2_20 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_220_URL, //
			"2.20.7", "");

	public static BugzillaFixture BUGS_2_22 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_222_URL, //
			"2.22.7", "");

	public static BugzillaFixture BUGS_3_0 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_30_URL, //
			"3.0.8", "");

	public static BugzillaFixture BUGS_3_2 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_32_URL, //
			"3.2.4", "");

	public static BugzillaFixture BUGS_3_2_2 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_322_URL, //
			"3.2.2", "");

	public static BugzillaFixture BUGS_3_2_3 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_323_URL, //
			"3.2.3", "");

	public static BugzillaFixture BUGS_3_4 = new BugzillaFixture(BugzillaTestConstants.TEST_BUGZILLA_34_URL, //
			"3.4.1", "");

	public static BugzillaFixture DEFAULT = BUGS_3_4;

	public static final BugzillaFixture[] ALL = new BugzillaFixture[] { BUGS_2_18, BUGS_2_20, BUGS_2_22, BUGS_3_0,
			BUGS_3_2_2, BUGS_3_2_3, BUGS_3_2, BUGS_3_4 };

	private final String version;

	public static void cleanup010() throws Exception {
	}

	public static BugzillaFixture current(BugzillaFixture fixture) {
		if (current == null) {
			current = fixture;
		}
		return current;
	}

	public static BugzillaFixture current() {
		return current(DEFAULT);
	}

	public BugzillaFixture(String url, String version, String info) {
		super(BugzillaCorePlugin.CONNECTOR_KIND, url);
		this.version = version;
		setInfo("Bugzilla " + version + ((info.length() > 0) ? "/" + info : ""));
	}

	@Override
	public BugzillaFixture activate() {
		current = this;
		return this;
	}

	public String getVersion() {
		return version;
	}

	public BugzillaClient client() throws CoreException, IOException {
		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.USER);
		return client(getRepositoryUrl(), credentials.username, credentials.password, "", "", "UTF-8");
	}

	public BugzillaClient client(String hostUrl, String username, String password, String htAuthUser,
			String htAuthPass, String encoding) throws CoreException, IOException {
		TaskRepository taskRepository = new TaskRepository(BugzillaCorePlugin.CONNECTOR_KIND, hostUrl);

		AuthenticationCredentials credentials = new AuthenticationCredentials(username, password);
		taskRepository.setCredentials(AuthenticationType.REPOSITORY, credentials, false);

		AuthenticationCredentials webCredentials = new AuthenticationCredentials(htAuthUser, htAuthPass);
		taskRepository.setCredentials(AuthenticationType.HTTP, webCredentials, false);
		taskRepository.setCharacterEncoding(encoding);

		BugzillaClientManager bugzillaClientManager = new BugzillaClientManager();
		BugzillaClient client = bugzillaClientManager.getClient(taskRepository, null);
		client.getRepositoryConfiguration(new NullProgressMonitor());
		return client;
	}

	public static File getFile(String filename) throws IOException {
		Bundle bundle = Platform.getBundle("org.eclipse.mylyn.bugzilla.tests");
		if (bundle != null) {
			URL localURL = FileLocator.toFileURL(bundle.getEntry(filename));
			filename = localURL.getFile();
		} else {
			URL localURL = BugzillaFixture.class.getResource("");
			filename = localURL.getFile() + "../../../../../../../" + filename;
		}
		return new File(filename);
	}

}
