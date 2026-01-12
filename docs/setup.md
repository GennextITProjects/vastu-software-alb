# Environment Setup Guide

This guide provides step-by-step instructions for setting up the development environment for the Master Vastu application on both Debian-based systems (Ubuntu, Debian, etc.) and Fedora.

## Prerequisites

Before installing the application, ensure your system meets these requirements:

- **Operating System**: Linux (Debian-based or Fedora)
- **Architecture**: x86_64 (64-bit)
- **Memory**: Minimum 4GB RAM, 8GB recommended
- **Storage**: 500MB free space for installation and build
- **Display**: 1920x1080 resolution minimum

## Option 1: Debian-based Systems (Ubuntu, Debian, Linux Mint, etc.)

### Step 1: Update System Packages

```bash
sudo apt update && sudo apt upgrade -y
```

### Step 2: Install Java 17

Choose one of the following methods:

#### Method A: OpenJDK 17 (Recommended)

```bash
# Install OpenJDK 17
sudo apt install -y openjdk-17-jdk

# Verify installation
java -version
javac -version
```

Expected output:
```
openjdk version "17.0.x" 2021-09-14
OpenJDK Runtime Environment (build 17.0.x+7-Ubuntu-x.x.x)
OpenJDK 64-Bit Server VM (build 17.0.x+7-Ubuntu-x.x.x, mixed mode)
```

#### Method B: Oracle JDK 17

```bash
# Add Oracle JDK repository
sudo add-apt-repository ppa:linuxuprising/java
sudo apt update

# Install Oracle JDK 17
sudo apt install -y oracle-java17-installer

# Set Java 17 as default
sudo apt install -y oracle-java17-set-default
```

### Step 3: Install Maven

```bash
# Install Maven
sudo apt install -y maven

# Verify installation
mvn -version
```

Expected output:
```
Apache Maven 3.x.x
Maven home: /usr/share/maven
Java version: 17.0.x, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-17-oracle
```

**For Fedora, see Step 3 in the Fedora section below.**

### Step 4: Install Additional Dependencies (Optional)

```bash
# Install Git for version control
sudo apt install -y git

# Install development tools
sudo apt install -y build-essential

# Install image processing libraries (for better PDF rendering)
sudo apt install -y libtiff-tools libjpeg-dev zlib1g-dev
```

## Option 2: Fedora

### Step 1: Update System Packages

```bash
sudo dnf update -y
```

### Step 2: Install Java 17

Choose one of the following methods:

#### Method A: OpenJDK 17 (Recommended)

```bash
# Install OpenJDK 17
sudo dnf install -y java-17-openjdk-devel

# Verify installation
java -version
javac -version
```

Expected output:
```
openjdk version "17.0.x" 2021-09-14
OpenJDK Runtime Environment (build 17.0.x+7)
OpenJDK 64-Bit Server VM (build 17.0.x+7, mixed mode)
```

#### Method B: Oracle JDK 17

```bash
# Download Oracle JDK 17 RPM from Oracle website
# Visit: https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html

# Install the downloaded RPM
sudo rpm -ivh jdk-17_linux-x64_bin.rpm

# Set JAVA_HOME (add to ~/.bashrc or ~/.zshrc)
export JAVA_HOME=/usr/java/jdk-17
export PATH=$JAVA_HOME/bin:$PATH
```

### Step 3: Install Maven

```bash
# Install Maven
sudo dnf install -y maven

# Verify installation
mvn -version
```

Expected output:
```
Apache Maven 3.x.x
Maven home: /usr/share/maven
Java version: 17.0.x, vendor: Oracle Corporation, runtime: /usr/lib/jvm/java-17-openjdk
```

### Step 4: Install Additional Dependencies (Optional)

```bash
# Install Git for version control
sudo dnf install -y git

# Install development tools
sudo dnf groupinstall -y "Development Tools"

# Install image processing libraries
sudo dnf install -y libtiff-tools libjpeg-turbo-devel zlib-devel
```

