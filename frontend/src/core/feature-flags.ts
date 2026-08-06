import { env } from "@/config/env";

export type FeatureName = keyof typeof env.flags;

export function isFeatureEnabled(feature: FeatureName): boolean {
  return env.flags[feature];
}
