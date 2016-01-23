/*******************************************************************************
 * Copyright (c) 2015 Frank Becker and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.rest.core;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.operations.IOperationMonitor;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpResponse;
import org.eclipse.mylyn.commons.repositories.http.core.HttpUtil;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.BugzillaRestIdsResult;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.LoginToken;
import org.eclipse.mylyn.tasks.core.data.AbstractTaskAttachmentSource;
import org.eclipse.mylyn.tasks.core.data.TaskAttachmentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.osgi.util.NLS;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class BugzillaRestPostNewAttachment extends BugzillaRestAuthenticatedPostRequest<BugzillaRestIdsResult> {
	private final String bugReportID;

	private final String comment;

	private final AbstractTaskAttachmentSource source;

	private final TaskAttribute attachmentAttribute;

	public BugzillaRestPostNewAttachment(BugzillaRestHttpClient client, String bugReportID, String comment,
			AbstractTaskAttachmentSource source, TaskAttribute attachmentAttribute, IOperationMonitor monitor) {
		super(client);
		this.bugReportID = bugReportID;
		this.comment = comment;
		this.source = source;
		this.attachmentAttribute = attachmentAttribute;
	}

	@Override
	protected String getUrlSuffix() {
		return "/bug/" + bugReportID + "/attachment"; //$NON-NLS-1$
	}

	List<NameValuePair> requestParameters;

	@Override
	protected void addHttpRequestEntities(HttpRequestBase request) throws BugzillaRestException {
		super.addHttpRequestEntities(request);

		LoginToken token = ((BugzillaRestHttpClient) getClient()).getLoginToken();
		String description = source.getDescription();
		String contentType = source.getContentType();
		String filename = source.getName();
		boolean isPatch = false;

		if (attachmentAttribute != null) {
			TaskAttachmentMapper mapper = TaskAttachmentMapper.createFrom(attachmentAttribute);

			if (mapper.getDescription() != null) {
				description = mapper.getDescription();
			}

			if (mapper.getContentType() != null) {
				contentType = mapper.getContentType();
			}

			if (mapper.getFileName() != null) {
				filename = mapper.getFileName();
			}

			if (mapper.isPatch() != null) {
				isPatch = mapper.isPatch();
			}
		}
		Assert.isNotNull(bugReportID);
		Assert.isNotNull(source);
		Assert.isNotNull(contentType);
		ByteArrayOutputStream outb = new ByteArrayOutputStream();
		InputStream is = null;

		try {
			is = source.createInputStream(null);
			IOUtils.copy(is, outb);
		} catch (CoreException | IOException e) {
			throw new BugzillaRestException(
					"BugzillaRestPostNewAttachment.createHttpRequestBase could not get stream form source", e);
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					throw new BugzillaRestException(
							"BugzillaRestPostNewAttachment.createHttpRequestBase could not cloase stream form source",
							e);
				}
			}
		}

		if (description == null) {
			throw new BugzillaRestException(new CoreException(new Status(IStatus.WARNING, BugzillaRestCore.ID_PLUGIN,
					"Description required when submitting attachments")));
		}
		Base64 base64 = new Base64();
		String dataBase64 = base64.encodeAsString(outb.toByteArray());
		try {
			String gsonString = "{\"Bugzilla_token\":\"" + token.getToken() + "\", \"ids\" : [ " + bugReportID + " ]," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					+ " \"is_patch\" : " + Boolean.toString(isPatch) + "," + " \"summary\" : \"" + description + "\"," //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
					+ " \"content_type\" : \"" + contentType + "\"," + " \"data\" : \"" + dataBase64 + "\"," //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
					+ " \"file_name\" : \"" + filename + "\"," + " \"is_private\" : false}"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
			StringEntity requestEntity = new StringEntity(gsonString);
			((HttpPost) request).setEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			throw new BugzillaRestException(
					"BugzillaRestPostNewAttachment.createHttpRequestBase could not create StringEntity", e);
		}
	}

	@Override
	protected BugzillaRestIdsResult parseFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestIdsResult> type = new TypeToken<BugzillaRestIdsResult>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

	protected BugzillaRestStatus parseErrorFromJson(InputStreamReader in) {
		TypeToken<BugzillaRestStatus> type = new TypeToken<BugzillaRestStatus>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

	@Override
	protected void doValidate(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		int statusCode = response.getStatusCode();
		if (statusCode != 400 && statusCode != 201) {
			if (statusCode == HttpStatus.SC_NOT_FOUND) {
				throw new BugzillaRestResourceNotFoundException(
						NLS.bind("Requested resource ''{0}'' does not exist", response.getRequestPath()));
			}
			throw new BugzillaRestException(NLS.bind("Unexpected response from Bugzilla REST server for ''{0}'': {1}",
					response.getRequestPath(), HttpUtil.getStatusText(statusCode)));
		}

	}

	@Override
	protected BugzillaRestIdsResult doProcess(CommonHttpResponse response, IOperationMonitor monitor)
			throws IOException, BugzillaRestException {
		InputStream is = response.getResponseEntityAsStream();
		InputStreamReader in = new InputStreamReader(is);
		switch (response.getStatusCode()) {
		case HttpURLConnection.HTTP_OK:
		case HttpURLConnection.HTTP_CREATED:
			return parseFromJson(in);
		default:
			BugzillaRestStatus status = parseErrorFromJson(in);
			throw new BugzillaRestException(
					NLS.bind("{2}  (status: {1} from {0})", new String[] { response.getRequestPath(),
							HttpUtil.getStatusText(response.getStatusCode()), status.getMessage() }));
		}
	}
}