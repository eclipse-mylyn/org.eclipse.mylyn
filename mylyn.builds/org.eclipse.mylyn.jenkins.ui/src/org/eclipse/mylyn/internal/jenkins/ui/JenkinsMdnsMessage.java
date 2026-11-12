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

package org.eclipse.mylyn.internal.jenkins.ui;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Minimal encoder/decoder for the parts of the Multicast DNS (RFC 6762) wire format needed to discover Jenkins servers.
 */
public final class JenkinsMdnsMessage {

	static final String JENKINS_SERVICE_TYPE = "_jenkins._tcp.local"; //$NON-NLS-1$

	private static final int TYPE_PTR = 12;

	private static final int TYPE_TXT = 16;

	private static final int CLASS_IN = 1;

	private static final int MAX_NAME_POINTERS = 16;

	private JenkinsMdnsMessage() {
	}

	/**
	 * Creates a query asking all Jenkins servers to announce themselves.
	 */
	public static byte[] createQuery() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		// ID, flags, one question, no other records
		out.writeBytes(new byte[] { 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0 });
		for (String label : JENKINS_SERVICE_TYPE.split("\\.")) { //$NON-NLS-1$
			out.write(label.length());
			out.writeBytes(label.getBytes(StandardCharsets.UTF_8));
		}
		out.write(0);
		out.writeBytes(new byte[] { 0, TYPE_PTR, 0, CLASS_IN });
		return out.toByteArray();
	}

	/**
	 * Extracts the TXT record properties of all Jenkins services contained in the given packet.
	 *
	 * @return the properties of each service, empty if the packet contains none or is malformed
	 */
	public static List<Map<String, String>> parseServices(byte[] data, int length) {
		List<Map<String, String>> services = new ArrayList<>();
		try {
			ByteBuffer buffer = ByteBuffer.wrap(data, 0, length);
			buffer.getShort(); // ID
			buffer.getShort(); // flags
			int questions = buffer.getShort() & 0xFFFF;
			int records = (buffer.getShort() & 0xFFFF) + (buffer.getShort() & 0xFFFF) + (buffer.getShort() & 0xFFFF);
			for (int i = 0; i < questions; i++) {
				readName(buffer);
				buffer.position(buffer.position() + 4); // type and class
			}
			for (int i = 0; i < records; i++) {
				String name = readName(buffer);
				int type = buffer.getShort() & 0xFFFF;
				buffer.position(buffer.position() + 6); // class and TTL
				int recordLength = buffer.getShort() & 0xFFFF;
				int end = buffer.position() + recordLength;
				if (type == TYPE_TXT && name.toLowerCase(Locale.ROOT).endsWith("." + JENKINS_SERVICE_TYPE)) { //$NON-NLS-1$
					byte[] txt = new byte[recordLength];
					buffer.get(txt);
					services.add(parseTxtRecord(txt));
				}
				buffer.position(end);
			}
		} catch (RuntimeException e) {
			// malformed packet, e.g. buffer underflow or invalid position; keep what was found so far
		}
		return services;
	}

	/**
	 * Reads a (possibly compressed) domain name and leaves the buffer positioned after it.
	 */
	private static String readName(ByteBuffer buffer) {
		StringBuilder name = new StringBuilder();
		ByteBuffer current = buffer;
		int pointers = 0;
		while (true) {
			int length = current.get() & 0xFF;
			if (length == 0) {
				return name.toString();
			}
			if ((length & 0xC0) == 0xC0) {
				int offset = ((length & 0x3F) << 8) | (current.get() & 0xFF);
				if (++pointers > MAX_NAME_POINTERS) {
					throw new IllegalArgumentException("Too many name pointers"); //$NON-NLS-1$
				}
				if (current == buffer) {
					// the caller continues after the first pointer
					current = buffer.duplicate();
				}
				current.position(offset);
				continue;
			}
			if (name.length() > 0) {
				name.append('.');
			}
			byte[] label = new byte[length];
			current.get(label);
			name.append(new String(label, StandardCharsets.UTF_8));
		}
	}

	private static Map<String, String> parseTxtRecord(byte[] data) {
		Map<String, String> properties = new HashMap<>();
		ByteBuffer buffer = ByteBuffer.wrap(data);
		while (buffer.hasRemaining()) {
			byte[] entry = new byte[Math.min(buffer.get() & 0xFF, buffer.remaining())];
			buffer.get(entry);
			String text = new String(entry, StandardCharsets.UTF_8);
			int separator = text.indexOf('=');
			if (separator > 0) {
				properties.put(text.substring(0, separator), text.substring(separator + 1));
			}
		}
		return properties;
	}

}
