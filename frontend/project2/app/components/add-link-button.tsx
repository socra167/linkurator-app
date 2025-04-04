"use client";

import { DialogTrigger } from "@/components/ui/dialog";

import type React from "react";

import { useState } from "react";
import { useRouter } from "next/navigation";
import { Plus } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { addItemToPlaylist } from "@/lib/playlist-service";
import type { PlaylistItem } from "@/types/playlist";

interface AddLinkButtonProps {
  playlistId: number;
  onLinkAdded?: (newItem: PlaylistItem) => void; // 새 콜백 prop 추가
}

export default function AddLinkButton({
  playlistId,
  onLinkAdded,
}: AddLinkButtonProps) {
  const router = useRouter();
  const [open, setOpen] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [formData, setFormData] = useState({
    title: "",
    url: "",
    description: "",
  });

  const handleChange = (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>
  ) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setIsSubmitting(true);
    try {
      const result = await addItemToPlaylist(playlistId, {
        title: formData.title,
        url: formData.url,
        description: formData.description,
      });

      // 새 아이템이 추가되었을 때 콜백 호출
      if (onLinkAdded && result.items && result.items.length > 0) {
        // 가장 최근에 추가된 아이템을 찾음
        const newItem = result.items[result.items.length - 1];
        onLinkAdded(newItem);
      }

      setFormData({ title: "", url: "", description: "" });
      setOpen(false);
      // router.refresh()는 유지하되, 클라이언트 상태도 업데이트하기 위해 콜백을 활용
      router.refresh();
    } catch (error) {
      console.error("Failed to add link:", error);
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <Dialog open={open} onOpenChange={setOpen}>
      <DialogTrigger asChild>
        <Button>
          <Plus className="mr-2 h-4 w-4" />
          링크 추가
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-[500px]">
        <form onSubmit={handleSubmit}>
          <DialogHeader>
            <DialogTitle>새 링크 추가</DialogTitle>
            <DialogDescription>
              플레이리스트에 추가할 링크 정보를 입력하세요.
            </DialogDescription>
          </DialogHeader>
          <div className="grid gap-4 py-4">
            <div className="grid gap-2">
              <Label htmlFor="url">URL</Label>
              <Input
                id="url"
                name="url"
                type="url"
                placeholder="https://example.com"
                value={formData.url}
                onChange={handleChange}
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="title">제목</Label>
              <Input
                id="title"
                name="title"
                placeholder="링크 제목"
                value={formData.title}
                onChange={handleChange}
                required
              />
            </div>
            <div className="grid gap-2">
              <Label htmlFor="description">설명</Label>
              <Textarea
                id="description"
                name="description"
                placeholder="링크에 대한 설명을 입력하세요"
                value={formData.description}
                onChange={handleChange}
                rows={3}
              />
            </div>
          </div>
          <DialogFooter>
            <Button
              type="button"
              variant="outline"
              onClick={() => setOpen(false)}
            >
              취소
            </Button>
            <Button type="submit" disabled={isSubmitting}>
              {isSubmitting ? "추가 중..." : "추가하기"}
            </Button>
          </DialogFooter>
        </form>
      </DialogContent>
    </Dialog>
  );
}
