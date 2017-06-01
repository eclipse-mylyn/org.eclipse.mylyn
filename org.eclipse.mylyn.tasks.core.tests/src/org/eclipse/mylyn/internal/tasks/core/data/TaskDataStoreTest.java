/*******************************************************************************
 * Copyright (c) 2017 Tasktop Technologies and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Tasktop Technologies - initial API and implementation
 *******************************************************************************/

package org.eclipse.mylyn.internal.tasks.core.data;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.mylyn.internal.tasks.core.TaskRepositoryManager;
import org.eclipse.mylyn.tasks.core.TaskRepository;
import org.eclipse.mylyn.tasks.core.data.ITaskDataWorkingCopy;
import org.eclipse.mylyn.tasks.core.data.TaskAttributeMapper;
import org.eclipse.mylyn.tasks.core.data.TaskData;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.xml.sax.SAXException;

import com.google.common.base.Charsets;
import com.google.common.io.ByteStreams;

public class TaskDataStoreTest {

	@Rule
	public TemporaryFolder folder = new TemporaryFolder();

	private static final int THREAD_COUNT = 10;

	private static final long DELAY = 100;

	private static final String DATA_XML_CONTENT = "test";

	private static final TaskData TASK_DATA = newTaskData();

	private static final TaskDataState TEST_STATE = new TaskDataState("connectorKind", "repositoryUrl", "taskId");

	@Test
	public void getTaskDataStateByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(getTaskDataState(file));

