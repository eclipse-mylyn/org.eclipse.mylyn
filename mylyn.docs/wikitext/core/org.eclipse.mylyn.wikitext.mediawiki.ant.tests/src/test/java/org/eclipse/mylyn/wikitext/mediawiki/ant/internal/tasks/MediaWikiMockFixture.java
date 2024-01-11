/*******************************************************************************
 * Copyright (c) 2017, 2024 Jeremie Bresson and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Jeremie Bresson - initial API and implementation
 *     ArSysOp - ongoing support
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.mediawiki.ant.internal.tasks;

import static java.text.MessageFormat.format;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.mylyn.wikitext.toolkit.TestResources;

class MediaWikiMockFixture {
	private final File serverFolder;

	public MediaWikiMockFixture(File serverFolder) {
		Objects.requireNonNull(serverFolder);
		this.serverFolder = serverFolder;
	}

	private static final List<String> IMG_FILE_NAMES = Arrays.asList("Image1.png", "IMG2.png", "image3.png", "I4.png",
			"img5.jpg", "Image6.jpeg", "image7.gif", "IMG8.png", "Icon9.gif", "ImageTen.png", "pict1.gif", "p2.jpeg",
			"PICTURE3.JPG", "Pict4.png");

	public void createImageFiles() throws IOException {
		for (String name : IMG_FILE_NAMES) {
			new File(serverFolder, name).createNewFile();
		}
	}

	public Map<String, String> createImageServerContent(String scheme) {
		Map<String, String> map = new HashMap<>();
		map.put(scheme
				+ "://wiki.eclipse.org/api.php?action=query&titles=Some%2FMy_Page&generator=images&prop=imageinfo&iiprop=url&format=xml",
				createServerEntry("WikiToDocTaskIntegrationTest1.xml"));
		map.put(scheme
				+ "://wiki.eclipse.org/api.php?action=query&titles=Some%2FMy_Page&generator=images&prop=imageinfo&iiprop=url&format=xml&gimcontinue=49671%7Cpict1.gif",
				createServerEntry("WikiToDocTaskIntegrationTest2.xml"));
		return map;
	}

	private String createServerEntry(String resourcePath) {
		String content = TestResources.load(this.getClass(), resourcePath);
		content = content.replaceAll("__TEMP-DIR-URL__/", serverFolder.toPath().toUri().toString());
		return content;
	}

	public void assertImageFiles(File imagesFolder) {
		assertTrue(imagesFolder.getPath(), imagesFolder.exists() && imagesFolder.isDirectory());

		File[] images = imagesFolder.listFiles();
		assertNotNull(format("{0} should have files", imagesFolder), images);
		for (String name : IMG_FILE_NAMES) {
			File imageFile = new File(imagesFolder, name);
			assertTrue(format("Expected to find an image file {0} in folder {1}.", name, imagesFolder),
					imageFile.exists());
		}
	}
}
