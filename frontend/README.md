# MTB GUI

This is the GUI for the MONOCLE project.

## Services

### Internal services

`mtb-gui`

### External services

`Keycloak` as an identity and access manager with openID protocol

## Installation

For authorization, you need a Keycloak server running. Please refer to following [documentation](https://www.keycloak.org/getting-started/getting-started-docker) if you need to set up one first.
Add a client with a standard flow authentication only, like this:
![readme_keycloak_img.png](readme_keycloak_img.png)

A bwHc from DNPM and Knowledge Connector (KC) service are not necessary to run the application, but please note that the URLs in the frontend (buttons)
will not take you anywhere.

The bwHc service is deprecated and will soon be updated to the DNPM:Dip service. Therefore, there is no link to the old bwHc.
The Knowledge Connector (KC) service is an external service that is not yet open source.

#### Environment Variables
If you want to run the GUI without docker,
please copy the file [config.js.example](./../config.js.example) to `public/config.js` and configure all environment variables.


#### Install dependencies
```bash
npm install
```

#### Generate api resources
To generate the api resources needed to run the application, run the following command:
```bash
npm run generate-sources
```

#### Run the application

```bash
npm start
```