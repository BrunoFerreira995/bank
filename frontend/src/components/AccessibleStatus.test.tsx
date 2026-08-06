import { render, screen } from "@testing-library/react-native";
import { AccessibleStatus } from "./AccessibleStatus";

describe("AccessibleStatus", () => {
  it("exposes a live accessible status", () => {
    render(<AccessibleStatus title="Serviço" message="Operacional" />);
    expect(screen.getByLabelText("status-Serviço")).toBeTruthy();
    expect(screen.getByText("Operacional")).toBeTruthy();
  });
});
