#!/bin/bash

# Script located in project root (same folder as pom.xml)

install_java17() {
  echo "Installing Java 17..."
  sudo apt update
  sudo apt install -y openjdk-17-jdk
  if ! java -version 2>&1 | grep "17"; then
    echo "Java 17 installation failed."
    exit 1
  fi
  echo "Java 17 installed successfully."
}

install_maven() {
  echo "Installing Maven..."
  sudo apt update
  sudo apt install -y maven
  if ! mvn -version >/dev/null 2>&1; then
    echo "Maven installation failed."
    exit 1
  fi
  echo "Maven installed successfully."
}

# Check Java version
echo "Checking Java version..."
if java -version 2>&1 | grep "17" >/dev/null; then
  echo "Java 17 is already installed."
else
  echo "Java 17 not found."
  install_java17
fi

# Check Maven
echo "Checking Maven..."
if mvn -version >/dev/null 2>&1; then
  echo "Maven is already installed."
else
  echo "Maven not found."
  install_maven
fi

# Navigate to the directory where the script is (project root)
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR" || { echo "Failed to navigate to project directory."; exit 1; }

echo "Building the project..."
mvn clean package || { echo "Build failed."; exit 1; }

# Find the first JAR in target folder
JAR_FILE=$(find target -name "*.jar" | head -n 1)

if [[ -z "$JAR_FILE" ]]; then
  echo "Jar file not found after build."
  exit 1
fi

echo "Running the application from $JAR_FILE"
java -jar "$JAR_FILE"
