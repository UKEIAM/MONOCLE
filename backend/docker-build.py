#!/usr/bin/env python3
import sys
import subprocess
import os
import argparse
import shutil

# Define Docker registry and service name
DOCKER_REGISTRY = "dockerreg.iam-extern.de/"
SERVICE_NAME = "mtb-control"

def parse_args():
    parser = argparse.ArgumentParser(description="Build and deploy Docker images")
    parser.add_argument("-v", "--version", required=True, help="Version tag for the Docker image")
    parser.add_argument("-e", "--environment", required=True, choices=["development", "staging", "production"], help="Environment to deploy (development, staging, or production)")
    return parser.parse_args()

def is_tag_local(version):
    try:
        # Check if the tag exists locally
        result = subprocess.run(["git", "tag", "-l", f"{version}"], capture_output=True, text=True, check=True)
        # If the tag exists locally, return True
        return result.stdout.strip() != ""
    except subprocess.CalledProcessError as e:
        raise RuntimeError(f"Git command failed with exit status {e.returncode}: {e.stderr}")
    except Exception as e:
       raise RuntimeError(f"An unexpected error occurred: {e}")

def is_tag_remote(version):
    try:
        subprocess.run(["git", "fetch", "--tags"], check=True)
        # Run the git ls-remote command and capture the output
        process = subprocess.Popen(["git", "ls-remote", "--tags", "origin", version],
                                   stdout=subprocess.PIPE, stderr=subprocess.PIPE, text=True)
        stdout, stderr = process.communicate()
        # Check if the tag exists in the output
        return version in stdout
    except subprocess.CalledProcessError as e:
        raise RuntimeError(f"Git command failed with exit status {e.returncode}: {e.stderr}")
    except Exception as e:
        raise RuntimeError(f"An unexpected error occurred: {e}")


def tag_and_push(version):
  try:
    # Check if the tag exists locally
    print(f"Checking if tag '{version}' exists locally...")
    if is_tag_local(version):
      print(f"Tag '{version}' found locally.")
      # Check if the tag exists on the remote repository
      if is_tag_remote(version):
        print(f"Tag '{version}' already exists on remote. No need to push.")
      else:
        print("Tag not found on remote. Pushing it...")
        subprocess.run(["git", "push", "origin", version], check=True)
    else:
        print("Tag not found locally. Creating a new one and push it to remote...")
        # Create the tag and push it to the repository
        subprocess.run(["git", "tag", version], check=True)
        subprocess.run(["git", "push", "origin", version], check=True)
        print(f"Tag '{version}' created and pushed to the repository.")
  except Exception as e:
    print(str(e))
    sys.exit(1)

def copy_env_file(environment):
    env_file = f".env.docker.{environment}"
    if not os.path.isfile(env_file):
        print(f"{env_file} not found. Copying .env.docker.example...")
        shutil.copyfile(".env.docker.example", env_file)
        print(f"\n\n.env.docker.example copied to {env_file}\n\n")
    return env_file

def build_image(docker_build_command, image):
    print(f"Building image '{image}'...")
    try:
        # Befehl ausführen
        result = subprocess.run(docker_build_command, capture_output=True, text=True, check=True)
        print("\n\nBuilding Image output:")
        print(result.stdout)
        print("\n\nErrors or Warnings:")
        print(result.stderr)
        print("\n\nImage built successfully.\n\n")
    except subprocess.CalledProcessError as e:
        print("\n\nCommand failed with return code", e.returncode)
        print(e.stderr)
        sys.exit(1)

def push_image(image):
    print(f"Pushing image '{image}' to Docker registry...")
    subprocess.run(["docker", "push", image], check=True)
    print("\n\nImage pushed to Docker registry successfully.\n\n")

if __name__ == "__main__":
    try:
        args = parse_args()
        version = args.version
        environment = args.environment

        print(f"Performing actions for {environment} environment...")

        if environment == "development":
            image = f"{SERVICE_NAME}:latest"
            env_file = copy_env_file(environment)
            final_command = f"docker-compose --env-file {env_file} up"
            docker_build_command = ["docker", "build", "-t", image, "."]
            build_image(docker_build_command, image)
            print("To run your image, you can use the following command: \n")
        else:
            tag_and_push(version)
            image = f"{DOCKER_REGISTRY}{SERVICE_NAME}:{version}"
            env_file = copy_env_file(environment)
            docker_build_command = ["docker", "build", "-t", image, "."]
            build_image(docker_build_command, image)

            if environment in ["staging", "production"]:
                push_image(image)
                print(f"\n\nTo save your image in {environment} environment, you can use the following command:\n")
                if environment == "staging":
                    final_command = f"docker save {image} | ssh iamdev@dockergfx2 -J jumper@10.167.29.200:22 docker load"
                elif environment == "production":
                    final_command = f"docker save {image} | ssh root@srvkaamiappp02.kisabt.uke.de -J jumper@10.167.29.200:22,jumper@kajumper docker load"
        print(final_command)
    except Exception as e:
        print(str(e))
        sys.exit(1)