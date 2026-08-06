type Credentials = { username: string; password: string };
const memory = new Map<string, Credentials>();

export const ACCESSIBLE = { WHEN_UNLOCKED_THIS_DEVICE_ONLY: "web-session" } as const;

export async function getGenericPassword(options: {
  service: string;
}): Promise<Credentials | false> {
  return memory.get(options.service) ?? false;
}

export async function setGenericPassword(
  username: string,
  password: string,
  options: { service: string },
): Promise<boolean> {
  memory.set(options.service, { username, password });
  return true;
}

export async function resetGenericPassword(options: { service: string }): Promise<boolean> {
  memory.delete(options.service);
  return true;
}

export async function getSupportedBiometryType(): Promise<null> {
  return null;
}
