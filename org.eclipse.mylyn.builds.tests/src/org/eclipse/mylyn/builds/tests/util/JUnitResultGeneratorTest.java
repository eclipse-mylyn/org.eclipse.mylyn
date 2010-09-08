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

	public void testWrite() throws Exception {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		StreamResult streamResult = new StreamResult(out);
		SAXTransformerFactory factory = (SAXTransformerFactory) TransformerFactory.newInstance();
		TransformerHandler handler = factory.newTransformerHandler();
		handler.setResult(streamResult);

		ITestResult testResult = BuildFactory.eINSTANCE.createTestResult();
		testResult.setDuration(111L);
		testResult.setErrorCount(1);
		testResult.setFailCount(2);
		testResult.setIgnoredCount(3);
		testResult.setPassCount(4);
		IBuild build = BuildFactory.eINSTANCE.createBuild();
		build.setLabel("Build1");
		testResult.setBuild(build);

		ITestSuite suite = BuildFactory.eINSTANCE.createTestSuite();
		suite.setLabel("Suite1");
		suite.setDuration(222L);
		testResult.getSuites().add(suite);

		ITestCase testCase = BuildFactory.eINSTANCE.createTestCase();
		testCase.setClassName("TestClass1");
		testCase.setLabel("TestCase1");
		testCase.setDuration(333L);
		suite.getCases().add(testCase);

		JUnitResultGenerator generator = new JUnitResultGenerator(testResult);
		generator.write(handler);

		assertEquals(
				"<?xml version=\"1.0\" encoding=\"UTF-8\"?><testrun name=\"Build1\" project=\"Build1\" tests=\"6\" started=\"6\" failures=\"2\" errors=\"1\" ignored=\"3\"><testsuite name=\"Suite1\" time=\"0.222\"><testcase name=\"TestCase1\" classname=\"TestClass1\" time=\"0.333\"/></testsuite></testrun>",
				out.toString());
	}

}
