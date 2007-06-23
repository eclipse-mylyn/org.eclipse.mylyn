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

package org.eclipse.mylyn.internal.tasks.ui;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.eclipse.mylyn.internal.tasks.core.ITaskDataStorage;
import org.eclipse.mylyn.internal.tasks.core.TaskDataState;
import org.eclipse.mylyn.monitor.core.StatusHandler;
import org.eclipse.mylyn.tasks.core.AbstractAttributeFactory;
import org.eclipse.mylyn.tasks.core.RepositoryAttachment;
import org.eclipse.mylyn.tasks.core.RepositoryOperation;
import org.eclipse.mylyn.tasks.core.RepositoryTaskAttribute;
import org.eclipse.mylyn.tasks.core.RepositoryTaskData;
import org.eclipse.mylyn.tasks.core.TaskComment;
import org.eclipse.mylyn.web.core.XmlUtil;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

/**
 * @author Rob Elves
 */
public class OfflineFileStorage implements ITaskDataStorage {

	private static final String ATTRIBUTE_TASK_KIND = "taskKind";

	private static final String ATTRIBUTE_IS_PATCH = "isPatch";

	private static final String ATTRIBUTE_IS_OBSOLETE = "isObsolete";

	private static final String ATTRIBUTE_CREATOR = "creator";

	private static final String ATTRIBUTE_NUMBER = "number";

	private static final String ATTRIBUTE_HAS_ATTACHMENT = "hasAttachment";

	private static final String ATTRIBUTE_ATTACHMENT_ID = "attachmentId";

	private static final String ATTRIBUTE_KNOB_NAME = "knob_name";

	private static final String ATTRIBUTE_OPERATION_NAME = "operationName";

	private static final String ATTRIBUTE_OPTION_NAME = "optionName";

	private static final String ATTRIBUTE_OPTION_SELECTION = "optionSelection";

	private static final String ATTRIBUTE_IS_CHECKED = "isChecked";

	private static final String ATTRIBUTE_INPUT_NAME = "inputName";

	private static final String ATTRIBUTE_INPUT_VALUE = "inputValue";

	private static final String ATTRIBUTE_READONLY = "readonly";

	private static final String ATTRIBUTE_HIDDEN = "hidden";

	private static final String ATTRIBUTE_PARAMETER = "parameter";

	private static final String ATTRIBUTE_VALUE = "value";

	private static final String ELEMENT_META_DATA = "MetaData";

	private static final String ELEMENT_META = "meta";

	private static final String ELEMENT_OPTION = "option";

	private static final String ELEMENT_OPTIONS = "options";

	private static final String ELEMENT_VALUES = "values";

	private static final String ELEMENT_VALUE = "value";

	private static final String ELEMENT_ATTRIBUTE = "Attribute";

	private static final String ELEMENT_NAME = "name";

	private static final String ELEMENT_OPTION_NAMES = "optionNames";

	private static final String ELEMENT_OPERATION = "Operation";

	private static final String ELEMENT_SELECTED = "Selected";

	private static final String ELEMENT_COMMENT = "Comment";

	private static final String ELEMENT_ATTACHMENT = "Attachment";

	private static final String ELEMENT_ATTACHMENTS = "Attachments";

	private static final String ELEMENT_COMMENTS = "Comments";

	private static final String ELEMENT_OPERATIONS = "Operations";

	private static final String ELEMENT_ATTRIBUTES = "Attributes";

	private static final String ATTRIBUTE_REPOSITORY_KIND = "repositoryKind";

	private static final String ATTRIBUTE_REPOSITORY_URL = "repositoryUrl";

	private static final String ATTRIBUTE_KEY = "key";

	private static final String ATTRIBUTE_ID = "id";

	private static final String ATTRIBUTE_NAME = "name";

	private static final String FILE_NAME_INTERNAL = "data.xml";

	private static final String ELEMENT_EDITS_DATA = "EditsData";

	private static final String ELEMENT_OLD_DATA = "OldData";

