package org.eclipse.mylyn.versions.tasks.mapper.internal;
/*******************************************************************************
 * Copyright (c) 2012 Research Group for Industrial Software (INSO), Vienna University of Technology.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Research Group for Industrial Software (INSO), Vienna University of Technology - initial API and implementation
 *******************************************************************************/

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.mylyn.tasks.core.ITask;
import org.eclipse.mylyn.tasks.tests.connector.MockTask;
import org.eclipse.mylyn.versions.core.Change;
import org.eclipse.mylyn.versions.core.ChangeSet;
import org.eclipse.mylyn.versions.core.ScmRepository;
import org.eclipse.mylyn.versions.core.ScmUser;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetCollector;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetIndexer;
import org.eclipse.mylyn.versions.tasks.mapper.generic.IChangeSetSource;
import org.junit.Before;
import org.junit.Test;

/**
 *
 * @author Kilian Matt
 *
 */
@SuppressWarnings("restriction")
public class ChangeSetIndexerTest {

	protected static final String REPO_URL = "http://git.eclipse.org/c/mylyn/org.eclipse.mylyn.versions.git";
	private ChangeSetIndexer indexer;

	@Before
	public void prepareIndex() {
		File dir = createTempDirectoryForIndex();

		indexer = new ChangeSetIndexer(dir, createIndexerSource());
		indexer.reindex(new NullProgressMonitor());
	}

	@Test
	public void testSingleResult() throws CoreException{
		ITask task =  new MockTask(REPO_URL,"1");
		task.setUrl(REPO_URL+"/1");
		ExpectingChangeSetCollector collectors= new ExpectingChangeSetCollector();
		collectors.expect("1", REPO_URL);
		assertEquals(1, indexer.search(task,REPO_URL, 5,collectors));
		collectors.verifyAllExpectations();
	}
	@Test
	public void testMultipleResults() throws CoreException{
		ITask task =  new MockTask(REPO_URL,"2");
		task.setUrl(REPO_URL+"/1");
		ExpectingChangeSetCollector collectors= new ExpectingChangeSetCollector();
		collectors.expect("2", REPO_URL);
		collectors.expect("3", REPO_URL);
		assertEquals(2, indexer.search(task,REPO_URL, 5,collectors));
		collectors.verifyAllExpectations();
	}

	@Test
	public void testFindByTaskUrl() throws CoreException{
		ITask task =  new MockTask(REPO_URL,"4");
		task.setUrl(REPO_URL+"/4");
		ExpectingChangeSetCollector collectors= new ExpectingChangeSetCollector();
		collectors.expect("4", REPO_URL);
		assertEquals(1, indexer.search(task,REPO_URL, 5,collectors));
		collectors.verifyAllExpectations();
	}
	@Test
	public void testComplexTaskKeys() throws CoreException{
		ITask task =  new MockTask(REPO_URL,"2131");
		task.setTaskKey("SPR-9030");
		task.setUrl(REPO_URL+"/1");
		ExpectingChangeSetCollector collectors= new ExpectingChangeSetCollector();
		collectors.expect("5", REPO_URL);
		collectors.expect("6", REPO_URL);
		assertEquals(2, indexer.search(task,REPO_URL, 5,collectors));
		collectors.verifyAllExpectations();
	}

	static class ExpectingChangeSetCollector implements IChangeSetCollector{
		private List<Pair> expected=new LinkedList<Pair>();
		void expect(String revision, String repositoryUrl){
			this.expected.add(new Pair(revision,repositoryUrl));
		}

		public void verifyAllExpectations() {
			if(expected.size()>0){
				fail( expected.size() + " expected changesets not collected");
			}
		}

		private static class Pair{
			Pair(String rev, String repoUrl){
				this.rev=rev;
				this.repoUrl=repoUrl;
			}
			final String rev;
			final String repoUrl;
		}

		@Override
		public void collect(String revision, String repositoryUrl)
				throws CoreException {
			if(expected.size()==0){
				fail("unexpected changeset");
			}
			Pair first = expected.remove(0);
			assertEquals(first.rev, revision);
			assertEquals(first.repoUrl, repositoryUrl);
		}
	}
	private ListChangeSetSource createIndexerSource() {
		ScmRepository repository=new ScmRepository(null, "", REPO_URL);
		ScmRepository otherRepo=new ScmRepository(null, "", "http://git.eclipse.org/c/mylyn/org.eclipse.mylyn.reviews.git");
		ListChangeSetSource source = new ListChangeSetSource(Arrays.asList(
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "1", "commit message 1", repository, new ArrayList<Change>()),
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "1", "commit message 1", otherRepo, new ArrayList<Change>()),
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "2", "commit message 2", repository, new ArrayList<Change>()),
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "3", "commit message 2", repository, new ArrayList<Change>()),
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "4", "another commit message with url http://git.eclipse.org/c/mylyn/org.eclipse.mylyn.versions.git/4 ", repository, new ArrayList<Change>()),

				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "5", "SPR-9030: Test", repository, new ArrayList<Change>()),
				new ChangeSet(new ScmUser("test", "Name", "test@eclipse.org"), new Date(), "6", "Fixed Bug (SPR-9030)", repository, new ArrayList<Change>())
		));
		return source;
	}

	class ListChangeSetSource implements IChangeSetSource {
		private List<ChangeSet> changesets;
		public ListChangeSetSource(List<ChangeSet> changesets){
			this.changesets=changesets;
		}
		@Override
		public void fetchAllChangesets(IProgressMonitor monitor,
				IChangeSetIndexer indexer) throws CoreException {
			for(ChangeSet changeset : changesets){
				indexer.index(changeset);
			}
		}
	}

	private File createTempDirectoryForIndex() {
		File dir = null;
		try {
			dir = File.createTempFile("test","dasd");
			dir.delete();
			dir.mkdir();
			dir.deleteOnExit();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return dir;
	}



}
