/*******************************************************************************
 * Copyright (c) 2004 - 2006 University Of British Columbia and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     University Of British Columbia - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylar.internal.bugzilla.core;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.PartBase;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.internal.tasklist.LocalAttachment;
import org.eclipse.mylar.provisional.tasklist.AbstractRepositoryTask;
import org.eclipse.mylar.provisional.tasklist.IAttachmentHandler;
import org.eclipse.mylar.provisional.tasklist.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaAttachmentHandler implements IAttachmentHandler {

	public static final String POST_ARGS_ATTACHMENT_DOWNLOAD = "/attachment.cgi?id=";

	public static final String POST_ARGS_ATTACHMENT_UPLOAD = "/attachment.cgi";// ?action=insert";//&bugid=";
	
	private static final String VALUE_CONTENTTYPEMETHOD_MANUAL = "manual";

	private static final String VALUE_ISPATCH = "1";

	private static final String VALUE_ACTION_INSERT = "insert";

	private static final String ATTRIBUTE_CONTENTTYPEENTRY = "contenttypeentry";

	private static final String ATTRIBUTE_CONTENTTYPEMETHOD = "contenttypemethod";

	private static final String ATTRIBUTE_ISPATCH = "ispatch";

	private static final String ATTRIBUTE_DATA = "data";

	private static final String ATTRIBUTE_COMMENT = "comment";

	private static final String ATTRIBUTE_DESCRIPTION = "description";

	private static final String ATTRIBUTE_BUGID = "bugid";

	private static final String ATTRIBUTE_BUGZILLA_PASSWORD = "Bugzilla_password";

	private static final String ATTRIBUTE_BUGZILLA_LOGIN = "Bugzilla_login";

	private static final String ATTRIBUTE_ACTION = "action";
	
	public void downloadAttachment(TaskRepository repository, AbstractRepositoryTask task, int attachmentId, File file, Proxy proxySettings) throws CoreException {
		try {
			downloadAttachment(repository.getUrl(), repository.getUserName(), repository.getPassword(), proxySettings, attachmentId, file, true);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "could not download", e));
		}
	}

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment, String description, File file, String contentType, boolean isPatch, Proxy proxySettings) throws CoreException {
		try {
			uploadAttachment(repository.getUrl(), repository.getUserName(), repository.getPassword(), AbstractRepositoryTask.getTaskIdAsInt(task.getHandleIdentifier()), comment, description, file, contentType, isPatch, proxySettings);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, 0, "could not download", e));
		}
	}
	
	private boolean uploadAttachment(String repositoryUrl, String userName, String password, int bugReportID,
			String comment, String description, File sourceFile, String contentType, boolean isPatch, Proxy proxySettings)
			throws IOException {

		// Note: The following debug code requires http commons-logging and
		// commons-logging-api jars
		// System.setProperty("org.apache.commons.logging.Log",
		// "org.apache.commons.logging.impl.SimpleLog");
		// System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
		// "true");
		// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire",
		// "debug");
		// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
		// "debug");

		boolean uploadResult = true;
		
		Protocol.registerProtocol("https", new Protocol("https", new TrustAllSslProtocolSocketFactory(), 443));
		HttpClient client = new HttpClient();
		if (proxySettings != null && proxySettings.address() instanceof InetSocketAddress) {
			InetSocketAddress address = (InetSocketAddress)proxySettings.address();
			client.getHostConfiguration().setProxy(address.getHostName(), address.getPort());
		}
		
		PostMethod postMethod = new PostMethod(repositoryUrl + POST_ARGS_ATTACHMENT_UPLOAD);
		
		// My understanding is that this option causes the client to first check
		// with the server to see if it will in fact recieve the post before
		// actually sending the contents.
		postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE, true);

		try {
			List<PartBase> parts = new ArrayList<PartBase>();
			parts.add(new StringPart(ATTRIBUTE_ACTION, VALUE_ACTION_INSERT));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_LOGIN, userName));
			parts.add(new StringPart(ATTRIBUTE_BUGZILLA_PASSWORD, password));
			parts.add(new StringPart(ATTRIBUTE_BUGID, String.valueOf(bugReportID)));
			parts.add(new StringPart(ATTRIBUTE_DESCRIPTION, description));
			parts.add(new StringPart(ATTRIBUTE_COMMENT, comment));
			parts.add(new FilePart(ATTRIBUTE_DATA, sourceFile));

			if (isPatch) {
				parts.add(new StringPart(ATTRIBUTE_ISPATCH, VALUE_ISPATCH));
			} else {
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEMETHOD, VALUE_CONTENTTYPEMETHOD_MANUAL));
				parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEENTRY, contentType));
			}

			postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new Part[1]), postMethod.getParams()));

			client.getHttpConnectionManager().getParams().setConnectionTimeout(5000);
			int status = client.executeMethod(postMethod);
			if (status == HttpStatus.SC_OK) {
				InputStreamReader reader = new InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod
						.getResponseCharSet());
				BufferedReader bufferedReader = new BufferedReader(reader);
				String newLine;
				while ((newLine = bufferedReader.readLine()) != null) {
					if (newLine.indexOf("Invalid Username Or Password") >= 0) {
						throw new IOException(
								"Invalid Username Or Password - Check credentials in Task Repositories view.");
					}
					// TODO: test for no comment and no description etc.
				}
			} else {
				// MylarStatusHandler.log(HttpStatus.getStatusText(status),
				// BugzillaRepositoryUtil.class);
				uploadResult = false;
			}
			// } catch (HttpException e) {
			// MylarStatusHandler.log("Attachment upload failed\n" +
			// e.getMessage(), BugzillaRepositoryUtil.class);
			// uploadResult = false;
		} finally {
			postMethod.releaseConnection();
		}

		return uploadResult;
	}
	

	public boolean uploadAttachment(LocalAttachment attachment, String uname, String password, Proxy proxySettings) throws IOException {
		
		File file = new File(attachment.getFilePath());
		if (!file.exists() || file.length() <= 0) {
			return false;
		}
		
		return uploadAttachment(attachment.getReport().getRepositoryUrl(), uname, password, attachment.getReport().getId(),
				attachment.getComment(), attachment.getDescription(), file,
				attachment.getContentType(), attachment.isPatch(), proxySettings);
	}
	
	private boolean downloadAttachment(String repositoryUrl, String userName, String password,
			Proxy proxySettings, int id, File destinationFile, boolean overwrite) throws IOException,
			GeneralSecurityException {
		BufferedInputStream in = null;
		FileOutputStream outStream = null;
		try {
			String url = repositoryUrl + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
			url = BugzillaRepositoryUtil.addCredentials(url, userName, password);
			URL downloadUrl = new URL(url);
			URLConnection connection = BugzillaPlugin.getUrlConnection(downloadUrl, proxySettings);
			if (connection != null) {
				InputStream input = connection.getInputStream();
				outStream = new FileOutputStream(destinationFile);
				copyByteStream(input, outStream);
				return true;

			}
		} finally {
			try {
				if (in != null)
					in.close();
				if (outStream != null)
					outStream.close();
			} catch (IOException e) {
				BugzillaPlugin.log(new Status(IStatus.ERROR, BugzillaPlugin.PLUGIN_ID, IStatus.ERROR,
						"Problem closing the stream", e));
			}
		}
		return false;
	}
	
	private void copyByteStream(InputStream in, OutputStream out) throws IOException {
		if (in != null && out != null) {
			BufferedInputStream inBuffered = new BufferedInputStream(in);

			int bufferSize = 1000;
			byte[] buffer = new byte[bufferSize];

			int readCount;

			BufferedOutputStream fout = new BufferedOutputStream(out);

			while ((readCount = inBuffered.read(buffer)) != -1) {
				if (readCount < bufferSize) {
					fout.write(buffer, 0, readCount);
				} else {
					fout.write(buffer);
				}
			}
			fout.flush();
			fout.close();
			in.close();
		}
	}
}
