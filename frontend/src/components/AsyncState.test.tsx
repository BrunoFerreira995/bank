import { render, screen } from "@testing-library/react-native";
import { AsyncState } from "./AsyncState";

describe("AsyncState", () => {
  it("exposes loading, empty and retryable error states", () => {
    const { rerender } = render(<AsyncState state="loading" />);
    expect(screen.getByLabelText("Carregando")).toBeTruthy();
    rerender(<AsyncState state="empty" />);
    expect(screen.getByLabelText("Sem resultados")).toBeTruthy();
    rerender(<AsyncState state="error" message="Falha" onRetry={() => undefined} />);
    expect(screen.getByText("Tentar novamente")).toBeTruthy();
  });
});
