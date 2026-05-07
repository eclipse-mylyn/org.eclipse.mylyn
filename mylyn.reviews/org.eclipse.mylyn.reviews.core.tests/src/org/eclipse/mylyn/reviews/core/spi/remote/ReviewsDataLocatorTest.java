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
import java.nio.file.Files;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 * @author Miles Parker
 */
@SuppressWarnings("nls")
public class ReviewsDataLocatorTest {
	@TempDir
	public File temporaryFolder;

	ReviewsDataLocator reviewDataLocator = new ReviewsDataLocator() {
		@Override
		public IPath getSystemDataPath() {
			return new Path(temporaryFolder.getAbsolutePath());
		}
	};

	@Test
	public void testFilePath() {
		IPath testPath = Path.fromOSString(temporaryFolder.getAbsolutePath());
		testPath = testPath.append("/reviews_bin/Parent/Class/123.txt");

		assertThat(reviewDataLocator.getFilePath("Parent", "Class", "123", "txt"), is(testPath));
	}

	@Test
	public void testMigrate() throws IOException {
		String testPath = temporaryFolder.getAbsolutePath();
		File binDir = new File(testPath + "/reviews_bin");
		Files.createDirectories(binDir.toPath());
		File xmlDir = new File(testPath + "/reviews_xml");
		Files.createDirectories(xmlDir.toPath());
		File xmlFile = new File(testPath + "/reviews_xml/SomeFile.txt");
		xmlFile.createNewFile();
		File modelDir = new File(testPath + "/model");
		Files.createDirectories(modelDir.toPath());
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
