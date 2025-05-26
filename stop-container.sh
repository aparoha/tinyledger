#!/bin/bash

set -e  # Exit on error

echo "Stopping and removing Docker Compose containers and cleaning up..."

docker compose down --rmi all --volumes --remove-orphans

echo "Cleanup completed."
