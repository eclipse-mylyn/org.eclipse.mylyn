/*******************************************************************************
 * Copyright (c) 2004, 2012 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v2.0
 * which accompanies this distribution, and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.commons.sdk.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * A server that listens for requests and returns preconfigured responses, used for testing HTTP clients.
 *
 * @author Steffen Pingel
 */
public class MockServer implements Runnable {

	public static String CRLF = "\r\n";

	public static class Message {

		public List<String> headers = new ArrayList<>();

		public String request;

		private String charset;

		public Message(String request) {
			this.request = request;
			charset = "ISO-8859-1";
		}

		public String getCharset() {
			return charset;
		}

		public String getHeader(String prefix) {
			if (headers != null) {
				for (String header : headers) {
					if (header.startsWith(prefix)) {
						return header;
					}
				}
			}
			return null;
		}

		public String getHeaderValue(String prefix) {
			String header = getHeader(prefix);
			if (header != null) {
				int i = header.indexOf(": ");
				return i != -1 ? header.substring(i + 2) : "";
			}
			return null;
		}

		public String getMethod() {
			int i = request.indexOf(" ");
			return i != -1 ? request.substring(0, i) : request;
		}

		public void setCharset(String charset) {
			this.charset = charset;
		}

		public String getStatusLine() {
			int i = request.indexOf("\n");
			return i != -1 ? request.substring(0, i) : request;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(request);
			sb.append("\n");
			if (headers != null) {
				for (String header : headers) {
					sb.append(header);
					sb.append("\n");
				}
			}
			sb.append("\n");
			return sb.toString().replaceAll("\n", CRLF);
		}

	}

	public static final String HEADER_CONNECTION_CLOSE = "Connection: Close";

	public static final String HEADER_NO_CONTENT = "Content-Length: 0";

	public static final Message NOT_FOUND = new Message("HTTP/1.1 404 Not Found");

	public static final Message OK = new Message("HTTP/1.1 200 OK");

	public static final Message TIMEOUT = new Message("HTTP/1.1 200 OK");

	public static final Message UNAUTHORIZED = new Message("HTTP/1.1 401 Unauthorized");

	public static final Message SERVICE_UNVAILABLE = createEmptyMessage("HTTP/1.1 503 Service Unavailable");

	static {
		NOT_FOUND.headers.add(HEADER_CONNECTION_CLOSE);
		OK.headers.add(HEADER_CONNECTION_CLOSE);
		SERVICE_UNVAILABLE.headers.add(HEADER_CONNECTION_CLOSE);
		TIMEOUT.headers.add("Content-Length: 500");
		UNAUTHORIZED.headers.add("WWW-Authenticate: Basic realm=\"Test\"");
	}

	private static Message createEmptyMessage(String status) {
		return new Message(status + "\n" + HEADER_NO_CONTENT + "\n\n");
	}

	private boolean autoClose;

	private IOException exception;

	private final int listenPort;

	private final List<Message> requests = new ArrayList<>();

	private final List<Message> responses = new ArrayList<>();

	private Thread runner;

	private volatile ServerSocket serverSocket;

	private volatile boolean stopped;

	private boolean waitForResponse;

	private boolean closeOnConnect;

	private boolean debugEnabled;

	public MockServer() {
		this(0);
	}

	public MockServer(int listenPort) {
		this.listenPort = listenPort;
		autoClose = true;
	}

	public synchronized void addRequest(Message request) {
		requests.add(request);
		notifyAll();
	}

	public synchronized void addResponse(Message response) {
		responses.add(response);
		notifyAll();
	}

	public synchronized void addResponse(String response) {
		responses.add(new Message(response));
		notifyAll();
	}

	public synchronized void checkForException() throws IOException {
		if (exception != null) {
			throw exception;
		}
	}

	public int getPort() {
		return serverSocket.getLocalPort();
	}

	public synchronized Message getRequest() throws InterruptedException {
		if (requests.isEmpty()) {
			throw new AssertionFailedError("Request list is empty");
		}
		return requests.remove(0);
	}

