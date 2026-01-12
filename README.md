# Master Vastu Application

A comprehensive JavaFX desktop application for Vastu Shastra analysis and visualization. This tool enables Vastu consultants and enthusiasts to analyze property layouts by overlaying traditional Vastu diagrams, performing interactive analysis, and generating professional reports.

## 🚀 Quick Start

### Prerequisites
- Java 17 or higher
- Maven 3.6+ (or use included Maven wrapper)

### Run the Application
```bash
# Clone the repository
git clone https://github.com/GennextITProjects/vastu-software-alb.git
cd vastu-software-alb

# Run with Maven wrapper (recommended)
./mvnw clean javafx:run

# Or build and run JAR
./mvnw clean package
java -jar target/VastuApplication-1.0-SNAPSHOT-jar-with-dependencies.jar
```

## 📚 Documentation

Complete documentation is available in the [`docs/`](./docs/) directory:

- **[📖 Main Documentation](./docs/README.md)** - Project overview and features
- **[🛠️ Setup Guide](./docs/setup.md)** - Environment setup for Debian/Ubuntu and Fedora
- **[📋 Usage Guide](./docs/usage.md)** - Comprehensive user manual with workflows
- **[🏗️ Architecture](./docs/architecture.md)** - Technical architecture and implementation details
- **[🐛 Issues & Troubleshooting](./docs/issues.md)** - Known issues and solutions

## ✨ Key Features

- **Property Analysis**: Import and analyze architectural plans (PNG, JPG, PDF)
- **Vastu Overlays**: 40+ traditional Vastu diagrams (5 Elements, 9 Zones, 45 Devtas, etc.)
- **Interactive Tools**: Point selection, rotation, scaling, and color customization
- **Multi-Format Export**: PNG images and PDF reports
- **Cross-Platform**: Runs on Windows, macOS, and Linux
- **Multi-Monitor Support**: Optimized for extended workspaces

## 🏠 What is Vastu Shastra?

Vastu Shastra is an ancient Indian architectural science that harmonizes buildings and spaces with natural energies through proper design, orientation, and spatial arrangement. This application brings traditional Vastu wisdom into the digital age with modern analysis tools.

## 🛠️ Technology Stack

- **Java 17** - Modern JVM with latest features
- **JavaFX 17** - Rich desktop GUI framework
- **Maven** - Build automation and dependency management
- **Apache PDFBox** - PDF processing and rendering

## 📦 Project Structure

```
vastu-software-alb/
├── docs/                      # 📚 Complete documentation
├── src/main/java/             # Source code
├── src/main/resources/        # UI layouts, icons, Vastu overlays
├── pom.xml                    # Maven configuration
├── mvnw                       # Maven wrapper (Unix)
└── README.md                  # This file
```

## 🤝 Contributing

We welcome contributions! Please see our [Contributing Guidelines](./CONTRIBUTING.md) and [Development Setup](./docs/setup.md).

## 📄 License

Proprietary software developed by CPS Company.

## 🆘 Support

- **Documentation**: [Complete User Guide](./docs/usage.md)
- **Setup Help**: [Environment Setup](./docs/setup.md)
- **Technical Details**: [Architecture Guide](./docs/architecture.md)
- **Website**: www.cpscompany.com
- **Support**: support@cpscompany.com

---

**Master Vastu** - Harmonizing spaces with ancient wisdom, powered by modern technology.
