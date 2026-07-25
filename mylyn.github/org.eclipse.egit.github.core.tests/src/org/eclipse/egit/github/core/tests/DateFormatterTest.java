/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License 2.0
 *  which accompanies this distribution, and is available at
 *  https://www.eclipse.org/legal/epl-2.0/
 *
 *  SPDX-License-Identifier: EPL-2.0
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *    See git history
 *****************************************************************************/
package org.eclipse.egit.github.core.tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.eclipse.egit.github.core.client.DateFormatter;
import org.junit.jupiter.api.Test;

import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;

/**
 * Unit tests of {@link DateFormatter}
 */
@SuppressWarnings("nls")
public class DateFormatterTest {

	/**
	 * Verify serialized date returns value deserialized
	 */
	@Test
	public void serializeDeserialize() {
		DateFormatter formatter = new DateFormatter();
		Date date = new Date(10000);
		JsonElement element = formatter.serialize(date, null, null);
		assertNotNull(element);
		String value = element.getAsString();
		assertNotNull(value);
		assertTrue(value.length() > 0);
		Date out = formatter.deserialize(element, null, null);
		assertNotNull(out);
		assertEquals(date.getTime(), out.getTime());
	}

	/**
	 * Deserialize empty string
	 */
	@Test
	public void emptyInput() {
		assertThrows(JsonParseException.class,
				() -> new DateFormatter().deserialize(new JsonPrimitive(""), null, null));
	}
}
