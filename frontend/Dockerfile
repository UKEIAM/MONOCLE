# build environment
FROM node:20.2.0-buster as openapi-generate
RUN apt update && apt-get -y install default-jre
RUN npm install -g @openapitools/openapi-generator-cli
WORKDIR /openapi_generator
COPY ["package.json", "package-lock.json*", "./"]
COPY ["config/openapi.yaml", "config/openapi.yaml"]
RUN npm run generate-sources

FROM node:20.2.0-alpine as build

ENV PATH /app/node_modules/.bin:$PATH
WORKDIR /app
COPY ["package.json", "package-lock.json*", "./"]

RUN npm ci --silent
RUN npm install react-scripts@3.4.1 -g --silent
COPY . ./
COPY --from=openapi-generate /openapi_generator/src/gen/api ./src/gen/api
RUN npm run build

# production environment
FROM nginx:stable-alpine
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]