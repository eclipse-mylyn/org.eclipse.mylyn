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

package org.eclipse.mylar.trac.tests;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.Hashtable;
import java.util.Random;
import java.util.Vector;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import junit.framework.TestCase;

import org.apache.xmlrpc.DefaultXmlRpcTransportFactory;
import org.apache.xmlrpc.XmlRpc;
import org.apache.xmlrpc.XmlRpcClient;
import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.mylar.trac.tests.support.TrustAll;

/**
 * Test cases for Trac XML-RPC Plugin API. This class does not depend on any of
 * the Mylar connector classes.
 */
public class TracXmlRpcTest extends TestCase {

	public static final String XMLRPC_URL = "/login/xmlrpc";

	private XmlRpcClient xmlrpc;

	private String username;

//	private String password;

	private Random random;

	protected void setUp() throws Exception {
		super.setUp();

		random = new Random();

		XmlRpc.setDefaultInputEncoding("UTF-8");

		createConnection(new URL(Constants.TEST_REPOSITORY1_URL + XMLRPC_URL),
				Constants.TEST_REPOSITORY1_ADMIN_USERNAME, Constants.TEST_REPOSITORY1_ADMIN_PASSWORD);
	}

	@SuppressWarnings("deprecation")
	private void createConnection(URL url, String username, String password) throws Exception {
		if (url.toString().startsWith("https")) {
			DefaultXmlRpcTransportFactory transport = new DefaultXmlRpcTransportFactory(url);
			transport.setBasicAuthentication(username, password);

			SSLContext ctx = SSLContext.getInstance("TLS");

			javax.net.ssl.TrustManager[] tm = new javax.net.ssl.TrustManager[] { new TrustAll() };
			ctx.init(null, tm, null);
			HttpsURLConnection.setDefaultSSLSocketFactory(ctx.getSocketFactory());

			xmlrpc = new XmlRpcClient(url, transport);
		} else {
			xmlrpc = new XmlRpcClient(url);

			// the XML-RPC library is kind of broken: setting authentication
			// credentials for
			// http connections is only possible through a deprecated method
			xmlrpc.setBasicAuthentication(username, password);
		}

		this.username = username;
//		this.password = password;
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	private Object call(String method, Object... parameters) throws XmlRpcException, IOException {
		Vector<Object> params = new Vector<Object>();
		for (Object parameter : parameters) {
			params.add(parameter);
		}

		Object result = xmlrpc.execute(method, params);
		if (result instanceof XmlRpcException) {
			throw (XmlRpcException) result;
		}
		return result;
	}

	public Hashtable<String, Object> createMultiCall(String methodName, Object... parameters) throws XmlRpcException,
			IOException {
		Hashtable<String, Object> table = new Hashtable<String, Object>();
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

		assertHasValue(((Vector) call(module + ".getAll")).toArray(), "foo");
		assertEquals("bar", (String) (call(module + ".get", "foo")));

		call(module + ".update", "foo", "baz");
		assertEquals("baz", (String) (call(module + ".get", "foo")));

		call(module + ".delete", "foo");
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

		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		for (int i = 0; i < fields.length; i += 2) {
			attributes.put((String) fields[i], createValue(fields[i], fields[i + 1]));
		}

		call(module + ".create", "foo", attributes);

		assertHasValue(((Vector) call(module + ".getAll")).toArray(), "foo");
		Hashtable values = (Hashtable) call(module + ".get", "foo");
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}

		for (int i = 0; i < fields.length; i += 2) {
			attributes.put((String) fields[i], createValue(fields[i], fields[i + 1]));
		}

		call(module + ".update", "foo", attributes);
		values = (Hashtable) call(module + ".get", "foo");
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}

