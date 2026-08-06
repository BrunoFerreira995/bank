module.exports = {
  preset: "@react-native/jest-preset",
  moduleNameMapper: {
    "^@/(.*)$": "<rootDir>/src/$1",
    "^react-native-config$": "<rootDir>/__mocks__/react-native-config.js",
    "^react-native-keychain$": "<rootDir>/__mocks__/react-native-keychain.js",
  },
  testPathIgnorePatterns: ["/node_modules/", "/e2e/"],
  collectCoverageFrom: ["src/**/*.{ts,tsx}"],
};
