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

### Folder Mounts
Create four folders to store the genetic files and MTB report. The folder names should be as follows:
* main
* unzip
* archive
* reports

Update the paths in the docker-compose file (volumes) to be the same locations where these folders are created.

### Environment Variables

To provide environment variables using env file, copy the `example.env` file to your project directory and rename it to ```.env```.
Customize the variables inside the file, updating them with the desired values
(See [Local](#local) for local use).

**Note** : If the environment variables are defined in the system, the values in the .env file will be overwritten.

### Local
For a local run you can start the database service via docker with 
```bash
docker-compose up mtb-postgres
```
Please adjust the environment variables.

Build and package the application by running 
```bash
mvn package -f pom.xml
```

_**Hints**_:\
In most cases the database is now accessed via `localhost:5432`, not the docker service name anymore.

The Environment variables
* MTB_MAIN_DIR
* MTB_UNZIP_DIR
* MTB_ARCHIVE_DIR
* MTB_REPORTS_DIR

are now the folders on your local host, not the folders within the docker container. 
If your host is a Windows machine you need to adjust the paths accordingly (e.g. C:\\Users\\<user>\\MTB\\main).

Add the location of the .env file in the IDE settings when running.

Run the backend:
```bash
java -jar .\backend\target\control-<version>.jar fully.qualified.package.Application
```
\<version> must be replaced with [Version in pom](pom.xml).