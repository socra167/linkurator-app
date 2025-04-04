"use client";

import PlaylistGrid from "@/app/components/playlist-grid";
import LikedPlaylistGrid from "@/app/components/liked-playlist-grid";
import Link from "next/link";
import { useEffect, useState } from "react";
import { usePathname, useRouter, useSearchParams } from "next/navigation";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import { Button } from "@/components/ui/button";
import { getLikedPlaylists, checkLoginStatus } from "@/lib/playlist-service";
import type { Playlist } from "@/types/playlist";

export default function PlaylistsPage() {
  const pathname = usePathname();
  const searchParams = useSearchParams();
  const router = useRouter();
  const [activeTab, setActiveTab] = useState<string>("my");
  const [likedPlaylists, setLikedPlaylists] = useState<Playlist[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [isLoggedIn, setIsLoggedIn] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // 로그인 상태 확인
  useEffect(() => {
    const verifyLoginStatus = async () => {
      try {
        const loggedIn = await checkLoginStatus();
        setIsLoggedIn(loggedIn);
      } catch (error) {
        console.error("로그인 상태 확인 오류:", error);
        setIsLoggedIn(false);
      }
    };

    verifyLoginStatus();
  }, [activeTab]);

  // URL에서 탭 파라미터 가져오기
  useEffect(() => {
    const tab = searchParams.get("tab");
    if (tab === "liked") {
      setActiveTab("liked");
    } else {
      setActiveTab("my");
    }
  }, [searchParams]);

  // 좋아요한 플레이리스트 가져오기
  useEffect(() => {
    async function fetchLikedPlaylists() {
      if (activeTab !== "liked") return;

      try {
        setIsLoading(true);

        // 로그인 상태 확인
        if (!isLoggedIn) {
          setIsLoading(false);
          return;
        }

        // 서비스 모듈 함수 사용
        const playlists = await getLikedPlaylists();
        setLikedPlaylists(playlists);
      } catch (error) {
        console.error("좋아요한 플레이리스트 로딩 실패", error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchLikedPlaylists();
  }, [activeTab, isLoggedIn]);

  // 탭 변경 핸들러
  const handleTabChange = (value: string) => {
    setActiveTab(value);
    router.push(`/playlists${value === "liked" ? "?tab=liked" : ""}`);
  };

  // 현재 경로를 세션 스토리지에 저장
  useEffect(() => {
    sessionStorage.setItem("previousPath", pathname);
  }, [pathname]);

  // 좋아요 상태 변경 시 목록 업데이트
  const handleLikeStatusChange = async () => {
    if (activeTab === "liked" && isLoggedIn) {
      try {
        setIsLoading(true);
        const playlists = await getLikedPlaylists();
        setLikedPlaylists(playlists);
      } catch (error) {
        console.error("좋아요한 플레이리스트 업데이트 실패", error);
      } finally {
        setIsLoading(false);
      }
    }
  };

  // 로그인 페이지로 이동
  const handleLoginRedirect = () => {
    // 현재 경로를 저장하여 로그인 후 돌아올 수 있도록 함
    sessionStorage.setItem(
      "loginRedirectPath",
      pathname + (activeTab === "liked" ? "?tab=liked" : "")
    );
    router.push("/auth/login");
  };

  return (
    <div className="container mx-auto px-4 py-8">
      <header className="flex items-center justify-between mb-6">
        <h1 className="text-2xl font-bold">플레이리스트</h1>
        {isLoggedIn && (
          <Link href="/playlists/new">
            <button className="px-4 py-2 border rounded hover:bg-gray-100">
              새 플레이리스트 생성
            </button>
          </Link>
        )}
      </header>

      <Tabs value={activeTab} onValueChange={handleTabChange} className="mb-6">
        <TabsList className="grid w-full max-w-md grid-cols-2">
          <TabsTrigger value="my">내 플레이리스트</TabsTrigger>
          <TabsTrigger value="liked">좋아요한 플레이리스트</TabsTrigger>
        </TabsList>

        <TabsContent value="my">
          {isLoggedIn ? (
            <PlaylistGrid />
          ) : (
            <div className="text-center py-12 bg-gray-50 rounded-lg border">
              <h3 className="text-lg font-medium mb-2">로그인이 필요합니다</h3>
              <p className="text-muted-foreground mb-4">
                내 플레이리스트를 보려면 로그인해주세요.
              </p>
              <Button onClick={handleLoginRedirect}>로그인하기</Button>
            </div>
          )}
        </TabsContent>

        <TabsContent value="liked">
          {isLoggedIn ? (
            <LikedPlaylistGrid
              playlists={likedPlaylists}
              onLikeStatusChange={handleLikeStatusChange}
              isLoading={isLoading}
            />
          ) : (
            <div className="text-center py-12 bg-gray-50 rounded-lg border">
              <h3 className="text-lg font-medium mb-2">로그인이 필요합니다</h3>
              <p className="text-muted-foreground mb-4">
                좋아요한 플레이리스트를 보려면 로그인해주세요.
              </p>
              <Button onClick={handleLoginRedirect}>로그인하기</Button>
            </div>
          )}
        </TabsContent>
      </Tabs>
    </div>
  );
}
