/*******************************************************************************
 * Copyright (c) 2013 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import java.io.File;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class GerritProject {

	public static class GerritProjectTest {

		@Test
		public void testCommitAndPushFile() throws Exception {
			GerritProject project = new GerritProject(GerritFixture.current());
			try {
				project.commitAndPushFile();
			} finally {
				System.err.println(project.getFolder());
			}
		}

	}

	private static String PROJECT = "org.eclipse.mylyn.test"; //$NON-NLS-1$

	private File folder;

	private Git git;

	private final GerritFixture fixture;

	public GerritProject(GerritFixture fixture) throws Exception {
		this.fixture = fixture;
	}

	public File getFolder() {
		return folder;
	}

	public Git cloneProject() throws Exception {
		if (git == null) {
			folder = CommonTestUtil.createTempFolder("gerrit"); //$NON-NLS-1$
			String url = fixture.getRepositoryUrl() + PROJECT;
			AuthenticationCredentials credentials = fixture.location().getCredentials(AuthenticationType.REPOSITORY);
			String shortUsername = StringUtils.substringBefore(credentials.getUserName(), "@"); //$NON-NLS-1$
			url = url.replace("://", "://" + shortUsername + ":" + credentials.getPassword() + "@"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			git = Git.cloneRepository().setDirectory(folder).setURI(url).call();
		}
		return git;
	}

	public RevCommit commitAndPushFile() throws Exception {
		String email = registerAuthenticator();

		Git git = cloneProject();
		CommonTestUtil.write(new File(folder, "test.txt").getAbsolutePath(), new StringBuffer("test")); //$NON-NLS-1$ //$NON-NLS-2$
		RevCommit commit = git.commit().setAll(true).setInsertChangeId(true).setAuthor("Test", email) //$NON-NLS-1$
				.setCommitter("Test", email) //$NON-NLS-1$
				.setMessage("Test Commit") //$NON-NLS-1$
				.call();
		Iterable<PushResult> result = git.push().setRefSpecs(new RefSpec("HEAD:refs/for/master")).call(); //$NON-NLS-1$
		for (PushResult pushResult : result) {
			System.err.println(pushResult.getMessages());
		}
		return commit;
	}

	public String registerAuthenticator() throws Exception {
		// register authenticator to avoid HTTP password prompt
		AuthenticationCredentials credentials = fixture.location().getCredentials(AuthenticationType.REPOSITORY);
		final PasswordAuthentication authentication = new PasswordAuthentication(credentials.getUserName(),
				credentials.getPassword().toCharArray());
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		});
		return credentials.getUserName();
	}

	public void dispose() {
		if (folder != null) {
			CommonTestUtil.deleteFolderRecursively(folder);
		}
	}
}
