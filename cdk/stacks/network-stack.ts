import { Stack, StackProps } from 'aws-cdk-lib';
import { Construct } from 'constructs';
import { CorsHttpMethod, HttpApi, HttpMethod } from 'aws-cdk-lib/aws-apigatewayv2';
import { HttpLambdaIntegration } from 'aws-cdk-lib/aws-apigatewayv2-integrations';
import { Function } from 'aws-cdk-lib/aws-lambda';

interface NetworkStackProps extends StackProps {
  readonly lambda: Function;
}

export class NetworkStack extends Stack {
  constructor(scope: Construct, id: string, props: NetworkStackProps) {
    super(scope, id, props);

    const demoLambdaIntegration
      = new HttpLambdaIntegration('DemoLambdaIntegration', props.lambda);

    const httpApi = new HttpApi(this, 'HttpApi', {
      description: 'DemoMicronautServerlessFunction',
      createDefaultStage: false,
      corsPreflight: {
        allowHeaders: ['Authorization'],
        allowMethods: [CorsHttpMethod.ANY],
        allowOrigins: ['*']
      }
    });

    httpApi.addStage('DefaultStage', {
      stageName: '$default',
      autoDeploy: true,
      throttle: {
        burstLimit: 2,
        rateLimit: 1
      }
    });

    httpApi.addRoutes({
      path: '/health',
      methods: [HttpMethod.GET],
      integration: demoLambdaIntegration
    });

    httpApi.addRoutes({
      path: '/add',
      methods: [HttpMethod.POST],
      integration: demoLambdaIntegration
    });
  }
}
