#!/usr/bin/env python3

import sys
import subprocess
import argparse
import logging
from colorama import Fore, Style, init

# Initialize colorama
init(autoreset=True)

# Define Docker registry and service name
DOCKER_REGISTRY = "dockerreg.iam-extern.de/"
SERVICE_NAME = "mtb-gui"

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format="%(levelname)s - %(message)s",
    handlers=[logging.StreamHandler(sys.stdout)],
)

logger = logging.getLogger(__name__)


def parse_args():
    """
    Parse command-line arguments.
    """
    parser = argparse.ArgumentParser(description="Build and deploy Docker images")
    parser.add_argument(
        "-v", "--version", default="latest", help="Version tag for the Docker image (default: latest)"
    )
    return parser.parse_args()


def build_image(docker_build_command, image):
    """
    Build the Docker image using the provided command.
    """
    logger.info(f"Building image '{image}'...")
    try:
        result = subprocess.run(docker_build_command, capture_output=True, text=True, check=True)
        logger.info("Building Image output:\n%s", result.stdout)
        if result.stderr:
            logger.warning("Warnings during build:\n%s", result.stderr)
        logger.info("Image built successfully.\n")
    except subprocess.CalledProcessError as e:
        logger.error("Command failed with return code %d", e.returncode)
        logger.error("Error output:\n%s", e.stderr)
        sys.exit(1)


def push_image(image):
    """
    Push the Docker image to the registry.
    """
    logger.info(f"Pushing image '{image}' to Docker registry...")
    try:
        subprocess.run(["docker", "push", image], check=True)
        logger.info("Image pushed to Docker registry successfully.\n")
    except subprocess.CalledProcessError as e:
        logger.error("Failed to push image: %s", e.stderr)
        sys.exit(1)


def print_save_commands(image):
    """
    Print commands to save the Docker image to remote servers.
    """
    logger.info("Your image was saved locally and on the Docker registry.\n")

    # Highlight server names in color
    dockergfx2 = Fore.CYAN + "dockergfx2" + Style.RESET_ALL
    srvkaamiappp02 = Fore.CYAN + "srvkaamiappp02" + Style.RESET_ALL

    logger.info("To save your image on %s, use the following command:", dockergfx2)
    print(
        f"docker save {image} | ssh iamdev@{dockergfx2} -J jumper@10.167.29.200:22 docker load\n"
    )

    logger.info("To save your image on %s, use the following command:", srvkaamiappp02)
    print(
        f"docker save {image} | ssh root@{srvkaamiappp02}.kisabt.uke.de -J jumper@10.167.29.200:22,jumper@kajumper docker load\n"
    )


def main():
    """
    Main function to build, push, and provide save commands for the Docker image.
    """
    try:
        args = parse_args()
        version = args.version

        # Notify if no version was provided
        if version == "latest":
            logger.warning("No version provided. Using default version: %s", Fore.YELLOW + "latest" + Style.RESET_ALL)
        else:
            logger.info("Using provided version: %s", Fore.GREEN + version + Style.RESET_ALL)

        image = f"{DOCKER_REGISTRY}{SERVICE_NAME}:{version}"
        docker_build_command = ["docker", "build", "-t", image, "."]

        build_image(docker_build_command, image)
        push_image(image)
        print_save_commands(image)

    except Exception as e:
        logger.error("An unexpected error occurred: %s", str(e))
        sys.exit(1)


if __name__ == "__main__":
    main()