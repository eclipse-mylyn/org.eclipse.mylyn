/******************************************************************************
 *  Copyright (c) 2011 GitHub Inc.
 *  All rights reserved. This program and the accompanying materials
 *  are made available under the terms of the Eclipse Public License v1.0
 *  which accompanies this distribution, and is available at
 *  http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *    Kevin Sawicki (GitHub Inc.) - initial API and implementation
 *****************************************************************************/
package org.eclipse.egit.github.core.service;

import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_DOWNLOADS;
import static org.eclipse.egit.github.core.client.IGitHubConstants.SEGMENT_REPOS;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_FIRST;
import static org.eclipse.egit.github.core.client.PagedRequest.PAGE_SIZE;

import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ProxySelector;
import java.util.List;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.ProxySelectorRoutePlanner;
import org.eclipse.egit.github.core.Download;
import org.eclipse.egit.github.core.DownloadResource;
import org.eclipse.egit.github.core.IRepositoryIdProvider;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.client.GitHubRequest;
import org.eclipse.egit.github.core.client.PageIterator;
import org.eclipse.egit.github.core.client.PagedRequest;

/**
 * Service for accessing, creating, and deleting repositories downloads.
 *
 * @see <a href="http://developer.github.com/v3/repos/downloads">GitHub
 *      downloads API documentation</a>
 */
public class DownloadService extends GitHubService {

	/**
	 * UPLOAD_KEY
	 */
	public static final String UPLOAD_KEY = "key"; //$NON-NLS-1$

	/**
	 * UPLOAD_ACL
	 */
	public static final String UPLOAD_ACL = "acl"; //$NON-NLS-1$

	/**
	 * UPLOAD_SUCCESS_ACTION_STATUS
	 */
	public static final String UPLOAD_SUCCESS_ACTION_STATUS = "success_action_status"; //$NON-NLS-1$

	/**
	 * UPLOAD_FILENAME
	 */
	public static final String UPLOAD_FILENAME = "Filename"; //$NON-NLS-1$

	/**
	 * UPLOAD_AWS_ACCESS_KEY_ID
	 */
	public static final String UPLOAD_AWS_ACCESS_KEY_ID = "AWSAccessKeyId"; //$NON-NLS-1$

	/**
	 * UPLOAD_POLICY
	 */
	public static final String UPLOAD_POLICY = "Policy"; //$NON-NLS-1$

	/**
	 * UPLOAD_SIGNATURE
	 */
	public static final String UPLOAD_SIGNATURE = "Signature"; //$NON-NLS-1$

	/**
	 * UPLOAD_FILE
	 */
	public static final String UPLOAD_FILE = "file"; //$NON-NLS-1$

	private static class SizedInputStreamBody extends InputStreamBody {

		private final long size;

		/**
		 * @param in
		 * @param size
		 */
		public SizedInputStreamBody(InputStream in, long size) {
			super(in, null);
			this.size = size;
		}

		public long getContentLength() {
			return size;
		}
	}

	/**
	 * Create download service
	 */
	public DownloadService() {
		super();
	}

	/**
	 * Create download service
	 *
	 * @param client
	 */
	public DownloadService(GitHubClient client) {
		super(client);
	}

