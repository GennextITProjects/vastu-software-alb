# Windows Build Guide for Master Vastu Application

This guide provides instructions for building and distributing the Master Vastu JavaFX application for Windows.

## Quick Start

### Method 1: Cross-Platform JAR (Recommended for most users)

**Yes, the JAR file will run directly on Windows!** The application can be built once on any platform and run on Windows:

```bash
# Build the application
./mvnw clean package -DskipTests

# Copy the executable JAR to Windows
# File: target/VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar

# On Windows, run with:
java -jar VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
```

**Requirements:**
- Java 17 or later must be installed on the Windows machine
- The JAR file contains all dependencies (it's a "fat JAR")
- No additional setup required beyond Java installation

### Method 2: Native Windows Executable

For a native Windows experience with installer, use the JavaFX Maven Plugin with `jpackage`:

```bash
# Build with native packaging
./mvnw clean javafx:jlink javafx:jpackage -DskipTests

# This creates:
# - target/image/ (application image)
# - target/VastuApplication-1.0-SNAPSHOT.exe (Windows executable)
# - target/VastuApplication-1.0-SNAPSHOT.msi (Windows installer)
```

## Prerequisites for Windows Builds

### Option A: Build on Windows Machine

1. **Install Java 17+**
   - Download from [Adoptium Eclipse Temurin](https://adoptium.net/) or [Oracle JDK](https://www.oracle.com/java/technologies/downloads/)
   - Ensure `JAVA_HOME` is set correctly

2. **Install Maven 3.6+**
   - Download from [Apache Maven](https://maven.apache.org/download.cgi)
   - Or use package managers:
     ```bash
     # Chocolatey
     choco install maven
     
     # Scoop
     scoop install maven
     ```

3. **Install Windows SDK** (for jpackage)
   - Download from [Microsoft](https://developer.microsoft.com/en-us/windows/downloads/windows-sdk/)
   - Required for creating native installers

### Option B: Cross-compile from Linux/macOS

Use the Maven profile for cross-platform builds:

```bash
# Build for Windows from Linux/macOS
./mvnw clean package -Pwindows -DskipTests
```

## Build Configuration

### Current Maven Configuration

The `pom.xml` includes:

- **JavaFX Maven Plugin**: Handles JavaFX-specific builds
- **Maven Assembly Plugin**: Creates fat JAR with all dependencies
- **Java 17**: Target compatibility

### Enhanced Windows Configuration

To add native Windows support, add this profile to `pom.xml`:

```xml
<profile>
    <id>windows</id>
    <activation>
        <os><family>windows</family></os>
    </activation>
    <build>
        <plugins>
            <plugin>
                <groupId>org.openjfx</groupId>
                <artifactId>javafx-maven-plugin</artifactId>
                <version>0.0.8</version>
                <configuration>
                    <targetJvmVersion>17</targetJvmVersion>
                    <launcher>app</launcher>
                    <jlinkZipName>app-windows</jlinkZipName>
                    <jlinkImageName>app-windows</jlinkImageName>
                    <mainClass>com.cps.vastuapp.VastuApplication</mainClass>
                    <jpackage>
                        <name>Master Vastu</name>
                        <vendor>CPS Company</vendor>
                        <description>Vastu Shastra Analysis Application</description>
                        <app-version>1.0</app-version>
                        <type>exe</type>
                        <win-console>true</win-console>
                        <win-dir-chooser>true</win-dir-chooser>
                        <win-menu>true</win-menu>
                        <win-shortcut>true</win-shortcut>
                        <win-per-user-install>true</win-per-user-install>
                    </jpackage>
                </configuration>
            </plugin>
        </plugins>
    </build>
</profile>
```

## Distribution Options

### 1. JAR Distribution (Simplest)

**Pros:**
- Works on any platform with Java installed
- Single file to distribute
- Easy to update

**Cons:**
- Requires Java runtime on target machine
- Less native Windows integration

**Distribution Package:**
```
Master Vastu/
├── VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
├── README.txt
└── install.bat (optional launcher script)
```

### 2. Native Executable

**Pros:**
- No Java installation required
- Native Windows integration
- Professional installer experience

**Cons:**
- Larger file size (~50-100MB)
- Platform-specific builds required

**Distribution Package:**
```
Master Vastu/
├── Master Vastu.msi (installer)
├── Master Vastu.exe (standalone executable)
├── README.txt
└── license.txt
```

## Installation Scripts

### Simple JAR Launcher (install.bat)

```batch
@echo off
echo Installing Master Vastu Application...
echo.

REM Check if Java is installed
java -version >nul 2>&1
if %errorlevel% neq 0 (
    echo Java is not installed or not in PATH.
    echo Please install Java 17 or later from:
    echo https://adoptium.net/
    pause
    exit /b 1
)

REM Create application directory
mkdir "%USERPROFILE%\Master Vastu" 2>nul

REM Copy JAR file
copy "VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar" "%USERPROFILE%\Master Vastu\"

REM Create desktop shortcut
echo Set oWS = WScript.CreateObject("WScript.Shell") > CreateShortcut.vbs
echo sLinkFile = "%USERPROFILE%\Desktop\Master Vastu.lnk" >> CreateShortcut.vbs
echo Set oLink = oWS.CreateShortcut(sLinkFile) >> CreateShortcut.vbs
echo oLink.TargetPath = "java.exe" >> CreateShortcut.vbs
echo oLink.Arguments = "-jar ""%USERPROFILE%\Master Vastu\VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar""" >> CreateShortcut.vbs
echo oLink.Save >> CreateShortcut.vbs
cscript CreateShortcut.vbs
del CreateShortcut.vbs

echo.
echo Installation complete!
echo Application installed to: %USERPROFILE%\Master Vastu
echo Desktop shortcut created.
echo.
pause
```

### Native Installer Instructions

For native Windows installers created with `jpackage`:

1. **MSI Installer**: Double-click the `.msi` file and follow prompts
2. **EXE Installer**: Run the `.exe` file for interactive installation
3. **Portable EXE**: Copy the `.exe` file to any location and run directly

## Troubleshooting

### Common Issues

1. **"Java not found"**
   - Install Java 17+ from [Adoptium](https://adoptium.net/)
   - Add Java to PATH environment variable

2. **"Could not find or load main class"**
   - Ensure you're using the `-jar-with-dependencies.jar` file
   - Check file integrity (corrupted download)

3. **JAR file doesn't open when double-clicked**
   - **Solution**: Run from Command Prompt instead
   - Open Command Prompt (cmd.exe)
   - Navigate to the JAR file location: `cd "C:\path\to\your\file"`
   - Run: `java -jar VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar`
   - This will show any error messages that double-clicking hides

4. **Graphics issues on Windows**
   - Update graphics drivers
   - Try running with `-Dprism.order=sw` flag:
     ```batch
     java -Dprism.order=sw -jar VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
     ```

5. **High DPI scaling issues**
   - Right-click the JAR file → Properties → Compatibility
   - Check "Override high DPI scaling behavior"
   - Select "System" or "System (Enhanced)"

### Performance Optimization

For better performance on Windows:

```batch
REM Optimized launcher script
@echo off
java -Xmx2g -XX:+UseG1GC -Dprism.order=sw -jar VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## System Requirements

### Minimum Requirements
- **OS**: Windows 10 or later (64-bit)
- **RAM**: 4GB
- **Storage**: 100MB free space
- **Java**: Java 17+ (for JAR distribution)

### Recommended Requirements
- **OS**: Windows 11 (64-bit)
- **RAM**: 8GB
- **Storage**: 500MB free space
- **Graphics**: Hardware acceleration support

## Support

For Windows-specific issues:
- **Website**: www.cpscompany.com
- **Support**: support@cpscompany.com
- **Documentation**: [Setup Guide](docs/setup.md)

## Legal

This application is proprietary software developed by CPS Company.
Distribution requires appropriate licensing agreements.