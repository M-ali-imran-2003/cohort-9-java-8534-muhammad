import { useState, useEffect, useMemo } from "react";
import { getProfile } from "../api/userApi.js";
import { AuthContext } from "./authContextObject.js";

export function AuthProvider({ children }) {
  const [isAuthenticated, setIsAuthenticated] = useState(null);
  const [user, setUser] = useState(null);

  const checkAuth = async () => {
    try {
      const profile = await getProfile();
      setUser(profile);
      setIsAuthenticated(true);
    } catch {
      setUser(null);
      setIsAuthenticated(false);
    }
  };

  useEffect(() => {
    const publicPaths = ["/login", "/register"];
    if (publicPaths.includes(window.location.pathname)) {
      return; // skip the check entirely, don't touch state here
    }

    let ignore = false;

    (async () => {
      try {
        const profile = await getProfile();
        if (!ignore) {
          setUser(profile);
          setIsAuthenticated(true);
        }
      } catch {
        if (!ignore) {
          setUser(null);
          setIsAuthenticated(false);
        }
      }
    })();

    return () => {
      ignore = true;
    };
  }, []);

  const value = useMemo(
    () => ({ isAuthenticated, user, checkAuth, setIsAuthenticated, setUser }),
    [isAuthenticated, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
