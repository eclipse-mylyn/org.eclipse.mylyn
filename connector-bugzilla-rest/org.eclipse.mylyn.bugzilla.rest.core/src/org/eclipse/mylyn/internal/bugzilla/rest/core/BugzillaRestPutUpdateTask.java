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

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.http.core.CommonHttpClient;
import org.eclipse.mylyn.internal.bugzilla.rest.core.response.data.PutUpdateResult;
import org.eclipse.mylyn.tasks.core.data.TaskAttribute;
import org.eclipse.mylyn.tasks.core.data.TaskData;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

public class BugzillaRestPutUpdateTask extends BugzillaRestPutRequest<PutUpdateResult> {
	private final TaskData taskData;

	class OldAttributes {
		private final Set<TaskAttribute> oldAttributes;

		public OldAttributes(Set<TaskAttribute> oldAttributes) {
			super();
			this.oldAttributes = oldAttributes;
		}

	}

	OldAttributes oldAttributes;

	ImmutableList<String> legalUpdateAttributes = new ImmutableList.Builder<String>()
			.add(BugzillaRestTaskSchema.getDefault().PRODUCT.getKey())
			.add(BugzillaRestTaskSchema.getDefault().COMPONENT.getKey())
			.add(BugzillaRestTaskSchema.getDefault().SUMMARY.getKey())
			.add(BugzillaRestTaskSchema.getDefault().VERSION.getKey())
			.add(BugzillaRestTaskSchema.getDefault().DESCRIPTION.getKey())
			.add(BugzillaRestTaskSchema.getDefault().OS.getKey())
			.add(BugzillaRestTaskSchema.getDefault().PLATFORM.getKey())
			.add(BugzillaRestTaskSchema.getDefault().PRIORITY.getKey())
			.add(BugzillaRestTaskSchema.getDefault().SEVERITY.getKey())
			.add(BugzillaRestTaskSchema.getDefault().ALIAS.getKey())
			.add(BugzillaRestTaskSchema.getDefault().ASSIGNED_TO.getKey())
			.add(BugzillaRestTaskSchema.getDefault().QA_CONTACT.getKey())
			.add(TaskAttribute.OPERATION)
			.add(BugzillaRestTaskSchema.getDefault().TARGET_MILESTONE.getKey())
			.add(BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey())
			.add("resolutionInput") //$NON-NLS-1$
			.add(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey())
			.add(BugzillaRestTaskSchema.getDefault().DUPE_OF.getKey())
			.add(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey())
			.add(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey())
			.add(BugzillaRestTaskSchema.getDefault().KEYWORDS.getKey())
			.add(BugzillaRestTaskSchema.getDefault().RESET_QA_CONTACT.getKey())
			.add(BugzillaRestTaskSchema.getDefault().RESET_ASSIGNED_TO.getKey())
			.build();

	class TaskAttributeTypeAdapter extends TypeAdapter<OldAttributes> {
		RepositoryLocation location;

		public TaskAttributeTypeAdapter(RepositoryLocation location) {
			super();
			this.location = location;
		}

		private final Function<String, String> function = new Function<String, String>() {

			@Override
			public String apply(String input) {
				return BugzillaRestGsonUtil.convertString2GSonString(input);
			}
		};

