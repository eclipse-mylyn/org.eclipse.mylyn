/*******************************************************************************
 * Copyright (c) 2004 - 2005 University Of British Columbia and others.
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
package org.eclipse.mylar.tasklist.bugzilla.tests;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.mylar.bugs.MylarBugzillaPlugin;
import org.eclipse.mylar.bugs.search.BugzillaMylarSearch;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaReportNode;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaTask.BugTaskState;
import org.eclipse.mylar.core.search.IActiveSearchListener;
import org.eclipse.mylar.core.tests.support.WorkspaceSetupHelper;
import org.eclipse.mylar.core.tests.support.search.SearchPluginTestHelper;
import org.eclipse.mylar.tasklist.MylarTasklistPlugin;
import org.eclipse.mylar.tasklist.TaskListManager;
import org.eclipse.mylar.tasklist.internal.TaskCategory;

/*TEST CASES TO HANDLE
 * 1. what is here
 * 2. different scopes ( local and remote )
 * 3. no bugs
 * 4. offline bugs
 * 
 * DEGREE OF SEPARATIONS
 * 1 Local bug, qualified reference
 * 2 local bug, unqualified reference
 * 3 remote bug, qualified reference
 * 4 remote bug, unqualified reference
 * 5 NONE
 */

/**
 * Test the bugzilla search functionality of the bridge
 * @author Shawn Minto
 */
public class BugzillaSearchPluginTest extends TestCase{
    
	//SHAWNTODO Add tests for the different types of searches (local qual, local unqual, fully qual, unqual) and mock up a bugs db for testing
	
    /** The expected number of results when searching for astNode */
// SHAWNTODO add back in when we have a test server mocked up
//	private static final int NUM_AST_RESULTS = 302;
//	
//	private static final int NUM_AST_SETSOURCERANGE_RESULTS = 15;
	
    /** list to add collectors to when notified */
    private List<List<?>> lists = new ArrayList<List<?>>();

    private IType astNodeType;
    
    @Override
    protected void setUp() throws Exception {
    	WorkspaceSetupHelper.setupWorkspace();
    	IJavaProject jp = WorkspaceSetupHelper.getJdtCoreDomProject();
    	astNodeType = WorkspaceSetupHelper.getType(jp, "org.eclipse.jdt.core.dom.ASTNode");
    }
    
    @Override
    protected void tearDown() throws Exception {
    	WorkspaceSetupHelper.clearDoiModel();
    }
    
