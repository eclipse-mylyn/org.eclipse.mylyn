/*******************************************************************************
 * Copyright (c) 2012, 2013 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.io.File;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.reviews.core.spi.remote.ReviewsDataLocator;
import org.eclipse.mylyn.reviews.ui.spi.factories.ReviewsUiDataLocator;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * @author Miles Parker
 */
public class UiDataLocatorTest extends TestCase {

	@Test
	public void testGetLocation() {
		ReviewsDataLocator locator = new ReviewsUiDataLocator();
		String workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		String expectedPath = workSpaceRoot + "/.metadata/.mylyn/reviews_bin/Class/MyFile.txt"; //$NON-NLS-1$
		String systemPath = expectedPath.replace('/', File.separatorChar); //ensure platform neutrality
		IPath fileLocation = locator.getFilePath("", "Class", "MyFile", "txt"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
		assertThat(fileLocation.toPortableString(), is(expectedPath));
		assertThat(fileLocation.toOSString(), is(systemPath));
	}
}
