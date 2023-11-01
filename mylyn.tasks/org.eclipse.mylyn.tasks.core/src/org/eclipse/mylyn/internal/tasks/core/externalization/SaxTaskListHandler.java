/*******************************************************************************
 * Copyright (c) 2016 Tasktop Technologies and others.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.externalization;

import java.util.Collection;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.commons.core.StatusHandler;
import org.eclipse.mylyn.internal.tasks.core.AbstractTask;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskCategory;
import org.eclipse.mylyn.internal.tasks.core.AbstractTaskContainer;
import org.eclipse.mylyn.internal.tasks.core.ITransferList;
import org.eclipse.mylyn.internal.tasks.core.RepositoryModel;
import org.eclipse.mylyn.internal.tasks.core.RepositoryQuery;
import org.eclipse.mylyn.tasks.core.IRepositoryElement;
import org.eclipse.mylyn.tasks.core.IRepositoryManager;
import org.w3c.dom.Document;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SaxTaskListHandler extends DefaultHandler {

	private final LazyTransferList taskList;

	private final RepositoryModel repositoryModel;

	private final IRepositoryManager repositoryManager;

	private final MultiValuedMap<AbstractTask, String> subTasks;

	private final MultiValuedMap<RepositoryQuery, String> queryResults;

	private final MultiValuedMap<AbstractTaskCategory, String> categorizedTasks;

	private SaxTaskListElementBuilder<? extends IRepositoryElement> currentBuilder;

	private final SaxOrphanBuilder orphanBuilder;

	public SaxTaskListHandler(ITransferList taskList, RepositoryModel repositoryModel,
			IRepositoryManager repositoryManager) throws CoreException {
		this.taskList = new LazyTransferList(taskList);
		this.repositoryModel = repositoryModel;
		this.repositoryManager = repositoryManager;

		this.subTasks = new HashSetValuedHashMap<>();
		this.queryResults = new HashSetValuedHashMap<>();
		this.categorizedTasks = new HashSetValuedHashMap<>();

		this.orphanBuilder = new SaxOrphanBuilder();
	}

	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		switch (localName) {
		case TaskListExternalizationConstants.NODE_TASK:
			checkState(currentBuilder == null, "Cannot begin reading a task while reading another task list element."); //$NON-NLS-1$

			currentBuilder = new SaxTaskBuilder(repositoryModel, repositoryManager);
			currentBuilder.beginItem(attributes);

			break;
		case TaskListExternalizationConstants.NODE_QUERY:
			checkState(currentBuilder == null, "Cannot begin reading a query while reading another task list element."); //$NON-NLS-1$

			currentBuilder = new SaxQueryBuilder(repositoryModel, repositoryManager);
			currentBuilder.beginItem(attributes);

			break;
		case TaskListExternalizationConstants.NODE_CATEGORY:
			checkState(currentBuilder == null,
					"Cannot begin reading a category while reading another task list element."); //$NON-NLS-1$

			currentBuilder = new SaxCategoryBuilder(taskList);
			currentBuilder.beginItem(attributes);

			break;
		case TaskListExternalizationConstants.NODE_ATTRIBUTE:
			if (isOK(currentBuilder) && !currentBuilder.isAcceptingAttributeValues()) {
				currentBuilder.startAttribute(attributes);
			}

			break;
		case TaskListExternalizationConstants.NODE_SUB_TASK:
			checkState(currentBuilder instanceof SaxTaskBuilder, "Cannot read a sub task hit unless reading a task"); //$NON-NLS-1$

			recordHit(attributes, subTasks, (SaxTaskBuilder) currentBuilder);

			break;
		case TaskListExternalizationConstants.NODE_QUERY_HIT:
			checkState(currentBuilder instanceof SaxQueryBuilder, "Cannot read a query hit unless reading a query"); //$NON-NLS-1$

			recordHit(attributes, queryResults, (SaxQueryBuilder) currentBuilder);

			break;
		case TaskListExternalizationConstants.NODE_TASK_REFERENCE:
			checkState(currentBuilder instanceof SaxCategoryBuilder,
					"Cannot read a category hit unless reading a category"); //$NON-NLS-1$

			recordHit(attributes, categorizedTasks, (SaxCategoryBuilder) currentBuilder);

			break;
		default:
			break;
		}

		// don't attempt to make the root element an orphan
		if (!TaskListExternalizationConstants.NODE_TASK_LIST.equals(localName)) {
			orphanBuilder.startElement(localName, attributes);
		}
	}

	@Override
	public void characters(char ch[], int start, int length) throws SAXException {
		if (isOK(currentBuilder) && currentBuilder.isAcceptingAttributeValues()) {
			currentBuilder.acceptAttributeValueContent(ch, start, length);
		}
		orphanBuilder.acceptCharacters(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		switch (localName) {
		case TaskListExternalizationConstants.NODE_TASK:
		case TaskListExternalizationConstants.NODE_QUERY:
		case TaskListExternalizationConstants.NODE_CATEGORY:
			commitCurrentTopLevelElement();
			currentBuilder = null;

			break;
		case TaskListExternalizationConstants.NODE_ATTRIBUTE:
			if (isOK(currentBuilder) && currentBuilder.isAcceptingAttributeValues()) {
				currentBuilder.endAttribute();
			}

			break;
		case TaskListExternalizationConstants.NODE_TASK_LIST:
			applyContainmentToTaskList(subTasks);
			applyContainmentToTaskList(queryResults);
			applyContainmentToTaskList(categorizedTasks);
			commitUntransferedTasksToTaskList();
			break;
		default:
			break;
		}

		orphanBuilder.endElement();

	}

	private void commitUntransferedTasksToTaskList() {
		taskList.commit();
	}

	private <T extends IRepositoryElement> void recordHit(Attributes attributes, MultiValuedMap<T, String> hitMap,
			SaxTaskListElementBuilder<T> builder) {
		String handle = attributes.getValue(TaskListExternalizationConstants.KEY_HANDLE);
		if (StringUtils.isNotEmpty(handle) && isOK(builder)) {
			hitMap.put(builder.getItem(), handle);
		}
	}

	private void commitCurrentTopLevelElement() throws SAXException {
		checkState(currentBuilder != null,
				"Cannot finish reading a task list element without a corresponding builder."); //$NON-NLS-1$

		if (isOK(currentBuilder)) {
			currentBuilder.addToTaskList(taskList);
		} else {
			if (!currentBuilder.getErrors().isOK()) {
				StatusHandler.log(currentBuilder.getErrors());
			}
			orphanBuilder.commitOrphan();
		}
	}

	private <T extends AbstractTaskContainer> void applyContainmentToTaskList(MultiValuedMap<T, String> containment) {
		for (T container : containment.keySet()) {
			Collection<String> handles = containment.get(container);
			for (String handle : handles) {
				AbstractTask subTask = taskList.getTask(handle);
				if (subTask != null) {
					taskList.addTask(subTask, container);
				}
			}
		}
	}

	private boolean isOK(SaxTaskListElementBuilder<?> builder) {
		return builder != null && builder.getErrors().isOK() && builder.getItem() != null;
	}

	public Document getOrphans() {
		return orphanBuilder.getOrphans();
	}

	private void checkState(boolean condition, String message) throws SAXException {
		if (!condition) {
			throw new SAXException(message);
		}
	}
}
