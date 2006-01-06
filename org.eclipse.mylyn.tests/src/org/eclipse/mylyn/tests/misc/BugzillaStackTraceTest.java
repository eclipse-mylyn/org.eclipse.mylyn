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
////Created on Oct 12, 2004
package org.eclipse.mylar.tests.misc;

import java.io.File;
import java.io.FileReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.Path;
import org.eclipse.mylar.bugs.search.BugzillaMylarSearchOperation;
import org.eclipse.mylar.bugzilla.core.internal.BugParser;
import org.eclipse.mylar.bugzilla.core.search.BugzillaSearchHit;
import org.eclipse.mylar.bugzilla.tests.BugzillaTestPlugin;
import org.eclipse.mylar.bugzilla.ui.tasklist.BugzillaReportNode;
import org.eclipse.mylar.bugzilla.ui.tasklist.StackTrace;
import org.eclipse.mylar.core.tests.support.FileTool;


/**
 * Class to test the Bridge methods that do not require server queries
 * @author Shawn Minto
 */
public class BugzillaStackTraceTest extends TestCase {

    private static final String TEST_FILE_LOC = "testdata/reports-stacktrace/";
    
    private static final String BUGZILLA_SERVER_NAME = "https://bugs.eclipse.org/bugs";
    
	/**
	 * Test that the regular expression escaping mechanism works properly
	 */
	public void testREGEX(){
	    String r = ".*+(){}[]^$|?/\\";
	    String r2 = StackTrace.escapeForRegex(r);
	    String ans = "\\.\\*\\+\\(\\)\\{\\}\\[\\]\\^\\$\\|\\?\\/\\\\";
	    String msg = "Regular Expression matching wrong:\nwas: " + r2 + "\nshould be:" + ans;
	    assertTrue(msg, r2.equals(ans));
	}
	
//	/**
//	 * Test parsing the bug for multiple stacks in in
//	 */
//	public void testMultipleStacksDiffComments(){
//		// BUG 4862 - 2 stack traces - 1 in description, 1 in comment - text before and after
//	    performParse(4862, "4862.html", 2, false);
//	}
	
//	/**
//	 * Test parsing the bug for a single stack in the description with some
//	 * text before it
//	 */
//	public void testSingleStackCodeBeforeInDescription(){
//		// BUG 76388 - 1 stack trace - description - text before and formatted ugly 
//		performParse(76388, "76388.html", 1, false);
//	}
	
	/**
	 * Test parsing the bug for a single stack trace in the description with 
	 * text before and after it
	 */
	public void testSingleStackCodeBeforeAndAfterInDescription(){
	    
		// BUG 76146 - 1 stack trace - description - text before and code after
		performParse(76146, "76146.html", 1, false);
	}
	 
	/**
	 * Test parsing a bug that has 1 stack trace in the description with no extra
	 * text, but has lines in it that span 3 lines
	 */
	public void testSingleStackPoorFormatInDescription(){
		// BUG 67395 - 1 stack trace - description - no extra text, 1 at line spans 3 lines
		performParse(67395, "67395.html", 1, false);
	}
	
	/**
	 * Test parsing a bug with no stack traces and no qualified exception names
	 */
	public void testNoStackNoQualified(){
		// BUG 4548 - no stack traces, no qualified reference to an exception
		performParse(4548, "4548.html", 0, false);
	}
	 
	/**
	 * Test parsing a bug with no stack traces, but a qualified reference to
	 * an exception
	 */
	public void testNoStackQual(){
		// BUG 1 - no stack traces, qualified reference to exception - made up bug
		performParse(1, "1.html", 0, false);
	}

	
	/**
	 * Test parsing of a bug with 1 stack trace and multiple qualified references
	 */
	public void testSingleStackQual(){
		// BUG 2 - 1 stack trace- 2 qual ref, stack trace, 1 qual ref - made up bug 
		performParse(2, "2.html", 1, false);
	}
	
	/**
	 * Test parsing of a bug with many stacks traces in a single comment
	 */
	public void testMultipleStackSingleComment(){
		// BUG 40152 - 1 stack trace- 2 qual ref, stack trace, 1 qual ref - made up bug 
		performParse(40152, "40152.html", 33, false);
	}

	/**
	 * Print out the stack traces
	 * @param l List of stack traces
	 */
	private void printStackTraces(List<StackTrace> l){
	    System.out.println("\n\n");
	    for(int i = 0; i < l.size(); i++){
	        StackTrace trace = l.get(i);
	        System.out.println("*****************?????????????????*****************\n");
	        System.out.println("OFFSET: " + trace.getOffset() + " LENGTH: " + trace.getLength());
	        System.out.println(trace.getStackTrace());
	        System.out.println("*****************?????????????????*****************\n\n");
	    }
	}
	
	private void performParse(int bugNumber, String bugFileName, int numTracesExpected, boolean printStackTraces){
		
	    BugzillaSearchHit hit = new BugzillaSearchHit(bugNumber,"","","","","","","","", "<TEST-SERVER>"); // stack trace in desc and com
		
		// create a new doi info
	    BugzillaReportNode doi = new BugzillaReportNode(0, hit, false);
	    try {
	        
	        // read the bug in from a file
	   		File f = FileTool.getFileInPlugin(BugzillaTestPlugin.getDefault(), new Path(TEST_FILE_LOC+bugFileName));     	// used if run as a plugin test
//    		File f = new File(TEST_FILE_LOC+bugFileName); // used if run as a standalone test
	        Reader reader = new FileReader(f);
            doi.setBug(BugParser.parseBug(reader, hit.getId(), BUGZILLA_SERVER_NAME, true, null, null, null));
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        // do a second pass parse on the bug
	    List<BugzillaReportNode> l = new ArrayList<BugzillaReportNode>();
	    l.add(doi);
	    BugzillaMylarSearchOperation.secondPassBugzillaParser(l);
	    
	    // make sure that we received the right number of stack traces back
//	    System.out.println("*** BUG " + hit.getId() + " ***");
//	    System.out.println("NumStackTraces = " + doi.getStackTraces().size());
	    assertEquals("Wrong Number stack traces", numTracesExpected, doi.getStackTraces().size());
	    if(printStackTraces)
	    	printStackTraces(doi.getStackTraces());
	}
}
