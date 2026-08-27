import { useContext } from "react";
import { ToastContext } from "./toastContextObject.js";

export function useToast() {
  return useContext(ToastContext);
}
