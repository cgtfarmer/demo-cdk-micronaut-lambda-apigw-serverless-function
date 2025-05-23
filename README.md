# Demo CDK Java Lambda APIGW Hexagonal Architecture Playwright

This is a template repo for an API GW fronted Java Lambda using hexagonal architecture

## Prerequisites

- Docker
- Node 20
- Java 21
- Gradle
- awscli


## Setup

1. Clone this repository

2. Install NPM dependencies

```sh
npm install
```

3. Set your AWS Account ID in `./bin/cdk.ts`

## Build

Regular:

```sh
gradle build
```

Send build artifacts to specific dir:

```sh
gradle build -PoutDir=/tmp
```

## Deploy

```sh
npx cdk deploy --all
```

## Style Formatter

Check:

```sh
  gradle spotlessCheck
```

Format:

```sh
  gradle spotlessApply
```

## Automated Tests

Run all tests:

```sh
  gradle test
```

Run one test class:

```sh
  gradle test --tests TestClassNameHere
```

Run one test:

```sh
  gradle test --tests TestClassNameHere.testMethodHere
```

Run test categories by tag:

```sh
  gradle test -Ptags="unit"

  gradle test -Ptags="integration"

  gradle test -Ptags="application"

  gradle test -Ptags="system"

  gradle test -Ptags="unit,integration"
```
