/*******************************************************************************
 * Copyright (c) 2011, 2013 GitHub Inc. and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     GitHub Inc. - initial API and implementation
 *     Tasktop Technologies - improvements
 *     See git history
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Miles Parker
 */
@SuppressWarnings("nls")
public class ReviewsDataLocatorTest {

	ReviewsDataLocator reviewDataLocator = new ReviewsDataLocator() {
		@Override
		public IPath getSystemDataPath() {
			return new Path(FileUtils.getTempDirectory().getAbsolutePath());
		}
	};

	@Test
	public void testFilePath() {
		IPath testPath = Path.fromOSString(FileUtils.getTempDirectory().getAbsolutePath());
		testPath = testPath.append("/reviews_bin/Parent/Class/123.txt");

		assertThat(reviewDataLocator.getFilePath("Parent", "Class", "123", "txt"), is(testPath));
	}

	@Test
	public void testMigrate() throws IOException {
		String testPath = FileUtils.getTempDirectory().getAbsolutePath();
		File binDir = new File(testPath + "/reviews_bin");
		FileUtils.forceMkdir(binDir);
		File xmlDir = new File(testPath + "/reviews_xml");
		FileUtils.forceMkdir(xmlDir);
		File xmlFile = new File(testPath + "/reviews_xml/SomeFile.txt");
		xmlFile.createNewFile();
		File modelDir = new File(testPath + "/model");
		FileUtils.forceMkdir(modelDir);
		assertThat(binDir.exists(), is(true));
		assertThat(xmlDir.exists(), is(true));
		assertThat(xmlFile.exists(), is(true));
		assertThat(modelDir.exists(), is(true));
		reviewDataLocator.migrate();
		assertThat(binDir.exists(), is(true));
		assertThat(xmlDir.exists(), is(false));
		assertThat(xmlFile.exists(), is(false));
		assertThat(modelDir.exists(), is(false));
	}
}
