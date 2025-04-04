"use client"

// This file is a placeholder for the actual use-toast.ts implementation.
// In a real-world scenario, this file would contain the logic for displaying and managing toasts.

import { useState } from "react"

type ToastProps = {
  title?: string
  description?: string
  variant?: "default" | "destructive"
}

export function useToast() {
  const [toasts, setToasts] = useState<ToastProps[]>([])

  const toast = (props: ToastProps) => {
    setToasts((prev) => [...prev, props])

    // 3초 후에 토스트 제거
    setTimeout(() => {
      setToasts((prev) => prev.slice(1))
    }, 3000)

    return {
      id: Date.now(),
      ...props,
    }
  }

  return {
    toast,
    toasts,
  }
}

