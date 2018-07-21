/*******************************************************************************
 * Copyright (c) 2010 Tasktop Technologies and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.builds.tests.util;

import java.io.ByteArrayOutputStream;

import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXTransformerFactory;
import javax.xml.transform.sax.TransformerHandler;
import javax.xml.transform.stream.StreamResult;

import junit.framework.TestCase;

import org.eclipse.mylyn.builds.core.IBuild;
import org.eclipse.mylyn.builds.core.ITestCase;
import org.eclipse.mylyn.builds.core.ITestResult;
import org.eclipse.mylyn.builds.core.ITestSuite;
import org.eclipse.mylyn.builds.internal.core.BuildFactory;
import org.eclipse.mylyn.builds.internal.core.util.JUnitResultGenerator;

/**
 * @author Steffen Pingel
 */
public class JUnitResultGeneratorTest extends TestCase {

	private ByteArrayOutputStream out;

	private ITestResult testResult;

	private ITestSuite suite;

	private ITestCase testCase;

	private TransformerHandler handler;

	@Override
	protected void setUp() throws Exception {
		out = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
		handler = factory.newTransformerHandler();
		handler.setResult(streamResult);

		testResult = BuildFactory.eINSTANCE.createTestResult();
		testResult.setDuration(111L);
		testResult.setErrorCount(1);
		testResult.setFailCount(2);
		testResult.setIgnoredCount(3);
		testResult.setPassCount(4);
		IBuild build = BuildFactory.eINSTANCE.createBuild();
		build.setLabel("Build1");
		testResult.setBuild(build);

		suite = BuildFactory.eINSTANCE.createTestSuite();
		suite.setLabel("TestClass1");
		suite.setDuration(222L);
		testResult.getSuites().add(suite);

		testCase = BuildFactory.eINSTANCE.createTestCase();
		testCase.setClassName("TestClass1");
		testCase.setLabel("TestCase1");
		testCase.setDuration(333L);
		suite.getCases().add(testCase);
	}

	public void testWrite() throws Exception {
		JUnitResultGenerator generator = new JUnitResultGenerator(testResult);
		generator.write(handler);

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><testrun name=\"Build1\" project=\"Build1\" tests=\"6\" started=\"6\" failures=\"2\" errors=\"1\" ignored=\"3\"><testsuite name=\"TestClass1\" time=\"0.222\"><testcase name=\"TestCase1\" classname=\"TestClass1\" time=\"0.333\"/></testsuite></testrun>",
				out.toString());
	}

	public void testWriteWithChildSuite() throws Exception {
		suite.setLabel("Suite1");

		JUnitResultGenerator generator = new JUnitResultGenerator(testResult);
		generator.write(handler);

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><testrun name=\"Build1\" project=\"Build1\" tests=\"6\" started=\"6\" failures=\"2\" errors=\"1\" ignored=\"3\"><testsuite name=\"Suite1\" time=\"0.222\"><testsuite name=\"TestClass1\"><testcase name=\"TestCase1\" classname=\"TestClass1\" time=\"0.333\"/></testsuite></testsuite></testrun>",
				out.toString());
	}

}
