Bridge (Sage Bionetworks)
=========================================

[![Build Status](https://travis-ci.org/Sage-Bionetworks/BridgeDocs.svg?branch=release)](https://travis-ci.org/Sage-Bionetworks/BridgeDocs)

Bridge developer portal website.

This is a jekyll-based site, see: https://jekyllrb.com/ for further instructions. To view the output of any changes, you can run the following command (Ruby and bundler must be installed), and then open a web browser:

```
bundler exec jekyll server
```

In addition, we are using node to install the Swagger command line tools.

## Swagger API documentation

We're documenting our API using Swagger, and generating the base Java REST SDK from the `swagger.json` file. Additions to the `bridge-api/` folder are checked during the Travis build to ensure the `swagger.json` is valid and the file can produce compilable Java files.

The `install.sh` script will install the dependencies needed to do this verification locally (assuming Node is installed), while the `verify.sh` script will do a full verification of your markdown API files and generate the `swagger.json` file from them. If this passes, your build should pass.

## Valid Swagger

We use a subset of Swagger since we are generating Java class files from the specification, and not all valid Swagger definitions produce the correct Java files.

### Super types

A type can only include one super-type, which is expressed with the `allOf` annotation. There can only be one `$ref` to another type (the super-type), and then whatever additions the sub-type makes.

### Sub types

Sub-types are needed to deserialize JSON correctly. The super type defines a `discriminator` property pointing out a field that defines the sub-type (we use `type` everwhere except `UploadFieldDefinition`, where the value was used for something else... it's too late to fix this, but the only library that currently needs this explicit type information is the iOS SDK and it doesn't cover the developer APIs). **Then the property itself should be defined in the super-type, and it must be required.**

```yml
# The parent type... actually abstract
Constraints:
    type: object
    discriminator: type
    properties:
            type:
                type: string
# The concrete sub-type
BooleanConstraints:
    allOf:
        - $ref: ./constraints.yml
        - properties:
            someOtherProperty:
                type: boolean
```

Enumerations should be defined separately from other model definitions, so they are shared between Java classes correctly.

### Responses

Be sure to document the security requirements for an endpoint by documenting the error codes 
that can be returned. For example, there are 403 responses for all the role requirements 
that exist for endpoints, so the required roles are enumerated in the documentation. As 
well, any endpoint that requires consent should document 410/412 responses. And so forth.

### Model properties

The models are defined from the perspective of API consumers, so some keywords should be interpreted in that context:

|Term|Note|
|---|---|
|`readOnly`|This property appears defined in models, but it is not changeable by the API consumer (there's no setter). No default value can exist for it either... if an object is constructed by the client, this value will be missing.|
|`required`|This property must appear on a JSON model submitted by the API consumer; the API consumer will receive an error from the server if it is not present.|

If an object is used in an informational or readonly API, and used in a JSON payload that is sent to the server, define these fields for the object as it would be sent to the server.

## Generating the API client from Swagger specification

To look at the Java source code that will be generated from the specification, you can run the generation locally. Although the `verify.sh` file will do this, if you only want to verify the swagger file, you can download the swagger-codegen-cli.jar:

    wget http://repo1.maven.org/maven2/io/swagger/swagger-codegen-cli/2.2.1/swagger-codegen-cli-2.2.1.jar -O swagger-codegen-cli.jar

Then this should do it:

    java -jar swagger-codegen-cli.jar generate -i _site/swagger.json -l java --library retrofit2 -o java/

The resulting swagger.json file will be copied periodically to `BridgeJavaSdk/api-codegen/swagger.json` in order to update our REST API client.