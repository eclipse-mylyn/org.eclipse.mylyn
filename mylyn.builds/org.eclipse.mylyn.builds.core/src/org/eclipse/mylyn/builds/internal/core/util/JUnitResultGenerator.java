/*******************************************************************************
 * Copyright (c) 2010, 2011 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.internal.core.util;

import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.core.TestCaseResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

/**
 * @author Steffen Pingel
 */
public class JUnitResultGenerator {

	private static final String CLASS_NAME = "classname"; //$NON-NLS-1$

	private static final String ERROR = "error"; //$NON-NLS-1$

	private static final String ERRORS = "errors"; //$NON-NLS-1$

	private static final String FAILURE = "failure"; //$NON-NLS-1$

	private static final String FAILURES = "failures"; //$NON-NLS-1$

	private static final String IGNORED = "ignored"; //$NON-NLS-1$

	private static final String MESSAGE = "message"; //$NON-NLS-1$

	private static final String NAME = "name"; //$NON-NLS-1$

	private static final String PROJECT = "project"; //$NON-NLS-1$

	private static final String SKIPPED = "skipped"; //$NON-NLS-1$

	private static final String STARTED = "started"; //$NON-NLS-1$

	private static final String TESTCASE = "testcase"; //$NON-NLS-1$

	private static final String TESTRUN = "testrun"; //$NON-NLS-1$

	private static final String TESTS = "tests"; //$NON-NLS-1$

	private static final String TESTSUITE = "testsuite"; //$NON-NLS-1$

	private static final String TIME = "time"; //$NON-NLS-1$

	private final ITestResult result;

	/**
	 * True, if ignored test cases should be included (supported by Eclipse 3.6 and later).
	 */
	private boolean includeIgnored;

	public JUnitResultGenerator(ITestResult result) {
		this.result = result;
	}

	public boolean includeIgnored() {
		return includeIgnored;
	}

	public void setIncludeIgnored(boolean includeIgnored) {
		this.includeIgnored = includeIgnored;
	}

	public void write(ContentHandler handler) throws SAXException {
		AttributesImpl attributes = new AttributesImpl();

		handler.startDocument();

		attributes.clear();
		attributes.addAttribute(null, null, NAME, null, result.getBuild().getLabel());
		attributes.addAttribute(null, null, PROJECT, null, result.getBuild().getLabel());
		attributes.addAttribute(null, null, TESTS, null,
				Integer.toString(result.getFailCount() + result.getPassCount()));
		attributes.addAttribute(null, null, STARTED, null,
				Integer.toString(result.getFailCount() + result.getPassCount()));
		attributes.addAttribute(null, null, FAILURES, null, Integer.toString(result.getFailCount()));
		attributes.addAttribute(null, null, ERRORS, null, Integer.toString(result.getErrorCount()));
		attributes.addAttribute(null, null, IGNORED, null, Integer.toString(result.getIgnoredCount()));
		handler.startElement(null, null, TESTRUN, attributes);

		for (ITestSuite testsuite : result.getSuites()) {
			attributes.clear();
			attributes.addAttribute(null, null, NAME, null, testsuite.getLabel());
			attributes.addAttribute(null, null, TIME, null, Double.toString(testsuite.getDuration() / 1000.0d));
			handler.startElement(null, null, TESTSUITE, attributes);

			// group tests by class name
			String childSuite = null;
			for (ITestCase test : testsuite.getCases()) {
				// only use group if tests are not already grouped
				if (test.getClassName() != null && !test.getClassName().equals(testsuite.getLabel())) {
					if (childSuite == null) {
						attributes.clear();
						attributes.addAttribute(null, null, NAME, null, test.getClassName());
						handler.startElement(null, null, TESTSUITE, attributes);
					} else if (!childSuite.equals(test.getClassName())) {
						handler.endElement(null, null, TESTSUITE);

						attributes.clear();
						attributes.addAttribute(null, null, NAME, null, test.getClassName());
						handler.startElement(null, null, TESTSUITE, attributes);
					}
					childSuite = test.getClassName();
				}

				if (!test.isSkipped() || includeIgnored()) {
					attributes.clear();
					attributes.addAttribute(null, null, NAME, null, test.getLabel());
					attributes.addAttribute(null, null, CLASS_NAME, null, test.getClassName());
					attributes.addAttribute(null, null, TIME, null, Double.toString(test.getDuration() / 1000.0d));
					handler.startElement(null, null, TESTCASE, attributes);

					if (test.getStatus() == TestCaseResult.FAILED || test.getStatus() == TestCaseResult.REGRESSION) {
						attributes.clear();
						//attributes.addAttribute(null, TYPE, TYPE, null, test.getFailureType());
						attributes.addAttribute(null, null, MESSAGE, null, test.getMessage());

						String element = test.getMessage() != null ? FAILURE : ERROR;
						handler.startElement(null, null, element, attributes);

						if (test.getStackTrace() != null) {
							char[] charArray = test.getStackTrace().toCharArray();
							handler.characters(charArray, 0, charArray.length);
						}

						handler.endElement(null, null, element);
					}

					if (test.isSkipped()) {
						attributes.clear();
						handler.startElement(null, null, SKIPPED, attributes);
						handler.endElement(null, null, SKIPPED);
					}

					handler.endElement(null, null, TESTCASE);
				}
			}

			if (childSuite != null) {
				handler.endElement(null, null, TESTSUITE);
			}

			handler.endElement(null, null, TESTSUITE);
		}

		handler.endElement(null, null, TESTRUN);

		handler.endDocument();
	}
}
