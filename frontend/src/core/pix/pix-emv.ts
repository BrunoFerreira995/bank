export type PixQrData = {
  payload: string;
  key?: string;
  amount?: string;
  merchantName?: string;
  city?: string;
};

export function decodePixPayload(payload: string): PixQrData {
  const value = payload.trim();
  if (!value.startsWith("000201") || value.length < 10) throw new Error("QR Code Pix inválido");
  const fields = parseFields(value);
  return {
    payload: value,
    key: fields.get("26") ?? fields.get("01"),
    amount: fields.get("54"),
    merchantName: fields.get("59"),
    city: fields.get("60"),
  };
}

function parseFields(payload: string): Map<string, string> {
  const fields = new Map<string, string>();
  let index = 0;
  while (index + 4 <= payload.length) {
    const tag = payload.slice(index, index + 2);
    const length = Number(payload.slice(index + 2, index + 4));
    if (!Number.isInteger(length) || index + 4 + length > payload.length)
      throw new Error("QR Code Pix malformado");
    fields.set(tag, payload.slice(index + 4, index + 4 + length));
    index += 4 + length;
  }
  return fields;
}