	/**
	 * Get download metadata for given repository and id
	 *
	 * @param repository
	 * @param id
	 * @return download
	 * @throws IOException
	 */
	public Download getDownload(IRepositoryIdProvider repository, int id)
			throws IOException {
		final String repoId = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_DOWNLOADS);
		uri.append('/').append(id);
		GitHubRequest request = createRequest();
		request.setUri(uri);
		request.setType(Download.class);
		return (Download) client.get(request).getBody();
	}

	/**
	 * Create paged downloads request
	 *
	 * @param repository
	 * @param start
	 * @param size
	 * @return request
	 */
	protected PagedRequest<Download> createDownloadsRequest(
			IRepositoryIdProvider repository, int start, int size) {
		final String repoId = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_DOWNLOADS);
		PagedRequest<Download> request = createPagedRequest(start, size);
		request.setType(new TypeToken<List<Download>>() {
		}.getType());
		request.setUri(uri);
		return request;
	}

	/**
	 * Get metadata for all downloads for given repository
	 *
	 * @param repository
	 * @return non-null but possibly empty list of download metadata
	 * @throws IOException
	 */
	public List<Download> getDownloads(IRepositoryIdProvider repository)
			throws IOException {
		PagedRequest<Download> request = createDownloadsRequest(repository,
				PAGE_FIRST, PAGE_SIZE);
		return getAll(request);
	}

	/**
	 * Page metadata for downloads for given repository
	 *
	 * @param repository
	 * @return iterator over pages of downloads
	 */
	public PageIterator<Download> pageDownloads(IRepositoryIdProvider repository) {
		return pageDownloads(repository, PAGE_SIZE);
	}

	/**
	 * Page downloads for given repository
	 *
	 * @param repository
	 * @param size
	 * @return iterator over pages of downloads
	 */
	public PageIterator<Download> pageDownloads(
			IRepositoryIdProvider repository, int size) {
		return pageDownloads(repository, PAGE_FIRST, size);
	}

	/**
	 * Page downloads for given repository
	 *
	 * @param repository
	 * @param start
	 * @param size
	 * @return iterator over pages of downloads
	 */
	public PageIterator<Download> pageDownloads(
			IRepositoryIdProvider repository, int start, int size) {
		PagedRequest<Download> request = createDownloadsRequest(repository,
				start, size);
		return createPageIterator(request);
	}

	/**
	 * Delete download with given id from given repository
	 *
	 * @param repository
	 * @param id
	 * @throws IOException
	 */
	public void deleteDownload(IRepositoryIdProvider repository, int id)
			throws IOException {
		final String repoId = getId(repository);
		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_DOWNLOADS);
		uri.append('/').append(id);
		client.delete(uri.toString());
	}

	/**
	 * Create a new resource for download associated with the given repository
	 *
	 * @param repository
	 * @param download
	 * @return download resource
	 * @throws IOException
	 */
	public DownloadResource createResource(IRepositoryIdProvider repository,
			Download download) throws IOException {
		final String repoId = getId(repository);
		if (download == null)
			throw new IllegalArgumentException("Download cannot be null"); //$NON-NLS-1$

		StringBuilder uri = new StringBuilder(SEGMENT_REPOS);
		uri.append('/').append(repoId);
		uri.append(SEGMENT_DOWNLOADS);
		return (DownloadResource) client.post(uri.toString(), download,
				DownloadResource.class);
	}

	/**
	 * Upload a resource to be available as the download described by the given
	 * resource.
	 *
	 * @param resource
	 * @param content
	 * @param size
	 * @throws IOException
	 */
	public void uploadResource(DownloadResource resource, InputStream content,
			long size) throws IOException {
		if (resource == null)
			throw new IllegalArgumentException(
					"Download resource cannot be null"); //$NON-NLS-1$

		DefaultHttpClient client = new DefaultHttpClient();
		client.setRoutePlanner(new ProxySelectorRoutePlanner(client
				.getConnectionManager().getSchemeRegistry(), ProxySelector
				.getDefault()));

		HttpPost post = new HttpPost(resource.getS3Url());
		MultipartEntity entity = new MultipartEntity();
		entity.addPart(UPLOAD_KEY, new StringBody(resource.getPath()));
		entity.addPart(UPLOAD_ACL, new StringBody(resource.getAcl()));
		entity.addPart(UPLOAD_SUCCESS_ACTION_STATUS,
				new StringBody(Integer.toString(HttpStatus.SC_CREATED)));
		entity.addPart(UPLOAD_FILENAME, new StringBody(resource.getName()));
		entity.addPart(UPLOAD_AWS_ACCESS_KEY_ID,
				new StringBody(resource.getAccesskeyid()));
		entity.addPart(UPLOAD_POLICY, new StringBody(resource.getPolicy()));
		entity.addPart(UPLOAD_SIGNATURE,
				new StringBody(resource.getSignature()));
		entity.addPart(HttpHeaders.CONTENT_TYPE,
				new StringBody(resource.getMimeType()));
		entity.addPart(UPLOAD_FILE, new SizedInputStreamBody(content, size));
		post.setEntity(entity);

		HttpResponse response = client.execute(post);
		int status = response.getStatusLine().getStatusCode();
		if (status != HttpStatus.SC_CREATED)
			throw new IOException("Unexpected response status of " + status); //$NON-NLS-1$
	}

	/**
	 * Create download and set the content to be the content of given input
	 * stream. This is a convenience method that performs a
	 * {@link #createResource(IRepositoryIdProvider, Download)} followed by a
	 * {@link #uploadResource(DownloadResource, InputStream, long)} with the
	 * results.
	 *
	 * @param repository
	 * @param download
	 *            metadata about the download
	 * @param content
	 *            raw content of the download
	 * @param size
	 *            size of content in the input stream
	 * @throws IOException
	 */
	public void createDownload(IRepositoryIdProvider repository,
			Download download, InputStream content, long size)
			throws IOException {
		DownloadResource resource = createResource(repository, download);
		uploadResource(resource, content, size);
	}

	/**
	 * Create download from content of given file.
	 *
	 * @see #createDownload(IRepositoryIdProvider, Download, InputStream, long)
	 * @param repository
	 * @param download
	 *            metadata about the download
	 * @param file
	 *            must be non-null
	 * @throws IOException
	 */
	public void createDownload(IRepositoryIdProvider repository,
			Download download, File file) throws IOException {
		if (file == null)
			throw new IllegalArgumentException("File cannot be null"); //$NON-NLS-1$

		createDownload(repository, download, new FileInputStream(file),
				file.length());
	}
}
