import { QueryClientProvider } from "@tanstack/react-query";
import type { PropsWithChildren } from "react";
import { PaperProvider } from "react-native-paper";
import { queryClient } from "@/core/query/query-client";
import { appTheme } from "@/ui/theme";

export function AppProviders({ children }: PropsWithChildren) {
  return (
    <QueryClientProvider client={queryClient}>
      <PaperProvider theme={appTheme}>{children}</PaperProvider>
    </QueryClientProvider>
  );
}
