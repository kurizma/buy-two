# AWS Jenkins + SonarQube Setup Guide for Buy-Two Project

**Time Required:** 2-3 hours  
**Cost:** ~$32 for 14 days (terminate after project submission)  
**Difficulty:** Beginner-friendly

---

## Prerequisites Checklist

- [ ] AWS Account (free tier eligible or credit card for billing)
- [ ] GitHub access to kurizma/buy-two repository
- [ ] MongoDB Atlas URI (team shared)
- [ ] Cloudflare R2 credentials (team shared)
- [ ] Terminal/SSH client on your computer

---

## Part 1: Launch EC2 Instance (15 minutes)

### Step 1: Log into AWS Console

1. Go to https://console.aws.amazon.com/
2. Sign in with your AWS account
3. Select region: **us-east-1** (N. Virginia) or closest to your team

### Step 2: Launch EC2 Instance

1. Navigate to **EC2 Dashboard**
2. Click orange **"Launch Instance"** button

### Step 3: Configure Instance

**Name and Tags:**

```
Name: buy-two-jenkins
```

**Application and OS Images (AMI):**

```
AMI: Ubuntu Server 22.04 LTS (HVM), SSD Volume Type
Architecture: 64-bit (x86)
```

**Instance Type:**

```
Recommended: t3.large (2 vCPU, 8GB RAM) - ~$0.08/hour
Budget option: t3.medium (2 vCPU, 4GB RAM) - ~$0.04/hour
```

**Key Pair (login):**

```
1. Click "Create new key pair"
2. Key pair name: buy-two-jenkins
3. Key pair type: RSA
4. Private key file format: .pem
5. Click "Create key pair"
6. ⚠️ SAVE THE .pem FILE - You can't download it again!
7. Move it to safe location: ~/Documents/aws-keys/buy-two-jenkins.pem
```

**Network Settings:**

```
Click "Edit" then configure:

✅ Auto-assign public IP: Enable

Security Group Name: buy-two-jenkins-sg
Description: Security group for Jenkins and SonarQube

Inbound Security Group Rules:
Rule 1: SSH
  - Type: SSH
  - Protocol: TCP
  - Port: 22
  - Source: My IP (or 0.0.0.0/0 if you'll access from multiple locations)

Rule 2: Jenkins
  - Type: Custom TCP
  - Protocol: TCP
  - Port: 9090
  - Source: 0.0.0.0/0 (Anywhere IPv4)

Rule 3: SonarQube
  - Type: Custom TCP
  - Protocol: TCP
  - Port: 9000
  - Source: 0.0.0.0/0 (Anywhere IPv4)

Rule 4: HTTP (Optional - for future use)
  - Type: HTTP
  - Protocol: TCP
  - Port: 80
  - Source: 0.0.0.0/0

Rule 5: API Gateway (Optional - if deploying full app)
  - Type: Custom TCP
  - Protocol: TCP
  - Port: 8080
  - Source: 0.0.0.0/0
```

**Configure Storage:**

```
Volume 1 (Root): 30 GiB gp3
Delete on termination: ✅ Checked
```

**Advanced Details:**

```
Leave defaults (can skip this section)
```

### Step 4: Launch

1. Review your settings in the Summary panel
2. Click **"Launch instance"**
3. Wait 1-2 minutes for instance to start
4. Click **"View all instances"**
5. Wait until **Instance State = Running** and **Status checks = 2/2 checks passed**
6. **Note down your Public IPv4 address** (e.g., 54.123.45.67)

---

## Part 2: Connect to EC2 Instance (5 minutes)

### Step 1: Set Key Permissions (Mac/Linux)

```bash
# Open terminal
cd ~/Documents/aws-keys/  # or wherever you saved the key
chmod 400 buy-two-jenkins.pem
```

### Step 2: Connect via SSH

```bash
# Replace YOUR_EC2_PUBLIC_IP with the IP from AWS Console
ssh -i buy-two-jenkins.pem ubuntu@YOUR_EC2_PUBLIC_IP

# Example:
# ssh -i buy-two-jenkins.pem ubuntu@54.123.45.67

# Type "yes" when asked "Are you sure you want to continue connecting?"
```

