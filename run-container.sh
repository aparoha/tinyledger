#!/bin/bash

set -e  # Exit on error

# Function to check if a command exists
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

install_java17() {
    echo "Installing Java 17..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
}

install_maven() {
    echo "Installing Maven..."
    sudo apt update
    sudo apt install -y maven
}

install_docker_compose() {
    echo "Installing Docker Compose..."
    # Get latest Docker Compose version from GitHub
    COMPOSE_LATEST=$(curl -s https://api.github.com/repos/docker/compose/releases/latest | grep tag_name | cut -d '"' -f 4)
    sudo curl -L "https://github.com/docker/compose/releases/download/${COMPOSE_LATEST}/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "Docker Compose installed successfully."
}

echo "Checking Java version..."

if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}')
    if [[ "$JAVA_VERSION" == 17* ]]; then
        echo "Java 17 is already installed: version $JAVA_VERSION"
    else
        echo "Java installed but not version 17 (found $JAVA_VERSION). Installing Java 17..."
        install_java17
    fi
else
    echo "Java not found. Installing Java 17..."
    install_java17
fi

echo "Checking Maven installation..."

if command_exists mvn; then
    echo "Maven is already installed: $(mvn -v | head -n 1)"
else
    echo "Maven not found. Installing Maven..."
    install_maven
fi

echo "Checking Docker Compose installation..."

if command_exists docker-compose; then
    echo "Docker Compose is already installed: $(docker-compose --version)"
elif command_exists docker; then
    # Check if docker compose plugin is available (docker compose without dash)
    if docker compose version >/dev/null 2>&1; then
        echo "Docker Compose plugin is available: $(docker compose version)"
    else
        echo "Docker Compose not found. Installing Docker Compose..."
        install_docker_compose
    fi
else
    echo "Docker not found! Please install Docker first."
    exit 1
fi

echo "Building Spring Boot application jar..."

# Only use Maven wrapper now
if [ -f "./mvnw" ]; then
    echo "Using Maven Wrapper to build..."
    ./mvnw clean package -DskipTests
else
    echo "No Maven wrapper found! Exiting."
    exit 1
fi

echo "Building and starting Docker containers with Docker Compose..."

# Prefer 'docker compose' (plugin), fallback to 'docker-compose' binary
if command_exists docker && docker compose version >/dev/null 2>&1; then
    docker compose build
    docker compose up -d
else
    docker-compose build
    docker-compose up -d
fi

echo "Application started. Access it at http://localhost:8080"
