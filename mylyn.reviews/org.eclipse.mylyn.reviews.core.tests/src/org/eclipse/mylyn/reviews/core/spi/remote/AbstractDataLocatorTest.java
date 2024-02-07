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
 *******************************************************************************/
package org.eclipse.mylyn.reviews.core.spi.remote;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.junit.Test;

/**
 * @author Miles Parker
 */
@SuppressWarnings("nls")
public class AbstractDataLocatorTest {

	AbstractDataLocator testLocator = new AbstractDataLocator() {

		@Override
		public IPath getSystemDataPath() {
			return new Path("/Foo/SystemRoot");
		};

		@Override
		protected IPath getLocatorDataSegment() {
			return new Path("locator");
		}

		@Override
		public IPath getFileScalingFragment(String fileName) {
			return new Path(fileName.substring(0, 1) + File.separator + fileName.substring(1, 2));
		}
	};

	@Test
	public void testFilePath() {
		assertThat(testLocator.getFilePath("Parent", "Class", "123", "txt").toPortableString(),
				is("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt"));
	}

	@Test
	public void testObjectPath() {
		assertThat(testLocator.getObjectPath("Parent", "Class", "123").toPortableString(), is("/Parent/Class/123"));
	}

	@Test
	public void testParentDir() {
		assertThat(testLocator.getParentDir("Parent", "Class", "123").toPortableString(),
				is("/Foo/SystemRoot/locator/Parent/Class/1/2"));
	}

	@Test
	public void testParseFileScalingFragment() {
		assertThat(testLocator.parseScalingFragment(new Path("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt")),
				is("1/2"));
	}

	@Test
	public void testParseFileName() {
		assertThat(testLocator.parseFileName(new Path("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt")), is("123"));
		assertThat(testLocator.parseFileName(new Path("/Parent/Class/123.txt")), is("123"));
	}

	@Test
	public void testParseContainerSegment() {
		assertThat(testLocator.parseContainerSegment(new Path("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt")),
				is("Parent"));
		assertThat(testLocator.parseContainerSegment(new Path("/Parent/Class/123.txt")), is("Parent"));
	}

	@Test
	public void testParseFileType() {
		//Fails until we do bug 406843
		//assertThat(testLocator.parseFileType(new Path("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt")), is("Class"));
		assertThat(testLocator.parseFileType(new Path("/Parent/Class/123.txt")), is("Class"));
	}

	@Test
	public void testObjectPathFromFilePath() {
		assertThat(testLocator.getObjectPathFromFilePath(new Path("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt"))
				.toPortableString(), is("/Parent/Class/123"));
		assertThat(testLocator.getObjectPathFromFilePath(new Path("/Parent/Class/123")).toPortableString(),
				is("/Parent/Class/123"));
	}

	@Test
	public void testFilePathFromObjectPath() {
		assertThat(testLocator.getFilePathFromObjectPath(new Path("/Parent/Class/123.txt")).toPortableString(),
				is("/Foo/SystemRoot/locator/Parent/Class/1/2/123.txt"));
	}
}
