"use client";

import { useState, useEffect } from "react";
import { Tabs, TabsContent, TabsList, TabsTrigger } from "@/components/ui/tabs";
import LeftSidebar from "@/app/components/left-sidebar";
import PostList from "@/app/components/post-list";
import RightSidebar from "@/app/components/right-sidebar";
import FollowingCurations from "@/app/curation/following/page";

export default function HomePage() {
  const [isLoaded, setIsLoaded] = useState(false);

  useEffect(() => {
    // 컴포넌트가 마운트되면 로딩 완료 상태로 설정
    setIsLoaded(true);

    // 로그인 상태 확인
    const isLoggedIn = sessionStorage.getItem("isLoggedIn") === "true";
    console.log("홈 페이지 로드됨, 로그인 상태:", isLoggedIn);

    // 필요한 경우 여기서 추가 데이터 로드 가능
  }, []);

  // 로딩 중일 때 스켈레톤 UI 또는 로딩 인디케이터 표시
  if (!isLoaded) {
    return (
      <div className="container flex justify-center items-center min-h-[60vh]">
        <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
      </div>
    );
  }

  return (
    <main className="container grid grid-cols-12 gap-6 px-4 py-6">
      <div className="col-span-2">
        <LeftSidebar />
      </div>
      <div className="col-span-7">
        <Tabs defaultValue="latest" className="w-full">
          <TabsList className="mb-4 w-full">
            <TabsTrigger value="latest" className="flex-1">
              최신
            </TabsTrigger>
            <TabsTrigger value="following" className="flex-1">
              팔로잉
            </TabsTrigger>
          </TabsList>
          <TabsContent value="latest">
            <PostList />
          </TabsContent>
          <TabsContent value="following">
            <FollowingCurations />
          </TabsContent>
        </Tabs>
      </div>
      <div className="col-span-3">
        <RightSidebar />
      </div>
    </main>
  );
}
