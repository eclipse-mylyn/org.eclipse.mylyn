/*******************************************************************************
 * Copyright (c) 2010, 2014 Frank Becker and others.
 * 
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 * 
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Frank Becker - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.bugzilla.core.service;

import java.io.IOException;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.commons.httpclient.HttpClient;
import org.apache.xmlrpc.XmlRpcException;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.mylyn.commons.net.AbstractWebLocation;
import org.eclipse.mylyn.commons.net.AuthenticationCredentials;
import org.eclipse.mylyn.commons.net.AuthenticationType;
import org.eclipse.mylyn.internal.bugzilla.core.BugHistory;
import org.eclipse.mylyn.internal.bugzilla.core.BugHistory.Revision;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttachmentMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaAttribute;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaClient;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCorePlugin;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaCustomField;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaFlagMapper;
import org.eclipse.mylyn.internal.bugzilla.core.BugzillaTaskDataHandler;
import org.eclipse.mylyn.internal.bugzilla.core.IBugzillaConstants;
import org.eclipse.mylyn.internal.bugzilla.core.RepositoryConfiguration;
import org.eclipse.mylyn.internal.commons.xmlrpc.CommonXmlRpcClient;
import org.eclipse.mylyn.tasks.core.IRepositoryPerson;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskCommentMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.eclipse.mylyn.tasks.core.data.TaskDataCollector;

@SuppressWarnings("restriction")
public class BugzillaXmlRpcClient extends CommonXmlRpcClient {

	public static final String XML_BUGZILLA_VERSION = "Bugzilla.version"; //$NON-NLS-1$

	public static final String XML_BUGZILLA_TIME = "Bugzilla.time"; //$NON-NLS-1$

	public static final String XML_USER_LOGIN = "User.login"; //$NON-NLS-1$

	public static final String XML_USER_LOGOUT = "User.logout"; //$NON-NLS-1$

	public static final String XML_USER_GET = "User.get"; //$NON-NLS-1$

	public static final String XML_BUG_ATTACHMENTS = "Bug.attachments"; //$NON-NLS-1$

	public static final String XML_BUG_FIELDS = "Bug.fields"; //$NON-NLS-1$

	public static final String XML_BUG_GET = "Bug.get"; //$NON-NLS-1$

	public static final String XML_BUG_COMMENTS = "Bug.comments"; //$NON-NLS-1$

	public static final String XML_BUG_HISTORY = "Bug.history"; //$NON-NLS-1$

	public static final String XML_PRODUCT_GET_SELECTABLE = "Product.get_selectable_products"; //$NON-NLS-1$

	public static final String XML_PRODUCT_GET_ENTERABLE = "Product.get_enterable_products"; //$NON-NLS-1$

	public static final String XML_PRODUCT_GET_ACCESSIBLE = "Product.get_accessible_products"; //$NON-NLS-1$

	public static final String XML_PRODUCT_GET = "Product.get"; //$NON-NLS-1$

	/*
	 * Parameter Definitions
	 *
	 */

	public static final String XML_PARAMETER_LOGIN = "login"; //$NON-NLS-1$

	public static final String XML_PARAMETER_PASSWORD = "password"; //$NON-NLS-1$

	public static final String XML_PARAMETER_REMEMBER = "remember"; //$NON-NLS-1$

	public static final String XML_PARAMETER_IDS = "ids"; //$NON-NLS-1$

	public static final String XML_PARAMETER_NAMES = "names"; //$NON-NLS-1$

	public static final String XML_PARAMETER_MATCH = "match"; //$NON-NLS-1$

	public static final String XML_PARAMETER_EXCLUDE_FIELDS = "exclude_fields"; //$NON-NLS-1$

	public static final String XML_PARAMETER_TOKEN = "Bugzilla_token"; //$NON-NLS-1$

	/*
	 * Response Parameter Definitions
	 *
	 */

	public static final String XML_RESPONSE_DB_TIME = "db_time"; //$NON-NLS-1$

	public static final String XML_RESPONSE_WEB_TIME = "web_time"; //$NON-NLS-1$

	public static final String[] XML_BUGZILLA_TIME_RESPONSE_TO_REMOVE = {
			"tz_offset", "tz_short_name", "web_time_utc", "tz_name" }; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$

	public static final String XML_RESPONSE_VERSION = "version"; //$NON-NLS-1$

	public static final String XML_RESPONSE_USERS = "users"; //$NON-NLS-1$

	public static final String XML_RESPONSE_ID = "id"; //$NON-NLS-1$

	public static final String XML_RESPONSE_IDS = "ids"; //$NON-NLS-1$

	public static final String XML_RESPONSE_FIELDS = "fields"; //$NON-NLS-1$

	public static final String XML_RESPONSE_PRODUCTS = "products"; //$NON-NLS-1$

	public static final String XML_RESPONSE_BUGS = "bugs"; //$NON-NLS-1$

	public static final String XML_RESPONSE_TOKEN = "token"; //$NON-NLS-1$

	/*
	 * Fields
	 *
	 */
	private int userID = -1;

	private String token;

	private final BugzillaClient bugzillaClient;

	public BugzillaXmlRpcClient(AbstractWebLocation location, HttpClient client, BugzillaClient bugzillaClient) {
		super(location, client);
		this.bugzillaClient = bugzillaClient;
	}

	public BugzillaXmlRpcClient(AbstractWebLocation location, BugzillaClient bugzillaClient) {
		super(location);
		this.bugzillaClient = bugzillaClient;
	}

	public String getVersion(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<String>(this) {
			@Override
			public String execute() throws XmlRpcException {
				String result = ""; //$NON-NLS-1$
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUGZILLA_VERSION, (Object[]) null);
				result = response2String(response, XML_RESPONSE_VERSION);
				return result;
			}
		}).execute();
	}

	public Date getDBTime(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Date>(this) {
			@Override
			public Date execute() throws XmlRpcException {
				Date result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUGZILLA_TIME, (Object[]) null);
				result = response2Date(response, XML_RESPONSE_DB_TIME);
				return result;
			}
		}).execute();
	}

	public Date getWebTime(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Date>(this) {
			@Override
			public Date execute() throws XmlRpcException {
				Date result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUGZILLA_TIME, (Object[]) null);
				result = response2Date(response, XML_RESPONSE_WEB_TIME);
				return result;
			}
		}).execute();
	}

	public HashMap<?, ?> getTime(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<HashMap<?, ?>>(this) {
			@Override
			public HashMap<?, ?> execute() throws XmlRpcException {
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUGZILLA_TIME, (Object[]) null);
				if (response != null) {
					for (String exclude : XML_BUGZILLA_TIME_RESPONSE_TO_REMOVE) {
						response.remove(exclude);
					}
				}
				return response;
			}
		}).execute();
	}

	public int login(final IProgressMonitor monitor) throws XmlRpcException {
		userID = -1;
		final AuthenticationCredentials credentials = this.getLocation().getCredentials(AuthenticationType.REPOSITORY);
		if (credentials != null) {
			String user = credentials.getUserName();
			String password = credentials.getPassword();
			if ("".equals(user) || "".equals(password)) { //$NON-NLS-1$//$NON-NLS-2$
				return userID;
			}
			userID = (new BugzillaXmlRpcOperation<Integer>(this) {
				@SuppressWarnings("serial")
				@Override
				public Integer execute() throws XmlRpcException {
					HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_USER_LOGIN,
							new Object[] { new HashMap<String, Object>() {
								{
									put(XML_PARAMETER_LOGIN, credentials.getUserName());
									put(XML_PARAMETER_PASSWORD, credentials.getPassword());
									put(XML_PARAMETER_REMEMBER, true);
								}
							} });
					if (response != null) {
						Integer result = response2Integer(response, XML_RESPONSE_ID);
						if (response.get(XML_RESPONSE_TOKEN) != null) {
							token = response2String(response, XML_RESPONSE_TOKEN);
						} else {
							token = null;
						}
						return result;
					}
					return null;
				}
			}).execute();
		}
		return userID;
	}

	public void logout(final IProgressMonitor monitor) throws XmlRpcException {
		try {
			(new BugzillaXmlRpcOperation<Integer>(this) {
				@Override
				public Integer execute() throws XmlRpcException {
					call(monitor, XML_USER_LOGOUT);
					return -1;
				}
			}).execute();
		} finally {
			userID = -1;
			token = null;
		}
		return;
	}

	private Object[] getUserInfoInternal(final IProgressMonitor monitor, final Object[] callParm)
			throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_USER_GET, callParm);
				result = response2ObjectArray(response, XML_RESPONSE_USERS);
				return result;
			}
		}).execute();
	}

	@SuppressWarnings("serial")
	public Object[] getUserInfoFromIDs(final IProgressMonitor monitor, final Integer[] ids) throws XmlRpcException {
		return getUserInfoInternal(monitor, new Object[] { new HashMap<String, Object>() {
			{
				put(XML_PARAMETER_IDS, ids);
				if (token != null) {
					put(XML_PARAMETER_TOKEN, token);
				}
			}
		} });
	}

	@SuppressWarnings("serial")
	public Object[] getUserInfoFromNames(final IProgressMonitor monitor, final String[] names) throws XmlRpcException {
		return getUserInfoInternal(monitor, new Object[] { new HashMap<String, Object>() {
			{
				put(XML_PARAMETER_NAMES, names);
				if (token != null) {
					put(XML_PARAMETER_TOKEN, token);
				}
			}
		} });
	}

	public Object[] getUserInfoWithMatch(final IProgressMonitor monitor, String[] matchs) throws XmlRpcException {
		HashMap<String, Object[]> parmArray = new HashMap<String, Object[]>();
		Object[] callParm = new Object[] { parmArray };
		parmArray.put(XML_PARAMETER_MATCH, matchs);
		return getUserInfoInternal(monitor, callParm);
	}

	private Object[] getFieldsInternal(final IProgressMonitor monitor, final Object[] callParm) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUG_FIELDS, callParm);
				result = response2ObjectArray(response, XML_RESPONSE_FIELDS);
				return result;
			}
		}).execute();
	}

	public Object[] getAllFields(final IProgressMonitor monitor) throws XmlRpcException {
		return getFieldsInternal(monitor, null);
	}

	@SuppressWarnings("serial")
	public Object[] getFieldsWithNames(final IProgressMonitor monitor, final String[] names) throws XmlRpcException {
		return getFieldsInternal(monitor, new Object[] { new HashMap<String, Object>() {
			{
				put(XML_PARAMETER_NAMES, names);
				if (token != null) {
					put(XML_PARAMETER_TOKEN, token);
				}

			}
		} });
	}

	@SuppressWarnings("serial")
	public Object[] getFieldsWithIDs(final IProgressMonitor monitor, final Integer[] ids) throws XmlRpcException {
		return getFieldsInternal(monitor, new Object[] { new HashMap<String, Object>() {
			{
				put(XML_PARAMETER_IDS, ids);
				if (token != null) {
					put(XML_PARAMETER_TOKEN, token);
				}

			}
		} });
	}

	public Object[] getSelectableProducts(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_PRODUCT_GET_SELECTABLE, (token == null)
						? null
						: new Object[] { Collections.singletonMap(XML_PARAMETER_TOKEN, token) });
				result = response2ObjectArray(response, XML_RESPONSE_IDS);
				return result;
			}
		}).execute();
	}

	public Object[] getEnterableProducts(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_PRODUCT_GET_ENTERABLE, (token == null)
						? null
						: new Object[] { Collections.singletonMap(XML_PARAMETER_TOKEN, token) });
				result = response2ObjectArray(response, XML_RESPONSE_IDS);
				return result;
			}
		}).execute();
	}

	public Object[] getAccessibleProducts(final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_PRODUCT_GET_ACCESSIBLE, (token == null)
						? null
						: new Object[] { Collections.singletonMap(XML_PARAMETER_TOKEN, token) });
				result = response2ObjectArray(response, XML_RESPONSE_IDS);
				return result;
			}
		}).execute();
	}

	public Object[] getProducts(final IProgressMonitor monitor, final Object[] ids) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@SuppressWarnings("serial")
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_PRODUCT_GET,
						new Object[] { new HashMap<String, Object>() {
							{
								put(XML_PARAMETER_IDS, ids);
								if (token != null) {
									put(XML_PARAMETER_TOKEN, token);
								}
							}
						} });
				result = response2ObjectArray(response, XML_RESPONSE_PRODUCTS);
				return result;
			}
		}).execute();
	}

	private Object[] response2ObjectArray(HashMap<?, ?> response, String name) throws XmlRpcException {
		Object[] result;
		if (response == null) {
			return null;
		}
		try {
			result = (Object[]) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	private Integer response2Integer(HashMap<?, ?> response, String name) throws XmlRpcException {
		Integer result;
		if (response == null) {
			return null;
		}
		try {
			result = (Integer) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	private String response2String(HashMap<?, ?> response, String name) throws XmlRpcException {
		String result;
		if (response == null) {
			return null;
		}
		try {
			result = (String) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	private Date response2Date(HashMap<?, ?> response, String name) throws XmlRpcException {
		Date result;
		if (response == null) {
			return null;
		}
		try {
			result = (Date) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	public void updateConfiguration(IProgressMonitor monitor, RepositoryConfiguration repositoryConfiguration,
			String fileName) throws CoreException {
		repositoryConfiguration.setValidTransitions(monitor, fileName, this);
		if (!repositoryConfiguration.getOptionValues(BugzillaAttribute.PRODUCT).isEmpty()) {
			updateProductInfo(monitor, repositoryConfiguration);
		}
	}

	public void updateProductInfo(IProgressMonitor monitor, RepositoryConfiguration repositoryConfiguration)
			throws CoreException {
		try {
			Object[] productIDs = getAccessibleProducts(monitor);
			Object[] products = getProducts(monitor, productIDs);
			for (Object object : products) {
				if (object instanceof HashMap<?, ?>) {
					String defaultMilestone = null;
					String product = (String) ((HashMap<?, ?>) object).get("name"); //$NON-NLS-1$
					HashMap<?, ?> values = (HashMap<?, ?>) ((HashMap<?, ?>) object).get("internals"); //$NON-NLS-1$
					Object defaultMilestoneObj = null;
					if (values != null) {
						if (values instanceof HashMap<?, ?>) {
							defaultMilestoneObj = ((HashMap<?, ?>) values).get("defaultmilestone"); //$NON-NLS-1$
						}
					} else {
						defaultMilestoneObj = ((HashMap<?, ?>) object).get("default_milestone"); //$NON-NLS-1$
					}
					if (defaultMilestoneObj != null) {
						if (defaultMilestoneObj instanceof String) {
							defaultMilestone = (String) defaultMilestoneObj;
						} else if (defaultMilestoneObj instanceof Double) {
							defaultMilestone = ((Double) defaultMilestoneObj).toString();
						} else if (defaultMilestoneObj instanceof Integer) {
							defaultMilestone = ((Integer) defaultMilestoneObj).toString();
						}
					}
					if (product != null && !product.equals("") //$NON-NLS-1$
							&& defaultMilestone != null && !defaultMilestone.equals("")) { //$NON-NLS-1$
						repositoryConfiguration.setDefaultMilestone(product, defaultMilestone);
					}
				}
			}
		} catch (XmlRpcException e) {
			throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN,
					"Can not get the Default Milestones using XMLRPC")); //$NON-NLS-1$
		}

	}

	public int getUserID() {
		return userID;
	}

	public List<BugHistory> getHistory(final Integer[] ids, final IProgressMonitor monitor) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<List<BugHistory>>(this) {
			@Override
			public List<BugHistory> execute() throws XmlRpcException {
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("ids", ids); //$NON-NLS-1$
				Map<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUG_HISTORY, params);
				if (response != null) {
					List<BugHistory> result = new ArrayList<BugHistory>(ids.length);
					for (Object item : (Object[]) response.get("bugs")) { //$NON-NLS-1$
						Map<?, ?> map = (Map<?, ?>) item;
						Integer id = (Integer) map.get("id"); //$NON-NLS-1$
						BugHistory history = new BugHistory(id);
						Object[] historyItems = (Object[]) map.get("history"); //$NON-NLS-1$
						for (Object historyItem : historyItems) {
							Map<?, ?> historyItemMap = (Map<?, ?>) historyItem;
							Revision revision = history.createRevision((Date) historyItemMap.get("when"), //$NON-NLS-1$
									(String) historyItemMap.get("who")); //$NON-NLS-1$
							Object[] changeItems = (Object[]) historyItemMap.get("changes"); //$NON-NLS-1$
							if (changeItems != null) {
								for (Object changeItem : changeItems) {
									Map<?, ?> changeItemMap = (Map<?, ?>) changeItem;
									Object attachmentID = changeItemMap.get("attachment_id"); //$NON-NLS-1$
									int attachmentId = -1;
									if (attachmentID instanceof Integer) {
										attachmentId = (Integer) attachmentID;
									} else if (attachmentID instanceof String) {
										Integer.parseInt((String) attachmentID);
									}
									revision.addChange((String) changeItemMap.get("field_name"), //$NON-NLS-1$
											(String) changeItemMap.get("added"), (String) changeItemMap.get("removed"), //$NON-NLS-1$ //$NON-NLS-2$
											attachmentId);
								}
							}
						}
						result.add(history);
					}
					return result;
				}
				return null;
			}
		}).execute();
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, HashMap<String, Object[]>> response2HashMapHashMap(HashMap<?, ?> response, String name)
			throws XmlRpcException {
		HashMap<String, HashMap<String, Object[]>> result;
		if (response == null) {
			return null;
		}
		try {
			result = (HashMap<String, HashMap<String, Object[]>>) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	private HashMap<String, Object[]> response2HashMap(HashMap<?, ?> response, String name) throws XmlRpcException {
		HashMap<String, Object[]> result;
		if (response == null) {
			return null;
		}
		try {
			result = (HashMap<String, Object[]>) response.get(name);
		} catch (ClassCastException e) {
			result = null;
			throw new XmlRpcClassCastException(e);
		}
		return result;
	}

	protected String getConnectorKind() {
		return BugzillaCorePlugin.CONNECTOR_KIND;
	}

	public Object[] getBugs(final IProgressMonitor monitor, final Object[] ids) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<Object[]>(this) {
			@SuppressWarnings("serial")
			@Override
			public Object[] execute() throws XmlRpcException {
				Object[] result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUG_GET,
						new Object[] { new HashMap<String, Object[]>() {
							{
								put(XML_PARAMETER_IDS, ids);
							}
						} });
				result = response2ObjectArray(response, XML_RESPONSE_BUGS);
				return result;
			}
		}).execute();
	}

	public HashMap<String, HashMap<String, Object[]>> getCommentsInternal(final IProgressMonitor monitor,
			final Object[] ids) throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<HashMap<String, HashMap<String, Object[]>>>(this) {
			@SuppressWarnings("serial")
			@Override
			public HashMap<String, HashMap<String, Object[]>> execute() throws XmlRpcException {
				HashMap<String, HashMap<String, Object[]>> result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUG_COMMENTS,
						new Object[] { new HashMap<String, Object[]>() {
							{
								put(XML_PARAMETER_IDS, ids);
							}
						} });
				result = response2HashMapHashMap(response, XML_RESPONSE_BUGS);
				return result;
			}
		}).execute();
	}

	public HashMap<String, Object[]> getAttachmentsInternal(final IProgressMonitor monitor, final Object[] ids)
			throws XmlRpcException {
		return (new BugzillaXmlRpcOperation<HashMap<String, Object[]>>(this) {
			@SuppressWarnings("serial")
			@Override
			public HashMap<String, Object[]> execute() throws XmlRpcException {
				HashMap<String, Object[]> result = null;
				HashMap<?, ?> response = (HashMap<?, ?>) call(monitor, XML_BUG_ATTACHMENTS,
						new Object[] { new HashMap<String, Object[]>() {
							{
								put(XML_PARAMETER_IDS, ids);
								put(XML_PARAMETER_EXCLUDE_FIELDS, new String[] { "data" }); //$NON-NLS-1$
							}
						} });
				result = response2HashMap(response, XML_RESPONSE_BUGS);
				return result;
			}
		}).execute();
	}

	private String getMappedBugzillaAttribute(String xmlrpcName) {
		if (xmlrpcName != null) {
			if (xmlrpcName.equals("target_milestone") || xmlrpcName.equals("see_also") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("resolution") || xmlrpcName.equals("version") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("op_sys") || xmlrpcName.equals("component") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("priority") || xmlrpcName.equals("qa_contact") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("keywords") || xmlrpcName.equals("product") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("assigned_to") || xmlrpcName.equals("classification") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("cc") || xmlrpcName.equals("remaining_time") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("estimated_time") || xmlrpcName.equals("deadline") //$NON-NLS-1$ //$NON-NLS-2$
					|| xmlrpcName.equals("alias")) { //$NON-NLS-1$
				return xmlrpcName;
			} else if (xmlrpcName.equals("last_change_time")) { //$NON-NLS-1$
				return "delta_ts"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("summary")) { //$NON-NLS-1$
				return "short_desc"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("is_confirmed")) { //$NON-NLS-1$
				return "everconfirmed"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("is_open")) { //$NON-NLS-1$
				return "status_open"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("depends_on")) { //$NON-NLS-1$
				return "dependson"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("creator")) { //$NON-NLS-1$
				return "reporter"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("id")) { //$NON-NLS-1$
				return "bug_id"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("creation_time")) { //$NON-NLS-1$
				return "creation_ts"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("groups")) { //$NON-NLS-1$
				return "group"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("platform")) { //$NON-NLS-1$
				return "rep_platform"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("is_creator_accessible")) { //$NON-NLS-1$
				return "reporter_accessible"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("status")) { //$NON-NLS-1$
				return "bug_status"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("is_cc_accessible")) { //$NON-NLS-1$
				return "cclist_accessible"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("severity")) { //$NON-NLS-1$
				return "bug_severity"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("blocks")) { //$NON-NLS-1$
				return "blocked"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("url")) { //$NON-NLS-1$
				return "bug_file_loc"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("whiteboard")) { //$NON-NLS-1$
				return "status_whiteboard"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("update_token")) { //$NON-NLS-1$
				return "token"; //$NON-NLS-1$
			} else if (xmlrpcName.equals("dupe_of")) { //$NON-NLS-1$
				return "dup_id"; //$NON-NLS-1$
			}
		}
		return "UNKNOWN"; //$NON-NLS-1$
	}

	public void getTaskData(Set<String> taskIds, final TaskDataCollector collector, final TaskAttributeMapper mapper,
			final IProgressMonitor monitor) throws IOException, CoreException {
		HashMap<String, TaskData> taskDataMap = new HashMap<String, TaskData>();

		taskIds = new HashSet<String>(taskIds);
		while (taskIds.size() > 0) {

			Set<String> idsToRetrieve = new HashSet<String>();
			Iterator<String> itr = taskIds.iterator();
			for (int x = 0; itr.hasNext() && x < BugzillaClient.MAX_RETRIEVED_PER_QUERY; x++) {
				String taskId = itr.next();
				String taskIdOrg = taskId;
				// remove leading zeros
				boolean changed = false;
				while (taskId.startsWith("0")) { //$NON-NLS-1$
					taskId = taskId.substring(1);
					changed = true;
				}
				idsToRetrieve.add(taskId);
				if (changed) {
					taskIds.remove(taskIdOrg);
					taskIds.add(taskId);
				}
			}
			Integer[] formData = new Integer[idsToRetrieve.size()];

			if (idsToRetrieve.size() == 0) {
				return;
			}

			RepositoryConfiguration repositoryConfiguration = bugzillaClient.getRepositoryConfiguration();
			if (repositoryConfiguration == null) {
				repositoryConfiguration = bugzillaClient.getRepositoryConfiguration(new SubProgressMonitor(monitor, 1),
						null);
			}
			List<BugzillaCustomField> customFields = new ArrayList<BugzillaCustomField>();
			if (repositoryConfiguration != null) {
				customFields = repositoryConfiguration.getCustomFields();
			}
			itr = idsToRetrieve.iterator();
			int x = 0;
			for (; itr.hasNext(); x++) {
				String taskId = itr.next();
				formData[x] = new Integer(taskId);

				TaskData taskData = new TaskData(mapper, getConnectorKind(), getLocation().getUrl(), taskId);
				bugzillaClient.setupExistingBugAttributes(getLocation().getUrl(), taskData);
				taskDataMap.put(taskId, taskData);
			}
			try {
				if (getUserID() == -1) {
					login(monitor);
				}
				Object[] result = getBugs(monitor, formData);
				HashMap<String, Object[]> resultAttachments = getAttachmentsInternal(monitor, formData);
				HashMap<String, HashMap<String, Object[]>> resultComments = getCommentsInternal(monitor, formData);
				for (Object resultMap : result) {
					if (resultMap instanceof Map<?, ?>) {
						Map<?, ?> taskDataResultMap = (Map<?, ?>) resultMap;
						Integer id = (Integer) taskDataResultMap.get("id"); //$NON-NLS-1$
						TaskData taskData = taskDataMap.get(id.toString());
						HashMap<String, Object[]> comments = resultComments.get(id.toString());
						Object[] attachments = resultAttachments.get(id.toString());
						updateTaskDataFromMap(mapper, customFields, taskDataResultMap, taskData);
						addCommentsFromHashToTaskData(mapper, taskData, comments);
						addAttachmentsFromHashToTaskData(mapper, taskData, attachments);
						updateCustomFields(taskData);
						updateAttachmentMetaData(taskData);
						collector.accept(taskData);
					}
				}

				taskIds.removeAll(idsToRetrieve);
				taskDataMap.clear();
			} catch (XmlRpcException e) {
				throw new CoreException(new Status(IStatus.ERROR, BugzillaCorePlugin.ID_PLUGIN, "XmlRpcException: ", e)); //$NON-NLS-1$
			}
		}
	}

	@SuppressWarnings("unchecked")
	private void updateTaskDataFromMap(final TaskAttributeMapper mapper, List<BugzillaCustomField> customFields,
			Map<?, ?> taskDataResultMap, TaskData taskData) {
		for (String attrib : (Set<String>) taskDataResultMap.keySet()) {
			Object value = taskDataResultMap.get(attrib);
			if (attrib.compareTo("flags") == 0) { //$NON-NLS-1$
				addFlags(taskData, value, taskData.getRoot());
			} else if (attrib.startsWith(BugzillaCustomField.CUSTOM_FIELD_PREFIX)) {
				TaskAttribute endAttribute = taskData.getRoot().getAttribute(attrib);
				if (endAttribute == null) {
					endAttribute = taskData.getRoot().createAttribute(attrib);
				}
				if (value instanceof String || value instanceof Boolean || value instanceof Integer
						|| value instanceof Date) {
					endAttribute.addValue(getValueStringFromObject(value, false));
				} else if (value instanceof Object[]) {
					for (Object object1 : (Object[]) value) {
						if (object1 instanceof String || object1 instanceof Boolean || object1 instanceof Integer
								|| object1 instanceof Date) {
							endAttribute.addValue(getValueStringFromObject(object1, true));
						}
					}
				}
				continue;
			}
			BugzillaAttribute tag = BugzillaAttribute.UNKNOWN;
			try {
				String key = getMappedBugzillaAttribute(attrib);
				tag = BugzillaAttribute.valueOf(key.toUpperCase(Locale.ENGLISH));
			} catch (RuntimeException e) {
				if (e instanceof IllegalArgumentException) {
					// ignore unrecognized tags
					continue;
				}
				throw e;
			}
			switch (tag) {
			case UNKNOWN:
				continue;
			default:
				createAttrribute(taskData, mapper, value, tag, true);
				break;
			}
		}
	}

	private void addFlags(TaskData taskData, Object value, TaskAttribute rootAttribute) {
		if (value instanceof Object[]) {
			for (Object valueTemp : (Object[]) value) {
				HashMap<?, ?> flag = (HashMap<?, ?>) valueTemp;
				// We have the following information which are not used:
				// Integer type_id = (Integer) flag.get("type_id");
				// Date creation_date = (Date) flag.get("creation_date");
				// Date modification_date = (Date) flag.get("modification_date");
				Integer id = (Integer) flag.get("id"); //$NON-NLS-1$
				String name = (String) flag.get("name"); //$NON-NLS-1$
				String status = (String) flag.get("status"); //$NON-NLS-1$
				String setter = (String) flag.get("setter"); //$NON-NLS-1$
				Object requestee = flag.get("requestee"); //$NON-NLS-1$
				TaskAttribute attribute = rootAttribute.createAttribute(BugzillaAttribute.KIND_FLAG + id);
				BugzillaFlagMapper flagMapper = new BugzillaFlagMapper(bugzillaClient.getConnector());
				flagMapper.setRequestee((String) requestee);
				flagMapper.setSetter(setter);
				flagMapper.setState(status);
				flagMapper.setFlagId(name);
				flagMapper.setNumber(id);
				flagMapper.applyTo(attribute);
			}
		}
	}

	private void addAttachmentsFromHashToTaskData(final TaskAttributeMapper mapper, TaskData taskData,
			Object[] attachments) {
		if (attachments != null) {
			for (Object attachmentTemp : attachments) {
				// We have the following information which are not used:
				// (Integer) attachment.get("bug_id");
				// (Integer) attachment.get("is_private");
				// (Integer) attachment.get("is_url");

				HashMap<?, ?> attachment = (HashMap<?, ?>) attachmentTemp;
				Date creation_time = (Date) attachment.get("creation_time"); //$NON-NLS-1$
				Integer id = (Integer) attachment.get("id"); //$NON-NLS-1$
				String file_name = (String) attachment.get("file_name"); //$NON-NLS-1$
				String summary = (String) attachment.get("summary"); //$NON-NLS-1$
				String content_type = (String) attachment.get("content_type"); //$NON-NLS-1$
				Integer size = (Integer) attachment.get("size"); //$NON-NLS-1$

				Integer is_obsolete = (Integer) attachment.get("is_obsolete"); //$NON-NLS-1$
				Integer is_patch = (Integer) attachment.get("is_patch"); //$NON-NLS-1$
				String creator = (String) attachment.get("creator"); //$NON-NLS-1$
				Date lastChangeTime = (Date) attachment.get("last_change_time"); //$NON-NLS-1$

				try {
					SimpleDateFormat x0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US); //$NON-NLS-1$
					SimpleDateFormat x1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ"); //$NON-NLS-1$
					String dateString = x0.format(creation_time);
					creation_time = x1.parse(dateString + " GMT"); //$NON-NLS-1$
					dateString = x0.format(lastChangeTime);
					lastChangeTime = x1.parse(dateString + " GMT"); //$NON-NLS-1$
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				TaskAttribute attachmentAttribute = taskData.getRoot().createAttribute(
						TaskAttribute.PREFIX_ATTACHMENT + id);
				BugzillaAttachmentMapper attachmentMapper = BugzillaAttachmentMapper.createFrom(attachmentAttribute);
				attachmentMapper.setAttachmentId(id.toString());
				IRepositoryPerson author = taskData.getAttributeMapper().getTaskRepository().createPerson(creator);
				author.setName(creator);
				attachmentMapper.setAuthor(author);
//				attachmentMapper.setComment(summary);
				attachmentMapper.setDeltaDate(lastChangeTime);
				attachmentMapper.setContentType(content_type);
				attachmentMapper.setCreationDate(creation_time);
				attachmentMapper.setDeprecated(is_obsolete.equals(Integer.valueOf(1)));
				attachmentMapper.setDescription(summary);
				attachmentMapper.setFileName(file_name);
				attachmentMapper.setLength(size != null ? size : -1L);
				attachmentMapper.setPatch(is_patch.equals(Integer.valueOf(1)));
				attachmentMapper.applyTo(attachmentAttribute);

				addFlags(taskData, attachment.get("flags"), attachmentAttribute); //$NON-NLS-1$
			}
		}
	}

	private void addCommentsFromHashToTaskData(final TaskAttributeMapper mapper, TaskData taskData,
			HashMap<String, Object[]> comments) {
		if (comments != null) {
			TaskRepository taskRepository = mapper.getTaskRepository();
			String useParam = taskRepository.getProperty(IBugzillaConstants.BUGZILLA_INSIDER_GROUP);
			boolean useIsPrivate = (useParam == null || (useParam != null && useParam.equals("true"))); //$NON-NLS-1$
			Object[] commentArray = comments.get("comments"); //$NON-NLS-1$
			if (commentArray != null) {
				int commentNum = 0;
				for (Object object2 : commentArray) {
					@SuppressWarnings("unchecked")
					HashMap<String, Object> commentHash = (HashMap<String, Object>) object2;
					String text = (String) commentHash.get("text"); //$NON-NLS-1$
					if (commentNum == 0) {
						TaskAttribute description = createAttrribute(taskData, mapper, text,
								BugzillaAttribute.LONG_DESC, true);
						if (useIsPrivate) {
							Boolean is_private = (Boolean) commentHash.get("is_private"); //$NON-NLS-1$
							Integer commentID = (Integer) commentHash.get("id"); //$NON-NLS-1$
							TaskAttribute idAttribute = description.createAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_ID);
							idAttribute.setValue(commentID.toString());
							if (useIsPrivate) {
								TaskAttribute isprivateAttribute = description.createAttribute(IBugzillaConstants.BUGZILLA_DESCRIPTION_IS_PRIVATE);
								isprivateAttribute.setValue(is_private ? "1" : "0"); //$NON-NLS-1$ //$NON-NLS-2$
							}
						}
						commentNum++;
						continue;
					}
					TaskAttribute attribute = taskData.getRoot().createAttribute(
							TaskAttribute.PREFIX_COMMENT + commentNum);
					TaskCommentMapper taskComment = TaskCommentMapper.createFrom(attribute);
					Integer commentID = (Integer) commentHash.get("id"); //$NON-NLS-1$
					Date time = (Date) commentHash.get("time"); //$NON-NLS-1$
					String creator = (String) commentHash.get("creator"); //$NON-NLS-1$
					Boolean is_private = (Boolean) commentHash.get("is_private"); //$NON-NLS-1$
					@SuppressWarnings("unused")
					Integer attachment_id = (Integer) commentHash.get("attachment_id"); //$NON-NLS-1$

					taskComment.setCommentId(commentID.toString());
					taskComment.setNumber(commentNum);
					if (creator != null) {
						IRepositoryPerson author = taskData.getAttributeMapper()
								.getTaskRepository()
								.createPerson(creator);
						author.setName(creator);
						taskComment.setAuthor(author);
					}
					taskComment.setIsPrivate(is_private);
					TaskAttribute attrTimestamp = attribute.createAttribute(BugzillaAttribute.BUG_WHEN.getKey());
					attrTimestamp.setValue(getValueStringFromObject(time, false));
					try {
						SimpleDateFormat x0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US); //$NON-NLS-1$
						String dateString = x0.format(time);
						SimpleDateFormat x1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ"); //$NON-NLS-1$
						Date x2 = x1.parse(dateString + " GMT"); //$NON-NLS-1$

						taskComment.setCreationDate(x2);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					taskComment.setText(text.trim());
					taskComment.applyTo(attribute);
					commentNum++;
				}
				// Need to set LONGDESCLENGTH to number of comments + 1 for description
				TaskAttribute numCommentsAttribute = taskData.getRoot().getMappedAttribute(
						BugzillaAttribute.LONGDESCLENGTH.getKey());
				if (numCommentsAttribute == null) {
					numCommentsAttribute = BugzillaTaskDataHandler.createAttribute(taskData,
							BugzillaAttribute.LONGDESCLENGTH);
				}

				numCommentsAttribute.setValue("" + commentNum); //$NON-NLS-1$

			}
		}

	}

	private String getValueStringFromObject(Object value, boolean dateWithTimezone) {
		if (value instanceof String) {
			return (String) value;
		} else if (value instanceof Boolean) {
			return (Boolean) value ? "1" : "0"; //$NON-NLS-1$ //$NON-NLS-2$
		} else if (value instanceof Integer) {
			return ((Integer) value).toString();
		} else if (value instanceof Double) {
			NumberFormat numberInstance = NumberFormat.getInstance(Locale.US);//NumberFormat.getNumberInstance();
			numberInstance.setMaximumFractionDigits(2);
			numberInstance.setMinimumFractionDigits(2);
			return numberInstance.format(value);
		} else if (value instanceof Date) {
			try {
				Date y = (Date) value;
				SimpleDateFormat x0 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US); //$NON-NLS-1$
				String dateString = x0.format(y);
				SimpleDateFormat x1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss ZZZ"); //$NON-NLS-1$
				Date x2 = x1.parse(dateString + " GMT"); //$NON-NLS-1$
				return dateWithTimezone ? x1.format(x2) : x1.format(x2).substring(0, 10) + " 00:00:00"; //$NON-NLS-1$
			} catch (ParseException e) {
				return ""; //$NON-NLS-1$
			}
		}
		return null;
	}

	private TaskAttribute createAttrribute(TaskData taskData, TaskAttributeMapper mapper, Object value,
			BugzillaAttribute tag, boolean clearValueFirst) {
		TaskAttribute attribute = taskData.getRoot().getMappedAttribute(tag.getKey());
		if (value instanceof String || value instanceof Boolean || value instanceof Integer || value instanceof Date
				|| value instanceof Double) {
			if (attribute == null) {
				attribute = BugzillaTaskDataHandler.createAttribute(taskData, tag);
				attribute.setValue(getValueStringFromObject(value, true));
			} else {
				if (clearValueFirst) {
					attribute.clearValues();
				}
				attribute.addValue(getValueStringFromObject(value, true));
			}
		} else if (value instanceof Object[]) {
			if (attribute == null) {
				attribute = BugzillaTaskDataHandler.createAttribute(taskData, tag);
			} else if (clearValueFirst) {
				attribute.clearValues();
			}
			String valueList = ""; //$NON-NLS-1$
			if (tag.equals(BugzillaAttribute.DEPENDSON) || tag.equals(BugzillaAttribute.BLOCKED)
					|| tag.equals(BugzillaAttribute.KEYWORDS)) {
				for (Object object : (Object[]) value) {
					if (valueList.equals("")) { //$NON-NLS-1$
						if (object instanceof String || object instanceof Boolean || object instanceof Integer
								|| object instanceof Date) {
							valueList = getValueStringFromObject(object, true);
						}
					} else {
						if (object instanceof String || object instanceof Boolean || object instanceof Integer
								|| object instanceof Date) {
							valueList += ", " + getValueStringFromObject(object, true); //$NON-NLS-1$
						}
					}
				}
				attribute.setValue(valueList);
			} else {
				for (Object object : (Object[]) value) {
					if (object instanceof String || object instanceof Boolean || object instanceof Integer
							|| object instanceof Date) {
						attribute.addValue(getValueStringFromObject(object, true));
					}
				}

			}
		}
		return attribute;
	}

	private void updateCustomFields(TaskData taskData) {
		String repURL = taskData.getRepositoryUrl();
		if (repURL.indexOf("/xmlrpc.cgi") == -1) { //$NON-NLS-1$
			return;
		}
		RepositoryConfiguration config = bugzillaClient.getConnector().getRepositoryConfiguration(
				repURL.substring(0, repURL.indexOf("/xmlrpc.cgi"))); //$NON-NLS-1$
		if (config != null) {
			for (BugzillaCustomField bugzillaCustomField : config.getCustomFields()) {

				TaskAttribute atr = taskData.getRoot().getAttribute(bugzillaCustomField.getName());
				if (atr == null) {
					atr = taskData.getRoot().createAttribute(bugzillaCustomField.getName());
				}

				if (atr != null) {
					atr.getMetaData().defaults().setLabel(bugzillaCustomField.getDescription());
					atr.getMetaData().setKind(TaskAttribute.KIND_DEFAULT);

					switch (bugzillaCustomField.getFieldType()) {
					case FreeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						break;
					case DropDown:
						atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						break;
					case MultipleSelection:
						atr.getMetaData().setType(TaskAttribute.TYPE_MULTI_SELECT);
						break;
					case LargeText:
						atr.getMetaData().setType(TaskAttribute.TYPE_LONG_TEXT);
						break;
					case DateTime:
						atr.getMetaData().setType(TaskAttribute.TYPE_DATETIME);
						break;

					default:
						List<String> options = bugzillaCustomField.getOptions();
						if (options.size() > 0) {
							atr.getMetaData().setType(TaskAttribute.TYPE_SINGLE_SELECT);
						} else {
							atr.getMetaData().setType(TaskAttribute.TYPE_SHORT_TEXT);
						}
					}
					atr.getMetaData().setReadOnly(false);
				}
			}
		}
	}

	private void updateAttachmentMetaData(TaskData taskData) {
		List<TaskAttribute> taskAttachments = taskData.getAttributeMapper().getAttributesByType(taskData,
				TaskAttribute.TYPE_ATTACHMENT);
		String repURL = taskData.getRepositoryUrl();
		if (repURL.indexOf("/xmlrpc.cgi") == -1) { //$NON-NLS-1$
			return;
		}
		for (TaskAttribute attachment : taskAttachments) {
			BugzillaAttachmentMapper attachmentMapper = BugzillaAttachmentMapper.createFrom(attachment);
			attachmentMapper.setUrl(repURL.substring(0, repURL.indexOf("/xmlrpc.cgi")) //$NON-NLS-1$
					+ IBugzillaConstants.URL_GET_ATTACHMENT_SUFFIX + attachmentMapper.getAttachmentId());
			attachmentMapper.applyTo(attachment);
		}
	}
}
