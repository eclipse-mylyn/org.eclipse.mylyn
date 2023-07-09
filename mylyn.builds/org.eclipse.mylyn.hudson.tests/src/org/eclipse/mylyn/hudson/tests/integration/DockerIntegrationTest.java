package org.eclipse.mylyn.hudson.tests.integration;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.eclipse.mylyn.commons.repositories.core.RepositoryLocation;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationCredentials;
import org.eclipse.mylyn.commons.repositories.core.auth.AuthenticationType;
import org.eclipse.mylyn.commons.repositories.core.auth.UserCredentials;
import org.eclipse.mylyn.internal.commons.core.operations.NullOperationMonitor;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfiguration;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonConfigurationCache;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonException;
import org.eclipse.mylyn.internal.hudson.core.client.HudsonServerInfo;
import org.eclipse.mylyn.internal.hudson.core.client.RestfulHudsonClient;
import org.eclipse.mylyn.internal.hudson.model.HudsonModelJob;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.w3c.dom.Document;

public class DockerIntegrationTest {

	private static final String baseDir = "../../org.eclipse.mylyn.releng/multipass/roles/jenkins/files/";

	@ClassRule
	public static final GenericContainer jenkinsContainer = new GenericContainer(new ImageFromDockerfile() //
			.withDockerfile(new File(baseDir + "DockerfileUnitTest").toPath())
			.withBuildArg("DATA_DIR", baseDir) //
	).withExposedPorts(8080);

	private static final NullOperationMonitor monitor = new NullOperationMonitor();

	private static RepositoryLocation location;

	private static RestfulHudsonClient hud;

	@BeforeClass
	public static void waitForServer() {
		jenkinsContainer.start();

		location = new RepositoryLocation(getServerBase());
		String address = getServerBase();
		System.out.println(address);

		AuthenticationCredentials creds = new UserCredentials("admin", "admin");
		AuthenticationType authType = AuthenticationType.REPOSITORY;
		location.setCredentials(authType, creds);
		hud = new RestfulHudsonClient(location, new HudsonConfigurationCache());

		System.out.println("Waiting for Docker image to be ready");
		for (int i = 0; i < 6; i++) {
			try {
				hud.validate(monitor);
			} catch (HudsonException e) {
				System.out.println("waiting...");
			}
			try {
				Thread.sleep(4000); // Give Jenkins time to settle down
			} catch (InterruptedException e) {
			}
		}

		System.out.println("Docker image is ready:\n" + jenkinsContainer.getLogs());
	}

	@AfterClass
	public static void shutdown() {
		jenkinsContainer.close();
	}

	private static String getServerBase() {
		String baseUrl = "http://" + jenkinsContainer.getHost() + ":" + jenkinsContainer.getMappedPort(8080);
		return baseUrl;
	}

	@Test
	public void validate() throws HudsonException {
		HudsonServerInfo info = hud.validate(monitor);
		assertNotNull("Unable to retrieve server info", info);
		System.out.println("Jenkins version=" + info.getVersion());

	}

	@Test
	public void getJob() throws HudsonException {
		HudsonModelJob job = new HudsonModelJob();
		job.setName("mylyn-builds-nightly");

		Document jobConfig = hud.getJobConfig(job, monitor);
		assertNotNull("Unable to retrieve job config", jobConfig);
		System.out.println("jobConfig=" + jobConfig.getNodeName());
	}

	@Test
	public void getJobs() throws HudsonException {
		List<HudsonModelJob> jobs = hud.getJobs(null, monitor);
		assertNotNull("Unable to retrieve jobs", jobs);
		System.out.println(
				"jobs=" + jobs.stream().map(job -> String.valueOf(job.getName())).collect(Collectors.joining(",")));
	}

	@Test
	public void getConfiguration() {
		HudsonConfiguration config = hud.getConfiguration();
		assertNotNull("Unable to retrieve configuration", config);
		System.out.println("jenings config=" + config.toString());

	}
}