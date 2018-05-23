#!/bin/bash

mkdir target
mkdir target/site
npm install
./node_modules/swagger-cli/bin/swagger-cli.js bundle src/main/resources/index.yml > target/site/swagger.json
./node_modules/swagger-cli/bin/swagger-cli.js validate target/site/swagger.json
