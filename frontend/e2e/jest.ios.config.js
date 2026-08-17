module.exports = {
  rootDir: "..",
  testMatch: ["<rootDir>/e2e/ios/**/*.e2e.ts"],
  preset: "detox",
  testTimeout: 120000,
  reporters: ["detox/runners/jest/streamlineReporter"],
};
