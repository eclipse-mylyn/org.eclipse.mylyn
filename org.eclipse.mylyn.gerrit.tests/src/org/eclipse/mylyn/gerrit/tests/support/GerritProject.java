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
import java.io.IOException;
import java.net.Authenticator;
import java.net.PasswordAuthentication;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.transport.CredentialsProvider;
import org.eclipse.jgit.transport.PushResult;
import org.eclipse.jgit.transport.RefSpec;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
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

	public File getFolder() throws IOException {
		if (folder == null) {
			folder = CommonTestUtil.createTempFolder("gerrit"); //$NON-NLS-1$
		}
		return folder;
	}

	private CredentialsProvider getCredentialsProvider() throws Exception {
		AuthenticationCredentials credentials = fixture.location().getCredentials(AuthenticationType.REPOSITORY);
		return new UsernamePasswordCredentialsProvider(getGitUsername(credentials), credentials.getPassword());
	}

	public String getGitUsername(AuthenticationCredentials credentials) {
		String shortUsername = StringUtils.substringBefore(credentials.getUserName(), "@"); //$NON-NLS-1$
		return shortUsername;
	}

	public Git getGitProject() throws Exception {
		if (git == null) {
			String url = fixture.getRepositoryUrl() + PROJECT;
			AuthenticationCredentials credentials = fixture.location().getCredentials(AuthenticationType.REPOSITORY);
			url = url.replace("://", "://" + getGitUsername(credentials) + ":" + credentials.getPassword() + "@"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
			git = Git.cloneRepository().setDirectory(getFolder()).setURI(url).call();
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
		CommitCommand command = getGitProject().commit().setAll(true).setInsertChangeId(true).setMessage(message);
		return commitAndPush(command);
	}

	public void addFile(String fileName) throws Exception {
		addFile(fileName, "test");
	}

	public void addFile(String fileName, String text) throws Exception {
		Git gitProject = getGitProject();
		CommonTestUtil.write(new File(getFolder(), fileName).getAbsolutePath(), new StringBuffer(text));
		gitProject.add().addFilepattern(fileName).call();
	}

	public void addFile(String fileName, File file) throws Exception {
		Git gitProject = getGitProject();
		CommonTestUtil.copy(file, new File(getFolder(), fileName));
		gitProject.add().addFilepattern(fileName).call();
	}

	public void removeFile(String fileName) throws Exception {
		Git gitProject = getGitProject();
		gitProject.rm().addFilepattern(fileName).call();
	}

	public CommitResult commitAndPush(CommitCommand command) throws Exception {
		String email = registerAuthenticator();
		RevCommit call = command.setAuthor("Test", email) //$NON-NLS-1$
				.setCommitter("Test", email)
				.call();
		Iterable<PushResult> result = git.push()
				.setCredentialsProvider(getCredentialsProvider())
				.setRefSpecs(new RefSpec("HEAD:refs/for/master")).call(); //$NON-NLS-1$
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
