#!/usr/bin/env node

import { App, Environment } from 'aws-cdk-lib';

import { NetworkStack } from '../stacks/network-stack';
import { AppStack } from '../stacks/app-stack';

const cdkEnvironment: Environment = {
  account: '024848454171',
  region: 'us-east-1'
};

const app = new App();

const appStack = new AppStack(app, 'DemoMicronautServerlessFunctionAppStack', {
  env: cdkEnvironment
});

new NetworkStack(app, 'DemoMicronautServerlessFunctionNetworkStack', {
  env: cdkEnvironment,
  lambda: appStack.lambda
});
