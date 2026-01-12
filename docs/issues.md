# Known Issues and Solutions

This document lists known issues with the Master Vastu application and their solutions.

## Table of Contents

- [Build and Setup Issues](#build-and-setup-issues)
- [Runtime Issues](#runtime-issues)
- [UI/UX Issues](#uiux-issues)
- [Performance Issues](#performance-issues)
- [Platform-Specific Issues](#platform-specific-issues)
- [Reporting New Issues](#reporting-new-issues)

## Build and Setup Issues

### Maven Wrapper Properties File Missing

**Error Message:**
```
Exception in thread "main" java.lang.RuntimeException: Wrapper properties file '/path/to/project/.mvn/wrapper/maven-wrapper.properties' does not exist.
```

**Symptoms:**
- `./mvnw` commands fail with the above error
- Maven wrapper cannot download Maven distribution

**Solution:**
The `maven-wrapper.properties` file is missing from the `.mvn/wrapper/` directory.

1. **Create the missing properties file:**
   ```bash
   mkdir -p .mvn/wrapper
   cat > .mvn/wrapper/maven-wrapper.properties << 'EOF'
   # Licensed to the Apache Software Foundation (ASF) under one
   # or more contributor license agreements.  See the NOTICE file
   # distributed with this work for additional information
   # regarding copyright ownership.  The ASF licenses this file
   # to you under the Apache License, Version 2.0 (the
   # "License"); you may not use this file except in compliance
   # with the License.  You may obtain a copy of the License at
   #
   #   https://www.apache.org/licenses/LICENSE-2.0
   #
   # Unless required by applicable law or agreed to in writing,
   # software distributed under the License is distributed on an
   # "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
   # KIND, either express or implied.  See the License for the
   # specific language governing permissions and limitations
   # under the License.
   distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip
   wrapperUrl=https://repo.maven.apache.org/maven2/org/apache/maven/wrapper/maven-wrapper/3.2.0/maven-wrapper-3.2.0.jar
   EOF
   ```

2. **Verify the fix:**
   ```bash
   ./mvnw -version
   ```

**Status:** Fixed in repository - this file has been added.

### JAVA_HOME Not Set Correctly

**Error Message:**
```
Error: JAVA_HOME is not defined correctly.
  We cannot execute /path/to/java/bin/java
```

**Symptoms:**
- Maven wrapper starts but fails with JAVA_HOME error
- Java commands work but Maven can't find Java

**Solution:**
Set JAVA_HOME environment variable to point to your Java 17 installation.

**On Ubuntu/Debian:**
```bash
# Find Java installation path
update-java-alternatives --list

# Set JAVA_HOME (replace with your actual path)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64' >> ~/.bashrc
source ~/.bashrc
```

**On Fedora:**
```bash
# Find Java installation path
alternatives --list | grep java

# Set JAVA_HOME (replace with your actual path)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
echo 'export JAVA_HOME=/usr/lib/jvm/java-17-openjdk' >> ~/.bashrc
source ~/.bashrc
```

**Manual Setup:**
```bash
# Find where Java is installed
which java
ls -la /usr/bin/java  # Follow the symlink

# Set JAVA_HOME to the directory containing bin/java
export JAVA_HOME=/path/to/java/home
export PATH=$JAVA_HOME/bin:$PATH
```

**Verify:**
```bash
echo $JAVA_HOME
java -version
./mvnw -version
```

### JavaFX Modules Not Found

**Error Message:**
```
Error: JavaFX runtime components are missing, and are required to run this application
```

**Symptoms:**
- Application fails to start
- JavaFX-related exceptions

**Solution:**
Ensure JavaFX is properly installed and configured.

**On Ubuntu/Debian:**
```bash
sudo apt install -y openjfx
```

**On Fedora:**
```bash
sudo dnf install -y openjfx
```

**Alternative:** Use the Maven wrapper which includes JavaFX dependencies.

### Maven Build Fails with Java Version Error

**Error Message:**
```
[ERROR] Failed to execute goal org.apache.maven.plugins:maven-compiler-plugin:3.11.0:compile (default-compile) @ VastuApplication: Compilation failure
Required Java version is 17, but found 11 (or 8)
```

**Solution:**
1. **Check Java version:**
   ```bash
   java -version
   ```

2. **Install Java 17** (see [setup.md](setup.md) for detailed instructions)

3. **Set JAVA_HOME:**
   ```bash
   export JAVA_HOME=/path/to/java17
   export PATH=$JAVA_HOME/bin:$PATH
   ```

4. **Clean and rebuild:**
   ```bash
   ./mvnw clean compile
   ```

## Runtime Issues

### Application Won't Start - Display Issues

**Symptoms:**
- Application starts but window doesn't appear
- Errors about display or graphics

**Solutions:**

1. **Check display environment:**
   ```bash
   echo $DISPLAY  # Should show :0 or similar
   ```

2. **For headless servers:**
   ```bash
   export DISPLAY=:0
   ```

3. **For SSH sessions:**
   Use X11 forwarding or run locally.

4. **Multi-monitor issues:**
   The application automatically detects and positions on the secondary monitor if available.

### OutOfMemoryError During PDF Processing

**Error Message:**
```
java.lang.OutOfMemoryError: Java heap space
```

**Symptoms:**
- Application crashes when processing large PDF files
- Freezes during image import

**Solution:**
1. **Increase heap size:**
   ```bash
   export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"
   ./mvnw clean javafx:run
   ```

2. **For permanent fix, add to your shell profile:**
   ```bash
   echo 'export MAVEN_OPTS="-Xmx2048m -XX:MaxPermSize=512m"' >> ~/.bashrc
   source ~/.bashrc
   ```

3. **Process smaller files:**
   - Split large PDFs before importing
   - Use smaller resolution images

### File Import Fails Silently

**Symptoms:**
- File chooser opens but no error when importing fails
- Image doesn't appear in canvas

**Common Causes and Solutions:**

1. **Unsupported file format:**
   - Supported: PNG, JPG, JPEG, PDF
   - Check file extension and convert if needed

2. **Corrupted file:**
   - Try opening the file with another application
   - Re-save or re-export the file

3. **Permissions issue:**
   ```bash
   ls -la /path/to/file
   chmod 644 /path/to/file
   ```

4. **Memory constraints:**
   - Large files may need more heap space (see above)

## UI/UX Issues

### Overlay Images Not Loading

**Symptoms:**
- Dropdowns populate but overlays don't appear
- Error messages about missing resources

**Solution:**
1. **Check resource structure:**
   ```bash
   ls -la src/main/resources/Directory/
   ```

2. **Verify overlay path construction:**
   - Check console output for path errors
   - Ensure directory structure matches code expectations

3. **Rebuild resources:**
   ```bash
   ./mvnw clean resources:resources
   ```

### Compass Not Updating

**Symptoms:**
- Compass remains static when rotating overlays
- Direction indicators don't match selected orientation

**Solution:**
1. **Ensure direction is selected first:**
   - Select shape, then direction, then overlay
   - Compass updates based on direction selection

2. **Check for JavaScript errors in logs**

### Point Selection Not Working

**Symptoms:**
- Clicking on canvas doesn't create points
- Point selection mode doesn't activate

**Solution:**
1. **Activate point selection mode:**
   - Click "Point Selection" button
   - Cursor should change to hand icon

2. **Import image first:**
   - Point selection requires an imported image
   - Check that image is visible in canvas

3. **Check for modal dialogs:**
   - Close any open dialogs before selecting points

## Performance Issues

### Slow Application Startup

**Symptoms:**
- Long startup time (>30 seconds)
- High CPU usage during initialization

**Optimizations:**
1. **Use Maven wrapper instead of system Maven**
2. **Pre-download dependencies:**
   ```bash
   ./mvnw dependency:go-offline
   ```

3. **Increase system resources:**
   - Add more RAM if possible
   - Close other memory-intensive applications

### Lag During Large Image Processing

**Symptoms:**
- UI freezes when importing large images
- Slow response during zoom/rotation operations

**Solutions:**
1. **Process asynchronously:**
   - The application uses background threads for image processing
   - Wait for completion before proceeding

2. **Optimize image size:**
   - Resize large images before importing
   - Use appropriate resolution for your needs

3. **Monitor memory usage:**
   ```bash
   # During runtime, check memory
   jps -l | grep VastuApplication
   jmap -heap <pid>
   ```

## Platform-Specific Issues

### Fedora-Specific Issues

#### DNF Package Conflicts
```bash
# If package conflicts occur during Java installation
sudo dnf install -y java-17-openjdk-devel --allowerasing
```

#### SELinux Issues
```bash
# If file access is denied
sudo setenforce 0  # Temporary fix
# For permanent fix, configure SELinux policies
```

### Ubuntu/Debian-Specific Issues

#### Package Not Found Errors
```bash
# Update package lists
sudo apt update

# Install universe repository if needed
sudo add-apt-repository universe
sudo apt update
```

#### JavaFX Package Issues
```bash
# Alternative JavaFX installation
sudo apt install -y openjfx
# Or use Maven dependencies (recommended)
```

### Windows-Specific Issues (if applicable)

#### Path Length Limitations
- Use shorter project paths
- Enable long path support in Windows settings

#### File Permissions
- Run as administrator if file access issues occur
- Check antivirus exclusions

### macOS-Specific Issues (if applicable)

#### Gatekeeper Warnings
```bash
# Allow unsigned applications
sudo spctl --master-disable
```

#### JavaFX Path Issues
```bash
# Ensure JavaFX is in PATH
export PATH="/path/to/javafx/lib:$PATH"
```

## Reporting New Issues

### Before Reporting

1. **Check existing documentation:**
   - Review this issues.md file
   - Check [setup.md](setup.md) and [usage.md](usage.md)
   - Search existing issues

2. **Gather information:**
   - Operating system and version
   - Java version (`java -version`)
   - Maven version (`./mvnw -version`)
   - Error messages and stack traces
   - Steps to reproduce

3. **Try troubleshooting steps:**
   - Clean build: `./mvnw clean`
   - Update dependencies: `./mvnw dependency:resolve`
   - Test with different Java versions

### How to Report

**For bugs and issues:**
1. Create a detailed issue description
2. Include error logs and stack traces
3. Provide steps to reproduce
4. Mention your environment details

**For feature requests:**
1. Describe the desired functionality
2. Explain the use case
3. Provide mockups if applicable

**Contact:**
- **GitHub Issues:** [Create issue](https://github.com/GennextITProjects/vastu-software-alb/issues)
- **Email:** support@cpscompany.com
- **Forum:** www.cpscompany.com/forum

### Issue Template

```
## Issue Title

### Environment
- OS: [e.g., Fedora 39, Ubuntu 22.04]
- Java Version: [e.g., OpenJDK 17.0.9]
- Maven Version: [e.g., 3.9.6]

### Description
[Clear description of the issue]

### Steps to Reproduce
1. [Step 1]
2. [Step 2]
3. [Expected result]
4. [Actual result]

### Error Logs
```
[Paste error messages here]
```

### Additional Context
[Any additional information]
```

---

**Last Updated:** January 2026
**Version:** 1.0.0

For the latest updates, check the project repository.