    /**
     * Test adding and removing ISearchCompletedListeners 
     */
    public void testSearchCompletedListenerAddAndRemove(){
    	lists.clear();
    	
		// create 2 listeners
	 	IActiveSearchListener l1 =new IActiveSearchListener() {
		 		private boolean gathered = false; 
	 		
				public void searchCompleted(List<?> l)
				{
					lists.add(l);
					gathered = true;
				}

				public boolean resultsGathered() {
					return gathered;
				}
			};
		IActiveSearchListener l2 =new IActiveSearchListener() {
				private boolean gathered = false; 
		 		
				public void searchCompleted(List<?> l)
				{
					lists.add(l);
					gathered = true;
				}

				public boolean resultsGathered() {
					return gathered;
				}
			};
			
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType);
			
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
//		System.err.println("Search Took About " + time + " seconds");
    	MylarBugzillaPlugin.getBridge().removeFromLandmarksHash(astNodeType);
	 }
 
 	/**
 	 * Tests that the bridge gets the right data for us
 	 * This test is wierd because it waits on results.
 	 */
	public void testBridge() {
		lists.clear();
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType);
		
		IActiveSearchListener l = new IActiveSearchListener() {
	 			private boolean gathered = false; 
	 		
				public void searchCompleted(List<?> results)
				{
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
		assertTrue("Results not the right size", c.size() > 0); // TODO should be assertEquals on expected size
		
		// display the time it took for the search and the results returned
//		System.err.println("Search Took About " + time + " seconds");
//		System.err.println(c);
		MylarBugzillaPlugin.getBridge().removeFromLandmarksHash(astNodeType);

	}
	

	/**
  	* Tests that the bridge saves the results of a search so that it
  	* can be used later
  	*/
	public void testSaveResults() {
		lists.clear();
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.UNQUAL, astNodeType);
		
		IActiveSearchListener l = new IActiveSearchListener() {
 			private boolean gathered = false; 
 		
			public void searchCompleted(List<?> results)
			{
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};
		
		// perform the search
		SearchPluginTestHelper.search(s, l);
//		System.err.println("Search Took About " + time + " seconds");
		
		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertTrue("Results not the right size", c.size() > 0); // TODO should be assertEquals on expected size
		
		// check that the search has been saved
		List<BugzillaReportNode> saved = MylarBugzillaPlugin.getBridge().getFromLandmarksHash(astNodeType, BugzillaMylarSearch.UNQUAL);
		assertTrue("Results not cached", saved != null);
		assertTrue("Results not the right size", saved.size() > 0); // TODO should be assertEquals on expected size
		
		assertTrue(c.containsAll(saved) && saved.containsAll(c));
		MylarBugzillaPlugin.getBridge().removeFromLandmarksHash(astNodeType);
	}
	
	public void testLocalBugUnqual() throws InterruptedException {
		lists.clear();

		String bugPrefix = "Bugzilla-";
		
//		TaskList t = MylarTasklistPlugin.getTaskListManager().getTaskList();
//		MylarTasklistPlugin.getTaskListManager().setTaskList(t);
		TaskListManager manager = MylarTasklistPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.addCategory(cat);
		BugzillaTask bugTask1 = new BugzillaTask(bugPrefix +94185, "<bugzilla info>", true);
		cat.addTask(bugTask1);
		while(bugTask1.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}
		BugzillaTask bugTask2 = new BugzillaTask(bugPrefix + 3692, "<bugzilla info>", true);
		cat.addTask(bugTask2);
		while(bugTask2.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}
		BugzillaTask bugTask3 = new BugzillaTask(bugPrefix + 3693, "<bugzilla info>", true);
		cat.addTask(bugTask3);
		while(bugTask3.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}

		BugzillaTask bugTask4 = new BugzillaTask(bugPrefix + 9583, "<bugzilla info>", true);
		cat.addTask(bugTask4);
		while(bugTask4.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}
		
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.LOCAL_UNQUAL, astNodeType);
		
		IActiveSearchListener l = new IActiveSearchListener() {
 			private boolean gathered = false; 
 		
			public void searchCompleted(List<?> results)
			{
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};
		
		// perform the search
		SearchPluginTestHelper.search(s, l);
//		System.err.println("Search Took About " + time + " seconds");
		
		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 3, c.size());
		
		MylarBugzillaPlugin.getBridge().removeFromLandmarksHash(astNodeType);
        MylarTasklistPlugin.getTaskListManager().deleteCategory(cat);
	}
	
	//TODO need to test a bug that wraps...should fail since we can only search on a single line
	public void testLocalBugFullyQual() throws InterruptedException{
		lists.clear();

		String bugPrefix = "Bugzilla-";
		
		
//		TaskList t = MylarTasklistPlugin.getTaskListManager().getTaskList();
//		MylarTasklistPlugin.getTaskListManager().setTaskList(t);
		TaskListManager manager = MylarTasklistPlugin.getTaskListManager();
		TaskCategory cat = new TaskCategory("Testing Category");
		manager.addCategory(cat);
		BugzillaTask bugTask1 = new BugzillaTask(bugPrefix + 94185, "<bugzilla info>", true);
		cat.addTask(bugTask1);
		while(bugTask1.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}
		
		BugzillaTask bugTask2 = new BugzillaTask(bugPrefix + 9583, "<bugzilla info>", true);
		cat.addTask(bugTask2);
		while(bugTask2.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}
		BugzillaTask bugTask3 = new BugzillaTask(bugPrefix + 3693, "<bugzilla info>", true);
		cat.addTask(bugTask3);
		while(bugTask3.getState() != BugTaskState.FREE){
			Thread.sleep(500);
		}

		
		BugzillaMylarSearch s = new BugzillaMylarSearch(BugzillaMylarSearch.LOCAL_QUAL, astNodeType);
		
		IActiveSearchListener l = new IActiveSearchListener() {
 			private boolean gathered = false; 
 		
			public void searchCompleted(List<?> results)
			{
				lists.add(results);
				gathered = true;
			}

			public boolean resultsGathered() {
				return gathered;
			}
		};
		
		// perform the search
		SearchPluginTestHelper.search(s, l);
//		System.err.println("Search Took About " + time + " seconds");
		
		// do an inital search
		assertTrue("No collectors returned", lists.size() != 0);
		List<?> c = lists.get(0);
		assertEquals("Results not the right size", 1, c.size());
		
		MylarBugzillaPlugin.getBridge().removeFromLandmarksHash(astNodeType);
        MylarTasklistPlugin.getTaskListManager().deleteCategory(cat);
	}
	
}
