import jenkins.model.JenkinsLocationConfiguration

def env = System.getenv()

def config = JenkinsLocationConfiguration.get();
config.setUrl(env.JENKINS_URL);
config.setAdminAddress(env.JENKINS_ADMIN_ADRESS);
config.save()
