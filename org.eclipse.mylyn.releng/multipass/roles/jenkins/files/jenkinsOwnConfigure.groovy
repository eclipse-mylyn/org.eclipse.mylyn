import jenkins.model.JenkinsLocationConfiguration
import java.util.logging.Level;
import java.util.logging.Logger;
import jenkins.model.*
import hudson.model.*
import hudson.slaves.*
import hudson.plugins.sshslaves.*
import java.util.ArrayList;
import groovy.json.JsonSlurper;
import hudson.slaves.EnvironmentVariablesNodeProperty.Entry;
import jenkins.model.*
import hudson.security.*
import org.jenkinsci.plugins.matrixauth.*;

//Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());
Logger logger = Logger.getLogger("org.eclipse.mylyn.releng.multipass.jenkins");

def env = System.getenv()

logger.info("jenkinsOwnConfigure: " + env.JENKINS_URL)
logger.warning("jenkinsOwnConfigure: " + env.JENKINS_ADMIN_ADRESS)
logger.log(Level.INFO, "jenkinsOwnConfigure JENKINS_URL: " + env.JENKINS_URL);
logger.log(Level.WARNING, "jenkinsOwnConfigure JENKINS_ADMIN_ADRESS: " + env.JENKINS_ADMIN_ADRESS);

def config = JenkinsLocationConfiguration.get();
def instance = Jenkins.getInstance();

config.setUrl(env.JENKINS_URL);
config.setAdminAddress(env.JENKINS_ADMIN_ADRESS);
config.save()

def hudsonRealm = new HudsonPrivateSecurityRealm(false)
hudsonRealm.createAccount("admin@mylyn.eclipse.org","mylyntest")
instance.setSecurityRealm(hudsonRealm)

def hudsonRealmUser = new HudsonPrivateSecurityRealm(false)
hudsonRealmUser.createAccount("tests@mylyn.eclipse.org","mylyntest")
instance.setSecurityRealm(hudsonRealmUser)

def strategy = new GlobalMatrixAuthorizationStrategy()
strategy.add(Jenkins.ADMINISTER, PermissionEntry.user("admin@mylyn.eclipse.org"))
strategy.add(Jenkins.READ, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Computer.BUILD, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Item.BUILD, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Item.CANCEL, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Item.READ, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Item.WORKSPACE, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.Run.DELETE, PermissionEntry.user("tests@mylyn.eclipse.org"))
strategy.add(hudson.model.View.READ, PermissionEntry.user("tests@mylyn.eclipse.org"))

instance.setAuthorizationStrategy(strategy)

instance.setSlaveAgentPort(50000);
instance.setNumExecutors(0);
instance.save()


File f = new File( '/var/jenkins_home/slaveDef.json' )
if( f.exists() ) {
	def sDef = f.withReader { r ->
		new JsonSlurper().parse( r )
	}

	sDef.each { val ->
		def computer = instance.getComputer("${val.nodeName}");
		if ( computer == null) {
			List<Entry> nodeEnv = new ArrayList<Entry>();
			nodeEnv.add(new Entry("key1","value1"));
			nodeEnv.add(new Entry("key2","value2"));
			EnvironmentVariablesNodeProperty envPro = new EnvironmentVariablesNodeProperty(nodeEnv);
			logger.log(Level.INFO, "create Agent ${val.nodeName} " + val.nodeLables.join(" "));
			Slave slave = new DumbSlave(
					"${val.nodeName}",
					"${val.nodeDesc}" ?: "",
					"/home/jenkins",
					"${val.executors}" ?: "1",
					Node.Mode.NORMAL,
					"" + "${val.nodeLables.join(" ")}",
					new JNLPLauncher(),
					new RetentionStrategy.Always(),
					new LinkedList())
			slave.getNodeProperties().add(envPro)
			instance.addNode(slave)
		}
	}
}