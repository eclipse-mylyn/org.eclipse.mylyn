/*******************************************************************************
 * Copyright (c) 2006 - 2006 Mylar eclipse.org project and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mylar project committers - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.trac.tests;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;

import junit.framework.TestCase;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.eclipse.mylyn.context.tests.support.TestUtil;
import org.eclipse.mylyn.context.tests.support.TestUtil.Credentials;
import org.eclipse.mylyn.context.tests.support.TestUtil.PrivilegeLevel;
import org.eclipse.mylyn.internal.trac.core.util.TracHttpClientTransportFactory;

/**
 * Test cases for <a href="http://trac-hacks.org/wiki/XmlRpcPlugin">Trac XML-RPC
 * Plugin</a> API. Revision 1188 or higher is required.
 * 
 * <p>
 * This class does not depend on any Mylar (connector) classes except for
 * TracHttpClientTransportFactory which is needed for initialization of
 * HttpClient.
 * 
 * @author Steffen Pingel
 */
public class TracXmlRpcTest extends TestCase {

	public static final String XMLRPC_URL = "/login/xmlrpc";

	private XmlRpcClient xmlrpc;

	private String username;

	// private String password;

	private Random random;

	private ArrayList<Integer> tickets;

	@Override
	protected void setUp() throws Exception {
		super.setUp();

		random = new Random();

		Credentials credentials = TestUtil.readCredentials(PrivilegeLevel.ADMIN);
		createConnection(new URL(TracTestConstants.TEST_TRAC_010_URL + XMLRPC_URL), credentials.username, credentials.password);

		tickets = new ArrayList<Integer>();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();

		for (int id : tickets) {
			call("ticket.delete", id);
		}
	}

	private void createConnection(URL url, String username, String password) throws Exception {
		XmlRpcClientConfigImpl config = new XmlRpcClientConfigImpl();
		config.setEncoding("UTF-8");
		config.setBasicUserName(username);
		config.setBasicPassword(password);
		config.setServerURL(url);

		xmlrpc = new XmlRpcClient();
		xmlrpc.setConfig(config);

		TracHttpClientTransportFactory factory = new TracHttpClientTransportFactory(xmlrpc);
		xmlrpc.setTransportFactory(factory);

		this.username = username;
		// this.password = password;
	}

