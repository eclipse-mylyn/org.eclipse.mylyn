/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     David Green - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.wikitext.mediawiki.ant.tasks;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import com.google.common.base.Joiner;

class WikiPageAssertions {

	private static final int MINIMUM_EXPECTED_IMAGES_EXCLUSIVE = 10;

	public static void assertManyImages(File imagesFolder) {
		assertTrue(imagesFolder.getPath(), imagesFolder.exists() && imagesFolder.isDirectory());

		File[] images = imagesFolder.listFiles();
		assertNotNull(format("{0} should have files", imagesFolder), images);
		int imageCount = imageCount(images);
		assertTrue(
				format("Expected at least {0} images but got {1} (files in path: {2})",
						MINIMUM_EXPECTED_IMAGES_EXCLUSIVE, imageCount, Joiner.on(", ").join(images)),
				imageCount > MINIMUM_EXPECTED_IMAGES_EXCLUSIVE);
	}

	private static int imageCount(File[] images) {
		int count = 0;
		for (File file : images) {
			String name = file.getName();
			if (name.endsWith(".png") || name.endsWith(".gif") || name.endsWith(".jpeg") || name.endsWith(".jpg")) {
				++count;
			}
		}
		return count;
	}

	private WikiPageAssertions() {
		// prevent instantiation
	}
}
