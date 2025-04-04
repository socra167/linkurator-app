"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";
import { Home, Compass, Bookmark, User, Heart } from "lucide-react";

export default function Navbar() {
  const pathname = usePathname();

  const isActive = (path: string) => {
    return pathname === path || pathname.startsWith(`${path}/`);
  };

  return (
    <div className="fixed bottom-0 left-0 right-0 z-50 bg-background border-t md:hidden">
      <div className="flex justify-around items-center h-16">
        <Link
          href="/home"
          className={`flex flex-col items-center justify-center w-full h-full ${
            isActive("/home") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <Home className="h-5 w-5" />
          <span className="text-xs mt-1">홈</span>
        </Link>

        <Link
          href="/explore/playlists"
          className={`flex flex-col items-center justify-center w-full h-full ${
            isActive("/explore") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <Compass className="h-5 w-5" />
          <span className="text-xs mt-1">탐색</span>
        </Link>

        <Link
          href="/playlists?tab=liked"
          className={`flex flex-col items-center justify-center w-full h-full ${
            isActive("/playlists") && pathname.includes("tab=liked")
              ? "text-primary"
              : "text-muted-foreground"
          }`}
        >
          <Heart className="h-5 w-5" />
          <span className="text-xs mt-1">좋아요</span>
        </Link>

        <Link
          href="/playlists"
          className={`flex flex-col items-center justify-center w-full h-full ${
            isActive("/playlists") && !pathname.includes("tab=")
              ? "text-primary"
              : "text-muted-foreground"
          }`}
        >
          <Bookmark className="h-5 w-5" />
          <span className="text-xs mt-1">내 플레이리스트</span>
        </Link>

        <Link
          href="/profile"
          className={`flex flex-col items-center justify-center w-full h-full ${
            isActive("/profile") ? "text-primary" : "text-muted-foreground"
          }`}
        >
          <User className="h-5 w-5" />
          <span className="text-xs mt-1">프로필</span>
        </Link>
      </div>
    </div>
  );
}
