"use client";

import type React from "react";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Card, CardContent, CardFooter } from "@/components/ui/card";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Button } from "@/components/ui/button";

import { createPlaylist, updatePlaylist } from "@/lib/playlist-service";
import type { Playlist } from "@/types/playlist";

// PlaylistFormProps 인터페이스에 onPlaylistCreated 콜백 추가
interface PlaylistFormProps {
  playlist?: Playlist;
  onPlaylistCreated?: (playlistId: number) => void;
}

export default function PlaylistForm({
  playlist,
  onPlaylistCreated,
}: PlaylistFormProps) {
  const router = useRouter();
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: playlist?.title || "",
    description: playlist?.description || "",
    isPublic: true,
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleCheckboxChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setFormData((prev) => ({ ...prev, isPublic: e.target.checked }));
  };

  // handleSubmit 함수 수정
  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      if (playlist) {
        const updatedPlaylist = await updatePlaylist(playlist.id, {
          title: formData.title,
          description: formData.description,
          isPublic: formData.isPublic,
        });
        router.push(`/playlists/${updatedPlaylist.id}`);
      } else {
        const newPlaylist = await createPlaylist(formData);

        // 콜백이 있으면 호출
        if (onPlaylistCreated) {
          onPlaylistCreated(newPlaylist.id);
        } else {
          router.push(`/playlists/${newPlaylist.id}`);
        }
      }
    } catch (error) {
      console.error("플레이리스트 저장 실패:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Card>
      <form onSubmit={handleSubmit}>
        <CardContent className="pt-6 space-y-4">
          {/* 제목 입력 */}
          <div className="space-y-2">
            <Label htmlFor="title">제목</Label>
            <Input
              id="title"
              name="title"
              placeholder="플레이리스트 제목"
              value={formData.title}
              onChange={handleChange}
              required
            />
          </div>
          {/* 설명 입력 */}
          <div className="space-y-2">
            <Label htmlFor="description">설명</Label>
            <Textarea
              id="description"
              name="description"
              placeholder="플레이리스트 설명을 입력하세요"
              value={formData.description}
              onChange={handleChange}
              rows={4}
            />
          </div>
          {/* 공개 여부 선택 */}
          <div className="space-y-2">
            <Label htmlFor="isPublic">공개 여부</Label>
            <input
              type="checkbox"
              id="isPublic"
              name="isPublic"
              checked={formData.isPublic}
              onChange={handleCheckboxChange}
            />{" "}
            공개
          </div>
        </CardContent>
        <CardFooter className="flex justify-between">
          <Button type="button" variant="outline" onClick={() => router.back()}>
            취소
          </Button>
          <Button type="submit" disabled={isSubmitting}>
            {isSubmitting ? "저장 중..." : playlist ? "수정하기" : "생성하기"}
          </Button>
        </CardFooter>
      </form>
    </Card>
  );
}