		assertNoFailures(failures);
	}

	@Test
	public void discardEditsByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(discardEdits(file));

		assertNoFailures(failures);
	}

	@Test
	public void putEditsByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(putEdits(file));

		assertNoFailures(failures);
	}

	@Test
	public void putTaskDataSetLastReadUserByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(putTaskData(file));

		assertNoFailures(failures);
	}

	@Test
	public void setTaskDataByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(setTaskData(file));

		assertNoFailures(failures);
	}

	@Test
	public void putTaskDataByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(putTaskDataState(file));

		assertNoFailures(failures);
	}

	@Test
	public void deleteTaskDataByMultipleThreads() throws Exception {
		File file = newTaskDataZipFile();

		List<Throwable> failures = runSameByMultipleThreads(deleteTaskData(file));

		assertNoFailures(failures);
	}

	@Test
	public void manipulateTaskDataConcurrently() throws Exception {
		File file = newTaskDataZipFile();

		// run multiple times to increase chance of a failure
		for (int i = 0; i <= 10; i++) {
			List<Throwable> failures = runConcurrently( //
					getTaskDataState(file), //
					discardEdits(file), //
					putEdits(file), //
					putTaskData(file), //
					setTaskData(file), //
					putTaskDataState(file), //
					deleteTaskData(file));
			assertNoFailures(failures);
		}
	}

	@Test
	public void manipulateTaskDataByFixedNumberOfThreads() throws Exception {
		File file = newTaskDataZipFile();
		TaskDataStore store = newTaskDataStore();
		List<Throwable> failures = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(4);

		List<Thread> threads = new ArrayList<>();
		threads.addAll(Collections.nCopies(3, thread(getTaskDataState(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(discardEdits(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(putEdits(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(putTaskData(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(setTaskData(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(putTaskDataState(file), store, Optional.empty(), failures).get()));
		threads.addAll(Collections.nCopies(3, thread(deleteTaskData(file), store, Optional.empty(), failures).get()));
		Collections.shuffle(threads);

		threads.stream().forEach(thread -> executor.submit(thread));

		executor.shutdown();

		assertTrue(executor.awaitTermination(30, TimeUnit.SECONDS));
		assertNoFailures(failures);
	}

	private static TaskDataStore newTaskDataStore() {
		TaskRepositoryManager manager = new TaskRepositoryManager();
		TaskDataExternalizer externalizer = new TaskDataExternalizer(manager) {
			@Override
			public TaskDataState readState(InputStream in) throws IOException, SAXException {
				// read the input stream fully but do not return the result, not relevant for the purpose of the test
				assertEquals(DATA_XML_CONTENT, new String(ByteStreams.toByteArray(in), Charsets.UTF_8));
				in.close();
				// pretend that reading state takes more than the blink of an eye
				sleep();
				return TEST_STATE;
			};

			@Override
			public void writeState(OutputStream out, ITaskDataWorkingCopy state) throws IOException {
				out.write(DATA_XML_CONTENT.getBytes(Charsets.UTF_8));
				// pretend that writing state takes more than the blink of an eye
				sleep();
			}
		};
		return new TaskDataStore(externalizer);
	}

	private static void sleep() {
		try {
			Thread.sleep(DELAY);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
			throw new RuntimeException(e);
		}
	}

	private File newTaskDataZipFile() throws IOException {
		File file = folder.newFile("test.zip");
		try (ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file))) {
			ZipEntry entry = new ZipEntry("data.xml");
			outputStream.putNextEntry(entry);
			byte[] data = DATA_XML_CONTENT.getBytes();
			outputStream.write(data, 0, data.length);
			outputStream.closeEntry();
		}
		return file;
	}

	private static TaskData newTaskData() {
		TaskRepository repository = new TaskRepository("connectorKind", "repositoryUrl");
		TaskAttributeMapper mapper = new TaskAttributeMapper(repository);
		return new TaskData(mapper, "connectorKind", "repositoryUrl", "taskId");
	}

	@FunctionalInterface
	public interface ConsumerWithCoreException<T> {
		public void accept(T t) throws CoreException;
	}

	private ConsumerWithCoreException<TaskDataStore> getTaskDataState(File file) {
		return store -> store.getTaskDataState(file);
	}

	private ConsumerWithCoreException<TaskDataStore> discardEdits(File file) {
		return store -> store.discardEdits(file);
	}

	private ConsumerWithCoreException<TaskDataStore> putEdits(File file) {
		return store -> store.putEdits(file, TASK_DATA);
	}

	private ConsumerWithCoreException<TaskDataStore> putTaskData(File file) {
		return store -> store.putTaskData(file, TASK_DATA, true, true);
	}

	private ConsumerWithCoreException<TaskDataStore> setTaskData(File file) {
		return store -> store.setTaskData(file, TASK_DATA);
	}

	private ConsumerWithCoreException<TaskDataStore> putTaskDataState(File file) {
		return store -> store.putTaskData(file, TEST_STATE);
	}

	private ConsumerWithCoreException<TaskDataStore> deleteTaskData(File file) {
		return store -> store.deleteTaskData(file);
	}

	private static List<Throwable> runSameByMultipleThreads(ConsumerWithCoreException<TaskDataStore> consumer)
			throws Exception {
		TaskDataStore store = newTaskDataStore();
		CyclicBarrier barrier = new CyclicBarrier(THREAD_COUNT + 1);
		List<Throwable> failures = new ArrayList<>(THREAD_COUNT);

		List<Thread> threads = Stream.generate(thread(consumer, store, Optional.of(barrier), failures))
				.limit(THREAD_COUNT)
				.collect(toList());

		startThreadsSimultaneously(barrier, threads);
		return failures;
	}

	@SafeVarargs
	private static List<Throwable> runConcurrently(ConsumerWithCoreException<TaskDataStore>... consumers)
			throws Exception {
		TaskDataStore store = newTaskDataStore();
		CyclicBarrier barrier = new CyclicBarrier(consumers.length + 1);
		List<Throwable> failures = new ArrayList<>(consumers.length);

		List<Thread> threads = Arrays.asList(consumers)
				.stream()
				.map(c -> thread(c, store, Optional.of(barrier), failures).get())
				.collect(toList());

		startThreadsSimultaneously(barrier, threads);
		return failures;
	}

	private static void startThreadsSimultaneously(CyclicBarrier barrier, List<Thread> threads) throws Exception {
		threads.stream().forEach(Thread::start);
		barrier.await();
		for (Thread thread : threads) {
			thread.join();
		}
	}

	private static Supplier<Thread> thread(ConsumerWithCoreException<TaskDataStore> consumer, TaskDataStore store,
			Optional<CyclicBarrier> barrier, List<Throwable> failures) {
		return new Supplier<Thread>() {

			@Override
			public Thread get() {
				return new Thread(new Runnable() {

					@Override
					public void run() {
						try {
							barrier.ifPresent(b -> {
								try {
									b.await();
								} catch (InterruptedException | BrokenBarrierException e) {
									throw new RuntimeException(e);
								}
							});
							consumer.accept(store);
						} catch (Throwable e) {
							failures.add(e);
						}
					}
				});
			}
		};
	}

	private static void assertNoFailures(List<Throwable> failures) {
		assertTrue(format("expected no failures but found %d:\n%s", failures.size(), collectMessages(failures)),
				failures.isEmpty());
	}

	private static String collectMessages(List<Throwable> failures) {
		return failures.stream().map(e -> e.getClass().getSimpleName()).collect(Collectors.joining("\n"));
	}

}
