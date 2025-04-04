export default function CurationSkeleton() {
  return (
    <div className="space-y-4 border-b pb-6 animate-pulse">
      {/* 날짜 스켈레톤 */}
      <div className="flex items-center space-x-2">
        <div className="h-4 w-32 bg-gray-200 rounded"></div>
      </div>

      {/* 제목 스켈레톤 */}
      <div className="h-7 w-3/4 bg-gray-200 rounded"></div>

      {/* 내용 스켈레톤 */}
      <div className="space-y-2">
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-full bg-gray-200 rounded"></div>
        <div className="h-4 w-2/3 bg-gray-200 rounded"></div>
      </div>

      {/* 더보기 버튼 스켈레톤 */}
      <div className="h-5 w-16 bg-gray-200 rounded"></div>

      {/* 태그 스켈레톤 */}
      <div className="flex space-x-2">
        <div className="h-6 w-16 bg-gray-200 rounded-full"></div>
        <div className="h-6 w-20 bg-gray-200 rounded-full"></div>
        <div className="h-6 w-14 bg-gray-200 rounded-full"></div>
      </div>

      {/* 링크 메타데이터 카드 스켈레톤 */}
      <div className="mt-4 rounded-lg border p-4">
        <div className="flex items-center space-x-3">
          <div className="h-12 w-12 bg-gray-200 rounded-lg"></div>
          <div className="space-y-2 flex-1">
            <div className="h-5 w-3/4 bg-gray-200 rounded"></div>
            <div className="h-4 w-full bg-gray-200 rounded"></div>
          </div>
        </div>
      </div>

      {/* 액션 버튼 스켈레톤 */}
      <div className="flex items-center justify-between">
        <div className="flex items-center space-x-4">
          <div className="h-5 w-16 bg-gray-200 rounded"></div>
          <div className="h-5 w-16 bg-gray-200 rounded"></div>
        </div>
        <div className="flex space-x-2">
          <div className="h-5 w-5 bg-gray-200 rounded"></div>
          <div className="h-5 w-5 bg-gray-200 rounded"></div>
        </div>
      </div>
    </div>
  )
}

