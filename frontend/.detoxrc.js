/** @type {import('detox').DetoxConfig} */
module.exports = {
  testRunner: {
    args: {
      config: "e2e/jest.ios.config.js",
    },
    jest: {
      setupTimeout: 120000,
    },
  },
  apps: {
    "ios.debug": {
      type: "ios.app",
      binaryPath: "ios/build/Build/Products/Debug-iphonesimulator/CelcoinMobile.app",
      build:
        "ENVFILE=.env.e2e-ios xcodebuild -workspace ios/CelcoinMobile.xcworkspace -scheme CelcoinMobile -configuration Debug -sdk iphonesimulator -derivedDataPath ios/build",
    },
  },
  devices: {
    simulator: {
      type: "ios.simulator",
      device: {
        type: process.env.DETOX_IOS_DEVICE || "iPhone 17 Pro",
      },
    },
  },
  configurations: {
    "ios.sim.debug": {
      device: "simulator",
      app: "ios.debug",
    },
  },
};
