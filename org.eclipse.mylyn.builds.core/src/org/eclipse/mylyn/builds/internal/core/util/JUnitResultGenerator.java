/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.eclipse.mylyn.builds.internal.core.TestResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Steffen Pingel
 */
public class JUnitResultGenerator {

	private static final String CLASS_NAME = "classname"; //$NON-NLS-1$

	private static final String ERRORS = "errors"; //$NON-NLS-1$

	private static final String FAILURE = "failure"; //$NON-NLS-1$

	private static final String FAILURES = "failures"; //$NON-NLS-1$

	private static final String IGNORED = "ignored"; //$NON-NLS-1$

	private static final String NAME = "name"; //$NON-NLS-1$

	private static final String PROJECT = "project"; //$NON-NLS-1$

	private static final String STARTED = "started"; //$NON-NLS-1$

	private static final String TESTCASE = "testcase"; //$NON-NLS-1$

	private static final String TESTRUN = "testrun"; //$NON-NLS-1$

	private static final String TESTS = "tests"; //$NON-NLS-1$

	private static final String TESTSUITE = "testsuite"; //$NON-NLS-1$

	private static final String TIME = "time"; //$NON-NLS-1$

	private final TestResult result;

	public JUnitResultGenerator(TestResult result) {
		this.result = result;
	}

	public void write(ContentHandler handler) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();

		handler.startDocument();

		attributes.clear();
		attributes.addAttribute("", NAME, NAME, "", result.getBuild().getLabel()); //$NON-NLS-1$//$NON-NLS-2$
		attributes.addAttribute("", PROJECT, PROJECT, "", result.getBuild().getLabel());//$NON-NLS-1$//$NON-NLS-2$
		attributes.addAttribute("", TESTS, TESTS, "", Integer.toString(result.getFailCount() + result.getPassCount()));//$NON-NLS-1$//$NON-NLS-2$
		attributes.addAttribute("", STARTED, STARTED, "", Integer.toString(result.getFailCount() //$NON-NLS-1$//$NON-NLS-2$
				+ result.getPassCount()));
		attributes.addAttribute("", FAILURES, FAILURES, "", Integer.toString(result.getFailCount()));//$NON-NLS-1$//$NON-NLS-2$
		attributes.addAttribute("", ERRORS, ERRORS, "", Integer.toString(result.getErrorCount()));//$NON-NLS-1$//$NON-NLS-2$ 
		attributes.addAttribute("", IGNORED, IGNORED, "", Integer.toString(result.getIgnoredCount()));//$NON-NLS-1$//$NON-NLS-2$ 
		handler.startElement("", TESTRUN, TESTRUN, attributes); //$NON-NLS-1$

		for (ITestSuite testsuite : result.getSuites()) {
			attributes.clear();
			attributes.addAttribute("", NAME, NAME, "", testsuite.getLabel()); //$NON-NLS-1$ //$NON-NLS-2$
			attributes.addAttribute("", TIME, TIME, "", Long.toString(testsuite.getDuration())); //$NON-NLS-1$//$NON-NLS-2$
			handler.startElement("", TESTSUITE, TESTSUITE, attributes); //$NON-NLS-1$

			for (ITestCase test : testsuite.getCases()) {
				attributes.clear();
				attributes.addAttribute("", NAME, NAME, "", test.getLabel()); //$NON-NLS-1$ //$NON-NLS-2$
				attributes.addAttribute("", CLASS_NAME, CLASS_NAME, "", test.getClassName()); //$NON-NLS-1$ //$NON-NLS-2$
				attributes.addAttribute("", TIME, TIME, "", Long.toString(test.getDuration())); //$NON-NLS-1$//$NON-NLS-2$
				handler.startElement("", TESTCASE, TESTCASE, attributes); //$NON-NLS-1$

				if (test.getStatus() == TestCaseResult.FAILED) {
					handler.startElement("", FAILURE, FAILURE, new AttributesImpl()); //$NON-NLS-1$
					char[] charArray = test.getErrorOutput().toCharArray();
					handler.characters(charArray, 0, charArray.length);
					handler.endElement("", FAILURE, FAILURE); //$NON-NLS-1$
				}

				handler.endElement("", TESTCASE, TESTCASE); //$NON-NLS-1$
			}

			handler.endElement("", TESTSUITE, TESTSUITE); //$NON-NLS-1$
		}

		handler.endElement("", TESTRUN, TESTRUN); //$NON-NLS-1$

		handler.endDocument();
	}

}
