"use client";

import { useState, useEffect } from "react";
import Link from "next/link";
import { Eye, LinkIcon } from "lucide-react";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Badge } from "@/components/ui/badge";
import type { Playlist } from "@/types/playlist";
import LikeButton from "@/app/components/like-button";
import { Skeleton } from "@/components/ui/skeleton";

interface LikedPlaylistGridProps {
  playlists: Playlist[];
  onLikeStatusChange?: () => void;
  isLoading?: boolean;
}

const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  if (isNaN(date.getTime())) {
    return "유효하지 않은 날짜";
  }
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  const hours = String(date.getHours()).padStart(2, "0");
  const minutes = String(date.getMinutes()).padStart(2, "0");
  return `${year}년 ${month}월 ${day}일 ${hours}:${minutes}`;
};

export default function LikedPlaylistGrid({
  playlists,
  onLikeStatusChange,
  isLoading = false,
}: LikedPlaylistGridProps) {
  const [likedPlaylists, setLikedPlaylists] = useState<Playlist[]>(playlists);

  // props로 받은 플레이리스트가 변경되면 상태 업데이트
  useEffect(() => {
    setLikedPlaylists(playlists);
  }, [playlists]);

  // 좋아요 취소 핸들러
  const handleUnlike = (playlistId: number) => {
    // 로컬 UI 즉시 업데이트
    setLikedPlaylists((prev) => prev.filter((p) => p.id !== playlistId));

    // 부모 컴포넌트에 알림
    if (onLikeStatusChange) {
      onLikeStatusChange();
    }
  };

  // useEffect 추가 - 좋아요 상태 변경 이벤트 리스너
  useEffect(() => {
    // 다른 컴포넌트에서 발생한 좋아요 상태 변경 이벤트 처리
    const handleLikeChanged = (event: CustomEvent) => {
      const { playlistId: changedPlaylistId, isLiked: changedIsLiked } =
        event.detail;

      // 좋아요가 취소된 경우 해당 플레이리스트를 목록에서 제거
      if (!changedIsLiked) {
        setLikedPlaylists((prev) =>
          prev.filter((p) => p.id !== changedPlaylistId)
        );
      }
    };

    window.addEventListener(
      "playlist-like-changed",
      handleLikeChanged as EventListener
    );

    return () => {
      window.removeEventListener(
        "playlist-like-changed",
        handleLikeChanged as EventListener
      );
    };
  }, []);

  if (isLoading) {
    return (
      <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
        {[...Array(8)].map((_, i) => (
          <Card key={i} className="h-[180px]">
            <CardContent className="p-4">
              <Skeleton className="h-6 w-full mb-2" />
              <Skeleton className="h-4 w-1/2" />
            </CardContent>
            <CardFooter className="px-4 py-2">
              <Skeleton className="h-4 w-full" />
            </CardFooter>
          </Card>
        ))}
      </div>
    );
  }

  if (!likedPlaylists.length) {
    return (
      <div className="text-center py-12">
        <p className="text-muted-foreground">
          아직 좋아요한 플레이리스트가 없습니다.
        </p>
        <Link
          href="/explore/playlists"
          className="mt-4 inline-block text-blue-600 hover:underline"
        >
          플레이리스트 탐색하기
        </Link>
      </div>
    );
  }

  return (
    <div className="grid grid-cols-1 sm:grid-cols-2 md:grid-cols-3 lg:grid-cols-4 gap-5">
      {likedPlaylists.map((playlist) => (
        <Card
          key={playlist.id}
          className="relative hover:shadow-md transition-all duration-200 overflow-hidden group border-l-4 border-l-rose-500 flex flex-col h-[180px]"
        >
          <Link
            href={`/playlists/${playlist.id}`}
            className="block flex-1 flex flex-col"
          >
            <CardContent className="p-4 pb-2 flex-1 flex flex-col">
              <h3 className="font-bold text-lg truncate">{playlist.title}</h3>

              {playlist.description && (
                <p className="text-sm text-muted-foreground mt-1 line-clamp-2">
                  {playlist.description}
                </p>
              )}

              <div className="flex items-center flex-wrap gap-2 mt-auto text-xs text-muted-foreground">
                <Badge
                  variant="secondary"
                  className="flex items-center gap-1 font-normal"
                >
                  <Eye className="w-3 h-3" />
                  <span>{playlist.viewCount || 0}</span>
                </Badge>

                <Badge
                  variant="secondary"
                  className="flex items-center gap-1 font-normal"
                >
                  <LinkIcon className="w-3 h-3" />
                  <span>{playlist.items?.length || 0} 링크</span>
                </Badge>

                {/* 좋아요 버튼 -> 취소하면 목록에서 제거 */}
                <LikeButton
                  playlistId={playlist.id}
                  initialLikes={playlist.likeCount}
                  onUnlike={() => handleUnlike(playlist.id)}
                  size="sm"
                />
              </div>
            </CardContent>

            <CardFooter className="px-4 py-2 bg-muted/10 border-t text-xs text-muted-foreground flex justify-between items-center mt-auto">
              <span className="text-xs opacity-70">
                {playlist.createdAt
                  ? formatDate(playlist.createdAt)
                  : "날짜 정보 없음"}
              </span>
              <span className="text-xs font-medium text-primary/70 hover:text-primary transition-colors">
                자세히 보기
              </span>
            </CardFooter>
          </Link>
        </Card>
      ))}
    </div>
  );
}
