"use client"

// 이 파일은 hooks/use-toast.ts 대신 사용할 수 있는 임시 파일입니다.
// 실제로는 hooks/use-toast.ts를 사용해야 합니다.

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

