import type React from "react";
import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import ClientLayout from "./client-layout";

export const metadata: Metadata = {
  title: "Linkurator",
  description: "링크 큐레이션과 콘텐츠 공유를 위한 마이크로 블로그 서비스",
  keywords: [
    "링크 큐레이션",
    "마이크로 블로그",
    "콘텐츠 공유",
    "북마크",
    "플레이리스트",
  ],
  generator: "v0.dev",
  icons: {
    icon: [
      {
        url: "/favicon.ico",
        sizes: "32x32",
        type: "image/x-icon",
      },
      {
        url: "/icon.svg",
        type: "image/svg+xml",
      },
      {
        url: "/icon-192.png",
        sizes: "192x192",
        type: "image/png",
      },
      {
        url: "/icon-512.png",
        sizes: "512x512",
        type: "image/png",
      },
    ],
    apple: [
      {
        url: "/apple-icon.png",
        sizes: "180x180",
        type: "image/png",
      },
    ],
  },
};

const inter = Inter({ subsets: ["latin"] });

export default function RootLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <html lang="ko">
      <body className={inter.className}>
        <ClientLayout>{children}</ClientLayout>
      </body>
    </html>
  );
}