## Project Setup

### Step 1: Clone or Download the Project

```bash
# Clone the repository (if using Git)
git clone https://github.com/GennextITProjects/vastu-software-alb.git
cd vastu-software-alb

# Or download and extract the ZIP file
# wget https://github.com/GennextITProjects/vastu-software-alb/archive/main.zip
# unzip main.zip
# cd vastu-software-alb-main
```

### Step 2: Verify Java and Maven Setup

```bash
# Check Java version
java -version

# Check Maven version
mvn -version

# Check Maven wrapper (alternative to system Maven)
./mvnw -version
```

### Step 3: Build the Project

```bash
# Using Maven wrapper (recommended)
./mvnw clean compile

# Or using system Maven
mvn clean compile
```

### Step 4: Run the Application

```bash
# Using Maven wrapper (recommended)
./mvnw clean javafx:run

# Or using system Maven
mvn clean javafx:run
```

## Troubleshooting

### Common Issues

#### 1. Java Version Conflicts

If you have multiple Java versions installed:

**Debian/Ubuntu:**
```bash
# List installed Java versions
update-java-alternatives --list

# Set Java 17 as default
sudo update-java-alternatives --set java-1.17.0-openjdk-amd64

# Or manually set JAVA_HOME
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64
```

**Fedora:**
```bash
# List alternatives
alternatives --config java

# Set Java 17
alternatives --set java /usr/lib/jvm/java-17-openjdk/bin/java
```

#### 2. Maven Build Failures

```bash
# Clear Maven cache
./mvnw clean

# Force dependency download
./mvnw dependency:resolve

# Run with verbose output for debugging
./mvnw clean compile -X
```

#### 3. JavaFX Runtime Issues

```bash
# Ensure JavaFX modules are available
java --list-modules | grep javafx

# If missing, check Java installation
java -version
```

#### 4. Permission Issues

```bash
# Make Maven wrapper executable
chmod +x mvnw

# Run with sudo if needed (not recommended for development)
sudo ./mvnw clean javafx:run
```

#### 5. Memory Issues

If you encounter OutOfMemoryError:

```bash
# Increase Maven heap size
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
./mvnw clean javafx:run
```

### Environment Variables

Add these to your `~/.bashrc` or `~/.zshrc`:

```bash
# Java 17
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk-amd64  # Adjust path for your system
export PATH=$JAVA_HOME/bin:$PATH

# Maven (optional, if using system Maven)
export MAVEN_HOME=/usr/share/maven
export PATH=$MAVEN_HOME/bin:$PATH

# Increase heap size for large projects
export MAVEN_OPTS="-Xmx1024m -XX:MaxPermSize=256m"
```

### Testing the Setup

Create a simple test to verify your environment:

```bash
# Create a test directory
mkdir test-setup
cd test-setup

# Create a simple JavaFX application
cat > HelloJavaFX.java << 'EOF'
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class HelloJavaFX extends Application {
    @Override
    public void start(Stage stage) {
        Label label = new Label("Hello, JavaFX!");
        Scene scene = new Scene(label, 300, 200);
        stage.setScene(scene);
        stage.setTitle("JavaFX Test");
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
EOF

# Compile and run
javac --module-path /usr/share/openjfx/lib --add-modules javafx.controls HelloJavaFX.java
java --module-path /usr/share/openjfx/lib --add-modules javafx.controls HelloJavaFX

# Clean up
cd ..
rm -rf test-setup
```

## Next Steps

Once your environment is set up:

1. [Usage Guide](usage.md) - Learn how to use the application
2. [Architecture](architecture.md) - Understand the technical details
3. [Contributing](../CONTRIBUTING.md) - Guidelines for contributors

## Support

If you encounter issues:
- Check the [troubleshooting section](#troubleshooting) above
- Verify all prerequisites are met
- Ensure you're using Java 17 specifically
- Try using the Maven wrapper (`./mvnw`) instead of system Maven

For additional support, visit: www.cpscompany.com/support
