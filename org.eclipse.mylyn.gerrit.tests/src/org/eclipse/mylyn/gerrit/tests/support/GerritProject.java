/*******************************************************************************
 * Copyright (c) 2013, 2016 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.gerrit.tests.support;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.eclipse.mylyn.commons.sdk.util.CommonTestUtil.PrivilegeLevel;
import org.junit.Test;

/**
 * @author Steffen Pingel
 */
public class GerritProject {

	public static final String PROP_ALTERNATE_PUSH = "org.eclipse.mylyn.gerrit.tests.alternate.push";

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

	public static final String PROJECT = "org.eclipse.mylyn.test"; //$NON-NLS-1$

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

	private CredentialsProvider getCredentialsProvider(PrivilegeLevel privilegeLevel) throws Exception {
		AuthenticationCredentials credentials = fixture.location(privilegeLevel)
				.getCredentials(AuthenticationType.REPOSITORY);
		return new UsernamePasswordCredentialsProvider(getGitUsername(credentials), credentials.getPassword());
	}

	public String getGitUsername(AuthenticationCredentials credentials) {
		String shortUsername = StringUtils.substringBefore(credentials.getUserName(), "@"); //$NON-NLS-1$
		return shortUsername;
	}

	public Git getGitProject() throws Exception {
		return getGitProject(PrivilegeLevel.USER);
	}

	public Git getGitProject(PrivilegeLevel privilegeLevel) throws Exception {
		if (git == null) {
			String url = fixture.getRepositoryUrl() + PROJECT;
			AuthenticationCredentials credentials = fixture.location(privilegeLevel)
					.getCredentials(AuthenticationType.REPOSITORY);
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
		addFile(fileName, "this is line 1\nhere is another line\nline3");
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
		return commitAndPush(command, "HEAD:refs/for/master", PrivilegeLevel.USER);
	}

	public CommitResult commitAndPush(CommitCommand command, String refSpec, PrivilegeLevel privilegeLevel)
			throws Exception {
		AuthenticationCredentials credentials = registerAuthenticator(privilegeLevel);
		String email = credentials.getUserName();
		RevCommit call = command.setAuthor("Test", email) //$NON-NLS-1$
				.setCommitter("Test", email)
				.call();
		if (Boolean.getBoolean(PROP_ALTERNATE_PUSH)) {
			String username = StringUtils.substringBefore(email, "@");
			String protocol = StringUtils.substringBefore(fixture.getRepositoryUrl(), "://");
			String hostAndPath = StringUtils.substringAfter(fixture.getRepositoryUrl(), "://");
			String project = "org.eclipse.mylyn.test/";
			String url = protocol + "://" + username + ":" + credentials.getPassword() + "@" + hostAndPath + project;
			File directory = command.getRepository().getDirectory().getParentFile();
			final String responseMessage = execute(directory, "git", "push", url, refSpec);
			System.out.println("Response message:");
			System.out.println(responseMessage);
			System.out.println("######");
			PushResult result = new PushResult() {
				@Override
				public String getMessages() {
					return responseMessage.toString();
				}
			};
			return new CommitResult(call, result);
		} else {
			Iterable<PushResult> result = git.push()
					.setCredentialsProvider(getCredentialsProvider(privilegeLevel))
					.setRefSpecs(new RefSpec(refSpec))
					.call();
			//Safe to assume one and only one result?
			return new CommitResult(call, result.iterator().next());
		}
	}

	private String execute(File directory, String... command) throws IOException {
//		System.out.println("# Executing " + StringUtils.join(command, " "));
		Process process = Runtime.getRuntime().exec(command, null, directory);
		BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream()));
		final StringBuilder responseMessage = new StringBuilder();
		try {
			String line;
			while ((line = r.readLine()) != null) {
				if (!line.startsWith("remote:")) {
					line = line.replace('/', '\\');// don't break parsing of short id
				}
				responseMessage.append(line);
				responseMessage.append('\n');
			}
		} finally {
			r.close();
		}
		return responseMessage.toString();
	}

	public AuthenticationCredentials registerAuthenticator(PrivilegeLevel privilegeLevel) throws Exception {
		// register authenticator to avoid HTTP password prompt
		AuthenticationCredentials credentials = fixture.location(privilegeLevel)
				.getCredentials(AuthenticationType.REPOSITORY);
		final PasswordAuthentication authentication = new PasswordAuthentication(credentials.getUserName(),
				credentials.getPassword().toCharArray());
		Authenticator.setDefault(new Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return authentication;
			}
		});
		return credentials;
	}

	public void dispose() {
		if (folder != null) {
			CommonTestUtil.deleteFolderRecursively(folder);
		}
	}
}
