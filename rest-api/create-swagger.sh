#!/bin/bash

mkdir target
npm install
./node_modules/.bin/swagger bundle src/main/resources/index.yml > target/swagger.json
./node_modules/.bin/swagger validate target/swagger.json
