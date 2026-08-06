const values = new Map();

module.exports = {
  ACCESSIBLE: { WHEN_UNLOCKED_THIS_DEVICE_ONLY: "WhenUnlockedThisDeviceOnly" },
  getGenericPassword: async ({ service }) => {
    const password = values.get(service);
    return password ? { username: "mock", password } : false;
  },
  setGenericPassword: async (_username, password, { service }) => {
    values.set(service, password);
    return true;
  },
  resetGenericPassword: async ({ service }) => {
    values.delete(service);
    return true;
  },
};
