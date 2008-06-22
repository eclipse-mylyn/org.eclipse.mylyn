/*******************************************************************************
 * Copyright (c) 2005, 2007 Mylyn project committers and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *******************************************************************************/
package org.eclipse.mylyn.trac.tests.support;

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
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class TestProxy implements Runnable {

	public static final String NOT_FOUND = "HTTP/1.1 404 Not Found";

	private int listenPort;

	private Message request;

	private Message response;

	private Thread runner;

	private IOException exception;

	private volatile boolean stopped = false;

	public TestProxy(int listenPort) {
		this.listenPort = listenPort;
	}

	public TestProxy() {
	}

	public synchronized int getListenPort() throws InterruptedException {
		while (listenPort == 0) {
			wait();
		}
		return listenPort;
	}

	public void start() {
		runner = new Thread(this, "TestProxy :" + listenPort);
		runner.start();
	}

	public int startAndWait() throws InterruptedException {
		start();
		int port = getListenPort();
		// wait for socket to enter accept call
		Thread.sleep(100);
		return port;
	}

	public void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(listenPort);
			synchronized (this) {
				listenPort = serverSocket.getLocalPort();
				notifyAll();
			}
			while (!stopped) {
				Socket socket = serverSocket.accept();
				try {
					Message request = readMessage(socket.getInputStream());
					setRequest(request);

					Message response = waitForResponse();
					writeMessage(response, socket.getOutputStream());
				} finally {
					try {
						socket.close();
					} catch (IOException e1) {
					}
				}
			}
		} catch (InterruptedIOException e) {
		} catch (IOException e) {
			setException(e);
		} catch (InterruptedException e) {
		} finally {
			if (serverSocket != null) {
				try {
					serverSocket.close();
				} catch (IOException e) {
				}
			}
		}

	}

	private void writeMessage(Message message, OutputStream out) throws IOException {
		BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(out));
		writer.write(message.toString());
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
				StringTokenizer t = new StringTokenizer(line, ":");
				message.headers.put(t.nextToken(), t.nextToken().trim());
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

	public Message getRequest() {
		return request;
	}

	public synchronized Message waitForRequest() throws InterruptedException {
		while (request == null) {
			wait();
		}
		return request;
	}

	public synchronized Message waitForResponse() throws InterruptedException {
		while (response == null) {
			wait();
		}
		return response;
	}

	public synchronized void setResponse(Message response) {
		this.response = response;
		notifyAll();
	}

	public synchronized void setResponse(String response) {
		this.response = new Message(response);
		notifyAll();
	}

	public synchronized void setRequest(Message request) {
		this.request = request;
		notifyAll();
	}

	public static class Message {

		public Message(String request) {
			this.request = request;
		}

		public String request;

		public Map<String, String> headers = new HashMap<String, String>();

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append(request);
			sb.append("\n");
			if (headers != null) {
				for (String key : headers.keySet()) {
					sb.append(key + ": " + headers.get(key));
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

	}

	public static void main(String[] args) {
		TestProxy proxy = new TestProxy(8080);
		proxy.start();
		try {
			proxy.setResponse(new Message("404 / HTTP 1.1"));
			try {
				System.out.println(proxy.waitForRequest());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			proxy.stop();
		}

	}

}
