/*******************************************************************************
* Copyright (c) 2004, 2008 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.tests;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import junit.framework.AssertionFailedError;

/**
 * @author Steffen Pingel
 */
public class TestProxy implements Runnable {

	public static final String HEADER_CONNECTION_CLOSE = "Connection: Close";

	public static final String HEADER_NO_CONTENT = "Content-Length: 0";

	public static final Message NOT_FOUND = new Message("HTTP/1.1 404 Not Found");
	static {
		NOT_FOUND.headers.add(HEADER_CONNECTION_CLOSE);
	}

	public static final Message OK = new Message("HTTP/1.1 200 OK");
	static {
		OK.headers.add(HEADER_CONNECTION_CLOSE);
	}

	public static final Message SERVICE_UNVAILABLE = new Message("HTTP/1.1 503 Service Unavailable");
	static {
		OK.headers.add(HEADER_CONNECTION_CLOSE);
	}

	private int listenPort;

	private final List<Message> requests = new ArrayList<Message>();

	private final List<Message> responses = new ArrayList<Message>();

	private Thread runner;

	private IOException exception;

	private volatile boolean stopped = false;

	private boolean autoClose = true;

	private volatile ServerSocket serverSocket;

	public TestProxy(int listenPort) {
		this.listenPort = listenPort;
	}

	public TestProxy() {
	}

	public void start() {
		runner = new Thread(this, "TestProxy :" + listenPort);
		runner.start();
	}

	public int startAndWait() throws InterruptedException {
		start();
		while (serverSocket == null || serverSocket.getLocalPort() == -1) {
			Thread.sleep(100);
		}
		return serverSocket.getLocalPort();
	}

	public void run() {
		try {
			serverSocket = new ServerSocket(listenPort);
			while (!stopped) {
				Socket socket = serverSocket.accept();
				handleConnection(socket);
			}
		} catch (InterruptedIOException e) {
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

	private void handleConnection(Socket socket) {
		try {
			while (!stopped && hasMoreResponses()) {
				Message request = readMessage(socket.getInputStream());
				if (stopped || request == null) {
					break;
				}
				addRequest(request);

				Message response = waitForResponse();
				if (stopped || response == null) {
					break;
				}
				writeMessage(response, socket.getOutputStream());
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
		return !(autoClose && responses.isEmpty());
	}

	private void writeMessage(Message message, OutputStream out) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(message.toString());
		writer.flush();
	}

	private synchronized void setException(IOException exception) {
		this.exception = exception;
		notifyAll();
	}

	public synchronized void checkForException() throws IOException {
		if (exception != null) {
			throw exception;
		}
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
				message = new Message(line);
			} else {
				message.headers.add(line);
			}
		}
		throw new EOFException();
	}

	public void stop() {
		stopped = true;
		runner.interrupt();
		try {
			runner.join(500);
		} catch (InterruptedException e) {
		}
	}

	public synchronized boolean hasRequest() {
		return !requests.isEmpty();
	}

	public synchronized Message getRequest() throws InterruptedException {
		if (requests.isEmpty()) {
			throw new AssertionFailedError("Request list is empty");
		}
		return requests.remove(0);
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

	public synchronized void addResponse(Message response) {
		this.responses.add(response);
		notifyAll();
	}

	public synchronized void addResponse(String response) {
		this.responses.add(new Message(response));
		notifyAll();
	}

	public synchronized void addRequest(Message request) {
		this.requests.add(request);
		notifyAll();
	}

	public void setAutoClose(boolean autoClose) {
		this.autoClose = autoClose;
	}

	public boolean isAutoClose() {
		return autoClose;
	}

	public static class Message {

		public Message(String request) {
			this.request = request;
		}

		public String request;

		public List<String> headers = new ArrayList<String>();

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
			return sb.toString();
		}

		public String getMethod() {
			int i = request.indexOf(" ");
			return (i != -1) ? request.substring(0, i) : request;
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
				return (i != -1) ? header.substring(i + 2) : "";
			}
			return null;
		}

	}

}
