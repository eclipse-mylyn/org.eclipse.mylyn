/*******************************************************************************
 * Copyright (c) 2011-2015 Torkild U. Resheim.
 *
 * All rights reserved. This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Torkild U. Resheim - initial API and implementation
 *******************************************************************************/
package org.eclipse.mylyn.docs.epub.tests;

import org.eclipse.mylyn.docs.epub.tests.api.TestEPUB;
import org.eclipse.mylyn.docs.epub.tests.api.TestEPUBPublication;
import org.eclipse.mylyn.docs.epub.tests.api.TestOPSPublication;
import org.eclipse.mylyn.docs.epub.tests.api.TestPublication;
import org.eclipse.mylyn.docs.epub.tests.core.TestEPUBFileUtil;
import org.eclipse.mylyn.docs.epub.tests.core.TestEclipseTocImporter;
import org.eclipse.mylyn.docs.epub.tests.core.TestOPSValidator;
import org.eclipse.mylyn.docs.epub.tests.core.TestTOCGenerator;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(Suite.class)
@SuiteClasses({ TestEPUB.class, TestOPSPublication.class, TestEPUBPublication.class, TestPublication.class,
		TestEPUBFileUtil.class, TestOPSValidator.class, TestTOCGenerator.class, TestEclipseTocImporter.class })
public class AllTests {
}
