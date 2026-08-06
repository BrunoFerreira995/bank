export type FieldErrors = Record<string, string>;

export function validateCpf(value: string): boolean {
  const cpf = digits(value);
  if (cpf.length !== 11 || /^([0-9])\1+$/.test(cpf)) return false;
  const first = calculateDigit(cpf.slice(0, 9));
  const second = calculateDigit(cpf.slice(0, 9) + first);
  return cpf === cpf.slice(0, 9) + first + second;
}

export function validateCnpj(value: string): boolean {
  const cnpj = digits(value);
  if (cnpj.length !== 14 || /^([0-9])\1+$/.test(cnpj)) return false;
  const first = calculateCnpjDigit(cnpj.slice(0, 12));
  const second = calculateCnpjDigit(cnpj.slice(0, 12) + first);
  return cnpj === cnpj.slice(0, 12) + first + second;
}

export function validateEmail(value: string): boolean {
  return /^[^\s@]+@[^\s@]+\.[^\s@]+$/.test(value.trim());
}

export function validateBrazilianPhone(value: string): boolean {
  const phone = digits(value);
  return phone.length === 10 || phone.length === 11;
}

export function validatePassword(value: string): boolean {
  return value.length >= 8 && /[A-Za-z]/.test(value) && /\d/.test(value);
}

function digits(value: string): string {
  return value.replace(/\D/g, "");
}

function calculateDigit(partial: string): string {
  const sum = partial
    .split("")
    .reduce((total, digit, index) => total + Number(digit) * (partial.length + 1 - index), 0);
  const remainder = (sum * 10) % 11;
  return String(remainder === 10 ? 0 : remainder);
}

function calculateCnpjDigit(partial: string): string {
  const weights =
    partial.length === 12
      ? [5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2]
      : [6, 5, 4, 3, 2, 9, 8, 7, 6, 5, 4, 3, 2];
  const sum = partial
    .split("")
    .reduce((total, digit, index) => total + Number(digit) * (weights[index] ?? 0), 0);
  const remainder = sum % 11;
  return String(remainder < 2 ? 0 : 11 - remainder);
}