**Windows Users (PowerShell):**

```powershell
ssh -i C:\path\to\buy-two-jenkins.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

You should now see:

```
ubuntu@ip-172-31-xx-xx:~$
```

---

## Part 3: Install Docker and Dependencies (30 minutes)

### Step 1: Create Setup Script

Copy and paste this entire block into your SSH terminal:

```bash
cat > setup.sh << 'SCRIPT_END'
#!/bin/bash
set -e

echo "=================================================="
echo "🚀 Buy-Two CI/CD Setup on AWS EC2"
echo "=================================================="
echo ""

# Update system
echo "📦 Step 1/5: Updating system packages..."
sudo apt update
sudo DEBIAN_FRONTEND=noninteractive apt upgrade -y

# Install Docker
echo ""
echo "🐳 Step 2/5: Installing Docker..."
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu
rm get-docker.sh

# Install Docker Compose and Git
echo ""
echo "📦 Step 3/5: Installing Docker Compose and Git..."
sudo apt install -y docker-compose-plugin git

# Generate SSH key for GitHub
echo ""
echo "🔑 Step 4/5: Generating SSH key for GitHub..."
if [ ! -f ~/.ssh/id_ed25519 ]; then
    ssh-keygen -t ed25519 -C "jenkins@aws-buy-two" -f ~/.ssh/id_ed25519 -N ""
    echo "✅ SSH key generated"
else
    echo "⚠️  SSH key already exists"
fi

# Configure Git
git config --global user.name "Buy-Two Jenkins"
git config --global user.email "jenkins@buy-two.local"

echo ""
echo "=================================================="
echo "✅ Installation Complete!"
echo "=================================================="
echo ""
echo "📋 IMPORTANT: Add this SSH key to GitHub"
echo "=================================================="
cat ~/.ssh/id_ed25519.pub
echo "=================================================="
echo ""
echo "🔗 Add this key at:"
echo "   https://github.com/kurizma/buy-two/settings/keys"
echo ""
echo "   1. Click 'Add deploy key'"
echo "   2. Title: AWS Jenkins Server"
echo "   3. Key: [paste the key above]"
echo "   4. ✅ Allow write access"
echo "   5. Click 'Add key'"
echo ""
echo "⚠️  After adding the key to GitHub:"
echo "   1. Type: exit"
echo "   2. SSH back in to activate Docker group"
echo ""
SCRIPT_END

chmod +x setup.sh
```

### Step 2: Run Setup Script

```bash
./setup.sh
```

This will take 5-10 minutes. Watch for any errors.

### Step 3: Add SSH Key to GitHub

1. Copy the SSH public key shown in the output
2. Open in browser: https://github.com/kurizma/buy-two/settings/keys
3. Click **"Add deploy key"**
4. Title: `AWS Jenkins Server`
5. Key: Paste the key you copied
6. ✅ **Check "Allow write access"**
7. Click **"Add key"**

### Step 4: Logout and Login Again

```bash
exit
```

Then SSH back in:

```bash
ssh -i buy-two-jenkins.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

Verify Docker works:

```bash
docker --version
docker compose version
```

---

## Part 4: Clone Repository and Configure (15 minutes)

### Step 1: Clone Repository

```bash
# Test SSH connection to GitHub first
ssh -T git@github.com
# Should say: "Hi kurizma/buy-two! You've successfully authenticated..."

# Clone the repo
git clone git@github.com:kurizma/buy-two.git
cd buy-two

# Verify you're in the right place
pwd
# Should show: /home/ubuntu/buy-two
```

### Step 2: Create Environment File

```bash
nano .env
```

Paste this content and **REPLACE with your team's real credentials**:

