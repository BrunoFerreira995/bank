import { safeErrorMessage } from "./operations-api";

describe("operations safety", () => {
  it("returns a safe fallback for unknown errors", () => {
    expect(safeErrorMessage({})).toBe("Não foi possível concluir a operação. Tente novamente.");
  });
});