	private void handleConnection(Socket socket) {
		try {
			while (!closeOnConnect && !stopped) {
				Message request = readMessage(socket.getInputStream());
				if (stopped || request == null) {
					break;
				}
				addRequest(request);

				if (hasMoreResponses() || waitForResponse) {
					Message response = waitForResponse();
					if (stopped || response == null) {
						break;
					}
					writeMessage(response, socket.getOutputStream());

					if (autoClose && response.toString().contains(HEADER_CONNECTION_CLOSE)) {
						break;
					}
				} else {
					writeMessage(SERVICE_UNVAILABLE, socket.getOutputStream());
					System.err.println("Unexpected request: ");
					System.err.println(request.toString());
					break;
				}
			}
		} catch (IOException e) {
			setException(e);
		} catch (InterruptedException e) {
		} finally {
			try {
				socket.close();
			} catch (IOException e1) {
			}
		}
	}

	private synchronized boolean hasMoreResponses() {
		return !responses.isEmpty();
	}

	public synchronized boolean hasRequest() {
		return !requests.isEmpty();
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public boolean isCloseOnConnect() {
		return closeOnConnect;
	}

	private Message readMessage(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		Message message = null;
		String line;
		while ((line = reader.readLine()) != null) {
			if (line.length() == 0) {
				if (message == null) {
					throw new IOException("Incomplete message");
				}
				return message;
			}

			if (message == null) {
				if (isDebugEnabled()) {
					System.err.println("< " + line);
				}
				message = new Message(line);
			} else {
				message.headers.add(line);
			}
		}
		throw new EOFException();
	}

	@Override
	public void run() {
		try {
			serverSocket = new ServerSocket(listenPort);
			while (!stopped) {
				Socket socket = serverSocket.accept();
				handleConnection(socket);
			}
		} catch (InterruptedIOException e) {
			// ignore
		} catch (IOException e) {
			setException(e);
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}

	}

	public void setCloseOnConnect(boolean closeOnConnect) {
		this.closeOnConnect = closeOnConnect;
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	private synchronized void setException(IOException exception) {
		this.exception = exception;
		notifyAll();
	}

	public void setWaitForResponse(boolean waitForResponse) {
		this.waitForResponse = waitForResponse;
	}

	public void start() {
		runner = new Thread(this, "MockServer :" + listenPort);
		runner.start();
	}

	public int startAndWait() throws InterruptedException {
		start();
		while (serverSocket == null || serverSocket.getLocalPort() == -1) {
			Thread.sleep(100);
		}
		return serverSocket.getLocalPort();
	}

	public String getUrl() {
		InetSocketAddress address = new InetSocketAddress("localhost", serverSocket.getLocalPort());
		return "http://" + address.getHostName() + ":" + address.getPort() + "/";
//		try {
//			return "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + serverSocket.getLocalPort();
//		} catch (UnknownHostException e) {
//			return "http://localhost:" + serverSocket.getLocalPort();
//		}
	}

	public void stop() {
		stopped = true;
		try {
			if (serverSocket != null) {
				serverSocket.close();
			}
		} catch (IOException e1) {
			// ignore
		}
		runner.interrupt();
		try {
			runner.join(500);
		} catch (InterruptedException e) {
		}
	}

	public synchronized Message waitForRequest() throws InterruptedException {
		while (requests.isEmpty()) {
			if (stopped) {
				return null;
			}
			wait();
		}
		return requests.remove(0);
	}

	public synchronized Message waitForResponse() throws InterruptedException {
		while (!stopped && responses.isEmpty()) {
			if (stopped || autoClose) {
				return null;
			}
			wait();
		}
		return responses.remove(0);
	}

	private void writeMessage(Message message, OutputStream out) throws IOException {
		if (isDebugEnabled()) {
			System.err.println("> " + message.getStatusLine());
		}

		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out, message.getCharset()));
		writer.write(message.toString());
		writer.flush();
	}

	public boolean isDebugEnabled() {
		return debugEnabled;
	}

	public void setDebugEnabled(boolean debugEnabled) {
		this.debugEnabled = debugEnabled;
	}

}
