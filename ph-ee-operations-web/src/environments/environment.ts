// The file contents for the current environment will overwrite these during build.
// The build system defaults to the dev environment which uses `environment.ts`, but if you do
// `ng build --env=prod` then `environment.prod.ts` will be used instead.
// The list of which env maps to which file can be found in `.angular-cli.json`.

// `.env.ts` is generated by the `npm run env` command
import env from "./.env";

export let environment = {
  name: "dev",
  production: false,
  version: env.npm_package_version + "-dev",
  serverUrl: "https://paymenthub.qa.oneacrefund.org/opsapp",
  oauth: {
    enabled: "true", // For connecting to Mifos X using OAuth2 Authentication change the value to true
    serverUrl: "https://paymenthub.qa.oneacrefund.org/opsapp",
    basicAuth: "true",
    basicAuthToken: 'Y2xpZW50Og=='
  },
  defaultLanguage: "en-US",
  supportedLanguages: ["en-US", "fr-FR"],
  externalConfigurationFile: "configuration.properties"
};