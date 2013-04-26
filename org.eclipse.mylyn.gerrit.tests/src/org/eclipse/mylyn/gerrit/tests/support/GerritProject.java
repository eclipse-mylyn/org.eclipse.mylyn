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
import org.eclipse.jgit.api.CommitCommand;
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
				project.commitAndPushFile("test");
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

	public Git getGitProject() throws Exception {
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

	public class CommitResult {
		public RevCommit commit;

		public PushResult push;

		private CommitResult(RevCommit commit, PushResult result) {
			this.commit = commit;
			this.push = result;
		}
	}

	public CommitResult commitAndPushFile(String message) throws Exception {
		return commitAndPushFile(message, "test.txt");
	}

	public CommitResult commitAndPushFile(String message, String fileName) throws Exception {
		addFile(fileName);
		Git git = getGitProject();
		return commitAndPush(git.commit().setAll(true).setInsertChangeId(true).setMessage(message));
	}

	public void addFile(String fileName) throws Exception {
		CommonTestUtil.write(new File(folder, fileName).getAbsolutePath(), new StringBuffer("test")); //$NON-NLS-1$ 
		getGitProject().add().addFilepattern(".").call();
	}

	public void addFile(String fileName, String text) throws Exception {
		CommonTestUtil.write(new File(folder, fileName).getAbsolutePath(), new StringBuffer(text));
		getGitProject().add().addFilepattern(".").call();
	}

	public CommitResult commitAndPush(CommitCommand command) throws Exception {
		String email = registerAuthenticator();
		RevCommit call = command.setAuthor("Test", email) //$NON-NLS-1$
				.setCommitter("Test", email)
				.call();
		Iterable<PushResult> result = git.push().setRefSpecs(new RefSpec("HEAD:refs/for/master")).call(); //$NON-NLS-1$
		//Safe to assume one and only one result?
		return new CommitResult(call, result.iterator().next());
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
