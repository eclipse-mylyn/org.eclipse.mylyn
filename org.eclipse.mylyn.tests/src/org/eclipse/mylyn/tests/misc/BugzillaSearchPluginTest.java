/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/
/*
 * Created on Nov 19, 2004
 */
package org.eclipse.mylar.tests.misc;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylar.internal.bugs.MylarBugsPlugin;
import org.eclipse.mylar.internal.bugs.search.BugzillaMylarSearch;
import org.eclipse.mylar.internal.bugzilla.core.BugzillaPlugin;
import org.eclipse.mylar.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaReportNode;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.internal.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.internal.core.search.IActiveSearchListener;
import org.eclipse.mylar.internal.tasklist.TaskCategory;
import org.eclipse.mylar.internal.tasklist.TaskListManager;
import org.eclipse.mylar.java.tests.search.SearchPluginTestHelper;
import org.eclipse.mylar.java.tests.search.WorkspaceSetupHelper;
import org.eclipse.mylar.tasklist.MylarTaskListPlugin;
import org.eclipse.mylar.tasklist.TaskRepository;

/*
 * TEST CASES TO HANDLE 1. what is here 2. different scopes ( local and remote )
 * 3. no bugs 4. offline bugs
 * 
 * DEGREE OF SEPARATIONS 1 Local bug, qualified reference 2 local bug,
 * unqualified reference 3 remote bug, qualified reference 4 remote bug,
 * unqualified reference 5 NONE
 */

/**
 * Test the bugzilla search functionality of the bridge
 * 
 * @author Shawn Minto
 */
public class BugzillaSearchPluginTest extends TestCase {

	private TaskRepository repository;

	// SHAWNTODO Add tests for the different types of searches (local qual,
	// local unqual, fully qual, unqual) and mock up a bugs db for testing

	/** The expected number of results when searching for astNode */
	// SHAWNTODO add back in when we have a test server mocked up
	// private static final int NUM_AST_RESULTS = 302;
	//	
	// private static final int NUM_AST_SETSOURCERANGE_RESULTS = 15;
	/** list to add collectors to when notified */
	private List<List<?>> lists = new ArrayList<List<?>>();

	private IType astNodeType;

	@Override
	protected void setUp() throws Exception {
		WorkspaceSetupHelper.setupWorkspace();
		repository = new TaskRepository(BugzillaPlugin.REPOSITORY_KIND,
				new URL(IBugzillaConstants.ECLIPSE_BUGZILLA_URL));
		MylarTaskListPlugin.getRepositoryManager().addRepository(repository);

		IJavaProject jp = WorkspaceSetupHelper.getJdtCoreDomProject();
		astNodeType = WorkspaceSetupHelper.getType(jp, "org.eclipse.jdt.core.dom.ASTNode");
	}

	@Override
	protected void tearDown() throws Exception {
		WorkspaceSetupHelper.clearDoiModel();
		MylarTaskListPlugin.getRepositoryManager().removeRepository(repository);
	}