		@Override
		public void write(JsonWriter out, OldAttributes oldValues) throws IOException {
			out.beginObject();
			addAuthenticationToGson(out, location);
			for (TaskAttribute element : oldValues.oldAttributes) {
				TaskAttribute taskAttribute = taskData.getRoot().getAttribute(element.getId());
				String id = taskAttribute.getId();
				String value = BugzillaRestGsonUtil.convertString2GSonString(taskAttribute.getValue());
				if ((legalUpdateAttributes.contains(id) || id.startsWith("cf_")) && value != null) { //$NON-NLS-1$
					id = BugzillaRestTaskSchema.getFieldNameFromAttributeName(id);
					if (id.equals("status")) { //$NON-NLS-1$
						if (value != null && value.equals(TaskAttribute.PREFIX_OPERATION + "default")) { //$NON-NLS-1$
							continue;
						}
						if (value.equals("duplicate")) { //$NON-NLS-1$
							TaskAttribute res = element.getParentAttribute()
									.getAttribute(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey());
							if (!oldAttributes.oldAttributes.contains(res)) {
								out.name("resolution").value("DUPLICATE"); //$NON-NLS-1$ //$NON-NLS-2$
							} else {
								TaskAttribute res1 = taskData.getRoot()
										.getAttribute(BugzillaRestTaskSchema.getDefault().RESOLUTION.getKey());
								res1.setValue("DUPLICATE"); //$NON-NLS-1$
							}
							value = "RESOLVED"; //$NON-NLS-1$
						}
					}
					if (taskAttribute.getMetaData().getType() != null
							&& taskAttribute.getMetaData().getType().equals(TaskAttribute.TYPE_MULTI_SELECT)) {
						Iterable<String> taskIdsTemp = Iterables.transform(taskAttribute.getValues(), function);
						Joiner joiner = Joiner.on(",").skipNulls(); //$NON-NLS-1$
						value = joiner.join(taskIdsTemp);
					}
					if (id.equals(BugzillaRestTaskSchema.getDefault().NEW_COMMENT.getKey())) {
						out.name("comment").beginObject(); //$NON-NLS-1$
						out.name("body").value(value); //$NON-NLS-1$
						out.endObject();
					} else if (id.equals(BugzillaRestTaskSchema.getDefault().BLOCKS.getKey())
							|| id.equals(BugzillaRestTaskSchema.getDefault().DEPENDS_ON.getKey())) {
						Set<String> setOld;
						if (element.getValues().size() > 1) {
							setOld = new HashSet<String>(element.getValues());
						} else {
							setOld = new HashSet<String>(Arrays.asList(element.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
						}
						Set<String> setNew;
						if (taskAttribute.getValues().size() > 1) {
							setNew = new HashSet<String>(taskAttribute.getValues());
						} else {
							setNew = new HashSet<String>(Arrays.asList(taskAttribute.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
						}
						BugzillaRestGsonUtil.getDefault().buildAddRemoveIntegerHash(out, id, setOld, setNew);
					} else if (id.equals(BugzillaRestTaskSchema.getDefault().KEYWORDS.getKey())) {
						Set<String> setOld = new HashSet<String>(element.getValues());
						Set<String> setNew = new HashSet<String>(taskAttribute.getValues());
						BugzillaRestGsonUtil.getDefault().buildAddRemoveHash(out, id, setOld, setNew);
					} else {
						out.name(id).value(value);
						if (id.equals("description")) { //$NON-NLS-1$
							TaskAttribute descriptionpri = taskAttribute
									.getAttribute(BugzillaRestTaskSchema.getDefault().COMMENT_ISPRIVATE.getKey());
							Boolean descriptionprivalue = (descriptionpri != null)
									? (descriptionpri.getValue().equals("1")) //$NON-NLS-1$
									: false;
							out.name("comment_is_private").value(Boolean.toString(descriptionprivalue)); //$NON-NLS-1$
						}
					}
				}
			}
			TaskAttribute cc = taskData.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().CC.getKey());
			TaskAttribute addCC = taskData.getRoot().getAttribute(BugzillaRestTaskSchema.getDefault().ADD_CC.getKey());
			TaskAttribute removeCC = taskData.getRoot()
					.getAttribute(BugzillaRestTaskSchema.getDefault().REMOVE_CC.getKey());
			TaskAttribute addSelfCC = taskData.getRoot()
					.getAttribute(BugzillaRestTaskSchema.getDefault().ADD_SELF_CC.getKey());
			if (Boolean.valueOf(addSelfCC.getValue())) {
				String userName = addSelfCC.getMetaData().getValue("UserName"); //$NON-NLS-1$
				if (userName != null) {
					if (removeCC.getValues().contains(userName)) {
						removeCC.removeValue(userName);
					} else if (!cc.getValues().contains(userName)) {
						List<String> addCCList = Arrays.asList(addCC.getValue().split("\\s*,\\s*")); //$NON-NLS-1$
						if (!addCCList.contains(userName)) {
							addCC.setValue(userName + (addCCList.size() > 0 && !addCCList.get(0).equals("") ? ", " : "") //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
									+ addCC.getValue());
						}
					}
				}
			}
			if (addCC.getValues().size() > 0 || removeCC.getValues().size() > 0) {
				Set<String> setOld = new HashSet<String>(removeCC.getValues());
				HashSet<String> setNew = new HashSet<String>(Arrays.asList(addCC.getValue().split("\\s*,\\s*"))); //$NON-NLS-1$
				BugzillaRestGsonUtil.getDefault().buildAddRemoveHash(out, "cc", setOld, setNew); //$NON-NLS-1$
			}
			BugzillaRestGsonUtil.buildFlags(out, oldValues.oldAttributes, taskData.getRoot());
			out.endObject();
		}

		@Override
		public OldAttributes read(JsonReader in) throws IOException {
			throw new UnsupportedOperationException(
					"TaskAttributeTypeAdapter in BugzillaRestPutUpdateTask only supports write"); //$NON-NLS-1$
		}

	}

	public BugzillaRestPutUpdateTask(CommonHttpClient client, TaskData taskData, Set<TaskAttribute> oldAttributes) {
		super(client, "/bug/" + taskData.getTaskId(), false); //$NON-NLS-1$
		this.taskData = taskData;
		this.oldAttributes = new OldAttributes(oldAttributes);
	}

	List<NameValuePair> requestParameters;

	@Override
	protected void addHttpRequestEntities(HttpRequestBase request) throws BugzillaRestException {
		super.addHttpRequestEntities(request);
		try {
			Gson gson = new GsonBuilder()
					.registerTypeAdapter(OldAttributes.class, new TaskAttributeTypeAdapter(getClient().getLocation()))
					.create();
			StringEntity requestEntity = new StringEntity(gson.toJson(oldAttributes));
			((HttpPut) request).setEntity(requestEntity);
		} catch (UnsupportedEncodingException e) {
			Throwables.propagate(new CoreException(
					new Status(IStatus.ERROR, BugzillaRestCore.ID_PLUGIN, "Can not build HttpRequest", e))); //$NON-NLS-1$
		}
	}

	public static String convert(String str) {
		str = str.replace("\"", "\\\"").replace("\n", "\\\n"); //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$//$NON-NLS-4$
		StringBuffer ostr = new StringBuffer();
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if ((ch >= 0x0020) && (ch <= 0x007e)) {
				ostr.append(ch);
			} else {
				ostr.append("\\u"); //$NON-NLS-1$
				String hex = Integer.toHexString(str.charAt(i) & 0xFFFF);
				for (int j = 0; j < 4 - hex.length(); j++) {
					ostr.append("0"); //$NON-NLS-1$
				}
				ostr.append(hex.toLowerCase());
			}
		}
		return (new String(ostr));
	}

	@Override
	protected PutUpdateResult parseFromJson(InputStreamReader in) {
		TypeToken<PutUpdateResult> type = new TypeToken<PutUpdateResult>() {
		};
		return new Gson().fromJson(in, type.getType());
	}

}