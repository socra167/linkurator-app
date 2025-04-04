"use client";

import { useState } from "react";
import { Share2, Copy, Check } from "lucide-react";
import { Button } from "@/components/ui/button";
import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from "@/components/ui/popover";
import { toast } from "sonner";

interface ShareButtonProps {
  url?: string;
  id: number;
  variant?: "icon" | "default";
  className?: string;
}

export default function ShareButton({
  url,
  id,
  variant = "default",
  className = "",
}: ShareButtonProps) {
  const [open, setOpen] = useState(false);
  const [copied, setCopied] = useState(false);

  // Generate the full URL for sharing
  const getShareUrl = () => {
    // If a URL is provided, use it, otherwise construct from the current location
    if (url) return url;

    // Get the base URL (domain)
    const baseUrl =
      typeof window !== "undefined"
        ? `${window.location.protocol}//${window.location.host}`
        : "http://localhost:3000";

    // Return the full URL to the curation
    return `${baseUrl}/curation/${id}`;
  };

  const handleCopy = async () => {
    const shareUrl = getShareUrl();

    try {
      await navigator.clipboard.writeText(shareUrl);
      setCopied(true);
      toast.success("링크가 클립보드에 복사되었습니다.");

      // Reset the copied state after 2 seconds
      setTimeout(() => {
        setCopied(false);
        setOpen(false);
      }, 2000);
    } catch (err) {
      toast.error("링크 복사에 실패했습니다.");
      console.error("Failed to copy:", err);
    }
  };

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        {variant === "icon" ? (
          <Button variant="ghost" size="icon" className={className}>
            <Share2 className="h-4 w-4 text-gray-500" />
            <span className="sr-only">공유하기</span>
          </Button>
        ) : (
          <Button variant="ghost" size="sm" className={className}>
            <Share2 className="h-4 w-4 mr-2" />
            공유하기
          </Button>
        )}
      </PopoverTrigger>
      <PopoverContent className="w-72 p-3" align="end" side="top">
        <div className="space-y-2">
          <h3 className="text-sm font-medium">이 큐레이션 공유하기</h3>
          <div className="flex items-center space-x-2">
            <div className="flex-1 overflow-hidden rounded-md border bg-muted px-2 py-1">
              <p className="text-xs text-muted-foreground truncate">
                {getShareUrl()}
              </p>
            </div>
            <Button
              size="sm"
              variant="outline"
              className="h-7 px-2"
              onClick={handleCopy}
            >
              {copied ? (
                <Check className="h-3.5 w-3.5 text-green-500" />
              ) : (
                <Copy className="h-3.5 w-3.5" />
              )}
            </Button>
          </div>
          <p className="text-xs text-muted-foreground">
            링크를 복사하여 다른 사람과 공유하세요.
          </p>
        </div>
      </PopoverContent>
    </Popover>
  );
}
