export default function CommentSkeleton() {
  return (
    <div className="space-y-6 animate-pulse">
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
        {[...Array(3)].map((_, index) => (
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
  )
}

