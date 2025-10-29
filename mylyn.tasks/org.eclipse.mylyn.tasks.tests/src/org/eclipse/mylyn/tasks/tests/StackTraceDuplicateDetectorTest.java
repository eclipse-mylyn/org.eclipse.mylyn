/*******************************************************************************
 * Copyright (c) 2004, 2011 Jeff Pound and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeff Pound - initial API and implementation
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.tasks.tests;

import org.eclipse.mylyn.internal.tasks.ui.search.StackTraceDuplicateDetector;

import junit.framework.TestCase;

/**
 * @author Jeff Pound
 */
@SuppressWarnings("nls")
public class StackTraceDuplicateDetectorTest extends TestCase {

	public void testStackTrace() throws Exception {
		String stackTrace = "java.lang.NullPointerException\nat jeff.testing.stack.trace.functionality(jeff.java:481)";
		assertNotNull(StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testNoStackTrace() throws Exception {
		String stackTrace = "this is not really a stacktrace";
		assertNull(StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceWithAppendedText() throws Exception {
		String stackTrace = "java.lang.NullPointerException\nat jeff.testing.stack.trace.functionality(jeff.java:481)";
		String extraText = "\nExtra text that isnt' part of the stack trace java:";
		assertEquals(stackTrace + "\n",
				StackTraceDuplicateDetector.getStackTraceFromDescription(extraText + "\n" + stackTrace + "\n"));
	}

	public void testStackTraceMisaligned() throws Exception {
		String stackTrace = """
				java.lang.IllegalStateException: zip file closed
				     at java.util.zip.ZipFile.ensureOpen (ZipFile.java:518)
				at java.util.zip.ZipFile.getEntry (ZipFile.java:251)
				   at java.util.jar.JarFile.getEntry(JarFile.java:200)
				at sun.net.www.protocol.jar.URLJarFile.getEntry
				     (URLJarFile.java:90)
				at sun.net.www.protocol.jar.JarURLConnection.connect(JarURLConnection.java:112)
				at sun.net.www.protocol.jar.JarURLConnection.getInputStream
				(JarURLConnection.java:124)
				at org.eclipse.jdt.internal.core.JavaElement
				.getURLContents(JavaElement.java:734)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceSUN() throws Exception {
		// SUN, IBM (no space before brackets, one set of brackets)
		String stackTrace = """
				java.lang.IllegalStateException: zip file closed
				     at java.util.zip.ZipFile.ensureOpen(ZipFile.java:518)
				     at java.util.zip.ZipFile.getEntry(ZipFile.java:251)
				     at java.util.jar.JarFile.getEntry(JarFile.java:200)
				     at sun.net.www.protocol.jar.URLJarFile.getEntry(URLJarFile.java:90)
				     at sun.net.www.protocol.jar.JarURLConnection.connect(JarURLConnection.java:112)
				     at sun.net.www.protocol.jar.JarURLConnection.getInputStream(JarURLConnection.java:124)
				     at org.eclipse.jdt.internal.core.JavaElement.getURLContents(JavaElement.java:734)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceGCJ() throws Exception {
		// gcj/gij (path and lib names in additional brackets)
		String stackTrace = """
				java.lang.Error: Something bad happened
					   at testcase.main(java.lang.String[]) (Unknown Source)
					   at gnu.java.lang.MainThread.call_main() (/usr/lib/libgcj.so.6.0.0)
					   at gnu.java.lang.MainThread.run() (/usr/lib/libgcj.so.6.0.0)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceNoLineNums() throws Exception {
		// ikvm (no line numbers)
		String stackTrace = """
				java.lang.Error: Something bad happened
					at testcase.main (testcase.java)
					at java.lang.reflect.Method.Invoke (Method.java)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceJRockit() throws Exception {
		// jrockit (slash delimiters)
		String stackTrace = """
				java.lang.Error: Something bad happened
					at java/io/BufferedReader.readLine(BufferedReader.java:331)
					at java/io/BufferedReader.readLine(BufferedReader.java:362)
					at java/util/Properties.load(Properties.java:192)
					at java/util/logging/LogManager.readConfiguration(L:555)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

	public void testStackTraceOther() throws Exception {
		// jamvm, sablevm, kaffe, cacao (space before brackets, one set of brackets)
		String stackTrace = """
				java.lang.Error: Something bad happened
					   at testcase.main (testcase.java:3)
					   at java.lang.VirtualMachine.invokeMain (VirtualMachine.java)
					   at java.lang.VirtualMachine.main (VirtualMachine.java:108)""";
		assertEquals(stackTrace, StackTraceDuplicateDetector.getStackTraceFromDescription(stackTrace));
	}

}
