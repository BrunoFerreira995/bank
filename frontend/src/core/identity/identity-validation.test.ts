import {
  validateBrazilianPhone,
  validateCnpj,
  validateCpf,
  validateEmail,
  validatePassword,
} from "./identity-validation";

describe("identity validation", () => {
  it("validates Brazilian PF and PJ documents", () => {
    expect(validateCpf("529.982.247-25")).toBe(true);
    expect(validateCpf("111.111.111-11")).toBe(false);
    expect(validateCnpj("04.252.011/0001-10")).toBe(true);
    expect(validateCnpj("11.111.111/1111-11")).toBe(false);
  });

  it("validates contact and password requirements without normalizing secrets", () => {
    expect(validateEmail("cliente@example.com")).toBe(true);
    expect(validateBrazilianPhone("(11) 99999-9999")).toBe(true);
    expect(validatePassword("Senha123")).toBe(true);
    expect(validatePassword("123")).toBe(false);
  });
});
