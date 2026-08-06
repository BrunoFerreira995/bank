declare module "react-native-config" {
  const Config: {
    ENVIRONMENT?: string;
    BFF_BASE_URL?: string;
    ENABLE_PIX?: string;
    ENABLE_OPEN_FINANCE?: string;
    ENABLE_CREDIT?: string;
  };
  export default Config;
}
