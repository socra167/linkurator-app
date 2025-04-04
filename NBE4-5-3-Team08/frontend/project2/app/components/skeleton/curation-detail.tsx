export default function CurationDetailSkeleton() {
  return (
    <div className="space-y-6 animate-pulse">
      {/* 작성자 정보 스켈레톤 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-2">
          <div className="h-10 w-10 bg-gray-200 rounded-full"></div>
          <div>
            <div className="h-5 w-24 bg-gray-200 rounded"></div>
            <div className="h-3 w-32 bg-gray-200 rounded mt-1"></div>
          </div>
        </div>
        <div className="h-8 w-20 bg-gray-200 rounded-md"></div>
      </div>

      {/* 제목 스켈레톤 */}
      <div className="h-9 w-3/4 bg-gray-200 rounded"></div>

      {/* 링크 카드 섹션 스켈레톤 */}
      <div className="my-6 space-y-4">
        <div className="h-7 w-40 bg-gray-200 rounded"></div>

        {/* 링크 카드 스켈레톤 */}
        <div className="rounded-lg border shadow-sm overflow-hidden">
          <div className="bg-gray-50 p-6">
            <div className="flex flex-col sm:flex-row items-start sm:items-center">
              <div className="mr-0 sm:mr-4 mb-4 sm:mb-0 flex-shrink-0">
                <div className="h-20 w-20 bg-gray-200 rounded-md"></div>
              </div>
              <div className="flex-1 space-y-2">
                <div className="h-6 w-3/4 bg-gray-200 rounded"></div>
                <div className="h-4 w-full bg-gray-200 rounded"></div>
                <div className="h-4 w-1/2 bg-gray-200 rounded"></div>
                <div className="flex items-center">
                  <div className="h-4 w-32 bg-gray-200 rounded"></div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>

      {/* 태그 스켈레톤 */}
      <div className="flex flex-wrap gap-2">
        <div className="h-6 w-16 bg-gray-200 rounded-full"></div>
        <div className="h-6 w-20 bg-gray-200 rounded-full"></div>
        <div className="h-6 w-14 bg-gray-200 rounded-full"></div>
      </div>

      {/* 본문 내용 스켈레톤 */}
      <div className="space-y-3">
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-3/4 bg-gray-200 rounded"></div>
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-5/6 bg-gray-200 rounded"></div>
      </div>

      {/* 액션 버튼 스켈레톤 */}
      <div className="flex items-center justify-between border-t border-b py-4">
        <div className="flex items-center space-x-4">
          <div className="h-6 w-16 bg-gray-200 rounded"></div>
          <div className="h-6 w-16 bg-gray-200 rounded"></div>
        </div>
        <div className="flex space-x-2">
          <div className="h-9 w-9 bg-gray-200 rounded-md"></div>
          <div className="h-9 w-9 bg-gray-200 rounded-md"></div>
        </div>
      </div>

      {/* 댓글 섹션 스켈레톤 */}
      <div className="space-y-4">
        <div className="h-7 w-32 bg-gray-200 rounded"></div>

        {/* 댓글 입력 폼 스켈레톤 */}
        <div className="space-y-3">
          <div className="h-24 w-full bg-gray-200 rounded-md"></div>
          <div className="flex justify-end">
            <div className="h-9 w-24 bg-gray-200 rounded-md"></div>
          </div>
        </div>

        {/* 댓글 목록 스켈레톤 */}
        <div className="space-y-4 mt-6">
          {[...Array(2)].map((_, index) => (
            <div key={index} className="rounded-lg border p-4">
              <div className="flex justify-between">
                <div className="flex items-center space-x-2">
                  <div className="h-9 w-9 bg-gray-200 rounded-full"></div>
                  <div>
                    <div className="h-5 w-24 bg-gray-200 rounded"></div>
                    <div className="h-3 w-32 bg-gray-200 rounded mt-1"></div>
                  </div>
                </div>
              </div>
              <div className="mt-2 space-y-2">
                <div className="h-4 w-full bg-gray-200 rounded"></div>
                <div className="h-4 w-3/4 bg-gray-200 rounded"></div>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  )
}

