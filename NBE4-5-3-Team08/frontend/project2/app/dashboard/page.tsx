"use client"

import { useEffect, useState } from "react"
import { useRouter } from "next/navigation"
import Link from "next/link"
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from "@/components/ui/card"
import { MessageSquare, Flag, User } from "lucide-react"
import { useToast } from "@/components/ui/use-toast"

export default function DashboardPage() {
    const router = useRouter()
    const { toast } = useToast()
    const [isLoading, setIsLoading] = useState(true)
    const [userRole, setUserRole] = useState<string | null>(null)

    useEffect(() => {
        // 로그인 및 권한 확인
        const checkAuthAndRedirect = () => {
            const isLoggedIn = sessionStorage.getItem("isLoggedIn") === "true"
            const role = sessionStorage.getItem("userRole")

            if (!isLoggedIn) {
                toast({
                    title: "로그인이 필요합니다",
                    description: "로그인 페이지로 이동합니다.",
                    variant: "destructive",
                })
                router.push("/auth/login")
                return
            }

            setUserRole(role)

            // 관리자인 경우 관리자 대시보드로 리디렉션
            if (role === "ADMIN") {
                router.push("/admin")
                return
            }

            setIsLoading(false)
        }

        checkAuthAndRedirect()
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [])

    if (isLoading) {
        return (
            <div className="container flex justify-center items-center min-h-[60vh]">
                <div className="animate-spin rounded-full h-12 w-12 border-t-2 border-b-2 border-blue-500"></div>
            </div>
        )
    }

    return (
        <div className="container py-10">
            <div className="mb-6">
                <h1 className="text-3xl font-bold mb-2">사용자 대시보드</h1>
                <p className="text-gray-500">내 활동을 관리하고 확인할 수 있습니다.</p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                <Link href="/dashboard/comments">
                    <Card className="h-full cursor-pointer hover:shadow-md transition-shadow">
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-lg font-medium">댓글 관리</CardTitle>
                            <MessageSquare className="h-5 w-5 text-blue-500" />
                        </CardHeader>
                        <CardContent>
                            <CardDescription>내가 작성한 댓글을 확인하고 관리합니다.</CardDescription>
                        </CardContent>
                    </Card>
                </Link>

                <Link href="/dashboard/reports">
                    <Card className="h-full cursor-pointer hover:shadow-md transition-shadow">
                        <CardHeader className="flex flex-row items-center justify-between pb-2">
                            <CardTitle className="text-lg font-medium">신고 관리</CardTitle>
                            <Flag className="h-5 w-5 text-red-500" />
                        </CardHeader>
                        <CardContent>
                            <CardDescription>내가 신고한 콘텐츠를 확인하고 관리합니다.</CardDescription>
                        </CardContent>
                    </Card>
                </Link>
            </div>
        </div>
    )
}