	private static final String ELEMENT_NEW_DATA = "NewData";

	private static final String ATTRIBUTE_VERSION = "version";

	private static final String ELEMENT_TASK_STATE = "TaskState";

	private static final String ENCODING_UTF_8 = "UTF-8";

	private static final String SCHEMA_VERSION = "1.0";

	private static final String EXTENSION = ".zip";

	private File dataDir;

	// HACK: Remove attribute factories all together!!!
	private static final AbstractAttributeFactory temporaryFactory = new AbstractAttributeFactory() {
		private static final long serialVersionUID = 1L;

		@Override
		public Date getDateForAttributeType(String attributeKey, String dateString) {
			return null;
		}

		@Override
		public boolean isHidden(String key) {
			return false;
		}

		@Override
		public String getName(String key) {
			return key;
		}

		@Override
		public boolean isReadOnly(String key) {
			return false;
		}

		@Override
		public String mapCommonAttributeKey(String key) {
			return key;
		}

	};

	public OfflineFileStorage(File rootFolder) {
		dataDir = rootFolder;
	}

	public void start() throws Exception {
		if (!dataDir.exists()) {
			dataDir.mkdirs();
		}
	}

	public void stop() throws Exception {
		// ignore
	}

	public synchronized TaskDataState get(String repositoryUrl, String id) {
		TaskDataState state = null;
		FileInputStream fileInputStream = null;
		FileLock lock = null;
		try {
			File dataFile = getDataFile(URLEncoder.encode(repositoryUrl, ENCODING_UTF_8), id);
			if (dataFile != null && dataFile.exists()) {

				fileInputStream = new FileInputStream(dataFile);
				FileChannel channel = fileInputStream.getChannel();
				lock = channel.tryLock(0L, Long.MAX_VALUE, true);
				if (lock != null) {
					state = new TaskDataState(repositoryUrl, id);
					ZipInputStream inputStream = new ZipInputStream(fileInputStream);
					ZipEntry entry = inputStream.getNextEntry();
					if (entry != null) {
						XMLMemento input = XMLMemento.createReadRoot(new InputStreamReader(inputStream, ENCODING_UTF_8));
						if (input != null) {
							readData(state, input);
						}
					}
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Error retrieving offline data", false);
		} finally {
			try {
				if (lock != null && lock.isValid()) {
					lock.release();
				}
				if (fileInputStream != null) {
					fileInputStream.close();
				}
			} catch (IOException e) {
				StatusHandler.fail(e, "Error closing offline data input stream", false);
			}
		}

		return state;
	}

	public synchronized void put(TaskDataState taskDataState) {
		FileOutputStream fileOutputStream = null;
		FileLock lock = null;
		try {
			String repositoryFolder = URLEncoder.encode(taskDataState.getNewTaskData().getRepositoryUrl(),
					ENCODING_UTF_8);
			File dataFile = getDataFile(repositoryFolder, taskDataState.getId());
			if (dataFile != null) {
				if (!dataFile.getParentFile().exists()) {
					if (!dataFile.getParentFile().mkdirs()) {
						throw new IOException("Could not create offline data folder: "
								+ dataFile.getParentFile().getAbsolutePath());
					}
				}
				fileOutputStream = new FileOutputStream(dataFile);
				FileChannel channel = fileOutputStream.getChannel();
				lock = null;
				lock = channel.tryLock();
				if (lock != null) {
					final ZipOutputStream outputStream = new ZipOutputStream(fileOutputStream);
					outputStream.setMethod(ZipOutputStream.DEFLATED);
					ZipEntry zipEntry = new ZipEntry(FILE_NAME_INTERNAL);
					outputStream.putNextEntry(zipEntry);

					OutputStreamWriter writer = new OutputStreamWriter(outputStream, ENCODING_UTF_8);
					XMLMemento memento = XMLMemento.createWriteRoot(ELEMENT_TASK_STATE);
					memento.putString(ATTRIBUTE_VERSION, SCHEMA_VERSION);
					if (taskDataState.getNewTaskData() != null) {
						IMemento child = memento.createChild(ELEMENT_NEW_DATA);
						addTaskData(child, taskDataState.getNewTaskData());
					}
					if (taskDataState.getOldTaskData() != null) {
						IMemento child = memento.createChild(ELEMENT_OLD_DATA);
						addTaskData(child, taskDataState.getOldTaskData());
					}
					if (taskDataState.getEdits() != null && taskDataState.getEdits().size() > 0) {
						IMemento child = memento.createChild(ELEMENT_EDITS_DATA);
						addEdits(child, taskDataState.getEdits());
					}
					memento.save(writer);
				}
			}
		} catch (Exception e) {
			StatusHandler.fail(e, "Error saving offline data", false);
		} finally {
			try {
				if (lock != null && lock.isValid()) {
					lock.release();
				}
				if (fileOutputStream != null) {
					fileOutputStream.flush();
					fileOutputStream.close();
				}
			} catch (IOException e) {
				StatusHandler.fail(e, "Error closing offline data output stream", false);
			}
		}

	}

	private void readData(TaskDataState taskState, XMLMemento parent) {
		IMemento newData = parent.getChild(ELEMENT_NEW_DATA);
		if (newData != null) {
			RepositoryTaskData newTaskData = readTaskData(newData);
			if (newTaskData != null) {
				taskState.setNewTaskData(newTaskData);
			}
		}

		IMemento oldData = parent.getChild(ELEMENT_OLD_DATA);
		if (oldData != null) {
			RepositoryTaskData oldTaskData = readTaskData(oldData);
			if (oldTaskData != null) {
				taskState.setOldTaskData(oldTaskData);
			}
		}

		IMemento editsData = parent.getChild(ELEMENT_EDITS_DATA);
		if (editsData != null) {
			Set<RepositoryTaskAttribute> edits = readEdits(editsData);
			if (edits != null) {
				taskState.setEdits(edits);
			}
		}

	}

	private RepositoryTaskData readTaskData(IMemento newData) {
		String kind = newData.getString(ATTRIBUTE_REPOSITORY_KIND);
		String id = newData.getString(ATTRIBUTE_ID);
		String url = newData.getString(ATTRIBUTE_REPOSITORY_URL);
		String taskKind = newData.getString(ATTRIBUTE_TASK_KIND);

		if (kind == null || url == null || id == null) {
			return null;
		}

		RepositoryTaskData data = new RepositoryTaskData(temporaryFactory, kind, url, id, taskKind);
		IMemento attMemento = newData.getChild(ELEMENT_ATTRIBUTES);
		if (attMemento != null) {
			List<RepositoryTaskAttribute> attributes = readAttributes(attMemento);
			for (RepositoryTaskAttribute repositoryTaskAttribute : attributes) {
				data.addAttribute(repositoryTaskAttribute.getId(), repositoryTaskAttribute);
			}
		}

		IMemento opsMemento = newData.getChild(ELEMENT_OPERATIONS);
		if (opsMemento != null) {
			List<RepositoryOperation> operations = readOperations(opsMemento);
			for (RepositoryOperation operation : operations) {
				data.addOperation(operation);
			}
		}

		IMemento commentsMemento = newData.getChild(ELEMENT_COMMENTS);
		if (commentsMemento != null) {
			List<TaskComment> comments = readComments(commentsMemento);
			for (TaskComment comment : comments) {
				data.addComment(comment);
			}
		}

		IMemento attachmentsMemento = newData.getChild(ELEMENT_ATTACHMENTS);
		if (attachmentsMemento != null) {
			List<RepositoryAttachment> attachments = readAttachments(attachmentsMemento);
			for (RepositoryAttachment attachment : attachments) {
				data.addAttachment(attachment);
			}
		}

		return data;

	}

	private void addEdits(IMemento parent, Set<RepositoryTaskAttribute> edits) {
		List<RepositoryTaskAttribute> changedAttributes = new ArrayList<RepositoryTaskAttribute>();
		changedAttributes.addAll(edits);
		addAttributes(parent, changedAttributes);
	}

	private Set<RepositoryTaskAttribute> readEdits(IMemento parent) {
		return new HashSet<RepositoryTaskAttribute>(readAttributes(parent));
	}

	private void addTaskData(IMemento parent, RepositoryTaskData newTaskData) {
		parent.putString(ATTRIBUTE_ID, getCleanText(newTaskData.getId()));
		parent.putString(ATTRIBUTE_TASK_KIND, getCleanText(newTaskData.getTaskKind()));
		parent.putString(ATTRIBUTE_REPOSITORY_URL, getCleanText(newTaskData.getRepositoryUrl()));
		parent.putString(ATTRIBUTE_REPOSITORY_KIND, getCleanText(newTaskData.getRepositoryKind()));

		IMemento attributes = parent.createChild(ELEMENT_ATTRIBUTES);
		addAttributes(attributes, newTaskData.getAttributes());

		IMemento operations = parent.createChild(ELEMENT_OPERATIONS);
		addOperations(operations, newTaskData.getOperations());
		if (newTaskData.getSelectedOperation() != null) {
			addSelectedOperation(operations, newTaskData.getSelectedOperation());
		}

		IMemento comments = parent.createChild(ELEMENT_COMMENTS);
		addComments(comments, newTaskData.getComments());

		IMemento attachments = parent.createChild(ELEMENT_ATTACHMENTS);
		addAttachments(attachments, newTaskData.getAttachments());

	}

	public void addAttachments(IMemento parent, List<RepositoryAttachment> attachments) {
		for (RepositoryAttachment attachment : attachments) {
			IMemento memento = parent.createChild(ELEMENT_ATTACHMENT);
			memento.putString(ATTRIBUTE_IS_PATCH, String.valueOf(attachment.isPatch()));
			memento.putString(ATTRIBUTE_IS_OBSOLETE, String.valueOf(attachment.isObsolete()));
			memento.putString(ATTRIBUTE_CREATOR, getCleanText(attachment.getCreator()));
			memento.putString(ATTRIBUTE_ID, getCleanText(attachment.getTaskId()));
			memento.putString(ATTRIBUTE_REPOSITORY_KIND, getCleanText(attachment.getRepositoryKind()));
			memento.putString(ATTRIBUTE_REPOSITORY_URL, getCleanText(attachment.getRepositoryUrl()));
			IMemento attributes = memento.createChild(ELEMENT_ATTRIBUTES);
			addAttributes(attributes, attachment.getAttributes());
		}
	}

	public List<RepositoryAttachment> readAttachments(IMemento parent) {
		List<RepositoryAttachment> attachments = new ArrayList<RepositoryAttachment>();
		for (IMemento attachmentMemento : parent.getChildren(ELEMENT_ATTACHMENT)) {
			RepositoryAttachment attachment = new RepositoryAttachment(temporaryFactory);
			String isPatch = attachmentMemento.getString(ATTRIBUTE_IS_PATCH);
			String isObsolete = attachmentMemento.getString(ATTRIBUTE_IS_OBSOLETE);
			String taskId = attachmentMemento.getString(ATTRIBUTE_ID);
			String creator = attachmentMemento.getString(ATTRIBUTE_CREATOR);
			String repositoryKind = attachmentMemento.getString(ATTRIBUTE_REPOSITORY_KIND);
			String repositoryUrl = attachmentMemento.getString(ATTRIBUTE_REPOSITORY_URL);

			if (isPatch != null) {
				attachment.setPatch(Boolean.parseBoolean(isPatch));
			}
			if (isObsolete != null) {
				attachment.setObsolete(Boolean.parseBoolean(isObsolete));
			}
			if (creator != null) {
				attachment.setCreator(creator);
			}
			if (repositoryKind != null) {
				attachment.setRepositoryKind(repositoryKind);
			}
			if (repositoryUrl != null) {
				attachment.setRepositoryUrl(repositoryUrl);
			}
			if (taskId != null) {
				attachment.setTaskId(taskId);
			}
			IMemento attributesMemento = attachmentMemento.getChild(ELEMENT_ATTRIBUTES);
			if (attributesMemento != null) {
				List<RepositoryTaskAttribute> attributes = readAttributes(attributesMemento);
				for (RepositoryTaskAttribute repositoryTaskAttribute : attributes) {
					attachment.addAttribute(repositoryTaskAttribute.getId(), repositoryTaskAttribute);
				}
			}
			attachments.add(attachment);
		}
		return attachments;
	}

	public void addComments(IMemento parent, List<TaskComment> comments) {
		for (TaskComment taskComment : comments) {
			IMemento comment = parent.createChild(ELEMENT_COMMENT);
			comment.putInteger(ATTRIBUTE_NUMBER, taskComment.getNumber());
			comment.putString(ATTRIBUTE_HAS_ATTACHMENT, String.valueOf(taskComment.hasAttachment()));
			comment.putString(ATTRIBUTE_ATTACHMENT_ID, getCleanText(taskComment.getAttachmentId()));
			IMemento attributes = comment.createChild(ELEMENT_ATTRIBUTES);
			addAttributes(attributes, taskComment.getAttributes());
		}
	}

	public List<TaskComment> readComments(IMemento parent) {
		List<TaskComment> comments = new ArrayList<TaskComment>();
		for (IMemento commentMemento : parent.getChildren(ELEMENT_COMMENT)) {
			Integer commentNumber = commentMemento.getInteger(ATTRIBUTE_NUMBER);
			String hasAttachment = commentMemento.getString(ATTRIBUTE_HAS_ATTACHMENT);
			String attachmentId = commentMemento.getString(ATTRIBUTE_ATTACHMENT_ID);
			if (commentNumber != null) {
				TaskComment comment = new TaskComment(temporaryFactory, commentNumber);
				if (hasAttachment != null) {
					comment.setHasAttachment(Boolean.parseBoolean(hasAttachment));
				}
				if (attachmentId != null) {
					comment.setAttachmentId(attachmentId);
				}
				IMemento attributesMemento = commentMemento.getChild(ELEMENT_ATTRIBUTES);
				if (attributesMemento != null) {
					List<RepositoryTaskAttribute> attributes = readAttributes(attributesMemento);
					for (RepositoryTaskAttribute repositoryTaskAttribute : attributes) {
						comment.addAttribute(repositoryTaskAttribute.getId(), repositoryTaskAttribute);
					}
				}
				comments.add(comment);
			}
		}
		return comments;
	}

	private void addSelectedOperation(IMemento operations, RepositoryOperation selectedOperation) {
		IMemento selected = operations.createChild(ELEMENT_SELECTED);
		ArrayList<RepositoryOperation> list = new ArrayList<RepositoryOperation>();
		list.add(selectedOperation);
		addOperations(selected, list);
	}

	public void addOperations(IMemento parent, List<RepositoryOperation> operations) {
		for (RepositoryOperation operation : operations) {
			IMemento operationMemento = parent.createChild(ELEMENT_OPERATION);
			operationMemento.putString(ATTRIBUTE_KNOB_NAME, getCleanText(operation.getKnobName()));
			operationMemento.putString(ATTRIBUTE_OPERATION_NAME, getCleanText(operation.getOperationName()));
			operationMemento.putString(ATTRIBUTE_IS_CHECKED, String.valueOf(operation.isChecked()));
			if (operation.isInput()) {
				operationMemento.putString(ATTRIBUTE_INPUT_NAME, getCleanText(operation.getInputName()));
				operationMemento.putString(ATTRIBUTE_INPUT_VALUE, getCleanText(operation.getInputValue()));
			}
			if (operation.hasOptions()) {
				operationMemento.putString(ATTRIBUTE_OPTION_NAME, getCleanText(operation.getOptionName()));
				operationMemento.putString(ATTRIBUTE_OPTION_SELECTION, getCleanText(operation.getOptionSelection()));

				if (operation.getOptionNames() != null && operation.getOptionNames().size() > 0) {
					IMemento optionNames = operationMemento.createChild(ELEMENT_OPTION_NAMES);
					for (String name : operation.getOptionNames()) {
						IMemento nameMemento = optionNames.createChild(ELEMENT_NAME);
						nameMemento.putTextData(getCleanText(name));
						nameMemento.putString(ATTRIBUTE_VALUE, getCleanText(operation.getOptionValue(name)));
					}
				}
			}
		}
	}

	/* public for testing */
	public List<RepositoryOperation> readOperations(IMemento parent) {
		List<RepositoryOperation> operations = new ArrayList<RepositoryOperation>();
		for (IMemento operationMemento : parent.getChildren(ELEMENT_OPERATION)) {
			String knobName = operationMemento.getString(ATTRIBUTE_KNOB_NAME);
			String operationName = operationMemento.getString(ATTRIBUTE_OPERATION_NAME);
			String optionName = operationMemento.getString(ATTRIBUTE_OPTION_NAME);
			String selection = operationMemento.getString(ATTRIBUTE_OPTION_SELECTION);
			String isChecked = operationMemento.getString(ATTRIBUTE_IS_CHECKED);
			String inputName = operationMemento.getString(ATTRIBUTE_INPUT_NAME);
			String inputValue = operationMemento.getString(ATTRIBUTE_INPUT_VALUE);

			if (knobName == null || operationName == null)
				continue;

			RepositoryOperation op = new RepositoryOperation(knobName, operationName);
			if (optionName != null) {
				op.setUpOptions(optionName);
			}
			if (isChecked != null) {
				op.setChecked(Boolean.parseBoolean(isChecked));
			}
			if (inputName != null) {
				op.setInputName(inputName);
			}
			if (inputValue != null) {
				op.setInputValue(inputValue);
			}

			IMemento optionNames = operationMemento.getChild(ELEMENT_OPTION_NAMES);
			if (optionNames != null) {
				for (IMemento nameMemento : optionNames.getChildren(ELEMENT_NAME)) {
					if (nameMemento.getTextData() != null && nameMemento.getTextData().length() > 0) {
						op.addOption(nameMemento.getTextData(), nameMemento.getString(ATTRIBUTE_VALUE));
					}
				}
			}

			// Selection must be applied after addition of options
			if (selection != null) {
				op.setOptionSelection(selection);
			}

			operations.add(op);

		}
		return operations;
	}

	/* public for testing */
	public void addAttributes(IMemento parent, List<RepositoryTaskAttribute> attributes) {
		for (RepositoryTaskAttribute attribute : attributes) {
			IMemento attribMemento = parent.createChild(ELEMENT_ATTRIBUTE);
			attribMemento.putString(ATTRIBUTE_ID, getCleanText(attribute.getId()));
			attribMemento.putString(ATTRIBUTE_NAME, getCleanText(attribute.getName()));
			attribMemento.putString(ATTRIBUTE_HIDDEN, String.valueOf(attribute.isHidden()));
			attribMemento.putString(ATTRIBUTE_READONLY, String.valueOf(attribute.isReadOnly()));

			IMemento values = attribMemento.createChild(ELEMENT_VALUES);
			for (String value : attribute.getValues()) {
				values.createChild(ELEMENT_VALUE).putTextData(getCleanText(value));
			}

			IMemento options = attribMemento.createChild(ELEMENT_OPTIONS);
			for (String optionValue : attribute.getOptions()) {
				IMemento option = options.createChild(ELEMENT_OPTION);
				option.putTextData(getCleanText(optionValue));
				String parameter = attribute.getOptionParameter(optionValue);
				if (parameter != null) {
					option.putString(ATTRIBUTE_PARAMETER, getCleanText(parameter));
				}
			}
			IMemento metaData = attribMemento.createChild(ELEMENT_META_DATA);
			Map<String, String> metadata = attribute.getMetaData();
			if (metadata != null && metadata.size() > 0) {
				for (String key : metadata.keySet()) {
					IMemento meta = metaData.createChild(ELEMENT_META);
					meta.putString(ATTRIBUTE_KEY, getCleanText(key));
					meta.putTextData(getCleanText(metadata.get(key)));
				}
			}
		}
	}

	private String getCleanText(String text) {
		if (text == null)
			return "";
		String result = XmlUtil.cleanXmlString(text);
		if (result == null) {
			result = "";
		}
		return result;
	}

	/* public for testing */
	public List<RepositoryTaskAttribute> readAttributes(IMemento parent) {
		List<RepositoryTaskAttribute> attributes = new ArrayList<RepositoryTaskAttribute>();
		for (IMemento attrMemento : parent.getChildren(ELEMENT_ATTRIBUTE)) {
			String id = attrMemento.getString(ATTRIBUTE_ID);
			String name = attrMemento.getString(ATTRIBUTE_NAME);
			String hidden = attrMemento.getString(ATTRIBUTE_HIDDEN);
			if (id != null && name != null && hidden != null) {
				RepositoryTaskAttribute attribute = new RepositoryTaskAttribute(id, name, Boolean.parseBoolean(hidden));
				attributes.add(attribute);
				String readOnly = attrMemento.getString(ATTRIBUTE_READONLY);
				if (readOnly != null) {
					attribute.setReadOnly(Boolean.parseBoolean(readOnly));
				}

				IMemento values = attrMemento.getChild(ELEMENT_VALUES);
				if (values != null) {
					for (IMemento valueMemento : values.getChildren(ELEMENT_VALUE)) {
						attribute.addValue(getCleanText(valueMemento.getTextData()));
					}
				}

				IMemento options = attrMemento.getChild(ELEMENT_OPTIONS);
				if (options != null) {
					for (IMemento optionMemento : options.getChildren(ELEMENT_OPTION)) {
						attribute.addOption(getCleanText(optionMemento.getTextData()),
								optionMemento.getString(ATTRIBUTE_PARAMETER));
					}
				}

				IMemento metaData = attrMemento.getChild(ELEMENT_META_DATA);
				if (metaData != null) {
					for (IMemento optionMemento : metaData.getChildren(ELEMENT_META)) {
						attribute.putMetaDataValue(optionMemento.getString(ATTRIBUTE_KEY),
								getCleanText(optionMemento.getTextData()));
					}
				}
			}
		}
		return attributes;
	}

	public void remove(String repositoryUrl, String id) {
		if (repositoryUrl == null || id == null)
			return;
		File file;
		try {
			file = getDataFile(URLEncoder.encode(repositoryUrl, ENCODING_UTF_8), id);
			if (file != null && file.exists()) {
				file.delete();
			}

			if (file != null && file.getParentFile() != null && file.getParentFile().exists()
					&& file.getParentFile().list().length == 0) {
				file.getParentFile().delete();
			}

			// TODO: Remove folder if last file removed

		} catch (Exception e) {
			StatusHandler.fail(e, "Error removing offline data: " + repositoryUrl + "-" + id, false);
		}

	}

	private File getDataFile(String folder, String id) throws IOException {
		File repositoryFolder = new File(dataDir, folder);
		File dataFile = new File(repositoryFolder, id + EXTENSION);
		return dataFile;
	}

	/**
	 * Delete entire offline repositoy contents, FOR TESTING ONLY
	 */
	public void clear() {
		if (dataDir.exists()) {
			for (File file : dataDir.listFiles()) {
				destroy(file);
			}
		}
	}

	private void destroy(File folder) {
		if (folder.isDirectory()) {
			for (File file : folder.listFiles()) {
				destroy(file);
			}
		}
		folder.delete();
	}

	public void flush() {
		// ignore;
	}

}
