/*******************************************************************************
 * Copyright (c) 2007, 2021 David Green and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     David Green - initial API and implementation
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.wikitext.parser.builder;

import static org.eclipse.mylyn.wikitext.util.Preconditions.checkArgument;
import static org.eclipse.mylyn.wikitext.util.Preconditions.checkState;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

class HtmlEntities {

	private static HtmlEntities instance = new HtmlEntities();

	private static Map<String, List<String>> readHtmlEntities() {
		Map<String, List<String>> builder = new HashMap<>();

		try (BufferedReader reader = new BufferedReader(
				new InputStreamReader(HtmlDocumentBuilder.class.getResourceAsStream("html-entity-references.txt"), //$NON-NLS-1$
						StandardCharsets.UTF_8))) {

			String line;
			while ((line = reader.readLine()) != null) {
				String[] lineItems = line.split("\s"); //$NON-NLS-1$
				checkState(lineItems.length > 1);
				for (int x = 1; x < lineItems.length; ++x) {
					List<String> list = builder.computeIfAbsent(lineItems[0], i -> new ArrayList<>());
					list.add(lineItems[x]);
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		return builder.entrySet()
				.stream()
				.collect(Collectors.toUnmodifiableMap(Entry::getKey,
						entry -> List.copyOf(entry.getValue())));
	}

	public static HtmlEntities instance() {
		return instance;
	}

	private final Map<String, List<String>> nameToNumericEntityReferences;

	private final Map<String, String> nameToStringEquivalent;

	private HtmlEntities() {
		nameToNumericEntityReferences = readHtmlEntities();
		nameToStringEquivalent = createNameToStringEquivalent(nameToNumericEntityReferences);
	}

	public List<String> nameToEntityReferences(String name) {
		List<String> list = nameToNumericEntityReferences.get(name);
		return list != null ? list : Collections.emptyList();
	}

	public String nameToStringEquivalent(String name) {
		return nameToStringEquivalent.get(name);
	}

	private Map<String, String> createNameToStringEquivalent(
			Map<String, List<String>> nameToNumericEntityReferences) {
		return nameToNumericEntityReferences.entrySet()
				.stream()
				.collect(Collectors.toUnmodifiableMap(Entry::getKey, entry -> stringEquivalent(entry.getValue())));
	}

	private String stringEquivalent(Collection<String> collection) {
		return Normalizer.normalize(
				collection.stream().map(this::numericEntityToString).collect(Collectors.joining()),
				Normalizer.Form.NFC);
	}

	private String numericEntityToString(String s) {
		checkArgument(s.charAt(0) == '#');
		return String.valueOf((char) Integer.parseInt(s.substring(1)));
	}
}
