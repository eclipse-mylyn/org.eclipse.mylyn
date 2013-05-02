/*******************************************************************************
 * Copyright (c) 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.reviews.tests.ui;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;

import junit.framework.TestCase;

import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.mylyn.reviews.ui.spi.factories.UiDataLocator;
import org.junit.Test;

/**
 * @author Miles Parker
 */
public class UiDataLocatorTest extends TestCase {

	@Test
	public void testGetLocation() {
		UiDataLocator locator = new UiDataLocator();
		String workSpaceRoot = ResourcesPlugin.getWorkspace().getRoot().getLocation().toString();
		String expectedPath = workSpaceRoot + "/.metadata/.mylyn/model/Class/MyFile.txt";
		String systemPath = expectedPath.replaceAll("/", File.separator); //ensure platform neutrality
		IPath fileLocation = locator.getFilePath("", "Class", "MyFile", "txt");
		assertThat(fileLocation.toPortableString(), is(expectedPath));
		assertThat(fileLocation.toOSString(), is(systemPath));
	}
}
