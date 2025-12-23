Jenkins Local Setup
This directory contains the configuration for running a local Jenkins instance in Docker for the mr-jenk project. The goal is to provide a reproducible CI/CD environment for the team.

Prerequisites
Docker and Docker Compose installed on your machine.
​

Access to the mr-jenk Git repository (Gitea as primary remote, GitHub as mirror if configured).
​

What this Jenkins instance is for
Local CI/CD sandbox for the Buy-One e‑commerce platform.

Allows the team to:

Run the same pipelines locally that will run on shared CI.

Experiment with pipeline changes without affecting others.

Practice Jenkins administration and plugin configuration.

This Jenkins runs only on your machine and is safe to break and rebuild.

Directory contents
docker-compose.yml

Defines a single Jenkins controller container (jenkins/jenkins:lts).
​

The Jenkins home directory is stored in a Docker volume (jenkins_home), so jobs and configuration persist across container restarts.

How to start Jenkins
From the project root:

bash
cd infra/jenkins
docker compose up -d
Then open Jenkins in your browser:

URL: http://localhost:8080

To check that the container is running:

bash
docker ps | grep jenkins
You should see a container named jenkins (or the name defined in docker-compose.yml).
​

First-time setup
On the first run:

Open http://localhost:8080.

Jenkins will ask for an initial admin password.

Get it from the container logs:

bash
docker logs jenkins | grep -m 1 "Password"
Paste the password into the setup wizard.

Choose “Install suggested plugins”.

Create an admin user (recommended) and complete the wizard.
​

After setup, you should see the Jenkins dashboard and have access to Manage Jenkins (admin privileges).

Useful plugins
The following plugins should be installed (many come with the suggested set):

Git – for checking out repositories from Gitea/GitHub.

Pipeline – for Jenkinsfile-based pipelines.

(Later) Email / Slack plugins – for build notifications.
​

Verify and manage plugins under: Manage Jenkins → Plugins.

Connecting to the mr-jenk repository
Go to Manage Jenkins → Credentials.

Add a global credential for Gitea:

Type: “Username with password” or “Personal access token” (depending on how your Gitea is set up).

ID example: gitea-mr-jenk.
​

Create a Pipeline job:

New Item → Pipeline.

Configure it to use:

Pipeline script from SCM.

SCM: Git.

Repository URL: https://01.gritlab.ax/git/<user>/mr-jenk.git (adjust for your user).

Credentials: gitea-mr-jenk.

Branch: */main.
​

Once a Jenkinsfile exists in the repo, Jenkins will use it to define the full CI/CD pipeline.

Managing Jenkins
Common commands:

View logs:

bash
docker logs -f jenkins
Stop Jenkins:

bash
cd infra/jenkins
docker compose down
Restart Jenkins:

bash
cd infra/jenkins
docker compose restart
The jenkins_home volume keeps your configuration; if you ever want a completely fresh Jenkins, you can remove that volume (be aware this deletes all Jenkins jobs and settings):

bash
docker compose down
docker volume rm infra_jenkins_jenkins_home  # name depends on your compose project
Notes for teammates
This Jenkins is local only and is meant for experimentation and development; production CI will likely run on a shared server or other platform.
​

Pipelines should mirror what developers can run locally using the documented Maven and Angular commands in the repo’s main README.
​

If you change the Jenkins configuration (e.g., credentials, plugins, agents), document the change briefly so other teammates can reproduce or debug if needed.