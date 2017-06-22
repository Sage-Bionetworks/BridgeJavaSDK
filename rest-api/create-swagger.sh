#!/bin/bash

mkdir target
mkdir target/site
npm install
./node_modules/.bin/swagger bundle src/main/resources/index.yml > target/site/swagger.json
./node_modules/.bin/swagger validate target/site/swagger.json