		call(module + ".delete", "foo");
	}

	public void testMilestoneDate() throws XmlRpcException, IOException {
		try {
			call("ticket.milestone.delete", "foo");
		} catch (XmlRpcException e) {
		}

		int due = (int) (System.currentTimeMillis() / 1000) + 1000;
		int completed = (int) (System.currentTimeMillis() / 1000);

		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("description", "description");
		attributes.put("due", due);
		attributes.put("completed", completed);

		call("ticket.milestone.create", "foo", attributes);

		Hashtable values = (Hashtable) call("ticket.milestone.get", "foo");
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

	public void testGetTicket() throws XmlRpcException, IOException {
		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("type", "task");
		attributes.put("status", "closed");
		int id = (Integer) call("ticket.create", "summary", "description", attributes);

		attributes.put("summary", "summary");
		attributes.put("description", "description");

		Vector ticket = (Vector) call("ticket.get", id);
		assertEquals(id, ticket.get(0));
		assertTrue(ticket.get(1) instanceof Integer); // time created
		assertEquals(ticket.get(1), ticket.get(2)); // time changed
		Hashtable values = (Hashtable) ticket.get(3);
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}

		call("ticket.delete", id);
	}

	public void testGetTicketUmlaute() throws XmlRpcException, IOException {
		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		int id = (Integer) call("ticket.create", "summary‰÷‹", "ﬂﬂﬂ", attributes);

		attributes.put("summary", "summary‰÷‹");
		attributes.put("description", "ﬂﬂﬂ");

		Vector ticket = (Vector) call("ticket.get", id);
		assertEquals(id, ticket.get(0));
		assertTrue(ticket.get(1) instanceof Integer); // time created
		assertEquals(ticket.get(1), ticket.get(2)); // time changed
		Hashtable values = (Hashtable) ticket.get(3);
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}

		call("ticket.delete", id);
	}

	public void testUpdateTicket() throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", "summary", "description", new Hashtable());

		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("summary", "changed");
		call("ticket.update", id, "my comment", attributes);

		attributes.put("description", "description");

		Vector ticket = (Vector) call("ticket.get", id);
		Hashtable values = (Hashtable) ticket.get(3);
		for (String attribute : attributes.keySet()) {
			assertEquals(attributes.get(attribute), values.get(attribute));
		}

		call("ticket.delete", id);
	}

	public void testGetChangeLog() throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", "summary", "description", new Hashtable());

		Hashtable<String, Object> attributes = new Hashtable<String, Object>();
		attributes.put("summary", "changed");
		call("ticket.update", id, "my comment", attributes);

		Vector log = (Vector) call("ticket.changeLog", id, 0);
		Vector entry = (Vector) log.get(0);
		assertTrue(entry.get(0) instanceof Integer); // time
		assertEquals(username, entry.get(1)); // author
		assertEquals("summary", entry.get(2)); // field
		assertEquals("summary", entry.get(3)); // old value
		assertEquals("changed", entry.get(4)); // new value

		call("ticket.delete", id);
	}

	public void testMultiGetTicket() throws XmlRpcException, IOException {
		Object[] calls = new Object[] { createMultiCall("ticket.get", 1), createMultiCall("ticket.get", 2), };

		Vector ret = (Vector) call("system.multicall", new Object[] { calls });

		Vector ticket = (Vector) ret.get(0);
		assertEquals(1, ticket.get(0));

		ticket = (Vector) ret.get(1);
		assertEquals(2, ticket.get(0));
	}

	public void testAttachment() throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", "summary", "description", new Hashtable());

		String filename = (String) call("ticket.putAttachment", id, "attach.txt", "data".getBytes(), true);
		// the returned filename may differ, since another ticket may have an
		// attachment named "attach.txt"
		// assertEquals("attach.txt", filename);

		Vector ret = (Vector) call("ticket.listAttachments", id);
		assertEquals(1, ret.size());
		assertHasValue(ret.toArray(), filename);

		byte[] bytes = (byte[]) call("ticket.getAttachment", id, filename);
		String data = new String(bytes);
		assertEquals("data", data);

		String filename2 = (String) call("ticket.putAttachment", id, filename, "data".getBytes(), true);
		assertEquals(filename, filename2);
		ret = (Vector) call("ticket.listAttachments", id);
		assertEquals(1, ret.size());
		assertHasValue(ret.toArray(), filename);

		String filename3 = (String) call("ticket.putAttachment", id, "attach.txt", "data".getBytes(), false);
		assertFalse("attach.txt".equals(filename3));
		ret = (Vector) call("ticket.listAttachments", id);
		assertEquals(2, ret.size());
		assertHasValue(ret.toArray(), filename);
		assertHasValue(ret.toArray(), filename3);

		call("ticket.delete", id);
	}

	public void testDeleteAttachment() throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", "summary", "description", new Hashtable());

		String filename = (String) call("ticket.putAttachment", id, "attach.txt", "data".getBytes(), true);

		Vector ret = (Vector) call("ticket.listAttachments", id);
		assertEquals(1, ret.size());
		assertHasValue(ret.toArray(), filename);

		call("ticket.deleteAttachment", id, filename);

		ret = (Vector) call("ticket.listAttachments", id);
		assertEquals(0, ret.size());

		call("ticket.delete", id);
	}

	public void testQuery() throws XmlRpcException, IOException {
		Vector ret = (Vector) call("ticket.query", "summary=foobarsummary1|foobaz summary2");
		for (Object id : ret) {
			call("ticket.delete", (Integer) id);
		}

		int id1 = (Integer) call("ticket.create", "foobarsummary1", "description", new Hashtable());
		int id2 = (Integer) call("ticket.create", "foobaz summary2", "description", new Hashtable());

		ret = (Vector) call("ticket.query", "summary=foobarsummary1|foobaz summary2");
		assertEquals(2, ret.size());
		assertEquals(ret.get(0), id1);
		assertEquals(ret.get(1), id2);

		call("ticket.delete", id1);
		call("ticket.delete", id2);
	}

	public void testQueryAll() throws XmlRpcException, IOException {
		int id = (Integer) call("ticket.create", "foo", "description", new Hashtable());

		Vector ret = (Vector) call("ticket.query", "order=id");
		assertTrue(ret.size() > 0);
		assertHasValue(ret.toArray(), id);

		call("ticket.delete", id);
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
