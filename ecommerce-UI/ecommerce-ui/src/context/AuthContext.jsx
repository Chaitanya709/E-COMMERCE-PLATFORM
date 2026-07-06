import { createContext, useContext, useEffect, useMemo, useState } from "react";
import { decodeJwtPayload } from "../api/apiUtils";

const AuthContext = createContext(null);

function safeParseUser() {
  try {
    const storedUser = localStorage.getItem("user");
    return storedUser ? JSON.parse(storedUser) : null;
  } catch {
    localStorage.removeItem("user");
    return null;
  }
}

function normalizeRole(role) {
  if (!role) return "USER";
  if (Array.isArray(role)) return role.includes("ADMIN") ? "ADMIN" : role[0] || "USER";
  return String(role).replace("ROLE_", "").toUpperCase();
}

export function buildUserFromAuthPayload(payload, emailFallback) {
  const token = payload?.token || payload?.accessToken || payload?.jwt;
  const claims = token ? decodeJwtPayload(token) : null;
  const userPayload = payload?.user || payload?.userResponse || payload?.data?.user || {};

  console.log(userPayload);

  return {
    id: userPayload.id ?? userPayload.userId ?? payload?.id ?? claims?.id ?? null,
    name: userPayload.name ?? payload?.name ?? claims?.name ?? "",
    email:
      userPayload.email ??
      payload?.email ??
      claims?.sub ??
      claims?.email ??
      emailFallback ??
      "",
    role: normalizeRole(
      userPayload.role ??
        payload?.role ??
        claims?.role ??
        claims?.roles ??
        claims?.authorities,
    ),
  };
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => localStorage.getItem("token"));
  const [user, setUser] = useState(safeParseUser);

  useEffect(() => {
    if (token) {
      localStorage.setItem("token", token);
    } else {
      localStorage.removeItem("token");
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      localStorage.setItem("user", JSON.stringify(user));
    } else {
      localStorage.removeItem("user");
    }
  }, [user]);

  const login = ({ token: newToken, user: loggedInUser }) => {
    setToken(newToken);
    setUser(loggedInUser);
  };

  const logout = () => {
    setToken(null);
    setUser(null);
  };

  const value = useMemo(
    () => ({
      token,
      user,
      isAuthenticated: Boolean(token),
      isAdmin: user?.role === "ADMIN",
      login,
      logout,
    }),
    [token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const context = useContext(AuthContext);

  if (!context) {
    throw new Error("useAuth must be used inside AuthProvider");
  }

  return context;
}
