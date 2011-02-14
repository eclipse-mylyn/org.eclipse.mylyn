/*********************************************************************
 * Copyright (c) 2010 Sony Ericsson/ST Ericsson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 *  Contributors:
 *      Sony Ericsson/ST Ericsson - initial API and implementation
 *      Tasktop Techonologies - improvements
 *********************************************************************/
package org.eclipse.mylyn.gerrit.core;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * @author Steffen Pingel
 */
public class AllGerritTests {

	public static Test suite() {
		TestSuite suite = new TestSuite(AllGerritTests.class.getName());
		suite.addTestSuite(GerritAttributeTest.class);
		suite.addTestSuite(GerritConnectorTest.class);
		return suite;
	}

}
