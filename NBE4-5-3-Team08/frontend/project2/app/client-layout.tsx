"use client";

import type React from "react";
import HeaderWrapper from "./components/headerWrapper"; // ✅ 새로 만든 컴포넌트 불러오기
import { ThemeProvider } from "./components/theme-context";
import Footer from "./components/footer";
import { Toaster } from "sonner";

export default function ClientLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <ThemeProvider>
      <HeaderWrapper />{" "}
      {/* ✅ 클라이언트에서만 실행되므로 usePathname() 사용 가능 */}
      <main className="container mx-auto px-4 max-w-screen-xl">{children}</main>
      <Footer />
      <Toaster position="bottom-right" richColors />
    </ThemeProvider>
  );
}
