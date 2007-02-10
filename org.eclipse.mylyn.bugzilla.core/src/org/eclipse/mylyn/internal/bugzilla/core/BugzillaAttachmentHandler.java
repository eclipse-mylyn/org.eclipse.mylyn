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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylar.core.MylarStatusHandler;
import org.eclipse.mylar.tasks.core.AbstractRepositoryTask;
import org.eclipse.mylar.tasks.core.IAttachmentHandler;
import org.eclipse.mylar.tasks.core.IMylarStatusConstants;
import org.eclipse.mylar.tasks.core.MylarStatus;
import org.eclipse.mylar.tasks.core.RepositoryAttachment;
import org.eclipse.mylar.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylar.tasks.core.TaskRepository;

/**
 * @author Mik Kersten
 * @author Rob Elves
 */
public class BugzillaAttachmentHandler implements IAttachmentHandler {

	private BugzillaRepositoryConnector connector;

	public BugzillaAttachmentHandler(BugzillaRepositoryConnector connector) {
		this.connector = connector;
	}

	public byte[] getAttachmentData(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			byte[] data = client.getAttachmentData(attachment.getId());
			return data;
		} catch (IOException e) {
			throw new CoreException(new MylarStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.IO_ERROR, repository.getUrl(), e));			
		}
	}

	public void downloadAttachment(TaskRepository repository, RepositoryAttachment attachment, File file)
			throws CoreException {
		if (repository == null || attachment == null || file == null) {
			MylarStatusHandler.log("Unable to download. Null argument.", this);
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
					"Unable to download attachment", null));
		}
		String filename = attachment.getAttributeValue(RepositoryTaskAttribute.ATTACHMENT_FILENAME);
		if (filename == null) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
					"Attachment download from " + repository.getUrl() + " failed, missing attachment filename.", null));
		}

		try {
			BugzillaClient client = connector.getClientManager().getClient(repository);
			byte[] data = client.getAttachmentData("" + attachment.getId());
			writeData(file, data);
		} catch (Exception e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.PLUGIN_ID, 0,
					"Attachment download from " + repository.getUrl() + " failed.", e));
		}
	}

	private void writeData(File file, byte[] data) throws IOException {
		OutputStream out = new FileOutputStream(file);
		try {
			out.write(data);
		} finally {
			out.close();
		}
	}

	public void uploadAttachment(TaskRepository repository, AbstractRepositoryTask task, String comment,
			String description, File file, String contentType, boolean isPatch) throws CoreException {
		try {
			String bugId = task.getTaskId();
			BugzillaClient client = connector.getClientManager().getClient(repository);
			client.postAttachment(bugId, comment, description, file, contentType, isPatch);
		} catch (IOException e) {
			throw new CoreException(new MylarStatus(Status.ERROR, BugzillaCorePlugin.PLUGIN_ID,
					IMylarStatusConstants.IO_ERROR, repository.getUrl(), e));
		}
	}

	public boolean canDownloadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		return true;
	}

	public boolean canUploadAttachment(TaskRepository repository, AbstractRepositoryTask task) {
		return true;
	}

	public boolean canDeprecate(TaskRepository repository, RepositoryAttachment attachment) {
		return false;
	}

	public void updateAttachment(TaskRepository repository, RepositoryAttachment attachment) throws CoreException {
		// implement
	}
}

// public InputStream getAttachmentInputStream(TaskRepository repository,
// String taskId) throws CoreException {
// try {
// BugzillaClient client =
// connector.getClientManager().getClient(repository);
// return client.getAttachmentInputStream(taskId);
// } catch (Exception e) {
// throw new CoreException(new Status(IStatus.ERROR,
// BugzillaCorePlugin.PLUGIN_ID, 0,
// "Download of attachment "+taskId+" from " + repository.getUrl() + "
// failed.", e));
// }
// }

