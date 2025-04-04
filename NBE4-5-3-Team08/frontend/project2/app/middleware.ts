import { NextResponse } from "next/server";
import type { NextRequest } from "next/server";

export function middleware(request: NextRequest) {
  const { pathname } = request.nextUrl;

  // Handle favicon requests
  if (pathname === "/favicon.ico") {
    return NextResponse.rewrite(new URL("/api/favicon/icon", request.url));
  }

  if (pathname === "/icon-192.png") {
    return NextResponse.rewrite(new URL("/api/favicon?size=192", request.url));
  }

  if (pathname === "/icon-512.png") {
    return NextResponse.rewrite(new URL("/api/favicon?size=512", request.url));
  }

  if (pathname === "/apple-icon.png") {
    return NextResponse.rewrite(
      new URL("/api/favicon/apple-icon", request.url)
    );
  }

  return NextResponse.next();
}

export const config = {
  matcher: [
    "/favicon.ico",
    "/icon-192.png",
    "/icon-512.png",
    "/apple-icon.png",
  ],
};