```bash
# MongoDB Atlas (get from team)
ATLAS_URI=mongodb+srv://username:password@cluster0.xxxxx.mongodb.net/?retryWrites=true&w=majority

# JWT Security (team shared secret)
SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_SECRET=buy-two-super-secret-jwt-key-2026-change-this
KEY_STORE_PASSWORD=buy-two-keystore-password-2026

# Database names
USER_DB=userdb
PRODUCT_DB=productdb
MEDIA_DB=mediadb

# Cloudflare R2 (get from team)
R2_ENDPOINT=https://xxxxxxxxxxxxx.r2.cloudflarestorage.com
R2_ACCESS_KEY=your-r2-access-key-here
R2_SECRET_KEY=your-r2-secret-key-here

# Image tagging
IMAGE_TAG=latest
```

**Save and exit:**

- Press `Ctrl + O` (save)
- Press `Enter` (confirm)
- Press `Ctrl + X` (exit)

### Step 3: Verify .env File

```bash
cat .env
# Make sure all values are filled in (no "your-xxx-here" placeholders)
```

---

## Part 5: Start Services (45 minutes total wait time)

### Step 1: Start SonarQube

```bash
cd ~/buy-two

# Start SonarQube and PostgreSQL
docker compose -f sonarqube-compose.yml up -d

# Check status
docker ps
```

You should see 2 containers running:

- `sonarqube`
- `sonarqube-db` (PostgreSQL)

### Step 2: Wait for SonarQube to Initialize

```bash
# Wait 90 seconds
echo "⏳ Waiting for SonarQube to start (90 seconds)..."
sleep 90

# Check logs to confirm it's ready
docker logs sonarqube --tail 50
# Look for: "SonarQube is operational"
```

If not ready yet, wait another 30 seconds and check again.

### Step 3: Start Jenkins

```bash
cd ~/buy-two/infra/jenkins

# Start Jenkins
docker compose up -d

# Go back to project root
cd ~/buy-two

# Check all containers
docker ps
```

You should now see 3 containers running:

- `jenkins`
- `sonarqube`
- `sonarqube-db`

### Step 4: Wait for Jenkins to Initialize

```bash
echo "⏳ Waiting for Jenkins to start (60 seconds)..."
sleep 60

# Check Jenkins logs
docker logs jenkins --tail 100
```

### Step 5: Get Jenkins Initial Admin Password

```bash
docker logs jenkins 2>&1 | grep -A 2 "Please use the following password"
```

Copy the password (long alphanumeric string). You'll need it in the next step.

Example output:

```
*************************************************************

Jenkins initial setup is required. An admin user has been created and a password generated.
Please use the following password to proceed to installation:

a1b2c3d4e5f6g7h8i9j0k1l2m3n4o5p6

*************************************************************
```

---

## Part 6: Configure Jenkins (30 minutes)

### Step 1: Access Jenkins

1. Open browser
2. Go to: `http://YOUR_EC2_PUBLIC_IP:9090`
3. You should see "Unlock Jenkins" page

Example: `http://54.123.45.67:9090`

### Step 2: Initial Setup Wizard

**Unlock Jenkins:**

```
1. Paste the admin password from previous step
2. Click "Continue"
```

**Customize Jenkins:**

```
1. Click "Install suggested plugins"
2. Wait 5-10 minutes for plugins to install
3. ☕ Take a coffee break
```

**Create First Admin User:**

```
Username: admin
Password: [choose a strong password - save it!]
Confirm password: [same password]
Full name: Buy-Two Admin
Email: your-email@example.com

Click "Save and Continue"
```

**Instance Configuration:**

```
Jenkins URL: http://YOUR_EC2_PUBLIC_IP:9090/
Click "Save and Finish"
Click "Start using Jenkins"
```

### Step 3: Install Additional Plugins

1. Click **"Manage Jenkins"** (left sidebar)
2. Click **"Plugins"**
3. Click **"Available plugins"** tab
4. Search and install these (check the boxes):
   - `SonarQube Scanner` (if not already installed)
   - `Docker Pipeline` (if not already installed)
5. Click **"Install"** button
6. ✅ Check **"Restart Jenkins when installation is complete"**
7. Wait 2-3 minutes for restart

### Step 4: Access SonarQube