// private boolean uploadAttachment(String repositoryUrl, String userName,
// String password, int bugReportID,
// String comment, String description, File sourceFile, String contentType,
// boolean isPatch,
// Proxy proxySettings) throws CoreException {
//
// // Note: The following debug code requires http commons-logging and
// // commons-logging-api jars
// // System.setProperty("org.apache.commons.logging.Log",
// // "org.apache.commons.logging.impl.SimpleLog");
// //
// System.setProperty("org.apache.commons.logging.simplelog.showdatetime",
// // "true");
// //
// System.setProperty("org.apache.commons.logging.simplelog.log.httpclient.wire",
// // "debug");
// //
// System.setProperty("org.apache.commons.logging.simplelog.log.org.apache.commons.httpclient",
// // "debug");
//
// boolean uploadResult = true;
//
// // Protocol.registerProtocol("https", new Protocol("https", new
// // TrustAllSslProtocolSocketFactory(), 443));
// HttpClient client = new HttpClient();
// WebClientUtil.setupHttpClient(client, proxySettings, repositoryUrl,
// userName, password);
// PostMethod postMethod = new
// PostMethod(WebClientUtil.getRequestPath(repositoryUrl)
// + POST_ARGS_ATTACHMENT_UPLOAD);
//
// // My understanding is that this option causes the client to first check
// // with the server to see if it will in fact recieve the post before
// // actually sending the contents.
// postMethod.getParams().setBooleanParameter(HttpMethodParams.USE_EXPECT_CONTINUE,
// true);
//
// try {
// List<PartBase> parts = new ArrayList<PartBase>();
// parts.add(new StringPart(ATTRIBUTE_ACTION, VALUE_ACTION_INSERT));
// parts.add(new StringPart(ATTRIBUTE_BUGZILLA_LOGIN, userName));
// parts.add(new StringPart(ATTRIBUTE_BUGZILLA_PASSWORD, password));
// parts.add(new StringPart(ATTRIBUTE_BUGID, String.valueOf(bugReportID)));
// parts.add(new StringPart(ATTRIBUTE_DESCRIPTION, description));
// parts.add(new StringPart(ATTRIBUTE_COMMENT, comment));
// parts.add(new FilePart(ATTRIBUTE_DATA, sourceFile));
//
// if (isPatch) {
// parts.add(new StringPart(ATTRIBUTE_ISPATCH, VALUE_ISPATCH));
// } else {
// parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEMETHOD,
// VALUE_CONTENTTYPEMETHOD_MANUAL));
// parts.add(new StringPart(ATTRIBUTE_CONTENTTYPEENTRY, contentType));
// }
//
// postMethod.setRequestEntity(new MultipartRequestEntity(parts.toArray(new
// Part[1]), postMethod.getParams()));
// postMethod.setDoAuthentication(true);
// client.getHttpConnectionManager().getParams().setConnectionTimeout(CONNECT_TIMEOUT);
// int status = client.executeMethod(postMethod);
// if (status == HttpStatus.SC_OK) {
// InputStreamReader reader = new
// InputStreamReader(postMethod.getResponseBodyAsStream(), postMethod
// .getResponseCharSet());
// BufferedReader bufferedReader = new BufferedReader(reader);
//
// BugzillaClient.parseHtmlError(bufferedReader);
//
// } else {
// uploadResult = false;
// throw new CoreException(new Status(Status.OK,
// BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
// "Communication error occurred during upload. \n\n" +
// HttpStatus.getStatusText(status), null));
// }
//
// } catch (LoginException e) {
// throw new CoreException(new Status(Status.OK,
// BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
// "Your login name or password is incorrect. Ensure proper repository
// configuration.", e));
// } catch (UnrecognizedReponseException e) {
// if (e.getMessage().indexOf(CHANGES_SUBMITTED) > -1) {
// return true;
// }
// throw new CoreException(new Status(Status.OK,
// BugzillaCorePlugin.PLUGIN_ID, Status.INFO,
// "Response from server", e));
// } catch (IOException e) {
// throw new CoreException(new Status(Status.OK,
// BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
// "Check repository credentials and connectivity.", e));
//		
// } catch (BugzillaException e) {
// String message = e.getMessage();
// throw new CoreException(new Status(Status.OK,
// BugzillaCorePlugin.PLUGIN_ID, Status.ERROR,
// "Bugzilla could not post your bug. \n\n" + message, e));
// } finally {
// postMethod.releaseConnection();
// }
//
// return uploadResult;
// }

// public boolean uploadAttachment(LocalAttachment attachment, String uname,
// String password, Proxy proxySettings)
// throws CoreException {
//
// File file = new File(attachment.getFilePath());
// if (!file.exists() || file.length() <= 0) {
// return false;
// }
//
// uploadAttachment(attachment.getReport().getRepositoryUrl(), uname,
// password, Integer.parseInt(attachment
// .getReport().getId()), attachment.getComment(),
// attachment.getDescription(), file, attachment
// .getContentType(), attachment.isPatch(), proxySettings);
// }

// public void downloadAttachment(TaskRepository repository,
// AbstractRepositoryTask task,
// RepositoryAttachment attachment, File file, Proxy proxySettings) throws
// CoreException {
// try {
// BugzillaClient client =
// BugzillaCorePlugin.getDefault().getConnector().getClientManager().getClient(repository);
//
// client.downloadAttachment(attachment.getId(), file, true);
//
// downloadAttachment(repository.getUrl(), repository.getUserName(),
// repository.getPassword(), proxySettings,
// repository.getCharacterEncoding(), attachment.getId(), file, true);
// } catch (Exception e) {
// throw new CoreException(new Status(IStatus.ERROR,
// BugzillaCorePlugin.PLUGIN_ID, 0, "could not download", e));
// }
// }

// private boolean downloadAttachment(String repositoryUrl, String userName,
// String password, Proxy proxySettings,
// String encoding, int id, File destinationFile, boolean overwrite) throws
// IOException,
// GeneralSecurityException {
// BufferedInputStream in = null;
// FileOutputStream outStream = null;
// try {
// String url = repositoryUrl + POST_ARGS_ATTACHMENT_DOWNLOAD + id;
// url = BugzillaClient.addCredentials(url, encoding, userName, password);
// URL downloadUrl = new URL(url);
// URLConnection connection = WebClientUtil.openUrlConnection(downloadUrl,
// proxySettings, false, null, null);
// if (connection != null) {
// InputStream input = connection.getInputStream();
// outStream = new FileOutputStream(destinationFile);
// copyByteStream(input, outStream);
// return true;
//
// }
// } finally {
// try {
// if (in != null)
// in.close();
// if (outStream != null)
// outStream.close();
// } catch (IOException e) {
// BugzillaCorePlugin.log(new Status(IStatus.ERROR,
// BugzillaCorePlugin.PLUGIN_ID, IStatus.ERROR,
// "Problem closing the stream", e));
// }
// }
// return false;
// }
//
// private void copyByteStream(InputStream in, OutputStream out) throws
// IOException {
// if (in != null && out != null) {
// BufferedInputStream inBuffered = new BufferedInputStream(in);
//
// int bufferSize = 1000;
// byte[] buffer = new byte[bufferSize];
//
// int readCount;
//
// BufferedOutputStream fout = new BufferedOutputStream(out);
//
// while ((readCount = inBuffered.read(buffer)) != -1) {
// if (readCount < bufferSize) {
// fout.write(buffer, 0, readCount);
// } else {
// fout.write(buffer);
// }
// }
// fout.flush();
// fout.close();
// in.close();
// }
// }
