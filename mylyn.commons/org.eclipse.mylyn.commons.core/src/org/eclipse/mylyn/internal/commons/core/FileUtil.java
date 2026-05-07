/*******************************************************************************
 * Copyright (c) 2026 George Lindholm
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html.
 *
 * Contributors:
 *      See git history
 *******************************************************************************/

package org.eclipse.mylyn.internal.commons.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;

import org.eclipse.core.runtime.IPath;

public class FileUtil {

	public static String readFile(final File file) throws IOException {
		return Files.readString(file.toPath());
	}

	public static String readFile(final Class<?> clazz, final String file) throws IOException {
		return new String(clazz.getResource(file).openStream().readAllBytes(), StandardCharsets.UTF_8);
	}

	public static String readFile(final InputStream stream) throws IOException {
		return new String(stream.readAllBytes(), StandardCharsets.UTF_8);
	}

	public static String readFile(final URL file) throws IOException {
		return new String(file.openStream().readAllBytes(), StandardCharsets.UTF_8);
	}

	public static void deleteTree(final File rootDir) throws IOException {
		if (rootDir.exists()) {
			Files.walk(rootDir.toPath()) //
			.sorted(Comparator.reverseOrder()) //
			.map(Path::toFile) //
			.forEach(File::delete);
		}
	}

	public static void deleteTree(final IPath rootDir) throws IOException {
		deleteTree(rootDir.toFile());
	}
}