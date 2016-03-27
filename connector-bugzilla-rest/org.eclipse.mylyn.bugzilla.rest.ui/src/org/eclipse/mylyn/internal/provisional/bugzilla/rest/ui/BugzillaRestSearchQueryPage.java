package org.eclipse.mylyn.internal.provisional.bugzilla.rest.ui;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Map.Entry;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConfiguration;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestConnector;
import org.eclipse.mylyn.internal.bugzilla.rest.core.BugzillaRestTaskSchema;
import org.eclipse.mylyn.internal.bugzilla.rest.ui.BugzillaRestUiPlugin;
import org.eclipse.mylyn.internal.bugzilla.rest.ui.Messages;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.AbstractQueryPageSchema;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.QueryPageDetails;
import org.eclipse.mylyn.internal.provisional.tasks.ui.wizards.RepositoryQuerySchemaPage;
import org.eclipse.mylyn.tasks.core.IRepositoryQuery;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.ui.editors.AbstractAttributeEditor;
import org.eclipse.osgi.util.NLS;

public class BugzillaRestSearchQueryPage extends RepositoryQuerySchemaPage {

	public BugzillaRestSearchQueryPage(String pageName, TaskRepository repository, IRepositoryQuery query,
			AbstractQueryPageSchema schema, TaskData data, QueryPageDetails pageDetails) {
		super(pageName, repository, query, schema, data, pageDetails);
		if (query != null) {
			setTitle(NLS.bind(Messages.BugzillaRestSearchQueryPage_PropertiesForQuery, query.getSummary()));
		} else {
			setTitle(Messages.BugzillaRestSearchQueryPage_PropertiesForNewQuery);
		}
	}

