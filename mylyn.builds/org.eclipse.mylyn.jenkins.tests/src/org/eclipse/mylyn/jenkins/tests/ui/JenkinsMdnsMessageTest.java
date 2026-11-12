/*******************************************************************************
 * Copyright (c) 2026 Christoph Läubrich and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 *     See git history
 *******************************************************************************/

package org.eclipse.mylyn.jenkins.tests.ui;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.eclipse.mylyn.internal.jenkins.ui.JenkinsMdnsMessage;
import org.junit.jupiter.api.Test;

public class JenkinsMdnsMessageTest {

	@Test
	void createQueryAsksForJenkinsPtrRecords() {
		byte[] expected = { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, //
				8, '_', 'j', 'e', 'n', 'k', 'i', 'n', 's', //
				4, '_', 't', 'c', 'p', //
				5, 'l', 'o', 'c', 'a', 'l', 0, //
				0, 12, 0, 1 };
		assertArrayEquals(expected, JenkinsMdnsMessage.createQuery());
	}

	@Test
	void parsesTxtRecordWithCompressedNames() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// response with question, PTR answer and TXT additional record
		out.writeBytes(new byte[] { 0, 0, (byte) 0x84, 0, 0, 1, 0, 1, 0, 0, 0, 1 });
		int serviceOffset = out.size();
		name(out, "_jenkins", "_tcp", "local");
		out.writeBytes(new byte[] { 0, 12, 0, 1 });
		// PTR answer: service name is a pointer, rdata is "my jenkins" + pointer to service
		out.writeBytes(new byte[] { (byte) 0xC0, (byte) serviceOffset, 0, 12, 0, 1, 0, 0, 0, 120, 0, 13 });
		int instanceOffset = out.size();
		label(out, "my jenkins");
		out.writeBytes(new byte[] { (byte) 0xC0, (byte) serviceOffset });
		// TXT additional record: instance name is a pointer
		byte[] txt = txt("url=http://localhost:8080/", "server-id=abc", "ignored", "empty=");
		out.writeBytes(new byte[] { (byte) 0xC0, (byte) instanceOffset, 0, 16, 0, 1, 0, 0, 0, 120, 0, (byte) txt.length });
		out.writeBytes(txt);

		byte[] data = out.toByteArray();
		List<Map<String, String>> services = JenkinsMdnsMessage.parseServices(data, data.length);

		assertEquals(1, services.size());
		assertEquals(Map.of("url", "http://localhost:8080/", "server-id", "abc", "empty", ""), services.get(0));
	}

	@Test
	void ignoresOtherServicesAndMalformedPackets() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.writeBytes(new byte[] { 0, 0, (byte) 0x84, 0, 0, 0, 0, 1, 0, 0, 0, 0 });
		name(out, "printer", "_ipp", "_tcp", "local");
		byte[] txt = txt("url=http://printer/");
		out.writeBytes(new byte[] { 0, 16, 0, 1, 0, 0, 0, 120, 0, (byte) txt.length });
		out.writeBytes(txt);
		byte[] data = out.toByteArray();
		assertTrue(JenkinsMdnsMessage.parseServices(data, data.length).isEmpty());

		// truncated packet and invalid name pointers must not throw
		assertTrue(JenkinsMdnsMessage.parseServices(data, 15).isEmpty());
		byte[] loop = { 0, 0, (byte) 0x84, 0, 0, 0, 0, 1, 0, 0, 0, 0, (byte) 0xC0, 12, 0, 16 };
		assertTrue(JenkinsMdnsMessage.parseServices(loop, loop.length).isEmpty());
		byte[] outOfBounds = { 0, 0, (byte) 0x84, 0, 0, 0, 0, 1, 0, 0, 0, 0, (byte) 0xC3, (byte) 0xFF, 0, 16 };
		assertTrue(JenkinsMdnsMessage.parseServices(outOfBounds, outOfBounds.length).isEmpty());
	}

	private static void name(ByteArrayOutputStream out, String... labels) {
		for (String label : labels) {
			label(out, label);
		}
		out.write(0);
	}

	private static void label(ByteArrayOutputStream out, String label) {
		byte[] bytes = label.getBytes(StandardCharsets.UTF_8);
		out.write(bytes.length);
		out.writeBytes(bytes);
	}

	private static byte[] txt(String... entries) {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		for (String entry : entries) {
			label(out, entry);
		}
		return out.toByteArray();
	}
}
