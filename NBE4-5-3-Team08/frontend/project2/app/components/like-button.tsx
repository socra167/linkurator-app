"use client";

import type React from "react";

import { useState, useEffect } from "react";
import { Heart } from "lucide-react";
import { Button } from "@/components/ui/button";
import { useToast } from "@/components/ui/use-toast";
import {
  getPlaylistLikeStatus,
  getPlaylistLikeCount,
  likePlaylist,
  checkLoginStatus,
} from "@/lib/playlist-service";

interface LikeButtonProps {
  playlistId: number;
  initialLikes: number;
  size?: "sm" | "default";
  onUnlike?: () => void;
}

// 전역 좋아요 상태 캐시 - 페이지 간 이동 시에도 유지됨
const LIKE_STATUS_CACHE: Record<number, boolean> = {};
const LIKE_COUNT_CACHE: Record<number, number> = {};

export default function LikeButton({
  playlistId,
  initialLikes,
  size = "default",
  onUnlike,
}: LikeButtonProps) {
  const { toast } = useToast();
  const [likes, setLikes] = useState(
    // 캐시된 좋아요 수가 있으면 사용, 없으면 initialLikes 사용
    LIKE_COUNT_CACHE[playlistId] !== undefined
      ? LIKE_COUNT_CACHE[playlistId]
      : initialLikes
  );

  // 캐시된 좋아요 상태가 있으면 사용, 없으면 false로 초기화
  const [isLiked, setIsLiked] = useState(
    LIKE_STATUS_CACHE[playlistId] || false
  );
  const [isLoading, setIsLoading] = useState(true);

  // 좋아요 상태 변경 시 전역 캐시 업데이트
  useEffect(() => {
    LIKE_STATUS_CACHE[playlistId] = isLiked;
    LIKE_COUNT_CACHE[playlistId] = likes;

    // 세션 스토리지에도 저장 (새로고침 대비)
    sessionStorage.setItem(`playlist_like_${playlistId}`, String(isLiked));
    sessionStorage.setItem(`playlist_like_count_${playlistId}`, String(likes));

    console.log(
      `좋아요 상태 업데이트 - playlistId: ${playlistId}, isLiked: ${isLiked}, likes: ${likes}`
    );
  }, [playlistId, isLiked, likes]);

  useEffect(() => {
    async function fetchLikeData() {
      try {
        setIsLoading(true);

        // 로그인 상태 확인
        const loggedIn = await checkLoginStatus();
        if (!loggedIn) {
          setIsLoading(false);
          return;
        }

        // 1. 세션 스토리지에서 먼저 확인 (새로고침 대비)
        const cachedLikeStatus = sessionStorage.getItem(
          `playlist_like_${playlistId}`
        );
        const cachedLikeCount = sessionStorage.getItem(
          `playlist_like_count_${playlistId}`
        );

        if (cachedLikeStatus !== null) {
          const parsedStatus = cachedLikeStatus === "true";
          console.log(`세션 스토리지에서 가져온 좋아요 상태: ${parsedStatus}`);
          setIsLiked(parsedStatus);
          LIKE_STATUS_CACHE[playlistId] = parsedStatus;
        }

        if (cachedLikeCount !== null) {
          const parsedCount = Number.parseInt(cachedLikeCount, 10);
          console.log(`세션 스토리지에서 가져온 좋아요 수: ${parsedCount}`);
          setLikes(parsedCount);
          LIKE_COUNT_CACHE[playlistId] = parsedCount;
        }

        // 2. API에서 최신 데이터 가져오기 (백그라운드에서 업데이트)
        try {
          const [likeStatus, likeCount] = await Promise.all([
            getPlaylistLikeStatus(playlistId),
            getPlaylistLikeCount(playlistId),
          ]);

          console.log(
            `API에서 가져온 좋아요 상태: ${likeStatus}, 좋아요 수: ${likeCount}`
          );

          // 사용자가 최근에 좋아요 상태를 변경하지 않은 경우에만 API 값으로 업데이트
          const recentlyChanged = sessionStorage.getItem(
            `playlist_like_recently_changed_${playlistId}`
          );
          if (!recentlyChanged) {
            setIsLiked(likeStatus);
            LIKE_STATUS_CACHE[playlistId] = likeStatus;
            sessionStorage.setItem(
              `playlist_like_${playlistId}`,
              String(likeStatus)
            );
          }

          // 좋아요 수는 항상 최신 값으로 업데이트
          setLikes(likeCount);
          LIKE_COUNT_CACHE[playlistId] = likeCount;
          sessionStorage.setItem(
            `playlist_like_count_${playlistId}`,
            String(likeCount)
          );
        } catch (error) {
          console.error("API 데이터 조회 실패:", error);
          // API 실패 시 캐시된 값 유지 (이미 위에서 설정됨)
        }
      } catch (error) {
        console.error("좋아요 데이터 로딩 실패", error);
      } finally {
        setIsLoading(false);
      }
    }

    fetchLikeData();

    // 다른 컴포넌트에서 발생한 좋아요 상태 변경 이벤트 처리
    const handleLikeChanged = (event: CustomEvent) => {
      const {
        playlistId: changedPlaylistId,
        isLiked: changedIsLiked,
        likeCount: changedLikeCount,
      } = event.detail;

      // 숫자로 변환하여 비교 (문자열 비교 방지)
      if (Number(changedPlaylistId) === Number(playlistId)) {
        console.log(
          `좋아요 이벤트 수신 및 적용: playlistId=${changedPlaylistId}, isLiked=${changedIsLiked}, count=${changedLikeCount}`
        );

        // 상태 업데이트
        setIsLiked(changedIsLiked);
        setLikes(changedLikeCount);

        // 전역 캐시 업데이트
        LIKE_STATUS_CACHE[playlistId] = changedIsLiked;
        LIKE_COUNT_CACHE[playlistId] = changedLikeCount;

        // 세션 스토리지 업데이트
        sessionStorage.setItem(
          `playlist_like_${playlistId}`,
          String(changedIsLiked)
        );
        sessionStorage.setItem(
          `playlist_like_count_${playlistId}`,
          String(changedLikeCount)
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
  }, [playlistId, initialLikes]);

  // 좋아요 상태 변경 함수
  const handleToggleLike = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();

    if (isLoading) return;

    // 로그인 상태 확인
    const isLoggedIn = sessionStorage.getItem("isLoggedIn") === "true";
    if (!isLoggedIn) {
      toast({
        title: "로그인이 필요합니다",
        description: "좋아요 기능을 사용하려면 로그인해주세요.",
        variant: "destructive",
      });
      sessionStorage.setItem("loginRedirectPath", window.location.pathname);
      window.location.href = "/auth/login";
      return;
    }

    setIsLoading(true);

    // 이전 상태 저장 (롤백을 위해)
    const prevIsLiked = isLiked;
    const prevLikes = likes;

    // 현재 상태의 반대로 토글
    const newLikeStatus = !prevIsLiked;
    const newLikeCount = newLikeStatus ? prevLikes + 1 : prevLikes - 1;

    console.log(`좋아요 상태 변경 시작: ${prevIsLiked} -> ${newLikeStatus}`);

    // 낙관적 UI 업데이트
    setIsLiked(newLikeStatus);
    setLikes(newLikeCount);
    LIKE_STATUS_CACHE[playlistId] = newLikeStatus;
    LIKE_COUNT_CACHE[playlistId] = newLikeCount;
    sessionStorage.setItem(
      `playlist_like_${playlistId}`,
      String(newLikeStatus)
    );
    sessionStorage.setItem(
      `playlist_like_count_${playlistId}`,
      String(newLikeCount)
    );

    // 최근 변경 플래그 설정 (5초 동안 유지)
    sessionStorage.setItem(
      `playlist_like_recently_changed_${playlistId}`,
      "true"
    );
    setTimeout(() => {
      sessionStorage.removeItem(`playlist_like_recently_changed_${playlistId}`);
    }, 5000);

    try {
      // **항상 POST 방식 호출하여 토글 처리**
      await likePlaylist(playlistId);

      toast({
        title: newLikeStatus
          ? "좋아요가 추가되었습니다"
          : "좋아요가 취소되었습니다",
      });

      // onUnlike 콜백 호출 (좋아요 취소 시)
      if (!newLikeStatus && onUnlike) {
        onUnlike();
      }

      // 전역 이벤트 발생 - 다른 컴포넌트에도 변경 알림
      window.dispatchEvent(
        new CustomEvent("playlist-like-changed", {
          detail: {
            playlistId,
            isLiked: newLikeStatus,
            likeCount: newLikeCount,
          },
        })
      );
    } catch (error) {
      console.error("좋아요 요청 처리 실패", error);

      // 실패 시 롤백
      setIsLiked(prevIsLiked);
      setLikes(prevLikes);
      LIKE_STATUS_CACHE[playlistId] = prevIsLiked;
      LIKE_COUNT_CACHE[playlistId] = prevLikes;
      sessionStorage.setItem(
        `playlist_like_${playlistId}`,
        String(prevIsLiked)
      );
      sessionStorage.setItem(
        `playlist_like_count_${playlistId}`,
        String(prevLikes)
      );

      toast({
        title: "오류 발생",
        description: (error as Error).message,
        variant: "destructive",
      });
    } finally {
      setIsLoading(false);
    }
  };

  if (size === "sm") {
    const isLoggedIn = sessionStorage.getItem("isLoggedIn") === "true";

    return (
      <button
        onClick={handleToggleLike}
        disabled={isLoading || !isLoggedIn}
        className={`flex items-center gap-1 text-xs ${
          isLiked
            ? "text-rose-500"
            : isLoggedIn
            ? "text-muted-foreground hover:text-rose-500"
            : "text-muted-foreground opacity-60"
        } transition-colors`}
      >
        <Heart className={`h-3.5 w-3.5 ${isLiked ? "fill-rose-500" : ""}`} />
        <span>{likes.toLocaleString()}</span>
      </button>
    );
  }

  // 기본 버튼 렌더링 부분
  const isLoggedIn = sessionStorage.getItem("isLoggedIn") === "true";

  return (
    <Button
      variant="outline"
      size="sm"
      className={`gap-2 ${isLiked ? "text-rose-500" : ""} ${
        !isLoggedIn ? "opacity-70" : ""
      }`}
      onClick={handleToggleLike}
      disabled={isLoading || !isLoggedIn}
    >
      <Heart className={`h-4 w-4 ${isLiked ? "fill-rose-500" : ""}`} />
      <span>{likes.toLocaleString()}</span>
    </Button>
  );
}