	/**
	 * Test adding and removing ISearchCompletedListeners
	 */
	public void testSearchCompletedListenerAddAndRemove() {
		lists.clear();

		// create 2 listeners
		IActiveSearchListener l1 = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> l) {
				lists.add(l);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};
		IActiveSearchListener l2 = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> l) {
				lists.add(l);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		// add the first listener
		s.addListener(l1);
		// remove the first listener
		s.removeListener(l1);

		// perform the search
		SearchPluginTestHelper.search(s, l2);

		// make sure that only the second listener added has any results left
		assertTrue("listener was not removed", lists.size() >= 1 && !l1.resultsGathered());
		assertTrue("listener was not added", lists.size() == 1);

		// display the time it took for the search
		MylarBugsPlugin.getBridge().removeFromLandmarksHash(astNodeType);
	}

	/**
	 * Tests that the bridge gets the right data for us This test is wierd
	 * because it waits on results.
	 */
	public void testBridge() {
		lists.clear();
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// make sure we got the right number of bugs back
		assertTrue("No collector returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertTrue("Results not the right size", c.size() > 0); // TODO should
																// be
																// assertEquals
																// on expected
																// size

		// display the time it took for the search and the results returned
		MylarBugsPlugin.getBridge().removeFromLandmarksHash(astNodeType);

	}

	/**
	 * Tests that the bridge saves the results of a search so that it can be
	 * used later
	 */
	public void testSaveResults() {
		lists.clear();
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertTrue("Results not the right size", c.size() > 0); // TODO should
																// be
																// assertEquals
																// on expected
																// size

		// check that the search has been saved
		List<BugzillaReportNode> saved = MylarBugsPlugin.getBridge().getFromLandmarksHash(astNodeType,
				BugzillaMylarSearch.UNQUAL);
		assertTrue("Results not cached", saved != null);
		assertTrue("Results not the right size", saved.size() > 0); // TODO
																	// should be
																	// assertEquals
																	// on
																	// expected
																	// size

		assertTrue(c.containsAll(saved) && saved.containsAll(c));
		MylarBugsPlugin.getBridge().removeFromLandmarksHash(astNodeType);
	}

	public void testLocalBugUnqual() throws InterruptedException {
		lists.clear();

		String bugPrefix = "<server>-";

		// TaskList t = MylarTaskListPlugin.getTaskListManager().getTaskList();
		// MylarTaskListPlugin.getTaskListManager().setTaskList(t);
		TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.addCategory(cat);
		BugzillaTask bugTask1 = new BugzillaTask(bugPrefix + 94185, "<bugzilla info>", true);

		manager.moveToCategory(cat, bugTask1);
		// cat.addTask(bugTask1);
		while (bugTask1.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}
		BugzillaTask bugTask2 = new BugzillaTask(bugPrefix + 3692, "<bugzilla info>", true);
		manager.moveToCategory(cat, bugTask2);
		// cat.addTask(bugTask2);
		while (bugTask2.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}
		BugzillaTask bugTask3 = new BugzillaTask(bugPrefix + 3693, "<bugzilla info>", true);
		manager.moveToCategory(cat, bugTask3);
		// cat.addTask(bugTask3);
		while (bugTask3.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}

		BugzillaTask bugTask4 = new BugzillaTask(bugPrefix + 9583, "<bugzilla info>", true);
		manager.moveToCategory(cat, bugTask4);
		// cat.addTask(bugTask4);
		while (bugTask4.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}

		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.LOCAL_UNQUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 3, c.size());

		MylarBugsPlugin.getBridge().removeFromLandmarksHash(astNodeType);
		MylarTaskListPlugin.getTaskListManager().deleteCategory(cat);
	}

	// TODO need to test a bug that wraps...should fail since we can only search
	// on a single line
	public void testLocalBugFullyQual() throws InterruptedException {
		lists.clear();

		String bugPrefix = "Bugzilla-";

		// TaskList t = MylarTaskListPlugin.getTaskListManager().getTaskList();
		// MylarTaskListPlugin.getTaskListManager().setTaskList(t);
		TaskListManager manager = MylarTaskListPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.addCategory(cat);
		BugzillaTask bugTask1 = new BugzillaTask(bugPrefix + 94185, "<bugzilla info>", true, true);
		manager.moveToCategory(cat, bugTask1);
		// cat.addTask(bugTask1);
		while (bugTask1.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}

		BugzillaTask bugTask2 = new BugzillaTask(bugPrefix + 9583, "<bugzilla info>", true, true);
		manager.moveToCategory(cat, bugTask2);
		// cat.addTask(bugTask2);
		while (bugTask2.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}
		BugzillaTask bugTask3 = new BugzillaTask(bugPrefix + 3693, "<bugzilla info>", true, true);
		manager.moveToCategory(cat, bugTask3);
		// cat.addTask(bugTask3);
		while (bugTask3.getState() != BugTaskState.FREE) {
			Thread.sleep(500);
		}

		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.LOCAL_QUAL, astNodeType,
				IBugzillaConstants.ECLIPSE_BUGZILLA_URL);

		IActiveSearchListener l = new IActiveSearchListener() {
			private boolean gathered = false;

			public void searchCompleted(List<?> results) {
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};

		// perform the search
		SearchPluginTestHelper.search(s, l);

		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 1, c.size());

		MylarBugsPlugin.getBridge().removeFromLandmarksHash(astNodeType);
		MylarTaskListPlugin.getTaskListManager().deleteCategory(cat);
	}

}
