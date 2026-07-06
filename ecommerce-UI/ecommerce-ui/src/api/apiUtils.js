export function unwrapApiResponse(response) {
  const payload = response?.data ?? response;

  if (payload && typeof payload === "object" && "data" in payload) {
    return payload.data;
  }

  return payload;
}

export function normalizeList(payload) {
  const data = unwrapApiResponse(payload);

  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.content)) return data.content;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.cartItems)) return data.cartItems;
  if (Array.isArray(data?.orderItems)) return data.orderItems;

  return [];
}

export function getApiErrorMessage(error, fallback = "Something went wrong.") {
  const payload = error?.response?.data;

  if (typeof payload === "string") return payload;
  if (payload?.message) return payload.message;
  if (Array.isArray(payload?.errors) && payload.errors.length > 0) {
    return payload.errors[0];
  }

  return fallback;
}

export function getProductId(product) {
  return product?.id ?? product?.productId;
}

export function getCategoryId(category) {
  return category?.id ?? category?.categoryId;
}

export function getCategoryName(productOrCategory) {
  return (
    productOrCategory?.category?.name ||
    productOrCategory?.categoryName ||
    productOrCategory?.name ||
    productOrCategory?.category ||
    "Other"
  );
}

export function getCartItems(cartPayload) {
  const data = unwrapApiResponse(cartPayload);

  if (Array.isArray(data)) return data;
  if (Array.isArray(data?.cartItems)) return data.cartItems;
  if (Array.isArray(data?.items)) return data.items;
  if (Array.isArray(data?.data)) return data.data;

  return [];
}

export function getCartItemId(item) {
  return item?.id ?? item?.cartItemId;
}

export function getProductFromCartItem(item) {
  return item?.product || item?.productResponse || item;
}

export function getItemPrice(item) {
  const product = getProductFromCartItem(item);
  return Number(item?.priceAtTime ?? item?.price ?? product?.price ?? 0);
}

export function getItemQuantity(item) {
  return Number(item?.quantity ?? item?.qty ?? 1);
}

export function formatPrice(value) {
  const number = Number(value ?? 0);
  return new Intl.NumberFormat("en-IN", {
    style: "currency",
    currency: "INR",
    maximumFractionDigits: 2,
  }).format(number);
}

export function decodeJwtPayload(token) {
  try {
    const [, payload] = token.split(".");
    if (!payload) return null;
    const decoded = atob(payload.replace(/-/g, "+").replace(/_/g, "/"));
    return JSON.parse(decoded);
  } catch {
    return null;
  }
}
