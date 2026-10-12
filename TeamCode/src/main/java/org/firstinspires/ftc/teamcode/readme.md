## Welcome Techno Creators!
---

## NOTICE
This repository contains Techno creators team public FTC BIOBUZZ (2026-2027) competition code.

Team Number : 16355
Team Name : Techno Creators

Team Members:
....

# Git & GitHub Setup Guide

Welcome! This guide covers the basic steps to set up a GitHub account, install Git on your laptop, and verify your installation.
---

## 1. Create a GitHub Account

1. Go to [github.com/signup](https://github.com/signup).
2. Enter your email address, create a strong password, and choose a descent username (USE PROFESSIONAL USERNAME!!).
3. Complete the verification prompt and enter the 8-digit verification code sent to your email.
4. **Recommended Security Settings:**
    * Go to **Settings > Emails** and check **"Keep my email addresses private"**.
    * Enable Two-Factor Authentication (2FA) under **Settings > Password and authentication**.

---

## 2. Install Git on Your Laptop

1. Go to the official download page at [git-scm.com/downloads](https://git-scm.com/downloads).
2. Download the installer for your operating system (Windows, macOS, or Linux).
3. Run the installer and click **Next** through the setup wizard (default options are recommended for most users).

---

## 3. Verify Your Installation

Open your terminal (Command Prompt or PowerShell on Windows, or Terminal on macOS/Linux) and run the following command to check if Git is installed correctly:

```bash
git --version
```

## 4. Configure Git (First-Time Setup)
Set up your identity so your commits are attributed correctly by running these commands in your terminal:

```
git config --global user.name "Your Name"
git config --global user.email "your.email@example.com"
```

# Install Android Studio on Windows to run the code

This guide covers downloading, installing, and setting up the latest version of Android Studio on a Windows laptop.

---

## 1. Check System Requirements

Before downloading, ensure your Windows laptop meets the minimum requirements:
* **Operating System:** 64-bit Windows 10 or 11.
* **RAM:** Minimum 8 GB RAM (16 GB RAM recommended).
* **Disk Space:** At least 8 GB of available disk space (for IDE, SDK, and emulator caches).
* **Screen Resolution:** 1280 x 800 minimum screen resolution.

---

## 2. Download the Installer

1. Open your web browser and go to the official [Android Studio Download Archive](https://developer.android.com/studio/archive).
   Install "Android Studio Quail 4 | 2026.1.4 Patch 1 September 18, 2026"
2. Click the **Download Android Studio** button.
3. Read the terms and conditions, check the box to agree to the License Agreement, and click download.
4. Save the `.exe` installer file to your **Downloads** folder.

---

## 3. Run the Installation Wizard

1. Open your **Downloads** folder and double-click the downloaded Android Studio installation file (e.g., `android-studio-<version>-windows.exe`).
2. If a Windows User Account Control prompt appears asking to allow the app to make changes to your device, click **Yes**.
3. Click **Next** on the Welcome screen.
4. Select the components to install (leave **Android Studio** and **Android Virtual Device** checked) and click **Next**.
5. Choose your installation location (default path is recommended) and click **Next**, then click **Install**.
6. Once the installation completes, click **Next** and then **Finish** to launch Android Studio.

---

## 4. Complete the Setup Wizard

1. When Android Studio launches for the first time, choose whether you want to import previous settings (select *Do not import settings* if this is a fresh install).
2. Select your preferred UI theme (Dark or Light).
3. In the **SDK Components Setup** screen, leave the default recommended components checked. This will download the necessary Android SDK, build tools, and platform tools.
4. Accept the license agreements for the components, and wait for the download and installation to finish.
5. Click **Finish** when the setup wizard is complete.

---

## 5. Verify the Installation

To verify that Android Studio is working correctly:
1. From the welcome screen, click **New Project**.
2. Select **Empty Views Activity** (or basic template) and click **Next**.
3. Name your project, select **Java** or **Kotlin**, and click **Finish**.
4. **Verification:** Wait for Gradle to finish syncing. If the project loads successfully without critical build errors in the bottom build window, Android Studio is installed and fully functional.

# Git Fork and Sync Guide

This guide covers how to fork a repository on GitHub, clone it to your local machine, link it to the original project, and keep it updated with the latest changes.

---

## 1. Fork and Clone a Repository

### Step A: Fork the Repository
1. Navigate to the original repository in your web browser https://github.com/vijaisarath/Ftc2026BioBuzz.
2. Click the **Fork** button in the top-right corner.
3. Select your personal account or organization as the destination and click **Create fork**.

### Step B: Clone to Your Laptop
1. On your forked repository page, click the green **Code** button and copy the HTTPS URL.
2. Open your terminal and run the clone command:
   ```bash
   git clone https://github.com/YOUR-USERNAME/repository-name.git
   ```
3. Move into the project directory:
   ```bash
   cd repository-name
   ```

---

## 2. Set Up the Upstream Remote (One-Time Setup)

To pull updates from the original repository, link it as your `upstream` remote:

```bash
git remote add upstream https://github.com/ORIGINAL-OWNER/original-repository.git
```

Verify your remotes are set up correctly:
```bash
git remote -v
```
*(You should see `origin` pointing to your fork and `upstream` pointing to the original repo).*

---

## 3. Update Your Forked Repository

Whenever the original repository receives updates, follow these steps to pull them into your local copy and push them to your GitHub fork:

1. **Fetch the latest changes from the original repository:**
   ```bash
   git fetch upstream
   ```

2. **Switch to your local main branch:**
   ```bash
   git checkout main
   ```

3. **Merge the upstream changes into your local branch:**
   ```bash
   git merge upstream/main
   ```

4. **Push the updates up to your GitHub fork (`origin`):**
   ```bash
   git push origin main
   ```

## Creating your own OpModes

The easiest way to create your own OpMode is to copy a Sample OpMode and make it your own.

Sample opmodes exist in the FtcRobotController module.
To locate these samples, find the FtcRobotController module in the "Project/Android" tab.

Expand the following tree elements:
 FtcRobotController/java/org.firstinspires.ftc.robotcontroller/external/samples

### Naming of Samples

To gain a better understanding of how the samples are organized, and how to interpret the
naming system, it will help to understand the conventions that were used during their creation.

These conventions are described (in detail) in the sample_conventions.md file in this folder.

To summarize: A range of different samples classes will reside in the java/external/samples.
The class names will follow a naming convention which indicates the purpose of each class.
The prefix of the name will be one of the following:

Basic:  	This is a minimally functional OpMode used to illustrate the skeleton/structure
            of a particular style of OpMode.  These are bare bones examples.

Sensor:    	This is a Sample OpMode that shows how to use a specific sensor.
            It is not intended to drive a functioning robot, it is simply showing the minimal code
            required to read and display the sensor values.

Robot:	    This is a Sample OpMode that assumes a simple two-motor (differential) drive base.
            It may be used to provide a common baseline driving OpMode, or
            to demonstrate how a particular sensor or concept can be used to navigate.

Concept:	This is a sample OpMode that illustrates performing a specific function or concept.
            These may be complex, but their operation should be explained clearly in the comments,
            or the comments should reference an external doc, guide or tutorial.
            Each OpMode should try to only demonstrate a single concept so they are easy to
            locate based on their name.  These OpModes may not produce a drivable robot.

After the prefix, other conventions will apply:

* Sensor class names are constructed as:    Sensor - Company - Type
* Robot class names are constructed as:     Robot - Mode - Action - OpModetype
* Concept class names are constructed as:   Concept - Topic - OpModetype

Once you are familiar with the range of samples available, you can choose one to be the
basis for your own robot.  In all cases, the desired sample(s) needs to be copied into
your TeamCode module to be used.

This is done inside Android Studio directly, using the following steps:

 1) Locate the desired sample class in the Project/Android tree.

 2) Right click on the sample class and select "Copy"

 3) Expand the  TeamCode/java folder

 4) Right click on the org.firstinspires.ftc.teamcode folder and select "Paste"

 5) You will be prompted for a class name for the copy.
    Choose something meaningful based on the purpose of this class.
    Start with a capital letter, and remember that there may be more similar classes later.

Once your copy has been created, you should prepare it for use on your robot.
This is done by adjusting the OpMode's name, and enabling it to be displayed on the
Driver Station's OpMode list.

Each OpMode sample class begins with several lines of code like the ones shown below:

```
 @TeleOp(name="Template: Linear OpMode", group="Linear Opmode")
 @Disabled
 
```

The name that will appear on the driver station's "opmode list" is defined by the code:
 ``name="Template: Linear OpMode"``
You can change what appears between the quotes to better describe your opmode.
The "group=" portion of the code can be used to help organize your list of OpModes.

As shown, the current OpMode will NOT appear on the driver station's OpMode list because of the
  ``@Disabled`` annotation which has been included.
This line can simply be deleted , or commented out, to make the OpMode visible.