	private int createTicket(String summary, String description, Map<String, Object> attributes)
			throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", summary, description, attributes);
		tickets.add(id);
		return id;
	}

	private Object call(String method, Object... parameters) throws XmlRpcException, IOException {
		Object result = xmlrpc.execute(method, parameters);
		if (result instanceof XmlRpcException) {
			throw (XmlRpcException) result;
		}
		return result;
	}

	public Map<String, Object> createMultiCall(String methodName, Object... parameters) throws XmlRpcException,
			IOException {
		Map<String, Object> table = new Hashtable<String, Object>();
		table.put("methodName", methodName);
		table.put("params", parameters);
		return table;
	}

	private void internalTestCrud(String module) throws XmlRpcException, IOException {
		try {
			call(module + ".delete", "foo");
		} catch (XmlRpcException e) {
		}

		call(module + ".create", "foo", "bar");
		try {
			assertHasValue((Object[]) call(module + ".getAll"), "foo");
			assertEquals("bar", (String) (call(module + ".get", "foo")));

			call(module + ".update", "foo", "baz");
			assertEquals("baz", (String) (call(module + ".get", "foo")));
		} finally {
			call(module + ".delete", "foo");
		}
	}

	private Object createValue(Object fieldName, Object clazz) {
		if (clazz == String.class) {
			return fieldName.toString() + random.nextInt();
		} else if (clazz == Date.class) {
			return new Date();
		} else if (clazz == Boolean.class) {
			return random.nextBoolean();
		} else if (clazz == Double.class) {
			return random.nextDouble();
		} else if (clazz == Integer.class) {
			return random.nextInt();
		}

		throw new RuntimeException("Invalid test data: " + fieldName + ", " + clazz);
	}

	private void internalTestComponent(String module, Object... fields) throws XmlRpcException, IOException {
		try {
			call(module + ".delete", "foo");
		} catch (XmlRpcException e) {
		}

		Map<String, Object> attributes = new Hashtable<String, Object>();
		for (int i = 0; i < fields.length; i += 2) {
			attributes.put((String) fields[i], createValue(fields[i], fields[i + 1]));
		}

		call(module + ".create", "foo", attributes);

		try {
			assertHasValue((Object[]) call(module + ".getAll"), "foo");
			Map<?, ?> values = (Map<?, ?>) call(module + ".get", "foo");
			for (String attribute : attributes.keySet()) {
				assertEquals(attributes.get(attribute), values.get(attribute));
			}

			for (int i = 0; i < fields.length; i += 2) {
				attributes.put((String) fields[i], createValue(fields[i], fields[i + 1]));
			}

			call(module + ".update", "foo", attributes);
			values = (Map<?, ?>) call(module + ".get", "foo");
			for (String attribute : attributes.keySet()) {
				assertEquals(attributes.get(attribute), values.get(attribute));
			}
		} finally {
			call(module + ".delete", "foo");
		}
	}

	public void testMilestoneDate() throws XmlRpcException, IOException {
		try {
			call("ticket.milestone.delete", "foo");
		} catch (XmlRpcException e) {
		}

		int due = (int) (System.currentTimeMillis() / 1000) + 1000;
		int completed = (int) (System.currentTimeMillis() / 1000);

		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("description", "description");
		attributes.put("due", due);
		attributes.put("completed", completed);

		call("ticket.milestone.create", "foo", attributes);

		Map<?, ?> values = (Map<?, ?>) call("ticket.milestone.get", "foo");
		assertEquals(new Integer(due), (Integer) values.get("due"));
		assertEquals(new Integer(completed), (Integer) values.get("completed"));

		call("ticket.milestone.delete", "foo");
	}

	private void assertHasValue(Object[] items, Object value) {
		for (Object item : items) {
			if (item.equals(value)) {
				return;
			}
		}
		fail("Could not find expected value: " + value);
	}

	private void assertTicketHasAttributes(Map<String, Object> attributes, int id, Object[] ticket) {
		assertTicketHasAttributes(attributes, id, ticket, true);
	}

	private void assertTicketHasAttributes(Map<String, Object> attributes, int id, Object[] ticket, boolean newTicket) {
		assertEquals(id, ticket[0]);
		assertTrue(ticket[1] instanceof Integer); // time created
		// time changed
		if (newTicket) {
			assertEquals(ticket[1], ticket[2]);
		} else {
			assertTrue((Integer) ticket[2] >= (Integer) ticket[1]);
		}
		Map<?, ?> values = (Map<?, ?>) ticket[3];
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}
	}

	public void testGetTicket() throws XmlRpcException, IOException {
		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("type", "task");
		attributes.put("status", "closed");
		int id = createTicket("summary", "description", attributes);

		attributes.put("summary", "summary");
		attributes.put("description", "description");

		Object[] ticket = (Object[]) call("ticket.get", id);
		assertTicketHasAttributes(attributes, id, ticket);
	}

	public void testGetTicketNonExistant() throws XmlRpcException, IOException {
		try {
			call("ticket.delete", Integer.MAX_VALUE);
		} catch (Exception e) {
			// ignore
		}

		try {
			List<?> ticket = (List<?>) call("ticket.get", Integer.MAX_VALUE);
			fail("Expected XmlRpcException, got ticket instead: " + ticket);
		} catch (XmlRpcException e) {
			// ignore
		}
	}

	public void testGetTicketUmlaute() throws XmlRpcException, IOException {
		Map<String, Object> attributes = new Hashtable<String, Object>();
		int id = createTicket("summarya\u0308O\u030b", "\u00d8", attributes);

		attributes.put("summary", "summarya\u0308O\u030b");
		attributes.put("description", "\u00d8");

		Object[] ticket = (Object[]) call("ticket.get", id);
		assertTicketHasAttributes(attributes, id, ticket);
	}

	public void testUpdateTicket() throws XmlRpcException, IOException {
		int id = createTicket("summary", "description", new Hashtable<String, Object>());

		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("summary", "changed");
		call("ticket.update", id, "my comment", attributes);

		attributes.put("description", "description");

		Object[] ticket = (Object[]) call("ticket.get", id);
		Map<?, ?> values = (Map<?, ?>) ticket[3];
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}
	}

	public void testTicketCustomFields() throws XmlRpcException, IOException {
		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("custom_text_field", "myvalue");
		int id = createTicket("summary", "description", attributes);

		// check for default values
		attributes.put("custom_checkbox_field", "1");
		attributes.put("custom_select_field", "two");
		attributes.put("custom_radio_field", "baz");
		attributes.put("custom_textarea_field", "default text");

		Object[] ticket = (Object[]) call("ticket.get", id);
		assertTicketHasAttributes(attributes, id, ticket);

		attributes.put("custom_text_field", "myvalue2");
		attributes.put("custom_checkbox_field", "0");
		attributes.put("custom_select_field", "one");
		attributes.put("custom_radio_field", "foo");
		attributes.put("custom_textarea_field", "mytext");

		call("ticket.update", id, "my comment", attributes);

		ticket = (Object[]) call("ticket.get", id);
		assertTicketHasAttributes(attributes, id, ticket, false);
	}

	public void testGetChangeLog() throws XmlRpcException, IOException {
		int id = createTicket("summary", "description", new Hashtable<String, Object>());

		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("summary", "changed");
		call("ticket.update", id, "my comment", attributes);

		Object[] log = (Object[]) call("ticket.changeLog", id, 0);
		Object[] entry = (Object[]) log[0];
		assertTrue(entry[0] instanceof Integer); // time
		assertEquals(username, entry[1]); // author
		assertEquals("summary", entry[2]); // field
		assertEquals("summary", entry[3]); // old value
		assertEquals("changed", entry[4]); // new value
	}

	public void testMultiGetTicket() throws XmlRpcException, IOException {
		int id1 = createTicket("summary1", "description1", new Hashtable<String, Object>());
		int id2 = createTicket("summary2", "description2", new Hashtable<String, Object>());

		List<Map<?, ?>> calls = new ArrayList<Map<?, ?>>();
		calls.add(createMultiCall("ticket.get", id1));
		calls.add(createMultiCall("ticket.get", id2));
		Object[] ret = (Object[]) call("system.multicall", calls);

		Object[] ticket = (Object[]) ((Object[]) ret[0])[0];
		Map<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("summary", "summary1");
		attributes.put("description", "description1");
		assertTicketHasAttributes(attributes, id1, ticket);

		ticket = (Object[]) ((Object[]) ret[1])[0];
		attributes.clear();
		attributes.put("summary", "summary2");
		attributes.put("description", "description2");
		assertTicketHasAttributes(attributes, id2, ticket);
	}

	public void testAttachment() throws XmlRpcException, IOException {
		int id = createTicket("summary", "description", new Hashtable<String, Object>());

		String filename = (String) call("ticket.putAttachment", id, "attach.txt", "description", "data".getBytes(),
				true);
		// the returned filename may differ, since another ticket may have an
		// attachment named "attach.txt"
		// assertEquals("attach.txt", filename);

		Object[] ret = (Object[]) call("ticket.listAttachments", id);
		assertEquals(1, ret.length);
		Object[] attachment = (Object[]) ret[0];
		assertEquals("attach.txt", attachment[0]);
		assertEquals("description", attachment[1]);
		assertEquals(4, attachment[2]);
		// date
		assertEquals(username, attachment[4]);

		byte[] bytes = (byte[]) call("ticket.getAttachment", id, filename);
		String data = new String(bytes);
		assertEquals("data", data);

		// test override

		String filename2 = (String) call("ticket.putAttachment", id, filename, "newdescription", "newdata".getBytes(),
				true);
		assertEquals(filename, filename2);
		ret = (Object[]) call("ticket.listAttachments", id);
		assertEquals(1, ret.length);
		attachment = (Object[]) ret[0];
		assertEquals("attach.txt", attachment[0]);
		assertEquals("newdescription", attachment[1]);
		assertEquals(7, attachment[2]);
		// date
		assertEquals(username, attachment[4]);
		bytes = (byte[]) call("ticket.getAttachment", id, filename);
		data = new String(bytes);
		assertEquals("newdata", data);

		String filename3 = (String) call("ticket.putAttachment", id, "attach.txt", "description", "data".getBytes(),
				false);
		assertFalse("attach.txt".equals(filename3));
		ret = (Object[]) call("ticket.listAttachments", id);
		assertEquals(2, ret.length);
	}

	public void testDeleteAttachment() throws XmlRpcException, IOException {
		int id = createTicket("summary", "description", new Hashtable<String, Object>());

		String filename = (String) call("ticket.putAttachment", id, "attach.txt", "description", "data".getBytes(),
				true);

		Object[] ret = (Object[]) call("ticket.listAttachments", id);
		assertEquals(1, ret.length);

		call("ticket.deleteAttachment", id, filename);

		ret = (Object[]) call("ticket.listAttachments", id);
		assertEquals(0, ret.length);
	}

	public void testDuplicateAttachment() throws XmlRpcException, IOException {
		int id1 = createTicket("summary", "description", new Hashtable<String, Object>());
		int id2 = createTicket("summary", "description", new Hashtable<String, Object>());

		String filename1 = (String) call("ticket.putAttachment", id1, "attach.txt", "description", "data".getBytes(),
				true);
		String filename2 = (String) call("ticket.putAttachment", id2, "attach.txt", "description", "data2".getBytes(),
				true);
		assertEquals("attach.txt", filename1);
		assertEquals(filename1, filename2);

		byte[] bytes = (byte[]) call("ticket.getAttachment", id1, "attach.txt");
		String data = new String(bytes);
		assertEquals("data", data);

		bytes = (byte[]) call("ticket.getAttachment", id2, "attach.txt");
		data = new String(bytes);
		assertEquals("data2", data);
	}

	public void testQuery() throws XmlRpcException, IOException {
		Object[] ret = (Object[]) call("ticket.query", "summary~=foo|bar|baz");
		for (Object id : ret) {
			call("ticket.delete", (Integer) id);
		}

		int id1 = createTicket("foobarsum1", "description", new Hashtable<String, Object>());
		int id2 = createTicket("foobaz sum2", "description", new Hashtable<String, Object>());
		int id3 = createTicket("foobarbaz3", "foobarbaz description3", new Hashtable<String, Object>());

		ret = (Object[]) call("ticket.query", "summary=foobarsum1|foobaz sum2");
		assertEquals(2, ret.length);
		assertEquals(id1, ret[0]);
		assertEquals(id2, ret[1]);

		// the first criterium is ignored
		ret = (Object[]) call("ticket.query", "summary~=foobarsum1&summary~=foobaz sum2");
		assertEquals(1, ret.length);
		assertEquals(id2, ret[0]);

		ret = (Object[]) call("ticket.query", "summary~=bar|baz");
		assertEquals(3, ret.length);

		ret = (Object[]) call("ticket.query", "description~=foobarbaz description3");
		assertEquals(1, ret.length);
		assertEquals(id3, ret[0]);
	}

	public void testQueryAll() throws XmlRpcException, IOException {
		int id = createTicket("foo", "description", new Hashtable<String, Object>());

		Object[] ret = (Object[]) call("ticket.query", "order=id");
		assertTrue(ret.length > 0);
		assertHasValue(ret, id);
	}

	public void testPriorities() throws XmlRpcException, IOException {
		internalTestCrud("ticket.priority");
	}

	public void testSeverities() throws XmlRpcException, IOException {
		internalTestCrud("ticket.severity");
	}

	public void testType() throws XmlRpcException, IOException {
		internalTestCrud("ticket.type");
	}

	public void testStatus() throws XmlRpcException, IOException {
		internalTestCrud("ticket.status");
	}

	public void testResolutions() throws XmlRpcException, IOException {
		internalTestCrud("ticket.resolution");
	}

	public void testVersions() throws XmlRpcException, IOException {
		internalTestComponent("ticket.version", "time", Integer.class, "description", String.class);
	}

	public void testComponents() throws XmlRpcException, IOException {
		internalTestComponent("ticket.component", "owner", String.class, "description", String.class);
	}

	public void testMilestones() throws XmlRpcException, IOException {
		internalTestComponent("ticket.milestone", "due", Integer.class, "completed", Integer.class, "description",
				String.class);
	}

}
