/*******************************************************************************
 * Copyright (c) 2004, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.context.sdk.java.search;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import junit.framework.TestCase;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.mylyn.context.core.IInteractionElement;
import org.eclipse.mylyn.context.sdk.util.search.ISearchPluginTest;
import org.eclipse.mylyn.internal.context.core.IActiveSearchListener;
import org.eclipse.mylyn.internal.context.core.IActiveSearchOperation;

/**
 * @author Shawn Minto
 */
public class SearchPluginTestHelper extends TestCase {

	private final ISearchPluginTest test;

	/**
	 * maximum time to wait for search results * 2. so 60 = 30sec - only sleeping 500ms at a time instead of 1 sec
	 */
	private static final long MAXWAIT = 360;

	public SearchPluginTestHelper(ISearchPluginTest test) {
		this.test = test;
	}

	public void searchResultsNotNull(ActiveSearchNotifier notifier, String handle, String kind,
			IInteractionElement searchNode, int dos, int expected, boolean includeOnlyJava) throws IOException,
			CoreException {
		notifier.mockRaiseInterest(handle, kind);

		List<?> results = test.search(dos, searchNode);
		int size = results.size();
		if (includeOnlyJava) {
			size = 0;
			for (Object o : results) {
				if (o instanceof IJavaElement) {
					size++;
				}
			}
		}
		assertNotNull("Results Null", results);
		assertEquals("Wrong number search results", expected, size);
		notifier.clearContext();
	}

	public void searchResultsNotNullInteresting(ActiveSearchNotifier notifier, String handle, String kind,
			IInteractionElement searchNode, int dos, int expected, boolean includeOnlyJava) throws IOException,
			CoreException {
		notifier.mockEditorSelection(handle, kind);

		List<?> results = test.search(dos, searchNode);

		int size = results.size();
		if (includeOnlyJava) {
			size = 0;
			for (Object o : results) {
				if (o instanceof IJavaElement) {
					size++;
				}
			}
		}

		assertNotNull("Results Null", results);
		assertEquals("Wrong number search results", expected, size);
		notifier.clearContext();
	}

	public void searchResultsNotNull(ActiveSearchNotifier notifier, IInteractionElement searchNode, int dos,
			int expected, boolean includeOnlyJava) throws IOException, CoreException {
		List<?> results = test.search(dos, searchNode);
		int size = results.size();
		if (includeOnlyJava) {
			size = 0;
			for (Object o : results) {
				if (o instanceof IJavaElement) {
					size++;
				}
			}
		}
		assertNotNull("Results Null", results);
		assertEquals("Wrong number search results", expected, size);
		notifier.clearContext();
	}

	public void searchResultsNull(ActiveSearchNotifier notifier, String handle, String kind,
			IInteractionElement searchNode, int dos) throws IOException, CoreException {
		notifier.mockRaiseInterest(handle, kind);

		List<?> results = test.search(dos, searchNode);
		assertNull("Results Not Null", results);
		notifier.clearContext();
	}

	public void searchResultsNull(ActiveSearchNotifier notifier, IInteractionElement searchNode, int dos)
			throws IOException, CoreException {
		List<?> results = test.search(dos, searchNode);
		assertNull("Results Not Null", results);
		notifier.clearContext();
	}

	/**
	 * @return -1 if there was a prob, else the search time in seconds
	 */
	public static long search(IActiveSearchOperation op, IActiveSearchListener listener) {
		if (op == null) {
			return -1;
		}

		op.addListener(listener);

		long start = new Date().getTime();

		op.run(new NullProgressMonitor());

		for (int i = 0; i < MAXWAIT && !listener.resultsGathered(); i++) {
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				// don't need to do anything here
			}
		}

		long time = (new Date().getTime() - start) / 1000;

		if (!listener.resultsGathered()) {
			return -1;
		}
		return time;
	}
}
