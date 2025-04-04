export default function SidebarSkeleton() {
  return (
    <div className="space-y-6 animate-pulse">
      {/* 인기 큐레이션 스켈레톤 */}
      <div className="rounded-lg border p-4">
        <div className="h-6 w-40 bg-gray-200 rounded mb-4"></div>
        <div className="space-y-4">
          {[...Array(3)].map((_, index) => (
            <div key={index} className="space-y-1">
              <div className="flex items-center">
                <div className="mr-2 h-6 w-6 bg-gray-200 rounded-full"></div>
                <div className="h-5 w-40 bg-gray-200 rounded"></div>
              </div>
              <div className="pl-8 h-3 w-32 bg-gray-200 rounded"></div>
            </div>
          ))}
        </div>
      </div>

      {/* 작성자 정보 스켈레톤 */}
      <div className="rounded-lg border p-4">
        <div className="h-6 w-32 bg-gray-200 rounded mb-3"></div>
        <div className="flex items-center space-x-3">
          <div className="h-12 w-12 bg-gray-200 rounded-full"></div>
          <div>
            <div className="h-5 w-24 bg-gray-200 rounded"></div>
            <div className="h-3 w-32 bg-gray-200 rounded mt-1"></div>
          </div>
        </div>
        <div className="mt-3 h-8 w-full bg-gray-200 rounded-md"></div>
      </div>
    </div>
  )
}