	@Override
	protected void doRefreshControls() {
		try {
			BugzillaRestConnector connectorREST = (BugzillaRestConnector) getConnector();
			connectorREST.getRepositoryConfiguration(getTaskRepository()).updateProductOptions(getTargetTaskData());

			for (Entry<String, AbstractAttributeEditor> entry : editorMap.entrySet()) {
				entry.getValue().refresh();
			}
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaRestUiPlugin.PLUGIN_ID,
					"BugzillaRestSearchQueryPage could not refresh!", e)); //$NON-NLS-1$
		}
	}

	@Override
	protected boolean hasRepositoryConfiguration() {
		return getRepositoryConfiguration() != null;
	}

	private BugzillaRestConfiguration getRepositoryConfiguration() {
		try {
			return ((BugzillaRestConnector) getConnector()).getRepositoryConfiguration(getTaskRepository());
		} catch (CoreException e) {
			StatusHandler.log(new Status(IStatus.ERROR, BugzillaRestUiPlugin.PLUGIN_ID,
					"BugzillaRestSearchQueryPage could get the RepositoryConfiguration!", e)); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	protected boolean restoreState(IRepositoryQuery query) {
		if (query != null) {
			try {
				restoreStateFromUrl(query.getUrl());
				doRefreshControls();
				return true;
			} catch (UnsupportedEncodingException e) {
				// ignore
			}
		}
		return false;
	}

	@SuppressWarnings("restriction")
	private void restoreStateFromUrl(String queryUrl) throws UnsupportedEncodingException {
		queryUrl = queryUrl.substring(queryUrl.indexOf("?") + 1); //$NON-NLS-1$
		String[] options = queryUrl.split("&"); //$NON-NLS-1$
		for (String option : options) {
			String key;
			int endindex = option.indexOf("="); //$NON-NLS-1$
			if (endindex == -1) {
				key = null;
			} else {
				key = option.substring(0, option.indexOf("=")); //$NON-NLS-1$
			}
			if (key == null || key.equals("order")) { //$NON-NLS-1$
				continue;
			}
			String value = URLDecoder.decode(option.substring(option.indexOf("=") + 1), //$NON-NLS-1$
					getTaskRepository().getCharacterEncoding());
			key = mapAttributeKey(key);
			TaskAttribute attr = getTargetTaskData().getRoot().getAttribute(key);
			if (attr != null) {
				if (getTargetTaskData().getRoot().getAttribute(key).getValue().equals("")) { //$NON-NLS-1$
					getTargetTaskData().getRoot().getAttribute(key).setValue(value);
				} else {
					getTargetTaskData().getRoot().getAttribute(key).addValue(value);
				}
			}
		}
	}

	@Override
	protected String getQueryUrl(String repsitoryUrl) {
		return getQueryURL(repsitoryUrl, getQueryParameters());
	}

	private StringBuilder getQueryParameters() {
		StringBuilder sb = new StringBuilder();
		for (Entry<String, AbstractAttributeEditor> entry : editorMap.entrySet()) {
			TaskAttribute attrib = getTargetTaskData().getRoot().getAttribute(entry.getKey());
			for (String string : attrib.getValues()) {
				if (string != null && !string.equals("")) { //$NON-NLS-1$
					try {
						appendToBuffer(sb, mapUrlKey(entry.getKey()) + "=", //$NON-NLS-1$
								URLEncoder.encode(string.replaceAll(" ", "%20"), //$NON-NLS-1$//$NON-NLS-2$
										getTaskRepository().getCharacterEncoding()).replaceAll("%2520", "%20")); //$NON-NLS-1$ //$NON-NLS-2$
					} catch (UnsupportedEncodingException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
		return sb;
	}

	private String getQueryURL(String repsitoryUrl, StringBuilder params) {
		StringBuilder url = new StringBuilder(getQueryURLStart(repsitoryUrl));
		url.append(params);

		// HACK make sure that the searches come back sorted by priority. This
		// should be a search option though
		url.append("&order=Importance"); //$NON-NLS-1$
		return url.toString();
	}

	/**
	 * Creates the bugzilla query URL start. Example: https://bugs.eclipse.org/bugs/buglist.cgi?
	 */
	private String getQueryURLStart(String repsitoryUrl) {
		return repsitoryUrl + (repsitoryUrl.endsWith("/") ? "" : "/") + "rest.cgi/bug?"; //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
	}

	private void appendToBuffer(StringBuilder sb, String key, String value) {
		if (sb.length() > 0) {
			sb.append('&');
		}
		sb.append(key);
		sb.append(value);
	}

	protected String mapUrlKey(String attributeKey) {
		String key;
		if (TaskAttribute.SUMMARY.equals(attributeKey) || TaskAttribute.PRODUCT.equals(attributeKey)
				|| TaskAttribute.COMPONENT.equals(attributeKey) || TaskAttribute.VERSION.equals(attributeKey)) {
			key = BugzillaRestTaskSchema.getFieldNameFromAttributeName(attributeKey);
		} else {
			key = attributeKey;
		}
		return key;
	}

	protected String mapAttributeKey(String attributeKey) {
		String key;
		if (BugzillaRestTaskSchema.getFieldNameFromAttributeName(BugzillaRestTaskSchema.getDefault().SUMMARY.getKey())
				.equals(attributeKey)) {
			key = TaskAttribute.SUMMARY;
		} else if (BugzillaRestTaskSchema
				.getFieldNameFromAttributeName(BugzillaRestTaskSchema.getDefault().PRODUCT.getKey())
				.equals(attributeKey)) {
			key = TaskAttribute.PRODUCT;
		} else if (BugzillaRestTaskSchema
				.getFieldNameFromAttributeName(BugzillaRestTaskSchema.getDefault().COMPONENT.getKey())
				.equals(attributeKey)) {
			key = TaskAttribute.COMPONENT;
		} else if (BugzillaRestTaskSchema
				.getFieldNameFromAttributeName(BugzillaRestTaskSchema.getDefault().VERSION.getKey())
				.equals(attributeKey)) {
			key = TaskAttribute.VERSION;
		} else {
			key = attributeKey;
		}
		return key;
	}

}