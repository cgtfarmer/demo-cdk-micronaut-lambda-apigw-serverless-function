import { join } from 'path';
import { Construct } from 'constructs';
import { BundlingOutput, Duration, Stack, StackProps } from 'aws-cdk-lib';
import { Code, Function, Runtime } from 'aws-cdk-lib/aws-lambda';

export class AppStack extends Stack {
  public readonly lambda: Function;

  constructor(scope: Construct, id: string, props: StackProps) {
    super(scope, id, props);

    this.lambda = new Function(this, 'DemoLambda', {
      runtime: Runtime.JAVA_21,
      handler: 'com.cgtfarmer.app.Handler',
      code: Code.fromAsset(join(__dirname, '../../'), {
        bundling: {
          image: Runtime.JAVA_21.bundlingImage,
          user: 'root',
          command: [
            '/bin/sh',
            '-c',
            'gradle build -PoutDir=/tmp --no-daemon && '
            + 'cp -v /tmp/libs/*.jar /asset-output/'
          ],
          // TODO: Modify this for Gradle?
          // Mounting local ~/.m2 repo to avoid re-downloading all the dependencies
          // volumes: [
          //   {
          //     hostPath: join(homedir(), '.m2/repository'),
          //     containerPath: '/root/.m2/repository/'
          //   }
          // ],
          outputType: BundlingOutput.ARCHIVED
        }
      }),
      environment: {
        MICRONAUT_ENVIRONMENTS: 'cloud',
        LOGGER_LEVELS_ROOT: 'DEBUG',
        LOGGER_LEVELS_COM_CGTFARMER: 'TRACE'
      },
      memorySize: 1024,
      timeout: Duration.seconds(30)
    });
  }
}
