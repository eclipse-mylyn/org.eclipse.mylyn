/*******************************************************************************
 * Copyright (c) 2011 SAP and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Sascha Scholz (SAP) - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.gerrit.core.egit;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.matches;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.RepositoryUtil;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.lib.StoredConfig;
import org.eclipse.jgit.transport.URIish;
import org.junit.Test;

/**
 * @author Sascha Scholz
 */
public class GerritProjectToGitRepositoryMappingTest {

	private static final String GERRIT_GIT_HOST = "egit.eclipse.org"; //$NON-NLS-1$

	private static final String GERRIT_PROJECT = "jgit"; //$NON-NLS-1$

	@Test
	public void testFindNoMatchingEmptyList() throws Exception {
		GerritProjectToGitRepositoryMapping mapping = createTestMapping(createRepositories());
		assertNull(mapping.findRepository());
	}

	@Test
	public void testFindNoMatching() throws Exception {
		GerritProjectToGitRepositoryMapping mapping = createTestMapping(createRepositories("project1", "project2")); //$NON-NLS-1$ //$NON-NLS-2$
		assertNull(mapping.findRepository());
	}

	@Test
	public void testFindMatching() throws Exception {
		GerritProjectToGitRepositoryMapping mapping = createTestMapping(createRepositories("project1", GERRIT_PROJECT)); //$NON-NLS-1$ 
		assertNotNull(mapping.findRepository());
	}

	private Repository[] createRepositories(String... projects) {
		List<Repository> repos = new ArrayList<Repository>();
		for (String project : projects) {
			repos.add(createRepository(project));
		}
		return repos.toArray(new Repository[0]);
	}

	private Repository createRepository(String project) {
		StoredConfig config = mock(StoredConfig.class);
		Set<String> configSubSections = new HashSet<String>();
		String remoteName = "remotename"; //$NON-NLS-1$
		configSubSections.add(remoteName);
		String remoteSection = "remote"; //$NON-NLS-1$
		when(config.getSubsections(remoteSection)).thenReturn(configSubSections);
		when(config.getStringList(eq(remoteSection), eq(remoteName), anyString())).thenReturn(new String[0]);
		when(config.getStringList(eq(remoteSection), eq(remoteName), matches("url"))).thenReturn( //$NON-NLS-1$
				new String[] { "git://" + GERRIT_GIT_HOST + "/" + project }); //$NON-NLS-1$//$NON-NLS-2$
		Repository repo = mock(Repository.class);
		when(repo.getConfig()).thenReturn(config);
		return repo;
	}

	private GerritProjectToGitRepositoryMapping createTestMapping(Repository[] repositories) throws Exception {
		List<String> repoDirList = createRepositoryDirList(repositories.length);

		final RepositoryUtil util = mock(RepositoryUtil.class);
		when(util.getConfiguredRepositories()).thenReturn(repoDirList);

		final RepositoryCache cache = mock(RepositoryCache.class);
		for (String dir : repoDirList) {
			when(cache.lookupRepository(new File(dir))).thenReturn(repositories[Integer.parseInt(dir)]);
		}
		return new GerritProjectToGitRepositoryMapping(GERRIT_GIT_HOST, GERRIT_PROJECT) {

			@Override
			RepositoryCache getRepositoryCache() {
				return cache;
			}

			@Override
			RepositoryUtil getRepositoryUtil() {
				return util;
			}

		};
	}

	private List<String> createRepositoryDirList(int length) {
		List<String> repoList = new ArrayList<String>(length);
		for (int i = 0; i < length; i++) {
			repoList.add(new String() + i);
		}
		return repoList;
	}

	@Test
	public void testCalcGit() throws Exception {
		URIish uri = new URIish("git://egit.eclipse.org/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcGitWithDotGitSuffix() throws Exception {
		URIish uri = new URIish("git://egit.eclipse.org/jgit.git"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcGitWithGerritHttpPrefix() throws Exception {
		URIish uri = new URIish("git://egit.eclipse.org/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("p/jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcSsh() throws Exception {
		URIish uri = new URIish("ssh://user@egit.eclipse.org:29418/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttp() throws Exception {
		URIish uri = new URIish("http://egit.eclipse.org/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttps() throws Exception {
		URIish uri = new URIish("https://egit.eclipse.org/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttpWithPort() throws Exception {
		URIish uri = new URIish("http://egit.eclipse.org:8080/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttpWithUser() throws Exception {
		URIish uri = new URIish("http://user@egit.eclipse.org/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttpWithPrefix() throws Exception {
		URIish uri = new URIish("http://egit.eclipse.org/r/p/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttpWithSubProject() throws Exception {
		URIish uri = new URIish("http://egit.eclipse.org/p/jgit/subproj"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit/subproj")); //$NON-NLS-1$
	}

	@Test
	public void testCalcHttpWithoutGerritPrefix() throws Exception {
		URIish uri = new URIish("http://egit.eclipse.org/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

	@Test
	public void testCalcUnknownProtocol() throws Exception {
		URIish uri = new URIish("xyz://user@egit.eclipse.org:29418/jgit"); //$NON-NLS-1$
		assertThat(GerritProjectToGitRepositoryMapping.calcProjectNameFromUri(uri), is("jgit")); //$NON-NLS-1$
	}

}