1. Open new browser tab
2. Go to: `http://YOUR_EC2_PUBLIC_IP:9000`
3. Login:
   - Username: `admin`
   - Password: `admin`
4. You'll be prompted to change password
5. New password: [choose and save it]
6. Skip tutorial if prompted

### Step 5: Generate SonarQube Token

In SonarQube:

1. Click on **"A"** icon (top right) → **My Account**
2. Click **"Security"** tab
3. Under **"Generate Tokens"**:
   - Name: `jenkins-token`
   - Type: `User Token`
   - Expires in: `90 days`
4. Click **"Generate"**
5. **⚠️ COPY THE TOKEN** (you can't see it again!)
   - Example: `squ_1a2b3c4d5e6f7g8h9i0j1k2l3m4n5o6p7q8r9s0`

---

## Part 7: Configure Jenkins Credentials (20 minutes)

### Step 1: Add GitHub SSH Credentials

In Jenkins dashboard:

1. Click **"Manage Jenkins"** → **"Credentials"**
2. Click **"System"** → **"Global credentials (unrestricted)"**
3. Click **"Add Credentials"** (left sidebar)

**Configure:**

```
Kind: SSH Username with private key
Scope: Global
ID: safe-zone
Description: GitHub SSH Key for safe-zone/buy-two
Username: git
Private Key: ✅ Enter directly
```

4. Click **"Add"** button under Private Key
5. Go back to your SSH terminal and run:

```bash
cat ~/.ssh/id_ed25519
```

6. Copy the ENTIRE output (including `-----BEGIN` and `-----END` lines)
7. Paste into Jenkins "Key" text box
8. Click **"Create"**

### Step 2: Add SonarQube Token

1. Click **"Add Credentials"** again
2. Configure:

```
Kind: Secret text
Scope: Global
Secret: [paste the SonarQube token from earlier]
ID: sonarqube-token
Description: SonarQube Authentication Token
```

3. Click **"Create"**

### Step 3: Add Slack Webhook (Optional)

If you have a Slack workspace:

1. Click **"Add Credentials"** again
2. Configure:

```
Kind: Secret text
Scope: Global
Secret: [your Slack webhook URL]
ID: slack-webhook
Description: Slack Webhook for Build Notifications
```

3. Click **"Create"**

---

## Part 8: Configure SonarQube in Jenkins (10 minutes)

### Step 1: Configure SonarQube Server

1. Go to **"Manage Jenkins"** → **"System"**
2. Scroll down to **"SonarQube servers"** section
3. Click **"Add SonarQube"**

**Configure:**

```
Name: sonarqube
Server URL: http://sonarqube:9000
Server authentication token: Select "sonarqube-token" from dropdown
```

4. Scroll to bottom and click **"Save"**

### Step 2: Configure SonarQube Scanner

1. Go to **"Manage Jenkins"** → **"Tools"**
2. Scroll to **"SonarQube Scanner installations"**
3. Click **"Add SonarQube Scanner"**

**Configure:**

```
Name: SonarQube Scanner
✅ Install automatically
Version: [select latest version from dropdown]
```

4. Click **"Save"**

---

## Part 9: Create Pipeline Job (15 minutes)

### Step 1: Create New Pipeline

1. From Jenkins Dashboard, click **"New Item"** (left sidebar)
2. Enter name: `buy-two-pipeline`
3. Select **"Pipeline"**
4. Click **"OK"**

### Step 2: Configure Pipeline

**General Section:**

```
✅ GitHub project
Project url: https://github.com/kurizma/buy-two/
```

**Build Triggers:**

```
✅ GitHub hook trigger for GITScm polling
```

**Pipeline Section:**

```
Definition: Pipeline script from SCM

SCM: Git

Repository URL: git@github.com:kurizma/buy-two.git

Credentials: Select "safe-zone" from dropdown

Branches to build:
Branch Specifier: */main

Script Path: Jenkinsfile

✅ Lightweight checkout
```

### Step 3: Save and Build

1. Click **"Save"**
2. Click **"Build Now"** (left sidebar)
3. Watch the build progress in **"Build History"**
4. Click on **#1** build number to see details
5. Click **"Console Output"** to see full log

**First build will take 10-15 minutes** because it needs to:

- Download Maven dependencies
- Download Node.js packages
- Build all 5 backend services
- Build frontend
- Run tests
- Run SonarQube analysis

---

## Part 10: Verify Everything Works (10 minutes)

### Step 1: Check Build Status

Build should go through these stages:

- ✅ Checkout
- ✅ Backend Build (5 services)
- ✅ Backend Tests (5 services)
- ✅ Frontend Build & Test
- ✅ SonarQube Analysis
- ✅ Build Images
- ✅ Deploy & Verify
- ✅ Post Actions

### Step 2: Check SonarQube Results

1. Go to SonarQube: `http://YOUR_EC2_PUBLIC_IP:9000`
2. You should see projects for each service:
   - buy-two-discovery-service
   - buy-two-gateway-service
   - buy-two-user-service
   - buy-two-product-service
   - buy-two-media-service
3. Click on each to see code quality metrics

### Step 3: Test Auto-Trigger (Optional)

**Set up GitHub Webhook:**

1. Go to: https://github.com/kurizma/buy-two/settings/hooks
2. Click **"Add webhook"**
3. Configure:
   ```
   Payload URL: http://YOUR_EC2_PUBLIC_IP:9090/github-webhook/
   Content type: application/json
   Secret: [leave blank]
   Which events: Just the push event
   ✅ Active
   ```
4. Click **"Add webhook"**

Now every push to the repository will automatically trigger a build!

---

## Part 11: Share with Team (5 minutes)

### Create Team Access Document

Send this to your team via Slack/Email:

```
🎯 Buy-Two CI/CD Server - Team Access

Jenkins Dashboard:
URL: http://YOUR_EC2_PUBLIC_IP:9090
Username: admin
Password: [the password you set]

SonarQube:
URL: http://YOUR_EC2_PUBLIC_IP:9000
Username: admin
Password: [the password you set]

How to trigger builds:
1. Push your feature branch to GitHub
2. Jenkins will auto-build (if webhook configured)
   OR manually click "Build Now" in Jenkins

Development Workflow:
1. Clone repo: git clone https://github.com/kurizma/buy-two.git
2. Create feature branch: git checkout -b feature/my-feature
3. Make changes and commit
4. Push: git push origin feature/my-feature
5. Check Jenkins build status
6. Fix any issues reported by SonarQube
7. Create PR when build is green
```

---

## Maintenance Commands

### SSH into Server

```bash
ssh -i ~/Documents/aws-keys/buy-two-jenkins.pem ubuntu@YOUR_EC2_PUBLIC_IP
```

### Check Service Status

```bash
cd ~/buy-two
docker ps
```

### View Jenkins Logs

```bash
docker logs jenkins -f
# Press Ctrl+C to exit
```

### View SonarQube Logs

```bash
docker logs sonarqube -f
# Press Ctrl+C to exit
```

### Restart Jenkins

```bash
cd ~/buy-two/infra/jenkins
docker compose restart
```

### Restart SonarQube

```bash
cd ~/buy-two
docker compose -f sonarqube-compose.yml restart
```

### Stop All Services (saves money)

```bash
cd ~/buy-two
docker compose -f infra/jenkins/docker-compose.yml stop
docker compose -f sonarqube-compose.yml stop
```

### Start All Services

```bash
cd ~/buy-two
docker compose -f sonarqube-compose.yml start
docker compose -f infra/jenkins/docker-compose.yml start
```

### Update Repository

```bash
cd ~/buy-two
git pull origin main
```

### Check Disk Space

```bash
df -h
# If low, clean up Docker
docker system prune -a
```

---

## Troubleshooting

### Problem: Can't access Jenkins/SonarQube from browser

**Solution:**

1. Check Security Group in AWS Console
2. EC2 → Security Groups → buy-two-jenkins-sg
3. Verify inbound rules for ports 9090 and 9000
4. Source should be `0.0.0.0/0`

### Problem: Jenkins shows "Permission denied" for Docker

**Solution:**

```bash
# Logout and login again
exit
ssh -i buy-two-jenkins.pem ubuntu@YOUR_EC2_IP
```

### Problem: SonarQube not starting

**Solution:**

```bash
# Check if enough memory
free -h
# If using t3.medium and memory is low, upgrade to t3.large

# Check logs
docker logs sonarqube --tail 100

# Restart
cd ~/buy-two
docker compose -f sonarqube-compose.yml restart
```

### Problem: Build fails with "Out of memory"

**Solution:**

```bash
# In AWS Console, stop instance
# Change instance type to t3.large
# Start instance again
```

### Problem: Git clone fails with "Permission denied"

**Solution:**

```bash
# Test GitHub SSH connection
ssh -T git@github.com

# If fails, regenerate key and add to GitHub
ssh-keygen -t ed25519 -C "jenkins@aws" -f ~/.ssh/id_ed25519
cat ~/.ssh/id_ed25519.pub
# Add to: https://github.com/kurizma/buy-two/settings/keys
```

---

## Cost Management

### Check Current Costs

1. Go to AWS Console
2. Click on your account name (top right)
3. Click "Billing Dashboard"
4. View current month charges

### Set Up Billing Alert

1. Billing Dashboard → Budgets
2. Create budget
3. Set threshold: $50
4. Add your email for alerts

### Stop Instance When Not Needed

```bash
# In AWS Console:
EC2 → Instances → Select buy-two-jenkins
Actions → Instance State → Stop
```

Stopped instances don't cost compute charges (only storage ~$3/month)

### Terminate After Project (Day 15)

```bash
# ⚠️ This deletes everything!
# In AWS Console:
EC2 → Instances → Select buy-two-jenkins
Actions → Instance State → Terminate
```

---

## Success Checklist

- [ ] EC2 instance running
- [ ] Can SSH into instance
- [ ] Docker and Docker Compose installed
- [ ] Repository cloned
- [ ] .env file configured with team credentials
- [ ] SonarQube accessible at :9000
- [ ] Jenkins accessible at :9090
- [ ] Jenkins plugins installed
- [ ] GitHub SSH credentials added to Jenkins
- [ ] SonarQube token added to Jenkins
- [ ] SonarQube server configured in Jenkins
- [ ] Pipeline job created
- [ ] First build successful (green)
- [ ] SonarQube shows analysis results
- [ ] Team has access credentials
- [ ] GitHub webhook configured (optional)

---

## Quick Reference

**Your EC2 Public IP:** `_____________` (fill this in)

**SSH Command:**

```bash
ssh -i ~/Documents/aws-keys/buy-two-jenkins.pem ubuntu@YOUR_EC2_IP
```

**Jenkins URL:** `http://YOUR_EC2_IP:9090`  
**SonarQube URL:** `http://YOUR_EC2_IP:9000`

**Project Directory:** `/home/ubuntu/buy-two`

**Jenkins Admin:** `admin` / `[your password]`  
**SonarQube Admin:** `admin` / `[your password]`

---

## After Project Submission

**Day 15 - Clean Up:**

1. Backup any important data/screenshots
2. In AWS Console:
   - EC2 → Instances
   - Select buy-two-jenkins
   - Actions → Instance State → **Terminate**
3. Verify no other resources are running:
   - Check EBS volumes (should auto-delete)
   - Check Elastic IPs (release if any)
4. Final billing check in 2-3 days

**Total Project Cost Estimate:** ~$32-60 for 14 days

---

## Need Help?

**Common issues and solutions:** See Troubleshooting section above

**AWS Support:** https://console.aws.amazon.com/support/home

**Team Slack/Discord:** Share this guide and your EC2 IP with the team

---

**Setup Complete! 🎉**

Your Jenkins and SonarQube servers are now running 24/7 in the cloud, accessible by your entire team. Focus on building features - the CI/CD infrastructure is ready!
