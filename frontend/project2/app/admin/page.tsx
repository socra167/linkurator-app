"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { Users, FileText, Flag, BarChart, Shield } from "lucide-react"
import { useToast } from "@/components/ui/use-toast"

export default function AdminPage() {
    const router = useRouter()
    const { toast } = useToast()
    const [isAdmin, setIsAdmin] = useState(false)
    const [isLoading, setIsLoading] = useState(true)

    useEffect(() => {
        // 관리자 권한 확인
        const checkAdmin = () => {
            const userRole = sessionStorage.getItem("userRole")
            if (userRole !== "ADMIN") {
                toast({
                    title: "접근 권한 없음",
                    description: "관리자만 접근할 수 있는 페이지입니다.",
                    variant: "destructive",
                })
                router.push("/home")
                return
            }

            setIsAdmin(true)
            setIsLoading(false)
        }

        checkAdmin()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, []) // 의존성 배열을 비워서 컴포넌트 마운트 시 한 번만 실행되도록 함

    if (!isAdmin || isLoading) {
        return (
            <div className="container flex justify-center items-center min-h-[60vh]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    return (
        <div className="container py-10">
            <div className="mb-6">
                <h1 className="text-3xl font-bold mb-2">관리자 대시보드</h1>
                <p className="text-gray-500">사이트 관리를 위한 관리자 기능에 접근할 수 있습니다.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <Link href="/admin/dashboard">
                    <Card className="h-full cursor-pointer hover:shadow-md transition-shadow">
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-lg font-medium">통합 대시보드</CardTitle>
                            <Shield className="h-5 w-5 text-blue-500" />
                        </CardHeader>
                        <CardContent>
                            <CardDescription>멤버와 큐레이션을 한 페이지에서 관리합니다.</CardDescription>
                        </CardContent>
                    </Card>
                </Link>

                <Link href="/admin/reported-curations">
                    <Card className="h-full cursor-pointer hover:shadow-md transition-shadow">
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-lg font-medium">신고된 큐레이션</CardTitle>
                            <Flag className="h-5 w-5 text-red-500" />
                        </CardHeader>
                        <CardContent>
                            <CardDescription>사용자가 신고한 큐레이션을 검토하고 조치합니다.</CardDescription>
                        </CardContent>
                    </Card>
                </Link>

                <Link href="/admin/stats">
                    <Card className="h-full cursor-pointer hover:shadow-md transition-shadow">
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-lg font-medium">통계</CardTitle>
                            <BarChart className="h-5 w-5 text-blue-500" />
                        </CardHeader>
                        <CardContent>
                            <CardDescription>사이트 사용 통계와 분석 데이터를 확인합니다.</CardDescription>
                        </CardContent>
                    </Card>
                </Link>
            </div>
        </div>
    )
}

