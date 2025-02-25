
# MONOCLE
Our software consists of [MTB Backend ](#mtb-backend) and [MTB GUI/Frontend](#mtb-gui).


# MTB Backend

The MTB (MONOCLE) backend provides the API to read and save data regarding MTB patients.

## Services

### Internal services
`mtb-control and mtb-postgres`

### External services
```Mainzellist``` for pseudonymization (Env. variable: ML_XXX) \
**Note:** currently Mainzellist but will be replaced soon

`Keycloak` for authentication (Env. variable: KEYCLOAKAMDIN_XXX)

`Bwhc` from DNPN (Env. variable: BWHC_XXX)

**Note:** currently deactivated because will be replaced soon with Dnpm:Dip

## Installation

#### Folder Mounts
Create four folders to store the genetic files and MTB report. The folder names should be as follows:
* main
* unzip
* archive
* reports

Update the paths in the docker-compose file (volumes) to be the same locations where these folders are created.

#### Environment Variables

To provide environment variables using env file, copy the `example.env` file to your project directory and rename it to ```.env```.
Customize the variables inside the file, updating them with the desired values.


**Note** : If you are using docker (docker-compose up) and your env file is not inside the main directory, you need to change the
location of the env file.
change the env_file configuration in mtb-control service to:

```
env_file:
- ./file_path/.env
```

**Note** : If the environment variables are defined in the system, the values in the .env file will be overwritten.


# MTB GUI

This is the GUI for the MONOCLE project.

## Services

### Internal services

`mtb-gui`

### External services

`Keycloak` as an identity and access manager with openID protocol

## Installation

#### Environment Variables

Please copy the file [config.js.example](./config.js.example) to `config.js`.

# Docker
If everything is set up, you can run the following command to start the services:

```bash 
docker-compose up
```
or to run it in the background:
```bash 
docker-compose up -d
```
